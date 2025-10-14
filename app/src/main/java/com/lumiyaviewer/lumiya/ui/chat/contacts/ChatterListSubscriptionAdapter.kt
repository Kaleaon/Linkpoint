package com.lumiyaviewer.lumiya.ui.chat.contacts
import java.util.*

import android.content.Context
import android.view.View
import android.view.ViewGroup
import com.google.common.base.Predicate
import com.google.common.collect.ImmutableList
import com.google.common.collect.Iterables
import com.lumiyaviewer.lumiya.react.Subscription
import com.lumiyaviewer.lumiya.react.UIThreadExecutor
import com.lumiyaviewer.lumiya.slproto.users.manager.ChatterDisplayData
import com.lumiyaviewer.lumiya.slproto.users.manager.ChatterListType
import com.lumiyaviewer.lumiya.slproto.users.manager.UserManager
import java.io.Closeable
import java.io.IOException

class ChatterListSubscriptionAdapter : ChatterListSimpleAdapter : Subscription.OnData<ImmutableList<ChatterDisplayData>>, Closeable {
    private Predicate<ChatterDisplayData> predicate
    private Subscription<ChatterListType, ImmutableList<ChatterDisplayData>> subscription

    ChatterListSubscriptionAdapter(Context context, UserManager userManager, ChatterListType chatterListType) {
        super(context, userManager)
        this.predicate = null
        this.subscription = userManager.getChatterList().getChatterList().subscribe(chatterListType, UIThreadExecutor.getInstance(), this)
    }

    ChatterListSubscriptionAdapter(Context context, UserManager userManager, ChatterListType chatterListType, Predicate<ChatterDisplayData> predicate2) {
        super(context, userManager)
        this.predicate = predicate2
        this.subscription = userManager.getChatterList().getChatterList().subscribe(chatterListType, UIThreadExecutor.getInstance(), this)
    }

    /* bridge */ /* synthetic */ Boolean areAllItemsEnabled() {
        return super.areAllItemsEnabled()
    }

    Unit close() throws IOException {
        this.subscription.unsubscribe()
    }

    /* bridge */ /* synthetic */ Int getCount() {
        return super.getCount()
    }

    /* bridge */ /* synthetic */ Any getItem(Int i) {
        return super.getItem(i)
    }

    /* bridge */ /* synthetic */ Long getItemId(Int i) {
        return super.getItemId(i)
    }

    /* bridge */ /* synthetic */ View getView(Int i, View view, ViewGroup viewGroup) {
        return super.getView(i, view, viewGroup)
    }

    /* bridge */ /* synthetic */ Boolean hasStableIds() {
        return super.hasStableIds()
    }

    /* bridge */ /* synthetic */ Boolean isEmpty() {
        return super.isEmpty()
    }

    /* bridge */ /* synthetic */ Boolean isEnabled(Int i) {
        return super.isEnabled(i)
    }

    Unit onData(ImmutableList<ChatterDisplayData> immutableList) {
        if (this.predicate == null) {
            setData(immutableList)
        } else {
            setData(ImmutableList.copyOf(Iterables.filter(immutableList, this.predicate)))
        }
    }
}
