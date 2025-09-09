package android.support.v7.widget.helper;

import android.graphics.Canvas;
import android.support.v4.view.ViewCompat;
import android.support.v7.recyclerview.R;
import android.support.v7.widget.RecyclerView;
import android.view.View;

class ItemTouchUIUtilImpl {

    static class Api21Impl extends BaseImpl {
        Api21Impl() {
        }

        private float findMaxElevation(RecyclerView recyclerView, View view) {
            float f;
            int childCount = recyclerView.getChildCount();
            float f2 = 0.0f;
            int i = 0;
            while (i < childCount) {
                View childAt = recyclerView.getChildAt(i);
                if (childAt != view) {
                    f = ViewCompat.getElevation(childAt);
                    if (f <= f2) {
                        f = f2;
                    }
                } else {
                    f = f2;
                }
                i++;
                f2 = f;
            }
            return f2;
        }

        public void clearView(View view) {
            Object tag = view.getTag(R.id.item_touch_helper_previous_elevation);
            if (tag != null && (tag instanceof Float)) {
                ViewCompat.setElevation(view, ((Float) tag).floatValue());
            }
            view.setTag(R.id.item_touch_helper_previous_elevation, (Object) null);
            super.clearView(view);
        }

        public void onDraw(Canvas canvas, RecyclerView recyclerView, View view, float f, float f2, int i, boolean z) {
            if (z && view.getTag(R.id.item_touch_helper_previous_elevation) == null) {
                Float valueOf = Float.valueOf(ViewCompat.getElevation(view));
                ViewCompat.setElevation(view, findMaxElevation(recyclerView, view) + 1.0f);
                view.setTag(R.id.item_touch_helper_previous_elevation, valueOf);
            }
            super.onDraw(canvas, recyclerView, view, f, f2, i, z);
        }
    }

    static class BaseImpl implements ItemTouchUIUtil {
        BaseImpl() {
        }

        public void clearView(View view) {
            view.setTranslationX(0.0f);
            view.setTranslationY(0.0f);
        }

        public void onDraw(Canvas canvas, RecyclerView recyclerView, View view, float f, float f2, int i, boolean z) {
            view.setTranslationX(f);
            view.setTranslationY(f2);
        }

        public void onDrawOver(Canvas canvas, RecyclerView recyclerView, View view, float f, float f2, int i, boolean z) {
        }

        public void onSelected(View view) {
        }
    }

    ItemTouchUIUtilImpl() {
    }
}
