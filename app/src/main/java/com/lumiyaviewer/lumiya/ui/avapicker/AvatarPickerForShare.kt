package com.lumiyaviewer.lumiya.ui.avapicker

import android.os.Bundle
import com.lumiyaviewer.lumiya.R
import com.lumiyaviewer.lumiya.slproto.inventory.SLInventoryEntry
import com.lumiyaviewer.lumiya.slproto.users.ChatterID
import com.lumiyaviewer.lumiya.ui.common.DetailsActivity
import com.lumiyaviewer.lumiya.ui.inventory.InventoryFragmentHelper
import java.util.UUID

class AvatarPickerForShare : AvatarPickerFragment() {

    companion object {
        private const val INVENTORY_ENTRY_KEY = "inventoryEntry"

        @JvmStatic
        fun makeArguments(uuid: UUID, inventoryEntry: SLInventoryEntry): Bundle {
            return Bundle().apply {
                putString("activeAgentUUID", uuid.toString())
                putParcelable(INVENTORY_ENTRY_KEY, inventoryEntry)
            }
        }
    }

    private val inventoryFragmentHelper = InventoryFragmentHelper(this)

    override fun getTitle(): String {
        return getString(R.string.share_with_title)
    }

    override fun onAvatarSelected(chatterID: ChatterID, displayName: String?) {
        val args = arguments ?: return
        
        if (args.containsKey(INVENTORY_ENTRY_KEY)) {
            val inventoryEntry = args.getParcelable<SLInventoryEntry>(INVENTORY_ENTRY_KEY)
            inventoryEntry?.let {
                inventoryFragmentHelper.ConfirmShareInventoryEntry(
                    it,
                    chatterID,
                    displayName
                ) {
                    // Callback when sharing is complete
                    val activity = activity
                    if (activity is DetailsActivity) {
                        activity.closeDetailsFragment(this)
                    }
                }
            }
        }
    }
}
