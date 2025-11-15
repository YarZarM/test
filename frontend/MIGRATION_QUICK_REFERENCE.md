# Migration Quick Reference

## File-by-File Mapping

| React Native File | Type | Kotlin Equivalent | Priority |
|------------------|------|-------------------|----------|
| `app/(tabs)/index.js` | Screen | `ui/screens/HomeScreen.kt` + `ui/viewmodel/HomeViewModel.kt` | 🔴 High |
| `app/(tabs)/explore.tsx` | Screen | `ui/screens/ExploreScreen.kt` | 🟡 Medium |
| `app/modal.tsx` | Screen | `ui/screens/ModalScreen.kt` | 🟢 Low |
| `app/_layout.tsx` | Navigation | `navigation/AppNavigation.kt` (update) | 🔴 High |
| `app/(tabs)/_layout.tsx` | Navigation | `navigation/TabNavigation.kt` | 🔴 High |
| `app/notifications/registerPush.js` | Service | `data/notifications/PushNotificationService.kt` | 🔴 High |
| `app/notifications/onNotification.js` | Service | `data/notifications/NotificationHandler.kt` | 🔴 High |
| `components/themed-text.tsx` | Component | `ui/components/ThemedText.kt` | 🟡 Medium |
| `components/themed-view.tsx` | Component | Use Material3 `Surface` | 🟢 Low |
| `components/haptic-tab.tsx` | Component | `ui/components/HapticModifier.kt` | 🟢 Low |
| `components/parallax-scroll-view.tsx` | Component | `ui/components/ParallaxScrollView.kt` | 🟡 Medium |
| `components/ui/collapsible.tsx` | Component | `ui/components/CollapsibleSection.kt` | 🟡 Medium |
| `components/ui/icon-symbol.tsx` | Component | Use Material Icons directly | 🟢 Low |
| `components/external-link.tsx` | Component | `ui/components/ExternalLink.kt` | 🟢 Low |
| `components/hello-wave.tsx` | Component | `ui/components/HelloWave.kt` | 🟢 Low |
| `hooks/use-color-scheme.ts` | Hook | Use `isSystemInDarkTheme()` | 🟢 Low |
| `hooks/use-theme-color.ts` | Hook | Use `MaterialTheme.colorScheme` | 🟢 Low |
| `constants/theme.ts` | Config | `ui/theme/Color.kt` (extend) | 🟡 Medium |
| `app.json` | Config | `AndroidManifest.xml` + `build.gradle.kts` | 🔴 High |
| `package.json` | Config | `app/build.gradle.kts` | 🔴 High |

## API Endpoints

| Endpoint | Method | React Native | Kotlin Function |
|----------|--------|--------------|------------------|
| `/api/v1/latest` | GET | `fetch()` | `ApiService.getLatestPrediction()` |
| `/api/v1/register-token` | POST | `fetch()` | `ApiService.registerPushToken()` |
| `/api/register-token` | POST | `axios.post()` | `ApiService.registerNotificationToken()` |

## Data Models Required

```kotlin
MigrainePrediction
├── p_next_hour: Double
├── top_factors: List<RiskFactor>
└── recommended_actions: List<String>

RiskFactor
├── feature: String
└── score: Double

PushTokenRequest
└── expoPushToken: String

NotificationTokenRequest
├── user_id: String
└── fcm_token: String
```

## Component Dependencies

### HomeScreen Dependencies
- `CircularGauge.kt` - Custom circular progress indicator
- `RiskFactorChip.kt` - Interactive chip component
- `ActionPill.kt` - Action badge component
- `HomeViewModel.kt` - State management
- `MigraineRepository.kt` - Data layer

### ExploreScreen Dependencies
- `CollapsibleSection.kt` - Expandable sections
- `ParallaxScrollView.kt` - Parallax scroll effect
- `ExternalLink.kt` - External link handler

## Navigation Structure

```
RootLayout (Stack)
├── Tabs (Bottom Navigation)
│   ├── Home
│   └── Explore
└── Modal
```

## Key Conversions

| React Native | Kotlin Compose |
|--------------|----------------|
| `useState()` | `mutableStateOf()` |
| `useEffect()` | `LaunchedEffect()` |
| `StyleSheet` | `Modifier` chains |
| `Animated.View` | `AnimatedVisibility` |
| `fetch()` | Retrofit suspend functions |
| Expo Notifications | Firebase Cloud Messaging |

## Priority Order

1. **Phase 1**: Core infrastructure (Navigation, API, FCM)
2. **Phase 2**: Home Screen (main feature)
3. **Phase 3**: Supporting screens and components
4. **Phase 4**: Polish and testing

