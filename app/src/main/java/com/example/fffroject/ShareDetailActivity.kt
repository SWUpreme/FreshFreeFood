package com.example.fffroject

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.core.content.ContextCompat
import com.example.fffroject.databinding.ActivitySharedetailBinding
import com.example.fffroject.fragment.PostDetail
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.android.synthetic.main.activity_sharedetail.*
import kotlinx.android.synthetic.main.activity_sharepost.*
import kotlinx.android.synthetic.main.fragment_share.*
import kotlinx.android.synthetic.main.item_sharelist.*


class ShareDetailActivity: AppCompatActivity()  {

    val TAG: String = "로그"

    //파이어스토어
    var auth: FirebaseAuth? = null
    var db: FirebaseFirestore? = null
    var user: FirebaseUser? = null

    // 바인딩 객체
    lateinit var binding: ActivitySharedetailBinding

    // 툴바
    lateinit var toolbar_sharedetail: Toolbar

    // Data에 있는 PostDetail
    //lateinit var postDetailList: ArrayList<PostDetail>
    // 화면 구성 내용
    lateinit var detailIndex : String
    lateinit var detailWriter : String
    lateinit var detailTitle : String
    lateinit var detailName : String
    lateinit var detailRegion : String
    lateinit var detailLocation : String
    lateinit var detailDeadline : String
    lateinit var detailCreatedAt : String
    lateinit var detailPurchasedAt : String
    lateinit var detailContent : String
    lateinit var detailFlag : String

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
        // 상단 툴바 사용
        toolbar_sharedetail = findViewById(R.id.toolbSharedetail)
        // postDetail 초기화
        //postDetailList = arrayListOf<PostDetail>()

        // 메세지 버튼
        toolbSharedetail.setOnMenuItemClickListener {
            when(it.itemId) {
                R.id.btnGotoMessage -> {
                    val intent = Intent(this@ShareDetailActivity, ChatActivity::class.java)
                    intent.putExtra("detailIndex", detailIndex.toString())
                    intent.putExtra("detailWriter", detailWriter.toString())


                    true

                }
                else -> false
            }
        }

        // ShareFragment Intent 연결
        detailIndex = intent.getStringExtra("detailIndex")!!    // 게시글 인덱스
        detailWriter = intent.getStringExtra("detailWriter")!!    // 게시글 냉장고 넘김 여부
        detailFlag = intent.getStringExtra("detailFlag")!!    // 게시글 냉장고 넘김 여부

        // 냉장고에서 넘기기 여부 확인 후 색상 변경
        if(detailFlag=="true"){
            binding.detailRegion.setBackgroundResource(R.drawable.txt_background_round2_blue)
            binding.detailLocation.setBackgroundResource(R.drawable.txt_background_round2_blue)
            binding.detailRegion.setTextColor(ContextCompat.getColor(this, R.color.white))
            binding.detailLocation.setTextColor(ContextCompat.getColor(this, R.color.white))
        }else{
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
        if (user != null) {
            // 해당 인덱스의 게시글 가져오기
            var detailDocRef = db?.collection("post")?.document(detailIndex.toString())
            detailDocRef?.get()
                ?.addOnSuccessListener { documentSnapshot ->
                    var item = documentSnapshot.toObject(PostDetail::class.java)
                    binding.detailTitle.text = item?.title!!
                    binding.detailRegion.text = item?.region!!
                    binding.detailLocation.text = item?.location!!
                    binding.detailName.text = item?.name!!
                    binding.detailDeadline.text = item?.deadline!!
                    binding.detailCreatedAt.text = item?.createdAt!!
                    binding.detailPurchasedAt.text = item?.purchasedAt!!
                    binding.detailContent.text = item?.content!!

                }
                ?.addOnFailureListener {
                    val toast = Toast.makeText(this, "게시글 가져오기 실패", Toast.LENGTH_SHORT)
                    toast.show()
                }

        }

    }
}