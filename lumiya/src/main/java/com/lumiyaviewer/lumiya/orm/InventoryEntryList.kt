package com.lumiyaviewer.lumiya.orm

import android.database.Cursor
import com.google.common.cache.CacheBuilder
import com.google.common.cache.CacheLoader
import com.google.common.cache.LoadingCache
import com.lumiyaviewer.lumiya.Debug
import com.lumiyaviewer.lumiya.slproto.inventory.SLInventoryEntry
import java.util.AbstractList
import java.util.concurrent.ExecutionException

class InventoryEntryList : AbstractList<SLInventoryEntry> {
    
    private var cursor: Cursor? = null
    private val entryCache: LoadingCache<Int, SLInventoryEntry>
    private var folder: SLInventoryEntry? = null
    private val lock = Any()
    private var listSize: Int = 0
    private var title: String? = null

    constructor() {
        this.cursor = null
        this.entryCache = createCache()
        this.listSize = 0
    }

    constructor(title: String?, folder: SLInventoryEntry?, cursor: Cursor?) {
        this.title = title
        this.folder = folder
        this.cursor = cursor
        this.listSize = cursor?.count ?: 0
        this.entryCache = createCache()
    }

    private fun createCache(): LoadingCache<Int, SLInventoryEntry> {
        return CacheBuilder.newBuilder()
            .maximumSize(1000)
            .weakValues()
            .build(object : CacheLoader<Int, SLInventoryEntry>() {
                override fun load(key: Int): SLInventoryEntry {
                    var entry: SLInventoryEntry? = null
                    if (cursor == null) {
                        entry = null
                    } else if (!cursor!!.isClosed) {
                        synchronized(lock) {
                            try {
                                cursor!!.moveToPosition(key)
                                entry = SLInventoryEntry(cursor!!)
                            } catch (e: Exception) {
                                Debug.Warning(e)
                                entry = null
                            }
                        }
                    }
                    return entry ?: SLInventoryEntry() // Return empty entry if failed
                }
            })
    }

    fun close() {
        synchronized(lock) {
            if (cursor != null && !cursor!!.isClosed) {
                cursor!!.close()
            }
        }
    }

    override fun get(index: Int): SLInventoryEntry {
        if (cursor == null || cursor!!.isClosed) {
            Debug.Printf("InventoryEntryList: returning null for %d because cursor is %s", index, if (cursor == null) "null" else "closed")
            return SLInventoryEntry() // Return empty instead of null to satisfy AbstractList contract
        }
        try {
            return entryCache.get(index)
        } catch (e: ExecutionException) {
            Debug.Warning(e)
            return SLInventoryEntry()
        }
    }

    fun getFolder(): SLInventoryEntry? {
        return folder
    }

    fun getTitle(): String? {
        return title
    }

    override val size: Int
        get() = listSize
}
