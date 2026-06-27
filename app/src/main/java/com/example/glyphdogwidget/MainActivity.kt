package com.example.glyphdogwidget

import android.appwidget.AppWidgetManager
import android.content.ComponentName
import android.content.Intent
import android.content.IntentFilter
import android.os.BatteryManager
import android.os.Bundle
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val battery = getBatteryPercent()
        val mood = DogMood.fromBattery(battery)

        findViewById<TextView>(R.id.tv_main_dog).text = mood.dogArt
        findViewById<TextView>(R.id.tv_main_mood).text = mood.label
        findViewById<TextView>(R.id.tv_main_battery).text = "Battery: $battery%"
        findViewById<TextView>(R.id.tv_hint).text =
            "Long-press your home screen → Widgets → Glyph Dog to add the widget"

        // Trigger widget update
        val manager = AppWidgetManager.getInstance(this)
        val ids = manager.getAppWidgetIds(ComponentName(this, DogWidgetProvider::class.java))
        sendBroadcast(Intent(AppWidgetManager.ACTION_APPWIDGET_UPDATE).apply {
            putExtra(AppWidgetManager.EXTRA_APPWIDGET_IDS, ids)
        })
    }

    private fun getBatteryPercent(): Int {
        val intent = registerReceiver(null, IntentFilter(Intent.ACTION_BATTERY_CHANGED))
        val level = intent?.getIntExtra(BatteryManager.EXTRA_LEVEL, -1) ?: -1
        val scale = intent?.getIntExtra(BatteryManager.EXTRA_SCALE, -1) ?: -1
        return if (level >= 0 && scale > 0) (level * 100 / scale) else 50
    }
}
