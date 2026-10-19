# Milestone 17 — Delete recent cards

## Goal
Let users remove individual cards from their private recent-card history.

## Included
- Add a delete control to each recent-card row.
- Provide localized screen-reader labels and localized error feedback.
- Validate that deletion targets a file directly inside the app's card-history directory.
- Refresh the list after deletion.

## Privacy and compatibility
- Preserves `com.knockheart.mywishes`.
- No new permissions, dependencies, or network access.

## Verification
The GitHub connector cannot run Gradle or Android SDK builds, and this repository has no Gradle wrapper. No build or device QA has been run here. Run `gradle testDebugUnitTest assembleDebug` locally and verify deleting one entry, persistence after restart, and rejection of files outside the history directory.
