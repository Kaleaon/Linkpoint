// 
// Decompiled by Procyon v0.6.0
// 

package com.lumiyaviewer.lumiya.ui.settings;

import android.content.DialogInterface$OnClickListener;
import android.support.v7.app.AlertDialog;
import com.lumiyaviewer.lumiya.sync.CloudSyncServiceConnection;
import android.net.Uri;
import android.content.Intent;
import android.content.DialogInterface;
import android.util.AttributeSet;
import android.content.Context;
import android.support.v7.preference.CheckBoxPreference;

public class GoogleDriveSyncPreference extends CheckBoxPreference
{
    public GoogleDriveSyncPreference(final Context context) {
        super(context);
    }
    
    public GoogleDriveSyncPreference(final Context context, final AttributeSet set) {
        super(context, set);
    }
    
    public GoogleDriveSyncPreference(final Context context, final AttributeSet set, final int n) {
        super(context, set, n);
    }
    
    public GoogleDriveSyncPreference(final Context context, final AttributeSet set, final int n, final int n2) {
        super(context, set, n, n2);
    }
    
    @Override
    protected void onClick() {
        super.onClick();
        if (this.isChecked() && !CloudSyncServiceConnection.checkPluginInstalled(this.getContext())) {
            new AlertDialog.Builder(this.getContext()).setTitle(2131296524).setMessage(this.getContext().getString(2131296525, new Object[] { "Google Play" })).setPositiveButton("Yes", (DialogInterface$OnClickListener)new _$Lambda$GONhG2H9_w043w0Zbd_p0nmAUgQ$1(this)).setNegativeButton("No", (DialogInterface$OnClickListener)new _$Lambda$GONhG2H9_w043w0Zbd_p0nmAUgQ()).setCancelable(true).create().show();
        }
    }
}
