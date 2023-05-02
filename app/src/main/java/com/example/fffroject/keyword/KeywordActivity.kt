package com.example.fffroject.keyword


import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.example.fffroject.R
import com.example.fffroject.databinding.ActivityKeywordBinding
import com.example.fffroject.fragment.KeyWord
import com.google.android.flexbox.*
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import kotlinx.android.synthetic.main.activity_keyword.*
import kotlinx.android.synthetic.main.fragment_share.*
import java.text.SimpleDateFormat
import java.util.*


//코드 수정 예정
class KeywordActivity : AppCompatActivity() {
    var auth: FirebaseAuth? = null
    var user : FirebaseUser? = null
    // 전역 변수로 바인딩 객체 선언
    private var mBinding: ActivityKeywordBinding? = null

    // 매번 null 체크를 할 필요 없이 편의성을 위해 바인딩 변수 재 선언
    private val binding get() = mBinding!!
    var firestore: FirebaseFirestore? = null

    // Data에 있는 keyword와 연결
    lateinit var keywordList: ArrayList<KeyWord>

    var count = 0 //키워드 수
    lateinit var keyid: String
    // 리사이클러뷰
    lateinit var recyclerviewKeyword: RecyclerView
    var docname : String? = null

    override fun onDestroy() {
        mBinding = null
        super.onDestroy()
    }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        mBinding = ActivityKeywordBinding.inflate(layoutInflater)
        setContentView(binding.root)

        keywordList = arrayListOf<KeyWord>()
        auth = FirebaseAuth.getInstance()
        user = auth!!.currentUser
        // 파이어스토어 인스턴스 초기화
        firestore = FirebaseFirestore.getInstance()

        loadData()

        val flexboxLayoutManager = FlexboxLayoutManager(applicationContext).apply {
            flexWrap = FlexWrap.WRAP
            flexDirection = FlexDirection.ROW
            alignItems = AlignItems.STRETCH
        }
        recyclerviewKeyword = binding.recyclerviewKeyword
        binding.recyclerviewKeyword.run{
            layoutManager = flexboxLayoutManager
            adapter = KeyWordAdapter()
            setHasFixedSize(false)
        }



        binding.keywordInput.addTextChangedListener(object: TextWatcher {
            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
            }
            override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
                var userinput = binding.keywordInput.text.toString()
                if(userinput.length > 0){
                    binding.addkeywordBtn.setTextColor(ContextCompat.getColor(applicationContext!!, R.color.chat_2))
                }
                else{

                }
            }
            override fun afterTextChanged(p0: Editable?) {
            }
        })

        //키워드 추가버튼 클릭 시
        mBinding?.addkeywordBtn?.setOnClickListener {

            val input = binding.keywordInput
            //val input_tostring = input.text.toString()
           // val inputToKeyword = KeyWord(input_tostring)
            keyid = UUID.randomUUID().toString()
            val nowTime = System.currentTimeMillis()
            val timeformatter = SimpleDateFormat("yyyy.MM.dd.hh.mm.ss")
            val dateTime = timeformatter.format(nowTime)

            var usuableKeyword = true


            if (input.length() <= 0) {
                Toast.makeText(this, "키워드를 입력해주세요.", Toast.LENGTH_SHORT).show()
            }else if (count > 19) {
                Toast.makeText(this, "최대 20개까지 가능합니다.", Toast.LENGTH_SHORT).show()
            }
            else {
                firestore?.collection("user")?.document(user!!.uid)
                    ?.collection("mykeyword")
                    ?.whereEqualTo("status", "active")
                    ?.get()?.addOnSuccessListener { documnets ->
                    for (document in documnets) {
                        val existKeyword = document["keyword"].toString()
                        if (existKeyword == binding.keywordInput.text.toString()) {

                            usuableKeyword = false
                        }

                    }
                    if (usuableKeyword == true) {
                        firestore?.collection("user")?.document(user!!.uid)?.collection("mykeyword")
                            ?.document("$keyid")
                            ?.set(
                                hashMapOf(
                                    "keyId" to keyid,
                                    "keyword" to input.text.toString(),
                                    "createdAt" to dateTime,
                                    "updatedAt" to dateTime,
                                    "status" to "active"
                                )
                            )

                        Toast.makeText(this, "키워드가 추가되었습니다.", Toast.LENGTH_SHORT).show()
                    } else {

                        Toast.makeText(this, "이미 등록된 키워드입니다.", Toast.LENGTH_SHORT).show()
                    }
                }
            }
        }

    }

    // 뷰 홀더
    inner class KeywordViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView)

    // 뷰 어댑터
    inner class KeyWordAdapter(): RecyclerView.Adapter<RecyclerView.ViewHolder>(){

        // 리사이클러뷰의 아이템 총 개수 반환
        override fun getItemCount(): Int {
            return keywordList.size
        }

        // xml파일을 inflate하여 ViewHolder를 생성
        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
            var view =
                LayoutInflater.from(parent.context).inflate(R.layout.item_keyword, parent, false)
            return KeywordViewHolder(view)
        }

        // onCreateViewHolder에서 만든 view와 실제 데이터를 연결
        override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {

            // 바인딩
            val viewHolder = (holder as KeywordViewHolder).itemView
            val keyword: TextView =viewHolder.findViewById(R.id.txtkeyword)
            val delbtn: ImageView =viewHolder.findViewById(R.id.delete_btn)

            keyword.text = keywordList!![position].keyword
            var keyid = keywordList!![position].keyId.toString()

            //키워드 삭제
            delbtn.setOnClickListener{
                itemDelete(keyid)
                notifyItemRemoved(position)
            }

        }
    }
    //키워드 수
    fun getCount() {
        var text_count = findViewById<TextView>(R.id.registered_keyword)
        text_count.text = ""+ count
    }

    private fun loadData() {
        firestore?.collection("user")?.document(user!!.uid)
            ?.collection("mykeyword")?.whereEqualTo("status", "active")
            ?.orderBy("createdAt", Query.Direction.DESCENDING)
            ?.addSnapshotListener { value, error ->
                keywordList.clear()
                if (value != null) {

                    for (snapshot in value.documents) {
                        var item = snapshot.toObject(KeyWord::class.java)
                        if (item != null) {
                            keywordList.add(item)
                        }
                    }
                }
                count = keywordList.size
                getCount()
                recyclerviewKeyword.adapter?.notifyDataSetChanged()
            }

    }





    fun itemDelete(keyid: String){

        val nowTime = System.currentTimeMillis()
        val timeformatter = SimpleDateFormat("yyyy.MM.dd.hh.mm.ss")
        val dateTime = timeformatter.format(nowTime)

        firestore?.collection("user")?.document(user!!.uid)?.collection("mykeyword")?.document("$keyid")
            ?.update("status", "delete")
            ?.addOnSuccessListener {

            }
            ?.addOnFailureListener { }

        firestore?.collection("user")?.document(user!!.uid)?.collection("mykeyword")?.document("$keyid")
            ?.update("updatedAt", dateTime)
            ?.addOnSuccessListener { }
            ?.addOnFailureListener { }

    }


}