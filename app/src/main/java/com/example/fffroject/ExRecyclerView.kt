package com.example.fffroject

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.fffroject.fragment.CustomDiverItemDecoration
import com.example.fffroject.fragment.FoodList
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query

class ExRecyclerView : AppCompatActivity(), MyCustomDialogInterface {

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
        setContentView(R.layout.activity_recyclerview)

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

        recyclerview_foodlist = findViewById(R.id.recyclerviewEx)
        recyclerview_foodlist.adapter = RecyclerviewAdapter()
        recyclerview_foodlist.layoutManager = LinearLayoutManager(this)
        // 구분선 추가
        val customDecoration = CustomDiverItemDecoration(6f, 10f, resources.getColor(R.color.diver_gray))
        recyclerview_foodlist.addItemDecoration(customDecoration)



        // 식품 리스트 스와이프 삭제를 위한 클래스 연결
        val swipeHelperCallback = SwipeHelperCallback().apply {
            // 스와이프한 뒤 고정시킬 위치 지정
            setClamp(resources.displayMetrics.widthPixels.toFloat() / 4)    // 1080 / 4 = 270
        }
        val itemTouchHelper = ItemTouchHelper(swipeHelperCallback)
        itemTouchHelper.attachToRecyclerView(recyclerview_foodlist)
    }

    inner class RecyclerviewAdapter : RecyclerView.Adapter<RecyclerView.ViewHolder>() {
        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
            var view =
                LayoutInflater.from(parent.context).inflate(R.layout.item_recycler, parent, false)
            return ViewHolder(view)
        }

        inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView)

        override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
            var viewHolder = (holder as ExRecyclerView.RecyclerviewAdapter.ViewHolder).itemView
            var food_name: TextView
            var food_count: TextView
            var food_deadline: TextView
            var btn_eat: Button
            var food_dday: TextView
            var food_delete: TextView

            var food_index = foodlist!![position].foodId.toString()

            food_name = viewHolder.findViewById(R.id.exFoodName)

            food_delete = viewHolder.findViewById(R.id.foodRemove)

            // 리사이클러뷰 아이템 정보
            food_name.text = foodlist!![position].foodName

            // 삭제 텍스트뷰 클릭시 토스트 표시
            food_delete.setOnClickListener {
                Log.d(TAG, "삭제 텍스트뷰 클릭 가능함")
                foodDelete(food_index)
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

    fun foodDelete(foodindex: String) {
        firestore?.collection("fridge")?.document(index.toString())
            ?.collection("food")?.document(foodindex)
            ?.delete()
            ?.addOnSuccessListener { Toast.makeText(this, "삭제되었습니다.", Toast.LENGTH_SHORT).show() }
            ?.addOnFailureListener { }
    }

    override fun onBarcodeBtnClicked() {
        TODO("Not yet implemented")
    }

    override fun onWriteBtnClicked() {
        TODO("Not yet implemented")
    }

}