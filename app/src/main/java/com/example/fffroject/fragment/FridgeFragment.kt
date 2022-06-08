package com.example.fffroject.fragment

import android.app.AlertDialog
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.fffroject.FoodListActivity
import com.example.fffroject.R
//import com.example.fffroject.databinding.FragmentFridgeBinding
//import com.example.fffroject.databinding.FragmentMypageBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.firestore.FirebaseFirestore
import java.util.*
import kotlin.collections.ArrayList

import androidx.appcompat.widget.Toolbar
import androidx.core.content.ContextCompat

class FridgeFragment : Fragment() {
    var auth: FirebaseAuth? = null
    var firestore: FirebaseFirestore? = null
    var user: FirebaseUser? = null

    //lateinit var binding: FragmentFridgeBinding

    // Data에 있는 MyFridge랑 해줘야해
    lateinit var fridgelist: ArrayList<MyFridge>


    lateinit var btn_addFridge: Button
    lateinit var edt_fridgename: EditText
    lateinit var spinner: Spinner
    lateinit var select_fridge: String
    lateinit var fridgeid: String
    lateinit var recyclerview_fridge: RecyclerView

    lateinit var toolbar_fridge: Toolbar

//    fun newInstance() : FridgeFragment {
//        return FridgeFragment()
//    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        var view = LayoutInflater.from(activity).inflate(R.layout.fragment_fridge, container, false)

        fridgelist = arrayListOf<MyFridge>()

        // 파이어베이스 인증 객체
        auth = FirebaseAuth.getInstance()
        user = auth!!.currentUser
        // 파이어스토어 인스턴스 초기화
        firestore = FirebaseFirestore.getInstance()

        // 파이어베이스에서 냉장고 값 불러오기
        loadData()

        recyclerview_fridge = view.findViewById(R.id.recyclerviewFridge)
        recyclerview_fridge.adapter = RecyclerViewAdapter()
        recyclerview_fridge.layoutManager = LinearLayoutManager(activity)

        //binding = FragmentFridgeBinding.inflate(layoutInflater)
        //btn_addFridge = view.findViewById(R.id.btnFridgeAdd)

        // 냉장고 추가
//        btn_addFridge.setOnClickListener {
//            Toast.makeText(context, "냉장고 추가 누름", Toast.LENGTH_SHORT).show()
//            addFridge()
//        }

//        toolbar = view.findViewById(R.id.btnPlus)
//        toolbar.inflateMenu(R.menu.main_top_plus)
//        toolbar.setOnMenuItemClickListener {
//            when(it.itemId) {
//                R.id.toolbMainPlus -> {
//                    startActivity(Intent(context, FoodListActivity::class.java))
//                    true
//                }
//                else  -> false
//            }
//        }

        // 툴바
        toolbar_fridge = view.findViewById(R.id.toolbShare)    // 상단바
        //toolbar_fridge.inflateMenu(R.menu.main_top_plus)        // menu xml과 상단바 연결
        toolbar_fridge.setTitle("냉장고 페이지")

        // 상단바 메뉴 클릭시
        toolbar_fridge.setOnMenuItemClickListener {
            when(it.itemId) {
                R.id.btnPlus -> {
                    Toast.makeText(context, "냉장고 추가 누름", Toast.LENGTH_SHORT).show()
                    addFridge()
                    true
                }
                else -> false
            }
        }

