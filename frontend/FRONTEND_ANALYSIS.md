# Frontend Project Analysis

## Overview
This is a **React Native mobile application** built with **Expo** framework, designed as a **migraine prediction and management app**. The project uses modern React Native patterns with TypeScript support and file-based routing.

---

## Technology Stack

### Core Framework
- **Expo SDK**: ~54.0.23
- **React**: 19.1.0
- **React Native**: 0.81.5
- **Expo Router**: ~6.0.14 (file-based routing)
- **TypeScript**: ~5.9.2

### Key Libraries
- **Navigation**: `@react-navigation/native`, `@react-navigation/bottom-tabs`
- **HTTP Client**: `axios` (v1.13.2) for API calls
- **Notifications**: `expo-notifications` for push notifications
- **Animations**: `react-native-reanimated` (v3.6.2)
- **UI Components**: Custom themed components with Material Design support
- **Haptics**: `expo-haptics` for tactile feedback

### Build & Deployment
- **EAS Build**: Configured for development, preview, and production builds
- **Project ID**: `98decf00-8468-4ad1-bcf4-11857c6e5d52`
- **Owner**: `hps2025`
- **Package Name**: `com.hps2025.migraineapp`

---

## Project Structure

```
frontend/
├── app/                          # Expo Router file-based routing
│   ├── _layout.tsx              # Root layout with navigation setup
│   ├── (tabs)/                  # Tab navigation group
│   │   ├── _layout.tsx          # Tab bar configuration
│   │   ├── index.js             # Home screen (main migraine dashboard)
│   │   └── explore.tsx          # Explore/info screen
│   ├── modal.tsx                # Modal screen
│   └── notifications/           # Push notification handlers
│       ├── registerPush.js      # Token registration
│       └── onNotification.js    # Notification listeners
├── components/                   # Reusable UI components
│   ├── themed-text.tsx          # Theme-aware text component
│   ├── themed-view.tsx          # Theme-aware view component
│   ├── haptic-tab.tsx           # Haptic feedback for tabs
│   ├── parallax-scroll-view.tsx # Parallax scroll effect
│   └── ui/                      # UI primitives
│       ├── collapsible.tsx
│       └── icon-symbol.tsx
├── constants/
│   └── theme.ts                 # Color schemes and fonts
├── hooks/
│   ├── use-color-scheme.ts      # Color scheme detection
│   └── use-theme-color.ts       # Theme color hook
├── assets/
│   └── images/                  # App icons and images
└── scripts/
    └── reset-project.js         # Project reset utility
```

---

## Application Architecture

### Navigation Structure
- **Root Layout** (`app/_layout.tsx`): Stack navigator with theme provider
- **Tab Navigation** (`app/(tabs)/_layout.tsx`): Bottom tab bar with 2 tabs
  - Home tab (index.js)
  - Explore tab (explore.tsx)
- **Modal Screen**: Standalone modal presentation

### State Management
- **React Hooks**: `useState`, `useEffect` for local component state
- **No global state management library** (Redux, Zustand, etc.)
- State is managed at component level

### Theming System
- **Light/Dark Mode**: Automatic detection via `useColorScheme()`
- **Theme Provider**: React Navigation's `ThemeProvider`
- **Custom Themed Components**: `ThemedText`, `ThemedView` for consistent styling
- **Color Constants**: Defined in `constants/theme.ts`

---

## Core Features

### 1. Migraine Risk Dashboard (Home Screen)
**Location**: `app/(tabs)/index.js`

