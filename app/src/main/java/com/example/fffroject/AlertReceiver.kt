package com.example.fffroject

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.os.Build
import android.graphics.Color
import androidx.core.app.NotificationCompat
import android.media.RingtoneManager
import android.util.Log
import androidx.annotation.RequiresApi
import com.example.fffroject.fragment.FoodList
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import java.text.SimpleDateFormat
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.util.*

//AlertReceiver class에서 알림 기능을 동작하도록
class AlertReceiver : BroadcastReceiver() {
    //파이어스토어
    var auth: FirebaseAuth? = null
    var firestore: FirebaseFirestore? = null
    var user: FirebaseUser? = null

    //받아오는 냉장고 인덱스, 이름
    var fridgeindex: String? = null
    var fridgename: String? = null

    lateinit var notificationManager: NotificationManager  //알람 매니저

    //onReceive: 알람 시간이 되었을 때 동작
    @RequiresApi(Build.VERSION_CODES.O)
    override fun onReceive(context: Context, intent: Intent) {

        // 파이어베이스 인증 객체
        auth = FirebaseAuth.getInstance()
        user = auth!!.currentUser
        // 파이어스토어 인스턴스 초기화
        firestore = FirebaseFirestore.getInstance()

        notificationManager =
            context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

        createNotificationChannel(context)
        deliverNotification(context)
    }

    //채널 생성
    private fun createNotificationChannel(context: Context) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val notificationChannel = NotificationChannel(
                CHANNEL_ID, // 채널의 아이디
                CHANNEL_NAME, // 채널의 이름
                NotificationManager.IMPORTANCE_HIGH
            )
            notificationChannel.enableLights(true) // 불빛
            notificationChannel.lightColor = Color.RED // 색상
            notificationChannel.enableVibration(true) // 진동 여부
            notificationChannel.description = context.getString(R.string.app_name) // 채널 정보
            notificationManager?.createNotificationChannel(
                notificationChannel
            )
        }
    }

    //노티피케이션을 등록
    @RequiresApi(Build.VERSION_CODES.O)
    private fun deliverNotification(context: Context) {


        val contentIntent = Intent(context, FoodListActivity::class.java)
        contentIntent.putExtra("index", fridgeindex) //인덱스
        contentIntent.putExtra("name", fridgename)  //이름

        val contentPendingIntent = PendingIntent.getActivity(
            context,
            NOTIFICATION_ID, // requestCode
            contentIntent, // 알림 클릭 시 이동할 인텐트
            PendingIntent.FLAG_MUTABLE
        )

        //NotificationCompat.Builder 객체
        val builder1 =
            NotificationCompat.Builder(context, CHANNEL_ID)
                .setSmallIcon(R.drawable.ic_launcher_foreground) // 아이콘
                .setContentTitle("FFF") // 제목
                .setContentText("의 유통기한이 임박해요! 이웃들에게 나눔을 해보세요!") // 내용
                .setContentIntent(contentPendingIntent)
                .setPriority(NotificationCompat.PRIORITY_HIGH)
                .setAutoCancel(true)
                .setDefaults(NotificationCompat.DEFAULT_ALL)
        notificationManager?.notify(
            NOTIFICATION_ID,
            builder1.build()
        )



    }

    companion object {
        private const val NOTIFICATION_ID = 0
        private const val CHANNEL_ID = "channel_id"  //채널 아이디
        private const val CHANNEL_NAME = "ChannelName"  //채널 이름
    }
}