// 
// Decompiled by Procyon v0.6.0
// 

package android.support.v4.widget;

import java.util.Comparator;
import java.util.List;
import java.util.Collections;
import java.util.ArrayList;
import android.support.annotation.Nullable;
import android.support.annotation.NonNull;
import android.graphics.Rect;

class FocusStrategy
{
    private static boolean beamBeats(final int n, @NonNull final Rect rect, @NonNull final Rect rect2, @NonNull final Rect rect3) {
        boolean b = false;
        final boolean beamsOverlap = beamsOverlap(n, rect, rect2);
        if (beamsOverlap(n, rect, rect3) || !beamsOverlap) {
            return false;
        }
        if (!isToDirectionOf(n, rect, rect3)) {
            return true;
        }
        if (n != 17 && n != 66) {
            if (majorAxisDistance(n, rect, rect2) < majorAxisDistanceToFarEdge(n, rect, rect3)) {
                b = true;
            }
            return b;
        }
        return true;
    }
    
    private static boolean beamsOverlap(final int n, @NonNull final Rect rect, @NonNull final Rect rect2) {
        final boolean b = true;
        boolean b2 = true;
        switch (n) {
            default: {
                throw new IllegalArgumentException("direction must be one of {FOCUS_UP, FOCUS_DOWN, FOCUS_LEFT, FOCUS_RIGHT}.");
            }
            case 17:
            case 66: {
                if (rect2.bottom < rect.top || rect2.top > rect.bottom) {
                    b2 = false;
                }
                return b2;
            }
            case 33:
            case 130: {
                if (rect2.right >= rect.left) {
                    final boolean b3 = b;
                    if (rect2.left <= rect.right) {
                        return b3;
                    }
                }
                return false;
            }
        }
    }
    
    public static <L, T> T findNextFocusInAbsoluteDirection(@NonNull final L l, @NonNull final CollectionAdapter<L, T> collectionAdapter, @NonNull final BoundsAdapter<T> boundsAdapter, @Nullable final T t, @NonNull final Rect rect, final int n) {
        final Rect rect2 = new Rect(rect);
        switch (n) {
            default: {
                throw new IllegalArgumentException("direction must be one of {FOCUS_UP, FOCUS_DOWN, FOCUS_LEFT, FOCUS_RIGHT}.");
            }
            case 17: {
                rect2.offset(rect.width() + 1, 0);
                break;
            }
            case 66: {
                rect2.offset(-(rect.width() + 1), 0);
                break;
            }
            case 33: {
                rect2.offset(0, rect.height() + 1);
                break;
            }
            case 130: {
                rect2.offset(0, -(rect.height() + 1));
                break;
            }
        }
        final int size = collectionAdapter.size(l);
        final Rect rect3 = new Rect();
        int i = 0;
        T t2 = null;
        while (i < size) {
            final T value = collectionAdapter.get(l, i);
            T t3 = t2;
            if (value != t) {
                boundsAdapter.obtainBounds(value, rect3);
                if (!isBetterCandidate(n, rect, rect3, rect2)) {
                    t3 = t2;
                }
                else {
                    rect2.set(rect3);
                    t3 = value;
                }
            }
            ++i;
            t2 = t3;
        }
        return t2;
    }
    
    public static <L, T> T findNextFocusInRelativeDirection(@NonNull final L l, @NonNull final CollectionAdapter<L, T> collectionAdapter, @NonNull final BoundsAdapter<T> boundsAdapter, @Nullable final T t, final int n, final boolean b, final boolean b2) {
        final int size = collectionAdapter.size(l);
        final ArrayList list = new ArrayList<T>(size);
        for (int i = 0; i < size; ++i) {
            list.add(collectionAdapter.get(l, i));
        }
        Collections.sort((List<T>)list, new SequentialComparator<Object>(b, (BoundsAdapter<? super T>)boundsAdapter));
        switch (n) {
            default: {
                throw new IllegalArgumentException("direction must be one of {FOCUS_FORWARD, FOCUS_BACKWARD}.");
            }
            case 2: {
                return getNextFocusable(t, (ArrayList<T>)list, b2);
            }
            case 1: {
                return getPreviousFocusable(t, (ArrayList<T>)list, b2);
            }
        }
    }
    
