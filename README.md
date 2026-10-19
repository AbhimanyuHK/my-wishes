# My Wishes

Offline-first Android greeting-card application built with Kotlin, Jetpack Compose, and Material 3.

## Knockheart package identity

The permanent Android namespace, application ID, and Kotlin source package are `com.knockheart.mywishes`. Keep **Knockheart** consistent in all future technical identifiers. CI validates the namespace and application ID to prevent accidental identity drift.

## Current milestones

- **Milestones 1–20:** merged into `main`.
- **Milestone 21 — CI and regression tests:** merged into `main`.
- **Milestone 22 — Release readiness and final QA checklist:** proposed in pull request.

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

GitHub Actions runs catalog/package identity validation, JVM unit tests, and debug/release variant assembly. CI build success does not replace visual QA on real devices or emulators.

With Java 17, Android SDK tooling, and Gradle 8.13 installed, run:

```bash
gradle --no-daemon testDebugUnitTest
gradle --no-daemon assembleDebug
gradle --no-daemon assembleRelease
```

Release variant assembly validates the release configuration but does **not** sign an installable production release. Configure and protect a real signing key outside source control before distributing a production APK or publishing to a store. See [release readiness and QA checklist](docs/release-readiness.md).

## Architecture notes

See the [Milestone 4 architecture notes](docs/milestones/milestone-4-greeting-card-ui.md), and milestone notes under `docs/milestones/`.
