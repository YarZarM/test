# React Native to Android Kotlin Migration Plan

## Executive Summary

This document provides a complete mapping of all React Native/Expo files to their Android Kotlin equivalents using Jetpack Compose, MVVM architecture, and modern Android development practices.

**Total Files Analyzed**: 26 files
**Screens**: 3
**Components**: 8
**Hooks**: 3
**Services/Modules**: 2
**Configuration Files**: 5

---

## 1. SCREENS

### 1.1 Home Screen (Main Dashboard)
**File**: `app/(tabs)/index.js`
**Type**: Screen (Main Feature Screen)
**Lines**: 296

#### Kotlin Equivalent
- **Compose Screen**: `ui/screens/HomeScreen.kt`
- **ViewModel**: `ui/viewmodel/HomeViewModel.kt`
- **Repository**: `data/repository/MigraineRepository.kt`
- **Data Models**: 
  - `data/model/MigrainePrediction.kt`
  - `data/model/RiskFactor.kt`
  - `data/model/RecommendedAction.kt`

#### Dependencies
- **Navigation**: Entry point from Tab Navigation
- **State Management**: Uses `HomeViewModel` with `StateFlow`
- **UI Components**: 
  - Custom Circular Progress Indicator
  - Chip/Card components for risk factors
  - Action pills/badges
  - Pull-to-refresh wrapper

#### API Calls to Convert
1. **GET** `http://172.20.10.3:5000/api/v1/latest`
   - **Kotlin**: `ApiService.getLatestPrediction(): Response<MigrainePrediction>`
   - **Method**: Suspend function in Retrofit interface
   - **Response Model**:
     ```kotlin
     data class MigrainePrediction(
         val p_next_hour: Double,
         val top_factors: List<RiskFactor>,
         val recommended_actions: List<String>
     )
     ```

2. **POST** `http://172.20.10.3:5000/api/v1/register-token`
   - **Kotlin**: `ApiService.registerPushToken(token: PushTokenRequest): Response<Unit>`
   - **Called**: During ViewModel initialization

#### Assets Used
- **Icons**: Material Icons (home icon)
- **Colors**: 
  - Risk High: `#f87171` (Color(0xFFF87171))
  - Risk Elevated: `#fbbf24` (Color(0xFFFBBF24))
  - Risk Low: `#34d399` (Color(0xFF34D399))
  - Background: `#080808` (Color(0xFF080808))
  - Gradient: `#22d3ee` to `#8b5cf6`

#### Styles/Theme
- Dark theme background (`#080808`)
- Custom typography for percentages
- Circular gauge with SVG-like drawing using Canvas
- Card/chip styling with borders and backgrounds

#### State Management
- **Loading State**: `isLoading: Boolean`
- **Refreshing State**: `isRefreshing: Boolean` (separate from loading)
- **Data State**: `migraineData: MigrainePrediction?`
- **Selected Factor**: `selectedFactor: String?`
- **Error State**: `error: String?`

#### Business Logic
- **Risk Calculation**: `p_next_hour * 100` → percentage
- **Risk Level Classification**:
  - High: ≥70%
  - Elevated: 30-69%
  - Low: <30%
- **Feature Name Mapping**: 
  - "stress" → "Stress"
  - "workload" → "Workload"
  - "hrv" → "HRV"
- **Minimum Load Time**: 900ms artificial delay (remove in Kotlin)

#### Navigation Relationships
- **Parent**: Tab Navigation (`(tabs)/_layout.tsx`)
- **Siblings**: Explore Screen
- **No child navigation** (self-contained)

---

### 1.2 Explore Screen
**File**: `app/(tabs)/explore.tsx`
**Type**: Screen (Information/Help Screen)
**Lines**: 113

#### Kotlin Equivalent
- **Compose Screen**: `ui/screens/ExploreScreen.kt`
- **ViewModel**: Not required (static content)
- **Components**: 
  - `ui/components/CollapsibleSection.kt` (reusable)

#### Dependencies
- **Navigation**: Tab Navigation
- **UI Components**:
  - ParallaxScrollView equivalent
  - Collapsible sections
  - External links (WebView or Intent)
  - Image display

#### API Calls
- **None** - Static informational content

#### Assets Used
- **Images**: `react-logo.png` (from assets/images/)
- **Icons**: Chevron icons for collapsible sections
- **Colors**: Theme colors from `constants/theme.ts`

