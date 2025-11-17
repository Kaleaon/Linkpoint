package com.linkpoint.ui.common

import androidx.recyclerview.widget.RecyclerView
import android.view.ViewGroup
import com.google.common.base.Optional
import com.linkpoint.react.UIThreadExecutor
import com.linkpoint.slproto.users.manager.SubscribableList
import java.util.AbstractList
import java.util.ArrayList
import java.util.List
import androidx.annotation.Nullable

abstract class RecyclerSubscribableListAdapter<T> : RecyclerView.Adapter {
    private RecyclerSubscribableListAdapter<T>.LocalItemList<T> localItemList

    private class LocalItemList<T> : AbstractList<T> {
        private List<T> backingList = ArrayList()

        /* JADX WARNING: type inference failed for: r5v0, types: [com.google.common.base.Optional<java.util.concurrent.Executor>, com.google.common.base.Optional] */
        /* JADX WARNING: Unknown variable types count: 1 */
        /* Code decompiled incorrectly, please refer to instructions dump. */
        LocalItemList(com.linkpoint.slproto.users.manager.SubscribableList<T> r4, com.google.common.base.Optional<java.util.concurrent.Executor> r5) {
            /*
                r2 = this
                com.linkpoint.ui.common.RecyclerSubscribableListAdapter.this = r3
                r2.<init>()
                java.util.ArrayList r0 = java.util.ArrayList
                r0.<init>()
                r2.backingList = r0
                java.util.List<T> r0 = r2.backingList
                java.util.List r1 = r4.addSubscription(r2, r5)
                r0.addAll(r1)
                return
            */
            throw UnsupportedOperationException("Method not decompiled: com.linkpoint.ui.common.RecyclerSubscribableListAdapter.LocalItemList.<init>(com.linkpoint.ui.common.RecyclerSubscribableListAdapter, com.linkpoint.slproto.users.manager.SubscribableList, com.google.common.base.Optional):Unit")
        }

        Unit add(Int i, T t) {
            this.backingList.add(i, t)
            RecyclerSubscribableListAdapter.this.notifyItemInserted(i)
        }

        Unit clear() {
            this.backingList.clear()
            RecyclerSubscribableListAdapter.this.notifyDataSetChanged()
        }

        T get(Int i) {
            return this.backingList.get(i)
        }

        T remove(Int i) {
            T remove = this.backingList.remove(i)
            RecyclerSubscribableListAdapter.this.notifyItemRemoved(i)
            return remove
        }

        T set(Int i, T t) {
            T t2 = this.backingList.set(i, t)
            RecyclerSubscribableListAdapter.this.notifyItemChanged(i)
            return t2
        }

        Int size() {
            return this.backingList.size()
        }
    }

    RecyclerSubscribableListAdapter(SubscribableList<T> subscribableList) {
        this.localItemList = LocalItemList<>(subscribableList, Optional.of(UIThreadExecutor.getInstance()))
    }

    /* access modifiers changed from: protected */
    abstract Unit bindObjectViewHolder(RecyclerView.ViewHolder viewHolder, T t)

    /* access modifiers changed from: protected */
    abstract RecyclerView.ViewHolder createObjectViewHolder(ViewGroup viewGroup, Int i)

    Int getItemCount() {
        return this.localItemList.size()
    }

    Int getItemViewType(Int i) {
        return getObjectViewType(this.localItemList.get(i))
    }

    @Nullable
    T getObject(Int i) {
        if (i < 0 || i >= this.localItemList.size()) {
            return null
        }
        return this.localItemList.get(i)
    }

    /* access modifiers changed from: protected */
    abstract Int getObjectViewType(T t)

    Unit onBindViewHolder(RecyclerView.ViewHolder viewHolder, Int i) {
        bindObjectViewHolder(viewHolder, this.localItemList.get(i))
    }

    RecyclerView.ViewHolder onCreateViewHolder(ViewGroup viewGroup, Int i) {
        return createObjectViewHolder(viewGroup, i)
    }
}
