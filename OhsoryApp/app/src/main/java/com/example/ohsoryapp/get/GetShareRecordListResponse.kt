package com.example.ohsoryapp.get

data class GetShareRecordListResponse(
        val id : Int,
        val sharer_name : String,
        val sharee_name : String,
        val listening_noti : Boolean,
        val download_noti : Boolean,
        val download_auth : Boolean
)