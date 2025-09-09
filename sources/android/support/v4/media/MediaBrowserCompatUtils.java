package android.support.v4.media;

import android.os.Bundle;
import android.support.annotation.RestrictTo;

@RestrictTo({RestrictTo.Scope.LIBRARY_GROUP})
public class MediaBrowserCompatUtils {
    public static boolean areSameOptions(Bundle bundle, Bundle bundle2) {
        if (bundle != bundle2) {
            return bundle != null ? bundle2 != null ? bundle.getInt(MediaBrowserCompat.EXTRA_PAGE, -1) == bundle2.getInt(MediaBrowserCompat.EXTRA_PAGE, -1) && bundle.getInt(MediaBrowserCompat.EXTRA_PAGE_SIZE, -1) == bundle2.getInt(MediaBrowserCompat.EXTRA_PAGE_SIZE, -1) : bundle.getInt(MediaBrowserCompat.EXTRA_PAGE, -1) == -1 && bundle.getInt(MediaBrowserCompat.EXTRA_PAGE_SIZE, -1) == -1 : bundle2.getInt(MediaBrowserCompat.EXTRA_PAGE, -1) == -1 && bundle2.getInt(MediaBrowserCompat.EXTRA_PAGE_SIZE, -1) == -1;
        }
        return true;
    }

    public static boolean hasDuplicatedItems(Bundle bundle, Bundle bundle2) {
        int i;
        int i2;
        int i3;
        int i4;
        int i5 = bundle != null ? bundle.getInt(MediaBrowserCompat.EXTRA_PAGE, -1) : -1;
        int i6 = bundle2 != null ? bundle2.getInt(MediaBrowserCompat.EXTRA_PAGE, -1) : -1;
        int i7 = bundle != null ? bundle.getInt(MediaBrowserCompat.EXTRA_PAGE_SIZE, -1) : -1;
        int i8 = bundle2 != null ? bundle2.getInt(MediaBrowserCompat.EXTRA_PAGE_SIZE, -1) : -1;
        if (i5 == -1 || i7 == -1) {
            i2 = Integer.MAX_VALUE;
            i = 0;
        } else {
            i = i7 * i5;
            i2 = (i + i7) - 1;
        }
        if (i6 == -1 || i8 == -1) {
            i4 = Integer.MAX_VALUE;
            i3 = 0;
        } else {
            i3 = i8 * i6;
            i4 = (i3 + i8) - 1;
        }
        if (i <= i3 && i3 <= i2) {
            return true;
        }
        return i <= i4 && i4 <= i2;
    }
}
