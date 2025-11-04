// 
// Decompiled by Procyon v0.6.0
// 

package android.support.v4.graphics.drawable;

import android.graphics.drawable.DrawableContainer$DrawableContainerState;
import android.graphics.drawable.DrawableContainer;
import android.graphics.drawable.InsetDrawable;
import android.util.Log;
import java.lang.reflect.Method;
import android.support.annotation.RequiresApi;
import android.graphics.PorterDuff$Mode;
import android.content.res.ColorStateList;
import android.support.annotation.ColorInt;
import java.io.IOException;
import org.xmlpull.v1.XmlPullParserException;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import org.xmlpull.v1.XmlPullParser;
import android.content.res.Resources;
import android.graphics.ColorFilter;
import android.content.res.Resources$Theme;
import android.support.annotation.NonNull;
import android.graphics.drawable.Drawable;
import android.os.Build$VERSION;

public final class DrawableCompat
{
    static final DrawableCompatBaseImpl IMPL;
    
    static {
        if (Build$VERSION.SDK_INT < 23) {
            if (Build$VERSION.SDK_INT < 21) {
                if (Build$VERSION.SDK_INT < 19) {
                    if (Build$VERSION.SDK_INT < 17) {
                        IMPL = new DrawableCompatBaseImpl();
                    }
                    else {
                        IMPL = (DrawableCompatBaseImpl)new DrawableCompatApi17Impl();
                    }
                }
                else {
                    IMPL = (DrawableCompatBaseImpl)new DrawableCompatApi19Impl();
                }
            }
            else {
                IMPL = (DrawableCompatBaseImpl)new DrawableCompatApi21Impl();
            }
        }
        else {
            IMPL = (DrawableCompatBaseImpl)new DrawableCompatApi23Impl();
        }
    }
    
    private DrawableCompat() {
    }
    
    public static void applyTheme(@NonNull final Drawable drawable, @NonNull final Resources$Theme resources$Theme) {
        DrawableCompat.IMPL.applyTheme(drawable, resources$Theme);
    }
    
    public static boolean canApplyTheme(@NonNull final Drawable drawable) {
        return DrawableCompat.IMPL.canApplyTheme(drawable);
    }
    
    public static void clearColorFilter(@NonNull final Drawable drawable) {
        DrawableCompat.IMPL.clearColorFilter(drawable);
    }
    
    public static int getAlpha(@NonNull final Drawable drawable) {
        return DrawableCompat.IMPL.getAlpha(drawable);
    }
    
    public static ColorFilter getColorFilter(@NonNull final Drawable drawable) {
        return DrawableCompat.IMPL.getColorFilter(drawable);
    }
    
    public static int getLayoutDirection(@NonNull final Drawable drawable) {
        return DrawableCompat.IMPL.getLayoutDirection(drawable);
    }
    
    public static void inflate(@NonNull final Drawable drawable, @NonNull final Resources resources, @NonNull final XmlPullParser xmlPullParser, @NonNull final AttributeSet set, @Nullable final Resources$Theme resources$Theme) throws XmlPullParserException, IOException {
        DrawableCompat.IMPL.inflate(drawable, resources, xmlPullParser, set, resources$Theme);
    }
    
    public static boolean isAutoMirrored(@NonNull final Drawable drawable) {
        return DrawableCompat.IMPL.isAutoMirrored(drawable);
    }
    
    public static void jumpToCurrentState(@NonNull final Drawable drawable) {
        DrawableCompat.IMPL.jumpToCurrentState(drawable);
    }
    
    public static void setAutoMirrored(@NonNull final Drawable drawable, final boolean b) {
        DrawableCompat.IMPL.setAutoMirrored(drawable, b);
    }
    
    public static void setHotspot(@NonNull final Drawable drawable, final float n, final float n2) {
        DrawableCompat.IMPL.setHotspot(drawable, n, n2);
    }
    
    public static void setHotspotBounds(@NonNull final Drawable drawable, final int n, final int n2, final int n3, final int n4) {
        DrawableCompat.IMPL.setHotspotBounds(drawable, n, n2, n3, n4);
    }
    
    public static boolean setLayoutDirection(@NonNull final Drawable drawable, final int n) {
        return DrawableCompat.IMPL.setLayoutDirection(drawable, n);
    }
    
    public static void setTint(@NonNull final Drawable drawable, @ColorInt final int n) {
        DrawableCompat.IMPL.setTint(drawable, n);
    }
    
    public static void setTintList(@NonNull final Drawable drawable, @Nullable final ColorStateList list) {
        DrawableCompat.IMPL.setTintList(drawable, list);
    }
    
    public static void setTintMode(@NonNull final Drawable drawable, @Nullable final PorterDuff$Mode porterDuff$Mode) {
        DrawableCompat.IMPL.setTintMode(drawable, porterDuff$Mode);
    }
    
    public static <T extends Drawable> T unwrap(@NonNull final Drawable drawable) {
        if (!(drawable instanceof DrawableWrapper)) {
            return (T)drawable;
        }
        return (T)((DrawableWrapper)drawable).getWrappedDrawable();
    }
    
