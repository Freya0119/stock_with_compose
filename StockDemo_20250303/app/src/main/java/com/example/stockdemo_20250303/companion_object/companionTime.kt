package com.example.stockdemo_20250303.companion_object

import android.os.Build
import androidx.annotation.RequiresApi
import java.time.LocalDate
import java.time.LocalTime
import java.time.ZoneId
import java.util.Calendar
import java.util.Date
import java.util.TimeZone

object CompanionTime {
    private val twZone = TimeZone.getTimeZone("Asia/Taipei")
    // 9:00 ~ 13:30
    val startTime by lazy { getCalendarTime(9, 0) }
    val endTime by lazy { getCalendarTime(13, 30) }

    val times: List<Int> by lazy { getTimeList() }

    fun getNowTime(): Long {
        return Calendar.getInstance(twZone).apply {
            time = Date()
        }.timeInMillis / 1000
    }

    private fun getCalendarTime(hour: Int, minute: Int): Long {
        return Calendar.getInstance(twZone).apply {
            time = Date()
            set(Calendar.HOUR_OF_DAY, hour)
            set(Calendar.MINUTE, minute)
            set(Calendar.SECOND, 0)
            set(Calendar.MILLISECOND, 0)
        }.timeInMillis / 1000
    }

    private fun getTimeList(): List<Int> {
        val times = mutableListOf<Int>()
        var time = startTime
        val tick = 60
        while (time < endTime) {
            times.add(time.toInt())
            time += tick
        }
        return times
    }
}