#### Styles/Theme
- Parallax header effect
- Collapsible section styling
- Link styling
- Image display

#### Navigation Relationships
- **Parent**: Tab Navigation
- **Siblings**: Home Screen
- **No child navigation**

---

### 1.3 Modal Screen
**File**: `app/modal.tsx`
**Type**: Screen (Modal/Dialog Screen)
**Lines**: 30

#### Kotlin Equivalent
- **Compose Screen**: `ui/screens/ModalScreen.kt`
- **ViewModel**: Not required
- **Navigation**: Modal presentation in Navigation Compose

#### Dependencies
- **Navigation**: Stack navigation (modal presentation)
- **UI Components**: ThemedView, ThemedText equivalents

#### API Calls
- **None**

#### Assets Used
- **None**

#### Styles/Theme
- Centered layout
- Theme-aware background and text

#### Navigation Relationships
- **Parent**: Root Stack Navigation
- **Presentation**: Modal style
- **Can dismiss**: Yes (back navigation)

---

## 2. NAVIGATION

### 2.1 Root Layout
**File**: `app/_layout.tsx`
**Type**: Navigation Configuration
**Lines**: 36

#### Kotlin Equivalent
- **Navigation Setup**: `navigation/AppNavigation.kt` (already exists, needs enhancement)
- **Theme Provider**: `ui/theme/AppTheme.kt`
- **Notification Handler**: `data/notifications/NotificationHandler.kt`

#### Dependencies
- **Stack Navigation**: Expo Router Stack → Navigation Compose NavHost
- **Theme Provider**: React Navigation ThemeProvider → Material3 Theme
- **Notification Setup**: Expo Notifications → Android NotificationManager

#### Configuration
- **Notification Handler**: Configure notification behavior
- **Theme Detection**: System theme detection
- **Status Bar**: Status bar styling

#### Navigation Structure
```
RootLayout (Stack)
├── (tabs) - Tab Navigation
│   ├── index (Home)
│   └── explore
└── modal - Modal Screen
```

**Kotlin Navigation Graph**:
```kotlin
NavHost(
    startDestination = Screen.Tabs.route
) {
    composable(Screen.Tabs.route) { TabNavigation() }
    composable(Screen.Modal.route) { ModalScreen() }
}
```

---

### 2.2 Tab Layout
**File**: `app/(tabs)/_layout.tsx`
**Type**: Navigation Configuration (Tab Bar)
**Lines**: 36

#### Kotlin Equivalent
- **Tab Navigation**: `navigation/TabNavigation.kt`
- **Tab Bar**: Custom bottom navigation bar using Compose

#### Dependencies
- **Tab Icons**: Material Icons
- **Theme Colors**: From theme constants
- **Haptic Feedback**: Android HapticFeedback

#### Tab Configuration
1. **Home Tab**
   - Route: `home`
   - Icon: `house.fill` → Material Icons `home`
   - Title: "Home"

2. **Explore Tab**
   - Route: `explore`
   - Icon: `paperplane.fill` → Material Icons `send`
   - Title: "Explore"

#### Assets Used
- **Icons**: Material Icons library
- **Colors**: Theme tint colors

---

## 3. COMPONENTS

### 3.1 ThemedText
**File**: `components/themed-text.tsx`
**Type**: UI Component (Reusable)
**Lines**: 61

#### Kotlin Equivalent
- **Compose Component**: `ui/components/ThemedText.kt`
- **Text Styles**: `ui/theme/Typography.kt` (extend existing)

#### Dependencies
- **Theme**: Material3 Typography
- **Color Scheme**: System theme detection

#### Text Variants
- `default`: Body text
- `title`: Large heading (32sp)
- `defaultSemiBold`: Body with semibold weight
- `subtitle`: Medium heading (20sp)
- `link`: Link styling

#### Kotlin Implementation
```kotlin
@Composable
fun ThemedText(
    text: String,
    type: TextType = TextType.Default,
    modifier: Modifier = Modifier,
    color: Color? = null
)
```

---

### 3.2 ThemedView
**File**: `components/themed-view.tsx`
**Type**: UI Component (Reusable)
**Lines**: 15

#### Kotlin Equivalent
- **Compose Component**: `ui/components/ThemedSurface.kt` or use `Surface` with theme

