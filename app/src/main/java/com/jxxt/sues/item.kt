package com.jxxt.sues

import android.content.Context
import android.graphics.Color
import android.graphics.Typeface
import android.graphics.drawable.GradientDrawable
import android.util.AttributeSet
import android.view.View
import android.widget.RelativeLayout
import android.widget.TextView
import androidx.core.graphics.ColorUtils
import kotlinx.android.synthetic.main.classtable_item.view.*
import org.jetbrains.anko.*
import java.text.SimpleDateFormat
import java.util.*

data class Item(var date: Date, var name: String, var room: String? = null)

class ListItem(context: Context, attrs: AttributeSet? = null) : RelativeLayout(context, attrs) {
    val layout: RelativeLayout by lazy { relativeLayout(SuesPref.mainColor) }
    fun setData(pos: Int, list: List<Item>, primeColor: Int?, week0: Date, isSetAlpha: Boolean) {
        //Color
        if (primeColor != null) {
            val dark = ColorUtils.calculateLuminance(primeColor) <= 0.2
            val lightDark = ColorUtils.calculateLuminance(primeColor) <= 0.4
            if (dark) {
                //黑色
                layout.setBackgroundColor(Color.BLACK)
                if (pos == 0) {
                    itemPadding.visibility = View.VISIBLE
                    itemPadding.backgroundColor = Color.BLACK
                }
            } else {
                //白色
                if (lightDark) date.setTextColor(Color.WHITE)
                layout.setBackgroundColor(Color.WHITE)
                if (pos == 0) {
                    itemPadding.visibility = View.VISIBLE
                    itemPadding.backgroundColor = Color.WHITE
                }
            }
            val tView = date.background as GradientDrawable
            tView.setColor(primeColor)
        }
        //Data
        val alphaView: List<TextView> = listOf(day, date, name, classroom)
        val item: Item = list[pos]
        val itemBefore: Item? = if (pos == 0) null else list[pos - 1]
        val itemWeek: Int = ((item.date.time - week0.time) / (24 * 3600000)).toInt() / 7

        date.text = SimpleDateFormat("HH:mm", Locale.CHINA).format(item.date)
        name.text = item.name
        classroom.text = "-> 位置:" + list[pos].room

        when (pos) {
            0 -> {
                day.text = SimpleDateFormat("MM/dd EE", Locale.CHINA).format(item.date) + " 第${itemWeek}周"
                val now = Date()
                if (now <= item.date) {
                    name.textColor = Color.parseColor("#CF6F06")
                    name.typeface = Typeface.defaultFromStyle(Typeface.BOLD_ITALIC)
                }
            }
            else -> {
                val after = SimpleDateFormat("MM/dd EE", Locale.CHINA).format(item.date)
                val before = SimpleDateFormat("MM/dd EE", Locale.CHINA).format(itemBefore!!.date)
                if (before == after) {
                    day.visibility = View.GONE
                } else {
                    day.text = after + " 第${itemWeek}周"
                }
                //找到今日日程
                val now = Date()
                if (now in item.date..itemBefore.date) {
                    name.textColor = Color.parseColor("#CF6F06")
                    name.typeface = Typeface.defaultFromStyle(Typeface.BOLD_ITALIC)
                }
            }
        }
        if (isSetAlpha) alphaView.forEach { it.alpha = 0.5f }
    }

    private fun relativeLayout(colorTheme: Int): RelativeLayout {
        return inflate(context, R.layout.classtable_item, this).apply {
            listOf(this@ListItem.name, this@ListItem.date, this@ListItem.day, this@ListItem.classroom).forEach { it.textColor = colorTheme }
        } as RelativeLayout
    }
}