# Retrofit API Setup Documentation

## Overview

All API calls from the React Native application have been converted to Retrofit with proper dependency injection, error handling, and data models.

## API Endpoints

### 1. Get Latest Migraine Prediction
- **Endpoint**: `GET /api/v1/latest`
- **Service Method**: `ApiService.getLatestPrediction()`
- **Response Model**: `MigrainePrediction`
- **Repository**: `MigraineRepository.getLatestPrediction()`

**Response Structure**:
```json
{
  "p_next_hour": 0.75,
  "top_factors": [
    {
      "feature": "stress_level",
      "score": 0.85
    }
  ],
  "recommended_actions": ["Take a break", "Drink water"]
}
```

### 2. Register Expo Push Token
- **Endpoint**: `POST /api/v1/register-token`
- **Service Method**: `ApiService.registerPushToken()`
- **Request Model**: `PushTokenRequest`
- **Repository**: `NotificationRepository.registerPushToken()`

**Request Structure**:
```json
{
  "expoPushToken": "ExponentPushToken[...]"
}
```

### 3. Register FCM Token
- **Endpoint**: `POST /api/register-token`
- **Service Method**: `ApiService.registerNotificationToken()`
- **Request Model**: `NotificationTokenRequest`
- **Repository**: `NotificationRepository.registerNotificationToken()`

**Request Structure**:
```json
{
  "user_id": "YZMM",
  "fcm_token": "Firebase Cloud Messaging token"
}
```

## Data Models

### Core Models
- `MigrainePrediction.kt` - Main prediction response
- `RiskFactor.kt` - Risk factor with feature and score
- `PushTokenRequest.kt` - Expo push token request
- `NotificationTokenRequest.kt` - FCM token request

### Utility Models
- `ApiResponse.kt` - Generic API response wrapper
- `ApiError.kt` - Error response model
- `User.kt` - User model (legacy/example)

## Repository Pattern

### MigraineRepository
- Handles migraine prediction API calls
- Implements minimum load time for better UX
- Comprehensive error handling

### NotificationRepository
- Handles push notification token registration
- Supports both Expo and FCM tokens
- Network error handling

## Dependency Injection

### NetworkModule
- Provides `OkHttpClient` with interceptors and caching
- Provides `Retrofit` instance
- Provides `ApiService` interface

### AppModule
- Central dependency injection module
- Provides all repositories
- Provides DataStore manager
- Initializes notification services

### Usage Example
```kotlin
// In ViewModel Factory
val repository = AppModule.provideMigraineRepository(context)

// In Activity
val notificationRepository = AppModule.provideNotificationRepository(this)
```

## Error Handling

### RetrofitException
Custom exception types:
- `HttpError` - HTTP error responses (4xx, 5xx)
- `NetworkError` - Network connectivity issues
- `UnexpectedError` - Unexpected exceptions

### Repository Error Handling
All repositories handle:
- `UnknownHostException` - Server unreachable
- `SocketTimeoutException` - Request timeout
- `IOException` - Network I/O errors
- Generic exceptions

## Network Configuration

### Base URL
Currently: `http://172.20.10.3:5000/`
- Should be moved to BuildConfig for different environments
- Supports debug/release variants

### Timeouts
- Connect: 30 seconds
- Read: 30 seconds
- Write: 30 seconds

### Caching
- HTTP cache: 10 MB
- Cache directory: `context.cacheDir/http_cache`

### Interceptors
- **Logging**: HTTP request/response logging (debug only)
- **Headers**: Automatic Accept and Content-Type headers

## File Structure

```
data/
├── model/
│   ├── MigrainePrediction.kt
│   ├── RiskFactor.kt
│   ├── PushTokenRequest.kt
│   ├── NotificationTokenRequest.kt
│   ├── ApiResponse.kt
│   └── ApiError.kt
├── remote/
│   ├── ApiService.kt
│   ├── RetrofitClient.kt (legacy, use NetworkModule)
│   ├── NetworkModule.kt
│   └── RetrofitException.kt
└── repository/
    ├── MigraineRepository.kt
    └── NotificationRepository.kt

di/
└── AppModule.kt
```

## Migration from React Native

### Before (React Native)
```javascript
const res = await fetch("http://172.20.10.3:5000/api/v1/latest");
const json = await res.json();
```

### After (Kotlin Retrofit)
```kotlin
val repository = AppModule.provideMigraineRepository(context)
val result = repository.getLatestPrediction()
result.onSuccess { data -> /* handle success */ }
result.onFailure { error -> /* handle error */ }
```

## Best Practices

1. **Always use repositories** - Don't call ApiService directly
2. **Use Result type** - All repository methods return `Result<T>`
3. **Handle errors** - Check `Result.isSuccess` before accessing data
4. **Use DI** - Always use AppModule for dependency injection
5. **Test with mock** - Use dependency injection for easy testing

## Testing

### Mock ApiService
```kotlin
val mockApiService = mock<ApiService>()
val repository = MigraineRepository(mockApiService)
```

### Test Network Errors
```kotlin
whenever(mockApiService.getLatestPrediction())
    .thenThrow(UnknownHostException())
```

## Future Improvements

1. **Environment Configuration**
   - Move BASE_URL to BuildConfig
   - Support multiple environments (dev, staging, prod)

2. **Authentication**
   - Add authentication interceptor
   - Token refresh mechanism

3. **Response Caching**
   - Implement response caching strategy
   - Cache invalidation

4. **Retry Logic**
   - Add retry interceptor for failed requests
   - Exponential backoff

5. **Hilt Integration**
   - Consider migrating to Hilt for DI
   - Better testability and scoping

