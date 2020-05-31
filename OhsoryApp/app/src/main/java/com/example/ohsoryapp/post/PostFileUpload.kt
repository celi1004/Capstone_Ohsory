package com.example.ohsoryapp.post

import java.sql.Timestamp

data class PostFileUpload(
        val id : Int,
        val voice : String,
        val text : String,
        val duration : Float,
        val user_id : Int
)