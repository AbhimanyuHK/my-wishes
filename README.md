# My Wishes

Offline-first Android greeting-card application built with Kotlin, Jetpack Compose, and Material 3.

## Knockheart package identity

The permanent Android namespace, application ID, and Kotlin source package are `com.knockheart.mywishes`. Keep **Knockheart** consistent in all future technical identifiers.

## Current milestones

- **Milestone 1 — Project scaffolding:** merged into `main`.
- **Milestone 2 — Offline data foundation:** merged into `main`.
- **Milestone 3 — Card generation engine:** merged into `main`.
- **Milestone 4 — Greeting catalog and card creation UI:** merged into `main`.
- **Milestone 5 — Persistent profile:** merged into `main`.
- **Milestone 6 — Theme preference and accessibility polish:** merged into `main`.
- **Milestone 7 — Language-aware UI strings:** merged into `main`.
- **Milestone 8 — Persist card signature:** merged into `main`.
- **Milestone 9 — Offline greeting search:** merged into `main`.
- **Milestone 10 — Favorite greetings:** proposed in pull request.

## Offline data foundation

- AndroidX DataStore Preferences stores the display name, app-private profile image path, card signature, preferred language, theme preference, and favorite greeting IDs.
- Bundled JSON catalogs are located in `app/src/main/assets/catalog/`.
- Current catalog languages: English (`en`) and Kannada (`kn`).
- No network calls or internet permission are required for catalog loading or rendering.

## Card generation

- `CardGeneratorEngine` renders 1080×1080 card bitmaps.
- Supports occasion title, optional photo, multiline greeting, signature, theme colors, and a caller-supplied typeface.
- Generated PNGs are exported into app-private cache and shared through FileProvider.

## User interface

- Browse and search occasions/greetings offline, save favorites, filter to favorite greetings, switch language, add a photo, generate a preview, and share the PNG.
- Display name, card signature, and selected profile photo are restored from local preferences/app-private storage.
- Photo selection uses the Android system picker; selected image bytes are copied into app-private storage.
- The typeface hook supports a bundled Kannada font later, but no custom font binary is currently included.

See the [Milestone 4 architecture notes](docs/milestones/milestone-4-greeting-card-ui.md).

## Build and test

The repository does not currently include a Gradle wrapper. In an Android development environment with a compatible Gradle installation and Android SDK, run:

```bash
gradle testDebugUnitTest assembleDebug
```

Compilation, automated tests, and device/emulator visual QA have not been executed by the GitHub connector environment.

See [Milestone 5 profile notes](docs/milestones/milestone-5-profile-settings-accessibility.md), [Milestone 6 theme notes](docs/milestones/milestone-6-theme-accessibility.md), [Milestone 7 localization notes](docs/milestones/milestone-7-localized-ui-strings.md), and [Milestone 8 signature notes](docs/milestones/milestone-8-persist-card-signature.md), and [Milestone 9 search notes](docs/milestones/milestone-9-search-greetings.md).


See [Milestone 10 favorite greetings notes](docs/milestones/milestone-10-favorite-greetings.md).
