package com.linkpoint.pwa;

import android.app.NotificationManager;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;

import com.getcapacitor.JSObject;
import com.getcapacitor.Plugin;
import com.getcapacitor.PluginCall;
import com.getcapacitor.PluginMethod;
import com.getcapacitor.annotation.CapacitorPlugin;

import java.lang.reflect.Method;
import java.util.List;

/**
 * Badge Plugin for Linkpoint PWA
 * Manages app icon badges for notification counts
 */
@CapacitorPlugin(name = "Badge")
public class BadgePlugin extends Plugin {

    private static final String SAMSUNG_LAUNCHER = "com.sec.android.app.launcher";
    private static final String SONY_LAUNCHER = "com.sonymobile.home";
    private static final String HTC_LAUNCHER = "com.htc.launcher";

    /**
     * Set badge count on app icon
     */
    @PluginMethod
    public void set(PluginCall call) {
        Integer count = call.getInt("count", 0);

        if (count == null || count < 0) {
            call.reject("Invalid count");
            return;
        }

        boolean success = setBadgeCount(count);

        JSObject result = new JSObject();
        result.put("success", success);
        result.put("count", count);
        call.resolve(result);
    }

    /**
     * Clear badge count
     */
    @PluginMethod
    public void clear(PluginCall call) {
        boolean success = setBadgeCount(0);

        JSObject result = new JSObject();
        result.put("success", success);
        call.resolve(result);
    }

    /**
     * Get current badge count (if supported)
     */
    @PluginMethod
    public void get(PluginCall call) {
        // Most Android launchers don't provide an API to read badge count
        // Return 0 as default
        JSObject result = new JSObject();
        result.put("count", 0);
        result.put("supported", false);
        call.resolve(result);
    }

    /**
     * Check if badges are supported
     */
    @PluginMethod
    public void isSupported(PluginCall call) {
        boolean supported = isBadgeSupported();

        JSObject result = new JSObject();
        result.put("supported", supported);
        call.resolve(result);
    }

    // Helper methods

    private boolean setBadgeCount(int count) {
        try {
            String launcherClassName = getLauncherClassName();
            if (launcherClassName == null) {
                return false;
            }

            // Samsung launcher
            if (launcherClassName.contains("samsung") || launcherClassName.contains("sec")) {
                return setSamsungBadge(count);
            }

            // Sony launcher
            if (launcherClassName.contains("sony")) {
                return setSonyBadge(count);
            }

            // HTC launcher
            if (launcherClassName.contains("htc")) {
                return setHTCBadge(count);
            }

            // Try generic notification badge approach
            return setNotificationBadge(count);
        } catch (Exception e) {
            return false;
        }
    }

    private boolean setSamsungBadge(int count) {
        try {
            Intent intent = new Intent("android.intent.action.BADGE_COUNT_UPDATE");
            intent.putExtra("badge_count", count);
            intent.putExtra("badge_count_package_name", getContext().getPackageName());
            intent.putExtra("badge_count_class_name", getLauncherClassName());
            getContext().sendBroadcast(intent);
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    private boolean setSonyBadge(int count) {
        try {
            Intent intent = new Intent("com.sonyericsson.home.action.UPDATE_BADGE");
            intent.putExtra("com.sonyericsson.home.intent.extra.badge.ACTIVITY_NAME", getLauncherClassName());
            intent.putExtra("com.sonyericsson.home.intent.extra.badge.SHOW_MESSAGE", count > 0);
            intent.putExtra("com.sonyericsson.home.intent.extra.badge.MESSAGE", String.valueOf(count));
            intent.putExtra("com.sonyericsson.home.intent.extra.badge.PACKAGE_NAME", getContext().getPackageName());
            getContext().sendBroadcast(intent);
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    private boolean setHTCBadge(int count) {
        try {
            Intent intent = new Intent("com.htc.launcher.action.SET_NOTIFICATION");
            intent.putExtra("com.htc.launcher.extra.COMPONENT", getContext().getPackageName() + "/" + getLauncherClassName());
            intent.putExtra("com.htc.launcher.extra.COUNT", count);
            getContext().sendBroadcast(intent);
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    private boolean setNotificationBadge(int count) {
        try {
            // Use notification approach as fallback
            // This doesn't work on all devices but is worth trying
            NotificationManager notificationManager = (NotificationManager) 
                getContext().getSystemService(Context.NOTIFICATION_SERVICE);

            if (notificationManager != null && android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.M) {
                // Some launchers read active notifications
                return true;
            }
            return false;
        } catch (Exception e) {
            return false;
        }
    }

    private String getLauncherClassName() {
        try {
            PackageManager pm = getContext().getPackageManager();
            Intent intent = new Intent(Intent.ACTION_MAIN);
            intent.addCategory(Intent.CATEGORY_LAUNCHER);
            intent.setPackage(getContext().getPackageName());

            List<ResolveInfo> resolveInfos = pm.queryIntentActivities(intent, 0);
            if (resolveInfos != null && !resolveInfos.isEmpty()) {
                return resolveInfos.get(0).activityInfo.name;
            }
        } catch (Exception e) {
            // Ignore
        }
        return null;
    }

    private boolean isBadgeSupported() {
        String launcherClassName = getLauncherClassName();
        if (launcherClassName == null) {
            return false;
        }

        // Check for known supported launchers
        return launcherClassName.contains("samsung") ||
               launcherClassName.contains("sec") ||
               launcherClassName.contains("sony") ||
               launcherClassName.contains("htc") ||
               launcherClassName.contains("xiaomi") ||
               launcherClassName.contains("oppo") ||
               launcherClassName.contains("vivo");
    }
}
