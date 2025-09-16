package com.lumiyaviewer.lumiya.slproto;

import com.lumiyaviewer.lumiya.Debug;
import com.lumiyaviewer.lumiya.eventbus.EventBus;
import com.lumiyaviewer.lumiya.res.textures.TextureCache;
import com.lumiyaviewer.lumiya.slproto.auth.SLAuth;
import com.lumiyaviewer.lumiya.slproto.auth.SLAuthParams;
import com.lumiyaviewer.lumiya.slproto.auth.SLAuthReply;
import com.lumiyaviewer.lumiya.slproto.caps.SLCapEventQueue;
import com.lumiyaviewer.lumiya.slproto.caps.SLCaps;
import com.lumiyaviewer.lumiya.slproto.caps.SLCaps.NoSuchCapabilityException;
import com.lumiyaviewer.lumiya.slproto.caps.SLCaps.SLCapability;
import com.lumiyaviewer.lumiya.slproto.events.SLConnectionStateChangedEvent;
import com.lumiyaviewer.lumiya.slproto.events.SLDisconnectEvent;
import com.lumiyaviewer.lumiya.slproto.events.SLLoginResultEvent;
import com.lumiyaviewer.lumiya.slproto.modules.SLModules;
import com.lumiyaviewer.lumiya.slproto.users.manager.UserManager;
import java.io.IOException;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;
import java.util.UUID;

public class SLGridConnection extends SLConnection {
    /* renamed from: -com-lumiyaviewer-lumiya-slproto-SLGridConnection$ConnectionStateSwitchesValues */
    private static final /* synthetic */ int[] syntheticField = null;
    private static final String DEFAULT_SYSTEM_ACCOUNT = "Second Life";
    private static boolean autoresponseEnabled = false;
    private static String autoresponseText = "";
    private UUID activeAgentUUID;
    private SLAgentCircuit agentCircuit;
    private SLAuthParams authParams;
    public SLAuthReply authReply;
    public SLCapEventQueue capEventQueue;
    private ConnectionState connectionState = ConnectionState.Idle;
    private final EventBus eventBus = EventBus.getInstance();
    private volatile boolean firstConnect = true;
    private volatile boolean hadConnected = false;
    private volatile boolean isReconnecting = false;
    private volatile Thread loginThread = null;
    private SLModules modules;
    public final SLParcelInfo parcelInfo = new SLParcelInfo();
    private volatile int reconnectAttempts = 0;
    private Map<SLAuthReply, SLTempCircuit> tempCircuits = Collections.synchronizedMap(new HashMap());
    private UserManager userManager;
    private volatile boolean userWantsConnected = false;

    public enum ConnectionState {
        Idle,
        Connecting,
        Connected
    }

    public static class NotConnectedException extends Exception {
        private static final long serialVersionUID = 2164121452714562470L;

        public NotConnectedException() {
            super("Grid not connected");
        }
    }

    /* renamed from: -getcom-lumiyaviewer-lumiya-slproto-SLGridConnection$ConnectionStateSwitchesValues */
    private static /* synthetic */ int[] m71-getcom-lumiyaviewer-lumiya-slproto-SLGridConnection$ConnectionStateSwitchesValues() {
        if (syntheticField != null) {
            return syntheticField;
        }
        int[] iArr = new int[ConnectionState.values().length];
        try {
            iArr[ConnectionState.Connected.ordinal()] = 1;
        } catch (NoSuchFieldError e) {
        }
        try {
            iArr[ConnectionState.Connecting.ordinal()] = 2;
        } catch (NoSuchFieldError e2) {
        }
        try {
            iArr[ConnectionState.Idle.ordinal()] = 3;
        } catch (NoSuchFieldError e3) {
        }
        syntheticField = iArr;
        return iArr;
    }

    private void DoConnect(SLAuthParams sLAuthParams, String str) {
        try {
            SLAuthReply Login = new SLAuth().Login(sLAuthParams.withLocation(str));
            if (Login.success) {
                synchronized (this) {
                    if (this.connectionState == ConnectionState.Idle) {
                        return;
                    }
                    this.authReply = Login;
                    this.activeAgentUUID = this.authReply.agentID;
                    this.userManager = UserManager.getUserManager(this.activeAgentUUID);
                    if (this.userManager != null) {
                        this.userManager.getChatterList().getFriendManager().updateFriendList(this.authReply.friends);
                    }
                    this.parcelInfo.reset(this.userManager);
                    startCircuit(Login, null);
                    return;
                }
            }
            setConnectionState(ConnectionState.Idle);
            reconnectOrDrop(true, false, Login.message);
        } catch (Exception e) {
            setConnectionState(ConnectionState.Idle);
            reconnectOrDrop(true, false, "Failed to connect to login server.");
        }
    }

