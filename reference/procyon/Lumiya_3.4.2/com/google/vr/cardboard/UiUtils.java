// 
// Decompiled by Procyon v0.6.0
// 

package com.google.vr.cardboard;

import android.app.Activity;
import android.content.DialogInterface$OnCancelListener;
import android.content.ActivityNotFoundException;
import android.widget.Toast;
import android.net.Uri;
import android.content.DialogInterface;
import android.content.DialogInterface$OnClickListener;
import android.app.AlertDialog;
import java.util.Iterator;
import java.util.List;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import java.util.ArrayList;
import android.content.Intent;
import android.content.Context;
import android.app.AlertDialog$Builder;

public class UiUtils
{
    private static final String CARDBOARD_CONFIGURE_ACTION = "com.google.vrtoolkit.cardboard.CONFIGURE";
    private static final String CARDBOARD_WEBSITE = "https://google.com/cardboard/cfg";
    private static final String DAYDREAM_HELP_CENTER_LINK = "https://support.google.com/daydream?p=daydream_help_menu";
    public static AlertDialog$Builder dialogBuilderForTesting;
    public static StoragePermissionUtils permissionUtils;
    
    static {
        UiUtils.permissionUtils = new StoragePermissionUtils();
    }
    
    private static AlertDialog$Builder createAlertDialogBuilder(final Context context) {
        if (UiUtils.dialogBuilderForTesting == null) {
            return new AlertDialog$Builder(context, R.style.GvrDialogTheme);
        }
        return UiUtils.dialogBuilderForTesting;
    }
    
    public static void launchOrInstallCardboard(final Context context) {
        final PackageManager packageManager = context.getPackageManager();
        final Intent intent = new Intent();
        intent.setAction("com.google.vrtoolkit.cardboard.CONFIGURE");
        final List queryIntentActivities = packageManager.queryIntentActivities(intent, 0);
        final ArrayList list = new ArrayList();
        final Iterator iterator = queryIntentActivities.iterator();
        Integer n = null;
        while (iterator.hasNext()) {
            final ResolveInfo resolveInfo = (ResolveInfo)iterator.next();
            final String packageName = resolveInfo.activityInfo.packageName;
            if (PackageUtils.isGooglePackage(packageName)) {
                int priority = resolveInfo.priority;
                if (PackageUtils.isSystemPackage(context, packageName)) {
                    ++priority;
                }
                if (n != null) {
                    if (priority <= n) {
                        if (priority < n) {
                            continue;
                        }
                    }
                    else {
                        n = priority;
                        list.clear();
                    }
                }
                else {
                    n = priority;
                }
                final Intent intent2 = new Intent(intent);
                intent2.setClassName(packageName, resolveInfo.activityInfo.name);
                list.add(intent2);
            }
        }
        if (!VrParamsProviderFactory.isContentProviderAvailable(context)) {
            UiUtils.permissionUtils.requestStoragePermission(context);
        }
        if (!list.isEmpty()) {
            Intent intent3;
            if (list.size() != 1) {
                intent3 = intent;
            }
            else {
                intent3 = (Intent)list.get(0);
            }
            context.startActivity(intent3);
            return;
        }
        showInstallDialog(context);
    }
    
    public static AlertDialog showDaydreamHelpCenterDialog(final Context context, final int title, final int message, final Runnable runnable) {
        final DialogInterface$OnClickListener dialogInterface$OnClickListener = (DialogInterface$OnClickListener)new DialogInterface$OnClickListener() {
            public final void onClick(final DialogInterface dialogInterface, final int n) {
                final Intent intent = new Intent("android.intent.action.VIEW", Uri.parse("https://support.google.com/daydream?p=daydream_help_menu"));
                try {
                    context.startActivity(intent);
                }
                catch (final ActivityNotFoundException ex) {
                    Toast.makeText(context, R.string.no_browser_text, 1).show();
                    dialogInterface.cancel();
                }
            }
        };
        final AlertDialog$Builder alertDialogBuilder = createAlertDialogBuilder(context);
        alertDialogBuilder.setTitle(title).setMessage(message).setCancelable(false).setPositiveButton(R.string.dialog_button_open_help_center, (DialogInterface$OnClickListener)dialogInterface$OnClickListener).setNegativeButton(R.string.dialog_button_got_it, (DialogInterface$OnClickListener)new DialogInterface$OnClickListener() {
            public final void onClick(final DialogInterface dialogInterface, final int n) {
                dialogInterface.cancel();
            }
        });
        if (runnable != null) {
            alertDialogBuilder.setOnCancelListener((DialogInterface$OnCancelListener)new DialogInterface$OnCancelListener() {
                public final void onCancel(final DialogInterface dialogInterface) {
                    runnable.run();
                }
            });
        }
        final AlertDialog create = alertDialogBuilder.create();
        create.setCanceledOnTouchOutside(false);
        return showImmersiveDialog(context, create);
    }
    
    private static AlertDialog showImmersiveDialog(final Context context, final AlertDialog alertDialog) {
        alertDialog.getWindow().setFlags(8, 8);
        alertDialog.show();
        final Activity activity = ContextUtils.getActivity(context);
        if (activity != null) {
            alertDialog.getWindow().getDecorView().setSystemUiVisibility(activity.getWindow().getDecorView().getSystemUiVisibility());
        }
        alertDialog.getWindow().clearFlags(8);
        return alertDialog;
    }
    
    private static void showInstallDialog(final Context context) {
        final DialogInterface$OnClickListener dialogInterface$OnClickListener = (DialogInterface$OnClickListener)new DialogInterface$OnClickListener() {
            public final void onClick(final DialogInterface dialogInterface, final int n) {
                try {
                    context.startActivity(new Intent("android.intent.action.VIEW", Uri.parse("https://google.com/cardboard/cfg")));
                }
                catch (final ActivityNotFoundException ex) {
                    Toast.makeText(context, R.string.no_browser_text, 1).show();
                }
            }
        };
        final AlertDialog$Builder alertDialogBuilder = createAlertDialogBuilder(context);
        alertDialogBuilder.setTitle(R.string.dialog_title).setMessage(R.string.dialog_message_no_cardboard).setPositiveButton(R.string.go_to_playstore_button, (DialogInterface$OnClickListener)dialogInterface$OnClickListener).setNegativeButton(R.string.cancel_button, (DialogInterface$OnClickListener)null);
        showImmersiveDialog(context, alertDialogBuilder.create());
    }
}
