package com.example.fffroject

import android.content.ContentValues.TAG
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.fffroject.databinding.ActivityOpenApiBinding
import com.example.fffroject.databinding.ActivityWriteBinding
import com.example.fffroject.fragment.FoodList
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.firestore.FirebaseFirestore
import com.google.zxing.integration.android.IntentIntegrator
import org.json.JSONObject
import java.io.BufferedReader
import java.io.InputStreamReader
import java.net.URL
import java.text.SimpleDateFormat
import java.time.LocalDate
import java.util.*


class WriteActivity : AppCompatActivity() {
    //파이어스토어
    var auth: FirebaseAuth? = null
    var firestore: FirebaseFirestore? = null
    var user: FirebaseUser? = null
    lateinit var food: ArrayList<food>

    lateinit var name: EditText  //상품명
    lateinit var count: EditText  //개수
    lateinit var uploadBtn: Button  //업로드
    lateinit var deadline:EditText //유통기한
    lateinit var purchasedAt:EditText //유통기한


    lateinit var foodlist: ArrayList<FoodList>
    lateinit var foodindex: String

    // 전역 변수로 바인딩 객체 선언
    private var mBinding: ActivityWriteBinding? = null
    // 매번 null 체크를 할 필요 없이 편의성을 위해 바인딩 변수 재 선언
    private val binding get() = mBinding!!

    var fridgeindex : String? = null
    //var done = false


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_write)

        // 자동 생성된 뷰 바인딩 클래스에서의 inflate라는 메서드를 활용해서
        // 액티비티에서 사용할 바인딩 클래스의 인스턴스 생성
        mBinding = ActivityWriteBinding.inflate(layoutInflater)
        // getRoot 메서드로 레이아웃 내부의 최상위 위치 뷰의
        // 인스턴스를 활용하여 생성된 뷰를 액티비티에 표시 합니다.
        setContentView(binding.root)

        foodlist = arrayListOf<FoodList>()


        //파이어스토어
        auth = FirebaseAuth.getInstance()
        user = auth!!.currentUser
        firestore = FirebaseFirestore.getInstance()

        fridgeindex = intent.getStringExtra("index")  // 냉장고 id

        //날짜 계산
        var now = LocalDate.now().toString()
        var nowdate = now.split("-")
        binding.fpurchasedAtYear.setText(nowdate[0])  //년
        binding.fpurchasedAtMonth.setText(nowdate[1]) //월
        binding.fpurchasedAtDate.setText(nowdate[2])  //일

        // 데이터 추가
        binding.uploadBtn.setOnClickListener {
            if (checkAllWritten()) {
                if(checkYear()){
                    if(checkMonth()){
                        if(checkDate()){
                            //화면에 현재 날짜, 시간 정보를 나타내고자 사용
                            var formatter = SimpleDateFormat("yyyy.MM.dd")
                            var food_deadline = binding.fdeadlineYear.text.toString() + "." + binding.fdeadlineMonth.text.toString() + "." + binding.fdeadlineDate.text.toString()
                            var deadline = formatter.parse(food_deadline).time
                            var purchasedAt = binding.fpurchasedAtYear.text.toString() + "." + binding.fpurchasedAtMonth.text.toString() + "." + binding.fpurchasedAtDate.text.toString()
                            var day = formatter.parse(purchasedAt).time
                            var d_day = (deadline - day)/ (60 * 60 * 24 * 1000)  //(각 시간값에 따른 차이점)

                            //식품 시간순 정렬
                            val nowTime = System.currentTimeMillis()
                            val timeformatter = SimpleDateFormat("yyyy.MM.dd.hh.mm.ss")
                            val dateTime = timeformatter.format(nowTime)

                            if (d_day.toInt() >= 0){
                                if (user != null) {
                                    //유통기한 형식
                                    var food_deadline =
                                        binding.fdeadlineYear.text.toString() + "." + binding.fdeadlineMonth.text.toString() + "." + binding.fdeadlineDate.text.toString()
                                    //구매일 형식
                                    var purchasedAt =
                                        binding.fpurchasedAtYear.text.toString() + "." + binding.fpurchasedAtMonth.text.toString() + "." + binding.fpurchasedAtDate.text.toString()
                                    foodindex = UUID.randomUUID().toString()
                                    //food에 저장
                                    firestore?.collection("fridge")?.document("$fridgeindex")
                                        ?.collection("food")?.document("$foodindex")
                                        ?.set(
                                            hashMapOf(
                                                "foodId" to foodindex,
                                                "foodName" to binding.name.text.toString(),
                                                "deadline" to food_deadline,
                                                "purchaseAt" to purchasedAt,
                                                "count" to binding.count.text.toString().toInt(),
                                                "status" to "active",
                                                "createdAt" to dateTime.toString(),
                                                "updatedAt" to dateTime.toString()
                                            )
                                        )

                                }
                                Toast.makeText(this, "등록되었습니다.", Toast.LENGTH_SHORT).show()
                                val intent = Intent(this, AlertReceiver::class.java)
                                intent.putExtra("index", fridgeindex)  //AlertReceiver에 냉장고인덱스 넘겨주기
                                finish()
                            }
                            else {
                                Toast.makeText(this, "유통기한이 이미 지난 제품입니다.", Toast.LENGTH_SHORT).show()
                            }
                        } else {
                            Toast.makeText(this, "정확한 날짜를 입력하세요.", Toast.LENGTH_SHORT).show()
                        }
                    } else {
                        Toast.makeText(this, "정확한 달을 입력하세요.", Toast.LENGTH_SHORT).show()
                    }

                } else {
                    Toast.makeText(this, "정확한 년도를 입력하세요.", Toast.LENGTH_SHORT).show()
                }



            } else {
                Toast.makeText(this, "내용을 입력하세요.", Toast.LENGTH_SHORT).show()
            }

        }

    }


    //다 작성했는지 확인
    private fun checkAllWritten(): Boolean{
        return (binding.name.length()>0 && binding.fdeadlineYear.length()>0 && binding.fdeadlineMonth.length()>0 && binding.fdeadlineDate.length()>0
                && binding.fpurchasedAtYear.length()>0 && binding.fpurchasedAtMonth.length()>0 && binding.fpurchasedAtDate.length()>0
                && binding.count.length()>0)

    }

    //날짜 형식 확인
    private fun checkDate(): Boolean{
        return (Integer.parseInt(binding.fdeadlineDate.text.toString()) in 1..31
                && Integer.parseInt(binding.fpurchasedAtDate.text.toString())>0 && Integer.parseInt(binding.fpurchasedAtDate.text.toString())<=31)
    }

    //달 형식 확인
    private fun checkMonth(): Boolean{
        return (Integer.parseInt(binding.fdeadlineMonth.text.toString()) in 1..12
                && Integer.parseInt(binding.fpurchasedAtMonth.text.toString())>0 && Integer.parseInt(binding.fpurchasedAtMonth.text.toString())<=12)
    }

    //년 형식 확인
    private fun checkYear(): Boolean{
        return(Integer.parseInt(binding.fdeadlineYear.text.toString()) in 2000..2100
                && Integer.parseInt(binding.fpurchasedAtYear.text.toString())>2020 && Integer.parseInt(binding.fpurchasedAtYear.text.toString())<=2100)
    }

}