// Decompiled by Jad v1.5.8e. Copyright 2001 Pavel Kouznetsov.
// Jad home page: http://www.geocities.com/kpdus/jad.html
// Decompiler options: braces fieldsfirst space lnc 

package com.lumiyaviewer.lumiya.utils;


// Referenced classes of package com.lumiyaviewer.lumiya.utils:
//            InlineListEntry

public class InlineList
{

    private InlineListEntry first;

    public InlineList()
    {
        first = null;
    }

    public void addEntry(InlineListEntry inlinelistentry)
    {
        InlineList inlinelist = inlinelistentry.getList();
        if (inlinelist != this)
        {
            if (inlinelist != null)
            {
                inlinelist.removeEntry(inlinelistentry);
            }
            inlinelistentry.setNext(first);
            inlinelistentry.setPrev(null);
            if (first != null)
            {
                first.setPrev(inlinelistentry);
            }
            first = inlinelistentry;
            inlinelistentry.setList(this);
        }
    }

    public final InlineListEntry getFirst()
    {
        return first;
    }

    public void removeEntry(InlineListEntry inlinelistentry)
    {
        if (inlinelistentry.getList() == this)
        {
            InlineListEntry inlinelistentry1 = inlinelistentry.getNext();
            InlineListEntry inlinelistentry2 = inlinelistentry.getPrev();
            if (inlinelistentry2 != null)
            {
                inlinelistentry2.setNext(inlinelistentry1);
            } else
            {
                first = inlinelistentry1;
            }
            if (inlinelistentry1 != null)
            {
                inlinelistentry1.setPrev(inlinelistentry2);
            }
            inlinelistentry.setPrev(null);
            inlinelistentry.setNext(null);
            inlinelistentry.setList(null);
        }
    }

    public void requestEntryRemoval(InlineListEntry inlinelistentry)
    {
    }
}
