package com.boosters.promise.data.alarm

import com.boosters.promise.data.alarm.source.local.AlarmLocalDataSource
import com.boosters.promise.data.alarm.source.local.toAlarm
import javax.inject.Inject

class AlarmRepositoryImpl @Inject constructor(
    private val alarmLocalDataSource: AlarmLocalDataSource
) : AlarmRepository {

    override suspend fun getAlarm(promiseId: String): Alarm {
        return alarmLocalDataSource.getAlarm(promiseId).toAlarm()
    }

    override suspend fun getAlarmCount(): Int {
        return alarmLocalDataSource.getAlarmCount()
    }

    override suspend fun addAlarm(alarm: Alarm) {
        alarmLocalDataSource.insertAlarm(alarm.toAlarmEntity())
    }

}