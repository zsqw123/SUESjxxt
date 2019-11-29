package com.jxxt.sues

import android.content.Context
import android.graphics.Color
import android.graphics.Typeface
import android.graphics.drawable.GradientDrawable
import android.util.AttributeSet
import android.view.View
import android.widget.RelativeLayout
import android.widget.TextView
import kotlinx.android.synthetic.main.list_item.view.*
import org.jetbrains.anko.textColor
import java.io.File
import java.text.SimpleDateFormat
import java.util.*

data class Item(var date: Date, var name: String)

class ListItem(context: Context, attrs: AttributeSet? = null) : RelativeLayout(context, attrs) {
    init {
        View.inflate(context, R.layout.list_item, this)
        val colorString = File(context.filesDir, "/color")
        if (colorString.exists()) {
            val primeColor: Int = colorString.readText().toInt()
            val tView = findViewById<TextView>(R.id.date).background as GradientDrawable
            tView.setColor(primeColor)
            when (primeColor) {
                //黑色
                -13816531 -> {
                    name.setTextColor(Color.parseColor("#FFFFFF"))
                    day.setTextColor(Color.parseColor("#FFFFFF"))
                    name.setBackgroundColor(-13816531)
                    layout.setBackgroundColor(-13816531)
                }
                //白色 or Other
                -723724 -> date.setTextColor(Color.parseColor("#000000"))
                else -> {
                    name.setTextColor(Color.parseColor("#000000"))
                    day.setTextColor(Color.parseColor("#000000"))
                    name.setBackgroundColor(-723724)
                    layout.setBackgroundColor(-723724)
                }
            }
        }
    }

    fun setData(pos: Int, list: List<Item>, week: Int) {
        date.text = SimpleDateFormat("MM/dd\nHH:mm", Locale.CHINA).format(list[pos].date)
        name.text = list[pos].name
        if (pos != 0) {
            val after = SimpleDateFormat("MM/dd EE", Locale.CHINA).format(list[pos].date)
            val before = SimpleDateFormat("MM/dd EE", Locale.CHINA).format(list[pos - 1].date)
            if (before == after) {
                day.visibility = View.GONE
            } else {
                day.text = after + " 第${week}周"
            }
            //找到今日日程
            val now = Date()
            if (now <= list[pos].date && now >= list[pos - 1].date) {
                name.textColor = Color.parseColor("#CF6F06")
                name.typeface = Typeface.defaultFromStyle(Typeface.BOLD_ITALIC)
            }
        } else {
            day.text = SimpleDateFormat("MM/dd EE", Locale.CHINA).format(list[pos].date) + " 第${week}周"
            val now = Date()
            if (now <= list[pos].date) {
                name.typeface = Typeface.defaultFromStyle(Typeface.BOLD_ITALIC)
            }
        }

    }
}