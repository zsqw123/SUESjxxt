package com.jxxt.sues

import android.app.Application

lateinit var suesApp: Application

class MyApp : Application() {
    override fun onCreate() {
        super.onCreate()
        suesApp = this
//        Utils.init(this)
    }
}
