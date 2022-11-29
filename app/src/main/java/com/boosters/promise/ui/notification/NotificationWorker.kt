package com.boosters.promise.ui.notification

import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.util.Log
import androidx.core.app.NotificationCompat
import androidx.work.Worker
import androidx.work.WorkerParameters
import androidx.work.workDataOf
import com.boosters.promise.R
import com.boosters.promise.data.promise.Promise

class NotificationWorker(val context: Context, params: WorkerParameters): Worker(context, params) {

    override fun doWork(): Result {
        val promiseId = inputData.getString("promiseId")
        val promiseTitle = inputData.getString("promiseTitle")
        val promiseDate = inputData.getString("promiseDate")

        val notificationManager =
            context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        notificationManager.createNotificationChannel(createChannel())
        val builder = NotificationCompat.Builder(context, NotificationService.CHANNEL_ID)
            .setSmallIcon(R.mipmap.ic_launcher)
            .setContentTitle(promiseTitle)
            .setContentText(String.format(context.getString(R.string.notification_request), promiseDate))
            .setAutoCancel(true)
            .setCategory(Notification.CATEGORY_MESSAGE)
            .setPriority(NotificationCompat.PRIORITY_HIGH)
        notificationManager.notify(System.currentTimeMillis().toInt(), builder.build())
        return Result.success()
    }

    private fun createChannel(): NotificationChannel {
        return NotificationChannel(
            NotificationService.CHANNEL_ID,
            NotificationService.CHANNEL_NAME,
            NotificationManager.IMPORTANCE_HIGH
        ).apply {
            description = NotificationService.CHANNEL_NAME
        }
    }

}