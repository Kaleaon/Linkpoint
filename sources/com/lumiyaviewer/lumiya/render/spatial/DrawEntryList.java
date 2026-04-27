// Decompiled by Jad v1.5.8e. Copyright 2001 Pavel Kouznetsov.
// Jad home page: http://www.geocities.com/kpdus/jad.html
// Decompiler options: braces fieldsfirst space lnc 

package com.lumiyaviewer.lumiya.render.spatial;

import com.lumiyaviewer.lumiya.utils.InlineList;
import com.lumiyaviewer.lumiya.utils.InlineListEntry;

// Referenced classes of package com.lumiyaviewer.lumiya.render.spatial:
//            DrawListEntry

public class DrawEntryList extends InlineList
{
    public static interface EntryRemovalListener
    {

        public abstract void onEntryRemovalRequested(DrawListEntry drawlistentry);
    }


    private final EntryRemovalListener listener;

    public DrawEntryList(EntryRemovalListener entryremovallistener)
    {
        listener = entryremovallistener;
    }

    public void requestEntryRemoval(DrawListEntry drawlistentry)
    {
        if (listener != null)
        {
            listener.onEntryRemovalRequested(drawlistentry);
        }
    }

    public volatile void requestEntryRemoval(InlineListEntry inlinelistentry)
    {
        requestEntryRemoval((DrawListEntry)inlinelistentry);
    }
}
