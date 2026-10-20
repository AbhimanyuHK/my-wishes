# My Wishes

Offline-first Android greeting-card application built with Kotlin, Jetpack Compose, and Material 3.

## Knockheart package identity

The permanent Android namespace, application ID, and Kotlin source package are `com.knockheart.mywishes`. Keep **Knockheart** consistent in all future technical identifiers. CI validates the namespace and application ID to prevent accidental identity drift.

## Current milestones

- **Milestones 1–22:** merged into `main`.
- **Milestone 23 — Production release signing:** signing configuration and CI verification are proposed in a separate pull request.

## Offline data foundation

- AndroidX DataStore Preferences stores the display name, app-private profile image path, card signature, preferred language, theme preference, favorite greeting IDs, and selected card palette.
- Bundled JSON catalogs are located in `app/src/main/assets/catalog/`.
- Current catalog languages: English (`en`) and Kannada (`kn`). The bundled catalogs share occasion and greeting IDs across both languages.
- No network calls or internet permission are required for catalog loading or rendering.

## Card generation

- `CardGeneratorEngine` renders 1080×1080 card bitmaps.
- Supports occasion title, optional photo, multiline greeting, signature, theme colors, and a caller-supplied typeface. Greeting text is measured and reduced to fit the available card area, with Android sans-serif as the default font fallback.
- Generated PNGs are exported into app-private cache and shared through FileProvider.

## User interface

- Browse and search occasions/greetings offline, save favorites with screen-reader-labelled controls, filter to favorite greetings, switch language, use custom greeting text, add a photo, preview cards, reopen or delete recent generated cards, save a PNG through Android’s document picker, and share the PNG.
- Display name, card signature, and selected profile photo are restored from local preferences/app-private storage; users can clear the saved profile and its private photo.
- Photo selection uses the Android system picker; selected image bytes are copied into app-private storage.

## Build, test, and release

GitHub Actions validates the catalogs and package identity, runs JVM unit tests, and builds debug/release variants. Pull-request builds run without release secrets and therefore produce unsigned release variants. Pushes to `main` and manual workflow runs require the four release-signing secrets, decode the keystore only on the temporary runner, sign the release APK/AAB, verify signatures, and upload the signed packages as a GitHub Actions artifact retained for 30 days.

Required repository secrets under **Settings → Secrets and variables → Actions**:

- `ANDROID_KEYSTORE_BASE64`: Base64 text of the private `.jks` file.
- `ANDROID_KEYSTORE_PASSWORD`: keystore password.
- `ANDROID_KEY_ALIAS`: key alias (for this key: `my-wishes-release`).
- `ANDROID_KEY_PASSWORD`: private key password.

Never commit the keystore, Base64 file, or passwords. Base64 is an encoding, not encryption. Keep a secure offline backup of the keystore and passwords. Losing the signing key can prevent future updates signed with the same identity.

With Java 17, Android SDK tooling, and Gradle 8.13 installed, run:

```bash
gradle --no-daemon testDebugUnitTest
gradle --no-daemon assembleDebug
gradle --no-daemon assembleRelease
gradle --no-daemon bundleRelease
```

The signed production artifact is available only after a successful push-to-`main` or manually dispatched workflow with all four secrets configured. Open the successful [GitHub Actions runs](https://github.com/AbhimanyuHK/my-wishes/actions), select the run, and download `my-wishes-production-release-<run-number>` from **Artifacts**. Artifacts expire after 30 days; attach approved binaries to a versioned GitHub Release for longer-term distribution. CI success does not replace visual QA on a real device or emulator. See [release readiness and QA checklist](docs/release-readiness.md).

## Architecture notes

See the [Milestone 4 architecture notes](docs/milestones/milestone-4-greeting-card-ui.md), and milestone notes under `docs/milestones/`.
