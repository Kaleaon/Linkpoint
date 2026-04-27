package com.linkpoint.slproto.users.manager

import com.google.common.collect.ImmutableList
import com.linkpoint.Debug
import com.linkpoint.slproto.users.ChatterID
import java.util.Comparator
import java.util.HashMap
import java.util.Iterator
import java.util.List
import java.util.Map
import java.util.concurrent.Executor
import java.util.concurrent.atomic.AtomicBoolean
import javax.annotation.Nonnull
import javax.annotation.Nullable

abstract class ChatterDisplayDataList {
    private val Map<ChatterID, ChatterSubscription> chatterSubscriptions = HashMap()
    private val SortedChatterList chatters
    private val AtomicBoolean needsRefresh = AtomicBoolean(false)
    private val Runnable refreshRunnable = $Lambda$n0kmAon3UDOV6Jcsw0ejXq6u0xA(this)
    protected val UserManager userManager

    ChatterDisplayDataList(UserManager userManager2, OnListUpdated onListUpdated, Comparator<? super ChatterDisplayData> comparator) {
        this.userManager = userManager2
        this.chatters = SortedChatterList(onListUpdated, comparator)
    }

     private fun refreshList() {
        for (ChatterSubscription chatterSubscription : this.chatterSubscriptions.values()) {
            chatterSubscription.isValid = false
        }
        for (ChatterID chatterID : getChatters()) {
            val chatterSubscription2: ChatterSubscription = this.chatterSubscriptions.get(chatterID)
            if (chatterSubscription2 == null) {
                if (chatterID instanceof ChatterID.ChatterIDUser) {
                    chatterSubscription2 = ChatterUserSubscription(this.chatters, (ChatterID.ChatterIDUser) chatterID, this.userManager)
                } else if (chatterID instanceof ChatterID.ChatterIDGroup) {
                    chatterSubscription2 = ChatterGroupSubscription(this.chatters, (ChatterID.ChatterIDGroup) chatterID, this.userManager)
                }
                if (chatterSubscription2 != null) {
                    this.chatterSubscriptions.put(chatterID, chatterSubscription2)
                }
            }
            if (chatterSubscription2 != null) {
                chatterSubscription2.isValid = true
            }
        }
        Iterator<Map.Entry<ChatterID, ChatterSubscription>> it = this.chatterSubscriptions.entrySet().iterator()
        while (it.hasNext()) {
            val chatterSubscription3: ChatterSubscription = (ChatterSubscription) it.next().getValue()
            if (!chatterSubscription3.isValid) {
                it.remove()
                chatterSubscription3.dispose()
            }
        }
        Debug.Printf("FriendList: refreshList: %d subscriptions", Integer.valueOf(this.chatterSubscriptions.size()))
    }

    fun dispose() {
        for (ChatterSubscription unsubscribe : this.chatterSubscriptions.values()) {
            unsubscribe.unsubscribe()
        }
    }

    public ImmutableList<ChatterDisplayData> getChatterList() {
        return this.chatters.getChatterList()
    }

    /* access modifiers changed from: protected */
    public abstract List<ChatterID> getChatters()

    /* access modifiers changed from: package-private */
    /* renamed from: lambda$-com_lumiyaviewer_lumiya_slproto_users_manager_ChatterDisplayDataList_2957  reason: not valid java name */
    public /* synthetic */ Unit m288lambda$com_lumiyaviewer_lumiya_slproto_users_manager_ChatterDisplayDataList_2957() {
        this.needsRefresh.set(false)
        refreshList()
    }

    /* access modifiers changed from: package-private */
    fun requestRefresh(executor: Executor) {
        Debug.Printf("FriendList: requestRefresh: needsRefresh = %s", Boolean.toString(this.needsRefresh.get()))
        if (this.needsRefresh.getAndSet(true)) {
            return
        }
        if (executor != null) {
            executor.execute(this.refreshRunnable)
        } else {
            this.refreshRunnable.run()
        }
    }
}
