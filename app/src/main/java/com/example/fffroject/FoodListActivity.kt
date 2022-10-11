package com.example.fffroject

import android.content.Intent
import android.graphics.Color
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
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
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.ItemTouchHelper.ACTION_STATE_SWIPE
import com.example.fffroject.fragment.CustomDiverItemDecoration
import com.example.fffroject.fragment.food
import com.google.common.net.InetAddresses.decrement
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.Query
import com.google.firebase.firestore.SetOptions
import com.google.firebase.firestore.ktx.toObject
import kotlinx.android.synthetic.main.item_foodlist.*
import kotlinx.android.synthetic.main.item_foodlist.view.*
import kotlinx.coroutines.flow.merge
import org.threeten.bp.format.DateTimeFormatter
import org.w3c.dom.Text
import java.lang.reflect.Array.get
import java.lang.reflect.Array.getLength
import java.text.SimpleDateFormat
import java.time.LocalDate
import java.time.format.DateTimeFormatter.ofPattern
import java.util.*
import kotlin.collections.ArrayList
import kotlin.math.max
import kotlin.math.min

class FoodListActivity : AppCompatActivity(), MyCustomDialogInterface {

    var auth : FirebaseAuth? = null
    var firestore : FirebaseFirestore? = null
    var user : FirebaseUser? = null

    // Data에 있는 FoodList와 연결
    lateinit var foodlist: ArrayList<FoodList>
    lateinit var recyclerview_foodlist: RecyclerView
    lateinit var toolbar_foodlist: Toolbar
    lateinit var toolbar_fridgename : TextView
    lateinit var spinner_foodlist : Spinner

    var name : String? = null
    var index : String? = null

    val TAG: String = "로그"

    var selectFood = -1
    var preselect = -1
    var eatfoodindex : String = ""
    var foodcount = 0

    var foodname : String? = null
    var foodlistdeadline : String? = null
    var foodlistpurchase :  String? = null

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
        toolbar_fridgename = findViewById(R.id.textFridgeID)
        toolbar_fridgename.setText(name)

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

        // Spinner 설정
        spinner_foodlist = findViewById(R.id.spinFoodHow)
        val fooditems = resources.getStringArray(R.array.foodarray)
        spinner_foodlist.adapter = ArrayAdapter.createFromResource(this, R.array.foodarray, R.layout.spinner_style)

        spinner_foodlist.onItemSelectedListener = object : AdapterView.OnItemSelectedListener{
            override fun onItemSelected(p0: AdapterView<*>?, view: View?, position: Int, id: Long) {
                // 아이템이 클릭되면 맨 위인 position 0번부터 순서대로 동작하게 됩니다.
                when(position) {
                    0 -> {
                        loadData()
                    }
                    1 -> {
                        loadDataDate()
                    }
                }
            }

            override fun onNothingSelected(p0: AdapterView<*>?) {
            }
        }



        // 파이어베이스에서 식품 리스트 값 불러오기
        //loadData()

        recyclerview_foodlist = findViewById(R.id.recyclerviewFoodlist)
        recyclerview_foodlist.adapter = RecyclerviewAdapter()
        recyclerview_foodlist.layoutManager = LinearLayoutManager(this)
        // 구분선 추가
        val customDecoration = CustomDiverItemDecoration(6f, 10f, resources.getColor(R.color.diver_gray))

        // 식품 리스트 스와이프 삭제를 위한 클래스 연결
        val swipeHelperCallback = SwipeHelperCallback().apply {
            // 스와이프한 뒤 고정시킬 위치 지정
            setClamp(resources.displayMetrics.widthPixels.toFloat() / 4)    // 1080 / 4 = 270
        }
        val itemTouchHelper = ItemTouchHelper(swipeHelperCallback)
        itemTouchHelper.attachToRecyclerView(recyclerview_foodlist)

        recyclerview_foodlist.apply {
            layoutManager = LinearLayoutManager(applicationContext)
            //adapter = swipeAdapter
            //addItemDecoration(RecyclerView.ItemDecoration())
            recyclerview_foodlist.addItemDecoration(customDecoration)

            setOnTouchListener { _, _ ->
                swipeHelperCallback.removePreviousClamp(this)
                false
            }
        }

        // 다른 곳 터치 시 기존 선택 뷰 닫기 (근데 안됨)
//        recyclerview_foodlist.setOnTouchListener { _, _ ->
//            swipeHelperCallback.removePreviousClamp(recyclerview_foodlist)
//            false
//        }

