package com.example.fffroject.keyword

import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.ContentValues.TAG
import android.content.Context
import android.content.Intent
import android.media.RingtoneManager
import android.os.Build
import android.util.Log
import androidx.core.app.NotificationCompat
import com.example.fffroject.MainActivity
import com.example.fffroject.R
import com.example.fffroject.fragment.KeyWord
import com.example.fffroject.fragment.ShareFragment
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage
import java.time.LocalDate
import java.time.format.DateTimeFormatter

class FireBaseMessagingService : FirebaseMessagingService() {
    var auth: FirebaseAuth? = null
    var firestore: FirebaseFirestore? = null
    var user: FirebaseUser? = null


    override fun onMessageReceived(remoteMessage: RemoteMessage) {
        super.onMessageReceived(remoteMessage)

        if (remoteMessage.notification != null){

            sendNotification(remoteMessage)
        }else{
            Log.d(TAG, "수신 에러")

    }


    }
    override fun onNewToken(token: String) {
        Log.d("mytag", "Refreshed token: $token")
        super.onNewToken(token)
    }

    private fun sendNotification(remoteMessage: RemoteMessage) {

        var title = remoteMessage.notification!!.title
        var fridgeName = remoteMessage.notification!!.body

        val requestCode = 0
        val intent = Intent(this, KeywordActivity::class.java)
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
        val pendingIntent = PendingIntent.getActivity(
            this,
            requestCode,
            intent,
            PendingIntent.FLAG_IMMUTABLE,
        )


        val channelId = "my_channel"
        val defaultSoundUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION)
        val notificationBuilder = NotificationCompat.Builder(this, channelId)
            .setSmallIcon(R.mipmap.ic_launcher)
            .setContentTitle(title)
            .setContentText(fridgeName)
            .setAutoCancel(true)
            .setSound(defaultSoundUri)
            .setContentIntent(pendingIntent)

        val notificationManager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

        // Since android Oreo notification channel is needed.
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(channelId,
                "Channel human readable title",
                NotificationManager.IMPORTANCE_DEFAULT)
            notificationManager.createNotificationChannel(channel)
        }

        notificationManager.notify(0 /* ID of notification */, notificationBuilder.build())
    }

}