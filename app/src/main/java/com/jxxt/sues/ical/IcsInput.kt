package com.jxxt.sues.ical

import android.Manifest
import android.annotation.SuppressLint
import android.content.ContentUris
import android.content.ContentValues
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.database.Cursor
import android.graphics.Color
import android.net.Uri
import android.os.Bundle
import android.provider.CalendarContract
import android.text.TextUtils
import android.view.View
import android.widget.Button
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
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
import permissions.dispatcher.NeedsPermission
import permissions.dispatcher.RuntimePermissions
import java.io.ByteArrayOutputStream
import java.io.File
import java.io.FileInputStream
import java.io.InputStream
import java.text.SimpleDateFormat
import java.util.*
import java.util.regex.Matcher
import java.util.regex.Pattern

data class MyEvent(var start: Long, var end: Long, var theme: String, var discri: String, var remindersMinutes: Int = 15, var location: String = "null")

@RuntimePermissions
class IcsInput : AppCompatActivity() {
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
                    toast("导入完成 请返回上一级")
                }
            }
            toSystemCalendarButton = button("导入到系统日历") {
                visibility = View.INVISIBLE
                onClick {
                    alert {
                        customView {
                            textView("确定导入到系统日历吗 有可能操作无法撤销")
                            positiveButton("导入") {
                                toast("正在导入")
                                doAsync {
                                    myEventList.forEach {
                                        addEventWithPermissionCheck(Utils.getContext(), it)
                                    }
                                }
                            }
                        }
                    }.show()
                }
            }
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (data == null) {
            return
        }
        val dataUri = data.data
        val path = dataUri!!.path
        println("wocccccccccccccccccccccccccccssacas$path")
        //储存的ics文件目录
        val icsStorePath = File(filesDir, "/icsSelf")
        doAsync {
            val inputStream = contentResolver.openInputStream(dataUri)
            val icsFileText = inputParseString(inputStream!!)
            icsStorePath.writeText(icsFileText)
            println(icsStorePath.readText())
            myEventList = IcsToDateMap().b()
            uiThread {
                toSystemCalendarButton.visibility = View.VISIBLE
                toMyClassTableButton.visibility = View.VISIBLE
                textV.text = "已导入ics文件，请继续操作"
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

    //添加日历事件
    @NeedsPermission(Manifest.permission.READ_CALENDAR)
    fun addEvent(context: Context, input: MyEvent) {
        if (CalendarProviderManager.isEventAlreadyExist(context, input.start, input.end, input.theme)) {
            return
        }
        CalendarProviderManager.addCalendarEvent(
            context,
            CalendarEvent(input.theme, input.discri, input.location, input.start, input.end, input.remindersMinutes, null)
        )
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
            val myEvent = MyEvent(getDateTime(start), getDateTime(end), theme, discri, alarmTime / 60000, place)
            returnList += myEvent
        }
        return returnList
    }
}


private fun getDateTime(tmp: String): Long {
    val time0 = SimpleDateFormat("yyyyMMdd-HHmmss", Locale.CHINA).parse(tmp.substring(0, 14).replace("T", "-"))
    return time0!!.time
}


fun checkCalendarPermission(): Boolean {
    return PackageManager.PERMISSION_GRANTED == Utils.getContext().checkSelfPermission("android.permission.WRITE_CALENDAR")
}

// 系统日历工具
object CalendarProviderManager {

    private val builder = StringBuilder()
    // ----------------------------- 日历账户名相关设置 -----------------------------------
    /*
           TIP: 要向系统日历插入事件,前提系统中必须存在至少1个日历账户
         */
// ----------------------- 创建日历账户时账户名使用 ---------------------------
    private var calendarName = "ClassTable0"
    private var calendarAccountName = "MyClassTable"
    private var calendarDisplayName = "课程表"
    // ------------------------------- 日历账户 -----------------------------------
    /**
     * 获取日历账户ID(若没有则会自动创建一个)
     *
     * @return success: 日历账户ID  failed : -1  permission deny : -2
     */
    @JvmStatic
    fun obtainCalendarAccountID(context: Context): Long {
        val calID = checkCalendarAccount(context)
        return if (calID >= 0) {
            calID
        } else {
            createCalendarAccount(context)
        }
    }

