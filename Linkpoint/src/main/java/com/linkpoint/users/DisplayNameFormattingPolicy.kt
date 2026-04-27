package com.linkpoint.users

/**
 * UI-agnostic display-name formatting policy.
 *
 * Mirrors Firestorm-style behavior concepts (format mode + legacy handling)
 * without static/global mutable flags.
 */
data class DisplayNameFormattingPolicy(
    val outputMode: DisplayNameOutputMode = DisplayNameOutputMode.DISPLAY_ONLY,
    val hideResidentLastName: Boolean = true,
    val fallbackToUsernameWhenLegacyIncomplete: Boolean = true
)

enum class DisplayNameOutputMode {
    DISPLAY_ONLY,
    DISPLAY_AND_USERNAME,
    LEGACY_FALLBACK
}

class DisplayNameFormattingPolicyHolder(
    var policy: DisplayNameFormattingPolicy = DisplayNameFormattingPolicy()
)

