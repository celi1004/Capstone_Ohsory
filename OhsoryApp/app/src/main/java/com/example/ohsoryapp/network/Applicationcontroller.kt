package com.example.ohsoryapp.network

import android.app.Application
import android.util.Log
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

class ApplicationController : Application(){
    //액티비티 통신 부 이전에 레트로핏 세팅을 위함
    private val baseURL =  "http://3.34.3.134"
    lateinit var networkService: NetworkService

    //싱글톤 구현
    companion object {
        lateinit var instance: ApplicationController
    }

    //공유 클래스로 이 클래스의 온크리에이트 메소드는 시스템 진입점으로 가장 먼저 실행
    override fun onCreate() {
        super.onCreate()
        instance = this
        buildNetWork()
    }

    //레트로핏 빌더를 통해 클라이언트와 컨버터 설정
    //인터페이스 선택
    fun buildNetWork(){
        val retrofit: Retrofit = Retrofit.Builder()
                .baseUrl(baseURL)
                .addConverterFactory(GsonConverterFactory.create())
                .build()
        networkService = retrofit.create(NetworkService::class.java)
    }
}