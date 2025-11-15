package com.example.myapp.data.repository

import com.example.myapp.data.local.UserPreferencesManager
import com.example.myapp.data.model.User
import com.example.myapp.data.remote.ApiService
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first

/**
 * Repository for user data
 * Handles API calls and local storage using DataStore
 */
class UserRepository(
    private val apiService: ApiService,
    private val userPreferencesManager: UserPreferencesManager
) {
    suspend fun getUsers(): Result<List<User>> {
        return try {
            val response = apiService.getUsers()
            if (response.isSuccessful) {
                Result.success(response.body() ?: emptyList())
            } else {
                Result.failure(Exception("Failed to fetch users: ${response.code()}"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun getUserById(id: Int): Result<User> {
        return try {
            val response = apiService.getUserById(id)
            if (response.isSuccessful) {
                Result.success(response.body() ?: throw Exception("User not found"))
            } else {
                Result.failure(Exception("Failed to fetch user: ${response.code()}"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun saveUserData(name: String, email: String, userId: String? = null) {
        userPreferencesManager.saveUser(name, email, userId)
    }

    suspend fun saveUserName(name: String) {
        val currentEmail = userPreferencesManager.userEmail.first()
        userPreferencesManager.saveUser(name, currentEmail, null)
    }

    suspend fun saveUserEmail(email: String) {
        val currentName = userPreferencesManager.userName.first()
        userPreferencesManager.saveUser(currentName, email, null)
    }

    fun getUserName(): Flow<String> = userPreferencesManager.userName

    fun getUserEmail(): Flow<String> = userPreferencesManager.userEmail
    
    fun getUserId(): Flow<String> = userPreferencesManager.userId

    suspend fun clearLocalData() {
        userPreferencesManager.clearUser()
    }
}

