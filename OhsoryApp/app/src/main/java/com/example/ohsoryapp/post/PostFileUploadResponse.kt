package com.example.ohsoryapp.post

import java.sql.Timestamp

data class PostFileUploadResponse(
        val id : Int,
        val voice : String,
        val text : String,
        val duration : Float,
        val user_id : Int
)