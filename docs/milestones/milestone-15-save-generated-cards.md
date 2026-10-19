# Milestone 15 — Save generated cards to device

## Goal
Let users save the currently displayed greeting-card preview as a PNG to a location they choose, rather than only sharing a temporary cache file.

## Included
- Add a localized “Save card to device” action beneath the preview.
- Open Android's system document picker with a suggested `my-wish.png` filename.
- Write the already-rendered preview bitmap as PNG to the selected destination.
- Display localized success/error feedback in English and Kannada.
- No storage permission is needed because the system document picker grants access to the user-selected destination.
- Preserve package identity `com.knockheart.mywishes`.

## Compatibility and privacy
- No new dependencies or network access.
- The user chooses the save location; the app writes only to that URI.
- Saving uses the visible preview bitmap, so saved content matches what is previewed.

## Verification
The GitHub connector cannot run Gradle or Android SDK builds, and the repository has no Gradle wrapper. No build or device QA has been run here. Locally run `gradle testDebugUnitTest assembleDebug`; test save/cancel/failure paths and confirm the PNG opens correctly.
