package com.example.myapp.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.myapp.data.local.MigrainePreferencesManager
import com.example.myapp.data.model.MigrainePrediction
import com.example.myapp.data.model.RiskFactor
import com.example.myapp.data.repository.MigraineRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch

data class HomeUiState(
    val isLoading: Boolean = false,
    val isRefreshing: Boolean = false,
    val migraineData: MigrainePrediction? = null,
    val selectedFactor: String? = null,
    val error: String? = null,
    val hasCachedData: Boolean = false
) {
    val percentage: Int
        get() = migraineData?.let { (it.probability * 100).toInt() } ?: 0
    
    val riskLevel: RiskLevel
        get() = when {
            percentage >= 70 -> RiskLevel.HIGH
            percentage >= 30 -> RiskLevel.ELEVATED
            else -> RiskLevel.LOW
        }
    
    val ringColor: Long
        get() = when (riskLevel) {
            RiskLevel.HIGH -> 0xFFF87171
            RiskLevel.ELEVATED -> 0xFFFBBF24
            RiskLevel.LOW -> 0xFF34D399
        }
    
    val filteredActions: List<String>
        get() = if (selectedFactor != null && migraineData != null) {
            migraineData.recommendedActions
        } else {
            emptyList()
        }
}

enum class RiskLevel {
    HIGH, ELEVATED, LOW
}

class HomeViewModel(
    private val repository: MigraineRepository,
    private val preferencesManager: MigrainePreferencesManager
) : ViewModel() {
    
    private val _uiState = MutableStateFlow(HomeUiState())
    val uiState: StateFlow<HomeUiState> = _uiState.asStateFlow()
    
    init {
        loadCachedData()
        loadData(isRefresh = false)
        observeSelectedFactor()
    }
    
    /**
     * Load cached prediction data from DataStore
     */
    private fun loadCachedData() {
        viewModelScope.launch {
            val hasCached = preferencesManager.hasCachedPrediction()
            val cachedProbability = preferencesManager.getLastPredictionProbability()
            val cachedTimestamp = preferencesManager.getLastPredictionTimestamp()
            
            _uiState.value = _uiState.value.copy(
                hasCachedData = hasCached
            )
            
            // If we have cached data and it's recent (less than 1 hour old), show it
            if (hasCached && cachedTimestamp != null) {
                val age = System.currentTimeMillis() - cachedTimestamp
                if (age < 3600000) { // 1 hour
                    cachedProbability?.let { prob ->
                        // Create a minimal prediction from cached data
                        // This provides offline support
                    }
                }
            }
        }
    }
    
    /**
     * Observe selected risk factor from DataStore
     */
    private fun observeSelectedFactor() {
        viewModelScope.launch {
            preferencesManager.selectedRiskFactor.collect { factor ->
                if (factor != null) {
                    _uiState.value = _uiState.value.copy(selectedFactor = factor)
                }
            }
        }
    }
    
    fun loadData(isRefresh: Boolean = false) {
        viewModelScope.launch {
            if (isRefresh) {
                _uiState.value = _uiState.value.copy(isRefreshing = true, error = null)
            } else {
                _uiState.value = _uiState.value.copy(isLoading = true, error = null)
            }
            
            repository.getLatestPrediction()
                .onSuccess { data ->
                    // Save to DataStore for offline access
                    preferencesManager.saveLastPrediction(data.probability)
                    
                    _uiState.value = _uiState.value.copy(
                        isLoading = false,
                        isRefreshing = false,
                        migraineData = data,
                        selectedFactor = _uiState.value.selectedFactor, // Preserve selection
                        hasCachedData = true
                    )
                }
                .onFailure { exception ->
                    _uiState.value = _uiState.value.copy(
                        isLoading = false,
                        isRefreshing = false,
                        error = exception.message ?: "Unknown error occurred"
                    )
                }
        }
    }
    
    fun selectFactor(feature: String) {
        val currentState = _uiState.value
        val newSelection = if (currentState.selectedFactor == feature) null else feature
        
        _uiState.value = currentState.copy(selectedFactor = newSelection)
        
        // Persist selection to DataStore
        viewModelScope.launch {
            preferencesManager.saveSelectedFactor(newSelection)
        }
    }
    
    fun clearError() {
        _uiState.value = _uiState.value.copy(error = null)
    }
    
    fun getFeatureName(feature: String): String {
        return when {
            feature.contains("stress", ignoreCase = true) -> "Stress"
            feature.contains("workload", ignoreCase = true) -> "Workload"
            feature.contains("hrv", ignoreCase = true) -> "HRV"
            else -> feature
        }
    }
    
    fun getFeatureEmoji(feature: String): String {
        return when {
            feature.contains("hrv", ignoreCase = true) -> "💓"
            feature.contains("stress", ignoreCase = true) -> "😮‍💨"
            else -> "📅"
        }
    }
    
    fun getFactorColor(score: Double): Long {
        return when {
            score > 0.35 -> 0xFFF87171
            score > 0.2 -> 0xFFFBBF24
            else -> 0xFF34D399
        }
    }
    
    fun getFactorArrow(score: Double): String {
        return if (score >= 0.5) "↑" else "↓"
    }
}

