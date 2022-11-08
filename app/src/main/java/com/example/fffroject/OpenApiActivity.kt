package com.example.fffroject

import android.content.ContentValues.TAG
import android.content.Intent
import android.os.Bundle
import android.text.InputFilter
import android.util.Log
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.fffroject.fragment.FoodList
import com.google.common.collect.ComparisonChain.start
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.firestore.FirebaseFirestore
import com.google.zxing.integration.android.IntentIntegrator
import kotlinx.android.synthetic.main.activity_barcode.*
import org.json.JSONObject
import java.io.BufferedReader
import java.io.InputStreamReader
import java.net.URL
import java.util.*
import kotlin.collections.ArrayList



class OpenApiActivity : AppCompatActivity() {

    var auth: FirebaseAuth? = null
    var firestore: FirebaseFirestore? = null
    var user: FirebaseUser? = null
    var fridgeindex : String? = null

    lateinit var name: TextView
    lateinit var deadline_year: EditText
    lateinit var deadline_month: EditText
    lateinit var deadline_day: EditText
    lateinit var purchasedAt_year: EditText
    lateinit var purchasedAt_month: EditText
    lateinit var purchasedAt_day: EditText
    lateinit var count: EditText
    lateinit var upload_btn: Button
    lateinit var scan_btn: Button

    lateinit var foodlist: ArrayList<FoodList>
    lateinit var foodindex: String
    var done = false

    //private var qrScan: IntentIntegrator? = null
    val integrator = IntentIntegrator(this)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_open_api)
        name.text = ""
        barcodode.text = ""
        integrator.setBeepEnabled(false)
        integrator.setOrientationLocked(false)
        integrator.initiateScan()

        auth = FirebaseAuth.getInstance()
        user = auth!!.currentUser
        firestore = FirebaseFirestore.getInstance()
        foodlist = arrayListOf<FoodList>()



        name = findViewById(R.id.name)
        deadline_year = findViewById(R.id.fdeadlineYear)
        deadline_month = findViewById(R.id.fdeadlineMonth)
        deadline_month.setFilters(arrayOf<InputFilter>(InputFilterMinMax("1", "12")))
        deadline_day = findViewById(R.id.fdeadlineDate)
        purchasedAt_year = findViewById(R.id.fpurchasedAtYear)
        purchasedAt_month = findViewById(R.id.fpurchasedAtMonth)
        purchasedAt_month.setFilters(arrayOf<InputFilter>(InputFilterMinMax("1", "12")))
        purchasedAt_day = findViewById(R.id.fpurchasedAtDate)
        count = findViewById(R.id.count)
        upload_btn = findViewById(R.id.upload_btn)
        scan_btn = findViewById(R.id.scan_btn)
        fridgeindex = intent.getStringExtra("index")  // 냉장고 id

        // 데이터 추가
        upload_btn.setOnClickListener {

            if (user != null) {
                var food_deadline = deadline_year.text.toString()+"."+deadline_month.text.toString()+"."+deadline_day.text.toString()
                var purchasedAt = purchasedAt_year.text.toString()+"."+ purchasedAt_month.text.toString()+"."+ purchasedAt_day.text.toString()
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
                    ?.addOnSuccessListener { finish() }
                    ?.addOnFailureListener {  }
            }


            Toast.makeText(this, "저장되었습니다.", Toast.LENGTH_SHORT).show()
        }


    }


    // 네트워크를 이용할 때는 쓰레드를 사용해서 접근해야 함
    class NetworkThread(var bar:String) : Thread() {

        override fun run() {
            var key = "74cb78df7c2b4d38b2f7"
            // API 정보를 가지고 있는 주소
            val site =
                "https://openapi.foodsafetykorea.go.kr/api/"+key+"/C005/json/1/2/BAR_CD=" +bar

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
            val root = JSONObject(buf.toString())
            var C005 = root.getJSONObject("C005")
            var total_count: String = C005.getString("total_count")

            if (total_count == "1") {
                Log.d("스레드로 넘어옴:","${total_count}")
                var row = C005.getJSONArray("row") // 객체 안에 있는 content라는 이름의 리스트를 가져옴
                var obj2 = row.getJSONObject(0)
                var result = C005.getJSONObject("RESULT")
                var code: String = result.getString("CODE") //결과코드

                var PRDLST_NM: String = obj2.getString("PRDLST_NM") //제품명
                var POG_DAYCNT: String = obj2.getString("POG_DAYCNT")  //유통기한 일자
                var BAR_CD: String = obj2.getString("BAR_CD")  //바코드

                //Toast.makeText(this@NetworkThread, "바코드_번호: ${BAR_CD} 바코드_제품이름: ${PRDLST_NM} 바코드_유통기한: ${POG_DAYCNT}", Toast.LENGTH_LONG).show()


                // 화면에 출력

                Log.d("바코드_번호:","${BAR_CD}")
                Log.d("바코드_제품이름:","${PRDLST_NM}")
                Log.d("바코드_유통기한:","${POG_DAYCNT}")





            }
            else{
                Log.d("바코드실패:","다시 시도해주세요")
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
                var thread = NetworkThread(result.contents.toString())
                thread.start()
                thread.join()
            }
            else {
            super.onActivityResult(requestCode, resultCode, data)
        }
    }



    }
}