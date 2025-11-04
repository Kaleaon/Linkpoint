// 
// Decompiled by Procyon v0.6.0
// 

package android.support.transition;

import android.support.annotation.Nullable;
import android.graphics.Matrix;
import android.util.Log;
import android.support.annotation.NonNull;
import android.support.v4.view.ViewCompat;
import android.os.Build$VERSION;
import java.lang.reflect.Field;
import android.graphics.Rect;
import android.view.View;
import android.util.Property;

class ViewUtils
{
    static final Property<View, Rect> CLIP_BOUNDS;
    private static final ViewUtilsImpl IMPL;
    private static final String TAG = "ViewUtils";
    static final Property<View, Float> TRANSITION_ALPHA;
    private static final int VISIBILITY_MASK = 12;
    private static Field sViewFlagsField;
    private static boolean sViewFlagsFieldFetched;
    
    static {
        if (Build$VERSION.SDK_INT < 22) {
            if (Build$VERSION.SDK_INT < 21) {
                if (Build$VERSION.SDK_INT < 19) {
                    if (Build$VERSION.SDK_INT < 18) {
                        IMPL = new ViewUtilsApi14();
                    }
                    else {
                        IMPL = new ViewUtilsApi18();
                    }
                }
                else {
                    IMPL = new ViewUtilsApi19();
                }
            }
            else {
                IMPL = new ViewUtilsApi21();
            }
        }
        else {
            IMPL = new ViewUtilsApi22();
        }
        TRANSITION_ALPHA = new Property<View, Float>(Float.class, "translationAlpha") {
            public Float get(final View view) {
                return ViewUtils.getTransitionAlpha(view);
            }
            
            public void set(final View view, final Float n) {
                ViewUtils.setTransitionAlpha(view, n);
            }
        };
        CLIP_BOUNDS = new Property<View, Rect>(Rect.class, "clipBounds") {
            public Rect get(final View view) {
                return ViewCompat.getClipBounds(view);
            }
            
            public void set(final View view, final Rect rect) {
                ViewCompat.setClipBounds(view, rect);
            }
        };
    }
    
    static void clearNonTransitionAlpha(@NonNull final View view) {
        ViewUtils.IMPL.clearNonTransitionAlpha(view);
    }
    
    private static void fetchViewFlagsField() {
        if (!ViewUtils.sViewFlagsFieldFetched) {
            while (true) {
                try {
                    (ViewUtils.sViewFlagsField = View.class.getDeclaredField("mViewFlags")).setAccessible(true);
                    ViewUtils.sViewFlagsFieldFetched = true;
                }
                catch (final NoSuchFieldException ex) {
                    Log.i("ViewUtils", "fetchViewFlagsField: ");
                    continue;
                }
                break;
            }
        }
    }
    
    static ViewOverlayImpl getOverlay(@NonNull final View view) {
        return ViewUtils.IMPL.getOverlay(view);
    }
    
    static float getTransitionAlpha(@NonNull final View view) {
        return ViewUtils.IMPL.getTransitionAlpha(view);
    }
    
    static WindowIdImpl getWindowId(@NonNull final View view) {
        return ViewUtils.IMPL.getWindowId(view);
    }
    
    static void saveNonTransitionAlpha(@NonNull final View view) {
        ViewUtils.IMPL.saveNonTransitionAlpha(view);
    }
    
    static void setAnimationMatrix(@NonNull final View view, @Nullable final Matrix matrix) {
        ViewUtils.IMPL.setAnimationMatrix(view, matrix);
    }
    
    static void setLeftTopRightBottom(@NonNull final View view, final int n, final int n2, final int n3, final int n4) {
        ViewUtils.IMPL.setLeftTopRightBottom(view, n, n2, n3, n4);
    }
    
    static void setTransitionAlpha(@NonNull final View view, final float n) {
        ViewUtils.IMPL.setTransitionAlpha(view, n);
    }
    
    static void setTransitionVisibility(@NonNull final View view, final int n) {
        fetchViewFlagsField();
        if (ViewUtils.sViewFlagsField != null) {
            try {
                ViewUtils.sViewFlagsField.setInt(view, (ViewUtils.sViewFlagsField.getInt(view) & 0xFFFFFFF3) | n);
            }
            catch (final IllegalAccessException ex) {}
        }
    }
    
    static void transformMatrixToGlobal(@NonNull final View view, @NonNull final Matrix matrix) {
        ViewUtils.IMPL.transformMatrixToGlobal(view, matrix);
    }
    
    static void transformMatrixToLocal(@NonNull final View view, @NonNull final Matrix matrix) {
        ViewUtils.IMPL.transformMatrixToLocal(view, matrix);
    }
}
