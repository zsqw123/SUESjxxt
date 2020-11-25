package com.jxxt.sues

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.FileProvider
import com.jxxt.sues.getpage.GetPage
import com.jxxt.sues.ical.ExIcs
import kotlinx.android.synthetic.main.to_calendar.*
import java.io.File

class ToCalendar : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.to_calendar)
        //定义Flies目录
        val file = File(filesDir, "/classJs")
        if (!file.exists()) {
            toast("文件不存在！无法导出")
            finish()
        }
        val text = file.readText()
        val content = Show().textShow(text)

        toics_share.setOnClickListener {
            val extraTimeText = extra_time.text.toString()
            var extraTime = 0
            try {
                extraTime = extraTimeText.toInt() * 60000
            } catch (e: Exception) {
                toast("未输入正确数字格式 不进行提前提醒")
            }
            if (!file.exists()) startActivity(GetPage::class.java) else {
                doAsync {
                    val a = ExIcs()
                    a.ex(content, extraTime)
                    uiThread {
                        toast("导出成功 请选择要分享的应用")
                    }
                    val share = Intent(Intent.ACTION_SEND)
                    share.type = "text/calendar"
                    val uri = FileProvider.getUriForFile(suesApp, "com.jxxt.sues.provider", a.expath)
                    share.putExtra(Intent.EXTRA_STREAM, uri)
                    share.flags = Intent.FLAG_ACTIVITY_NEW_TASK

                    startActivity(Intent.createChooser(share, "分享到其他设备/APP"))
                }
            }
        }

        toics.setOnClickListener {
            val extraTimeText = extra_time.text.toString()
            var extraTime = 0
            try {
                extraTime = extraTimeText.toInt() * 60000
            } catch (e: Exception) {
                toast("未输入正确数字格式 不进行提前提醒")
            }
            if (!file.exists()) startActivity(GetPage::class.java) else {
                doAsync {
                    val a = ExIcs()
                    a.ex(content, extraTime)
                    uiThread {
                        toast("导出成功 请返回上一级")
                    }
                }
            }
        }
    }
}