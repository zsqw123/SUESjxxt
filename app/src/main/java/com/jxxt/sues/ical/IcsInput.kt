package com.jxxt.sues.ical

import android.Manifest
import android.annotation.SuppressLint
import android.app.Activity
import android.content.*
import android.content.pm.PackageManager
import android.database.Cursor
import android.graphics.Color
import android.icu.util.TimeZone
import android.net.Uri
import android.os.Bundle
import android.provider.CalendarContract
import android.view.View
import android.widget.Button
import android.widget.TextView
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.jxxt.sues.HomePage
import com.jxxt.sues.Item
import com.jxxt.sues.widget.Utils
import net.fortuna.ical4j.data.CalendarBuilder
import net.fortuna.ical4j.model.Component
import net.fortuna.ical4j.model.Property
import net.fortuna.ical4j.model.component.CalendarComponent
import net.fortuna.ical4j.model.component.VAlarm
import net.fortuna.ical4j.model.component.VEvent
import net.fortuna.ical4j.util.MapTimeZoneCache
import org.jetbrains.anko.*
import org.jetbrains.anko.sdk27.coroutines.onClick
import java.io.ByteArrayOutputStream
import java.io.File
import java.io.FileInputStream
import java.io.InputStream
import java.text.SimpleDateFormat
import java.util.*
import java.util.regex.Matcher
import java.util.regex.Pattern

data class MyEvent(var start: Long, var end: Long, var theme: String, var discri: String)

