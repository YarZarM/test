package com.example.myapp.data.local

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.doublePreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.intPreferencesKey
import androidx.datastore.preferences.core.longPreferencesKey
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.map
import java.io.IOException

private val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "app_preferences")

/**
 * Enhanced DataStoreManager for local data persistence
 * Replaces AsyncStorage functionality from React Native
 */
class DataStoreManager(private val context: Context) {
    
    private val dataStore: DataStore<Preferences> = context.dataStore
    
    // ========== User Preferences ==========
    
    suspend fun saveUserName(name: String) {
        dataStore.edit { preferences ->
            preferences[PreferencesKeys.USER_NAME] = name
        }
    }

    suspend fun saveUserEmail(email: String) {
        dataStore.edit { preferences ->
            preferences[PreferencesKeys.USER_EMAIL] = email
        }
    }
    
    suspend fun saveUserId(userId: String) {
        dataStore.edit { preferences ->
            preferences[PreferencesKeys.USER_ID] = userId
        }
    }

    val userName: Flow<String> = dataStore.data
        .catch { exception ->
            if (exception is IOException) {
                emit(androidx.datastore.preferences.core.emptyPreferences())
            } else {
                throw exception
            }
        }
        .map { preferences ->
            preferences[PreferencesKeys.USER_NAME] ?: ""
        }

    val userEmail: Flow<String> = dataStore.data
        .catch { exception ->
            if (exception is IOException) {
                emit(androidx.datastore.preferences.core.emptyPreferences())
            } else {
                throw exception
            }
        }
        .map { preferences ->
            preferences[PreferencesKeys.USER_EMAIL] ?: ""
        }
    
    val userId: Flow<String> = dataStore.data
        .catch { exception ->
            if (exception is IOException) {
                emit(androidx.datastore.preferences.core.emptyPreferences())
            } else {
                throw exception
            }
        }
        .map { preferences ->
            preferences[PreferencesKeys.USER_ID] ?: ""
        }
    
    // ========== Migraine Prediction Preferences ==========
    
    suspend fun saveLastPrediction(probability: Double) {
        dataStore.edit { preferences ->
            preferences[PreferencesKeys.LAST_PREDICTION_PROBABILITY] = probability
            preferences[PreferencesKeys.LAST_PREDICTION_TIMESTAMP] = System.currentTimeMillis()
        }
    }
    
    suspend fun saveSelectedRiskFactor(factor: String?) {
        dataStore.edit { preferences ->
            if (factor != null) {
                preferences[PreferencesKeys.SELECTED_RISK_FACTOR] = factor
            } else {
                preferences.remove(PreferencesKeys.SELECTED_RISK_FACTOR)
            }
        }
    }
    
    val lastPredictionProbability: Flow<Double?> = dataStore.data
        .catch { exception ->
            if (exception is IOException) {
                emit(androidx.datastore.preferences.core.emptyPreferences())
            } else {
                throw exception
            }
        }
        .map { preferences ->
            preferences[PreferencesKeys.LAST_PREDICTION_PROBABILITY]
        }
    
    val lastPredictionTimestamp: Flow<Long?> = dataStore.data
        .catch { exception ->
            if (exception is IOException) {
                emit(androidx.datastore.preferences.core.emptyPreferences())
            } else {
                throw exception
            }
        }
        .map { preferences ->
            preferences[PreferencesKeys.LAST_PREDICTION_TIMESTAMP]
        }
    
    val selectedRiskFactor: Flow<String?> = dataStore.data
        .catch { exception ->
            if (exception is IOException) {
                emit(androidx.datastore.preferences.core.emptyPreferences())
            } else {
                throw exception
            }
        }
        .map { preferences ->
            preferences[PreferencesKeys.SELECTED_RISK_FACTOR]
        }
    
    // ========== Notification Preferences ==========
    
    suspend fun saveNotificationEnabled(enabled: Boolean) {
        dataStore.edit { preferences ->
            preferences[PreferencesKeys.NOTIFICATION_ENABLED] = enabled
        }
    }
    
    suspend fun saveFcmToken(token: String) {
        dataStore.edit { preferences ->
            preferences[PreferencesKeys.FCM_TOKEN] = token
        }
    }
    
    suspend fun saveExpoPushToken(token: String) {
        dataStore.edit { preferences ->
            preferences[PreferencesKeys.EXPO_PUSH_TOKEN] = token
        }
    }
    
