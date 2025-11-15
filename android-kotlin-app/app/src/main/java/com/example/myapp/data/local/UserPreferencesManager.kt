package com.example.myapp.data.local

import kotlinx.coroutines.flow.Flow

/**
 * Helper class for managing user preferences
 * Provides a clean API for user-related data storage
 */
class UserPreferencesManager(
    private val dataStoreManager: DataStoreManager
) {
    suspend fun saveUser(name: String, email: String, userId: String? = null) {
        dataStoreManager.saveUserName(name)
        dataStoreManager.saveUserEmail(email)
        userId?.let { dataStoreManager.saveUserId(it) }
    }
    
    suspend fun clearUser() {
        dataStoreManager.clearUserData()
    }
    
    val userName: Flow<String> = dataStoreManager.userName
    val userEmail: Flow<String> = dataStoreManager.userEmail
    val userId: Flow<String> = dataStoreManager.userId
}

