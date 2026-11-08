package com.linkpoint.inventory

import com.linkpoint.protocol.capabilities.CapabilityManager
import com.linkpoint.protocol.llsd.LLSDMap
import com.linkpoint.protocol.messages.MessageIdRegistry
import com.linkpoint.protocol.messages.UDPConnectionFixed
import kotlinx.coroutines.runBlocking
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.kotlin.any
import org.mockito.kotlin.anyOrNull
import org.mockito.kotlin.eq
import org.mockito.kotlin.mock
import org.mockito.kotlin.never
import org.mockito.kotlin.verify
import org.mockito.kotlin.whenever
import org.robolectric.RobolectricTestRunner
import java.util.UUID

@RunWith(RobolectricTestRunner::class)
class InventoryManagerTest {

    private lateinit var capabilityManager: CapabilityManager
    private lateinit var udpConnection: UDPConnectionFixed
    private lateinit var agentId: UUID
    private lateinit var inventoryManager: InventoryManager

    private val sampleItemId = UUID.randomUUID()
    private val oldParentId = UUID.randomUUID()
    private val newParentId = UUID.randomUUID()

    @Before
    fun setUp() {
        capabilityManager = mock()
        udpConnection = mock()
        agentId = UUID.randomUUID()

        whenever(udpConnection.getSessionId()).thenReturn(UUID.randomUUID())
        whenever(udpConnection.getCircuitCode()).thenReturn(1001)

        inventoryManager = InventoryManager(
            capabilityManager = capabilityManager,
            udpConnection = udpConnection,
            agentId = agentId
        )

        // Seed an item into inventoryManager cache
        inventoryManager.putItem(
            InventoryItem(
                itemId = sampleItemId,
                assetId = UUID.randomUUID(),
                parentId = oldParentId,
                name = "Test Item",
                description = "Test Description",
                assetType = 0,
                inventoryType = 0,
                flags = 0,
                permissions = ItemPermissions(
                    baseMask = 0,
                    ownerMask = 0,
                    groupMask = 0,
                    everyoneMask = 0,
                    nextOwnerMask = 0,
                    ownerId = agentId,
                    creatorId = agentId
                ),
                saleInfo = SaleInfo(saleType = 0, salePrice = 0),
                creationDate = 0
            )
        )
    }

    @Test
    fun `moveItem uses AISv3 when available and successful`() = runBlocking {
        whenever(capabilityManager.hasCapability(CapabilityManager.CAP_INVENTORY_API)).thenReturn(true)
        whenever(capabilityManager.request(eq(CapabilityManager.CAP_INVENTORY_API), anyOrNull())).thenReturn(LLSDMap())

        val result = inventoryManager.moveItem(sampleItemId, newParentId)

        assertTrue(result)
        verify(capabilityManager).request(eq(CapabilityManager.CAP_INVENTORY_API), anyOrNull())
        verify(capabilityManager, never()).request(eq(CapabilityManager.CAP_MOVE_INVENTORY_ITEM), anyOrNull())
        verify(udpConnection, never()).sendPacket(any(), any(), any(), any(), anyOrNull())

        val updatedItem = inventoryManager.getItem(sampleItemId)
        assertEquals(newParentId, updatedItem?.parentId)
    }

    @Test
    fun `moveItem falls back to CAP_MOVE_INVENTORY_ITEM when AISv3 is absent`() = runBlocking {
        whenever(capabilityManager.hasCapability(CapabilityManager.CAP_INVENTORY_API)).thenReturn(false)
        whenever(capabilityManager.hasCapability(CapabilityManager.CAP_MOVE_INVENTORY_ITEM)).thenReturn(true)
        whenever(capabilityManager.request(eq(CapabilityManager.CAP_MOVE_INVENTORY_ITEM), anyOrNull())).thenReturn(LLSDMap())

        val result = inventoryManager.moveItem(sampleItemId, newParentId)

        assertTrue(result)
        verify(capabilityManager, never()).request(eq(CapabilityManager.CAP_INVENTORY_API), anyOrNull())
        verify(capabilityManager).request(eq(CapabilityManager.CAP_MOVE_INVENTORY_ITEM), anyOrNull())
        verify(udpConnection, never()).sendPacket(any(), any(), any(), any(), anyOrNull())

        val updatedItem = inventoryManager.getItem(sampleItemId)
        assertEquals(newParentId, updatedItem?.parentId)
    }

