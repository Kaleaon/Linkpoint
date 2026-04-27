package com.linkpoint.inventory.notecard

import com.linkpoint.protocol.capabilities.CapabilityManager
import com.linkpoint.protocol.capabilities.FakeCapabilityRequester
import com.linkpoint.protocol.llsd.LLSDMap
import kotlinx.coroutines.runBlocking
import okhttp3.OkHttpClient
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test
import java.util.UUID

class NotecardManagerCapabilityRequestTest {

    @Test
    fun `save task notecard routes through task capability with task id`() = runBlocking {
        val fake = FakeCapabilityRequester().apply {
            enqueueResponse(CapabilityManager.CAP_UPDATE_NOTECARD_TASK, LLSDMap())
        }
        val manager = NotecardManager(null, fake, OkHttpClient())
        val itemId = UUID.fromString("00000000-0000-0000-0000-000000000010")
        val taskId = UUID.fromString("00000000-0000-0000-0000-000000000011")

        manager.saveNotecard(itemId, "hello", taskId)

        val call = fake.invocations.single()
        assertEquals(CapabilityManager.CAP_UPDATE_NOTECARD_TASK, call.capName)
        val body = call.body as LLSDMap
        assertEquals(itemId, body.getUUID("item_id"))
        assertEquals(taskId, body.getUUID("task_id"))
    }

    @Test
    fun `copy inventory from notecard sends expected ids`() = runBlocking {
        val fake = FakeCapabilityRequester().apply {
            enqueueResponse(CapabilityManager.CAP_COPY_INVENTORY_FROM_NOTECARD, LLSDMap())
        }
        val manager = NotecardManager(null, fake, OkHttpClient())

        val ok = manager.copyInventoryFromNotecard(
            notecardItemId = UUID.fromString("00000000-0000-0000-0000-000000000020"),
            objectId = UUID.fromString("00000000-0000-0000-0000-000000000021"),
            destinationFolderId = UUID.fromString("00000000-0000-0000-0000-000000000022"),
            embeddedItemId = UUID.fromString("00000000-0000-0000-0000-000000000023")
        )

        assertTrue(ok)
        val call = fake.invocations.single()
        assertEquals(CapabilityManager.CAP_COPY_INVENTORY_FROM_NOTECARD, call.capName)
        val body = call.body as LLSDMap
        assertEquals(UUID.fromString("00000000-0000-0000-0000-000000000020"), body.getUUID("notecard-id"))
        assertEquals(UUID.fromString("00000000-0000-0000-0000-000000000022"), body.getUUID("folder-id"))
    }

    @Test
    fun `move inventory item sends items array payload`() = runBlocking {
        val fake = FakeCapabilityRequester().apply {
            enqueueResponse(CapabilityManager.CAP_MOVE_INVENTORY_ITEM, LLSDMap())
        }
        val manager = NotecardManager(null, fake, OkHttpClient())

        val ok = manager.moveInventoryItem(
            itemId = UUID.fromString("00000000-0000-0000-0000-000000000030"),
            destinationFolderId = UUID.fromString("00000000-0000-0000-0000-000000000031")
        )

        assertTrue(ok)
        val call = fake.invocations.single()
        assertEquals(CapabilityManager.CAP_MOVE_INVENTORY_ITEM, call.capName)
        val body = call.body as LLSDMap
        val items = body.getArray("items")
        val first = items?.get(0) as LLSDMap
        assertEquals(UUID.fromString("00000000-0000-0000-0000-000000000030"), first.getUUID("item_id"))
    }
}
