package com.example.ohsoryapp.put

import java.sql.Timestamp

data class PutShareAuthResponse(
        val id : Int,
        val listening_noti : Boolean,
        val download_noti : Boolean,
        val download_auth : Boolean
)