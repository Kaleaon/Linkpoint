package com.linkpoint.users

import org.junit.runner.RunWith
import androidx.test.ext.junit.runners.AndroidJUnit4

import com.linkpoint.protocol.capabilities.CapabilityManager
import com.linkpoint.protocol.llsd.LLSDInteger
import com.linkpoint.protocol.llsd.LLSDMap
import com.linkpoint.protocol.llsd.LLSDString
import com.linkpoint.protocol.llsd.LLSDUUID
import com.linkpoint.protocol.llsd.LLSDValue
import com.linkpoint.protocol.messages.UDPConnectionFixed
import kotlinx.coroutines.runBlocking
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Test
import java.util.UUID
import java.util.concurrent.atomic.AtomicInteger

@RunWith(AndroidJUnit4::class)
class UserProfileManagerTest {

    @Test
    fun `requestProfile uses AgentProfile capability response and caches callback result`() = runBlocking {
        val capabilityManager = CapabilityManager().apply {
            setCapabilityForTest("AgentProfile", "http://example.invalid/agent-profile")
        }
        val avatarId = UUID.randomUUID()

        var capturedRequest: LLSDValue? = null
        val fallbackCount = AtomicInteger(0)
        var callbackProfile: UserProfile? = null

        val manager = UserProfileManager(
            capabilityManager = capabilityManager,
            udpConnection = UDPConnectionFixed(),
            agentId = UUID.randomUUID(),
            capabilityRequest = { capName, body ->
                assertEquals("AgentProfile", capName)
                capturedRequest = body
                LLSDMap().apply {
                    this["sl_image_id"] = LLSDUUID(UUID.fromString("11111111-1111-1111-1111-111111111111"))
                    this["fl_image_id"] = LLSDUUID(UUID.fromString("22222222-2222-2222-2222-222222222222"))
                    this["partner_id"] = LLSDUUID(UUID.fromString("33333333-3333-3333-3333-333333333333"))
                    this["sl_about_text"] = LLSDString("about")
                    this["fl_about_text"] = LLSDString("first life")
                    this["born_on"] = LLSDString("January 1, 2000")
                    this["profile_url"] = LLSDString("https://example.com/profile")
                    this["flags"] = LLSDInteger(0x10)
                    this["display_name"] = LLSDString("Display Name")
                    this["username"] = LLSDString("display.name")
                    this["account_type"] = LLSDString("Resident")
                }
            },
            udpFallbackRequest = {
                fallbackCount.incrementAndGet()
            }
        )

        manager.requestProfile(avatarId) { profile ->
            callbackProfile = profile
        }

        val requestMap = capturedRequest as? LLSDMap
        assertNotNull(requestMap)
        assertEquals(avatarId, requestMap?.getUUID("avatar_id"))

        assertEquals(0, fallbackCount.get())
        assertNotNull(callbackProfile)
        assertEquals("Display Name", callbackProfile?.displayName)
        assertEquals("about", callbackProfile?.aboutText)
        assertTrue(callbackProfile?.isOnline == true)

        val cached = manager.getCachedProfile(avatarId)
        assertNotNull(cached)
        assertEquals("display.name", cached?.username)
    }

    @Test
    fun `requestProfile falls back to UDP when AgentProfile returns malformed LLSD`() = runBlocking {
        val capabilityManager = CapabilityManager().apply {
            setCapabilityForTest("AgentProfile", "http://example.invalid/agent-profile")
        }
        val fallbackCount = AtomicInteger(0)
        val manager = UserProfileManager(
            capabilityManager = capabilityManager,
            udpConnection = UDPConnectionFixed(),
            agentId = UUID.randomUUID(),
            capabilityRequest = { _, _ -> LLSDString("not a map") },
            udpFallbackRequest = { fallbackCount.incrementAndGet() }
        )

        var callbackInvoked = false
        val avatarId = UUID.randomUUID()
        manager.requestProfile(avatarId) {
            callbackInvoked = true
        }

        assertEquals(1, fallbackCount.get())
        assertFalse(callbackInvoked)
        assertNull(manager.getCachedProfile(avatarId))
    }

    @Test
    fun `requestProfile falls back to UDP when capability is unavailable`() = runBlocking {
        val capabilityManager = CapabilityManager()
        var capabilityCalled = false
        val fallbackCount = AtomicInteger(0)
        val manager = UserProfileManager(
            capabilityManager = capabilityManager,
            udpConnection = UDPConnectionFixed(),
            agentId = UUID.randomUUID(),
            capabilityRequest = { _, _ ->
                capabilityCalled = true
                null
            },
            udpFallbackRequest = { fallbackCount.incrementAndGet() }
        )

        manager.requestProfile(UUID.randomUUID())

        assertFalse(capabilityCalled)
        assertEquals(1, fallbackCount.get())
    }

    @Suppress("UNCHECKED_CAST")
    private fun CapabilityManager.setCapabilityForTest(name: String, url: String) {
        val field = CapabilityManager::class.java.getDeclaredField("capabilities")
        field.isAccessible = true
        val capabilities = field.get(this) as MutableMap<String, String>
        capabilities[name] = url
    }
}
