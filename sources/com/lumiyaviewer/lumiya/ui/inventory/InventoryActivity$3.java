// Decompiled by Jad v1.5.8e. Copyright 2001 Pavel Kouznetsov.
// Jad home page: http://www.geocities.com/kpdus/jad.html
// Decompiler options: braces fieldsfirst space lnc 

package com.lumiyaviewer.lumiya.ui.inventory;

import android.view.MenuItem;

// Referenced classes of package com.lumiyaviewer.lumiya.ui.inventory:
//            InventoryActivity

class this._cls0
    implements android.support.v4.view.ionExpandListener
{

    final InventoryActivity this$0;

    public boolean onMenuItemActionCollapse(MenuItem menuitem)
    {
        InventoryActivity._2D_set1(InventoryActivity.this, false);
        InventoryActivity._2D_wrap0(InventoryActivity.this);
        return true;
    }

    public boolean onMenuItemActionExpand(MenuItem menuitem)
    {
        InventoryActivity._2D_set1(InventoryActivity.this, true);
        InventoryActivity._2D_wrap0(InventoryActivity.this);
        return true;
    }

    ener()
    {
        this$0 = InventoryActivity.this;
        super();
    }
}
