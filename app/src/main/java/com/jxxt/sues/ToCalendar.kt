package com.jxxt.sues

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.jxxt.sues.ical.ExIcs
import com.jxxt.sues.widget.Utils
import kotlinx.android.synthetic.main.to_calendar.*
import org.jetbrains.anko.doAsync
import org.jetbrains.anko.longToast
import org.jetbrains.anko.startActivity
import org.jetbrains.anko.uiThread
import org.slf4j.helpers.Util
import java.io.File
import java.text.SimpleDateFormat
import java.util.*
import androidx.core.content.FileProvider



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
                    val a = ExIcs()
                    a.ex(content)
                    uiThread {
                        longToast("导出成功 请返回上一级")
                    }
                    val share = Intent(Intent.ACTION_SEND)
                    share.type = "text/calendar"
                    val uri = FileProvider.getUriForFile(Utils.getContext(), "com.jxxt.sues.provider", a.expath)
                    share.putExtra(Intent.EXTRA_STREAM,uri)
                    share.flags=Intent.FLAG_ACTIVITY_NEW_TASK

                    startActivity(Intent.createChooser(share, "分享到其他设备/APP"))
                }
            }
        }
    }
}