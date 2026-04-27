package com.google.vr.cardboard;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.net.Uri;
import android.widget.Toast;
import java.util.ArrayList;
import java.util.List;

public class UiUtils {
    private static final String CARDBOARD_CONFIGURE_ACTION = "com.google.vrtoolkit.cardboard.CONFIGURE";
    private static final String CARDBOARD_WEBSITE = "https://google.com/cardboard/cfg";
    private static final String DAYDREAM_HELP_CENTER_LINK = "https://support.google.com/daydream?p=daydream_help_menu";
    public static AlertDialog.Builder dialogBuilderForTesting;
    public static StoragePermissionUtils permissionUtils = new StoragePermissionUtils();

    private static AlertDialog.Builder createAlertDialogBuilder(Context context) {
        return dialogBuilderForTesting == null ? new AlertDialog.Builder(context, R.style.GvrDialogTheme) : dialogBuilderForTesting;
    }

    public static void launchOrInstallCardboard(Context context) {
        PackageManager packageManager = context.getPackageManager();
        Intent intent = new Intent();
        intent.setAction(CARDBOARD_CONFIGURE_ACTION);
        List<ResolveInfo> queryIntentActivities = packageManager.queryIntentActivities(intent, 0);
        ArrayList arrayList = new ArrayList();
        Integer num = null;
        for (ResolveInfo next : queryIntentActivities) {
            String str = next.activityInfo.packageName;
            if (PackageUtils.isGooglePackage(str)) {
                int i = next.priority;
                if (PackageUtils.isSystemPackage(context, str)) {
                    i++;
                }
                if (num == null) {
                    num = Integer.valueOf(i);
                } else if (i > num.intValue()) {
                    num = Integer.valueOf(i);
                    arrayList.clear();
                } else if (i < num.intValue()) {
                }
                Intent intent2 = new Intent(intent);
                intent2.setClassName(str, next.activityInfo.name);
                arrayList.add(intent2);
            }
        }
        if (!VrParamsProviderFactory.isContentProviderAvailable(context)) {
            permissionUtils.requestStoragePermission(context);
        }
        if (!arrayList.isEmpty()) {
            context.startActivity(arrayList.size() != 1 ? intent : (Intent) arrayList.get(0));
        } else {
            showInstallDialog(context);
        }
    }

    public static AlertDialog showDaydreamHelpCenterDialog(final Context context, int i, int i2, final Runnable runnable) {
        AnonymousClass1 r0 = new DialogInterface.OnClickListener() {
            public final void onClick(DialogInterface dialogInterface, int i) {
                try {
                    context.startActivity(new Intent("android.intent.action.VIEW", Uri.parse(UiUtils.DAYDREAM_HELP_CENTER_LINK)));
                } catch (ActivityNotFoundException e) {
                    Toast.makeText(context, R.string.no_browser_text, 1).show();
                    dialogInterface.cancel();
                }
            }
        };
        AlertDialog.Builder createAlertDialogBuilder = createAlertDialogBuilder(context);
        createAlertDialogBuilder.setTitle(i).setMessage(i2).setCancelable(false).setPositiveButton(R.string.dialog_button_open_help_center, r0).setNegativeButton(R.string.dialog_button_got_it, new DialogInterface.OnClickListener() {
            public final void onClick(DialogInterface dialogInterface, int i) {
                dialogInterface.cancel();
            }
        });
        if (runnable != null) {
            createAlertDialogBuilder.setOnCancelListener(new DialogInterface.OnCancelListener() {
                public final void onCancel(DialogInterface dialogInterface) {
                    runnable.run();
                }
            });
        }
        AlertDialog create = createAlertDialogBuilder.create();
        create.setCanceledOnTouchOutside(false);
        return showImmersiveDialog(context, create);
    }

    private static AlertDialog showImmersiveDialog(Context context, AlertDialog alertDialog) {
        alertDialog.getWindow().setFlags(8, 8);
        alertDialog.show();
        Activity activity = ContextUtils.getActivity(context);
        if (activity != null) {
            alertDialog.getWindow().getDecorView().setSystemUiVisibility(activity.getWindow().getDecorView().getSystemUiVisibility());
        }
        alertDialog.getWindow().clearFlags(8);
        return alertDialog;
    }

    private static void showInstallDialog(final Context context) {
        AnonymousClass4 r0 = new DialogInterface.OnClickListener() {
            public final void onClick(DialogInterface dialogInterface, int i) {
                try {
                    context.startActivity(new Intent("android.intent.action.VIEW", Uri.parse(UiUtils.CARDBOARD_WEBSITE)));
                } catch (ActivityNotFoundException e) {
                    Toast.makeText(context, R.string.no_browser_text, 1).show();
                }
            }
        };
        AlertDialog.Builder createAlertDialogBuilder = createAlertDialogBuilder(context);
        createAlertDialogBuilder.setTitle(R.string.dialog_title).setMessage(R.string.dialog_message_no_cardboard).setPositiveButton(R.string.go_to_playstore_button, r0).setNegativeButton(R.string.cancel_button, (DialogInterface.OnClickListener) null);
        showImmersiveDialog(context, createAlertDialogBuilder.create());
    }
}