    public static Drawable wrap(@NonNull final Drawable drawable) {
        return DrawableCompat.IMPL.wrap(drawable);
    }
    
    @RequiresApi(17)
    static class DrawableCompatApi17Impl extends DrawableCompatBaseImpl
    {
        private static final String TAG = "DrawableCompatApi17";
        private static Method sGetLayoutDirectionMethod;
        private static boolean sGetLayoutDirectionMethodFetched;
        private static Method sSetLayoutDirectionMethod;
        private static boolean sSetLayoutDirectionMethodFetched;
        
        @Override
        public int getLayoutDirection(final Drawable obj) {
            Label_0055: {
                Block_2: {
                    Label_0006: {
                        if (!DrawableCompatApi17Impl.sGetLayoutDirectionMethodFetched) {
                            while (true) {
                                try {
                                    (DrawableCompatApi17Impl.sGetLayoutDirectionMethod = Drawable.class.getDeclaredMethod("getLayoutDirection", (Class<?>[])new Class[0])).setAccessible(true);
                                    DrawableCompatApi17Impl.sGetLayoutDirectionMethodFetched = true;
                                    break Label_0006;
                                }
                                catch (final NoSuchMethodException ex) {
                                    Log.i("DrawableCompatApi17", "Failed to retrieve getLayoutDirection() method", (Throwable)ex);
                                    continue;
                                }
                                break;
                            }
                            break Block_2;
                        }
                    }
                    if (DrawableCompatApi17Impl.sGetLayoutDirectionMethod != null) {
                        break Label_0055;
                    }
                    return 0;
                }
                try {
                    return (int)DrawableCompatApi17Impl.sGetLayoutDirectionMethod.invoke(obj, new Object[0]);
                }
                catch (final Exception ex2) {
                    Log.i("DrawableCompatApi17", "Failed to invoke getLayoutDirection() via reflection", (Throwable)ex2);
                    DrawableCompatApi17Impl.sGetLayoutDirectionMethod = null;
                    return 0;
                }
            }
        }
        
        @Override
        public boolean setLayoutDirection(final Drawable obj, final int i) {
            Label_0061: {
                Block_2: {
                    Label_0006: {
                        if (!DrawableCompatApi17Impl.sSetLayoutDirectionMethodFetched) {
                            while (true) {
                                try {
                                    (DrawableCompatApi17Impl.sSetLayoutDirectionMethod = Drawable.class.getDeclaredMethod("setLayoutDirection", Integer.TYPE)).setAccessible(true);
                                    DrawableCompatApi17Impl.sSetLayoutDirectionMethodFetched = true;
                                    break Label_0006;
                                }
                                catch (final NoSuchMethodException ex) {
                                    Log.i("DrawableCompatApi17", "Failed to retrieve setLayoutDirection(int) method", (Throwable)ex);
                                    continue;
                                }
                                break;
                            }
                            break Block_2;
                        }
                    }
                    if (DrawableCompatApi17Impl.sSetLayoutDirectionMethod != null) {
                        break Label_0061;
                    }
                    return false;
                }
                try {
                    DrawableCompatApi17Impl.sSetLayoutDirectionMethod.invoke(obj, i);
                    return true;
                }
                catch (final Exception ex2) {
                    Log.i("DrawableCompatApi17", "Failed to invoke setLayoutDirection(int) via reflection", (Throwable)ex2);
                    DrawableCompatApi17Impl.sSetLayoutDirectionMethod = null;
                    return false;
                }
            }
        }
    }
    
    @RequiresApi(19)
    static class DrawableCompatApi19Impl extends DrawableCompatApi17Impl
    {
        @Override
        public int getAlpha(final Drawable drawable) {
            return drawable.getAlpha();
        }
        
        @Override
        public boolean isAutoMirrored(final Drawable drawable) {
            return drawable.isAutoMirrored();
        }
        
        @Override
        public void setAutoMirrored(final Drawable drawable, final boolean autoMirrored) {
            drawable.setAutoMirrored(autoMirrored);
        }
        
        @Override
        public Drawable wrap(final Drawable drawable) {
            if (drawable instanceof TintAwareDrawable) {
                return drawable;
            }
            return new DrawableWrapperApi19(drawable);
        }
    }
    
    @RequiresApi(21)
    static class DrawableCompatApi21Impl extends DrawableCompatApi19Impl
    {
        @Override
        public void applyTheme(final Drawable drawable, final Resources$Theme resources$Theme) {
            drawable.applyTheme(resources$Theme);
        }
        
        @Override
        public boolean canApplyTheme(final Drawable drawable) {
            return drawable.canApplyTheme();
        }
        
