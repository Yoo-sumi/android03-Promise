package com.boosters.promise.data.promise.source.remote

import com.boosters.promise.data.user.User
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.tasks.await
import javax.inject.Inject

class PromiseRemoteDataSourceImpl @Inject constructor(
    private val database: FirebaseFirestore
) : PromiseRemoteDataSource {

    private val promiseRef = database.collection(DATABASE_PROMISE_REF_PATH)

    override fun addPromise(promise: PromiseBody): Flow<Boolean> {
        var id = promise.promiseId
        if (promise.promiseId == "") {
            id = promiseRef.document().id
        }
        return callbackFlow {
            promiseRef.document(id).set(promise.copy(promiseId = id))
                .addOnSuccessListener {
                    trySend(true)
                    close()
                }
                .addOnFailureListener {
                    trySend(false)
                    close()
                }
            awaitClose()
        }
    }

    override fun removePromise(promiseId: String): Flow<Boolean> {
        return callbackFlow {
            promiseRef.document(promiseId).delete()
                .addOnSuccessListener {
                    trySend(true)
                    close()
                }
                .addOnFailureListener {
                    trySend(false)
                    close()
                }
            awaitClose()
        }
    }

    override suspend fun getPromiseList(user: User, date: String): List<PromiseBody> {
        val task = promiseRef
            .whereEqualTo(DATABASE_PROMISE_DATE_KEY, date)
            .whereArrayContainsAny(DATABASE_PROMISE_MEMBERS_KEY, listOf(user.copy(userToken = "")))
            .get()
        task.await()
        return task.result.documents.mapNotNull {
            it.toObject(PromiseBody::class.java)
        }
    }

    companion object {
        private const val DATABASE_PROMISE_REF_PATH = "promise"
        private const val DATABASE_PROMISE_DATE_KEY = "date"
        private const val DATABASE_PROMISE_MEMBERS_KEY = "members"
    }

}