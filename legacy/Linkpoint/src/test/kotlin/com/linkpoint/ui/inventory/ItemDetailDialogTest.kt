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
import org.robolectric.Robolectric
import org.robolectric.RobolectricTestRunner
import java.util.UUID

@RunWith(RobolectricTestRunner::class)
class ItemDetailDialogTest {

    private lateinit var context: Context
    private val agentId = UUID.randomUUID()

    @Before
    fun setUp() {
        context = ApplicationProvider.getApplicationContext()
    }

    private fun createTestItem(ownerMask: Int): InventoryItem {
        return InventoryItem(
            itemId = UUID.randomUUID(),
            assetId = UUID.randomUUID(),
            parentId = UUID.randomUUID(),
            name = "Permissions Test Item",
            description = "Detail Dialog Test",
            assetType = 0,
            inventoryType = 0,
            flags = 0,
            permissions = ItemPermissions(
                baseMask = ownerMask,
                ownerMask = ownerMask,
                groupMask = 0,
                everyoneMask = 0,
                nextOwnerMask = ownerMask,
                ownerId = agentId,
                creatorId = agentId
            ),
            saleInfo = SaleInfo(0, 0),
            creationDate = 1600000000
        )
    }

    @Test
    fun `ItemDetailDialog newInstance initializes with arguments`() {
        val mask = ItemPermissions.PERM_COPY or ItemPermissions.PERM_MODIFY or ItemPermissions.PERM_TRANSFER
        val item = createTestItem(mask)
        val dialog = ItemDetailDialog.newInstance(item)

        assertNotNull(dialog.arguments)
        val retrievedItem = dialog.arguments?.getParcelable<InventoryItem>("item")
        assertNotNull(retrievedItem)
        assertEquals(item.name, retrievedItem?.name)
    }
}
