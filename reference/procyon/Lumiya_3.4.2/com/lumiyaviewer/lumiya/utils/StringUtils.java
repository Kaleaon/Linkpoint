// 
// Decompiled by Procyon v0.6.0
// 

package com.lumiyaviewer.lumiya.utils;

import javax.annotation.Nullable;

public final class StringUtils
{
    public static int countOccurrences(final String s, final char c) {
        int i = 0;
        int n = 0;
        while (i < s.length()) {
            int n2 = n;
            if (s.charAt(i) == c) {
                n2 = n + 1;
            }
            ++i;
            n = n2;
        }
        return n;
    }
    
    @Nullable
    public static String toString(@Nullable final Object o) {
        if (o != null) {
            return o.toString();
        }
        return null;
    }
}
