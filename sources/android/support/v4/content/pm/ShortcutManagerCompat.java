package android.support.v4.content.pm;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentSender;
import android.content.pm.ShortcutManager;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.annotation.VisibleForTesting;

public class ShortcutManagerCompat {
    @VisibleForTesting
    static final String ACTION_INSTALL_SHORTCUT = "com.android.launcher.action.INSTALL_SHORTCUT";
    @VisibleForTesting
    static final String INSTALL_SHORTCUT_PERMISSION = "com.android.launcher.permission.INSTALL_SHORTCUT";

    private ShortcutManagerCompat() {
    }

    @NonNull
    public static Intent createShortcutResultIntent(@NonNull Context context, @NonNull ShortcutInfoCompat shortcutInfoCompat) {
        Intent intent = null;
        if (Build.VERSION.SDK_INT >= 26) {
            intent = ((ShortcutManager) context.getSystemService(ShortcutManager.class)).createShortcutResultIntent(shortcutInfoCompat.toShortcutInfo());
        }
        if (intent == null) {
            intent = new Intent();
        }
        return shortcutInfoCompat.addToIntent(intent);
    }

    /* JADX WARNING: Removed duplicated region for block: B:11:0x0039  */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public static boolean isRequestPinShortcutSupported(@android.support.annotation.NonNull android.content.Context r4) {
        /*
            r3 = 0
            int r0 = android.os.Build.VERSION.SDK_INT
            r1 = 26
            if (r0 >= r1) goto L_0x002b
            java.lang.String r0 = "com.android.launcher.permission.INSTALL_SHORTCUT"
            int r0 = android.support.v4.content.ContextCompat.checkSelfPermission(r4, r0)
            if (r0 != 0) goto L_0x0038
            android.content.pm.PackageManager r0 = r4.getPackageManager()
            android.content.Intent r1 = new android.content.Intent
            java.lang.String r2 = "com.android.launcher.action.INSTALL_SHORTCUT"
            r1.<init>(r2)
            java.util.List r0 = r0.queryBroadcastReceivers(r1, r3)
            java.util.Iterator r1 = r0.iterator()
        L_0x0024:
            boolean r0 = r1.hasNext()
            if (r0 != 0) goto L_0x0039
            return r3
        L_0x002b:
            java.lang.Class<android.content.pm.ShortcutManager> r0 = android.content.pm.ShortcutManager.class
            java.lang.Object r0 = r4.getSystemService(r0)
            android.content.pm.ShortcutManager r0 = (android.content.pm.ShortcutManager) r0
            boolean r0 = r0.isRequestPinShortcutSupported()
            return r0
        L_0x0038:
            return r3
        L_0x0039:
            java.lang.Object r0 = r1.next()
            android.content.pm.ResolveInfo r0 = (android.content.pm.ResolveInfo) r0
            android.content.pm.ActivityInfo r0 = r0.activityInfo
            java.lang.String r0 = r0.permission
            boolean r2 = android.text.TextUtils.isEmpty(r0)
            if (r2 == 0) goto L_0x004b
        L_0x0049:
            r0 = 1
            return r0
        L_0x004b:
            java.lang.String r2 = "com.android.launcher.permission.INSTALL_SHORTCUT"
            boolean r0 = r2.equals(r0)
            if (r0 != 0) goto L_0x0049
            goto L_0x0024
        */
        throw new UnsupportedOperationException("Method not decompiled: android.support.v4.content.pm.ShortcutManagerCompat.isRequestPinShortcutSupported(android.content.Context):boolean");
    }

    public static boolean requestPinShortcut(@NonNull Context context, @NonNull ShortcutInfoCompat shortcutInfoCompat, @Nullable final IntentSender intentSender) {
        if (Build.VERSION.SDK_INT >= 26) {
            return ((ShortcutManager) context.getSystemService(ShortcutManager.class)).requestPinShortcut(shortcutInfoCompat.toShortcutInfo(), intentSender);
        }
        if (!isRequestPinShortcutSupported(context)) {
            return false;
        }
        Intent addToIntent = shortcutInfoCompat.addToIntent(new Intent(ACTION_INSTALL_SHORTCUT));
        if (intentSender != null) {
            context.sendOrderedBroadcast(addToIntent, (String) null, new BroadcastReceiver() {
                public void onReceive(Context context, Intent intent) {
                    try {
                        intentSender.sendIntent(context, 0, (Intent) null, (IntentSender.OnFinished) null, (Handler) null);
                    } catch (IntentSender.SendIntentException e) {
                    }
                }
            }, (Handler) null, -1, (String) null, (Bundle) null);
            return true;
        }
        context.sendBroadcast(addToIntent);
        return true;
    }
}
