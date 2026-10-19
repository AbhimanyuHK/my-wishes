# Milestone 18 — Profile settings reset

## Goal
Give users a simple way to clear saved profile details.

## Included
- Add a localized Clear profile action.
- Clear the saved display name, signature, and profile-image preference.
- Delete the previous image only when it resolves directly inside the app-private profile-images directory.
- Reset the current screen's profile fields without clearing language, theme, favorites, card palette, or card history.

## Compatibility and privacy
- Preserves `com.knockheart.mywishes`.
- No new permissions, dependencies, or network access.

## Verification
The GitHub connector cannot run Gradle or Android SDK builds, and this repository has no Gradle wrapper. No build or device QA has been run here. Run `gradle testDebugUnitTest assembleDebug` locally; verify profile clear, restart persistence, and that unrelated preferences/history remain intact.
