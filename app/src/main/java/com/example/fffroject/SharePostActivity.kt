package com.example.fffroject

import android.content.Context
import android.content.Intent
import android.graphics.BitmapFactory
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.net.Uri
import android.os.Bundle
import android.os.Environment
import android.provider.MediaStore
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.KeyEvent
import android.view.LayoutInflater
import android.view.View
import android.widget.EditText
import android.widget.ImageView
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.FileProvider
import androidx.core.widget.addTextChangedListener
import com.example.fffroject.databinding.ActivitySharepostBinding
import com.example.fffroject.databinding.DialogAddimageBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.firestore.FirebaseFirestore
import com.jakewharton.threetenabp.AndroidThreeTen
import java.util.*
import org.threeten.bp.LocalDate;
import org.threeten.bp.temporal.ChronoUnit;
import java.io.File
import java.text.SimpleDateFormat

class SharePostActivity : AppCompatActivity() {
    //파이어스토어
    var auth: FirebaseAuth? = null
    var db: FirebaseFirestore? = null
    var user: FirebaseUser? = null

    // 바인딩 객체
    lateinit var binding: ActivitySharepostBinding

    lateinit var filePath: String

    var eraseHyphen : Boolean = false

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

        // 완료버튼-post db 저장
        binding.btnSharepost.setOnClickListener(View.OnClickListener {
            if(checkAllWritten()){
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
            } else {
                //양식 작성 안되어 있을 시
                val toast = Toast.makeText(this, "양식을 모두 작성해주세요", Toast.LENGTH_SHORT)
                toast.show()
            }
        })

        //하이픈 자동 입력
        addAutoHyphen()

        // 이미지 삽입 버튼
        binding.btnCamera.setOnClickListener{
            showDialogAddimage()
        }

    }
    //Edittext Hyphen(-) 자동 입력
    private fun addAutoHyphen(){
        binding.deadline.addTextChangedListener(object : TextWatcher{
            var textlength = 0
            override fun afterTextChanged(s: Editable?) {

            }
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
            }
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
//                var textlength = 0
//                if(binding.deadline.isFocusable() && !s.toString().equals("")){
//                    try {
//                        textlength = binding.deadline.text.toString().length
//                    } catch (e : NumberFormatException){
//                        e.printStackTrace()
//                        return
//                    }
//
//                    if(textlength == 4 && before != 1){
//                        binding.deadline.setText(binding.deadline.text.toString()+"-")
//                        binding.deadline.setSelection(binding.deadline.text.length)
//                    }else if (textlength == 7 && before != 1){
//                        binding.deadline.setText(binding.deadline.text.toString()+"-")
//                        binding.deadline.setSelection(binding.deadline.text.length)
//                    }else if (textlength == 5 && !binding.deadline.text.toString().contains("-")){
//                        binding.deadline.setText(binding.deadline.text.toString().substring(0,4)+"-"+binding.deadline.text.toString().substring(4))
//                        binding.deadline.setSelection(binding.deadline.text.length)
//                    }else if (textlength == 8 && !binding.deadline.text.toString().substring(7,8).equals(".")){
//                        binding.deadline.setText(binding.deadline.text.toString().substring(0,7)+"-"+binding.deadline.text.toString().substring(7))
//                        binding.deadline.setSelection(binding.deadline.text.length)
//                    }
//                }


            }
        })
    }



    // 양식 작성 여부 확인
    private fun checkAllWritten(): Boolean{
        return (binding.title.length()>0 && binding.deadline.length()>0 && binding.purchasedAt.length()>0 && binding.name.length()>0
                && binding.region.length()>0 && binding.location.length()>0 && binding.context.length()>0)
    }

    // 사진 삽입 dialog를 디자인
    private fun showDialogAddimage() {
        //뷰 바인딩을 적용한 XML 파일 초기화
        val dialogBinding = DialogAddimageBinding.inflate(layoutInflater)
        val alertDialog =AlertDialog.Builder(this).run {
            setView(dialogBinding.root)
            show()
        }//.setCanceledOnTouchOutside(true)  //외부 터치시 닫기

        //배경 투명으로 지정(모서리 둥근 배경 보이게 하기)
        alertDialog.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))

        //닫기 버튼
        dialogBinding.btnClose.setOnClickListener(View.OnClickListener {
            alertDialog.dismiss()
        })

        //카메라 버튼
        dialogBinding.btnCameraOn.setOnClickListener{
            // 카메라 앱
            // 파일 준비
            val timeStamp: String =
                SimpleDateFormat("yyyyMMdd_HHmmss").format(Date())
            val storageDir: File? =
                getExternalFilesDir(Environment.DIRECTORY_PICTURES)
            val file = File.createTempFile(
                "JPEG_${timeStamp}_",
                ".jpg",
                storageDir
            )
            filePath = file.absolutePath
            val photoURI: Uri = FileProvider.getUriForFile(
                this,
                "com.example.fffroject.fileprovider", file
            )
            val intent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
            intent.putExtra(MediaStore.EXTRA_OUTPUT, photoURI);
            requestCameraFileLauncher.launch(intent)
            alertDialog.dismiss()
        }

        //앨범 버튼
        dialogBinding.btnAlbum.setOnClickListener{
            // 갤러리 열기
            val intent = Intent(Intent.ACTION_PICK)
            intent.type = MediaStore.Images.Media.CONTENT_TYPE
            requestGalleryLauncher.launch(intent)
            alertDialog.dismiss()
        }

    }

    //이미지 불러오기(gallery request launcher)
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
                binding.imgFood.visibility = View.VISIBLE
            } ?: let {
                Log.d("kkang", "bitmap null")
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    //카메라 불러오기(camera request launcher)
    val requestCameraFileLauncher = registerForActivityResult(
        ActivityResultContracts.StartActivityForResult())
    {
        // 카메라 앱
        val calRatio = calculateInSampleSize(
            Uri.fromFile(File(filePath)),
            resources.getDimensionPixelSize(R.dimen.imgSize),
            resources.getDimensionPixelSize(R.dimen.imgSize)
        )
        val option = BitmapFactory.Options()
        option.inSampleSize = calRatio
        val bitmap = BitmapFactory.decodeFile(filePath, option)
        bitmap?.let {
            binding.imgFood.setImageBitmap(bitmap)
            binding.imgFood.visibility = View.VISIBLE
        }
    }

    // 데이터 저장
    private fun addPostDB(title: String, name: String, deadline: String, purchasedAt: String, region: String,
    location: String, context: String){
        //유저가 존재한다면
        if (user != null){
            postId = UUID.randomUUID().toString()
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