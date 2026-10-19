# Milestone 12 — Browsing UX polish

## Changes
- Show an empty state whenever search and favorite filters produce no greetings, including when favorites-only is enabled but no favorites have been saved.
- Add a localized action to clear the search and return to all greetings.
- Render the active card palette as a filled Material button while keeping inactive palettes outlined, making the selection easier to identify.
- Add English and Kannada resource strings for the empty-state action.

## Compatibility and privacy
- No changes to the app ID, namespace, stored preference schema, catalog content, or permissions.
- Filtering remains local and offline.

## Verification
The GitHub connector cannot run Gradle or Android SDK builds, and the repository has no Gradle wrapper. Run `gradle testDebugUnitTest assembleDebug` in an Android development environment. Manually verify:
1. Favorites-only with zero favorites displays the empty state and clear action.
2. A search with no matches displays the same empty state.
3. Clearing filters resets the query and favorites-only mode.
4. The selected palette is visually filled; other palettes remain outlined.
5. English and Kannada labels render correctly, including on a narrow screen.
