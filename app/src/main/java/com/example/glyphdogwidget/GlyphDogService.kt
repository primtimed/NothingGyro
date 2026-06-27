package com.example.glyphdogwidget

import android.app.Service
import android.content.Intent
import android.os.Handler
import android.os.IBinder
import android.os.Looper
import com.nothing.ketchum.Common
import com.nothing.ketchum.GlyphFrame
import com.nothing.ketchum.GlyphManager

class GlyphDogService : Service() {

    companion object {
        const val EXTRA_BATTERY = "battery_percent"
    }

    private var glyphManager: GlyphManager? = null
    private val handler = Handler(Looper.getMainLooper())
    private var currentMood: DogMood = DogMood.CALM
    private var pulseOn = false
    private val pulseRunnable = object : Runnable {
        override fun run() {
            if (currentMood.glyphPulse) {
                pulse()
                handler.postDelayed(this, 400)
            }
        }
    }

    private val glyphCallback = object : GlyphManager.GlyphCallback {
        override fun onServiceConnected(manager: GlyphManager) {
            glyphManager = manager
            try {
                if (Common.is20111()) manager.register(Common.DEVICE_20111)
                if (Common.is22111()) manager.register(Common.DEVICE_22111)
                if (Common.is23111()) manager.register(Common.DEVICE_23111)
            } catch (e: Exception) {
                e.printStackTrace()
            }
            applyMood()
        }

        override fun onServiceDisconnected(e: Exception?) {
            glyphManager = null
        }
    }

    override fun onCreate() {
        super.onCreate()
        GlyphManager.getInstance(applicationContext)?.init(glyphCallback)
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        val battery = intent?.getIntExtra(EXTRA_BATTERY, 50) ?: 50
        currentMood = DogMood.fromBattery(battery)
        applyMood()
        return START_NOT_STICKY
    }

    private fun applyMood() {
        handler.removeCallbacks(pulseRunnable)
        val gm = glyphManager ?: return
        try {
            gm.openSession()
            if (currentMood.glyphPulse) {
                handler.post(pulseRunnable)
            } else {
                val frame = buildFrame(gm, currentMood.glyphBrightness)
                gm.displayProgress(frame, currentMood.glyphBrightness, false)
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    private fun pulse() {
        val gm = glyphManager ?: return
        try {
            pulseOn = !pulseOn
            val brightness = if (pulseOn) currentMood.glyphBrightness else currentMood.glyphBrightness / 4
            val frame = buildFrame(gm, brightness)
            gm.displayProgress(frame, brightness, false)
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    private fun buildFrame(gm: GlyphManager, brightness: Int): GlyphFrame {
        return GlyphFrame.Builder()
            .buildChannel(gm.getChannel(GlyphFrame.CHANNEL_A))
            .buildChannelA(brightness)
            .build()
    }

    override fun onDestroy() {
        handler.removeCallbacks(pulseRunnable)
        try {
            glyphManager?.closeSession()
            glyphManager?.unInit()
        } catch (e: Exception) {
            e.printStackTrace()
        }
        super.onDestroy()
    }

    override fun onBind(intent: Intent?): IBinder? = null
}