    /**
     * 检查是否存在日历账户
     *
     * @return 存在：日历账户ID  不存在：-1
     */
    @SuppressLint("MissingPermission")
    fun checkCalendarAccount(context: Context): Long {
        context.contentResolver.query(
            CalendarContract.Calendars.CONTENT_URI,
            null, null, null, null
        ).use { cursor ->
            // 不存在日历账户
            if (null == cursor) {
                return -1
            }
            val count = cursor.count
            // 存在日历账户，获取第一个账户的ID
            return if (count > 0) {
                cursor.moveToFirst()
                cursor.getInt(cursor.getColumnIndex(CalendarContract.Calendars._ID)).toLong()
            } else {
                -1
            }
        }
    }

    /**
     * 创建一个新的日历账户
     *
     * @return success：ACCOUNT ID , create failed：-1 , permission deny：-2
     */
    private fun createCalendarAccount(context: Context): Long { // 系统日历表
        var uri = CalendarContract.Calendars.CONTENT_URI
        // 要创建的账户
        val accountUri: Uri?
        // 开始组装账户数据
        val account = ContentValues()
        // 账户类型：本地
        // 在添加账户时，如果账户类型不存在系统中，则可能该新增记录会被标记为脏数据而被删除
        // 设置为ACCOUNT_TYPE_LOCAL可以保证在不存在账户类型时，该新增数据不会被删除
        account.put(CalendarContract.Calendars.ACCOUNT_TYPE, CalendarContract.ACCOUNT_TYPE_LOCAL)
        // 日历在表中的名称
        account.put(CalendarContract.Calendars.NAME, calendarName)
        // 日历账户的名称
        account.put(CalendarContract.Calendars.ACCOUNT_NAME, calendarAccountName)
        // 账户显示的名称
        account.put(CalendarContract.Calendars.CALENDAR_DISPLAY_NAME, calendarDisplayName)
        // 日历的颜色
        account.put(CalendarContract.Calendars.CALENDAR_COLOR, Color.parseColor("#515bd4"))
        // 用户对此日历的获取使用权限等级
        account.put(CalendarContract.Calendars.CALENDAR_ACCESS_LEVEL, CalendarContract.Calendars.CAL_ACCESS_OWNER)
        // 设置此日历可见
        account.put(CalendarContract.Calendars.VISIBLE, 1)
        // 日历时区
        account.put(CalendarContract.Calendars.CALENDAR_TIME_ZONE, TimeZone.getDefault().id)
        // 可以修改日历时区
        account.put(CalendarContract.Calendars.CAN_MODIFY_TIME_ZONE, 1)
        // 同步此日历到设备上
        account.put(CalendarContract.Calendars.SYNC_EVENTS, 1)
        // 拥有者的账户
        account.put(CalendarContract.Calendars.OWNER_ACCOUNT, calendarAccountName)
        // 可以响应事件
        account.put(CalendarContract.Calendars.CAN_ORGANIZER_RESPOND, 1)
        // 单个事件设置的最大的提醒数
        account.put(CalendarContract.Calendars.MAX_REMINDERS, 8)
        // 设置允许提醒的方式
        account.put(CalendarContract.Calendars.ALLOWED_REMINDERS, "0,1,2,3,4")
        // 设置日历支持的可用性类型
        account.put(CalendarContract.Calendars.ALLOWED_AVAILABILITY, "0,1,2")
        // 设置日历允许的出席者类型
        account.put(CalendarContract.Calendars.ALLOWED_ATTENDEE_TYPES, "0,1,2")
        /*
            TIP: 修改或添加ACCOUNT_NAME只能由SYNC_ADAPTER调用
            对uri设置CalendarContract.CALLER_IS_SYNCADAPTER为true,即标记当前操作为SYNC_ADAPTER操作
            在设置CalendarContract.CALLER_IS_SYNCADAPTER为true时,必须带上参数ACCOUNT_NAME和ACCOUNT_TYPE(任意)
         */uri = uri.buildUpon()
            .appendQueryParameter(CalendarContract.CALLER_IS_SYNCADAPTER, "true")
            .appendQueryParameter(CalendarContract.Calendars.ACCOUNT_NAME, calendarAccountName)
            .appendQueryParameter(
                CalendarContract.Calendars.ACCOUNT_TYPE,
                CalendarContract.Calendars.CALENDAR_LOCATION
            )
            .build()
        accountUri =  // 检查日历权限
            if (PackageManager.PERMISSION_GRANTED == context.checkSelfPermission(
                    "android.permission.WRITE_CALENDAR"
                )
            ) {
                context.contentResolver.insert(uri, account)
            } else {
                return -2
            }
        return if (accountUri == null) -1 else ContentUris.parseId(accountUri)
    }

