package com.jxxt.sues.ical

import android.content.Context
import java.text.SimpleDateFormat
import java.util.*

/**
 * Created by KYLE on 2019/3/6 - 13:53
 */
object Util {
    /**
     * 获取日历事件结束日期
     *
     * @param time time in ms
     */
    private fun getEndDate(time: Long): String {
        val date = Date(time)
        val format = SimpleDateFormat("yyyyMMdd", Locale.getDefault())
        return format.format(date)
    }

    /**
     * 获取最终日历事件重复规则
     *
     * @param time time in ms
     * "T235959"
     */
    fun getFinalRRuleMode(time: Long): String {
        return getEndDate(time) + "T235959Z"
    }

    /**
     * 格式化星期
     */
    private fun formatWeek(week: Int): String? {
        return when (week) {
            0 -> "SU"
            1 -> "MO"
            2 -> "TU"
            3 -> "WE"
            4 -> "TH"
            5 -> "FR"
            6 -> "SA"
            else -> null
        }
    }

    /**
     * 获取重复周
     *
     * @param time time in ms
     */
    fun getWeekForDate(time: Long): String? {
        val date = Date(time)
        val calendar = Calendar.getInstance()
        calendar.time = date
        var week = calendar[Calendar.DAY_OF_WEEK] - 1
        if (week < 0) {
            week = 0
        }
        return formatWeek(week)
    }

    /**
     * 获取指定时间段在一个月中的哪一天
     *
     * @param time time in ms
     */
    fun getDayOfMonth(time: Long): Int {
        val date = Date(time)
        val calendar = Calendar.getInstance()
        calendar.time = date
        return calendar[Calendar.DAY_OF_MONTH]
    }

    /**
     * check null
     */
    fun checkContextNull(context: Context?) {
        requireNotNull(context) { "context can not be null" }
    }
}