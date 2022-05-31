package com.example.fffroject

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import android.widget.Toast
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

        toolbar_foodlist = findViewById(R.id.toolbFoodlist)
        //toolbar_foodlist.setTitle(name)

        // 파이어베이스에서 식품 리스트 값 불러오기
        loadData()

        recyclerview_foodlist = findViewById(R.id.recyclerviewFoodlist)
        recyclerview_foodlist.adapter = RecyclerviewAdapter()
        recyclerview_foodlist.layoutManager = LinearLayoutManager(this)
    }



    fun onDialogBtnClicked(view: View){
        Log.d(TAG, "FoodListActivity - onDialogBtnClicked() called")

        val myCustomDialog = MyCustomDialog(this, this)

        myCustomDialog.show()

    }


    // 바코드 버튼 클릭
    override fun onBarcodeBtnClicked() {
        Log.d(TAG, "FoodListActivity - onBarcodeBtnClicked() called")
        val intent = Intent(applicationContext, BarCodeActivity::class.java)
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
            var foodname: TextView

            foodname = viewHolder.findViewById(R.id.textFoodName)

            // 리사이클러뷰 아이템 정보
            foodname.text = foodlist!![position].name
        }

        override fun getItemCount(): Int {
            return foodlist.size
        }

    }


    // 냉장고별 식품 리스트 불러오기
    fun loadData(){
        firestore?.collection("fridge")?.document(index.toString())
            ?.collection("food")
            ?.addSnapshotListener { value, error ->
                foodlist.clear()
                if (value != null) {
                    for (snapshot in value.documents) {
                        var item = snapshot.toObject(FoodList::class.java)
                        if (item != null) {
                            foodlist.add(item)
                        }
                    }
                }
                recyclerview_foodlist.adapter?.notifyDataSetChanged()
            }
    }
}
