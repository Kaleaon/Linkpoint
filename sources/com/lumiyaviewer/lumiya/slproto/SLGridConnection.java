package com.lumiyaviewer.lumiya.slproto;

import com.lumiyaviewer.lumiya.Debug;
import com.lumiyaviewer.lumiya.eventbus.EventBus;
import com.lumiyaviewer.lumiya.res.textures.TextureCache;
import com.lumiyaviewer.lumiya.slproto.auth.SLAuth;
import com.lumiyaviewer.lumiya.slproto.auth.SLAuthParams;
import com.lumiyaviewer.lumiya.slproto.auth.SLAuthReply;
import com.lumiyaviewer.lumiya.slproto.caps.SLCapEventQueue;
import com.lumiyaviewer.lumiya.slproto.caps.SLCaps;
import com.lumiyaviewer.lumiya.slproto.events.SLConnectionStateChangedEvent;
import com.lumiyaviewer.lumiya.slproto.events.SLDisconnectEvent;
import com.lumiyaviewer.lumiya.slproto.events.SLLoginResultEvent;
import com.lumiyaviewer.lumiya.slproto.modules.SLModules;
import com.lumiyaviewer.lumiya.slproto.modules.texfetcher.SLTextureFetcher;
import com.lumiyaviewer.lumiya.slproto.users.manager.UserManager;
import java.io.IOException;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.UUID;

public class SLGridConnection extends SLConnection {

    /* renamed from: -com-lumiyaviewer-lumiya-slproto-SLGridConnection$ConnectionStateSwitchesValues  reason: not valid java name */
    private static final /* synthetic */ int[] f62comlumiyaviewerlumiyaslprotoSLGridConnection$ConnectionStateSwitchesValues = null;
    private static final String DEFAULT_SYSTEM_ACCOUNT = "Second Life";
    private static boolean autoresponseEnabled = false;
    private static String autoresponseText = "";
    private UUID activeAgentUUID;
    private SLAgentCircuit agentCircuit;
    /* access modifiers changed from: private */
    public SLAuthParams authParams;
    public SLAuthReply authReply;
    public SLCapEventQueue capEventQueue;
    private ConnectionState connectionState = ConnectionState.Idle;
    private final EventBus eventBus = EventBus.getInstance();
    private volatile boolean firstConnect = true;
    private volatile boolean hadConnected = false;
    private volatile boolean isReconnecting = false;
    /* access modifiers changed from: private */
    public volatile Thread loginThread = null;
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

