package com.linkpoint.orm

import android.database.Cursor
import com.google.common.cache.CacheBuilder
import com.google.common.cache.CacheLoader
import com.google.common.cache.LoadingCache
import com.linkpoint.Debug
import com.linkpoint.slproto.inventory.SLInventoryEntry
import java.util.AbstractList
import java.util.concurrent.ExecutionException
import javax.annotation.Nonnull
import javax.annotation.Nullable

class InventoryEntryList : AbstractList()<SLInventoryEntry> {
    /* access modifiers changed from: private */
    val Cursor cursor
    private val LoadingCache<Integer, SLInventoryEntry> entryCache
    private val SLInventoryEntry folder
    /* access modifiers changed from: private */
    val Object lock
    private val Int size
    private val String title

    public InventoryEntryList() {
        this.lock = Object()
        this.entryCache = CacheBuilder.newBuilder().maximumSize(1000).weakValues().build(CacheLoader<Integer, SLInventoryEntry>() {
            public SLInventoryEntry load(Integer num) {
                SLInventoryEntry sLInventoryEntry
                if (InventoryEntryList.this.cursor == null) {
                    sLInventoryEntry = null
                } else if (!InventoryEntryList.this.cursor.isClosed()) {
                    synchronized (InventoryEntryList.this.lock) {
                        try {
                            InventoryEntryList.this.cursor.moveToPosition(num.intValue())
                            sLInventoryEntry = SLInventoryEntry(InventoryEntryList.this.cursor)
                        } catch (Exception e) {
                            Debug.Warning(e)
                            sLInventoryEntry = null
                        }
                    }
                } else {
                    sLInventoryEntry = null
                }
                return sLInventoryEntry == null ? SLInventoryEntry() : sLInventoryEntry
            }
        this.title = null
        this.cursor = null
        this.folder = null
        this.size = 0
    }

    InventoryEntryList(String str, SLInventoryEntry sLInventoryEntry, Cursor cursor2) {
        this.lock = Object()
        this.entryCache = CacheBuilder.newBuilder().maximumSize(1000).weakValues().build(CacheLoader<Integer, SLInventoryEntry>() {
            public SLInventoryEntry load(Integer num) {
                SLInventoryEntry sLInventoryEntry
                if (InventoryEntryList.this.cursor == null) {
                    sLInventoryEntry = null
                } else if (!InventoryEntryList.this.cursor.isClosed()) {
                    synchronized (InventoryEntryList.this.lock) {
                        try {
                            InventoryEntryList.this.cursor.moveToPosition(num.intValue())
                            sLInventoryEntry = SLInventoryEntry(InventoryEntryList.this.cursor)
                        } catch (Exception e) {
                            Debug.Warning(e)
                            sLInventoryEntry = null
                        }
                    }
                } else {
                    sLInventoryEntry = null
                }
                return sLInventoryEntry == null ? SLInventoryEntry() : sLInventoryEntry
            }
        this.title = str
        this.folder = sLInventoryEntry
        this.cursor = cursor2
        this.size = cursor2 != null ? cursor2.getCount() : 0
    }

    public Unit close() {
        synchronized (this.lock) {
            if (this.cursor != null && !this.cursor.isClosed()) {
                this.cursor.close()
            }
        }
    }

    public SLInventoryEntry get(Int i) {
        if (this.cursor == null || !(!this.cursor.isClosed())) {
            Object[] objArr = Object[2]
            objArr[0] = Integer.valueOf(i)
            objArr[1] = this.cursor == null ? "null" : "closed"
            Debug.Printf("InventoryEntryList: returning null for %d because cursor is %s", objArr)
            return null
        }
        try {
            return this.entryCache.get(Integer.valueOf(i))
        } catch (ExecutionException e) {
            Debug.Warning(e)
            return null
        }
    }

    public SLInventoryEntry getFolder() {
        return this.folder
    }

    public String getTitle() {
        return this.title
    }

    public Int size() {
        return this.size
    }
}
