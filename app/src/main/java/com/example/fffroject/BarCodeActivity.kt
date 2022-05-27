package com.example.fffroject

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.firestore.FirebaseFirestore
import com.google.zxing.integration.android.IntentIntegrator
import kotlinx.android.synthetic.main.activity_barcode.*
import kotlinx.coroutines.*
import kotlinx.coroutines.Dispatchers.IO
import org.jsoup.Jsoup
import org.jsoup.select.Elements
import java.util.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import kotlinx.coroutines.withTimeoutOrNull




class BarCodeActivity : AppCompatActivity() {

    var auth: FirebaseAuth? = null
    var firestore: FirebaseFirestore? = null
    var user: FirebaseUser? = null
    lateinit var food: ArrayList<food>

    lateinit var name: TextView
    lateinit var deadline: EditText
    lateinit var purchasedAt: EditText
    lateinit var count: EditText
    lateinit var upload_btn: Button
    lateinit var scan_btn: Button

    //qr code scanner object
    //private var integrator: IntentIntegrator? = null
    val TAG: String = "로그"
    val errMsg: String = "유통물류 DB에 등록되지 않은 코드입니다."
    private val tvResult: TextView? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_barcode)

        food = arrayListOf<food>()
        auth = FirebaseAuth.getInstance()
        user = auth!!.currentUser
        firestore = FirebaseFirestore.getInstance()


        name = findViewById(R.id.name)
        deadline = findViewById(R.id.deadline)
        purchasedAt = findViewById(R.id.purchasedAt)
        count = findViewById(R.id.count)
        upload_btn = findViewById(R.id.upload_btn)
        scan_btn = findViewById(R.id.scan_btn)

        //바코드 스캔버튼
        scan_btn.setOnClickListener(View.OnClickListener {
            val integrator = IntentIntegrator(this@BarCodeActivity)
            integrator.setBeepEnabled(false)
            integrator.setOrientationLocked(false)
            integrator.initiateScan()
        })

        // 데이터 추가
        upload_btn.setOnClickListener {

            val food = hashMapOf(
                "name" to name.text.toString(),
                "deadline" to deadline.text.toString(),
                "purchasedAt" to purchasedAt.text.toString(),
                "count" to count.text.toString(),
            )

            firestore!!.collection("food")
                .add(food)
                .addOnSuccessListener { documentReference ->
                    Log.d(
                        "DatabaseTest",
                        documentReference.id
                    )
                }
                .addOnFailureListener { exception -> Log.d("DatabaseTest", exception.message!!) }


            Toast.makeText(this, "저장되었습니다.", Toast.LENGTH_SHORT).show()
        }
    }

    // 상품정보 크롤링
    private fun setProductInfo(code: String) {
        Log.d(TAG, "getProductInfo: called")
        suspend fun getResultFromApi(): String {
            // do something
//            val code = txtProductName.text.toString()
            val url = "http://www.koreannet.or.kr/home/hpisSrchGtin.gs1?gtin=${code}"
            val doc = Jsoup.connect(url).timeout(1000 * 10).get()  //타임아웃 10초
            val contentData : Elements = doc.select("div.productTit")
            val productName = contentData.toString().substringAfterLast("&nbsp;").substringBefore("</div>")
            var rtnValue : String = ""
            if ( productName.toString().trim() !="" ) {
                rtnValue = productName.toString().trim()
//                IsFindProduct = true
            }
            else {
                rtnValue = errMsg //"유통물류 DB에 등록되지 않은 코드입니다."
//                IsFindProduct = false
            }
//            Log.d(TAG, "getProductInfo: called -IsFindProduct = $IsFindProduct")
            return rtnValue
        }

        CoroutineScope(IO).launch {
            val resultStr = withTimeoutOrNull(10000) {
                getResultFromApi()
            }
            if (resultStr != null) {
                withContext(Dispatchers.Main) {
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
                tvResult?.text = result.contents
                    setProductInfo(result.contents)

            } else {
                Toast.makeText(this, "Cancelled", Toast.LENGTH_LONG).show()
            }
        } else {
            super.onActivityResult(requestCode, resultCode, data)
        }

    }

}

