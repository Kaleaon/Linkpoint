package com.lumiyaviewer.lumiya.orm;

import android.database.Cursor;
import com.google.common.cache.CacheBuilder;
import com.google.common.cache.CacheLoader;
import com.google.common.cache.LoadingCache;
import com.lumiyaviewer.lumiya.Debug;
import com.lumiyaviewer.lumiya.slproto.inventory.SLInventoryEntry;
import java.util.AbstractList;
import java.util.concurrent.ExecutionException;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;
/* loaded from: classes.dex */
public class InventoryEntryList extends AbstractList<SLInventoryEntry> {
    @Nullable
    private final Cursor cursor;
    private final LoadingCache<Integer, SLInventoryEntry> entryCache;
    @Nullable
    private final SLInventoryEntry folder;
    private final Object lock;
    private final int size;
    @Nullable
    private final String title;

    public InventoryEntryList() {
        this.lock = new Object();
        this.entryCache = CacheBuilder.newBuilder().maximumSize(1000L).weakValues().build(new CacheLoader<Integer, SLInventoryEntry>() { // from class: com.lumiyaviewer.lumiya.orm.InventoryEntryList.1
            @Override // com.google.common.cache.CacheLoader
            public SLInventoryEntry load(@Nonnull Integer num) {
                SLInventoryEntry sLInventoryEntry;
                if (InventoryEntryList.this.cursor == null) {
                    sLInventoryEntry = null;
                } else if (!InventoryEntryList.this.cursor.isClosed()) {
                    synchronized (InventoryEntryList.this.lock) {
                        try {
                            InventoryEntryList.this.cursor.moveToPosition(num.intValue());
                            sLInventoryEntry = new SLInventoryEntry(InventoryEntryList.this.cursor);
                        } catch (Exception e) {
                            Debug.Warning(e);
                            sLInventoryEntry = null;
                        }
                    }
                } else {
                    sLInventoryEntry = null;
                }
                return sLInventoryEntry == null ? new SLInventoryEntry() : sLInventoryEntry;
            }
        });
        this.title = null;
        this.cursor = null;
        this.folder = null;
        this.size = 0;
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public InventoryEntryList(@Nullable String str, @Nullable SLInventoryEntry sLInventoryEntry, @Nullable Cursor cursor) {
        this.lock = new Object();
        this.entryCache = CacheBuilder.newBuilder().maximumSize(1000L).weakValues().build(new CacheLoader<Integer, SLInventoryEntry>() { // from class: com.lumiyaviewer.lumiya.orm.InventoryEntryList.1
            @Override // com.google.common.cache.CacheLoader
            public SLInventoryEntry load(@Nonnull Integer num) {
                SLInventoryEntry sLInventoryEntry2;
                if (InventoryEntryList.this.cursor == null) {
                    sLInventoryEntry2 = null;
                } else if (!InventoryEntryList.this.cursor.isClosed()) {
                    synchronized (InventoryEntryList.this.lock) {
                        try {
                            InventoryEntryList.this.cursor.moveToPosition(num.intValue());
                            sLInventoryEntry2 = new SLInventoryEntry(InventoryEntryList.this.cursor);
                        } catch (Exception e) {
                            Debug.Warning(e);
                            sLInventoryEntry2 = null;
                        }
                    }
                } else {
                    sLInventoryEntry2 = null;
                }
                return sLInventoryEntry2 == null ? new SLInventoryEntry() : sLInventoryEntry2;
            }
        });
        this.title = str;
        this.folder = sLInventoryEntry;
        this.cursor = cursor;
        this.size = cursor != null ? cursor.getCount() : 0;
    }

    public void close() {
        synchronized (this.lock) {
            if (this.cursor != null && !this.cursor.isClosed()) {
                this.cursor.close();
            }
        }
    }

    @Override // java.util.AbstractList, java.util.List
    public SLInventoryEntry get(int i) {
        if (this.cursor != null && (!this.cursor.isClosed())) {
            try {
                return this.entryCache.get(Integer.valueOf(i));
            } catch (ExecutionException e) {
                Debug.Warning(e);
                return null;
            }
        }
        Object[] objArr = new Object[2];
        objArr[0] = Integer.valueOf(i);
        objArr[1] = this.cursor == null ? "null" : "closed";
        Debug.Printf("InventoryEntryList: returning null for %d because cursor is %s", objArr);
        return null;
    }

    @Nullable
    public SLInventoryEntry getFolder() {
        return this.folder;
    }

    @Nullable
    public String getTitle() {
        return this.title;
    }

    @Override // java.util.AbstractCollection, java.util.Collection, java.util.List
    public int size() {
        return this.size;
    }
}