        var btn_food_eat = findViewById<Button>(R.id.btnFoodEat)
        btn_food_eat.setOnClickListener {
//            Toast.makeText(this, selectFood.toString(), Toast.LENGTH_SHORT).show()
            if (eatfoodindex!=""){
                eatDone(eatfoodindex)
                selectFood = preselect
                eatfoodindex = ""
            }
        }

        var btn_go_post = findViewById<Button>(R.id.btnGoPost)
        btn_go_post.setOnClickListener {
            if (eatfoodindex!=""){
                goPost(eatfoodindex)
                selectFood = preselect
                eatfoodindex = ""
            }
        }

    }



//    fun onDialogBtnClicked(view: View){
//        Log.d(TAG, "FoodListActivity - onDialogBtnClicked() called")
//
//        val myCustomDialog = MyCustomDialog(this, this)
//
//        myCustomDialog.show()
//
//    }

    // 음식 개수 세는 함수
    // OnCreate에서 수행시 업데이트가 제대로 되지 않는 문제로 함수를 따로 뺌(foodcount가 제대로 되지 않음)
    fun getfoodCount(){
        var text_food_count = findViewById<TextView>(R.id.textShowCount)
        text_food_count.text = "상품 " + foodcount + "개"
    }


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

    inner class FoodlistViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView)

    // 리사이클러뷰 사용
    inner class RecyclerviewAdapter : RecyclerView.Adapter<RecyclerView.ViewHolder>(){

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
            var view =
                LayoutInflater.from(parent.context).inflate(R.layout.item_foodlist, parent, false)
            return FoodlistViewHolder(view)
        }



//        inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView){
//            init {
//                itemView.setOnClickListener {
//                    Log.d("click", this.toString())
//                }
//            }
//        }

        // view와 실제 데이터 연결
        @RequiresApi(Build.VERSION_CODES.O)
        override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
            var viewHolder = (holder as FoodlistViewHolder).itemView
            var food_name: TextView
            var food_count: TextView
            var food_deadline: TextView

            var food_dday: TextView
            var food_delete: TextView
            var btn_minus: Button
            var btn_plus: Button

            food_name = viewHolder.findViewById(R.id.textFoodName)
            food_count = viewHolder.findViewById(R.id.textFoodCount)
            food_deadline = viewHolder.findViewById(R.id.textFoodDeadline)
            //btn_eat = viewHolder.findViewById(R.id.btnFoodlistEat)
//            btn_food_eat = viewHolder.findViewById(R.id.btnFoodEat)
            food_dday = viewHolder.findViewById(R.id.textDday)

            food_delete = viewHolder.findViewById(R.id.tvRemove)

            btn_minus = viewHolder.findViewById(R.id.btnFoodMinus)
            btn_plus = viewHolder.findViewById(R.id.btnFoodPlus)

            // 리사이클러뷰 아이템 정보
            food_name.text = foodlist!![position].name
            food_count.text = foodlist!![position].count.toString()
            food_deadline.text = foodlist!![position].deadline + " 까지"
            var food_index = foodlist!![position].index.toString()
            var deadline = foodlist!![position].deadline

            var formatter = SimpleDateFormat("yyyy.MM.dd")
            var nowdate = LocalDate.now().format(ofPattern("yyyy.MM.dd"))
            var date = formatter.parse(deadline).time
            var day = formatter.parse(nowdate).time
            var d_day = (date - day)/ (60 * 60 * 24 * 1000)
            if (d_day.toInt() > 0){
                food_dday.text = "D-" + d_day.toString()
                food_dday.setTextColor(Color.parseColor("#71ABFF"))
            }
            else if (d_day.toInt() == 0){
                food_dday.text = "D-Day"
                food_dday.setTextColor(Color.parseColor("#FEC10A"))
            }
            else {
                food_dday.text = "D+" + (d_day.toInt()*(-1)).toString()
                food_dday.setTextColor(Color.parseColor("#ED6C3C"))
            }

            // 삭제 텍스트뷰 클릭시 토스트 표시
            food_delete.setOnClickListener {
                Log.d(TAG, "삭제 텍스트뷰 클릭 가능함")
                foodDelete(food_index)
                notifyItemRemoved(position)
            }

            // 마이너스 버튼 클릭시
            btn_minus.setOnClickListener {
                var count = foodlist!![position].count
                countMinus(food_index, count)
            }

            // 플러스 버튼 클릭시
            btn_plus.setOnClickListener {
                var count = foodlist!![position].count
                countPlus(food_index, count)
            }


            if(selectFood == position && selectFood != preselect) {
                viewHolder.swipe_view.setBackgroundColor(ContextCompat.getColor(viewHolder.context, R.color.diver_gray))
            }
            else if (selectFood == preselect){
                viewHolder.swipe_view.setBackgroundColor(ContextCompat.getColor(viewHolder.context, R.color.white))
                eatfoodindex = ""
            }
            else{
                viewHolder.swipe_view.setBackgroundColor(ContextCompat.getColor(viewHolder.context, R.color.white))
            }

            // swipe_view click: 안됐던 이유: viewHolder.id 를 입력 하고 setOnClickListener 해야 함
            // id를 맞게 잘 설정해줘야함(Framelayout을 해서 안됐던 거였음)
            viewHolder.swipe_view.setOnClickListener {
//                Toast.makeText(viewHolder.context, "click", Toast.LENGTH_SHORT).show()
                if(selectFood == preselect) preselect = -1
                else preselect = selectFood
                selectFood = position
                eatfoodindex = foodlist!![position].index.toString()
                // 이하는 포스트 이동을 위한 intent를 위해 추가
                foodname = foodlist!![position].name
                foodlistdeadline = foodlist!![position].deadline
                foodlistpurchase = foodlist!![position].purchaseAt
                notifyDataSetChanged()
            }

        }

        override fun getItemCount(): Int {
            return foodlist.size
        }

    }


