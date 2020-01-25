package com.jxxt.sues

import android.content.Context
import android.graphics.Color
import android.graphics.Typeface
import android.graphics.drawable.GradientDrawable
import android.util.AttributeSet
import android.view.Gravity
import android.view.View
import android.widget.RelativeLayout
import android.widget.TextView
import androidx.core.graphics.ColorUtils
import kotlinx.android.synthetic.main.activity_main.view.*
import org.jetbrains.anko.*
import java.text.SimpleDateFormat
import java.util.*

data class Item(var date: Date, var name: String, var room: String? = null)

class ListItem(context: Context, attrs: AttributeSet? = null) : RelativeLayout(context, attrs) {
    //layout定义
    lateinit var layout: RelativeLayout
    private lateinit var day: TextView
    private lateinit var date: TextView
    lateinit var name: TextView
    private lateinit var itemPadding: TextView
    private lateinit var classroom: TextView


    fun setData(pos: Int, list: List<Item>, primeColor: Int?, week0: Date, isSetAlpha: Boolean) {
        //Color
        if (primeColor != null) {
            val dark = ColorUtils.calculateLuminance(primeColor) <= 0.2
            val lightDark = ColorUtils.calculateLuminance(primeColor) <= 0.4
            if (dark) {
                //黑色
                itemAnko(Color.WHITE)
                layout.setBackgroundColor(Color.BLACK)
                if (pos == 0) {
                    itemPadding.visibility = View.VISIBLE
                    itemPadding.backgroundColor = Color.BLACK
                }
            } else {
                //白色
                itemAnko(Color.BLACK)
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

    private fun itemAnko(colorTheme: Int): RelativeLayout {
        layout = relativeLayout {
            itemPadding = textView {
                id = R.id.itemPadding
                visibility = View.GONE
                backgroundColor = Color.WHITE
            }.lparams(matchParent, dip(120))
            day = textView {
                id = R.id.day
                gravity = Gravity.CENTER
                text = "today"
                textSize = 25f
                textColor = colorTheme
                setTypeface(typeface, Typeface.BOLD)
            }.lparams(width = matchParent) {
                margin = dip(15)
                bottomOf(itemPadding)
            }
            relativeLayout {
                date = textView {
                    id = R.id.date
                    backgroundResource = R.drawable.shape
                    elevation = dip(1.41f).toFloat()
                    minimumHeight = dip(45)
                    text = "date"
                    textColor = colorTheme
                    textSize = 30f
                    setTypeface(typeface, Typeface.BOLD)
                    gravity = Gravity.CENTER
                }.lparams {
                    marginStart = dip(4)
                    topMargin = dip(2)
                    marginEnd = dip(4)
                    bottomMargin = dip(4)
                }
                verticalLayout {
                    name = textView {
                        text = "className"
                        textSize = 18f
                        textColor = colorTheme
                        maxLines = 1
                    }.lparams {
                        marginStart = dip(14)
                        marginEnd = dip(14)
                    }
                    classroom = textView {
                        text = "classroom"
                        textSize = 18f
                        textColor = colorTheme
                        maxLines = 1
                    }.lparams {
                        marginStart = dip(14)
                        marginEnd = dip(14)
                    }
                }.lparams {
                    height = matchParent
                    width = matchParent
                    rightOf(date)
                }
            }.lparams {
                below(day)
            }
        }
        return layout
    }
}