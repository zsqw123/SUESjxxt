package com.jxxt.sues

import android.app.Application
import com.chibatching.kotpref.Kotpref

lateinit var suesApp: Application

class MyApp : Application() {
    override fun onCreate() {
        super.onCreate()
        suesApp = this
        Kotpref.init(this)
//        Utils.init(this)
    }
}