#### Dependencies
- **Theme**: Material3 Surface colors
- **Color Scheme**: Light/dark mode

#### Kotlin Implementation
- Use Material3 `Surface` with theme colors
- Or create custom `ThemedBox` composable

---

### 3.3 HapticTab
**File**: `components/haptic-tab.tsx`
**Type**: UI Component (Navigation Enhancement)
**Lines**: 19

#### Kotlin Equivalent
- **Compose Modifier**: `ui/components/HapticModifier.kt`
- **Usage**: Apply to tab bar buttons

#### Dependencies
- **Haptic Feedback**: Android `HapticFeedback`
- **Platform**: Android-specific

#### Kotlin Implementation
```kotlin
fun Modifier.hapticFeedback(): Modifier = composed {
    clickable(
        onClick = { /* navigation */ },
        indication = rememberRipple(),
        interactionSource = remember { MutableInteractionSource() }
    ) {
        // Trigger haptic feedback
        LocalHapticFeedback.current.performHapticFeedback(
            HapticFeedbackType.LongPress
        )
    }
}
```

---

### 3.4 ParallaxScrollView
**File**: `components/parallax-scroll-view.tsx`
**Type**: UI Component (Animated Container)
**Lines**: 80

#### Kotlin Equivalent
- **Compose Component**: `ui/components/ParallaxScrollView.kt`
- **Animation**: Use `LazyColumn` with `scrollState` and `Modifier.parallax()`

#### Dependencies
- **Reanimated**: React Native Reanimated → Compose Animation APIs
- **Scroll State**: `LazyListState`
- **Animation**: `Animatable`, `animateScrollOffsetAsState`

#### Features
- Parallax header image
- Scale transformation on scroll
- Header height: 250dp

#### Kotlin Implementation
- Use `LazyColumn` with custom header
- Apply `graphicsLayer` with transform based on scroll offset
- Use `derivedStateOf` for scroll-based animations

---

### 3.5 Collapsible
**File**: `components/ui/collapsible.tsx`
**Type**: UI Component (Interactive)
**Lines**: 46

#### Kotlin Equivalent
- **Compose Component**: `ui/components/CollapsibleSection.kt`

#### Dependencies
- **Icons**: Material Icons (chevron)
- **Animation**: AnimatedVisibility for expand/collapse
- **Theme**: Theme colors

#### Features
- Expandable/collapsible content
- Animated chevron rotation
- Theme-aware styling

#### Kotlin Implementation
```kotlin
@Composable
fun CollapsibleSection(
    title: String,
    content: @Composable () -> Unit
) {
    var expanded by remember { mutableStateOf(false) }
    // AnimatedVisibility with rotation animation
}
```

---

### 3.6 IconSymbol
**Files**: 
- `components/ui/icon-symbol.tsx` (Android/Web fallback)
- `components/ui/icon-symbol.ios.tsx` (iOS native)

**Type**: UI Component (Icon Wrapper)
**Lines**: 42 (Android), 33 (iOS)

#### Kotlin Equivalent
- **Compose Component**: Use Material Icons directly
- **Icon Mapping**: Create icon name constants

#### Dependencies
- **Material Icons**: `androidx.compose.material:material-icons-extended`
- **Icon Mapping**: Map SF Symbols names to Material Icons

#### Icon Mappings
- `house.fill` → `Icons.Default.Home`
- `paperplane.fill` → `Icons.Default.Send`
- `chevron.left.forwardslash.chevron.right` → `Icons.Default.Code`
- `chevron.right` → `Icons.Default.ChevronRight`

#### Kotlin Implementation
- Use Material Icons directly in Compose
- Create extension function for icon name mapping if needed

---

### 3.7 ExternalLink
**File**: `components/external-link.tsx`
**Type**: UI Component (Link Handler)
**Lines**: 26

#### Kotlin Equivalent
- **Compose Component**: `ui/components/ExternalLink.kt`
- **Intent Handler**: Use Android `Intent.ACTION_VIEW`

#### Dependencies
- **Web Browser**: Chrome Custom Tabs or Intent
- **Linking**: Android Intent system

#### Features
- Opens URLs in external browser
- Handles web vs native differently

#### Kotlin Implementation
```kotlin
@Composable
fun ExternalLink(
    url: String,
    text: String,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    TextButton(
        onClick = {
            val intent = Intent(Intent.ACTION_VIEW, Uri.parse(url))
            context.startActivity(intent)
        }
    ) {
        Text(text)
    }
}
```

