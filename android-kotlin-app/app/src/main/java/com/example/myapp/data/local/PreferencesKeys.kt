package com.example.myapp.data.local

import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.doublePreferencesKey
import androidx.datastore.preferences.core.intPreferencesKey
import androidx.datastore.preferences.core.longPreferencesKey
import androidx.datastore.preferences.core.stringPreferencesKey

/**
 * DataStore preference keys
 * Centralized location for all preference keys
 */
object PreferencesKeys {
    // User preferences
    val USER_NAME = stringPreferencesKey("user_name")
    val USER_EMAIL = stringPreferencesKey("user_email")
    val USER_ID = stringPreferencesKey("user_id")
    
    // Migraine prediction preferences
    val LAST_PREDICTION_PROBABILITY = doublePreferencesKey("last_prediction_probability")
    val LAST_PREDICTION_TIMESTAMP = longPreferencesKey("last_prediction_timestamp")
    val SELECTED_RISK_FACTOR = stringPreferencesKey("selected_risk_factor")
    
    // Notification preferences
    val NOTIFICATION_ENABLED = booleanPreferencesKey("notification_enabled")
    val FCM_TOKEN = stringPreferencesKey("fcm_token")
    val EXPO_PUSH_TOKEN = stringPreferencesKey("expo_push_token")
    
    // App preferences
    val IS_FIRST_LAUNCH = booleanPreferencesKey("is_first_launch")
    val THEME_MODE = stringPreferencesKey("theme_mode") // "light", "dark", "system"
    val LAST_API_CALL_TIME = longPreferencesKey("last_api_call_time")
    
    // Settings
    val MIN_LOAD_TIME_MS = intPreferencesKey("min_load_time_ms")
    val AUTO_REFRESH_ENABLED = booleanPreferencesKey("auto_refresh_enabled")
    val AUTO_REFRESH_INTERVAL = intPreferencesKey("auto_refresh_interval") // in minutes
}
