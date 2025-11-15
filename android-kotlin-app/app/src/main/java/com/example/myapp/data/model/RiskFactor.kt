package com.example.myapp.data.model

import com.google.gson.annotations.SerializedName

data class RiskFactor(
    @SerializedName("feature")
    val feature: String,
    
    @SerializedName("score")
    val score: Double
)

