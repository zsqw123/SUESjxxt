package com.jxxt.sues

import android.content.Context
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView


class MainAdapter(private val context: Context, private val dataList: List<Item>) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return Holder(ListItem(context))
    }
    override fun getItemCount(): Int = dataList.size
    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        val view = holder.itemView as ListItem
        view.setData(dataList[position])
    }
    class Holder(itemView: View) : RecyclerView.ViewHolder(itemView)
}