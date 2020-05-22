package com.example.ohsoryapp.post

data class PostShareCreateResponse(
        val id : Int,
        val sharer_name : String,
        val sharee_name : String,
        val listening_noti : Boolean,
        val download_noti : Boolean,
        val download_auth : Boolean
)