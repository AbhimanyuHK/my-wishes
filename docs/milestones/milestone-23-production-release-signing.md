# Milestone 23 — Production release signing

## Scope

- Configure the Android release build to use the private release keystore only when it exists on the runner and all signing environment variables are available.
- Require all four GitHub Actions signing secrets for pushes to `main` and manually dispatched runs.
- Keep pull-request builds safe to run without secrets; their release variants remain unsigned.
- Decode the keystore only into the temporary GitHub Actions runner workspace.
- Verify the release APK with `apksigner` and the AAB with `jarsigner` before uploading both as a 30-day artifact.
- Preserve namespace and application ID `com.knockheart.mywishes`.

## Required repository secrets

`ANDROID_KEYSTORE_BASE64`, `ANDROID_KEYSTORE_PASSWORD`, `ANDROID_KEY_ALIAS`, and `ANDROID_KEY_PASSWORD`.

## Security notes

The keystore and passwords are not stored in Git. Base64 is not encryption. Do not upload the keystore file to repository artifacts. A signed build is not automatically store-ready: confirm versioning, manual QA, privacy disclosures, and release listing before publishing.

## Validation

GitHub Actions must pass the unit tests, catalog/package validation, APK signature verification, and AAB signature verification. This milestone is not considered validated until the workflow has completed successfully with repository secrets configured.
