package android.support.v7.widget;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.ViewGroup;
import java.util.ArrayList;
import java.util.List;
/* JADX INFO: Access modifiers changed from: package-private */
/* loaded from: classes.dex */
public class ChildHelper {
    private static final boolean DEBUG = false;
    private static final String TAG = "ChildrenHelper";
    final Callback mCallback;
    final Bucket mBucket = new Bucket();
    final List<View> mHiddenViews = new ArrayList();

    /* JADX INFO: Access modifiers changed from: package-private */
    /* loaded from: classes.dex */
    public static class Bucket {
        static final int BITS_PER_WORD = 64;
        static final long LAST_BIT = Long.MIN_VALUE;
        long mData = 0;
        Bucket mNext;

        Bucket() {
        }

        private void ensureNext() {
            if (this.mNext != null) {
                return;
            }
            this.mNext = new Bucket();
        }

        void clear(int i) {
            if (i < 64) {
                this.mData &= (1 << i) ^ (-1);
            } else if (this.mNext != null) {
                this.mNext.clear(i - 64);
            }
        }

        int countOnesBefore(int i) {
            return this.mNext != null ? i >= 64 ? this.mNext.countOnesBefore(i - 64) + Long.bitCount(this.mData) : Long.bitCount(this.mData & ((1 << i) - 1)) : i < 64 ? Long.bitCount(this.mData & ((1 << i) - 1)) : Long.bitCount(this.mData);
        }

        boolean get(int i) {
            if (i < 64) {
                return (this.mData & (1 << i)) != 0;
            }
            ensureNext();
            return this.mNext.get(i - 64);
        }

        void insert(int i, boolean z) {
            if (i >= 64) {
                ensureNext();
                this.mNext.insert(i - 64, z);
                return;
            }
            boolean z2 = (this.mData & LAST_BIT) != 0;
            long j = (1 << i) - 1;
            this.mData = (((j ^ (-1)) & this.mData) << 1) | (this.mData & j);
            if (z) {
                set(i);
            } else {
                clear(i);
            }
            if (!z2 && this.mNext == null) {
                return;
            }
            ensureNext();
            this.mNext.insert(0, z2);
        }

        boolean remove(int i) {
            if (i >= 64) {
                ensureNext();
                return this.mNext.remove(i - 64);
            }
            long j = 1 << i;
            boolean z = (this.mData & j) != 0;
            this.mData &= j ^ (-1);
            long j2 = j - 1;
            this.mData = Long.rotateRight((j2 ^ (-1)) & this.mData, 1) | (this.mData & j2);
            if (this.mNext != null) {
                if (this.mNext.get(0)) {
                    set(63);
                }
                this.mNext.remove(0);
            }
            return z;
        }

        void reset() {
            this.mData = 0L;
            if (this.mNext == null) {
                return;
            }
            this.mNext.reset();
        }

        void set(int i) {
            if (i < 64) {
                this.mData |= 1 << i;
                return;
            }
            ensureNext();
            this.mNext.set(i - 64);
        }

        public String toString() {
            return this.mNext != null ? this.mNext.toString() + "xx" + Long.toBinaryString(this.mData) : Long.toBinaryString(this.mData);
        }
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    /* loaded from: classes.dex */
    public interface Callback {
        void addView(View view, int i);

        void attachViewToParent(View view, int i, ViewGroup.LayoutParams layoutParams);

        void detachViewFromParent(int i);

        View getChildAt(int i);

        int getChildCount();

        RecyclerView.ViewHolder getChildViewHolder(View view);

        int indexOfChild(View view);

        void onEnteredHiddenState(View view);

        void onLeftHiddenState(View view);

        void removeAllViews();

        void removeViewAt(int i);
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public ChildHelper(Callback callback) {
        this.mCallback = callback;
    }

    private int getOffset(int i) {
        if (i >= 0) {
            int childCount = this.mCallback.getChildCount();
            int i2 = i;
            while (i2 < childCount) {
                int countOnesBefore = i - (i2 - this.mBucket.countOnesBefore(i2));
                if (countOnesBefore == 0) {
                    while (this.mBucket.get(i2)) {
                        i2++;
                    }
                    return i2;
                }
                i2 += countOnesBefore;
            }
            return -1;
        }
        return -1;
    }

    private void hideViewInternal(View view) {
        this.mHiddenViews.add(view);
        this.mCallback.onEnteredHiddenState(view);
    }