    private static <T> T getNextFocusable(final T o, final ArrayList<T> list, final boolean b) {
        final int size = list.size();
        int lastIndex;
        if (o != null) {
            lastIndex = list.lastIndexOf(o);
        }
        else {
            lastIndex = -1;
        }
        if (++lastIndex < size) {
            return (T)list.get(lastIndex);
        }
        if (b && size > 0) {
            return (T)list.get(0);
        }
        return null;
    }
    
    private static <T> T getPreviousFocusable(final T o, final ArrayList<T> list, final boolean b) {
        final int size = list.size();
        int index;
        if (o != null) {
            index = list.indexOf(o);
        }
        else {
            index = size;
        }
        if (--index >= 0) {
            return (T)list.get(index);
        }
        if (b && size > 0) {
            return (T)list.get(size - 1);
        }
        return null;
    }
    
    private static int getWeightedDistanceFor(final int n, final int n2) {
        return n * 13 * n + n2 * n2;
    }
    
    private static boolean isBetterCandidate(final int n, @NonNull final Rect rect, @NonNull final Rect rect2, @NonNull final Rect rect3) {
        boolean b = false;
        if (!isCandidate(rect, rect2, n)) {
            return false;
        }
        if (!isCandidate(rect, rect3, n)) {
            return true;
        }
        if (beamBeats(n, rect, rect2, rect3)) {
            return true;
        }
        if (!beamBeats(n, rect, rect3, rect2)) {
            if (getWeightedDistanceFor(majorAxisDistance(n, rect, rect2), minorAxisDistance(n, rect, rect2)) < getWeightedDistanceFor(majorAxisDistance(n, rect, rect3), minorAxisDistance(n, rect, rect3))) {
                b = true;
            }
            return b;
        }
        return false;
    }
    
    private static boolean isCandidate(@NonNull final Rect rect, @NonNull final Rect rect2, final int n) {
        final boolean b = true;
        final boolean b2 = true;
        final boolean b3 = true;
        boolean b4 = true;
        switch (n) {
            default: {
                throw new IllegalArgumentException("direction must be one of {FOCUS_UP, FOCUS_DOWN, FOCUS_LEFT, FOCUS_RIGHT}.");
            }
            case 17: {
                if (rect.right > rect2.right || rect.left >= rect2.right) {
                    if (rect.left > rect2.left) {
                        return b4;
                    }
                }
                b4 = false;
                return b4;
            }
            case 66: {
                if (rect.left < rect2.left || rect.right <= rect2.left) {
                    final boolean b5 = b;
                    if (rect.right < rect2.right) {
                        return b5;
                    }
                }
                return false;
            }
            case 33: {
                if (rect.bottom > rect2.bottom || rect.top >= rect2.bottom) {
                    final boolean b6 = b2;
                    if (rect.top > rect2.top) {
                        return b6;
                    }
                }
                return false;
            }
            case 130: {
                if (rect.top < rect2.top || rect.bottom <= rect2.top) {
                    final boolean b7 = b3;
                    if (rect.bottom < rect2.bottom) {
                        return b7;
                    }
                }
                return false;
            }
        }
    }
    
    private static boolean isToDirectionOf(final int n, @NonNull final Rect rect, @NonNull final Rect rect2) {
        final boolean b = false;
        final boolean b2 = false;
        final boolean b3 = false;
        boolean b4 = false;
        switch (n) {
            default: {
                throw new IllegalArgumentException("direction must be one of {FOCUS_UP, FOCUS_DOWN, FOCUS_LEFT, FOCUS_RIGHT}.");
            }
            case 17: {
                if (rect.left >= rect2.right) {
                    b4 = true;
                }
                return b4;
            }
            case 66: {
                return rect.right <= rect2.left || b;
            }
            case 33: {
                return rect.top >= rect2.bottom || b2;
            }
            case 130: {
                return rect.bottom <= rect2.top || b3;
            }
        }
    }
    
    private static int majorAxisDistance(final int n, @NonNull final Rect rect, @NonNull final Rect rect2) {
        return Math.max(0, majorAxisDistanceRaw(n, rect, rect2));
    }
    
