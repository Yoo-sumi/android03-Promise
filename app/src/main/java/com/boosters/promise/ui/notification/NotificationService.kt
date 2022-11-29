package com.boosters.promise.ui.notification

import android.app.*
import android.content.Context
import android.content.Intent
import android.os.SystemClock
import android.util.Log
import androidx.core.app.NotificationCompat
import androidx.core.app.TaskStackBuilder
import androidx.work.OneTimeWorkRequestBuilder
import androidx.work.WorkManager
import androidx.work.workDataOf
import com.boosters.promise.R
import com.boosters.promise.data.promise.Promise
import com.boosters.promise.ui.detail.PromiseDetailActivity
import com.boosters.promise.ui.promisecalendar.PromiseCalendarActivity
import com.boosters.promise.ui.promisesetting.PromiseSettingViewModel
import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage
import com.google.gson.Gson
import java.text.SimpleDateFormat
import java.util.*
import java.util.concurrent.TimeUnit
import javax.inject.Inject
import kotlin.time.Duration.Companion.milliseconds

class NotificationService : FirebaseMessagingService() {

    private val alarmDiretor = AlarmDiretor(this)

    override fun onMessageReceived(remoteMessage: RemoteMessage) {
        if (remoteMessage.data.isNotEmpty()) {
            sendNotification(remoteMessage)
        }
    }

    private fun sendNotification(remoteMessage: RemoteMessage) {
        val uniId = System.currentTimeMillis().toInt()
        val promise = Gson().fromJson(remoteMessage.data[MESSAGE_BODY], Promise::class.java)

        val contentText = if (remoteMessage.data[MESSAGE_TITLE] == NOTIFICATION_EDIT) {
            String.format(getString(R.string.notification_edit), promise.date)
        } else if (remoteMessage.data[MESSAGE_TITLE] == NOTIFICATION_ADD) {
            alarmDiretor.registerAlarm(0, promise)
            String.format(getString(R.string.notification_add), promise.date)
        } else {
            String.format(getString(R.string.notification_delete), promise.date)
        }

        val intent = if (remoteMessage.data[MESSAGE_TITLE] == NOTIFICATION_DELETE) {
            Intent(this, PromiseCalendarActivity::class.java)
        } else {
            Intent(this, PromiseDetailActivity::class.java).putExtra(PromiseCalendarActivity.PROMISE_ID_KEY, promise.promiseId)
        }
        val pendingIntent: PendingIntent? = TaskStackBuilder.create(this).run {
            addNextIntentWithParentStack(intent)
            getPendingIntent(0, PendingIntent.FLAG_IMMUTABLE or PendingIntent.FLAG_UPDATE_CURRENT)
        }

        val notificationManager =
            getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        notificationManager.createNotificationChannel(createChannel())
        val builder = NotificationCompat.Builder(this, CHANNEL_ID)
            .setSmallIcon(R.mipmap.ic_launcher)
            .setContentTitle(promise.title)
            .setContentText(contentText)
            .setAutoCancel(true)
            .setContentIntent(pendingIntent)
            .setCategory(Notification.CATEGORY_MESSAGE)
            .setPriority(NotificationCompat.PRIORITY_HIGH)
        notificationManager.notify(uniId, builder.build())
    }

    private fun createChannel(): NotificationChannel {
        return NotificationChannel(
            CHANNEL_ID,
            CHANNEL_NAME,
            NotificationManager.IMPORTANCE_HIGH
        ).apply {
            description = CHANNEL_NAME
        }
    }

    private fun reserveNotification(promise: Promise) {
        val date = promise.date.split("/").map { it.toInt() }
        val time = promise.time.split(":").map { it.toInt() }
        val cal = Calendar.getInstance()
        cal.set(Calendar.YEAR, date[0])
        cal.set(Calendar.MONTH, date[1] - 1)
        cal.set(Calendar.DAY_OF_MONTH, date[2])
        cal.set(Calendar.HOUR_OF_DAY, time[0])
        cal.set(Calendar.MINUTE, time[1])

        val alarmManager = getSystemService(ALARM_SERVICE) as AlarmManager
        val intent = Intent(this, AlarmReceiver::class.java)
            .putExtra("promiseId", promise.promiseId)
            .putExtra("promiseTitle", promise.title)
            .putExtra("promiseDate", promise.date)

        val pendingIntent = PendingIntent.getBroadcast(
            this, 0, intent,
            PendingIntent.FLAG_IMMUTABLE)
        alarmManager.setExactAndAllowWhileIdle(
            AlarmManager.RTC_WAKEUP, // 절전 모드일 때도 알람 발생
            cal.timeInMillis,
            pendingIntent
        )
    }

    companion object {
        const val CHANNEL_ID = "my_channel"
        const val CHANNEL_NAME = "Notice"
        private const val MESSAGE_BODY = "body"
        private const val MESSAGE_TITLE = "title"
        const val NOTIFICATION_EDIT = "0"
        const val NOTIFICATION_ADD = "1"
        const val NOTIFICATION_DELETE = "2"
    }

}