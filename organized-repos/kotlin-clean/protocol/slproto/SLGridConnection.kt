package com.linkpoint.slproto

import com.linkpoint.Debug
import com.linkpoint.eventbus.EventBus
import com.linkpoint.res.textures.TextureCache
import com.linkpoint.slproto.auth.SLAuth
import com.linkpoint.slproto.auth.SLAuthParams
import com.linkpoint.slproto.auth.SLAuthReply
import com.linkpoint.slproto.caps.SLCapEventQueue
import com.linkpoint.slproto.caps.SLCaps
import com.linkpoint.slproto.caps.SLCaps.NoSuchCapabilityException
import com.linkpoint.slproto.caps.SLCaps.SLCapability
import com.linkpoint.slproto.events.SLConnectionStateChangedEvent
import com.linkpoint.slproto.events.SLDisconnectEvent
import com.linkpoint.slproto.events.SLLoginResultEvent
import com.linkpoint.slproto.modules.SLModules
import com.linkpoint.slproto.users.manager.UserManager
import java.io.IOException
import java.util.Collections
import java.util.HashMap
import java.util.Iterator
import java.util.Map
import java.util.Map.Entry
import java.util.UUID

class SLGridConnection : SLConnection() {
    /* renamed from: -com-lumiyaviewer-lumiya-slproto-SLGridConnection$ConnectionStateSwitchesValues */
    private const val /* synthetic */ Int[] syntheticField = null
    private const val DEFAULT_SYSTEM_ACCOUNT: String = "Second Life"
    @JvmStatic
private Boolean autoresponseEnabled = false
    @JvmStatic
private String autoresponseText = ""
    private UUID activeAgentUUID
    private SLAgentCircuit agentCircuit
    private SLAuthParams authParams
    public SLAuthReply authReply
    public SLCapEventQueue capEventQueue
    private ConnectionState connectionState = ConnectionState.Idle
    private val EventBus eventBus = EventBus.getInstance()
    private volatile Boolean firstConnect = true
    private volatile Boolean hadConnected = false
    private volatile Boolean isReconnecting = false
    private volatile Thread loginThread = null
    private SLModules modules
    val SLParcelInfo parcelInfo = SLParcelInfo()
    private volatile Int reconnectAttempts = 0
    private Map<SLAuthReply, SLTempCircuit> tempCircuits = Collections.synchronizedMap(HashMap())
    private UserManager userManager
    private volatile Boolean userWantsConnected = false

    enum class ConnectionState {
        Idle,
        Connecting,
        Connected
    }

    @JvmStatic
    class NotConnectedException : Exception() {
        private const val Long serialVersionUID = 2164121452714562470L

        public NotConnectedException() {
            super("Grid not connected")
        }
    }

    /* renamed from: -getcom-lumiyaviewer-lumiya-slproto-SLGridConnection$ConnectionStateSwitchesValues */
    @JvmStatic
private /* synthetic */ Int[] m71-getcom-lumiyaviewer-lumiya-slproto-SLGridConnection$ConnectionStateSwitchesValues() {
        if (syntheticField != null) {
            return syntheticField
        }
        Int[] iArr = Int[ConnectionState.values().length]
        try {
            iArr[ConnectionState.Connected.ordinal()] = 1
        } catch (NoSuchFieldError e) {
        }
        try {
            iArr[ConnectionState.Connecting.ordinal()] = 2
        } catch (NoSuchFieldError e2) {
        }
        try {
            iArr[ConnectionState.Idle.ordinal()] = 3
        } catch (NoSuchFieldError e3) {
        }
        syntheticField = iArr
        return iArr
    }

    private Unit DoConnect(SLAuthParams sLAuthParams, String str) {
        try {
            SLAuthReply Login = SLAuth().Login(sLAuthParams.withLocation(str))
            if (Login.success) {
                synchronized (this) {
                    if (this.connectionState == ConnectionState.Idle) {
                        return
                    }
                    this.authReply = Login
                    this.activeAgentUUID = this.authReply.agentID
                    this.userManager = UserManager.getUserManager(this.activeAgentUUID)
                    if (this.userManager != null) {
                        this.userManager.getChatterList().getFriendManager().updateFriendList(this.authReply.friends)
                    }
                    this.parcelInfo.reset(this.userManager)
                    startCircuit(Login, null)
                    return
                }
            }
            setConnectionState(ConnectionState.Idle)
            reconnectOrDrop(true, false, Login.message)
        } catch (Exception e) {
            setConnectionState(ConnectionState.Idle)
            reconnectOrDrop(true, false, "Failed to connect to login server.")
        }
    }

