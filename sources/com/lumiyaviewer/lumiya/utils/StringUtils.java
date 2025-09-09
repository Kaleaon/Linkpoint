package com.lumiyaviewer.lumiya.utils;

import javax.annotation.Nullable;

public final class StringUtils {
    public static int countOccurrences(String str, char c) {
        int i = 0;
        for (int i2 = 0; i2 < str.length(); i2++) {
            if (str.charAt(i2) == c) {
                i++;
            }
        }
        return i;
    }

    @Nullable
    public static String toString(@Nullable Object obj) {
        if (obj != null) {
            return obj.toString();
        }
        return null;
    }
}
