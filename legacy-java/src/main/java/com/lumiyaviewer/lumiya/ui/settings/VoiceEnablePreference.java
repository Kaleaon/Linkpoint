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
import com.lumiyaviewer.lumiya.voiceintf.VoicePluginServiceConnection;

/* loaded from: classes.dex */
public class VoiceEnablePreference extends CheckBoxPreference {
    public VoiceEnablePreference(Context context) {
        super(context);
        initVoicePrefCapability();
    }

    public VoiceEnablePreference(Context context, AttributeSet attributeSet) {
        super(context, attributeSet);
        initVoicePrefCapability();
    }

    public VoiceEnablePreference(Context context, AttributeSet attributeSet, int i) {
        super(context, attributeSet, i);
        initVoicePrefCapability();
    }

    public VoiceEnablePreference(Context context, AttributeSet attributeSet, int i, int i2) {
        super(context, attributeSet, i, i2);
        initVoicePrefCapability();
    }

    private void initVoicePrefCapability() {
        boolean isPluginSupported = VoicePluginServiceConnection.isPluginSupported();
        setEnabled(isPluginSupported);
        if (isPluginSupported) {
            return;
        }
        setChecked(false);
        setSummary(getContext().getString(R.string.voice_not_supported));
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    /* renamed from: lambda$-com_lumiyaviewer_lumiya_ui_settings_VoiceEnablePreference_2130, reason: not valid java name */
    public /* synthetic */ void m872xef85dc(DialogInterface dialogInterface, int i) {
        dialogInterface.dismiss();
        Intent intent = new Intent("android.intent.action.VIEW");
        intent.setData(Uri.parse(LicenseChecker.VOICE_PLUGIN_URL));
        getContext().startActivity(intent);
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @Override // android.support.v7.preference.TwoStatePreference, android.support.v7.preference.Preference
    public void onClick() {
        VoicePluginServiceConnection.setInstallOfferDisplayed(true);
        super.onClick();
        if (!isChecked() || VoicePluginServiceConnection.checkPluginInstalled(getContext())) {
            return;
        }
        new AlertDialog.Builder(getContext()).setTitle(R.string.enable_voice).setMessage(getContext().getString(R.string.enable_voice_plugin_message, LicenseChecker.APP_STORE_NAME)).setPositiveButton("Yes", new DialogInterface.OnClickListener() { // from class: com.lumiyaviewer.lumiya.ui.settings.-$Lambda$0TY5QW0tBCNc4BcO-pElkyve9kc.1
            private final /* synthetic */ void $m$0(DialogInterface dialogInterface, int i) {
                ((VoiceEnablePreference) this).m872xef85dc(dialogInterface, i);
            }

            @Override // android.content.DialogInterface.OnClickListener
            public final void onClick(DialogInterface dialogInterface, int i) {
                $m$0(dialogInterface, i);
            }
        }).setNegativeButton("No", new DialogInterface.OnClickListener() { // from class: com.lumiyaviewer.lumiya.ui.settings.-$Lambda$0TY5QW0tBCNc4BcO-pElkyve9kc
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
