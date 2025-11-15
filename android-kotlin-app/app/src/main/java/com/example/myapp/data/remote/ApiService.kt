package com.example.myapp.data.remote

import com.example.myapp.data.model.ApiError
import com.example.myapp.data.model.MigrainePrediction
import com.example.myapp.data.model.NotificationTokenRequest
import com.example.myapp.data.model.PushTokenRequest
import com.example.myapp.data.model.User
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Path

/**
 * Retrofit API service interface
 * Defines all API endpoints for the application
 */
interface ApiService {
    
    // ========== User Endpoints (Legacy/Example) ==========
    
    /**
     * Get all users
     * @return List of users
     */
    @GET("users")
    suspend fun getUsers(): Response<List<User>>

    /**
     * Get user by ID
     * @param id User ID
     * @return User object
     */
    @GET("users/{id}")
    suspend fun getUserById(@Path("id") id: Int): Response<User>
    
    // ========== Migraine Prediction Endpoints ==========
    
    /**
     * Get latest migraine prediction data
     * Endpoint: GET /api/v1/latest
     * 
     * Response structure:
     * {
     *   "p_next_hour": 0.75,
     *   "top_factors": [
     *     {
     *       "feature": "stress_level",
     *       "score": 0.85
     *     }
     *   ],
     *   "recommended_actions": ["Take a break", "Drink water"]
     * }
     * 
     * @return MigrainePrediction with probability, factors, and actions
     */
    @GET("api/v1/latest")
    suspend fun getLatestPrediction(): Response<MigrainePrediction>
    
    // ========== Notification Token Endpoints ==========
    
    /**
     * Register Expo push token
     * Endpoint: POST /api/v1/register-token
     * 
     * Request body:
     * {
     *   "expoPushToken": "ExponentPushToken[...]"
     * }
     * 
     * @param request PushTokenRequest containing expo push token
     * @return Empty response on success
     */
    @POST("api/v1/register-token")
    suspend fun registerPushToken(@Body request: PushTokenRequest): Response<Unit>
    
    /**
     * Register FCM notification token
     * Endpoint: POST /api/register-token
     * 
     * Request body:
     * {
     *   "user_id": "YZMM",
     *   "fcm_token": "Firebase Cloud Messaging token"
     * }
     * 
     * @param request NotificationTokenRequest containing user ID and FCM token
     * @return Empty response on success
     */
    @POST("api/register-token")
    suspend fun registerNotificationToken(@Body request: NotificationTokenRequest): Response<Unit>
}

