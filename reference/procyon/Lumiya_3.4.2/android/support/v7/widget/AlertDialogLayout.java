// 
// Decompiled by Procyon v0.6.0
// 

package android.support.v7.widget;

import android.graphics.drawable.Drawable;
import android.support.v4.view.GravityCompat;
import android.support.v7.appcompat.R;
import android.view.ViewGroup;
import android.support.v4.view.ViewCompat;
import android.view.View;
import android.view.View$MeasureSpec;
import android.util.AttributeSet;
import android.support.annotation.Nullable;
import android.content.Context;
import android.support.annotation.RestrictTo;

@RestrictTo({ RestrictTo.Scope.LIBRARY_GROUP })
public class AlertDialogLayout extends LinearLayoutCompat
{
    public AlertDialogLayout(@Nullable final Context context) {
        super(context);
    }
    
    public AlertDialogLayout(@Nullable final Context context, @Nullable final AttributeSet set) {
        super(context, set);
    }
    
    private void forceUniformWidth(final int n, final int n2) {
        final int measureSpec = View$MeasureSpec.makeMeasureSpec(this.getMeasuredWidth(), 1073741824);
        for (int i = 0; i < n; ++i) {
            final View child = this.getChildAt(i);
            if (child.getVisibility() != 8) {
                final LayoutParams layoutParams = (LayoutParams)child.getLayoutParams();
                if (layoutParams.width == -1) {
                    final int height = layoutParams.height;
                    layoutParams.height = child.getMeasuredHeight();
                    this.measureChildWithMargins(child, measureSpec, 0, n2, 0);
                    layoutParams.height = height;
                }
            }
        }
    }
    
    private static int resolveMinimumHeight(final View view) {
        final int minimumHeight = ViewCompat.getMinimumHeight(view);
        if (minimumHeight <= 0) {
            if (view instanceof ViewGroup) {
                final ViewGroup viewGroup = (ViewGroup)view;
                if (viewGroup.getChildCount() == 1) {
                    return resolveMinimumHeight(viewGroup.getChildAt(0));
                }
            }
            return 0;
        }
        return minimumHeight;
    }
    
    private void setChildFrame(final View view, final int n, final int n2, final int n3, final int n4) {
        view.layout(n, n2, n + n3, n2 + n4);
    }
    
    private boolean tryOnMeasure(final int n, final int n2) {
        View view = null;
        View view2 = null;
        View view3 = null;
        final int childCount = this.getChildCount();
        View view5;
        View view6;
        View view8;
        for (int i = 0; i < childCount; ++i, view8 = view5, view2 = view6, view = view8) {
            final View child = this.getChildAt(i);
            if (child.getVisibility() != 8) {
                final int id = child.getId();
                if (id != R.id.topPanel) {
                    if (id != R.id.buttonPanel) {
                        if (id != R.id.contentPanel && id != R.id.customPanel) {
                            return false;
                        }
                        if (view3 != null) {
                            return false;
                        }
                        final View view4 = view2;
                        view5 = view;
                        view6 = view4;
                        view3 = child;
                    }
                    else {
                        view5 = view;
                        view6 = child;
                    }
                }
                else {
                    view6 = view2;
                    view5 = child;
                }
            }
            else {
                final View view7 = view;
                view6 = view2;
                view5 = view7;
            }
        }
        final int mode = View$MeasureSpec.getMode(n2);
        final int size = View$MeasureSpec.getSize(n2);
        final int mode2 = View$MeasureSpec.getMode(n);
        int n3 = this.getPaddingTop() + this.getPaddingBottom();
        int combineMeasuredStates;
        if (view == null) {
            combineMeasuredStates = 0;
        }
        else {
            view.measure(n, 0);
            n3 += view.getMeasuredHeight();
            combineMeasuredStates = View.combineMeasuredStates(0, view.getMeasuredState());
        }
        int b;
        int n5;
        int n6;
        if (view2 == null) {
            b = 0;
            final int n4 = 0;
            n5 = combineMeasuredStates;
            n6 = n4;
        }
        else {
            view2.measure(n, 0);
            final int resolveMinimumHeight = resolveMinimumHeight(view2);
            final int measuredHeight = view2.getMeasuredHeight();
            n5 = View.combineMeasuredStates(combineMeasuredStates, view2.getMeasuredState());
            n3 += resolveMinimumHeight;
            final int n7 = measuredHeight - resolveMinimumHeight;
            n6 = resolveMinimumHeight;
            b = n7;
        }
        int measuredHeight2;
        if (view3 == null) {
            measuredHeight2 = 0;
        }
        else {
            int measureSpec;
            if (mode != 0) {
                measureSpec = View$MeasureSpec.makeMeasureSpec(Math.max(0, size - n3), mode);
            }
            else {
                measureSpec = 0;
            }
            view3.measure(n, measureSpec);
            measuredHeight2 = view3.getMeasuredHeight();
            n5 = View.combineMeasuredStates(n5, view3.getMeasuredState());
            n3 += measuredHeight2;
        }
        final int a = size - n3;
        int n8;
        int combineMeasuredStates2;
        int n9;
        if (view2 == null) {
            n8 = n3;
            combineMeasuredStates2 = n5;
            n9 = a;
        }
        else {
            final int min = Math.min(a, b);
            int n10;
            if (min <= 0) {
                n9 = a;
                n10 = n6;
            }
            else {
                n9 = a - min;
                n10 = n6 + min;
            }
            view2.measure(n, View$MeasureSpec.makeMeasureSpec(n10, 1073741824));
            final int measuredHeight3 = view2.getMeasuredHeight();
            final int combineMeasuredStates3 = View.combineMeasuredStates(n5, view2.getMeasuredState());
            n8 = n3 - n6 + measuredHeight3;
            combineMeasuredStates2 = combineMeasuredStates3;
        }
        if (view3 != null && n9 > 0) {
            view3.measure(n, View$MeasureSpec.makeMeasureSpec(measuredHeight2 + n9, mode));
            n8 = n8 - measuredHeight2 + view3.getMeasuredHeight();
            combineMeasuredStates2 = View.combineMeasuredStates(combineMeasuredStates2, view3.getMeasuredState());
        }
        int max = 0;
        for (int j = 0; j < childCount; ++j) {
            final View child2 = this.getChildAt(j);
            if (child2.getVisibility() != 8) {
                max = Math.max(max, child2.getMeasuredWidth());
            }
        }
        this.setMeasuredDimension(View.resolveSizeAndState(max + (this.getPaddingLeft() + this.getPaddingRight()), n, combineMeasuredStates2), View.resolveSizeAndState(n8, n2, 0));
        if (mode2 != 1073741824) {
            this.forceUniformWidth(childCount, n2);
        }
        return true;
    }
    
