// 
// Decompiled by Procyon v0.6.0
// 

package com.lumiyaviewer.lumiya.ui.settings;

import android.content.DialogInterface$OnClickListener;
import android.support.v7.app.AlertDialog;
import android.net.Uri;
import android.content.Intent;
import android.content.DialogInterface;
import com.lumiyaviewer.lumiya.voiceintf.VoicePluginServiceConnection;
import android.util.AttributeSet;
import android.content.Context;
import android.support.v7.preference.CheckBoxPreference;

public class VoiceEnablePreference extends CheckBoxPreference
{
    public VoiceEnablePreference(final Context context) {
        super(context);
        this.initVoicePrefCapability();
    }
    
    public VoiceEnablePreference(final Context context, final AttributeSet set) {
        super(context, set);
        this.initVoicePrefCapability();
    }
    
    public VoiceEnablePreference(final Context context, final AttributeSet set, final int n) {
        super(context, set, n);
        this.initVoicePrefCapability();
    }
    
    public VoiceEnablePreference(final Context context, final AttributeSet set, final int n, final int n2) {
        super(context, set, n, n2);
        this.initVoicePrefCapability();
    }
    
    private void initVoicePrefCapability() {
        final boolean pluginSupported = VoicePluginServiceConnection.isPluginSupported();
        this.setEnabled(pluginSupported);
        if (!pluginSupported) {
            this.setChecked(false);
            this.setSummary(this.getContext().getString(2131297150));
        }
    }
    
    @Override
    protected void onClick() {
        VoicePluginServiceConnection.setInstallOfferDisplayed(true);
        super.onClick();
        if (this.isChecked() && !VoicePluginServiceConnection.checkPluginInstalled(this.getContext())) {
            new AlertDialog.Builder(this.getContext()).setTitle(2131296531).setMessage(this.getContext().getString(2131296532, new Object[] { "Google Play" })).setPositiveButton("Yes", (DialogInterface$OnClickListener)new _$Lambda$0TY5QW0tBCNc4BcO_pElkyve9kc$1(this)).setNegativeButton("No", (DialogInterface$OnClickListener)new _$Lambda$0TY5QW0tBCNc4BcO_pElkyve9kc()).setCancelable(true).create().show();
        }
    }
}
