package com.example.myapp.data.model

import com.google.gson.annotations.SerializedName

data class MigrainePrediction(
    @SerializedName("p_next_hour")
    val probability: Double,
    
    @SerializedName("top_factors")
    val topFactors: List<RiskFactor>,
    
    @SerializedName("recommended_actions")
    val recommendedActions: List<String>
)

