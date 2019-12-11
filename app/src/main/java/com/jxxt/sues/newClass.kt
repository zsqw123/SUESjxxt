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
            a.settings.javaScriptEnabled = true//JavaScript脚本支持
            a.settings.domStorageEnabled = true//访问储存
            a.requestFocus()
            a.settings.useWideViewPort = true//这里需要设置为true，才能让Webivew支持<meta>标签的viewport属性
            a.settings.loadWithOverviewMode = true//是否使用概览模式
            a.settings.setSupportZoom(true)//是否可放大画面
            a.settings.builtInZoomControls = true//是否可自由放大画面
            a.addJavascriptInterface(InJavaScriptLocalObj(), "java_obj")//JavaScript接口 需要用这玩意获取html
            a.webViewClient = object : WebViewClient() {
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
            }

        }
        setWeb(webView0)
        webView0.loadUrl("http://jxxt.sues.edu.cn")
        //login or logined
        loadButton.setOnClickListener {
            setWeb(webView0)
            webView0.loadUrl(url0)
            textView2.text = "加载网页中 延迟5秒"
            //判断是否登录
            doAsync {
                Thread.sleep(5000)//超时3s
                uiThread {
                    if (FindContext().findText(str) == "no") {
                        textView2.text = "你网络太菜了(延迟大于5秒)\nor 没连接网康VPN\nor 没登陆"
                    } else {
                        loadButton.isVisible = false
                        okButton.isVisible = true
                        textView2.text = "\n\n请继续点击左上角按钮读取数据"
                    }
                }
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
                            println(str1)
                        }
                    }

                    fun setWeb1(a: WebView) {
                        a.settings.javaScriptEnabled = true//JavaScript脚本支持
                        a.settings.domStorageEnabled = true//访问储存
                        a.requestFocus()
                        a.settings.useWideViewPort = true//这里需要设置为true，才能让Webivew支持<meta>标签的viewport属性
                        a.settings.loadWithOverviewMode = true//是否使用概览模式
                        a.settings.setSupportZoom(true)//是否可放大画面
                        a.settings.builtInZoomControls = true//是否可自由放大画面
                        a.addJavascriptInterface(InJavaScriptLocalObj1(), "java_obj1")//JavaScript接口 需要用这玩意获取html
                        a.webViewClient = object : WebViewClient() {
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
                        }

                    }
                    setWeb1(webView0)
                    webView0.loadUrl(url1)
                }
            }
            //find Classes
            Thread {
                try {
                    Thread.sleep(5000)
                    textView2.text = str1
                    FindContext().resolveClasses(str1)
                    Thread.sleep(1000)
                    startActivity(intentFor<MainActivity>().newTask().clearTask())
                } catch (e: Exception) {
                    textView2.text = "你网络太菜了(或延迟大于5s) \nor 没连接网康VPN \nor 没登陆"
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