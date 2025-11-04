// 
// Decompiled by Procyon v0.6.0
// 

package android.support.v7.widget.util;

import android.support.v7.widget.RecyclerView;
import android.support.v7.util.SortedList;

public abstract class SortedListAdapterCallback<T2> extends Callback<T2>
{
    final RecyclerView.Adapter mAdapter;
    
    public SortedListAdapterCallback(final RecyclerView.Adapter mAdapter) {
        this.mAdapter = mAdapter;
    }
    
    @Override
    public void onChanged(final int n, final int n2) {
        this.mAdapter.notifyItemRangeChanged(n, n2);
    }
    
    @Override
    public void onInserted(final int n, final int n2) {
        this.mAdapter.notifyItemRangeInserted(n, n2);
    }
    
    @Override
    public void onMoved(final int n, final int n2) {
        this.mAdapter.notifyItemMoved(n, n2);
    }
    
    @Override
    public void onRemoved(final int n, final int n2) {
        this.mAdapter.notifyItemRangeRemoved(n, n2);
    }
}
