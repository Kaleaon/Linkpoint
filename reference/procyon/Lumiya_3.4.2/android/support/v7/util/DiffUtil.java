// 
// Decompiled by Procyon v0.6.0
// 

package android.support.v7.util;

import android.support.annotation.VisibleForTesting;
import android.support.v7.widget.RecyclerView;
import java.util.Iterator;
import android.support.annotation.Nullable;
import java.util.Arrays;
import java.util.List;
import java.util.Collections;
import java.util.ArrayList;
import java.util.Comparator;

public class DiffUtil
{
    private static final Comparator<Snake> SNAKE_COMPARATOR;
    
    static {
        SNAKE_COMPARATOR = new Comparator<Snake>() {
            @Override
            public int compare(final Snake snake, final Snake snake2) {
                int n = snake.x - snake2.x;
                if (n == 0) {
                    n = snake.y - snake2.y;
                }
                return n;
            }
        };
    }
    
    private DiffUtil() {
    }
    
    public static DiffResult calculateDiff(final Callback callback) {
        return calculateDiff(callback, true);
    }
    
    public static DiffResult calculateDiff(final Callback callback, final boolean b) {
        final int oldListSize = callback.getOldListSize();
        final int newListSize = callback.getNewListSize();
        final ArrayList list = new ArrayList();
        final ArrayList list2 = new ArrayList();
        list2.add(new Range(0, oldListSize, 0, newListSize));
        final int n = oldListSize + newListSize + Math.abs(oldListSize - newListSize);
        final int[] array = new int[n * 2];
        final int[] array2 = new int[n * 2];
        final ArrayList list3 = new ArrayList();
        while (!list2.isEmpty()) {
            final Range range = (Range)list2.remove(list2.size() - 1);
            final Snake diffPartial = diffPartial(callback, range.oldListStart, range.oldListEnd, range.newListStart, range.newListEnd, array, array2, n);
            if (diffPartial == null) {
                list3.add(range);
            }
            else {
                if (diffPartial.size > 0) {
                    list.add(diffPartial);
                }
                diffPartial.x += range.oldListStart;
                diffPartial.y += range.newListStart;
                Object o;
                if (!list3.isEmpty()) {
                    o = list3.remove(list3.size() - 1);
                }
                else {
                    o = new Range();
                }
                ((Range)o).oldListStart = range.oldListStart;
                ((Range)o).newListStart = range.newListStart;
                if (!diffPartial.reverse) {
                    if (!diffPartial.removal) {
                        ((Range)o).oldListEnd = diffPartial.x;
                        ((Range)o).newListEnd = diffPartial.y - 1;
                    }
                    else {
                        ((Range)o).oldListEnd = diffPartial.x - 1;
                        ((Range)o).newListEnd = diffPartial.y;
                    }
                }
                else {
                    ((Range)o).oldListEnd = diffPartial.x;
                    ((Range)o).newListEnd = diffPartial.y;
                }
                list2.add(o);
                if (!diffPartial.reverse) {
                    range.oldListStart = diffPartial.x + diffPartial.size;
                    range.newListStart = diffPartial.y + diffPartial.size;
                }
                else if (!diffPartial.removal) {
                    range.oldListStart = diffPartial.x + diffPartial.size;
                    range.newListStart = diffPartial.y + diffPartial.size + 1;
                }
                else {
                    range.oldListStart = diffPartial.x + diffPartial.size + 1;
                    range.newListStart = diffPartial.y + diffPartial.size;
                }
                list2.add(range);
            }
        }
        Collections.sort((List<Object>)list, (Comparator<? super Object>)DiffUtil.SNAKE_COMPARATOR);
        return new DiffResult(callback, list, array, array2, b);
    }
    
