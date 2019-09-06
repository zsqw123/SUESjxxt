package com.jxxt.sues

import android.content.Context
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.floatingactionbutton.FloatingActionButton
import java.io.File
import java.text.SimpleDateFormat
import java.util.*

class MainAdapter(private val context: Context, private val dataList: List<Item>) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return Holder(ListItem(context))
    }

    override fun getItemCount(): Int = dataList.size
    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        val view = holder.itemView as ListItem

        val weekFile = File(context.filesDir, "weekNow")
        val week0 = Calendar.getInstance()
        //获得第0周周一日期
        if (weekFile.exists()) {
            val file = weekFile.readText()
            val date = SimpleDateFormat("yyyy-MM-dd", Locale.CHINA).parse(file)
            week0.time = date!!
            week0.set(Calendar.HOUR_OF_DAY, 1)
        } else {
            week0.firstDayOfWeek = Calendar.MONDAY
            week0.set(Calendar.DAY_OF_WEEK, Calendar.MONDAY)
            week0.set(Calendar.HOUR_OF_DAY, 1)
        }
        val posDate = Calendar.getInstance()
        posDate.time = dataList[position].date

        val a = posDate.timeInMillis - week0.timeInMillis
        val week = (a / (24 * 3600000)).toInt() / 7
        view.setData(position, dataList, week)
        /*
        怎么解决复用导致的数据错乱呢？ 这里只是简单粗暴禁止了复用...
         */
        holder.setIsRecyclable(false)
    }

    class Holder(itemView: View) : RecyclerView.ViewHolder(itemView)
}

//滚动隐藏FAB
class RecListener(private val fab: FloatingActionButton) : RecyclerView.OnScrollListener() {
    private var distance = 0
    private var visiable = true

    override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
        super.onScrolled(recyclerView, dx, dy)
        if (distance > 10 && visiable) {
            visiable = false
            fab.hide()
            distance = 0
        } else if (distance < -20 && !visiable) {
            visiable = true
            fab.show()
            distance = 0
        }
        if ((visiable && dy > 0) || (!visiable && dy < 0)) {
            distance += dy
        }
    }
}