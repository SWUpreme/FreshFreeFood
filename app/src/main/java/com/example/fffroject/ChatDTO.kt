package com.example.fffroject

data class ChatDTO(
    var uid : String? = "",
    var kind : Int? = -1,
    var timestamp : Long? = -1,
    var message : String? = ""
)