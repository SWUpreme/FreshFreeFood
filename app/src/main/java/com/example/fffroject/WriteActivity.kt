package com.example.fffroject

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import com.example.fffroject.fragment.FoodList
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.firestore.FirebaseFirestore
import java.util.*
import kotlin.collections.ArrayList

class WriteActivity : AppCompatActivity() {

    var auth: FirebaseAuth? = null
    var firestore: FirebaseFirestore? = null
    var user: FirebaseUser? = null
    lateinit var food: ArrayList<food>

    lateinit var name: EditText
    lateinit var deadline: EditText
    lateinit var purchasedAt: EditText
    lateinit var count: EditText
    lateinit var upload_btn: Button

    lateinit var foodlist: ArrayList<FoodList>
    lateinit var foodindex: String

    var fridgeindex : String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_write)

        foodlist = arrayListOf<FoodList>()

        food = arrayListOf<food>()
        auth = FirebaseAuth.getInstance()
        user = auth!!.currentUser
        firestore = FirebaseFirestore.getInstance()


        name = findViewById(R.id.name)
        deadline = findViewById(R.id.deadline)
        purchasedAt = findViewById(R.id.purchasedAt)
        count = findViewById(R.id.count)
        upload_btn = findViewById(R.id.upload_btn)

        fridgeindex = intent.getStringExtra("index")  // 냉장고 id
        Toast.makeText(this, fridgeindex, Toast.LENGTH_SHORT).show()

        // 데이터 추가
        upload_btn.setOnClickListener {

//            val food = hashMapOf(
//                "name" to name.text.toString(),
//                "deadline" to deadline.text.toString(),
//                "purchasedAt" to purchasedAt.text.toString(),
//                "count" to count.text.toString(),
//            )

            // 민영 추가 파이어스토어 코드
            if (user != null) {
                foodindex = UUID.randomUUID().toString()
                firestore?.collection("fridge")?.document("$fridgeindex")
                    ?.collection("food")?.document("$foodindex")
                    ?.set(
                        hashMapOf(
                            "index" to foodindex,
                            "name" to name.text.toString(),
                            "deadline" to deadline.text.toString(),
                            "purchaseAt" to purchasedAt.text.toString(),
                            "count" to count.inputType
                        )
                    )
                    ?.addOnSuccessListener {  }
                    ?.addOnFailureListener {  }
            }


//            firestore!!.collection("food")
//                .add(food)
//                .addOnSuccessListener { documentReference ->
//                    Log.d(
//                        "DatabaseTest",
//                        documentReference.id
//                    )
//                }
//                .addOnFailureListener { exception -> Log.d("DatabaseTest", exception.message!!) }
//
//
            Toast.makeText(this, name.text.toString() + count.text.toString(), Toast.LENGTH_SHORT).show()
        }
    }

}