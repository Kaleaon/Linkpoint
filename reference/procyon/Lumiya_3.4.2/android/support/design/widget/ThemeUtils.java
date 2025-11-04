// 
// Decompiled by Procyon v0.6.0
// 

package android.support.design.widget;

import android.content.res.TypedArray;
import android.content.Context;
import android.support.v7.appcompat.R;

class ThemeUtils
{
    private static final int[] APPCOMPAT_CHECK_ATTRS;
    
    static {
        APPCOMPAT_CHECK_ATTRS = new int[] { R.attr.colorPrimary };
    }
    
    static void checkAppCompatTheme(final Context context) {
        int n = 0;
        final TypedArray obtainStyledAttributes = context.obtainStyledAttributes(ThemeUtils.APPCOMPAT_CHECK_ATTRS);
        if (!obtainStyledAttributes.hasValue(0)) {
            n = 1;
        }
        obtainStyledAttributes.recycle();
        if (n == 0) {
            return;
        }
        throw new IllegalArgumentException("You need to use a Theme.AppCompat theme (or descendant) with the design library.");
    }
}
