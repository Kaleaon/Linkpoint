// 
// Decompiled by Procyon v0.6.0
// 

package android.support.v4.view;

import android.support.annotation.Nullable;
import android.support.annotation.NonNull;
import android.view.View;
import android.view.ViewParent;

public class NestedScrollingChildHelper
{
    private boolean mIsNestedScrollingEnabled;
    private ViewParent mNestedScrollingParentNonTouch;
    private ViewParent mNestedScrollingParentTouch;
    private int[] mTempNestedScrollConsumed;
    private final View mView;
    
    public NestedScrollingChildHelper(@NonNull final View mView) {
        this.mView = mView;
    }
    
    private ViewParent getNestedScrollingParentForType(final int n) {
        switch (n) {
            default: {
                return null;
            }
            case 0: {
                return this.mNestedScrollingParentTouch;
            }
            case 1: {
                return this.mNestedScrollingParentNonTouch;
            }
        }
    }
    
    private void setNestedScrollingParentForType(final int n, final ViewParent viewParent) {
        switch (n) {
            case 0: {
                this.mNestedScrollingParentTouch = viewParent;
                break;
            }
            case 1: {
                this.mNestedScrollingParentNonTouch = viewParent;
                break;
            }
        }
    }
    
    public boolean dispatchNestedFling(final float n, final float n2, final boolean b) {
        if (this.isNestedScrollingEnabled()) {
            final ViewParent nestedScrollingParentForType = this.getNestedScrollingParentForType(0);
            if (nestedScrollingParentForType != null) {
                return ViewParentCompat.onNestedFling(nestedScrollingParentForType, this.mView, n, n2, b);
            }
        }
        return false;
    }
    
    public boolean dispatchNestedPreFling(final float n, final float n2) {
        if (this.isNestedScrollingEnabled()) {
            final ViewParent nestedScrollingParentForType = this.getNestedScrollingParentForType(0);
            if (nestedScrollingParentForType != null) {
                return ViewParentCompat.onNestedPreFling(nestedScrollingParentForType, this.mView, n, n2);
            }
        }
        return false;
    }
    
    public boolean dispatchNestedPreScroll(final int n, final int n2, @Nullable final int[] array, @Nullable final int[] array2) {
        return this.dispatchNestedPreScroll(n, n2, array, array2, 0);
    }
    
    public boolean dispatchNestedPreScroll(final int n, final int n2, @Nullable int[] mTempNestedScrollConsumed, @Nullable final int[] array, final int n3) {
        if (this.isNestedScrollingEnabled()) {
            final ViewParent nestedScrollingParentForType = this.getNestedScrollingParentForType(n3);
            if (nestedScrollingParentForType == null) {
                return false;
            }
            if (n != 0 || n2 != 0) {
                int n4;
                int n5;
                if (array == null) {
                    n4 = 0;
                    n5 = 0;
                }
                else {
                    this.mView.getLocationInWindow(array);
                    n5 = array[0];
                    n4 = array[1];
                }
                if (mTempNestedScrollConsumed == null) {
                    if (this.mTempNestedScrollConsumed == null) {
                        this.mTempNestedScrollConsumed = new int[2];
                    }
                    mTempNestedScrollConsumed = this.mTempNestedScrollConsumed;
                }
                mTempNestedScrollConsumed[1] = (mTempNestedScrollConsumed[0] = 0);
                ViewParentCompat.onNestedPreScroll(nestedScrollingParentForType, this.mView, n, n2, mTempNestedScrollConsumed, n3);
                if (array != null) {
                    this.mView.getLocationInWindow(array);
                    array[0] -= n5;
                    array[1] -= n4;
                }
                return mTempNestedScrollConsumed[0] != 0 || mTempNestedScrollConsumed[1] != 0;
            }
            if (array != null) {
                array[1] = (array[0] = 0);
            }
        }
        return false;
    }
    
    public boolean dispatchNestedScroll(final int n, final int n2, final int n3, final int n4, @Nullable final int[] array) {
        return this.dispatchNestedScroll(n, n2, n3, n4, array, 0);
    }
    
    public boolean dispatchNestedScroll(final int n, final int n2, final int n3, final int n4, @Nullable final int[] array, final int n5) {
        if (this.isNestedScrollingEnabled()) {
            final ViewParent nestedScrollingParentForType = this.getNestedScrollingParentForType(n5);
            if (nestedScrollingParentForType == null) {
                return false;
            }
            if (n != 0 || n2 != 0 || n3 != 0 || n4 != 0) {
                int n6;
                int n7;
                if (array == null) {
                    n6 = 0;
                    n7 = 0;
                }
                else {
                    this.mView.getLocationInWindow(array);
                    n7 = array[0];
                    n6 = array[1];
                }
                ViewParentCompat.onNestedScroll(nestedScrollingParentForType, this.mView, n, n2, n3, n4, n5);
                if (array != null) {
                    this.mView.getLocationInWindow(array);
                    array[0] -= n7;
                    array[1] -= n6;
                }
                return true;
            }
            if (array != null) {
                array[1] = (array[0] = 0);
            }
        }
        return false;
    }
    
    public boolean hasNestedScrollingParent() {
        return this.hasNestedScrollingParent(0);
    }
    
    public boolean hasNestedScrollingParent(final int n) {
        return this.getNestedScrollingParentForType(n) != null;
    }
    
    public boolean isNestedScrollingEnabled() {
        return this.mIsNestedScrollingEnabled;
    }
    
    public void onDetachedFromWindow() {
        ViewCompat.stopNestedScroll(this.mView);
    }
    
    public void onStopNestedScroll(@NonNull final View view) {
        ViewCompat.stopNestedScroll(this.mView);
    }
    
    public void setNestedScrollingEnabled(final boolean mIsNestedScrollingEnabled) {
        if (this.mIsNestedScrollingEnabled) {
            ViewCompat.stopNestedScroll(this.mView);
        }
        this.mIsNestedScrollingEnabled = mIsNestedScrollingEnabled;
    }
    
    public boolean startNestedScroll(final int n) {
        return this.startNestedScroll(n, 0);
    }
    
    public boolean startNestedScroll(final int n, final int n2) {
        if (!this.hasNestedScrollingParent(n2)) {
            if (this.isNestedScrollingEnabled()) {
                ViewParent viewParent = this.mView.getParent();
                View mView = this.mView;
                while (viewParent != null) {
                    if (ViewParentCompat.onStartNestedScroll(viewParent, mView, this.mView, n, n2)) {
                        this.setNestedScrollingParentForType(n2, viewParent);
                        ViewParentCompat.onNestedScrollAccepted(viewParent, mView, this.mView, n, n2);
                        return true;
                    }
                    if (viewParent instanceof View) {
                        mView = (View)viewParent;
                    }
                    viewParent = viewParent.getParent();
                }
            }
            return false;
        }
        return true;
    }
    
    public void stopNestedScroll() {
        this.stopNestedScroll(0);
    }
    
    public void stopNestedScroll(final int n) {
        final ViewParent nestedScrollingParentForType = this.getNestedScrollingParentForType(n);
        if (nestedScrollingParentForType != null) {
            ViewParentCompat.onStopNestedScroll(nestedScrollingParentForType, this.mView, n);
            this.setNestedScrollingParentForType(n, null);
        }
    }
}
