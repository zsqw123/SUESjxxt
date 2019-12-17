package com.jxxt.sues

import android.content.res.ColorStateList
import android.graphics.Color
import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.core.graphics.ColorUtils
import androidx.navigation.findNavController
import androidx.navigation.ui.setupWithNavController
import com.tencent.bugly.crashreport.CrashReport
import kotlinx.android.synthetic.main.activity_home_page.*
import org.jetbrains.anko.backgroundColor
import org.jetbrains.anko.doAsync
import org.jetbrains.anko.uiThread
import java.io.File
import java.util.*

class HomePage : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_home_page)

        //bugly
        CrashReport.initCrashReport(applicationContext, "85638bad59", false)

        val navController = findNavController(R.id.nav_host_fragment)
        nav_view.setupWithNavController(navController)

        val colorList = listOf("#F4F4F4", "#FA7298", "#2D2D2D", "#F44236", "#FEC107", "#8BC24A", "#2196F3", "#9C28B1")
        val stausColorList = listOf("#E6E6E6", "#FB628D", "#1D1D1D", "#F23022", "#EEB507", "#7FB83C", "#148EEE", "#9121A6")

        doAsync {
            //ColorSettings
            val colorString = File(filesDir, "/color")
            if (colorString.exists()) {
                val primeColor: Int = colorString.readText().toInt()
                for (i in colorList.indices) {
                    if (Color.parseColor(colorList[i]) == primeColor) {
                        //判断是否dark色系对任务栏图标显示颜色作出更改
                        val dark = ColorUtils.calculateLuminance(Color.parseColor(stausColorList[i])) <= 0.4
                        uiThread {
                            window.statusBarColor = Color.parseColor(stausColorList[i])
                            window.decorView.systemUiVisibility = if (dark) View.SYSTEM_UI_FLAG_VISIBLE
                            else View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR
                            val states = arrayOf(intArrayOf(-android.R.attr.state_checked), intArrayOf(android.R.attr.state_checked))
                            val colorsWhite = intArrayOf(Color.parseColor(colorList[i]), Color.parseColor(reverse(colorList[i])))
                            val csl = ColorStateList(states, colorsWhite)
                            nav_view.itemIconTintList = csl
                            nav_view.itemTextColor = csl
                            if (dark) {
                                nav_view.backgroundColor = Color.parseColor("#000000")
                            } else {
                                nav_view.backgroundColor = Color.parseColor("#FFFFFF")
                            }
                        }
                    }
                }
            }
        }
    }

    //取反色
    private fun reverse(str: String): String {
        val sb: StringBuilder = StringBuilder().append("#")
        val input = str.replace("#", "")
        for (element in input) {
            val st: String = element.toString()
            val temp = Integer.parseInt(st, 16)
            sb.append(Integer.toHexString(15 - temp).toUpperCase(Locale.ROOT))
        }
        return sb.toString()
    }
}
