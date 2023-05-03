package com.example.fffroject

import android.content.ContentValues.TAG
import android.content.Intent
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.annotation.RequiresApi
import com.example.fffroject.databinding.ActivityAuthBinding
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.api.ApiException
import com.google.android.gms.tasks.OnCompleteListener
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.GoogleAuthProvider
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.ktx.Firebase
import com.google.firebase.messaging.FirebaseMessaging
import com.google.firebase.storage.FirebaseStorage
import java.time.LocalDate
import java.time.format.DateTimeFormatter

class AuthActivity : AppCompatActivity() {
    lateinit var binding: ActivityAuthBinding

    var auth: FirebaseAuth? = null
    var firestore: FirebaseFirestore? = null
//    var user: FirebaseUser? = null

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityAuthBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // 파이어스토어를 위한 Gradle 추가 및 import
        // 인스턴스 추가시 오류 없다면 잘 된 것
        // 파이어스토어 인스턴스 초기화
        auth = FirebaseAuth.getInstance()
        firestore = FirebaseFirestore.getInstance()

        if (FFFroject.checkAuth()) {
            startActivity(Intent(this, MainActivity::class.java))
            finish()
        } else {

        }

        val requestLauncher = registerForActivityResult(
            ActivityResultContracts.StartActivityForResult()
        )
        {
            // 구글 로그인 결과 처리
            val task = GoogleSignIn.getSignedInAccountFromIntent(it.data)
            try {
                val account = task.getResult(ApiException::class.java)!!
                val credential = GoogleAuthProvider
                    .getCredential(account.idToken, null)
                FFFroject.auth.signInWithCredential(credential)
                    .addOnCompleteListener(this) { task ->
                        if (task.isSuccessful) {
                            // 구글 로그인 성공
                            FFFroject.email = account.email

                            // 민영 추가(파이어스토어 연동 시도)
                            var user = auth!!.currentUser

                            if (user != null) {
                                var nickname = user?.email?.split("@")?.get(0)

                                firestore?.collection("user")?.document(user?.uid)
                                    ?.get()?.addOnSuccessListener { snapShot ->
                                        // 기존 유저일 경우
                                        if(snapShot.exists() == true) {
                                            Toast.makeText(this, "로그인 되었습니다.", Toast.LENGTH_LONG).show()
                                        }
                                        // 신규 유저일 경우
                                        else{
                                            getSetFCMToken(user)    // fcm 토큰 발급받고 저장하기
                                            var nowdate = LocalDate.now().format(DateTimeFormatter.ofPattern("yyyy.MM.dd"))
                                            firestore?.collection("user")?.document(user.uid)
                                                ?.set(hashMapOf("email" to user?.email, "userId" to user?.uid, "nickname" to nickname,
                                                    "eatCount" to 0, "envlevel" to 1,"sharepoint" to 0,
                                                    "nowRegion" to "n", "loginDate" to nowdate, "createdAt" to nowdate, "updatedAt" to nowdate,
                                                    "status" to "active"))
                                            Toast.makeText(this, "회원가입 되었습니다.", Toast.LENGTH_LONG).show()
                                        }
                                    }
                            }
                            moveMainPage(task.result?.user)
                        } else {
                            // 구글 로그인 실패
                        }
                    }
            } catch (e: ApiException) {
            }
        }

        binding.btnLoginGoogle.setOnClickListener {
            // 구글 로그인
            val loginGoogle = GoogleSignInOptions
                .Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.google_client_id))   // default_web_client_id 인데 오류발생(이미사용되고있어서)
                // strings.xml에서 새로 선언 후 json의 oatuh에서 client id 옆부분을 넣어서 바꿔줌 -> 오류 안남
                .requestEmail()
                .build()
            // 구글의 인증 관리 앱 실행
            val signInIntent = GoogleSignIn.getClient(this, loginGoogle).signInIntent
            requestLauncher.launch(signInIntent)
        }

    }

    // 로그인 성공 후 메인페이지로 이동
    fun moveMainPage(user:FirebaseUser?){
        if(user != null){
            startActivity(Intent(this, MainActivity::class.java))
            finish()
        }
    }

    // [minjeong] fcm 토큰 가져오고 db에 저장하기
    private fun getSetFCMToken(user:FirebaseUser?): String?{
        var token: String? = null
        FirebaseMessaging.getInstance().token.addOnCompleteListener(OnCompleteListener { task ->
            // fcm 토큰 발급받기
            if (!task.isSuccessful) {
                return@OnCompleteListener
            }

            // Get new FCM registration token
            token = task.result

            // fcm 토큰 db에 저장하기
            firestore?.collection("user")?.document(user?.uid.toString())
                ?.update("fcmToken", token)
                ?.addOnSuccessListener {
                }
        })

        return token
    }
}