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
import com.example.fffroject.foodlist.FoodListActivity
import com.example.fffroject.R
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.firestore.FirebaseFirestore
import java.util.*
import kotlin.collections.ArrayList

import androidx.appcompat.widget.Toolbar
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.content.ContextCompat
import com.example.fffroject.databinding.*
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.Query
import java.text.SimpleDateFormat

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
    lateinit var text_nofridge: TextView

    lateinit var text_fridge_name: TextView


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
        text_nofridge = view.findViewById(R.id.textNoFridge)

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
            var peopleback: Button
            var fridgeback: ConstraintLayout
            var text_current : TextView

            fridgename = viewHolder.findViewById(R.id.textFridgeName)
            foodname = viewHolder.findViewById(R.id.textCurrentFood)
            member = viewHolder.findViewById(R.id.textMemberCount)
            fridgeback = viewHolder.findViewById(R.id.cardviewFridge)
            peopleback = viewHolder.findViewById(R.id.btnPeople)
            text_current = viewHolder.findViewById(R.id.textCurrent)

            // 리사이클러뷰 아이템 정보
            fridgename.text = fridgelist!![position].fridgeName
            fridgeid = fridgelist!![position].fridgeId!!
            foodname.text = fridgelist!![position].current
            member.text = fridgelist!![position].member.toString()

            // 리사이클러뷰의 아이템에 버튼이 있으므로 inner class에서 냉장고 삭제를 해야 함
            btn_option = viewHolder.findViewById(R.id.btnFridgeOption)

            // 냉장고 뒷배경 설정
            firestore?.collection("fridge")?.document(fridgeid)?.get()
                ?.addOnSuccessListener { document ->
                    if (document != null) {
                        // 해당하는 냉장고의 owner 받아오기
                        var owner = document.data?.get("owner").toString()
                        // current음식 설정을 위해(삭제/먹었음시 바로 디비에 올리고 갱신은 복잡해)
                        foodname.setText(document.data?.get("current").toString())
                        // 내가 member인 경우
                        if (owner != user!!.uid) {
                            fridgeback.setBackgroundResource(R.drawable.img_btn_fridge_member)
                            btn_option.setBackgroundResource(R.drawable.btn_fridgemore_blue)
                            peopleback.setBackgroundResource(R.drawable.img_btn_peopleblue)
                            fridgename.setTextColor(Color.parseColor("#71ABFF"))
                            foodname.setTextColor(Color.parseColor("#71ABFF"))
                            text_current.setTextColor(Color.parseColor("#71ABFF"))
                            member.setTextColor(Color.parseColor("#FFFFFF"))
                        }
                        // 내가 owner인 경우
                        else {
                            fridgeback.setBackgroundResource(R.drawable.img_btn_fridge_owner)
                            btn_option.setBackgroundResource(R.drawable.btn_fridgemore)
                            peopleback.setBackgroundResource(R.drawable.img_btn_peoplewhite)
                            fridgename.setTextColor(Color.parseColor("#FFFFFF"))
                            foodname.setTextColor(Color.parseColor("#FFFFFF"))
                            text_current.setTextColor(Color.parseColor("#FFFFFF"))
                            member.setTextColor(Color.parseColor("#71ABFF"))
                        }
                    }
                }

            // 냉장고별 옵션 선택
            var index = fridgelist!![position].fridgeId
            var fname = fridgelist!![position].fridgeName.toString()
            var currentname = fridgelist!![position].current.toString()
            var fcount = fridgelist!![position].member.toString().toInt()
            var ftime = fridgelist!![position].current.toString()
            var updatedAt = fridgelist!![position].createdAt.toString()
            var status = fridgelist!![position].status.toString()
            btn_option.setOnClickListener {
                if (index != null) {
                    // 냉장고의 owner인지 member인지 확인
                    firestore?.collection("fridge")?.document(index)?.get()
                        ?.addOnSuccessListener { document ->
                            if (document != null) {
                                // 해당하는 냉장고의 owner 받아오기
                                var owner = document.data?.get("owner").toString()
                                // 내가 owner인 경우
                                if (owner == user!!.uid) {
                                    fridgeOption(index, fname, currentname, fcount, ftime, updatedAt, status)
                                }
                                // 내가 member인 경우
                                else {
                                    fridgememberOption(index, fname, fcount, status)
                                }
                            }
                        }
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

    // 냉장고 추가 부분 완전히 수정 완료(이상없음 확인)
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
            // 최근 입력순 정렬 위한 시간 추가
            val nowTime = System.currentTimeMillis()
            val timeformatter = SimpleDateFormat("yyyy.MM.dd.hh.mm.ss")
            val dateTime = timeformatter.format(nowTime)
            if (edt_fridgename.text.toString() != null) {
                if (user != null) {
                    fridgeid = UUID.randomUUID().toString()
                    firestore?.collection("fridge")?.document("$fridgeid")
                        ?.set(
                            hashMapOf(
                                "fridgeId" to fridgeid,
                                "fridgeName" to edt_fridgename.text.toString(),
                                "owner" to user?.uid,
                                "current" to "냉장고가 비었습니다",
                                // 이하 추가
                                "createdAt" to dateTime.toString(),
                                "updatedAt" to dateTime.toString(),
                                "status" to "active"
                            )
                        )
                        ?.addOnSuccessListener { }
                        ?.addOnFailureListener { }
                    firestore?.collection("user")?.document(user!!.uid)?.collection("myfridge")
                        ?.document("$fridgeid")
                        ?.set(
                            hashMapOf(
                                "fridgeId" to fridgeid,
                                "fridgeName" to edt_fridgename.text.toString(),
                                "current" to "냉장고가 비었습니다",
                                "status" to "active",
                                "member" to 1,
                                "createdAt" to dateTime
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

    // 냉장고별 옵션 선택 수정 완료(owner 경우)
    // 냉장고별 옵션 선택(owner의 경우)
    fun fridgeOption(fridgeId: String, fridgeName: String, current: String, fcount: Int, ftime: String,
                     updatedAt: String, status: String) {
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
            fixnameFridge(fridgeId, fridgeName)
            optionalertDialog?.dismiss()
        }

        // 냉장고 멤버 추가 선택시
        var btn_add_member = optionview.findViewById<Button>(R.id.btnFridgeMemberAdd)
        btn_add_member.setOnClickListener {
            addMember(fridgeId, fridgeName, current, fcount, ftime)
            optionalertDialog?.dismiss()
        }

        // 냉장고 멤버 보기 선택시
        var btn_show_member = optionview.findViewById<Button>(R.id.btnShowMember)
        btn_show_member.setOnClickListener {
            showMember(fridgeId)
            optionalertDialog?.dismiss()
        }

        // 냉장고 삭제 버튼 선택시
        var btn_delete_fridge = optionview.findViewById<Button>(R.id.btnFridgeDelete)
        btn_delete_fridge.setOnClickListener {
            deleteFridge(fridgeId, fridgeName)
            optionalertDialog?.dismiss()
        }

        // 취소 버튼 선택시
        var btn_option_close = optionview.findViewById<Button>(R.id.btnFridgeOptionClose)
        btn_option_close.setOnClickListener {
            optionalertDialog?.dismiss()
        }

    }

    // 냉장고 옵션 선택 수정 완료(멤버의 경우)
    // 냉장고 옵션 멤버의 경우
    fun fridgememberOption(fridgeId: String, fridgeName: String, fcount: Int, status: String) {
        //뷰 바인딩을 적용한 XML 파일 초기화
        val memberdial = DialogMemberoptionBinding.inflate(layoutInflater)
        val memberview = memberdial.root
        val memberalertDialog = context?.let {
            androidx.appcompat.app.AlertDialog.Builder(it).run {
                setView(memberdial.root)
                show()
            }
        }//.setCanceledOnTouchOutside(true)  //외부 터치시 닫기
        //배경 투명으로 지정(모서리 둥근 배경 보이게 하기)
        memberalertDialog?.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        // 다이얼로그 밑으로 나오게
        memberalertDialog?.window?.setGravity(Gravity.BOTTOM)

        // 냉장고 나가기 선택시
        var btn_member_out = memberview.findViewById<Button>(R.id.btnMemberDrop)
        btn_member_out.setOnClickListener {
            dropMember(fridgeId, fridgeName, fcount)
            memberalertDialog?.dismiss()
        }

        // 냉장고 멤버 보기
        var btn_show_member = memberview.findViewById<Button>(R.id.btnShowMember)
        btn_show_member.setOnClickListener {
            showMember(fridgeId)
            memberalertDialog?.dismiss()
        }

        // 취소 선택시
        var btn_memberoption_close = memberview.findViewById<Button>(R.id.btnFridgememOptionClose)
        btn_memberoption_close.setOnClickListener {
            memberalertDialog?.dismiss()
        }
    }

    // 냉장고 이름 변경 완전 수정 완료
    // 냉장고 이름 변경
    fun fixnameFridge(fridgeId: String, fridgeName: String) {
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
        fridgename.setText(fridgeName)

        // 다이얼로그의 확인 버튼과 연동해주기
        // 냉장고 이름 업데이트
        var fix_fridgename_ok = fixfridgeview.findViewById<Button>(R.id.btnFridgenameFix)
        fix_fridgename_ok.setOnClickListener {
            val nowTime = System.currentTimeMillis()
            val timeformatter = SimpleDateFormat("yyyy.MM.dd.hh.mm.ss")
            val dateTime = timeformatter.format(nowTime)

            if (fridgename.length() > 0) {
                if (user != null) {
                    firestore?.collection("fridge")?.document(fridgeId)
                        ?.update("fridgeName", fridgename.text.toString())
                        ?.addOnSuccessListener { }
                        ?.addOnFailureListener { }
                    firestore?.collection("fridge")?.document(fridgeId)
                        ?.update("updatedAt", dateTime)
                        ?.addOnSuccessListener { }
                        ?.addOnFailureListener { }
                    firestore?.collection("user")?.document(user!!.uid)?.collection("myfridge")
                        ?.document(fridgeId)
                        ?.update("fridgeName", fridgename.text.toString())
                        ?.addOnSuccessListener {
                            Toast.makeText(context, "냉장고 이름이 변경되었습니다.", Toast.LENGTH_SHORT).show()
                            fixfridgeDialog?.dismiss()
                        }
                        ?.addOnFailureListener {
                            Toast.makeText(
                                context,"다시 입력해 주세요.",Toast.LENGTH_SHORT).show()
                        }
                    // 멤버의 냉장고 이름도 변경해주기
                    var membercount = 0
                    // 멤버 수 받아오기
                    firestore?.collection("fridge")?.document(fridgeId)
                        ?.collection("member")?.get()
                        ?.addOnSuccessListener { task ->
                            membercount = task.size()
                            // 멤버가 있다면
                            if (membercount != 0) {
                                // 멤버의 myfridge의 냉장고명 바꿔주기
                                for (count: Int in 0..(membercount - 1)) {
                                    var doc = task.documents?.get(count)
                                    var memberuid = doc.get("userId").toString()
                                    firestore?.collection("user")?.document(memberuid)
                                        ?.collection("myfridge")
                                        ?.document(fridgeId)
                                        ?.update("fridgeName", fridgename.text.toString())
                                        ?.addOnSuccessListener { }
                                        ?.addOnFailureListener { }
                                }
                            }
                        }
                }
            } else {
                Toast.makeText(context, "냉장고 이름을 입력해 주세요.", Toast.LENGTH_SHORT).show()
            }
        }
        // X버튼 연동
        var btn_fixfridge_close = fixfridgeview.findViewById<ImageButton>(R.id.btnNicknameClose)
        btn_fixfridge_close.setOnClickListener {
            fixfridgeDialog?.dismiss()
        }
    }

    // 냉장고에 멤버 추가 수정 전부 완료 및 확인 완료
    // updatedAt 수정해줘야함 확인하기
    fun addMember(fridgeId: String, fridgeName: String, current: String, fcount: Int, ftime: String) {
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
            var memberstatus = ""
            // 이메일칸이 비지 않았다면
            if (addmember.length > 0) {
                val nowTime = System.currentTimeMillis()
                val timeformatter = SimpleDateFormat("yyyy.MM.dd.hh.mm.ss")
                val dateTime = timeformatter.format(nowTime)

                firestore?.collection("user")?.whereEqualTo("email", addmember)?.get()
                    ?.addOnSuccessListener { document ->
                        if (document.size() != 0) {
                            // 해당하는 아이디의 사람이 있다면 uid 받아오기
                            var sheet = document.documents?.get(0)
                            memberuid = sheet.get("userId").toString()
                            membername = sheet.get("nickname").toString()
                            // 상태 받아와서 active인지 확인해주기
                            memberstatus = sheet.get("status").toString()
                            if (memberstatus == "active") {
                                // 해당 멤버를 fridge의 멤버에 추가해주기
                                // 이미 있는 멤버인지 검색
                                // 이미 있는 멤버라면 status가 active인지 delete인지 확인 --> 아직 안해줌
                                firestore?.collection("fridge")?.document(fridgeId)?.collection("member")
                                    ?.whereEqualTo("userId", memberuid)?.get()
                                    ?.addOnCompleteListener { task ->
                                        // 새로운 멤버인 경우
                                        if (task.result?.size() == 0) {
                                            // fridge의 멤버에 추가
                                            firestore?.collection("fridge")?.document(fridgeId)
                                                ?.collection("member")
                                                ?.document("$memberuid")
                                                ?.set(
                                                    hashMapOf(
                                                        "userId" to memberuid,
                                                        "memNickname" to membername,
                                                        "memEmail" to addmember,
                                                        "createdAt" to dateTime,
                                                        "updatedAt" to dateTime,
                                                        "status" to "active"
                                                    )
                                                )
                                            // 새로운 멤버의 myfridge에 냉장고 추가
                                            firestore?.collection("user")?.document(memberuid)
                                                ?.collection("myfridge")
                                                ?.document("$fridgeId")
                                                ?.set(
                                                    hashMapOf(
                                                        "fridgeId" to fridgeId,
                                                        "fridgeName" to fridgeName,
                                                        "current" to current,
                                                        "status" to "active",
                                                        "member" to fcount,
                                                        "createdAt" to ftime
                                                    )
                                                )
                                                ?.addOnSuccessListener {
                                                    membercount = fcount + 1
                                                    // 멤버의 냉장고의 현재 멤버 수 업데이트
                                                    firestore?.collection("fridge")?.document(fridgeId)
                                                        ?.collection("member")?.get()
                                                        ?.addOnSuccessListener { task ->
                                                            if (membercount != 0) {
                                                                for (count: Int in 0..(membercount - 2)) {
                                                                    var doc = task.documents?.get(count)
                                                                    var memberuid =
                                                                        doc.get("userId").toString()
                                                                    firestore?.collection("user")
                                                                        ?.document(memberuid)
                                                                        ?.collection("myfridge")
                                                                        ?.document(fridgeId)
                                                                        ?.update("member", membercount)
                                                                        ?.addOnSuccessListener { }
                                                                        ?.addOnFailureListener { }
                                                                }
                                                            }
                                                        }
                                                    // 나의 냉장고에서 멤버 수 업데이트
                                                    firestore?.collection("user")?.document(user!!.uid)
                                                        ?.collection("myfridge")
                                                        ?.document(fridgeId)
                                                        ?.update("member", membercount)
                                                        ?.addOnSuccessListener {
                                                            Toast.makeText(context, "등록되었습니다.", Toast.LENGTH_SHORT).show()
                                                        }
                                                        ?.addOnFailureListener { }
                                                }
                                                ?.addOnFailureListener { }
                                            addmemberDialog?.dismiss()
                                        }
                                        // 기존에 등록된 멤버인 경우
                                        // 기존 등록인지 삭제되었던 앤지 확인해줘야함 (위에서)
                                        else {
                                            firestore?.collection("fridge")?.document(fridgeId)
                                                ?.collection("member")
                                                ?.document("$memberuid")?.get()
                                                ?.addOnSuccessListener { document ->
                                                    if (document != null) {
                                                        var memberstatus = document.data?.get("status").toString()
                                                        if (memberstatus == "active") {
                                                            Toast.makeText(context, "이미 등록된 멤버입니다.", Toast.LENGTH_SHORT).show()
                                                        } else if (memberstatus == "delete") {
                                                            firestore?.collection("fridge")?.document(fridgeId)
                                                                ?.collection("member")?.document(memberuid)
                                                                ?.update("status", "active")
                                                                ?.addOnSuccessListener {
                                                                    membercount = fcount + 1
                                                                    // 멤버의 냉장고의 현재 멤버 수 업데이트
                                                                    firestore?.collection("fridge")?.document(fridgeId)
                                                                        ?.collection("member")?.get()
                                                                        ?.addOnSuccessListener { task ->
                                                                            if (membercount != 0) {
                                                                                for (count: Int in 0..(membercount - 2)) {
                                                                                    var doc = task.documents?.get(count)
                                                                                    var memberuid =
                                                                                        doc.get("userId").toString()
                                                                                    firestore?.collection("user")
                                                                                        ?.document(memberuid)
                                                                                        ?.collection("myfridge")
                                                                                        ?.document(fridgeId)
                                                                                        ?.update("member", membercount)
                                                                                        ?.addOnSuccessListener { }
                                                                                        ?.addOnFailureListener { }
                                                                                }
                                                                            }
                                                                        }
                                                                    // 나의 냉장고에서 멤버 수 업데이트
                                                                    firestore?.collection("user")?.document(user!!.uid)
                                                                        ?.collection("myfridge")
                                                                        ?.document(fridgeId)
                                                                        ?.update("member", membercount)
                                                                        ?.addOnSuccessListener {
                                                                            Toast.makeText(context,"등록되었습니다.",Toast.LENGTH_SHORT).show()
                                                                            addmemberDialog?.dismiss()
                                                                        }
                                                                        ?.addOnFailureListener { }
                                                                    // 멤버의 냉장고에서 냉장고 status active로 변경
                                                                    firestore?.collection("user")?.document(memberuid)
                                                                        ?.collection("myfridge")?.document(fridgeId)
                                                                        ?.update("status", "active")
                                                                        ?.addOnSuccessListener {  }
                                                                        ?.addOnFailureListener {  }
                                                                }
                                                                ?.addOnFailureListener { }
                                                            firestore?.collection("fridge")?.document(fridgeId)
                                                                ?.collection("member")?.document(memberuid)
                                                                ?.update("updatedAt", dateTime)
                                                                ?.addOnSuccessListener {  }
                                                                ?.addOnFailureListener {  }
                                                        }
                                                    }
                                                }
                                            //Toast.makeText(context, "이미 등록된 멤버입니다.", Toast.LENGTH_SHORT).show()
                                        }
                                    }
                            } else {
                                Toast.makeText(context, "해당 유저가 없습니다.", Toast.LENGTH_SHORT).show()
                            }
                        } else {
                            Toast.makeText(context, "해당 유저가 없습니다.", Toast.LENGTH_SHORT).show()
                        }
                    }
            } else {
                Toast.makeText(context, "이메일을 입력해 주세요.", Toast.LENGTH_SHORT).show()
            }

        }

        // 취소 버튼과 연동
        var btn_member_close = addmemberview.findViewById<ImageButton>(R.id.btnAddmemberClose)
        btn_member_close.setOnClickListener {
            addmemberDialog?.dismiss()
        }
    }

    // 냉장고 멤버 보기 멤버 상태별 여부 완료
    fun showMember(index: String) {
        val showmemdial = DialogShowmemberBinding.inflate(layoutInflater)
        val showmemview = showmemdial.root
        val showmemDialog = context?.let {
            androidx.appcompat.app.AlertDialog.Builder(it).run {
                setView(showmemdial.root)
                show()
            }
        }//.setCanceledOnTouchOutside(true)  //외부 터치시 닫기
        //배경 투명으로 지정(모서리 둥근 배경 보이게 하기)
        showmemDialog?.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))

        var text_owner = showmemview.findViewById<TextView>(R.id.textOwner)
        var text_member = showmemview.findViewById<TextView>(R.id.textMember)
        var btn_member_close = showmemview.findViewById<ImageButton>(R.id.btnMemberShowClose)

        text_owner.setText("")
        text_member.setText("")
        var text_name = ""

        // 취소 버튼 눌렀을 경우
        btn_member_close.setOnClickListener{
            showmemDialog?.dismiss()
        }
        // owner 이름 추가해주기
        // 상태가 active인지 delete인지 확인하는 부분 추가
        firestore?.collection("fridge")?.document(index)?.get()
            ?.addOnSuccessListener { document ->
                if (document != null) {
                    // 해당하는 냉장고의 owner 받아오기
                    var ownerid = document.data?.get("owner").toString()
                    firestore?.collection("user")?.document(ownerid)?.get()
                        ?.addOnSuccessListener { document ->
                            if (document != null) {
                                var owner = document.data?.get("nickname").toString()
                                text_owner.setText(owner)
                            }
                        }
                    // 멤버 이름 추가해주기
                    // 멤버 active인지 확인
                    firestore?.collection("fridge")?.document(index)
                        ?.collection("member")?.get()
                        ?.addOnSuccessListener { task ->
                            var membercount = 0
                            membercount = task.size()
                            if (membercount != 0) {
                                for (count: Int in 0..(membercount - 1)) {
                                    var doc = task.documents?.get(count)
                                    var memberuid = doc.data?.get("userId").toString()
                                    var membernickname = doc.data?.get("memNickname").toString()
//                                    var memberstatus = doc.data?.get("status").toString()
                                    firestore?.collection("user")?.document(memberuid)?.get()
                                        ?.addOnSuccessListener { tasks ->
                                            var memname = tasks.data?.get("nickname").toString()
                                            var memberstatus = tasks.data?.get("status").toString()
                                            if (memberstatus == "active") {
                                                if (text_name == "") {
                                                    text_name = memname
                                                }
                                                else {
                                                    text_name = text_name + "\n\n" + memname
                                                }
                                                text_member.setText(text_name)
                                            }
//                                            if (count == 0) {
//                                                text_name = memname
//                                            }
//                                            else {
//                                                text_name = text_name + "\n\n" + memname
//                                            }
//                                            text_member.setText(text_name)
////                                            if (count == 0) {
////                                                Toast.makeText(context, count.toString(), Toast.LENGTH_SHORT).show()
////                                                if (memberstatus == "active")
////                                                    text_name = memname
////                                            }
////                                            else {
////
////                                                if (memberstatus == "active")
////                                                    text_name = text_name + "\n\n" + memname
////                                            }
////                                            text_member.setText(text_name)
                                        }
//                                    if (memberstatus == "active") {
//                                        if (text_name == "") {
//                                            text_name = membernickname
//                                        }
//                                        else {
//                                            text_name = text_name + "\n\n" + membernickname
//                                        }
//                                        text_member.setText(text_name)
//                                    }

                                }
                            }
                        }
                }
            }
    }

    // 냉장고 삭제
    // 삭제버튼까지 수정 완료
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
        fridgename.setText("'" + fname + "'" + " 을(를) 삭제하시겠습니까?")

        // 다이얼로그의 확인 버튼과 연동해주기
        var delete_fridge_ok = deleteview.findViewById<Button>(R.id.btnFridgedelOk)
        delete_fridge_ok.setOnClickListener {
            val nowTime = System.currentTimeMillis()
            val timeformatter = SimpleDateFormat("yyyy.MM.dd.HH.mm.ss")
            val dateTime = timeformatter.format(nowTime)
            // 멤버의 냉장고 status 변경해주기
            var membercount = 0
            firestore?.collection("fridge")?.document(index)
                ?.collection("member")?.get()
                ?.addOnSuccessListener { task ->
                    membercount = task.size()
                    if (membercount != 0) {
                        for (count: Int in 0..(membercount - 1)) {
                            var doc = task.documents?.get(count)
                            var memberuid = doc.get("userId").toString()
                            firestore?.collection("user")?.document(memberuid)
                                ?.collection("myfridge")
                                ?.document(index)
                                ?.update("status", "delete")
                                ?.addOnSuccessListener { }
                                ?.addOnFailureListener { }
                        }
                    }
                }

            // 나의 냉장고 status 변경해주기
            firestore?.collection("user")?.document(user!!.uid)?.collection("myfridge")
                ?.document(index)
                ?.update("status", "delete")
                ?.addOnSuccessListener {
                    Toast.makeText(activity, "삭제되었습니다.", Toast.LENGTH_SHORT).show()
                }
                ?.addOnFailureListener { }

            // fridge status delete로 바꾸기
            firestore?.collection("fridge")?.document(fridgeid)
                ?.update("status", "delete")
                ?.addOnSuccessListener { }
                ?.addOnFailureListener { }
            firestore?.collection("fridge")?.document(fridgeid)
                ?.update("updatedAt", dateTime)
                ?.addOnSuccessListener { }
                ?.addOnFailureListener { }
            // 나의 냉장고 완전삭제 할 경우 해당 코드 사용
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

    // 냉장고 나가기 버튼 선택시
    fun dropMember(index: String, fname: String, fcount: Int) {
        val dropdial = DialogDropmemberBinding.inflate(layoutInflater)
        val dropview = dropdial.root
        val dropalertDialog = context?.let {
            androidx.appcompat.app.AlertDialog.Builder(it).run {
                setView(dropdial.root)
                show()
            }
        }//.setCanceledOnTouchOutside(true)  //외부 터치시 닫기
        //배경 투명으로 지정(모서리 둥근 배경 보이게 하기)
        dropalertDialog?.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))

        // 다이얼로그의 냉장고 이름 연동해주기
        var fridgename = dropview.findViewById<TextView>(R.id.textFridgenameDrop)
        fridgename.setText("'" + fname + "'")

        // 다이얼로그의 확인 버튼과 연동해주기
        var drop_member_ok = dropview.findViewById<Button>(R.id.btnFridgedropOk)
        drop_member_ok.setOnClickListener {
            val nowTime = System.currentTimeMillis()
            val timeformatter = SimpleDateFormat("yyyy.MM.dd.HH.mm.ss")
            val dateTime = timeformatter.format(nowTime)
            // membercount 줄여주기
            if (user != null) {
                firestore?.collection("fridge")?.document(index)?.collection("member")
                    ?.document(user!!.uid)
                    ?.update("status", "delete")
                    ?.addOnSuccessListener { }
                    ?.addOnFailureListener { }
                firestore?.collection("fridge")?.document(index)?.collection("member")
                    ?.document(user!!.uid)
                    ?.update("updatedAt", dateTime)
                    ?.addOnSuccessListener { }
                    ?.addOnFailureListener { }
                // member의 membercount 줄이기
                firestore?.collection("fridge")?.document(index)
                    ?.collection("member")?.get()
                    ?.addOnSuccessListener { task ->
                        if (fcount > 2) {
                            for (count: Int in 0..(fcount - 2)) {
                                var doc = task.documents?.get(count)
                                var memberuid = doc.get("userId").toString()
                                firestore?.collection("user")?.document(memberuid)
                                    ?.collection("myfridge")
                                    ?.document(index)
                                    ?.update("member", FieldValue.increment(-1))
                                    ?.addOnSuccessListener { }
                                    ?.addOnFailureListener { }
                            }
                        }
                    }
                // owner의 membercount 줄이기
                firestore?.collection("fridge")?.document(index)?.get()
                    ?.addOnSuccessListener { document ->
                        if (document != null) {
                            // 해당하는 냉장고의 owner 받아오기
                            var owner = document.data?.get("owner").toString()
                            firestore?.collection("user")?.document(owner)?.collection("myfridge")
                                ?.document(index)
                                ?.update("member", FieldValue.increment(-1))
                                ?.addOnSuccessListener { }
                                ?.addOnFailureListener { }
                        }
                    }
            }
            // myfridge에서 냉장고 삭제
            firestore?.collection("user")?.document(user!!.uid)?.collection("myfridge")
                ?.document(index)
                ?.update("status","delete")
                ?.addOnSuccessListener {
                    Toast.makeText(activity, "냉장고에서 나갔습니다.", Toast.LENGTH_SHORT).show()
                    dropalertDialog?.dismiss()
                }
                ?.addOnFailureListener { }
        }

        // 다이얼로그의 취소 버튼과 연동해주기
        var btn_memberdrop_close = dropview.findViewById<ImageButton>(R.id.btnMemberdropClose)
        btn_memberdrop_close.setOnClickListener {
            dropalertDialog?.dismiss()
        }
    }

    // 파이어베이스에서 데이터 불러오는 함수
    fun loadData() {
        // 냉장고 리스트 불러오기
        if (user != null) {
            // visible이 true인 냉장고만 가져오
            firestore?.collection("user")?.document(user!!.uid)
                ?.collection("myfridge")?.whereEqualTo("status", "active")
                ?.orderBy("createdAt", Query.Direction.DESCENDING)
                ?.addSnapshotListener { value, error ->
                    fridgelist.clear()
                    if (value != null) {
                        for (snapshot in value.documents) {
                            var item = snapshot.toObject(MyFridge::class.java)
                            if (item != null) {
                                fridgelist.add(item)
                            }
                        }
                        if (fridgelist.size > 0) {
                            text_nofridge.visibility = View.INVISIBLE
                        }
                    }
                    recyclerview_fridge.adapter?.notifyDataSetChanged()
                }
        }
    }
}