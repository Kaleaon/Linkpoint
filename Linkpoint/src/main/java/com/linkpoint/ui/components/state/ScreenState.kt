package com.linkpoint.ui.components.state

/**
 * Shared UI state contract for list/detail style screens.
 */
sealed interface ScreenState<out T> {
    data object Loading : ScreenState<Nothing>
    data class Content<T>(val data: T) : ScreenState<T>
    data object Empty : ScreenState<Nothing>
    data class Error(
        val message: String,
        val throwable: Throwable? = null,
        val telemetryContext: String = "unknown"
    ) : ScreenState<Nothing>
}
