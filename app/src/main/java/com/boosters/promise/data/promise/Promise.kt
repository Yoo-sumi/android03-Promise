package com.boosters.promise.data.promise

import android.os.Parcelable
import com.boosters.promise.data.model.Location
import com.boosters.promise.data.promise.source.remote.PromiseBody
import com.boosters.promise.data.user.User
import kotlinx.parcelize.Parcelize
import com.boosters.promise.data.user.toUserUiState
import com.boosters.promise.ui.promisesetting.model.PromiseUiState

data class Promise(
    val promiseId: String,
    val title: String,
    val destinationName: String,
    val destinationLocation: Location?,
    val date: String,
    val time: String,
    val members: List<User>
)

fun Promise.toPromiseBody() =
    PromiseBody(
        promiseId = promiseId,
        title = title,
        destinationName = destinationName,
        destinationLocation = destinationLocation,
        date = date,
        time = time,
        members = members
    )

fun Promise.toPromiseUiState() =
    PromiseUiState(
        promiseId = promiseId,
        title = title,
        destinationName = destinationName,
        destinationLocation = destinationLocation,
        date = date,
        time = time,
        members = members.map {
            it.toUserUiState()
        }
    )