    /**
     * 删除创建的日历账户
     *
     * @return -2: permission deny  0: No designated account  1: delete success
     */
    fun deleteCalendarAccountByName(context: Context): Int {
        Util.checkContextNull(context)
        val deleteCount: Int
        val uri = CalendarContract.Calendars.CONTENT_URI
        val selection = ("((" + CalendarContract.Calendars.ACCOUNT_NAME + " = ?) AND ("
                + CalendarContract.Calendars.ACCOUNT_TYPE + " = ?))")
        val selectionArgs = arrayOf(calendarAccountName, CalendarContract.ACCOUNT_TYPE_LOCAL)
        deleteCount =
            if (PackageManager.PERMISSION_GRANTED == context.checkSelfPermission(
                    "android.permission.WRITE_CALENDAR"
                )
            ) {
                context.contentResolver.delete(uri, selection, selectionArgs)
            } else {
                return -2
            }
        return deleteCount
    }
    // ------------------------------- 添加日历事件 -----------------------------------
    /**
     * 添加日历事件
     *
     * @param calendarEvent 日历事件(详细参数说明请参看[CalendarEvent]构造方法)
     * @return 0: success  -1: failed  -2: permission deny
     */
    @JvmStatic
    fun addCalendarEvent(context: Context, calendarEvent: CalendarEvent): Int {
        /*
            TIP: 插入一个新事件的规则：
             1.  必须包含CALENDAR_ID和DTSTART字段
             2.  必须包含EVENT_TIMEZONE字段,使用TimeZone.getDefault().getID()方法获取默认时区
             3.  对于非重复发生的事件,必须包含DTEND字段
             4.  对重复发生的事件,必须包含一个附加了RRULE或RDATE字段的DURATION字段
         */
        Util.checkContextNull(context)
        // 获取日历账户ID，也就是要将事件插入到的账户
        val calID = obtainCalendarAccountID(context)
        // 系统日历事件表
        val uri1 = CalendarContract.Events.CONTENT_URI
        // 创建的日历事件
        val eventUri: Uri?
        // 系统日历事件提醒表
        val uri2 = CalendarContract.Reminders.CONTENT_URI
        // 创建的日历事件提醒
        val reminderUri: Uri?
        // 开始组装事件数据
        val event = ContentValues()
        // 事件要插入到的日历账户
        event.put(CalendarContract.Events.CALENDAR_ID, calID)
        setupEvent(calendarEvent, event)
        eventUri =  // 判断权限
            if (PackageManager.PERMISSION_GRANTED == context.checkSelfPermission("android.permission.WRITE_CALENDAR")) {
                context.contentResolver.insert(uri1, event)
            } else return -2
        if (null == eventUri) {
            return -1
        }
        if (-2 != calendarEvent.advanceTime) { // 获取事件ID
            val eventID = ContentUris.parseId(eventUri)
            // 开始组装事件提醒数据
            val reminders = ContentValues()
            // 此提醒所对应的事件ID
            reminders.put(CalendarContract.Reminders.EVENT_ID, eventID)
            // 设置提醒提前的时间(0：准时  -1：使用系统默认)
            reminders.put(CalendarContract.Reminders.MINUTES, calendarEvent.advanceTime)
            // 设置事件提醒方式为通知警报
            reminders.put(CalendarContract.Reminders.METHOD, CalendarContract.Reminders.METHOD_ALERT)
            reminderUri = context.contentResolver.insert(uri2, reminders)
            if (null == reminderUri) {
                return -1
            }
        }
        return 0
    }
    // ------------------------------- 更新日历事件 -----------------------------------
    /**
     * 更新指定ID的日历事件
     *
     * @param newCalendarEvent 更新的日历事件
     * @return -2: permission deny  else success
     */
    fun updateCalendarEvent(context: Context, eventID: Long, newCalendarEvent: CalendarEvent): Int {
        Util.checkContextNull(context)
        val updatedCount1: Int
        val uri1 = CalendarContract.Events.CONTENT_URI
        val uri2 = CalendarContract.Reminders.CONTENT_URI
        val event = ContentValues()
        setupEvent(newCalendarEvent, event)
        // 更新匹配条件
        val selection1 = "(" + CalendarContract.Events._ID + " = ?)"
        val selectionArgs1 = arrayOf(eventID.toString())
        updatedCount1 =
            if (PackageManager.PERMISSION_GRANTED == context.checkSelfPermission("android.permission.WRITE_CALENDAR")) {
                context.contentResolver.update(uri1, event, selection1, selectionArgs1)
            } else {
                return -2
            }
        val reminders = ContentValues()
        reminders.put(CalendarContract.Reminders.MINUTES, newCalendarEvent.advanceTime)
        reminders.put(CalendarContract.Reminders.METHOD, CalendarContract.Reminders.METHOD_ALERT)
        // 更新匹配条件
        val selection2 = "(" + CalendarContract.Reminders.EVENT_ID + " = ?)"
        val selectionArgs2 = arrayOf(eventID.toString())
        val updatedCount2 = context.contentResolver.update(uri2, reminders, selection2, selectionArgs2)
        return (updatedCount1 + updatedCount2) / 2
    }

