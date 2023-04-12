package com.example.fffroject.keyword

import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Intent
import android.media.RingtoneManager
import android.os.Build
import android.util.Log
import androidx.core.app.NotificationCompat
import com.example.fffroject.R
import com.example.fffroject.fragment.KeyWord
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage

class FireBaseMessagingService : FirebaseMessagingService() {
    var auth: FirebaseAuth? = null
    var firestore: FirebaseFirestore? = null
    var user: FirebaseUser? = null
    var fridgeindex : String? = null

    override fun onMessageReceived(remoteMessage: RemoteMessage) {

        // 파이어베이스 인증 객체
        auth = FirebaseAuth.getInstance()
        user = auth!!.currentUser
        // 파이어스토어 인스턴스 초기화
        firestore = FirebaseFirestore.getInstance()


        // 내 냉장고에서 status가 true인 것만 불러오기
            firestore?.collection("user")?.document(user!!.uid)?.collection("mykeyword")
                ?.whereEqualTo("status", "active")
                ?.get()
                ?.addOnCompleteListener { task ->
                    var count = 0
                    count = task.result.size()

                    if (task.result?.size() != 0) {
                        val strings = ArrayList<KeyWord>()
                        for (sheet: Int in 0 until count) {

                            // 해당하는 나의 냉장고에서 냉장고id 받아오기
                            var docindex = task.result.documents?.get(sheet)
                            fridgeindex = docindex?.get("keyword").toString()

                            when (task.result?.size()) {
                                1 -> if (remoteMessage.data["listTitle"]?.contains(fridgeindex!!.get(0))!!) {
                                    sendNotification(
                                        remoteMessage.data["listTitle"]
                                    )
                                } else Log.d(
                                    TAG,
                                    "onMessageReceived: " + "넘김"
                                )
                                2 -> if (remoteMessage.data["listTitle"]?.contains(fridgeindex!!.get(0))!!
                                    || remoteMessage.data["listTitle"]?.contains(fridgeindex!!.get(1))!!
                                ) {
                                    sendNotification(
                                        remoteMessage.data["listTitle"]
                                    )
                                } else Log.d(
                                    TAG,
                                    "onMessageReceived: " + "넘김"
                                )
                                3 -> if (remoteMessage.data["listTitle"]?.contains(fridgeindex!!.get(0))!!
                                    || remoteMessage.data["listTitle"]?.contains(fridgeindex!!.get(1))!!
                                    || remoteMessage.data["listTitle"]?.contains(fridgeindex!!.get(2))!!
                                ) {
                                    sendNotification(
                                        remoteMessage.data["listTitle"]
                                    )
                                } else Log.d(
                                    TAG,
                                    "onMessageReceived: " + "넘김"
                                )
                                4 -> if (remoteMessage.data["listTitle"]?.contains(fridgeindex!!.get(0))!!
                                    || remoteMessage.data["listTitle"]?.contains(fridgeindex!!.get(1))!!
                                    || remoteMessage.data["listTitle"]?.contains(fridgeindex!!.get(2))!!
                                    || remoteMessage.data["listTitle"]?.contains(fridgeindex!!.get(3))!!
                                ) {
                                    sendNotification(
                                        remoteMessage.data["listTitle"]
                                    )
                                } else Log.d(
                                    TAG,
                                    "onMessageReceived: " + "넘김"
                                )
                                5 -> if (remoteMessage.data["listTitle"]?.contains(fridgeindex!!.get(0))!!
                                    || remoteMessage.data["listTitle"]?.contains(fridgeindex!!.get(1))!!
                                    || remoteMessage.data["listTitle"]?.contains(fridgeindex!!.get(2))!!
                                    || remoteMessage.data["listTitle"]?.contains(fridgeindex!!.get(3))!!
                                    || remoteMessage.data["listTitle"]?.contains(fridgeindex!!.get(4))!!
                                ) {
                                    sendNotification(
                                        remoteMessage.data["listTitle"]
                                    )
                                } else Log.d(
                                    TAG,
                                    "onMessageReceived: " + "넘김"
                                )
                            }


                        }
                    }


        if (remoteMessage.data.size > 0) {
            Log.d(TAG, "onMessageReceived: Data: " + remoteMessage.data.toString())
        }
    }

    }

    override fun onDeletedMessages() {
        super.onDeletedMessages()
        Log.d(TAG, "onDeletedMessages: called")
    }

    override fun onNewToken(s: String) {
        super.onNewToken(s)
        Log.d(TAG, "onNewToken: called")
    }

    private fun sendNotification(messageBody: String?) {
        // RequestCode, Id를 고유값으로 지정하여 알림이 개별 표시되도록 함
        val uniId: Int = (System.currentTimeMillis() / 1000).toInt()

        val intent = Intent(this, KeywordActivity::class.java)
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
        intent.putExtra("listTitle", messageBody) // get(content) -> 2020 년 .. 안내 툴바 서브타이틀

        Log.d(
            TAG,
            """
                sendNotification: intent 내용 : 
                서브툴바제목 : ${intent.getStringExtra("title")}
                """.trimIndent()
        )

        // 일회용 PendingIntent
        // PendingIntent : Intent 의 실행 권한을 외부의 어플리케이션에게 위임한다.
        val pendingIntent = PendingIntent.getActivity(
            this, uniId, intent,
            PendingIntent.FLAG_ONE_SHOT
        )

        // 알림 채널 이름
        val channelId =  "KeywordNotification"

        // 알림 소리
        val defaultSoundUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION)
        val notificationBuilder: NotificationCompat.Builder =
            NotificationCompat.Builder(this, channelId)
                .setSmallIcon(R.mipmap.ic_launcher)
                .setContentTitle("ㅈ목")
                .setContentText(messageBody)
                .setAutoCancel(true)
                .setDefaults(Notification.DEFAULT_ALL) // 알림, 사운드 진동 설정
                .setPriority(NotificationCompat.PRIORITY_HIGH)
                .setSound(defaultSoundUri) // 알림 소리
                .setContentIntent(pendingIntent)

        val notificationManager = getSystemService(NOTIFICATION_SERVICE) as NotificationManager

        // Since android Oreo notification channel is needed.
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(channelId, "Notice", NotificationManager.IMPORTANCE_HIGH)
            channel.importance = NotificationManager.IMPORTANCE_HIGH
            channel.enableLights(true)
            channel.enableVibration(true)
            notificationManager.createNotificationChannel(channel)
            notificationManager.notify(uniId, notificationBuilder.build())
        }
    }



    companion object {
        private const val TAG = "MyFirebaseMsgService"
    }
}