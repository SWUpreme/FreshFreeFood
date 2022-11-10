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
import androidx.fragment.app.findFragment
import com.example.fffroject.databinding.*

class FridgeFragment : Fragment() {
    var auth: FirebaseAuth? = null
    var firestore: FirebaseFirestore? = null
    var user: FirebaseUser? = null

    // Data에 있는 MyFridge랑 해줘야해
    lateinit var fridgelist: ArrayList<MyFridge>
    lateinit var fbinding: FragmentFridgeBinding

    lateinit var edt_fridgename: EditText
    lateinit var btn_addfridgeclose: ImageButton

    lateinit var fridgeid: String
    lateinit var recyclerview_fridge: RecyclerView

    lateinit var toolbar_fridge: Toolbar

    lateinit var btn_fridgeclose: ImageButton
    lateinit var btn_fridgedel: Button

    lateinit var btn_addfridge: Button

    lateinit var text_fridge_name: TextView

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
            var foodname: TextView
            var btn_option: Button

            fridgename = viewHolder.findViewById(R.id.textFridgeName)
            foodname = viewHolder.findViewById(R.id.textCurrentFood)

            // 리사이클러뷰 아이템 정보
            fridgename.text = fridgelist!![position].name
            fridgeid = fridgelist!![position].index!!
            foodname.text = fridgelist!![position].current

            // 리사이클러뷰의 아이템에 버튼이 있으므로 inner class에서 냉장고 삭제를 해야 함
            btn_option = viewHolder.findViewById(R.id.btnFridgeOption)

            // 냉장고별 옵션 선택
            var index = fridgelist!![position].index
            var fname = fridgelist!![position].name.toString()
            btn_option.setOnClickListener {
                if (index != null) {
                    fridgeOption(index, fname)
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
    }

    // 냉장고별 옵션 선택
    fun fridgeOption(index: String, fname: String) {
        //뷰 바인딩을 적용한 XML 파일 초기화
        val optiondial = DialogFridgeoptionBinding.inflate(layoutInflater)
        val optionview = optiondial.root
        val optionalertDialog = context?.let {
            androidx.appcompat.app.AlertDialog.Builder(it).run {
                setView(optiondial.root)
                show()
            }
        }//.setCanceledOnTouchOutside(true)  //외부 터치시 닫기
        //배경 투명으로 지정(모서리 둥근 배경 보이게 하기)
        optionalertDialog?.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        // 다이얼로그 밑으로 나오게
        optionalertDialog?.window?.setGravity(Gravity.BOTTOM)

        // 냉장고 이름 변경 선택시
        // user의 myfridge에서 이름 바꿔줘야함
        // fridge에서 이름 바꿔줘야함
        var btn_fridgename_fix = optionview.findViewById<Button>(R.id.btnFridgeNameFix)
        btn_fridgename_fix.setOnClickListener {
            fixnameFridge(index, fname)
            optionalertDialog?.dismiss()
        }

        // 냉장고 삭제 버튼 선택시
        var btn_delete_fridge = optionview.findViewById<Button>(R.id.btnFridgeDelete)
        btn_delete_fridge.setOnClickListener {
            deleteFridge(index, fname)
            optionalertDialog?.dismiss()
        }

        // 취소 버튼 선택시
        var btn_option_close = optionview.findViewById<Button>(R.id.btnFridgeOptionClose)
        btn_option_close.setOnClickListener {
            optionalertDialog?.dismiss()
        }

    }

    // 냉장고 이름 변경
    fun fixnameFridge(index: String, fname: String) {
        //뷰 바인딩을 적용한 XML 파일 초기화
        val fixfridgedial = DialogFixfridgeBinding.inflate(layoutInflater)
        val fixfridgeview = fixfridgedial.root
        val fixfridgeDialog = context?.let {
            androidx.appcompat.app.AlertDialog.Builder(it).run {
                setView(fixfridgedial.root)
                show()
            }
        }//.setCanceledOnTouchOutside(true)  //외부 터치시 닫기
        //배경 투명으로 지정(모서리 둥근 배경 보이게 하기)
        fixfridgeDialog?.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))

        // 다이얼로그의 냉장고 이름 연동해주기
        var fridgename = fixfridgeview.findViewById<EditText>(R.id.edtFixFridgeName)
        fridgename.setText(fname)

        // 다이얼로그의 확인 버튼과 연동해주기
        // 냉장고 이름 업데이트
        var fix_fridgename_ok = fixfridgeview.findViewById<Button>(R.id.btnFridgenameFix)
        fix_fridgename_ok.setOnClickListener {
            if(fridgename.length() > 0) {
                if(user != null){
                    firestore?.collection("fridge")?.document(index)
                        ?.update("name", fridgename.text.toString())
                        ?.addOnSuccessListener {  }
                        ?.addOnFailureListener {  }
                    firestore?.collection("user")?.document(user!!.uid)?.collection("myfridge")?.document(index)
                        ?.update("name", fridgename.text.toString())
                        ?.addOnSuccessListener { Toast.makeText(context, "냉장고 이름이 변경되었습니다.", Toast.LENGTH_SHORT).show()
                            fixfridgeDialog?.dismiss()}
                        ?.addOnFailureListener { Toast.makeText(context, "다시 입력해 주세요.", Toast.LENGTH_SHORT).show() }
                }
            }
            else {
                Toast.makeText(context, "냉장고 이름을 입력해 주세요.", Toast.LENGTH_SHORT).show()
            }
        }
    }

    // 냉장고 삭제
    fun deleteFridge(index: String, fname: String) {
        val deletedial = DialogDeletefridgeBinding.inflate(layoutInflater)
        val deleteview = deletedial.root
        val deletealertDialog = context?.let {
            androidx.appcompat.app.AlertDialog.Builder(it).run {
                setView(deletedial.root)
                show()
            }
        }//.setCanceledOnTouchOutside(true)  //외부 터치시 닫기
        //배경 투명으로 지정(모서리 둥근 배경 보이게 하기)
        deletealertDialog?.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))

        // 다이얼로그의 냉장고 이름 연동해주기
        var fridgename = deleteview.findViewById<TextView>(R.id.textFridgenameDelete)
        fridgename.setText("'" + fname + "'")

        // 다이얼로그의 확인 버튼과 연동해주기
        var delete_fridge_ok = deleteview.findViewById<Button>(R.id.btnFridgedelOk)
        delete_fridge_ok.setOnClickListener {
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

            deletealertDialog?.dismiss()
        }

        // 다이얼로그의 X버튼과 연동해주기
        var delete_fridge_cancel = deleteview.findViewById<ImageButton>(R.id.btnFridgedelClose)
        delete_fridge_cancel.setOnClickListener {
            deletealertDialog?.dismiss()
        }
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