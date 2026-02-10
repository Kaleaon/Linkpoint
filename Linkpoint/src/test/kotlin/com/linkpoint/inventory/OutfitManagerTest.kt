package com.linkpoint.inventory

import android.util.Log
import com.linkpoint.avatar.AvatarBaker
import com.linkpoint.objects.ObjectManager
import com.linkpoint.protocol.messages.MessageIds
import com.linkpoint.protocol.messages.UDPConnectionFixed
import io.mockk.*
import kotlinx.coroutines.runBlocking
import org.junit.After
import org.junit.Assert.*
import org.junit.Before
import org.junit.Test
import java.nio.ByteBuffer
import java.nio.ByteOrder
import java.util.UUID

/**
 * Test for OutfitManager, specifically the RezSingleAttachmentFromInv packet implementation
 */
class OutfitManagerTest {

    private lateinit var inventoryManager: InventoryManager
    private lateinit var baker: AvatarBaker
    private lateinit var gestureManager: GestureManager
    private lateinit var udpConnection: UDPConnectionFixed
    private lateinit var objectManager: ObjectManager
    private lateinit var outfitManager: OutfitManager
    
    private val testAgentId = UUID.randomUUID()
    private val testSessionId = UUID.randomUUID()
    
    @Before
    fun setup() {
        // Mock Android Log
        mockkStatic(Log::class)
        every { Log.d(any(), any()) } returns 0
        every { Log.e(any(), any(), any()) } returns 0
        every { Log.w(any(), any()) } returns 0
        every { Log.i(any(), any()) } returns 0
        
        // Create mocks
        inventoryManager = mockk(relaxed = true)
        baker = mockk(relaxed = true)
        gestureManager = mockk(relaxed = true)
        udpConnection = mockk(relaxed = true)
        objectManager = mockk(relaxed = true)
        
        // Create OutfitManager with mocks
        outfitManager = OutfitManager(
            inventoryManager = inventoryManager,
            baker = baker,
            gestureManager = gestureManager,
            udpConnection = udpConnection,
            agentId = testAgentId,
            sessionId = testSessionId,
            objectManager = objectManager
        )
    }
    
    @After
    fun teardown() {
        unmockkAll()
    }
    
    @Test
    fun testAttachObject_sendsCorrectPacket() = runBlocking {
        // Arrange
        val testItemId = UUID.randomUUID()
        val testAssetId = UUID.randomUUID()
        val testParentId = UUID.randomUUID()
        val testOwnerId = UUID.randomUUID()
        val testCreatorId = UUID.randomUUID()
        
        val testItem = InventoryItem(
            itemId = testItemId,
            assetId = testAssetId,
            parentId = testParentId,
            name = "Test Attachment",
            description = "Test Description",
            assetType = 6, // OBJECT
            inventoryType = InventoryType.OBJECT,
            flags = 0x00000001, // Default attach point
            permissions = ItemPermissions(
                baseMask = 0x7FFFFFFF,
                ownerMask = 0x7FFFFFFF,
                groupMask = 0x00000000,
                everyoneMask = 0x00000000,
                nextOwnerMask = 0x00082000,
                ownerId = testOwnerId,
                creatorId = testCreatorId
            ),
            saleInfo = SaleInfo(
                saleType = 0,
                salePrice = 0
            ),
            creationDate = 1234567890
        )
        
        every { inventoryManager.getItem(testItemId) } returns testItem
        
        val capturedMessageId = slot<Int>()
        val capturedPayload = slot<ByteArray>()
        val capturedReliable = slot<Boolean>()
        
        coEvery { 
            udpConnection.sendPacket(
                capture(capturedMessageId),
                capture(capturedPayload),
                capture(capturedReliable)
            )
        } just Runs
        
        val attachPoint = 5 // LEFT_HAND
        val replace = true
        
        // Act
        val result = outfitManager.wearItem(testItemId, replace, attachPoint)
        
        // Assert
        assertTrue("wearItem should return true", result)
        
        // Verify the packet was sent
        verify { 
            udpConnection.sendPacket(
                MessageIds.REZ_SINGLE_ATTACHMENT_FROM_INV,
                any(),
                true
            )
        }
        
        // Verify message ID
        assertEquals(MessageIds.REZ_SINGLE_ATTACHMENT_FROM_INV, capturedMessageId.captured)
        
        // Verify reliable flag
        assertTrue(capturedReliable.captured)
        
        // Verify payload structure
        val payload = ByteBuffer.wrap(capturedPayload.captured).order(ByteOrder.LITTLE_ENDIAN)
        
        // Skip AgentID and SessionID (32 bytes)
        payload.position(32)
        
        // Verify ItemID
        val itemIdMsb = payload.long
        val itemIdLsb = payload.long
        val receivedItemId = UUID(itemIdMsb, itemIdLsb)
        assertEquals(testItemId, receivedItemId)
        
        // Verify OwnerID
        val ownerIdMsb = payload.long
        val ownerIdLsb = payload.long
        val receivedOwnerId = UUID(ownerIdMsb, ownerIdLsb)
        assertEquals(testOwnerId, receivedOwnerId)
        
        // Verify AttachmentPt (should be 5 for LEFT_HAND, no append flag since replace=true)
        val attachPt = payload.get().toInt() and 0xFF
        assertEquals(attachPoint, attachPt)
    }
    
