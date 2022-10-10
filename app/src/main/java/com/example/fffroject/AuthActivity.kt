package com.example.fffroject

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import com.example.fffroject.databinding.ActivityAuthBinding
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.api.ApiException
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.GoogleAuthProvider
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.ktx.Firebase

class AuthActivity : AppCompatActivity() {
    lateinit var binding: ActivityAuthBinding

    var auth: FirebaseAuth? = null
    var firestore: FirebaseFirestore? = null

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
            //changeVisibility("login")
            startActivity(Intent(this, MainActivity::class.java))
        } else {
            //changeVisibility("logout")
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

                                Toast.makeText(
                                    this, "로그인 되었습니다.",
                                    Toast.LENGTH_SHORT
                                ).show()
                                var nickname = user?.email?.split("@")?.get(0)


//                                firestore?.collection("user")?.document(user.uid)
//                                    ?.set(hashMapOf("email" to user?.email, "uid" to user?.uid, "nickname" to nickname,
//                                        "contribution" to 0))


                                firestore?.collection("user")?.document(user?.uid)
                                    ?.get()?.addOnSuccessListener { snapShot ->
                                        if(snapShot.exists() == true) {
                                            Toast.makeText(this, "기존 유저입니다.", Toast.LENGTH_LONG).show()
                                        }
                                        else{
                                            firestore?.collection("user")?.document(user.uid)
                                                ?.set(hashMapOf("email" to user?.email, "uid" to user?.uid, "nickname" to nickname,
                                                    "contribution" to 0, "envlevel" to 0))
                                        }
                                    }

                            }
                            //changeVisibility("login")
                            moveMainPage(task.result?.user)
                        } else {
                            // 구글 로그인 실패
                            //changeVisibility("logout")
                        }
                    }
            } catch (e: ApiException) {
                //changeVisibility("logout")
            }
        }

//        binding.btnLogout.setOnClickListener {
//            // 구글 계정 로그아웃
//            Toast.makeText(
//                baseContext, "로그아웃 누름",
//                Toast.LENGTH_SHORT
//            ).show()
//            FFFroject.auth.signOut()
//            FFFroject.email = null
//            changeVisibility("logout")
//        }

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

    fun changeVisibility(mode: String) {
        if (mode === "login") {
            binding.run {
//                texLoginCheck.text = "${FFFroject.email} 님 안녕하세요!"
//                btnLogout.visibility = View.VISIBLE
                btnLoginGoogle.visibility = View.GONE
            }
        } else if (mode === "logout") {
            binding.run {
                //texLoginCheck.text = "로그인 해 주세요!"
                //btnLogout.visibility = View.GONE
                btnLoginGoogle.visibility = View.VISIBLE
            }
        }
    }

    // 로그인 성공 후 메인페이지로 이동
    fun moveMainPage(user:FirebaseUser?){
        if(user != null){
            startActivity(Intent(this, MainActivity::class.java))
            finish()
        }
    }
}