package com.example.myapp

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.ui.Modifier
import com.example.myapp.di.AppModule
import com.example.myapp.data.notifications.PushNotificationService
import com.example.myapp.navigation.AppNavigation
import com.example.myapp.ui.theme.MyAppTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        
        // Initialize dependencies using DI module
        AppModule.initializeNotifications(this)
        
        // Register for push notifications using DI
        val notificationRepository = AppModule.provideNotificationRepository(this)
        val notificationPreferencesManager = AppModule.provideNotificationPreferencesManager(this)
        PushNotificationService.registerForPushNotifications(
            this,
            notificationRepository,
            notificationPreferencesManager
        )
        
        setContent {
            MyAppTheme {
                Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
                    AppNavigation(
                        modifier = Modifier.padding(innerPadding)
                    )
                }
            }
        }
    }
}

