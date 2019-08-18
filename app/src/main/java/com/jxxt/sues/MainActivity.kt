package com.jxxt.sues

import android.animation.ObjectAnimator
import android.content.Intent
import android.graphics.Color
import android.graphics.drawable.GradientDrawable
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.InputType
import android.view.View
import android.view.WindowManager
import android.widget.TextView
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.android.material.floatingactionbutton.FloatingActionButton
import kotlinx.android.synthetic.main.activity_main.*
import org.jetbrains.anko.*
import org.jetbrains.anko.sdk27.coroutines.onClick
import java.io.File
import java.net.URL
import java.text.SimpleDateFormat
import java.util.*

class MainActivity : AppCompatActivity() {
    //read and judge
    private lateinit var file: File
    private lateinit var colorString: File
    private lateinit var weekNow: File

    private val colorNameList = listOf("简洁白", "少女粉", "夜间模式", "姨妈红", "咸蛋黄", "早苗绿", "胖次蓝", "基佬紫")
    private val colorList = listOf("#F4F4F4", "#FA7298", "#2D2D2D", "#F44236", "#FEC107", "#8BC24A", "#2196F3", "#9C28B1")
    private val stausColorList = listOf("#E6E6E6", "#FB628D", "#1D1D1D", "#F23022", "#EEB507", "#7FB83C", "#148EEE", "#9121A6")

    private var isOnce = true
    private var fab1Dy = 0f
    private var fabColorDy = 0f
    private var fabAboutDy = 0f
    private var fabNowDy = 0f

