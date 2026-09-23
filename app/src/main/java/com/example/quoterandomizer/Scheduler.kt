package com.example.quoterandomizer

import android.app.*
import android.content.*
import kotlin.random.Random

object Scheduler {
    private const val REQ = 77

    fun scheduleNext(context: Context, min: Long, max: Long) {
        if (min <= 0 || max < min) return
        Prefs.run {
            context.getSharedPreferences("quotes", 0).edit()
                .putLong("min", min).putLong("max", max).apply()
        }
        val upper = max.coerceAtLeast(min)
        val delay = Random.nextLong(min, upper + 1) * 60_000L
        val am = context.getSystemService(AlarmManager::class.java)
        val intent = PendingIntent.getBroadcast(
            context, REQ,
            Intent(context, NotificationReceiver::class.java),
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )
        am.setExactAndAllowWhileIdle(AlarmManager.RTC_WAKEUP, System.currentTimeMillis() + delay, intent)
    }

    fun cancel(context: Context) {
        val am = context.getSystemService(AlarmManager::class.java)
        val intent = PendingIntent.getBroadcast(
            context, REQ,
            Intent(context, NotificationReceiver::class.java),
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )
        am.cancel(intent)
    }
}
