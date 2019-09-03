package com.jxxt.sues

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.method.ScrollingMovementMethod
import android.webkit.JavascriptInterface
import android.webkit.WebView
import android.webkit.WebViewClient
import kotlinx.android.synthetic.main.newclass.*
import android.widget.TextView
import androidx.core.view.isVisible
import org.jetbrains.anko.*
import java.io.File
import java.lang.Exception

var str = ""
var str1 = ""

class NewAct : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.newclass)
        //webView初始值
        val url0 = "http://jxxt.sues.edu.cn/eams/courseTableForStd.action?method=stdHome"
        //textView
        val view = findViewById<TextView>(R.id.textView2)
        view.movementMethod = ScrollingMovementMethod.getInstance()
        //webView初始化
        fun setWeb(a: WebView) {
            a.settings.setJavaScriptEnabled(true)//JavaScript脚本支持
            a.settings.setDomStorageEnabled(true)//访问储存
            a.requestFocus()
            a.settings.setUseWideViewPort(true)//这里需要设置为true，才能让Webivew支持<meta>标签的viewport属性
            a.settings.setLoadWithOverviewMode(true)//是否使用概览模式
            a.settings.setSupportZoom(true)//是否可放大画面
            a.settings.setBuiltInZoomControls(true)//是否可自由放大画面
            a.addJavascriptInterface(InJavaScriptLocalObj(), "java_obj")//JavaScript接口 需要用这玩意获取html
            a.setWebViewClient(object : WebViewClient() {
                override fun shouldOverrideUrlLoading(view: WebView, url: String): Boolean {
                    view.loadUrl(url)
                    return true
                }

                override fun onPageFinished(view: WebView, url: String) {
                    // 在结束加载网页时会回调
                    // 获取页面内容
                    view.loadUrl(
                        "javascript:window.java_obj.showSource("
                                + "document.getElementsByTagName('html')[0].innerHTML);"
                    )
                    super.onPageFinished(view, url)
                }
            })

        }
        setWeb(webView0)
        webView0.loadUrl(url0)
        //login or logined
        loadButton.setOnClickListener {
            val dialog = indeterminateProgressDialog("loading...", "如果3s以上没反应的话请检查网络连接(网康VPN)")
            dialog.setCanceledOnTouchOutside(false)
            setWeb(webView0)
            webView0.loadUrl(url0)
            //判断是否登录
            doAsync {
                Thread.sleep(3000)//超时3s
                uiThread {
                    if (FindContext().findText(str) == "no") {
                        textView2.text = "你网络太菜了 or 没连接网康VPN or 没登陆"
                    } else {
                        loadButton.isVisible = false
                        okButton.isVisible = true
                        textView2.text = "请继续点击左上角按钮读取数据"
                    }
                }
                dialog.dismiss()
            }
        }
        //get data
        okButton.setOnClickListener {
            //resolve url0->html to get url1
            doAsync {
                val url1 = FindContext().findText(str)
                uiThread {
                    class InJavaScriptLocalObj1 {
                        @JavascriptInterface
                        fun showSource(html: String) {
                            str1 = html
                        }
                    }

                    fun setWeb1(a: WebView) {
                        a.settings.setJavaScriptEnabled(true)//JavaScript脚本支持
                        a.settings.setDomStorageEnabled(true)//访问储存
                        a.requestFocus()
                        a.settings.setUseWideViewPort(true)//这里需要设置为true，才能让Webivew支持<meta>标签的viewport属性
                        a.settings.setLoadWithOverviewMode(true)//是否使用概览模式
                        a.settings.setSupportZoom(true)//是否可放大画面
                        a.settings.setBuiltInZoomControls(true)//是否可自由放大画面
                        a.addJavascriptInterface(InJavaScriptLocalObj1(), "java_obj1")//JavaScript接口 需要用这玩意获取html
                        a.setWebViewClient(object : WebViewClient() {
                            override fun shouldOverrideUrlLoading(view: WebView, url: String): Boolean {
                                view.loadUrl(url)
                                return true
                            }

                            override fun onPageFinished(view: WebView, url: String) {
                                // 在结束加载网页时会回调
                                // 获取页面内容
                                view.loadUrl(
                                    "javascript:window.java_obj1.showSource("
                                            + "document.getElementsByTagName('html')[0].innerHTML);"
                                )
                                super.onPageFinished(view, url)
                            }
                        })

                    }
                    setWeb1(webView0)
                    webView0.loadUrl(url1)
                }
            }
            //dialog loading...
            val dialog0 = indeterminateProgressDialog("loading...", "耐心等待3s")
            dialog0.setCanceledOnTouchOutside(false)
            //find Classes
            Thread {
                try {
                    Thread.sleep(3000)
                    textView2.text = str1
                    val content = FindContext().resolveClasses(str1)
//                    textView2.text=content
                    var text = ""
                    content.forEach { key, value ->
                        text = "$text$key $value\n"
                    }
                    val file = File(filesDir, "/a")
                    file.writeText(str1)
                    dialog0.dismiss()
                    textView2.text = "数据获取完成! 马上进入主界面! \n\n$text"
                    Thread.sleep(1000)
                    startActivity(intentFor<MainActivity>().newTask().clearTask())
                } catch (e: Exception) {
                    textView2.text = "你网络太菜了 or 没连接网康VPN or 没登陆"
                }
            }.start()

        }
    }

    //显示网页源代码
    class InJavaScriptLocalObj {
        @JavascriptInterface
        fun showSource(html: String) {
            str = html
        }
    }
}