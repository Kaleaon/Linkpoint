package com.linkpoint.network.events

import com.linkpoint.network.NetworkStateMonitor

/**
 * Event fired when network state changes.
 * Used to notify UI and connection managers about network availability.
 */
data class NetworkStateChangedEvent(
    val state: NetworkStateMonitor.NetworkState,
    val previousType: NetworkStateMonitor.NetworkType
)
