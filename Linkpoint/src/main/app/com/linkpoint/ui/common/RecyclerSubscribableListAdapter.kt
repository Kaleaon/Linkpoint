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

        fun add(Int i, T t): Unit {
            this.backingList.add(i, t)
            RecyclerSubscribableListAdapter.this.notifyItemInserted(i)
        }

        fun clear(): Unit {
            this.backingList.clear()
            RecyclerSubscribableListAdapter.this.notifyDataSetChanged()
        }

        fun get(Int i): T {
            return this.backingList.get(i)
        }

        fun remove(Int i): T {
            T remove = this.backingList.remove(i)
            RecyclerSubscribableListAdapter.this.notifyItemRemoved(i)
            return remove
        }

        fun set(Int i, T t): T {
            T t2 = this.backingList.set(i, t)
            RecyclerSubscribableListAdapter.this.notifyItemChanged(i)
            return t2
        }

        fun size(): Int {
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

    fun getItemCount(): Int {
        return this.localItemList.size()
    }

    fun getItemViewType(Int i): Int {
        return getObjectViewType(this.localItemList.get(i))
    }

    @Nullable
    fun getObject(Int i): T {
        if (i < 0 || i >= this.localItemList.size()) {
            return null
        }
        return this.localItemList.get(i)
    }

    /* access modifiers changed from: protected */
    abstract Int getObjectViewType(T t)

    fun onBindViewHolder(RecyclerView.ViewHolder viewHolder, Int i): Unit {
        bindObjectViewHolder(viewHolder, this.localItemList.get(i))
    }

    RecyclerView.ViewHolder onCreateViewHolder(ViewGroup viewGroup, Int i) {
        return createObjectViewHolder(viewGroup, i)
    }
}
