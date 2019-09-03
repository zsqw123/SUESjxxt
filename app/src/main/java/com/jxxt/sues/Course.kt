package com.jxxt.sues

import java.util.*

/*
course数据类 可转换为Date Calendar <List>
--------------------------------------
        WeekX:周几上课
        Which:第几节课
        Week<List>:上课周数列表
--------------------------------------
 */
data class Course(var WeekX: Int, var Which: Int, var Week: List<String>)

/*
WithRoomName数据类 可转换为Date Calendar <List>
--------------------------------------
        room:上课教室
        name:课程名称
--------------------------------------
 */
data class WithRoomName(var room: String, var name: String)

//True 转化为Course+CourseName形式(All Course by Map<String,Calendar>)
class SwitchToCourse(private val input: Map<String, String>) {
    private fun getWeek(str: String): List<String> {
        val result = mutableListOf("0", "0", "0", "0", "0", "0", "0", "0", "0", "0", "0", "0", "0", "0", "0", "0", "0", "0", "0", "0", "0", "0", "0", "0", "0")
        val str0 = str.replace("(", "").replace(")", "")
        val week = str0.split(",")[0]
        val weekList = week.split(" ")
        for (i in weekList) {
            when {
                i.contains("单") -> {
                    val i0 = i.replace("单", "")
                    val wklist = i0.split("-")
                    val start = wklist[0].toInt()
                    val end = wklist[1].toInt()
                    for (a in start..end step 2) {
                        result[a - 1] = "1"
                    }
                }
                i.contains("双") -> {
                    val i0 = i.replace("双", "")
                    val wklist = i0.split("-")
                    val start = wklist[0].toInt()
                    val end = wklist[1].toInt()
                    for (a in start..end step 2) {
                        result[a - 1] = "1"
                    }
                }
                else -> {
                    val wklist = i.split("-")
                    val start = wklist[0].toInt()
                    val end = if (wklist.size == 1) start else wklist[1].toInt()
                    for (a in start..end step 1) {
                        result[a - 1] = "1"
                    }
                }
            }
        }
        return result
    }

    private fun getRoom(str: String): String {
        val str0 = str.replace("(", "").replace(")", "")
        return "${str0.split(",")[1]} "
    }

    fun switch(): Map<Course, WithRoomName> {
        val map = mutableMapOf<Course, WithRoomName>()
        for (i in input) {
            //Key String字段
            val weekStr = i.key.toInt()
            val weekX = weekStr / 14
            val which = weekStr % 14 + 1
            //Value String字段
            val str = i.value.split(";")
            var a = 0//循环常数
            while (a < str.size) {
                val name = WithRoomName(getRoom(str[a + 1]), str[a])
                map[Course(weekX, which, getWeek(str[a + 1]))] = name
                a += 2
            }
        }
        return map
    }
}

//转化为Calendar+Name(One Course)
class CourseToDate(private val name: WithRoomName, private val course: Course) {
    private fun weekToDateList(weekNow: Calendar, weekX: Int, week: List<String>, room: String): List<Date> {
        //课表第一周 周一
        weekNow.add(Calendar.DATE, weekX)
        weekNow.set(
            Calendar.HOUR_OF_DAY, when (course.Which) {
                1 -> 8;2 -> 9;3 -> 10;4 -> 10;5 -> 13;6 -> 13;7 -> 14;8 -> 15;9 -> 18;10 -> 18;11 -> 19;12 -> 20;13 -> 16;14 -> 17;else -> 0
            }
        )
        weekNow.set(
            Calendar.MINUTE, when (course.Which) {
                1 -> 15;2 -> 0;3 -> 5;4 -> 50;5 -> 0;6 -> 45;7 -> 50;8 -> 35;9 -> 0;10 -> 45;11 -> 30;12 -> 15;14 -> 15;else -> 0
            }
        )
        if (room.contains("D") || room.contains("E") || room.contains("F")) {
            weekNow.set(
                Calendar.HOUR_OF_DAY, when (course.Which) {
                    1 -> 8;2 -> 9;3 -> 10;4 -> 10;5 -> 13;6 -> 13;7 -> 14;8 -> 15;9 -> 18;10 -> 18;11 -> 19;12 -> 20;13 -> 16;14 -> 17;else -> 0
                }
            )
            weekNow.set(
                Calendar.MINUTE, when (course.Which) {
                    1 -> 15;2 -> 0;3 -> 25;4 -> 50;5 -> 0;6 -> 45;7 -> 50;8 -> 35;9 -> 0;10 -> 45;11 -> 30;12 -> 15;14 -> 15;else -> 0
                }
            )
        }

        val list: MutableList<Date> = mutableListOf()
        for (i in week) {
            weekNow.add(Calendar.DATE, 7)
            if (i == "1") {
                list.add(weekNow.time)
            }
        }
//        for (i in list){
//            println(SimpleDateFormat("MM/dd HH:mm", Locale.CHINA).format(i.time))
//        }
        return list
    }

    fun a(weekNow: Calendar): Map<Date, String> {
        val weekX = course.WeekX
        val week = course.Week
        val weekNow0: Calendar = weekNow
        val map: MutableMap<Date, String> = mutableMapOf()
        val dateList = weekToDateList(weekNow0, weekX, week, name.room)

        for (i in dateList) {
            map[i] = name.room + name.name
        }
        return map
    }

}

//转化为DateStrng+Name(All Course)
class AllDate(private val input: List<Map<Date, String>>) {
    private val output: MutableMap<Date, String> = mutableMapOf()
    private val map: MutableMap<Date, String> = mutableMapOf()
    fun all(): MutableMap<Date, String> {
        for (i in input) {
            for (a in i) {
                map[a.key] = a.value
            }
        }
        for (i in map.toSortedMap()) {
            output[i.key] = i.value
        }
        return output
    }
}