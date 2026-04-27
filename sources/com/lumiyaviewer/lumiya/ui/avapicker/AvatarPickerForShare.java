// Decompiled by Jad v1.5.8e. Copyright 2001 Pavel Kouznetsov.
// Jad home page: http://www.geocities.com/kpdus/jad.html
// Decompiler options: braces fieldsfirst space lnc 

package com.lumiyaviewer.lumiya.ui.avapicker;

import android.os.Bundle;
import com.lumiyaviewer.lumiya.slproto.inventory.SLInventoryEntry;
import com.lumiyaviewer.lumiya.slproto.users.ChatterID;
import com.lumiyaviewer.lumiya.ui.common.DetailsActivity;
import com.lumiyaviewer.lumiya.ui.inventory.InventoryFragmentHelper;
import java.util.UUID;

// Referenced classes of package com.lumiyaviewer.lumiya.ui.avapicker:
//            AvatarPickerFragment

public class AvatarPickerForShare extends AvatarPickerFragment
{

    private static final String INVENTORY_ENTRY_KEY = "inventoryEntry";
    private final InventoryFragmentHelper inventoryFragmentHelper = new InventoryFragmentHelper(this);

    public AvatarPickerForShare()
    {
    }

    public static Bundle makeArguments(UUID uuid, SLInventoryEntry slinventoryentry)
    {
        Bundle bundle = new Bundle();
        bundle.putString("activeAgentUUID", uuid.toString());
        bundle.putParcelable("inventoryEntry", slinventoryentry);
        return bundle;
    }

    public String getTitle()
    {
        return getString(0x7f090307);
    }

    void lambda$_2D_com_lumiyaviewer_lumiya_ui_avapicker_AvatarPickerForShare_1468()
    {
        android.support.v4.app.FragmentActivity fragmentactivity = getActivity();
        if (fragmentactivity instanceof DetailsActivity)
        {
            ((DetailsActivity)fragmentactivity).closeDetailsFragment(this);
        }
    }

    protected void onAvatarSelected(ChatterID chatterid, String s)
    {
        Object obj = getArguments();
        if (obj != null && ((Bundle) (obj)).containsKey("inventoryEntry"))
        {
            obj = (SLInventoryEntry)((Bundle) (obj)).getParcelable("inventoryEntry");
            inventoryFragmentHelper.ConfirmShareInventoryEntry(((SLInventoryEntry) (obj)), chatterid, s, new _2D_.Lambda.GxFBFkg7vdmipTAXKE3eB_2D_6HqSs(this));
        }
    }
}
