package com.jxxt.sues.widget

import android.content.Intent
import android.widget.RemoteViewsService

class Service : RemoteViewsService(){
    override fun onGetViewFactory(p0: Intent):RemoteViewsFactory {
        return Factory(this,p0)
    }
}