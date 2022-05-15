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
import com.example.fffroject.AuthActivity
import com.example.fffroject.FFFroject
import com.example.fffroject.R
import com.example.fffroject.databinding.FragmentFridgeBinding
import com.example.fffroject.databinding.FragmentMypageBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.GoogleAuthProvider
import com.google.firebase.firestore.FirebaseFirestore
import java.util.*
import kotlin.collections.ArrayList

class FridgeFragment : Fragment() {
    var auth: FirebaseAuth? = null
    var firestore: FirebaseFirestore? = null
    var user: FirebaseUser? = null

    lateinit var binding: FragmentFridgeBinding

    // Data에 있는 MyFridge랑 해줘야해
    lateinit var fridgelist : ArrayList<MyFridge>


    lateinit var btn_addFridge: Button
    lateinit var edt_fridgename: EditText
    lateinit var spinner: Spinner
    lateinit var select_fridge: String
    lateinit var fridgeid: String
    lateinit var recyclerview_fridge : RecyclerView

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

        binding = FragmentFridgeBinding.inflate(layoutInflater)
        btn_addFridge = view.findViewById(R.id.btnFridgeAdd)

        // 냉장고 추가+
        btn_addFridge?.setOnClickListener {
            Toast.makeText(context,"냉장고 추가 누름", Toast.LENGTH_SHORT).show()
            addFridge()
        }

        return view
//        return inflater.inflate(R.layout.fragment_fridge, container, false)
    }

    // 리사이클러뷰 사용
    inner class RecyclerViewAdapter : RecyclerView.Adapter<RecyclerView.ViewHolder>(){

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
            var view = LayoutInflater.from(parent.context).inflate(R.layout.item_fridgelist, parent, false)
            return ViewHolder(view)
        }

        inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView)

        // view와 실제 데이터 연결결
       override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
            var viewHolder = (holder as ViewHolder).itemView
            var fridgename : TextView

            fridgename = viewHolder.findViewById(R.id.textFridgeName)

            // 리사이클러뷰 아이템 정보
            fridgename.text = fridgelist!![position].fridgename
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
                            ?.set(hashMapOf("fid" to fridgeid, "fridgename" to edt_fridgename.text.toString()))
                            ?.addOnSuccessListener { }
                            ?.addOnFailureListener { }
                        firestore?.collection("user")?.document(user!!.uid)?.collection("userfridge")
                            ?.document("$fridgeid")
                            ?.set(hashMapOf("fridgename" to edt_fridgename.text.toString()))
                            ?.addOnSuccessListener { }
                            ?.addOnFailureListener { }
                    }
                }
                else {
                    Toast.makeText(activity, "내용을 입력하세요.", Toast.LENGTH_SHORT).show()
                }
            }
            .setNegativeButton("취소", null)
            .show()
    }

    // 파이어베이스에서 데이터 불러오는 함수
    fun loadData(){
        // 냉장고 리스트 불러오기
        if (user != null) {
            firestore?.collection("user")?.document(user!!.uid)
                ?.collection("userfridge")
                ?.addSnapshotListener{ value, error ->
                    fridgelist.clear()
                    for (snapshot in value!!.documents){
                        var item = snapshot.toObject(MyFridge::class.java)
                        if (item != null) {
                            fridgelist.add(item)
                        }
                    }
                    recyclerview_fridge.adapter?.notifyDataSetChanged()
                }
        }
    }

}