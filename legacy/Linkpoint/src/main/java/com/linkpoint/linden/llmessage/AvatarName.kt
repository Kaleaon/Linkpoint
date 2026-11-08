package com.linkpoint.linden.llmessage

import com.linkpoint.linden.llcommon.LLSD

data class AvatarName(
    val username: String = "",
    val displayName: String = "",
    val legacyFirstName: String = "",
    val legacyLastName: String = "",
    val isDisplayNameDefault: Boolean = false,
    val expiresDate: Double = Double.MAX_VALUE,
    val nextUpdate: Double = 0.0,
    val isTemporary: Boolean = false
) : Comparable<AvatarName> {

    override fun compareTo(other: AvatarName): Int {
        val cmp = username.compareTo(other.username)
        return if (cmp != 0) cmp else displayName.compareTo(other.displayName)
    }

    fun getCompleteName(useParentheses: Boolean = true, forceComplete: Boolean = false): String {
        return if (useDisplayNames || forceComplete) {
            if (username.isEmpty() || isDisplayNameDefault) {
                if (trimResidentSurname || legacyFirstName.isEmpty()) {
                    displayName
                } else if (legacyLastName.isEmpty()) {
                    "$legacyFirstName Resident"
                } else {
                    "$legacyFirstName $legacyLastName"
                }
            } else if (useLegacyNameFormat) {
                when {
                    trimResidentSurname && (legacyLastName == "Resident" || legacyLastName.isEmpty()) ->
                        "$displayName ($legacyFirstName)"
                    legacyLastName.isEmpty() && !trimResidentSurname ->
                        "$displayName ($legacyLastName Resident)"
                    else ->
                        "$displayName ($legacyFirstName $legacyLastName)"
                }
            } else {
                var name = displayName
                if (useUsernames || forceComplete) {
                    name += if (useParentheses) " ($username)" else "  [ $username ]"
                }
                name
            }
        } else {
            getUserNameForDisplay()
        }
    }

    fun getLegacyName(): String {
        if (legacyFirstName.isEmpty() && legacyLastName.isEmpty()) return displayName
        return "$legacyFirstName $legacyLastName"
    }

    fun getUserName(lowercase: Boolean = false): String = when {
        legacyLastName.isEmpty() || legacyLastName == "Resident" -> {
            if (legacyFirstName.isEmpty()) displayName else legacyFirstName
        }
        lowercase -> "${legacyFirstName.lowercase()}.${legacyLastName.lowercase()}"
        else -> "$legacyFirstName $legacyLastName"
    }

    fun getUserNameForDisplay(): String = when {
        legacyFirstName.isEmpty() && legacyLastName.isEmpty() -> displayName
        (legacyLastName.isEmpty() || legacyLastName == "Resident") && trimResidentSurname -> legacyFirstName
        legacyLastName.isEmpty() && !trimResidentSurname -> "$legacyFirstName Resident"
        else -> "$legacyFirstName $legacyLastName"
    }

    fun toSD(): LLSD = LLSD.ofMap(mapOf(
        "username"                  to LLSD.of(username),
        "display_name"              to LLSD.of(displayName),
        "legacy_first_name"         to LLSD.of(legacyFirstName),
        "legacy_last_name"          to LLSD.of(legacyLastName),
        "is_display_name_default"   to LLSD.of(isDisplayNameDefault),
        "display_name_expires"      to LLSD.of(expiresDate.toDouble()),
        "display_name_next_update"  to LLSD.of(nextUpdate.toDouble())
    ))

    companion object {
        var useDisplayNames: Boolean = true
        var useUsernames: Boolean = true
        var useLegacyNameFormat: Boolean = false
        var trimResidentSurname: Boolean = true

        private const val MIN_ENTRY_LIFETIME = 60.0

        fun fromSD(sd: LLSD): AvatarName {
            val rawDisplay = sd["display_name"].asString()
            val uname = sd["username"].asString()
            val display = if (rawDisplay.isEmpty()) uname else rawDisplay
            return AvatarName(
                username = uname,
                displayName = display,
                legacyFirstName = sd["legacy_first_name"].asString(),
                legacyLastName = sd["legacy_last_name"].asString(),
                isDisplayNameDefault = sd["is_display_name_default"].asBoolean() || rawDisplay.isEmpty(),
                expiresDate = sd["display_name_expires"].asReal(),
                nextUpdate = sd["display_name_next_update"].asReal()
            )
        }

        fun fromFullName(fullName: String): AvatarName {
            val spaceIdx = fullName.indexOf(' ')
            val now = System.currentTimeMillis() / 1000.0
            return if (spaceIdx != -1) {
                val first = fullName.substring(0, spaceIdx)
                val last = fullName.substring(spaceIdx + 1)
                if (last != "Resident" || !trimResidentSurname) {
                    AvatarName(
                        username = "${first.lowercase()}.${last.lowercase()}",
                        displayName = fullName,
                        legacyFirstName = first,
                        legacyLastName = last,
                        isDisplayNameDefault = true,
                        isTemporary = true,
                        expiresDate = now + MIN_ENTRY_LIFETIME
                    )
                } else {
                    AvatarName(
                        username = first,
                        displayName = first,
                        legacyFirstName = first,
                        legacyLastName = last,
                        isDisplayNameDefault = true,
                        isTemporary = true,
                        expiresDate = now + MIN_ENTRY_LIFETIME
                    )
                }
            } else {
                AvatarName(
                    username = fullName,
                    displayName = fullName,
                    legacyFirstName = fullName,
                    legacyLastName = "",
                    isDisplayNameDefault = true,
                    isTemporary = true,
                    expiresDate = now + MIN_ENTRY_LIFETIME
                )
            }
        }
    }
}
