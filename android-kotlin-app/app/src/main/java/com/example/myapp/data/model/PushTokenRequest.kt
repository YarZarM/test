package com.example.myapp.data.model

import com.google.gson.annotations.SerializedName

data class PushTokenRequest(
    @SerializedName("expoPushToken")
    val expoPushToken: String
)

