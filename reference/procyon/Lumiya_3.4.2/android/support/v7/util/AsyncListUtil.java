// 
// Decompiled by Procyon v0.6.0
// 

package android.support.v7.util;

import android.support.annotation.UiThread;
import android.support.annotation.WorkerThread;
import android.util.SparseBooleanArray;
import android.util.Log;
import android.util.SparseIntArray;

public class AsyncListUtil<T>
{
    static final boolean DEBUG = false;
    static final String TAG = "AsyncListUtil";
    boolean mAllowScrollHints;
    private final ThreadUtil.BackgroundCallback<T> mBackgroundCallback;
    final ThreadUtil.BackgroundCallback<T> mBackgroundProxy;
    final DataCallback<T> mDataCallback;
    int mDisplayedGeneration;
    int mItemCount;
    private final ThreadUtil.MainThreadCallback<T> mMainThreadCallback;
    final ThreadUtil.MainThreadCallback<T> mMainThreadProxy;
    final SparseIntArray mMissingPositions;
    final int[] mPrevRange;
    int mRequestedGeneration;
    private int mScrollHint;
    final Class<T> mTClass;
    final TileList<T> mTileList;
    final int mTileSize;
    final int[] mTmpRange;
    final int[] mTmpRangeExtended;
    final ViewCallback mViewCallback;
    
