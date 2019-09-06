package com.jxxt.sues

import android.app.Activity
import android.content.Intent
import android.graphics.Color
import android.net.Uri
import android.os.Bundle
import android.text.InputType
import android.view.View
import kotlinx.android.synthetic.main.settings.*
import org.jetbrains.anko.*
import org.jetbrains.anko.sdk27.coroutines.onClick
import java.io.File
import java.text.SimpleDateFormat
import java.util.*

class Settings : Activity() {
    //read and judge
    private lateinit var file: File
    private lateinit var colorString: File
    private lateinit var weekNow: File


    private val colorNameList = listOf("简洁白", "少女粉", "夜间模式", "姨妈红", "咸蛋黄", "早苗绿", "胖次蓝", "基佬紫")
    private val colorList = listOf("#F4F4F4", "#FA7298", "#2D2D2D", "#F44236", "#FEC107", "#8BC24A", "#2196F3", "#9C28B1")

    override fun onWindowFocusChanged(hasFocus: Boolean) {
        super.onWindowFocusChanged(hasFocus)
        colorString = File(filesDir, "/color")
        //ColorSettings
        if (colorString.exists()) {
            val primeColor: Int = colorString.readText().toInt()
            fab_theme.background.setTint(primeColor)
            window.statusBarColor = Color.TRANSPARENT
            for (i in colorList.indices) {
                if (Color.parseColor(colorList[i]) == primeColor) {
                    text_theme.text = colorNameList[i]
                    window.decorView.systemUiVisibility = View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR
                }
            }
        }
    }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.settings)
        //定义Flies目录
        file = File(filesDir, "/a")
        weekNow = File(filesDir, "weekNow")
        doAsync {
            if (weekNow.exists()) {
                val weeknow = weekNow.readText()
                val week0 = Calendar.getInstance()
                val date = SimpleDateFormat("yyyy-MM-dd", Locale.CHINA).parse(weeknow)
                week0.time = date!!
                week0.set(Calendar.HOUR_OF_DAY, 1)
                val now = Calendar.getInstance()
                val a = now.timeInMillis - week0.timeInMillis
                val week = if (a > 0) (a / (24 * 3600000)).toInt() / 7 else (a / (24 * 3600000)).toInt() / 7 - 1
                text_week.text = "当前第${week}周"
            }
        }

        //导入课程
        text_import.setOnClickListener {
            startActivity<NewAct>()
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
                                val weeknow = task.text.toString().toInt()
                                val week0 = Calendar.getInstance(Locale.CHINA)
                                week0.firstDayOfWeek = Calendar.MONDAY
                                week0.set(Calendar.DAY_OF_WEEK, Calendar.MONDAY)
                                week0.add(Calendar.DATE, -7 * weeknow)
                                weekNow.writeText(SimpleDateFormat("yyyy-MM-dd", Locale.CHINA).format(week0.time))
                                toast("设置成功 当前第 ${task.text} 周")
                                startActivity(intentFor<MainActivity>().newTask().clearTask())
                            }
                        }
                        positiveButton("OK(负周数)") {
                            if (task.text.toString().isEmpty()) {
                                toast("没当前周你玩个鸡儿??及你太美")
                            } else {
                                val a = "-" + task.text.toString()
                                val weeknow = a.toInt()
                                val week0 = Calendar.getInstance(Locale.CHINA)
                                week0.firstDayOfWeek = Calendar.MONDAY
                                week0.set(Calendar.DAY_OF_WEEK, Calendar.MONDAY)
                                week0.add(Calendar.DATE, -7 * weeknow)
                                weekNow.writeText(SimpleDateFormat("yyyy-MM-dd", Locale.CHINA).format(week0.time))
                                toast("设置成功 当前第 -${task.text} 周")
                                startActivity(intentFor<MainActivity>().newTask().clearTask())
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
                                doAsync {
                                    val uri = Uri.parse("https://qr.alipay.com/fkx05866rmc3tvpisucbsef")
                                    val intent = Intent(Intent.ACTION_VIEW, uri)
                                    startActivity(intent)
                                }
                            }
                        }
                        button("项目已在github开源 点我查看") {
                            onClick {
                                doAsync {
                                    val uri = Uri.parse("https://github.com/zsqw123/SUESjxxt")
                                    val intent = Intent(Intent.ACTION_VIEW, uri)
                                    startActivity(intent)
                                }
                            }
                        }
                    }
                }
            }.show()
        }
        //颜色选择
        text_theme.setOnClickListener {
            selector("请选择主题色", colorNameList) { _, i ->
                text_theme.text = " ${colorNameList[i]} "
                val primeColor: Int = Color.parseColor(colorList[i])
                now_week.background.setTint(primeColor)
                window.statusBarColor = Color.TRANSPARENT

                doAsync {
                    colorString = File(filesDir, "/color")
                    colorString.writeText(primeColor.toString())
                    startActivity(intentFor<MainActivity>().newTask().clearTask())
                }
            }
        }
        text_ex.setOnClickListener {
            startActivity<ToCalendar>()
        }
    }
}