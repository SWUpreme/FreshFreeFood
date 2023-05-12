package com.example.fffroject

import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.databinding.DataBindingUtil.setContentView
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.FirebaseFirestore
import java.text.SimpleDateFormat

class UserDeleteActivity : AppCompatActivity() {

    var auth: FirebaseAuth? = null
    var firestore: FirebaseFirestore? = null
    var user: FirebaseUser? = null

    lateinit var btn_check: Button
    lateinit var text_check: TextView
    lateinit var btn_checkok: Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_userdelete)

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
        var googleSigninClient = GoogleSignIn.getClient(this, loginGoogle)

        btn_check = findViewById(R.id.btnCheck)
        btn_checkok = findViewById(R.id.btnDeleteOk)
        text_check = findViewById(R.id.textCheck)
        var check = false

        btn_check.setOnClickListener {
            if (check == false) {
                btn_check.setBackgroundResource(R.drawable.checkbox_blue)
                btn_checkok.setBackgroundResource(R.drawable.btn_checkok_blue)
                text_check.setTextColor(Color.parseColor("#30353D"))
                check = true
            } else {
                btn_check.setBackgroundResource(R.drawable.checkbox_grey)
                btn_checkok.setBackgroundResource(R.drawable.btn_checkok_grey)
                text_check.setTextColor(Color.parseColor("#95979B"))
                check = false
            }
        }

        btn_checkok.setOnClickListener {
            // 체크박스 확인했을경우
            // 회원탈퇴
            if (check == true) {
                val nowTime = System.currentTimeMillis()
                val timeformatter = SimpleDateFormat("yyyy.MM.dd.HH.mm.ss")
                val dateTime = timeformatter.format(nowTime)
                var useruid = user!!.uid

                firestore?.collection("user")?.document(useruid)
                    ?.update("status", "delete")
                    ?.addOnSuccessListener {
                        // Post의 status 바꾸기
                        firestore?.collection("post")?.whereEqualTo("writer", useruid)?.get()
                            ?.addOnSuccessListener { document ->
                                if (document.size() != 0) {
                                    var postid = ""
                                    for (count: Int in 0..(document.size() - 1)) {
                                        var doc = document.documents?.get(count)
                                        postid = doc.get("postId").toString()
                                        firestore?.collection("post")?.document(postid)
                                            ?.update("status", "delete")
                                    }
                                }
                            }
                        // 냉장고(공유되는 냉장고) delete처리 해주기 내가 오너인거는 delete 멤버인거는 나가기처리
                        firestore?.collection("user")?.document(useruid)
                            ?.collection("myfridge")?.get()
                            ?.addOnSuccessListener { document ->
                                if (document.size() != 0) {
                                    for (count: Int in 0..(document.size() - 1)) {
                                        var doc = document.documents?.get(count)
                                        var fridgeid = doc.get("fridgeId").toString()
                                        var memcount = doc.get("member").toString().toInt()
                                        firestore?.collection("fridge")?.document(fridgeid)?.get()
                                            ?.addOnSuccessListener { data ->
                                                // 내가 owner인 냉장고라면
                                                // 냉장고 delete
                                                if (data?.get("owner").toString() == useruid) {
                                                    firestore?.collection("fridge")
                                                        ?.document(fridgeid)
                                                        ?.update("status", "delete")
                                                        ?.addOnSuccessListener { }
                                                        ?.addOnFailureListener { }
                                                    firestore?.collection("fridge")
                                                        ?.document(fridgeid)
                                                        ?.update("updatedAt", dateTime)
                                                        ?.addOnSuccessListener { }
                                                        ?.addOnFailureListener { }

                                                    firestore?.collection("fridge")
                                                        ?.document(fridgeid)
                                                        ?.collection("member")?.get()
                                                        ?.addOnSuccessListener { task ->
                                                            Toast.makeText(
                                                                this,
                                                                fridgeid,
                                                                Toast.LENGTH_SHORT
                                                            )
                                                            var membercount = task.size()
                                                            // 여기 잘 안되는거같음
                                                            if (membercount != 0) {
                                                                for (count: Int in 0..(membercount - 1)) {
                                                                    var doc =
                                                                        task.documents?.get(count)
                                                                    var memberuid =
                                                                        doc.get("userId").toString()
                                                                    firestore?.collection("user")
                                                                        ?.document(memberuid)
                                                                        ?.collection("myfridge")
                                                                        ?.document(fridgeid)
                                                                        ?.update("status", "delete")
                                                                        ?.addOnSuccessListener { }
                                                                        ?.addOnFailureListener { }
                                                                }
                                                            }
                                                        }
                                                }
                                                // 내가 owner가 아니면
                                                else {
                                                    firestore?.collection("fridge")
                                                        ?.document(fridgeid)?.collection("member")
                                                        ?.document(useruid)
                                                        ?.update("status", "delete")
                                                        ?.addOnSuccessListener { }
                                                        ?.addOnFailureListener { }
                                                    firestore?.collection("fridge")
                                                        ?.document(fridgeid)?.collection("member")
                                                        ?.document(useruid)
                                                        ?.update("updatedAt", dateTime)
                                                        ?.addOnSuccessListener { }
                                                        ?.addOnFailureListener { }
                                                    // member의 membercount 줄이기
                                                    firestore?.collection("fridge")
                                                        ?.document(fridgeid)
                                                        ?.collection("member")?.get()
                                                        ?.addOnSuccessListener { task ->
                                                            if (memcount > 2) {
                                                                for (count: Int in 0..(memcount - 2)) {
                                                                    var doc =
                                                                        task.documents?.get(count)
                                                                    var memberuid =
                                                                        doc.get("userId").toString()
                                                                    firestore?.collection("user")
                                                                        ?.document(memberuid)
                                                                        ?.collection("myfridge")
                                                                        ?.document(fridgeid)
                                                                        ?.update(
                                                                            "member",
                                                                            FieldValue.increment(-1)
                                                                        )
                                                                        ?.addOnSuccessListener { }
                                                                        ?.addOnFailureListener { }
                                                                }
                                                            }
                                                        }
                                                    // owner의 membercount 줄이기
                                                    firestore?.collection("fridge")
                                                        ?.document(fridgeid)?.get()
                                                        ?.addOnSuccessListener { document ->
                                                            if (document != null) {
                                                                // 해당하는 냉장고의 owner 받아오기
                                                                var owner =
                                                                    document.data?.get("owner")
                                                                        .toString()
                                                                firestore?.collection("user")
                                                                    ?.document(owner)
                                                                    ?.collection("myfridge")
                                                                    ?.document(fridgeid)
                                                                    ?.update(
                                                                        "member",
                                                                        FieldValue.increment(-1)
                                                                    )
                                                                    ?.addOnSuccessListener { }
                                                                    ?.addOnFailureListener { }
                                                            }
                                                        }
                                                }

                                            }
                                    }
                                }
                            }

                        // 유저의 status 바꾸기
                        firestore?.collection("user")?.document(useruid)
                            ?.update("nickname", "(탈퇴유저)")
                        firestore?.collection("user")?.document(useruid)
                            ?.update("updatedAt", dateTime)
                            ?.addOnSuccessListener {
//                                user?.delete()
                                auth?.signOut()     // 이거 안하면 계정이 걔속 남아있더라
                                user?.delete()
                                googleSigninClient!!.revokeAccess()
                                Toast.makeText(this, "회원탈퇴되셨습니다.", Toast.LENGTH_SHORT).show()
//                            val intent = Intent(this, AuthActivity::class.java)
//                            activity?.let { ContextCompat.startActivity(this, intent, null) }
                                startActivity(Intent(this, AuthActivity::class.java))
                                finish()
                            }
                    }
            }
        }

    }
}