// 
// Decompiled by Procyon v0.6.0
// 

package android.support.transition;

import android.support.annotation.RestrictTo;
import java.lang.reflect.InvocationTargetException;
import android.view.ViewParent;
import android.view.MotionEvent;
import android.graphics.Canvas;
import android.support.v4.view.ViewCompat;
import android.graphics.drawable.Drawable$Callback;
import android.graphics.Rect;
import java.util.ArrayList;
import java.lang.reflect.Method;
import android.support.annotation.NonNull;
import android.graphics.drawable.Drawable;
import android.view.View;
import android.view.ViewGroup;
import android.content.Context;
import android.support.annotation.RequiresApi;

@RequiresApi(14)
class ViewOverlayApi14 implements ViewOverlayImpl
{
    protected OverlayViewGroup mOverlayViewGroup;
    
    ViewOverlayApi14(final Context context, final ViewGroup viewGroup, final View view) {
        this.mOverlayViewGroup = new OverlayViewGroup(context, viewGroup, view, this);
    }
    
    static ViewOverlayApi14 createFrom(final View view) {
        final ViewGroup contentView = getContentView(view);
        if (contentView == null) {
            return null;
        }
        for (int childCount = contentView.getChildCount(), i = 0; i < childCount; ++i) {
            final View child = contentView.getChildAt(i);
            if (child instanceof OverlayViewGroup) {
                return ((OverlayViewGroup)child).mViewOverlay;
            }
        }
        return new ViewGroupOverlayApi14(contentView.getContext(), contentView, view);
    }
    
    static ViewGroup getContentView(View view) {
        while (view != null) {
            if (view.getId() == 16908290 && view instanceof ViewGroup) {
                return (ViewGroup)view;
            }
            if (!(view.getParent() instanceof ViewGroup)) {
                continue;
            }
            view = (View)view.getParent();
        }
        return null;
    }
    
    @Override
    public void add(@NonNull final Drawable drawable) {
        this.mOverlayViewGroup.add(drawable);
    }
    
    @Override
    public void clear() {
        this.mOverlayViewGroup.clear();
    }
    
    ViewGroup getOverlayView() {
        return this.mOverlayViewGroup;
    }
    
    boolean isEmpty() {
        return this.mOverlayViewGroup.isEmpty();
    }
    
    @Override
    public void remove(@NonNull final Drawable drawable) {
        this.mOverlayViewGroup.remove(drawable);
    }
    
    static class OverlayViewGroup extends ViewGroup
    {
        static Method sInvalidateChildInParentFastMethod;
        ArrayList<Drawable> mDrawables;
        ViewGroup mHostView;
        View mRequestingView;
        ViewOverlayApi14 mViewOverlay;
        
        static {
            try {
                OverlayViewGroup.sInvalidateChildInParentFastMethod = ViewGroup.class.getDeclaredMethod("invalidateChildInParentFast", Integer.TYPE, Integer.TYPE, Rect.class);
            }
            catch (final NoSuchMethodException ex) {}
        }
        
        OverlayViewGroup(final Context context, final ViewGroup mHostView, final View mRequestingView, final ViewOverlayApi14 mViewOverlay) {
            super(context);
            this.mDrawables = null;
            this.mHostView = mHostView;
            this.mRequestingView = mRequestingView;
            this.setRight(mHostView.getWidth());
            this.setBottom(mHostView.getHeight());
            mHostView.addView((View)this);
            this.mViewOverlay = mViewOverlay;
        }
        
        private void getOffset(final int[] array) {
            final int[] array2 = new int[2];
            final int[] array3 = new int[2];
            this.mHostView.getLocationOnScreen(array2);
            this.mRequestingView.getLocationOnScreen(array3);
            array[0] = array3[0] - array2[0];
            array[1] = array3[1] - array2[1];
        }
        
        public void add(final Drawable drawable) {
            if (this.mDrawables == null) {
                this.mDrawables = new ArrayList<Drawable>();
            }
            if (!this.mDrawables.contains(drawable)) {
                this.mDrawables.add(drawable);
                this.invalidate(drawable.getBounds());
                drawable.setCallback((Drawable$Callback)this);
            }
        }
        
