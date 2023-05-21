package com.example.fffroject.keyword

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
import com.example.fffroject.R
import com.example.fffroject.share.ShareDetailActivity
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage

class FireBaseMessagingService : FirebaseMessagingService() {
    var auth: FirebaseAuth? = null
    var firestore: FirebaseFirestore? = null
    var user: FirebaseUser? = null


    override fun onMessageReceived(remoteMessage: RemoteMessage) {
        // 파이어베이스 인증 객체
        auth = FirebaseAuth.getInstance()
        user = auth!!.currentUser
        // 파이어스토어 인스턴스 초기화
        firestore = FirebaseFirestore.getInstance()

        super.onMessageReceived(remoteMessage)
        if (remoteMessage.data.isNotEmpty()) {
            Log.d(TAG, "Message data payload: ${remoteMessage.data}")
        }

        if (remoteMessage.notification != null){
            Log.d(TAG, "notification: ${remoteMessage.data}")
            if(remoteMessage.data["writer"] != user!!.uid ) {
            sendNotification(remoteMessage)
             }
        }else{
            Log.d(TAG, "수신 에러")

        }


    }
    override fun onNewToken(token: String) {
        Log.d("mytag", "Refreshed token: $token")
        super.onNewToken(token)
    }

    private fun sendNotification(remoteMessage: RemoteMessage) {

        val post = remoteMessage.data["postId"]
        val writer = remoteMessage.data["writer"]
        val flag = remoteMessage.data["flag"]
        var title = remoteMessage.notification!!.title
        var fridgeName = remoteMessage.notification!!.body
        Log.d("postId 받아오는지:", "${post}")
        Log.d("writer 받아오는지:", "${writer}")
        Log.d("flag 받아오는지:", "${flag}")

        val timestamp = System.currentTimeMillis() // 현재 시간의 타임스탬프 (고유한 request code를 위해)
        val requestCode = timestamp.toInt()
        val intent = Intent(this, ShareDetailActivity::class.java)
        intent.putExtra("detailIndex", post)
        intent.putExtra("detailWriter", writer)
        intent.putExtra("detailFlag", flag)

        val pendingIntent = PendingIntent.getActivity(
            this,
            requestCode,
            intent,
            PendingIntent.FLAG_IMMUTABLE,
        )
        Log.d("postId 넘어가는지:", "${post}")
        Log.d("writer 넘어가는지:", "${writer}")
        Log.d("flag 넘어가는지:", "${flag}")

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