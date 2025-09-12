// Decompiled by Jad v1.5.8e. Copyright 2001 Pavel Kouznetsov.
// Jad home page: http://www.geocities.com/kpdus/jad.html
// Decompiler options: braces fieldsfirst space lnc 

package com.lumiyaviewer.lumiya.ui.outfits;

import android.widget.ListView;
import com.lumiyaviewer.lumiya.ui.common.DismissableAdapter;

// Referenced classes of package com.lumiyaviewer.lumiya.ui.outfits:
//            CurrentOutfitFragment

class this._cls0
    implements com.lumiyaviewer.lumiya.ui.common.hListener.DismissCallbacks
{

    final CurrentOutfitFragment this$0;

    public boolean canDismiss(ListView listview, int i)
    {
        listview = listview.getAdapter();
        if (listview instanceof DismissableAdapter)
        {
            return ((DismissableAdapter)listview).canDismiss(i);
        } else
        {
            return false;
        }
    }

    public void onDismiss(ListView listview, int i)
    {
        listview = listview.getAdapter();
        if (listview instanceof DismissableAdapter)
        {
            ((DismissableAdapter)listview).onDismiss(i);
        }
    }

    Listener.DismissCallbacks()
    {
        this$0 = CurrentOutfitFragment.this;
        super();
    }
}
