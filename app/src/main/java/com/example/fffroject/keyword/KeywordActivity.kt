package com.example.fffroject.keyword

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.View
import android.widget.TextView
import android.widget.Toast
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.fffroject.KeyWordAdapter
import com.example.fffroject.R
import com.example.fffroject.databinding.ActivityKeywordBinding
import com.example.fffroject.fragment.KeyWord
import com.example.fffroject.fragment.MyFridge
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
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
    val keywordList = arrayListOf<KeyWord>()
    val keyadapter = KeyWordAdapter(keywordList)
    var count = 0 //키워드 수
    var mDocuments: List<DocumentSnapshot>? = null
    lateinit var keyid: String



    override fun onDestroy() {
        mBinding = null
        super.onDestroy()
    }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        mBinding = ActivityKeywordBinding.inflate(layoutInflater)
        setContentView(binding.root)


        auth = FirebaseAuth.getInstance()
        user = auth!!.currentUser
        // 파이어스토어 인스턴스 초기화
        firestore = FirebaseFirestore.getInstance()

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
            keyid = UUID.randomUUID().toString()
            val nowTime = System.currentTimeMillis()
            val timeformatter = SimpleDateFormat("yyyy.MM.dd.hh.mm.ss")
            val dateTime = timeformatter.format(nowTime)
            firestore?.collection("user")?.document(user!!.uid)?.collection("mykeyword")
                ?.document("$keyid")
                ?.set(
                    hashMapOf(
                        "keyId" to keyid,
                        "keyword" to input.text.toString(),
                        "createdAt" to dateTime.toString(),
                        "status" to "active"
                    )
                )
                ?.addOnSuccessListener {
                    Toast.makeText(this, "키워드가 추가되었습니다", Toast.LENGTH_SHORT).show()
                    firestore?.collection("user")?.document(user!!.uid)?.collection("mykeyword")!!
                        .orderBy("createdAt", Query.Direction.DESCENDING)
                        .addSnapshotListener { snapshot, exception ->
                            if (exception != null) {
                            } else {
                                if (snapshot != null) {
                                    if (!snapshot.isEmpty) {
                                        keywordList.clear()
                                        mDocuments = snapshot.documents
                                        val documents = snapshot.documents
                                        for (document in documents) {
                                            val item = KeyWord(document["keyword"] as String)
                                            keywordList.add(item)
                                        }
                                        keyadapter.notifyDataSetChanged()
                                    }
                                }
                            }
                        }
                }

                ?.addOnFailureListener { exception ->
                }
        }

        val gridLayoutManager = GridLayoutManager(applicationContext, 4)
        mBinding?.recyclerviewKeyword?.layoutManager = gridLayoutManager
        mBinding?.recyclerviewKeyword?.adapter = keyadapter

        //키워드 삭제
        (mBinding?.recyclerviewKeyword?.adapter as KeyWordAdapter)
        keyadapter.itemClick = object : KeyWordAdapter.ItemClick {
            override fun onClick(view: View, pos: Int) {
                when (view.id) {
                    R.id.delete_btn -> itemDelete(mDocuments!!.get(pos))
                }
            }
        }

        (mBinding?.recyclerviewKeyword?.adapter as KeyWordAdapter).getDataFromFirestore()
    }
    //키워드 수
    fun getCount() {
        var text_count = findViewById<TextView>(R.id.registered_keyword)
        text_count.text = ""+ count
    }



    fun KeyWordAdapter.getDataFromFirestore() {
        firestore?.collection("user")?.document(user!!.uid)?.collection("mykeyword")
            ?.orderBy("createdAt", Query.Direction.DESCENDING)
            ?.addSnapshotListener { snapshot, exception ->

                keywordList.clear()


                if (snapshot != null) {
                    if (!snapshot.isEmpty) {
                        mDocuments = snapshot.documents
                        val documents = snapshot.documents

                        for (document in documents) {
                            val item = KeyWord(document["keyword"] as String)
                            if (item != null) {
                                keywordList.add(item)

                            }
                        }

                    }
                    count = keywordList.size
                    getCount()
                    keyadapter.notifyDataSetChanged()

                }
            }


    }

    fun itemDelete(doc: DocumentSnapshot){


        firestore?.collection("user")?.document(user!!.uid)?.collection("mykeyword")?.document(doc.id)
            ?.update("status", "delete")
            ?.addOnSuccessListener {

            }
            ?.addOnFailureListener { }

    }
}