    /* DevToolsApp WARNING: Missing block: B:17:0x0048, code:
            return true;
     */
    private synchronized boolean Reconnect() {
    public synchronized boolean Reconnect() {
        // Check if we should attempt reconnection
        if (!this.userWantsConnected || !this.hadConnected) {
            this.isReconnecting = false;
            return false;
        }
        
        if (!com.lumiyaviewer.lumiya.GlobalOptions.getInstance().getAutoReconnect()) {
            this.isReconnecting = false;
            return false;
        }
        
        if (this.reconnectAttempts >= com.lumiyaviewer.lumiya.GlobalOptions.getInstance().getMaxReconnectAttempts()) {
            this.isReconnecting = false;
            return false;
        }
        
        // Only reconnect if we're in idle state and have auth params
        if (this.connectionState != ConnectionState.Idle || this.authParams == null) {
            this.isReconnecting = false;
            return true; // Return true to indicate we're still trying to reconnect
        }
        
        // Attempt reconnection
        this.reconnectAttempts++;
        this.isReconnecting = true;
        
        // Publish reconnecting event
        this.eventBus.publish(new com.lumiyaviewer.lumiya.slproto.events.SLReconnectingEvent(this.reconnectAttempts));
        
        // Start connecting with "last" location
        startConnecting(true, "last");
        
        return true;
    }
    }

    public static String getAutoresponse() {
        return !autoresponseEnabled ? null : autoresponseText;
    }

    private void reconnectOrDrop(boolean z, boolean z2, String str) {
        if (!Reconnect()) {
            if (this.activeAgentUUID != null) {
                GridConnectionManager.removeConnection(this.activeAgentUUID, this);
            }
            if (z) {
                this.eventBus.publish(new SLLoginResultEvent(false, str, this.activeAgentUUID));
            } else {
                this.eventBus.publish(new SLDisconnectEvent(z2, str));
            }
        }
    }

    public static void setAutoresponseInfo(boolean z, String str) {
        autoresponseEnabled = z;
        autoresponseText = str;
    }

    private void setConnectionState(ConnectionState connectionState) {
        if (this.connectionState != connectionState) {
            this.connectionState = connectionState;
            this.eventBus.publish(new SLConnectionStateChangedEvent(connectionState));
        }
    }

    private void startCircuit(SLAuthReply sLAuthReply, SLTempCircuit sLTempCircuit) {
        Debug.Log("login reply: ip = " + sLAuthReply.simAddress.toString() + ", port = " + sLAuthReply.simPort + ", ccode = " + sLAuthReply.circuitCode);
        if (sLAuthReply.inventoryRoot != null) {
            Debug.Log("inventory root: " + sLAuthReply.inventoryRoot.toString());
        } else {
            Debug.Log("inventory root is null");
        }
        SLCaps sLCaps = new SLCaps();
        sLCaps.GetCapabilites(this.authReply.loginURL, this.authReply.seedCapability);
        try {
            this.agentCircuit = new SLAgentCircuit(this, new SLCircuitInfo(sLAuthReply), sLAuthReply, sLCaps, sLTempCircuit);
            this.modules = this.agentCircuit.getModules();
            try {
                this.capEventQueue = new SLCapEventQueue(sLCaps.getCapabilityOrThrow(SLCapability.EventQueueGet), this.agentCircuit);
            } catch (NoSuchCapabilityException e) {
                e.printStackTrace();
            }
            this.parcelInfo.reset(this.userManager);
            TextureCache.getInstance().setFetcher(this.modules.textureFetcher);
            AddCircuit(this.agentCircuit);
            this.agentCircuit.SendUseCode();
            this.firstConnect = false;
        } catch (IOException e2) {
            setConnectionState(ConnectionState.Idle);
            reconnectOrDrop(true, false, "Failed to connect to the simulator.");
        }
    }

