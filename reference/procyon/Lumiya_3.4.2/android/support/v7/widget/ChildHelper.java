// 
// Decompiled by Procyon v0.6.0
// 

package android.support.v7.widget;

import android.view.ViewGroup$LayoutParams;
import java.util.ArrayList;
import android.view.View;
import java.util.List;

class ChildHelper
{
    private static final boolean DEBUG = false;
    private static final String TAG = "ChildrenHelper";
    final Bucket mBucket;
    final Callback mCallback;
    final List<View> mHiddenViews;
    
    ChildHelper(final Callback mCallback) {
        this.mCallback = mCallback;
        this.mBucket = new Bucket();
        this.mHiddenViews = new ArrayList<View>();
    }
    
    private int getOffset(final int n) {
        if (n >= 0) {
            int n2;
            for (int childCount = this.mCallback.getChildCount(), i = n; i < childCount; i += n2) {
                n2 = n - (i - this.mBucket.countOnesBefore(i));
                int n3 = i;
                if (n2 == 0) {
                    while (this.mBucket.get(n3)) {
                        ++n3;
                    }
                    return n3;
                }
            }
            return -1;
        }
        return -1;
    }
    
    private void hideViewInternal(final View view) {
        this.mHiddenViews.add(view);
        this.mCallback.onEnteredHiddenState(view);
    }
    
    private boolean unhideViewInternal(final View view) {
        if (!this.mHiddenViews.remove(view)) {
            return false;
        }
        this.mCallback.onLeftHiddenState(view);
        return true;
    }
    
    void addView(final View view, int n, final boolean b) {
        if (n >= 0) {
            n = this.getOffset(n);
        }
        else {
            n = this.mCallback.getChildCount();
        }
        this.mBucket.insert(n, b);
        if (b) {
            this.hideViewInternal(view);
        }
        this.mCallback.addView(view, n);
    }
    
    void addView(final View view, final boolean b) {
        this.addView(view, -1, b);
    }
    
    void attachViewToParent(final View view, int n, final ViewGroup$LayoutParams viewGroup$LayoutParams, final boolean b) {
        if (n >= 0) {
            n = this.getOffset(n);
        }
        else {
            n = this.mCallback.getChildCount();
        }
        this.mBucket.insert(n, b);
        if (b) {
            this.hideViewInternal(view);
        }
        this.mCallback.attachViewToParent(view, n, viewGroup$LayoutParams);
    }
    
    void detachViewFromParent(int offset) {
        offset = this.getOffset(offset);
        this.mBucket.remove(offset);
        this.mCallback.detachViewFromParent(offset);
    }
    
    View findHiddenNonRemovedView(final int n) {
        for (int size = this.mHiddenViews.size(), i = 0; i < size; ++i) {
            final View view = this.mHiddenViews.get(i);
            final RecyclerView.ViewHolder childViewHolder = this.mCallback.getChildViewHolder(view);
            if (childViewHolder.getLayoutPosition() == n && !childViewHolder.isInvalid() && !childViewHolder.isRemoved()) {
                return view;
            }
        }
        return null;
    }
    
    View getChildAt(int offset) {
        offset = this.getOffset(offset);
        return this.mCallback.getChildAt(offset);
    }
    
    int getChildCount() {
        return this.mCallback.getChildCount() - this.mHiddenViews.size();
    }
    
    View getUnfilteredChildAt(final int n) {
        return this.mCallback.getChildAt(n);
    }
    
    int getUnfilteredChildCount() {
        return this.mCallback.getChildCount();
    }
    
    void hide(final View obj) {
        final int indexOfChild = this.mCallback.indexOfChild(obj);
        if (indexOfChild >= 0) {
            this.mBucket.set(indexOfChild);
            this.hideViewInternal(obj);
            return;
        }
        throw new IllegalArgumentException("view is not a child, cannot hide " + obj);
    }
    
    int indexOfChild(final View view) {
        final int indexOfChild = this.mCallback.indexOfChild(view);
        if (indexOfChild == -1) {
            return -1;
        }
        if (!this.mBucket.get(indexOfChild)) {
            return indexOfChild - this.mBucket.countOnesBefore(indexOfChild);
        }
        return -1;
    }
    
    boolean isHidden(final View view) {
        return this.mHiddenViews.contains(view);
    }
    
    void removeAllViewsUnfiltered() {
        this.mBucket.reset();
        for (int i = this.mHiddenViews.size() - 1; i >= 0; --i) {
            this.mCallback.onLeftHiddenState(this.mHiddenViews.get(i));
            this.mHiddenViews.remove(i);
        }
        this.mCallback.removeAllViews();
    }
    
    void removeView(final View view) {
        final int indexOfChild = this.mCallback.indexOfChild(view);
        if (indexOfChild >= 0) {
            if (this.mBucket.remove(indexOfChild)) {
                this.unhideViewInternal(view);
            }
            this.mCallback.removeViewAt(indexOfChild);
        }
    }
    
