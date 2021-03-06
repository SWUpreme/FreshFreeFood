package com.example.fffroject

import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.net.Uri
import android.os.Bundle
import android.os.Environment
import android.provider.MediaStore
import android.util.Log
import android.view.MenuItem
import android.view.View
import android.widget.EditText
import android.widget.ImageView
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.core.content.FileProvider
import androidx.fragment.app.Fragment
import com.example.fffroject.databinding.ActivitySharepostBinding
import com.example.fffroject.databinding.DialogAddimageBinding
import com.example.fffroject.fragment.ShareFragment
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import com.google.firebase.storage.UploadTask
import com.jakewharton.threetenabp.AndroidThreeTen
import java.util.*
import org.threeten.bp.LocalDate;
import org.threeten.bp.LocalDateTime
import org.threeten.bp.format.DateTimeFormatter
import org.threeten.bp.format.DateTimeFormatter.*
import java.io.ByteArrayOutputStream
import java.io.File
import java.text.SimpleDateFormat

class SharePostActivity : AppCompatActivity() {
    //파이어스토어
    var auth: FirebaseAuth? = null
    var db: FirebaseFirestore? = null
    var user: FirebaseUser? = null
    var storage: FirebaseStorage? = null

    lateinit var bitmap: Bitmap

    // 바인딩 객체
    lateinit var binding: ActivitySharepostBinding
    lateinit var filePath: String

    // 파이어스토어 게시글 리스트
    lateinit var title : EditText
    lateinit var deadline : EditText
    lateinit var purchasedAt : EditText
    lateinit var name : EditText
    lateinit var region : EditText
    lateinit var location : EditText
    lateinit var context : EditText
    lateinit var imgFood : ImageView

    // 툴바
    lateinit var toolbar_sharepost: Toolbar


    lateinit var postId: String
    lateinit var nowdate: LocalDate
    lateinit var date : String
    lateinit var createdAt: String

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        //'ThreeTen' 백포트 사용
        AndroidThreeTen.init(this);

        // 바인딩 객체 획득
        binding = ActivitySharepostBinding.inflate(layoutInflater)

        // 액티비티 화면 출력
        setContentView(binding.root)

        // 툴바 뒤로가기 버튼 활성화
//        toolbar_sharepost = findViewById(R.id.toolbSharepostUpload)
//        setSupportActionBar(toolbar_sharepost)
//        supportActionBar?.setDisplayHomeAsUpEnabled(true)      // 뒤로가기 버튼 default로 만들기
//        supportActionBar?.setHomeAsUpIndicator(R.drawable.ic_x_rec)
//        supportActionBar?.setDisplayShowTitleEnabled(false)    // 기본 타이틀 숨기기

//        getSupportActionBar()?.setDisplayHomeAsUpEnabled(true)      // 뒤로가기 버튼 default로 만들기
//        getSupportActionBar()?.setHomeAsUpIndicator(R.drawable.ic_x_rec)
//        getSupportActionBar()?.setDisplayShowTitleEnabled(false)    // 기본 타이틀 숨기기


        // 파이어베이스 인증 객체
        auth = FirebaseAuth.getInstance()
        user = auth!!.currentUser
        // 파이어스토어 인스턴스 초기화
        db = FirebaseFirestore.getInstance()
        // 파이어스토리지 인스턴스 초기화
        storage = FirebaseStorage.getInstance()

        // 상단 툴바 사용
        toolbar_sharepost = findViewById(R.id.toolbSharepostUpload)

        // 완료버튼-post db 저장
        toolbar_sharepost.setOnMenuItemClickListener {
            when(it.itemId) {
                R.id.btnPostUpload -> {
                    if(checkAllWritten()){
                        // editText -> string
                        var title = binding.title.text.toString()
                        var deadline = binding.deadlineYear.text.toString()+"."+binding.deadlineMonth.text.toString()+"."+binding.deadlineDate.text.toString()
                        var purchasedAt = binding.purchasedAtYear.text.toString()+"."+ binding.purchasedAtMonth.text.toString()+"."+ binding.purchasedAtDate.text.toString()
                        var name = binding.name.text.toString()
                        var region = binding.region.text.toString()
                        var location = binding.location.text.toString()
                        var content = binding.context.text.toString()
                        // db 저장
                        addPostDB(title, deadline, purchasedAt, name, region, location, content)
                        // 전체 게시글로 되돌아가기
                        finish()
                    } else {
                        //양식 작성 안되어 있을 시
                        val toast = Toast.makeText(this, "양식을 모두 작성해주세요", Toast.LENGTH_SHORT)
                        toast.show()
                    }
                    true
                }
                else -> false
            }
        }

