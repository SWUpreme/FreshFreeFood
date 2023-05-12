package com.example.fffroject.share

import android.graphics.BitmapFactory
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.core.content.ContextCompat
import com.example.fffroject.R
import com.example.fffroject.chat.ChatActivity
//import com.example.directmessage.ChatActivity
import com.example.fffroject.databinding.ActivitySharedetailBinding
import com.example.fffroject.fragment.PostDetail
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import kotlinx.android.synthetic.main.activity_sharedetail.*

// 상세 나눔
class ShareDetailActivity: AppCompatActivity() {

    val TAG: String = "로그"

    //파이어스토어
    var auth: FirebaseAuth? = null
    var db: FirebaseFirestore? = null
    var user: FirebaseUser? = null
    var storage: FirebaseStorage? = null

    // 바인딩 객체
    lateinit var binding: ActivitySharedetailBinding

    // 툴바
    lateinit var toolbar_sharedetail: Toolbar

    // 화면 구성 내용
    lateinit var postId: String
    lateinit var writer: String
    lateinit var fridgeToss: String

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        // 바인딩 객체 획득
        binding = ActivitySharedetailBinding.inflate(layoutInflater)
        // 액티비티 화면 출력
        setContentView(binding.root)

        // 파이어베이스 인증 객체
        auth = FirebaseAuth.getInstance()
        user = auth!!.currentUser
        // 파이어스토어 인스턴스 초기화
        db = FirebaseFirestore.getInstance()
        // 파이어스토리지 인스턴스 초기화
        storage = FirebaseStorage.getInstance()
        // 상단 툴바 사용
        toolbar_sharedetail = findViewById(R.id.toolbSharedetail)

        // ShareFragment Intent 연결
        Log.d("intent 성공:", "${intent.hasExtra("detailWriter")}")

        postId = intent.getStringExtra("detailIndex").orEmpty()    // 게시글 인덱스
        writer = intent.getStringExtra("detailWriter").orEmpty()    // 게시글 작성자
        fridgeToss = intent.getStringExtra("detailFlag").orEmpty()    // 게시글 냉장고 넘김 여부
        Log.d("postId 성공:", "${postId}")
        Log.d("writer 성공:", "${writer}")
        Log.d("fridgeToss 성공:", "${fridgeToss}")

        // 메세지 버튼
        toolbSharedetail.setOnMenuItemClickListener {
            when (it.itemId) {
                R.id.btnGotoMessage -> {
                    // 본인 글일 시 , 메시지 버튼 숨기기
                    if (user != null) {
                        var userString = user?.uid
                        if (userString.equals(writer)) {
                            // 내가 작성한 글이라면
                            Log.d("user", "it's mine")
                            Toast.makeText(this, "자신이 작성한 나눔글입니다.", Toast.LENGTH_SHORT).show()
                            //btnGotoMessage.setVisibility(View.GONE)
                        } else {
                            val intent = Intent(applicationContext, ChatActivity::class.java)
                            intent.putExtra("detailIndex", postId)
                            intent.putExtra("detailWriter", writer)
                            startActivity(intent)
                            Log.d("user", "it's not mine")
                        }
                    }
                    true
                }
                else -> false
            }
        }

        // 냉장고에서 넘기기 여부 확인 후 색상 변경
        if (fridgeToss == "true") {
            binding.detailRegion.setBackgroundResource(R.drawable.txt_background_round2_blue)
            binding.detailLocation.setBackgroundResource(R.drawable.txt_background_round2_blue)
            binding.detailRegion.setTextColor(ContextCompat.getColor(this, R.color.white))
            binding.detailLocation.setTextColor(ContextCompat.getColor(this, R.color.white))
        } else {
            binding.detailRegion.setBackgroundResource(R.drawable.txt_background_round2_white)
            binding.detailLocation.setBackgroundResource(R.drawable.txt_background_round2_white)
            binding.detailRegion.setTextColor(ContextCompat.getColor(this, R.color.blueblack))
            binding.detailLocation.setTextColor(ContextCompat.getColor(this, R.color.blueblack))
        }

        // 세부 게시글 내용 불러오기
        loadData()
    }

    // 세부 게시글 내용 불러오기
    fun loadData() {
        // 유저가 존재한다면
        if (user != null && postId.isNotEmpty()) {
            // 해당 인덱스의 게시글 가져오기
            var detailDocRef = db?.collection("post")?.document(postId)
            detailDocRef?.get()
                ?.addOnSuccessListener { documentSnapshot ->
                    var item = documentSnapshot.toObject(PostDetail::class.java)
                    binding.detailTitle.text = item?.title.orEmpty()
                    binding.detailRegion.text = item?.region.orEmpty()
                    binding.detailLocation.text = item?.location.orEmpty()
                    binding.detailName.text = item?.foodName.orEmpty()
                    binding.detailDeadline.text = item?.deadline.orEmpty()
                    binding.detailCreatedAt.text = item?.postedAt.orEmpty()
                    binding.detailPurchasedAt.text = item?.purchasedAt.orEmpty()
                    binding.detailContent.text = item?.content.orEmpty()

                    Log.d("aaa", writer)
                    // 게시글 작성 유저 정보 가져오기
                    db?.collection("user")?.document(writer)?.get()
                        ?.addOnSuccessListener { value ->
                            var dbUserNickname = value.data?.get("nickname") as String
                            var dbEnvLevel = value.data?.get("envlevel").toString()
                            when (dbEnvLevel) {
                                "1" -> dbEnvLevel = "씨앗이"
                                "2" -> dbEnvLevel = "새싹이"
                                "3" -> dbEnvLevel = "세잎이"
                                "4" -> dbEnvLevel = "묘목이"
                                "5" -> dbEnvLevel = "유목이"
                                "6" -> dbEnvLevel = "성목이"
                                "7" -> dbEnvLevel = "꽃잎이"
                                "8" -> dbEnvLevel = "낙옆이"
                                "9" -> dbEnvLevel = "과실이"
                                else -> dbEnvLevel = "씨앗이"
                            }
                            binding.detailWriter.text = dbUserNickname
                            binding.detailEnvLevel.text = dbEnvLevel
                        }

                    downloadImage(postId)
                }
                ?.addOnFailureListener {
                    val toast = Toast.makeText(this, "게시글 가져오기 실패", Toast.LENGTH_SHORT)
                    toast.show()
                }
        }

    }

    private fun downloadImage(imgId: String) {
        // 스토리지를 참조하는 StorageReference 생성
        val storageRef: StorageReference? = storage?.reference
        // 실제 업로드하는 파일을 참조하는 StorageReference 생성
        val imgRef: StorageReference? = storageRef?.child("images/${imgId}.jpg")
        val ONE_MEGABYTE: Long = 1024 * 1024
        imgRef?.getBytes(ONE_MEGABYTE)
            ?.addOnSuccessListener {
                val bitmap = BitmapFactory.decodeByteArray(it, 0, it.size)
                binding.detailimageView.setImageBitmap(bitmap)
            }?.addOnFailureListener {
                Log.d("download", "fail")
            }
    }
}
