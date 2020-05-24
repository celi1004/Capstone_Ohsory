package com.example.ohsoryapp.get

import com.example.ohsoryapp.post.PostNotificationResponse

data class GetShareAlarmListResponse(
        val id : Int,
        val sharer_name : String,
        val share_infos : ArrayList<PostNotificationResponse>
)