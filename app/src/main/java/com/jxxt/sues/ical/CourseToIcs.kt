package com.jxxt.sues.ical

import com.jxxt.sues.Item
import com.jxxt.sues.widget.Utils
import net.fortuna.ical4j.data.CalendarOutputter
import net.fortuna.ical4j.model.*
import net.fortuna.ical4j.model.Calendar
import net.fortuna.ical4j.model.component.VEvent
import net.fortuna.ical4j.model.property.*
import java.io.File
import java.io.FileOutputStream

class ExIcs {
    lateinit var expath: File
    fun ex(list: List<Item>) {
        // 创建日历
        val calendar = Calendar()
        calendar.properties.add(ProdId("iCal4j 3.0.9//EN"))
        calendar.properties.add(Version.VERSION_2_0)
        calendar.properties.add(CalScale.GREGORIAN)
        for (i in list) {
            // 时间主题
            val summary = i.name
            // 新建普通事件
            val event = VEvent(DateTime(i.date.time), DateTime(i.date.time + 5400000L), summary)
            // 生成唯一标示
            event.properties.add(Uid("iCal4j"))
            // 添加事件
            calendar.components.add(event)
        }
        expath = File(Utils.getContext().getExternalFilesDir(null), "/suesjxxt/1.ics")
        if (!expath.exists()) expath.parentFile!!.mkdirs()
        expath.createNewFile()
        val fout = FileOutputStream(expath)
        val outputter = CalendarOutputter()
        outputter.output(calendar, fout)
    }
}


