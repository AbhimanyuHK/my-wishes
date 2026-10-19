# Milestone 5 — Persistent profile and session polish

## Included
- Added `ProfileImageStorage` to copy selected images into the app-private `filesDir/profile-images` directory.
- Profile image is saved as a compressed JPEG and its private path is stored in DataStore.
- Display name and profile image are restored from DataStore when the screen opens.
- Selecting a profile/photo image now persists it for later app sessions.
- Image loader validates that the stored path points inside the app's private files directory.

## Privacy
- No broad storage permissions are added.
- The original picker URI is not stored; the app copies image bytes to private storage.
- The stored photo is not uploaded. Sharing still happens only when the user taps the card share action.

## Verification
Run `gradle testDebugUnitTest assembleDebug` locally and verify that the profile name and image persist after closing/reopening the app, the picker can be cancelled, and an unavailable/corrupt image path does not crash the screen. Android build/device testing has not been run in the GitHub connector environment.
