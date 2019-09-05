package com.jxxt.sues

import android.graphics.Color
import android.os.Bundle
import android.view.View
import android.view.WindowManager
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import kotlinx.android.synthetic.main.activity_main.*
import org.jetbrains.anko.backgroundColor
import org.jetbrains.anko.doAsync
import org.jetbrains.anko.startActivity
import org.jetbrains.anko.uiThread
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
    /*
      不舍得删除 留着吧
     private var isOnce = true
    private var fab1Dy = 0f
    private var fabColorDy = 0f
    private var fabAboutDy = 0f
    private var fabNowDy = 0f
    private var fabToicsDy = 0f

    private var text1Dx = 0f
    private var textColorDx = 0f
    private var textNowDx = 0f
    private var textAboutDx = 0f
    private var textToicsDx = 0f
        override fun onWindowFocusChanged(hasFocus: Boolean) {
        super.onWindowFocusChanged(hasFocus)
        if (isOnce) {
            //记录坐标
            fab1Dy = fab0.y - fab1.y
            fabColorDy = fab0.y - fab_color.y
            fabAboutDy = fab0.y - fab_about.y
            fabNowDy = fab0.y - fab_now_week.y
            fabToicsDy = fab0.y - fab_toics.y

            text1Dx = fab1.x - fab1_text.x
            textAboutDx = fab1.x - text_about.x
            textNowDx = fab1.x - text_now.x
            textColorDx = fab1.x - text_color.x
            textToicsDx = fab1.x - text_toics.x
            //赋初值
            fab1.y = fab0.y
            fab_about.y = fab0.y
            fab_now_week.y = fab0.y
            fab_color.y = fab0.y
            fab_toics.y = fab0.y

            fab1_text.x = fab1.x
            text_color.x = fab_color.x
            text_about.x = fab_about.x
            text_now.x = fab_now_week.x
            text_toics.x = fab_toics.x

            fab1_text.alpha = 0f
            text_color.alpha = 0f
            text_about.alpha = 0f
            text_now.alpha = 0f
            text_toics.alpha = 0f

            fab_color.alpha = 0f
            fab_now_week.alpha = 0f
            fab_about.alpha = 0f
            fab1.alpha = 0f
            fab_toics.alpha = 0f


            isOnce = !isOnce
        }

    }
    */


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
            //loaded
            progressBar.visibility = View.INVISIBLE
            window.clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE)
        }
        fab0.setOnClickListener{
            startActivity<Settings>()
        }
        fab0.setOnLongClickListener {
            findToday()
            return@setOnLongClickListener true
        }
        /*
        不舍得删除 留着吧

        /*
        FloatActionButton Anim
        */
        //收回
        fun viewIn(view: FloatingActionButton, textView: TextView, dy: Float, dx: Float) {
            val transIn = ObjectAnimator.ofFloat(view, "translationY", dy)
            val textMoveIn = ObjectAnimator.ofFloat(textView, "translationX", dx)
            val alphaIn = ObjectAnimator.ofFloat(textView, "alpha", 1f, 0f)
            val viewAlphaIn = ObjectAnimator.ofFloat(view, "alpha", 1f, 0f)
            alphaIn.duration = 200
            textMoveIn.duration = 200
            transIn.duration = 200
            viewAlphaIn.duration = 200
            viewAlphaIn.start()
            alphaIn.start()
            transIn.start()
            textMoveIn.start()
        }

        //弹出
        fun viewOut(view: FloatingActionButton, textView: TextView) {
            val transOut = ObjectAnimator.ofFloat(view, "translationY", 0f)
            val textMoveOut = ObjectAnimator.ofFloat(textView, "translationX", textView.translationX / 3, 0f)
            val alphaOut = ObjectAnimator.ofFloat(textView, "alpha", 0f, 0.1f, 0.2f, 1f)
            val viewAlphaOut = ObjectAnimator.ofFloat(view, "alpha", 0f, 0.1f, 0.2f, 1f)
            val textSizeOutX = ObjectAnimator.ofFloat(textView, "scaleX", 0f, 0.2f, 1f)
            val textSizeOutY = ObjectAnimator.ofFloat(textView, "scaleY", 0f, 0.2f, 1f)
            val viewSizeOutX = ObjectAnimator.ofFloat(view, "scaleX", 0f, 1f, 0.9f)
            val viewSizeOutY = ObjectAnimator.ofFloat(view, "scaleY", 0f, 1f, 0.9f)
            transOut.duration = 200
            viewAlphaOut.duration = 200
            alphaOut.duration = 200
            textMoveOut.duration = 200
            textSizeOutX.duration = 200
            textSizeOutY.duration = 200
            viewSizeOutX.duration = 200
            viewSizeOutY.duration = 200
            viewSizeOutX.start()
            viewSizeOutY.start()
            textSizeOutX.start()
            textSizeOutY.start()
            transOut.start()
            textMoveOut.start()
            alphaOut.start()
            viewAlphaOut.start()
        }

        var time0 = 0L
        fab0.setOnClickListener {
            if (fab_about.translationY == 0f) {
                viewIn(fab1, fab1_text, fab1Dy, text1Dx)
                viewIn(fab_color, text_color, fabColorDy, textColorDx)
                viewIn(fab_now_week, text_now, fabNowDy, textNowDx)
                viewIn(fab_about, text_about, fabAboutDy, textAboutDx)
                viewIn(fab_toics, text_toics, fabToicsDy, textToicsDx)
                val rot = ObjectAnimator.ofFloat(fab0, "rotation", 0f)
                rot.duration = 200
                rot.start()
            }
            if (fab1.translationY == fab1Dy) {
                viewOut(fab1, fab1_text)
                viewOut(fab_color, text_color)
                viewOut(fab_now_week, text_now)
                viewOut(fab_about, text_about)
                viewOut(fab_toics, text_toics)
                val rot = ObjectAnimator.ofFloat(fab0, "rotation", 15f, -135f)
                rot.duration = 200
                rot.start()

                if (colorString.exists()) {
                    val primeColor: Int = colorString.readText().toInt()
                    fab_color.background.setTint(primeColor)
                }
            }
            if (System.currentTimeMillis() - time0 < 250) {
                findToday()
                toast("已回到今日课程")
            } else {
                time0 = System.currentTimeMillis()
            }
        }currentTimeMillis
         */
    }
}
