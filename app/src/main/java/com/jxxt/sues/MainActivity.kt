package com.jxxt.sues

import android.graphics.Color
import android.graphics.drawable.GradientDrawable
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.InputType
import android.view.View
import android.view.WindowManager
import android.widget.TextView
import androidx.core.view.isVisible
import androidx.recyclerview.widget.LinearLayoutManager
import kotlinx.android.synthetic.main.activity_main.*
import org.jetbrains.anko.*
import java.io.File
import java.text.SimpleDateFormat
import java.util.*

class MainActivity : AppCompatActivity() {
    //read and judge
    private lateinit var file: File
    private lateinit var colorString: File
    private lateinit var weekNow: File

    private val colorNameList = listOf("简洁白", "少女粉", "夜间模式", "姨妈红", "咸蛋黄", "早苗绿", "胖次蓝", "基佬紫")
    private val colorList = listOf("#F4F4F4", "#FA7298", "#2D2D2D", "#F44236", "#FEC107", "#8BC24A", "#2196F3", "#9C28B1")
    private val stausColorList = listOf("#E6E6E6", "#FB628D", "#1D1D1D", "#F23022", "#EEB507", "#7FB83C", "#148EEE", "#9121A6")

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        //定义Flies目录
        file = File(filesDir, "/a")
        colorString = File(filesDir, "/color")
        weekNow = File(filesDir, "weekNow")

        if (!file.exists()) startActivity<NewAct>() else {
            //loading...
            progressBar.visibility = View.VISIBLE
            window.setFlags(
                WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE,
                WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE
            )
            doAsync {
                val text = file.readText()
                var weeknow = ""
                if (weekNow.exists()) {
                    weeknow = weekNow.readText()
                }
                val content = Show().textShow(text, weeknow)
                uiThread {
                    mainView.apply {
                        setHasFixedSize(true)
                        layoutManager = LinearLayoutManager(context)
                        adapter = MainAdapter(context, content)
                    }
                    //找到今日日程
                    val now = Date()
                    for (i in content.indices) {
                        val date = content[i].date
                        if (now <= date) {
                            mainView.scrollToPosition(i)
                            break
                        }
                    }
                }
            }
            //ColorSettings
            if (colorString.exists()) {
                val primeColor: Int = colorString.readText().toInt()
                bar.backgroundColor = primeColor
                for (i in colorList.indices) {
                    if (Color.parseColor(colorList[i]) == primeColor) {
                        window.statusBarColor = Color.parseColor(stausColorList[i])
                    }
                }
                when (primeColor) {
                    -13816531 -> mainView.backgroundColor = Color.parseColor("#FFFFFF")
                }
            }
            //loaded
            progressBar.visibility = View.INVISIBLE
            window.clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE)
        }
        button.setOnClickListener {
            startActivity<NewAct>()
        }
        calendarView.isVisible = false
        calendarView.setOnClickListener {
            startActivity<ToCalendar>()
        }
        thisWeek.setOnClickListener {
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
                            } else
                            {
                                val weeknow=task.text.toString().toInt()
                                val week0 = Calendar.getInstance(Locale.CHINA)
                                week0.set(Calendar.DAY_OF_WEEK, Calendar.MONDAY)
                                week0.add(Calendar.DATE, -7 * weeknow)
                                weekNow.writeText(SimpleDateFormat("yyyy-MM-dd", Locale.CHINA).format(week0.time))
                                toast("请结束软件进程并重启即可看到效果")
                            }
                        }
                        positiveButton("OK(负周数)") {
                            if (task.text.toString().isEmpty()) {
                                toast("没当前周你玩个鸡儿??及你太美")
                            } else
                            {
                                val a="-" + task.text.toString()
                                val weeknow=a.toInt()
                                val week0 = Calendar.getInstance(Locale.CHINA)
                                week0.set(Calendar.DAY_OF_WEEK, Calendar.MONDAY)
                                week0.add(Calendar.DATE, -7 * weeknow)
                                weekNow.writeText(SimpleDateFormat("yyyy-MM-dd", Locale.CHINA).format(week0.time))
                                toast("请结束软件进程并重启即可看到效果")
                            }
                        }
                    }
                }
            }.show()
        }
        about.setOnClickListener {
            alert {
                customView {
                    verticalLayout {
                        //标题
                        toolbar {
                            lparams(width = matchParent, height = wrapContent)
                            title = "关于作者"
                        }
                        val text = textView(R.string.about)
                    }
                }
            }.show()
        }
        textView.setOnClickListener {
            selector("请选择主题色", colorNameList) { _, i ->
                textView.text = colorNameList[i]
                val primeColor: Int = Color.parseColor(colorList[i])
                progressBar.visibility = View.VISIBLE

                window.statusBarColor = Color.parseColor(stausColorList[i])
                bar.backgroundColor = primeColor

                window.setFlags(
                    WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE,
                    WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE
                )
                val tView = findViewById<TextView>(R.id.date).background as GradientDrawable
                tView.setColor(primeColor)
                doAsync {
                    val text = file.readText()
                    colorString = File(filesDir, "/color")
                    colorString.writeText(primeColor.toString())

                    var weeknow = ""
                    if (weekNow.exists()) {
                        weeknow = weekNow.readText()
                    }
                    val content = Show().textShow(text,weeknow)
                    uiThread {
                        mainView.apply {
                            setHasFixedSize(true)
                            layoutManager = LinearLayoutManager(context)
                            adapter = MainAdapter(context, content)
                        }
                        window.clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE)
                        progressBar.visibility = View.INVISIBLE
                    }
                }
            }
        }
    }
}

