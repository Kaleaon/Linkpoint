package com.lumiyaviewer.lumiya.slproto

import com.lumiyaviewer.lumiya.Debug
import com.lumiyaviewer.lumiya.GlobalOptions
import com.lumiyaviewer.lumiya.eventbus.EventBus
import com.lumiyaviewer.lumiya.res.textures.TextureCache
import com.lumiyaviewer.lumiya.slproto.auth.SLAuth
import com.lumiyaviewer.lumiya.slproto.auth.SLAuthParams
import com.lumiyaviewer.lumiya.slproto.auth.SLAuthReply
import com.lumiyaviewer.lumiya.slproto.caps.SLCapEventQueue
import com.lumiyaviewer.lumiya.slproto.caps.SLCaps
import com.lumiyaviewer.lumiya.slproto.caps.SLCaps.NoSuchCapabilityException
import com.lumiyaviewer.lumiya.slproto.caps.SLCaps.SLCapability
import com.lumiyaviewer.lumiya.slproto.events.SLConnectionStateChangedEvent
import com.lumiyaviewer.lumiya.slproto.events.SLDisconnectEvent
import com.lumiyaviewer.lumiya.slproto.events.SLLoginResultEvent
import com.lumiyaviewer.lumiya.slproto.events.SLReconnectingEvent
import com.lumiyaviewer.lumiya.slproto.modules.SLModules
import com.lumiyaviewer.lumiya.slproto.users.manager.UserManager
import java.io.IOException
import java.util.*

/**
 * Grid connection manager for Second Life protocol
 * Handles authentication, circuit management, and reconnection logic
 */
class SLGridConnection : SLConnection() {
    
    private val DEFAULT_SYSTEM_ACCOUNT = "Second Life"
    private var autoresponseEnabled = false
    private var autoresponseText = ""
    
    private var activeAgentUUID: UUID? = null
    private var agentCircuit: SLAgentCircuit? = null
    private var authParams: SLAuthParams? = null
    internal var authReply: SLAuthReply? = null
    internal var capEventQueue: SLCapEventQueue? = null
    private var connectionState = ConnectionState.Idle
    private val eventBus = EventBus.getInstance()
    
    @Volatile private var firstConnect = true
    @Volatile private var hadConnected = false
    @Volatile private var isReconnecting = false
    @Volatile private var loginThread: Thread? = null
    
    private var modules: SLModules? = null
    internal val parcelInfo = SLParcelInfo()
    
    @Volatile private var reconnectAttempts = 0
    private val tempCircuits = Collections.synchronizedMap(HashMap<SLAuthReply, SLTempCircuit>())
    private var userManager: UserManager? = null
    
    @Volatile private var userWantsConnected = false

    /**
     * Connection states
     */
    enum class ConnectionState {
        Idle,
        Connecting,
        Connected
    }

    /**
     * Exception thrown when operations require an active connection
     */
    class NotConnectedException : Exception("Grid not connected") {
        companion object {
            private const val serialVersionUID = 2164121452714562470L
        }
    }

    /**
     * Perform connection to grid with authentication
     */
    private fun doConnect(authParams: SLAuthParams, location: String) {
        try {
            val loginReply = SLAuth().Login(authParams.withLocation(location))
            if (loginReply.success) {
                synchronized(this) {
                    if (connectionState == ConnectionState.Idle) {
                        return
                    }
                    authReply = loginReply
                    activeAgentUUID = loginReply.agentID
                    userManager = UserManager.getUserManager(activeAgentUUID)
                    userManager?.getChatterList()?.getFriendManager()?.updateFriendList(loginReply.friends)
                    parcelInfo.reset(userManager)
                    startCircuit(loginReply, null)
                    return
                }
            }
            setConnectionState(ConnectionState.Idle)
            reconnectOrDrop(isLogin = true, fromLogout = false, message = loginReply.message ?: "Login failed")
        } catch (e: Exception) {
            setConnectionState(ConnectionState.Idle)
            reconnectOrDrop(isLogin = true, fromLogout = false, message = "Failed to connect to login server.")
        }
    }

