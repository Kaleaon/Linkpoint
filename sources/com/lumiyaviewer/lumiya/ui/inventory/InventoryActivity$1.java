// Decompiled by Jad v1.5.8e. Copyright 2001 Pavel Kouznetsov.
// Jad home page: http://www.geocities.com/kpdus/jad.html
// Decompiler options: braces fieldsfirst space lnc 

package com.lumiyaviewer.lumiya.ui.inventory;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import com.lumiyaviewer.lumiya.ui.common.FragmentActivityFactory;

// Referenced classes of package com.lumiyaviewer.lumiya.ui.inventory:
//            InventoryActivity, InventoryFragment

class this._cls0
    implements FragmentActivityFactory
{

    final InventoryActivity this$0;

    public Intent createIntent(Context context, Bundle bundle)
    {
        context = new Intent(context, com/lumiyaviewer/lumiya/ui/inventory/InventoryActivity);
        context.putExtra("selection", bundle);
        return context;
    }

    public Class getFragmentClass()
    {
        return com/lumiyaviewer/lumiya/ui/inventory/InventoryFragment;
    }

    ()
    {
        this$0 = InventoryActivity.this;
        super();
    }
}
