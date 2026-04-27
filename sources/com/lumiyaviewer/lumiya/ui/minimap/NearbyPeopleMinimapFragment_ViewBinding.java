// Decompiled by Jad v1.5.8e. Copyright 2001 Pavel Kouznetsov.
// Jad home page: http://www.geocities.com/kpdus/jad.html
// Decompiler options: braces fieldsfirst space lnc 

package com.lumiyaviewer.lumiya.ui.minimap;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import butterknife.Unbinder;
import butterknife.internal.Utils;

// Referenced classes of package com.lumiyaviewer.lumiya.ui.minimap:
//            NearbyPeopleMinimapFragment

public class NearbyPeopleMinimapFragment_ViewBinding
    implements Unbinder
{

    private NearbyPeopleMinimapFragment target;

    public NearbyPeopleMinimapFragment_ViewBinding(NearbyPeopleMinimapFragment nearbypeopleminimapfragment, View view)
    {
        target = nearbypeopleminimapfragment;
        nearbypeopleminimapfragment.emptyView = Utils.findRequiredView(view, 0x1020004, "field 'emptyView'");
        nearbypeopleminimapfragment.userListView = (RecyclerView)Utils.findRequiredViewAsType(view, 0x7f1001e2, "field 'userListView'", android/support/v7/widget/RecyclerView);
    }

    public void unbind()
    {
        NearbyPeopleMinimapFragment nearbypeopleminimapfragment = target;
        if (nearbypeopleminimapfragment == null)
        {
            throw new IllegalStateException("Bindings already cleared.");
        } else
        {
            target = null;
            nearbypeopleminimapfragment.emptyView = null;
            nearbypeopleminimapfragment.userListView = null;
            return;
        }
    }
}
