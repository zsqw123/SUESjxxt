package com.jxxt.sues

import android.content.Context
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import java.io.File
import java.text.SimpleDateFormat
import java.util.*

class MainAdapter(private val context: Context, private val dataList: List<Item>) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return Holder(ListItem(context).layout)
    }

    private var primeColor: Int = R.color.colorPrimary
    private val weekFile = File(context.filesDir, "weekNow")
    private val week0 = if (weekFile.exists()) SimpleDateFormat("yyyy-MM-dd", Locale.CHINA).parse(weekFile.readText()) ?: Date() else Date()

    init {
        //Color
        val colorString = File(context.filesDir, "/color")

        if (colorString.exists()) {
            primeColor = colorString.readText().toInt()
        }
    }

    override fun getItemCount(): Int = dataList.size
    private var lightWeek = -1//高亮的周
    private fun week(input: Date): Int = ((input.time - week0.time) / (24 * 3600000)).toInt() / 7
    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        val view = holder.itemView as ListItem
        //Data
        var isSetAlpha = true
        val itemDate: Date = dataList[position].date
        val itemWeek: Int = week(itemDate)

        val now = Date()
        //寻找高亮课程
        if (lightWeek == -1) {
            if (position == 0) {
                if (now < itemDate) lightWeek = itemWeek
            } else if (now in itemDate..dataList[position - 1].date) lightWeek = itemWeek
        }
        if (lightWeek != -1) {
            if (week(itemDate) == lightWeek) isSetAlpha = false
        }

        view.setData(position, dataList, primeColor, week0, isSetAlpha)
        //怎么解决复用导致的数据错乱呢？ 这里只是简单粗暴禁止了复用...
        holder.setIsRecyclable(false)
    }

    class Holder(itemView: View) : RecyclerView.ViewHolder(itemView)
}