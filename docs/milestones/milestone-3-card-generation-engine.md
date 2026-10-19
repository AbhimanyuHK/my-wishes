# Milestone 3 — Offline card generation engine

## Scope

- Render a square 1080×1080 ARGB bitmap with an inset card surface.
- Draw optional occasion title, circular-cropped photo, multiline greeting, and optional signature.
- Use Android `StaticLayout` so text wrapping and Kannada/English shaping are handled by Android's text stack.
- Accept an optional `Typeface`; the caller can supply a bundled Kannada font when the font asset is added.
- Export PNGs under the app-private `cacheDir/generated-cards` directory. Share only through the existing FileProvider configuration.
- Keep generation offline and avoid broad storage permissions.

## API

- `CardGenerationRequest` contains the greeting, optional title/signature/photo, colors, and optional typeface.
- `CardGeneratorEngine.render(request)` returns a caller-owned bitmap.
- `CardGeneratorEngine.exportPng(request)` writes a PNG into private cache and returns its `File`.

## Notes and limitations

- This milestone supplies the rendering engine, not the screen flow, photo-picker integration, or share intent. Those should be wired in later UI milestones.
- The engine accepts a decoded bitmap; the caller is responsible for decoding a photo from app-private storage and managing bitmap memory.
- No custom Kannada font binary currently exists in the repository. The optional typeface hook is ready, but the exact font will need to be bundled and licensed before it is selected by default.
- Layout uses a conservative fallback font size for long greetings. A future visual QA pass should test long Kannada compound words, emoji, and lower-end device memory.
- Build/test execution requires a local Android SDK and Gradle installation; the GitHub connector cannot execute Android builds.
