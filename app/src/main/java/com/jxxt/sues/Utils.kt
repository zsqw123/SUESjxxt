package com.jxxt.sues

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.lifecycleScope
import com.chibatching.kotpref.KotprefModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch

fun LifecycleOwner.doAsync(event: () -> Unit) {
    lifecycleScope.launch(Dispatchers.IO) { event() }
}

fun uiThread(event: () -> Unit) {
    GlobalScope.launch((Dispatchers.Main)) { event() }
}

fun toast(id: Int) {
    uiThread { Toast.makeText(suesApp, id, Toast.LENGTH_SHORT).show() }
}

fun toast(string: String) {
    uiThread { Toast.makeText(suesApp, string, Toast.LENGTH_SHORT).show() }
}

fun Activity.startActivity(clazz: Class<*>) {
    startActivity(Intent(this, clazz))
}

/**
 *  only can set
 */
//var View.backgroundColor: Int
//    get() = 0
//    set(value) {
//        background.setTint(value)
//    }

/**
 *  only can set
 */
//var TextView.textColor: Int
//    get() = 0
//    set(value) {
//        setTextColor(value)
//    }

/**
 *  convert dp to px
 */
val Int.dp
    get() = this * suesApp.resources.displayMetrics.density.toInt()

object SuesPref : KotprefModel() {
    var isdark by booleanPref(false)
    var currentWeek by intPref(0)
    var mainColor by intPref(0)
}

fun View.setMarginBottomPlusNavBarHeight() {
    if (this.layoutParams is ViewGroup.MarginLayoutParams) {
        val lp = this.layoutParams as ViewGroup.MarginLayoutParams
        lp.bottomMargin = context.getNavigationBarHeight() + lp.bottomMargin
    }
}
fun Context.getNavigationBarHeight(): Int {
    val resourceId = this.resources.getIdentifier("navigation_bar_height", "dimen", "android")
    return if (resourceId > 0) {
        this.resources.getDimensionPixelSize(resourceId)
    } else {
        0
    }
}