        @Override
        public void clearColorFilter(final Drawable drawable) {
            int i = 0;
            drawable.clearColorFilter();
            if (!(drawable instanceof InsetDrawable)) {
                if (!(drawable instanceof DrawableWrapper)) {
                    if (drawable instanceof DrawableContainer) {
                        final DrawableContainer$DrawableContainerState drawableContainer$DrawableContainerState = (DrawableContainer$DrawableContainerState)((DrawableContainer)drawable).getConstantState();
                        if (drawableContainer$DrawableContainerState != null) {
                            while (i < drawableContainer$DrawableContainerState.getChildCount()) {
                                final Drawable child = drawableContainer$DrawableContainerState.getChild(i);
                                if (child != null) {
                                    this.clearColorFilter(child);
                                }
                                ++i;
                            }
                        }
                    }
                }
                else {
                    this.clearColorFilter(((DrawableWrapper)drawable).getWrappedDrawable());
                }
            }
            else {
                this.clearColorFilter(((InsetDrawable)drawable).getDrawable());
            }
        }
        
        @Override
        public ColorFilter getColorFilter(final Drawable drawable) {
            return drawable.getColorFilter();
        }
        
        @Override
        public void inflate(final Drawable drawable, final Resources resources, final XmlPullParser xmlPullParser, final AttributeSet set, final Resources$Theme resources$Theme) throws IOException, XmlPullParserException {
            drawable.inflate(resources, xmlPullParser, set, resources$Theme);
        }
        
        @Override
        public void setHotspot(final Drawable drawable, final float n, final float n2) {
            drawable.setHotspot(n, n2);
        }
        
        @Override
        public void setHotspotBounds(final Drawable drawable, final int n, final int n2, final int n3, final int n4) {
            drawable.setHotspotBounds(n, n2, n3, n4);
        }
        
        @Override
        public void setTint(final Drawable drawable, final int tint) {
            drawable.setTint(tint);
        }
        
        @Override
        public void setTintList(final Drawable drawable, final ColorStateList tintList) {
            drawable.setTintList(tintList);
        }
        
        @Override
        public void setTintMode(final Drawable drawable, final PorterDuff$Mode tintMode) {
            drawable.setTintMode(tintMode);
        }
        
        @Override
        public Drawable wrap(final Drawable drawable) {
            if (drawable instanceof TintAwareDrawable) {
                return drawable;
            }
            return new DrawableWrapperApi21(drawable);
        }
    }
    
    @RequiresApi(23)
    static class DrawableCompatApi23Impl extends DrawableCompatApi21Impl
    {
        @Override
        public void clearColorFilter(final Drawable drawable) {
            drawable.clearColorFilter();
        }
        
        @Override
        public int getLayoutDirection(final Drawable drawable) {
            return drawable.getLayoutDirection();
        }
        
        @Override
        public boolean setLayoutDirection(final Drawable drawable, final int layoutDirection) {
            return drawable.setLayoutDirection(layoutDirection);
        }
        
        @Override
        public Drawable wrap(final Drawable drawable) {
            return drawable;
        }
    }
    
    static class DrawableCompatBaseImpl
    {
        public void applyTheme(final Drawable drawable, final Resources$Theme resources$Theme) {
        }
        
        public boolean canApplyTheme(final Drawable drawable) {
            return false;
        }
        
        public void clearColorFilter(final Drawable drawable) {
            drawable.clearColorFilter();
        }
        
        public int getAlpha(final Drawable drawable) {
            return 0;
        }
        
        public ColorFilter getColorFilter(final Drawable drawable) {
            return null;
        }
        
        public int getLayoutDirection(final Drawable drawable) {
            return 0;
        }
        
        public void inflate(final Drawable drawable, final Resources resources, final XmlPullParser xmlPullParser, final AttributeSet set, final Resources$Theme resources$Theme) throws IOException, XmlPullParserException {
            drawable.inflate(resources, xmlPullParser, set);
        }
        
        public boolean isAutoMirrored(final Drawable drawable) {
            return false;
        }
        
        public void jumpToCurrentState(final Drawable drawable) {
            drawable.jumpToCurrentState();
        }
        
        public void setAutoMirrored(final Drawable drawable, final boolean b) {
        }
        
        public void setHotspot(final Drawable drawable, final float n, final float n2) {
        }
        
        public void setHotspotBounds(final Drawable drawable, final int n, final int n2, final int n3, final int n4) {
        }
        
        public boolean setLayoutDirection(final Drawable drawable, final int n) {
            return false;
        }
        
        public void setTint(final Drawable drawable, final int tint) {
            if (drawable instanceof TintAwareDrawable) {
                ((TintAwareDrawable)drawable).setTint(tint);
            }
        }
        
        public void setTintList(final Drawable drawable, final ColorStateList tintList) {
            if (drawable instanceof TintAwareDrawable) {
                ((TintAwareDrawable)drawable).setTintList(tintList);
            }
        }
        
        public void setTintMode(final Drawable drawable, final PorterDuff$Mode tintMode) {
            if (drawable instanceof TintAwareDrawable) {
                ((TintAwareDrawable)drawable).setTintMode(tintMode);
            }
        }
        
        public Drawable wrap(final Drawable drawable) {
            if (drawable instanceof TintAwareDrawable) {
                return drawable;
            }
            return new DrawableWrapperApi14(drawable);
        }
    }
}
