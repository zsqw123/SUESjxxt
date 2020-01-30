package com.jxxt.sues.ui.settings

import android.Manifest
import android.graphics.Color
import android.os.Bundle
import android.text.InputType
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.FrameLayout
import androidx.core.graphics.ColorUtils
import androidx.fragment.app.Fragment
import com.jxxt.sues.*
import com.jxxt.sues.getpage.GetPage
import com.jxxt.sues.ical.*
import kotlinx.android.synthetic.main.settings.*
import me.everything.android.ui.overscroll.OverScrollDecoratorHelper
import org.jetbrains.anko.*
import org.jetbrains.anko.sdk27.coroutines.onClick
import org.jetbrains.anko.support.v4.*
import permissions.dispatcher.NeedsPermission
import permissions.dispatcher.RuntimePermissions
import java.io.File
import java.text.SimpleDateFormat
import java.util.*

@RuntimePermissions
class SettingsFragment : Fragment() {

    //read and judge
    private lateinit var colorString: File

    private val colorNameList = listOf("简洁白", "少女粉", "夜间模式", "姨妈红", "咸蛋黄", "早苗绿", "胖次蓝", "基佬紫")
    private val colorList = listOf("#F4F4F4", "#FA7298", "#2D2D2D", "#F44236", "#FEC107", "#8BC24A", "#2196F3", "#9C28B1")

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val myContext = context!!
        colorString = File(myContext.filesDir, "/color")
        return inflater.inflate(R.layout.settings, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        //定义Flies目录
        val myContext = context!!
        if (colorString.exists()) {
            val primeColor: Int = colorString.readText().toInt()
            val dark = ColorUtils.calculateLuminance(primeColor) <= 0.2
            val settingsTextList = listOf(text_import, text_week, text_about, text_theme, text_ex)
            if (dark) {
                settings_frag.backgroundColor = Color.BLACK
                settingsTextList.forEach {
                    it.textColor = Color.WHITE
                }
            } else {
                settings_frag.backgroundColor = Color.WHITE
                settingsTextList.forEach {
                    it.textColor = Color.BLACK
                }
            }
        }
        //回弹效果
        OverScrollDecoratorHelper.setUpOverScroll(settings_frag)
        //导入课程
        text_import.setOnClickListener {
            selector("选择导入方式", listOf("从ics文件导入", "从SUES(上海工程技术大学)课程表导入")) { _, i ->
                if (i == 0) startActivity<IcsInput>()
                if (i == 1) startActivity<GetPage>()
            }
        }
        //当前周
        text_week.setOnClickListener {
            alert {
                customView {
                    verticalLayout {
                        //标题
                        toolbar {
                            lparams(width = matchParent, height = wrapContent)
                            title = "设置当前周数(整数 可选正负)"
                        }
                        //输入框
                        val task = editText {
                            hint = "当前周"
                            inputType = InputType.TYPE_CLASS_NUMBER
                            padding = dip(20)
                        }
                        //button
                        negativeButton("OK(正周数)") {
                            if (task.text.toString().isEmpty()) {
                                toast("没当前周你玩个鸡儿??及你太美")
                            } else {
                                val weeknowWeek = task.text.toString().toInt()
                                val weeknowDate = Calendar.getInstance(Locale.CHINA)
                                //获得当前年周一日期
                                val cal = Calendar.getInstance()
                                cal.set(cal.get(Calendar.YEAR), 0, 1, 0, 0, 0)
                                cal.firstDayOfWeek = Calendar.MONDAY
                                cal.set(Calendar.DAY_OF_WEEK, Calendar.MONDAY)
                                //Week0's Week
                                val w0: Calendar = weeknowDate
                                w0.firstDayOfWeek = Calendar.MONDAY
                                w0.add(Calendar.DATE, -7 * weeknowWeek)
                                w0.set(Calendar.DAY_OF_WEEK, Calendar.MONDAY)
                                val w0w = SimpleDateFormat("yyyy-MM-dd", Locale.CHINA).format(w0.time)
                                val w0wFile = File(myContext.filesDir, "/weekNow")
                                w0wFile.writeText(w0w)
                                toast("设置成功 当前第 ${task.text} 周")
                                startActivity(intentFor<HomePage>().newTask().clearTask())
                            }
                        }
                        positiveButton("OK(负周数)") {
                            if (task.text.toString().isEmpty()) {
                                toast("没当前周你玩个鸡儿??及你太美")
                            } else {
                                val weeknowWeek = -task.text.toString().toInt()
                                val weeknowDate = Calendar.getInstance(Locale.CHINA)
                                //获得当前年周一日期
                                val cal = Calendar.getInstance()
                                cal.set(cal.get(Calendar.YEAR), 0, 1, 0, 0, 0)
                                cal.firstDayOfWeek = Calendar.MONDAY
                                cal.set(Calendar.DAY_OF_WEEK, Calendar.MONDAY)
                                //Week0's Week
                                val w0: Calendar = weeknowDate
                                w0.firstDayOfWeek = Calendar.MONDAY
                                w0.add(Calendar.DATE, -7 * weeknowWeek)
                                w0.set(Calendar.DAY_OF_WEEK, Calendar.MONDAY)
                                val w0w = SimpleDateFormat("yyyy-MM-dd", Locale.CHINA).format(w0.time)
                                val w0wFile = File(myContext.filesDir, "/weekNow")
                                w0wFile.writeText(w0w)
                                toast("设置成功 当前第 -${task.text} 周")
                                startActivity(intentFor<HomePage>().newTask().clearTask())
                            }
                        }
                    }
                }
            }.show()
        }
        //关于
        text_about.setOnClickListener {
            alert {
                customView {
                    verticalLayout {
                        //标题
                        toolbar {
                            lparams(width = matchParent, height = wrapContent)
                            title = "关于作者"
                        }
                        textView(R.string.about)
                        button("捐赠!! 打赏!! 点我!!!\n打开浏览器以后选择使用支付宝打开!!") {
                            onClick {
                                val alipay = "https://qr.alipay.com/fkx05866rmc3tvpisucbsef"
                                browse(alipay)
                            }
                        }
                        button("项目已在github开源 点我查看") {
                            onClick {
                                val github = "https://github.com/zsqw123/SUESjxxt"
                                browse(github)
                            }
                        }
                        button("加作者QQ...") {
                            onClick {
                                val qq = "https://qm.qq.com/cgi-bin/qm/qr?k=LBReU2xt52bv8E1mSr1BPBcoA61egKal"
                                browse(qq)
                            }
                        }
                        button("debug") {
                            onClick {
                            }
                        }
                    }
                }
            }.show()
        }
        //颜色选择
        text_theme.setOnClickListener {
            toast("长按颜色设置入口可自定义颜色哦")
            selector("请选择主题色", colorNameList) { _, i ->
                text_theme.text = " ${colorNameList[i]} "
                val primeColor: Int = Color.parseColor(colorList[i])
                fab_theme.background.setTint(primeColor)
                colorString.writeText(primeColor.toString())
                toast("建议在颜色设置更改之后重启APP")
                startActivity(intentFor<HomePage>().newTask().clearTask())
            }
        }
        text_theme.setOnLongClickListener {
            alert {
                customView {
                    verticalLayout {
                        //标题
                        toolbar {
                            lparams(width = matchParent, height = wrapContent)
                            title = "自定义颜色(HTML格式)"
                        }
                        //输入框
                        val task = editText {
                            hint = "#000000:黑色"
                            padding = dip(20)
                        }
                        button("在线取色器(颜色要用‘代码’框内的颜色代码)\n第三方接口 有可能失效 如失效则自行获取") {
                            onClick {
                                val pickColorPage = "http://xiaohudie.net/RGB.html"
                                browse(pickColorPage)
                            }
                        }
                        //button
                        negativeButton("OK") {
                            if (task.text.toString().isEmpty()) {
                                toast("宁未输入正确颜色值 检查#是否为英文#(半角)")
                            } else {
                                text_theme.text = task.text.toString()
                                val primeColor: Int = Color.parseColor(task.text.toString())
                                fab_theme.background.setTint(primeColor)
                                colorString = File(myContext.filesDir, "/color")
                                colorString.writeText(primeColor.toString())
                                toast("建议在颜色设置更改之后重启APP")
                                startActivity(intentFor<HomePage>().newTask().clearTask())
                            }
                        }
                    }
                }
            }.show()
            true
        }
        //导出ICS
        text_ex.setOnClickListener {
            selector("选择导出的方式", listOf("导出为ics文件", "导出到系统日历")) { _, i ->
                if (i == 0) startActivity<ToCalendar>()
                if (i == 1) {
                    val icsFile = File(myContext.filesDir, "/icsSelf")
                    val suesFile = File(myContext.filesDir, "/classJs")
                    if (!icsFile.exists()) {
                        if (suesFile.exists()) {
                            toast("正在转化课程表为ics格式...3s左右")
                            doAsync {
                                val text: String = suesFile.readText()
                                val listItem: List<Item> = Show().textShow(text)
                                val a = ExIcs()
                                a.ex(listItem)
                                val icsInput: String = a.expath.readText()
                                icsFile.writeText(icsInput)
                                uiThread {
                                    toast("正在导入...可能10s左右")
                                }
                                uiThread {
                                    icsToCalendarViewWithPermissionCheck()
                                }
                            }
                        } else {
                            toast("请先导入")
                        }
                    } else {
                        doAsync {
                            uiThread {
                                toast("正在导入...可能10s左右")
                            }
                            uiThread {
                                icsToCalendarViewWithPermissionCheck()
                            }
                        }
                    }
                }
            }
        }
        val lp = settings_layout.layoutParams as FrameLayout.LayoutParams
        lp.setMargins(0, getStatusBarHeight(context!!), 0, 0)
    }