        public void add(final View view) {
            if (view.getParent() instanceof ViewGroup) {
                final ViewGroup viewGroup = (ViewGroup)view.getParent();
                if (viewGroup != this.mHostView && viewGroup.getParent() != null && ViewCompat.isAttachedToWindow((View)viewGroup)) {
                    final int[] array = new int[2];
                    final int[] array2 = new int[2];
                    viewGroup.getLocationOnScreen(array);
                    this.mHostView.getLocationOnScreen(array2);
                    ViewCompat.offsetLeftAndRight(view, array[0] - array2[0]);
                    ViewCompat.offsetTopAndBottom(view, array[1] - array2[1]);
                }
                viewGroup.removeView(view);
                if (view.getParent() != null) {
                    viewGroup.removeView(view);
                }
            }
            super.addView(view, this.getChildCount() - 1);
        }
        
        public void clear() {
            this.removeAllViews();
            if (this.mDrawables != null) {
                this.mDrawables.clear();
            }
        }
        
        protected void dispatchDraw(final Canvas canvas) {
            int i = 0;
            final int[] array = new int[2];
            final int[] array2 = new int[2];
            this.mHostView.getLocationOnScreen(array);
            this.mRequestingView.getLocationOnScreen(array2);
            canvas.translate((float)(array2[0] - array[0]), (float)(array2[1] - array[1]));
            canvas.clipRect(new Rect(0, 0, this.mRequestingView.getWidth(), this.mRequestingView.getHeight()));
            super.dispatchDraw(canvas);
            int size;
            if (this.mDrawables != null) {
                size = this.mDrawables.size();
            }
            else {
                size = 0;
            }
            while (i < size) {
                this.mDrawables.get(i).draw(canvas);
                ++i;
            }
        }
        
        public boolean dispatchTouchEvent(final MotionEvent motionEvent) {
            return false;
        }
        
        public void invalidateChildFast(final View view, final Rect rect) {
            if (this.mHostView != null) {
                final int left = view.getLeft();
                final int top = view.getTop();
                final int[] array = new int[2];
                this.getOffset(array);
                rect.offset(left + array[0], top + array[1]);
                this.mHostView.invalidate(rect);
            }
        }
        
        public ViewParent invalidateChildInParent(final int[] array, final Rect rect) {
            if (this.mHostView != null) {
                rect.offset(array[0], array[1]);
                if (this.mHostView instanceof ViewGroup) {
                    array[1] = (array[0] = 0);
                    final int[] array2 = new int[2];
                    this.getOffset(array2);
                    rect.offset(array2[0], array2[1]);
                    return super.invalidateChildInParent(array, rect);
                }
                this.invalidate(rect);
            }
            return null;
        }
        
        @RestrictTo({ RestrictTo.Scope.LIBRARY_GROUP })
        protected ViewParent invalidateChildInParentFast(final int i, final int j, final Rect rect) {
            if (this.mHostView instanceof ViewGroup && OverlayViewGroup.sInvalidateChildInParentFastMethod != null) {
                try {
                    this.getOffset(new int[2]);
                    OverlayViewGroup.sInvalidateChildInParentFastMethod.invoke(this.mHostView, i, j, rect);
                }
                catch (final IllegalAccessException ex) {
                    ex.printStackTrace();
                }
                catch (final InvocationTargetException ex2) {
                    ex2.printStackTrace();
                }
            }
            return null;
        }
        
        public void invalidateDrawable(@NonNull final Drawable drawable) {
            this.invalidate(drawable.getBounds());
        }
        
        boolean isEmpty() {
            boolean b = false;
            if (this.getChildCount() == 0) {
                if (this.mDrawables == null || this.mDrawables.size() == 0) {
                    b = true;
                }
            }
            return b;
        }
        
        protected void onLayout(final boolean b, final int n, final int n2, final int n3, final int n4) {
        }
        
        public void remove(final Drawable o) {
            if (this.mDrawables != null) {
                this.mDrawables.remove(o);
                this.invalidate(o.getBounds());
                o.setCallback((Drawable$Callback)null);
            }
        }
        
        public void remove(final View view) {
            super.removeView(view);
            if (this.isEmpty()) {
                this.mHostView.removeView((View)this);
            }
        }
        
        protected boolean verifyDrawable(@NonNull final Drawable o) {
            final boolean b = false;
            if (!super.verifyDrawable(o)) {
                boolean b2 = b;
                if (this.mDrawables == null) {
                    return b2;
                }
                if (!this.mDrawables.contains(o)) {
                    b2 = b;
                    return b2;
                }
            }
            return true;
        }
        
        static class TouchInterceptor extends View
        {
            TouchInterceptor(final Context context) {
                super(context);
            }
        }
    }
}