    private var text1Dx = 0f
    private var textColorDx = 0f
    private var textNowDx = 0f
    private var textAboutDx = 0f
    override fun onWindowFocusChanged(hasFocus: Boolean) {
        super.onWindowFocusChanged(hasFocus)
        if (isOnce) {
            //记录坐标
            fab1Dy = fab0.y - fab1.y
            fabColorDy = fab0.y - fab_color.y
            fabAboutDy = fab0.y - fab_about.y
            fabNowDy = fab0.y - fab_now_week.y

            text1Dx = fab1.x - fab1_text.x
            textAboutDx = fab1.x - text_about.x
            textNowDx = fab1.x - text_now.x
            textColorDx = fab1.x - text_color.x
            //赋初值
            fab1.y = fab0.y
            fab_about.y = fab0.y
            fab_now_week.y = fab0.y
            fab_color.y = fab0.y

            fab1_text.x = fab1.x
            text_color.x = fab_color.x
            text_about.x = fab_about.x
            text_now.x = fab_now_week.x

            fab1_text.alpha = 0f
            text_color.alpha = 0f
            text_about.alpha = 0f
            text_now.alpha = 0f
            fab_color.alpha = 0f
            fab_now_week.alpha = 0f
            fab_about.alpha = 0f
            fab1.alpha = 0f

            isOnce = !isOnce
        }

    }

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
                    val week0 = Calendar.getInstance()
                    val date = SimpleDateFormat("yyyy-MM-dd", Locale.CHINA).parse(weeknow)
                    week0.time = date!!
                    week0.set(Calendar.HOUR_OF_DAY, 1)
                    val now = Calendar.getInstance()
                    val a = now.timeInMillis - week0.timeInMillis
                    val week = if (a > 0) (a / (24 * 3600000)).toInt() / 7 else (a / (24 * 3600000)).toInt() / 7 - 1
                    text_now.text = " 当前第${week}周 "
                }
                val content = Show().textShow(text, weeknow)
                uiThread {
                    mainView.apply {
                        setHasFixedSize(true)
                        layoutManager = LinearLayoutManager(context)
                        adapter = MainAdapter(context, content)
                        val viewList = listOf(fab1, fab_color, fab_about, fab_now_week, text_color, text_now, text_about, fab1_text)
                        addOnScrollListener(RecListener(fab0, viewList))
                    }
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
            }
            //ColorSettings
            if (colorString.exists()) {
                val primeColor: Int = colorString.readText().toInt()
                for (i in colorList.indices) {
                    if (Color.parseColor(colorList[i]) == primeColor) {
                        text_color.text = " ${colorNameList[i]} "
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
        //导入课程
        fab1.setOnClickListener {
            startActivity<NewAct>()
        }

        //当前周
        fab_now_week.setOnClickListener {
            alert {
                customView {
                    verticalLayout {
                        //标题
                        toolbar {
                            lparams(width = matchParent, height = wrapContent)
                            title = "设置当前周数(整数 可选正负)"
                        }
                        //输入框
                        val task = editText {
                            hint = "当前周"
                            inputType = InputType.TYPE_CLASS_NUMBER
                            padding = dip(20)
                        }
                        //button
                        negativeButton("OK(正周数)") {
                            if (task.text.toString().isEmpty()) {
                                toast("没当前周你玩个鸡儿??及你太美")
                            } else {
                                val weeknow = task.text.toString().toInt()
                                val week0 = Calendar.getInstance(Locale.CHINA)
                                week0.firstDayOfWeek = Calendar.MONDAY
                                week0.set(Calendar.DAY_OF_WEEK, Calendar.MONDAY)
                                week0.add(Calendar.DATE, -7 * weeknow)
                                weekNow.writeText(SimpleDateFormat("yyyy-MM-dd", Locale.CHINA).format(week0.time))
                                toast("请结束软件进程并重启即可看到效果")
                            }
                        }
                        positiveButton("OK(负周数)") {
                            if (task.text.toString().isEmpty()) {
                                toast("没当前周你玩个鸡儿??及你太美")
                            } else {
                                val a = "-" + task.text.toString()
                                val weeknow = a.toInt()
                                val week0 = Calendar.getInstance(Locale.CHINA)
                                week0.firstDayOfWeek = Calendar.MONDAY
                                week0.set(Calendar.DAY_OF_WEEK, Calendar.MONDAY)
                                week0.add(Calendar.DATE, -7 * weeknow)
                                weekNow.writeText(SimpleDateFormat("yyyy-MM-dd", Locale.CHINA).format(week0.time))
                                toast("请结束软件进程并重启即可看到效果")
                            }
                        }
                    }
                }
            }.show()
        }
        //关于
        fab_about.setOnClickListener {
            alert {
                customView {
                    verticalLayout {
                        //标题
                        toolbar {
                            lparams(width = matchParent, height = wrapContent)
                            title = "关于作者"
                        }
                        val text = textView(R.string.about)
                        val web = webView()
                        button("捐赠!! 打赏!! 点我!!!\n打开浏览器以后选择使用支付宝打开!!") {
                            onClick {
                                doAsync {
                                    val uri = Uri.parse("https://qr.alipay.com/fkx05866rmc3tvpisucbsef")
                                    val intent = Intent(Intent.ACTION_VIEW, uri)
                                    startActivity(intent)
                                }
                            }
                        }
                    }
                }
            }.show()
        }
        //颜色选择
        fab_color.setOnClickListener {
            selector("请选择主题色", colorNameList) { _, i ->
                text_color.text = " ${colorNameList[i]} "
                val primeColor: Int = Color.parseColor(colorList[i])
                progressBar.visibility = View.VISIBLE
                fab_color.background.setTint(primeColor)
                window.statusBarColor = Color.parseColor(stausColorList[i])

                window.setFlags(
                    WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE,
                    WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE
                )
                doAsync {
                    val text = file.readText()
                    colorString = File(filesDir, "/color")
                    colorString.writeText(primeColor.toString())

                    var weeknow = ""
                    if (weekNow.exists()) {
                        weeknow = weekNow.readText()
                    }
                    val content = Show().textShow(text, weeknow)
                    uiThread {
                        mainView.apply {
                            setHasFixedSize(true)
                            layoutManager = LinearLayoutManager(context)
                            adapter = MainAdapter(context, content)
                        }
                        window.clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE)
                        progressBar.visibility = View.INVISIBLE
                    }
                }
            }
        }

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

        fab0.setOnClickListener {
            if (fab_about.translationY == 0f) {
                viewIn(fab1, fab1_text, fab1Dy, text1Dx)
                viewIn(fab_color, text_color, fabColorDy, textColorDx)
                viewIn(fab_now_week, text_now, fabNowDy, textNowDx)
                viewIn(fab_about, text_about, fabAboutDy, textAboutDx)
                val rot = ObjectAnimator.ofFloat(fab0, "rotation", 0f)
                rot.duration = 200
                rot.start()
            }
            if (fab1.translationY == fab1Dy) {
                viewOut(fab1, fab1_text)
                viewOut(fab_color, text_color)
                viewOut(fab_now_week, text_now)
                viewOut(fab_about, text_about)
                val rot = ObjectAnimator.ofFloat(fab0, "rotation", 15f, -135f)
                rot.duration = 200
                rot.start()

                if (colorString.exists()) {
                    val primeColor: Int = colorString.readText().toInt()
                    fab_color.background.setTint(primeColor)
                }
            }
        }
    }
}

