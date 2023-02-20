package com.example.fffroject

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Environment
import android.provider.MediaStore
import android.provider.Settings
import android.util.Log
import android.view.MenuItem
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.widget.Toolbar
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.core.content.FileProvider
import com.example.fffroject.databinding.ActivityFoodlistToShareBinding
import com.example.fffroject.databinding.DialogAddimageBinding
import com.google.android.material.snackbar.Snackbar
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import com.jakewharton.threetenabp.AndroidThreeTen
import org.threeten.bp.LocalDate
import org.threeten.bp.format.DateTimeFormatter
import java.io.ByteArrayOutputStream
import java.io.File
import java.text.SimpleDateFormat
import java.util.*

class ShareUpdateActivity : AppCompatActivity() {
    //파이어스토어
    var auth: FirebaseAuth? = null
    var db: FirebaseFirestore? = null
    var user: FirebaseUser? = null
    var storage: FirebaseStorage? = null

    lateinit var bitmap: Bitmap

    // 바인딩 객체
    lateinit var binding: ActivityFoodlistToShareBinding
    lateinit var filePath: String

    // 파이어스토어 게시글 리스트
    lateinit var title : EditText
    lateinit var deadline : EditText
    lateinit var purchasedAt : EditText
    lateinit var name : EditText
    lateinit var region : Button
    lateinit var location : EditText
    lateinit var context : EditText
    lateinit var imgFood : ImageView

    // 툴바
    lateinit var toolbar_sharepost: Toolbar

    lateinit var postId: String
    lateinit var nowdate: LocalDate
    lateinit var date : String
    lateinit var postedAt: String

