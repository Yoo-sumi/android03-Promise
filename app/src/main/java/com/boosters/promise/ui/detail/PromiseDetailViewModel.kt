package com.boosters.promise.ui.detail

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.boosters.promise.data.notification.NotificationRepository
import com.boosters.promise.data.promise.Promise
import com.boosters.promise.data.promise.PromiseRepository
import com.boosters.promise.data.promise.ServerKeyRepository
import com.boosters.promise.data.user.UserRepository
import com.boosters.promise.ui.notification.NotificationService
import com.boosters.promise.ui.promisesetting.PromiseSettingViewModel
import com.boosters.promise.ui.promisesetting.model.PromiseSettingUiState
import com.naver.maps.map.overlay.Marker
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class PromiseDetailViewModel @Inject constructor(
    private val notificationRepository: NotificationRepository,
    private val serverKeyRepository: ServerKeyRepository,
    private val promiseRepository: PromiseRepository,
    private val userRepository: UserRepository
) : ViewModel() {

    private val _promiseInfo = MutableStateFlow(Promise())
    val promiseInfo: StateFlow<Promise> get() = _promiseInfo.asStateFlow()

    private val _isDeleted = MutableLiveData<Boolean>()
    val isDeleted: LiveData<Boolean> = _isDeleted

    lateinit var memberMarkers: List<Marker>

    @OptIn(ExperimentalCoroutinesApi::class)
    val memberLocations = promiseInfo.flatMapLatest { promise ->
        userRepository.getUserList(promise.members.map { user -> user.userCode }).map { members ->
            members.map { member -> member.geoLocation }
        }
    }

    fun setPromiseInfo(promiseId: String) {
        viewModelScope.launch {
            _promiseInfo.value = promiseRepository.getPromise(promiseId).first().copy()
            memberMarkers = List(_promiseInfo.value.members.size) { Marker() }
        }
    }

    fun removePromise() {
        viewModelScope.launch {
            _promiseInfo.value.let {
                promiseRepository.removePromise(it.promiseId).collectLatest { isDeleted ->
                    if (isDeleted) {
                        sendNotification()
                    } else {
                        _isDeleted.value = isDeleted
                    }
                }
            }
        }
    }

    private fun sendNotification() {
        viewModelScope.launch {
            userRepository.getMyInfo().first().onSuccess { myInfo ->
                val userCodeList =
                    _promiseInfo.value.members.filter { it.userCode != myInfo.userCode }
                        .map { it.userCode }
                if (userCodeList.isEmpty()) { return@launch }

                val key = serverKeyRepository.getServerKey()

                userRepository.getUserList(userCodeList).collectLatest {
                    it.forEach { user ->
                        notificationRepository.sendNotification(
                            NotificationService.NOTIFICATION_DELETE,
                            _promiseInfo.value,
                            user.userToken,
                            key
                        )
                    }
                }
                _isDeleted.value = true
            }
        }
    }

}