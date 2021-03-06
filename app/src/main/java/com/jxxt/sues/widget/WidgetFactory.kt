package com.jxxt.sues.widget

import android.content.Context
import android.graphics.Color
import android.widget.RemoteViews
import android.widget.RemoteViewsService
import com.jxxt.sues.Item
import com.jxxt.sues.R
import com.jxxt.sues.Show
import java.io.File
import java.text.SimpleDateFormat
import java.util.*

class WidgetFactory(private val mContext: Context) : RemoteViewsService.RemoteViewsFactory {

    private lateinit var data: MutableList<Item>
    //初始化ListView的数据
    override fun onCreate() {
        //定义Flies目录
        val filesDir = mContext.filesDir
        val file = File(filesDir, "/classJs")
        //loading...
        if (file.exists()) {
            val text = file.readText()
            data = Show().textShow(text).toMutableList()//Item类型list
            //找到今日日程
            val now = Date()
            var a = 0
            for (i in data.indices) {
                val date = data[i - a].date
                if (now >= date) {
                    data.removeAt(i - a)
                    a++
                }
            }
        }
    }

    override fun getViewAt(p0: Int): RemoteViews {
        // 获取 item 对应的RemoteViews
        val rvItem = RemoteViews(mContext.packageName, R.layout.widget_list_item)
        // 设置 第position位的“视图”的数据
        try {
            val date = SimpleDateFormat("MM/dd\nHH:mm", Locale.CHINA).format(data[p0].date)
            val name = data[p0].name
            rvItem.setTextViewText(R.id.widget_date, date)
            rvItem.setTextViewText(R.id.widget_name, name)
        } catch (e: Exception) {
            rvItem.setTextViewText(R.id.widget_date, "NaN")
            rvItem.setTextViewText(R.id.widget_name, "暂无课程")
        }
        if (p0 == 0) {
            rvItem.setTextColor(R.id.widget_name, Color.parseColor("#BA9063"))
        } else {
            rvItem.setTextColor(R.id.widget_name, Color.parseColor("#FFFFFF"))
        }
        return rvItem
    }

    override fun getCount() = 15 // 返回“集合视图”中的数据的总数
    override fun getViewTypeCount() = 1 // 只有一类 ListView
    override fun onDestroy() {}
    override fun getLoadingView() = null
    override fun getItemId(p0: Int) = p0.toLong() // 返回当前项在“集合视图”中的位置
    override fun onDataSetChanged() {}
    override fun hasStableIds() = true
}