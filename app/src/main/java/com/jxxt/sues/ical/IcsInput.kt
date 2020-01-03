package com.jxxt.sues.ical

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import com.jxxt.sues.widget.Utils
import org.jetbrains.anko.button
import org.jetbrains.anko.doAsync
import org.jetbrains.anko.linearLayout
import org.jetbrains.anko.sdk27.coroutines.onClick
import org.jetbrains.anko.textView
import java.io.ByteArrayOutputStream
import java.io.File
import java.io.InputStream


class IcsInput : Activity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        linearLayout {
            val text = textView("点击按钮导入ics文件")
            val bt = button("导入") {
                onClick {
                    doAsync {
                        val intent = Intent(Intent.ACTION_GET_CONTENT)
                        //任意类型文件
                        intent.type = "text/calendar"
                        intent.addCategory(Intent.CATEGORY_OPENABLE)
                        startActivityForResult(intent, 1)
                        // 获取用户选择文件的路径

                    }
                }
            }
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        val mContext = Utils.getContext()
        if (data == null) {
            return
        }
        val dataUri = data.data
        val path = dataUri?.path
        println("wocccccccccccccccccccccccccccssacas$path")
        val icsInputPath = File(path!!)
        val icsStorePath = File(mContext.filesDir, "/icsSelf")
        doAsync {
            val inputStream = mContext.contentResolver.openInputStream(dataUri)
            val icsFileText= inputStream?.let { inputParseString(it) }
            if (icsFileText != null) {
                icsStorePath.writeText(icsFileText)
                println(icsStorePath.readText())
            }

        }
    }

    private fun inputParseString(inputStream: InputStream): String {
        val swapStream = ByteArrayOutputStream()
        var ch: Int
        while (inputStream.read().also { ch = it } != -1) {
            swapStream.write(ch)
        }
        return swapStream.toString()
    }
}