    /**
     * Attempt to reconnect to the grid
     */
    @Synchronized
    private fun reconnect(): Boolean {
        // Check if we should attempt reconnection
        if (!userWantsConnected || !hadConnected) {
            isReconnecting = false
            return false
        }
        
        if (!GlobalOptions.getAutoReconnect()) {
            isReconnecting = false
            return false
        }
        
        if (reconnectAttempts >= GlobalOptions.getMaxReconnectAttempts()) {
            isReconnecting = false
            return false
        }
        
        // Only reconnect if we're in idle state and have auth params
        if (connectionState != ConnectionState.Idle || authParams == null) {
            isReconnecting = false
            return true // Return true to indicate we're still trying to reconnect
        }
        
        // Attempt reconnection
        reconnectAttempts++
        isReconnecting = true
        
        // Publish reconnecting event
        eventBus.publish(SLReconnectingEvent(reconnectAttempts))
        
        // Start connecting with "last" location
        startConnecting(delay = true, location = "last")
        
        return true
    }

    /**
     * Get autoresponse text if enabled
     */
    fun getAutoresponse(): String? {
        return if (autoresponseEnabled) autoresponseText else null
    }

    /**
     * Reconnect or drop connection based on state
     */
    private fun reconnectOrDrop(isLogin: Boolean, fromLogout: Boolean, message: String) {
        if (!reconnect()) {
            activeAgentUUID?.let {
                GridConnectionManager.removeConnection(it, this)
            }
            if (isLogin) {
                eventBus.publish(SLLoginResultEvent(false, message, activeAgentUUID))
            } else {
                eventBus.publish(SLDisconnectEvent(fromLogout, message))
            }
        }
    }

    /**
     * Set autoresponse information
     */
    fun setAutoresponseInfo(enabled: Boolean, text: String) {
        autoresponseEnabled = enabled
        autoresponseText = text
    }

    /**
     * Update connection state and notify listeners
     */
    private fun setConnectionState(newState: ConnectionState) {
        if (connectionState != newState) {
            connectionState = newState
            eventBus.publish(SLConnectionStateChangedEvent(newState))
        }
    }

    /**
     * Start circuit connection to simulator
     */
    private fun startCircuit(authReply: SLAuthReply, tempCircuit: SLTempCircuit?) {
        Debug.Log("login reply: ip = ${authReply.simAddress}, port = ${authReply.simPort}, ccode = ${authReply.circuitCode}")
        authReply.inventoryRoot?.let {
            Debug.Log("inventory root: $it")
        } ?: Debug.Log("inventory root is null")
        
        val caps = SLCaps()
        caps.GetCapabilites(this.authReply!!.loginURL, this.authReply!!.seedCapability)
        
        try {
            agentCircuit = SLAgentCircuit(this, SLCircuitInfo(authReply), authReply, caps, tempCircuit)
            modules = agentCircuit?.getModules()
            
            try {
                capEventQueue = SLCapEventQueue(caps.getCapabilityOrThrow(SLCapability.EventQueueGet), agentCircuit!!)
            } catch (e: NoSuchCapabilityException) {
                e.printStackTrace()
            }
            
            parcelInfo.reset(userManager)
            TextureCache.getInstance().setFetcher(modules?.textureFetcher)
            AddCircuit(agentCircuit!!)
            agentCircuit?.SendUseCode()
            firstConnect = false
        } catch (e: IOException) {
            setConnectionState(ConnectionState.Idle)
            reconnectOrDrop(isLogin = true, fromLogout = false, message = "Failed to connect to the simulator.")
        }
    }

    /**
     * Start connecting with optional delay
     */
    private fun startConnecting(delay: Boolean, location: String) {
        loginThread = Thread {
            if (delay) {
                try {
                    Thread.sleep(3000)
                } catch (e: InterruptedException) {
                    e.printStackTrace()
                }
            }
            authParams?.let { doConnect(it, location) }
            loginThread = null
        }
        setConnectionState(ConnectionState.Connecting)
        loginThread?.start()
    }

    /**
     * Cancel connection attempt
     */
    @Synchronized
    fun CancelConnect() {
        userWantsConnected = false
        isReconnecting = false
        hadConnected = false
        closeConnectionObjects()
    }

    /**
     * Connect to grid with authentication parameters
     */
    @Synchronized
    fun Connect(authParams: SLAuthParams) {
        if (connectionState == ConnectionState.Idle) {
            this.authParams = authParams
            userWantsConnected = true
            reconnectAttempts = 0
            isReconnecting = false
            hadConnected = false
            firstConnect = true
            startConnecting(delay = false, location = authParams.startLocation)
        }
    }

    /**
     * Disconnect from grid
     */
    @Synchronized
    fun Disconnect() {
        userWantsConnected = false
        isReconnecting = false
        hadConnected = false
        agentCircuit?.SendLogoutRequest() ?: processDisconnect(fromLogout = true, message = "Logged out")
    }