    lateinit var indexIntent: String
    var isImageChange: Boolean = false      // 이미지 변환 확인 변수

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_share_update)
        //'ThreeTen' 백포트 사용
        AndroidThreeTen.init(this);

        // 바인딩 객체 획득
        binding = ActivityFoodlistToShareBinding.inflate(layoutInflater)
        // 액티비티 화면 출력
        setContentView(binding.root)
        // 파이어베이스 인증 객체
        auth = FirebaseAuth.getInstance()
        user = auth!!.currentUser
        // 파이어스토어 인스턴스 초기화
        db = FirebaseFirestore.getInstance()
        // 파이어스토리지 인스턴스 초기화
        storage = FirebaseStorage.getInstance()

        // MyShareActivity Intent 연결
        indexIntent = intent.getStringExtra("postId")!!

        // 상단 툴바 사용
        toolbar_sharepost = findViewById(R.id.toolbSharepostUpload)

        // 지역설정 버튼
        binding.region.setOnClickListener(){
            val intent = Intent(this, RegionSelectActivity::class.java)
            startForResult.launch(intent)
        }

        // 완료버튼-post db 저장
        toolbar_sharepost.setOnMenuItemClickListener {
            when(it.itemId) {
                R.id.btnPostUpload -> {
                    if(binding.imgFood.visibility == View.VISIBLE){
                        // 업로드된 이미지 있을 시
                        if(checkAllWritten()){
                            // 유효한 날짜-년이 맞을 시
                            if(checkValidYear()){
                                // 유효한 날짜-월이 맞을 시
                                if(checkValidMonth()){
                                    // 유효한 날짜-일이 맞을 시
                                    if(checkValidDate()){
                                        // editText -> string
                                        var title = binding.title.text.toString()
                                        var deadline = binding.deadlineYear.text.toString()+"."+binding.deadlineMonth.text.toString()+"."+binding.deadlineDate.text.toString()
                                        var purchasedAt = binding.purchasedAtYear.text.toString()+"."+ binding.purchasedAtMonth.text.toString()+"."+ binding.purchasedAtDate.text.toString()
                                        var foodName = binding.name.text.toString()
                                        var region = binding.region.text.toString()
                                        var location = binding.location.text.toString()
                                        var content = binding.context.text.toString()
                                        // db 저장
                                        addPostDB(title, deadline, purchasedAt, foodName, region, location, content)
                                        // 전체 게시글로 되돌아가기
                                        finish()
                                    }else{
                                        // 유효한 날짜-일이 아닐 시
                                        Toast.makeText(this, "1~31일 사이의 값을 입력해주세요.", Toast.LENGTH_SHORT).show()
                                    }
                                }else{
                                    // 유효한 날짜-월이 아닐 시
                                    Toast.makeText(this, "1~12월 사이의 값을 입력해주세요.", Toast.LENGTH_SHORT).show()
                                }
                            }else{
                                // 유효한 날짜-년이 아닐 시
                                Toast.makeText(this, "정확한 년도를 입력해주세요.", Toast.LENGTH_SHORT).show()
                            }
                        } else {
                            //양식 작성 안되어 있을 시
                            val toast = Toast.makeText(this, "양식을 모두 작성해 주세요.", Toast.LENGTH_SHORT)
                            toast.show()
                        }
                    }else{
                        // 업로드된 이미지 없을 시
                        Toast.makeText(this, "사진을 업로드해 주세요.", Toast.LENGTH_SHORT).show()
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

        loadData()      // db에서 수정할 데이터 불러오기

    }

    // db에서 수정할 데이터 불러오기
    private fun loadData(){
        // 지역버튼에 유저DB의 현재 지정한 지역 가져오기
        if(user != null) {
            // 선택한 포스트 조회
            db?.collection("post")?.document(indexIntent)?.get()
                ?.addOnSuccessListener { value ->
                    // 분리할 날짜 데이터 가져오기
                    var deadline = value.data?.get("deadline") as String
                    var purchasedAt = value.data?.get("purchasedAt") as String
                    // 조회한 데이터 넣기
                    binding.title.setText(value.data?.get("title") as String)                 // 타이틀
                    binding.deadlineYear.setText(deadline.split('.')[0])            // 유통기한
                    binding.deadlineMonth.setText(deadline.split('.')[1])
                    binding.deadlineDate.setText(deadline.split('.')[2])
                    binding.purchasedAtYear.setText(purchasedAt.split('.')[0])      // 구매일
                    binding.purchasedAtMonth.setText(purchasedAt.split('.')[1])
                    binding.purchasedAtDate.setText(purchasedAt.split('.')[2])
                    binding.name.setText(value.data?.get("foodName") as String)                   // 상품명
                    binding.region.setText(value.data?.get("region") as String)               // 지역
                    binding.location.setText(value.data?.get("location") as String)           // 거래희망위치
                    binding.context.setText(value.data?.get("content") as String)             // 게시글내용
                }
            downloadImage(indexIntent)

        }
    }

    // 콜백 받는 부분
    private val startForResult = registerForActivityResult(ActivityResultContracts.StartActivityForResult()){
        // RegionSelectActivity로부터 결과값을 이곳으로 전달
        if (it.resultCode == Activity.RESULT_OK) {
            // 콜백으로 받아온 데이터가 있따면
            if(it.data != null){
                var regionData : String? = it.data!!.getStringExtra("data")
                // 지역 설정 버튼 텍스트로 저장
                binding.region.text = regionData
                // 유저db-현재 설정된 지역 수정
                db?.collection("user")?.document(user?.uid.toString())
                    ?.update("nowRegion", regionData)
                    ?.addOnSuccessListener {}
            }
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
    }

    // 날짜 입력 년 유효값 확인
    private fun checkValidYear(): Boolean{
        return(Integer.parseInt(binding.deadlineYear.text.toString())>2020 && Integer.parseInt(binding.deadlineYear.text.toString())<=2299
                && Integer.parseInt(binding.purchasedAtYear.text.toString())>2020 && Integer.parseInt(binding.purchasedAtYear.text.toString())<=2299)
    }

    // 날짜 입력 월 유효값 확인
    private fun checkValidMonth(): Boolean{
        return (Integer.parseInt(binding.deadlineMonth.text.toString())>0 && Integer.parseInt(binding.deadlineMonth.text.toString())<=12
                && Integer.parseInt(binding.purchasedAtMonth.text.toString())>0 && Integer.parseInt(binding.purchasedAtMonth.text.toString())<=12)
    }

    // 날짜 입력 일 유효값 확인
    private fun checkValidDate(): Boolean{
        return (Integer.parseInt(binding.deadlineDate.text.toString())>0 && Integer.parseInt(binding.deadlineDate.text.toString())<=31
                && Integer.parseInt(binding.purchasedAtDate.text.toString())>0 && Integer.parseInt(binding.purchasedAtDate.text.toString())<=31)
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
        val alertDialog = AlertDialog.Builder(this).run {
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
            // 카메라 권한 승인했다면
            if (isCameraPermissionGranted(alertDialog)){
                // 카메라 기능 실행
                startCameraProcess(alertDialog)
            }
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

    // 카메라 권한 처리
    private fun isCameraPermissionGranted(alertDialog: AlertDialog): Boolean{
        val preference = getPreferences(Context.MODE_PRIVATE)
        val isFirstCheck = preference.getBoolean("isFirstPermissionCheck", true)

        if (ContextCompat.checkSelfPermission(this, android.Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
            // 권한이 거절된 상태
            if (ActivityCompat.shouldShowRequestPermissionRationale(this, android.Manifest.permission.CAMERA)) {
                // 1. 사용자가 승인 거절을 누른경우
                // 사용자에게 왜 필요한지 이유를 설명해주는게 좋음
                val snackBar = Snackbar.make(binding.sharepostConst, "촬영을 위해서 카메라 권한이 필요합니다.", Snackbar.LENGTH_INDEFINITE)
                snackBar.setAction("권한승인") {
                    ActivityCompat.requestPermissions(this, arrayOf(android.Manifest.permission.CAMERA), 1)
                }
                snackBar.show()
            } else {
                if (isFirstCheck){
                    // 2. 혹은 아직 승인요청을 한적이 없는 경우
                    // 처음 물었는지 여부를 저장
                    preference.edit().putBoolean("isFirstPermissionCheck", false).apply()
                    // 권한요청
                    ActivityCompat.requestPermissions(this, arrayOf(android.Manifest.permission.CAMERA), 1)
                }else{
                    // 3. 사용자가 승인 거절과 동시에 다시 표시하지 않기 옵션을 선택한 경우
                    // requestPermission을 요청해도 창이 나타나지 않기 때문에 설정창으로 이동
                    val snackBar = Snackbar.make(binding.sharepostConst, "카메라 권한이 필요합니다. 확인을 누르면 설정 화면으로 이동합니다.", Snackbar.LENGTH_INDEFINITE)
                    snackBar.setAction("확인") {
                        val intent = Intent()
                        intent.action = Settings.ACTION_APPLICATION_DETAILS_SETTINGS
                        val uri = Uri.fromParts("package", packageName, null)
                        intent.data = uri
                        startActivity(intent)
                    }
                    snackBar.show()
                    alertDialog.dismiss()
                }
            }
            return false
        } else {
            // 4. 권한이 승인된 상태
            return true
        }
    }

    // 카메라 기능 실행(권한 있을 시 카메라 버튼 터치)
    private fun startCameraProcess(alertDialog: AlertDialog){
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
            // 이미지 변경 변수 false -> true
            isImageChange = true
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
        isImageChange = true
    }

    // 데이터 저장
    private fun addPostDB(title: String, purchasedAt: String, deadline: String, foodName: String, region: String,
                          location: String, content: String){
        //유저가 존재한다면
        if (user != null){
            postId = indexIntent
            //게시글 등록 날짜
            nowdate = LocalDate.now()
            date = nowdate.format(DateTimeFormatter.ofPattern("yyyy.MM.dd"))
            postedAt = date.toString()

            val nowTime = System.currentTimeMillis()
            val timeformatter = SimpleDateFormat("yyyy.MM.dd.HH.mm.ss")
            val dateTime = timeformatter.format(nowTime)

            // 변경 db 내용
            var map= mutableMapOf<String,Any>()
            map["postId"] = postId
            map["title"] = title
            map["foodName"] = foodName
            map["deadline"] = deadline
            map["purchasedAt"] = purchasedAt
            map["region"] = region
            map["location"] = location
            map["content"] = content
            map["postedAt"] = postedAt
            map["updatedAt"] = dateTime

            //db 전송
            db?.collection("post")?.document("$postId")
                ?.update(map)
                ?.addOnSuccessListener {
                    //  스토리지에 데이터 저장 후 postId값으로 스토리지에 이미지 업로드
                    if(isImageChange){
                        uploadImage(postId)
                    }
                    val toast = Toast.makeText(this, "게시글 수정 완료", Toast.LENGTH_SHORT)
                    toast.show()
                }
                ?.addOnFailureListener {
                    val toast = Toast.makeText(this, "게시글 수정 실패", Toast.LENGTH_SHORT)
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

    // 스토리지에서 이미지 다운로드
    private fun downloadImage(imgId: String){
        // 스토리지를 참조하는 StorageReference 생성
        val storageRef: StorageReference? = storage?.reference
        // 실제 업로드하는 파일을 참조하는 StorageReference 생성
        val imgRef: StorageReference? = storageRef?.child("images/${imgId}.jpg")
        val ONE_MEGABYTE: Long = 1024*1024
        imgRef?.getBytes(ONE_MEGABYTE)
            ?.addOnSuccessListener {
                val bitmap = BitmapFactory.decodeByteArray(it, 0, it.size)
                binding.imgFood.setImageBitmap(bitmap)
            }?.addOnFailureListener{
                Log.d("download", "fail")
            }
        binding.imgFood.visibility = View.VISIBLE
    }
}