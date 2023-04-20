package com.example.fffroject.fragment

import android.content.Intent
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.ImageButton
import android.widget.Toast
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import com.example.fffroject.*
import com.example.fffroject.alarm.FcmActivity
import com.example.fffroject.chat.ChatListActivity
import com.example.fffroject.databinding.DialogFixnicknameBinding
import com.example.fffroject.keyword.KeywordActivity
import com.example.fffroject.share.MyShareActivity
import com.example.fffroject.share.MyShareMainActivity
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.FirebaseFirestore
import java.text.SimpleDateFormat

class MypageFragment : Fragment() {

    var auth: FirebaseAuth? = null
    var firestore: FirebaseFirestore? = null
    var user : FirebaseUser? = null


    lateinit var btn_logout: Button
    lateinit var btn_mypage_share: Button
    lateinit var btn_mypage_message: Button
    lateinit var btn_mypage_alarm: Button
    lateinit var btn_mypage_nickname: Button
    lateinit var btn_mypage_keyalarm: Button //임시
    lateinit var btn_delete: Button

    lateinit var btn_nickname_close: ImageButton
    lateinit var edt_mypage_nickname: EditText
    lateinit var btn_nickname_fix : Button

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        var view = LayoutInflater.from(activity).inflate(R.layout.fragment_mypage, container, false)

        // 파이어베이스 인증 객체
        auth = FirebaseAuth.getInstance()
        user = auth!!.currentUser
        // 파이어스토어 인스턴스 초기화
        firestore = FirebaseFirestore.getInstance()
        val loginGoogle = GoogleSignInOptions
            .Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestIdToken(getString(R.string.google_client_id))   // default_web_client_id 인데 오류발생(이미사용되고있어서)
            // strings.xml에서 새로 선언 후 json의 oatuh에서 client id 옆부분을 넣어서 바꿔줌 -> 오류 안남
            .requestEmail()
            .build()
        var googleSigninClient = GoogleSignIn.getClient(view.context, loginGoogle)

        // 버튼 연동
        btn_logout = view.findViewById(R.id.btnLogout)
        btn_mypage_share = view.findViewById(R.id.btnMypageShare)
        btn_mypage_message = view.findViewById(R.id.btnMypageMessage)
        btn_mypage_alarm = view.findViewById(R.id.btnMypageAlarm)
        btn_mypage_nickname = view.findViewById(R.id.btnMypageNickname)
        btn_mypage_keyalarm = view.findViewById(R.id.btnMypageKeyword)
        btn_delete = view.findViewById(R.id.btnDelete)


        // 로그아웃 처리
        btn_logout.setOnClickListener {
            Toast.makeText(context,"로그아웃되셨습니다.", Toast.LENGTH_SHORT).show()
            auth?.signOut()
            googleSigninClient!!.signOut()
            val intent = Intent(activity, AuthActivity::class.java)
            activity?.let { ContextCompat.startActivity(it, intent, null) }
        }

