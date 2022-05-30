package com.example.fffroject.fragment


// 나의 냉장고 리스트
data class MyFridge(
    var index : String? = null,
    var name : String? = null
)

// 나눔 전체 게시글 리스트
data class PostAll(
    var index :  String? =  null
)

// 식품 리스트 데이터
data class FoodList(
    var name : String? = null
)

//입력
data class food(
    var name :String? = null,
    var deadline : String? = null,
    var purchasedAt : String? = null,
    var count : String? = null

)