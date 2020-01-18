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
        return Holder(ListItem(context))
    }

    private var primeColor: Int = R.color.colorPrimary
    private val weekFile = File(context.filesDir, "weekNow")
    private val week0 = if (weekFile.exists()) SimpleDateFormat("yyyy-MM-dd", Locale.CHINA).parse(weekFile.readText()) ?: Date() else Date()
    private val posDate: Calendar = Calendar.getInstance()

    init {
        //Color
        val colorString = File(context.filesDir, "/color")

        if (colorString.exists()) {
            primeColor = colorString.readText().toInt()
        }
    }

    override fun getItemCount(): Int = dataList.size
    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        val view = holder.itemView as ListItem
        //Data
        posDate.time = dataList[position].date
        val a = posDate.timeInMillis - week0.time
        val week = (a / (24 * 3600000)).toInt() / 7

        view.setData(position, dataList, week, primeColor)
        //怎么解决复用导致的数据错乱呢？ 这里只是简单粗暴禁止了复用...
        holder.setIsRecyclable(false)
    }

    class Holder(itemView: View) : RecyclerView.ViewHolder(itemView)
}