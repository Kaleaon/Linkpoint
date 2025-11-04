// 
// Decompiled by Procyon v0.6.0
// 

package android.support.v7.util;

interface ThreadUtil<T>
{
    BackgroundCallback<T> getBackgroundProxy(final BackgroundCallback<T> p0);
    
    MainThreadCallback<T> getMainThreadProxy(final MainThreadCallback<T> p0);
    
    public interface BackgroundCallback<T>
    {
        void loadTile(final int p0, final int p1);
        
        void recycleTile(final TileList.Tile<T> p0);
        
        void refresh(final int p0);
        
        void updateRange(final int p0, final int p1, final int p2, final int p3, final int p4);
    }
    
    public interface MainThreadCallback<T>
    {
        void addTile(final int p0, final TileList.Tile<T> p1);
        
        void removeTile(final int p0, final int p1);
        
        void updateItemCount(final int p0, final int p1);
    }
}
