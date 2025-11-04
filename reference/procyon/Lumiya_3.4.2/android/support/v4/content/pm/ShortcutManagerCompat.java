// 
// Decompiled by Procyon v0.6.0
// 

package android.support.v4.content.pm;

import android.os.Bundle;
import android.content.IntentSender$SendIntentException;
import android.os.Handler;
import android.content.IntentSender$OnFinished;
import android.content.BroadcastReceiver;
import android.support.annotation.Nullable;
import android.content.IntentSender;
import java.util.Iterator;
import android.text.TextUtils;
import android.content.pm.ResolveInfo;
import android.support.v4.content.ContextCompat;
import android.content.pm.ShortcutManager;
import android.os.Build$VERSION;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.content.Context;
import android.support.annotation.VisibleForTesting;

public class ShortcutManagerCompat
{
    @VisibleForTesting
    static final String ACTION_INSTALL_SHORTCUT = "com.android.launcher.action.INSTALL_SHORTCUT";
    @VisibleForTesting
    static final String INSTALL_SHORTCUT_PERMISSION = "com.android.launcher.permission.INSTALL_SHORTCUT";
    
    private ShortcutManagerCompat() {
    }
    
    @NonNull
    public static Intent createShortcutResultIntent(@NonNull final Context context, @NonNull final ShortcutInfoCompat shortcutInfoCompat) {
        final Intent intent = null;
        Intent shortcutResultIntent;
        if (Build$VERSION.SDK_INT < 26) {
            shortcutResultIntent = intent;
        }
        else {
            shortcutResultIntent = ((ShortcutManager)context.getSystemService((Class)ShortcutManager.class)).createShortcutResultIntent(shortcutInfoCompat.toShortcutInfo());
        }
        if (shortcutResultIntent == null) {
            shortcutResultIntent = new Intent();
        }
        return shortcutInfoCompat.addToIntent(shortcutResultIntent);
    }
    
    public static boolean isRequestPinShortcutSupported(@NonNull final Context context) {
        if (Build$VERSION.SDK_INT >= 26) {
            return ((ShortcutManager)context.getSystemService((Class)ShortcutManager.class)).isRequestPinShortcutSupported();
        }
        if (ContextCompat.checkSelfPermission(context, "com.android.launcher.permission.INSTALL_SHORTCUT") == 0) {
            final Iterator iterator = context.getPackageManager().queryBroadcastReceivers(new Intent("com.android.launcher.action.INSTALL_SHORTCUT"), 0).iterator();
            while (iterator.hasNext()) {
                final String permission = ((ResolveInfo)iterator.next()).activityInfo.permission;
                if (!TextUtils.isEmpty((CharSequence)permission) && !"com.android.launcher.permission.INSTALL_SHORTCUT".equals(permission)) {
                    continue;
                }
                return true;
            }
            return false;
        }
        return false;
    }
    
    public static boolean requestPinShortcut(@NonNull final Context context, @NonNull final ShortcutInfoCompat shortcutInfoCompat, @Nullable final IntentSender intentSender) {
        if (Build$VERSION.SDK_INT >= 26) {
            return ((ShortcutManager)context.getSystemService((Class)ShortcutManager.class)).requestPinShortcut(shortcutInfoCompat.toShortcutInfo(), intentSender);
        }
        if (!isRequestPinShortcutSupported(context)) {
            return false;
        }
        final Intent addToIntent = shortcutInfoCompat.addToIntent(new Intent("com.android.launcher.action.INSTALL_SHORTCUT"));
        if (intentSender != null) {
            context.sendOrderedBroadcast(addToIntent, (String)null, (BroadcastReceiver)new BroadcastReceiver() {
                public void onReceive(final Context context, final Intent intent) {
                    try {
                        intentSender.sendIntent(context, 0, (Intent)null, (IntentSender$OnFinished)null, (Handler)null);
                    }
                    catch (final IntentSender$SendIntentException ex) {}
                }
            }, (Handler)null, -1, (String)null, (Bundle)null);
            return true;
        }
        context.sendBroadcast(addToIntent);
        return true;
    }
}
