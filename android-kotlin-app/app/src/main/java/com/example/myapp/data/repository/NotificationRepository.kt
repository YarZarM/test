package com.example.myapp.data.repository

import com.example.myapp.data.model.NotificationTokenRequest
import com.example.myapp.data.model.PushTokenRequest
import com.example.myapp.data.remote.ApiService

/**
 * Repository for notification token registration
 * Handles push notification token registration with the backend
 */
class NotificationRepository(
    private val apiService: ApiService
) {
    /**
     * Registers an Expo push token with the backend
     * 
     * @param token Expo push token string
     * @return Result<Unit> indicating success or failure
     */
    suspend fun registerPushToken(token: String): Result<Unit> {
        return try {
            val request = PushTokenRequest(expoPushToken = token)
            val response = apiService.registerPushToken(request)
            
            if (response.isSuccessful) {
                Result.success(Unit)
            } else {
                val errorBody = response.errorBody()?.string() ?: "Unknown error"
                Result.failure(
                    Exception("Failed to register push token: ${response.code()}. Body: $errorBody")
                )
            }
        } catch (e: java.net.UnknownHostException) {
            Result.failure(Exception("Unable to reach server. Please check your connection."))
        } catch (e: java.net.SocketTimeoutException) {
            Result.failure(Exception("Request timed out. Please try again."))
        } catch (e: java.io.IOException) {
            Result.failure(Exception("Network error: ${e.message}"))
        } catch (e: Exception) {
            Result.failure(Exception("Unexpected error: ${e.message}", e))
        }
    }
    
    /**
     * Registers a Firebase Cloud Messaging token with the backend
     * 
     * @param userId User identifier
     * @param fcmToken Firebase Cloud Messaging token
     * @return Result<Unit> indicating success or failure
     */
    suspend fun registerNotificationToken(userId: String, fcmToken: String): Result<Unit> {
        return try {
            val request = NotificationTokenRequest(
                userId = userId,
                fcmToken = fcmToken
            )
            val response = apiService.registerNotificationToken(request)
            
            if (response.isSuccessful) {
                Result.success(Unit)
            } else {
                val errorBody = response.errorBody()?.string() ?: "Unknown error"
                Result.failure(
                    Exception("Failed to register notification token: ${response.code()}. Body: $errorBody")
                )
            }
        } catch (e: java.net.UnknownHostException) {
            Result.failure(Exception("Unable to reach server. Please check your connection."))
        } catch (e: java.net.SocketTimeoutException) {
            Result.failure(Exception("Request timed out. Please try again."))
        } catch (e: java.io.IOException) {
            Result.failure(Exception("Network error: ${e.message}"))
        } catch (e: Exception) {
            Result.failure(Exception("Unexpected error: ${e.message}", e))
        }
    }
}

