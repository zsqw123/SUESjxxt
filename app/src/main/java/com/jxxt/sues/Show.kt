package com.jxxt.sues

import java.text.SimpleDateFormat
import java.util.*

class Show {
    fun textShow(input: String, weekNow: String): List<Item> {
        val mapInput: Map<String, String> = FindContext().resolveClasses(input)
        val courseStrMap: Map<Course, String> = SwitchToCourse(mapInput).switch()
        val list: MutableList<Map<Date, String>> = mutableListOf()
        for (i in courseStrMap) {
            val week0 = Calendar.getInstance()
            week0.time = SimpleDateFormat("yyyy-MM-dd", Locale.CHINA).parse(weekNow)
            val item: Map<Date, String> = CourseToDate(i.value, i.key).a(week0)
            list.add(item)
        }

        val map: MutableMap<Date, String> = AllDate(list).all()

        val content: MutableList<Item> = mutableListOf()
        for (i in map) {
            content.add(Item(i.key, i.value))
        }
        return content
    }
}