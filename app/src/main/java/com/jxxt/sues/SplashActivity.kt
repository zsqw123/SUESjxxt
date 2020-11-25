package com.jxxt.sues

import android.app.Activity
import android.os.Bundle
import org.jetbrains.anko.startActivity

class SplashActivity : Activity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        startActivity<MainActivity>()
        finish()
    }
}