        // 이미지 삽입 버튼
        binding.btnCamera.setOnClickListener{
            showDialogAddimage()
        }

    }

    // 스토리지에 이미지 업로드
    private fun uploadImage(postId: String){

        // 스토리지를 참조하는 StorageReference 생성
        val storageRef: StorageReference? = storage?.reference
        // 실제 업로드하는 파일을 참조하는 StorageReference 생성
        val imgRef: StorageReference? = storageRef?.child("images/${postId}.jpg")

        // 이미지를 바이트값으로 읽기
        val baos = ByteArrayOutputStream()
        bitmap?.compress(Bitmap.CompressFormat.JPEG, 100, baos)
        val data = baos.toByteArray()

        //바이트값을 스토리지에 저장하기
        var UploadTask = imgRef?.putBytes(data)
        UploadTask?.addOnFailureListener{
            Log.d("error", "upload fail....")
        }?.addOnCompleteListener{
            Log.d("error", "upload success....")
        }


//        // 스토리지를 참조하는 StorageReference 생성
//        val storageRef: StorageReference? = storage?.reference
//        // 실제 업로드하는 파일을 참조하는 StorageReference 생성
//        val imgRef: StorageReference? = storageRef?.child("images/${postId}.jpg")
//        // 파일 업로드
//        var file = Uri.fromFile(File(filePath))
//        imgRef?.putFile(file)
//            ?.addOnFailureListener {
//                Log.d("storage"," failure............."+it)
//            }?.addOnSuccessListener {
//                Log.d("storage"," Success............."+it)
//                finish()
//            }
    }

    // 양식 작성 여부 확인
    private fun checkAllWritten(): Boolean{
        return (binding.title.length()>0 && binding.deadlineYear.length()>0 && binding.deadlineMonth.length()>0 && binding.deadlineDate.length()>0
                && binding.purchasedAtYear.length()>0 && binding.purchasedAtMonth.length()>0 && binding.purchasedAtDate.length()>0
                && binding.name.length()>0 && binding.region.length()>0 && binding.location.length()>0 && binding.context.length()>0)
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
            bitmap = BitmapFactory.decodeStream(inputStream, null, option)!!
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
        bitmap = BitmapFactory.decodeFile(filePath, option)!!
        bitmap?.let {
            binding.imgFood.setImageBitmap(bitmap)
            binding.imgFood.visibility = View.VISIBLE
        }
    }

    // 데이터 저장
    private fun addPostDB(title: String, purchasedAt: String, deadline: String, name: String, region: String,
    location: String, content: String){
        //유저가 존재한다면
        if (user != null){
            postId = UUID.randomUUID().toString()
            //게시글 등록 날짜
            nowdate = LocalDate.now()
            date = nowdate.format(ofPattern("yyyy.MM.dd"))
            createdAt = date.toString()

//            nowDateTime = LocalDateTime.now()
//            val formatter = DateTimeFormatter.ISO_DATE
//            dateTime = nowDateTime.format(formatter)
            val nowTime = System.currentTimeMillis()
            val timeformatter = SimpleDateFormat("yyyy.MM.dd.hh.mm")
            val dateTime = timeformatter.format(nowTime)

            //db 전송
            db?.collection("post")?.document("$postId")
                ?.set(
                    hashMapOf(
                        "index" to postId,
                        "writer" to user?.uid,
                        "title" to title,
                        "name" to name,
                        "deadline" to deadline,
                        "purchasedAt" to purchasedAt,
                        "region" to region,
                        "location" to location,
                        "content" to content,
                        "createdAt" to createdAt,
                        "dateTime" to dateTime,
                        "flag" to false,
                        "done" to false
                    )
                )
                ?.addOnSuccessListener {
                    //  스토리지에 데이터 저장 후 postId값으로 스토리지에 이미지 업로드
                    uploadImage(postId)
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

    // 뒤로가기 버튼 클릭
    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        val id = item.itemId
        when (id) {
            android.R.id.home -> {      //툴바 백키 동작
                finish()
                return true
            }
        }
        return super.onOptionsItemSelected(item)
    }

}