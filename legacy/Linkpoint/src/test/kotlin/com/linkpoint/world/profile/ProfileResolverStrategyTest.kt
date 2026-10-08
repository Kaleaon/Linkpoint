package com.linkpoint.world.profile

import com.linkpoint.eventbus.AppEventBus
import com.linkpoint.eventbus.events.AvatarPropertiesReplyEvent
import com.linkpoint.protocol.capabilities.CapabilityRequester
import com.linkpoint.protocol.llsd.LLSDMap
import com.linkpoint.protocol.llsd.LLSDString
import com.linkpoint.protocol.llsd.LLSDValue
import com.linkpoint.world.AvatarProfile
import com.linkpoint.world.ProfileManager
import kotlinx.coroutines.*
import org.junit.After
import org.junit.Assert.*
import org.junit.Before
import org.junit.Test
import java.util.UUID

class ProfileResolverStrategyTest {

    private val testAgentId = UUID.randomUUID()
    private val targetAvatarId = UUID.randomUUID()

    @Before
    fun setUp() {
        AppEventBus.clear()
    }

    @After
    fun tearDown() {
        AppEventBus.clear()
    }

    @Test
    fun testAppEventBusDispatchingAndUnsubscribe() {
        var eventReceived: AvatarPropertiesReplyEvent? = null
        val sub = AppEventBus.subscribe<AvatarPropertiesReplyEvent> { event ->
            eventReceived = event
        }

        val event = AvatarPropertiesReplyEvent(
            agentId = testAgentId,
            avatarId = targetAvatarId,
            imageId = UUID.randomUUID(),
            firstLifeImageId = UUID.randomUUID(),
            partnerId = UUID.randomUUID(),
            aboutText = "Event Bus Test",
            firstLifeAboutText = "First Life Text",
            bornOn = "2026-01-01",
            profileUrl = "https://example.com/profile",
            flags = 0x01
        )

        AppEventBus.publish(event)
        assertNotNull("Event should be received by subscriber", eventReceived)
        assertEquals("Event Bus Test", eventReceived?.aboutText)

        // Unsubscribe and verify no further events delivered
        sub.unsubscribe()
        eventReceived = null
        AppEventBus.publish(event)
        assertNull("Event should not be delivered after unsubscribe", eventReceived)
    }

    @Test
    fun testCapabilityProfileStrategyResolution() = runBlocking {
        val mockCapRequester = object : CapabilityRequester {
            override suspend fun request(capName: String, body: LLSDValue?): LLSDValue? {
                return LLSDMap().apply {
                    this["display_name"] = LLSDString("CapUser")
                    this["username"] = LLSDString("cap.user")
                    this["sl_about_text"] = LLSDString("About Cap User")
                }
            }

            override fun getCapability(name: String): String? = "https://example.com/cap"
            override fun hasCapability(name: String): Boolean = true
        }

        val strategy = CapabilityProfileStrategy(mockCapRequester)
        assertTrue("Capability strategy should be available", strategy.isAvailable())

        val profile = strategy.getAvatarProfile(targetAvatarId)
        assertNotNull("Profile should be resolved via capability", profile)
        assertEquals("CapUser", profile?.displayName)
        assertEquals("About Cap User", profile?.aboutText)
    }

    @Test
    fun testUdpProfileStrategyResolutionViaEventBus() = runBlocking {
        val requestSent = CompletableDeferred<UUID>()

        val strategy = UdpProfileStrategy(
            sendRequestOverride = { agentId ->
                requestSent.complete(agentId)
            }
        )

        assertTrue("UDP strategy should be available", strategy.isAvailable())

        val profileDeferred = CompletableDeferred<AvatarProfile?>()
        val job = CoroutineScope(Dispatchers.IO).launch {
            val res = strategy.getAvatarProfile(targetAvatarId)
            profileDeferred.complete(res)
        }

        // Await request sending
        val requestedId = requestSent.await()
        assertEquals(targetAvatarId, requestedId)

        // Simulate incoming UDP AvatarPropertiesReply packet event via AppEventBus
        val replyEvent = AvatarPropertiesReplyEvent(
            agentId = testAgentId,
            avatarId = targetAvatarId,
            imageId = UUID.randomUUID(),
            firstLifeImageId = UUID.randomUUID(),
            partnerId = UUID.randomUUID(),
            aboutText = "Resolved via UDP Event",
            firstLifeAboutText = "UDP First Life",
            bornOn = "2026-02-02",
            profileUrl = "",
            flags = 0
        )
        AppEventBus.publish(replyEvent)

        val profile = profileDeferred.await()
        assertNotNull("Profile should be resolved via UDP event", profile)
        assertEquals("Resolved via UDP Event", profile?.aboutText)

        job.cancel()
        strategy.dispose()
    }

