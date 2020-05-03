package com.example.ohsoryapp.data

//서버에 보내고 싶은 json object모양 객체 생성
data class SignUpData(
        var username : String,
        var password : String,
        var password_check : String,
        var last_name : String,
        var first_name : String,
        var email : String
)