        // 회원탈퇴 처리
        btn_delete.setOnClickListener {
            val intent = Intent(view.context, UserDeleteActivity::class.java)
            ContextCompat.startActivity(view.context, intent, null)
//            val nowTime = System.currentTimeMillis()
//            val timeformatter = SimpleDateFormat("yyyy.MM.dd.HH.mm.ss")
//            val dateTime = timeformatter.format(nowTime)
//
//            firestore?.collection("user")?.document(user!!.uid)
//                ?.update("status", "delete")
//                ?.addOnSuccessListener {
//                    // Post의 status 바꾸기
//                    firestore?.collection("post")?.whereEqualTo("writer", user!!.uid)?.get()
//                        ?.addOnSuccessListener { document ->
//                            if (document.size() != 0) {
//                                var postid = ""
//                                for (count: Int in 0..(document.size()-1)) {
//                                    var doc = document.documents?.get(count)
//                                    postid = doc.get("postId").toString()
//                                    firestore?.collection("post")?.document(postid)
//                                        ?.update("status", "delete")
//                                }
//                            }
//                        }
//                    // 냉장고(공유되는 냉장고) delete처리 해주기 내가 오너인거는 delete 멤버인거는 나가기처리
//                    firestore?.collection("user")?.document(user!!.uid)
//                        ?.collection("myfridge")?.get()
//                        ?.addOnSuccessListener { document ->
//                            if (document.size() != 0) {
//                                for (count: Int in 0..(document.size()-1)) {
//                                    var doc = document.documents?.get(count)
//                                    var fridgeid = doc.get("fridgeId").toString()
//                                    var memcount = doc.get("member").toString().toInt()
//                                    firestore?.collection("fridge")?.document(fridgeid)?.get()
//                                        ?.addOnSuccessListener { data ->
//                                            // 내가 owner인 냉장고라면
//                                            // 냉장고 delete
//                                            if (data?.get("owner").toString() == user!!.uid) {
//                                                firestore?.collection("fridge")?.document(fridgeid)
//                                                    ?.update("status", "delete")
//                                                    ?.addOnSuccessListener { }
//                                                    ?.addOnFailureListener { }
//                                                firestore?.collection("fridge")?.document(fridgeid)
//                                                    ?.update("updatedAt", dateTime)
//                                                    ?.addOnSuccessListener { }
//                                                    ?.addOnFailureListener { }
//
//                                                firestore?.collection("fridge")?.document(fridgeid)
//                                                    ?.collection("member")?.get()
//                                                    ?.addOnSuccessListener { task ->
//                                                        Toast.makeText(context, fridgeid, Toast.LENGTH_SHORT)
//                                                        var membercount = task.size()
//                                                        // 여기 잘 안되는거같음
//                                                        if (membercount != 0) {
//                                                            for (count: Int in 0..(membercount - 1)) {
//                                                                var doc = task.documents?.get(count)
//                                                                var memberuid = doc.get("userId").toString()
//                                                                firestore?.collection("user")?.document(memberuid)
//                                                                    ?.collection("myfridge")
//                                                                    ?.document(fridgeid)
//                                                                    ?.update("status", "delete")
//                                                                    ?.addOnSuccessListener { }
//                                                                    ?.addOnFailureListener { }
//                                                            }
//                                                        }
//                                                    }
//                                            }
//                                            // 내가 owner가 아니면
//                                            else{
//                                                firestore?.collection("fridge")?.document(fridgeid)?.collection("member")
//                                                    ?.document(user!!.uid)
//                                                    ?.update("status", "delete")
//                                                    ?.addOnSuccessListener { }
//                                                    ?.addOnFailureListener { }
//                                                firestore?.collection("fridge")?.document(fridgeid)?.collection("member")
//                                                    ?.document(user!!.uid)
//                                                    ?.update("updatedAt", dateTime)
//                                                    ?.addOnSuccessListener { }
//                                                    ?.addOnFailureListener { }
//                                                // member의 membercount 줄이기
//                                                firestore?.collection("fridge")?.document(fridgeid)
//                                                    ?.collection("member")?.get()
//                                                    ?.addOnSuccessListener { task ->
//                                                        if (memcount > 2) {
//                                                            for (count: Int in 0..(memcount - 2)) {
//                                                                var doc = task.documents?.get(count)
//                                                                var memberuid = doc.get("userId").toString()
//                                                                firestore?.collection("user")?.document(memberuid)
//                                                                    ?.collection("myfridge")
//                                                                    ?.document(fridgeid)
//                                                                    ?.update("member", FieldValue.increment(-1))
//                                                                    ?.addOnSuccessListener { }
//                                                                    ?.addOnFailureListener { }
//                                                            }
//                                                        }
//                                                    }
//                                                // owner의 membercount 줄이기
//                                                firestore?.collection("fridge")?.document(fridgeid)?.get()
//                                                    ?.addOnSuccessListener { document ->
//                                                        if (document != null) {
//                                                            // 해당하는 냉장고의 owner 받아오기
//                                                            var owner = document.data?.get("owner").toString()
//                                                            firestore?.collection("user")?.document(owner)?.collection("myfridge")
//                                                                ?.document(fridgeid)
//                                                                ?.update("member", FieldValue.increment(-1))
//                                                                ?.addOnSuccessListener { }
//                                                                ?.addOnFailureListener { }
//                                                        }
//                                                    }
//                                            }
//
//                                        }
//                                }
//                            }
//                        }
//
//                    // 유저의 status 바꾸기
//                    firestore?.collection("user")?.document(user!!.uid)
//                        ?.update("nickname", "(탈퇴유저)")
//                    firestore?.collection("user")?.document(user!!.uid)
//                        ?.update("updatedAt", dateTime)
//                        ?.addOnSuccessListener {
//                            user?.delete()
//                            auth?.signOut()     // 이거 안하면 계정이 걔속 남아있더라
//                            googleSigninClient!!.revokeAccess()
//                            Toast.makeText(context,"회원탈퇴되셨습니다.", Toast.LENGTH_SHORT).show()
//                            val intent = Intent(activity, AuthActivity::class.java)
//                            activity?.let { ContextCompat.startActivity(it, intent, null) }
//                        }
//                }



//            user?.delete()
//            auth?.signOut()     // 이거 안하면 계정이 걔속 남아있더라
//            googleSigninClient!!.revokeAccess()
//            val intent = Intent(activity, AuthActivity::class.java)
//            activity?.let { ContextCompat.startActivity(it, intent, null) }
            //activity?.finishAffinity()        // 아예 앱 실행 종료
        }

        // 나의 나눔 버튼 눌렀을 경우
        btn_mypage_share.setOnClickListener {
            val intent = Intent(view.context, MyShareMainActivity::class.java)
            ContextCompat.startActivity(view.context, intent, null)
        }

