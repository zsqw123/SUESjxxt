package com.jxxt.sues

import android.graphics.Color
import android.os.Bundle
import android.view.View
import android.view.WindowManager
import androidx.appcompat.app.AppCompatActivity
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

    private fun findToday() {
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
                }else{
                    val cal=Calendar.getInstance()
                    cal.firstDayOfWeek=Calendar.MONDAY
                    cal.set(Calendar.DAY_OF_WEEK,Calendar.MONDAY)
                    weeknow=SimpleDateFormat("yyyy-MM-dd", Locale.CHINA).format(cal.time)
                }
                content = Show().textShow(text, weeknow)
                uiThread {
                    mainView.apply {
                        setHasFixedSize(true)
                        layoutManager = LinearLayoutManager(context)
                        adapter = MainAdapter(context, content)
                        addOnScrollListener(RecListener(fab0))
                    }
                    //找到今日日程
                    findToday()
                    //loaded
                    progressBar.visibility = View.INVISIBLE
                    window.clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE)
                }
            }
            //ColorSettings
            if (colorString.exists()) {
                val primeColor: Int = colorString.readText().toInt()
                for (i in colorList.indices) {
                    if (Color.parseColor(colorList[i]) == primeColor) {
                        window.statusBarColor = Color.parseColor(stausColorList[i])
                    }
                }
                when (primeColor) {
                    -13816531 -> mainView.backgroundColor = Color.parseColor("#FFFFFF")
                }
            }
        }
        fab0.setOnClickListener{
            startActivity<Settings>()
        }
        fab0.setOnLongClickListener {
            findToday()
            toast("已回到今日日程")
            return@setOnLongClickListener true
        }
    }
}
