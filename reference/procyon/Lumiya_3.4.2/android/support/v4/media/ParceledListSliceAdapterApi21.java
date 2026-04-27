// 
// Decompiled by Procyon v0.6.0
// 

package android.support.v4.media;

import java.lang.reflect.InvocationTargetException;
import android.media.browse.MediaBrowser$MediaItem;
import java.util.List;
import java.lang.reflect.Constructor;
import android.support.annotation.RequiresApi;

@RequiresApi(21)
class ParceledListSliceAdapterApi21
{
    private static Constructor sConstructor;
    
    static {
        try {
            ParceledListSliceAdapterApi21.sConstructor = Class.forName("android.content.pm.ParceledListSlice").getConstructor(List.class);
        }
        catch (final ClassNotFoundException | NoSuchMethodException ex) {
            ((Throwable)ex).printStackTrace();
        }
    }
    
    static Object newInstance(final List<MediaBrowser$MediaItem> list) {
        try {
            return ParceledListSliceAdapterApi21.sConstructor.newInstance(list);
        }
        catch (final InstantiationException | IllegalAccessException | InvocationTargetException ex) {
            ((Throwable)ex).printStackTrace();
            return null;
        }
    }
}
