package com.blissvine.xchat.models

data class UserData(
    var userId: String? = "",
    var name: String? = "",
    var email : String? = "",
    var number: String? = "",
    var imageUrl: String? = "",
){
    fun toMap() = mapOf(
        "userId" to userId,
        "name" to name,
        "email" to email,
        "number" to number,
        "imageUrl" to imageUrl

    )
}