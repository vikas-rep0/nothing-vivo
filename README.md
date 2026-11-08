# NOTHING × VIVO — Community Edition

A lightweight, Nothing-inspired Android home launcher starter for the Vivo Y21.

> Independent community project. Not affiliated with Nothing Technology Limited or Vivo. “Nothing” and “Vivo” are third-party trademarks; this project uses an original visual treatment inspired by monochrome/dot-matrix design.

## Current build (0.1.0)

- Black interface with subtle dot/ring decoration and red accent
- Large digital clock and date
- App drawer grid populated from launchable apps
- App search
- Monochrome treatment for app icons
- Works as a selectable Android Home app
- No root access required

## Important limitations

This is a **launcher APK**, not a full Funtouch OS system theme. A regular third-party launcher cannot replace Vivo's notification shade, Settings app, system status bar, system fonts, or secure lock screen. Those areas require OEM theme support or privileged/system-level access.

The app is an early starter build. Test it on the actual Vivo Y21 before distributing widely. Some Vivo firmware versions may apply battery/background restrictions or alter default-home selection.

## Build the APK using GitHub Actions (phone-only workflow)

1. On GitHub, create a **new public repository** named `nothing-vivo`.
2. Download this ZIP and extract it on your phone (Files app / ZArchiver).
3. Open the new repository in Chrome and choose **Add file → Upload files**.
4. Upload the *contents* of this folder, including the hidden `.github` folder. Do not upload the outer `nothing-vivo` folder as a nested folder.
5. Commit the files to the `main` branch.
6. Open the repository's **Actions** tab. The workflow named **Build Android APK** should start automatically.
7. Open the latest successful run and download the `nothing-vivo-debug-apk` artifact.
8. Extract the artifact ZIP. Install `app-debug.apk` on your Vivo Y21. If Android asks, allow installation from that browser/file manager.
9. Press Home and select **NOTHING × VIVO** as the Home app.

If GitHub's mobile view hides “Actions”, open the browser menu and enable **Desktop site**.

## Requirements / build details

- Android Gradle Plugin: 8.7.3
- Gradle: 8.9
- Kotlin: 2.0.21
- JDK: 17
- compileSdk: 35
- minSdk: 23
- targetSdk: 35

## Next development milestones

- Add a proper favorites dock and configurable grid
- Add icon-pack mapping and custom vector icons
- Add wallpaper picker and home-screen settings
- Add optional widgets
- Add signing configuration for a release APK
- Test Android 11 / Funtouch OS 11.1 and later versions

## License

MIT License. See `LICENSE`.
