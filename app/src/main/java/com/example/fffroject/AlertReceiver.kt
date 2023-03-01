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
import android.util.Log
import androidx.annotation.RequiresApi
import com.example.fffroject.foodlist.FoodListActivity
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import java.time.LocalDate
import java.time.format.DateTimeFormatter

//AlertReceiver class에서 알림 기능을 동작하도록
class AlertReceiver : BroadcastReceiver() {
    //파이어스토어
    var auth: FirebaseAuth? = null
    var firestore: FirebaseFirestore? = null
    var user: FirebaseUser? = null

    //받아오는 냉장고 인덱스, 이름
    var fridgeindex : String? = null
    var fridgename : String? = null

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

        // 내 냉장고에서 status가 true인 것만 불러오기
        firestore?.collection("user")?.document(user!!.uid)?.collection("myfridge")
            ?.whereEqualTo("status", "active")
            ?.get()
            ?.addOnCompleteListener { task ->
                var count = 0
                var compare = "3000.12.31" //비교대상
                var mazinoname = ""  //식품이름
                count = task.result.size()

                //0이 아니라면
                if (task.result?.size() != 0) {
                    for (i in 0 until count) {

                        // 해당하는 나의 냉장고에서 냉장고id 받아오기
                        var docindex = task.result.documents?.get(i)
                        fridgeindex = docindex?.get("index").toString()

                        // 해당하는 나의 냉장고에서 냉장고 이름 받아오기
                        var docname = task.result.documents?.get(i)
                        fridgename = docname?.get("name").toString()


                        //index를 가지고 food에 접근
                        firestore?.collection("fridge")?.document("$fridgeindex")
                            ?.collection("food")
                            ?.orderBy("deadline", Query.Direction.ASCENDING) //유통기한 임박순
                            ?.get()
                            ?.addOnSuccessListener() { task ->
                                if (task.size() != 0) {

                                    // 현재 날짜 가져오기
                                    var nowdate = LocalDate.now()
                                        .format(DateTimeFormatter.ofPattern("yyyy.MM.dd"))

                                    //냉장고에 유통기한 지나지 않은 음식 있을 시
                                    // 가장 임박한 식품 가져오기
                                    var sheet = task.documents?.get(0)
                                    var current = sheet.get("deadline").toString()
                                    if (current < compare) {
                                        compare = current
                                        mazinoname = sheet.get("foodName").toString()  //식품 이름 가져오기

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
                                                .setContentText(mazinoname + "의 유통기한이 임박해요! 이웃들에게 나눔을 해보세요!") // 내용
                                                .setContentIntent(contentPendingIntent)
                                                .setPriority(NotificationCompat.PRIORITY_HIGH)
                                                .setAutoCancel(true)
                                                .setDefaults(NotificationCompat.DEFAULT_ALL)
                                        notificationManager?.notify(
                                            NOTIFICATION_ID,
                                            builder1.build()
                                        )
                                    }

                                    //냉장고 음식이 모두 유통기한이 지났을 시
                                    if (current < nowdate) {
                                        val contentIntent = Intent(context, FoodListActivity::class.java)
                                        contentIntent.putExtra("index", fridgeindex)  //인덱스
                                        contentIntent.putExtra("name", fridgename)    //이름

                                        val contentPendingIntent = PendingIntent.getActivity(
                                            context,
                                            NOTIFICATION_ID, // requestCode
                                            contentIntent, // 알림 클릭 시 이동할 인텐트
                                            PendingIntent.FLAG_MUTABLE
                                        )

                                        //NotificationCompat.Builder 객체
                                        val builder2 =
                                            NotificationCompat.Builder(context, CHANNEL_ID)
                                                .setSmallIcon(R.drawable.ic_launcher_foreground) // 아이콘
                                                .setContentTitle("FFF") // 제목
                                                .setContentText("유통기한이 지난 식품이 있어요...") // 내용
                                                .setContentIntent(contentPendingIntent)
                                                .setPriority(NotificationCompat.PRIORITY_HIGH)
                                                .setAutoCancel(true)
                                                .setDefaults(NotificationCompat.DEFAULT_ALL)
                                        notificationManager?.notify(
                                            NOTIFICATION_ID,
                                            builder2.build()
                                        )
                                        Log.d("성공:", "${compare}")
                                    }
                                }

                                //냉장고에 음식 없을 시
                                if (mazinoname == "") {
                                    val contentIntent = Intent(context, FoodListActivity::class.java)
                                    contentIntent.putExtra("index", fridgeindex)  //인덱스
                                    contentIntent.putExtra("name", fridgename)    //이름

                                    val contentPendingIntent = PendingIntent.getActivity(
                                        context,
                                        NOTIFICATION_ID, // requestCode
                                        contentIntent, // 알림 클릭 시 이동할 인텐트
                                        PendingIntent.FLAG_MUTABLE
                                    )

                                    //NotificationCompat.Builder 객체
                                    val builder3 =
                                        NotificationCompat.Builder(context, CHANNEL_ID)
                                            .setSmallIcon(R.drawable.ic_launcher_foreground) // 아이콘
                                            .setContentTitle("FFF") // 제목
                                            .setContentText("냉장고를 채워주세요!") // 내용
                                            .setContentIntent(contentPendingIntent)
                                            .setPriority(NotificationCompat.PRIORITY_HIGH)
                                            .setAutoCancel(true)
                                            .setDefaults(NotificationCompat.DEFAULT_ALL)
                                    notificationManager?.notify(
                                        NOTIFICATION_ID,
                                        builder3.build()
                                    )

                                }

                            }
                    }
                }

            }


    }

    companion object {
        private const val NOTIFICATION_ID = 0
        private const val CHANNEL_ID = "channel_id"  //채널 아이디
        private const val CHANNEL_NAME = "ChannelName"  //채널 이름
    }
}