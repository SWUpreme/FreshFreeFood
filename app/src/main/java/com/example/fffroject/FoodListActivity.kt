package com.example.fffroject

import android.app.AlertDialog
import android.app.ProgressDialog.show
import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.annotation.RequiresApi
//import android.widget.Toolbar
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.fffroject.fragment.FoodList
import com.example.fffroject.fragment.FridgeFragment
import com.example.fffroject.fragment.MyFridge
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.android.synthetic.main.activity_sharepost.*
import kotlinx.android.synthetic.main.activity_write.*

import androidx.appcompat.widget.Toolbar
import com.example.fffroject.fragment.CustomDiverItemDecoration
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.Query
import com.google.firebase.firestore.ktx.toObject
import org.threeten.bp.format.DateTimeFormatter
import org.w3c.dom.Text
import java.text.SimpleDateFormat
import java.time.LocalDate
import java.time.format.DateTimeFormatter.ofPattern
import java.util.*
import kotlin.collections.ArrayList

class FoodListActivity : AppCompatActivity(), MyCustomDialogInterface {

    var auth : FirebaseAuth? = null
    var firestore : FirebaseFirestore? = null
    var user : FirebaseUser? = null

    // Data에 있는 FoodList와 연결
    lateinit var foodlist: ArrayList<FoodList>
    lateinit var recyclerview_foodlist: RecyclerView
    lateinit var toolbar_foodlist: Toolbar

    var name : String? = null
    var index : String? = null

    val TAG: String = "로그"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_foodlist)
        Log.d(TAG, "FoodListActivity - onCreate() called")


        foodlist = arrayListOf<FoodList>()

        // 파이어베이스 인증 객체
        auth = FirebaseAuth.getInstance()
        user = auth!!.currentUser
        // 파이어스토어 인스턴스 초기화
        firestore = FirebaseFirestore.getInstance()

        // intent와 연결(FridgeFragment에서 넘겨 준 것들)
        name = intent.getStringExtra("name")    // 냉장고 이름
        index = intent.getStringExtra("index")  // 냉장고 id

        // 상단 툴바 사용
        toolbar_foodlist = findViewById(R.id.toolbFoodlist)
        toolbar_foodlist.setTitle(name)

        // 상단 툴바 +버튼 클릭시
        toolbar_foodlist.setOnMenuItemClickListener {
            when(it.itemId) {
                R.id.btnPlus -> {
                    val inputDialog = MyCustomDialog(this, this)
                    inputDialog.show()
                    true
                }
                else -> false
            }
        }


        // 파이어베이스에서 식품 리스트 값 불러오기
        loadData()

        recyclerview_foodlist = findViewById(R.id.recyclerviewFoodlist)
        recyclerview_foodlist.adapter = RecyclerviewAdapter()
        recyclerview_foodlist.layoutManager = LinearLayoutManager(this)
        // 구분선 추가
        val customDecoration = CustomDiverItemDecoration(6f, 10f, resources.getColor(R.color.diver_gray))
        recyclerview_foodlist.addItemDecoration(customDecoration)
    }



