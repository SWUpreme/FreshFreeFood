/*package com.example.fffroject

import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.ContextWrapper
import android.content.Intent
import android.graphics.Color
import android.os.Build
import androidx.annotation.RequiresApi
import androidx.core.app.NotificationCompat

//알림 기능
class NotificationHelper(base: Context?) : ContextWrapper(base) {

    //채널 변수 만들기
    private val channelID: String = "channelID"
    private val channelNm: String = "channelName"

    init {
        //안드로이드 버전이 오레오보다 크면
        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.O){

            //채널 생성
            createChannel()
        }
    }

    //채널 생성 함수
    @RequiresApi(Build.VERSION_CODES.O)
    private fun createChannel(){

        //객체 생성
        val channel: NotificationChannel =
            NotificationChannel(channelID, channelNm, NotificationManager.IMPORTANCE_DEFAULT)

        //설정
        channel.enableLights(true) //빛
        channel.enableVibration(true) //진동
        channel.lightColor = Color.RED
        channel.lockscreenVisibility = Notification.VISIBILITY_PRIVATE
        channel.setShowBadge(false)     // false 시 알림 채널이 알림 뱃지 안뜨게 함
        //생성
        getManager().createNotificationChannel(channel)
    }

    //NotificationManager 생성
    fun getManager(): NotificationManager {

        return getSystemService(NOTIFICATION_SERVICE) as NotificationManager
    }

    //Notification 설정
    fun getChannelNotification(): NotificationCompat.Builder{
        val intent = Intent(this, FoodListActivity::class.java)
        val pendingIntent = PendingIntent.getActivity(this, 0, intent, PendingIntent.FLAG_MUTABLE)

        return NotificationCompat.Builder(applicationContext, channelID)
            .setContentTitle("FFF") //제목
            .setContentText("냉장고야")//내용
            .setWhen(System.currentTimeMillis())
            .setSmallIcon(R.drawable.ic_launcher_background) //아이콘
            .setContentIntent(pendingIntent)
            .setAutoCancel(true)
    }
}*/