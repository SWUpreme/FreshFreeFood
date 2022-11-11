package com.example.fffroject

import android.content.ContentValues.TAG
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.constraintlayout.widget.ConstraintSet.Layout
import androidx.databinding.adapters.TextViewBindingAdapter.setText
import com.example.fffroject.fragment.FoodList
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.firestore.FirebaseFirestore
import com.google.zxing.integration.android.IntentIntegrator
import kotlinx.android.synthetic.main.activity_barcode.*
import kotlinx.android.synthetic.main.activity_open_api.*
import org.json.JSONObject
import java.io.BufferedReader
import java.io.InputStreamReader
import java.net.URL
import java.text.SimpleDateFormat
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.util.*


class OpenApiActivity : AppCompatActivity() {
    //파이어스토어
    var auth: FirebaseAuth? = null
    var firestore: FirebaseFirestore? = null
    var user: FirebaseUser? = null
    lateinit var food: ArrayList<food>

    lateinit var name: EditText  //상품명
    lateinit var deadline_year:EditText  //유통기한_년
    lateinit var deadline_month: EditText  //유통기한_월
    lateinit var deadline_day: EditText  //유통기한_일
    lateinit var purchasedAt_year: EditText  //구매일_년
    lateinit var purchasedAt_month: EditText  //구매일_월
    lateinit var purchasedAt_day: EditText  //구매일_일
    lateinit var count: EditText  //개수
    lateinit var upload_btn: Button  //업로드
    lateinit var api_layout: View

    lateinit var foodlist: ArrayList<FoodList>
    lateinit var foodindex: String

    var fridgeindex : String? = null
    var done = false

    val integrator = IntentIntegrator(this)  //context를 넣어줍니다

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_open_api)

        integrator.setBeepEnabled(false) //스캔 시 삡 소리 OFF
        integrator.setOrientationLocked(false)
        integrator.setPrompt("바코드를 읽어주세요")//QR 스캐너 하단 메세지 셋팅
        integrator.initiateScan()  //초기화

        foodlist = arrayListOf<FoodList>()


        auth = FirebaseAuth.getInstance()
        user = auth!!.currentUser
        firestore = FirebaseFirestore.getInstance()

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

        fridgeindex = intent.getStringExtra("index")  // 냉장고 id

        //날짜 계산
        var now = LocalDate.now().toString()
        var nowdate = now.split("-")
        purchasedAt_year.setText(nowdate[0])
        purchasedAt_month.setText(nowdate[1])
        purchasedAt_day.setText(nowdate[2])

        // 데이터 추가
        upload_btn.setOnClickListener {
            if (checkAllWritten()) {
                var formatter = SimpleDateFormat("yyyy.MM.dd")
                var nowdate = LocalDate.now().format(DateTimeFormatter.ofPattern("yyyy.MM.dd"))
                var deadline = nowdate[0] + "." + nowdate[1] + "." + nowdate[2]

                //var date = formatter.parse(deadline).time
                //var day = formatter.parse(nowdate).time

                if (user != null) {
                    var food_deadline =
                        deadline_year.text.toString() + "." + deadline_month.text.toString() + "." + deadline_day.text.toString()
                    var purchasedAt =
                        purchasedAt_year.text.toString() + "." + purchasedAt_month.text.toString() + "." + purchasedAt_day.text.toString()
                    foodindex = UUID.randomUUID().toString()
                    firestore?.collection("fridge")?.document("$fridgeindex")
                        ?.collection("food")?.document("$foodindex")
                        ?.set(
                            hashMapOf(
                                "index" to foodindex,
                                "name" to name.text.toString(),
                                "deadline" to food_deadline,
                                "purchaseAt" to purchasedAt,
                                "count" to count.text.toString().toInt(),
                                "done" to done
                            )
                        )

                }
            } else {
                Toast.makeText(this, "내용을 입력하세요.", Toast.LENGTH_SHORT).show()
            }

        }

    }

    private fun checkAllWritten(): Boolean{
        return (name.length()>0 && deadline_year.length()>0 && deadline_month.length()>0 && deadline_day.length()>0
                && purchasedAt_year.length()>0 && purchasedAt_month.length()>0 && purchasedAt_day.length()>0
                && count.length()>0)


    }




    // 네트워크를 이용할 때는 쓰레드를 사용해서 접근해야 함
    inner class NetworkThread(var bar:String) : Thread() {

        override fun run() {
            var apiview = api_layout
            var key = "74cb78df7c2b4d38b2f7"
            // API 정보를 가지고 있는 주소
            val site = "https://openapi.foodsafetykorea.go.kr/api/"+key+"/C005/json/1/5/BAR_CD="+bar

            val url = URL(site)
            val conn = url.openConnection()
            val input = conn.getInputStream()
            val isr = InputStreamReader(input)
            // br: 라인 단위로 데이터를 읽어오기 위해서 만듦
            val br = BufferedReader(isr)

            // Json 문서는 일단 문자열로 데이터를 모두 읽어온 후, Json에 관련된 객체를 만들어서 데이터를 가져옴
            var str: String? = null
            val buf = StringBuffer()

            do {
                str = br.readLine()

                if (str != null) {
                    buf.append(str)
                }
            } while (str != null)

            // 전체가 객체로 묶여있기 때문에 객체형태로 가져옴
            val root = JSONObject(buf.toString())  //받아온 내용 객체로 가져옴
            var C005 = root.getJSONObject("C005")  //받아온 내용에서 C005객체 가져옴
            var total_count: String = C005.getString("total_count")  //검색된 총 수량

            if (total_count == "0") {
                Log.d("바코드실패:","해당 상품이 없습니다.")
            }
            else {


                Log.d("스레드로 넘어옴:", "${total_count}")
                var row = C005.getJSONArray("row") // 객체 안에 있는 row라는 이름의 리스트를 가져옴&검색 결과 리스트
                var obj2 = row.getJSONObject(0)
                var result = C005.getJSONObject("RESULT") //결과값
                var code: String = result.getString("CODE") //결과코드

                var PRDLST_NM = obj2.getString("PRDLST_NM")!! //제품명
                var POG_DAYCNT: String = obj2.getString("POG_DAYCNT")  //유통기한 일자
                var BAR_CD: String = obj2.getString("BAR_CD")  //바코드

                //name.setText(PRDLST_NM)

                // 화면에 출력
                Log.d("바코드_번호:", "${BAR_CD}")
                Log.d("바코드_제품이름:", "${PRDLST_NM}")
                Log.d("바코드_유통기한:", "${POG_DAYCNT}")
            }
        }
    }


    // QR/바코드 스캔 결과
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {

        Log.d(TAG, "onActivityResult: called")
        // QR 코드를 찍은 결과를 변수에 담는다.
        val result = IntentIntegrator.parseActivityResult(requestCode, resultCode, data)
        //결과가 있으면
        if (result != null) {
            //QRCode Scan 성공&컨텐츠가 있으면
            if (result.contents != null) {
                // 쓰레드 생성
                Toast.makeText(this, "scanned: ${result.contents}", Toast.LENGTH_LONG).show()
                var thread = NetworkThread(result.contents.toString())
                thread.start()
                thread.join()
            } else {
                Toast.makeText(this, "Cancelled", Toast.LENGTH_LONG).show()
            }
        }
        else {
            super.onActivityResult(requestCode, resultCode, data)
        }
    }



}
