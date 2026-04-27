// Decompiled by Jad v1.5.8e. Copyright 2001 Pavel Kouznetsov.
// Jad home page: http://www.geocities.com/kpdus/jad.html
// Decompiler options: braces fieldsfirst space lnc 

package com.lumiyaviewer.lumiya.ui.objects;

import com.lumiyaviewer.lumiya.Debug;

// Referenced classes of package com.lumiyaviewer.lumiya.ui.objects:
//            ObjectSelectorFragment

class this._cls0
    implements android.support.v7.widget.tener
{

    final ObjectSelectorFragment this$0;

    public boolean onQueryTextChange(String s)
    {
        Debug.Printf("searchview: textchange", new Object[0]);
        ObjectSelectorFragment._2D_wrap0(ObjectSelectorFragment.this);
        return true;
    }

    public boolean onQueryTextSubmit(String s)
    {
        return true;
    }

    A()
    {
        this$0 = ObjectSelectorFragment.this;
        super();
    }
}
