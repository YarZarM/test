package com.example.myapp.data.model

import com.google.gson.annotations.SerializedName

data class NotificationTokenRequest(
    @SerializedName("user_id")
    val userId: String,
    
    @SerializedName("fcm_token")
    val fcmToken: String
)