class IcsInput : Activity() {
    private lateinit var toSystemCalendarButton: Button
    private lateinit var toMyClassTableButton: Button
    private lateinit var myEventList: List<MyEvent>
    private lateinit var textV: TextView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        verticalLayout {
            linearLayout {
                textV = textView("点击按钮导入ics文件")
                button("导入") {
                    onClick {
                        textV.text = "正在导入..."
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
            toMyClassTableButton = button("导入到此软件课程表") {
                visibility = View.INVISIBLE
                onClick {
                    myEventList.forEach {
                        val item = mutableListOf<Item>()
                        val date = Date()
                        date.time = it.start
                        item += Item(date, it.discri + it.theme)
                    }
                }
            }
            toSystemCalendarButton = button("导入到系统日历") {
                visibility = View.INVISIBLE
                onClick {

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
            myEventList = IcsToDateMap().b()
            uiThread {
                toSystemCalendarButton.visibility = View.VISIBLE
                toMyClassTableButton.visibility = View.VISIBLE
                textV.text = "点击按钮导入ics文件"
            }
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

    fun b(): List<MyEvent> {
        val returnList = mutableListOf<MyEvent>()

        //ical4j配置TimeZoneCache
        System.setProperty("net.fortuna.ical4j.timezone.cache.impl", MapTimeZoneCache::class.java.name)
        val mContext = Utils.getContext()//全局cotext
        val icsFileInputStream = FileInputStream(File(mContext.filesDir, "/icsSelf"))
        val icsCalendar = CalendarBuilder().build(icsFileInputStream)
        val i: Iterator<*> = icsCalendar.getComponents<CalendarComponent>(Component.VEVENT).iterator()
        while (i.hasNext()) {
            val event = i.next() as VEvent
            // 开始时间
            val start = event.startDate.value
            // 结束时间
            val end = event.endDate.value
            // 主题
            val theme = event.summary.value
            // 地点
            val place = if (null != event.location) {
                event.location.value
            } else ""
            // 描述
            val discri = if (null != event.description) {
                event.description.value
            } else ""
            // 重复规则
            if (null != event.getProperty<Property?>("RRULE")) {
                println("RRULE:" + event.getProperty<Property>("RRULE").value)
            }
            // 提前多久提醒
            val alrams: Iterator<*> = event.alarms.iterator()
            var alarmTime = 0
            while (alrams.hasNext()) {
                val alarm = alrams.next() as VAlarm
                val p: Pattern = Pattern.compile("[^0-9]")
                val aheadTime = alarm.trigger.value
                val m: Matcher = p.matcher(aheadTime)
                val timeTemp: Int = Integer.valueOf(m.replaceAll("").trim())
                //提前提醒毫秒数
                alarmTime = when {
                    aheadTime.endsWith("W") -> {
                        timeTemp * 604800000
                    }
                    aheadTime.endsWith("D") -> {
                        timeTemp * 86400000
                    }
                    aheadTime.endsWith("H") -> {
                        timeTemp * 3600000
                    }
                    aheadTime.endsWith("M") -> {
                        timeTemp * 60000
                    }
                    aheadTime.endsWith("S") -> {
                        timeTemp * 1000
                    }
                    else -> 0
                }
            }
            val myEvent = MyEvent(getDateTime(start) - alarmTime, getDateTime(end) - alarmTime, theme, "$place $discri")
            returnList += myEvent
        }
        return returnList
    }
}

private fun getDateTime(tmp: String): Long {
    //20191224T081500
//    val time = Calendar.getInstance()
    val time0 = SimpleDateFormat("yyyyMMdd-HHmmss", Locale.CHINA).parse(tmp.substring(0, 14).replace("T", "-"))
//    time.set(
//        tmp.substring(0, 4).toInt(),
//        tmp.substring(4, 2).toInt(),
//        tmp.substring(6, 2).toInt(),
//        tmp.substring(9, 2).toInt(),
//        tmp.substring(11, 2).toInt(),
//        tmp.substring(13, 2).toInt()
//    )
    return time0!!.time
}

class DateToSystemCalendar {
    // ContentProvider的uri
    private val calendarUri: Uri = CalendarContract.Calendars.CONTENT_URI
    private val eventUri: Uri = CalendarContract.Events.CONTENT_URI
    private val reminderUri: Uri = CalendarContract.Reminders.CONTENT_URI

    private var contentResolver: ContentResolver? = null

    private val mContext = Utils.getContext()

    // 检查是否有日历表,有返回日历id，没有-1

    @SuppressLint("MissingPermission")
    private fun isHaveCalender(): Int {
        // 查询日历表的cursor
        val cursor: Cursor? = contentResolver!!.query(calendarUri, null, null, null, null)
        return if (cursor == null || 0 == cursor.count) {
            -1
        } else {
            // 如果有日历表
            cursor.use { cursor0 ->
                cursor0.moveToFirst()
                // 通过cursor返回日历表的第一行的属性值 第一个日历的id
                cursor0.getInt(cursor0.getColumnIndex(CalendarContract.Calendars._ID))
            }
        }
    }


    /**
     * 添加日历表
     */
    @SuppressLint("MissingPermission")
    private fun addCalendar(): Long {
        // 时区
        val timeZone: TimeZone = TimeZone.getDefault()
        // 配置Calendar
        val value = ContentValues()
        value.put(CalendarContract.Calendars.NAME, "我的日历表")
        value.put(CalendarContract.Calendars.ACCOUNT_NAME, "myAccount")
        value.put(CalendarContract.Calendars.ACCOUNT_TYPE, "myType")
        value.put(CalendarContract.Calendars.CALENDAR_DISPLAY_NAME, "ClassTable")
        value.put(CalendarContract.Calendars.VISIBLE, 1)
        value.put(CalendarContract.Calendars.CALENDAR_COLOR, Color.BLUE)
        value.put(CalendarContract.Calendars.CALENDAR_ACCESS_LEVEL, CalendarContract.Calendars.CAL_ACCESS_OWNER)
        value.put(CalendarContract.Calendars.SYNC_EVENTS, 1)
        value.put(CalendarContract.Calendars.CALENDAR_TIME_ZONE, timeZone.id)
        value.put(CalendarContract.Calendars.OWNER_ACCOUNT, "myAccount")
        value.put(CalendarContract.Calendars.CAN_ORGANIZER_RESPOND, 0)
        value.put(CalendarContract.CALLER_IS_SYNCADAPTER, true)
        // 插入calendar
        val insertCalendarUri: Uri? = contentResolver!!.insert(calendarUri, value)
        return if (insertCalendarUri == null) {
            -1
        } else {
            ContentUris.parseId(insertCalendarUri)
        }
    }

    /**
     * 添加日历事件
     */
    private fun addEvent(context: Context) {
        val checkSelfPermission = ContextCompat.checkSelfPermission(
            HomePage(),
            Manifest.permission.WRITE_CALENDAR
        )
        if (checkSelfPermission != PackageManager.PERMISSION_GRANTED) {
            //requset permission
            ActivityCompat.requestPermissions(
                HomePage(),
                arrayOf(Manifest.permission.WRITE_CALENDAR), 0
            )
        }

        // 创建contentResolver
        contentResolver = context.contentResolver
        // 日历表id
        var calendarId = isHaveCalender()
        if (calendarId == -1) {
            addCalendar()
            calendarId = isHaveCalender()
        }
        // startMillis
        val beginTime: Calendar = Calendar.getInstance()
        beginTime.set(2019, 8, 15)
        val startMillis: Long = beginTime.timeInMillis
        // endMillis
        val endTime: Calendar = Calendar.getInstance()
        endTime.set(2019, 8, 15)
        val endMillis: Long = endTime.timeInMillis
        // 准备event
        val valueEvent = ContentValues()
        valueEvent.put(CalendarContract.Events.DTSTART, startMillis)
        valueEvent.put(CalendarContract.Events.DTEND, endMillis)
        valueEvent.put(CalendarContract.Events.TITLE, "事件标题")
        valueEvent.put(CalendarContract.Events.DESCRIPTION, "事件描述")
        valueEvent.put(CalendarContract.Events.CALENDAR_ID, calendarId)
        valueEvent.put(CalendarContract.Events.EVENT_TIMEZONE, "Asia/Shanghai")
        // 添加event
        val insertEventUri: Uri? = contentResolver!!.insert(eventUri, valueEvent)
        if (insertEventUri == null) {
            mContext.toast("添加event失败")
        }
        // 添加提醒
        val eventId = ContentUris.parseId(insertEventUri!!)
        val valueReminder = ContentValues()
        valueReminder.put(CalendarContract.Reminders.EVENT_ID, eventId)
        valueReminder.put(CalendarContract.Reminders.MINUTES, 15)
        valueReminder.put(CalendarContract.Reminders.METHOD, CalendarContract.Reminders.METHOD_ALARM)
        val insertReminderUri: Uri? = contentResolver!!.insert(reminderUri, valueReminder)
        if (insertReminderUri == null) {
            mContext.toast("添加reminder失败")
        }
    }
}