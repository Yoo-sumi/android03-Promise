package com.boosters.promise.ui.notification

import android.app.AlarmManager
import android.app.PendingIntent
import android.content.Context
import android.content.Context.ALARM_SERVICE
import android.content.Intent
import com.boosters.promise.data.promise.Promise
import java.util.*

class AlarmDiretor(private val context: Context) {

    private val alarmManager = context.getSystemService(ALARM_SERVICE) as AlarmManager

    fun registerAlarm(requestCode: Int, promise: Promise) {
        val date = promise.date.split("/").map { it.toInt() }
        val time = promise.time.split(":").map { it.toInt() }
        val cal = Calendar.getInstance()
        cal.set(Calendar.YEAR, date[0])
        cal.set(Calendar.MONTH, date[1] - 1)
        cal.set(Calendar.DAY_OF_MONTH, date[2])
        cal.set(Calendar.HOUR_OF_DAY, time[0])
        cal.set(Calendar.MINUTE, time[1])

        val intent = Intent(context, AlarmReceiver::class.java)
            .putExtra("promiseId", promise.promiseId)
            .putExtra("promiseTitle", promise.title)
            .putExtra("promiseDate", promise.date)

        val pendingIntent = PendingIntent.getBroadcast(
            context, requestCode, intent,
            PendingIntent.FLAG_IMMUTABLE)
        alarmManager.setExactAndAllowWhileIdle(
            AlarmManager.RTC_WAKEUP, // 절전 모드일 때도 알람 발생
            cal.timeInMillis,
            pendingIntent
        )
    }

    fun removeAlarm(requestCode: Int) {
        val intent = Intent(context, AlarmReceiver::class.java)
        val pendingIntent = PendingIntent.getBroadcast(context, requestCode, intent, PendingIntent.FLAG_IMMUTABLE)
        alarmManager.cancel(pendingIntent)
    }

}