# Milestone 8 — Persist card signature

## Included
- Adds an optional card signature to the local profile model and DataStore preferences.
- Restores the saved signature when the app opens, alongside the display name and profile photo.
- Saves the signature with the profile when a photo is selected or a card preview is generated.
- Clears the signature when the profile is cleared.
- Existing installations remain compatible: missing signature preference values default to an empty string.
- Keeps all data on-device and preserves package identity `com.knockheart.mywishes`.

## Manual verification
- Enter a signature, generate a preview, close and reopen the app, and verify the signature is restored.
- Select a photo and verify the signature remains saved.
- Leave the signature blank and generate a card; verify the display name is still used as the card-signature fallback.
- Switch English/Kannada and light/dark theme; verify these preferences still persist.

## Verification limitations
The GitHub connector cannot run Gradle or an Android emulator. Run `gradle testDebugUnitTest assembleDebug` locally and perform the manual checks before release.
