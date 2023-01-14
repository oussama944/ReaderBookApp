package com.projet.appreader.model

data class MUser(
    val id:String?,
    val userId: String,
    val displajName: String,
    val avatarUrl: String,
    val quote :String,
    val profession: String
){
    fun toMap(): MutableMap<String,Any>{
        return mutableMapOf(
            "user_id" to this.userId,
            "displajName" to this.displajName,
            "avatarUrl" to this.avatarUrl,
            "quote" to this.quote,
            "profession" to this.profession
        )
    }
}