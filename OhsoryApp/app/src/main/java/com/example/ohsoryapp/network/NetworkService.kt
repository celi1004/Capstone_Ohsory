package com.example.ohsoryapp.network

import com.example.ohsoryapp.data.LoginData
import com.example.ohsoryapp.data.SignUpData
import com.example.ohsoryapp.get.GetProgressResponse
import com.example.ohsoryapp.post.PostSignUpResponse
import retrofit2.Call
import retrofit2.http.*

interface NetworkService{

    //POST방식으로 UserData를 보냄 서버의 반환은 PostSignUpResponse형
    //회원 가입
    @POST("/auth_app/auth/register")
    @Headers("Content-Type: application/json")
    fun postSignUpResponse(
            @Body userData: SignUpData
    ): Call<PostSignUpResponse>

    // 로그인
    @POST("/auth_app/auth/login")
    @Headers("Content-Type: application/json")
    fun postLoginResponse(
            @Body loginData: LoginData
    ): Call<PostSignUpResponse>

    // 진척도 받아오기
    @GET("/auth_app/auth/profile/{user_pk}/get-progress")
    @Headers("Content-Type: application/json")
    fun getProgressResponse(
            @Path("user_pk") user_pk: Int
    ): Call<GetProgressResponse>

}