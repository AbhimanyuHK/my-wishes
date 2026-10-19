# Milestone 2 — Offline data foundation

## Knockheart package identity

The Android namespace, application ID, and Kotlin packages use `com.knockheart.mywishes`.

## Local preferences

`UserPreferencesRepository` stores the display name, app-private profile image path, preferred language code, and theme preference in AndroidX DataStore Preferences. Its read API is a Flow so screens can react to updates. Profile image paths must point to files copied into app-private storage; temporary picker URIs do not belong in preferences.

## Bundled greeting content

`AssetGreetingCatalogRepository` reads versioned-shape JSON files from `app/src/main/assets/catalog/`. The first catalogs are English (`en`) and Kannada (`kn`). The model is independent of a specific UI and uses stable IDs for occasions and greeting entries. Unknown language codes currently resolve to English until the corresponding language and bundled catalog are added.

## Offline and privacy constraints

- No network calls or internet permission are introduced.
- DataStore and bundled assets work without connectivity.
- No broad media/storage permissions are required for this milestone.
- Photo Picker integration and copying selected photos into private storage are reserved for a later milestone.

## Verification

The repository's GitHub connector cannot run an Android SDK build. Run `./gradlew testDebugUnitTest assembleDebug` in an Android development environment after the Gradle wrapper is available.
