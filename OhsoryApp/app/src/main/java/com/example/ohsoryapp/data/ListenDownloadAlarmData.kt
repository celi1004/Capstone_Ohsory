package com.example.ohsoryapp.data

//서버에 보내고 싶은 json object모양 객체 생성
data class ListenDownloadAlarmData(
        val listening_noti : Boolean,
        val download_noti : Boolean,
        val download_auth : Boolean
)