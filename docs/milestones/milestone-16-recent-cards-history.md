# Milestone 16 — Recent generated cards

## Goal
Let users reopen recent greeting cards without having to recreate them.

## Included
- Store generated preview PNGs in the app-private `filesDir/card-history` directory.
- Keep at most ten recent generated cards, newest first.
- Show a localized recent-cards list with thumbnails; tapping an entry reopens it in the preview.
- Keep card history local to the device and preserve package identity `com.knockheart.mywishes`.

## Privacy and storage
- No network calls, external storage permissions, or new dependencies.
- Card history remains in app-private storage and is removed if the app's data is cleared or the app is uninstalled.
- History files are bounded to ten cards.

## Verification
The GitHub connector cannot run Gradle or the Android SDK, and this repository has no Gradle wrapper. No build or device QA has been run here. Run `gradle testDebugUnitTest assembleDebug` locally; verify generating, app restart, reopening a recent card, and cleanup beyond ten entries.
