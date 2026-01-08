package com.linkpoint.slproto

import com.linkpoint.Debug
import com.linkpoint.GlobalOptions
import com.linkpoint.eventbus.EventBus
import com.linkpoint.res.textures.TextureCache
import com.lumiyaviewer.linkpoint.GridConnectionManager
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
import com.linkpoint.slproto.events.SLReconnectingEvent
import com.linkpoint.slproto.modules.SLModules
import com.linkpoint.slproto.users.manager.UserManager
import com.linkpoint.slproto.auth.SessionManager
import java.io.IOException
import java.util.Collections
import java.util.UUID

/**
 * Manages the lifecycle of a Second Life grid connection.
 */
class SLGridConnection : SLConnection() {

    enum class ConnectionState { Idle, Connecting, Connected }

    class NotConnectedException : Exception("Grid not connected")

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
    private val tempCircuits = Collections.synchronizedMap(mutableMapOf<SLAuthReply, SLTempCircuit>())
    private var userManager: UserManager? = null
    @Volatile private var userWantsConnected = false

    // ---------------------------------------------------------------------
    // Public API

    fun getAutoresponse(): String? = if (autoresponseEnabled) autoresponseText else null

    fun setAutoresponseInfo(enabled: Boolean, text: String) {
        autoresponseEnabled = enabled
        autoresponseText = text
    }

    fun getConnectionState(): ConnectionState = connectionState

    fun isReconnecting(): Boolean = isReconnecting

    fun getActiveAgentUUID(): UUID? = activeAgentUUID

    fun getReconnectAttempt(): Int = reconnectAttempts

    fun isFirstConnect(): Boolean = firstConnect

    @Synchronized
    @Throws(NotConnectedException::class)
    fun getAgentCircuit(): SLAgentCircuit = agentCircuit ?: throw NotConnectedException()

    @Synchronized
    @Throws(NotConnectedException::class)
    fun getModules(): SLModules = modules ?: throw NotConnectedException()

    @Synchronized
    fun connect(params: SLAuthParams) {
        if (connectionState != ConnectionState.Idle) return

        authParams = params
        userWantsConnected = true
        reconnectAttempts = 0
        isReconnecting = false
        hadConnected = false
        firstConnect = true
        startConnecting(delay = false, location = params.startLocation)
    }

    @Synchronized
    fun cancelConnect() {
        userWantsConnected = false
        isReconnecting = false
        hadConnected = false
        closeConnectionObjects()
    }

