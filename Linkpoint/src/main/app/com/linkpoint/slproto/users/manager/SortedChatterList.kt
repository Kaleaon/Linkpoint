package com.linkpoint.slproto.users.manager

import com.google.common.collect.ImmutableList
import com.linkpoint.Debug
import java.util.Comparator
import java.util.SortedSet
import java.util.TreeSet
import androidx.annotation.Nullable
import androidx.annotation.ThreadSafe

@ThreadSafe
class SortedChatterList {
    private SortedSet<ChatterDisplayData> chatters
    private Any lock = Any()
    private OnListUpdated onListUpdatedListener
    private ImmutableList<ChatterDisplayData> sortedList = null

    SortedChatterList(OnListUpdated onListUpdated, @Nullable Comparator<? super ChatterDisplayData> comparator) {
        this.chatters = TreeSet(comparator)
        this.onListUpdatedListener = onListUpdated
    }

    /* access modifiers changed from: package-private */
    Unit addChatter(ChatterDisplayData chatterDisplayData) {
        Boolean add
        synchronized (this.lock) {
            add = this.chatters.add(chatterDisplayData)
            Debug.Printf("FriendList: added chatter data %s, needUpdate %s, count %d", chatterDisplayData.displayName, Boolean.toString(add), Int.valueOf(this.chatters.size()))
            if (this.sortedList != null) {
                Debug.Printf("FriendList: dropping instance because of addChatter", Any[0])
            }
            this.sortedList = null
        }
        if (add && this.onListUpdatedListener != null) {
            this.onListUpdatedListener.onListUpdated()
        }
    }

    ImmutableList<ChatterDisplayData> getChatterList() {
        ImmutableList<ChatterDisplayData> immutableList
        synchronized (this.lock) {
            if (this.sortedList == null) {
                Debug.Printf("FriendList: creating list instance", Any[0])
                this.sortedList = ImmutableList.copyOf(this.chatters)
            }
            immutableList = this.sortedList
        }
        return immutableList
    }

    /* access modifiers changed from: package-private */
    Unit removeChatter(ChatterDisplayData chatterDisplayData) {
        Boolean remove
        synchronized (this.lock) {
            remove = this.chatters.remove(chatterDisplayData)
            if (this.sortedList != null) {
                Debug.Printf("FriendList: dropping instance because of removeChatter", Any[0])
            }
            this.sortedList = null
        }
        if (remove && this.onListUpdatedListener != null) {
            this.onListUpdatedListener.onListUpdated()
        }
    }

    /* access modifiers changed from: package-private */
    Unit replaceChatter(ChatterDisplayData chatterDisplayData, ChatterDisplayData chatterDisplayData2) {
        Boolean z = false
        synchronized (this.lock) {
            if (this.chatters.remove(chatterDisplayData)) {
                z = true
                this.chatters.add(chatterDisplayData2)
            }
            if (this.sortedList != null) {
                Debug.Printf("FriendList: dropping instance because of replaceChatter", Any[0])
            }
            this.sortedList = null
        }
        if (z && this.onListUpdatedListener != null) {
            this.onListUpdatedListener.onListUpdated()
        }
    }
}
