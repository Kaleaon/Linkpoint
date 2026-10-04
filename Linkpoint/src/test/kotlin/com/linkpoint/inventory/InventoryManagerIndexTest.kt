package com.linkpoint.inventory

import com.linkpoint.protocol.capabilities.CapabilityManager
import com.linkpoint.protocol.llsd.LLSDArray
import com.linkpoint.protocol.llsd.LLSDInteger
import com.linkpoint.protocol.llsd.LLSDMap
import com.linkpoint.protocol.llsd.LLSDString
import com.linkpoint.protocol.messages.UDPConnectionFixed
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.runBlocking
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.kotlin.mock
import org.robolectric.RobolectricTestRunner
import java.util.UUID

@RunWith(RobolectricTestRunner::class)
class InventoryManagerIndexTest {

    private lateinit var capabilityManager: CapabilityManager
    private lateinit var udpConnection: UDPConnectionFixed
    private lateinit var agentId: UUID
    private lateinit var inventoryManager: InventoryManager

    @Before
    fun setUp() {
        capabilityManager = CapabilityManager()
        udpConnection = UDPConnectionFixed()
        agentId = UUID.randomUUID()
        inventoryManager = InventoryManager(
            capabilityManager = capabilityManager,
            udpConnection = udpConnection,
            agentId = agentId
        )
    }

    @Test
    fun `index maps populate during addFolderFromLogin and parseInventoryResponse`() {
        val rootId = UUID.randomUUID()
        val folderId = UUID.randomUUID()

        // 1. Add folder from login
        inventoryManager.addFolderFromLogin(
            folderId = folderId,
            parentId = rootId,
            name = "Test Subfolder",
            typeDefault = 0,
            version = 1
        )

        assertTrue(
            "parentToFoldersMap must contain folderId under rootId",
            inventoryManager.parentToFoldersMap[rootId]?.contains(folderId) == true
        )

        // 2. Parse inventory response with LLSDMap
        val itemId1 = UUID.randomUUID()
        val itemId2 = UUID.randomUUID()
        val childFolderId = UUID.randomUUID()

        val response = LLSDMap().apply {
            this["folders"] = LLSDArray().apply {
                add(LLSDMap().apply {
                    this["categories"] = LLSDArray().apply {
                        add(LLSDMap().apply {
                            this["category_id"] = LLSDString(childFolderId.toString())
                            this["parent_id"] = LLSDString(folderId.toString())
                            this["name"] = LLSDString("Child Folder")
                            this["type_default"] = LLSDInteger(-1)
                            this["version"] = LLSDInteger(1)
                        })
                    }
                    this["items"] = LLSDArray().apply {
                        add(LLSDMap().apply {
                            this["item_id"] = LLSDString(itemId1.toString())
                            this["parent_id"] = LLSDString(folderId.toString())
                            this["name"] = LLSDString("Item 1")
                            this["type"] = LLSDInteger(0)
                        })
                        add(LLSDMap().apply {
                            this["item_id"] = LLSDString(itemId2.toString())
                            this["parent_id"] = LLSDString(folderId.toString())
                            this["name"] = LLSDString("Item 2")
                            this["type"] = LLSDInteger(1)
                        })
                    }
                })
            }
        }

        // Invoke private parseInventoryResponse via reflection or public method that uses putFolder/putItem
        val parseMethod = InventoryManager::class.java.getDeclaredMethod("parseInventoryResponse", LLSDMap::class.java)
        parseMethod.isAccessible = true
        parseMethod.invoke(inventoryManager, response)

        assertTrue(
            "parentToFoldersMap must contain childFolderId under folderId",
            inventoryManager.parentToFoldersMap[folderId]?.contains(childFolderId) == true
        )
        assertTrue(
            "parentToItemsMap must contain itemId1 under folderId",
            inventoryManager.parentToItemsMap[folderId]?.contains(itemId1) == true
        )
        assertTrue(
            "parentToItemsMap must contain itemId2 under folderId",
            inventoryManager.parentToItemsMap[folderId]?.contains(itemId2) == true
        )
    }

