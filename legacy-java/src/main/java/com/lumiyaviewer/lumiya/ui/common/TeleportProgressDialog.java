package com.lumiyaviewer.lumiya.ui.common;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Handler;
import com.lumiyaviewer.lumiya.Debug;
import com.lumiyaviewer.lumiya.R;
import com.lumiyaviewer.lumiya.eventbus.EventHandler;
import com.lumiyaviewer.lumiya.slproto.SLAgentCircuit;
import com.lumiyaviewer.lumiya.slproto.events.SLTeleportResultEvent;
import com.lumiyaviewer.lumiya.slproto.users.manager.UserManager;
import com.lumiyaviewer.lumiya.ui.chat.ChatNewActivity;
import java.util.UUID;

/* loaded from: classes.dex */
public class TeleportProgressDialog extends ProgressDialog implements DialogInterface.OnCancelListener {
    private final Handler mHandler;
    private final UserManager userManager;

    public TeleportProgressDialog(Context context, UserManager userManager, int i) {
        super(context);
        this.mHandler = new Handler();
        this.userManager = userManager;
        setMessage(context.getString(i));
        setCancelable(true);
        setIndeterminate(true);
        setOnCancelListener(this);
    }

    public static void TeleportToLandmark(final Context context, final UserManager userManager, final UUID uuid, boolean z) {
        final SLAgentCircuit activeAgentCircuit;
        if (userManager == null || (activeAgentCircuit = userManager.getActiveAgentCircuit()) == null || !activeAgentCircuit.getModules().rlvController.canTeleportToLandmark()) {
            return;
        }
        final Runnable runnable = new Runnable() { // from class: com.lumiyaviewer.lumiya.ui.common.-$Lambda$8gWLFwrhoxKapEC8iWggaUhFR1c.2
            private final /* synthetic */ void $m$0() {
                TeleportProgressDialog.m556x70f40358((SLAgentCircuit) activeAgentCircuit, (UUID) uuid, (Context) context, (UserManager) userManager);
            }

            @Override // java.lang.Runnable
            public final void run() {
                $m$0();
            }
        };
        if (!z) {
            runnable.run();
            return;
        }
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setMessage(context.getString(R.string.teleport_confirm_title)).setCancelable(true).setPositiveButton("Yes", new DialogInterface.OnClickListener() { // from class: com.lumiyaviewer.lumiya.ui.common.-$Lambda$8gWLFwrhoxKapEC8iWggaUhFR1c.1
            private final /* synthetic */ void $m$0(DialogInterface dialogInterface, int i) {
                TeleportProgressDialog.m557x70f46c95((Runnable) runnable, dialogInterface, i);
            }

            @Override // android.content.DialogInterface.OnClickListener
            public final void onClick(DialogInterface dialogInterface, int i) {
                $m$0(dialogInterface, i);
            }
        }).setNegativeButton("No", new DialogInterface.OnClickListener() { // from class: com.lumiyaviewer.lumiya.ui.common.-$Lambda$8gWLFwrhoxKapEC8iWggaUhFR1c
            private final /* synthetic */ void $m$0(DialogInterface dialogInterface, int i) {
                dialogInterface.cancel();
            }

            @Override // android.content.DialogInterface.OnClickListener
            public final void onClick(DialogInterface dialogInterface, int i) {
                $m$0(dialogInterface, i);
            }
        });
        builder.create().show();
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    /* renamed from: lambda$-com_lumiyaviewer_lumiya_ui_common_TeleportProgressDialog_1322, reason: not valid java name */
    public static /* synthetic */ void m556x70f40358(SLAgentCircuit sLAgentCircuit, UUID uuid, Context context, UserManager userManager) {
        if (sLAgentCircuit.getModules().rlvController.canTeleportToLandmark()) {
            sLAgentCircuit.TeleportToLandmarkAsset(uuid);
            new TeleportProgressDialog(context, userManager, R.string.teleporting_progress_message).show();
        }
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    /* renamed from: lambda$-com_lumiyaviewer_lumiya_ui_common_TeleportProgressDialog_2034, reason: not valid java name */
    public static /* synthetic */ void m557x70f46c95(Runnable runnable, DialogInterface dialogInterface, int i) {
        dialogInterface.dismiss();
        runnable.run();
    }

    @EventHandler
    public void handleTeleportResult(SLTeleportResultEvent sLTeleportResultEvent) {
        boolean isShowing = isShowing();
        Debug.Log("TeleportResult: success = " + sLTeleportResultEvent.success);
        try {
            dismiss();
        } catch (Exception e) {
            Debug.Warning(e);
        }
        if (sLTeleportResultEvent.success) {
            Intent intent = new Intent(getContext(), (Class<?>) ChatNewActivity.class);
            if (this.userManager != null) {
                ActivityUtils.setActiveAgentID(intent, this.userManager.getUserID());
            }
            intent.addFlags(335577088);
            getContext().startActivity(intent);
            return;
        }
        if (isShowing) {
            AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
            builder.setTitle(getContext().getString(R.string.teleport_failed_dialog_title));
            builder.setMessage(sLTeleportResultEvent.message);
            builder.setCancelable(true);
            builder.create().show();
        }
    }

    @Override // android.content.DialogInterface.OnCancelListener
    public void onCancel(DialogInterface dialogInterface) {
        if (this.userManager != null) {
            try {
                SLAgentCircuit activeAgentCircuit = this.userManager.getActiveAgentCircuit();
                if (activeAgentCircuit != null) {
                    activeAgentCircuit.getModules().worldMap.CancelPendingTeleports();
                }
            } catch (Exception e) {
                Debug.Warning(e);
            }
        }
    }

    @Override // android.app.ProgressDialog, android.app.Dialog
    public void onStart() {
        super.onStart();
        if (this.userManager != null) {
            this.userManager.getEventBus().subscribe(this, null, this.mHandler);
        }
    }

    @Override // android.app.ProgressDialog, android.app.Dialog
    public void onStop() {
        if (this.userManager != null) {
            this.userManager.getEventBus().unsubscribe(this);
        }
        super.onStop();
    }
}
