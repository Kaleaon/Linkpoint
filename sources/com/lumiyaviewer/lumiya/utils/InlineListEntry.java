// Decompiled by Jad v1.5.8e. Copyright 2001 Pavel Kouznetsov.
// Jad home page: http://www.geocities.com/kpdus/jad.html
// Decompiler options: braces fieldsfirst space lnc 

package com.lumiyaviewer.lumiya.utils;


// Referenced classes of package com.lumiyaviewer.lumiya.utils:
//            InlineList

public interface InlineListEntry
{

    public abstract InlineList getList();

    public abstract InlineListEntry getNext();

    public abstract InlineListEntry getPrev();

    public abstract void requestEntryRemoval();

    public abstract void setList(InlineList inlinelist);

    public abstract void setNext(InlineListEntry inlinelistentry);

    public abstract void setPrev(InlineListEntry inlinelistentry);
}
