package com.example.glyphdogwidget

import android.appwidget.AppWidgetManager
import android.appwidget.AppWidgetProvider
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.os.BatteryManager
import android.widget.RemoteViews

class DogWidgetProvider : AppWidgetProvider() {

    override fun onUpdate(
        context: Context,
        appWidgetManager: AppWidgetManager,
        appWidgetIds: IntArray
    ) {
        val battery = getBatteryPercent(context)
        val mood = DogMood.fromBattery(battery)
        appWidgetIds.forEach { id ->
            updateWidget(context, appWidgetManager, id, mood, battery)
        }
        // Keep the Glyph service in sync
        context.startService(Intent(context, GlyphDogService::class.java).apply {
            putExtra(GlyphDogService.EXTRA_BATTERY, battery)
        })
    }

    override fun onReceive(context: Context, intent: Intent) {
        super.onReceive(context, intent)
        if (intent.action == Intent.ACTION_BATTERY_CHANGED) {
            val manager = AppWidgetManager.getInstance(context)
            val ids = manager.getAppWidgetIds(
                android.content.ComponentName(context, DogWidgetProvider::class.java)
            )
            onUpdate(context, manager, ids)
        }
    }

    private fun updateWidget(
        context: Context,
        manager: AppWidgetManager,
        widgetId: Int,
        mood: DogMood,
        battery: Int
    ) {
        val views = RemoteViews(context.packageName, R.layout.widget_dog).apply {
            setTextViewText(R.id.tv_dog_art, mood.dogArt)
            setTextViewText(R.id.tv_mood_label, mood.label)
            setTextViewText(R.id.tv_battery, "$battery%")
        }
        manager.updateAppWidget(widgetId, views)
    }

    private fun getBatteryPercent(context: Context): Int {
        val intent = context.registerReceiver(null, IntentFilter(Intent.ACTION_BATTERY_CHANGED))
        val level = intent?.getIntExtra(BatteryManager.EXTRA_LEVEL, -1) ?: -1
        val scale = intent?.getIntExtra(BatteryManager.EXTRA_SCALE, -1) ?: -1
        return if (level >= 0 && scale > 0) (level * 100 / scale) else 50
    }
}
