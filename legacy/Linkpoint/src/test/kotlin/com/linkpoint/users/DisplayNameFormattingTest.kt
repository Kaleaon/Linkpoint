package com.linkpoint.users

import org.junit.Assert.assertEquals
import org.junit.Test
import java.util.UUID

class DisplayNameFormattingTest {

    private val agentId = UUID.fromString("11111111-1111-1111-1111-111111111111")

    @Test
    fun `legacy fallback hides Resident last name`() {
        val name = DisplayName(
            agentId = agentId,
            username = "charlie.resident",
            displayName = null,
            isDefault = true,
            nextUpdate = 0L,
            legacyFirstName = "Charlie",
            legacyLastName = "Resident"
        )

        assertEquals(
            "Charlie",
            name.format(DisplayNameFormattingPolicy(outputMode = DisplayNameOutputMode.LEGACY_FALLBACK))
        )
    }

    @Test
    fun `legacy fallback uses username when last name missing`() {
        val name = DisplayName(
            agentId = agentId,
            username = "soloavatar",
            displayName = null,
            isDefault = true,
            nextUpdate = 0L,
            legacyFirstName = "",
            legacyLastName = ""
        )

        assertEquals(
            "soloavatar",
            name.format(DisplayNameFormattingPolicy(outputMode = DisplayNameOutputMode.LEGACY_FALLBACK))
        )
    }

    @Test
    fun `display plus username falls back to username when display name empty`() {
        val name = DisplayName(
            agentId = agentId,
            username = "avery.avatar",
            displayName = "",
            isDefault = false,
            nextUpdate = 0L
        )

        assertEquals(
            "avery.avatar",
            name.format(DisplayNameFormattingPolicy(outputMode = DisplayNameOutputMode.DISPLAY_AND_USERNAME))
        )
    }

    @Test
    fun `display plus username includes both when display present`() {
        val name = DisplayName(
            agentId = agentId,
            username = "avery.avatar",
            displayName = "Avery",
            isDefault = false,
            nextUpdate = 0L
        )

        assertEquals(
            "Avery (avery.avatar)",
            name.format(DisplayNameFormattingPolicy(outputMode = DisplayNameOutputMode.DISPLAY_AND_USERNAME))
        )
    }
}
