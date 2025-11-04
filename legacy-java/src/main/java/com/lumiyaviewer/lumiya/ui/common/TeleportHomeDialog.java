package com.lumiyaviewer.lumiya.ui.common;

import android.app.Activity;
import android.content.DialogInterface;
import android.support.v7.app.AlertDialog;
import com.lumiyaviewer.lumiya.R;
import com.lumiyaviewer.lumiya.slproto.SLAgentCircuit;
import com.lumiyaviewer.lumiya.slproto.users.manager.UserManager;
import com.lumiyaviewer.lumiya.utils.UUIDPool;

/* loaded from: classes.dex */
public class TeleportHomeDialog {
    /* JADX INFO: Access modifiers changed from: package-private */
    /* renamed from: lambda$-com_lumiyaviewer_lumiya_ui_common_TeleportHomeDialog_956, reason: not valid java name */
    public static /* synthetic */ void m555lambda$com_lumiyaviewer_lumiya_ui_common_TeleportHomeDialog_956(Activity activity, UserManager userManager, SLAgentCircuit sLAgentCircuit, DialogInterface dialogInterface, int i) {
        new TeleportProgressDialog(activity, userManager, R.string.teleporting_progress_message).show();
        sLAgentCircuit.TeleportToLandmarkAsset(UUIDPool.ZeroUUID);
        dialogInterface.dismiss();
    }

    public static void show(final Activity activity) {
        final SLAgentCircuit activeAgentCircuit;
        final UserManager userManager = ActivityUtils.getUserManager(activity.getIntent());
        if (userManager == null || (activeAgentCircuit = userManager.getActiveAgentCircuit()) == null || !activeAgentCircuit.getModules().rlvController.canTeleportToLocation()) {
            return;
        }
        new AlertDialog.Builder(activity).setMessage(R.string.teleport_home_confirm_title).setPositiveButton("Yes", new DialogInterface.OnClickListener() { // from class: com.lumiyaviewer.lumiya.ui.common.-$Lambda$sKhJxooMqZpn4u0mFmtSpF7hGx8.1
            private final /* synthetic */ void $m$0(DialogInterface dialogInterface, int i) {
                TeleportHomeDialog.m555lambda$com_lumiyaviewer_lumiya_ui_common_TeleportHomeDialog_956((Activity) activity, (UserManager) userManager, (SLAgentCircuit) activeAgentCircuit, dialogInterface, i);
            }

            @Override // android.content.DialogInterface.OnClickListener
            public final void onClick(DialogInterface dialogInterface, int i) {
                $m$0(dialogInterface, i);
            }
        }).setNegativeButton("No", new DialogInterface.OnClickListener() { // from class: com.lumiyaviewer.lumiya.ui.common.-$Lambda$sKhJxooMqZpn4u0mFmtSpF7hGx8
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
