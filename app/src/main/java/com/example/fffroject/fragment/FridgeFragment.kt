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
import com.google.firebase.firestore.Query

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
            var member: TextView

            fridgename = viewHolder.findViewById(R.id.textFridgeName)
            foodname = viewHolder.findViewById(R.id.textCurrentFood)
            member = viewHolder.findViewById(R.id.textMemberCount)

            // 리사이클러뷰 아이템 정보
            fridgename.text = fridgelist!![position].name
            fridgeid = fridgelist!![position].index!!
            foodname.text = fridgelist!![position].current
            member.text = fridgelist!![position].member.toString()

            // 리사이클러뷰의 아이템에 버튼이 있으므로 inner class에서 냉장고 삭제를 해야 함
            btn_option = viewHolder.findViewById(R.id.btnFridgeOption)

            // 냉장고별 옵션 선택
            var index = fridgelist!![position].index
            var fname = fridgelist!![position].name.toString()
            var currentname = fridgelist!![position].current.toString()
            var fcount = fridgelist!![position].member.toString().toInt()
            btn_option.setOnClickListener {
                if (index != null) {
                    fridgeOption(index, fname, currentname, fcount)
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
                                "current" to "냉장고가 비었습니다",
                                "status" to true,
                                "member" to 1
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
    fun fridgeOption(index: String, fname: String, current: String, fcount: Int) {
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

        // 냉장고 멤버 추가 선택시
        var btn_add_member = optionview.findViewById<Button>(R.id.btnFridgeMemberAdd)
        btn_add_member.setOnClickListener {
            addMember(index, fname, current, fcount)
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
                    // 멤버의 냉장고 이름도 변경해주기
                    var membercount = 0
                    // 멤버 수 받아오기
                    firestore?.collection("fridge")?.document(index)
                        ?.collection("member")?.get()
                        ?.addOnSuccessListener { task ->
                            membercount = task.size()
                            // 멤버가 있다면
                            if (membercount != 0) {
                                // 멤버의 myfridge의 냉장고명 바꿔주기
                                for (count:Int in 0..(membercount-1)){
                                    var doc = task.documents?.get(count)
                                    var memberuid = doc.get("uid").toString()
                                    firestore?.collection("user")?.document(memberuid)?.collection("myfridge")
                                        ?.document(index)
                                        ?.update("name", fridgename.text.toString())
                                        ?.addOnSuccessListener { }
                                        ?.addOnFailureListener { }
                                }
                            }
                        }
                }
            }
            else {
                Toast.makeText(context, "냉장고 이름을 입력해 주세요.", Toast.LENGTH_SHORT).show()
            }
        }
    }

    // 냉장고에 멤버 추가
    fun addMember(index: String, fname: String, current: String, fcount: Int){
        //뷰 바인딩을 적용한 XML 파일 초기화
        val addmemberdial = DialogAddmemberBinding.inflate(layoutInflater)
        val addmemberview = addmemberdial.root
        val addmemberDialog = context?.let {
            androidx.appcompat.app.AlertDialog.Builder(it).run {
                setView(addmemberdial.root)
                show()
            }
        }//.setCanceledOnTouchOutside(true)  //외부 터치시 닫기
        //배경 투명으로 지정(모서리 둥근 배경 보이게 하기)
        addmemberDialog?.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))

        // 이메일 받는 부분 연동하기
        var memberemail = addmemberview.findViewById<EditText>(R.id.edtMemberEmail)

        // 다이얼로그의 확인 부분과 연동
        val btn_memberok = addmemberview.findViewById<Button>(R.id.btnMemberOk)
        btn_memberok.setOnClickListener {
            // 이메일 받아와서 파이어스토어에서 멤버 찾기
            var addmember = memberemail.text.toString()
            var memberuid = ""
            var membername = ""
            var membercount = 0
            // 이메일칸이 비지 않았다면
                if(addmember.length > 0){
                    firestore?.collection("user")?.whereEqualTo("email",addmember)?.get()
                        ?.addOnSuccessListener { document ->
                            if (document.size() != 0) {
                                // 해당하는 아이디의 사람이 있다면 uid 받아오기
                                var sheet = document.documents?.get(0)
                                memberuid = sheet.get("uid").toString()
                                membername = sheet.get("nickname").toString()
                                // 위에까지 오류 안나고 가능
                                // 해당 멤버를 fridge의 멤버에 추가해주기
                                // 이미 있는 멤버인지 검색
                                firestore?.collection("fridge")?.document(index)?.collection("member")
                                    ?.whereEqualTo("uid",memberuid)?.get()
                                    ?.addOnCompleteListener { task ->
                                        // 새로운 멤버인 경우
                                        if(task.result?.size() == 0) {
                                            // fridge의 멤버에 추가
                                            firestore?.collection("fridge")?.document(index)?.collection("member")
                                                ?.document("$memberuid")
                                                ?.set(
                                                    hashMapOf(
                                                        "uid" to memberuid,
                                                        "nickname" to membername,
                                                        "email" to addmember
                                                    )
                                                )
                                            // 새로운 멤버의 myfridge에 냉장고 추가
                                            firestore?.collection("user")?.document(memberuid)?.collection("myfridge")
                                                ?.document("$index")
                                                ?.set(
                                                    hashMapOf(
                                                        "index" to index,
                                                        "name" to fname,
                                                        "current" to current,
                                                        "status" to true,
                                                        "member" to fcount
                                                    )
                                                )
                                                ?.addOnSuccessListener {
                                                    membercount = fcount + 1
                                                    // 멤버의 냉장고의 현재 멤버 수 업데이트
                                                    firestore?.collection("fridge")?.document(index)
                                                        ?.collection("member")?.get()
                                                        ?.addOnSuccessListener { task ->
                                                            //Toast.makeText(activity, membercount.toString(), Toast.LENGTH_SHORT).show()
                                                            if (membercount != 0) {
                                                                for (count:Int in 0..(membercount-2)){
                                                                    var doc = task.documents?.get(count)
                                                                    var memberuid = doc.get("uid").toString()
                                                                    //Toast.makeText(activity, memberuid, Toast.LENGTH_SHORT).show()
                                                                    firestore?.collection("user")?.document(memberuid)?.collection("myfridge")
                                                                        ?.document(index)
                                                                        ?.update("member", membercount)
                                                                        ?.addOnSuccessListener { }
                                                                        ?.addOnFailureListener { }
                                                                }
                                                            }
                                                        }
                                                    // 나의 냉장고에서 멤버 수 업데이트
                                                    firestore?.collection("user")?.document(user!!.uid)?.collection("myfridge")
                                                        ?.document(index)
                                                        ?.update("member", membercount)
                                                        ?.addOnSuccessListener { Toast.makeText(context, "등록되었습니다.", Toast.LENGTH_SHORT).show()}
                                                        ?.addOnFailureListener { }
                                                }
                                                ?.addOnFailureListener { }
                                            //Toast.makeText(context, "등록되었습니다.", Toast.LENGTH_SHORT).show()
                                            addmemberDialog?.dismiss()
                                        }
                                        // 기존에 등록된 멤버인 경우
                                        else {
                                            Toast.makeText(context, "이미 등록된 멤버입니다.", Toast.LENGTH_SHORT).show()
                                        }
                                    }

                            }
                            else {
                                Toast.makeText(context, "해당 유저가 없습니다.", Toast.LENGTH_SHORT).show()
                            }
                        }
                }
            else {
                Toast.makeText(context, "이메일을 입력해 주세요.", Toast.LENGTH_SHORT).show()
                }

        }

        // 취소 버튼과 연동
        var btn_member_close = addmemberview.findViewById<ImageButton>(R.id.btnAddmemberClose)
        btn_member_close.setOnClickListener {
            addmemberDialog?.dismiss()
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
            // 멤버의 냉장고 status 변경해주기
            var membercount = 0
            firestore?.collection("fridge")?.document(index)
                ?.collection("member")?.get()
                ?.addOnSuccessListener { task ->
                    membercount = task.size()
                    Toast.makeText(activity, membercount.toString(), Toast.LENGTH_SHORT).show()
                    if (membercount != 0) {
                        for (count:Int in 0..(membercount-1)){
                            var doc = task.documents?.get(count)
                            var memberuid = doc.get("uid").toString()
                            Toast.makeText(activity, memberuid, Toast.LENGTH_SHORT).show()
                            firestore?.collection("user")?.document(memberuid)?.collection("myfridge")
                                ?.document(index)
                                ?.update("status", false)
                                ?.addOnSuccessListener { }
                                ?.addOnFailureListener { }
                        }
                    }
                }
//                ?.addSnapshotListener { value, error ->
//                    if (value != null) {
//                        var i = 0
//                        for (snapshot in value.documents) {
//                            var memberuid = value?.documents.get(i).toString()
////                                firestore?.collection("fridge")?.document(index)
////                                ?.collection("member")?.document("uid")?.get().toString()
//                            Toast.makeText(activity, memberuid, Toast.LENGTH_SHORT).show()
//                            firestore?.collection("user")?.document(memberuid)?.collection("myfridge")
//                                ?.document(index)
//                                ?.update("status", false)
//                                ?.addOnSuccessListener { }
//                                ?.addOnFailureListener { }
//                            i += i + 1
//                        }
//                    }
//                }
            // 나의 냉장고 status 변경해주기
            firestore?.collection("user")?.document(user!!.uid)?.collection("myfridge")
                ?.document(index)
                ?.update("status", false)
                ?.addOnSuccessListener { Toast.makeText(activity, "삭제되었습니다.", Toast.LENGTH_SHORT).show()}
                ?.addOnFailureListener { }
            // 냉장고 완전삭제
//            firestore?.collection("user")?.document(user!!.uid)?.collection("myfridge")
//                ?.document(index)
//                ?.delete()
//                ?.addOnSuccessListener {
//                    Toast.makeText(activity, "삭제되었습니다.", Toast.LENGTH_SHORT).show()
//                }
//                ?.addOnFailureListener { }

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
            // visible이 true인 냉장고만 가져오
            firestore?.collection("user")?.document(user!!.uid)
                ?.collection("myfridge")?.whereEqualTo("status", true)
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