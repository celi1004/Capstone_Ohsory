package com.example.ohsoryapp.get

import okhttp3.ResponseBody

data class GetFileDownloadResponse(
        val id : Int,
        val req_text : String,
        val tts_file : String,
        val user_id : Int
)