    void removeViewAt(int offset) {
        offset = this.getOffset(offset);
        final View child = this.mCallback.getChildAt(offset);
        if (child != null) {
            if (this.mBucket.remove(offset)) {
                this.unhideViewInternal(child);
            }
            this.mCallback.removeViewAt(offset);
        }
    }
    
    boolean removeViewIfHidden(final View view) {
        final int indexOfChild = this.mCallback.indexOfChild(view);
        if (indexOfChild == -1) {
            this.unhideViewInternal(view);
            return true;
        }
        if (!this.mBucket.get(indexOfChild)) {
            return false;
        }
        this.mBucket.remove(indexOfChild);
        this.unhideViewInternal(view);
        this.mCallback.removeViewAt(indexOfChild);
        return true;
    }
    
    @Override
    public String toString() {
        return this.mBucket.toString() + ", hidden list:" + this.mHiddenViews.size();
    }
    
    void unhide(final View view) {
        final int indexOfChild = this.mCallback.indexOfChild(view);
        if (indexOfChild < 0) {
            throw new IllegalArgumentException("view is not a child, cannot hide " + view);
        }
        if (this.mBucket.get(indexOfChild)) {
            this.mBucket.clear(indexOfChild);
            this.unhideViewInternal(view);
            return;
        }
        throw new RuntimeException("trying to unhide a view that was not hidden" + view);
    }
    
    static class Bucket
    {
        static final int BITS_PER_WORD = 64;
        static final long LAST_BIT = Long.MIN_VALUE;
        long mData;
        Bucket mNext;
        
        Bucket() {
            this.mData = 0L;
        }
        
        private void ensureNext() {
            if (this.mNext == null) {
                this.mNext = new Bucket();
            }
        }
        
        void clear(final int n) {
            if (n < 64) {
                this.mData &= ~(1L << n);
            }
            else if (this.mNext != null) {
                this.mNext.clear(n - 64);
            }
        }
        
        int countOnesBefore(final int n) {
            if (this.mNext != null) {
                if (n >= 64) {
                    return this.mNext.countOnesBefore(n - 64) + Long.bitCount(this.mData);
                }
                return Long.bitCount(this.mData & (1L << n) - 1L);
            }
            else {
                if (n < 64) {
                    return Long.bitCount(this.mData & (1L << n) - 1L);
                }
                return Long.bitCount(this.mData);
            }
        }
        
        boolean get(final int n) {
            if (n < 64) {
                return (this.mData & 1L << n) != 0x0L;
            }
            this.ensureNext();
            return this.mNext.get(n - 64);
        }
        
        void insert(final int n, final boolean b) {
            if (n < 64) {
                final boolean b2 = (this.mData & Long.MIN_VALUE) != 0x0L;
                final long n2 = (1L << n) - 1L;
                this.mData = ((~n2 & this.mData) << 1 | (this.mData & n2));
                if (!b) {
                    this.clear(n);
                }
                else {
                    this.set(n);
                }
                if (b2 || this.mNext != null) {
                    this.ensureNext();
                    this.mNext.insert(0, b2);
                }
            }
            else {
                this.ensureNext();
                this.mNext.insert(n - 64, b);
            }
        }
        
        boolean remove(final int n) {
            if (n < 64) {
                final long n2 = 1L << n;
                final boolean b = (this.mData & n2) != 0x0L;
                this.mData &= ~n2;
                final long n3 = n2 - 1L;
                this.mData = (Long.rotateRight(~n3 & this.mData, 1) | (this.mData & n3));
                if (this.mNext != null) {
                    if (this.mNext.get(0)) {
                        this.set(63);
                    }
                    this.mNext.remove(0);
                }
                return b;
            }
            this.ensureNext();
            return this.mNext.remove(n - 64);
        }
        
        void reset() {
            this.mData = 0L;
            if (this.mNext != null) {
                this.mNext.reset();
            }
        }
        
        void set(final int n) {
            if (n < 64) {
                this.mData |= 1L << n;
            }
            else {
                this.ensureNext();
                this.mNext.set(n - 64);
            }
        }
        
        @Override
        public String toString() {
            String s;
            if (this.mNext != null) {
                s = this.mNext.toString() + "xx" + Long.toBinaryString(this.mData);
            }
            else {
                s = Long.toBinaryString(this.mData);
            }
            return s;
        }
    }
    
    interface Callback
    {
        void addView(final View p0, final int p1);
        
        void attachViewToParent(final View p0, final int p1, final ViewGroup$LayoutParams p2);
        
        void detachViewFromParent(final int p0);
        
        View getChildAt(final int p0);
        
        int getChildCount();
        
        RecyclerView.ViewHolder getChildViewHolder(final View p0);
        
        int indexOfChild(final View p0);
        
        void onEnteredHiddenState(final View p0);
        
        void onLeftHiddenState(final View p0);
        
        void removeAllViews();
        
        void removeViewAt(final int p0);
    }
}
