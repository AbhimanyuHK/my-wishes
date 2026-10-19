# Milestone 21 — CI and regression tests

## Goal
Add repeatable checks for bilingual offline catalog integrity and compile/test the Android app in GitHub Actions.

## Included
- GitHub Actions workflow runs on pushes to main, pull requests targeting main, and manual dispatch.
- Python standard-library validator checks JSON structure, non-empty content, unique IDs, and English/Kannada occasion and greeting ID parity.
- JVM unit tests cover card palette ID lookup, fallback behavior, and opaque ARGB colors.
- README documents the CI workflow.

## Build setup
The workflow uses Java 17, Android SDK setup, and Gradle 8.13 to match the Android Gradle Plugin 8.13.0 declared in the version catalog. It runs `gradle testDebugUnitTest` and `gradle assembleDebug`; no Gradle wrapper is added in this milestone.

## Verification status
The catalog validator can be run locally with `python scripts/validate_catalogs.py`. The GitHub connector cannot execute Gradle, Android SDK tasks, or workflow runs, so CI results are pending after merge/push. Keep the permanent namespace/application ID `com.knockheart.mywishes`.