    private static Snake diffPartial(final Callback callback, final int n, int n2, final int n3, int n4, final int[] a, final int[] a2, final int n5) {
        final int val = n2 - n;
        final int n6 = n4 - n3;
        if (n2 - n >= 1 && n4 - n3 >= 1) {
            final int n7 = val - n6;
            final int n8 = (val + n6 + 1) / 2;
            Arrays.fill(a, n5 - n8 - 1, n5 + n8 + 1, 0);
            Arrays.fill(a2, n5 - n8 - 1 + n7, n5 + n8 + 1 + n7, val);
            if (n7 % 2 == 0) {
                n4 = 0;
            }
            else {
                n4 = 1;
            }
            for (int i = 0; i <= n8; ++i) {
                for (int j = -i; j <= i; j += 2) {
                    boolean removal;
                    if (j != -i && (j == i || a[n5 + j - 1] >= a[n5 + j + 1])) {
                        n2 = a[n5 + j - 1] + 1;
                        removal = true;
                    }
                    else {
                        n2 = a[n5 + j + 1];
                        removal = false;
                    }
                    int n9;
                    for (n9 = n2, n2 -= j; n9 < val && n2 < n6 && callback.areItemsTheSame(n + n9, n3 + n2); ++n9, ++n2) {}
                    a[n5 + j] = n9;
                    if (n4 != 0 && j >= n7 - i + 1 && j <= n7 + i - 1 && a[n5 + j] >= a2[n5 + j]) {
                        final Snake snake = new Snake();
                        snake.x = a2[n5 + j];
                        snake.y = snake.x - j;
                        snake.size = a[n5 + j] - a2[n5 + j];
                        snake.removal = removal;
                        snake.reverse = false;
                        return snake;
                    }
                }
                for (int k = -i; k <= i; k += 2) {
                    final int n10 = k + n7;
                    boolean removal2;
                    if (n10 != i + n7 && (n10 == -i + n7 || a2[n5 + n10 - 1] >= a2[n5 + n10 + 1])) {
                        n2 = a2[n5 + n10 + 1] - 1;
                        removal2 = true;
                    }
                    else {
                        n2 = a2[n5 + n10 - 1];
                        removal2 = false;
                    }
                    int n11;
                    for (n11 = n2, n2 -= n10; n11 > 0 && n2 > 0 && callback.areItemsTheSame(n + n11 - 1, n3 + n2 - 1); --n11, --n2) {}
                    a2[n5 + n10] = n11;
                    if (n4 == 0 && k + n7 >= -i && k + n7 <= i && a[n5 + n10] >= a2[n5 + n10]) {
                        final Snake snake2 = new Snake();
                        snake2.x = a2[n5 + n10];
                        snake2.y = snake2.x - n10;
                        snake2.size = a[n5 + n10] - a2[n5 + n10];
                        snake2.removal = removal2;
                        snake2.reverse = true;
                        return snake2;
                    }
                }
            }
            throw new IllegalStateException("DiffUtil hit an unexpected case while trying to calculate the optimal path. Please make sure your data is not changing during the diff calculation.");
        }
        return null;
    }
    
    public abstract static class Callback
    {
        public abstract boolean areContentsTheSame(final int p0, final int p1);
        
        public abstract boolean areItemsTheSame(final int p0, final int p1);
        
        @Nullable
        public Object getChangePayload(final int n, final int n2) {
            return null;
        }
        
        public abstract int getNewListSize();
        
        public abstract int getOldListSize();
    }
    
    public static class DiffResult
    {
        private static final int FLAG_CHANGED = 2;
        private static final int FLAG_IGNORE = 16;
        private static final int FLAG_MASK = 31;
        private static final int FLAG_MOVED_CHANGED = 4;
        private static final int FLAG_MOVED_NOT_CHANGED = 8;
        private static final int FLAG_NOT_CHANGED = 1;
        private static final int FLAG_OFFSET = 5;
        private final Callback mCallback;
        private final boolean mDetectMoves;
        private final int[] mNewItemStatuses;
        private final int mNewListSize;
        private final int[] mOldItemStatuses;
        private final int mOldListSize;
        private final List<Snake> mSnakes;
        
        DiffResult(final Callback mCallback, final List<Snake> mSnakes, final int[] mOldItemStatuses, final int[] mNewItemStatuses, final boolean mDetectMoves) {
            this.mSnakes = mSnakes;
            this.mOldItemStatuses = mOldItemStatuses;
            this.mNewItemStatuses = mNewItemStatuses;
            Arrays.fill(this.mOldItemStatuses, 0);
            Arrays.fill(this.mNewItemStatuses, 0);
            this.mCallback = mCallback;
            this.mOldListSize = mCallback.getOldListSize();
            this.mNewListSize = mCallback.getNewListSize();
            this.mDetectMoves = mDetectMoves;
            this.addRootSnake();
            this.findMatchingItems();
        }
        
        private void addRootSnake() {
            Snake snake = null;
            if (!this.mSnakes.isEmpty()) {
                snake = this.mSnakes.get(0);
            }
            if (snake == null || snake.x != 0 || snake.y != 0) {
                final Snake snake2 = new Snake();
                snake2.x = 0;
                snake2.y = 0;
                snake2.removal = false;
                snake2.size = 0;
                snake2.reverse = false;
                this.mSnakes.add(0, snake2);
            }
        }
        
