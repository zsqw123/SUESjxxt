package com.jxxt.sues.widget

import android.annotation.SuppressLint
import android.app.Application
import android.content.Context

@SuppressLint("Registered")
class Utils private constructor() :Application(){

    companion object {

        private var context: Context? = null

        //初始化工具类
        fun init(context: Context) {
            Utils.context = context.applicationContext
        }
        /**
           获取ApplicationContext
         */
        fun getContext(): Context {
            val i = context
            if (i != null) return i
            throw NullPointerException("u should init first")
        }

    }
}