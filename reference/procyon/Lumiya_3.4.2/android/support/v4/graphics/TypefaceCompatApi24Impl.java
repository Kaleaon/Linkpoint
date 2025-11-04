// 
// Decompiled by Procyon v0.6.0
// 

package android.support.v4.graphics;

import android.net.Uri;
import android.support.v4.util.SimpleArrayMap;
import android.support.annotation.NonNull;
import android.support.v4.provider.FontsContractCompat;
import android.support.annotation.Nullable;
import android.os.CancellationSignal;
import android.content.res.Resources;
import android.support.v4.content.res.FontResourcesParserCompat;
import android.content.Context;
import java.lang.reflect.InvocationTargetException;
import android.util.Log;
import java.lang.reflect.Array;
import android.graphics.Typeface;
import java.util.List;
import java.nio.ByteBuffer;
import java.lang.reflect.Constructor;
import java.lang.reflect.Method;
import android.support.annotation.RestrictTo;
import android.support.annotation.RequiresApi;

@RequiresApi(24)
@RestrictTo({ RestrictTo.Scope.LIBRARY_GROUP })
class TypefaceCompatApi24Impl extends TypefaceCompatBaseImpl
{
    private static final String ADD_FONT_WEIGHT_STYLE_METHOD = "addFontWeightStyle";
    private static final String CREATE_FROM_FAMILIES_WITH_DEFAULT_METHOD = "createFromFamiliesWithDefault";
    private static final String FONT_FAMILY_CLASS = "android.graphics.FontFamily";
    private static final String TAG = "TypefaceCompatApi24Impl";
    private static final Method sAddFontWeightStyle;
    private static final Method sCreateFromFamiliesWithDefault;
    private static final Class sFontFamily;
    private static final Constructor sFontFamilyCtor;
    
    static {
        Method sAddFontWeightStyle2 = null;
        while (true) {
            try {
                final Class<?> forName = Class.forName("android.graphics.FontFamily");
                final Constructor<?> constructor = forName.getConstructor((Class<?>[])new Class[0]);
                final Method method = forName.getMethod("addFontWeightStyle", ByteBuffer.class, Integer.TYPE, List.class, Integer.TYPE, Boolean.TYPE);
                final Method method2 = Typeface.class.getMethod("createFromFamiliesWithDefault", Array.newInstance(forName, 1).getClass());
                sAddFontWeightStyle2 = method;
                sFontFamilyCtor = constructor;
                sFontFamily = forName;
                sAddFontWeightStyle = sAddFontWeightStyle2;
                sCreateFromFamiliesWithDefault = method2;
            }
            catch (final ClassNotFoundException | NoSuchMethodException ex) {
                Log.e("TypefaceCompatApi24Impl", ex.getClass().getName(), (Throwable)ex);
                final Method method2 = null;
                final Constructor<?> constructor = null;
                final Class<?> forName = null;
                continue;
            }
            break;
        }
    }
    
    private static boolean addFontWeightStyle(final Object obj, final ByteBuffer byteBuffer, final int i, final int j, final boolean b) {
        try {
            return (boolean)TypefaceCompatApi24Impl.sAddFontWeightStyle.invoke(obj, byteBuffer, i, null, j, b);
        }
        catch (final IllegalAccessException | InvocationTargetException cause) {
            throw new RuntimeException((Throwable)cause);
        }
    }
    
    private static Typeface createFromFamiliesWithDefault(final Object o) {
        try {
            final Object instance = Array.newInstance(TypefaceCompatApi24Impl.sFontFamily, 1);
            Array.set(instance, 0, o);
            return (Typeface)TypefaceCompatApi24Impl.sCreateFromFamiliesWithDefault.invoke(null, instance);
        }
        catch (final IllegalAccessException | InvocationTargetException cause) {
            throw new RuntimeException((Throwable)cause);
        }
    }
    
    public static boolean isUsable() {
        if (TypefaceCompatApi24Impl.sAddFontWeightStyle == null) {
            Log.w("TypefaceCompatApi24Impl", "Unable to collect necessary private methods.Fallback to legacy implementation.");
        }
        return TypefaceCompatApi24Impl.sAddFontWeightStyle != null;
    }
    
    private static Object newFamily() {
        try {
            return TypefaceCompatApi24Impl.sFontFamilyCtor.newInstance(new Object[0]);
        }
        catch (final IllegalAccessException | InstantiationException | InvocationTargetException cause) {
            throw new RuntimeException((Throwable)cause);
        }
    }
    
    @Override
    public Typeface createFromFontFamilyFilesResourceEntry(final Context context, final FontResourcesParserCompat.FontFamilyFilesResourceEntry fontFamilyFilesResourceEntry, final Resources resources, int i) {
        final Object family = newFamily();
        final FontResourcesParserCompat.FontFileResourceEntry[] entries = fontFamilyFilesResourceEntry.getEntries();
        int length;
        FontResourcesParserCompat.FontFileResourceEntry fontFileResourceEntry;
        for (length = entries.length, i = 0; i < length; ++i) {
            fontFileResourceEntry = entries[i];
            if (!addFontWeightStyle(family, TypefaceCompatUtil.copyToDirectBuffer(context, resources, fontFileResourceEntry.getResourceId()), 0, fontFileResourceEntry.getWeight(), fontFileResourceEntry.isItalic())) {
                return null;
            }
        }
        return createFromFamiliesWithDefault(family);
    }
    
    @Override
    public Typeface createFromFontInfo(final Context context, @Nullable final CancellationSignal cancellationSignal, @NonNull final FontsContractCompat.FontInfo[] array, int i) {
        final Object family = newFamily();
        final SimpleArrayMap simpleArrayMap = new SimpleArrayMap();
        int length;
        FontsContractCompat.FontInfo fontInfo;
        Uri uri;
        ByteBuffer mmap;
        for (length = array.length, i = 0; i < length; ++i) {
            fontInfo = array[i];
            uri = fontInfo.getUri();
            mmap = simpleArrayMap.get(uri);
            if (mmap == null) {
                mmap = TypefaceCompatUtil.mmap(context, cancellationSignal, uri);
                simpleArrayMap.put(uri, mmap);
            }
            if (!addFontWeightStyle(family, mmap, fontInfo.getTtcIndex(), fontInfo.getWeight(), fontInfo.isItalic())) {
                return null;
            }
        }
        return createFromFamiliesWithDefault(family);
    }
}
