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


class OpenApiActivity : AppCompatActivity() {
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
    private var mBinding: ActivityOpenApiBinding? = null
    // 매번 null 체크를 할 필요 없이 편의성을 위해 바인딩 변수 재 선언
    private val binding get() = mBinding!!

    var fridgeindex : String? = null
    var done = false

    val integrator = IntentIntegrator(this)  //context를 넣어줍니다

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_open_api)

        // 자동 생성된 뷰 바인딩 클래스에서의 inflate라는 메서드를 활용해서
        // 액티비티에서 사용할 바인딩 클래스의 인스턴스 생성
        mBinding = ActivityOpenApiBinding.inflate(layoutInflater)
        // getRoot 메서드로 레이아웃 내부의 최상위 위치 뷰의
        // 인스턴스를 활용하여 생성된 뷰를 액티비티에 표시 합니다.
        setContentView(binding.root)

        integrator.setBeepEnabled(false) //스캔 시 삡 소리 OFF
        integrator.setOrientationLocked(false)
        integrator.setPrompt("바코드를 읽어주세요")//QR 스캐너 하단 메세지 셋팅
        integrator.initiateScan()  //초기화

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
                //화면에 현재 날짜, 시간 정보를 나타내고자 사용
                var formatter = SimpleDateFormat("yyyy.MM.dd")
                var food_deadline = binding.fdeadlineYear.text.toString() + "." + binding.fdeadlineMonth.text.toString() + "." + binding.fdeadlineDate.text.toString()
                var deadline = formatter.parse(food_deadline).time
                var purchasedAt = binding.fpurchasedAtYear.text.toString() + "." + binding.fpurchasedAtMonth.text.toString() + "." + binding.fpurchasedAtDate.text.toString()
                var day = formatter.parse(purchasedAt).time
                var d_day = (deadline - day)/ (60 * 60 * 24 * 1000)

                //식품 시간순 정렬
                val nowTime = System.currentTimeMillis()
                val timeformatter = SimpleDateFormat("yyyy.MM.dd.hh.mm")
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
                                    "index" to foodindex,
                                    "name" to binding.name.text.toString(),
                                    "deadline" to food_deadline,
                                    "purchaseAt" to purchasedAt,
                                    "count" to binding.count.text.toString().toInt(),
                                    "done" to done,
                                    "addTime" to dateTime
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

    //다 작성했는지 확인
    private fun checkAllWritten(): Boolean{
        return (binding.name.length()>0 && binding.fdeadlineYear.length()>0 && binding.fdeadlineMonth.length()>0 && binding.fdeadlineDate.length()>0
                && binding.fpurchasedAtYear.length()>0 && binding.fpurchasedAtMonth.length()>0 && binding.fpurchasedAtDate.length()>0
                && binding.count.length()>0)


    }


    // 네트워크를 이용할 때는 쓰레드를 사용해서 접근해야 함
    inner class NetworkThread(var barcode:String) : Thread() {

        override fun run() {
            // var apiview = api_layout
            var key = "74cb78df7c2b4d38b2f7"  //사용자 키
            // API 정보를 가지고 있는 주소
            val site = "https://openapi.foodsafetykorea.go.kr/api/"+key+"/C005/json/1/5/BAR_CD="+barcode

            val url = URL(site)
            val conn = url.openConnection()  //url객체에서 openConnection() 메서드를 호출하여 연결을 생성
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

            /*다른 방식으로 짠 코드
                        // 전체가 객체로 묶여있기 때문에 객체형태로 가져옴
            val root = JSONObject(buf.toString())  //받아온 내용 객체로 가져옴
            var C005 = root.getJSONObject("C005")  //받아온 내용에서 C005객체 가져옴
            var row = C005.getJSONArray("row")  // 객체 안에 있는 row라는 이름의 리스트를 가져옴&검색 결과 리스트
            var obj2 = row.getJSONObject(0)
            //var total_count: String = C005.getString("total_count")  //검색된 총 수량
            // 화면에 출력
            runOnUiThread {
                // 페이지 수만큼 반복하여 데이터를 불러옴
                for(i in 0 until row.length()) {

                    // 쪽수 별로 데이터를 읽는다.
                    //append:문자열 추가 메서드
                    val jObject = row.getJSONObject(i)
                     var PRDLST_NM: String = jObject.getString("PRDLST_NM") //제품명
                    //  var POG_DAYCNT: String = jObject.getString("POG_DAYCNT")  //유통기한 일자
                    // var BAR_CD: String = jObject.getString("BAR_CD")  //바코드
                   // binding.name.append("${ JSON_Parse(jObject,"PRDLST_NM")}\n")
                    binding.name.setText(PRDLST_NM)
                    //textView.append("2. 캠핑장 이름: ${JSON_Parse(jObject,"POG_DAYCNT")}\n")
                    // Log.d("바코드_번호:", "${BAR_CD}")
                    //  Log.d("바코드_제품이름:", "${PRDLST_NM}")
                    //  Log.d("바코드_유통기한:", "${POG_DAYCNT}")

                    //Log.d("바코드_제품이름:", "${PRDLST_NM}")
                    //Log.d("바코드_번호:", "${BAR_CD}")
                    //Log.d("바코드_유통기한:", "${POG_DAYCNT}")
                    //  Log.d("바코드_유통기한:", "${POG_DAYCNT}")

                }

            }
        }

        // 함수를 통해 데이터를 불러온다.
        fun JSON_Parse(obj:JSONObject, data : String): String {

            // 원하는 정보를 불러와 리턴받고 없는 정보는 캐치하여 "없습니다."로 리턴받는다.
            return try {

                obj.getString(data)

            } catch (e: Exception) {
                "없습니다."
            }
        }
    }
*/


            // 전체가 객체로 묶여있기 때문에 객체형태로 가져옴
            val root = JSONObject(buf.toString())  //받아온 내용 객체로 가져옴
            var C005 = root.getJSONObject("C005")  //받아온 내용에서 C005객체 가져옴
            var total_count: String = C005.getString("total_count")  //검색된 총 수량

            //사용자 인터페이스와 관련된 모든 동작은 onCreate () 및 이벤트 처리가 실행되는 주 스레드 또는 UI 스레드에서 수행되어야 함
            // 다른 스레드에서 동작할 때 에러가 발생-> runOnUiThread를 사용
            runOnUiThread {
                if (total_count == "0") {
                    Log.d("바코드실패:","해당 상품이 없습니다.")
                    Toast.makeText(this@OpenApiActivity, "해당 상품이 없습니다.", Toast.LENGTH_LONG).show()
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

                    // 화면에 출력
                    binding.name.setText(PRDLST_NM)
                    Log.d("바코드_번호:", "${BAR_CD}")
                    Log.d("바코드_제품이름:", "${PRDLST_NM}")
                    Log.d("바코드_유통기한:", "${POG_DAYCNT}")
                }
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
                //Toast.makeText(this, "scanned: ${result.contents}", Toast.LENGTH_LONG).show()
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