    /* renamed from: -getcom-lumiyaviewer-lumiya-slproto-SLGridConnection$ConnectionStateSwitchesValues  reason: not valid java name */
    private static /* synthetic */ int[] m130getcomlumiyaviewerlumiyaslprotoSLGridConnection$ConnectionStateSwitchesValues() {
        if (f62comlumiyaviewerlumiyaslprotoSLGridConnection$ConnectionStateSwitchesValues != null) {
            return f62comlumiyaviewerlumiyaslprotoSLGridConnection$ConnectionStateSwitchesValues;
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
        f62comlumiyaviewerlumiyaslprotoSLGridConnection$ConnectionStateSwitchesValues = iArr;
        return iArr;
    }

    /* access modifiers changed from: private */
    public void DoConnect(SLAuthParams sLAuthParams, String str) {
        try {
            SLAuthReply Login = new SLAuth().Login(sLAuthParams.withLocation(str));
            if (!Login.success) {
                setConnectionState(ConnectionState.Idle);
                reconnectOrDrop(true, false, Login.message);
                return;
            }
            synchronized (this) {
                if (this.connectionState != ConnectionState.Idle) {
                    this.authReply = Login;
                    this.activeAgentUUID = this.authReply.agentID;
                    this.userManager = UserManager.getUserManager(this.activeAgentUUID);
                    if (this.userManager != null) {
                        this.userManager.getChatterList().getFriendManager().updateFriendList(this.authReply.friends);
                    }
                    this.parcelInfo.reset(this.userManager);
                    startCircuit(Login, (SLTempCircuit) null);
                }
            }
        } catch (Exception e) {
            setConnectionState(ConnectionState.Idle);
            reconnectOrDrop(true, false, "Failed to connect to login server.");
        }
    }

    /* JADX WARNING: Code restructure failed: missing block: B:17:0x0048, code lost:
        return true;
     */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    private synchronized boolean Reconnect() {
        /*
            r4 = this;
            r2 = 0
            r3 = 1
            monitor-enter(r4)
            boolean r0 = r4.userWantsConnected     // Catch:{ all -> 0x004e }
            if (r0 == 0) goto L_0x0049
            boolean r0 = r4.hadConnected     // Catch:{ all -> 0x004e }
            if (r0 == 0) goto L_0x0049
            com.lumiyaviewer.lumiya.GlobalOptions r0 = com.lumiyaviewer.lumiya.GlobalOptions.getInstance()     // Catch:{ all -> 0x004e }
            boolean r0 = r0.getAutoReconnect()     // Catch:{ all -> 0x004e }
            if (r0 == 0) goto L_0x0049
            int r0 = r4.reconnectAttempts     // Catch:{ all -> 0x004e }
            com.lumiyaviewer.lumiya.GlobalOptions r1 = com.lumiyaviewer.lumiya.GlobalOptions.getInstance()     // Catch:{ all -> 0x004e }
            int r1 = r1.getMaxReconnectAttempts()     // Catch:{ all -> 0x004e }
            if (r0 >= r1) goto L_0x0049
            com.lumiyaviewer.lumiya.slproto.SLGridConnection$ConnectionState r0 = r4.connectionState     // Catch:{ all -> 0x004e }
            com.lumiyaviewer.lumiya.slproto.SLGridConnection$ConnectionState r1 = com.lumiyaviewer.lumiya.slproto.SLGridConnection.ConnectionState.Idle     // Catch:{ all -> 0x004e }
            if (r0 != r1) goto L_0x0047
            com.lumiyaviewer.lumiya.slproto.auth.SLAuthParams r0 = r4.authParams     // Catch:{ all -> 0x004e }
            if (r0 == 0) goto L_0x0047
            int r0 = r4.reconnectAttempts     // Catch:{ all -> 0x004e }
            int r0 = r0 + 1
            r4.reconnectAttempts = r0     // Catch:{ all -> 0x004e }
            r0 = 1
            r4.isReconnecting = r0     // Catch:{ all -> 0x004e }
            com.lumiyaviewer.lumiya.eventbus.EventBus r0 = r4.eventBus     // Catch:{ all -> 0x004e }
            com.lumiyaviewer.lumiya.slproto.events.SLReconnectingEvent r1 = new com.lumiyaviewer.lumiya.slproto.events.SLReconnectingEvent     // Catch:{ all -> 0x004e }
            int r2 = r4.reconnectAttempts     // Catch:{ all -> 0x004e }
            r1.<init>(r2)     // Catch:{ all -> 0x004e }
            r0.publish(r1)     // Catch:{ all -> 0x004e }
            java.lang.String r0 = "last"
            r1 = 1
            r4.startConnecting(r1, r0)     // Catch:{ all -> 0x004e }
        L_0x0047:
            monitor-exit(r4)
            return r3
        L_0x0049:
            r0 = 0
            r4.isReconnecting = r0     // Catch:{ all -> 0x004e }
            monitor-exit(r4)
            return r2
        L_0x004e:
            r0 = move-exception
            monitor-exit(r4)
            throw r0
        */
        throw new UnsupportedOperationException("Method not decompiled: com.lumiyaviewer.lumiya.slproto.SLGridConnection.Reconnect():boolean");
    }

    public static String getAutoresponse() {
        if (!autoresponseEnabled) {
            return null;
        }
        return autoresponseText;
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

    private void setConnectionState(ConnectionState connectionState2) {
        if (this.connectionState != connectionState2) {
            this.connectionState = connectionState2;
            this.eventBus.publish(new SLConnectionStateChangedEvent(connectionState2));
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
                this.capEventQueue = new SLCapEventQueue(sLCaps.getCapabilityOrThrow(SLCaps.SLCapability.EventQueueGet), this.agentCircuit);
            } catch (SLCaps.NoSuchCapabilityException e) {
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
                Thread unused = SLGridConnection.this.loginThread = null;
            }
        });
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
        startCircuit(sLAuthReply, this.tempCircuits.remove(this.authReply));
    }

    /* access modifiers changed from: package-private */
    public synchronized void addTempCircuit(SLAuthReply sLAuthReply) {
        if (!this.tempCircuits.containsKey(sLAuthReply)) {
            try {
                SLTempCircuit sLTempCircuit = new SLTempCircuit(this, new SLCircuitInfo(sLAuthReply), sLAuthReply);
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
        TextureCache.getInstance().setFetcher((SLTextureFetcher) null);
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
        switch (m130getcomlumiyaviewerlumiyaslprotoSLGridConnection$ConnectionStateSwitchesValues()[this.connectionState.ordinal()]) {
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
        this.eventBus.publish(new SLLoginResultEvent(true, (String) null, this.activeAgentUUID));
    }

    public synchronized void processDisconnect(boolean z, String str) {
        if (this.connectionState != ConnectionState.Idle) {
            closeConnectionObjects();
            reconnectOrDrop(false, z, str);
        }
    }

    /* access modifiers changed from: package-private */
    public synchronized void removeTempCircuit(SLTempCircuit sLTempCircuit) {
        Iterator<Map.Entry<SLAuthReply, SLTempCircuit>> it = this.tempCircuits.entrySet().iterator();
        while (it.hasNext()) {
            if (it.next().getValue() == sLTempCircuit) {
                it.remove();
            }
        }
        sLTempCircuit.CloseCircuit();
    }
}
