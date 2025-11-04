package com.lumiyaviewer.lumiya.ui.common;

import android.support.v7.widget.RecyclerView;
import android.view.ViewGroup;
import com.google.common.base.Optional;
import com.lumiyaviewer.lumiya.react.UIThreadExecutor;
import com.lumiyaviewer.lumiya.slproto.users.manager.SubscribableList;
import java.util.AbstractList;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Executor;
import javax.annotation.Nullable;

/* loaded from: classes.dex */
public abstract class RecyclerSubscribableListAdapter<T> extends RecyclerView.Adapter {
    private final RecyclerSubscribableListAdapter<T>.LocalItemList<T> localItemList;

    /* loaded from: classes.dex */
    private class LocalItemList<T> extends AbstractList<T> {
        private final List<T> backingList = new ArrayList();

        public LocalItemList(SubscribableList<T> subscribableList, Optional<Executor> optional) {
            this.backingList.addAll(subscribableList.addSubscription(this, optional));
        }

        @Override // java.util.AbstractList, java.util.List
        public void add(int i, T t) {
            this.backingList.add(i, t);
            RecyclerSubscribableListAdapter.this.notifyItemInserted(i);
        }

        @Override // java.util.AbstractList, java.util.AbstractCollection, java.util.Collection, java.util.List
        public void clear() {
            this.backingList.clear();
            RecyclerSubscribableListAdapter.this.notifyDataSetChanged();
        }

        @Override // java.util.AbstractList, java.util.List
        public T get(int i) {
            return this.backingList.get(i);
        }

        @Override // java.util.AbstractList, java.util.List
        public T remove(int i) {
            T remove = this.backingList.remove(i);
            RecyclerSubscribableListAdapter.this.notifyItemRemoved(i);
            return remove;
        }

        @Override // java.util.AbstractList, java.util.List
        public T set(int i, T t) {
            T t2 = this.backingList.set(i, t);
            RecyclerSubscribableListAdapter.this.notifyItemChanged(i);
            return t2;
        }

        @Override // java.util.AbstractCollection, java.util.Collection, java.util.List
        public int size() {
            return this.backingList.size();
        }
    }

    public RecyclerSubscribableListAdapter(SubscribableList<T> subscribableList) {
        this.localItemList = new LocalItemList<>(subscribableList, Optional.of(UIThreadExecutor.getInstance()));
    }

    protected abstract void bindObjectViewHolder(RecyclerView.ViewHolder viewHolder, T t);

    protected abstract RecyclerView.ViewHolder createObjectViewHolder(ViewGroup viewGroup, int i);

    @Override // android.support.v7.widget.RecyclerView.Adapter
    public int getItemCount() {
        return this.localItemList.size();
    }

    @Override // android.support.v7.widget.RecyclerView.Adapter
    public int getItemViewType(int i) {
        return getObjectViewType(this.localItemList.get(i));
    }

    @Nullable
    public T getObject(int i) {
        if (i < 0 || i >= this.localItemList.size()) {
            return null;
        }
        return this.localItemList.get(i);
    }

    protected abstract int getObjectViewType(T t);

    @Override // android.support.v7.widget.RecyclerView.Adapter
    public void onBindViewHolder(RecyclerView.ViewHolder viewHolder, int i) {
        bindObjectViewHolder(viewHolder, this.localItemList.get(i));
    }

    @Override // android.support.v7.widget.RecyclerView.Adapter
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
        return createObjectViewHolder(viewGroup, i);
    }
}
