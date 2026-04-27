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
import com.lumiyaviewer.lumiya.voiceintf.VoicePluginServiceConnection;

public class VoiceEnablePreference extends CheckBoxPreference
{

    public VoiceEnablePreference(Context context)
    {
        super(context);
        initVoicePrefCapability();
    }

    public VoiceEnablePreference(Context context, AttributeSet attributeset)
    {
        super(context, attributeset);
        initVoicePrefCapability();
    }

    public VoiceEnablePreference(Context context, AttributeSet attributeset, int i)
    {
        super(context, attributeset, i);
        initVoicePrefCapability();
    }

    public VoiceEnablePreference(Context context, AttributeSet attributeset, int i, int j)
    {
        super(context, attributeset, i, j);
        initVoicePrefCapability();
    }

    private void initVoicePrefCapability()
    {
        boolean flag = VoicePluginServiceConnection.isPluginSupported();
        setEnabled(flag);
        if (!flag)
        {
            setChecked(false);
            setSummary(getContext().getString(0x7f09037e));
        }
    }

    static void lambda$_2D_com_lumiyaviewer_lumiya_ui_settings_VoiceEnablePreference_2570(DialogInterface dialoginterface, int i)
    {
        dialoginterface.cancel();
    }

    void lambda$_2D_com_lumiyaviewer_lumiya_ui_settings_VoiceEnablePreference_2130(DialogInterface dialoginterface, int i)
    {
        dialoginterface.dismiss();
        dialoginterface = new Intent("android.intent.action.VIEW");
        dialoginterface.setData(Uri.parse("https://play.google.com/store/apps/details?id=com.lumiyaviewer.lumiya.voice"));
        getContext().startActivity(dialoginterface);
    }

    protected void onClick()
    {
        VoicePluginServiceConnection.setInstallOfferDisplayed(true);
        super.onClick();
        if (isChecked() && !VoicePluginServiceConnection.checkPluginInstalled(getContext()))
        {
            (new android.support.v7.app.AlertDialog.Builder(getContext())).setTitle(0x7f090113).setMessage(getContext().getString(0x7f090114, new Object[] {
                "Google Play"
            })).setPositiveButton("Yes", new _2D_.Lambda._cls0TY5QW0tBCNc4BcO_2D_pElkyve9kc._cls1(this)).setNegativeButton("No", new _2D_.Lambda._cls0TY5QW0tBCNc4BcO_2D_pElkyve9kc()).setCancelable(true).create().show();
        }
    }
}
