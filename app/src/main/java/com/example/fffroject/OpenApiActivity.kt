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
import kotlinx.coroutines.NonCancellable.start
import org.json.JSONObject
import java.io.BufferedReader
import java.io.InputStreamReader
import java.net.URL


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
    }


    // 네트워크를 이용할 때는 쓰레드를 사용해서 접근해야 함
    class NetworkThread() : Thread() {
        lateinit var bar:String
        override fun run() {
            //var Key = "6FV9
            //            var conn = url.openConnection()
            //            var input = conn.getInputStream()
            //            var isr = InputStreamReader(input)GXM40O"
            // API 정보를 가지고 있는 주소
            val site =
                "https ://www.consumer.go.kr/openapi/recall/contents/index.do?serviceKey=6FV9GXM40O\n" +
                        "&pageNo=1&cntPerPage=10&stdBrcd=" + bar

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
            var channel = root.getJSONObject("channel")
            var allCnt: String = channel.getString("allCnt")

            if (allCnt == "1") {
                var content = channel.getJSONArray("content") // 객체 안에 있는 content라는 이름의 리스트를 가져옴
                var obj2 = content.getJSONObject(0)
                var common = channel.getJSONObject("return")
                var code: String = common.getString("code") //결과코드

                var productNm: String = obj2.getString("productNm") //제품명
                var distbTmlmtDe: String = obj2.getString("distbTmlmtDe")  //유통기한 일자
                var stdBrcd: String = obj2.getString("stdBrcd")  //바코드
/*
                // 화면에 출력
                runOnUiThread {
                    textView.append("제품명: ${productNm}\n")
                    textView.append("유통기한: ${distbTmlmtDe}\n")
                    textView.append("유통기한: ${stdBrcd}\n")
                }
*/

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
                Toast.makeText(this, "scanned: ${result.contents} format: ${result.formatName}", Toast.LENGTH_LONG).show()
                //NetworkThread.start()
            } else {
                Toast.makeText(this, "Cancelled", Toast.LENGTH_LONG).show()
            }

        } else {
            super.onActivityResult(requestCode, resultCode, data)
        }

    }
}