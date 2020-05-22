package com.example.ohsoryapp.data

import java.sql.Timestamp

data class NotificationData(
        val share_id : Int,
        val req_type : Int,
        val req_text : String,
        val if_approve : Int
)