    val notificationEnabled: Flow<Boolean> = dataStore.data
        .catch { exception ->
            if (exception is IOException) {
                emit(androidx.datastore.preferences.core.emptyPreferences())
            } else {
                throw exception
            }
        }
        .map { preferences ->
            preferences[PreferencesKeys.NOTIFICATION_ENABLED] ?: true
        }
    
    val fcmToken: Flow<String?> = dataStore.data
        .catch { exception ->
            if (exception is IOException) {
                emit(androidx.datastore.preferences.core.emptyPreferences())
            } else {
                throw exception
            }
        }
        .map { preferences ->
            preferences[PreferencesKeys.FCM_TOKEN]
        }
    
    // ========== App Preferences ==========
    
    suspend fun saveIsFirstLaunch(isFirstLaunch: Boolean) {
        dataStore.edit { preferences ->
            preferences[PreferencesKeys.IS_FIRST_LAUNCH] = isFirstLaunch
        }
    }
    
    suspend fun saveThemeMode(themeMode: String) {
        dataStore.edit { preferences ->
            preferences[PreferencesKeys.THEME_MODE] = themeMode
        }
    }
    
    suspend fun saveLastApiCallTime(timestamp: Long) {
        dataStore.edit { preferences ->
            preferences[PreferencesKeys.LAST_API_CALL_TIME] = timestamp
        }
    }
    
    val isFirstLaunch: Flow<Boolean> = dataStore.data
        .catch { exception ->
            if (exception is IOException) {
                emit(androidx.datastore.preferences.core.emptyPreferences())
            } else {
                throw exception
            }
        }
        .map { preferences ->
            preferences[PreferencesKeys.IS_FIRST_LAUNCH] ?: true
        }
    
    val themeMode: Flow<String> = dataStore.data
        .catch { exception ->
            if (exception is IOException) {
                emit(androidx.datastore.preferences.core.emptyPreferences())
            } else {
                throw exception
            }
        }
        .map { preferences ->
            preferences[PreferencesKeys.THEME_MODE] ?: "system"
        }
    
    // ========== Settings Preferences ==========
    
    suspend fun saveMinLoadTime(ms: Int) {
        dataStore.edit { preferences ->
            preferences[PreferencesKeys.MIN_LOAD_TIME_MS] = ms
        }
    }
    
    suspend fun saveAutoRefreshEnabled(enabled: Boolean) {
        dataStore.edit { preferences ->
            preferences[PreferencesKeys.AUTO_REFRESH_ENABLED] = enabled
        }
    }
    
    suspend fun saveAutoRefreshInterval(minutes: Int) {
        dataStore.edit { preferences ->
            preferences[PreferencesKeys.AUTO_REFRESH_INTERVAL] = minutes
        }
    }
    
    val minLoadTime: Flow<Int> = dataStore.data
        .catch { exception ->
            if (exception is IOException) {
                emit(androidx.datastore.preferences.core.emptyPreferences())
            } else {
                throw exception
            }
        }
        .map { preferences ->
            preferences[PreferencesKeys.MIN_LOAD_TIME_MS] ?: 900
        }
    
    val autoRefreshEnabled: Flow<Boolean> = dataStore.data
        .catch { exception ->
            if (exception is IOException) {
                emit(androidx.datastore.preferences.core.emptyPreferences())
            } else {
                throw exception
            }
        }
        .map { preferences ->
            preferences[PreferencesKeys.AUTO_REFRESH_ENABLED] ?: false
        }
    
    val autoRefreshInterval: Flow<Int> = dataStore.data
        .catch { exception ->
            if (exception is IOException) {
                emit(androidx.datastore.preferences.core.emptyPreferences())
            } else {
                throw exception
            }
        }
        .map { preferences ->
            preferences[PreferencesKeys.AUTO_REFRESH_INTERVAL] ?: 15
        }
    
    // ========== Utility Methods ==========
    
    suspend fun clearData() {
        dataStore.edit { preferences ->
            preferences.clear()
        }
    }
    
    suspend fun clearUserData() {
        dataStore.edit { preferences ->
            preferences.remove(PreferencesKeys.USER_NAME)
            preferences.remove(PreferencesKeys.USER_EMAIL)
            preferences.remove(PreferencesKeys.USER_ID)
        }
    }
    
    suspend fun clearPredictionData() {
        dataStore.edit { preferences ->
            preferences.remove(PreferencesKeys.LAST_PREDICTION_PROBABILITY)
            preferences.remove(PreferencesKeys.LAST_PREDICTION_TIMESTAMP)
            preferences.remove(PreferencesKeys.SELECTED_RISK_FACTOR)
        }
    }
}

