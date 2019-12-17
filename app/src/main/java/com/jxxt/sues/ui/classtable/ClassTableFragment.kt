package com.jxxt.sues.ui.classtable

import android.graphics.Color
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.graphics.ColorUtils
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.jxxt.sues.*
import com.jxxt.sues.getpage.GetPage
import com.jxxt.sues.widget.Utils
import kotlinx.android.synthetic.main.activity_main.*
import org.jetbrains.anko.backgroundColor
import org.jetbrains.anko.doAsync
import org.jetbrains.anko.support.v4.startActivity
import org.jetbrains.anko.support.v4.toast
import org.jetbrains.anko.uiThread
import java.io.File
import java.lang.Exception
import java.text.SimpleDateFormat
import java.util.*

class ClassTableFragment : Fragment() {
    private lateinit var content: List<Item>
    private var hadCycled = false
    private var a = 0

    private fun timeCycle() {
        if (!hadCycled) {
            doAsync {
                while (nowbar_time != null) {
                    val nowClassDate = content[a].date.time
                    val nowClassDateEnd = content[a].date.time + 5400000
                    if (Date().time in nowClassDate until nowClassDateEnd) {
                        uiThread {
                            try {
                                nowbar_time.text = SimpleDateFormat("HH:mm:ss", Locale.CHINA).format(Date())
                                nowbar_class.text = "当前正在上课:\n" + content[a - 1].name
                                val remain = (nowClassDateEnd - Date().time) / 1000
                                val remainH = remain / 3600
                                val remainM = (remain % 3600) / 60
                                val remainS = (remain % 3600) % 60
                                nowbar_remain.text = "离下课仅剩 ${remainH.toInt()}小时${remainM.toInt()}分${remainS.toInt()}秒"
                            } catch (e: Exception) {
                            }
                        }
                    } else {
                        uiThread {
                            try {
                                nowbar_time.text = SimpleDateFormat("HH:mm:ss", Locale.CHINA).format(Date())
                                val remain = (content[a].date.time - Date().time) / 1000
                                if (remain < 0) {
                                    nowbar_remain.text = "距离上课还剩我也不知道多长时间"
                                    nowbar_class.text = "暂无更多课程\n请调整当前周或下学期见"
                                } else {
                                    val remainH = remain / 3600
                                    val remainM = (remain % 3600) / 60
                                    val remainS = (remain % 3600) % 60
                                    nowbar_class.text = "下一节课:\n" + content[a].name
                                    nowbar_remain.text = "距离上课还剩 ${remainH.toInt()}小时${remainM.toInt()}分${remainS.toInt()}秒"
                                }
                            } catch (e: Exception) {
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


    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? = inflater.inflate(R.layout.activity_main, container, false)


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val myContext = Utils.getContext()
        val file = File(myContext.filesDir, "/classJs")
        val colorString = File(myContext.filesDir, "/color")

        if (!file.exists()) {
            //loaded
            progressBar.visibility = View.INVISIBLE
            startActivity<GetPage>()
        } else {
            val text = file.readText()
            //主列表视图显示
            content = Show().textShow(text)
            mainView.apply {
                setHasFixedSize(true)
                layoutManager = LinearLayoutManager(context)
                adapter = MainAdapter(context, content, FindContext().getToyear(text))
            }
            doAsync {
                //ColorSettings
                if (colorString.exists()) {
                    val primeColor: Int = colorString.readText().toInt()
                    for (i in colorList.indices) {
                        if (Color.parseColor(colorList[i]) == primeColor) {
                            //判断是否dark色系对任务栏图标显示颜色作出更改
                            val dark = ColorUtils.calculateLuminance(Color.parseColor(stausColorList[i])) <= 0.3
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
                }
            }
            //loaded
            progressBar.visibility = View.INVISIBLE
        }
        nowbar_class.setOnClickListener {
            findToday()
            toast("已回到今日日程")
        }
    }
}
