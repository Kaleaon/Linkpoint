package com.linkpoint.ui.inventory

import android.content.Context
import androidx.test.core.app.ApplicationProvider
import com.linkpoint.inventory.InventoryItem
import com.linkpoint.inventory.ItemPermissions
import com.linkpoint.inventory.SaleInfo
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import java.util.UUID

@RunWith(RobolectricTestRunner::class)
class ItemPropertiesDialogTest {

    private lateinit var context: Context
    private val agentId = UUID.randomUUID()

    @Before
    fun setUp() {
        context = ApplicationProvider.getApplicationContext()
    }

    private fun createTestItem(): InventoryItem {
        return InventoryItem(
            itemId = UUID.randomUUID(),
            assetId = UUID.randomUUID(),
            parentId = UUID.randomUUID(),
            name = "Test Inventory Object",
            description = "A test inventory item description",
            assetType = 0,
            inventoryType = 0,
            flags = 1,
            permissions = ItemPermissions(
                baseMask = 0x7FFFFFFF,
                ownerMask = 0x7FFFFFFF,
                groupMask = 0,
                everyoneMask = 0,
                nextOwnerMask = 0x7FFFFFFF,
                ownerId = agentId,
                creatorId = agentId
            ),
            saleInfo = SaleInfo(0, 0),
            creationDate = 1700000000
        )
    }

    @Test
    fun `ItemPropertiesDialog newInstance initializes with arguments`() {
        val item = createTestItem()
        val dialog = ItemPropertiesDialog.newInstance(item)

        assertNotNull(dialog.arguments)
        val retrievedItem = dialog.arguments?.getParcelable<InventoryItem>("item")
        assertNotNull(retrievedItem)
        assertEquals(item.name, retrievedItem?.name)
        assertEquals(item.itemId, retrievedItem?.itemId)
        assertEquals(item.assetId, retrievedItem?.assetId)
    }
}
