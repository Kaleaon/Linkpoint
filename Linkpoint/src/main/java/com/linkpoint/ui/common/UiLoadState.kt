package com.linkpoint.ui.common

sealed interface UiLoadState {
    data object Content : UiLoadState
    data class Loading(val message: String = "Loading…") : UiLoadState
    data class Error(
        val title: String = "Something went wrong",
        val message: String,
        val retryLabel: String = "Retry"
    ) : UiLoadState
    data class Empty(
        val title: String,
        val message: String,
        val actionLabel: String? = null
    ) : UiLoadState
    data class Reconnecting(val message: String = "Reconnecting…") : UiLoadState
    data class LowBandwidth(val message: String = "Low bandwidth detected") : UiLoadState
}

typealias UiRetryCallback = () -> Unit

object UiTelemetryEvents {
    const val RETRY_TAPPED = "ui_state_retry_tapped"
    const val PRIMARY_ACTION_TAPPED = "ui_state_primary_action_tapped"
    const val DISMISSED = "ui_state_dismissed"
}

fun logUiTelemetry(eventName: String, screen: String, state: UiLoadState) {
    android.util.Log.d(
        "UiStateTelemetry",
        "event=$eventName screen=$screen state=${state::class.simpleName}"
    )
}
