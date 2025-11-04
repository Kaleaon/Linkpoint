// 
// Decompiled by Procyon v0.6.0
// 

package android.support.v4.util;

import android.support.annotation.RestrictTo;

@RestrictTo({ RestrictTo.Scope.LIBRARY_GROUP })
public class DebugUtils
{
    public static void buildShortClassTag(final Object o, final StringBuilder sb) {
        if (o != null) {
            String str = o.getClass().getSimpleName();
            if (str == null || str.length() <= 0) {
                str = o.getClass().getName();
                final int lastIndex = str.lastIndexOf(46);
                if (lastIndex > 0) {
                    str = str.substring(lastIndex + 1);
                }
            }
            sb.append(str);
            sb.append('{');
            sb.append(Integer.toHexString(System.identityHashCode(o)));
        }
        else {
            sb.append("null");
        }
    }
}
