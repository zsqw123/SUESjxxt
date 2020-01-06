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

    override fun getItemCount(): Int = dataList.size
    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        //获得第0周周一日期
        val view = holder.itemView as ListItem

        val weekFile = File(context.filesDir, "weekNow")
        val week0 = if (weekFile.exists()) SimpleDateFormat("yyyy-MM-dd", Locale.CHINA).parse(weekFile.readText()) ?: Date() else Date()
        val posDate = Calendar.getInstance()
        posDate.time = dataList[position].date
        val a = posDate.timeInMillis - week0.time
        val week = (a / (24 * 3600000)).toInt() / 7
        view.setData(position, dataList, week)
        /*
        怎么解决复用导致的数据错乱呢？ 这里只是简单粗暴禁止了复用...
         */
        holder.setIsRecyclable(false)
    }

    class Holder(itemView: View) : RecyclerView.ViewHolder(itemView)
}