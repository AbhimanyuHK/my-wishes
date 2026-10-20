# Release readiness and final QA checklist

This checklist separates automated checks from manual device verification. Check off items only after actually performing them.

## Automated checks

- [ ] GitHub Actions catalog validation passes for English and Kannada.
- [ ] CI confirms namespace and application ID remain `com.knockheart.mywishes`.
- [ ] JVM unit tests pass.
- [ ] Debug APK variant assembles.
- [ ] Release APK and AAB variants assemble.
- [ ] On pushes to `main` and manual workflow runs, CI confirms release APK and AAB signatures using the configured keystore secrets.
- [ ] Review warnings and dependency updates before creating a release tag.

## Manual device / emulator QA

Test on at least one API 26+ device/emulator, and include a current Android version where possible.

- [ ] First launch works offline and shows the greeting catalog.
- [ ] Switch English ↔ Kannada; catalog text and UI labels update consistently.
- [ ] Browse occasions, search greetings, clear search/filter, and verify empty states.
- [ ] Add/remove favorites, filter favorites, close and reopen the app, and verify persistence.
- [ ] Save and restore display name and card signature.
- [ ] Select a profile photo, restart the app, and confirm it remains available.
- [ ] Clear saved profile details and confirm the private profile photo is removed.
- [ ] Preview cards with short and long English text and Kannada text; inspect clipping and readability.
- [ ] Test custom greeting text, each palette, optional photo, and signature on the generated card.
- [ ] Export a PNG using the system document picker and open the saved image.
- [ ] Share the generated image to an available target; verify the recipient receives a readable PNG.
- [ ] Reopen a recent card and delete a recent card; verify the list updates.
- [ ] Toggle dark mode and check contrast, controls, and dialogs.
- [ ] Check TalkBack labels for favorite controls and keyboard/focus behavior where applicable.
- [ ] Verify no network permission or runtime network dependency was introduced.

## Release identity and distribution

- [ ] Confirm the release manifest/application ID is `com.knockheart.mywishes` (debug builds intentionally add `.debug`).
- [ ] Increment `versionCode` and update `versionName` for the intended release.
- [ ] Configure release signing through secure CI secrets or a local protected keystore; never commit signing keys or passwords.
- [ ] Verify signed APK and AAB signatures in CI.
- [ ] Install/update the signed APK on a test device before publishing.
- [ ] Review privacy policy, store listing, screenshots, support contact, and data-safety declarations before store submission.

## Current limitations

CI assembly is not evidence of visual/device QA. Pull-request builds intentionally omit release secrets and therefore build unsigned release variants. Pushes to `main` and manually dispatched workflows require signing secrets and verify signed release outputs, but each artifact still needs human device QA before publication.