    /**
     * Handle teleport completion
     */
    @Synchronized
    fun HandleTeleportFinish(authReply: SLAuthReply) {
        agentCircuit?.let {
            it.CloseCircuit()
            agentCircuit = null
        }
        capEventQueue?.let {
            it.stopQueue()
            capEventQueue = null
        }
        this.authReply = authReply
        startCircuit(authReply, tempCircuits.remove(authReply))
    }

    /**
     * Add temporary circuit for region crossing
     */
    @Synchronized
    fun addTempCircuit(authReply: SLAuthReply) {
        if (!tempCircuits.containsKey(authReply)) {
            try {
                val tempCircuit = SLTempCircuit(this, SLCircuitInfo(authReply), authReply)
                tempCircuits[authReply] = tempCircuit
                AddCircuit(tempCircuit)
                tempCircuit.SendUseCode()
            } catch (e: IOException) {
                e.printStackTrace()
            }
        }
    }

    /**
     * Close all connection objects
     */
    @Synchronized
    fun closeConnectionObjects() {
        loginThread?.interrupt()
        loginThread = null
        modules = null
        
        agentCircuit?.let {
            it.CloseCircuit()
            agentCircuit = null
        }
        
        capEventQueue?.let {
            it.stopQueue()
            capEventQueue = null
        }
        
        TextureCache.getInstance().setFetcher(null)
        
        tempCircuits.values.forEach { it.CloseCircuit() }
        tempCircuits.clear()
        
        setConnectionState(ConnectionState.Idle)
    }

    /**
     * Force disconnect from grid
     */
    @Synchronized
    fun forceDisconnect(fromLogoutRequest: Boolean) {
        if (fromLogoutRequest) {
            userWantsConnected = false
            isReconnecting = false
            hadConnected = false
        }
        Debug.Log("GridConnection: forceDisconnect() called, fromLogoutRequest = $fromLogoutRequest")
        
        when (connectionState) {
            ConnectionState.Connected -> {
                closeConnectionObjects()
                reconnectOrDrop(isLogin = false, fromLogout = fromLogoutRequest, message = "Network connection lost.")
            }
            ConnectionState.Connecting -> {
                closeConnectionObjects()
                reconnectOrDrop(isLogin = true, fromLogout = fromLogoutRequest, message = "Network connection lost.")
            }
            ConnectionState.Idle -> {
                // Already idle, nothing to do
            }
        }
    }

    // Public accessors

    fun getActiveAgentUUID(): UUID? = activeAgentUUID

    @Throws(NotConnectedException::class)
    fun getAgentCircuit(): SLAgentCircuit {
        return agentCircuit ?: throw NotConnectedException()
    }

    @Synchronized
    fun getConnectionState(): ConnectionState = connectionState

    fun getIsReconnecting(): Boolean = isReconnecting

    @Synchronized
    @Throws(NotConnectedException::class)
    fun getModules(): SLModules {
        return modules ?: throw NotConnectedException()
    }

    fun getReconnectAttempt(): Int = reconnectAttempts

    fun isFirstConnect(): Boolean = firstConnect

    /**
     * Notify that login failed
     */
    @Synchronized
    fun notifyLoginError(message: String) {
        closeConnectionObjects()
        reconnectOrDrop(isLogin = true, fromLogout = false, message = message)
    }

    /**
     * Notify that login succeeded
     */
    @Synchronized
    fun notifyLoginSuccess() {
        hadConnected = true
        reconnectAttempts = 0
        isReconnecting = false
        setConnectionState(ConnectionState.Connected)
        
        activeAgentUUID?.let {
            GridConnectionManager.setConnection(it, this)
        }
        
        eventBus.publish(SLLoginResultEvent(true, null, activeAgentUUID))
    }

    /**
     * Process disconnect event
     */
    @Synchronized
    fun processDisconnect(fromLogout: Boolean, message: String) {
        if (connectionState != ConnectionState.Idle) {
            closeConnectionObjects()
            reconnectOrDrop(isLogin = false, fromLogout = fromLogout, message = message)
        }
    }

    /**
     * Remove temporary circuit
     */
    @Synchronized
    fun removeTempCircuit(tempCircuit: SLTempCircuit) {
        val iterator = tempCircuits.entries.iterator()
        while (iterator.hasNext()) {
            if (iterator.next().value == tempCircuit) {
                iterator.remove()
            }
        }
        tempCircuit.CloseCircuit()
    }
}
