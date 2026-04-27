// Decompiled by Jad v1.5.8e. Copyright 2001 Pavel Kouznetsov.
// Jad home page: http://www.geocities.com/kpdus/jad.html
// Decompiler options: braces fieldsfirst space lnc 

package com.lumiyaviewer.lumiya.render.spatial;

import com.lumiyaviewer.lumiya.utils.InlineList;
import com.lumiyaviewer.lumiya.utils.InlineListEntry;

// Referenced classes of package com.lumiyaviewer.lumiya.render.spatial:
//            DrawList

public abstract class DrawListEntry
    implements InlineListEntry
{

    final float boundingBox[] = new float[6];
    private volatile InlineList list;
    private DrawListEntry next;
    private DrawListEntry prev;

    public DrawListEntry()
    {
    }

    public abstract void addToDrawList(DrawList drawlist);

    public InlineList getList()
    {
        return list;
    }

    public DrawListEntry getNext()
    {
        return next;
    }

    public volatile InlineListEntry getNext()
    {
        return getNext();
    }

    public DrawListEntry getPrev()
    {
        return prev;
    }

    public volatile InlineListEntry getPrev()
    {
        return getPrev();
    }

    public void requestEntryRemoval()
    {
        InlineList inlinelist = list;
        if (inlinelist != null)
        {
            inlinelist.requestEntryRemoval(this);
        }
    }

    public void setList(InlineList inlinelist)
    {
        list = inlinelist;
    }

    public void setNext(DrawListEntry drawlistentry)
    {
        next = drawlistentry;
    }

    public volatile void setNext(InlineListEntry inlinelistentry)
    {
        setNext((DrawListEntry)inlinelistentry);
    }

    public void setPrev(DrawListEntry drawlistentry)
    {
        prev = drawlistentry;
    }

    public volatile void setPrev(InlineListEntry inlinelistentry)
    {
        setPrev((DrawListEntry)inlinelistentry);
    }
}
