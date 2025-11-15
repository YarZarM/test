package com.example.myapp.di

import android.content.Context
import com.example.myapp.data.local.AppPreferencesManager
import com.example.myapp.data.local.DataStoreManager
import com.example.myapp.data.local.MigrainePreferencesManager
import com.example.myapp.data.local.NotificationPreferencesManager
import com.example.myapp.data.local.UserPreferencesManager
import com.example.myapp.data.notifications.PushNotificationService
import com.example.myapp.data.remote.ApiService
import com.example.myapp.data.remote.NetworkModule
import com.example.myapp.data.repository.MigraineRepository
import com.example.myapp.data.repository.NotificationRepository

/**
 * Application dependency injection module
 * Provides all dependencies for the app
 */
object AppModule {
    
    /**
     * Provides ApiService
     * Uses NetworkModule for proper configuration
     */
    fun provideApiService(context: Context): ApiService {
        val okHttpClient = NetworkModule.provideOkHttpClient(context)
        val retrofit = NetworkModule.provideRetrofit(okHttpClient)
        return NetworkModule.provideApiService(retrofit)
    }
    
    /**
     * Provides DataStoreManager
     */
    fun provideDataStoreManager(context: Context): DataStoreManager {
        return DataStoreManager(context)
    }
    
    /**
     * Provides MigraineRepository
     */
    fun provideMigraineRepository(context: Context): MigraineRepository {
        val apiService = provideApiService(context)
        return MigraineRepository(apiService)
    }
    
    /**
     * Provides NotificationRepository
     */
    fun provideNotificationRepository(context: Context): NotificationRepository {
        val apiService = provideApiService(context)
        return NotificationRepository(apiService)
    }
    
    // ========== Preference Managers ==========
    
    /**
     * Provides UserPreferencesManager
     */
    fun provideUserPreferencesManager(context: Context): UserPreferencesManager {
        val dataStoreManager = provideDataStoreManager(context)
        return UserPreferencesManager(dataStoreManager)
    }
    
    /**
     * Provides MigrainePreferencesManager
     */
    fun provideMigrainePreferencesManager(context: Context): MigrainePreferencesManager {
        val dataStoreManager = provideDataStoreManager(context)
        return MigrainePreferencesManager(dataStoreManager)
    }
    
    /**
     * Provides NotificationPreferencesManager
     */
    fun provideNotificationPreferencesManager(context: Context): NotificationPreferencesManager {
        val dataStoreManager = provideDataStoreManager(context)
        return NotificationPreferencesManager(dataStoreManager)
    }
    
    /**
     * Provides AppPreferencesManager
     */
    fun provideAppPreferencesManager(context: Context): AppPreferencesManager {
        val dataStoreManager = provideDataStoreManager(context)
        return AppPreferencesManager(dataStoreManager)
    }
    
    /**
     * Initializes notification service
     */
    fun initializeNotifications(context: Context) {
        PushNotificationService.createNotificationChannel(context)
    }
}

/**
 * Extension function to get ApiService from context
 * Convenience method for dependency injection
 */
fun Context.getApiService(): ApiService {
    return AppModule.provideApiService(this)
}

/**
 * Extension function to get MigraineRepository from context
 */
fun Context.getMigraineRepository(): MigraineRepository {
    return AppModule.provideMigraineRepository(this)
}

/**
 * Extension function to get NotificationRepository from context
 */
fun Context.getNotificationRepository(): NotificationRepository {
    return AppModule.provideNotificationRepository(this)
}

