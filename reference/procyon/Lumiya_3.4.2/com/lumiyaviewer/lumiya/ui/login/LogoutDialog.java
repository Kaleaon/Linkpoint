// 
// Decompiled by Procyon v0.6.0
// 

package com.lumiyaviewer.lumiya.ui.login;

import com.lumiyaviewer.lumiya.utils.UUIDPool;
import android.os.Bundle;
import com.lumiyaviewer.lumiya.slproto.SLGridConnection;
import com.lumiyaviewer.lumiya.slproto.SLAgentCircuit;
import com.lumiyaviewer.lumiya.eventbus.EventHandler;
import com.lumiyaviewer.lumiya.Debug;
import com.lumiyaviewer.lumiya.slproto.events.SLDisconnectEvent;
import com.lumiyaviewer.lumiya.ui.common.ActivityUtils;
import android.app.Activity;
import com.lumiyaviewer.lumiya.slproto.users.manager.UserManager;
import android.content.Context;
import android.os.Handler;
import com.lumiyaviewer.lumiya.eventbus.EventBus;
import java.util.UUID;
import android.app.ProgressDialog;

public class LogoutDialog extends ProgressDialog
{
    private static final long DISCONNECT_TIMEOUT = 5000L;
    private UUID agentUUID;
    private final EventBus eventBus;
    private final Handler handler;
    private final Runnable onDisconnectTimeout;
    
    public LogoutDialog(final Context context) {
        super(context);
        this.handler = new Handler();
        this.eventBus = EventBus.getInstance();
        this.onDisconnectTimeout = new _$Lambda$Ido4EAnXE9yUsM2nDeFKnyTfU7w(this);
    }
    
    public LogoutDialog(final Context context, final int n) {
        super(context, n);
        this.handler = new Handler();
        this.eventBus = EventBus.getInstance();
        this.onDisconnectTimeout = new _$Lambda$Ido4EAnXE9yUsM2nDeFKnyTfU7w$1(this);
    }
    
    private UserManager getUserManager() {
        if (this.agentUUID != null) {
            return UserManager.getUserManager(this.agentUUID);
        }
        return null;
    }
    
    public static void show(final Activity activity) {
        final UUID activeAgentID = ActivityUtils.getActiveAgentID(activity.getIntent());
        if (activeAgentID != null) {
            final LogoutDialog logoutDialog = new LogoutDialog((Context)activity);
            logoutDialog.setAgentUUID(activeAgentID);
            logoutDialog.show();
        }
    }
    
    @EventHandler
    public void handleDisconnectEvent(final SLDisconnectEvent slDisconnectEvent) {
        Debug.Printf("LogoutDialog: disconnect event", new Object[0]);
        this.dismiss();
    }
    
    protected void onCreate(final Bundle bundle) {
        super.onCreate(bundle);
        this.setProgressStyle(0);
        this.setMessage((CharSequence)this.getContext().getString(2131296659));
        if (bundle != null) {
            this.agentUUID = UUIDPool.getUUID(bundle.getString("agentUUID"));
        }
    }
    
    public Bundle onSaveInstanceState() {
        Bundle onSaveInstanceState;
        if ((onSaveInstanceState = super.onSaveInstanceState()) == null) {
            onSaveInstanceState = new Bundle();
        }
        if (this.agentUUID != null) {
            onSaveInstanceState.putString("agentUUID", this.agentUUID.toString());
        }
        return onSaveInstanceState;
    }
    
    public void onStart() {
        final boolean b = false;
        super.onStart();
        this.eventBus.subscribe(this, null, this.handler);
        final UserManager userManager = this.getUserManager();
        int n = b ? 1 : 0;
        if (userManager != null) {
            final SLAgentCircuit activeAgentCircuit = userManager.getActiveAgentCircuit();
            n = (b ? 1 : 0);
            if (activeAgentCircuit != null) {
                final SLGridConnection gridConnection = activeAgentCircuit.getGridConnection();
                n = (b ? 1 : 0);
                if (gridConnection != null) {
                    Debug.Printf("LogoutDialog: connection is not null", new Object[0]);
                    this.handler.postDelayed(this.onDisconnectTimeout, 5000L);
                    gridConnection.Disconnect();
                    n = 1;
                }
            }
        }
        if (n == 0) {
            this.dismiss();
            EventBus.getInstance().publish(new SLDisconnectEvent(true, "Disconnected"));
        }
    }
    
    protected void onStop() {
        this.handler.removeCallbacks(this.onDisconnectTimeout);
        this.eventBus.unsubscribe(this);
        super.onStop();
    }
    
    public void setAgentUUID(final UUID agentUUID) {
        this.agentUUID = agentUUID;
    }
}