        return view
//        return inflater.inflate(R.layout.fragment_fridge, container, false)
    }

    // 리사이클러뷰 사용
    inner class RecyclerViewAdapter : RecyclerView.Adapter<RecyclerView.ViewHolder>() {

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
            var view =
                LayoutInflater.from(parent.context).inflate(R.layout.item_fridgelist, parent, false)
            return ViewHolder(view)
        }

        inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView)

        // view와 실제 데이터 연결결
        override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
            var viewHolder = (holder as ViewHolder).itemView
            var fridgename: TextView
            var btn_fridge_delete: Button

            fridgename = viewHolder.findViewById(R.id.textFridgeName)

            // 리사이클러뷰 아이템 정보
            fridgename.text = fridgelist!![position].name
            fridgeid = fridgelist!![position].index!!

            // 리사이클러뷰의 아이템에 버튼이 있으므로 inner class에서 냉장고 삭제를 해야 함
            btn_fridge_delete = viewHolder.findViewById(R.id.btnFridgeDelete)

            // 냉장고 삭제
            var index = fridgelist!![position].index
            btn_fridge_delete.setOnClickListener {
                if (index != null) {
                    deleteFridge(index)
                }
            }

            // 클릭이벤트(해당 냉장고로 넘어감)
            viewHolder.setOnClickListener{
                val intent = Intent(viewHolder.context, FoodListActivity::class.java)
                intent.putExtra("index", index)
                intent.putExtra("name", fridgename.text.toString())
                ContextCompat.startActivity(viewHolder.context, intent, null)
            }

        }

        override fun getItemCount(): Int {
            return fridgelist.size
        }

    }

    // 냉장고 추가
    fun addFridge() {
        val builder = AlertDialog.Builder(activity)
        val dialogView = layoutInflater.inflate(R.layout.dialog_addfridge, null)

        edt_fridgename = dialogView.findViewById(R.id.edtFridgeName)

        builder.setView(dialogView)
            .setPositiveButton("등록") { dialogInterFace, i ->
                if (edt_fridgename.text.toString() != null) {
                    if (user != null) {
                        fridgeid = UUID.randomUUID().toString()
                        firestore?.collection("fridge")?.document("$fridgeid")
                            ?.set(
                                hashMapOf(
                                    "index" to fridgeid,
                                    "name" to edt_fridgename.text.toString(),
                                    "owner" to user?.uid
                                )
                            )
                            ?.addOnSuccessListener { }
                            ?.addOnFailureListener { }
                        firestore?.collection("user")?.document(user!!.uid)?.collection("myfridge")
                            ?.document("$fridgeid")
                            ?.set(
                                hashMapOf(
                                    "index" to fridgeid,
                                    "name" to edt_fridgename.text.toString()
                                )
                            )
                            ?.addOnSuccessListener { }
                            ?.addOnFailureListener { }
                    }
                } else {
                    Toast.makeText(activity, "내용을 입력하세요.", Toast.LENGTH_SHORT).show()
                }
            }
            .setNegativeButton("취소", null)
            .show()
    }

    // 냉장고 삭제
    fun deleteFridge(index: String) {
        val builder = AlertDialog.Builder(activity)
        val dialogView = layoutInflater.inflate(R.layout.dialog_deletefridge, null)
        var findex = index

        builder.setView(dialogView)
            .setPositiveButton("확인") { dialogInterFace, i ->
                firestore?.collection("fridge")?.document(findex)
                    ?.delete()
                    ?.addOnSuccessListener { }
                    ?.addOnFailureListener { }
                firestore?.collection("user")?.document(user!!.uid)?.collection("myfridge")
                    ?.document(findex)
                    ?.delete()
                    ?.addOnSuccessListener {
                        Toast.makeText(activity, "삭제되었습니다.", Toast.LENGTH_SHORT).show()
                    }
                    ?.addOnFailureListener { }
            }
            .setNegativeButton("취소", null)
            .show()
    }

    // 파이어베이스에서 데이터 불러오는 함수
    fun loadData() {
        // 냉장고 리스트 불러오기
        if (user != null) {
            firestore?.collection("user")?.document(user!!.uid)
                ?.collection("myfridge")
                ?.addSnapshotListener { value, error ->
                    fridgelist.clear()
                    if (value != null) {
                        for (snapshot in value.documents) {
                            var item = snapshot.toObject(MyFridge::class.java)
                            if (item != null) {
                                fridgelist.add(item)
                            }
                        }
                    }
                    recyclerview_fridge.adapter?.notifyDataSetChanged()
                }
        }
    }

}