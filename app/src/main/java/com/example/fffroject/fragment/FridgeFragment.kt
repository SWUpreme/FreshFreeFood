package com.example.fffroject.fragment

import android.app.AlertDialog
import android.content.Intent
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.fffroject.FoodListActivity
import com.example.fffroject.R
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.firestore.FirebaseFirestore
import java.util.*
import kotlin.collections.ArrayList

import androidx.appcompat.widget.Toolbar
import androidx.core.content.ContextCompat
import com.example.fffroject.ExRecyclerView
import com.example.fffroject.databinding.DialogAddfridgeBinding
import com.example.fffroject.databinding.DialogDeletefridgeBinding
import com.example.fffroject.databinding.DialogFridgeoptionBinding
import com.example.fffroject.databinding.FragmentFridgeBinding
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.google.firebase.firestore.Query
import kotlinx.android.synthetic.main.activity_write.*
import kotlinx.android.synthetic.main.dialog_deletefridge.view.*
import kotlinx.android.synthetic.main.item_fridgelist.*

class FridgeFragment : Fragment() {
    var auth: FirebaseAuth? = null
    var firestore: FirebaseFirestore? = null
    var user: FirebaseUser? = null

    //lateinit var binding: FragmentFridgeBinding

    // Data에 있는 MyFridge랑 해줘야해
    lateinit var fridgelist: ArrayList<MyFridge>
    lateinit var fbinding: FragmentFridgeBinding

    lateinit var edt_fridgename: EditText
    lateinit var btn_addfridgeclose: ImageButton

//    lateinit var spinner: Spinner
//    lateinit var select_fridge: String
    lateinit var fridgeid: String
    lateinit var recyclerview_fridge: RecyclerView

    lateinit var toolbar_fridge: Toolbar

    lateinit var btn_fridgeclose: ImageButton
    lateinit var btn_fridgedel: Button

    lateinit var btn_addfridge: Button

    lateinit var text_fridge_name: TextView
    //var current = ""

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

        // 바인딩 객체 획득
        fbinding = FragmentFridgeBinding.inflate(layoutInflater)

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
        //toolbar_fridge.setTitle("냉장고 페이지")

        // 상단바 메뉴 클릭시
        toolbar_fridge.setOnMenuItemClickListener {
            when (it.itemId) {
                R.id.btnPlus -> {
                    addFridge()
                    true
                }
                else -> false
            }
        }

        return view
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
            var foodname: TextView
            var btn_add_close: Button

            fridgename = viewHolder.findViewById(R.id.textFridgeName)
            foodname = viewHolder.findViewById(R.id.textCurrentFood)

            // 리사이클러뷰 아이템 정보
            fridgename.text = fridgelist!![position].name
            fridgeid = fridgelist!![position].index!!
            foodname.text = fridgelist!![position].current


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
            viewHolder.setOnClickListener {
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

        val addfridgedial = DialogAddfridgeBinding.inflate(layoutInflater)
        val addfridgeview = addfridgedial.root
        val addfridgealertDialog = context?.let {
            androidx.appcompat.app.AlertDialog.Builder(it).run {
                setView(addfridgedial.root)
                show()
            }
        }

        addfridgealertDialog?.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        edt_fridgename = addfridgeview.findViewById(R.id.edtFridgeName)

        // 닫기 버튼
        btn_addfridgeclose = addfridgeview.findViewById(R.id.btnAddClose)
        btn_addfridgeclose.setOnClickListener {
            addfridgealertDialog?.dismiss()
        }

        // 냉장고 추가 버튼 눌렀을 시
        btn_addfridge = addfridgeview.findViewById(R.id.btnFridgeAdd)
        btn_addfridge.setOnClickListener {
            if (edt_fridgename.text.toString() != null) {
                if (user != null) {
                    fridgeid = UUID.randomUUID().toString()
                    firestore?.collection("fridge")?.document("$fridgeid")
                        ?.set(
                            hashMapOf(
                                "index" to fridgeid,
                                "name" to edt_fridgename.text.toString(),
                                "owner" to user?.uid,
                                "current" to "냉장고가 비었습니다"
                            )
                        )
                        ?.addOnSuccessListener { }
                        ?.addOnFailureListener { }
                    firestore?.collection("user")?.document(user!!.uid)?.collection("myfridge")
                        ?.document("$fridgeid")
                        ?.set(
                            hashMapOf(
                                "index" to fridgeid,
                                "name" to edt_fridgename.text.toString(),
                                "current" to "냉장고가 비었습니다"
                            )
                        )
                        ?.addOnSuccessListener { }
                        ?.addOnFailureListener { }
                }
            } else {
                Toast.makeText(activity, "내용을 입력하세요.", Toast.LENGTH_SHORT).show()
            }
            addfridgealertDialog?.dismiss()
        }

//        builder.setView(dialogView)
//            .setPositiveButton("등록") { dialogInterFace, i ->
//                if (edt_fridgename.text.toString() != null) {
//                    if (user != null) {
//                        fridgeid = UUID.randomUUID().toString()
//                        firestore?.collection("fridge")?.document("$fridgeid")
//                            ?.set(
//                                hashMapOf(
//                                    "index" to fridgeid,
//                                    "name" to edt_fridgename.text.toString(),
//                                    "owner" to user?.uid,
//                                    "current" to "냉장고가 비었습니다"
//                                )
//                            )
//                            ?.addOnSuccessListener { }
//                            ?.addOnFailureListener { }
//                        firestore?.collection("user")?.document(user!!.uid)?.collection("myfridge")
//                            ?.document("$fridgeid")
//                            ?.set(
//                                hashMapOf(
//                                    "index" to fridgeid,
//                                    "name" to edt_fridgename.text.toString(),
//                                    "current" to "냉장고가 비었습니다"
//                                )
//                            )
//                            ?.addOnSuccessListener { }
//                            ?.addOnFailureListener { }
//                    }
//                } else {
//                    Toast.makeText(activity, "내용을 입력하세요.", Toast.LENGTH_SHORT).show()
//                }
//            }
//            .setNegativeButton("취소", null)
//            .show()
    }

