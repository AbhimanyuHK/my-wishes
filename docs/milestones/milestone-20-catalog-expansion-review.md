# Milestone 20 — Expand bilingual greeting catalog

## Goal
Broaden the app's offline greeting coverage with common personal and family milestones in English and Kannada.

## Included
- Add six occasion categories: Wedding & Anniversary, New Baby, Congratulations & Achievements, Get Well Soon, New Home & Housewarming, and Retirement.
- Add two greetings per new category in both languages.
- Keep occasion IDs and greeting IDs identical between English and Kannada catalogs so language switching and favorites remain consistent.
- Update README catalog documentation.

## Verification
Both catalog files were parsed during preparation; the existing seven categories remain and the six new categories were appended. No Android build or device QA has been run. Run `gradle testDebugUnitTest assembleDebug` locally and verify both languages load, all IDs align, search/favorites still work, and new greetings render correctly on cards.
