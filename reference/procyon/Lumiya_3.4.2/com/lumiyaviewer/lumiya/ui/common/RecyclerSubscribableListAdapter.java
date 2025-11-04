// 
// Decompiled by Procyon v0.6.0
// 

package com.lumiyaviewer.lumiya.ui.common;

import java.util.Collection;
import java.util.ArrayList;
import java.util.concurrent.Executor;
import java.util.List;
import java.util.AbstractList;
import javax.annotation.Nullable;
import android.view.ViewGroup;
import com.google.common.base.Optional;
import com.lumiyaviewer.lumiya.react.UIThreadExecutor;
import com.lumiyaviewer.lumiya.slproto.users.manager.SubscribableList;
import android.support.v7.widget.RecyclerView;

public abstract class RecyclerSubscribableListAdapter<T> extends Adapter
{
    private final LocalItemList<T> localItemList;
    
    public RecyclerSubscribableListAdapter(final SubscribableList<T> list) {
        this.localItemList = new LocalItemList<T>(list, Optional.of(UIThreadExecutor.getInstance()));
    }
    
    protected abstract void bindObjectViewHolder(final ViewHolder p0, final T p1);
    
    protected abstract ViewHolder createObjectViewHolder(final ViewGroup p0, final int p1);
    
    @Override
    public int getItemCount() {
        return this.localItemList.size();
    }
    
    @Override
    public int getItemViewType(final int n) {
        return this.getObjectViewType(this.localItemList.get(n));
    }
    
    @Nullable
    public T getObject(final int n) {
        if (n >= 0 && n < this.localItemList.size()) {
            return this.localItemList.get(n);
        }
        return null;
    }
    
    protected abstract int getObjectViewType(final T p0);
    
    @Override
    public void onBindViewHolder(final ViewHolder viewHolder, final int n) {
        this.bindObjectViewHolder(viewHolder, this.localItemList.get(n));
    }
    
    @Override
    public ViewHolder onCreateViewHolder(final ViewGroup viewGroup, final int n) {
        return this.createObjectViewHolder(viewGroup, n);
    }
    
    private class LocalItemList<T> extends AbstractList<T>
    {
        private final List<T> backingList;
        
        public LocalItemList(final SubscribableList<T> list, final Optional<Executor> optional) {
            (this.backingList = new ArrayList<T>()).addAll((Collection<? extends T>)list.addSubscription(this, optional));
        }
        
        @Override
        public void add(final int n, final T t) {
            this.backingList.add(n, t);
            RecyclerSubscribableListAdapter.this.notifyItemInserted(n);
        }
        
        @Override
        public void clear() {
            this.backingList.clear();
            RecyclerSubscribableListAdapter.this.notifyDataSetChanged();
        }
        
        @Override
        public T get(final int n) {
            return this.backingList.get(n);
        }
        
        @Override
        public T remove(final int n) {
            final T remove = this.backingList.remove(n);
            RecyclerSubscribableListAdapter.this.notifyItemRemoved(n);
            return remove;
        }
        
        @Override
        public T set(final int n, final T t) {
            final T set = this.backingList.set(n, t);
            RecyclerSubscribableListAdapter.this.notifyItemChanged(n);
            return set;
        }
        
        @Override
        public int size() {
            return this.backingList.size();
        }
    }
}