//    fun onDialogBtnClicked(view: View){
//        Log.d(TAG, "FoodListActivity - onDialogBtnClicked() called")
//
//        val myCustomDialog = MyCustomDialog(this, this)
//
//        myCustomDialog.show()
//
//    }


    // 바코드 버튼 클릭
    override fun onBarcodeBtnClicked() {
        Log.d(TAG, "FoodListActivity - onBarcodeBtnClicked() called")
        val intent = Intent(applicationContext, BarCodeActivity::class.java)
        intent.putExtra("index", index)
        startActivity(intent)
    }

    // 직접 입력 버튼 클릭
    override fun onWriteBtnClicked() {
        Log.d(TAG, "FoodListActivity - onWriteBtnClicked() called")
        val intent = Intent(applicationContext, WriteActivity::class.java)
        intent.putExtra("index", index)
        startActivity(intent)
    }

    // 리사이클러뷰 사용
    inner class RecyclerviewAdapter : RecyclerView.Adapter<RecyclerView.ViewHolder>(){

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
            var view =
                LayoutInflater.from(parent.context).inflate(R.layout.item_foodlist, parent, false)
            return ViewHolder(view)
        }

        inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView)

        // view와 실제 데이터 연결
        override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
            var viewHolder = (holder as ViewHolder).itemView
            var food_name: TextView
            var food_count: TextView
            var food_deadline: TextView
            var btn_eat: Button
            var food_dday: TextView

            food_name = viewHolder.findViewById(R.id.textFoodName)
            food_count = viewHolder.findViewById(R.id.textFoodCount)
            food_deadline = viewHolder.findViewById(R.id.textFoodDeadline)
            btn_eat = viewHolder.findViewById(R.id.btnFoodlistEat)
            food_dday = viewHolder.findViewById(R.id.textDday)

            // 리사이클러뷰 아이템 정보
            food_name.text = foodlist!![position].name
            food_count.text = foodlist!![position].count.toString()
            food_deadline.text = foodlist!![position].deadline
            var food_index = foodlist!![position].index.toString()
            //food_dday.text = foodlist!![position].dday.toString()

            // 먹었음 버튼 눌렀을 경우
            btn_eat.setOnClickListener {
                eatDone(food_index)
            }

        }

        override fun getItemCount(): Int {
            return foodlist.size
        }

    }


    // 냉장고별 식품 리스트 불러오기
    fun loadData() {
        firestore?.collection("fridge")?.document(index.toString())
            ?.collection("food")
            ?.orderBy("deadline", Query.Direction.ASCENDING)
            ?.addSnapshotListener { value, error ->
                foodlist.clear()
                if (value != null) {
                    for (snapshot in value.documents) {
                        var done = firestore?.collection("fridge")?.document(index.toString())
                            ?.collection("food")?.document("done")?.get().toString().toBoolean()
                        //Toast.makeText(this, done.toString(), Toast.LENGTH_SHORT).show()
//                        firestore?.collection("fridge")?.document(index.toString())
//                            ?.collection("food")
//                            ?.whereEqualTo("done", "false")
//                            ?.get()
//                            ?.addOnSuccessListener {
//                                var item = snapshot.toObject(FoodList::class.java)
//                                if (item != null) {
//                                    foodlist.add(item)
//                                }
                        var item = snapshot.toObject(FoodList::class.java)
                        if (item != null && done == false) {
                            foodlist.add(item)
//                        }
                            }
                    }
                    recyclerview_foodlist.adapter?.notifyDataSetChanged()
                }
            }

    }

    // 식품 먹음 버튼 클릭시
    fun eatDone(foodindex: String) {
        firestore?.collection("user")?.document(user!!.uid)?.update("contribution", FieldValue.increment(1))
        //recyclerview_foodlist.adapter?.notifyDataSetChanged()
        firestore?.collection("fridge")?.document(index.toString())
            ?.collection("food")?.document(foodindex)
            ?.delete()
            ?.addOnSuccessListener { Toast.makeText(this, "음식을 다 먹었어요!", Toast.LENGTH_SHORT).show() }
            ?.addOnFailureListener { }
    }

    @RequiresApi(Build.VERSION_CODES.O)
    fun dDayCount(foodindex: String) {
        var enddate = firestore?.collection("fridge")?.document(index.toString())
            ?.collection("food")?.document("deadline")?.get().toString()
        var nowdate = LocalDate.now()
        var startDate = nowdate.format(ofPattern("yyyyMMdd"))
        var endDate = enddate.format(ofPattern("yyyyMMdd"))
        var d_day = (startDate.toInt() - endDate.toInt()) / (60 * 60 * 24 * 1000)
        firestore?.collection("fridge")?.document(index.toString())
            ?.collection("food")?.document(foodindex)?.update("dday", d_day)
        recyclerview_foodlist.adapter?.notifyDataSetChanged()

    }

//    fun eatDone(foodindex: String){
//        firestore?.collection("fridge")?.document(index.toString())
//            ?.collection("food")?.document(foodindex)?.update("done", true)
//        recyclerview_foodlist.adapter?.notifyDataSetChanged()
//    }

//    fun search(){
//        var done = firestore?.collection("fridge")?.document(index.toString())
//            ?.collection("food")?.document("done")?.get()
//    }

//    // 먹은 식품 삭제
//    fun deleteFood(index: String) {
//        var findex = index
//        firestore?.collection("fridge")?.document(index.toString())
//            ?.collection("food")?.document(findex)
//            ?.delete()
//            ?.addOnSuccessListener {
//                Toast.makeText(this, "삭제되었습니다.", Toast.LENGTH_SHORT).show()
//            }
//            ?.addOnFailureListener {  }
//    }
}