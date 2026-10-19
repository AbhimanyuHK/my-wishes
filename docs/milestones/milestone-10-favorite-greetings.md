# Milestone 10 — Favorite greetings

## Goal
Let users save greeting IDs as favorites and browse only their favorites without requiring an account or network access.

## Implementation
- Store favorite greeting IDs in AndroidX DataStore Preferences as a string set.
- Add a star toggle beside each greeting; filled star means favorite.
- Add a toggle to show favorites only or return to all matching greetings.
- Keep favorites across app restarts and language changes by storing stable catalog IDs rather than localized greeting text.
- Add English and Kannada labels.

## Privacy and compatibility
- Favorites stay in app-private local preferences.
- No network calls or new Android permissions.
- Existing installations default to an empty favorites set.
- Permanent namespace/application ID remains `com.knockheart.mywishes`.

## Verification checklist
This milestone has not been compiled or device-tested in the GitHub connector environment. Run `gradle testDebugUnitTest assembleDebug` locally, then verify:
1. Favorite and unfavorite a greeting.
2. Enable favorites-only mode and verify only saved greetings remain.
3. Combine favorites-only mode with search, then clear search.
4. Restart the app and verify favorites persist.
5. Switch English/Kannada and verify favorites follow matching stable greeting IDs.
6. Generate and share a card as before.
