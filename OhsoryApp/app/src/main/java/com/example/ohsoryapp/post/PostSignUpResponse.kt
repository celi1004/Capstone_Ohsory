package com.example.ohsoryapp.post

data class PostSignUpResponse(
        val user : PostUserData,
        val token : String
)

data class PostUserData(
        val id : Int,
        val username : String
)