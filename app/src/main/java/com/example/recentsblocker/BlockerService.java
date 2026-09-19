package com.example.recentsblocker;

import android.accessibilityservice.AccessibilityService;
import android.content.SharedPreferences;
import android.view.accessibility.AccessibilityEvent;

public class BlockerService extends AccessibilityService {

    private static final String[] RECENTS_PACKAGES = {
            "com.android.systemui",
            "com.google.android.apps.nexuslauncher",
            "com.android.launcher3",
            "com.sec.android.app.launcher",
            "com.miui.home",
            "com.android.quickstep"
    };

    @Override
    public void onAccessibilityEvent(AccessibilityEvent event) {
        if (event == null) return;

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

        for (String p : RECENTS_PACKAGES) {
            if (p.equals(pkgName)) {
                performGlobalAction(GLOBAL_ACTION_HOME);
                return;
            }
        }
    }

    @Override
    public void onInterrupt() {
    }
}
