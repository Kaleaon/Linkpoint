package com.linkpoint.ui.avapicker;

import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import com.linkpoint.R;
import com.linkpoint.slproto.inventory.SLInventoryEntry;
import com.linkpoint.slproto.users.ChatterID;
import com.linkpoint.ui.common.DetailsActivity;
import com.linkpoint.ui.inventory.InventoryFragmentHelper;
import java.util.UUID;
import javax.annotation.Nullable;

public class AvatarPickerForShare extends AvatarPickerFragment {
    private static final String INVENTORY_ENTRY_KEY = "inventoryEntry";
    private final InventoryFragmentHelper inventoryFragmentHelper = new InventoryFragmentHelper(this);

    public static Bundle makeArguments(UUID uuid, SLInventoryEntry sLInventoryEntry) {
        Bundle bundle = new Bundle();
        bundle.putString("activeAgentUUID", uuid.toString());
        bundle.putParcelable(INVENTORY_ENTRY_KEY, sLInventoryEntry);
        return bundle;
    }

    public String getTitle() {
        return getString(R.string.share_with_title);
    }

    /* access modifiers changed from: package-private */
    /* renamed from: lambda$-com_lumiyaviewer_lumiya_ui_avapicker_AvatarPickerForShare_1468  reason: not valid java name */
    public /* synthetic */ void m387lambda$com_lumiyaviewer_lumiya_ui_avapicker_AvatarPickerForShare_1468() {
        FragmentActivity activity = getActivity();
        if (activity instanceof DetailsActivity) {
            ((DetailsActivity) activity).closeDetailsFragment(this);
        }
    }

    /* access modifiers changed from: protected */
    public void onAvatarSelected(ChatterID chatterID, @Nullable String str) {
        Bundle arguments = getArguments();
        if (arguments != null && arguments.containsKey(INVENTORY_ENTRY_KEY)) {
            this.inventoryFragmentHelper.ConfirmShareInventoryEntry((SLInventoryEntry) arguments.getParcelable(INVENTORY_ENTRY_KEY), chatterID, str, new $Lambda$GxFBFkg7vdmipTAXKE3eB6HqSs(this));
        }
    }
}