    public AsyncListUtil(final Class<T> mtClass, final int mTileSize, final DataCallback<T> mDataCallback, final ViewCallback mViewCallback) {
        this.mTmpRange = new int[2];
        this.mPrevRange = new int[2];
        this.mTmpRangeExtended = new int[2];
        this.mScrollHint = 0;
        this.mItemCount = 0;
        this.mDisplayedGeneration = 0;
        this.mRequestedGeneration = this.mDisplayedGeneration;
        this.mMissingPositions = new SparseIntArray();
        this.mMainThreadCallback = new ThreadUtil.MainThreadCallback<T>() {
            private boolean isRequestedGeneration(final int n) {
                return n == AsyncListUtil.this.mRequestedGeneration;
            }
            
            private void recycleAllTiles() {
                for (int i = 0; i < AsyncListUtil.this.mTileList.size(); ++i) {
                    AsyncListUtil.this.mBackgroundProxy.recycleTile(AsyncListUtil.this.mTileList.getAtIndex(i));
                }
                AsyncListUtil.this.mTileList.clear();
            }
            
            @Override
            public void addTile(int i, final TileList.Tile<T> tile) {
                final int n = 0;
                if (this.isRequestedGeneration(i)) {
                    final TileList.Tile<T> addOrReplace = AsyncListUtil.this.mTileList.addOrReplace(tile);
                    if (addOrReplace != null) {
                        Log.e("AsyncListUtil", "duplicate tile @" + addOrReplace.mStartPosition);
                        AsyncListUtil.this.mBackgroundProxy.recycleTile(addOrReplace);
                    }
                    final int mStartPosition = tile.mStartPosition;
                    final int mItemCount = tile.mItemCount;
                    i = n;
                    while (i < AsyncListUtil.this.mMissingPositions.size()) {
                        final int key = AsyncListUtil.this.mMissingPositions.keyAt(i);
                        if (tile.mStartPosition <= key && key < mStartPosition + mItemCount) {
                            AsyncListUtil.this.mMissingPositions.removeAt(i);
                            AsyncListUtil.this.mViewCallback.onItemLoaded(key);
                        }
                        else {
                            ++i;
                        }
                    }
                    return;
                }
                AsyncListUtil.this.mBackgroundProxy.recycleTile(tile);
            }
            
            @Override
            public void removeTile(final int n, final int i) {
                if (!this.isRequestedGeneration(n)) {
                    return;
                }
                final TileList.Tile<T> removeAtPos = AsyncListUtil.this.mTileList.removeAtPos(i);
                if (removeAtPos != null) {
                    AsyncListUtil.this.mBackgroundProxy.recycleTile(removeAtPos);
                    return;
                }
                Log.e("AsyncListUtil", "tile not found @" + i);
            }
            
            @Override
            public void updateItemCount(final int n, final int mItemCount) {
                if (this.isRequestedGeneration(n)) {
                    AsyncListUtil.this.mItemCount = mItemCount;
                    AsyncListUtil.this.mViewCallback.onDataRefresh();
                    AsyncListUtil.this.mDisplayedGeneration = AsyncListUtil.this.mRequestedGeneration;
                    this.recycleAllTiles();
                    AsyncListUtil.this.mAllowScrollHints = false;
                    AsyncListUtil.this.updateRange();
                }
            }
        };
        this.mBackgroundCallback = new ThreadUtil.BackgroundCallback<T>() {
            private int mFirstRequiredTileStart;
            private int mGeneration;
            private int mItemCount;
            private int mLastRequiredTileStart;
            final SparseBooleanArray mLoadedTiles = new SparseBooleanArray();
            private TileList.Tile<T> mRecycledRoot;
            
            private TileList.Tile<T> acquireTile() {
                if (this.mRecycledRoot == null) {
                    return (TileList.Tile<T>)new TileList.Tile((Class<Object>)AsyncListUtil.this.mTClass, AsyncListUtil.this.mTileSize);
                }
                final TileList.Tile<T> mRecycledRoot = this.mRecycledRoot;
                this.mRecycledRoot = this.mRecycledRoot.mNext;
                return mRecycledRoot;
            }
            
            private void addTile(final TileList.Tile<T> tile) {
                this.mLoadedTiles.put(tile.mStartPosition, true);
                AsyncListUtil.this.mMainThreadProxy.addTile(this.mGeneration, tile);
            }
            
            private void flushTileCache(final int n) {
                while (this.mLoadedTiles.size() >= AsyncListUtil.this.mDataCallback.getMaxCachedTiles()) {
                    final int key = this.mLoadedTiles.keyAt(0);
                    final int key2 = this.mLoadedTiles.keyAt(this.mLoadedTiles.size() - 1);
                    final int n2 = this.mFirstRequiredTileStart - key;
                    final int n3 = key2 - this.mLastRequiredTileStart;
                    if (n2 > 0 && (n2 >= n3 || n == 2)) {
                        this.removeTile(key);
                    }
                    else {
                        if (n3 <= 0 || (n2 >= n3 && n != 1)) {
                            return;
                        }
                        this.removeTile(key2);
                    }
                }
            }
            
            private int getTileStart(final int n) {
                return n - n % AsyncListUtil.this.mTileSize;
            }
            
            private boolean isTileLoaded(final int n) {
                return this.mLoadedTiles.get(n);
            }
            
            private void log(final String format, final Object... args) {
                Log.d("AsyncListUtil", "[BKGR] " + String.format(format, args));
            }
            
            private void removeTile(final int n) {
                this.mLoadedTiles.delete(n);
                AsyncListUtil.this.mMainThreadProxy.removeTile(this.mGeneration, n);
            }
            
            private void requestTiles(final int n, final int n2, final int n3, final boolean b) {
                for (int i = n; i <= n2; i += AsyncListUtil.this.mTileSize) {
                    int n4;
                    if (!b) {
                        n4 = i;
                    }
                    else {
                        n4 = n2 + n - i;
                    }
                    AsyncListUtil.this.mBackgroundProxy.loadTile(n4, n3);
                }
            }
            
            @Override
            public void loadTile(final int mStartPosition, final int n) {
                if (!this.isTileLoaded(mStartPosition)) {
                    final TileList.Tile<T> acquireTile = this.acquireTile();
                    acquireTile.mStartPosition = mStartPosition;
                    acquireTile.mItemCount = Math.min(AsyncListUtil.this.mTileSize, this.mItemCount - acquireTile.mStartPosition);
                    AsyncListUtil.this.mDataCallback.fillData(acquireTile.mItems, acquireTile.mStartPosition, acquireTile.mItemCount);
                    this.flushTileCache(n);
                    this.addTile(acquireTile);
                }
            }
            
            @Override
            public void recycleTile(final TileList.Tile<T> mRecycledRoot) {
                AsyncListUtil.this.mDataCallback.recycleData(mRecycledRoot.mItems, mRecycledRoot.mItemCount);
                mRecycledRoot.mNext = this.mRecycledRoot;
                this.mRecycledRoot = mRecycledRoot;
            }
            
            @Override
            public void refresh(final int mGeneration) {
                this.mGeneration = mGeneration;
                this.mLoadedTiles.clear();
                this.mItemCount = AsyncListUtil.this.mDataCallback.refreshData();
                AsyncListUtil.this.mMainThreadProxy.updateItemCount(this.mGeneration, this.mItemCount);
            }
            
            @Override
            public void updateRange(int tileStart, int tileStart2, final int n, final int n2, final int n3) {
                if (tileStart <= tileStart2) {
                    tileStart = this.getTileStart(tileStart);
                    tileStart2 = this.getTileStart(tileStart2);
                    this.mFirstRequiredTileStart = this.getTileStart(n);
                    this.mLastRequiredTileStart = this.getTileStart(n2);
                    if (n3 != 1) {
                        this.requestTiles(tileStart, this.mLastRequiredTileStart, n3, false);
                        this.requestTiles(this.mFirstRequiredTileStart, tileStart - AsyncListUtil.this.mTileSize, n3, true);
                    }
                    else {
                        this.requestTiles(this.mFirstRequiredTileStart, tileStart2, n3, true);
                        this.requestTiles(AsyncListUtil.this.mTileSize + tileStart2, this.mLastRequiredTileStart, n3, false);
                    }
                }
            }
        };
        this.mTClass = mtClass;
        this.mTileSize = mTileSize;
        this.mDataCallback = mDataCallback;
        this.mViewCallback = mViewCallback;
        this.mTileList = new TileList<T>(this.mTileSize);
        final MessageThreadUtil messageThreadUtil = new MessageThreadUtil();
        this.mMainThreadProxy = (ThreadUtil.MainThreadCallback<T>)messageThreadUtil.getMainThreadProxy((ThreadUtil.MainThreadCallback)this.mMainThreadCallback);
        this.mBackgroundProxy = (ThreadUtil.BackgroundCallback<T>)messageThreadUtil.getBackgroundProxy((ThreadUtil.BackgroundCallback)this.mBackgroundCallback);
        this.refresh();
    }
    