    @Synchronized
    fun disconnect() {
        userWantsConnected = false
        isReconnecting = false
        hadConnected = false
        agentCircuit?.SendLogoutRequest() ?: processDisconnect(true, "Logged out")
    }

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
            else -> Unit
        }
    }

    @Synchronized
    fun handleTeleportFinish(reply: SLAuthReply) {
        agentCircuit?.CloseCircuit()
        agentCircuit = null
        capEventQueue?.stopQueue()
        capEventQueue = null

        authReply = reply
        val tempCircuit = tempCircuits.remove(reply)
        startCircuit(reply, tempCircuit)
    }

    @Synchronized
    fun addTempCircuit(reply: SLAuthReply) {
        if (tempCircuits.containsKey(reply)) return

        try {
            val tempCircuit = SLTempCircuit(this, SLCircuitInfo(reply), reply)
            tempCircuits[reply] = tempCircuit
            AddCircuit(tempCircuit)
            tempCircuit.SendUseCode()
        } catch (ioe: IOException) {
            Debug.Warning(ioe)
        }
    }

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

    @Synchronized
    fun closeConnectionObjects() {
        loginThread?.interrupt()
        loginThread = null

        modules = null
        agentCircuit?.CloseCircuit()
        agentCircuit = null

        capEventQueue?.stopQueue()
        capEventQueue = null

        TextureCache.getInstance().setFetcher(null)
        tempCircuits.values.toList().forEach { it.CloseCircuit() }
        tempCircuits.clear()

        SessionManager.clear()
        setConnectionState(ConnectionState.Idle)
    }

    @Synchronized
    fun notifyLoginError(message: String) {
        closeConnectionObjects()
        reconnectOrDrop(isLogin = true, fromLogout = false, message = message)
    }

    @Synchronized
    fun notifyLoginSuccess() {
        hadConnected = true
        reconnectAttempts = 0
        isReconnecting = false
        setConnectionState(ConnectionState.Connected)

        activeAgentUUID?.let { GridConnectionManager.setConnection(it, this) }
        eventBus.publish(SLLoginResultEvent(true, null, activeAgentUUID))
    }

    @Synchronized
    fun processDisconnect(fromLogout: Boolean, message: String) {
        if (connectionState == ConnectionState.Idle) return
        closeConnectionObjects()
        reconnectOrDrop(isLogin = false, fromLogout = fromLogout, message = message)
    }

    // ---------------------------------------------------------------------
    // Internal helpers

    private fun doConnect(params: SLAuthParams, location: String) {
        try {
            val reply = SLAuth().Login(params.withLocation(location))
            if (reply.success) {
                synchronized(this) {
                    if (connectionState == ConnectionState.Idle) return

                    authReply = reply
                    activeAgentUUID = reply.agentID
                    userManager = UserManager.getUserManager(activeAgentUUID)
                    userManager?.getChatterList()?.getFriendManager()?.updateFriendList(reply.friends)
                    parcelInfo.reset(userManager)
                    startCircuit(reply, null)
                }
            } else {
                setConnectionState(ConnectionState.Idle)
                reconnectOrDrop(isLogin = true, fromLogout = false, message = reply.message ?: "Login failed")
            }
        } catch (ex: Exception) {
            Debug.Warning(ex)
            setConnectionState(ConnectionState.Idle)
            reconnectOrDrop(isLogin = true, fromLogout = false, message = "Failed to connect to login server.")
        }
    }

    private fun startCircuit(reply: SLAuthReply, tempCircuit: SLTempCircuit?) {
        Debug.Log("login reply: ip = ${reply.simAddress}, port = ${reply.simPort}, ccode = ${reply.circuitCode}")
        reply.inventoryRoot?.let { Debug.Log("inventory root: $it") } ?: Debug.Log("inventory root is null")

        val caps = SLCaps()
        caps.GetCapabilites(reply.loginURL, reply.seedCapability)

        try {
            val circuit = SLAgentCircuit(this, SLCircuitInfo(reply), reply, caps, tempCircuit)
            agentCircuit = circuit
            modules = circuit.getModules()

            try {
                capEventQueue = SLCapEventQueue(caps.getCapabilityOrThrow(SLCapability.EventQueueGet), circuit)
            } catch (ex: NoSuchCapabilityException) {
                Debug.Warning(ex)
            }

            parcelInfo.reset(userManager)
            TextureCache.getInstance().setFetcher(modules?.textureFetcher)
            AddCircuit(circuit)
            circuit.SendUseCode()
            firstConnect = false
        } catch (ioe: IOException) {
            Debug.Warning(ioe)
            setConnectionState(ConnectionState.Idle)
            reconnectOrDrop(isLogin = true, fromLogout = false, message = "Failed to connect to the simulator.")
        }
    }

    private fun startConnecting(delay: Boolean, location: String) {
        loginThread = Thread {
            if (delay) {
                try {
                    Thread.sleep(3_000)
                } catch (_: InterruptedException) {
                    Thread.currentThread().interrupt()
                    return@Thread
                }
            }
            authParams?.let { doConnect(it, location) }
            loginThread = null
        }.also {
            setConnectionState(ConnectionState.Connecting)
            it.start()
        }
    }

    @Synchronized
    private fun reconnect(): Boolean {
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

        if (connectionState != ConnectionState.Idle || authParams == null) {
            isReconnecting = false
            return true
        }

        reconnectAttempts++
        isReconnecting = true
        eventBus.publish(SLReconnectingEvent(reconnectAttempts))
        startConnecting(delay = true, location = "last")
        return true
    }

    private fun reconnectOrDrop(isLogin: Boolean, fromLogout: Boolean, message: String) {
        if (reconnect()) return

        activeAgentUUID?.let { GridConnectionManager.removeConnection(it, this) }
        if (isLogin) {
            eventBus.publish(SLLoginResultEvent(false, message, activeAgentUUID))
        } else {
            eventBus.publish(SLDisconnectEvent(fromLogout, message))
        }
    }

    private fun setConnectionState(state: ConnectionState) {
        if (connectionState == state) return
        connectionState = state
        eventBus.publish(SLConnectionStateChangedEvent(state))
    }

    // ---------------------------------------------------------------------
    // Legacy aliases retained for decompiled call sites.

    fun Connect(params: SLAuthParams) = connect(params)
    fun CancelConnect() = cancelConnect()
    fun Disconnect() = disconnect()
    fun ForceDisconnect(fromLogoutRequest: Boolean) = forceDisconnect(fromLogoutRequest)
    fun HandleTeleportFinish(reply: SLAuthReply) = handleTeleportFinish(reply)
    fun AddTempCircuit(reply: SLAuthReply) = addTempCircuit(reply)
    fun CloseConnectionObjects() = closeConnectionObjects()
    fun NotifyLoginError(message: String) = notifyLoginError(message)
    fun NotifyLoginSuccess() = notifyLoginSuccess()
    fun ProcessDisconnect(fromLogout: Boolean, message: String) = processDisconnect(fromLogout, message)
    fun Reconnect(): Boolean = reconnect()
}
