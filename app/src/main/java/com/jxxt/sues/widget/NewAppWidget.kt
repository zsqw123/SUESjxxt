package com.jxxt.sues.widget

import android.app.PendingIntent
import android.appwidget.AppWidgetManager
import android.appwidget.AppWidgetProvider
import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.os.Handler
import android.view.View
import android.widget.RemoteViews
import android.widget.Toast
import com.jxxt.sues.R

class NewAppWidget : AppWidgetProvider() {
    private val runnable = Runnable {
        hideLoading(Utils.getContext())
        Toast.makeText(Utils.getContext(), "刷新成功", Toast.LENGTH_SHORT).show()
    }

    //加载loading
    private fun showLoading(context: Context) {
        val remoteViews = RemoteViews(context.packageName, R.layout.new_app_widget)
        remoteViews.setViewVisibility(R.id.tv_refresh, View.VISIBLE)
        remoteViews.setViewVisibility(R.id.progress_bar, View.VISIBLE)
        remoteViews.setTextViewText(R.id.tv_refresh, "正在刷新...")
        refreshWidget(context, remoteViews, false)
    }

    //隐藏loading
    private fun hideLoading(context: Context) {
        val remoteViews = RemoteViews(context.packageName, R.layout.new_app_widget)
        remoteViews.setViewVisibility(R.id.progress_bar, View.GONE)
        remoteViews.setTextViewText(R.id.tv_refresh, "刷新")
        refreshWidget(context, remoteViews, false)
    }

    //刷新Widget
    private fun refreshWidget(context: Context, remoteViews: RemoteViews, refreshList: Boolean) {
        val appWidgetManager = AppWidgetManager.getInstance(context)
        val componentName = ComponentName(context, NewAppWidget::class.java)
        appWidgetManager.updateAppWidget(componentName, remoteViews)
        if (refreshList)
            appWidgetManager.notifyAppWidgetViewDataChanged(appWidgetManager.getAppWidgetIds(componentName), R.id.lv_widget)
    }

    //广播发生器
    override fun onUpdate(context: Context, appWidgetManager: AppWidgetManager, appWidgetIds: IntArray) {
        // There may be multiple widgets active, so update all of them
        for (appWidgetId in appWidgetIds) {
            updateAppWidget(context, appWidgetManager, appWidgetId)
        }
        super.onUpdate(context, appWidgetManager, appWidgetIds)
    }

    //广播接收器
    override fun onReceive(context: Context, intent: Intent) {
        val action = intent.action
        if (action == REFRESH_WIDGET) {
            //表面工作
            Toast.makeText(context, "刷新...", Toast.LENGTH_SHORT).show()
            val mgr = AppWidgetManager.getInstance(context)
            val cn = ComponentName(context, NewAppWidget::class.java)
            mgr.notifyAppWidgetViewDataChanged(mgr.getAppWidgetIds(cn), R.id.lv_widget)
            Handler().postDelayed(runnable, 1000)
            showLoading(context)
        }
        super.onReceive(context, intent)
    }

    companion object {
        private const val REFRESH_WIDGET = "com.jxxt.sues.REFRESH_WIDGET"
        internal fun updateAppWidget(context: Context, appWidgetManager: AppWidgetManager, appWidgetId: Int) {
            // 获取AppWidget对应的视图
            val views = RemoteViews(context.packageName, R.layout.new_app_widget)
            // 设置响应 “tv_refresh” 的intent
            val tvIntent = Intent().setAction(REFRESH_WIDGET)
            val tvPendingIntent = PendingIntent.getBroadcast(context, 0, tvIntent, PendingIntent.FLAG_UPDATE_CURRENT)
            views.setOnClickPendingIntent(R.id.tv_refresh, tvPendingIntent)

            //adapter
            val serviceIntent = Intent(context, Service::class.java)
            views.setRemoteAdapter(R.id.lv_widget, serviceIntent)
            // Instruct the widget manager to update the widget
            appWidgetManager.updateAppWidget(appWidgetId, views)
        }
    }
}

