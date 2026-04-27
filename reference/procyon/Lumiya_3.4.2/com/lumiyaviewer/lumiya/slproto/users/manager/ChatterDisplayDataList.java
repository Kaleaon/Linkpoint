// 
// Decompiled by Procyon v0.6.0
// 

package com.lumiyaviewer.lumiya.slproto.users.manager;

import java.util.concurrent.Executor;
import java.util.List;
import com.google.common.collect.ImmutableList;
import java.util.Iterator;
import com.lumiyaviewer.lumiya.Debug;
import java.util.HashMap;
import javax.annotation.Nullable;
import java.util.Comparator;
import javax.annotation.Nonnull;
import java.util.concurrent.atomic.AtomicBoolean;
import com.lumiyaviewer.lumiya.slproto.users.ChatterID;
import java.util.Map;

abstract class ChatterDisplayDataList
{
    private final Map<ChatterID, ChatterSubscription> chatterSubscriptions;
    private final SortedChatterList chatters;
    private final AtomicBoolean needsRefresh;
    private final Runnable refreshRunnable;
    @Nonnull
    protected final UserManager userManager;
    
    ChatterDisplayDataList(@Nonnull final UserManager userManager, final OnListUpdated onListUpdated, @Nullable final Comparator<? super ChatterDisplayData> comparator) {
        this.chatterSubscriptions = new HashMap<ChatterID, ChatterSubscription>();
        this.needsRefresh = new AtomicBoolean(false);
        this.refreshRunnable = new _$Lambda$n0kmAon3UDOV6Jcsw0ejXq6u0xA(this);
        this.userManager = userManager;
        this.chatters = new SortedChatterList(onListUpdated, comparator);
    }
    
    private void refreshList() {
        final Iterator<Object> iterator = this.chatterSubscriptions.values().iterator();
        while (iterator.hasNext()) {
            iterator.next().isValid = false;
        }
        for (final ChatterID chatterID : this.getChatters()) {
            ChatterSubscription chatterSubscription = this.chatterSubscriptions.get(chatterID);
            ChatterSubscription chatterSubscription2;
            if ((chatterSubscription2 = chatterSubscription) == null) {
                if (chatterID instanceof ChatterID.ChatterIDUser) {
                    chatterSubscription = new ChatterUserSubscription(this.chatters, (ChatterID.ChatterIDUser)chatterID, this.userManager);
                }
                else if (chatterID instanceof ChatterID.ChatterIDGroup) {
                    chatterSubscription = new ChatterGroupSubscription(this.chatters, (ChatterID.ChatterIDGroup)chatterID, this.userManager);
                }
                if ((chatterSubscription2 = chatterSubscription) != null) {
                    this.chatterSubscriptions.put(chatterID, chatterSubscription);
                    chatterSubscription2 = chatterSubscription;
                }
            }
            if (chatterSubscription2 != null) {
                chatterSubscription2.isValid = true;
            }
        }
        final Iterator<Map.Entry<ChatterID, ChatterSubscription>> iterator3 = this.chatterSubscriptions.entrySet().iterator();
        while (iterator3.hasNext()) {
            final ChatterSubscription chatterSubscription3 = ((Map.Entry<K, ChatterSubscription>)iterator3.next()).getValue();
            if (!chatterSubscription3.isValid) {
                iterator3.remove();
                chatterSubscription3.dispose();
            }
        }
        Debug.Printf("FriendList: refreshList: %d subscriptions", this.chatterSubscriptions.size());
    }
    
    public void dispose() {
        final Iterator<Object> iterator = this.chatterSubscriptions.values().iterator();
        while (iterator.hasNext()) {
            iterator.next().unsubscribe();
        }
    }
    
    public ImmutableList<ChatterDisplayData> getChatterList() {
        return this.chatters.getChatterList();
    }
    
    protected abstract List<ChatterID> getChatters();
    
    void requestRefresh(@Nullable final Executor executor) {
        Debug.Printf("FriendList: requestRefresh: needsRefresh = %s", Boolean.toString(this.needsRefresh.get()));
        if (!this.needsRefresh.getAndSet(true)) {
            if (executor != null) {
                executor.execute(this.refreshRunnable);
            }
            else {
                this.refreshRunnable.run();
            }
        }
    }
}
