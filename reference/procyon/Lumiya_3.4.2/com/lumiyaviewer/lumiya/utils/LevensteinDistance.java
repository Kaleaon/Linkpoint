// 
// Decompiled by Procyon v0.6.0
// 

package com.lumiyaviewer.lumiya.utils;

public class LevensteinDistance
{
    public static int computeLevensteinDistance(final CharSequence charSequence, final CharSequence charSequence2) {
        final int[][] array = new int[charSequence.length() + 1][charSequence2.length() + 1];
        for (int i = 0; i <= charSequence.length(); ++i) {
            array[i][0] = i;
        }
        for (int j = 1; j <= charSequence2.length(); ++j) {
            array[0][j] = j;
        }
        for (int k = 1; k <= charSequence.length(); ++k) {
            for (int l = 1; l <= charSequence2.length(); ++l) {
                final int[] array2 = array[k];
                final int n = array[k - 1][l];
                final int n2 = array[k][l - 1];
                final int n3 = array[k - 1][l - 1];
                int n4;
                if (charSequence.charAt(k - 1) == charSequence2.charAt(l - 1)) {
                    n4 = 0;
                }
                else {
                    n4 = 1;
                }
                array2[l] = minimum(n + 1, n2 + 1, n4 + n3);
            }
        }
        return array[charSequence.length()][charSequence2.length()];
    }
    
    private static int minimum(final int a, final int b, final int b2) {
        return Math.min(Math.min(a, b), b2);
    }
}