    private boolean isRefreshPending() {
        return this.mRequestedGeneration != this.mDisplayedGeneration;
    }
    
    public T getItem(final int i) {
        if (i >= 0 && i < this.mItemCount) {
            final T item = this.mTileList.getItemAt(i);
            if (item == null && !this.isRefreshPending()) {
                this.mMissingPositions.put(i, 0);
            }
            return item;
        }
        throw new IndexOutOfBoundsException(i + " is not within 0 and " + this.mItemCount);
    }
    
    public int getItemCount() {
        return this.mItemCount;
    }
    
    void log(final String format, final Object... args) {
        Log.d("AsyncListUtil", "[MAIN] " + String.format(format, args));
    }
    
    public void onRangeChanged() {
        if (!this.isRefreshPending()) {
            this.updateRange();
            this.mAllowScrollHints = true;
        }
    }
    
    public void refresh() {
        this.mMissingPositions.clear();
        this.mBackgroundProxy.refresh(++this.mRequestedGeneration);
    }
    
    void updateRange() {
        this.mViewCallback.getItemRangeInto(this.mTmpRange);
        if (this.mTmpRange[0] > this.mTmpRange[1] || this.mTmpRange[0] < 0) {
            return;
        }
        if (this.mTmpRange[1] < this.mItemCount) {
            if (this.mAllowScrollHints) {
                if (this.mTmpRange[0] <= this.mPrevRange[1] && this.mPrevRange[0] <= this.mTmpRange[1]) {
                    if (this.mTmpRange[0] >= this.mPrevRange[0]) {
                        if (this.mTmpRange[0] > this.mPrevRange[0]) {
                            this.mScrollHint = 2;
                        }
                    }
                    else {
                        this.mScrollHint = 1;
                    }
                }
                else {
                    this.mScrollHint = 0;
                }
            }
            else {
                this.mScrollHint = 0;
            }
            this.mPrevRange[0] = this.mTmpRange[0];
            this.mPrevRange[1] = this.mTmpRange[1];
            this.mViewCallback.extendRangeInto(this.mTmpRange, this.mTmpRangeExtended, this.mScrollHint);
            this.mTmpRangeExtended[0] = Math.min(this.mTmpRange[0], Math.max(this.mTmpRangeExtended[0], 0));
            this.mTmpRangeExtended[1] = Math.max(this.mTmpRange[1], Math.min(this.mTmpRangeExtended[1], this.mItemCount - 1));
            this.mBackgroundProxy.updateRange(this.mTmpRange[0], this.mTmpRange[1], this.mTmpRangeExtended[0], this.mTmpRangeExtended[1], this.mScrollHint);
        }
    }
    
    public abstract static class DataCallback<T>
    {
        @WorkerThread
        public abstract void fillData(final T[] p0, final int p1, final int p2);
        
        @WorkerThread
        public int getMaxCachedTiles() {
            return 10;
        }
        
        @WorkerThread
        public void recycleData(final T[] array, final int n) {
        }
        
        @WorkerThread
        public abstract int refreshData();
    }
    
    public abstract static class ViewCallback
    {
        public static final int HINT_SCROLL_ASC = 2;
        public static final int HINT_SCROLL_DESC = 1;
        public static final int HINT_SCROLL_NONE = 0;
        
        @UiThread
        public void extendRangeInto(final int[] array, final int[] array2, final int n) {
            final int n2 = array[1] - array[0] + 1;
            int n3 = n2 / 2;
            final int n4 = array[0];
            int n5;
            if (n != 1) {
                n5 = n3;
            }
            else {
                n5 = n2;
            }
            array2[0] = n4 - n5;
            final int n6 = array[1];
            if (n == 2) {
                n3 = n2;
            }
            array2[1] = n6 + n3;
        }
        
        @UiThread
        public abstract void getItemRangeInto(final int[] p0);
        
        @UiThread
        public abstract void onDataRefresh();
        
        @UiThread
        public abstract void onItemLoaded(final int p0);
    }
}
