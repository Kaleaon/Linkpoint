package com.lumiyaviewer.lumiya.slproto.users.manager;

import com.google.common.collect.ImmutableList;
import com.lumiyaviewer.lumiya.Debug;
import java.util.Comparator;
import java.util.SortedSet;
import java.util.TreeSet;
import javax.annotation.Nullable;
import javax.annotation.concurrent.ThreadSafe;

@ThreadSafe
class SortedChatterList {
    private final SortedSet<ChatterDisplayData> chatters;
    private final Object lock = new Object();
    private final OnListUpdated onListUpdatedListener;
    private ImmutableList<ChatterDisplayData> sortedList = null;

    SortedChatterList(OnListUpdated onListUpdated, @Nullable Comparator<? super ChatterDisplayData> comparator) {
        this.chatters = new TreeSet(comparator);
        this.onListUpdatedListener = onListUpdated;
    }

    /* access modifiers changed from: package-private */
    public void addChatter(ChatterDisplayData chatterDisplayData) {
        boolean add;
        synchronized (this.lock) {
            add = this.chatters.add(chatterDisplayData);
            Debug.Printf("FriendList: added chatter data %s, needUpdate %s, count %d", chatterDisplayData.displayName, Boolean.toString(add), Integer.valueOf(this.chatters.size()));
            if (this.sortedList != null) {
                Debug.Printf("FriendList: dropping instance because of addChatter", new Object[0]);
            }
            this.sortedList = null;
        }
        if (add && this.onListUpdatedListener != null) {
            this.onListUpdatedListener.onListUpdated();
        }
    }

    public ImmutableList<ChatterDisplayData> getChatterList() {
        ImmutableList<ChatterDisplayData> immutableList;
        synchronized (this.lock) {
            if (this.sortedList == null) {
                Debug.Printf("FriendList: creating new list instance", new Object[0]);
                this.sortedList = ImmutableList.copyOf(this.chatters);
            }
            immutableList = this.sortedList;
        }
        return immutableList;
    }

    /* access modifiers changed from: package-private */
    public void removeChatter(ChatterDisplayData chatterDisplayData) {
        boolean remove;
        synchronized (this.lock) {
            remove = this.chatters.remove(chatterDisplayData);
            if (this.sortedList != null) {
                Debug.Printf("FriendList: dropping instance because of removeChatter", new Object[0]);
            }
            this.sortedList = null;
        }
        if (remove && this.onListUpdatedListener != null) {
            this.onListUpdatedListener.onListUpdated();
        }
    }

    /* access modifiers changed from: package-private */
    public void replaceChatter(ChatterDisplayData chatterDisplayData, ChatterDisplayData chatterDisplayData2) {
        boolean z = false;
        synchronized (this.lock) {
            if (this.chatters.remove(chatterDisplayData)) {
                z = true;
                this.chatters.add(chatterDisplayData2);
            }
            if (this.sortedList != null) {
                Debug.Printf("FriendList: dropping instance because of replaceChatter", new Object[0]);
            }
            this.sortedList = null;
        }
        if (z && this.onListUpdatedListener != null) {
            this.onListUpdatedListener.onListUpdated();
        }
    }
}
