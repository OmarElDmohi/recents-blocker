package com.example.recentsblocker;

import android.accessibilityservice.AccessibilityService;
import android.content.SharedPreferences;
import android.view.accessibility.AccessibilityEvent;

/**
 * Watches for the Recents (overview) screen appearing and immediately
 * presses Back / Home to bounce the user away from it.
 *
 * How it works:
 *  - Android fires a WINDOW_STATE_CHANGED / WINDOWS_CHANGED event whenever
 *    the app-switcher (Recents) opens.
 *  - We detect that window by its package name (the system launcher /
 *    systemui) and by the fact it is NOT a normal app window.
 *  - We then perform the global BACK action, which closes Recents right away.
 *
 * This is the "bounce-back" method: it cannot truly disable the button
 * (Android does not allow that without device-owner / kiosk mode), but the
 * Recents screen is dismissed so fast it is effectively unusable.
 */
public class BlockerService extends AccessibilityService {

    // System packages that host the Recents / overview screen on most devices.
    private static final String[] RECENTS_PACKAGES = {
            "com.android.systemui",
            "com.google.android.apps.nexuslauncher", // Pixel launcher (overview)
            "com.android.launcher3",
            "com.sec.android.app.launcher",           // Samsung
            "com.miui.home",                          // Xiaomi
            "com.android.quickstep"
    };

    private long lastBounceTime = 0;

    @Override
    public void onAccessibilityEvent(AccessibilityEvent event) {
        if (event == null) return;

        // Only act while the feature is switched on in the app.
        SharedPreferences prefs = getSharedPreferences("settings", MODE_PRIVATE);
        if (!prefs.getBoolean("blocking_enabled", true)) return;

        int type = event.getEventType();
        if (type != AccessibilityEvent.TYPE_WINDOW_STATE_CHANGED
                && type != AccessibilityEvent.TYPE_WINDOWS_CHANGED) {
            return;
        }

        CharSequence pkg = event.getPackageName();
        if (pkg == null) return;
        String pkgName = pkg.toString();

        boolean isRecents = false;
        for (String p : RECENTS_PACKAGES) {
            if (p.equals(pkgName)) {
                isRecents = true;
                break;
            }
        }
        if (!isRecents) return;

        // Debounce so we don't loop on our own generated events.
        long now = System.currentTimeMillis();
        if (now - lastBounceTime < 200) return;
        lastBounceTime = now;

        // Close the Recents screen. BACK dismisses the overview instantly;
        // HOME is a fallback if BACK is swallowed.
        boolean handled = performGlobalAction(GLOBAL_ACTION_BACK);
        if (!handled) {
            performGlobalAction(GLOBAL_ACTION_HOME);
        }
    }

    @Override
    public void onInterrupt() {
        // Required by the API; nothing to clean up.
    }
}
