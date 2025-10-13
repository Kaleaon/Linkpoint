package com.lumiyaviewer.lumiya.ui.chat.contacts

import android.content.Context
import com.google.common.base.Predicate
import com.google.common.collect.ImmutableList
import com.google.common.collect.Iterables
import com.lumiyaviewer.lumiya.react.Subscription
import com.lumiyaviewer.lumiya.react.UIThreadExecutor
import com.lumiyaviewer.lumiya.slproto.users.manager.ChatterDisplayData
import com.lumiyaviewer.lumiya.slproto.users.manager.ChatterListType
import com.lumiyaviewer.lumiya.slproto.users.manager.UserManager
import java.io.Closeable

class ChatterListSubscriptionAdapter : ChatterListSimpleAdapter, 
    Subscription.OnData<ImmutableList<ChatterDisplayData>>, Closeable {

    private val predicate: Predicate<ChatterDisplayData>?
    private val subscription: Subscription<ChatterListType, ImmutableList<ChatterDisplayData>>

    constructor(context: Context, userManager: UserManager, chatterListType: ChatterListType) : super(context, userManager) {
        predicate = null
        subscription = userManager.chatterList.chatterList.subscribe(
            chatterListType,
            UIThreadExecutor.getInstance(),
            this
        )
    }

    constructor(
        context: Context,
        userManager: UserManager,
        chatterListType: ChatterListType,
        predicate: Predicate<ChatterDisplayData>
    ) : super(context, userManager) {
        this.predicate = predicate
        subscription = userManager.chatterList.chatterList.subscribe(
            chatterListType,
            UIThreadExecutor.getInstance(),
            this
        )
    }

    override fun close() {
        subscription.unsubscribe()
    }

    override fun onData(data: ImmutableList<ChatterDisplayData>) {
        if (predicate == null) {
            setData(data)
        } else {
            setData(ImmutableList.copyOf(Iterables.filter(data, predicate)))
        }
    }
}
