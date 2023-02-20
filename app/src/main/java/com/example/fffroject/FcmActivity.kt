package com.example.fffroject

import android.app.AlarmManager
import android.app.PendingIntent
import android.app.TimePickerDialog
import android.content.Context
import android.content.DialogInterface
import android.content.Intent
import android.icu.util.Calendar
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.CompoundButton
import android.widget.TimePicker
import android.widget.Toast
import androidx.annotation.RequiresApi
import com.example.fffroject.databinding.ActivityFcmBinding
import java.lang.reflect.Array.getInt


class FcmActivity : AppCompatActivity() {


    //알람 시간 변수
    var myampm: String = ""
    var myhour: Int = -1
    var mymin: Int = -1

    // 전역 변수로 바인딩 객체 선언
    private var mBinding: ActivityFcmBinding? = null
    private var isNoticeOn: Boolean = false
    // 매번 null 체크를 할 필요 없이 편의성을 위해 바인딩 변수 재 선언
    private val binding get() = mBinding!!

    @RequiresApi(Build.VERSION_CODES.N)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_fcm)


        // 자동 생성된 뷰 바인딩 클래스에서의 inflate라는 메서드를 활용해서
        // 액티비티에서 사용할 바인딩 클래스의 인스턴스 생성
        mBinding = ActivityFcmBinding.inflate(layoutInflater)
        // getRoot 메서드로 레이아웃 내부의 최상위 위치 뷰의
        // 인스턴스를 활용하여 생성된 뷰를 액티비티에 표시 합니다.
        setContentView(binding.root)

        // 현재 알람 설정 상태 확인
        loadNoticeData()
        loadNoticeTime()

        //스위치 현재 상태 확인
        binding.RefAlarm.setOnCheckedChangeListener(object : CompoundButton.OnCheckedChangeListener{
            override fun onCheckedChanged(buttonView: CompoundButton?, isChecked: Boolean) {
                isNoticeOn = if(isChecked){
                    Toast.makeText(this@FcmActivity, "냉장고 알림이 켜졌어요!", Toast.LENGTH_SHORT).show()
                    true
                }else{
                    // on -> off
                    Toast.makeText(this@FcmActivity, "냉장고 알림이 꺼졌어요!", Toast.LENGTH_SHORT).show()
                    delAlarm()
                    false

                }
            }
        })

        //현재 시간 가져오기
        fun getTime(button: Button, context: Context) {

            val cal = Calendar.getInstance()

            // TimePicker 클릭 이벤트
            val timeSetListener =
                TimePickerDialog.OnTimeSetListener { timePicker, hour, minute ->
                    cal.set(Calendar.HOUR_OF_DAY, hour)
                    cal.set(Calendar.MINUTE, minute)

                    // 알람 시간 변수 저장
                    myhour = hour.toInt()  //시간
                    mymin = minute.toInt()  //분

                    //오후일 때
                    if(hour >= 13) {
                        myampm = "오후"
                        var timestr: Int = myhour - 12  //myhour - 12 해줘야 12~23시를 1~11시로 표현 가능
                        if (timestr in 0..9 && mymin in 0..9) {
                            binding.Alarm.text = "오후 0$timestr:0$mymin"

                            //시간이 만약 1의 자리일 경우 앞에 0을 넣어주는 함수
                        } else if (timestr in 0..9) {
                            binding.Alarm.text = "오후 0$timestr:$mymin"
                        } else if (mymin in 0..9) {
                            binding.Alarm.text = "오후 $timestr:0$mymin"
                            //만약 10~11일 경우 그냥 출력
                        } else {
                            binding.Alarm.text = "오후 $timestr:$mymin"
                        }

                        //오전일 때
                    } else {
                        myampm = "오전"
                        if (myhour == 0) {
                            if (mymin in 0..9) {
                                binding.Alarm.text = "오전 12:0$mymin"
                            } else {
                                binding.Alarm.text = "오전 12:$mymin"
                            }
                        } else if (myhour in 0..9 && mymin in 0..9) {
                            binding.Alarm.text = "오전 0$myhour:0$mymin"

                            //시간이 만약 1의 자리일 경우 앞에 0을 넣어주는 함수
                        } else if (myhour in 0..9) {
                            binding.Alarm.text = "오전 0$myhour:$mymin"
                        } else if (mymin in 0..9) {
                            binding.Alarm.text = "오전 $myhour:0$mymin"
                            //만약 10~11일 경우 그냥 출력
                        } else {
                            binding.Alarm.text = "오전 $myhour:$mymin"
                        }
                    }

                    //시간 저장
                    addAlarm(myhour.toInt(), mymin.toInt())
                    val pref = getSharedPreferences("my_pref", 0)
                    val edit = pref.edit()  //수정
                    edit.putInt("noticeHour", myhour)
                    edit.putInt("noticeMinute", mymin)
                    edit.apply()
                    Log.d("시간:", "${myhour}")



                }

            //다이얼로그 생성
            val dialog = TimePickerDialog(
                context,
                android.R.style.Theme_Holo_Light_Dialog_NoActionBar,
                timeSetListener,
                cal.get(Calendar.HOUR_OF_DAY),
                cal.get(Calendar.MINUTE),
                false
            )
            dialog.getWindow()?.setBackgroundDrawableResource(android.R.color.transparent) //
/*
            dialog.setButton(TimePickerDialog.BUTTON_POSITIVE, "확인",
                DialogInterface.OnClickListener { dialogInterface, i ->
                })
            dialog.setButton(TimePickerDialog.BUTTON_NEGATIVE, "취소",
                DialogInterface.OnClickListener { dialogInterface, i ->
                    binding.RefAlarm.isChecked = false
                    delAlarm()
                })*/
            dialog.show()
        }

        //알람 클릭 시
        binding.Alarm.setOnClickListener {
            if(isNoticeOn != false) {
                getTime(binding.Alarm, this)
            }

        }

    }

    // 알람 설정
    fun addAlarm(myhour: Int, mymin: Int){

        // 알람매니저 선언
        var alarmManager : AlarmManager = getSystemService(Context.ALARM_SERVICE) as AlarmManager
        var intent = Intent(this, AlertReceiver::class.java)


        //AlertReceiver
        var pendingIntent = PendingIntent.getBroadcast(this, 1, intent, PendingIntent.FLAG_IMMUTABLE)
        var calendar = Calendar.getInstance()

        calendar.set(java.util.Calendar.HOUR_OF_DAY, myhour)  //시간
        calendar.set(java.util.Calendar.MINUTE, mymin)  //분
        calendar.set(java.util.Calendar.SECOND, 0)  //초

        // 지나간 시간의 경우 다음날 알람으로 울리도록
        if(calendar.before(Calendar.getInstance())){
            calendar.add(Calendar.DATE, 1)  //하루 더하기
        }

        //이미 예약된 경우 새로 덮어쓰도록
        alarmManager.setExact(AlarmManager.RTC_WAKEUP, calendar.timeInMillis, pendingIntent)

    }

    // 알람 취소
    fun delAlarm(){
        // 알람 제거
        val pendingIntent = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            PendingIntent.getBroadcast(this, 1, Intent(this, AlertReceiver::class.java), PendingIntent.FLAG_IMMUTABLE)
        }else {
            PendingIntent.getBroadcast(this, 1, Intent(this, AlertReceiver::class.java), PendingIntent.FLAG_NO_CREATE)
        }
        pendingIntent?.cancel()
    }



    // 현재 알림 설정 상태 불러오기
    fun loadNoticeData(){
        val pref = getSharedPreferences("my_pref", MODE_PRIVATE)
        // 스위치 on/off
        isNoticeOn = pref.getBoolean("noticeStatus", false)
        binding.RefAlarm.isChecked = isNoticeOn
        // on/off 따른 텍스트 색상 변경
        if(isNoticeOn) {
            true

        }
        else {
            false

        }


    }
    fun loadNoticeTime() {
        val pref = getSharedPreferences("my_pref", MODE_PRIVATE)
        myhour = pref.getInt("noticeHour", myhour)  //시간
        mymin = pref.getInt("noticeMinute", mymin)  //분

        //오후일 때
        if(myhour >= 13) {
            myampm = "오후"
            var timestr: Int = myhour - 12 //myhour - 12 해줘야 12~23시를 1~11시로 표현 가능
            if (timestr in 0..9 && mymin in 0..9) {
                binding.Alarm.text = "오후 0$timestr:0$mymin"
                //시간이 만약 1의 자리일 경우 앞에 0을 넣어주는 함수
            } else if (timestr in 0..9) {
                binding.Alarm.text = "오후 0$timestr:$mymin"
            } else if (mymin in 0..9) {
                binding.Alarm.text = "오후 $timestr:0$mymin"
                //만약 10~11일 경우 그냥 출력
            } else {
                binding.Alarm.text = "오후 $timestr:$mymin"
            }

            //오전일 때
        } else {
            myampm = "오전"
            if (myhour == 0) {
                if (mymin in 0..9) {
                    binding.Alarm.text = "오전 12:0$mymin"
                } else {
                    binding.Alarm.text = "오전 12:$mymin"
                }
            } else if (myhour in 0..9 && mymin in 0..9) {
                binding.Alarm.text = "오전 0$myhour:0$mymin"
                //시간이 만약 1의 자리일 경우 앞에 0을 넣어주는 함수
            } else if (myhour in 0..9) {
                binding.Alarm.text = "오전 0$myhour:$mymin"
            } else if (mymin in 0..9) {
                binding.Alarm.text = "오전 $myhour:0$mymin"
                //만약 10~11일 경우 그냥 출력
            } else {
                binding.Alarm.text = "오전 $myhour:$mymin"
            }

        }
    }


    // 종료
    override fun onDestroy() {
        super.onDestroy()

        // 알림 상태 저장
        val pref = getSharedPreferences("my_pref", 0)
        val edit = pref.edit()
        edit.putBoolean("noticeStatus", isNoticeOn)
        //edit.putInt("noticeHour", myhour)
        //edit.putInt("noticeMinute", mymin)
        edit.apply()
    }




}