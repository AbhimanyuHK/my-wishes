# My Wishes

Offline-first Android greeting-card application built with Kotlin, Jetpack Compose, and Material 3.

## Knockheart package identity

The permanent Android namespace, application ID, and Kotlin source package are `com.knockheart.mywishes`. Keep **Knockheart** consistent in all future technical identifiers.

## Current milestones

- **Milestone 1 — Project scaffolding:** merged into `main`.
- **Milestone 2 — Offline data foundation:** merged into `main`.
- **Milestone 3 — Card generation engine:** implemented on the `milestone-3-card-generation-engine` branch and submitted for review.

## Offline data foundation

- AndroidX DataStore Preferences stores the display name, app-private profile image path, preferred language, and theme preference.
- Bundled JSON catalogs are located in `app/src/main/assets/catalog/`.
- Initial catalog languages: English (`en`) and Kannada (`kn`).
- Unknown language codes fall back to English until a language catalog is added.
- No network calls or internet permission are required for this data layer.

See [Milestone 2 architecture notes](docs/milestones/milestone-2-offline-data-foundation.md).

## Card generation engine

- `CardGeneratorEngine` renders 1080×1080 PNG-ready bitmaps offline.
- Supports an optional occasion title, circular-cropped photo, multiline greeting, signature, theme colors, and caller-supplied typeface.
- PNG exports are written to the app-private `cacheDir/generated-cards` directory; sharing should use the configured FileProvider.
- Kannada font support is exposed through an optional `Typeface`; a licensed font asset has not yet been added.
- UI integration, Photo Picker integration, and share-intent wiring are separate follow-up work.

See [Milestone 3 architecture notes](docs/milestones/milestone-3-card-generation-engine.md).

## Build and test

The repository currently does not include a Gradle wrapper. In an Android development environment with a compatible Gradle installation and Android SDK, run:

```bash
gradle testDebugUnitTest assembleDebug
```

The GitHub connector environment cannot execute an Android SDK build, so compilation and tests still need to be verified locally.