        // 메시지 버튼을 눌렀을 경우
        btn_mypage_message.setOnClickListener {
            val intent = Intent(view.context, ChatListActivity::class.java)
            ContextCompat.startActivity(view.context, intent, null)
        }

        // 닉네임 변경 버튼을 눌렀을 경우
        btn_mypage_nickname.setOnClickListener {
            editNickName()
        }

        // 임시 - 알람 버튼을 눌렀을 경우 후기 페이지로 이동
        btn_mypage_alarm.setOnClickListener {
            val intent = Intent(view.context, FcmActivity::class.java)
            ContextCompat.startActivity(view.context, intent, null)
        }

        // 임시 - 도움말 버튼을 눌렀을 경우 키워드 알람 페이지로 이동
        btn_mypage_keyalarm.setOnClickListener {
            val intent = Intent(view.context, KeywordActivity::class.java)
            ContextCompat.startActivity(view.context, intent, null)
        }

        return view
    }

    private fun editNickName() {
        val nicknamedial = DialogFixnicknameBinding.inflate(layoutInflater)
        val nicknameview = nicknamedial.root
        val nicknamealertDialog = context?.let {
            androidx.appcompat.app.AlertDialog.Builder(it).run {
                setView(nicknamedial.root)
                show()
            }
        }
        //배경 투명으로 지정(모서리 둥근 배경 보이게 하기)
        nicknamealertDialog?.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))

        // 에딧텍스트 나의 닉네임 연동
        // view가 다르기 때문에 새로운뷰.파인드~ 이런 식으로 해 줘야 널값 오류 안남
        edt_mypage_nickname = nicknameview.findViewById(R.id.edtNickName)
        firestore?.collection("user")?.document(user!!.uid)
            ?.get()?.addOnSuccessListener { document ->
                if (document != null) {
                    edt_mypage_nickname.setText(document?.data?.get("nickname").toString())
                }
            }

        // 확인 버튼을 눌렀을 경우 닉네임 업데이트
        btn_nickname_fix = nicknameview.findViewById(R.id.btnNicknameFix)
        btn_nickname_fix.setOnClickListener {
            if(edt_mypage_nickname.length() > 0) {
                if(user != null) {
                    val nowTime = System.currentTimeMillis()
                    val timeformatter = SimpleDateFormat("yyyy.MM.dd.HH.mm.ss")
                    val dateTime = timeformatter.format(nowTime)
                    firestore?.collection("user")?.document(user!!.uid)
                        ?.update("nickname", edt_mypage_nickname.text.toString())
                        ?.addOnSuccessListener { Toast.makeText(context, "이름이 변경되었습니다.", Toast.LENGTH_SHORT).show()
                            nicknamealertDialog?.dismiss()
                            firestore?.collection("user")?.document(user!!.uid)
                                ?.update("updatedAt", dateTime)
                                ?.addOnSuccessListener { }
                                ?.addOnFailureListener { }

                            // 닉네임 변경시 내가 멤버로 속한 냉장고에서도 업데이트
                            firestore?.collection("user")?.document(user!!.uid)
                                ?.collection("myfridge")?.get()
                                ?.addOnSuccessListener { task ->
                                    var fridgesize = task.size()
                                    for (count: Int in 0..(fridgesize - 1)) {
                                        var doc = task.documents?.get(count)
                                        var fridgeid = doc.get("fridgeId").toString()
                                        firestore?.collection("fridge")?.document(fridgeid)?.get()
                                            ?.addOnSuccessListener { document ->
                                                if (document != null) {
                                                    var ownerid =
                                                        document.data?.get("owner").toString()
                                                    if (ownerid != user!!.uid) {
                                                        firestore?.collection("fridge")?.document(fridgeid)
                                                            ?.collection("member")?.document(user!!.uid)
                                                            ?.update("memNickname",edt_mypage_nickname.text.toString())
                                                            ?.addOnSuccessListener {
                                                                firestore?.collection("fridge")?.document(fridgeid)
                                                                    ?.collection("member")?.document(user!!.uid)
                                                                    ?.update("updatedAt",dateTime.toString())
                                                            }
                                                            ?.addOnFailureListener { }
                                                    }
                                                }
                                            }
                                    }
                                }
                        }
                        ?.addOnFailureListener { Toast.makeText(context, "다시 입력해 주세요.", Toast.LENGTH_SHORT).show() }
                }
            }
            else {
                Toast.makeText(context, "이름을 입력해 주세요.", Toast.LENGTH_SHORT).show()
            }
        }

        // 닫기 버튼
        // 닫기 버튼이 ImageButton인지 Button인지 구분 잘 해주기(아니면 오류남)
        btn_nickname_close = nicknameview.findViewById(R.id.btnNicknameClose)
        btn_nickname_close.setOnClickListener(View.OnClickListener {
            nicknamealertDialog?.dismiss()
        })
    }
}