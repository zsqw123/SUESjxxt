package com.jxxt.sues

import android.content.Context
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.floatingactionbutton.FloatingActionButton

class MainAdapter(private val context: Context, private val dataList: List<Item>) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return Holder(ListItem(context))
    }

    override fun getItemCount(): Int = dataList.size
    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        val view = holder.itemView as ListItem
        view.setData(position, dataList)
        /*
        怎么解决复用导致的数据错乱呢？ 这里只是简单粗暴禁止了复用...
         */
        holder.setIsRecyclable(false)
    }

    class Holder(itemView: View) : RecyclerView.ViewHolder(itemView)
}

//滚动隐藏FAB
class RecListener(private val fab: FloatingActionButton, private val viewList: List<View>) : RecyclerView.OnScrollListener() {
    private var distance = 0
    private var visiable = true

    override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
        super.onScrolled(recyclerView, dx, dy)
        if (distance > 10 && visiable) {
            visiable = false
            fab.hide()
            for (i in viewList) {
                i.visibility = View.INVISIBLE
            }
            distance = 0
        } else if (distance < -20 && !visiable) {
            visiable = true
            fab.show()
            for (i in viewList) {
                i.visibility = View.VISIBLE
            }
            distance = 0
        }
        if ((visiable && dy > 0) || (!visiable && dy < 0)) {
            distance += dy
        }
    }
}