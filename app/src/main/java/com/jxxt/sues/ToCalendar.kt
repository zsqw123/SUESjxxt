package com.jxxt.sues

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.FileProvider
import com.jxxt.sues.ical.ExIcs
import com.jxxt.sues.widget.Utils
import kotlinx.android.synthetic.main.to_calendar.*
import org.jetbrains.anko.*
import java.io.File
import java.lang.Exception

class ToCalendar : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.to_calendar)
        //定义Flies目录
        val file = File(filesDir, "/classJs")
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
            if (!file.exists()) startActivity<NewAct>() else {
                doAsync {
                    val a = ExIcs()
                    a.ex(content, extraTime)
                    uiThread {
                        longToast("导出成功 请选择要分享的应用")
                    }
                    val share = Intent(Intent.ACTION_SEND)
                    share.type = "text/calendar"
                    val uri = FileProvider.getUriForFile(Utils.getContext(), "com.jxxt.sues.provider", a.expath)
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
            if (!file.exists()) startActivity<NewAct>() else {
                doAsync {
                    val a = ExIcs()
                    a.ex(content, extraTime)
                    uiThread {
                        longToast("导出成功 请返回上一级")
                    }
                }
            }
        }
    }
}