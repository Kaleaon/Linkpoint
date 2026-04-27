// Decompiled by Jad v1.5.8e. Copyright 2001 Pavel Kouznetsov.
// Jad home page: http://www.geocities.com/kpdus/jad.html
// Decompiler options: braces fieldsfirst space lnc 

package com.lumiyaviewer.lumiya.slproto.llsd;


// Referenced classes of package com.lumiyaviewer.lumiya.slproto.llsd:
//            LLSDStreamingParser, LLSDXMLException, LLSDValueTypeException, LLSDNode

public static interface Q
{

    public abstract Q onArrayBegin(String s)
        throws LLSDXMLException;

    public abstract void onArrayEnd(String s)
        throws LLSDXMLException;

    public abstract Q onMapBegin(String s)
        throws LLSDXMLException;

    public abstract void onMapEnd(String s)
        throws LLSDXMLException, InterruptedException;

    public abstract void onPrimitiveValue(String s, LLSDNode llsdnode)
        throws LLSDXMLException, LLSDValueTypeException;
}