---

### 3.8 HelloWave
**File**: `components/hello-wave.tsx`
**Type**: UI Component (Animated)
**Lines**: 19

#### Kotlin Equivalent
- **Compose Component**: `ui/components/HelloWave.kt`
- **Animation**: Compose Animation API

#### Dependencies
- **Animation**: `animateFloatAsState`, `infiniteRepeatable`

#### Features
- Animated waving hand emoji
- Rotation animation (25deg, 4 iterations, 300ms)

#### Kotlin Implementation
- Use `rotate` modifier with animated rotation
- `infiniteRepeatable` animation

---

## 4. HOOKS

### 4.1 useColorScheme
**Files**: 
- `hooks/use-color-scheme.ts` (Native)
- `hooks/use-color-scheme.web.ts` (Web-specific)

**Type**: Custom Hook
**Lines**: 1 (native), 22 (web)

#### Kotlin Equivalent
- **Compose Function**: `ui/theme/ThemeUtils.kt`
- **System Theme Detection**: `Configuration.UI_MODE_NIGHT_YES`

#### Dependencies
- **System Configuration**: Android `Configuration`
- **Theme**: Material3 `isSystemInDarkTheme()`

#### Kotlin Implementation
```kotlin
@Composable
fun isDarkTheme(): Boolean {
    val configuration = LocalConfiguration.current
    return (configuration.uiMode and Configuration.UI_MODE_NIGHT_MASK) ==
           Configuration.UI_MODE_NIGHT_YES
}
```

**Note**: Material3 Theme already provides `isSystemInDarkTheme()` composable

---

### 4.2 useThemeColor
**File**: `hooks/use-theme-color.ts`
**Type**: Custom Hook
**Lines**: 22

#### Kotlin Equivalent
- **Compose Function**: Use Material3 `MaterialTheme.colorScheme` directly
- **Custom Colors**: Define in `ui/theme/Color.kt`

#### Dependencies
- **Theme**: Material3 ColorScheme
- **Color Constants**: Theme color definitions

#### Kotlin Implementation
- Material3 provides `MaterialTheme.colorScheme.primary`, etc.
- For custom colors, use `LocalContentColor` or define in Color.kt

---

## 5. SERVICES & MODULES

### 5.1 Push Notification Registration
**File**: `app/notifications/registerPush.js`
**Type**: Service Module
**Lines**: 43

#### Kotlin Equivalent
- **Service**: `data/notifications/PushNotificationService.kt`
- **Repository**: `data/repository/NotificationRepository.kt`
- **ViewModel**: `ui/viewmodel/NotificationViewModel.kt` (if needed)

#### Dependencies
- **Firebase Cloud Messaging**: FCM SDK
- **Expo Push Token**: Convert to FCM token
- **API Service**: Retrofit for token registration
- **Permissions**: Android notification permissions

#### API Calls to Convert
1. **POST** `http://172.20.10.3:5000/api/register-token`
   - **Kotlin**: `ApiService.registerNotificationToken(request: NotificationTokenRequest): Response<Unit>`
   - **Request Model**:
     ```kotlin
     data class NotificationTokenRequest(
         val user_id: String,
         val fcm_token: String
     )
     ```

#### Android Implementation
- Use Firebase Cloud Messaging (FCM)
- Request notification permissions (Android 13+)
- Create notification channel
- Get FCM token
- Register token with backend

#### Configuration
- **Notification Channel**: "default"
- **Importance**: MAX
- **Vibration Pattern**: [0, 250, 250, 250]
- **Light Color**: `#E6F4FE`

---

### 5.2 Notification Listeners
**File**: `app/notifications/onNotification.js`
**Type**: Service Module
**Lines**: 40

#### Kotlin Equivalent
- **Service**: `data/notifications/NotificationHandler.kt`
- **Broadcast Receiver**: `data/notifications/NotificationReceiver.kt`
- **Foreground Service**: If needed for background notifications

#### Dependencies
- **Firebase Messaging**: `FirebaseMessagingService`
- **Notification Manager**: Android `NotificationManager`
- **Navigation**: Deep linking from notifications

#### Features
- Foreground notification handling
- Background notification handling
- Notification tap handling
- Deep linking support

