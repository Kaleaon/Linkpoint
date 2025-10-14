package com.lumiyaviewer.lumiya.orm

import android.database.Cursor
import com.google.common.cache.CacheBuilder
import com.google.common.cache.CacheLoader
import com.google.common.cache.LoadingCache
import com.lumiyaviewer.lumiya.Debug
import com.lumiyaviewer.lumiya.slproto.inventory.SLInventoryEntry
import java.util.AbstractList
import java.util.concurrent.ExecutionException
import javax.annotation.Nonnull
import javax.annotation.Nullable

class InventoryEntryList : AbstractList<SLInventoryEntry> {
    /* access modifiers changed from: private */
    @Nullable
    Cursor cursor
    private LoadingCache<Int, SLInventoryEntry> entryCache
    @Nullable
    private SLInventoryEntry folder
    /* access modifiers changed from: private */
    Any lock
    private Int size
    @Nullable
    private String title

    constructor() {
        this.lock = Any()
        this.entryCache = CacheBuilder.newBuilder().maximumSize(1000).weakValues().build(CacheLoader<Int, SLInventoryEntry>() {
            fun load(num: Int): SLInventoryEntry {
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

    InventoryEntryList(@Nullable String str, @Nullable SLInventoryEntry sLInventoryEntry, @Nullable Cursor cursor2) {
        this.lock = Any()
        this.entryCache = CacheBuilder.newBuilder().maximumSize(1000).weakValues().build(CacheLoader<Int, SLInventoryEntry>() {
            fun load(num: Int): SLInventoryEntry {
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

    fun close(): Unit {
        synchronized (this.lock) {
            if (this.cursor != null && !this.cursor.isClosed()) {
                this.cursor.close()
            }
        }
    }

    fun get(i: Int): SLInventoryEntry {
        if (this.cursor == null || !(!this.cursor.isClosed())) {
            Any[] objArr = Any[2]
            objArr[0] = Int.valueOf(i)
            objArr[1] = this.cursor == null ? "null" : "closed";
            Debug.Printf("InventoryEntryList: returning null for %d because cursor is %s", objArr);
            return null
        }
        try {
            return this.entryCache.get(Int.valueOf(i))
        } catch (ExecutionException e) {
            Debug.Warning(e)
            return null
        }
    }

    @Nullable
    fun getFolder(): SLInventoryEntry {
        return this.folder
    }

    @Nullable
    fun getTitle(): String {
        return this.title
    }

    fun size(): Int {
        return this.size
    }
}
