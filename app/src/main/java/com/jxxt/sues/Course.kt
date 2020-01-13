package com.jxxt.sues

import com.jxxt.sues.widget.Utils
import org.jetbrains.anko.toast
import java.util.*


data class Course(
    var WeekX: Int,
    var Which: Int,
    var Week: List<String>,
    var Room: String,
    var Teacher: String,
    var courseName: String
)
/*
course数据类 可转换为Date Calendar <List>
--------------------------------------
        WeekX:周几上课
        Which:第几节课
        Week<List>:上课周数列表
--------------------------------------
 */


//True 转化为Course形式(All Course by Map<String,Calendar>)
class SwitchToCourse(private val input: MutableList<String>?) {


    //返回 课程类+课程名字的Map
    fun switch(): List<Course>? {
        if (input == null) {
            Utils.getContext().toast("课表为空! 请导入")
            return null
        } else {
            val course = mutableListOf<Course>()
            input.forEach {
                val aClass = it//一个课程
                //课程时间相关信息
                val whichAndWeekxRegexPattern = Regex("""index =.*?;""")
                val whichAndWeekxPattern = Regex("""\d""")//which weekX
                val wklistRegexPattern = Regex("""\d""")//wklist
                val whichAndWeekxRegex = whichAndWeekxRegexPattern.find(aClass)?.value//获取含which和weekX的index字符串
                val whichAndWeekx = mutableListOf<String>()
                if (whichAndWeekxRegex != null) {
                    whichAndWeekxPattern.findAll(whichAndWeekxRegex).forEach { s ->
                        whichAndWeekx += s.value
                    }
                }
                val which = whichAndWeekx[1].toInt() + 1 // Which:第几节课 0
                val weekX = whichAndWeekx[0].toInt() // WeekX:周几上课 0

                //课程相关信息
                val classRegexPattern = Regex("""".*?"""")
                val classRegex = mutableListOf<String>()
                classRegexPattern.findAll(aClass).forEach { s ->
                    classRegex += s.value
                }
                val teacher = classRegex[1]
                val courseName = classRegex[3].replace("\"","")
                val room = classRegex[5].replace("\"","")
                val wk = mutableListOf<String>()
                wklistRegexPattern.findAll(classRegex[6]).forEach { s ->
                    wk += s.value
                }
                course += Course(weekX, which, wk, room, teacher, courseName)
            }
            return course
        }
    }
}


//转化为Calendar+Name(One Course)
class CourseToDate(private val course: Course) {
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
        return list
    }

    fun a(weekNow: Calendar): Map<Date, Course> {
        val weekX = course.WeekX
        val week = course.Week
        val room = course.Room
        val map: MutableMap<Date, Course> = mutableMapOf()
        val dateList = weekToDateList(weekNow, weekX, week, room)

        for (i in dateList) {
            //i时间应该上名为name的课
            map[i] = course
        }
        return map
    }

}

//转化为DateStrng+Name(All Course)
class AllDate(private val input: List<Map<Date, Course>>) {
    private val output: MutableMap<Date, Course> = mutableMapOf()
    private val map: MutableMap<Date, Course> = mutableMapOf()
    fun all(): MutableMap<Date, Course> {
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