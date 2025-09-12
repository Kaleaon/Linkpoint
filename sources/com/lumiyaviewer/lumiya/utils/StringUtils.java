// Decompiled by Jad v1.5.8e. Copyright 2001 Pavel Kouznetsov.
// Jad home page: http://www.geocities.com/kpdus/jad.html
// Decompiler options: braces fieldsfirst space lnc 

package com.lumiyaviewer.lumiya.utils;


public final class StringUtils
{

    public StringUtils()
    {
    }

    public static int countOccurrences(String s, char c)
    {
        int i = 0;
        int j;
        int k;
        for (j = 0; i < s.length(); j = k)
        {
            k = j;
            if (s.charAt(i) == c)
            {
                k = j + 1;
            }
            i++;
        }

        return j;
    }

    public static String toString(Object obj)
    {
        if (obj != null)
        {
            return obj.toString();
        } else
        {
            return null;
        }
    }
}
