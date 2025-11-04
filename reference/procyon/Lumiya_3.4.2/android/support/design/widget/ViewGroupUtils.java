// 
// Decompiled by Procyon v0.6.0
// 

package android.support.design.widget;

import android.view.ViewParent;
import android.graphics.Rect;
import android.view.View;
import android.view.ViewGroup;
import android.graphics.RectF;
import android.graphics.Matrix;

class ViewGroupUtils
{
    private static final ThreadLocal<Matrix> sMatrix;
    private static final ThreadLocal<RectF> sRectF;
    
    static {
        sMatrix = new ThreadLocal<Matrix>();
        sRectF = new ThreadLocal<RectF>();
    }
    
    static void getDescendantRect(final ViewGroup viewGroup, final View view, final Rect rect) {
        rect.set(0, 0, view.getWidth(), view.getHeight());
        offsetDescendantRect(viewGroup, view, rect);
    }
    
    private static void offsetDescendantMatrix(final ViewParent viewParent, final View view, final Matrix matrix) {
        final ViewParent parent = view.getParent();
        if (parent instanceof View && parent != viewParent) {
            final View view2 = (View)parent;
            offsetDescendantMatrix(viewParent, view2, matrix);
            matrix.preTranslate((float)(-view2.getScrollX()), (float)(-view2.getScrollY()));
        }
        matrix.preTranslate((float)view.getLeft(), (float)view.getTop());
        if (!view.getMatrix().isIdentity()) {
            matrix.preConcat(view.getMatrix());
        }
    }
    
    static void offsetDescendantRect(final ViewGroup viewGroup, final View view, final Rect rect) {
        Matrix value = ViewGroupUtils.sMatrix.get();
        if (value != null) {
            value.reset();
        }
        else {
            value = new Matrix();
            ViewGroupUtils.sMatrix.set(value);
        }
        offsetDescendantMatrix((ViewParent)viewGroup, view, value);
        RectF value2 = ViewGroupUtils.sRectF.get();
        if (value2 == null) {
            value2 = new RectF();
            ViewGroupUtils.sRectF.set(value2);
        }
        value2.set(rect);
        value.mapRect(value2);
        rect.set((int)(value2.left + 0.5f), (int)(value2.top + 0.5f), (int)(value2.right + 0.5f), (int)(value2.bottom + 0.5f));
    }
}
