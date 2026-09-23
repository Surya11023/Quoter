package com.example.quoterandomizer

import android.app.*
import android.content.*
import androidx.core.app.NotificationCompat
import kotlin.random.Random

class NotificationReceiver : BroadcastReceiver() {
    override fun onReceive(context: Context, intent: Intent?) {
        val quotes = Prefs.loadQuotes(context)
        if (quotes.isNotEmpty() && Prefs.enabled(context)) {
            val quote = quotes.random()
            val channelId = "quotes"
            val nm = context.getSystemService(NotificationManager::class.java)
            if (android.os.Build.VERSION.SDK_INT >= 26) {
                nm.createNotificationChannel(
                    NotificationChannel(channelId, "Quotes", NotificationManager.IMPORTANCE_DEFAULT)
                )
            }
            val notification = NotificationCompat.Builder(context, channelId)
                .setSmallIcon(android.R.drawable.ic_dialog_info)
                .setContentTitle("Your quote")
                .setContentText(quote)
                .setStyle(NotificationCompat.BigTextStyle().bigText(quote))
                .setAutoCancel(true)
                .build()
            nm.notify(Random.nextInt(), notification)

            Scheduler.scheduleNext(
                context,
                Prefs.minMinutes(context),
                Prefs.maxMinutes(context)
            )
        }
    }
}
