package com.linkpoint.app.bootstrap

import com.linkpoint.network.events.ConnectionState

interface SessionStateProvider {
    fun setConnectionState(state: ConnectionState)
}
