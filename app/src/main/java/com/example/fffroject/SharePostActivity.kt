package com.example.fffroject

import android.content.Intent
import android.graphics.BitmapFactory
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.View
import android.widget.EditText
import android.widget.ImageView
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.annotation.RequiresPermission
import androidx.appcompat.app.AppCompatActivity
import com.example.fffroject.databinding.ActivityMainBinding
import com.example.fffroject.databinding.ActivitySharepostBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.ktx.Firebase
import com.jakewharton.threetenabp.AndroidThreeTen
import java.io.InputStream
//import java.time.LocalDate
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import java.time.format.DateTimeFormatter.*
import java.util.*

import org.threeten.bp.LocalDate;
import org.threeten.bp.temporal.ChronoUnit;

class SharePostActivity : AppCompatActivity() {
    //파이어스토어
    var auth: FirebaseAuth? = null
    var db: FirebaseFirestore? = null
    var user: FirebaseUser? = null

    // 바인딩 객체
    lateinit var binding: ActivitySharepostBinding

    val REQ_GALLERY = 12

    // 파이어스토어 게시글 리스트
    lateinit var title : EditText
    lateinit var deadline : EditText
    lateinit var purchasedAt : EditText
    lateinit var name : EditText
    lateinit var region : EditText
    lateinit var location : EditText
    lateinit var context : EditText
    lateinit var imgFood : ImageView


    lateinit var postId: String
    lateinit var date: LocalDate
    lateinit var createdAt: String

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        //'ThreeTen' 백포트 사용
        AndroidThreeTen.init(this);

        // 바인딩 객체 획득
        binding = ActivitySharepostBinding.inflate(layoutInflater)
        // 액티비티 화면 출력
        setContentView(binding.root)

        // 파이어베이스 인증 객체
        auth = FirebaseAuth.getInstance()
        user = auth!!.currentUser
        // 파이어스토어 인스턴스 초기화
        db = FirebaseFirestore.getInstance()

        // 양식 작성 여부
        fun checkAllWritten(): Boolean{
            if (binding.title.length()>0 && binding.deadline.length()>0 && binding.purchasedAt.length()>0 && binding.name.length()>0
                && binding.region.length()>0 && binding.location.length()>0 && binding.context.length()>0){
                return true
            } else return false
        }

        // 완료버튼
        binding.btnSharepost.setOnClickListener(View.OnClickListener {
            if(checkAllWritten()==true){
                // editText -> string
                var title = binding.title.text.toString()
                var deadline = binding.deadline.text.toString()
                var purchasedAt = binding.purchasedAt.text.toString()
                var name = binding.name.text.toString()
                var region = binding.region.text.toString()
                var location = binding.location.text.toString()
                var context = binding.context.text.toString()
                // db 저장
                addPostDB(title, deadline, purchasedAt, name, region, location, context)
            } else{
                //양식 작성 안되어 있을 시
                val toast = Toast.makeText(this, "양식을 모두 작성해주세요", Toast.LENGTH_SHORT)
                toast.show()
            }
        })


        //이미지 불러오는 코드
        //gallery request launcher..................
        val requestGalleryLauncher = registerForActivityResult(
            ActivityResultContracts.StartActivityForResult())
        {
            try {
                // inSampleSize 비율 계산, 지정
                val calRatio = calculateInSampleSize(
                    it.data!!.data!!,
                    resources.getDimensionPixelSize(R.dimen.imgSize),
                    resources.getDimensionPixelSize(R.dimen.imgSize)
                )
                val option = BitmapFactory.Options()
                option.inSampleSize = calRatio
                // 이미지 로딩
                var inputStream = contentResolver.openInputStream(it.data!!.data!!)
                val bitmap = BitmapFactory.decodeStream(inputStream, null, option)
                inputStream!!.close()
                inputStream = null
                bitmap?.let {
                    binding.imgFood.setImageBitmap(bitmap)
                } ?: let {
                    Log.d("kkang", "bitmap null")
                }
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }

        // 앨범 버튼
        binding.btnCamera.setOnClickListener{
            // 갤러리 열기
            val intent = Intent(Intent.ACTION_PICK)
            intent.type = MediaStore.Images.Media.CONTENT_TYPE
            requestGalleryLauncher.launch(intent)
        }


    }

    // 데이터 저장
    private fun addPostDB(title: String, name: String, deadline: String, purchasedAt: String, region: String,
    location: String, context: String){
        //유저가 존재한다면
        if (user != null){
            postId = UUID.randomUUID().toString()
            //var date = LocalDateTime.now().format(ofPattern("yyyy-MM-dd HH:mm:ss"))
            //게시글 등록 날짜
            date = LocalDate.now()
            createdAt = date.toString()
            db?.collection("post")?.document("$postId")
                ?.set(
                    hashMapOf(
                        "index" to postId,
                        "title" to title,
                        "name" to name,
                        "deadline" to deadline,
                        "purchasedAt" to purchasedAt,
                        "region" to region,
                        "location" to location,
                        "context" to context,
                        "createdAt" to createdAt,
                        "flag" to "false",
                        "done" to "false"
                    )
                )
                ?.addOnSuccessListener {
                    val toast = Toast.makeText(this, "게시글 작성 완료", Toast.LENGTH_SHORT)
                    toast.show()
                }
                ?.addOnFailureListener {
                    val toast = Toast.makeText(this, "게시글 작성 실패", Toast.LENGTH_SHORT)
                    toast.show()
                }
        }
    }

    // 적절한 비율로 이미지 크기 줄이기
    private fun calculateInSampleSize(fileUri: Uri, reqWidth: Int, reqHeight: Int): Int {
        val options = BitmapFactory.Options()
        options.inJustDecodeBounds = true
        try {
            var inputStream = contentResolver.openInputStream(fileUri)

            //inJustDecodeBounds 값을 true 로 설정한 상태에서 decodeXXX() 를 호출.
            //로딩 하고자 하는 이미지의 각종 정보가 options 에 설정 된다.
            BitmapFactory.decodeStream(inputStream, null, options)
            inputStream!!.close()
            inputStream = null
        } catch (e: Exception) {
            e.printStackTrace()
        }
        //비율 계산........................
        val (height: Int, width: Int) = options.run { outHeight to outWidth }
        var inSampleSize = 1
        //inSampleSize 비율 계산
        if (height > reqHeight || width > reqWidth) {

            val halfHeight: Int = height / 2
            val halfWidth: Int = width / 2

            while (halfHeight / inSampleSize >= reqHeight && halfWidth / inSampleSize >= reqWidth) {
                inSampleSize *= 2
            }
        }
        return inSampleSize
    }

}