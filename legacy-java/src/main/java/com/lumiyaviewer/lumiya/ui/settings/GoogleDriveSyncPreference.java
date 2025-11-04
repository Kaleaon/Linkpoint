package com.lumiyaviewer.lumiya.ui.settings;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.support.v7.app.AlertDialog;
import android.support.v7.preference.CheckBoxPreference;
import android.util.AttributeSet;
import com.lumiyaviewer.lumiya.R;
import com.lumiyaviewer.lumiya.licensing.LicenseChecker;
import com.lumiyaviewer.lumiya.sync.CloudSyncServiceConnection;

/* loaded from: classes.dex */
public class GoogleDriveSyncPreference extends CheckBoxPreference {
    public GoogleDriveSyncPreference(Context context) {
        super(context);
    }

    public GoogleDriveSyncPreference(Context context, AttributeSet attributeSet) {
        super(context, attributeSet);
    }

    public GoogleDriveSyncPreference(Context context, AttributeSet attributeSet, int i) {
        super(context, attributeSet, i);
    }

    public GoogleDriveSyncPreference(Context context, AttributeSet attributeSet, int i, int i2) {
        super(context, attributeSet, i, i2);
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    /* renamed from: lambda$-com_lumiyaviewer_lumiya_ui_settings_GoogleDriveSyncPreference_1541, reason: not valid java name */
    public /* synthetic */ void m863x92031b8a(DialogInterface dialogInterface, int i) {
        dialogInterface.dismiss();
        Intent intent = new Intent("android.intent.action.VIEW");
        intent.setData(Uri.parse(LicenseChecker.CLOUD_PLUGIN_URL));
        getContext().startActivity(intent);
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @Override // android.support.v7.preference.TwoStatePreference, android.support.v7.preference.Preference
    public void onClick() {
        super.onClick();
        if (!isChecked() || CloudSyncServiceConnection.checkPluginInstalled(getContext())) {
            return;
        }
        new AlertDialog.Builder(getContext()).setTitle(R.string.enable_google_drive).setMessage(getContext().getString(R.string.enable_google_drive_message, LicenseChecker.APP_STORE_NAME)).setPositiveButton("Yes", new DialogInterface.OnClickListener() { // from class: com.lumiyaviewer.lumiya.ui.settings.-$Lambda$GONhG2H9-w043w0Zbd-p0nmAUgQ.1
            private final /* synthetic */ void $m$0(DialogInterface dialogInterface, int i) {
                ((GoogleDriveSyncPreference) this).m863x92031b8a(dialogInterface, i);
            }

            @Override // android.content.DialogInterface.OnClickListener
            public final void onClick(DialogInterface dialogInterface, int i) {
                $m$0(dialogInterface, i);
            }
        }).setNegativeButton("No", new DialogInterface.OnClickListener() { // from class: com.lumiyaviewer.lumiya.ui.settings.-$Lambda$GONhG2H9-w043w0Zbd-p0nmAUgQ
            private final /* synthetic */ void $m$0(DialogInterface dialogInterface, int i) {
                dialogInterface.cancel();
            }

            @Override // android.content.DialogInterface.OnClickListener
            public final void onClick(DialogInterface dialogInterface, int i) {
                $m$0(dialogInterface, i);
            }
        }).setCancelable(true).create().show();
    }
}