    // 냉장고 삭제
    fun deleteFridge(index: String) {
        //뷰 바인딩을 적용한 XML 파일 초기화
        //val fridgeoption = DialogFridgeoptionBinding.inflate(layoutInflater)
        //val fridgeopDialog = layoutInflater.inflate(R.layout.dialog_fridgeoption, null)

        val fridgedial = DialogDeletefridgeBinding.inflate(layoutInflater)
        val fridgeview = fridgedial.root
        val fridgealertDialog = context?.let {
            androidx.appcompat.app.AlertDialog.Builder(it).run {
                setView(fridgedial.root)
                show()
            }
        }//.setCanceledOnTouchOutside(true)  //외부 터치시 닫기
        //배경 투명으로 지정(모서리 둥근 배경 보이게 하기)
        fridgealertDialog?.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        //fridgealertDialog?.window?.setGravity(Gravity.BOTTOM)
        // 다이얼로그 밑으로 나오게 하는 것

        //닫기 버튼
        btn_fridgeclose = fridgeview.findViewById(R.id.btnFridgeClose)
        btn_fridgeclose.setOnClickListener(View.OnClickListener {
            fridgealertDialog?.dismiss()

        })

        btn_fridgedel = fridgeview.findViewById(R.id.btnFridgedelOk)
        btn_fridgedel.setOnClickListener {
            firestore?.collection("fridge")?.document(index)
                ?.delete()
                ?.addOnSuccessListener { }
                ?.addOnFailureListener { }
            firestore?.collection("user")?.document(user!!.uid)?.collection("myfridge")
                ?.document(index)
                ?.delete()
                ?.addOnSuccessListener {
                    Toast.makeText(activity, "삭제되었습니다.", Toast.LENGTH_SHORT).show()
                }
                ?.addOnFailureListener { }

            fridgealertDialog?.dismiss()
        }


//        val builder = AlertDialog.Builder(activity)
//        val dialogView = layoutInflater.inflate(R.layout.dialog_deletefridge, null)
//        var findex = index
//
//
//        builder.setView(dialogView)
//            .setPositiveButton("확인") { dialogInterFace, i ->
//                firestore?.collection("fridge")?.document(findex)
//                    ?.delete()
//                    ?.addOnSuccessListener { }
//                    ?.addOnFailureListener { }
//                firestore?.collection("user")?.document(user!!.uid)?.collection("myfridge")
//                    ?.document(findex)
//                    ?.delete()
//                    ?.addOnSuccessListener {
//                        Toast.makeText(activity, "삭제되었습니다.", Toast.LENGTH_SHORT).show()
//                    }
//                    ?.addOnFailureListener { }
//            }
//            .setNegativeButton("취소", null)
//            .show()
    }

    // 공유인원 추가(냉장고 ID 추가)
    fun addotherFridge() {

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