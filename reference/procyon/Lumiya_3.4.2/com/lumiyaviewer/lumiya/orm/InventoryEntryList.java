// 
// Decompiled by Procyon v0.6.0
// 

package com.lumiyaviewer.lumiya.orm;

import java.util.concurrent.ExecutionException;
import com.lumiyaviewer.lumiya.Debug;
import javax.annotation.Nonnull;
import com.google.common.cache.CacheLoader;
import com.google.common.cache.CacheBuilder;
import com.google.common.cache.LoadingCache;
import javax.annotation.Nullable;
import android.database.Cursor;
import com.lumiyaviewer.lumiya.slproto.inventory.SLInventoryEntry;
import java.util.AbstractList;

public class InventoryEntryList extends AbstractList<SLInventoryEntry>
{
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
        this.entryCache = CacheBuilder.newBuilder().maximumSize(1000L).weakValues().build((CacheLoader<? super Integer, SLInventoryEntry>)new CacheLoader<Integer, SLInventoryEntry>() {
            @Override
            public SLInventoryEntry load(@Nonnull final Integer n) {
            Label_0071:
                while (true) {
                    Label_0087: {
                        if (InventoryEntryList.this.cursor == null) {
                            break Label_0087;
                        }
                        if (!(InventoryEntryList.this.cursor.isClosed() ^ true)) {
                            break Label_0087;
                        }
                        Object -get1 = InventoryEntryList.this.lock;
                        synchronized (-get1) {
                            while (true) {
                                try {
                                    InventoryEntryList.this.cursor.moveToPosition((int)n);
                                    SLInventoryEntry slInventoryEntry = new SLInventoryEntry(InventoryEntryList.this.cursor);
                                    monitorexit(-get1);
                                    -get1 = slInventoryEntry;
                                    if (slInventoryEntry == null) {
                                        -get1 = new SLInventoryEntry();
                                    }
                                    return (SLInventoryEntry)-get1;
                                    slInventoryEntry = null;
                                    continue Label_0071;
                                }
                                catch (final Exception ex) {
                                    Debug.Warning(ex);
                                    final SLInventoryEntry slInventoryEntry = null;
                                    continue;
                                }
                                break;
                            }
                        }
                    }
                    SLInventoryEntry slInventoryEntry = null;
                    continue Label_0071;
                }
            }
        });
        this.title = null;
        this.cursor = null;
        this.folder = null;
        this.size = 0;
    }
    
    InventoryEntryList(@Nullable final String title, @Nullable final SLInventoryEntry folder, @Nullable final Cursor cursor) {
        this.lock = new Object();
        this.entryCache = CacheBuilder.newBuilder().maximumSize(1000L).weakValues().build((CacheLoader<? super Integer, SLInventoryEntry>)new CacheLoader<Integer, SLInventoryEntry>() {
            @Override
            public SLInventoryEntry load(@Nonnull final Integer n) {
            Label_0071:
                while (true) {
                    Label_0087: {
                        if (InventoryEntryList.this.cursor == null) {
                            break Label_0087;
                        }
                        if (!(InventoryEntryList.this.cursor.isClosed() ^ true)) {
                            break Label_0087;
                        }
                        Object -get1 = InventoryEntryList.this.lock;
                        synchronized (-get1) {
                            while (true) {
                                try {
                                    InventoryEntryList.this.cursor.moveToPosition((int)n);
                                    SLInventoryEntry slInventoryEntry = new SLInventoryEntry(InventoryEntryList.this.cursor);
                                    monitorexit(-get1);
                                    -get1 = slInventoryEntry;
                                    if (slInventoryEntry == null) {
                                        -get1 = new SLInventoryEntry();
                                    }
                                    return (SLInventoryEntry)-get1;
                                    slInventoryEntry = null;
                                    continue Label_0071;
                                }
                                catch (final Exception ex) {
                                    Debug.Warning(ex);
                                    final SLInventoryEntry slInventoryEntry = null;
                                    continue;
                                }
                                break;
                            }
                        }
                    }
                    SLInventoryEntry slInventoryEntry = null;
                    continue Label_0071;
                }
            }
        });
        this.title = title;
        this.folder = folder;
        this.cursor = cursor;
        int count;
        if (cursor != null) {
            count = cursor.getCount();
        }
        else {
            count = 0;
        }
        this.size = count;
    }
    
    public void close() {
        synchronized (this.lock) {
            if (this.cursor != null && !this.cursor.isClosed()) {
                this.cursor.close();
            }
        }
    }
    
    @Override
    public SLInventoryEntry get(final int n) {
        if (this.cursor != null && (this.cursor.isClosed() ^ true)) {
            try {
                return this.entryCache.get(n);
            }
            catch (final ExecutionException ex) {
                Debug.Warning(ex);
                return null;
            }
        }
        String s;
        if (this.cursor == null) {
            s = "null";
        }
        else {
            s = "closed";
        }
        Debug.Printf("InventoryEntryList: returning null for %d because cursor is %s", n, s);
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
    
    @Override
    public int size() {
        return this.size;
    }
}
