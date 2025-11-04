// 
// Decompiled by Procyon v0.6.0
// 

package com.lumiyaviewer.lumiya.slproto.users.manager;

import java.util.Collection;
import com.lumiyaviewer.lumiya.Debug;
import java.util.TreeSet;
import javax.annotation.Nullable;
import java.util.Comparator;
import com.google.common.collect.ImmutableList;
import java.util.SortedSet;
import javax.annotation.concurrent.ThreadSafe;

@ThreadSafe
class SortedChatterList
{
    private final SortedSet<ChatterDisplayData> chatters;
    private final Object lock;
    private final OnListUpdated onListUpdatedListener;
    private ImmutableList<ChatterDisplayData> sortedList;
    
    SortedChatterList(final OnListUpdated onListUpdatedListener, @Nullable final Comparator<? super ChatterDisplayData> comparator) {
        this.lock = new Object();
        this.sortedList = null;
        this.chatters = new TreeSet<ChatterDisplayData>(comparator);
        this.onListUpdatedListener = onListUpdatedListener;
    }
    
    void addChatter(final ChatterDisplayData chatterDisplayData) {
        synchronized (this.lock) {
            final boolean add = this.chatters.add(chatterDisplayData);
            Debug.Printf("FriendList: added chatter data %s, needUpdate %s, count %d", chatterDisplayData.displayName, Boolean.toString(add), this.chatters.size());
            if (this.sortedList != null) {
                Debug.Printf("FriendList: dropping instance because of addChatter", new Object[0]);
            }
            this.sortedList = null;
            monitorexit(this.lock);
            if (add && this.onListUpdatedListener != null) {
                this.onListUpdatedListener.onListUpdated();
            }
        }
    }
    
    public ImmutableList<ChatterDisplayData> getChatterList() {
        synchronized (this.lock) {
            if (this.sortedList == null) {
                Debug.Printf("FriendList: creating new list instance", new Object[0]);
                this.sortedList = ImmutableList.copyOf((Collection<? extends ChatterDisplayData>)this.chatters);
            }
            return this.sortedList;
        }
    }
    
    void removeChatter(final ChatterDisplayData chatterDisplayData) {
        synchronized (this.lock) {
            final boolean remove = this.chatters.remove(chatterDisplayData);
            if (this.sortedList != null) {
                Debug.Printf("FriendList: dropping instance because of removeChatter", new Object[0]);
            }
            this.sortedList = null;
            monitorexit(this.lock);
            if (remove && this.onListUpdatedListener != null) {
                this.onListUpdatedListener.onListUpdated();
            }
        }
    }
    
    void replaceChatter(final ChatterDisplayData chatterDisplayData, final ChatterDisplayData chatterDisplayData2) {
        boolean b = false;
        synchronized (this.lock) {
            if (this.chatters.remove(chatterDisplayData)) {
                b = true;
                this.chatters.add(chatterDisplayData2);
            }
            if (this.sortedList != null) {
                Debug.Printf("FriendList: dropping instance because of replaceChatter", new Object[0]);
            }
            this.sortedList = null;
            monitorexit(this.lock);
            if (b && this.onListUpdatedListener != null) {
                this.onListUpdatedListener.onListUpdated();
            }
        }
    }
}
