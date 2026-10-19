# Milestone 19 — Kannada typography and text fitting

## Goal
Improve card rendering for longer greetings, including Kannada text, while avoiding text clipping and double-render artifacts.

## Included
- Measure the greeting layout and progressively reduce font size until it fits the available card area, down to a readable minimum.
- Draw the greeting once using the final measured layout.
- Use Android's `sans-serif` typeface as the default fallback while preserving a caller-supplied typeface.
- Document the behavior and current font limitation.

## Font note
No custom Kannada font binary is bundled in this milestone. Actual shaping and appearance can vary by Android version/device; visual QA on a Kannada-capable device remains necessary.

## Compatibility and verification
- Preserves `com.knockheart.mywishes`.
- No new permissions, dependencies, or network access.
- The GitHub connector cannot run Gradle or Android SDK builds. Run `gradle testDebugUnitTest assembleDebug` locally and inspect long English/Kannada greetings with and without a photo and signature.
