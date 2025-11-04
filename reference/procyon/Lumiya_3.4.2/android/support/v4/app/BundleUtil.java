// 
// Decompiled by Procyon v0.6.0
// 

package android.support.v4.app;

import android.os.Parcelable;
import java.util.Arrays;
import android.os.Bundle;

class BundleUtil
{
    public static Bundle[] getBundleArrayFromBundle(final Bundle bundle, final String s) {
        final Parcelable[] parcelableArray = bundle.getParcelableArray(s);
        if (!(parcelableArray instanceof Bundle[]) && parcelableArray != null) {
            final Bundle[] array = Arrays.copyOf((Bundle[])parcelableArray, ((Bundle[])parcelableArray).length, (Class<? extends Bundle[]>)Bundle[].class);
            bundle.putParcelableArray(s, (Parcelable[])array);
            return array;
        }
        return (Bundle[])parcelableArray;
    }
}