#### Kotlin Implementation
```kotlin
class MyFirebaseMessagingService : FirebaseMessagingService() {
    override fun onMessageReceived(remoteMessage: RemoteMessage) {
        // Handle foreground notifications
    }
    
    override fun onNewToken(token: String) {
        // Handle token refresh
    }
}
```

---

## 6. CONSTANTS & CONFIGURATION

### 6.1 Theme Constants
**File**: `constants/theme.ts`
**Type**: Configuration/Constants
**Lines**: 54

#### Kotlin Equivalent
- **Theme Colors**: `ui/theme/Color.kt` (extend existing)
- **Typography**: `ui/theme/Type.kt` (extend existing)

#### Color Mappings
```kotlin
object AppColors {
    // Light theme
    object Light {
        val text = Color(0xFF11181C)
        val background = Color(0xFFFFFFFF)
        val tint = Color(0xFF0A7EA4)
        val icon = Color(0xFF687076)
        val tabIconDefault = Color(0xFF687076)
        val tabIconSelected = Color(0xFF0A7EA4)
    }
    
    // Dark theme
    object Dark {
        val text = Color(0xFFECEDEE)
        val background = Color(0xFF151718)
        val tint = Color(0xFFFFFFFF)
        val icon = Color(0xFF9BA1A6)
        val tabIconDefault = Color(0xFF9BA1A6)
        val tabIconSelected = Color(0xFFFFFFFF)
    }
}
```

#### Font Mappings
- iOS-specific fonts not needed (Android uses system fonts)
- Use Material3 Typography system

---

### 6.2 App Configuration
**File**: `app.json`
**Type**: Configuration
**Lines**: 66

#### Kotlin Equivalent
- **AndroidManifest.xml**: Already exists, may need updates
- **Build Configuration**: `build.gradle.kts`
- **String Resources**: `res/values/strings.xml`

#### Configuration Mappings
- **App Name**: "migraine-app" → `strings.xml`
- **Package Name**: `com.hps2025.migraineapp` → Already in manifest
- **Version**: "1.0.0" → `build.gradle.kts`
- **Orientation**: Portrait → `AndroidManifest.xml`
- **Icons**: Android adaptive icons → `res/mipmap-*`
- **Splash Screen**: Configure in theme/styles
- **Notification Icon**: `res/drawable/notification_icon.xml`

#### Android-Specific Settings
- **Edge-to-Edge**: Already enabled in manifest
- **Predictive Back**: Disabled → Keep disabled
- **Google Services**: `google-services.json` (if using FCM)

---

### 6.3 EAS Build Configuration
**File**: `eas.json`
**Type**: Build Configuration
**Lines**: 28

#### Kotlin Equivalent
- **Gradle Build Variants**: `build.gradle.kts`
- **Build Types**: `debug`, `release`, `staging` (if needed)

#### Build Profile Mappings
- **Development**: Debug build type
- **Preview**: Staging/release build
- **Production**: Release build with signing

---

### 6.4 TypeScript Configuration
**File**: `tsconfig.json`
**Type**: Build Configuration
**Lines**: 17

#### Kotlin Equivalent
- **Kotlin Compiler Options**: `build.gradle.kts`
- **Lint Configuration**: Detekt or Android Lint

---

### 6.5 Package Configuration
**File**: `package.json`
**Type**: Dependency Configuration
**Lines**: 54

#### Kotlin Equivalent
- **Dependencies**: `app/build.gradle.kts` (already exists)
- **Scripts**: Gradle tasks

#### Dependency Mappings
- **Expo Router** → Navigation Compose (already added)
- **React Navigation** → Navigation Compose (already added)
- **Axios** → Retrofit (already added)
- **Expo Notifications** → Firebase Cloud Messaging
- **React Native Reanimated** → Compose Animation APIs
- **Expo Haptics** → Android HapticFeedback

---

## 7. ASSETS

### 7.1 Images
**Location**: `assets/images/`

#### Assets to Migrate
1. **App Icons**:
   - `icon.png` → Android adaptive icons
   - `android-icon-foreground.png` → Already configured
   - `android-icon-background.png` → Already configured
   - `android-icon-monochrome.png` → Optional

