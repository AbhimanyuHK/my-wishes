# Milestone 9 — Offline greeting search

## Included
- Adds an on-device search field for occasions, descriptions, and greeting text.
- Searches the currently selected language's bundled catalog; results update as the user types.
- When a greeting matches, shows its occasion and matching greeting. If the occasion title or description matches, all greetings for that occasion remain visible.
- Adds English and Kannada strings for search and the empty-results state.
- Search is case-insensitive for scripts with case distinctions, does not require internet access, and does not alter catalog data or preferences.
- Preserves the permanent package identity `com.knockheart.mywishes`.

## Manual verification
- Search for an occasion title and verify the occasion appears.
- Search for a word contained in a greeting and verify only relevant occasions/greetings are shown.
- Search for text that has no matches and verify the localized empty state appears.
- Switch English/Kannada and confirm search operates on the currently loaded language.
- Clear the query and verify the full catalog returns; verify selected greeting and card generation still work.

## Verification limitations
The GitHub connector cannot run Gradle or an Android emulator. Run `gradle testDebugUnitTest assembleDebug` locally and perform manual UI checks before release.
