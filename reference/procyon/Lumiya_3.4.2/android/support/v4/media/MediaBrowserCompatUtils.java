// 
// Decompiled by Procyon v0.6.0
// 

package android.support.v4.media;

import android.os.Bundle;
import android.support.annotation.RestrictTo;

@RestrictTo({ RestrictTo.Scope.LIBRARY_GROUP })
public class MediaBrowserCompatUtils
{
    public static boolean areSameOptions(final Bundle bundle, final Bundle bundle2) {
        final boolean b = true;
        final boolean b2 = true;
        boolean b3 = true;
        if (bundle == bundle2) {
            return true;
        }
        if (bundle == null) {
            if (bundle2.getInt("android.media.browse.extra.PAGE", -1) == -1) {
                final boolean b4 = b;
                if (bundle2.getInt("android.media.browse.extra.PAGE_SIZE", -1) == -1) {
                    return b4;
                }
            }
            return false;
        }
        if (bundle2 != null) {
            if (bundle.getInt("android.media.browse.extra.PAGE", -1) != bundle2.getInt("android.media.browse.extra.PAGE", -1) || bundle.getInt("android.media.browse.extra.PAGE_SIZE", -1) != bundle2.getInt("android.media.browse.extra.PAGE_SIZE", -1)) {
                b3 = false;
            }
            return b3;
        }
        if (bundle.getInt("android.media.browse.extra.PAGE", -1) == -1) {
            final boolean b5 = b2;
            if (bundle.getInt("android.media.browse.extra.PAGE_SIZE", -1) == -1) {
                return b5;
            }
        }
        return false;
    }
    
    public static boolean hasDuplicatedItems(final Bundle bundle, final Bundle bundle2) {
        int int1;
        if (bundle != null) {
            int1 = bundle.getInt("android.media.browse.extra.PAGE", -1);
        }
        else {
            int1 = -1;
        }
        int int2;
        if (bundle2 != null) {
            int2 = bundle2.getInt("android.media.browse.extra.PAGE", -1);
        }
        else {
            int2 = -1;
        }
        int int3;
        if (bundle != null) {
            int3 = bundle.getInt("android.media.browse.extra.PAGE_SIZE", -1);
        }
        else {
            int3 = -1;
        }
        int int4;
        if (bundle2 != null) {
            int4 = bundle2.getInt("android.media.browse.extra.PAGE_SIZE", -1);
        }
        else {
            int4 = -1;
        }
        int n;
        int n2;
        if (int1 != -1 && int3 != -1) {
            n = int3 * int1;
            n2 = n + int3 - 1;
        }
        else {
            n2 = Integer.MAX_VALUE;
            n = 0;
        }
        int n3;
        int n4;
        if (int2 != -1 && int4 != -1) {
            n3 = int4 * int2;
            n4 = n3 + int4 - 1;
        }
        else {
            n4 = Integer.MAX_VALUE;
            n3 = 0;
        }
        return (n <= n3 && n3 <= n2) || (n <= n4 && n4 <= n2);
    }
}
