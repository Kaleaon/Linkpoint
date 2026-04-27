// 
// Decompiled by Procyon v0.6.0
// 

package com.lumiyaviewer.lumiya.ui.common;

import android.content.DialogInterface$OnClickListener;
import android.support.v7.app.AlertDialog;
import com.lumiyaviewer.lumiya.utils.UUIDPool;
import android.content.Context;
import com.lumiyaviewer.lumiya.slproto.SLAgentCircuit;
import com.lumiyaviewer.lumiya.slproto.users.manager.UserManager;
import android.app.Activity;
import android.content.DialogInterface;

public class TeleportHomeDialog
{
    public static void show(final Activity activity) {
        final UserManager userManager = ActivityUtils.getUserManager(activity.getIntent());
        if (userManager != null) {
            final SLAgentCircuit activeAgentCircuit = userManager.getActiveAgentCircuit();
            if (activeAgentCircuit != null && activeAgentCircuit.getModules().rlvController.canTeleportToLocation()) {
                new AlertDialog.Builder((Context)activity).setMessage(2131297086).setPositiveButton("Yes", (DialogInterface$OnClickListener)new _$Lambda$sKhJxooMqZpn4u0mFmtSpF7hGx8$1(activity, userManager, activeAgentCircuit)).setNegativeButton("No", (DialogInterface$OnClickListener)new _$Lambda$sKhJxooMqZpn4u0mFmtSpF7hGx8()).setCancelable(true).create().show();
            }
        }
    }
}
