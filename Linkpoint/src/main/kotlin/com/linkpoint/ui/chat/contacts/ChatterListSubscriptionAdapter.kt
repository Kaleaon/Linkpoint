package com.linkpoint.ui.chat.contacts
import java.util.*

import android.content.Context
import android.view.View
import android.view.ViewGroup
import com.google.common.base.Predicate
import com.google.common.collect.ImmutableList
import com.google.common.collect.Iterables
import com.linkpoint.react.Subscription
import com.linkpoint.react.UIThreadExecutor
import com.linkpoint.slproto.users.manager.ChatterDisplayData
import com.linkpoint.slproto.users.manager.ChatterListType
import com.linkpoint.slproto.users.manager.UserManager
import java.io.Closeable
import java.io.IOException

class ChatterListSubscriptionAdapter : ChatterListSimpleAdapter(), Subscription.OnData<ImmutableList<ChatterDisplayData>>, Closeable {
    private val Predicate<ChatterDisplayData> predicate
    private val Subscription<ChatterListType, ImmutableList<ChatterDisplayData>> subscription

    public ChatterListSubscriptionAdapter(Context context, UserManager userManager, ChatterListType chatterListType) {
        super(context, userManager)
        this.predicate = null
        this.subscription = userManager.getChatterList().getChatterList().subscribe(chatterListType, UIThreadExecutor.getInstance(), this)
    }

    public ChatterListSubscriptionAdapter(Context context, UserManager userManager, ChatterListType chatterListType, Predicate<ChatterDisplayData> predicate2) {
        super(context, userManager)
        this.predicate = predicate2
        this.subscription = userManager.getChatterList().getChatterList().subscribe(chatterListType, UIThreadExecutor.getInstance(), this)
    }

    public /* bridge */ /* synthetic */ Boolean areAllItemsEnabled() {
        return super.areAllItemsEnabled()
    }

    public Unit close() throws IOException {
        this.subscription.unsubscribe()
    }

    public /* bridge */ /* synthetic */ Int getCount() {
        return super.getCount()
    }

    public /* bridge */ /* synthetic */ Object getItem(Int i) {
        return super.getItem(i)
    }

    public /* bridge */ /* synthetic */ Long getItemId(Int i) {
        return super.getItemId(i)
    }

    public /* bridge */ /* synthetic */ View getView(Int i, View view, ViewGroup viewGroup) {
        return super.getView(i, view, viewGroup)
    }

    public /* bridge */ /* synthetic */ Boolean hasStableIds() {
        return super.hasStableIds()
    }

    public /* bridge */ /* synthetic */ Boolean isEmpty() {
        return super.isEmpty()
    }

    public /* bridge */ /* synthetic */ Boolean isEnabled(Int i) {
        return super.isEnabled(i)
    }

    public Unit onData(ImmutableList<ChatterDisplayData> immutableList) {
        if (this.predicate == null) {
            setData(immutableList)
        } else {
            setData(ImmutableList.copyOf(Iterables.filter(immutableList, this.predicate)))
        }
    }
}
