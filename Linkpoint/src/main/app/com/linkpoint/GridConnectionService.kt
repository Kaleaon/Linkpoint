package com.linkpoint

import android.app.Service
import android.content.Context
import android.content.Intent
import android.os.Binder
import android.os.IBinder
import com.linkpoint.modern.protocol.HybridSLTransport
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.cancel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

/**
 * Service that owns the long-lived connection to the Second Life grid.
 * Exposes state via [ConnectionBinder] so UI layers can observe login progress,
 * session info, and raw event queue traffic.
 */
class GridConnectionService : Service() {

    sealed class ConnectionState {
        object Idle : ConnectionState()
        data class Connecting(val avatarName: String, val grid: HybridSLTransport.Grid) : ConnectionState()
        data class Connected(val session: HybridSLTransport.SessionInfo) : ConnectionState()
        data class Error(val avatarName: String, val grid: HybridSLTransport.Grid, val message: String) : ConnectionState()
    }

    private val binder = ConnectionBinder()
    private val scope = CoroutineScope(SupervisorJob() + Dispatchers.Main.immediate)
    private val transport = HybridSLTransport()
    private val serviceState = MutableStateFlow<ConnectionState>(ConnectionState.Idle)
    private var loginJob = scope.launch { }

    override fun onBind(intent: Intent?): IBinder = binder

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        when (intent?.action) {
            ACTION_CONNECT -> handleConnectIntent(intent)
            ACTION_DISCONNECT -> disconnect()
        }
        return START_STICKY
    }

    override fun onDestroy() {
        loginJob.cancel()
        scope.cancel("service destroyed")
        super.onDestroy()
    }

    private fun handleConnectIntent(intent: Intent) {
        val first = intent.getStringExtra(EXTRA_FIRST_NAME).orEmpty()
        val last = intent.getStringExtra(EXTRA_LAST_NAME).orEmpty()
        val password = intent.getStringExtra(EXTRA_PASSWORD).orEmpty()
        val gridId = intent.getStringExtra(EXTRA_GRID_NAME)
        connect(first, last, password, HybridSLTransport.Grid.fromId(gridId))
    }

    private fun connect(first: String, last: String, password: String, grid: HybridSLTransport.Grid) {
        if (first.isBlank() || last.isBlank()) {
            serviceState.value = ConnectionState.Error("", grid, "Missing avatar name")
            return
        }

        loginJob.cancel()
        loginJob = scope.launch {
            val avatarName = "$first $last".trim()
            serviceState.value = ConnectionState.Connecting(avatarName, grid)
            try {
                val session = transport.login(
                    grid = grid,
                    loginName = avatarName,
                    password = password
                )
                serviceState.value = ConnectionState.Connected(session)
            } catch (error: Exception) {
                serviceState.value = ConnectionState.Error(
                    avatarName = avatarName,
                    grid = grid,
                    message = error.message ?: "Unknown error"
                )
            }
        }
    }

    private fun disconnect() {
        loginJob.cancel()
        serviceState.value = ConnectionState.Idle
        scope.launch {
            transport.shutdown()
            stopSelf()
        }
    }

    inner class ConnectionBinder : Binder() {
        fun state(): StateFlow<ConnectionState> = serviceState.asStateFlow()
        fun transportState(): StateFlow<HybridSLTransport.State> =
            transport.state().stateIn(scope, SharingStarted.Eagerly, HybridSLTransport.State.Idle)
        fun events(): SharedFlow<HybridSLTransport.GridEvent> = transport.events()
        fun connect(first: String, last: String, password: String, grid: HybridSLTransport.Grid) =
            this@GridConnectionService.connect(first, last, password, grid)
        fun disconnect() = this@GridConnectionService.disconnect()
    }

    companion object {
        private const val ACTION_CONNECT = "com.linkpoint.action.CONNECT"
        private const val ACTION_DISCONNECT = "com.linkpoint.action.DISCONNECT"
        private const val EXTRA_FIRST_NAME = "extra_first_name"
        private const val EXTRA_LAST_NAME = "extra_last_name"
        private const val EXTRA_PASSWORD = "extra_password"
        private const val EXTRA_GRID_NAME = "extra_grid_name"

        fun connect(
            context: Context,
            firstName: String,
            lastName: String,
            password: String,
            grid: HybridSLTransport.Grid = HybridSLTransport.Grid.SecondLifeMain
        ) {
            val intent = Intent(context, GridConnectionService::class.java).apply {
                action = ACTION_CONNECT
                putExtra(EXTRA_FIRST_NAME, firstName)
                putExtra(EXTRA_LAST_NAME, lastName)
                putExtra(EXTRA_PASSWORD, password)
                putExtra(EXTRA_GRID_NAME, grid.id)
            }
            context.startService(intent)
        }

        fun disconnect(context: Context) {
            val intent = Intent(context, GridConnectionService::class.java).apply {
                action = ACTION_DISCONNECT
            }
            context.startService(intent)
        }
    }
}