    private static int majorAxisDistanceRaw(final int n, @NonNull final Rect rect, @NonNull final Rect rect2) {
        switch (n) {
            default: {
                throw new IllegalArgumentException("direction must be one of {FOCUS_UP, FOCUS_DOWN, FOCUS_LEFT, FOCUS_RIGHT}.");
            }
            case 17: {
                return rect.left - rect2.right;
            }
            case 66: {
                return rect2.left - rect.right;
            }
            case 33: {
                return rect.top - rect2.bottom;
            }
            case 130: {
                return rect2.top - rect.bottom;
            }
        }
    }
    
    private static int majorAxisDistanceToFarEdge(final int n, @NonNull final Rect rect, @NonNull final Rect rect2) {
        return Math.max(1, majorAxisDistanceToFarEdgeRaw(n, rect, rect2));
    }
    
    private static int majorAxisDistanceToFarEdgeRaw(final int n, @NonNull final Rect rect, @NonNull final Rect rect2) {
        switch (n) {
            default: {
                throw new IllegalArgumentException("direction must be one of {FOCUS_UP, FOCUS_DOWN, FOCUS_LEFT, FOCUS_RIGHT}.");
            }
            case 17: {
                return rect.left - rect2.left;
            }
            case 66: {
                return rect2.right - rect.right;
            }
            case 33: {
                return rect.top - rect2.top;
            }
            case 130: {
                return rect2.bottom - rect.bottom;
            }
        }
    }
    
    private static int minorAxisDistance(final int n, @NonNull final Rect rect, @NonNull final Rect rect2) {
        switch (n) {
            default: {
                throw new IllegalArgumentException("direction must be one of {FOCUS_UP, FOCUS_DOWN, FOCUS_LEFT, FOCUS_RIGHT}.");
            }
            case 17:
            case 66: {
                return Math.abs(rect.top + rect.height() / 2 - (rect2.top + rect2.height() / 2));
            }
            case 33:
            case 130: {
                return Math.abs(rect.left + rect.width() / 2 - (rect2.left + rect2.width() / 2));
            }
        }
    }
    
    public interface BoundsAdapter<T>
    {
        void obtainBounds(final T p0, final Rect p1);
    }
    
    public interface CollectionAdapter<T, V>
    {
        V get(final T p0, final int p1);
        
        int size(final T p0);
    }
    
    private static class SequentialComparator<T> implements Comparator<T>
    {
        private final BoundsAdapter<T> mAdapter;
        private final boolean mIsLayoutRtl;
        private final Rect mTemp1;
        private final Rect mTemp2;
        
        SequentialComparator(final boolean mIsLayoutRtl, final BoundsAdapter<T> mAdapter) {
            this.mTemp1 = new Rect();
            this.mTemp2 = new Rect();
            this.mIsLayoutRtl = mIsLayoutRtl;
            this.mAdapter = mAdapter;
        }
        
        @Override
        public int compare(final T t, final T t2) {
            final int n = 1;
            final int n2 = 1;
            int n3 = -1;
            final Rect mTemp1 = this.mTemp1;
            final Rect mTemp2 = this.mTemp2;
            this.mAdapter.obtainBounds(t, mTemp1);
            this.mAdapter.obtainBounds(t2, mTemp2);
            if (mTemp1.top < mTemp2.top) {
                return -1;
            }
            if (mTemp1.top > mTemp2.top) {
                return 1;
            }
            if (mTemp1.left < mTemp2.left) {
                if (this.mIsLayoutRtl) {
                    n3 = 1;
                }
                return n3;
            }
            if (mTemp1.left > mTemp2.left) {
                int n4;
                if (!this.mIsLayoutRtl) {
                    n4 = n2;
                }
                else {
                    n4 = -1;
                }
                return n4;
            }
            if (mTemp1.bottom < mTemp2.bottom) {
                return -1;
            }
            if (mTemp1.bottom > mTemp2.bottom) {
                return 1;
            }
            if (mTemp1.right < mTemp2.right) {
                if (this.mIsLayoutRtl) {
                    n3 = 1;
                }
                return n3;
            }
            if (mTemp1.right <= mTemp2.right) {
                return 0;
            }
            int n5;
            if (!this.mIsLayoutRtl) {
                n5 = n;
            }
            else {
                n5 = -1;
            }
            return n5;
        }
    }
}
