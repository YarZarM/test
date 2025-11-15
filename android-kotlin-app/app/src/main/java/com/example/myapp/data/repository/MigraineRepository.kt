package com.example.myapp.data.repository

import com.example.myapp.data.model.MigrainePrediction
import com.example.myapp.data.remote.ApiService
import kotlinx.coroutines.delay

/**
 * Repository for migraine prediction data
 * Handles API calls and data transformation
 */
class MigraineRepository(
    private val apiService: ApiService
) {
    private val MIN_LOAD_TIME_MS = 900L
    
    /**
     * Fetches the latest migraine prediction from the API
     * 
     * @return Result containing MigrainePrediction on success, or Exception on failure
     */
    suspend fun getLatestPrediction(): Result<MigrainePrediction> {
        val startTime = System.currentTimeMillis()
        
        return try {
            val response = apiService.getLatestPrediction()
            
            if (response.isSuccessful) {
                val data = response.body()
                if (data != null) {
                    // Ensure minimum load time for better UX (prevents flickering)
                    val elapsed = System.currentTimeMillis() - startTime
                    if (elapsed < MIN_LOAD_TIME_MS) {
                        delay(MIN_LOAD_TIME_MS - elapsed)
                    }
                    Result.success(data)
                } else {
                    Result.failure(Exception("Response body is null"))
                }
            } else {
                val errorBody = response.errorBody()?.string() ?: "Unknown error"
                Result.failure(
                    Exception("API error: ${response.code()} - ${response.message()}. Body: $errorBody")
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

