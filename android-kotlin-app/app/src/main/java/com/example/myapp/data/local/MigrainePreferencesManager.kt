package com.example.myapp.data.local

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first

/**
 * Helper class for managing migraine prediction preferences
 * Stores last prediction data and selected factors
 */
class MigrainePreferencesManager(
    private val dataStoreManager: DataStoreManager
) {
    suspend fun saveLastPrediction(probability: Double) {
        dataStoreManager.saveLastPrediction(probability)
    }
    
    suspend fun saveSelectedFactor(factor: String?) {
        dataStoreManager.saveSelectedRiskFactor(factor)
    }
    
    suspend fun clearPredictionData() {
        dataStoreManager.clearPredictionData()
    }
    
    suspend fun getLastPredictionTimestamp(): Long? {
        return dataStoreManager.lastPredictionTimestamp.first()
    }
    
    suspend fun getLastPredictionProbability(): Double? {
        return dataStoreManager.lastPredictionProbability.first()
    }
    
    suspend fun getSelectedRiskFactor(): String? {
        return dataStoreManager.selectedRiskFactor.first()
    }
    
    val lastPredictionProbability: Flow<Double?> = dataStoreManager.lastPredictionProbability
    val lastPredictionTimestamp: Flow<Long?> = dataStoreManager.lastPredictionTimestamp
    val selectedRiskFactor: Flow<String?> = dataStoreManager.selectedRiskFactor
    
    /**
     * Check if we have cached prediction data
     */
    suspend fun hasCachedPrediction(): Boolean {
        return getLastPredictionTimestamp() != null
    }
}

