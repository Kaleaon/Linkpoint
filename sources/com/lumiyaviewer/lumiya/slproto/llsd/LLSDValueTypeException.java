// Decompiled by Jad v1.5.8e. Copyright 2001 Pavel Kouznetsov.
// Jad home page: http://www.geocities.com/kpdus/jad.html
// Decompiler options: braces fieldsfirst space lnc 

package com.lumiyaviewer.lumiya.slproto.llsd;


// Referenced classes of package com.lumiyaviewer.lumiya.slproto.llsd:
//            LLSDException, LLSDNode

public class LLSDValueTypeException extends LLSDException
{

    private static final long serialVersionUID = 0xe69548e0d5aa16cbL;

    public LLSDValueTypeException()
    {
        super("Invalid value type");
    }

    public LLSDValueTypeException(String s, LLSDNode llsdnode)
    {
        super((new StringBuilder()).append("Invalid value type: requested ").append(s).append(", actual ").append(llsdnode.getClass().getSimpleName()).toString());
    }
}
