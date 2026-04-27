// Decompiled by Jad v1.5.8e. Copyright 2001 Pavel Kouznetsov.
// Jad home page: http://www.geocities.com/kpdus/jad.html
// Decompiler options: braces fieldsfirst space lnc 

package com.lumiyaviewer.lumiya.render.spatial;

import com.lumiyaviewer.lumiya.utils.InlineList;
import com.lumiyaviewer.lumiya.utils.InlineListEntry;

// Referenced classes of package com.lumiyaviewer.lumiya.render.spatial:
//            DrawListEntry, SpatialTree, SpatialObjectIndex, DrawList

public class MyAvatarTreeNode extends InlineList
{

    private final SpatialTree spatialTree;

    public MyAvatarTreeNode(SpatialTree spatialtree)
    {
        spatialTree = spatialtree;
    }

    public void addDrawables(DrawList drawlist)
    {
        for (DrawListEntry drawlistentry = (DrawListEntry)getFirst(); drawlistentry != null; drawlistentry = drawlistentry.getNext())
        {
            drawlistentry.addToDrawList(drawlist);
        }

    }

    public void addEntry(DrawListEntry drawlistentry)
    {
        super.addEntry(drawlistentry);
        spatialTree.setDrawListChanged();
    }

    public volatile void addEntry(InlineListEntry inlinelistentry)
    {
        addEntry((DrawListEntry)inlinelistentry);
    }

    public void removeEntry(DrawListEntry drawlistentry)
    {
        super.removeEntry(drawlistentry);
        spatialTree.setDrawListChanged();
    }

    public volatile void removeEntry(InlineListEntry inlinelistentry)
    {
        removeEntry((DrawListEntry)inlinelistentry);
    }

    public void requestEntryRemoval(DrawListEntry drawlistentry)
    {
        spatialTree.spatialObjectIndex.requestEntryRemoval(drawlistentry);
    }

    public volatile void requestEntryRemoval(InlineListEntry inlinelistentry)
    {
        requestEntryRemoval((DrawListEntry)inlinelistentry);
    }
}
