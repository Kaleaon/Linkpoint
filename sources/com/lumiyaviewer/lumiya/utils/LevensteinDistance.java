// Decompiled by Jad v1.5.8e. Copyright 2001 Pavel Kouznetsov.
// Jad home page: http://www.geocities.com/kpdus/jad.html
// Decompiler options: braces fieldsfirst space lnc 

package com.lumiyaviewer.lumiya.utils;

import java.lang.reflect.Array;

public class LevensteinDistance
{

    public LevensteinDistance()
    {
    }

    public static int computeLevensteinDistance(CharSequence charsequence, CharSequence charsequence1)
    {
        int ai[][] = (int[][])Array.newInstance(Integer.TYPE, new int[] {
            charsequence.length() + 1, charsequence1.length() + 1
        });
        for (int i = 0; i <= charsequence.length(); i++)
        {
            ai[i][0] = i;
        }

        for (int j = 1; j <= charsequence1.length(); j++)
        {
            ai[0][j] = j;
        }

        for (int k = 1; k <= charsequence.length(); k++)
        {
            int l = 1;
            while (l <= charsequence1.length()) 
            {
                int ai1[] = ai[k];
                int j1 = ai[k - 1][l];
                int k1 = ai[k][l - 1];
                int l1 = ai[k - 1][l - 1];
                int i1;
                if (charsequence.charAt(k - 1) == charsequence1.charAt(l - 1))
                {
                    i1 = 0;
                } else
                {
                    i1 = 1;
                }
                ai1[l] = minimum(j1 + 1, k1 + 1, i1 + l1);
                l++;
            }
        }

        return ai[charsequence.length()][charsequence1.length()];
    }

    private static int minimum(int i, int j, int k)
    {
        return Math.min(Math.min(i, j), k);
    }
}
