package com.example.ohsoryapp.data

import java.sql.Timestamp

data class NotificationData(
        val sharer_name : String,
        val sharee_name : String,
        val req_type : Int,
        val req_text : String
)