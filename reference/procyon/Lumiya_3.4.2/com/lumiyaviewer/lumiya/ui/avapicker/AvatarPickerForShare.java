// 
// Decompiled by Procyon v0.6.0
// 

package com.lumiyaviewer.lumiya.ui.avapicker;

import javax.annotation.Nullable;
import com.lumiyaviewer.lumiya.slproto.users.ChatterID;
import android.support.v4.app.FragmentActivity;
import com.lumiyaviewer.lumiya.ui.common.DetailsActivity;
import android.os.Parcelable;
import android.os.Bundle;
import com.lumiyaviewer.lumiya.slproto.inventory.SLInventoryEntry;
import java.util.UUID;
import android.support.v4.app.Fragment;
import com.lumiyaviewer.lumiya.ui.inventory.InventoryFragmentHelper;

public class AvatarPickerForShare extends AvatarPickerFragment
{
    private static final String INVENTORY_ENTRY_KEY = "inventoryEntry";
    private final InventoryFragmentHelper inventoryFragmentHelper;
    
    public AvatarPickerForShare() {
        this.inventoryFragmentHelper = new InventoryFragmentHelper(this);
    }
    
    public static Bundle makeArguments(final UUID uuid, final SLInventoryEntry slInventoryEntry) {
        final Bundle bundle = new Bundle();
        bundle.putString("activeAgentUUID", uuid.toString());
        bundle.putParcelable("inventoryEntry", (Parcelable)slInventoryEntry);
        return bundle;
    }
    
    @Override
    public String getTitle() {
        return this.getString(2131297031);
    }
    
    @Override
    protected void onAvatarSelected(final ChatterID chatterID, @Nullable final String s) {
        final Bundle arguments = this.getArguments();
        if (arguments != null && arguments.containsKey("inventoryEntry")) {
            this.inventoryFragmentHelper.ConfirmShareInventoryEntry((SLInventoryEntry)arguments.getParcelable("inventoryEntry"), chatterID, s, new _$Lambda$GxFBFkg7vdmipTAXKE3eB_6HqSs(this));
        }
    }
}