    /**
     * 更新指定ID事件的起始时间
     *
     * @return If successfully returns 1
     */
    fun updateCalendarEventTime(
        context: Context, eventID: Long, newBeginTime: Long,
        newEndTime: Long
    ): Int {
        Util.checkContextNull(context)
        val uri = CalendarContract.Events.CONTENT_URI
        // 新的数据
        val event = ContentValues()
        event.put(CalendarContract.Events.DTSTART, newBeginTime)
        event.put(CalendarContract.Events.DTEND, newEndTime)
        // 匹配条件
        val selection = "(" + CalendarContract.Events._ID + " = ?)"
        val selectionArgs = arrayOf(eventID.toString())
        return if (checkCalendarPermission()) context.contentResolver.update(uri, event, selection, selectionArgs) else -1
    }

    /**
     * 更新指定ID事件的常用信息(标题、描述、地点)
     *
     * @return If successfully returns 1
     */
    fun updateCalendarEventCommonInfo(
        context: Context, eventID: Long, newEventTitle: String?,
        newEventDes: String?, newEventLocation: String?
    ): Int {
        Util.checkContextNull(context)
        val uri = CalendarContract.Events.CONTENT_URI
        // 新的数据
        val event = ContentValues()
        event.put(CalendarContract.Events.TITLE, newEventTitle)
        event.put(CalendarContract.Events.DESCRIPTION, newEventDes)
        event.put(CalendarContract.Events.EVENT_LOCATION, newEventLocation)
        // 匹配条件
        val selection = "(" + CalendarContract.Events._ID + " = ?)"
        val selectionArgs = arrayOf(eventID.toString())
        return if (PackageManager.PERMISSION_GRANTED == context.checkSelfPermission("android.permission.WRITE_CALENDAR"))
            context.contentResolver.update(uri, event, selection, selectionArgs) else -1
    }

