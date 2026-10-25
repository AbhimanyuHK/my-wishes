# My Wishes 1.0.0 — Google Play internal testing

## Release identity (verified in repository)
- Application ID: `com.knockheart.mywishes`
- Version: `1.0.0`, versionCode `1`
- minSdk 26, targetSdk 36
- App bundle task: `gradle --no-daemon bundleRelease`
- Signing credentials: GitHub Actions secrets; **never upload secrets or keystores to the repository**.

## Gate 1 — obtain a verified signed bundle
1. Open [Android CI](https://github.com/AbhimanyuHK/my-wishes/actions/workflows/android-ci.yml).
2. Run the workflow on **main**, or inspect the latest main-branch run.
3. Confirm **both** jobs pass: catalog validation and Android tests/build variants.
4. Confirm `Verify production AAB signature` passes.
5. Download the artifact `my-wishes-production-release-<run-number>`.
6. Extract the **.aab** file; do not upload the APK to Google Play.
7. Record the run URL, commit SHA, bundle version, and SHA-256 in the release record below.
8. Install the corresponding signed APK on test devices. CI build success is not device QA.

## Gate 2 — app quality
- [ ] First launch and complete offline operation (airplane mode).
- [ ] English/Kannada catalogs; all tabs and date-first wishes.
- [ ] Profile image pick/save/restore/delete.
- [ ] Generate cards; long Kannada/English text, photo blending, preview.
- [ ] Export/share PNG; receiver can open image.
- [ ] History persists and delete works.
- [ ] Verify UI on Android 8/9 and a recent Android version.
- [ ] Verify release build (not only debug).
- [ ] Review date correctness and festival coverage; fixed-date catalog is **not** complete movable-festival coverage.

## Gate 3 — Play Console setup
1. In [Play Console](https://play.google.com/console/), create the app **My Wishes** (if it does not already exist).
2. Complete **App content** forms: privacy policy URL, data safety, ads declaration, target audience, content rating, and any other required declarations. Base answers on the **final audited release**, not solely on this document.
3. Prepare store listing: app name, short/full description, app icon, feature graphic, phone screenshots, support contact. Use real screenshots of the release build.
4. In **Testing → Internal testing**, create a release and upload the verified signed `.aab`.
5. Configure tester email list or group, save, review, and roll out to internal testing.
6. Share the opt-in link with testers. Confirm Play installation, signing, launch, and image sharing.
7. Fix any issues and increment versionCode for each replacement bundle.
8. Do not start production rollout until the checklist is signed off and Play Console eligibility/testing requirements are satisfied.

## Release record (fill after successful CI and Play upload)
- Git commit:
- GitHub Actions run URL:
- AAB filename:
- AAB SHA-256:
- Play Console release versionCode:
- Device QA results:
- Privacy policy public URL:
- Internal test opt-in URL:
- Date submitted:
- Approved by:
