package com.linkpoint.ui.inventory

import android.content.Context
import android.view.View
import androidx.test.core.app.ApplicationProvider
import com.linkpoint.R
import com.linkpoint.inventory.InventoryItem
import com.linkpoint.inventory.ItemPermissions
import com.linkpoint.inventory.SaleInfo
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.shadows.ShadowPopupMenu
import java.util.UUID

@RunWith(RobolectricTestRunner::class)
class ItemContextMenuTest {

    private lateinit var context: Context
    private lateinit var anchorView: View
    private val agentId = UUID.randomUUID()

    @Before
    fun setUp() {
        context = ApplicationProvider.getApplicationContext()
        anchorView = View(context)
    }

    private fun createTestItem(ownerMask: Int): InventoryItem {
        return InventoryItem(
            itemId = UUID.randomUUID(),
            assetId = UUID.randomUUID(),
            parentId = UUID.randomUUID(),
            name = "Test Object",
            description = "Test Description",
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
            creationDate = 0
        )
    }

    @Test
    fun `show with full permissions enables all actions and has no tooltips`() {
        val fullMask = ItemPermissions.PERM_COPY or ItemPermissions.PERM_MODIFY or ItemPermissions.PERM_TRANSFER
        val item = createTestItem(fullMask)

        var selectedAction: ItemContextMenu.Action? = null
        ItemContextMenu.show(anchorView, item) { action ->
            selectedAction = action
        }

        val popupMenu = ShadowPopupMenu.getLatestPopupMenu()
        val copyItem = popupMenu.menu.findItem(R.id.action_copy)
        val deleteItem = popupMenu.menu.findItem(R.id.action_delete)

        assertTrue("Copy action should be enabled", copyItem.isEnabled)
        assertNull("Copy action should have no tooltip when enabled", copyItem.tooltipText)

        assertTrue("Delete action should be enabled", deleteItem.isEnabled)
        assertNull("Delete action should have no tooltip when enabled", deleteItem.tooltipText)
    }

    @Test
    fun `show with no permissions disables copy and delete with explanatory tooltips`() {
        val item = createTestItem(0)

        ItemContextMenu.show(anchorView, item) {}

        val popupMenu = ShadowPopupMenu.getLatestPopupMenu()
        val copyItem = popupMenu.menu.findItem(R.id.action_copy)
        val deleteItem = popupMenu.menu.findItem(R.id.action_delete)

        assertFalse("Copy action should be disabled", copyItem.isEnabled)
        assertEquals(
            context.getString(R.string.inventory_item_no_copy_tooltip),
            copyItem.tooltipText
        )

        assertFalse("Delete action should be disabled", deleteItem.isEnabled)
        assertEquals(
            context.getString(R.string.inventory_item_no_modify_no_transfer_tooltip),
            deleteItem.tooltipText
        )
    }

    @Test
    fun `show with copy permission only enables copy and disables delete`() {
        val mask = ItemPermissions.PERM_COPY
        val item = createTestItem(mask)

        ItemContextMenu.show(anchorView, item) {}

        val popupMenu = ShadowPopupMenu.getLatestPopupMenu()
        val copyItem = popupMenu.menu.findItem(R.id.action_copy)
        val deleteItem = popupMenu.menu.findItem(R.id.action_delete)

        assertTrue("Copy action should be enabled", copyItem.isEnabled)
        assertNull(copyItem.tooltipText)

        assertFalse("Delete action should be disabled", deleteItem.isEnabled)
        assertEquals(
            context.getString(R.string.inventory_item_no_modify_no_transfer_tooltip),
            deleteItem.tooltipText
        )
    }

    @Test
    fun `show missing modify permission displays missing modify tooltip on delete`() {
        val mask = ItemPermissions.PERM_COPY or ItemPermissions.PERM_TRANSFER
        val item = createTestItem(mask)

        ItemContextMenu.show(anchorView, item) {}

        val popupMenu = ShadowPopupMenu.getLatestPopupMenu()
        val deleteItem = popupMenu.menu.findItem(R.id.action_delete)

        assertFalse("Delete action should be disabled without modify permission", deleteItem.isEnabled)
        assertEquals(
            context.getString(R.string.inventory_item_no_modify_tooltip),
            deleteItem.tooltipText
        )
    }

    @Test
    fun `show missing transfer permission displays missing transfer tooltip on delete`() {
        val mask = ItemPermissions.PERM_COPY or ItemPermissions.PERM_MODIFY
        val item = createTestItem(mask)

        ItemContextMenu.show(anchorView, item) {}

        val popupMenu = ShadowPopupMenu.getLatestPopupMenu()
        val deleteItem = popupMenu.menu.findItem(R.id.action_delete)

        assertFalse("Delete action should be disabled without transfer permission", deleteItem.isEnabled)
        assertEquals(
            context.getString(R.string.inventory_item_no_transfer_tooltip),
            deleteItem.tooltipText
        )
    }

    @Test
    fun `selecting menu items triggers corresponding action callbacks`() {
        val fullMask = ItemPermissions.PERM_COPY or ItemPermissions.PERM_MODIFY or ItemPermissions.PERM_TRANSFER
        val item = createTestItem(fullMask)

        var selectedAction: ItemContextMenu.Action? = null
        ItemContextMenu.show(anchorView, item) { action ->
            selectedAction = action
        }

        val popupMenu = ShadowPopupMenu.getLatestPopupMenu()

        popupMenu.menu.performIdentifierAction(R.id.action_wear, 0)
        assertEquals(ItemContextMenu.Action.WEAR, selectedAction)

        popupMenu.menu.performIdentifierAction(R.id.action_copy, 0)
        assertEquals(ItemContextMenu.Action.COPY, selectedAction)

        popupMenu.menu.performIdentifierAction(R.id.action_delete, 0)
        assertEquals(ItemContextMenu.Action.DELETE, selectedAction)

        popupMenu.menu.performIdentifierAction(R.id.action_properties, 0)
        assertEquals(ItemContextMenu.Action.PROPERTIES, selectedAction)
    }
}