    @Override
    protected void onLayout(final boolean b, int n, int gravity, int n2, int i) {
        final int paddingLeft = this.getPaddingLeft();
        final int n3 = n2 - n;
        final int paddingRight = this.getPaddingRight();
        final int paddingRight2 = this.getPaddingRight();
        n = this.getMeasuredHeight();
        final int childCount = this.getChildCount();
        final int gravity2 = this.getGravity();
        switch (gravity2 & 0x70) {
            default: {
                n = this.getPaddingTop();
                break;
            }
            case 80: {
                n = this.getPaddingTop() + i - gravity - n;
                break;
            }
            case 16: {
                n2 = this.getPaddingTop();
                n = (i - gravity - n) / 2 + n2;
                break;
            }
        }
        final Drawable dividerDrawable = this.getDividerDrawable();
        if (dividerDrawable != null) {
            n2 = dividerDrawable.getIntrinsicHeight();
        }
        else {
            n2 = 0;
        }
        View child;
        int measuredWidth;
        int measuredHeight;
        LayoutParams layoutParams;
        for (i = 0; i < childCount; ++i, n = gravity) {
            child = this.getChildAt(i);
            if (child == null) {
                gravity = n;
            }
            else {
                gravity = n;
                if (child.getVisibility() != 8) {
                    measuredWidth = child.getMeasuredWidth();
                    measuredHeight = child.getMeasuredHeight();
                    layoutParams = (LayoutParams)child.getLayoutParams();
                    gravity = layoutParams.gravity;
                    if (gravity < 0) {
                        gravity = (gravity2 & 0x800007);
                    }
                    switch (GravityCompat.getAbsoluteGravity(gravity, ViewCompat.getLayoutDirection((View)this)) & 0x7) {
                        default: {
                            gravity = paddingLeft + layoutParams.leftMargin;
                            break;
                        }
                        case 1: {
                            gravity = (n3 - paddingLeft - paddingRight2 - measuredWidth) / 2 + paddingLeft + layoutParams.leftMargin - layoutParams.rightMargin;
                            break;
                        }
                        case 5: {
                            gravity = n3 - paddingRight - measuredWidth - layoutParams.rightMargin;
                            break;
                        }
                    }
                    if (this.hasDividerBeforeChildAt(i)) {
                        n += n2;
                    }
                    n += layoutParams.topMargin;
                    this.setChildFrame(child, gravity, n, measuredWidth, measuredHeight);
                    gravity = n + (layoutParams.bottomMargin + measuredHeight);
                }
            }
        }
    }
    
    @Override
    protected void onMeasure(final int n, final int n2) {
        if (!this.tryOnMeasure(n, n2)) {
            super.onMeasure(n, n2);
        }
    }
}