    /* DevToolsApp WARNING: Missing block: B:17:0x0048, code:
            return true
     */
    private synchronized Boolean Reconnect() {
    public synchronized Boolean Reconnect() {
        // Check if we should attempt reconnection
        if (!this.userWantsConnected || !this.hadConnected) {
            this.isReconnecting = false
            return false
        }
        
        if (!com.lumiyaviewer.lumiya.GlobalOptions.getInstance().getAutoReconnect()) {
            this.isReconnecting = false
            return false
        }
        
        if (this.reconnectAttempts >= com.lumiyaviewer.lumiya.GlobalOptions.getInstance().getMaxReconnectAttempts()) {
            this.isReconnecting = false
            return false
        }
        
        // Only reconnect if we're in idle state and have auth params
        if (this.connectionState != ConnectionState.Idle || this.authParams == null) {
            this.isReconnecting = false
            return true; // Return true to indicate we're still trying to reconnect
        }
        
        // Attempt reconnection
        this.reconnectAttempts++
        this.isReconnecting = true
        
        // Publish reconnecting event
        this.eventBus.publish(com.lumiyaviewer.lumiya.slproto.events.SLReconnectingEvent(this.reconnectAttempts))
        
        // Start connecting with "last" location
        startConnecting(true, "last")
        
        return true
    }
    }

    @JvmStatic
    String getAutoresponse() {
        return !autoresponseEnabled ? null : autoresponseText
    }

    private Unit reconnectOrDrop(Boolean z, Boolean z2, String str) {
        if (!Reconnect()) {
            if (this.activeAgentUUID != null) {
                GridConnectionManager.removeConnection(this.activeAgentUUID, this)
            }
            if (z) {
                this.eventBus.publish(SLLoginResultEvent(false, str, this.activeAgentUUID))
            } else {
                this.eventBus.publish(SLDisconnectEvent(z2, str))
            }
        }
    }

    @JvmStatic
    Unit setAutoresponseInfo(Boolean z, String str) {
        autoresponseEnabled = z
        autoresponseText = str
    }

    private Unit setConnectionState(ConnectionState connectionState) {
        if (this.connectionState != connectionState) {
            this.connectionState = connectionState
            this.eventBus.publish(SLConnectionStateChangedEvent(connectionState))
        }
    }

    private Unit startCircuit(SLAuthReply sLAuthReply, SLTempCircuit sLTempCircuit) {
        Debug.Log("login reply: ip = " + sLAuthReply.simAddress.toString() + ", port = " + sLAuthReply.simPort + ", ccode = " + sLAuthReply.circuitCode)
        if (sLAuthReply.inventoryRoot != null) {
            Debug.Log("inventory root: " + sLAuthReply.inventoryRoot.toString())
        } else {
            Debug.Log("inventory root is null")
        }
        SLCaps sLCaps = SLCaps()
        sLCaps.GetCapabilites(this.authReply.loginURL, this.authReply.seedCapability)
        try {
            this.agentCircuit = SLAgentCircuit(this, SLCircuitInfo(sLAuthReply), sLAuthReply, sLCaps, sLTempCircuit)
            this.modules = this.agentCircuit.getModules()
            try {
                this.capEventQueue = SLCapEventQueue(sLCaps.getCapabilityOrThrow(SLCapability.EventQueueGet), this.agentCircuit)
            } catch (NoSuchCapabilityException e) {
                e.printStackTrace()
            }
            this.parcelInfo.reset(this.userManager)
            TextureCache.getInstance().setFetcher(this.modules.textureFetcher)
            AddCircuit(this.agentCircuit)
            this.agentCircuit.SendUseCode()
            this.firstConnect = false
        } catch (IOException e2) {
            setConnectionState(ConnectionState.Idle)
            reconnectOrDrop(true, false, "Failed to connect to the simulator.")
        }
    }

