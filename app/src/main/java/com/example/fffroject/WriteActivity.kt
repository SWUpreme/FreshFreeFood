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
    var done = false


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


        auth = FirebaseAuth.getInstance()
        user = auth!!.currentUser
        firestore = FirebaseFirestore.getInstance()
/*
        name = findViewById(R.id.name)
        deadline_year = findViewById(R.id.fdeadlineYear)
        deadline_month = findViewById(R.id.fdeadlineMonth)
        api_layout = findViewById(R.id.apilayout)

        deadline_day = findViewById(R.id.fdeadlineDate)
        purchasedAt_year = findViewById(R.id.fpurchasedAtYear)
        purchasedAt_month = findViewById(R.id.fpurchasedAtMonth)

        purchasedAt_day = findViewById(R.id.fpurchasedAtDate)
        count = findViewById(R.id.count)
        upload_btn = findViewById(R.id.upload_btn)
*/
        fridgeindex = intent.getStringExtra("index")  // 냉장고 id

        //날짜 계산
        var now = LocalDate.now().toString()
        var nowdate = now.split("-")
        binding.fpurchasedAtYear.setText(nowdate[0])
        binding.fpurchasedAtMonth.setText(nowdate[1])
        binding.fpurchasedAtDate.setText(nowdate[2])

        // 데이터 추가
        binding.uploadBtn.setOnClickListener {
            if (checkAllWritten()) {
                //화면에 현재 날짜, 시간 정보를 나타내고자 사용
                var formatter = SimpleDateFormat("yyyy.MM.dd")
                var food_deadline = binding.fdeadlineYear.text.toString() + "." + binding.fdeadlineMonth.text.toString() + "." + binding.fdeadlineDate.text.toString()
                var deadline = formatter.parse(food_deadline).time
                var purchasedAt = binding.fpurchasedAtYear.text.toString() + "." + binding.fpurchasedAtMonth.text.toString() + "." + binding.fpurchasedAtDate.text.toString()
                var day = formatter.parse(purchasedAt).time
                var d_day = (deadline - day)/ (60 * 60 * 24 * 1000)

                if (d_day.toInt() >= 0){
                    if (user != null) {
                        var food_deadline =
                            binding.fdeadlineYear.text.toString() + "." + binding.fdeadlineMonth.text.toString() + "." + binding.fdeadlineDate.text.toString()
                        var purchasedAt =
                            binding.fpurchasedAtYear.text.toString() + "." + binding.fpurchasedAtMonth.text.toString() + "." + binding.fpurchasedAtDate.text.toString()
                        foodindex = UUID.randomUUID().toString()
                        firestore?.collection("fridge")?.document("$fridgeindex")
                            ?.collection("food")?.document("$foodindex")
                            ?.set(
                                hashMapOf(
                                    "index" to foodindex,
                                    "name" to binding.name.text.toString(),
                                    "deadline" to food_deadline,
                                    "purchaseAt" to purchasedAt,
                                    "count" to binding.count.text.toString().toInt(),
                                    "done" to done
                                )
                            )

                    }
                    Toast.makeText(this, "등록되었습니다.", Toast.LENGTH_SHORT).show()
                    finish()
                }
                else {
                    Toast.makeText(this, "유통기한이 이미 지난 제품입니다.", Toast.LENGTH_SHORT).show()
                }
            } else {
                Toast.makeText(this, "내용을 입력하세요.", Toast.LENGTH_SHORT).show()
            }

        }

    }

    private fun checkAllWritten(): Boolean{
        return (binding.name.length()>0 && binding.fdeadlineYear.length()>0 && binding.fdeadlineMonth.length()>0 && binding.fdeadlineDate.length()>0
                && binding.fpurchasedAtYear.length()>0 && binding.fpurchasedAtMonth.length()>0 && binding.fpurchasedAtDate.length()>0
                && binding.count.length()>0)


    }

}