    @Test
    fun testUdpProfileStrategyUpdateProfile() = runBlocking {
        var updateCalled = false
        var updatedAboutText = ""

        val strategy = UdpProfileStrategy(
            sendUpdateOverride = { imageId, flImageId, aboutText, flAboutText, allowPublish, maturePublish, profileUrl ->
                updateCalled = true
                updatedAboutText = aboutText
                true
            }
        )

        val success = strategy.updateProfile(aboutText = "Updated UDP About")
        assertTrue("UDP profile update should succeed", success)
        assertTrue("Update override should have been called", updateCalled)
        assertEquals("Updated UDP About", updatedAboutText)

        strategy.dispose()
    }

    @Test
    fun testOpenSimRestProfileStrategyResolution() = runBlocking {
        val strategy = OpenSimRestProfileStrategy(
            restBaseUrl = "https://grid.opensim.org/profile",
            httpFetcher = { url, method, body ->
                if (url.contains(targetAvatarId.toString()) && method == "GET") {
                    """{
                        "display_name": "REST User",
                        "username": "rest.user",
                        "sl_about_text": "Resolved via REST Service",
                        "born_on": "2025-05-05"
                    }"""
                } else null
            }
        )

        assertTrue("REST strategy should be available", strategy.isAvailable())

        val profile = strategy.getAvatarProfile(targetAvatarId)
        assertNotNull("Profile should be resolved via REST service", profile)
        assertEquals("REST User", profile?.displayName)
        assertEquals("Resolved via REST Service", profile?.aboutText)
    }

    @Test
    fun testProfileManagerSequentialFallback() = runBlocking {
        // Strategy 1: Failing Capability Strategy
        val failingCapRequester = object : CapabilityRequester {
            override suspend fun request(capName: String, body: LLSDValue?): LLSDValue? = null
            override fun getCapability(name: String): String? = null
            override fun hasCapability(name: String): Boolean = false
        }
        val capStrategy = CapabilityProfileStrategy(failingCapRequester)

        // Strategy 2: UDP Strategy that returns profile
        val udpStrategy = UdpProfileStrategy(
            sendRequestOverride = { avatarId ->
                val reply = AvatarPropertiesReplyEvent(
                    agentId = testAgentId,
                    avatarId = avatarId,
                    imageId = UUID.randomUUID(),
                    firstLifeImageId = UUID.randomUUID(),
                    partnerId = UUID.randomUUID(),
                    aboutText = "Fallback UDP Profile",
                    firstLifeAboutText = "",
                    bornOn = "2026-03-03",
                    profileUrl = "",
                    flags = 0
                )
                AppEventBus.publish(reply)
            }
        )

        // Strategy 3: REST Strategy
        val restStrategy = OpenSimRestProfileStrategy(
            restBaseUrl = "https://grid.opensim.org/profile",
            httpFetcher = { _, _, _ -> null }
        )

        val profileManager = ProfileManager(failingCapRequester, listOf(capStrategy, udpStrategy, restStrategy))

        val profile = profileManager.getAvatarProfile(targetAvatarId)
        assertNotNull("ProfileManager should fall back and resolve profile via UDP", profile)
        assertEquals("Fallback UDP Profile", profile?.aboutText)

        udpStrategy.dispose()
        profileManager.shutdown()
    }

    @Test
    fun testProfileManagerFallbackToRestWhenCapAndUdpFail() = runBlocking {
        // Strategy 1: Unavailable Cap strategy
        val emptyCapRequester = object : CapabilityRequester {
            override suspend fun request(capName: String, body: LLSDValue?): LLSDValue? = null
            override fun getCapability(name: String): String? = null
            override fun hasCapability(name: String): Boolean = false
        }
        val capStrategy = CapabilityProfileStrategy(emptyCapRequester)

        // Strategy 2: UDP strategy that times out / fails
        val udpStrategy = UdpProfileStrategy(
            sendRequestOverride = { _ ->
                // Do not publish reply event, causing timeout
            }
        )

        // Strategy 3: REST strategy that succeeds
        val restStrategy = OpenSimRestProfileStrategy(
            restBaseUrl = "https://grid.opensim.org/profile",
            httpFetcher = { url, method, body ->
                """{
                    "display_name": "REST Fallback Avatar",
                    "sl_about_text": "Resolved after UDP timeout"
                }"""
            }
        )

        val profileManager = ProfileManager(emptyCapRequester, listOf(capStrategy, udpStrategy, restStrategy))

        val profile = profileManager.getAvatarProfile(targetAvatarId)
        assertNotNull("ProfileManager should fall back to REST strategy", profile)
        assertEquals("REST Fallback Avatar", profile?.displayName)
        assertEquals("Resolved after UDP timeout", profile?.aboutText)

        udpStrategy.dispose()
        profileManager.shutdown()
    }
}
