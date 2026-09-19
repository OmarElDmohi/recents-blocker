# Recents Blocker

A small Android app that blocks the **Recent Apps** (app switcher) button using
the "bounce-back" method: whenever the Recents screen opens, the app instantly
closes it, so the button becomes unusable.

## Important — read first

- Android does **not** let any app truly disable a system button (an overlay
  cannot cover them). This app uses an **Accessibility Service** instead, which
  detects the Recents screen and dismisses it immediately.
- Because of that, the Recents screen may **flash for a fraction of a second**
  before it closes. This is normal and the best possible without a full
  "kiosk / device-owner" setup.
- **Google Play will not accept** this kind of app, so you install it manually
  (see below). That is fine for your own devices.

## How to build the installable file (.apk)

1. Install **Android Studio** (free) on a computer.
2. Open Android Studio → **Open** → select this `RecentsBlocker` folder.
3. Wait for it to finish loading (it downloads what it needs automatically).
4. Menu **Build → Build Bundle(s) / APK(s) → Build APK(s)**.
5. When it finishes, click **locate** — the file is
   `app/build/outputs/apk/debug/app-debug.apk`.
6. Copy that `.apk` to the phone and tap it to install (allow
   "install from unknown sources" if asked).

## How to use it on the phone

1. Open the **Recents Blocker** app.
2. Tap **Open Accessibility settings**.
3. Find **Recents Blocker** in the list and switch it **ON**.
4. Done. The Recent Apps button no longer works.
5. To stop it: turn the service off in the same Accessibility list, or flip the
   switch inside the app.

## Files that matter

- `BlockerService.java` — the logic that detects Recents and bounces back.
- `MainActivity.java` — the on/off screen.
- `AndroidManifest.xml` — registers the accessibility service.
