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
    var auth: FirebaseAuth? = null
    var firestore: FirebaseFirestore? = null
    var user: FirebaseUser? = null
    lateinit var foodlist: ArrayList<FoodList>
    lateinit var fridgeindex: String

    lateinit var notificationManager: NotificationManager
    override fun onReceive(context: Context, intent: Intent) {
        foodlist = arrayListOf<FoodList>()
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

    private fun createNotificationChannel(context: Context) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val notificationChannel = NotificationChannel(
                CHANNEL_ID, // 채널의 아이디
                CHANNEL_NAME, // 채널의 이름
                NotificationManager.IMPORTANCE_HIGH
                /*
                1. IMPORTANCE_HIGH = 알림음이 울리고 헤드업 알림으로 표시
                2. IMPORTANCE_DEFAULT = 알림음 울림
                3. IMPORTANCE_LOW = 알림음 없음
                4. IMPORTANCE_MIN = 알림음 없고 상태줄 표시 X
                 */
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

    private fun deliverNotification(context: Context) {
        val contentIntent = Intent(context, FoodListActivity::class.java)
        val contentPendingIntent = PendingIntent.getActivity(
            context,
            NOTIFICATION_ID, // requestCode
            contentIntent, // 알림 클릭 시 이동할 인텐트
            PendingIntent.FLAG_MUTABLE
            /*
            1. FLAG_UPDATE_CURRENT : 현재 PendingIntent를 유지하고, 대신 인텐트의 extra data는 새로 전달된 Intent로 교체
            2. FLAG_CANCEL_CURRENT : 현재 인텐트가 이미 등록되어있다면 삭제, 다시 등록
            3. FLAG_NO_CREATE : 이미 등록된 인텐트가 있다면, null
            4. FLAG_ONE_SHOT : 한번 사용되면, 그 다음에 다시 사용하지 않음
             */
        )

        firestore?.collection("user")?.document(user!!.uid)?.collection("myfridge")
            ?.whereEqualTo("status", true)
            ?.get()
            ?.addOnCompleteListener { task ->
                var count = 0
                var compare = "3000.12.31" //비교대상
                var mazinoname = ""
                count = task.result.size()
                if (task.result?.size() != 0) {
                    for (i in 0 until count) {

                        var doc = task.result.documents?.get(i)
                        fridgeindex = doc?.get("index").toString()
                        Log.d("성공:", "${doc.toString()}")

                        //index를 가지고 food에 접근
                        firestore?.collection("fridge")?.document("$fridgeindex")
                            ?.collection("food")
                            ?.orderBy("deadline", Query.Direction.ASCENDING)//유통기한 임박순
                            ?.get()
                            ?.addOnSuccessListener() { task ->

                                // 가장 임박한 식품 가져오기
                                var sheet = task.documents?.get(0)
                                var current = sheet.get("deadline").toString()
                                if (current < compare) {
                                    compare = current
                                    mazinoname = sheet.get("name").toString()
                                }

                                Log.d("성공:", "${compare}")
/*
                            for (i in 0 until task.size()){
                                var doc = task.documents?.get(i)
                                var dates = doc.get("deadline").toString()
                                var formatter = SimpleDateFormat("yyyy.MM.dd")
                                var nowdate = LocalDate.now().format(
                                    DateTimeFormatter.ofPattern(
                                        "yyyy.MM.dd"
                                    )
                                )
                                var date = formatter.parse(dates).time
                                var day = formatter.parse(nowdate).time
                                var d_day = (date - day)/ (60 * 60 * 24 * 1000)
                                if (d_day.toInt() < 3){*/
//                                var name = firestore?.collection("fridge")?.document("$fridgeindex")
//                                    ?.collection("food")?.document("name")?.get().toString()
                                // Log.d("성공:", "${d_day.toString()}")
                                val builder1 = NotificationCompat.Builder(context, CHANNEL_ID)
                                    .setSmallIcon(R.drawable.ic_launcher_foreground) // 아이콘
                                    .setContentTitle("FFF") // 제목

                                    .setContentText(mazinoname + "의 유통기한이 하루 남았습니다!") // 내용
                                    .setContentIntent(contentPendingIntent)
                                    .setPriority(NotificationCompat.PRIORITY_HIGH)
                                    .setAutoCancel(true)
                                    .setDefaults(NotificationCompat.DEFAULT_ALL)
                                // Log.d("성공:", "${foodlist.get(0).name.toString()}")
                                notificationManager?.notify(NOTIFICATION_ID, builder1.build())
                            }
                    }
                }
            }

    }

    companion object {
        private const val NOTIFICATION_ID = 0
        private const val CHANNEL_ID = "channel_id"
        private const val CHANNEL_NAME = "ChannelName"
    }
}