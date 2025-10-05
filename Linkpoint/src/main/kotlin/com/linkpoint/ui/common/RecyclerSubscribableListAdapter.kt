package com.linkpoint.ui.common

import android.support.v7.widget.RecyclerView
import android.view.ViewGroup
import com.google.common.base.Optional
import com.linkpoint.react.UIThreadExecutor
import com.linkpoint.slproto.users.manager.SubscribableList
import java.util.AbstractList
import java.util.ArrayList
import java.util.List
import javax.annotation.Nullable

abstract class RecyclerSubscribableListAdapter<T> : RecyclerView.Adapter {
    private val RecyclerSubscribableListAdapter<T>.LocalItemList<T> localItemList

    private class LocalItemList<T> : AbstractList<T> {
        private val List<T> backingList = ArrayList()

        /* JADX WARNING: type inference failed for: r5v0, types: [com.google.common.base.Optional<java.util.concurrent.Executor>, com.google.common.base.Optional] */
        /* JADX WARNING: Unknown variable types count: 1 */
        /* Code decompiled incorrectly, please refer to instructions dump. */
        public LocalItemList(com.lumiyaviewer.lumiya.slproto.users.manager.SubscribableList<T> r4, com.google.common.base.Optional<java.util.concurrent.Executor> r5) {
            /*
                r2 = this
                com.lumiyaviewer.lumiya.ui.common.RecyclerSubscribableListAdapter.this = r3
                r2.<init>()
                java.util.ArrayList r0 = java.util.ArrayList
                r0.<init>()
                r2.backingList = r0
                java.util.List<T> r0 = r2.backingList
                java.util.List r1 = r4.addSubscription(r2, r5)
                r0.addAll(r1)
                return
            */
            throw UnsupportedOperationException("Method not decompiled: com.lumiyaviewer.lumiya.ui.common.RecyclerSubscribableListAdapter.LocalItemList.<init>(com.lumiyaviewer.lumiya.ui.common.RecyclerSubscribableListAdapter, com.lumiyaviewer.lumiya.slproto.users.manager.SubscribableList, com.google.common.base.Optional):Unit")
        }

        fun add(Int i, T t) {
            this.backingList.add(i, t)
            RecyclerSubscribableListAdapter.this.notifyItemInserted(i)
        }

        fun clear() {
            this.backingList.clear()
            RecyclerSubscribableListAdapter.this.notifyDataSetChanged()
        }

        public T get(Int i) {
            return this.backingList.get(i)
        }

        public T remove(Int i) {
            T remove = this.backingList.remove(i)
            RecyclerSubscribableListAdapter.this.notifyItemRemoved(i)
            return remove
        }

        public T set(Int i, T t) {
            T t2 = this.backingList.set(i, t)
            RecyclerSubscribableListAdapter.this.notifyItemChanged(i)
            return t2
        }

        public Int size() {
            return this.backingList.size()
        }
    }

    public RecyclerSubscribableListAdapter(SubscribableList<T> subscribableList) {
        this.localItemList = LocalItemList<>(subscribableList, Optional.of(UIThreadExecutor.getInstance()))
    }

    /* access modifiers changed from: protected */
    public abstract Unit bindObjectViewHolder(RecyclerView.ViewHolder viewHolder, T t)

    /* access modifiers changed from: protected */
    public abstract RecyclerView.ViewHolder createObjectViewHolder(ViewGroup viewGroup, Int i)

    public Int getItemCount() {
        return this.localItemList.size()
    }

    public Int getItemViewType(Int i) {
        return getObjectViewType(this.localItemList.get(i))
    }

    public T getObject(Int i) {
        if (i < 0 || i >= this.localItemList.size()) {
            return null
        }
        return this.localItemList.get(i)
    }

    /* access modifiers changed from: protected */
    public abstract Int getObjectViewType(T t)

    fun onBindViewHolder(RecyclerView.ViewHolder viewHolder, Int i) {
        bindObjectViewHolder(viewHolder, this.localItemList.get(i))
    }

    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup viewGroup, Int i) {
        return createObjectViewHolder(viewGroup, i)
    }
}
