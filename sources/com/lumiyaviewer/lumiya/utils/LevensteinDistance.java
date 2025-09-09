package com.lumiyaviewer.lumiya.utils;

import java.lang.reflect.Array;

public class LevensteinDistance {
    public static int computeLevensteinDistance(CharSequence charSequence, CharSequence charSequence2) {
        int[][] iArr = (int[][]) Array.newInstance(Integer.TYPE, new int[]{charSequence.length() + 1, charSequence2.length() + 1});
        for (int i = 0; i <= charSequence.length(); i++) {
            iArr[i][0] = i;
        }
        for (int i2 = 1; i2 <= charSequence2.length(); i2++) {
            iArr[0][i2] = i2;
        }
        for (int i3 = 1; i3 <= charSequence.length(); i3++) {
            for (int i4 = 1; i4 <= charSequence2.length(); i4++) {
                iArr[i3][i4] = minimum(iArr[i3 - 1][i4] + 1, iArr[i3][i4 - 1] + 1, (charSequence.charAt(i3 + -1) == charSequence2.charAt(i4 + -1) ? 0 : 1) + iArr[i3 - 1][i4 - 1]);
            }
        }
        return iArr[charSequence.length()][charSequence2.length()];
    }

    private static int minimum(int i, int i2, int i3) {
        return Math.min(Math.min(i, i2), i3);
    }
}
