// 
// Decompiled by Procyon v0.6.0
// 

package android.support.transition;

import java.lang.reflect.InvocationTargetException;
import android.util.Log;
import android.graphics.Matrix;
import android.view.ViewGroup;
import android.support.annotation.NonNull;
import android.view.View;
import java.lang.reflect.Method;
import android.support.annotation.RequiresApi;

@RequiresApi(21)
class GhostViewApi21 implements GhostViewImpl
{
    private static final String TAG = "GhostViewApi21";
    private static Method sAddGhostMethod;
    private static boolean sAddGhostMethodFetched;
    private static Class<?> sGhostViewClass;
    private static boolean sGhostViewClassFetched;
    private static Method sRemoveGhostMethod;
    private static boolean sRemoveGhostMethodFetched;
    private final View mGhostView;
    
    private GhostViewApi21(@NonNull final View mGhostView) {
        this.mGhostView = mGhostView;
    }
    
    private static void fetchAddGhostMethod() {
        if (!GhostViewApi21.sAddGhostMethodFetched) {
            while (true) {
                try {
                    fetchGhostViewClass();
                    (GhostViewApi21.sAddGhostMethod = GhostViewApi21.sGhostViewClass.getDeclaredMethod("addGhost", View.class, ViewGroup.class, Matrix.class)).setAccessible(true);
                    GhostViewApi21.sAddGhostMethodFetched = true;
                }
                catch (final NoSuchMethodException ex) {
                    Log.i("GhostViewApi21", "Failed to retrieve addGhost method", (Throwable)ex);
                    continue;
                }
                break;
            }
        }
    }
    
    private static void fetchGhostViewClass() {
        if (!GhostViewApi21.sGhostViewClassFetched) {
            while (true) {
                try {
                    GhostViewApi21.sGhostViewClass = Class.forName("android.view.GhostView");
                    GhostViewApi21.sGhostViewClassFetched = true;
                }
                catch (final ClassNotFoundException ex) {
                    Log.i("GhostViewApi21", "Failed to retrieve GhostView class", (Throwable)ex);
                    continue;
                }
                break;
            }
        }
    }
    
    private static void fetchRemoveGhostMethod() {
        if (!GhostViewApi21.sRemoveGhostMethodFetched) {
            while (true) {
                try {
                    fetchGhostViewClass();
                    (GhostViewApi21.sRemoveGhostMethod = GhostViewApi21.sGhostViewClass.getDeclaredMethod("removeGhost", View.class)).setAccessible(true);
                    GhostViewApi21.sRemoveGhostMethodFetched = true;
                }
                catch (final NoSuchMethodException ex) {
                    Log.i("GhostViewApi21", "Failed to retrieve removeGhost method", (Throwable)ex);
                    continue;
                }
                break;
            }
        }
    }
    
    @Override
    public void reserveEndViewTransition(final ViewGroup viewGroup, final View view) {
    }
    
    @Override
    public void setVisibility(final int visibility) {
        this.mGhostView.setVisibility(visibility);
    }
    
    static class Creator implements GhostViewImpl.Creator
    {
        @Override
        public GhostViewImpl addGhost(final View view, final ViewGroup viewGroup, final Matrix matrix) {
            fetchAddGhostMethod();
            if (GhostViewApi21.sAddGhostMethod != null) {
                try {
                    return new GhostViewApi21((View)GhostViewApi21.sAddGhostMethod.invoke(null, view, viewGroup, matrix), null);
                }
                catch (final InvocationTargetException ex) {
                    throw new RuntimeException(ex.getCause());
                }
                catch (final IllegalAccessException ex2) {}
            }
            return null;
        }
        
        @Override
        public void removeGhost(final View view) {
            fetchRemoveGhostMethod();
            if (GhostViewApi21.sRemoveGhostMethod != null) {
                try {
                    GhostViewApi21.sRemoveGhostMethod.invoke(null, view);
                }
                catch (final IllegalAccessException ex) {}
                catch (final InvocationTargetException ex2) {
                    throw new RuntimeException(ex2.getCause());
                }
            }
        }
    }
}
