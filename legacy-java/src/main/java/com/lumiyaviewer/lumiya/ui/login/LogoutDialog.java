package com.lumiyaviewer.lumiya.ui.login;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import com.lumiyaviewer.lumiya.Debug;
import com.lumiyaviewer.lumiya.R;
import com.lumiyaviewer.lumiya.eventbus.EventBus;
import com.lumiyaviewer.lumiya.eventbus.EventHandler;
import com.lumiyaviewer.lumiya.slproto.SLAgentCircuit;
import com.lumiyaviewer.lumiya.slproto.SLGridConnection;
import com.lumiyaviewer.lumiya.slproto.events.SLDisconnectEvent;
import com.lumiyaviewer.lumiya.slproto.users.manager.UserManager;
import com.lumiyaviewer.lumiya.ui.common.ActivityUtils;
import com.lumiyaviewer.lumiya.utils.UUIDPool;
import java.util.UUID;

/* loaded from: classes.dex */
public class LogoutDialog extends ProgressDialog {
    private static final long DISCONNECT_TIMEOUT = 5000;
    private UUID agentUUID;
    private final EventBus eventBus;
    private final Handler handler;
    private final Runnable onDisconnectTimeout;

    public LogoutDialog(Context context) {
        super(context);
        this.handler = new Handler();
        this.eventBus = EventBus.getInstance();
        this.onDisconnectTimeout = new Runnable() { // from class: com.lumiyaviewer.lumiya.ui.login.-$Lambda$Ido4EAnXE9yUsM2nDeFKnyTfU7w
            private final /* synthetic */ void $m$0() {
                ((LogoutDialog) this).m647lambda$com_lumiyaviewer_lumiya_ui_login_LogoutDialog_3137();
            }

            @Override // java.lang.Runnable
            public final void run() {
                $m$0();
            }
        };
    }

    public LogoutDialog(Context context, int i) {
        super(context, i);
        this.handler = new Handler();
        this.eventBus = EventBus.getInstance();
        this.onDisconnectTimeout = new Runnable() { // from class: com.lumiyaviewer.lumiya.ui.login.-$Lambda$Ido4EAnXE9yUsM2nDeFKnyTfU7w.1
            private final /* synthetic */ void $m$0() {
                ((LogoutDialog) this).m647lambda$com_lumiyaviewer_lumiya_ui_login_LogoutDialog_3137();
            }

            @Override // java.lang.Runnable
            public final void run() {
                $m$0();
            }
        };
    }

    private UserManager getUserManager() {
        if (this.agentUUID != null) {
            return UserManager.getUserManager(this.agentUUID);
        }
        return null;
    }

    public static void show(Activity activity) {
        UUID activeAgentID = ActivityUtils.getActiveAgentID(activity.getIntent());
        if (activeAgentID != null) {
            LogoutDialog logoutDialog = new LogoutDialog(activity);
            logoutDialog.setAgentUUID(activeAgentID);
            logoutDialog.show();
        }
    }

    @EventHandler
    public void handleDisconnectEvent(SLDisconnectEvent sLDisconnectEvent) {
        Debug.Printf("LogoutDialog: disconnect event", new Object[0]);
        dismiss();
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    /* renamed from: lambda$-com_lumiyaviewer_lumiya_ui_login_LogoutDialog_3137, reason: not valid java name */
    public /* synthetic */ void m647lambda$com_lumiyaviewer_lumiya_ui_login_LogoutDialog_3137() {
        SLAgentCircuit activeAgentCircuit;
        SLGridConnection gridConnection;
        boolean z = true;
        UserManager userManager = getUserManager();
        if (userManager == null || (activeAgentCircuit = userManager.getActiveAgentCircuit()) == null || (gridConnection = activeAgentCircuit.getGridConnection()) == null) {
            z = false;
        } else {
            gridConnection.forceDisconnect(true);
        }
        if (z) {
            return;
        }
        dismiss();
    }

    @Override // android.app.ProgressDialog, android.app.AlertDialog, android.app.Dialog
    protected void onCreate(Bundle bundle) {
        super.onCreate(bundle);
        setProgressStyle(0);
        setMessage(getContext().getString(R.string.logging_out));
        if (bundle != null) {
            this.agentUUID = UUIDPool.getUUID(bundle.getString("agentUUID"));
        }
    }

    @Override // android.app.Dialog
    public Bundle onSaveInstanceState() {
        Bundle onSaveInstanceState = super.onSaveInstanceState();
        if (onSaveInstanceState == null) {
            onSaveInstanceState = new Bundle();
        }
        if (this.agentUUID != null) {
            onSaveInstanceState.putString("agentUUID", this.agentUUID.toString());
        }
        return onSaveInstanceState;
    }

    @Override // android.app.ProgressDialog, android.app.Dialog
    public void onStart() {
        SLAgentCircuit activeAgentCircuit;
        SLGridConnection gridConnection;
        boolean z = false;
        super.onStart();
        this.eventBus.subscribe(this, null, this.handler);
        UserManager userManager = getUserManager();
        if (userManager != null && (activeAgentCircuit = userManager.getActiveAgentCircuit()) != null && (gridConnection = activeAgentCircuit.getGridConnection()) != null) {
            Debug.Printf("LogoutDialog: connection is not null", new Object[0]);
            this.handler.postDelayed(this.onDisconnectTimeout, DISCONNECT_TIMEOUT);
            gridConnection.Disconnect();
            z = true;
        }
        if (z) {
            return;
        }
        dismiss();
        EventBus.getInstance().publish(new SLDisconnectEvent(true, "Disconnected"));
    }

    @Override // android.app.ProgressDialog, android.app.Dialog
    protected void onStop() {
        this.handler.removeCallbacks(this.onDisconnectTimeout);
        this.eventBus.unsubscribe(this);
        super.onStop();
    }

    public void setAgentUUID(UUID uuid) {
        this.agentUUID = uuid;
    }
}
