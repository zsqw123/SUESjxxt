package com.jxxt.sues

import android.content.Context
import android.graphics.Color
import android.graphics.drawable.GradientDrawable
import android.util.AttributeSet
import android.view.View
import android.widget.RelativeLayout
import android.widget.TextView
import kotlinx.android.synthetic.main.list_item.view.*
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
            when(primeColor){
                -723724 -> date.setTextColor(Color.parseColor("#000000"))
                -13816531 ->{
                    name.setTextColor(Color.parseColor("#FFFFFF"))
                    name.setBackgroundColor(-13816531)
                    layout.setBackgroundColor(-13816531)
                }
                else ->{
                    name.setTextColor(Color.parseColor("#000000"))
                    name.setBackgroundColor(-723724)
                    layout.setBackgroundColor(-723724)
                }
            }
        }
    }

    fun setData(dataList: Item) {
        date.text = SimpleDateFormat("MM/dd\nHH:mm", Locale.CHINA).format(dataList.date)
        name.text = dataList.name
    }
}