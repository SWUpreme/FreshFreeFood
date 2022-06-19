package com.example.fffroject

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import com.example.fffroject.fragment.FoodList

import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.firestore.FirebaseFirestore
import com.google.zxing.integration.android.IntentIntegrator
import kotlinx.android.synthetic.main.activity_barcode.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers.IO
import kotlinx.coroutines.Dispatchers.Main
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import kotlinx.coroutines.withTimeoutOrNull
import org.jsoup.Jsoup
import org.jsoup.select.Elements
import java.util.*
import kotlin.collections.ArrayList


class BarCodeActivity : AppCompatActivity() {

    var auth: FirebaseAuth? = null
    var firestore: FirebaseFirestore? = null
    var user: FirebaseUser? = null
    var fridgeindex : String? = null

    lateinit var name: TextView
    lateinit var deadline_year:EditText
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


    //qr code scanner object
    //private var integrator: IntentIntegrator? = null
    val TAG: String = "로그"
    val errMsg: String = "등록되지 않은 코드입니다."

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_barcode)


        auth = FirebaseAuth.getInstance()
        user = auth!!.currentUser
        firestore = FirebaseFirestore.getInstance()
        foodlist = arrayListOf<FoodList>()

        name = findViewById(R.id.name)
        deadline_year = findViewById(R.id.fdeadlineYear)
        deadline_month = findViewById(R.id.fdeadlineMonth)
        deadline_day = findViewById(R.id.fdeadlineDate)
        purchasedAt_year = findViewById(R.id.fpurchasedAtYear)
        purchasedAt_month = findViewById(R.id.fpurchasedAtMonth)
        purchasedAt_day = findViewById(R.id.fpurchasedAtDate)
        count = findViewById(R.id.count)
        upload_btn = findViewById(R.id.upload_btn)
        scan_btn = findViewById(R.id.scan_btn)
        fridgeindex = intent.getStringExtra("index")  // 냉장고 id

        //바코드 스캔버튼
        scan_btn.setOnClickListener(View.OnClickListener {
            name.text = ""
            barcodode.text = ""
            val integrator = IntentIntegrator(this@BarCodeActivity)
            integrator.setBeepEnabled(false)
            integrator.setOrientationLocked(false)
            integrator.initiateScan()
        })

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


    // 상품정보 크롤링
    private fun setProductInfo(code: String) {
        Log.d(TAG, "getProductInfo: called")
        suspend fun getResultFromApi(): String {
            val url = "http://www.koreannet.or.kr/home/hpisSrchGtin.gs1?gtin=${code}"
            val doc = Jsoup.connect(url).timeout(1000 * 10).get()  //타임아웃 10초
            val contentData : Elements = doc.select("div.productTit")
            val productName = contentData.toString().substringAfterLast("&nbsp;").substringBefore("</div>")
            var rtnValue : String = ""
            if ( productName.toString().trim() !="" ) {
                rtnValue = productName.toString().trim()
            }
            else {
                rtnValue = errMsg //"유통물류 DB에 등록되지 않은 코드입니다."
            }
            return rtnValue
        }

        CoroutineScope(IO).launch {
            val resultStr = withTimeoutOrNull(10000) {
                getResultFromApi()
            }

            if (resultStr != null) {
                withContext(Main) {
                    name.text = resultStr
                }
            }
        }
    }



    // QR/바코드 스캔 결과
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        Log.d(TAG, "onActivityResult: called")
        val result = IntentIntegrator.parseActivityResult(requestCode, resultCode, data)
        if (result != null) {
            if (result.contents != null) {
                Log.d(TAG, "onActivityResult: result - ${result.contents}")
                barcodode.text = result.contents
                if (result.contents.startsWith("97")){
                } else {
                    // 상품정보 크롤링 호출
                    setProductInfo(result.contents)
                }

            } else {
                Toast.makeText(this, "Cancelled", Toast.LENGTH_LONG).show()
            }
        } else {
            super.onActivityResult(requestCode, resultCode, data)
        }

    }

    override fun onResume() {
        super.onResume()
    }

}