// 
// Decompiled by Procyon v0.6.0
// 

package android.support.v7.widget.helper;

import android.graphics.Canvas;
import android.support.v7.recyclerview.R;
import android.support.v4.view.ViewCompat;
import android.view.View;
import android.support.v7.widget.RecyclerView;

class ItemTouchUIUtilImpl
{
    static class Api21Impl extends BaseImpl
    {
        private float findMaxElevation(final RecyclerView recyclerView, final View view) {
            final int childCount = recyclerView.getChildCount();
            float n = 0.0f;
            for (int i = 0; i < childCount; ++i) {
                final View child = recyclerView.getChildAt(i);
                if (child != view) {
                    final float elevation = ViewCompat.getElevation(child);
                    if (elevation > n) {
                        n = elevation;
                    }
                }
            }
            return n;
        }
        
        @Override
        public void clearView(final View view) {
            final Object tag = view.getTag(R.id.item_touch_helper_previous_elevation);
            if (tag != null && tag instanceof Float) {
                ViewCompat.setElevation(view, (float)tag);
            }
            view.setTag(R.id.item_touch_helper_previous_elevation, (Object)null);
            super.clearView(view);
        }
        
        @Override
        public void onDraw(final Canvas canvas, final RecyclerView recyclerView, final View view, final float n, final float n2, final int n3, final boolean b) {
            if (b && view.getTag(R.id.item_touch_helper_previous_elevation) == null) {
                final float elevation = ViewCompat.getElevation(view);
                ViewCompat.setElevation(view, this.findMaxElevation(recyclerView, view) + 1.0f);
                view.setTag(R.id.item_touch_helper_previous_elevation, (Object)elevation);
            }
            super.onDraw(canvas, recyclerView, view, n, n2, n3, b);
        }
    }
    
    static class BaseImpl implements ItemTouchUIUtil
    {
        @Override
        public void clearView(final View view) {
            view.setTranslationX(0.0f);
            view.setTranslationY(0.0f);
        }
        
        @Override
        public void onDraw(final Canvas canvas, final RecyclerView recyclerView, final View view, final float translationX, final float translationY, final int n, final boolean b) {
            view.setTranslationX(translationX);
            view.setTranslationY(translationY);
        }
        
        @Override
        public void onDrawOver(final Canvas canvas, final RecyclerView recyclerView, final View view, final float n, final float n2, final int n3, final boolean b) {
        }
        
        @Override
        public void onSelected(final View view) {
        }
    }
}
