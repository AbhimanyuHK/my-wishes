# Milestone 6 — Theme preference and accessibility polish

## Included
- Applies the saved dark-theme preference to the app-wide Material 3 color scheme.
- Adds a dark-theme switch to the home screen and persists changes through DataStore.
- Uses Material 3 light/dark color schemes so system component colors follow the selected app theme.
- Keeps the setting local and offline; no permissions or network calls are added.

## Manual verification
- Toggle dark theme on and off and verify the whole app updates immediately.
- Force-close/reopen the app and verify the selected theme is restored.
- Check text fields, cards, menus, buttons, errors, and the switch in both themes.
- Verify TalkBack announces the theme switch state and test large font/display-size settings.

## Verification limitations
The GitHub connector cannot run Gradle or an Android emulator, and this repository does not include a Gradle wrapper. Run `gradle testDebugUnitTest assembleDebug` in an Android development environment before release.
