package com.example.fffroject

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import androidx.core.app.NotificationCompat

//AlertReceiver class에서 알림 기능을 동작하도록
class AlertReceiver : BroadcastReceiver() {
    override fun onReceive(context: Context?, intent: Intent?) {
        var notificationHelper: NotificationHelper = NotificationHelper(context)

        var nb: NotificationCompat.Builder = notificationHelper.getChannelNotification()

        //알림 호출
        notificationHelper.getManager().notify(1, nb.build())
    }
}