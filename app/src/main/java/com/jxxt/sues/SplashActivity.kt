package com.jxxt.sues

import android.app.Activity
import android.graphics.Color
import android.os.Bundle
import android.view.Gravity
import org.jetbrains.anko.*

class SplashActivity : Activity() {

    override fun onWindowFocusChanged(hasFocus: Boolean) {
        super.onWindowFocusChanged(hasFocus)
        doAsync {
            startActivity<MainActivity>()
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        linearLayout {
            gravity = Gravity.CENTER
            backgroundColor = Color.parseColor("#FFFFFF")
            textView {
                text = "loading..."
                textSize = 30f
            }
        }
    }
}
