package com.jxxt.sues

import android.graphics.Color
import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.core.graphics.ColorUtils
import androidx.viewpager.widget.ViewPager
import com.aurelhubert.ahbottomnavigation.AHBottomNavigation
import com.aurelhubert.ahbottomnavigation.AHBottomNavigationAdapter
import com.jxxt.sues.ui.ViewPagerAdapter
import com.tencent.bugly.crashreport.CrashReport
import kotlinx.android.synthetic.main.home_page.*
import me.everything.android.ui.overscroll.OverScrollDecoratorHelper
import org.jetbrains.anko.backgroundColor
import org.jetbrains.anko.doAsync
import org.jetbrains.anko.uiThread
import java.io.File


class HomePage : AppCompatActivity() {
    private lateinit var adapter: ViewPagerAdapter
//    private lateinit var viewPager: AHBottomNavigationViewPager

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.home_page)

        //bugly
        CrashReport.initCrashReport(applicationContext, "85638bad59", false)

        val bottomNavi: AHBottomNavigation = bottom_navigation
        val bottomNaviAdapter = AHBottomNavigationAdapter(this, R.menu.bottom_nav_menu)
        bottomNaviAdapter.setupWithBottomNavigation(bottomNavi, null)

        adapter = ViewPagerAdapter(supportFragmentManager)
        view_pager.adapter = adapter
        OverScrollDecoratorHelper.setUpOverScroll(view_pager)
        view_pager.addOnPageChangeListener(object : ViewPager.OnPageChangeListener {
            override fun onPageScrollStateChanged(state: Int) {}

            override fun onPageScrolled(position: Int, positionOffset: Float, positionOffsetPixels: Int) {}

            override fun onPageSelected(position: Int) {
                bottom_navigation.currentItem = position
            }

        })
        bottom_navigation.setOnTabSelectedListener { position, _ ->
            view_pager.currentItem = position
            return@setOnTabSelectedListener true
        }

        doAsync {
            //ColorSettings
            val colorString = File(filesDir, "/color")
            if (colorString.exists()) {
                val primeColor: Int = colorString.readText().toInt()
                //判断是否dark色系对任务栏图标显示颜色作出更改
                val dark = ColorUtils.calculateLuminance(primeColor) <= 0.2
                uiThread {
                    //状态栏沉浸
                    window.decorView.systemUiVisibility = if (dark) View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                    else View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR
                    bottomNavi.accentColor = primeColor
                    if (dark) {
                        bottomNavi.defaultBackgroundColor = Color.parseColor("#000000")
                        home_page.backgroundColor = Color.parseColor("#000000")
                    } else {
                        bottomNavi.defaultBackgroundColor = Color.parseColor("#FFFFFF")
                        home_page.backgroundColor = Color.parseColor("#FFFFFF")
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