    @Test
    fun testAttachObject_withAppendFlag() = runBlocking {
        // Arrange
        val testItemId = UUID.randomUUID()
        val testAssetId = UUID.randomUUID()
        val testParentId = UUID.randomUUID()
        val testOwnerId = UUID.randomUUID()
        val testCreatorId = UUID.randomUUID()
        
        val testItem = InventoryItem(
            itemId = testItemId,
            assetId = testAssetId,
            parentId = testParentId,
            name = "Test Attachment",
            description = "Test Description",
            assetType = 6,
            inventoryType = InventoryType.OBJECT,
            flags = 0x00000001,
            permissions = ItemPermissions(
                baseMask = 0x7FFFFFFF,
                ownerMask = 0x7FFFFFFF,
                groupMask = 0x00000000,
                everyoneMask = 0x00000000,
                nextOwnerMask = 0x00082000,
                ownerId = testOwnerId,
                creatorId = testCreatorId
            ),
            saleInfo = SaleInfo(
                saleType = 0,
                salePrice = 0
            ),
            creationDate = 1234567890
        )
        
        every { inventoryManager.getItem(testItemId) } returns testItem
        
        val capturedPayload = slot<ByteArray>()
        
        coEvery { 
            udpConnection.sendPacket(any(), capture(capturedPayload), any())
        } just Runs
        
        val attachPoint = 5 // LEFT_HAND
        val replace = false // This should set the append flag
        
        // Act
        val result = outfitManager.wearItem(testItemId, replace, attachPoint)
        
        // Assert
        assertTrue(result)
        
        // Verify payload has append flag
        val payload = ByteBuffer.wrap(capturedPayload.captured).order(ByteOrder.LITTLE_ENDIAN)
        
        // Skip to AttachmentPt field (32 + 16 + 16 = 64 bytes)
        payload.position(64)
        
        // Verify AttachmentPt has 0x80 flag set (append mode)
        val attachPt = payload.get().toInt() and 0xFF
        val expectedWithFlag = attachPoint or 0x80
        assertEquals(expectedWithFlag, attachPt)
    }
    
    @Test
    fun testAttachObject_withLongName() = runBlocking {
        // Arrange - Create item with name > 255 bytes
        val longName = "A".repeat(300)
        val testItem = createTestItem(name = longName)
        
        every { inventoryManager.getItem(testItem.itemId) } returns testItem
        
        val capturedPayload = slot<ByteArray>()
        
        coEvery { 
            udpConnection.sendPacket(any(), capture(capturedPayload), any())
        } just Runs
        
        // Act
        val result = outfitManager.wearItem(testItem.itemId, true, 1)
        
        // Assert
        assertTrue(result)
        
        // Verify name was truncated to 255 bytes
        val payload = ByteBuffer.wrap(capturedPayload.captured).order(ByteOrder.LITTLE_ENDIAN)
        
        // Skip to Name field (32 + 16 + 16 + 1 + 4 + 4 + 4 + 4 = 81 bytes)
        payload.position(81)
        
        val nameLength = payload.get().toInt() and 0xFF
        assertEquals(255, nameLength)
    }
    
    private fun createTestItem(
        name: String = "Test",
        description: String = "Test"
    ): InventoryItem {
        return InventoryItem(
            itemId = UUID.randomUUID(),
            assetId = UUID.randomUUID(),
            parentId = UUID.randomUUID(),
            name = name,
            description = description,
            assetType = 6,
            inventoryType = InventoryType.OBJECT,
            flags = 0x00000001,
            permissions = ItemPermissions(
                baseMask = 0x7FFFFFFF,
                ownerMask = 0x7FFFFFFF,
                groupMask = 0x00000000,
                everyoneMask = 0x00000000,
                nextOwnerMask = 0x00082000,
                ownerId = UUID.randomUUID(),
                creatorId = UUID.randomUUID()
            ),
            saleInfo = SaleInfo(
                saleType = 0,
                salePrice = 0
            ),
            creationDate = 1234567890
        )
    }
}