    @Test
    fun `moveItem falls back to CAP_MOVE_INVENTORY_ITEM when AISv3 request fails`() = runBlocking {
        whenever(capabilityManager.hasCapability(CapabilityManager.CAP_INVENTORY_API)).thenReturn(true)
        whenever(capabilityManager.request(eq(CapabilityManager.CAP_INVENTORY_API), anyOrNull())).thenReturn(null)
        whenever(capabilityManager.hasCapability(CapabilityManager.CAP_MOVE_INVENTORY_ITEM)).thenReturn(true)
        whenever(capabilityManager.request(eq(CapabilityManager.CAP_MOVE_INVENTORY_ITEM), anyOrNull())).thenReturn(LLSDMap())

        val result = inventoryManager.moveItem(sampleItemId, newParentId)

        assertTrue(result)
        verify(capabilityManager).request(eq(CapabilityManager.CAP_INVENTORY_API), anyOrNull())
        verify(capabilityManager).request(eq(CapabilityManager.CAP_MOVE_INVENTORY_ITEM), anyOrNull())
        verify(udpConnection, never()).sendPacket(any(), any(), any(), any(), anyOrNull())

        val updatedItem = inventoryManager.getItem(sampleItemId)
        assertEquals(newParentId, updatedItem?.parentId)
    }

    @Test
    fun `moveItem falls back to UDP when AISv3 and CAP_MOVE_INVENTORY_ITEM are absent`() = runBlocking {
        whenever(capabilityManager.hasCapability(CapabilityManager.CAP_INVENTORY_API)).thenReturn(false)
        whenever(capabilityManager.hasCapability(CapabilityManager.CAP_MOVE_INVENTORY_ITEM)).thenReturn(false)

        val result = inventoryManager.moveItem(sampleItemId, newParentId)

        assertTrue(result)
        verify(capabilityManager, never()).request(eq(CapabilityManager.CAP_INVENTORY_API), anyOrNull())
        verify(capabilityManager, never()).request(eq(CapabilityManager.CAP_MOVE_INVENTORY_ITEM), anyOrNull())
        verify(udpConnection).sendPacket(eq(MessageIdRegistry.MOVE_INVENTORY_ITEM), any(), eq(true), eq(false), anyOrNull())

        val updatedItem = inventoryManager.getItem(sampleItemId)
        assertEquals(newParentId, updatedItem?.parentId)
    }

    @Test
    fun `moveItem falls back to UDP when both HTTP capabilities return null`() = runBlocking {
        whenever(capabilityManager.hasCapability(CapabilityManager.CAP_INVENTORY_API)).thenReturn(true)
        whenever(capabilityManager.request(eq(CapabilityManager.CAP_INVENTORY_API), anyOrNull())).thenReturn(null)
        whenever(capabilityManager.hasCapability(CapabilityManager.CAP_MOVE_INVENTORY_ITEM)).thenReturn(true)
        whenever(capabilityManager.request(eq(CapabilityManager.CAP_MOVE_INVENTORY_ITEM), anyOrNull())).thenReturn(null)

        val result = inventoryManager.moveItem(sampleItemId, newParentId)

        assertTrue(result)
        verify(capabilityManager).request(eq(CapabilityManager.CAP_INVENTORY_API), anyOrNull())
        verify(capabilityManager).request(eq(CapabilityManager.CAP_MOVE_INVENTORY_ITEM), anyOrNull())
        verify(udpConnection).sendPacket(eq(MessageIdRegistry.MOVE_INVENTORY_ITEM), any(), eq(true), eq(false), anyOrNull())

        val updatedItem = inventoryManager.getItem(sampleItemId)
        assertEquals(newParentId, updatedItem?.parentId)
    }
}
