package us.bojie.a_great_day.app_widget

import android.app.PendingIntent
import android.appwidget.AppWidgetManager
import android.appwidget.AppWidgetProvider
import android.content.Context
import android.content.Intent
import android.widget.RemoteViews
import us.bojie.a_great_day.R
import us.bojie.a_great_day.ui.MainActivity
import us.bojie.a_great_day.util.Util


class TimeTaskAppWidgetProvider : AppWidgetProvider() {

    override fun onUpdate(
        context: Context,
        appWidgetManager: AppWidgetManager,
        appWidgetIds: IntArray
    ) {
        // Perform this loop procedure for each App Widget that belongs to this provider
        for (element in appWidgetIds) {
            // Create an Intent to launch MainActivity
            val intent = Intent(context, MainActivity::class.java)
            val pendingIntent = PendingIntent.getActivity(context, 0, intent, 0)

            // Get the layout for the App Widget and attach an on-click listener
            // to the button
            val views = RemoteViews(context.packageName, R.layout.appwidget_time_task)
            views.setOnClickPendingIntent(R.id.tv_countdown_time, pendingIntent)
            val millis = Util.getEndOfDayInMillis()
            CountDown(millis, 1000) {
                // Tell the AppWidgetManager to perform an update on the current app widget
                views.setTextViewText(R.id.tv_countdown_time, it)
                appWidgetManager.updateAppWidget(element, views)
            }.start()
        }
    }
}