// Decompiled by Jad v1.5.8e. Copyright 2001 Pavel Kouznetsov.
// Jad home page: http://www.geocities.com/kpdus/jad.html
// Decompiler options: braces fieldsfirst space lnc 

package com.lumiyaviewer.lumiya.slproto.llsd;

import java.util.HashMap;
import java.util.Map;

public final class LLSDNodeType extends Enum
{

    private static final LLSDNodeType $VALUES[];
    public static final LLSDNodeType llsdArray;
    public static final LLSDNodeType llsdBinary;
    public static final LLSDNodeType llsdBoolean;
    public static final LLSDNodeType llsdDate;
    public static final LLSDNodeType llsdDouble;
    public static final LLSDNodeType llsdInteger;
    public static final LLSDNodeType llsdKey;
    public static final LLSDNodeType llsdMap;
    public static final LLSDNodeType llsdRoot;
    public static final LLSDNodeType llsdString;
    public static final LLSDNodeType llsdURI;
    public static final LLSDNodeType llsdUUID;
    public static final LLSDNodeType llsdUndef;
    private static final Map tagMap;
    private final String tagName;

    private LLSDNodeType(String s, int i, String s1)
    {
        super(s, i);
        tagName = s1;
    }

    public static LLSDNodeType byTag(String s)
    {
        return (LLSDNodeType)tagMap.get(s);
    }

    public static LLSDNodeType valueOf(String s)
    {
        return (LLSDNodeType)Enum.valueOf(com/lumiyaviewer/lumiya/slproto/llsd/LLSDNodeType, s);
    }

    public static LLSDNodeType[] values()
    {
        return $VALUES;
    }

    static 
    {
        int i = 0;
        llsdRoot = new LLSDNodeType("llsdRoot", 0, "llsd");
        llsdUndef = new LLSDNodeType("llsdUndef", 1, "undef");
        llsdBoolean = new LLSDNodeType("llsdBoolean", 2, "boolean");
        llsdInteger = new LLSDNodeType("llsdInteger", 3, "integer");
        llsdDouble = new LLSDNodeType("llsdDouble", 4, "real");
        llsdUUID = new LLSDNodeType("llsdUUID", 5, "uuid");
        llsdString = new LLSDNodeType("llsdString", 6, "string");
        llsdDate = new LLSDNodeType("llsdDate", 7, "date");
        llsdURI = new LLSDNodeType("llsdURI", 8, "uri");
        llsdBinary = new LLSDNodeType("llsdBinary", 9, "binary");
        llsdArray = new LLSDNodeType("llsdArray", 10, "array");
        llsdMap = new LLSDNodeType("llsdMap", 11, "map");
        llsdKey = new LLSDNodeType("llsdKey", 12, "key");
        $VALUES = (new LLSDNodeType[] {
            llsdRoot, llsdUndef, llsdBoolean, llsdInteger, llsdDouble, llsdUUID, llsdString, llsdDate, llsdURI, llsdBinary, 
            llsdArray, llsdMap, llsdKey
        });
        tagMap = new HashMap(values().length * 2);
        LLSDNodeType allsdnodetype[] = values();
        for (int j = allsdnodetype.length; i < j; i++)
        {
            LLSDNodeType llsdnodetype = allsdnodetype[i];
            tagMap.put(llsdnodetype.tagName, llsdnodetype);
        }

    }
}