    private void startConnecting(final boolean z, final String str) {
        this.loginThread = new Thread(new Runnable() {
            public void run() {
                if (z) {
                    try {
                        Thread.sleep(3000);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
                SLGridConnection.this.DoConnect(SLGridConnection.this.authParams, str);
                SLGridConnection.this.loginThread = null;
            }
        setConnectionState(ConnectionState.Connecting);
        this.loginThread.start();
    }

    public synchronized void CancelConnect() {
        this.userWantsConnected = false;
        this.isReconnecting = false;
        this.hadConnected = false;
        closeConnectionObjects();
    }

    public synchronized void Connect(SLAuthParams sLAuthParams) {
        if (this.connectionState == ConnectionState.Idle) {
            this.authParams = sLAuthParams;
            this.userWantsConnected = true;
            this.reconnectAttempts = 0;
            this.isReconnecting = false;
            this.hadConnected = false;
            this.firstConnect = true;
            startConnecting(false, sLAuthParams.startLocation);
        }
    }

    public synchronized void Disconnect() {
        this.userWantsConnected = false;
        this.isReconnecting = false;
        this.hadConnected = false;
        if (this.agentCircuit != null) {
            this.agentCircuit.SendLogoutRequest();
        } else {
            processDisconnect(true, "Logged out");
        }
    }

    public synchronized void HandleTeleportFinish(SLAuthReply sLAuthReply) {
        if (this.agentCircuit != null) {
            this.agentCircuit.CloseCircuit();
            this.agentCircuit = null;
        }
        if (this.capEventQueue != null) {
            this.capEventQueue.stopQueue();
            this.capEventQueue = null;
        }
        this.authReply = sLAuthReply;
        startCircuit(sLAuthReply, (SLTempCircuit) this.tempCircuits.remove(this.authReply));
    }

    synchronized void addTempCircuit(SLAuthReply sLAuthReply) {
        if (!this.tempCircuits.containsKey(sLAuthReply)) {
            try {
                SLCircuit sLTempCircuit = new SLTempCircuit(this, new SLCircuitInfo(sLAuthReply), sLAuthReply);
                this.tempCircuits.put(sLAuthReply, sLTempCircuit);
                AddCircuit(sLTempCircuit);
                sLTempCircuit.SendUseCode();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return;
    }

    public synchronized void closeConnectionObjects() {
        Thread thread = this.loginThread;
        if (thread != null) {
            thread.interrupt();
        }
        this.loginThread = null;
        this.modules = null;
        if (this.agentCircuit != null) {
            this.agentCircuit.CloseCircuit();
            this.agentCircuit = null;
        }
        if (this.capEventQueue != null) {
            this.capEventQueue.stopQueue();
            this.capEventQueue = null;
        }
        TextureCache.getInstance().setFetcher(null);
        for (SLTempCircuit CloseCircuit : this.tempCircuits.values()) {
            CloseCircuit.CloseCircuit();
        }
        this.tempCircuits.clear();
        setConnectionState(ConnectionState.Idle);
    }

    public synchronized void forceDisconnect(boolean z) {
        if (z) {
            this.userWantsConnected = false;
            this.isReconnecting = false;
            this.hadConnected = false;
        }
        Debug.Log("GridConnection: forceDisconnect() called, fromLogoutRequest = " + (z ? "true" : "false"));
        switch (m71-getcom-lumiyaviewer-lumiya-slproto-SLGridConnection$ConnectionStateSwitchesValues()[this.connectionState.ordinal()]) {
            case 1:
                closeConnectionObjects();
                reconnectOrDrop(false, z, "Network connection lost.");
                break;
            case 2:
                closeConnectionObjects();
                reconnectOrDrop(true, z, "Network connection lost.");
                break;
        }
    }

    public UUID getActiveAgentUUID() {
        return this.activeAgentUUID;
    }

    public SLAgentCircuit getAgentCircuit() throws NotConnectedException {
        if (this.agentCircuit != null) {
            return this.agentCircuit;
        }
        throw new NotConnectedException();
    }

    public synchronized ConnectionState getConnectionState() {
        return this.connectionState;
    }

    public boolean getIsReconnecting() {
        return this.isReconnecting;
    }

    public synchronized SLModules getModules() throws NotConnectedException {
        if (this.modules == null) {
            throw new NotConnectedException();
        }
        return this.modules;
    }

    public int getReconnectAttempt() {
        return this.reconnectAttempts;
    }

    public boolean isFirstConnect() {
        return this.firstConnect;
    }

    public synchronized void notifyLoginError(String str) {
        closeConnectionObjects();
        reconnectOrDrop(true, false, str);
    }

    public synchronized void notifyLoginSuccess() {
        this.hadConnected = true;
        this.reconnectAttempts = 0;
        this.isReconnecting = false;
        setConnectionState(ConnectionState.Connected);
        if (this.activeAgentUUID != null) {
            GridConnectionManager.setConnection(this.activeAgentUUID, this);
        }
        this.eventBus.publish(new SLLoginResultEvent(true, null, this.activeAgentUUID));
    }

    public synchronized void processDisconnect(boolean z, String str) {
        if (this.connectionState != ConnectionState.Idle) {
            closeConnectionObjects();
            reconnectOrDrop(false, z, str);
        }
    }

    synchronized void removeTempCircuit(SLTempCircuit sLTempCircuit) {
        Iterator it = this.tempCircuits.entrySet().iterator();
        while (it.hasNext()) {
            if (((Entry) it.next()).getValue() == sLTempCircuit) {
                it.remove();
            }
        }
        sLTempCircuit.CloseCircuit();
    }
}
