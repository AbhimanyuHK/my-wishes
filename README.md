# My Wishes

Offline-first Android greeting-card application built with Kotlin, Jetpack Compose, and Material 3.

## Knockheart package identity

The permanent Android namespace, application ID, and Kotlin source package are `com.knockheart.mywishes`. Keep **Knockheart** consistent in all future technical identifiers.

## Current milestones

- **Milestone 1 — Project scaffolding:** merged into `main`.
- **Milestone 2 — Offline data foundation:** DataStore preferences, profile/language models, and bundled English/Kannada greeting catalogs are under review in a separate pull request.

## Offline data foundation

- AndroidX DataStore Preferences stores the display name, app-private profile image path, preferred language, and theme preference.
- Bundled JSON catalogs are located in `app/src/main/assets/catalog/`.
- Initial catalog languages: English (`en`) and Kannada (`kn`).
- Unknown language codes fall back to English until a language catalog is added.
- No network calls or internet permission are required for this data layer.

See [Milestone 2 architecture notes](docs/milestones/milestone-2-offline-data-foundation.md).

## Build and test

The repository currently does not include a Gradle wrapper. In an Android development environment with a compatible Gradle installation and Android SDK, run:

```bash
gradle testDebugUnitTest assembleDebug
```

The GitHub connector environment cannot execute an Android SDK build, so compilation and tests still need to be verified locally.
