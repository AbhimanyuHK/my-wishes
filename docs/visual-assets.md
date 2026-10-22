# My Wishes — Premium Purple & Gold Assets

This asset set is intentionally vector-based so it ships inside the APK and works offline on Android 8.0 (API 26) and newer, including Android 9.

## Branding palette

- Royal purple: `#190A3D`
- Purple: `#4A247F`
- Gold: `#D4AF37`
- Warm cream: `#FFF3D6`

## Resources

- `drawable/ic_my_wishes_foreground.xml`: adaptive launcher icon foreground.
- `mipmap-anydpi-v26/ic_launcher.xml` and `ic_launcher_round.xml`: Android adaptive launcher icon resources.
- `drawable/ic_category_*.xml`: birthday, anniversary, love, friendship, festival, morning, congratulations, and general category tiles.
- `drawable/splash_screen.xml`: vector startup background.
- `drawable/card_background_*.xml`: reusable square card background artwork.
- `values/launcher_colors.xml`: shared brand colors.
- `values/themes.xml`: system bars and startup background.

All vector assets are local XML resources; no network access or image CDN is required. The category icons are wired into the category tiles. Card backgrounds are reusable resources; current generated cards use the selected `CardPalette` color values, so background art can be wired into the card compositor in a future enhancement.
