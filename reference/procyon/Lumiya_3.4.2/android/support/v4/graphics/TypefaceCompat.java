// 
// Decompiled by Procyon v0.6.0
// 

package android.support.v4.graphics;

import android.widget.TextView;
import android.content.res.Resources;
import android.support.v4.content.res.FontResourcesParserCompat;
import android.support.annotation.NonNull;
import android.support.v4.provider.FontsContractCompat;
import android.support.annotation.Nullable;
import android.os.CancellationSignal;
import android.content.Context;
import android.os.Build$VERSION;
import android.graphics.Typeface;
import android.support.v4.util.LruCache;
import android.support.annotation.RestrictTo;

@RestrictTo({ RestrictTo.Scope.LIBRARY_GROUP })
public class TypefaceCompat
{
    private static final String TAG = "TypefaceCompat";
    private static final LruCache<String, Typeface> sTypefaceCache;
    private static final TypefaceCompatImpl sTypefaceCompatImpl;
    
    static {
        if (Build$VERSION.SDK_INT < 26) {
            if (Build$VERSION.SDK_INT >= 24 && TypefaceCompatApi24Impl.isUsable()) {
                sTypefaceCompatImpl = (TypefaceCompatImpl)new TypefaceCompatApi24Impl();
            }
            else if (Build$VERSION.SDK_INT < 21) {
                sTypefaceCompatImpl = (TypefaceCompatImpl)new TypefaceCompatBaseImpl();
            }
            else {
                sTypefaceCompatImpl = (TypefaceCompatImpl)new TypefaceCompatApi21Impl();
            }
        }
        else {
            sTypefaceCompatImpl = (TypefaceCompatImpl)new TypefaceCompatApi26Impl();
        }
        sTypefaceCache = new LruCache<String, Typeface>(16);
    }
    
    private TypefaceCompat() {
    }
    
    public static Typeface createFromFontInfo(final Context context, @Nullable final CancellationSignal cancellationSignal, @NonNull final FontsContractCompat.FontInfo[] array, final int n) {
        return TypefaceCompat.sTypefaceCompatImpl.createFromFontInfo(context, cancellationSignal, array, n);
    }
    
    public static Typeface createFromResourcesFamilyXml(final Context context, final FontResourcesParserCompat.FamilyResourceEntry familyResourceEntry, final Resources resources, final int n, final int n2, @Nullable final TextView textView) {
        Typeface typeface;
        if (!(familyResourceEntry instanceof FontResourcesParserCompat.ProviderResourceEntry)) {
            typeface = TypefaceCompat.sTypefaceCompatImpl.createFromFontFamilyFilesResourceEntry(context, (FontResourcesParserCompat.FontFamilyFilesResourceEntry)familyResourceEntry, resources, n2);
        }
        else {
            final FontResourcesParserCompat.ProviderResourceEntry providerResourceEntry = (FontResourcesParserCompat.ProviderResourceEntry)familyResourceEntry;
            typeface = FontsContractCompat.getFontSync(context, providerResourceEntry.getRequest(), textView, providerResourceEntry.getFetchStrategy(), providerResourceEntry.getTimeout(), n2);
        }
        if (typeface != null) {
            TypefaceCompat.sTypefaceCache.put(createResourceUid(resources, n, n2), typeface);
        }
        return typeface;
    }
    
    @Nullable
    public static Typeface createFromResourcesFontFile(final Context context, final Resources resources, final int n, final String s, final int n2) {
        final Typeface fromResourcesFontFile = TypefaceCompat.sTypefaceCompatImpl.createFromResourcesFontFile(context, resources, n, s, n2);
        if (fromResourcesFontFile != null) {
            TypefaceCompat.sTypefaceCache.put(createResourceUid(resources, n, n2), fromResourcesFontFile);
        }
        return fromResourcesFontFile;
    }
    
    private static String createResourceUid(final Resources resources, final int i, final int j) {
        return resources.getResourcePackageName(i) + "-" + i + "-" + j;
    }
    
    public static Typeface findFromCache(final Resources resources, final int n, final int n2) {
        return TypefaceCompat.sTypefaceCache.get(createResourceUid(resources, n, n2));
    }
    
    interface TypefaceCompatImpl
    {
        Typeface createFromFontFamilyFilesResourceEntry(final Context p0, final FontResourcesParserCompat.FontFamilyFilesResourceEntry p1, final Resources p2, final int p3);
        
        Typeface createFromFontInfo(final Context p0, @Nullable final CancellationSignal p1, @NonNull final FontsContractCompat.FontInfo[] p2, final int p3);
        
        Typeface createFromResourcesFontFile(final Context p0, final Resources p1, final int p2, final String p3, final int p4);
    }
}
