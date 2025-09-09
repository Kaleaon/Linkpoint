package android.support.v7.util;

import android.util.SparseArray;
import java.lang.reflect.Array;

class TileList<T> {
    Tile<T> mLastAccessedTile;
    final int mTileSize;
    private final SparseArray<Tile<T>> mTiles = new SparseArray<>(10);

    public static class Tile<T> {
        public int mItemCount;
        public final T[] mItems;
        Tile<T> mNext;
        public int mStartPosition;

        public Tile(Class<T> cls, int i) {
            this.mItems = (Object[]) Array.newInstance(cls, i);
        }

        /* access modifiers changed from: package-private */
        public boolean containsPosition(int i) {
            return this.mStartPosition <= i && i < this.mStartPosition + this.mItemCount;
        }

        /* access modifiers changed from: package-private */
        public T getByPosition(int i) {
            return this.mItems[i - this.mStartPosition];
        }
    }

    public TileList(int i) {
        this.mTileSize = i;
    }

    public Tile<T> addOrReplace(Tile<T> tile) {
        int indexOfKey = this.mTiles.indexOfKey(tile.mStartPosition);
        if (indexOfKey >= 0) {
            Tile<T> valueAt = this.mTiles.valueAt(indexOfKey);
            this.mTiles.setValueAt(indexOfKey, tile);
            if (this.mLastAccessedTile == valueAt) {
                this.mLastAccessedTile = tile;
            }
            return valueAt;
        }
        this.mTiles.put(tile.mStartPosition, tile);
        return null;
    }

    public void clear() {
        this.mTiles.clear();
    }

    public Tile<T> getAtIndex(int i) {
        return this.mTiles.valueAt(i);
    }

    public T getItemAt(int i) {
        if (this.mLastAccessedTile == null || !this.mLastAccessedTile.containsPosition(i)) {
            int indexOfKey = this.mTiles.indexOfKey(i - (i % this.mTileSize));
            if (indexOfKey < 0) {
                return null;
            }
            this.mLastAccessedTile = this.mTiles.valueAt(indexOfKey);
        }
        return this.mLastAccessedTile.getByPosition(i);
    }

    public Tile<T> removeAtPos(int i) {
        Tile<T> tile = this.mTiles.get(i);
        if (this.mLastAccessedTile == tile) {
            this.mLastAccessedTile = null;
        }
        this.mTiles.delete(i);
        return tile;
    }

    public int size() {
        return this.mTiles.size();
    }
}
