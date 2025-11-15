# DataStore Preferences Setup Documentation

## Overview

All AsyncStorage functionality from React Native has been converted to Android DataStore Preferences. The implementation includes helper classes, preference managers, and full integration with ViewModels.

## Architecture

### Core Components

1. **DataStoreManager** - Main DataStore interface
2. **PreferencesKeys** - Centralized preference keys
3. **Preference Managers** - Specialized managers for different data types
4. **ViewModels** - Integrated with DataStore for state persistence

## Data Types Supported

### User Preferences
- `user_name` (String)
- `user_email` (String)
- `user_id` (String)

### Migraine Prediction Preferences
- `last_prediction_probability` (Double)
- `last_prediction_timestamp` (Long)
- `selected_risk_factor` (String)

### Notification Preferences
- `notification_enabled` (Boolean)
- `fcm_token` (String)
- `expo_push_token` (String)

### App Preferences
- `is_first_launch` (Boolean)
- `theme_mode` (String: "light", "dark", "system")
- `last_api_call_time` (Long)

### Settings Preferences
- `min_load_time_ms` (Int)
- `auto_refresh_enabled` (Boolean)
- `auto_refresh_interval` (Int) - in minutes

## Helper Classes

### UserPreferencesManager
Manages user-related preferences.

```kotlin
val manager = AppModule.provideUserPreferencesManager(context)

// Save user data
manager.saveUser("John Doe", "john@example.com", "user123")

// Observe user name
manager.userName.collect { name ->
    // Handle name changes
}

// Clear user data
manager.clearUser()
```

### MigrainePreferencesManager
Manages migraine prediction data and selections.

```kotlin
val manager = AppModule.provideMigrainePreferencesManager(context)

// Save last prediction
manager.saveLastPrediction(0.75)

// Save selected factor
manager.saveSelectedFactor("stress_level")

// Check for cached data
val hasCached = manager.hasCachedPrediction()

// Get cached data
val probability = manager.getLastPredictionProbability()
val timestamp = manager.getLastPredictionTimestamp()
```

### NotificationPreferencesManager
Manages notification settings and tokens.

```kotlin
val manager = AppModule.provideNotificationPreferencesManager(context)

// Save notification settings
manager.saveNotificationSettings(
    enabled = true,
    fcmToken = "token123",
    expoToken = "expo_token"
)

// Observe notification enabled state
manager.notificationEnabled.collect { enabled ->
    // Handle state changes
}
```

### AppPreferencesManager
Manages app-wide preferences.

```kotlin
val manager = AppModule.provideAppPreferencesManager(context)

// Mark first launch complete
manager.markFirstLaunchComplete()

// Save theme mode
manager.saveThemeMode("dark")

// Check first launch
val isFirst = manager.checkFirstLaunch()
```

## ViewModel Integration

### HomeViewModel Example

```kotlin
class HomeViewModel(
    private val repository: MigraineRepository,
    private val preferencesManager: MigrainePreferencesManager
) : ViewModel() {
    
    init {
        // Load cached data on init
        loadCachedData()
        
        // Observe selected factor from DataStore
        observeSelectedFactor()
    }
    
    fun loadData(isRefresh: Boolean = false) {
        viewModelScope.launch {
            repository.getLatestPrediction()
                .onSuccess { data ->
                    // Save to DataStore for offline access
                    preferencesManager.saveLastPrediction(data.probability)
                    // Update UI state
                }
        }
    }
    
    fun selectFactor(feature: String) {
        // Update UI state
        _uiState.value = _uiState.value.copy(selectedFactor = feature)
        
        // Persist to DataStore
        viewModelScope.launch {
            preferencesManager.saveSelectedFactor(feature)
        }
    }
}
```

## Usage Examples

### Saving Data

```kotlin
// In ViewModel
viewModelScope.launch {
    preferencesManager.saveLastPrediction(0.85)
    preferencesManager.saveSelectedFactor("stress_level")
}
```

### Reading Data

```kotlin
// One-time read
val probability = preferencesManager.getLastPredictionProbability()

// Observe changes
preferencesManager.lastPredictionProbability.collect { prob ->
    // Handle probability changes
}
```

### Observing Data in ViewModel

```kotlin
viewModelScope.launch {
    preferencesManager.selectedRiskFactor.collect { factor ->
        _uiState.value = _uiState.value.copy(selectedFactor = factor)
    }
}
```

## Migration from AsyncStorage

### React Native (AsyncStorage)
```javascript
// Save
await AsyncStorage.setItem('user_name', 'John');

// Read
const name = await AsyncStorage.getItem('user_name');

// Remove
await AsyncStorage.removeItem('user_name');
```

### Kotlin (DataStore)
```kotlin
// Save
dataStoreManager.saveUserName("John")

// Read (Flow)
dataStoreManager.userName.collect { name ->
    // Handle name
}

// One-time read
val name = dataStoreManager.userName.first()

// Remove (via clear)
dataStoreManager.clearUserData()
```

## Error Handling

DataStore automatically handles:
- IOException - Returns empty preferences
- Data corruption - Handled gracefully
- Concurrent access - Thread-safe by default

All Flow operations include error handling:

```kotlin
val userName: Flow<String> = dataStore.data
    .catch { exception ->
        if (exception is IOException) {
            emit(emptyPreferences())
        } else {
            throw exception
        }
    }
    .map { preferences ->
        preferences[PreferencesKeys.USER_NAME] ?: ""
    }
```

## Benefits Over AsyncStorage

1. **Type Safety** - Compile-time type checking
2. **Reactive** - Flow-based API for reactive updates
3. **Thread Safe** - Built-in thread safety
4. **Performance** - More efficient than SharedPreferences
5. **Error Handling** - Better error handling mechanisms
6. **Coroutines** - Native coroutine support

## File Structure

```
data/local/
├── DataStoreManager.kt          # Main DataStore interface
├── PreferencesKeys.kt            # All preference keys
├── UserPreferencesManager.kt     # User preferences helper
├── MigrainePreferencesManager.kt # Prediction preferences helper
├── NotificationPreferencesManager.kt # Notification preferences helper
└── AppPreferencesManager.kt      # App preferences helper

di/
└── AppModule.kt                  # Provides all preference managers

ui/viewmodel/
├── HomeViewModel.kt             # Uses MigrainePreferencesManager
└── UserViewModel.kt             # Uses UserPreferencesManager
```

## Best Practices

1. **Use Preference Managers** - Don't access DataStoreManager directly
2. **Observe Flows** - Use Flow.collect() for reactive updates
3. **Handle Errors** - All Flows include error handling
4. **Clear Data** - Use specific clear methods (clearUserData, clearPredictionData)
5. **Type Safety** - Use typed preference keys from PreferencesKeys

## Testing

### Mock DataStoreManager
```kotlin
val mockDataStore = mock<DataStoreManager>()
val manager = MigrainePreferencesManager(mockDataStore)

// Test saving
coVerify { mockDataStore.saveLastPrediction(0.75) }
```

### Test Flow Values
```kotlin
val testFlow = flowOf("test_name")
whenever(mockDataStore.userName).thenReturn(testFlow)

val result = mockDataStore.userName.first()
assertEquals("test_name", result)
```

## Performance Considerations

- DataStore is optimized for small key-value pairs
- Large data should use Room database instead
- All operations are suspend functions (non-blocking)
- Flow operations are efficient and memory-safe

## Thread Safety

- DataStore operations are thread-safe
- All operations should be called from coroutines
- Flow collections are safe from any thread