    /**
     * 更新指定ID事件的提醒方式
     *
     * @return If successfully returns 1
     */
    private fun updateCalendarEventReminder(context: Context, eventID: Long, newAdvanceTime: Long): Int {
        Util.checkContextNull(context)
        val uri = CalendarContract.Reminders.CONTENT_URI
        val reminders = ContentValues()
        reminders.put(CalendarContract.Reminders.MINUTES, newAdvanceTime)
        // 更新匹配条件
        val selection2 = "(" + CalendarContract.Reminders.EVENT_ID + " = ?)"
        val selectionArgs2 = arrayOf(eventID.toString())
        return if (PackageManager.PERMISSION_GRANTED == context.checkSelfPermission("android.permission.WRITE_CALENDAR"))
            context.contentResolver.update(uri, reminders, selection2, selectionArgs2) else -1
    }

    /**
     * 更新指定ID事件的提醒重复规则
     *
     * @return If successfully returns 1
     */
    private fun updateCalendarEventRRule(context: Context, eventID: Long, newRRule: String): Int {
        Util.checkContextNull(context)
        val uri = CalendarContract.Events.CONTENT_URI
        // 新的数据
        val event = ContentValues()
        event.put(CalendarContract.Events.RRULE, newRRule)
        // 匹配条件
        val selection = "(" + CalendarContract.Events._ID + " = ?)"
        val selectionArgs = arrayOf(eventID.toString())
        return if (PackageManager.PERMISSION_GRANTED == context.checkSelfPermission("android.permission.WRITE_CALENDAR"))
            context.contentResolver.update(uri, event, selection, selectionArgs) else -1
    }
    // ------------------------------- 删除日历事件 -----------------------------------
    /**
     * 删除日历事件
     *
     * @param eventID 事件ID
     * @return -2: permission deny  else success
     */
    @JvmStatic
    fun deleteCalendarEvent(context: Context, eventID: Long): Int {
        Util.checkContextNull(context)
        val deletedCount1: Int
        val uri1 = CalendarContract.Events.CONTENT_URI
        val uri2 = CalendarContract.Reminders.CONTENT_URI
        // 删除匹配条件
        val selection = "(" + CalendarContract.Events._ID + " = ?)"
        val selectionArgs = arrayOf(eventID.toString())
        deletedCount1 =
            if (PackageManager.PERMISSION_GRANTED == context.checkSelfPermission(
                    "android.permission.WRITE_CALENDAR"
                )
            ) {
                context.contentResolver.delete(uri1, selection, selectionArgs)
            } else {
                return -2
            }
        // 删除匹配条件
        val selection2 = "(" + CalendarContract.Reminders.EVENT_ID + " = ?)"
        val selectionArgs2 = arrayOf(eventID.toString())
        val deletedCount2 = context.contentResolver.delete(uri2, selection2, selectionArgs2)
        return (deletedCount1 + deletedCount2) / 2
    }
    // ------------------------------- 查询日历事件 -----------------------------------
    /**
     * 查询指定日历账户下的所有事件
     *
     * @return If failed return null else return List<CalendarEvent>
    </CalendarEvent> */
    @JvmStatic
    fun queryAccountEvent(context: Context, calID: Long): List<CalendarEvent>? {
        Util.checkContextNull(context)
        val projection = arrayOf(
            CalendarContract.Events.CALENDAR_ID,  // 在表中的列索引0
            CalendarContract.Events.TITLE,  // 在表中的列索引1
            CalendarContract.Events.DESCRIPTION,  // 在表中的列索引2
            CalendarContract.Events.EVENT_LOCATION,  // 在表中的列索引3
            CalendarContract.Events.DISPLAY_COLOR,  // 在表中的列索引4
            CalendarContract.Events.STATUS,  // 在表中的列索引5
            CalendarContract.Events.DTSTART,  // 在表中的列索引6
            CalendarContract.Events.DTEND,  // 在表中的列索引7
            CalendarContract.Events.DURATION,  // 在表中的列索引8
            CalendarContract.Events.EVENT_TIMEZONE,  // 在表中的列索引9
            CalendarContract.Events.EVENT_END_TIMEZONE,  // 在表中的列索引10
            CalendarContract.Events.ALL_DAY,  // 在表中的列索引11
            CalendarContract.Events.ACCESS_LEVEL,  // 在表中的列索引12
            CalendarContract.Events.AVAILABILITY,  // 在表中的列索引13
            CalendarContract.Events.HAS_ALARM,  // 在表中的列索引14
            CalendarContract.Events.RRULE,  // 在表中的列索引15
            CalendarContract.Events.RDATE,  // 在表中的列索引16
            CalendarContract.Events.HAS_ATTENDEE_DATA,  // 在表中的列索引17
            CalendarContract.Events.LAST_DATE,  // 在表中的列索引18
            CalendarContract.Events.ORGANIZER,  // 在表中的列索引19
            CalendarContract.Events.IS_ORGANIZER,  // 在表中的列索引20
            CalendarContract.Events._ID // 在表中的列索引21
        )
        // 事件匹配
        val uri = CalendarContract.Events.CONTENT_URI
        val uri2 = CalendarContract.Reminders.CONTENT_URI
        val selection = "(" + CalendarContract.Events.CALENDAR_ID + " = ?)"
        val selectionArgs = arrayOf(calID.toString())
        val cursor: Cursor?
        cursor =
            if (PackageManager.PERMISSION_GRANTED == context.checkSelfPermission(
                    "android.permission.READ_CALENDAR"
                )
            ) {
                context.contentResolver.query(
                    uri, projection, selection,
                    selectionArgs, null
                )
            } else {
                return null
            }
        if (null == cursor) {
            return null
        }
        // 查询结果
        val result: MutableList<CalendarEvent> = ArrayList()
        // 开始查询数据
        if (cursor.moveToFirst()) {
            do {
                val calendarEvent = CalendarEvent()
                result.add(calendarEvent)
                calendarEvent.id = cursor.getLong(
                    cursor.getColumnIndex(
                        CalendarContract.Events._ID
                    )
                )
                calendarEvent.calID = cursor.getLong(
                    cursor.getColumnIndex(
                        CalendarContract.Events.CALENDAR_ID
                    )
                )
                calendarEvent.title = cursor.getString(
                    cursor.getColumnIndex(
                        CalendarContract.Events.TITLE
                    )
                )
                calendarEvent.description = cursor.getString(
                    cursor.getColumnIndex(
                        CalendarContract.Events.DESCRIPTION
                    )
                )
                calendarEvent.eventLocation = cursor.getString(
                    cursor.getColumnIndex(
                        CalendarContract.Events.EVENT_LOCATION
                    )
                )
                calendarEvent.displayColor = cursor.getInt(
                    cursor.getColumnIndex(
                        CalendarContract.Events.DISPLAY_COLOR
                    )
                )
                calendarEvent.status = cursor.getInt(
                    cursor.getColumnIndex(
                        CalendarContract.Events.STATUS
                    )
                )
                calendarEvent.start = cursor.getLong(
                    cursor.getColumnIndex(
                        CalendarContract.Events.DTSTART
                    )
                )
                calendarEvent.end = cursor.getLong(
                    cursor.getColumnIndex(
                        CalendarContract.Events.DTEND
                    )
                )
                calendarEvent.duration = cursor.getString(
                    cursor.getColumnIndex(
                        CalendarContract.Events.DURATION
                    )
                )
                calendarEvent.eventTimeZone = cursor.getString(
                    cursor.getColumnIndex(
                        CalendarContract.Events.EVENT_TIMEZONE
                    )
                )
                calendarEvent.eventEndTimeZone = cursor.getString(
                    cursor.getColumnIndex(
                        CalendarContract.Events.EVENT_END_TIMEZONE
                    )
                )
                calendarEvent.allDay = cursor.getInt(
                    cursor.getColumnIndex(
                        CalendarContract.Events.ALL_DAY
                    )
                )
                calendarEvent.accessLevel = cursor.getInt(
                    cursor.getColumnIndex(
                        CalendarContract.Events.ACCESS_LEVEL
                    )
                )
                calendarEvent.availability = cursor.getInt(
                    cursor.getColumnIndex(
                        CalendarContract.Events.AVAILABILITY
                    )
                )
                calendarEvent.hasAlarm = cursor.getInt(
                    cursor.getColumnIndex(
                        CalendarContract.Events.HAS_ALARM
                    )
                )
                calendarEvent.rRule = cursor.getString(
                    cursor.getColumnIndex(
                        CalendarContract.Events.RRULE
                    )
                )
                calendarEvent.rDate = cursor.getString(
                    cursor.getColumnIndex(
                        CalendarContract.Events.RDATE
                    )
                )
                calendarEvent.hasAttendeeData = cursor.getInt(
                    cursor.getColumnIndex(
                        CalendarContract.Events.HAS_ATTENDEE_DATA
                    )
                )
                calendarEvent.lastDate = cursor.getInt(
                    cursor.getColumnIndex(
                        CalendarContract.Events.LAST_DATE
                    )
                )
                calendarEvent.organizer0 = cursor.getString(
                    cursor.getColumnIndex(
                        CalendarContract.Events.ORGANIZER
                    )
                )
                calendarEvent.isOrganizer = cursor.getString(
                    cursor.getColumnIndex(
                        CalendarContract.Events.IS_ORGANIZER
                    )
                )
                // ----------------------- 开始查询事件提醒 ------------------------------
                val projection1 = arrayOf(
                    CalendarContract.Reminders._ID,  // 在表中的列索引0
                    CalendarContract.Reminders.EVENT_ID,  // 在表中的列索引1
                    CalendarContract.Reminders.MINUTES,  // 在表中的列索引2
                    CalendarContract.Reminders.METHOD
                )
                val selection2 = "(" + CalendarContract.Reminders.EVENT_ID + " = ?)"
                val selectionArgs2 = arrayOf(calendarEvent.id.toString())
                context.contentResolver.query(
                    uri2, projection1,
                    selection2, selectionArgs2, null
                ).use { reminderCursor ->
                    if (null != reminderCursor) {
                        if (reminderCursor.moveToFirst()) {
                            val reminders: MutableList<CalendarEvent.EventReminders> = ArrayList()
                            do {
                                val reminders1 = CalendarEvent.EventReminders()
                                reminders.add(reminders1)
                                reminders1.reminderId = reminderCursor.getLong(
                                    reminderCursor.getColumnIndex(CalendarContract.Reminders._ID)
                                )
                                reminders1.reminderEventID = reminderCursor.getLong(
                                    reminderCursor.getColumnIndex(CalendarContract.Reminders.EVENT_ID)
                                )
                                reminders1.reminderMinute = reminderCursor.getInt(
                                    reminderCursor.getColumnIndex(CalendarContract.Reminders.MINUTES)
                                )
                                reminders1.reminderMethod = reminderCursor.getInt(
                                    reminderCursor.getColumnIndex(CalendarContract.Reminders.METHOD)
                                )
                            } while (reminderCursor.moveToNext())
                            calendarEvent.reminders = reminders
                        }
                    }
                }
            } while (cursor.moveToNext())
            cursor.close()
        }
        return result
    }

