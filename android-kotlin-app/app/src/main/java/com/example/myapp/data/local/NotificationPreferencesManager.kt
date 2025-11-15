package com.example.myapp.data.local

import kotlinx.coroutines.flow.Flow

/**
 * Helper class for managing notification preferences
 */
class NotificationPreferencesManager(
    private val dataStoreManager: DataStoreManager
) {
    suspend fun saveNotificationSettings(
        enabled: Boolean,
        fcmToken: String? = null,
        expoToken: String? = null
    ) {
        dataStoreManager.saveNotificationEnabled(enabled)
        fcmToken?.let { dataStoreManager.saveFcmToken(it) }
        expoToken?.let { dataStoreManager.saveExpoPushToken(it) }
    }
    
    suspend fun saveFcmToken(token: String) {
        dataStoreManager.saveFcmToken(token)
    }
    
    suspend fun saveExpoPushToken(token: String) {
        dataStoreManager.saveExpoPushToken(token)
    }
    
    val notificationEnabled: Flow<Boolean> = dataStoreManager.notificationEnabled
    val fcmToken: Flow<String?> = dataStoreManager.fcmToken
}

