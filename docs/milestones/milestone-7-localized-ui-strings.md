# Milestone 7 — Language-aware UI strings

## Included
- Moves user-facing home-screen labels, actions, and accessibility descriptions into Android string resources.
- Adds Kannada translations in `res/values-kn/strings.xml`, with English defaults in `res/values/strings.xml`.
- Applies the saved English/Kannada preference to the Compose resource configuration, so the UI labels and bundled greeting catalog change together without requiring the device language to change.
- Preserves the saved theme preference and the permanent package identity `com.knockheart.mywishes`.

## Manual verification
- Switch English → Kannada and verify buttons, labels, photo descriptions, and share chooser title change immediately.
- Switch Kannada → English and verify all strings return to English.
- Restart the app and confirm language and theme choices persist.
- Test long Kannada text with large font sizes and TalkBack.

## Verification limitations
The GitHub connector cannot run Gradle or an Android emulator, and this repository does not include a Gradle wrapper. Run `gradle testDebugUnitTest assembleDebug` locally and complete the manual checks before release.