2. **UI Images**:
   - `react-logo.png` → `res/drawable/react_logo.png`
   - `react-logo@2x.png` → `res/drawable-hdpi/react_logo.png`
   - `react-logo@3x.png` → `res/drawable-xhdpi/react_logo.png`
   - `partial-react-logo.png` → `res/drawable/partial_react_logo.png`

3. **System Images**:
   - `favicon.png` → Not needed (Android)
   - `splash-icon.png` → Splash screen drawable
   - `notification-icon.png` → `res/drawable/notification_icon.xml`

#### Asset Conversion
- **PNG to Vector**: Convert simple icons to XML drawables
- **Density Variants**: Create mdpi, hdpi, xhdpi, xxhdpi, xxxhdpi variants
- **Adaptive Icons**: Already configured in manifest

---

## 8. API SERVICE MAPPING

### 8.1 API Base Configuration
**Current**: Hardcoded `http://172.20.10.3:5000`
**Kotlin**: Use BuildConfig or environment variables

#### Kotlin Implementation
```kotlin
object ApiConfig {
    // Use BuildConfig for different environments
    const val BASE_URL = BuildConfig.API_BASE_URL
    // Or use environment-specific config
}
```

#### Build Variants
- **Debug**: Local development URL
- **Staging**: Staging server URL
- **Production**: Production server URL

---

### 8.2 API Endpoints Summary

| Endpoint | Method | React Native | Kotlin Retrofit |
|----------|--------|--------------|-----------------|
| `/api/v1/latest` | GET | `fetch()` | `suspend fun getLatestPrediction()` |
| `/api/v1/register-token` | POST | `fetch()` | `suspend fun registerPushToken()` |
| `/api/register-token` | POST | `axios.post()` | `suspend fun registerNotificationToken()` |

#### Data Models Required
```kotlin
// MigrainePrediction.kt
data class MigrainePrediction(
    @SerializedName("p_next_hour") val probability: Double,
    @SerializedName("top_factors") val topFactors: List<RiskFactor>,
    @SerializedName("recommended_actions") val recommendedActions: List<String>
)

// RiskFactor.kt
data class RiskFactor(
    @SerializedName("feature") val feature: String,
    @SerializedName("score") val score: Double
)

// PushTokenRequest.kt
data class PushTokenRequest(
    @SerializedName("expoPushToken") val expoPushToken: String
)

// NotificationTokenRequest.kt
data class NotificationTokenRequest(
    @SerializedName("user_id") val userId: String,
    @SerializedName("fcm_token") val fcmToken: String
)
```

---

## 9. STATE MANAGEMENT MAPPING

### 9.1 React Native State → Kotlin StateFlow

| React Native | Kotlin Equivalent |
|--------------|-------------------|
| `useState()` | `mutableStateOf()` or `StateFlow` |
| `useEffect()` | `LaunchedEffect()` or `collectAsState()` |
| Component State | ViewModel + StateFlow |
| Props | Composable Parameters |

### 9.2 State Management Architecture

```
React Native Component
    ↓
Kotlin ViewModel (StateFlow)
    ↓
Repository (Suspend functions)
    ↓
API Service (Retrofit)
```

---

## 10. NAVIGATION MAPPING

### 10.1 Navigation Structure

| React Native | Kotlin Equivalent |
|--------------|-------------------|
| Expo Router Stack | Navigation Compose NavHost |
| Tab Navigator | Bottom Navigation Bar |
| Modal Presentation | Dialog or Modal Bottom Sheet |
| Deep Linking | Navigation Deep Link |

### 10.2 Route Mappings

```kotlin
sealed class Screen(val route: String) {
    object Home : Screen("home")
    object Explore : Screen("explore")
    object Modal : Screen("modal")
    
    // Tab group
    object Tabs : Screen("tabs")
}
```

---

## 11. STYLING MAPPING

### 11.1 StyleSheet → Compose Styling

| React Native | Kotlin Compose |
|--------------|----------------|
| `StyleSheet.create()` | `Modifier` chains |
| Inline styles | Composable parameters |
| Theme colors | `MaterialTheme.colorScheme` |
| Typography | `MaterialTheme.typography` |

### 11.2 Custom Styles

- **Circular Gauge**: Use `Canvas` with `drawArc()`
- **Chips**: Use `Surface` with `RoundedCornerShape()`
- **Cards**: Use `Card` composable
- **Gradients**: Use `Brush.linearGradient()`

---

## 12. ANIMATION MAPPING

### 12.1 Animation Libraries