    private boolean unhideViewInternal(View view) {
        if (this.mHiddenViews.remove(view)) {
            this.mCallback.onLeftHiddenState(view);
            return true;
        }
        return false;
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public void addView(View view, int i, boolean z) {
        int offset = i >= 0 ? getOffset(i) : this.mCallback.getChildCount();
        this.mBucket.insert(offset, z);
        if (z) {
            hideViewInternal(view);
        }
        this.mCallback.addView(view, offset);
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public void addView(View view, boolean z) {
        addView(view, -1, z);
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public void attachViewToParent(View view, int i, ViewGroup.LayoutParams layoutParams, boolean z) {
        int offset = i >= 0 ? getOffset(i) : this.mCallback.getChildCount();
        this.mBucket.insert(offset, z);
        if (z) {
            hideViewInternal(view);
        }
        this.mCallback.attachViewToParent(view, offset, layoutParams);
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public void detachViewFromParent(int i) {
        int offset = getOffset(i);
        this.mBucket.remove(offset);
        this.mCallback.detachViewFromParent(offset);
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public View findHiddenNonRemovedView(int i) {
        int size = this.mHiddenViews.size();
        for (int i2 = 0; i2 < size; i2++) {
            View view = this.mHiddenViews.get(i2);
            RecyclerView.ViewHolder childViewHolder = this.mCallback.getChildViewHolder(view);
            if (childViewHolder.getLayoutPosition() == i && !childViewHolder.isInvalid() && !childViewHolder.isRemoved()) {
                return view;
            }
        }
        return null;
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public View getChildAt(int i) {
        return this.mCallback.getChildAt(getOffset(i));
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public int getChildCount() {
        return this.mCallback.getChildCount() - this.mHiddenViews.size();
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public View getUnfilteredChildAt(int i) {
        return this.mCallback.getChildAt(i);
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public int getUnfilteredChildCount() {
        return this.mCallback.getChildCount();
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public void hide(View view) {
        int indexOfChild = this.mCallback.indexOfChild(view);
        if (indexOfChild < 0) {
            throw new IllegalArgumentException("view is not a child, cannot hide " + view);
        }
        this.mBucket.set(indexOfChild);
        hideViewInternal(view);
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public int indexOfChild(View view) {
        int indexOfChild = this.mCallback.indexOfChild(view);
        if (indexOfChild == -1 || this.mBucket.get(indexOfChild)) {
            return -1;
        }
        return indexOfChild - this.mBucket.countOnesBefore(indexOfChild);
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public boolean isHidden(View view) {
        return this.mHiddenViews.contains(view);
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public void removeAllViewsUnfiltered() {
        this.mBucket.reset();
        for (int size = this.mHiddenViews.size() - 1; size >= 0; size--) {
            this.mCallback.onLeftHiddenState(this.mHiddenViews.get(size));
            this.mHiddenViews.remove(size);
        }
        this.mCallback.removeAllViews();
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public void removeView(View view) {
        int indexOfChild = this.mCallback.indexOfChild(view);
        if (indexOfChild >= 0) {
            if (this.mBucket.remove(indexOfChild)) {
                unhideViewInternal(view);
            }
            this.mCallback.removeViewAt(indexOfChild);
        }
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public void removeViewAt(int i) {
        int offset = getOffset(i);
        View childAt = this.mCallback.getChildAt(offset);
        if (childAt != null) {
            if (this.mBucket.remove(offset)) {
                unhideViewInternal(childAt);
            }
            this.mCallback.removeViewAt(offset);
        }
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public boolean removeViewIfHidden(View view) {
        int indexOfChild = this.mCallback.indexOfChild(view);
        if (indexOfChild == -1) {
            unhideViewInternal(view);
            return true;
        } else if (this.mBucket.get(indexOfChild)) {
            this.mBucket.remove(indexOfChild);
            unhideViewInternal(view);
            this.mCallback.removeViewAt(indexOfChild);
            return true;
        } else {
            return false;
        }
    }

    public String toString() {
        return this.mBucket.toString() + ", hidden list:" + this.mHiddenViews.size();
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public void unhide(View view) {
        int indexOfChild = this.mCallback.indexOfChild(view);
        if (indexOfChild < 0) {
            throw new IllegalArgumentException("view is not a child, cannot hide " + view);
        }
        if (!this.mBucket.get(indexOfChild)) {
            throw new RuntimeException("trying to unhide a view that was not hidden" + view);
        }
        this.mBucket.clear(indexOfChild);
        unhideViewInternal(view);
    }
}