**Features**:
- **Risk Gauge**: Circular progress indicator showing migraine probability (0-100%)
- **Risk Levels**:
  - High (≥70%): Red (#f87171)
  - Elevated (30-69%): Yellow (#fbbf24)
  - Low (<30%): Green (#34d399)
- **Top Drivers**: Interactive chips showing contributing factors
  - Stress indicators
  - Workload metrics
  - HRV (Heart Rate Variability) data
- **Recommended Actions**: Context-aware suggestions based on selected driver
- **Pull-to-Refresh**: Manual data refresh capability
- **Loading States**: Separate loading indicators for initial load vs. refresh

**API Integration**:
- **Endpoint**: `http://172.20.10.3:5000/api/v1/latest`
- **Method**: GET
- **Response Structure**:
  ```javascript
  {
    p_next_hour: number,        // Probability (0-1)
    top_factors: Array<{
      feature: string,
      score: number
    }>,
    recommended_actions: string[]
  }
  ```

### 2. Push Notifications
**Location**: `app/notifications/`

**Features**:
- **Token Registration**: Expo push token generation and backend registration
- **Notification Channels**: Android-specific channel configuration
- **Foreground/Background Handling**: Listeners for different app states
- **Backend Integration**: Token sent to `http://172.20.10.3:5000/api/register-token`

**Configuration**:
- **Project ID**: `98decf00-8468-4ad1-bcf4-11857c6e5d52`
- **Android Channel**: "default" with MAX importance
- **User ID**: Hardcoded as "YZMM" (should be dynamic)

### 3. Explore Screen
**Location**: `app/(tabs)/explore.tsx`

**Features**:
- **Information Display**: Collapsible sections explaining app features
- **Parallax Scroll Effect**: Animated header with scroll-based transforms
- **Educational Content**: Documentation about routing, theming, images, animations

---

## API Endpoints

### Backend Base URL
`http://172.20.10.3:5000` (Local network IP - needs configuration for production)

### Endpoints Used

1. **GET `/api/v1/latest`**
   - **Purpose**: Fetch latest migraine prediction data
   - **Response**: JSON with probability, factors, and recommendations
   - **Error Handling**: Catches fetch errors, handles non-JSON responses

2. **POST `/api/v1/register-token`** (in index.js)
   - **Purpose**: Register Expo push token
   - **Body**: `{ expoPushToken: string }`
   - **Usage**: Called during app initialization

3. **POST `/api/register-token`** (in registerPush.js)
   - **Purpose**: Alternative token registration endpoint
   - **Body**: `{ user_id: "YZMM", fcm_token: string }`
   - **Note**: Different endpoint path, may be legacy or alternative implementation

---

## Code Quality & Patterns

### Strengths
✅ **Modern React Patterns**: Hooks-based architecture
✅ **TypeScript Support**: Type safety where implemented
✅ **Component Reusability**: Themed components for consistency
✅ **Error Handling**: Try-catch blocks for API calls
✅ **Loading States**: Proper loading indicators
✅ **Pull-to-Refresh**: Good UX pattern implementation
✅ **Theme Support**: Comprehensive light/dark mode

### Areas for Improvement

#### 1. **State Management**
- ⚠️ No global state management (consider Context API or Zustand)
- ⚠️ Duplicate notification registration logic
- ⚠️ Hardcoded user ID ("YZMM")

#### 2. **API Configuration**
- ⚠️ Hardcoded IP address (`172.20.10.3:5000`)
- ⚠️ Should use environment variables for different environments
- ⚠️ No API error handling UI (only console logs)

#### 3. **Code Organization**
- ⚠️ Mixed JavaScript (.js) and TypeScript (.tsx) files
- ⚠️ `index.js` is very large (296 lines) - should be split into components
- ⚠️ Business logic mixed with UI code

#### 4. **Type Safety**
- ⚠️ `index.js` is not TypeScript (should be `index.tsx`)
- ⚠️ No TypeScript interfaces for API responses
- ⚠️ Missing type definitions for notification payloads

#### 5. **Error Handling**
- ⚠️ Errors only logged to console
- ⚠️ No user-facing error messages
- ⚠️ No retry mechanism for failed API calls

#### 6. **Performance**
- ⚠️ Minimum load time enforced (900ms) - may feel slow
- ⚠️ No caching of API responses
- ⚠️ No offline support

#### 7. **Security**
- ⚠️ HTTP (not HTTPS) for API calls
- ⚠️ Hardcoded user ID
- ⚠️ No authentication/authorization visible

---

## Dependencies Analysis

### Production Dependencies
- **Expo Ecosystem**: Well-maintained, latest versions
- **React 19.1.0**: Very recent (may have compatibility issues)
- **React Native 0.81.5**: Latest stable version
- **Axios**: Standard HTTP client
- **Reanimated**: For smooth animations

### Potential Issues
- ⚠️ **React 19.1.0**: Very new, may have breaking changes
- ⚠️ **React Native 0.81.5**: Ensure compatibility with Expo SDK 54
- ⚠️ **No testing libraries**: No Jest, React Native Testing Library

---

## Build Configuration

### EAS Build Profiles
1. **Development**: Development client, internal distribution
2. **Preview**: Internal distribution, preview channel
3. **Production**: App bundle (Android), auto-increment version

### Platform Support
- ✅ **iOS**: Supported (bundle ID: `com.hps2025.migraineapp`)
- ✅ **Android**: Supported (package: `com.hps2025.migraineapp`)
- ✅ **Web**: Static output configured

### App Configuration
- **Name**: "migraine-app"
- **Slug**: "migraine-app"
- **New Architecture**: Enabled (`newArchEnabled: true`)
- **React Compiler**: Enabled (experimental)
- **Typed Routes**: Enabled (experimental)

---

## Recommendations

### Immediate Actions
1. **Environment Variables**: Use `expo-constants` or `.env` for API URLs
2. **TypeScript Migration**: Convert `index.js` to `index.tsx`
3. **Error UI**: Add user-friendly error messages
4. **Component Splitting**: Break down large components
5. **API Service Layer**: Create a dedicated API service module

### Short-term Improvements
1. **State Management**: Implement Context API or Zustand
2. **Type Definitions**: Add TypeScript interfaces for all API responses
3. **Caching**: Implement response caching with React Query or SWR
4. **Authentication**: Add user authentication flow
5. **Testing**: Add unit and integration tests

### Long-term Enhancements
1. **Offline Support**: Implement offline-first architecture
2. **Analytics**: Add analytics tracking
3. **Error Tracking**: Integrate Sentry or similar
4. **CI/CD**: Set up automated testing and deployment
5. **Documentation**: Add JSDoc comments and README improvements

---

## Security Considerations

### Current Issues
- 🔴 **HTTP instead of HTTPS**: All API calls use HTTP
- 🔴 **Hardcoded Credentials**: User ID hardcoded
- 🔴 **No Authentication**: No visible auth mechanism
- 🟡 **Local IP Address**: Exposed in code (should be in env vars)

### Recommendations
- Use HTTPS for all API calls
- Implement proper authentication (JWT, OAuth, etc.)
- Store sensitive data in secure storage
- Use environment variables for configuration
- Implement certificate pinning for production

---

## Performance Considerations

### Current Implementation
- Minimum load time: 900ms (artificial delay)
- No response caching
- No request debouncing
- Full re-renders on state changes

### Optimization Opportunities
- Implement response caching
- Add request debouncing for rapid refreshes
- Use React.memo for expensive components
- Implement virtualized lists if data grows
- Add image optimization

---

## Conclusion

This is a **well-structured Expo React Native application** with modern patterns and good UX considerations. The migraine prediction dashboard is the core feature, with push notification support and a clean theming system.

**Key Strengths**:
- Modern tech stack
- Good component organization
- Theme support
- Smooth animations

**Key Weaknesses**:
- Hardcoded configuration
- Missing error handling UI
- No global state management
- Mixed JS/TS files
- Security concerns (HTTP, hardcoded values)

The app appears to be in **active development** and would benefit from the recommended improvements before production deployment.

