# Milestone 13 — Accessible favorite controls

## Changes
- Replace clickable star text with a Material 3 IconButton, providing a standard keyboard/focus target for toggling a greeting favorite.
- Add localized accessibility descriptions that state whether the action adds or removes the specific greeting from favorites.
- Remove an unused Android graphics import.
- Update README milestone status and UI capability notes.

## Compatibility and privacy
- Favorite IDs continue to be stored in the existing DataStore Preferences key.
- No new permissions, network calls, dependencies, or data migrations.
- English and Kannada are both supported.

## Verification
The GitHub connector cannot run Gradle/Android SDK builds, and the repository has no Gradle wrapper. Run `gradle testDebugUnitTest assembleDebug` in an Android environment. Manually verify TalkBack reads the action and greeting text in both states and both languages, and that favorite toggling/filtering still works.
