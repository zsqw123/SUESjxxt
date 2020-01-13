package com.jxxt.sues.ui.settings

import android.Manifest
import android.content.pm.PackageManager
import android.graphics.Color
import android.os.Bundle
import android.text.InputType
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import com.jxxt.sues.HomePage
import com.jxxt.sues.R
import com.jxxt.sues.ToCalendar
import com.jxxt.sues.getpage.GetPage
import com.jxxt.sues.ical.CalendarEvent
import com.jxxt.sues.ical.CalendarProviderManager
import com.jxxt.sues.ical.IcsInput
import com.jxxt.sues.ical.IcsToDateMap
import com.jxxt.sues.widget.Utils
import kotlinx.android.synthetic.main.settings.*
import org.jetbrains.anko.*
import org.jetbrains.anko.sdk27.coroutines.onClick
import org.jetbrains.anko.support.v4.*
import java.io.File
import java.text.SimpleDateFormat
import java.util.*

class SettingsFragment : Fragment() {

    //read and judge
    private lateinit var file: File
    private lateinit var colorString: File

    private val colorNameList = listOf("简洁白", "少女粉", "夜间模式", "姨妈红", "咸蛋黄", "早苗绿", "胖次蓝", "基佬紫")
    private val colorList = listOf("#F4F4F4", "#FA7298", "#2D2D2D", "#F44236", "#FEC107", "#8BC24A", "#2196F3", "#9C28B1")

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.settings, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val myContext = Utils.getContext()
        //定义Flies目录
        file = File(myContext.filesDir, "/a")

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
                                startActivity<GetPage>()
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
                colorString = File(myContext.filesDir, "/color")
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
                    doAsync {
                        val icsFile = File(myContext.filesDir, "/icsSelf")
                        if (icsFile.exists()) {
                            val myEventList = IcsToDateMap().b()
                            uiThread {
                                alert {
                                    customView {
                                        textView("确定导出到系统日历吗 有可能操作无法撤销")
                                        positiveButton("导出") {
                                            toast("正在导出")
                                            doAsync {
                                                val checkSelfPermission = ContextCompat.checkSelfPermission(
                                                    context!!,
                                                    Manifest.permission.WRITE_CALENDAR
                                                )
                                                if (checkSelfPermission != PackageManager.PERMISSION_GRANTED) {
                                                    toast("请授予访问日历权限!")
                                                } else {
                                                    for (event in myEventList) {
                                                        if (CalendarProviderManager.isEventAlreadyExist(context!!, event.start, event.end, event.theme)) {
                                                            continue
                                                        }
                                                        CalendarProviderManager.addCalendarEvent(
                                                            context!!,
                                                            CalendarEvent(event.theme, event.discri, event.location, event.start, event.end, event.remindersMinutes, null)
                                                        )
                                                    }
                                                }
                                                uiThread {
                                                    toast("导出成功")
                                                }
                                            }
                                        }
                                    }
                                }.show()
                            }
                        } else {
                            toast("您还未导入课程表!")
                        }
                    }
                }
            }
        }
    }
}