    /**
     * 判断日历账户中是否已经存在此事件
     *
     * @param begin 事件开始时间
     * @param end   事件结束时间
     * @param title 事件标题
     */
    @JvmStatic
    fun isEventAlreadyExist(context: Context, begin: Long, end: Long, title: String): Boolean {
        val projection = arrayOf(
            CalendarContract.Instances.BEGIN,
            CalendarContract.Instances.END,
            CalendarContract.Instances.TITLE
        )
        val cursor = CalendarContract.Instances.query(
            context.contentResolver, projection, begin, end, title
        )
        return (null != cursor && cursor.moveToFirst()
                && cursor.getString(
            cursor.getColumnIndex(CalendarContract.Instances.TITLE)
        ) == title)
    }
    // ------------------------------- 日历事件相关 -----------------------------------
    /**
     * 组装日历事件
     */
    private fun setupEvent(calendarEvent: CalendarEvent, event: ContentValues) { // 事件开始时间
        event.put(CalendarContract.Events.DTSTART, calendarEvent.start)
        // 事件结束时间
        event.put(CalendarContract.Events.DTEND, calendarEvent.end)
        // 事件标题
        event.put(CalendarContract.Events.TITLE, calendarEvent.title)
        // 事件描述(对应手机系统日历备注栏)
        event.put(CalendarContract.Events.DESCRIPTION, calendarEvent.description)
        // 事件地点
        event.put(CalendarContract.Events.EVENT_LOCATION, calendarEvent.eventLocation)
        // 事件时区
        event.put(CalendarContract.Events.EVENT_TIMEZONE, TimeZone.getDefault().id)
        // 定义事件的显示，默认即可
        event.put(CalendarContract.Events.ACCESS_LEVEL, CalendarContract.Events.ACCESS_DEFAULT)
        // 事件的状态
        event.put(CalendarContract.Events.STATUS, 0)
        // 设置事件提醒警报可用
        event.put(CalendarContract.Events.HAS_ALARM, 1)
        // 设置事件忙
        event.put(CalendarContract.Events.AVAILABILITY, CalendarContract.Events.AVAILABILITY_BUSY)
        if (null != calendarEvent.rRule) { // 设置事件重复规则
            event.put(
                CalendarContract.Events.RRULE,
                getFullRRuleForRRule(
                    calendarEvent.rRule!!,
                    calendarEvent.start, calendarEvent.end
                )
            )
        }
    }

