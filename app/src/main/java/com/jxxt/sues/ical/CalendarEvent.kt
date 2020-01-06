package com.jxxt.sues.ical

/**
 * 日历事件
 *
 *
 * Created by KYLE on 2019/3/4 - 9:53
 */
class CalendarEvent {
    // ----------------------- 事件属性 -----------------------
    /**
     * 事件在表中的ID
     */
    var id: Long = 0
    /**
     * 事件所属日历账户的ID
     */
    var calID: Long = 0
    var title: String? = null
    var description: String? = null
    var eventLocation: String? = null
    var displayColor = 0
    var status = 0
    var start: Long = 0
    var end: Long = 0
    var duration: String? = null
    var eventTimeZone: String? = null
    var eventEndTimeZone: String? = null
    var allDay = 0
    var accessLevel = 0
    var availability = 0
    var hasAlarm = 0
    var rRule: String? = null
    var rDate: String? = null
    var hasAttendeeData = 0
    var lastDate = 0
    var organizer0: String? = null
    var isOrganizer: String? = null
    // ----------------------------------------------------------------------------------------
    /**
     * 注：此属性不属于CalendarEvent
     * 这里只是为了方便构造方法提供事件提醒时间
     */
    var advanceTime = 0
    // ----------------------------------------------------------------------------------------
// ----------------------- 事件提醒属性 -----------------------
    var reminders: List<EventReminders>? = null

    internal constructor() {}
    /**
     * 用于方便添加完整日历事件提供一个构造方法
     *
     * @param title         事件标题
     * @param description   事件描述
     * @param eventLocation 事件地点
     * @param start         事件开始时间
     * @param end           事件结束时间  If is not a repeat event, this param is must need else null
     * @param advanceTime   事件提醒时间
     * (If you don't need to remind the incoming parameters -2)
     * @param rRule         事件重复规则    `null` if dose not need
     */
    constructor(
        title: String?, description: String?, eventLocation: String?,
        start: Long, end: Long, advanceTime: Int, rRule: String?
    ) {
        this.title = title
        this.description = description
        this.eventLocation = eventLocation
        this.start = start
        this.end = end
        this.advanceTime = advanceTime
        this.rRule = rRule
    }

    fun getIsOrganizer(): String? {
        return isOrganizer
    }

    fun setIsOrganizer(isOrganizer: String?) {
        this.isOrganizer = isOrganizer
    }

    override fun toString(): String {
        return "CalendarEvent{" +
                "\n id=" + id +
                "\n calID=" + calID +
                "\n title='" + title + '\'' +
                "\n description='" + description + '\'' +
                "\n eventLocation='" + eventLocation + '\'' +
                "\n displayColor=" + displayColor +
                "\n status=" + status +
                "\n start=" + start +
                "\n end=" + end +
                "\n duration='" + duration + '\'' +
                "\n eventTimeZone='" + eventTimeZone + '\'' +
                "\n eventEndTimeZone='" + eventEndTimeZone + '\'' +
                "\n allDay=" + allDay +
                "\n accessLevel=" + accessLevel +
                "\n availability=" + availability +
                "\n hasAlarm=" + hasAlarm +
                "\n rRule='" + rRule + '\'' +
                "\n rDate='" + rDate + '\'' +
                "\n hasAttendeeData=" + hasAttendeeData +
                "\n lastDate=" + lastDate +
                "\n organizer='" + organizer0 + '\'' +
                "\n isOrganizer='" + isOrganizer + '\'' +
                "\n reminders=" + reminders +
                '}'
    }

    override fun hashCode(): Int {
        return (id * 37 + calID).toInt()
    }

    /**
     * 事件提醒
     */
    class EventReminders {
        // ----------------------- 事件提醒属性 -----------------------
        var reminderId: Long = 0
        var reminderEventID: Long = 0
        var reminderMinute = 0
        var reminderMethod = 0

    }
}