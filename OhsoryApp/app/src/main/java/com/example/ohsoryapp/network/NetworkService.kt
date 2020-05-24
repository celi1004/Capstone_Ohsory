package com.example.ohsoryapp.network

import com.example.ohsoryapp.data.*
import com.example.ohsoryapp.get.*
import com.example.ohsoryapp.post.PostNotificationResponse
import com.example.ohsoryapp.post.PostShareCreateResponse
import com.example.ohsoryapp.post.PostSignUpResponse
import com.example.ohsoryapp.put.PutFCMKeyUpdateResponse
import com.example.ohsoryapp.put.PutAfterRequestNotificationResponse
import com.example.ohsoryapp.put.PutShareAuthResponse
import retrofit2.Call
import retrofit2.http.*
import com.example.ohsoryapp.data.ShareeData as ShareeData

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

    // fcm키 업데이트
    @PUT("/auth_app/auth/profile/{user_pk}/fcm-key-update")
    @Headers("Content-Type: application/json")
    fun putFCMKeyUpdateResponse(
            @Path("user_pk") user_pk: Int,
            @Body fcmData: FCMData
    ): Call<PutFCMKeyUpdateResponse>







    // 내가 공유 해준 모델 사용된 기록
    @POST("/share_app/share-info/myshare")
    @Headers("Content-Type: application/json")
    fun getMyShareModelRecordResponse(
            @Body sharerData: SharerData
    ): Call<ArrayList<GetMyShareAlarmListResponse>>

    // 내가 공유 받은 모델 사용된 기록
    @POST("/share_app/share-info/share-list")
    @Headers("Content-Type: application/json")
    fun getSharedModelRecordResponse(
            @Body shareeData: ShareeData
    ):Call<ArrayList<GetShareAlarmListResponse>>








    // 공유 모델 사용 시 알림 요청
    @POST("/share_app/share-info/notification")
    @Headers("Content-Type: application/json")
    fun postNotificationResponse(
            @Body notificationData: NotificationData
    ): Call<PostNotificationResponse>

    // sharee의 다운로드 요청 허락
    @PUT("/share_app/share-info/{id}/approve")
    @Headers("Content-Type: application/json")
    fun putApproveResponse(
            @Path("id") share_id: Int,
            @Body putNotificationResponse: PutAfterRequestNotificationResponse
    ): Call<PutAfterRequestNotificationResponse>








    // 새로운 공유 생성
    @POST("/share_app/share/create")
    @Headers("Content-Type: application/json")
    fun postShareCreateResponse(
            @Body shareCreateData: ShareCreateData
    ): Call<PostShareCreateResponse>

    // 공유 삭제
    @DELETE("/share_app/share/{id}/delete")
    @Headers("Content-Type: application/json")
    fun deleteShareDeleteResponse(
            @Path("id") share_pk: Int,
            @Body id : Int
    )









    // 내가 공유 해준 모델 목록 확인
    @GET("/share_app/share/myshare")
    @Headers("Content-Type: application/json")
    fun getMyShareModelListResponse(
            @Body sharerData: SharerData
    ): Call<GetMyShareModelListResponse>

    // 내가 공유 받은 모델 목록 확인
    @POST("/share_app/share/share-list")
    @Headers("Content-Type: application/json")
    fun getSharedModelListResponse(
            @Body shareeData: ShareeData
    ):Call<ArrayList<GetShareModelListResponse>>








    // 공유모델 알림설정 다운로드 권한 수정
    @PUT("/share_app/share-info/{id}/auth-update")
    @Headers("Content-Type: application/json")
    fun putResponse(
            @Path("id") share_pk: Int,
            @Body putShareAuthResponse: PutShareAuthResponse
    ): Call<PutShareAuthResponse>


}