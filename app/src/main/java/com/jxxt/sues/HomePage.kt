package com.jxxt.sues

import android.Manifest
import android.content.pm.PackageManager
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
import org.jetbrains.anko.toast
import org.jetbrains.anko.uiThread
import java.io.File

class HomePage : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        setTheme(R.style.AppTheme)
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_home_page)

        //bugly
        CrashReport.initCrashReport(applicationContext, "85638bad59", false)

        val navController = findNavController(R.id.nav_host_fragment)
        nav_view.setupWithNavController(navController)

        doAsync {
            //ColorSettings
            val colorString = File(filesDir, "/color")
            if (colorString.exists()) {
                val primeColor: Int = colorString.readText().toInt()
                //判断是否dark色系对任务栏图标显示颜色作出更改
                val dark = ColorUtils.calculateLuminance(primeColor) <= 0.17
                uiThread {
                    //状态栏沉浸
                    window.decorView.systemUiVisibility = if (dark) View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                    else View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR
                    val states = arrayOf(intArrayOf(-android.R.attr.state_checked), intArrayOf(android.R.attr.state_checked))
                    val colorsNav = intArrayOf(primeColor, reverse(primeColor))
                    val csl = ColorStateList(states, colorsNav)
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

    //取反色
    private fun reverse(color: Int): Int {
        val red = color and 0xff0000 shr 16
        val green = color and 0x00ff00 shr 8
        val blue = color and 0x0000ff
        val rR = 255 - red
        val rG = 255 - green
        val rB = 255 - blue
        return Color.rgb(rR, rG, rB)
    }
}