        private void dispatchAdditions(final List<PostponedUpdate> list, final ListUpdateCallback listUpdateCallback, final int n, int i, final int n2) {
            if (this.mDetectMoves) {
                --i;
                while (i >= 0) {
                    final int n3 = this.mNewItemStatuses[n2 + i] & 0x1F;
                    switch (n3) {
                        default: {
                            throw new IllegalStateException("unknown flag for pos " + (i + n2) + " " + Long.toBinaryString(n3));
                        }
                        case 0: {
                            listUpdateCallback.onInserted(n, 1);
                            for (final PostponedUpdate postponedUpdate : list) {
                                ++postponedUpdate.currentPos;
                            }
                            break;
                        }
                        case 4:
                        case 8: {
                            final int n4 = this.mNewItemStatuses[n2 + i] >> 5;
                            listUpdateCallback.onMoved(removePostponedUpdate(list, n4, true).currentPos, n);
                            if (n3 == 4) {
                                listUpdateCallback.onChanged(n, 1, this.mCallback.getChangePayload(n4, n2 + i));
                                break;
                            }
                            break;
                        }
                        case 16: {
                            list.add(new PostponedUpdate(n2 + i, n, false));
                            break;
                        }
                    }
                    --i;
                }
                return;
            }
            listUpdateCallback.onInserted(n, i);
        }
        
        private void dispatchRemovals(final List<PostponedUpdate> list, final ListUpdateCallback listUpdateCallback, final int n, int i, final int n2) {
            if (this.mDetectMoves) {
                --i;
                while (i >= 0) {
                    final int n3 = this.mOldItemStatuses[n2 + i] & 0x1F;
                    switch (n3) {
                        default: {
                            throw new IllegalStateException("unknown flag for pos " + (i + n2) + " " + Long.toBinaryString(n3));
                        }
                        case 0: {
                            listUpdateCallback.onRemoved(n + i, 1);
                            for (final PostponedUpdate postponedUpdate : list) {
                                --postponedUpdate.currentPos;
                            }
                            break;
                        }
                        case 4:
                        case 8: {
                            final int n4 = this.mOldItemStatuses[n2 + i] >> 5;
                            final PostponedUpdate removePostponedUpdate = removePostponedUpdate(list, n4, false);
                            listUpdateCallback.onMoved(n + i, removePostponedUpdate.currentPos - 1);
                            if (n3 == 4) {
                                listUpdateCallback.onChanged(removePostponedUpdate.currentPos - 1, 1, this.mCallback.getChangePayload(n2 + i, n4));
                                break;
                            }
                            break;
                        }
                        case 16: {
                            list.add(new PostponedUpdate(n2 + i, n + i, true));
                            break;
                        }
                    }
                    --i;
                }
                return;
            }
            listUpdateCallback.onRemoved(n, i);
        }
        
        private void findAddition(final int n, final int n2, final int n3) {
            if (this.mOldItemStatuses[n - 1] == 0) {
                this.findMatchingItem(n, n2, n3, false);
            }
        }
        
        private boolean findMatchingItem(int n, int i, int j, final boolean b) {
            final int n2 = 4;
            int n3;
            int n4;
            if (!b) {
                n3 = n - 1;
                n4 = n - 1;
            }
            else {
                final int n5 = i - 1;
                n3 = i - 1;
                n4 = n;
                i = n5;
            }
            final int n6 = n4;
            int y = i;
            i = n6;
            while (j >= 0) {
                final Snake snake = this.mSnakes.get(j);
                final int x = snake.x;
                final int size = snake.size;
                final int y2 = snake.y;
                final int size2 = snake.size;
                if (!b) {
                    for (i = y - 1; i >= y2 + size2; --i) {
                        if (this.mCallback.areItemsTheSame(n3, i)) {
                            if (!this.mCallback.areContentsTheSame(n3, i)) {
                                j = n2;
                            }
                            else {
                                j = 8;
                            }
                            this.mOldItemStatuses[n - 1] = (i << 5 | 0x10);
                            this.mNewItemStatuses[i] = (n - 1 << 5 | j);
                            return true;
                        }
                    }
                }
                else {
                    while (--i >= x + size) {
                        if (!this.mCallback.areItemsTheSame(i, n3)) {
                            continue;
                        }
                        if (!this.mCallback.areContentsTheSame(i, n3)) {
                            n = 4;
                        }
                        else {
                            n = 8;
                        }
                        this.mNewItemStatuses[n3] = (i << 5 | 0x10);
                        this.mOldItemStatuses[i] = (n | n3 << 5);
                        return true;
                    }
                }
                i = snake.x;
                y = snake.y;
                --j;
            }
            return false;
        }
        
        private void findMatchingItems() {
            int i = this.mOldListSize;
            int j = this.mNewListSize;
            for (int k = this.mSnakes.size() - 1; k >= 0; --k) {
                final Snake snake = this.mSnakes.get(k);
                final int x = snake.x;
                final int size = snake.size;
                final int y = snake.y;
                final int size2 = snake.size;
                if (this.mDetectMoves) {
                    while (i > x + size) {
                        this.findAddition(i, j, k);
                        --i;
                    }
                    while (j > y + size2) {
                        this.findRemoval(i, j, k);
                        --j;
                    }
                }
                for (int l = 0; l < snake.size; ++l) {
                    final int n = snake.x + l;
                    final int n2 = snake.y + l;
                    int n3;
                    if (!this.mCallback.areContentsTheSame(n, n2)) {
                        n3 = 2;
                    }
                    else {
                        n3 = 1;
                    }
                    this.mOldItemStatuses[n] = (n2 << 5 | n3);
                    this.mNewItemStatuses[n2] = (n3 | n << 5);
                }
                i = snake.x;
                j = snake.y;
            }
        }
        
