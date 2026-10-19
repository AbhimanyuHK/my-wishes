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
- **Milestone 6 — Theme preference and accessibility polish:** submitted for review.

## Offline data foundation

- AndroidX DataStore Preferences stores the display name, app-private profile image path, preferred language, and theme preference.
- Bundled JSON catalogs are located in `app/src/main/assets/catalog/`.
- Current catalog languages: English (`en`) and Kannada (`kn`).
- No network calls or internet permission are required for catalog loading or rendering.

## Card generation

- `CardGeneratorEngine` renders 1080×1080 card bitmaps.
- Supports occasion title, optional photo, multiline greeting, signature, theme colors, and a caller-supplied typeface.
- Generated PNGs are exported into app-private cache and shared through FileProvider.

## User interface

- Browse occasions and greetings, switch language, add a photo, generate a preview, and share the PNG.
- Photo selection uses the Android system picker; selected images are held in memory for the current session.
- The typeface hook supports a bundled Kannada font later, but no custom font binary is currently included.

See [Milestone 4 architecture notes](docs/milestones/milestone-4-greeting-card-ui.md).

## Build and test

The repository does not currently include a Gradle wrapper. In an Android development environment with a compatible Gradle installation and Android SDK, run:

```bash
gradle testDebugUnitTest assembleDebug
```

Compilation, automated tests, and device/emulator visual QA have not been executed by the GitHub connector environment.

## Persistent profile

- The display name and selected profile/card photo are restored across sessions.
- Selected image bytes are copied to app-private `filesDir/profile-images`; only the private path is stored in DataStore.
- No broad storage permissions or network calls are introduced.

See [Milestone 5 architecture notes](docs/milestones/milestone-5-profile-settings-accessibility.md).

## Theme preference

- The saved dark-theme preference now controls the app-wide Material 3 light/dark color scheme.
- Theme changes are persisted locally in DataStore and restored on startup.
- Review both themes with TalkBack and large font/display-size settings before release.

See [Milestone 6 architecture notes](docs/milestones/milestone-6-theme-accessibility.md).
