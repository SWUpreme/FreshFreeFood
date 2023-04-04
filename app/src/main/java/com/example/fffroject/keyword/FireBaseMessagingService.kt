package com.example.fffroject.keyword

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.graphics.Color
import android.media.RingtoneManager
import android.os.Build
import android.util.Log
import androidx.core.app.NotificationCompat
import com.example.fffroject.R
import com.example.fffroject.alarm.FcmActivity
import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage


class FireBaseMessagingService : FirebaseMessagingService() {
    private val TAG = "FirebaseService"

    // 토큰 생성
    override fun onMessageReceived(remoteMessage: RemoteMessage) {
        Log.d(TAG, "From: " + remoteMessage.from)

        if(remoteMessage.notification != null) {
            Log.d(TAG, "Notification Message Body: ${remoteMessage.notification?.body}")
            sendNotification(remoteMessage)

        }
    }
    override fun onNewToken(token: String) {
        Log.d(TAG, "new Token: $token")
    }

    private fun sendNotification(message: RemoteMessage) {
        val intent = Intent(this, FcmActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_CLEAR_TOP
            putExtra("Notification", message.notification?.body)    // 메시지 값 전달
        }

        val CHANNEL_ID = "KeywordNotification"
        val CHANNEL_NAME = "키워드 알림채널"
        val description = "해당하는 키워드의 게시물이 새로 올라오면 알려줍니다."
        val importance = NotificationManager.IMPORTANCE_HIGH

        var notificationManager: NotificationManager = this.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

        val channel = NotificationChannel(CHANNEL_ID, CHANNEL_NAME, importance)
        channel.description = description
        channel.enableLights(true)
        channel.lightColor = Color.RED
        channel.enableVibration(true)
        channel.setShowBadge(false)
        notificationManager.createNotificationChannel(channel)


        // 일회용 PendingIntent
        // PendingIntent : Intent 의 실행 권한을 외부의 어플리케이션에게 위임

        var pendingIntent = PendingIntent.getActivity(this, 0, intent, PendingIntent.FLAG_MUTABLE) //intent를 특정 시점에 실행시킬 때 사용
        val notificationSound = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION)


        // 알림에 대한 UI 정보와 작업을 지정
        var notificationBuilder = NotificationCompat.Builder(this,CHANNEL_ID)
            .setSmallIcon(R.mipmap.ic_launcher)
            .setContentTitle(message.notification?.title)
            .setContentText(message.notification?.body)
            .setAutoCancel(true)
            .setSound(notificationSound)
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setContentIntent(pendingIntent)


        // 알림 생성
        notificationManager.notify(0, notificationBuilder.build())
    }
}