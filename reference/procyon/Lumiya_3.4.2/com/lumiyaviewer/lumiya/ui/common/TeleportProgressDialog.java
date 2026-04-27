// 
// Decompiled by Procyon v0.6.0
// 

package com.lumiyaviewer.lumiya.ui.common;

import android.app.Activity;
import com.lumiyaviewer.lumiya.eventbus.EventHandler;
import android.content.Intent;
import com.lumiyaviewer.lumiya.ui.chat.ChatNewActivity;
import com.lumiyaviewer.lumiya.Debug;
import com.lumiyaviewer.lumiya.slproto.events.SLTeleportResultEvent;
import android.content.DialogInterface;
import com.lumiyaviewer.lumiya.slproto.SLAgentCircuit;
import android.content.DialogInterface$OnClickListener;
import android.app.AlertDialog$Builder;
import java.util.UUID;
import android.content.Context;
import com.lumiyaviewer.lumiya.slproto.users.manager.UserManager;
import android.os.Handler;
import android.content.DialogInterface$OnCancelListener;
import android.app.ProgressDialog;

public class TeleportProgressDialog extends ProgressDialog implements DialogInterface$OnCancelListener
{
    private final Handler mHandler;
    private final UserManager userManager;
    
    public TeleportProgressDialog(final Context context, final UserManager userManager, final int n) {
        super(context);
        this.mHandler = new Handler();
        this.userManager = userManager;
        this.setMessage((CharSequence)context.getString(n));
        this.setCancelable(true);
        this.setIndeterminate(true);
        this.setOnCancelListener((DialogInterface$OnCancelListener)this);
    }
    
    public static void TeleportToLandmark(final Context context, final UserManager userManager, final UUID uuid, final boolean b) {
        if (userManager != null) {
            final SLAgentCircuit activeAgentCircuit = userManager.getActiveAgentCircuit();
            if (activeAgentCircuit != null && activeAgentCircuit.getModules().rlvController.canTeleportToLandmark()) {
                final _$Lambda$8gWLFwrhoxKapEC8iWggaUhFR1c$2 $Lambda$8gWLFwrhoxKapEC8iWggaUhFR1c$2 = new _$Lambda$8gWLFwrhoxKapEC8iWggaUhFR1c$2(activeAgentCircuit, uuid, context, userManager);
                if (b) {
                    final AlertDialog$Builder alertDialog$Builder = new AlertDialog$Builder(context);
                    alertDialog$Builder.setMessage((CharSequence)context.getString(2131297084)).setCancelable(true).setPositiveButton((CharSequence)"Yes", (DialogInterface$OnClickListener)new _$Lambda$8gWLFwrhoxKapEC8iWggaUhFR1c$1($Lambda$8gWLFwrhoxKapEC8iWggaUhFR1c$2)).setNegativeButton((CharSequence)"No", (DialogInterface$OnClickListener)new _$Lambda$8gWLFwrhoxKapEC8iWggaUhFR1c());
                    alertDialog$Builder.create().show();
                }
                else {
                    $Lambda$8gWLFwrhoxKapEC8iWggaUhFR1c$2.run();
                }
            }
        }
    }
    
    @EventHandler
    public void handleTeleportResult(SLTeleportResultEvent slTeleportResultEvent) {
        while (true) {
            final boolean showing = this.isShowing();
            Debug.Log("TeleportResult: success = " + slTeleportResultEvent.success);
            while (true) {
                try {
                    this.dismiss();
                    if (!slTeleportResultEvent.success) {
                        if (showing) {
                            final AlertDialog$Builder alertDialog$Builder = new AlertDialog$Builder(this.getContext());
                            alertDialog$Builder.setTitle((CharSequence)this.getContext().getString(2131297085));
                            alertDialog$Builder.setMessage((CharSequence)slTeleportResultEvent.message);
                            alertDialog$Builder.setCancelable(true);
                            alertDialog$Builder.create().show();
                        }
                        return;
                    }
                }
                catch (final Exception ex) {
                    Debug.Warning(ex);
                    continue;
                }
                break;
            }
            slTeleportResultEvent = (SLTeleportResultEvent)new Intent(this.getContext(), (Class)ChatNewActivity.class);
            if (this.userManager != null) {
                ActivityUtils.setActiveAgentID((Intent)slTeleportResultEvent, this.userManager.getUserID());
            }
            ((Intent)slTeleportResultEvent).addFlags(335577088);
            this.getContext().startActivity((Intent)slTeleportResultEvent);
        }
    }
    
    public void onCancel(final DialogInterface dialogInterface) {
        if (this.userManager == null) {
            return;
        }
        try {
            final SLAgentCircuit activeAgentCircuit = this.userManager.getActiveAgentCircuit();
            if (activeAgentCircuit != null) {
                activeAgentCircuit.getModules().worldMap.CancelPendingTeleports();
            }
        }
        catch (final Exception ex) {
            Debug.Warning(ex);
        }
    }
    
    public void onStart() {
        super.onStart();
        if (this.userManager != null) {
            this.userManager.getEventBus().subscribe(this, null, this.mHandler);
        }
    }
    
    public void onStop() {
        if (this.userManager != null) {
            this.userManager.getEventBus().unsubscribe(this);
        }
        super.onStop();
    }
}