| React Native | Kotlin Compose |
|--------------|----------------|
| `react-native-reanimated` | Compose Animation APIs |
| `Animated.View` | `AnimatedVisibility`, `animate*AsState` |
| `useAnimatedStyle` | `animate*AsState` with `Modifier.graphicsLayer` |

### 12.2 Animation Examples

- **Parallax Scroll**: `LazyListState` + `derivedStateOf` + `graphicsLayer`
- **Collapsible**: `AnimatedVisibility` with `expandVertically()`
- **HelloWave**: `infiniteRepeatable` with `rotate` modifier
- **Loading States**: `CircularProgressIndicator` (built-in)

---

## 13. PERMISSIONS MAPPING

### 13.1 Required Permissions

| React Native | Android Manifest |
|--------------|------------------|
| Notification permissions (runtime) | `<uses-permission android:name="android.permission.POST_NOTIFICATIONS" />` (Android 13+) |
| Internet | Already in manifest |
| Network State | Already in manifest |

### 13.2 Runtime Permissions

- **Notifications**: Request in `NotificationRepository` or `MainActivity`
- Use `ActivityCompat.requestPermissions()` or Compose permission launcher

---

## 14. DEPENDENCY MAPPING SUMMARY

### 14.1 Core Dependencies

| React Native Package | Android/Kotlin Equivalent | Status |
|---------------------|---------------------------|--------|
| `expo-router` | Navigation Compose | ✅ Already added |
| `@react-navigation/native` | Navigation Compose | ✅ Already added |
| `axios` | Retrofit | ✅ Already added |
| `expo-notifications` | Firebase Cloud Messaging | ❌ Need to add |
| `react-native-reanimated` | Compose Animation | ✅ Built-in |
| `expo-haptics` | Android HapticFeedback | ✅ Built-in |
| `expo-linear-gradient` | `Brush.linearGradient()` | ✅ Built-in |

### 14.2 Additional Dependencies Needed

```kotlin
// Firebase Cloud Messaging
implementation("com.google.firebase:firebase-messaging-ktx:23.4.0")
implementation("com.google.firebase:firebase-bom:32.7.0")

// For environment variables (optional)
implementation("io.github.cdimascio:dotenv-kotlin:3.0.1")

// For image loading (if needed)
implementation("io.coil-kt:coil-compose:2.5.0")
```

---

## 15. MIGRATION PRIORITY

### Phase 1: Core Infrastructure (Week 1)
1. ✅ Project structure (already created)
2. ✅ Navigation setup (already created)
3. ✅ API service layer (already created)
4. ⚠️ Add Firebase Cloud Messaging
5. ⚠️ Environment configuration

### Phase 2: Main Features (Week 2)
1. Home Screen (migraine dashboard)
2. Circular gauge component
3. Risk factor chips
4. API integration
5. Pull-to-refresh

### Phase 3: Supporting Features (Week 3)
1. Explore Screen
2. Modal Screen
3. Push notifications
4. Theme system
5. Reusable components

### Phase 4: Polish & Testing (Week 4)
1. Animations
2. Error handling
3. Loading states
4. Testing
5. Performance optimization

---

## 16. FILE STRUCTURE MAPPING

### Final Kotlin Project Structure

```
app/src/main/java/com/example/myapp/
├── data/
│   ├── model/
│   │   ├── MigrainePrediction.kt
│   │   ├── RiskFactor.kt
│   │   ├── RecommendedAction.kt
│   │   ├── PushTokenRequest.kt
│   │   └── NotificationTokenRequest.kt
│   ├── remote/
│   │   ├── ApiService.kt (extend existing)
│   │   └── RetrofitClient.kt (update base URL)
│   ├── repository/
│   │   ├── MigraineRepository.kt (new)
│   │   └── NotificationRepository.kt (new)
│   └── notifications/
│       ├── PushNotificationService.kt
│       └── NotificationHandler.kt
├── ui/
│   ├── screens/
│   │   ├── HomeScreen.kt (new)
│   │   ├── ExploreScreen.kt (new)
│   │   └── ModalScreen.kt (new)
│   ├── components/
│   │   ├── CircularGauge.kt (new)
│   │   ├── RiskFactorChip.kt (new)
│   │   ├── ActionPill.kt (new)
│   │   ├── CollapsibleSection.kt (new)
│   │   ├── ParallaxScrollView.kt (new)
│   │   ├── ExternalLink.kt (new)
│   │   └── HelloWave.kt (new)
│   ├── viewmodel/
│   │   ├── HomeViewModel.kt (new)
│   │   └── NotificationViewModel.kt (new)
│   └── theme/
│       ├── Color.kt (extend existing)
│       ├── Type.kt (extend existing)
│       └── Theme.kt (extend existing)
├── navigation/
│   ├── Screen.kt (extend existing)
│   ├── AppNavigation.kt (extend existing)
│   └── TabNavigation.kt (new)
└── MainActivity.kt (update)
```

