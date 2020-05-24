package com.example.ohsoryapp.get

import com.example.ohsoryapp.post.PostNotificationResponse

data class GetMyShareAlarmListResponse(
        val id : Int,
        val sharee_name : String,
        val share_infos : ArrayList<PostNotificationResponse>
)