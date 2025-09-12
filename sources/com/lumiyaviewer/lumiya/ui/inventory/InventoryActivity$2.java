// Decompiled by Jad v1.5.8e. Copyright 2001 Pavel Kouznetsov.
// Jad home page: http://www.geocities.com/kpdus/jad.html
// Decompiler options: braces fieldsfirst space lnc 

package com.lumiyaviewer.lumiya.ui.inventory;


// Referenced classes of package com.lumiyaviewer.lumiya.ui.inventory:
//            InventoryActivity

class this._cls0
    implements android.support.v7.widget.xtListener
{

    final InventoryActivity this$0;

    public boolean onQueryTextChange(String s)
    {
        InventoryActivity._2D_set0(InventoryActivity.this, s);
        InventoryActivity._2D_wrap0(InventoryActivity.this);
        return true;
    }

    public boolean onQueryTextSubmit(String s)
    {
        return true;
    }

    ()
    {
        this$0 = InventoryActivity.this;
        super();
    }
}
