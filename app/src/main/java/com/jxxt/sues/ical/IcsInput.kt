package com.jxxt.sues.ical

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import com.jxxt.sues.widget.Utils
import net.fortuna.ical4j.data.CalendarBuilder
import net.fortuna.ical4j.model.Component
import net.fortuna.ical4j.model.Parameter
import net.fortuna.ical4j.model.ParameterList
import net.fortuna.ical4j.model.Property
import net.fortuna.ical4j.model.component.CalendarComponent
import net.fortuna.ical4j.model.component.VAlarm
import net.fortuna.ical4j.model.component.VEvent
import net.fortuna.ical4j.util.MapTimeZoneCache
import org.jetbrains.anko.button
import org.jetbrains.anko.doAsync
import org.jetbrains.anko.linearLayout
import org.jetbrains.anko.sdk27.coroutines.onClick
import org.jetbrains.anko.textView
import java.io.ByteArrayOutputStream
import java.io.File
import java.io.FileInputStream
import java.io.InputStream
import java.util.regex.Matcher
import java.util.regex.Pattern


class IcsInput : Activity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        linearLayout {
            textView("点击按钮导入ics文件")
            button("导入") {
                onClick {
                    doAsync {
                        val intent = Intent(Intent.ACTION_GET_CONTENT)
                        //筛选ics类型文件
                        intent.type = "text/calendar"
                        intent.addCategory(Intent.CATEGORY_OPENABLE)
                        startActivityForResult(intent, 1)
                    }
                }
            }
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        val mContext = Utils.getContext()
        if (data == null) {
            return
        }
        val dataUri = data.data
        val path = dataUri!!.path
        println("wocccccccccccccccccccccccccccssacas$path")
        //储存的ics文件目录
        val icsStorePath = File(mContext.filesDir, "/icsSelf")
        doAsync {
            val inputStream = mContext.contentResolver.openInputStream(dataUri)
            val icsFileText = inputParseString(inputStream!!)
            icsStorePath.writeText(icsFileText)
            println(icsStorePath.readText())
            IcsToDateMap().b()
        }
    }

    private fun inputParseString(inputStream: InputStream): String {
        val swapStream = ByteArrayOutputStream()
        var ch: Int
        while (inputStream.read().also { ch = it } != -1) {
            swapStream.write(ch)
        }
        return swapStream.toString()
    }
}

class IcsToDateMap {

    fun b() {
        //ical4j配置TimeZoneCache
        System.setProperty("net.fortuna.ical4j.timezone.cache.impl", MapTimeZoneCache::class.java.name)
        val mContext = Utils.getContext()//全局cotext
        val icsFileInputStream = FileInputStream(File(mContext.filesDir, "/icsSelf"))
        val icsCalendar = CalendarBuilder().build(icsFileInputStream)
        val i: Iterator<*> = icsCalendar.getComponents<CalendarComponent>(Component.VEVENT).iterator()
        while (i.hasNext()) {
            val event = i.next() as VEvent
            // 开始时间
            println("开始时间：" + event.startDate.value)
            // 结束时间
            println("结束时间：" + event.endDate.value)
            if (null != event.getProperty<Property?>("DTSTART")) {
                val parameters: ParameterList = event.getProperty<Property>("DTSTART").parameters
                if (null != parameters.getParameter("VALUE")) {
                    println(parameters.getParameter<Parameter>("VALUE").value)
                }
            }
            // 主题
            println("主题：" + event.summary.value)
            // 地点
            if (null != event.location) {
                println("地点：" + event.location.value)
            }
            // 描述
            if (null != event.description) {
                println("描述：" + event.description.value)
            }
            // 创建时间
            if (null != event.created) {
                println("创建时间：" + event.created.value)
            }
            // 最后修改时间
            if (null != event.lastModified) {
                println("最后修改时间：" + event.lastModified.value)
            }
            // 重复规则
            if (null != event.getProperty<Property?>("RRULE")) {
                println("RRULE:" + event.getProperty<Property>("RRULE").value)
            }
            // 提前多久提醒
            val alrams: Iterator<*> = event.alarms.iterator()
            while (alrams.hasNext()) {
                val alarm = alrams.next() as VAlarm
                val p: Pattern = Pattern.compile("[^0-9]")
                val aheadTime = alarm.trigger.value
                val m: Matcher = p.matcher(aheadTime)
                val timeTemp: Int = Integer.valueOf(m.replaceAll("").trim())
                when {
                    aheadTime.endsWith("W") -> {
                        println("提前多久：" + timeTemp + "周")
                    }
                    aheadTime.endsWith("D") -> {
                        println("提前多久：" + timeTemp + "天")
                    }
                    aheadTime.endsWith("H") -> {
                        println("提前多久：" + timeTemp + "小时")
                    }
                    aheadTime.endsWith("M") -> {
                        println("提前多久：" + timeTemp + "分钟")
                    }
                    aheadTime.endsWith("S") -> {
                        println("提前多久：" + timeTemp + "秒")
                    }
                }
            }
            println("----------------------------")
        }


    }
}