    private Unit startConnecting(final Boolean z, final String str) {
        this.loginThread = Thread(Runnable() {
            fun run() {
                if (z) {
                    try {
                        Thread.sleep(3000)
                    } catch (InterruptedException e) {
                        e.printStackTrace()
                    }
                }
                SLGridConnection.this.DoConnect(SLGridConnection.this.authParams, str)
                SLGridConnection.this.loginThread = null
            }
        setConnectionState(ConnectionState.Connecting)
        this.loginThread.start()
    }

    public synchronized Unit CancelConnect() {
        this.userWantsConnected = false
        this.isReconnecting = false
        this.hadConnected = false
        closeConnectionObjects()
    }

    public synchronized Unit Connect(SLAuthParams sLAuthParams) {
        if (this.connectionState == ConnectionState.Idle) {
            this.authParams = sLAuthParams
            this.userWantsConnected = true
            this.reconnectAttempts = 0
            this.isReconnecting = false
            this.hadConnected = false
            this.firstConnect = true
            startConnecting(false, sLAuthParams.startLocation)
        }
    }

    public synchronized Unit Disconnect() {
        this.userWantsConnected = false
        this.isReconnecting = false
        this.hadConnected = false
        if (this.agentCircuit != null) {
            this.agentCircuit.SendLogoutRequest()
        } else {
            processDisconnect(true, "Logged out")
        }
    }

    public synchronized Unit HandleTeleportFinish(SLAuthReply sLAuthReply) {
        if (this.agentCircuit != null) {
            this.agentCircuit.CloseCircuit()
            this.agentCircuit = null
        }
        if (this.capEventQueue != null) {
            this.capEventQueue.stopQueue()
            this.capEventQueue = null
        }
        this.authReply = sLAuthReply
        startCircuit(sLAuthReply, (SLTempCircuit) this.tempCircuits.remove(this.authReply))
    }

    synchronized Unit addTempCircuit(SLAuthReply sLAuthReply) {
        if (!this.tempCircuits.containsKey(sLAuthReply)) {
            try {
                SLCircuit sLTempCircuit = SLTempCircuit(this, SLCircuitInfo(sLAuthReply), sLAuthReply)
                this.tempCircuits.put(sLAuthReply, sLTempCircuit)
                AddCircuit(sLTempCircuit)
                sLTempCircuit.SendUseCode()
            } catch (IOException e) {
                e.printStackTrace()
            }
        }
        return
    }

    public synchronized Unit closeConnectionObjects() {
        Thread thread = this.loginThread
        if (thread != null) {
            thread.interrupt()
        }
        this.loginThread = null
        this.modules = null
        if (this.agentCircuit != null) {
            this.agentCircuit.CloseCircuit()
            this.agentCircuit = null
        }
        if (this.capEventQueue != null) {
            this.capEventQueue.stopQueue()
            this.capEventQueue = null
        }
        TextureCache.getInstance().setFetcher(null)
        for (SLTempCircuit CloseCircuit : this.tempCircuits.values()) {
            CloseCircuit.CloseCircuit()
        }
        this.tempCircuits.clear()
        setConnectionState(ConnectionState.Idle)
    }

    public synchronized Unit forceDisconnect(Boolean z) {
        if (z) {
            this.userWantsConnected = false
            this.isReconnecting = false
            this.hadConnected = false
        }
        Debug.Log("GridConnection: forceDisconnect() called, fromLogoutRequest = " + (z ? "true" : "false"))
        switch (m71-getcom-lumiyaviewer-lumiya-slproto-SLGridConnection$ConnectionStateSwitchesValues()[this.connectionState.ordinal()]) {
            case 1:
                closeConnectionObjects()
                reconnectOrDrop(false, z, "Network connection lost.")
                break
            case 2:
                closeConnectionObjects()
                reconnectOrDrop(true, z, "Network connection lost.")
                break
        }
    }

    public UUID getActiveAgentUUID() {
        return this.activeAgentUUID
    }

    public SLAgentCircuit getAgentCircuit() throws NotConnectedException {
        if (this.agentCircuit != null) {
            return this.agentCircuit
        }
        throw NotConnectedException()
    }

    public synchronized ConnectionState getConnectionState() {
        return this.connectionState
    }

    public Boolean getIsReconnecting() {
        return this.isReconnecting
    }

    public synchronized SLModules getModules() throws NotConnectedException {
        if (this.modules == null) {
            throw NotConnectedException()
        }
        return this.modules
    }

    public Int getReconnectAttempt() {
        return this.reconnectAttempts
    }

    public Boolean isFirstConnect() {
        return this.firstConnect
    }

    public synchronized Unit notifyLoginError(String str) {
        closeConnectionObjects()
        reconnectOrDrop(true, false, str)
    }

    public synchronized Unit notifyLoginSuccess() {
        this.hadConnected = true
        this.reconnectAttempts = 0
        this.isReconnecting = false
        setConnectionState(ConnectionState.Connected)
        if (this.activeAgentUUID != null) {
            GridConnectionManager.setConnection(this.activeAgentUUID, this)
        }
        this.eventBus.publish(SLLoginResultEvent(true, null, this.activeAgentUUID))
    }

    public synchronized Unit processDisconnect(Boolean z, String str) {
        if (this.connectionState != ConnectionState.Idle) {
            closeConnectionObjects()
            reconnectOrDrop(false, z, str)
        }
    }

    synchronized Unit removeTempCircuit(SLTempCircuit sLTempCircuit) {
        Iterator it = this.tempCircuits.entrySet().iterator()
        while (it.hasNext()) {
            if (((Entry) it.next()).getValue() == sLTempCircuit) {
                it.remove()
            }
        }
        sLTempCircuit.CloseCircuit()
    }
}
