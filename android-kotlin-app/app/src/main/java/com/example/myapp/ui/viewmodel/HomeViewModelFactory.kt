package com.example.myapp.ui.viewmodel

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.myapp.di.AppModule
import com.example.myapp.data.repository.MigraineRepository

/**
 * Factory for creating HomeViewModel with dependencies
 */
class HomeViewModelFactory(
    private val context: Context
) : ViewModelProvider.Factory {
    
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(HomeViewModel::class.java)) {
            val repository = AppModule.provideMigraineRepository(context)
            val preferencesManager = AppModule.provideMigrainePreferencesManager(context)
            @Suppress("UNCHECKED_CAST")
            return HomeViewModel(repository, preferencesManager) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class: ${modelClass.name}")
    }
}