    @Test
    fun `queries use direct parent key lookups and return expected items and folders`() {
        val parentId = UUID.randomUUID()
        val otherParentId = UUID.randomUUID()

        val folder1 = InventoryFolder(UUID.randomUUID(), parentId, "Folder A", -1, 0)
        val folder2 = InventoryFolder(UUID.randomUUID(), parentId, "Folder B", -1, 0)
        val otherFolder = InventoryFolder(UUID.randomUUID(), otherParentId, "Folder C", -1, 0)

        val item1 = InventoryItem(
            itemId = UUID.randomUUID(),
            assetId = UUID.randomUUID(),
            parentId = parentId,
            name = "Item 1",
            description = "",
            assetType = 0,
            inventoryType = 0,
            flags = 0,
            permissions = ItemPermissions(0, 0, 0, 0, 0, agentId, agentId),
            saleInfo = SaleInfo(0, 0),
            creationDate = 0
        )
        val item2 = InventoryItem(
            itemId = UUID.randomUUID(),
            assetId = UUID.randomUUID(),
            parentId = parentId,
            name = "Item 2",
            description = "",
            assetType = 1,
            inventoryType = 1,
            flags = 0,
            permissions = ItemPermissions(0, 0, 0, 0, 0, agentId, agentId),
            saleInfo = SaleInfo(0, 0),
            creationDate = 0
        )

        inventoryManager.putFolder(folder1)
        inventoryManager.putFolder(folder2)
        inventoryManager.putFolder(otherFolder)
        inventoryManager.putItem(item1)
        inventoryManager.putItem(item2)

        val retrievedFolders = inventoryManager.getFolders(parentId)
        assertEquals(2, retrievedFolders.size)
        assertEquals(listOf("Folder A", "Folder B"), retrievedFolders.map { it.name })

        val retrievedItems = inventoryManager.getItems(parentId)
        assertEquals(2, retrievedItems.size)
        assertEquals(listOf("Item 1", "Item 2"), retrievedItems.map { it.name })

        val contents = inventoryManager.getFolderContents(parentId)
        assertEquals(4, contents.size)
        // Subfolders appear first, then items
        assertTrue(contents[0] is InventoryNode.Folder)
        assertTrue(contents[1] is InventoryNode.Folder)
        assertTrue(contents[2] is InventoryNode.Item)
        assertTrue(contents[3] is InventoryNode.Item)
    }

    @Test
    fun `item and folder moves update parent index sets atomically`() = runBlocking {
        val oldParentId = UUID.randomUUID()
        val newParentId = UUID.randomUUID()

        val folder = InventoryFolder(UUID.randomUUID(), oldParentId, "Move Folder", -1, 0)
        val item = InventoryItem(
            itemId = UUID.randomUUID(),
            assetId = UUID.randomUUID(),
            parentId = oldParentId,
            name = "Move Item",
            description = "",
            assetType = 0,
            inventoryType = 0,
            flags = 0,
            permissions = ItemPermissions(0, 0, 0, 0, 0, agentId, agentId),
            saleInfo = SaleInfo(0, 0),
            creationDate = 0
        )

        inventoryManager.putFolder(folder)
        inventoryManager.putItem(item)

        assertTrue(inventoryManager.parentToFoldersMap[oldParentId]?.contains(folder.folderId) == true)
        assertTrue(inventoryManager.parentToItemsMap[oldParentId]?.contains(item.itemId) == true)

        // Move item
        inventoryManager.moveItem(item.itemId, newParentId)

        assertFalse(
            "oldParentId must no longer contain moved item",
            inventoryManager.parentToItemsMap[oldParentId]?.contains(item.itemId) == true
        )
        assertTrue(
            "newParentId must contain moved item",
            inventoryManager.parentToItemsMap[newParentId]?.contains(item.itemId) == true
        )
        assertEquals(newParentId, inventoryManager.getItem(item.itemId)?.parentId)

        // Move folder
        inventoryManager.moveFolder(folder.folderId, newParentId)

        assertFalse(
            "oldParentId must no longer contain moved folder",
            inventoryManager.parentToFoldersMap[oldParentId]?.contains(folder.folderId) == true
        )
        assertTrue(
            "newParentId must contain moved folder",
            inventoryManager.parentToFoldersMap[newParentId]?.contains(folder.folderId) == true
        )
        assertEquals(newParentId, inventoryManager.getFolder(folder.folderId)?.parentId)
    }

