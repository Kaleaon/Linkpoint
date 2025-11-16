package com.linkpoint

import android.app.Service
import android.content.Context
import android.content.Intent
import android.os.Binder
import android.os.IBinder
import android.util.Log
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.cancel
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import java.util.UUID

/**
 * Lightweight connection orchestrator that mimics the login/handshake phases
 * expected by the Linkpoint viewer. A real grid transport can slot into this
 * class later without having to change the UI contract.
 */
class GridConnectionService : Service() {

    sealed class ConnectionState {
        object Idle : ConnectionState()
        data class Connecting(val avatarName: String, val grid: String, val sessionId: UUID) : ConnectionState()
        data class Connected(val avatarName: String, val grid: String, val sessionId: UUID, val connectedAt: Long) :
            ConnectionState()
        data class Error(val avatarName: String?, val grid: String?, val message: String) : ConnectionState()
    }

    private val binder = ConnectionBinder()
    private val scope = CoroutineScope(SupervisorJob() + Dispatchers.IO)
    private val stateFlow = MutableStateFlow<ConnectionState>(ConnectionState.Idle)

    override fun onBind(intent: Intent?): IBinder = binder

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        when (intent?.action) {
            ACTION_CONNECT -> {
                val first = intent.getStringExtra(EXTRA_FIRST_NAME).orEmpty()
                val last = intent.getStringExtra(EXTRA_LAST_NAME).orEmpty()
                val password = intent.getStringExtra(EXTRA_PASSWORD)
                val grid = intent.getStringExtra(EXTRA_GRID_NAME) ?: DEFAULT_GRID_NAME
                connect(first, last, password, grid)
            }
            ACTION_DISCONNECT -> disconnect()
        }
        return START_STICKY
    }

    override fun onDestroy() {
        scope.cancel("service destroyed")
        super.onDestroy()
    }

    private fun connect(first: String, last: String, password: String?, grid: String) {
        if (first.isBlank() || last.isBlank()) {
            stateFlow.tryEmit(ConnectionState.Error(null, grid, "Missing avatar name"))
            return
        }

        scope.launch {
            val sessionId = UUID.randomUUID()
            val avatarName = "$first $last"
            stateFlow.emit(ConnectionState.Connecting(avatarName, grid, sessionId))
            try {
                // Simulate the legacy XML-RPC login call followed by region handoff.
                delay(LOGIN_DELAY_MS)
                if (password.isNullOrEmpty()) {
                    throw IllegalArgumentException("Password required")
                }
                delay(REGION_HANDOFF_DELAY_MS)
                stateFlow.emit(
                    ConnectionState.Connected(
                        avatarName = avatarName,
                        grid = grid,
                        sessionId = sessionId,
                        connectedAt = System.currentTimeMillis()
                    )
                )
            } catch (error: Throwable) {
                Log.e(TAG, "Connection failure", error)
                stateFlow.emit(ConnectionState.Error(avatarName, grid, error.message ?: "Unknown error"))
            }
        }
    }

    private fun disconnect() {
        scope.launch {
            stateFlow.emit(ConnectionState.Idle)
            stopSelf()
        }
    }

    inner class ConnectionBinder : Binder() {
        fun state(): StateFlow<ConnectionState> = stateFlow.asStateFlow()
        fun connect(first: String, last: String, password: String, grid: String = DEFAULT_GRID_NAME) {
            this@GridConnectionService.connect(first, last, password, grid)
        }

        fun disconnect() = this@GridConnectionService.disconnect()
    }

    companion object {
        private const val TAG = "GridConnectionService"
        private const val ACTION_CONNECT = "com.linkpoint.action.CONNECT"
        private const val ACTION_DISCONNECT = "com.linkpoint.action.DISCONNECT"
        private const val EXTRA_FIRST_NAME = "extra_first_name"
        private const val EXTRA_LAST_NAME = "extra_last_name"
        private const val EXTRA_PASSWORD = "extra_password"
        private const val EXTRA_GRID_NAME = "extra_grid_name"
        private const val DEFAULT_GRID_NAME = "Second Life"
        private const val LOGIN_DELAY_MS = 1_000L
        private const val REGION_HANDOFF_DELAY_MS = 750L

        fun connect(
            context: Context,
            firstName: String,
            lastName: String,
            password: String,
            gridName: String = DEFAULT_GRID_NAME
        ) {
            val intent = Intent(context, GridConnectionService::class.java).apply {
                action = ACTION_CONNECT
                putExtra(EXTRA_FIRST_NAME, firstName)
                putExtra(EXTRA_LAST_NAME, lastName)
                putExtra(EXTRA_PASSWORD, password)
                putExtra(EXTRA_GRID_NAME, gridName)
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
