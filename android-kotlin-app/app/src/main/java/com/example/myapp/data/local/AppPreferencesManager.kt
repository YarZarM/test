package com.example.myapp.data.local

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first

/**
 * Helper class for managing app-wide preferences
 */
class AppPreferencesManager(
    private val dataStoreManager: DataStoreManager
) {
    suspend fun markFirstLaunchComplete() {
        dataStoreManager.saveIsFirstLaunch(false)
    }
    
    suspend fun saveThemeMode(themeMode: String) {
        dataStoreManager.saveThemeMode(themeMode)
    }
    
    suspend fun saveLastApiCallTime() {
        dataStoreManager.saveLastApiCallTime(System.currentTimeMillis())
    }
    
    suspend fun getLastApiCallTime(): Long? {
        // Note: This would need to be added to DataStoreManager if needed
        return null
    }
    
    val isFirstLaunch: Flow<Boolean> = dataStoreManager.isFirstLaunch
    val themeMode: Flow<String> = dataStoreManager.themeMode
    
    /**
     * Check if it's the first launch
     */
    suspend fun checkFirstLaunch(): Boolean {
        return dataStoreManager.isFirstLaunch.first()
    }
}

