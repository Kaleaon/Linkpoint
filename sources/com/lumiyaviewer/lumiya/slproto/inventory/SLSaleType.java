// Decompiled by Jad v1.5.8e. Copyright 2001 Pavel Kouznetsov.
// Jad home page: http://www.geocities.com/kpdus/jad.html
// Decompiler options: braces fieldsfirst space lnc 

package com.lumiyaviewer.lumiya.slproto.inventory;


public final class SLSaleType extends Enum
{

    private static final SLSaleType $VALUES[];
    public static final SLSaleType FS_CONTENTS;
    public static final SLSaleType FS_COPY;
    public static final SLSaleType FS_NOT;
    public static final SLSaleType FS_ORIGINAL;
    public static final SLSaleType FS_UNKNOWN;
    private String stringCode;
    private int typeCode;

    private SLSaleType(String s, int i, int j, String s1)
    {
        super(s, i);
        typeCode = j;
        stringCode = s1;
    }

    public static SLSaleType getByString(String s)
    {
        SLSaleType aslsaletype[] = values();
        int i = 0;
        for (int j = aslsaletype.length; i < j; i++)
        {
            SLSaleType slsaletype = aslsaletype[i];
            if (slsaletype.stringCode.equalsIgnoreCase(s))
            {
                return slsaletype;
            }
        }

        return FS_UNKNOWN;
    }

    public static SLSaleType getByType(int i)
    {
        SLSaleType aslsaletype[] = values();
        int j = 0;
        for (int k = aslsaletype.length; j < k; j++)
        {
            SLSaleType slsaletype = aslsaletype[j];
            if (slsaletype.typeCode == i)
            {
                return slsaletype;
            }
        }

        return FS_UNKNOWN;
    }

    public static SLSaleType valueOf(String s)
    {
        return (SLSaleType)Enum.valueOf(com/lumiyaviewer/lumiya/slproto/inventory/SLSaleType, s);
    }

    public static SLSaleType[] values()
    {
        return $VALUES;
    }

    public String getStringCode()
    {
        return stringCode;
    }

    public int getTypeCode()
    {
        return typeCode;
    }

    static 
    {
        FS_NOT = new SLSaleType("FS_NOT", 0, 0, "not");
        FS_ORIGINAL = new SLSaleType("FS_ORIGINAL", 1, 1, "orig");
        FS_COPY = new SLSaleType("FS_COPY", 2, 2, "copy");
        FS_CONTENTS = new SLSaleType("FS_CONTENTS", 3, 3, "cntn");
        FS_UNKNOWN = new SLSaleType("FS_UNKNOWN", 4, -1, "unknown");
        $VALUES = (new SLSaleType[] {
            FS_NOT, FS_ORIGINAL, FS_COPY, FS_CONTENTS, FS_UNKNOWN
        });
    }
}
