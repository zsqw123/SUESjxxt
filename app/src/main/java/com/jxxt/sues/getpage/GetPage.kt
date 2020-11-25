package com.jxxt.sues.getpage

import android.graphics.BitmapFactory
import android.os.Bundle
import android.widget.EditText
import android.widget.ImageView
import androidx.appcompat.app.AppCompatActivity
import com.jxxt.sues.*
import kotlinx.android.synthetic.main.newclass.*
import net.dongliu.requests.Requests
import org.jetbrains.anko.*
import org.jetbrains.anko.sdk27.coroutines.onClick
import java.io.File

class GetPage : AppCompatActivity() {
    private lateinit var username: String
    private lateinit var passwd: String
    private lateinit var captcha: String
    private lateinit var cap: ImageView
    private lateinit var capText: EditText

    private val userFile = File(suesApp.filesDir, "/user0")
    private val passwdFile = File(suesApp.filesDir, "/passwd")

    private val session = Requests.session()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        getCaptha()//获取验证码
        doAsync {
            getXHRsessionID()
        }
        verticalLayout {
            val account = editText { hint = getString(R.string.login_accont) }
            val password = editText { hint = getString(R.string.login_password) }
            if (userFile.exists()) {
                account.setText(userFile.readText())
                password.setText(passwdFile.readText())
            }
            linearLayout {
                capText = editText {
                    hint = "请输入验证码"
                }
                button("刷新验证码") {
                    onClick {
                        getCaptha()
                    }
                }
                cap = imageView {
                    adjustViewBounds = true
                    scaleType = ImageView.ScaleType.FIT_CENTER
                    onClick { getCaptha() }
                }.lparams(height = matchParent, width = matchParent)
            }
            button("登录") {
                onClick {
                    username = account.text.toString()
                    passwd = password.text.toString()
                    userFile.writeText(username)
                    passwdFile.writeText(passwd)
                    captcha = capText.text.toString()
                    doAsync {
                        login()
                        uiThread {
                            toast("若10s无反应请检查账号密码验证码")
                        }
                        val xhrId = getXHRsessionID()
                        val xhrList = getY(xhrId)

                        uiThread {
                            selector("选择要打开的年", xhrList) { _, i ->
                                toast(xhrList[i])
                                doAsync {
                                    val termList = getTerms(xhrId, xhrList[i])
                                    uiThread {
                                        selector("选择要打开的学期", termList) { _, i0 ->
                                            toast("${xhrList[i]} 第${termList[i0]}学期")
                                            doAsync {
                                                FindContent.resolveClasses(getCourseTable(xhrList[i], termList[i0]))
                                                uiThread {
                                                    toast("课程导入成功")
                                                    getCaptha()
                                                    doAsync {
                                                        Thread.sleep(1000)
                                                        startActivity<MainActivity>()
                                                    }
                                                }
                                            }
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }
            textView(
                "1.教学系统会在晚上23:13以后关闭(可能) 此时软件无法读取数据\n\n" +
                        "2.读取数据时间取决于你的网速 且需要连接校园网(360connect 网康也行 后续可能考虑软件自带)\n" +
                        "\n3.选择学期全程不可返回 返回可能导致逻辑数据错乱\n\n" +
                        "4.若无法登录时需要重新获取验证码(点击图片)\n\n" +
                        "导入完成后便会重启本应用"
            )
            button("不信任开发者 前往学校官网登录读取(不推荐)") {
                onClick {
                    startActivity<NewAct>()
                }
            }
        }
    }

    //获取验证码
    private fun getCaptha() {
        try {
            doAsync {
                val img = session.get(
                    "http://jxxt.sues.edu.cn/eams/captcha/image.action"
                ).timeout(10000).send().readToBytes()
                val img0 = BitmapFactory.decodeByteArray(img, 0, img.size)
                uiThread {
                    cap.imageBitmap = img0
                }
            }
        } catch (e: Exception) {
            toast("验证码获取失败 请重新打开该页面重试")
        }
    }

    //Login
    private fun login() {
        val data = hashMapOf(
            "loginForm.name" to username,
            "loginForm.password" to passwd,
            "encodedPassword" to "",
            "loginForm.captcha" to captcha
        )
        session.post(
            "http://jxxt.sues.edu.cn/eams/login.action"
        ).params(data).timeout(10000).send().readToText()
    }

    //获取生成XHRSessionID需要的XHROriSessionID
    private fun getXHRsessionID(): String {
        val r = session.get(
            "http://jxxt.sues.edu.cn/eams/dwr/engine.js"
        ).timeout(10000).send().readToText()
        val reg = Regex("""dwr.engine._origScriptSessionId = .*?;""")
        val reg0 = Regex("""".*?"""")
        val id = reg.find(r)?.value?.let { reg0.find(it)?.value }
        val id0 = id?.replace("\"", "") ?: ""
        val r3 = (100..999).random().toString()//三个随机数
        return id0 + r3
    }

    //获取教学系统允许查询的教学年
    private fun getY(xhrID: String): List<String> {
        val payload = hashMapOf(
            "callCount" to "1",
            "page" to "/eams/courseTableForStd.action?method=stdHome",
            "httpSessionId" to "",
            "scriptSessionId" to xhrID,
            "c0-scriptName" to "semesterDao",
            "c0-methodName" to "getYearsOrderByDistance",
            "c0-id" to "0",
            "c0-param0" to "string:1",
            "batchId" to "0"
        )
        val r = session.post(
            "http://jxxt.sues.edu.cn/eams/dwr/call/plaincall/semesterDao.getYearsOrderByDistance.dwr"
        ).timeout(10000).params(payload).send().readToText()
        val reg = Regex("""\[.*?]""")
        val reg0 = Regex("""".*?"""")
        val res = reg.find(r)?.value
        val list = mutableListOf<String>()
        res?.let { reg0.findAll(it) }?.forEach {
            list += it.value.replace("\"", "")
        }
        return list
    }

    // 获取当前教学年对应的学期选项
    private fun getTerms(xhrID: String, yearStr: String): List<String> {
        //XHR调用请求参数
        val payload = hashMapOf(
            "callCount" to "1",
            "page" to "/eams/courseTableForStd.action?method=stdHome",
            "httpSessionId" to "",
            "scriptSessionId" to xhrID,
            "c0-scriptName" to "semesterDao",
            "c0-methodName" to "getTermsOrderByDistance",
            "c0-id" to "0",
            "c0-param0" to "string:1",
            "c0-param1" to "string:$yearStr",
            "batchId" to "1"
        )
        val r = session.post("http://jxxt.sues.edu.cn/eams/dwr/call/plaincall/semesterDao.getTermsOrderByDistance.dwr")
            .timeout(10000).params(payload).send().readToText()
        val reg = Regex("""\[.*?]""")
        val reg0 = Regex("""".*?"""")
        val res = reg.find(r)?.value
        val list = mutableListOf<String>()
        res?.let { reg0.findAll(it) }?.forEach {
            list += it.value.replace("\"", "")
        }
        return list
    }

    //获取课程列表
    private fun getCourseTable(yearStr: String, termStr: String): String {
        val r = session.get("http://jxxt.sues.edu.cn/eams/courseTableForStd.action?method=stdHome")
            .timeout(10000).send().readToText()
        val url = FindContent.findText(r)
        if (url == "no") {
            textView2.text = "你网络太菜了(延迟大于5秒)\nor 没连接网康VPN\nor 没登陆"
        }
        val payload = hashMapOf(
            "ignoreHead" to "1",
            "semester.id" to "semesterId",
            "semester.calendar.id" to "1",
            "semester.schoolYear" to yearStr,
            "semester.name" to termStr,
            "startWeek" to "1"
        )
        return session.post(url).timeout(10000).params(payload).send().readToText()
    }
}