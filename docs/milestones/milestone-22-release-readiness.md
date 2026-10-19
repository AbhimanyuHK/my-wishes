# Milestone 22 — Release readiness

## Scope

- Extend CI to assemble both debug and release variants after unit tests.
- Add a permanent package identity assertion to the catalog validation script.
- Document release build commands, signing cautions, and a manual QA checklist.

## Safety and release constraints

The application ID and namespace must remain `com.knockheart.mywishes`. Debug builds intentionally use the `.debug` application ID suffix. The release build job only checks that the release variant assembles; it does not configure production signing or certify store readiness.

Manual device/emulator testing remains required for image rendering, Kannada text fit, persistence, picker/export/share flows, accessibility, and dark mode.
