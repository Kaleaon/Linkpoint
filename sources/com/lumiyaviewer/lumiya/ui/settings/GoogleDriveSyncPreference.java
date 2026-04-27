// Decompiled by Jad v1.5.8e. Copyright 2001 Pavel Kouznetsov.
// Jad home page: http://www.geocities.com/kpdus/jad.html
// Decompiler options: braces fieldsfirst space lnc 

package com.lumiyaviewer.lumiya.ui.settings;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.support.v7.app.AlertDialog;
import android.support.v7.preference.CheckBoxPreference;
import android.util.AttributeSet;
import com.lumiyaviewer.lumiya.sync.CloudSyncServiceConnection;

public class GoogleDriveSyncPreference extends CheckBoxPreference
{

    public GoogleDriveSyncPreference(Context context)
    {
        super(context);
    }

    public GoogleDriveSyncPreference(Context context, AttributeSet attributeset)
    {
        super(context, attributeset);
    }

    public GoogleDriveSyncPreference(Context context, AttributeSet attributeset, int i)
    {
        super(context, attributeset, i);
    }

    public GoogleDriveSyncPreference(Context context, AttributeSet attributeset, int i, int j)
    {
        super(context, attributeset, i, j);
    }

    static void lambda$_2D_com_lumiyaviewer_lumiya_ui_settings_GoogleDriveSyncPreference_1981(DialogInterface dialoginterface, int i)
    {
        dialoginterface.cancel();
    }

    void lambda$_2D_com_lumiyaviewer_lumiya_ui_settings_GoogleDriveSyncPreference_1541(DialogInterface dialoginterface, int i)
    {
        dialoginterface.dismiss();
        dialoginterface = new Intent("android.intent.action.VIEW");
        dialoginterface.setData(Uri.parse("https://play.google.com/store/apps/details?id=com.lumiyaviewer.lumiya.cloud"));
        getContext().startActivity(dialoginterface);
    }

    protected void onClick()
    {
        super.onClick();
        if (isChecked() && !CloudSyncServiceConnection.checkPluginInstalled(getContext()))
        {
            (new android.support.v7.app.AlertDialog.Builder(getContext())).setTitle(0x7f09010c).setMessage(getContext().getString(0x7f09010d, new Object[] {
                "Google Play"
            })).setPositiveButton("Yes", new _2D_.Lambda.GONhG2H9_2D_w043w0Zbd_2D_p0nmAUgQ._cls1(this)).setNegativeButton("No", new _2D_.Lambda.GONhG2H9_2D_w043w0Zbd_2D_p0nmAUgQ()).setCancelable(true).create().show();
        }
    }
}
