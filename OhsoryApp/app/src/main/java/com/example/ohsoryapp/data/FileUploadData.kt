package com.example.ohsoryapp.data

import okhttp3.RequestBody

data class FileUploadData(
        var user_id : Int,
        var voice: RequestBody
)