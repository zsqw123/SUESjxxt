package com.jxxt.sues

import com.jxxt.sues.widget.Utils
import org.jetbrains.anko.toast
import java.util.*

class Show {
    fun textShow(input: String): List<Item> {
        val mapInput = FindContent().jsToList(input)
        val toyearInput = FindContent().getToyear(input)
        val courseStrMap: List<Course>? = SwitchToCourse(mapInput).switch()
        val list: MutableList<Map<Date, Course>> = mutableListOf()
        if (courseStrMap != null) {
            for (i in courseStrMap) {
                val cal = Calendar.getInstance()
                cal.set(toyearInput, 0, 1, 0, 0, 0)
                cal.firstDayOfWeek = Calendar.MONDAY
                cal.set(Calendar.DAY_OF_WEEK, Calendar.MONDAY)
                cal.add(Calendar.WEEK_OF_YEAR, -1)
                val item: Map<Date, Course> = CourseToDate(i).a(cal)
                list.add(item)
            }
        } else {
            Utils.getContext().toast("课表为空! 请导入")
            return emptyList()
        }

        val map: MutableMap<Date, Course> = AllDate(list).all()
        val content: MutableList<Item> = mutableListOf()
        for (i in map) {
            content.add(Item(i.key, i.value.courseName, i.value.Room))
        }
        return content
    }
}