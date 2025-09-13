package com.lumiyaviewer.lumiya.ui.common;

import android.support.v7.widget.RecyclerView;
import android.view.ViewGroup;
import com.google.common.base.Optional;
import com.lumiyaviewer.lumiya.react.UIThreadExecutor;
import com.lumiyaviewer.lumiya.slproto.users.manager.SubscribableList;
import java.util.AbstractList;
import java.util.ArrayList;
import java.util.List;
import javax.annotation.Nullable;

public abstract class RecyclerSubscribableListAdapter<T> extends RecyclerView.Adapter {
    private final RecyclerSubscribableListAdapter<T>.LocalItemList<T> localItemList;

    private class LocalItemList<T> extends AbstractList<T> {
        private final List<T> backingList = new ArrayList();

        /* JADX WARNING: type inference failed for: r5v0, types: [com.google.common.base.Optional<java.util.concurrent.Executor>, com.google.common.base.Optional] */
        /* JADX WARNING: Unknown variable types count: 1 */
        /* Code decompiled incorrectly, please refer to instructions dump. */
        public LocalItemList(com.lumiyaviewer.lumiya.slproto.users.manager.SubscribableList<T> r4, com.google.common.base.Optional<java.util.concurrent.Executor> r5) {
            /*
                r2 = this;
                com.lumiyaviewer.lumiya.ui.common.RecyclerSubscribableListAdapter.this = r3
                r2.<init>()
                java.util.ArrayList r0 = new java.util.ArrayList
                r0.<init>()
                r2.backingList = r0
                java.util.List<T> r0 = r2.backingList
                java.util.List r1 = r4.addSubscription(r2, r5)
                r0.addAll(r1)
                return
            */
            throw new UnsupportedOperationException("Method not decompiled: com.lumiyaviewer.lumiya.ui.common.RecyclerSubscribableListAdapter.LocalItemList.<init>(com.lumiyaviewer.lumiya.ui.common.RecyclerSubscribableListAdapter, com.lumiyaviewer.lumiya.slproto.users.manager.SubscribableList, com.google.common.base.Optional):void");
        }

        public void add(int i, T t) {
            this.backingList.add(i, t);
            RecyclerSubscribableListAdapter.this.notifyItemInserted(i);
        }

        public void clear() {
            this.backingList.clear();
            RecyclerSubscribableListAdapter.this.notifyDataSetChanged();
        }

        public T get(int i) {
            return this.backingList.get(i);
        }

        public T remove(int i) {
            T remove = this.backingList.remove(i);
            RecyclerSubscribableListAdapter.this.notifyItemRemoved(i);
            return remove;
        }

        public T set(int i, T t) {
            T t2 = this.backingList.set(i, t);
            RecyclerSubscribableListAdapter.this.notifyItemChanged(i);
            return t2;
        }

        public int size() {
            return this.backingList.size();
        }
    }

    public RecyclerSubscribableListAdapter(SubscribableList<T> subscribableList) {
        this.localItemList = new LocalItemList<>(subscribableList, Optional.of(UIThreadExecutor.getInstance()));
    }

    /* access modifiers changed from: protected */
    public abstract void bindObjectViewHolder(RecyclerView.ViewHolder viewHolder, T t);

    /* access modifiers changed from: protected */
    public abstract RecyclerView.ViewHolder createObjectViewHolder(ViewGroup viewGroup, int i);

    public int getItemCount() {
        return this.localItemList.size();
    }

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

    /* access modifiers changed from: protected */
    public abstract int getObjectViewType(T t);

    public void onBindViewHolder(RecyclerView.ViewHolder viewHolder, int i) {
        bindObjectViewHolder(viewHolder, this.localItemList.get(i));
    }

    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
        return createObjectViewHolder(viewGroup, i);
    }
}
