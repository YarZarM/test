package com.example.myapp.ui.viewmodel

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.myapp.di.AppModule
import com.example.myapp.data.repository.UserRepository

/**
 * Factory for creating UserViewModel with dependencies
 */
class UserViewModelFactory(
    private val context: Context
) : ViewModelProvider.Factory {
    
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(UserViewModel::class.java)) {
            val apiService = AppModule.provideApiService(context)
            val userPreferencesManager = AppModule.provideUserPreferencesManager(context)
            val repository = UserRepository(apiService, userPreferencesManager)
            @Suppress("UNCHECKED_CAST")
            return UserViewModel(repository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class: ${modelClass.name}")
    }
}

