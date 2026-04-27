package com.lumiyaviewer.lumiya.ui.avapicker;

import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import com.lumiyaviewer.lumiya.R;
import com.lumiyaviewer.lumiya.slproto.inventory.SLInventoryEntry;
import com.lumiyaviewer.lumiya.slproto.users.ChatterID;
import com.lumiyaviewer.lumiya.ui.common.DetailsActivity;
import com.lumiyaviewer.lumiya.ui.inventory.InventoryFragmentHelper;
import java.util.UUID;
import javax.annotation.Nullable;

/* loaded from: classes.dex */
public class AvatarPickerForShare extends AvatarPickerFragment {
    private static final String INVENTORY_ENTRY_KEY = "inventoryEntry";
    private final InventoryFragmentHelper inventoryFragmentHelper = new InventoryFragmentHelper(this);

    public static Bundle makeArguments(UUID uuid, SLInventoryEntry sLInventoryEntry) {
        Bundle bundle = new Bundle();
        bundle.putString("activeAgentUUID", uuid.toString());
        bundle.putParcelable(INVENTORY_ENTRY_KEY, sLInventoryEntry);
        return bundle;
    }

    @Override // com.lumiyaviewer.lumiya.ui.avapicker.AvatarPickerFragment, com.lumiyaviewer.lumiya.ui.common.FragmentWithTitle, com.lumiyaviewer.lumiya.ui.common.FragmentHasTitle
    public String getTitle() {
        return getString(R.string.share_with_title);
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    /* renamed from: lambda$-com_lumiyaviewer_lumiya_ui_avapicker_AvatarPickerForShare_1468, reason: not valid java name */
    public /* synthetic */ void m397xf146b8d7() {
        FragmentActivity activity = getActivity();
        if (activity instanceof DetailsActivity) {
            ((DetailsActivity) activity).closeDetailsFragment(this);
        }
    }

    @Override // com.lumiyaviewer.lumiya.ui.avapicker.AvatarPickerFragment
    protected void onAvatarSelected(ChatterID chatterID, @Nullable String str) {
        Bundle arguments = getArguments();
        if (arguments == null || !arguments.containsKey(INVENTORY_ENTRY_KEY)) {
            return;
        }
        this.inventoryFragmentHelper.ConfirmShareInventoryEntry((SLInventoryEntry) arguments.getParcelable(INVENTORY_ENTRY_KEY), chatterID, str, new Runnable() { // from class: com.lumiyaviewer.lumiya.ui.avapicker.-$Lambda$GxFBFkg7vdmipTAXKE3eB-6HqSs
            private final /* synthetic */ void $m$0() {
                ((AvatarPickerForShare) this).m397xf146b8d7();
            }

            @Override // java.lang.Runnable
            public final void run() {
                $m$0();
            }
        });
    }
}
