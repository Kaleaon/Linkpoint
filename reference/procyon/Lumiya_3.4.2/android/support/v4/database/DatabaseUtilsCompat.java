// 
// Decompiled by Procyon v0.6.0
// 

package android.support.v4.database;

import android.text.TextUtils;

public final class DatabaseUtilsCompat
{
    private DatabaseUtilsCompat() {
    }
    
    public static String[] appendSelectionArgs(final String[] array, final String[] array2) {
        if (array != null && array.length != 0) {
            final String[] array3 = new String[array.length + array2.length];
            System.arraycopy(array, 0, array3, 0, array.length);
            System.arraycopy(array2, 0, array3, array.length, array2.length);
            return array3;
        }
        return array2;
    }
    
    public static String concatenateWhere(final String str, final String str2) {
        if (TextUtils.isEmpty((CharSequence)str)) {
            return str2;
        }
        if (!TextUtils.isEmpty((CharSequence)str2)) {
            return "(" + str + ") AND (" + str2 + ")";
        }
        return str;
    }
}
