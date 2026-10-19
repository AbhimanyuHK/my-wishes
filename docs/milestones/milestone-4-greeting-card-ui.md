# Milestone 4 — Greeting catalog and card creation UI

## Included
- Compose screen loads the bundled catalog for the saved language.
- Occasion cards expand to show available greetings; selecting a greeting drives card generation.
- Language selector persists English/Kannada choice through DataStore.
- Optional name and signature fields.
- Android system photo picker allows selecting a local image for the current card.
- Card preview is rendered on-device by `CardGeneratorEngine`.
- Share action exports a PNG to app-private cache and launches Android's share chooser through FileProvider.

## Privacy and offline behavior
- Catalog loading and card rendering do not use network access.
- The Android photo picker is used instead of broad storage permissions.
- Selected photo is decoded in memory for the current session; this milestone does not persist the image or request durable URI access.
- Only the explicit share action exposes the generated PNG to another app, with temporary read permission.

## Verification limitations
The GitHub connector cannot run Gradle/Android SDK builds. Please run `gradle testDebugUnitTest assembleDebug` locally and check the layout on a device/emulator, including Kannada text, long greetings, photo picker cancellation, and share targets.