    /**
     * 获取完整的重复规则(包含终止时间)
     *
     * @param rRule     重复规则
     * @param beginTime 开始时间
     * @param endTime   结束时间
     */
    private fun getFullRRuleForRRule(rRule: String, beginTime: Long, endTime: Long): String {
        builder.delete(0, builder.length)
        return when (rRule) {
            RRuleConstant.REPEAT_WEEKLY_BY_MO, RRuleConstant.REPEAT_WEEKLY_BY_TU, RRuleConstant.REPEAT_WEEKLY_BY_WE, RRuleConstant.REPEAT_WEEKLY_BY_TH, RRuleConstant.REPEAT_WEEKLY_BY_FR, RRuleConstant.REPEAT_WEEKLY_BY_SA, RRuleConstant.REPEAT_WEEKLY_BY_SU -> builder.append(rRule).append(Util.getFinalRRuleMode(endTime)).toString()
            RRuleConstant.REPEAT_CYCLE_WEEKLY -> builder.append(rRule).append(Util.getWeekForDate(beginTime)).append("; UNTIL = ")
                .append(Util.getFinalRRuleMode(endTime)).toString()
            RRuleConstant.REPEAT_CYCLE_MONTHLY -> builder.append(rRule).append(Util.getDayOfMonth(beginTime))
                .append("; UNTIL = ").append(Util.getFinalRRuleMode(endTime)).toString()
            else -> rRule
        }
    }

    /**
     * 删除日历事件
     */
    fun deleteCalendarEvent(context: Context?, title: String) {
        val eventUrl = "content://com.android.calendar/events"
        if (context == null) {
            return
        }
        val eventCursor: Cursor? = context.contentResolver.query(Uri.parse(eventUrl), null, null, null, null)
        eventCursor.use { eventCursor1 ->
            if (eventCursor1 == null) { //查询返回空值
                return
            }
            if (eventCursor1.count > 0) { //遍历所有事件，找到title跟需要查询的title一样的项
                eventCursor1.moveToFirst()
                while (!eventCursor1.isAfterLast) {
                    val eventTitle: String = eventCursor1.getString(eventCursor1.getColumnIndex("title"))
                    if (!TextUtils.isEmpty(title) && title == eventTitle) {
                        val id = eventCursor1.getLong(eventCursor1.getColumnIndex(CalendarContract.Calendars._ID)) //取得id
                        val deleteUri: Uri = ContentUris.withAppendedId(Uri.parse(eventUrl), id)
                        val rows: Int = context.contentResolver.delete(deleteUri, null, null)
                        if (rows == -1) { //事件删除失败
                            return
                        }
                    }
                    eventCursor1.moveToNext()
                }
            }
        }
    }
}