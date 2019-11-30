package com.jxxt.sues

import android.graphics.Color
import android.os.Bundle
import android.view.View
import android.view.WindowManager
import androidx.appcompat.app.AppCompatActivity
import androidx.core.graphics.ColorUtils
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
    private lateinit var content: List<Item>

    private var hadCycled = false
    private var a = 0

    private fun timeCycle() {
        if (!hadCycled) {
            doAsync {
                while (true) {
                    val nowClassDate = content[a].date.time
                    val nowClassDateEnd = content[a].date.time + 5400000
                    if (Date().time in nowClassDate until nowClassDateEnd) {
                        uiThread {
                            nowbar_time.text = SimpleDateFormat("HH:mm:ss", Locale.CHINA).format(Date())
                            nowbar_class.text = "当前正在上课:\n" + content[a - 1].name
                            val remain = (nowClassDateEnd - Date().time) / 1000
                            val remainH = remain / 3600
                            val remainM = (remain % 3600) / 60
                            val remainS = (remain % 3600) % 60
                            nowbar_remain.text = "离下课仅剩 ${remainH.toInt()}小时${remainM.toInt()}分${remainS.toInt()}秒"
                        }
                    } else {
                        uiThread {
                            nowbar_time.text = SimpleDateFormat("HH:mm:ss", Locale.CHINA).format(Date())
//                            val haveNext = content[a + 1].name.isEmpty()
                            val remain = (content[a + 1].date.time - Date().time) / 1000
                            if (remain < 0) {
                                nowbar_remain.text = "距离上课还剩我也不知道多长时间"
                                nowbar_class.text ="暂无更多课程\n请调整当前周或下学期见"
                            } else {
                                val remainH = remain / 3600
                                val remainM = (remain % 3600) / 60
                                val remainS = (remain % 3600) % 60
                                nowbar_class.text = "下一节课:\n" + content[a].name
                                nowbar_remain.text = "距离上课还剩 ${remainH.toInt()}小时${remainM.toInt()}分${remainS.toInt()}秒"
                            }
                        }
                    }
                    Thread.sleep(1000)
                }
            }
            !hadCycled
        }
    }

    private fun findToday() {
        //找到今日日程
        val now = Date()
        for (i in content.indices) {
            val nowClassDate = content[i].date
            //mainView移动到指定位置
            if (now <= nowClassDate) {
                mainView.scrollToPosition(i)
                a = i
                timeCycle()
                val lm = mainView.layoutManager as LinearLayoutManager
                lm.scrollToPositionWithOffset(i, nowbar.height + 20)
                return
            }
        }
        timeCycle()
    }

    private val colorList = listOf("#F4F4F4", "#FA7298", "#2D2D2D", "#F44236", "#FEC107", "#8BC24A", "#2196F3", "#9C28B1")
    private val stausColorList = listOf("#E6E6E6", "#FB628D", "#1D1D1D", "#F23022", "#EEB507", "#7FB83C", "#148EEE", "#9121A6")

    override fun onWindowFocusChanged(hasFocus: Boolean) {
        super.onWindowFocusChanged(hasFocus)
        doAsync {
            //ColorSettings
            if (colorString.exists()) {
                val primeColor: Int = colorString.readText().toInt()
                for (i in colorList.indices) {
                    if (Color.parseColor(colorList[i]) == primeColor) {
                        //判断是否dark色系对任务栏图标显示颜色作出更改
                        val dark = ColorUtils.calculateLuminance(Color.parseColor(stausColorList[i])) <= 0.3
                        uiThread {
                            window.statusBarColor = Color.parseColor(stausColorList[i])
                            window.decorView.systemUiVisibility = if (dark) View.SYSTEM_UI_FLAG_VISIBLE
                            else View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR
                        }
                    }
                }
                uiThread {
                    val dark = ColorUtils.calculateLuminance(primeColor) <= 0.3
                    val ultraDark = ColorUtils.calculateLuminance(primeColor) <= 0.1
                    nowbar.backgroundColor = Color.parseColor("#EDE1E1")
                    mainView.backgroundColor = if (dark) Color.parseColor("#000000") else Color.parseColor("#FFFFFF")
                    nowbar_class.setTextColor(Color.parseColor("#000000"))
                    nowbar_time.setTextColor(Color.parseColor("#000000"))
                    nowbar_remain.setTextColor(Color.parseColor("#000000"))
                    if (ultraDark) {//很黑的情况下...
                        nowbar.backgroundColor = Color.parseColor("#4D4D4D")
                        nowbar_class.setTextColor(Color.parseColor("#FFFFFF"))
                        nowbar_time.setTextColor(Color.parseColor("#FFFFFF"))
                        nowbar_remain.setTextColor(Color.parseColor("#FFFFFF"))
                    }
                }
            }
            uiThread {
                //找到今日日程
                findToday()
                //loaded
                progressBar.visibility = View.INVISIBLE
                window.clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE)
                fab0.setOnClickListener {
                    startActivity<Settings>()
                }
                fab0.setOnLongClickListener {
                    findToday()
                    toast("已回到今日日程")
                    true
                }
            }
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        //定义Flies目录
        file = File(filesDir, "/a")
        colorString = File(filesDir, "/color")
        weekNow = File(filesDir, "weekNow")
        //loading...
        progressBar.visibility = View.VISIBLE
        window.setFlags(
            WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE,
            WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE
        )
        if (!file.exists()) startActivity<NewAct>() else {
            val text = file.readText()
            val weeknow = if (weekNow.exists()) weekNow.readText() else {
                val cal = Calendar.getInstance()
                cal.firstDayOfWeek = Calendar.MONDAY
                cal.set(Calendar.DAY_OF_WEEK, Calendar.MONDAY)
                SimpleDateFormat("yyyy-MM-dd", Locale.CHINA).format(cal.time)
            }
            //主列表视图显示
            content = Show().textShow(text, weeknow)
            mainView.apply {
                setHasFixedSize(true)
                layoutManager = LinearLayoutManager(context)
                adapter = MainAdapter(context, content)
                addOnScrollListener(RecListener(fab0))
            }
        }
    }
}
