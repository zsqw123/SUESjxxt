package com.jxxt.sues

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.jxxt.sues.ical.ExIcs
import kotlinx.android.synthetic.main.to_calendar.*
import org.jetbrains.anko.doAsync
import org.jetbrains.anko.longToast
import org.jetbrains.anko.startActivity
import org.jetbrains.anko.uiThread
import java.io.File
import java.text.SimpleDateFormat
import java.util.*

class ToCalendar : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.to_calendar)
        //定义Flies目录
        val file = File(filesDir, "/a")
        val weekNow = File(filesDir, "weekNow")
        toics.setOnClickListener {
            if (!file.exists()) startActivity<NewAct>() else {
                //loading...
                doAsync {
                    val text = file.readText()
                    val cal = Calendar.getInstance()
                    cal.firstDayOfWeek = Calendar.MONDAY
                    cal.set(Calendar.DAY_OF_WEEK, Calendar.MONDAY)
                    val weeknow = if (weekNow.exists()) weekNow.readText() else SimpleDateFormat("yyyy-MM-dd", Locale.CHINA).format(cal.time)
                    val content = Show().textShow(text, weeknow)
                    ExIcs().ex(content)
                    uiThread {
                        longToast("导出成功 请返回上一级")
                    }
                }
            }
        }
    }
}