//    interface OnItemClickListener{
//        fun onClick(v: View, position: Int)
//    }
//
//    private lateinit var itemClickListener : OnItemClickListener
//
//    fun setItemClicklistener(onItemClickListener: OnItemClickListener, function: () -> Unit){
//        this.itemClickListener = onItemClickListener
//    }


    // 냉장고별 식품 리스트 불러오기 (유통기한 임박 순)
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
                        }
                        
                        // 유통기한 임박 제품 업데이트
                        // 비어있는 경우 업데이트가 안되어서 If문으로 수정
                        // 유저가 갖고 있는 냉장고 리스트에 넣어줘야 FridgeFragment에서 한번에 리사이클러뷰의 카드에 넣을 수 있음(개별 소팅으로 가져오는거 안됨)
                        firestore?.collection("user")?.document(user!!.uid)?.collection("myfridge")
                            ?.document(index.toString())
                            ?.update("current", foodlist.get(0).name.toString())
                            ?.addOnSuccessListener { }
                            ?.addOnFailureListener { }
                        // 다른 사람의 냉장고를 추가했을 경우 다른 인덱스를 fridge에서 가져와야하므로 최근 목록도 여기에 넣어줘야함(안그럼 업데이트가 안될 것으로 예상)
                        firestore?.collection("fridge")?.document(index.toString())
                            ?.update("current", foodlist.get(0).name.toString())
                            ?.addOnSuccessListener { }
                            ?.addOnFailureListener { }
                    }
                    // 음식 개수 세는 부분
                    foodcount = foodlist.size
                    getfoodCount()
                    recyclerview_foodlist.adapter?.notifyDataSetChanged()
                }
            }

    }


    // 냉장고별 식품 리스트 최신등록순
    fun loadDataDate() {
        firestore?.collection("fridge")?.document(index.toString())
            ?.collection("food")
            ?.orderBy("purchaseAt", Query.Direction.DESCENDING)
            ?.addSnapshotListener { value, error ->
                foodlist.clear()
                if (value != null) {
                    for (snapshot in value.documents) {
                        var done = firestore?.collection("fridge")?.document(index.toString())
                            ?.collection("food")?.document("done")?.get().toString().toBoolean()
                        var item = snapshot.toObject(FoodList::class.java)
                        if (item != null && done == false) {
                            foodlist.add(item)
                        }
                    }
                    // 음식 개수 세는 부분
                    foodcount = foodlist.size
                    getfoodCount()
                    recyclerview_foodlist.adapter?.notifyDataSetChanged()
                }
            }

    }




    // 식품 먹음 버튼 클릭시
    fun eatDone(foodindex: String) {
        firestore?.collection("user")?.document(user!!.uid)?.update("contribution", FieldValue.increment(1))
        //recyclerview_foodlist.adapter?.notifyDataSetChanged()
        // 비어있는 경우 업데이트가 안되어서 If문으로 수정
        // 처음에 loadData에서 시도했으나 음식이 없는 경우 foodlist가 아예 생성이 안되어 한개 있는걸 할 때로 변경
        // 따라서 deleteFood에도 적용시켜주어야 함
        if (foodcount == 1) {
            // 유저가 갖고 있는 냉장고 리스트에 넣어줘야 FridgeFragment에서 한번에 리사이클러뷰의 카드에 넣을 수 있음(개별 소팅으로 가져오는거 안됨)
            firestore?.collection("user")?.document(user!!.uid)?.collection("myfridge")
                ?.document(index.toString())
                ?.update("current", "냉장고가 비었습니다")
                ?.addOnSuccessListener { }
                ?.addOnFailureListener { }
            // 다른 사람의 냉장고를 추가했을 경우 다른 인덱스를 fridge에서 가져와야하므로 최근 목록도 여기에 넣어줘야함(안그럼 업데이트가 안될 것으로 예상)
            firestore?.collection("fridge")?.document(index.toString())
                ?.update("current", "냉장고가 비었습니다")
                ?.addOnSuccessListener { }
                ?.addOnFailureListener { }
        }
        firestore?.collection("fridge")?.document(index.toString())
            ?.collection("food")?.document(foodindex)
            ?.delete()
            ?.addOnSuccessListener { Toast.makeText(this, "환경 기여도가 상승했습니다.", Toast.LENGTH_SHORT).show() }
            ?.addOnFailureListener { }
        // envlevel과 contribution 설정 및 상호 연동
        firestore?.collection("user")?.document(user!!.uid)
            ?.get()?.addOnSuccessListener { document ->
                var contribution = document?.data?.get("contribution").toString().toInt()
                var rest = 0
                if (contribution > 50) {
                    rest = contribution % 50
                    firestore?.collection("user")?.document(user!!.uid)
                        ?.update("contribution", rest)
                        ?.addOnSuccessListener { }
                        ?.addOnFailureListener { }
                    firestore?.collection("user")?.document(user!!.uid)
                        ?.update("envlevel", FieldValue.increment(1))
                        ?.addOnSuccessListener { }
                        ?.addOnFailureListener { }
                }
                else {
                    firestore?.collection("user")?.document(user!!.uid)
                        ?.update("contribution", contribution)
                        ?.addOnSuccessListener { }
                        ?.addOnFailureListener { }
                }
            }
    }

    fun foodDelete(foodindex: String) {
        if (foodcount == 1) {
            // 유저가 갖고 있는 냉장고 리스트에 넣어줘야 FridgeFragment에서 한번에 리사이클러뷰의 카드에 넣을 수 있음(개별 소팅으로 가져오는거 안됨)
            firestore?.collection("user")?.document(user!!.uid)?.collection("myfridge")
                ?.document(index.toString())
                ?.update("current", "냉장고가 비었습니다")
                ?.addOnSuccessListener { }
                ?.addOnFailureListener { }
            // 다른 사람의 냉장고를 추가했을 경우 다른 인덱스를 fridge에서 가져와야하므로 최근 목록도 여기에 넣어줘야함(안그럼 업데이트가 안될 것으로 예상)
            firestore?.collection("fridge")?.document(index.toString())
                ?.update("current", "냉장고가 비었습니다")
                ?.addOnSuccessListener { }
                ?.addOnFailureListener { }
        }

        firestore?.collection("fridge")?.document(index.toString())
            ?.collection("food")?.document(foodindex)
            ?.delete()
            ?.addOnSuccessListener { Toast.makeText(this, "삭제되었습니다.", Toast.LENGTH_SHORT).show() }
            ?.addOnFailureListener { }
    }

    fun countMinus(foodindex: String, count: Int) {
        if (count > 1) {
            firestore?.collection("fridge")?.document(index.toString())
                ?.collection("food")?.document(foodindex)?.update("count", FieldValue.increment(-1))
        }
    }

    fun countPlus(foodindex: String, count: Int) {
        firestore?.collection("fridge")?.document(index.toString())
            ?.collection("food")?.document(foodindex)?.update("count", FieldValue.increment(1))
    }

    // 무료 나눔 포스트 작성 페이지로 데이터 전달
    fun goPost(foodindex: String){
        val intent = Intent(this, SharePostActivity::class.java)
        intent.putExtra("index", index)
        intent.putExtra("foodname", foodname)
        intent.putExtra("foodlistdeadline", foodlistdeadline)
        intent.putExtra("foodlistpurchase", foodlistpurchase)
        //Toast.makeText(this, foodlistdeadline, Toast.LENGTH_SHORT).show()
        ContextCompat.startActivity(this, intent, null)
    }



}

