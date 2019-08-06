package com.jxxt.sues

import android.app.Application
import com.jxxt.sues.widget.Utils

class MyApp : Application() {
    override fun onCreate() {
        super.onCreate()
        Utils.init(this)
    }
}
