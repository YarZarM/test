package com.example.myapp.data.model

import com.google.gson.annotations.SerializedName

/**
 * Generic API response wrapper
 */
data class ApiResponse<T>(
    @SerializedName("data")
    val data: T? = null,
    
    @SerializedName("message")
    val message: String? = null,
    
    @SerializedName("success")
    val success: Boolean = true,
    
    @SerializedName("error")
    val error: String? = null
)

/**
 * API Error response model
 */
data class ApiError(
    @SerializedName("error")
    val error: String,
    
    @SerializedName("message")
    val message: String? = null,
    
    @SerializedName("code")
    val code: Int? = null
)

