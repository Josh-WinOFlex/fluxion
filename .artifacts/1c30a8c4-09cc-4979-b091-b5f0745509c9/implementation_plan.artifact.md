# Implement Splash and Login Screens

This plan outlines the steps to implement the Splash Screen and Login Screen for the Fluxion app, ensuring a premium feel and consistent theming.

## User Review Required

> [!IMPORTANT]
> The splash screen will have a fixed delay of 2 seconds before navigating to the Login screen. If there's a need for actual data loading, this will be adjusted in the future.

## Proposed Changes

### Navigation

#### [MODIFY] [Routes.kt](file:///F:/Fluxion/Fluxion_App/app/src/main/java/com/winoflex/fluxion/navigation/Routes.kt)
- Add `Splash` and `Login` routes to the `Route` sealed interface.

#### [MODIFY] [NavGraph.kt](file:///F:/Fluxion/Fluxion_App/app/src/main/java/com/winoflex/fluxion/navigation/NavGraph.kt)
- Set `Route.Splash` as the initial destination.
- Add entry providers for `Route.Splash` and `Route.Login`.

### UI Screens

#### [NEW] [SplashScreen.kt](file:///F:/Fluxion/Fluxion_App/app/src/main/java/com/winoflex/fluxion/ui/screens/SplashScreen.kt)
- Display `logo.png` centered.
- Pulsing animation for the logo.
- Cyan progress bar at the bottom.
- Navigation to `Login` after 2 seconds.

#### [NEW] [LoginScreen.kt](file:///F:/Fluxion/Fluxion_App/app/src/main/java/com/winoflex/fluxion/ui/screens/LoginScreen.kt)
- Modern layout with logo at the top.
- Email/Password input fields.
- `FluxionButton` for Login.
- "Forgot Password?" and "Sign Up" links.
- Consistent with Samsung SmartThings/Tesla aesthetic.

## Verification Plan

### Automated Tests
- Run `./gradlew :app:assembleDebug` to ensure the project builds correctly.

### Manual Verification
- Verify the Splash Screen transition.
- Verify the Login Screen layout and interactions.
