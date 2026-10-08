package com.linkpoint.world

import com.linkpoint.media.InWorldMediaNavigationService
import com.linkpoint.protocol.capabilities.CapabilityManager
import com.linkpoint.protocol.capabilities.FakeCapabilityRequester
import com.linkpoint.protocol.llsd.LLSDMap
import kotlinx.coroutines.runBlocking
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test
import java.util.UUID

class DeferredCapabilityConsumersTest {

    @Test
    fun `moap navigation routes through ObjectMediaNavigate capability`() = runBlocking {
        val fake = FakeCapabilityRequester().apply {
            enqueueResponse(CapabilityManager.CAP_OBJECT_MEDIA_NAVIGATE, LLSDMap())
        }
        val mediaNavigation = InWorldMediaNavigationService(fake)
        val objectId = UUID.fromString("00000000-0000-0000-0000-000000000101")

        val ok = mediaNavigation.navigateSurface(objectId = objectId, face = 2, url = "https://example.com/media")

        assertTrue(ok)
        val call = fake.invocations.single()
        assertEquals(CapabilityManager.CAP_OBJECT_MEDIA_NAVIGATE, call.capName)
        val body = call.body as LLSDMap
        assertEquals(objectId, body.getUUID("object_id"))
        assertEquals(2, body.getInt("face"))
        assertEquals("https://example.com/media", body.getString("current_url"))
        assertEquals(true, body.getBoolean("navigate"))
    }

    @Test
    fun `region experience fetch uses RegionExperiences capability with fetch op`() = runBlocking {
        val fake = FakeCapabilityRequester().apply {
            enqueueResponse(CapabilityManager.CAP_REGION_EXPERIENCE, LLSDMap())
        }
        val manager = RegionExperienceManager(fake)
        val regionId = UUID.fromString("00000000-0000-0000-0000-000000000202")

        manager.fetchRegionExperiences(regionId)

        val call = fake.invocations.single()
        assertEquals(CapabilityManager.CAP_REGION_EXPERIENCE, call.capName)
        val body = call.body as LLSDMap
        assertEquals("get", body.getString("op"))
        assertEquals(regionId, body.getUUID("region_id"))
    }

    @Test
    fun `region experience permission submit uses RegionExperiences capability`() = runBlocking {
        val fake = FakeCapabilityRequester().apply {
            enqueueResponse(CapabilityManager.CAP_REGION_EXPERIENCE, LLSDMap())
        }
        val manager = RegionExperienceManager(fake)
        val experienceId = UUID.fromString("00000000-0000-0000-0000-000000000303")

        val ok = manager.submitExperiencePermission(
            experienceId = experienceId,
            permissionToken = "perm-token-1",
            allow = true
        )

        assertTrue(ok)
        val call = fake.invocations.single()
        assertEquals(CapabilityManager.CAP_REGION_EXPERIENCE, call.capName)
        val body = call.body as LLSDMap
        assertEquals("permission", body.getString("op"))
        assertEquals(experienceId, body.getUUID("experience_id"))
        assertEquals("perm-token-1", body.getString("permission_token"))
        assertEquals(true, body.getBoolean("allow"))
    }
}
