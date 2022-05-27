package com.example.fffroject

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.firestore.FirebaseFirestore
import java.util.*

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

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_write)

        food = arrayListOf<food>()
        auth = FirebaseAuth.getInstance()
        user = auth!!.currentUser
        firestore = FirebaseFirestore.getInstance()


        name = findViewById(R.id.name)
        deadline = findViewById(R.id.deadline)
        purchasedAt = findViewById(R.id.purchasedAt)
        count = findViewById(R.id.count)
        upload_btn = findViewById(R.id.upload_btn)


        // 데이터 추가
        upload_btn.setOnClickListener {

            val food = hashMapOf(
                "name" to name.text.toString(),
                "deadline" to deadline.text.toString(),
                "purchasedAt" to purchasedAt.text.toString(),
                "count" to count.text.toString(),
            )

            firestore!!.collection("food")
                .add(food)
                .addOnSuccessListener { documentReference ->
                    Log.d(
                        "DatabaseTest",
                        documentReference.id
                    )
                }
                .addOnFailureListener { exception -> Log.d("DatabaseTest", exception.message!!) }


            Toast.makeText(this, "저장되었습니다.", Toast.LENGTH_SHORT).show()
        }
    }

}