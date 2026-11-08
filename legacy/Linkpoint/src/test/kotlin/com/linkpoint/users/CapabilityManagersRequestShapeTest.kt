package com.linkpoint.users

import org.junit.runner.RunWith
import androidx.test.ext.junit.runners.AndroidJUnit4

import com.linkpoint.protocol.capabilities.CapabilityManager
import com.linkpoint.protocol.capabilities.FakeCapabilityRequester
import com.linkpoint.protocol.llsd.LLSDArray
import com.linkpoint.protocol.llsd.LLSDMap
import com.linkpoint.protocol.llsd.LLSDString
import com.linkpoint.render.RenderMaterialsManager
import com.linkpoint.world.SimulatorFeaturesManager
import kotlinx.coroutines.runBlocking
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test
import java.util.UUID

@RunWith(AndroidJUnit4::class)
class CapabilityManagersRequestShapeTest {

    @Test
    fun `set display name sends expected payload`() = runBlocking {
        val fake = FakeCapabilityRequester()
        fake.enqueueResponse(CapabilityManager.CAP_SET_DISPLAY_NAME, LLSDMap())
        val manager = DisplayNameManager(fake)

        val ok = manager.setDisplayName("Test User")

        assertTrue(ok)
        val call = fake.invocations.single()
        assertEquals(CapabilityManager.CAP_SET_DISPLAY_NAME, call.capName)
        val body = call.body as LLSDMap
        assertEquals("Test User", body.getString("display_name"))
    }

    @Test
    fun `simulator features fetch includes region flags`() = runBlocking {
        val fake = FakeCapabilityRequester()
        fake.enqueueResponse(CapabilityManager.CAP_SIMULATOR_FEATURES, LLSDMap())
        val manager = SimulatorFeaturesManager(fake)

        manager.fetchSimulatorFeatures(regionFlags = 0x2000)

        val call = fake.invocations.single()
        assertEquals(CapabilityManager.CAP_SIMULATOR_FEATURES, call.capName)
        val body = call.body as LLSDMap
        assertEquals(0x2000, body.getInt("region_flags"))
    }

    @Test
    fun `agent preferences update sends expected booleans`() = runBlocking {
        val fake = FakeCapabilityRequester()
        fake.enqueueResponse(CapabilityManager.CAP_AGENT_PREFERENCES, LLSDMap())
        val manager = AgentPreferencesManager(fake)

        val ok = manager.updatePreferences(sendImToEmail = true, visibleInSearch = false)

        assertTrue(ok)
        val call = fake.invocations.single()
        assertEquals(CapabilityManager.CAP_AGENT_PREFERENCES, call.capName)
        val body = call.body as LLSDMap
        assertEquals(true, body.getBoolean("send_im_to_email"))
        assertEquals(false, body.getBoolean("visible_in_search"))
    }

    @Test
    fun `agent language update sends language code`() = runBlocking {
        val fake = FakeCapabilityRequester()
        fake.enqueueResponse(CapabilityManager.CAP_UPDATE_AGENT_LANGUAGE, LLSDMap())
        val manager = AgentPreferencesManager(fake)

        val ok = manager.updateLanguage("en")

        assertTrue(ok)
        val call = fake.invocations.single()
        assertEquals(CapabilityManager.CAP_UPDATE_AGENT_LANGUAGE, call.capName)
        val body = call.body as LLSDMap
        assertEquals("en", body.getString("language"))
    }

    @Test
    fun `render materials fetch sends object ids array`() = runBlocking {
        val fake = FakeCapabilityRequester()
        fake.enqueueResponse(CapabilityManager.CAP_RENDER_MATERIALS, LLSDMap())
        val manager = RenderMaterialsManager(fake)
        val objectId = UUID.fromString("00000000-0000-0000-0000-000000000001")

        manager.fetchRenderMaterials(listOf(objectId))

        val call = fake.invocations.single()
        assertEquals(CapabilityManager.CAP_RENDER_MATERIALS, call.capName)
        val body = call.body as LLSDMap
        val ids = body.getArray("object_ids") as LLSDArray
        assertEquals(objectId.toString(), (ids[0] as LLSDString).value)
    }
}
