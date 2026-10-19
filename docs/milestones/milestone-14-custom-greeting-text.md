# Milestone 14 — Custom greeting text

## Goal
Allow a user to personalize a generated greeting card with a message of their own, while keeping the selected catalog greeting as the default.

## Included
- Add an optional multiline custom-greeting field to the card creation screen.
- Use the trimmed custom text for both preview rendering and PNG export when non-empty.
- Fall back to the selected offline catalog greeting when the custom field is blank.
- Clear the temporary custom message when the user selects a different occasion/greeting.
- Localize labels and hint text in English and Kannada.
- Preserve the permanent package identity `com.knockheart.mywishes`.

## Compatibility and privacy
- No preference migration, new dependency, permission, or network access.
- Custom text is held in screen state and is not persisted to DataStore.

## Verification
The GitHub connector cannot run Gradle or the Android SDK, and this repository has no Gradle wrapper. Build and device tests have not been run here. Locally run `gradle testDebugUnitTest assembleDebug`, then verify blank fallback, multiline custom text in preview/export, and English/Kannada UI.
