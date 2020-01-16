package com.jxxt.sues

import android.content.Context
import android.graphics.Color
import android.graphics.Typeface
import android.graphics.drawable.GradientDrawable
import android.util.AttributeSet
import android.view.Gravity
import android.view.View
import android.widget.RelativeLayout
import androidx.core.graphics.ColorUtils
import kotlinx.android.synthetic.main.list_item.view.*
import org.jetbrains.anko.*
import java.io.File
import java.text.SimpleDateFormat
import java.util.*

data class Item(var date: Date, var name: String, var room: String? = null)

class ListItem(context: Context, attrs: AttributeSet? = null) : RelativeLayout(context, attrs) {
    init {
        itemAnko()
        //旧的view flate方式 性能较差
//        View.inflate(context, R.layout.list_item, this)
        val colorString = File(context.filesDir, "/color")
        if (colorString.exists()) {
            val primeColor: Int = colorString.readText().toInt()
        }
    }

    fun setData(pos: Int, list: List<Item>, week: Int, primeColor: Int?) {
        //Color
        if (primeColor != null) {
            val tView = date.background as GradientDrawable
            tView.setColor(primeColor)
            val dark = ColorUtils.calculateLuminance(primeColor) <= 0.2
            val lightDark = ColorUtils.calculateLuminance(primeColor) <= 0.4
            if (dark) {
                //黑色
                date.setTextColor(Color.WHITE)
                day.setTextColor(Color.WHITE)
                name.setTextColor(Color.WHITE)
                classroom.setTextColor(Color.WHITE)
                layout.setBackgroundColor(Color.BLACK)
            } else {
                //白色
                if (lightDark) {
                    date.setTextColor(Color.WHITE)
                } else {
                    date.setTextColor(Color.BLACK)
                }
                day.setTextColor(Color.BLACK)
                name.setTextColor(Color.BLACK)
                classroom.setTextColor(Color.BLACK)
                layout.setBackgroundColor(Color.WHITE)
            }
        }
        //Data
        date.text = SimpleDateFormat("HH:mm", Locale.CHINA).format(list[pos].date)
        name.text = list[pos].name
        classroom.text = "-> 位置:" + list[pos].room
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

    private fun itemAnko(): RelativeLayout {
        return relativeLayout {
            id = R.id.layout
            textView {
                id = R.id.day
                gravity = Gravity.CENTER
                text = "today"
                textSize = 25f //sp
                setTypeface(typeface, Typeface.BOLD)
            }.lparams(width = matchParent) {
                margin = dip(15)
            }
            relativeLayout {
                textView {
                    id = R.id.date
                    backgroundResource = R.drawable.shape
                    elevation = dip(1.41f).toFloat()
                    minimumHeight = dip(45)
                    text = "date"
                    textColor = Color.WHITE
                    textSize = 30f //sp
                    setTypeface(typeface, Typeface.BOLD)
                    gravity = Gravity.CENTER
                }.lparams {
                    marginStart = dip(4)
                    topMargin = dip(2)
                    marginEnd = dip(4)
                    bottomMargin = dip(4)
                }
                verticalLayout {
                    textView {
                        id = R.id.name
                        //android:layout_alignParentEnd = true //not support attribute
                        //android:layout_toEndOf = @+id/date //not support attribute
                        text = "className"
                        textSize = 18f //sp
                        maxLines = 1
                    }.lparams {
                        marginStart = dip(14)
                        marginEnd = dip(14)
                    }
                    textView {
                        //android:layout_alignStart = @+id/name //not support attribute
                        //android:layout_alignParentEnd = true //not support attribute
                        text = "classroom"
                        textSize = 18f //sp
                        id = R.id.classroom
                        maxLines = 1
                    }.lparams {
                        marginStart = dip(14)
                        marginEnd = dip(14)
                    }
                }.lparams {
                    height = matchParent
                    width = matchParent
                    rightOf(R.id.date)
                }
            }.lparams {
                below(R.id.day)
            }


        }
    }

}