    @Test
    fun `item and folder removals update entity maps and index sets`() {
        val parentId = UUID.randomUUID()
        val folder = InventoryFolder(UUID.randomUUID(), parentId, "Remove Folder", -1, 0)
        val item = InventoryItem(
            itemId = UUID.randomUUID(),
            assetId = UUID.randomUUID(),
            parentId = parentId,
            name = "Remove Item",
            description = "",
            assetType = 0,
            inventoryType = 0,
            flags = 0,
            permissions = ItemPermissions(0, 0, 0, 0, 0, agentId, agentId),
            saleInfo = SaleInfo(0, 0),
            creationDate = 0
        )

        inventoryManager.putFolder(folder)
        inventoryManager.putItem(item)

        // Remove item
        inventoryManager.removeItem(item.itemId)
        assertNull(inventoryManager.getItem(item.itemId))
        assertFalse(inventoryManager.parentToItemsMap[parentId]?.contains(item.itemId) == true)

        // Remove folder
        inventoryManager.removeFolder(folder.folderId)
        assertNull(inventoryManager.getFolder(folder.folderId))
        assertFalse(inventoryManager.parentToFoldersMap[parentId]?.contains(folder.folderId) == true)
        assertNull(inventoryManager.parentToFoldersMap[folder.folderId])
        assertNull(inventoryManager.parentToItemsMap[folder.folderId])
    }

    @Test
    fun `concurrent additions moves and deletions maintain index integrity`() = runBlocking {
        val rootId = UUID.randomUUID()
        val targetId = UUID.randomUUID()

        val addJobs = (1..100).map { i ->
            async(Dispatchers.Default) {
                val folderId = UUID.randomUUID()
                val itemId = UUID.randomUUID()

                val folder = InventoryFolder(folderId, rootId, "Concurrent Folder $i", -1, 0)
                val item = InventoryItem(
                    itemId = itemId,
                    assetId = UUID.randomUUID(),
                    parentId = rootId,
                    name = "Concurrent Item $i",
                    description = "",
                    assetType = 0,
                    inventoryType = 0,
                    flags = 0,
                    permissions = ItemPermissions(0, 0, 0, 0, 0, agentId, agentId),
                    saleInfo = SaleInfo(0, 0),
                    creationDate = 0
                )

                inventoryManager.putFolder(folder)
                inventoryManager.putItem(item)

                // Move half of them
                if (i % 2 == 0) {
                    inventoryManager.moveItem(itemId, targetId)
                    inventoryManager.moveFolder(folderId, targetId)
                }

                // Delete a quarter of them
                if (i % 4 == 0) {
                    inventoryManager.removeItem(itemId)
                    inventoryManager.removeFolder(folderId)
                }
            }
        }

        addJobs.awaitAll()

        // Validate index consistency
        val rootFolders = inventoryManager.getFolders(rootId)
        val rootItems = inventoryManager.getItems(rootId)
        val targetFolders = inventoryManager.getFolders(targetId)
        val targetItems = inventoryManager.getItems(targetId)

        rootFolders.forEach { f ->
            assertEquals(rootId, f.parentId)
            assertTrue(inventoryManager.parentToFoldersMap[rootId]?.contains(f.folderId) == true)
        }
        rootItems.forEach { item ->
            assertEquals(rootId, item.parentId)
            assertTrue(inventoryManager.parentToItemsMap[rootId]?.contains(item.itemId) == true)
        }
        targetFolders.forEach { f ->
            assertEquals(targetId, f.parentId)
            assertTrue(inventoryManager.parentToFoldersMap[targetId]?.contains(f.folderId) == true)
        }
        targetItems.forEach { item ->
            assertEquals(targetId, item.parentId)
            assertTrue(inventoryManager.parentToItemsMap[targetId]?.contains(item.itemId) == true)
        }
    }
}
