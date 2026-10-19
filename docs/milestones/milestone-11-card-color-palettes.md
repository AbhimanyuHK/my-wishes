# Milestone 11 — Card color palettes

## Goal
Expose the card generator's existing color controls through a simple, localized palette selector.

## Included
- Add five presets: Sunset, Ocean, Forest, Rose, and Night.
- Apply the selected palette to both preview rendering and exported PNG sharing.
- Persist the selected palette ID in DataStore Preferences.
- Add English and Kannada labels.
- Existing installations default to Sunset.

## Privacy and compatibility
Palette selection is stored on-device. No network calls or new permissions are introduced. The permanent namespace/application ID remains `com.knockheart.mywishes`.

## Verification checklist
This milestone has not been compiled or device-tested in the GitHub connector environment. Run `gradle testDebugUnitTest assembleDebug` locally and verify:
1. Select each palette and generate a preview.
2. Share the card and verify exported colors match the preview.
3. Restart the app and confirm the selected palette is restored.
4. Switch English/Kannada and verify labels are localized.
5. Check the selector on a narrow screen.
6. Confirm existing greeting search, favorites, photo, and signature behavior remains intact.
