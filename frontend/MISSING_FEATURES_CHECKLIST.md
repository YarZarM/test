# Missing Features & Unused Files Checklist

## 🔴 CRITICAL MISSING FEATURES

### 1. Notification Handler Configuration
- **File**: `MainActivity.kt`
- **Missing**: `Notifications.setNotificationHandler()` equivalent
- **Impact**: Notification behavior not fully configured
- **Priority**: HIGH

### 2. Notification Listeners Setup
- **File**: `MainActivity.kt`
- **Missing**: `setupNotificationListeners()` call
- **Impact**: No foreground/background notification handling
- **Priority**: HIGH

### 3. Notification Tap Handler
- **File**: `PushNotificationService.kt` or `MainActivity.kt`
- **Missing**: Deep linking from notification taps
- **Impact**: Cannot navigate from notifications
- **Priority**: HIGH

---

## 🟡 MISSING COMPONENTS

### 1. HelloWave Component
- **React Native**: `components/hello-wave.tsx`
- **Kotlin**: Not created
- **Status**: ❌ Missing
- **Priority**: LOW (may not be used)

### 2. ParallaxScrollView in ExploreScreen
- **React Native**: Uses `ParallaxScrollView` component
- **Kotlin**: `ExploreScreen.kt` uses basic scroll
- **Status**: ❌ Missing parallax effect
- **Priority**: MEDIUM

### 3. Haptic Feedback in TabNavigation
- **React Native**: `HapticTab` component
- **Kotlin**: `TabNavigation.kt` has no haptic feedback
- **Status**: ❌ Missing
- **Priority**: MEDIUM

---

## 🟠 MISSING LOGIC

### 1. Content-Type Validation
- **Location**: `MigraineRepository.kt`
- **Missing**: Response content-type check
- **Priority**: MEDIUM

### 2. Dual Token Registration
- **Location**: `MainActivity.kt`
- **Missing**: Expo push token registration (Android uses FCM)
- **Priority**: LOW (FCM is sufficient for Android)

---

## 🗑️ UNUSED/REDUNDANT FILES

### Screens (Not in React Native)
1. ❌ **SettingsScreen.kt** - Template screen, not in original app
2. ❌ **UserListScreen.kt** - Template screen, not in original app

### ViewModels (Not in React Native)
3. ❌ **UserViewModel.kt** - Only used by unused screens

### Models (Not in React Native)
4. ❌ **User.kt** - Example model, not used in actual app

### Utilities (Created but Unused)
5. ❌ **ApiResponse.kt** - Generic response wrapper, never used
6. ❌ **RetrofitException.kt** - Custom exception, never used
7. ⚠️ **RetrofitClient.kt** - Replaced by NetworkModule, check if still referenced

### Navigation Routes (Unused)
8. ❌ **Screen.UserList** - Defined but not in navigation graph
9. ❌ **Screen.Settings** - Defined but not in navigation graph

---

## ✅ VERIFICATION CHECKLIST

### Files to Check for Usage
- [ ] Check if `RetrofitClient.kt` is still referenced
- [ ] Check if `User.kt` is used anywhere
- [ ] Check if `ApiResponse.kt` is used anywhere
- [ ] Check if `RetrofitException.kt` is used anywhere

### Features to Verify
- [ ] Notification handler is configured
- [ ] Notification listeners are set up
- [ ] Parallax effect works in ExploreScreen
- [ ] Haptic feedback works in tabs
- [ ] All API endpoints are tested

---

## 📊 QUICK REFERENCE

### Missing from React Native → Kotlin
| Feature | Status | Priority |
|---------|--------|----------|
| Notification Handler | ❌ Missing | HIGH |
| Notification Listeners | ❌ Missing | HIGH |
| Parallax Effect | ❌ Missing | MEDIUM |
| Haptic Feedback | ❌ Missing | MEDIUM |
| HelloWave Component | ❌ Missing | LOW |
| Content-Type Validation | ❌ Missing | MEDIUM |

### Unused Files
| File | Type | Action |
|------|------|--------|
| SettingsScreen.kt | Screen | Remove |
| UserListScreen.kt | Screen | Remove |
| UserViewModel.kt | ViewModel | Remove |
| User.kt | Model | Remove |
| ApiResponse.kt | Model | Remove |
| RetrofitException.kt | Utility | Remove or Use |
| RetrofitClient.kt | Network | Remove if unused |

