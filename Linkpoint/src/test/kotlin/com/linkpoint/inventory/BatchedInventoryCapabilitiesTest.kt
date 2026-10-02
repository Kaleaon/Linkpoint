package com.linkpoint.inventory

import org.junit.runner.RunWith
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.linkpoint.protocol.capabilities.CapabilityManager
import com.linkpoint.protocol.messages.UDPConnectionFixed
import com.linkpoint.protocol.llsd.*
import kotlinx.coroutines.runBlocking
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertTrue
import org.junit.Test
import java.util.UUID

@RunWith(AndroidJUnit4::class)
class BatchedInventoryCapabilitiesTest {

    private class RecordingCapabilityManager : CapabilityManager() {
        data class CapRequest(val capName: String, val body: LLSDValue?)
        val requests = mutableListOf<CapRequest>()
        var mockResponse: LLSDValue? = null

        override suspend fun request(capName: String, body: LLSDValue?): LLSDValue? {
            requests.add(CapRequest(capName, body))
            return mockResponse
        }

        override fun getCapability(name: String): String? = "https://sim.example.com/cap/$name"
        override fun hasCapability(name: String): Boolean = true
    }

    @Test
    fun `fetchBatchFolderContents sends multiple folder descriptors in 1 capability request`() = runBlocking {
        val capManager = RecordingCapabilityManager()
        val agentId = UUID.randomUUID()
        val inventoryManager = InventoryManager(capManager, UDPConnectionFixed(), agentId)

        val folder1 = UUID.randomUUID()
        val folder2 = UUID.randomUUID()
        val folder3 = UUID.randomUUID()

        val item1Id = UUID.randomUUID()
        val item2Id = UUID.randomUUID()

        // Prepare mock response for the 3 folders
        capManager.mockResponse = LLSDMap().apply {
            this["folders"] = LLSDArray().apply {
                add(LLSDMap().apply {
                    this["categories"] = LLSDArray().apply {
                        add(LLSDMap().apply {
                            this["category_id"] = LLSDString(folder2.toString())
                            this["parent_id"] = LLSDString(folder1.toString())
                            this["name"] = LLSDString("SubFolder 2")
                            this["type_default"] = LLSDInteger(-1)
                            this["version"] = LLSDInteger(1)
                        })
                    }
                    this["items"] = LLSDArray().apply {
                        add(LLSDMap().apply {
                            this["item_id"] = LLSDString(item1Id.toString())
                            this["parent_id"] = LLSDString(folder1.toString())
                            this["name"] = LLSDString("Test Item 1")
                            this["type"] = LLSDInteger(0)
                        })
                        add(LLSDMap().apply {
                            this["item_id"] = LLSDString(item2Id.toString())
                            this["parent_id"] = LLSDString(folder3.toString())
                            this["name"] = LLSDString("Test Item 2")
                            this["type"] = LLSDInteger(0)
                        })
                    }
                })
            }
        }

        val success = inventoryManager.fetchBatchFolderContents(
            listOf(folder1, folder2, folder3),
            fetchFolders = false,
            fetchItems = true
        )

        assertTrue(success)
        assertEquals(1, capManager.requests.size)

        val req = capManager.requests.single()
        assertEquals(CapabilityManager.CAP_FETCH_INVENTORY_DESCENDENTS, req.capName)

        val bodyMap = req.body as LLSDMap
        val foldersArray = bodyMap.getArray("folders") as LLSDArray
        assertEquals(3, foldersArray.value.size)

        val requestedFolderIds = foldersArray.value.map { (it as LLSDMap).getString("folder_id") }
        assertEquals(listOf(folder1.toString(), folder2.toString(), folder3.toString()), requestedFolderIds)

        // Verify items and folders are cached in InventoryManager
        assertNotNull(inventoryManager.getFolder(folder2))
        assertNotNull(inventoryManager.getItem(item1Id))
        assertNotNull(inventoryManager.getItem(item2Id))
    }

    @Test
    fun `warmFetch sends 1 batched capability request for priority system folders`() = runBlocking {
        val capManager = RecordingCapabilityManager()
        val agentId = UUID.randomUUID()
        val inventoryManager = InventoryManager(capManager, UDPConnectionFixed(), agentId)

        val outfitFolder = UUID.randomUUID()
        val favoritesFolder = UUID.randomUUID()
        val landmarkFolder = UUID.randomUUID()
        val myOutfitsFolder = UUID.randomUUID()
        val inboxFolder = UUID.randomUUID()

        inventoryManager.registerSystemFolder(InventoryManager.FOLDER_TYPE_CURRENT_OUTFIT, outfitFolder)
        inventoryManager.registerSystemFolder(InventoryManager.FOLDER_TYPE_FAVORITES, favoritesFolder)
        inventoryManager.registerSystemFolder(InventoryManager.FOLDER_TYPE_LANDMARK, landmarkFolder)
        inventoryManager.registerSystemFolder(InventoryManager.FOLDER_TYPE_MYOUTFITS, myOutfitsFolder)
        inventoryManager.registerSystemFolder(InventoryManager.FOLDER_TYPE_INBOX, inboxFolder)

        capManager.mockResponse = LLSDMap().apply {
            this["folders"] = LLSDArray()
        }

        inventoryManager.warmFetch()

        // Give the coroutine launched on internal scope a brief moment to run
        kotlinx.coroutines.delay(100)

        assertEquals(1, capManager.requests.size)
        val req = capManager.requests.single()
        assertEquals(CapabilityManager.CAP_FETCH_INVENTORY_DESCENDENTS, req.capName)

        val bodyMap = req.body as LLSDMap
        val foldersArray = bodyMap.getArray("folders") as LLSDArray
        assertEquals(5, foldersArray.value.size)

        val requestedFolderIds = foldersArray.value.map { (it as LLSDMap).getString("folder_id") }
        val expectedFolderIds = listOf(
            outfitFolder.toString(),
            favoritesFolder.toString(),
            landmarkFolder.toString(),
            myOutfitsFolder.toString(),
            inboxFolder.toString()
        )
        assertEquals(expectedFolderIds, requestedFolderIds)
    }

    @Test
    fun `single fetchFolderContents delegates to batched request`() = runBlocking {
        val capManager = RecordingCapabilityManager()
        val agentId = UUID.randomUUID()
        val inventoryManager = InventoryManager(capManager, UDPConnectionFixed(), agentId)

        val folderId = UUID.randomUUID()
        capManager.mockResponse = LLSDMap().apply {
            this["folders"] = LLSDArray()
        }

        val success = inventoryManager.fetchFolderContents(folderId)

        assertTrue(success)
        assertEquals(1, capManager.requests.size)

        val req = capManager.requests.single()
        assertEquals(CapabilityManager.CAP_FETCH_INVENTORY_DESCENDENTS, req.capName)

        val bodyMap = req.body as LLSDMap
        val foldersArray = bodyMap.getArray("folders") as LLSDArray
        assertEquals(1, foldersArray.value.size)
        assertEquals(folderId.toString(), (foldersArray.value[0] as LLSDMap).getString("folder_id"))
    }

    @Test
    fun `fetchBatchFolderContents with empty list returns true without network call`() = runBlocking {
        val capManager = RecordingCapabilityManager()
        val agentId = UUID.randomUUID()
        val inventoryManager = InventoryManager(capManager, UDPConnectionFixed(), agentId)

        val success = inventoryManager.fetchBatchFolderContents(emptyList())

        assertTrue(success)
        assertEquals(0, capManager.requests.size)
    }
}
