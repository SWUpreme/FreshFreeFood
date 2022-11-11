package com.example.fffroject

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.InputFilter
import android.util.Log
import android.view.MenuItem
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.annotation.RequiresApi
import com.example.fffroject.fragment.FoodList
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.android.synthetic.main.activity_barcode.*
import kotlinx.android.synthetic.main.activity_write.*
import java.text.SimpleDateFormat
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.util.*
import kotlin.collections.ArrayList

class WriteActivity : AppCompatActivity() {

    var auth: FirebaseAuth? = null
    var firestore: FirebaseFirestore? = null
    var user: FirebaseUser? = null
    lateinit var food: ArrayList<food>

    lateinit var name: EditText
    lateinit var deadline_year:EditText
    lateinit var deadline_month: EditText
    lateinit var deadline_day: EditText
    lateinit var purchasedAt_year: EditText
    lateinit var purchasedAt_month: EditText
    lateinit var purchasedAt_day: EditText
    lateinit var count: EditText
    lateinit var upload_btn: Button

    lateinit var foodlist: ArrayList<FoodList>
    lateinit var foodindex: String

    var fridgeindex : String? = null
    var done = false

    @SuppressLint("MissingInflatedId")
    @RequiresApi(Build.VERSION_CODES.O)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_write)

        /*setSupportActionBar(toolbWrite)
        //Toolbar에 표시되는 제목의 표시 유무를 설정. false로 해야 custom한 툴바의 이름이 화면에 보인다.
        supportActionBar?.setDisplayShowTitleEnabled(false)
        //왼쪽 버튼 사용설정(기본은 뒤로가기)
        supportActionBar!!.setDisplayHomeAsUpEnabled(true)
        //왼쪽 버튼 아이콘 변경
        supportActionBar!!.setHomeAsUpIndicator(R.drawable.ic_back_btn)*/

        foodlist = arrayListOf<FoodList>()

        //food = arrayListOf<food>()
        auth = FirebaseAuth.getInstance()
        user = auth!!.currentUser
        firestore = FirebaseFirestore.getInstance()


        name = findViewById(R.id.name)
        deadline_year = findViewById(R.id.fdeadlineYear)
        deadline_month = findViewById(R.id.fdeadlineMonth)

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

//            val food = hashMapOf(
//                "name" to name.text.toString(),
//                "deadline" to deadline.text.toString(),
//                "purchasedAt" to purchasedAt.text.toString(),
//                "count" to count.text.toString(),
//            )

            if (checkAllWritten()) {
                var formatter = SimpleDateFormat("yyyy.MM.dd")
                var nowdate = LocalDate.now().format(DateTimeFormatter.ofPattern("yyyy.MM.dd"))
                var deadline = nowdate[0] + "." + nowdate[1] + "." + nowdate[2]

                var date = formatter.parse(deadline).time
                var day = formatter.parse(nowdate).time
                var d_day = (date - day)/ (60 * 60 * 24 * 1000)
                if (d_day.toInt() >= 0){
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
                }
                else {
                    Toast.makeText(this, "유통기한이 이미 지난 제품입니다.", Toast.LENGTH_SHORT).show()
                }
//                if (user != null) {
//
//                    var food_deadline =
//                        deadline_year.text.toString() + "." + deadline_month.text.toString() + "." + deadline_day.text.toString()
//                    var purchasedAt =
//                        purchasedAt_year.text.toString() + "." + purchasedAt_month.text.toString() + "." + purchasedAt_day.text.toString()
//                    foodindex = UUID.randomUUID().toString()
//                    firestore?.collection("fridge")?.document("$fridgeindex")
//                        ?.collection("food")?.document("$foodindex")
//                        ?.set(
//                            hashMapOf(
//                                "index" to foodindex,
//                                "name" to name.text.toString(),
//                                "deadline" to food_deadline,
//                                "purchaseAt" to purchasedAt,
//                                "count" to count.text.toString().toInt(),
//                                "done" to done
//                            )
//                        )
//
//                }
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


    //item 버튼 클릭 했을 때
    /*  override fun onOptionsItemSelected(item: MenuItem): Boolean {
          when (item?.itemId) {
              android.R.id.home -> {
                  //뒤로가기 버튼 눌렀을 때
                  Log.d("ToolBar_item: ", "뒤로가기 버튼 클릭")
                  val intent = Intent(applicationContext,FoodListActivity::class.java)
                  startActivity(intent)
                  return true
              }

              else -> return super.onOptionsItemSelected(item)
          }
      }*/


}