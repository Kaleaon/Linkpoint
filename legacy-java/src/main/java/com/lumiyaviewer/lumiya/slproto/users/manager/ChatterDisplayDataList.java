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

/* JADX INFO: Access modifiers changed from: package-private */
/* loaded from: classes.dex */
public abstract class ChatterDisplayDataList {
    private final SortedChatterList chatters;

    @Nonnull
    protected final UserManager userManager;
    private final Map<ChatterID, ChatterSubscription> chatterSubscriptions = new HashMap();
    private final AtomicBoolean needsRefresh = new AtomicBoolean(false);
    private final Runnable refreshRunnable = new Runnable() { // from class: com.lumiyaviewer.lumiya.slproto.users.manager.-$Lambda$n0kmAon3UDOV6Jcsw0ejXq6u0xA
        private final /* synthetic */ void $m$0() {
            ((ChatterDisplayDataList) this).m298x2aebe54e();
        }

        @Override // java.lang.Runnable
        public final void run() {
            $m$0();
        }
    };

    /* JADX INFO: Access modifiers changed from: package-private */
    public ChatterDisplayDataList(@Nonnull UserManager userManager, OnListUpdated onListUpdated, @Nullable Comparator<? super ChatterDisplayData> comparator) {
        this.userManager = userManager;
        this.chatters = new SortedChatterList(onListUpdated, comparator);
    }

    private void refreshList() {
        Iterator<T> it = this.chatterSubscriptions.values().iterator();
        while (it.hasNext()) {
            ((ChatterSubscription) it.next()).isValid = false;
        }
        for (ChatterID chatterID : getChatters()) {
            ChatterSubscription chatterSubscription = this.chatterSubscriptions.get(chatterID);
            if (chatterSubscription == null) {
                if (chatterID instanceof ChatterID.ChatterIDUser) {
                    chatterSubscription = new ChatterUserSubscription(this.chatters, (ChatterID.ChatterIDUser) chatterID, this.userManager);
                } else if (chatterID instanceof ChatterID.ChatterIDGroup) {
                    chatterSubscription = new ChatterGroupSubscription(this.chatters, (ChatterID.ChatterIDGroup) chatterID, this.userManager);
                }
                if (chatterSubscription != null) {
                    this.chatterSubscriptions.put(chatterID, chatterSubscription);
                }
            }
            if (chatterSubscription != null) {
                chatterSubscription.isValid = true;
            }
        }
        Iterator<Map.Entry<ChatterID, ChatterSubscription>> it2 = this.chatterSubscriptions.entrySet().iterator();
        while (it2.hasNext()) {
            ChatterSubscription value = it2.next().getValue();
            if (!value.isValid) {
                it2.remove();
                value.dispose();
            }
        }
        Debug.Printf("FriendList: refreshList: %d subscriptions", Integer.valueOf(this.chatterSubscriptions.size()));
    }

    public void dispose() {
        Iterator<T> it = this.chatterSubscriptions.values().iterator();
        while (it.hasNext()) {
            ((ChatterSubscription) it.next()).unsubscribe();
        }
    }

    public ImmutableList<ChatterDisplayData> getChatterList() {
        return this.chatters.getChatterList();
    }

    protected abstract List<ChatterID> getChatters();

    /* JADX INFO: Access modifiers changed from: package-private */
    /* renamed from: lambda$-com_lumiyaviewer_lumiya_slproto_users_manager_ChatterDisplayDataList_2957, reason: not valid java name */
    public /* synthetic */ void m298x2aebe54e() {
        this.needsRefresh.set(false);
        refreshList();
    }

    /* JADX INFO: Access modifiers changed from: package-private */
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
}
