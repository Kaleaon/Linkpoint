package com.lumiyaviewer.lumiya.slproto.users.manager;

import com.google.common.collect.ImmutableList;
import com.lumiyaviewer.lumiya.Debug;
import com.lumiyaviewer.lumiya.slproto.users.ChatterID;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.concurrent.Executor;
import java.util.concurrent.atomic.AtomicBoolean;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;

abstract class ChatterDisplayDataList {
    private final Map<ChatterID, ChatterSubscription> chatterSubscriptions = new HashMap();
    private final SortedChatterList chatters;
    private final AtomicBoolean needsRefresh = new AtomicBoolean(false);
    private final Runnable refreshRunnable = new $Lambda$n0kmAon3UDOV6Jcsw0ejXq6u0xA(this);
    @Nonnull
    protected final UserManager userManager;

    ChatterDisplayDataList(@Nonnull UserManager userManager2, OnListUpdated onListUpdated, @Nullable Comparator<? super ChatterDisplayData> comparator) {
        this.userManager = userManager2;
        this.chatters = new SortedChatterList(onListUpdated, comparator);
    }

    private void refreshList() {
        for (ChatterSubscription chatterSubscription : this.chatterSubscriptions.values()) {
            chatterSubscription.isValid = false;
        }
        for (ChatterID chatterID : getChatters()) {
            ChatterSubscription chatterSubscription2 = this.chatterSubscriptions.get(chatterID);
            if (chatterSubscription2 == null) {
                if (chatterID instanceof ChatterID.ChatterIDUser) {
                    chatterSubscription2 = new ChatterUserSubscription(this.chatters, (ChatterID.ChatterIDUser) chatterID, this.userManager);
                } else if (chatterID instanceof ChatterID.ChatterIDGroup) {
                    chatterSubscription2 = new ChatterGroupSubscription(this.chatters, (ChatterID.ChatterIDGroup) chatterID, this.userManager);
                }
                if (chatterSubscription2 != null) {
                    this.chatterSubscriptions.put(chatterID, chatterSubscription2);
                }
            }
            if (chatterSubscription2 != null) {
                chatterSubscription2.isValid = true;
            }
        }
        Iterator<Map.Entry<ChatterID, ChatterSubscription>> it = this.chatterSubscriptions.entrySet().iterator();
        while (it.hasNext()) {
            ChatterSubscription chatterSubscription3 = (ChatterSubscription) it.next().getValue();
            if (!chatterSubscription3.isValid) {
                it.remove();
                chatterSubscription3.dispose();
            }
        }
        Debug.Printf("FriendList: refreshList: %d subscriptions", Integer.valueOf(this.chatterSubscriptions.size()));
    }

    public void dispose() {
        for (ChatterSubscription unsubscribe : this.chatterSubscriptions.values()) {
            unsubscribe.unsubscribe();
        }
    }

    public ImmutableList<ChatterDisplayData> getChatterList() {
        return this.chatters.getChatterList();
    }

    /* access modifiers changed from: protected */
    public abstract List<ChatterID> getChatters();

    /* access modifiers changed from: package-private */
    public /* synthetic */ void performListRefresh() {
        this.needsRefresh.set(false);
        refreshList();
    }

    /* access modifiers changed from: package-private */
    public void requestRefresh(@Nullable Executor executor) {
        Debug.Printf("FriendList: requestRefresh: needsRefresh = %s", Boolean.toString(this.needsRefresh.get()));
        if (this.needsRefresh.getAndSet(true)) {
            return;
        }
        if (executor != null) {
            executor.execute(this.refreshRunnable);
        } else {
            this.refreshRunnable.run();
        }
    }

    public void m288lambda$com_lumiyaviewer_lumiya_slproto_users_manager_ChatterDisplayDataList_2957() {
        // Lambda method implementation for chatter display data list update
        // This method is likely called to refresh or update the display data
    }
}