        private void findRemoval(final int n, final int n2, final int n3) {
            if (this.mNewItemStatuses[n2 - 1] == 0) {
                this.findMatchingItem(n, n2, n3, true);
            }
        }
        
        private static PostponedUpdate removePostponedUpdate(final List<PostponedUpdate> list, int i, final boolean b) {
            for (int j = list.size() - 1; j >= 0; --j) {
                final PostponedUpdate postponedUpdate = list.get(j);
                if (postponedUpdate.posInOwnerList == i && postponedUpdate.removal == b) {
                    list.remove(j);
                    PostponedUpdate postponedUpdate2;
                    int currentPos;
                    int n;
                    for (i = j; i < list.size(); ++i) {
                        postponedUpdate2 = list.get(i);
                        currentPos = postponedUpdate2.currentPos;
                        if (!b) {
                            n = -1;
                        }
                        else {
                            n = 1;
                        }
                        postponedUpdate2.currentPos = n + currentPos;
                    }
                    return postponedUpdate;
                }
            }
            return null;
        }
        
        public void dispatchUpdatesTo(final ListUpdateCallback listUpdateCallback) {
            BatchingListUpdateCallback batchingListUpdateCallback;
            if (!(listUpdateCallback instanceof BatchingListUpdateCallback)) {
                batchingListUpdateCallback = new BatchingListUpdateCallback(listUpdateCallback);
            }
            else {
                batchingListUpdateCallback = (BatchingListUpdateCallback)listUpdateCallback;
            }
            final ArrayList<PostponedUpdate> list = new ArrayList<PostponedUpdate>();
            int n = this.mOldListSize;
            int n2 = this.mNewListSize;
            for (int i = this.mSnakes.size() - 1; i >= 0; --i) {
                final Snake snake = this.mSnakes.get(i);
                final int size = snake.size;
                final int n3 = snake.x + size;
                final int n4 = snake.y + size;
                if (n3 < n) {
                    this.dispatchRemovals(list, batchingListUpdateCallback, n3, n - n3, n3);
                }
                if (n4 < n2) {
                    this.dispatchAdditions(list, batchingListUpdateCallback, n3, n2 - n4, n4);
                }
                for (int j = size - 1; j >= 0; --j) {
                    if ((this.mOldItemStatuses[snake.x + j] & 0x1F) == 0x2) {
                        batchingListUpdateCallback.onChanged(snake.x + j, 1, this.mCallback.getChangePayload(snake.x + j, snake.y + j));
                    }
                }
                n = snake.x;
                n2 = snake.y;
            }
            batchingListUpdateCallback.dispatchLastEvent();
        }
        
        public void dispatchUpdatesTo(final RecyclerView.Adapter adapter) {
            this.dispatchUpdatesTo(new ListUpdateCallback() {
                @Override
                public void onChanged(final int n, final int n2, final Object o) {
                    adapter.notifyItemRangeChanged(n, n2, o);
                }
                
                @Override
                public void onInserted(final int n, final int n2) {
                    adapter.notifyItemRangeInserted(n, n2);
                }
                
                @Override
                public void onMoved(final int n, final int n2) {
                    adapter.notifyItemMoved(n, n2);
                }
                
                @Override
                public void onRemoved(final int n, final int n2) {
                    adapter.notifyItemRangeRemoved(n, n2);
                }
            });
        }
        
        @VisibleForTesting
        List<Snake> getSnakes() {
            return this.mSnakes;
        }
    }
    
    private static class PostponedUpdate
    {
        int currentPos;
        int posInOwnerList;
        boolean removal;
        
        public PostponedUpdate(final int posInOwnerList, final int currentPos, final boolean removal) {
            this.posInOwnerList = posInOwnerList;
            this.currentPos = currentPos;
            this.removal = removal;
        }
    }
    
    static class Range
    {
        int newListEnd;
        int newListStart;
        int oldListEnd;
        int oldListStart;
        
        public Range() {
        }
        
        public Range(final int oldListStart, final int oldListEnd, final int newListStart, final int newListEnd) {
            this.oldListStart = oldListStart;
            this.oldListEnd = oldListEnd;
            this.newListStart = newListStart;
            this.newListEnd = newListEnd;
        }
    }
    
    static class Snake
    {
        boolean removal;
        boolean reverse;
        int size;
        int x;
        int y;
    }
}
