package com.example.ohsoryapp.post

import java.sql.Timestamp

data class PostNotificationResponse(
        val id : Int,
        val req_type : Int,
        val req_text : String,
        val if_approve : Int,
        val timestamp : Timestamp,
        val share_id : Int
)