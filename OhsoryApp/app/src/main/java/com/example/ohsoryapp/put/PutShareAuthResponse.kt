package com.example.ohsoryapp.put

import java.sql.Timestamp

data class PutShareAuthResponse(
        val sharer_name : String,
        val sharee_name : String,
        val listening_noti : Boolean,
        val download_noti : Boolean,
        val download_auth : Boolean
)