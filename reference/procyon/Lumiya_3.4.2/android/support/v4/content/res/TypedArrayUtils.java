// 
// Decompiled by Procyon v0.6.0
// 

package android.support.v4.content.res;

import android.util.AttributeSet;
import android.content.res.Resources$Theme;
import android.content.res.Resources;
import android.support.annotation.AnyRes;
import android.support.annotation.ColorInt;
import org.xmlpull.v1.XmlPullParser;
import android.support.annotation.NonNull;
import android.graphics.drawable.Drawable;
import android.support.annotation.StyleableRes;
import android.content.res.TypedArray;
import android.util.TypedValue;
import android.content.Context;
import android.support.annotation.RestrictTo;

@RestrictTo({ RestrictTo.Scope.LIBRARY_GROUP })
public class TypedArrayUtils
{
    private static final String NAMESPACE = "http://schemas.android.com/apk/res/android";
    
    public static int getAttr(final Context context, final int n, final int n2) {
        final TypedValue typedValue = new TypedValue();
        context.getTheme().resolveAttribute(n, typedValue, true);
        if (typedValue.resourceId == 0) {
            return n2;
        }
        return n;
    }
    
    public static boolean getBoolean(final TypedArray typedArray, @StyleableRes final int n, @StyleableRes final int n2, final boolean b) {
        return typedArray.getBoolean(n, typedArray.getBoolean(n2, b));
    }
    
    public static Drawable getDrawable(final TypedArray typedArray, @StyleableRes final int n, @StyleableRes final int n2) {
        final Drawable drawable = typedArray.getDrawable(n);
        Drawable drawable2;
        if (drawable != null) {
            drawable2 = drawable;
        }
        else {
            drawable2 = typedArray.getDrawable(n2);
        }
        return drawable2;
    }
    
    public static int getInt(final TypedArray typedArray, @StyleableRes final int n, @StyleableRes final int n2, final int n3) {
        return typedArray.getInt(n, typedArray.getInt(n2, n3));
    }
    
    public static boolean getNamedBoolean(@NonNull final TypedArray typedArray, @NonNull final XmlPullParser xmlPullParser, final String s, @StyleableRes final int n, final boolean b) {
        if (hasAttribute(xmlPullParser, s)) {
            return typedArray.getBoolean(n, b);
        }
        return b;
    }
    
    @ColorInt
    public static int getNamedColor(@NonNull final TypedArray typedArray, @NonNull final XmlPullParser xmlPullParser, final String s, @StyleableRes final int n, @ColorInt final int n2) {
        if (hasAttribute(xmlPullParser, s)) {
            return typedArray.getColor(n, n2);
        }
        return n2;
    }
    
    public static float getNamedFloat(@NonNull final TypedArray typedArray, @NonNull final XmlPullParser xmlPullParser, @NonNull final String s, @StyleableRes final int n, final float n2) {
        if (hasAttribute(xmlPullParser, s)) {
            return typedArray.getFloat(n, n2);
        }
        return n2;
    }
    
    public static int getNamedInt(@NonNull final TypedArray typedArray, @NonNull final XmlPullParser xmlPullParser, final String s, @StyleableRes final int n, final int n2) {
        if (hasAttribute(xmlPullParser, s)) {
            return typedArray.getInt(n, n2);
        }
        return n2;
    }
    
    @AnyRes
    public static int getNamedResourceId(@NonNull final TypedArray typedArray, @NonNull final XmlPullParser xmlPullParser, final String s, @StyleableRes final int n, @AnyRes final int n2) {
        if (hasAttribute(xmlPullParser, s)) {
            return typedArray.getResourceId(n, n2);
        }
        return n2;
    }
    
    public static String getNamedString(@NonNull final TypedArray typedArray, @NonNull final XmlPullParser xmlPullParser, final String s, @StyleableRes final int n) {
        if (hasAttribute(xmlPullParser, s)) {
            return typedArray.getString(n);
        }
        return null;
    }
    
    @AnyRes
    public static int getResourceId(final TypedArray typedArray, @StyleableRes final int n, @StyleableRes final int n2, @AnyRes final int n3) {
        return typedArray.getResourceId(n, typedArray.getResourceId(n2, n3));
    }
    
    public static String getString(final TypedArray typedArray, @StyleableRes final int n, @StyleableRes final int n2) {
        final String string = typedArray.getString(n);
        String string2;
        if (string != null) {
            string2 = string;
        }
        else {
            string2 = typedArray.getString(n2);
        }
        return string2;
    }
    
    public static CharSequence getText(final TypedArray typedArray, @StyleableRes final int n, @StyleableRes final int n2) {
        final CharSequence text = typedArray.getText(n);
        CharSequence text2;
        if (text != null) {
            text2 = text;
        }
        else {
            text2 = typedArray.getText(n2);
        }
        return text2;
    }
    
    public static CharSequence[] getTextArray(final TypedArray typedArray, @StyleableRes final int n, @StyleableRes final int n2) {
        final CharSequence[] textArray = typedArray.getTextArray(n);
        CharSequence[] textArray2;
        if (textArray != null) {
            textArray2 = textArray;
        }
        else {
            textArray2 = typedArray.getTextArray(n2);
        }
        return textArray2;
    }
    
    public static boolean hasAttribute(@NonNull final XmlPullParser xmlPullParser, @NonNull final String s) {
        return xmlPullParser.getAttributeValue("http://schemas.android.com/apk/res/android", s) != null;
    }
    
    public static TypedArray obtainAttributes(final Resources resources, final Resources$Theme resources$Theme, final AttributeSet set, final int[] array) {
        if (resources$Theme != null) {
            return resources$Theme.obtainStyledAttributes(set, array, 0, 0);
        }
        return resources.obtainAttributes(set, array);
    }
    
    public static TypedValue peekNamedValue(final TypedArray typedArray, final XmlPullParser xmlPullParser, final String s, final int n) {
        if (hasAttribute(xmlPullParser, s)) {
            return typedArray.peekValue(n);
        }
        return null;
    }
}