    @NeedsPermission(Manifest.permission.READ_CALENDAR)
    fun icsToCalendarView() {
        val myEventList = IcsToDateMap().b()
        alert {
            customView {
                textView("确定导出到系统日历吗 有可能操作无法撤销(课程名一旦更改将无法撤销)")
                positiveButton("导出") {
                    toast("正在导出")
                    doAsync {
                        for ((count, event) in myEventList.withIndex()) {
                            if (count % 5 == 0) {
                                uiThread {
                                    toast("正在添加第${count}个事件")
                                }
                            }
                            if (CalendarProviderManager.isEventAlreadyExist(context!!, event.start, event.end, event.theme)) {
                                continue
                            }
                            CalendarProviderManager.addCalendarEvent(
                                context!!,
                                CalendarEvent(event.theme, event.discri, event.location, event.start, event.end, event.remindersMinutes, null)
                            )
                        }
                        uiThread {
                            toast("导出成功")
                        }
                    }
                }
                negativeButton("恢复(撤销)") {
                    if (CalendarProviderManager.checkCalendarAccount(context!!) == (-1).toLong()) {
                        toast("无本地账户或未导出过 无法继续操作")
                        return@negativeButton
                    }
                    alert {
                        customView {
                            textView("此操作会删除该设备上与课程名同名的日程\n云同步账户(Google、小米等云同步账户)中的日程不会被删除\n请谨慎操作 需要日历权限")
                        }
                        positiveButton("无所谓 我都是云同步账户") {
                            CalendarProviderManager.deleteCalendarAccountByName(context!!)
                            doAsync {
                                for ((count, event) in myEventList.withIndex()) {
                                    if (count % 5 == 0) {
                                        uiThread {
                                            toast("正在删除第${count}个事件")
                                        }
                                    }
                                    CalendarProviderManager.deleteCalendarEvent(context!!, event.theme)
                                }
                                uiThread {
                                    toast("已删除")
                                }
                            }
                        }
                    }.show()
                }
            }
        }.show()
    }
}
