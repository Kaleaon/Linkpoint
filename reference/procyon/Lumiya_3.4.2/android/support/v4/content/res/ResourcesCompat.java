// 
// Decompiled by Procyon v0.6.0
// 

package android.support.v4.content.res;

import java.io.IOException;
import org.xmlpull.v1.XmlPullParserException;
import android.util.Log;
import org.xmlpull.v1.XmlPullParser;
import android.support.v4.graphics.TypefaceCompat;
import android.support.annotation.RestrictTo;
import android.widget.TextView;
import android.util.TypedValue;
import android.graphics.Typeface;
import android.support.annotation.FontRes;
import android.content.Context;
import android.graphics.drawable.Drawable;
import android.support.annotation.DrawableRes;
import android.content.res.ColorStateList;
import android.support.annotation.ColorInt;
import android.content.res.Resources$NotFoundException;
import android.os.Build$VERSION;
import android.support.annotation.Nullable;
import android.content.res.Resources$Theme;
import android.support.annotation.ColorRes;
import android.support.annotation.NonNull;
import android.content.res.Resources;

public final class ResourcesCompat
{
    private static final String TAG = "ResourcesCompat";
    
    private ResourcesCompat() {
    }
    
    @ColorInt
    public static int getColor(@NonNull final Resources resources, @ColorRes final int n, @Nullable final Resources$Theme resources$Theme) throws Resources$NotFoundException {
        if (Build$VERSION.SDK_INT < 23) {
            return resources.getColor(n);
        }
        return resources.getColor(n, resources$Theme);
    }
    
    @Nullable
    public static ColorStateList getColorStateList(@NonNull final Resources resources, @ColorRes final int n, @Nullable final Resources$Theme resources$Theme) throws Resources$NotFoundException {
        if (Build$VERSION.SDK_INT < 23) {
            return resources.getColorStateList(n);
        }
        return resources.getColorStateList(n, resources$Theme);
    }
    
    @Nullable
    public static Drawable getDrawable(@NonNull final Resources resources, @DrawableRes final int n, @Nullable final Resources$Theme resources$Theme) throws Resources$NotFoundException {
        if (Build$VERSION.SDK_INT < 21) {
            return resources.getDrawable(n);
        }
        return resources.getDrawable(n, resources$Theme);
    }
    
    @Nullable
    public static Drawable getDrawableForDensity(@NonNull final Resources resources, @DrawableRes final int n, final int n2, @Nullable final Resources$Theme resources$Theme) throws Resources$NotFoundException {
        if (Build$VERSION.SDK_INT >= 21) {
            return resources.getDrawableForDensity(n, n2, resources$Theme);
        }
        if (Build$VERSION.SDK_INT < 15) {
            return resources.getDrawable(n);
        }
        return resources.getDrawableForDensity(n, n2);
    }
    
    @Nullable
    public static Typeface getFont(@NonNull final Context context, @FontRes final int n) throws Resources$NotFoundException {
        if (!context.isRestricted()) {
            return loadFont(context, n, new TypedValue(), 0, null);
        }
        return null;
    }
    
    @RestrictTo({ RestrictTo.Scope.LIBRARY_GROUP })
    public static Typeface getFont(@NonNull final Context context, @FontRes final int n, final TypedValue typedValue, final int n2, @Nullable final TextView textView) throws Resources$NotFoundException {
        if (!context.isRestricted()) {
            return loadFont(context, n, typedValue, n2, textView);
        }
        return null;
    }
    
    private static Typeface loadFont(@NonNull final Context context, final int i, final TypedValue typedValue, final int n, @Nullable final TextView textView) {
        final Resources resources = context.getResources();
        resources.getValue(i, typedValue, true);
        final Typeface loadFont = loadFont(context, resources, typedValue, i, n, textView);
        if (loadFont == null) {
            throw new Resources$NotFoundException("Font resource ID #0x" + Integer.toHexString(i));
        }
        return loadFont;
    }
    
    private static Typeface loadFont(@NonNull final Context context, final Resources resources, TypedValue string, final int i, final int n, @Nullable final TextView textView) {
        Label_0064: {
            if (string.string == null) {
                break Label_0064;
            }
            string = (TypedValue)string.string.toString();
            Label_0116: {
                if (!((String)string).startsWith("res/")) {
                    break Label_0116;
                }
                final Typeface fromCache = TypefaceCompat.findFromCache(resources, i, n);
                if (fromCache != null) {
                    return fromCache;
                }
                try {
                    if (!((String)string).toLowerCase().endsWith(".xml")) {
                        return TypefaceCompat.createFromResourcesFontFile(context, resources, i, (String)string, n);
                    }
                    final FontResourcesParserCompat.FamilyResourceEntry parse = FontResourcesParserCompat.parse((XmlPullParser)resources.getXml(i), resources);
                    if (parse != null) {
                        return TypefaceCompat.createFromResourcesFamilyXml(context, parse, resources, i, n, textView);
                    }
                    Log.e("ResourcesCompat", "Failed to find font-family tag");
                    return null;
                    return null;
                    throw new Resources$NotFoundException("Resource \"" + resources.getResourceName(i) + "\" (" + Integer.toHexString(i) + ") is not a Font: " + string);
                }
                catch (final XmlPullParserException ex) {
                    Log.e("ResourcesCompat", "Failed to parse xml resource " + (String)string, (Throwable)ex);
                }
                catch (final IOException ex2) {
                    Log.e("ResourcesCompat", "Failed to read xml resource " + (String)string, (Throwable)ex2);
                    goto Label_0187;
                }
            }
        }
    }
}