---

## 17. KEY DIFFERENCES & CONSIDERATIONS

### 17.1 Platform-Specific Features

1. **Notifications**:
   - React Native: Expo Push Notifications
   - Android: Firebase Cloud Messaging (FCM)
   - **Action**: Set up FCM project and configure

2. **Icons**:
   - React Native: SF Symbols (iOS) / Material Icons (Android)
   - Android: Material Icons only
   - **Action**: Use Material Icons directly

3. **Web Support**:
   - React Native: Built-in web support
   - Android: Not applicable
   - **Action**: Remove web-specific code

4. **Haptics**:
   - React Native: Expo Haptics
   - Android: HapticFeedback API
   - **Action**: Use Android HapticFeedback

### 17.2 Code Patterns

1. **Async Operations**:
   - React Native: Promises/async-await
   - Kotlin: Coroutines with suspend functions

2. **State Updates**:
   - React Native: `setState()` triggers re-render
   - Kotlin: `StateFlow` triggers recomposition

3. **Styling**:
   - React Native: StyleSheet objects
   - Kotlin: Modifier chains

4. **Navigation**:
   - React Native: Declarative routing
   - Kotlin: Imperative navigation with type-safe routes

---

## 18. TESTING STRATEGY

### 18.1 Unit Tests
- ViewModels
- Repositories
- Data models
- Utility functions

### 18.2 UI Tests
- Screen navigation
- User interactions
- State changes

### 18.3 Integration Tests
- API calls
- Notification handling
- Data persistence

---

## 19. CONFIGURATION CHANGES

### 19.1 Build Configuration
- Add Firebase configuration
- Environment-specific API URLs
- ProGuard rules for release builds

### 19.2 Manifest Updates
- Notification permissions
- Internet permissions (already present)
- Deep linking configuration (if needed)

---

## 20. SUMMARY CHECKLIST

### Files to Create (New)
- [ ] `data/model/MigrainePrediction.kt`
- [ ] `data/model/RiskFactor.kt`
- [ ] `data/repository/MigraineRepository.kt`
- [ ] `data/repository/NotificationRepository.kt`
- [ ] `data/notifications/PushNotificationService.kt`
- [ ] `ui/screens/HomeScreen.kt`
- [ ] `ui/screens/ExploreScreen.kt`
- [ ] `ui/screens/ModalScreen.kt`
- [ ] `ui/viewmodel/HomeViewModel.kt`
- [ ] `ui/components/CircularGauge.kt`
- [ ] `ui/components/RiskFactorChip.kt`
- [ ] `ui/components/ActionPill.kt`
- [ ] `ui/components/CollapsibleSection.kt`
- [ ] `ui/components/ParallaxScrollView.kt`
- [ ] `navigation/TabNavigation.kt`

### Files to Update (Existing)
- [ ] `data/remote/ApiService.kt` (add new endpoints)
- [ ] `data/remote/RetrofitClient.kt` (update base URL config)
- [ ] `navigation/AppNavigation.kt` (add tab navigation)
- [ ] `navigation/Screen.kt` (add new routes)
- [ ] `ui/theme/Color.kt` (add app-specific colors)
- [ ] `MainActivity.kt` (update navigation setup)
- [ ] `app/build.gradle.kts` (add FCM dependencies)
- [ ] `AndroidManifest.xml` (add notification permissions)

### Assets to Migrate
- [ ] Convert app icons to Android format
- [ ] Convert images to drawable resources
- [ ] Create notification icon drawable
- [ ] Create splash screen drawable

### Configuration
- [ ] Set up Firebase project
- [ ] Configure FCM
- [ ] Add environment configuration
- [ ] Update API base URLs

---

**END OF MIGRATION PLAN**

