// 
// Decompiled by Procyon v0.6.0
// 

package com.lumiyaviewer.lumiya.slproto.users.manager;

import com.lumiyaviewer.lumiya.react.UnsubscribableOne;
import com.lumiyaviewer.lumiya.react.Subscription;
import com.lumiyaviewer.lumiya.react.Subscribable;
import com.google.common.base.Predicate;
import com.google.common.base.Strings;
import com.lumiyaviewer.lumiya.orm.DBObject;
import com.lumiyaviewer.lumiya.react.DisposeHandler;
import com.lumiyaviewer.lumiya.react.AsyncRequestHandler;
import com.lumiyaviewer.lumiya.react.Refreshable;
import javax.annotation.Nullable;
import com.google.common.base.Objects;
import java.util.concurrent.Executor;
import com.lumiyaviewer.lumiya.react.RequestSource;
import com.lumiyaviewer.lumiya.orm.InventoryDBManager;
import com.lumiyaviewer.lumiya.Debug;
import java.util.concurrent.ConcurrentHashMap;
import java.util.Map;
import com.lumiyaviewer.lumiya.react.SubscriptionSingleKey;
import com.lumiyaviewer.lumiya.react.RequestHandler;
import com.lumiyaviewer.lumiya.react.OpportunisticExecutor;
import javax.annotation.Nonnull;
import com.lumiyaviewer.lumiya.orm.InventoryDB;
import com.lumiyaviewer.lumiya.react.RequestProcessor;
import com.lumiyaviewer.lumiya.slproto.inventory.SLInventoryEntry;
import com.lumiyaviewer.lumiya.orm.InventoryEntryList;
import com.lumiyaviewer.lumiya.orm.InventoryQuery;
import com.lumiyaviewer.lumiya.react.SubscriptionPool;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicReference;
import com.lumiyaviewer.lumiya.react.SubscriptionSingleDataPool;

public class InventoryManager
{
    private final SubscriptionSingleDataPool<InventoryClipboardEntry> clipboardPool;
    private final AtomicReference<UUID> currentSessionID;
    private final SubscriptionPool<InventoryQuery, InventoryEntryList> entryListPool;
    private final SubscriptionPool<UUID, SLInventoryEntry> folderEntryPool;
    private final SubscriptionPool<UUID, Boolean> folderLoadingPool;
    private final RequestProcessor<UUID, SLInventoryEntry, SLInventoryEntry> folderRequestProcessor;
    @Nonnull
    private final InventoryDB inventoryDB;
    private final OpportunisticExecutor inventoryDbExecutor;
    private final RequestHandler<InventoryQuery> queryRequestHandler;
    private final AtomicReference<UUID> rootFolderID;
    private final SubscriptionPool<SubscriptionSingleKey, Boolean> searchProcessPool;
    private final SubscriptionPool<SubscriptionSingleKey, Boolean> searchRunningPool;
    
    public InventoryManager(@Nonnull final UUID uuid) {
        this.inventoryDbExecutor = new OpportunisticExecutor("InventoryDB");
        this.folderLoadingPool = new SubscriptionPool<UUID, Boolean>();
        this.folderEntryPool = new SubscriptionPool<UUID, SLInventoryEntry>();
        this.entryListPool = new SubscriptionPool<InventoryQuery, InventoryEntryList>();
        this.rootFolderID = new AtomicReference<UUID>(null);
        this.currentSessionID = new AtomicReference<UUID>(null);
        this.searchProcessPool = new SubscriptionPool<SubscriptionSingleKey, Boolean>();
        this.searchRunningPool = new SubscriptionPool<SubscriptionSingleKey, Boolean>();
        this.clipboardPool = new SubscriptionSingleDataPool<InventoryClipboardEntry>();
        this.queryRequestHandler = new RequestHandler<InventoryQuery>() {
            private final Map<InventoryQuery, FolderSubscription> folderQueries = new ConcurrentHashMap<InventoryQuery, FolderSubscription>();
            
            @Override
            public void onRequest(@Nonnull final InventoryQuery inventoryQuery) {
                if (inventoryQuery.containsString() != null) {
                    InventoryManager.this.entryListPool.onResultData(inventoryQuery, inventoryQuery.query(null, InventoryManager.this.inventoryDB));
                }
                else {
                    UUID folderId;
                    if ((folderId = inventoryQuery.folderId()) == null) {
                        folderId = InventoryManager.this.rootFolderID.get();
                    }
                    Debug.Printf("Inventory: queryRequestHandler: folderId = '%s'", folderId);
                    if (folderId != null) {
                        final FolderSubscription folderSubscription = this.folderQueries.put(inventoryQuery, new FolderSubscription(inventoryQuery, folderId, (FolderSubscription)null));
                        if (folderSubscription != null) {
                            folderSubscription.unsubscribe();
                        }
                    }
                }
            }
            
            @Override
            public void onRequestCancelled(@Nonnull final InventoryQuery inventoryQuery) {
                final FolderSubscription folderSubscription = this.folderQueries.get(inventoryQuery);
                if (folderSubscription != null) {
                    folderSubscription.unsubscribe();
                }
            }
        };
        final InventoryDB userInventoryDB = InventoryDBManager.getUserInventoryDB(uuid);
        if (userInventoryDB == null) {
            throw new IllegalArgumentException("Null inventory database");
        }
        this.inventoryDB = userInventoryDB;
        this.folderRequestProcessor = new RequestProcessor<UUID, SLInventoryEntry, SLInventoryEntry>(this.folderEntryPool, this.inventoryDbExecutor) {
            @Override
            protected boolean isRequestComplete(@Nonnull final UUID uuid, final SLInventoryEntry slInventoryEntry) {
                return slInventoryEntry != null && Objects.equal(slInventoryEntry.sessionID, InventoryManager.this.currentSessionID.get());
            }
            
            @Nullable
            @Override
            protected SLInventoryEntry processRequest(@Nonnull final UUID uuid) {
                return userInventoryDB.findEntry(uuid);
            }
            
            @Override
            protected SLInventoryEntry processResult(@Nonnull final UUID uuid, final SLInventoryEntry slInventoryEntry) {
                if (slInventoryEntry != null) {
                    Debug.Printf("Inventory: entry subscription got name: %s with folderId = '%s'", slInventoryEntry.name, slInventoryEntry.uuid);
                }
                InventoryManager.this.updateSearchResults();
                return slInventoryEntry;
            }
        };
        this.folderEntryPool.setCacheInvalidateHandler(new _$Lambda$JIBenvPHaOomPgMJhTFPuiVXBzY$2(userInventoryDB), this.inventoryDbExecutor);
        this.entryListPool.attachRequestHandler(new AsyncRequestHandler<InventoryQuery>(this.inventoryDbExecutor, this.queryRequestHandler));
        this.entryListPool.setDisposeHandler(new _$Lambda$JIBenvPHaOomPgMJhTFPuiVXBzY$1(), this.inventoryDbExecutor);
    }
    
    private void updateSearchResults() {
        this.entryListPool.requestUpdateSome(new _$Lambda$JIBenvPHaOomPgMJhTFPuiVXBzY());
    }
    
    public void copyToClipboard(@Nullable final InventoryClipboardEntry inventoryClipboardEntry) {
        this.clipboardPool.setData(SubscriptionSingleKey.Value, inventoryClipboardEntry);
    }
    
    public Subscribable<SubscriptionSingleKey, InventoryClipboardEntry> getClipboard() {
        return (Subscribable<SubscriptionSingleKey, InventoryClipboardEntry>)this.clipboardPool;
    }
    
    public InventoryDB getDatabase() {
        return this.inventoryDB;
    }
    
    public Executor getExecutor() {
        return this.inventoryDbExecutor;
    }
    
    public Subscribable<UUID, SLInventoryEntry> getFolderEntryPool() {
        return this.folderEntryPool;
    }
    
    public Subscribable<UUID, Boolean> getFolderLoading() {
        return this.folderLoadingPool;
    }
    
    public RequestSource<UUID, Boolean> getFolderLoadingRequestSource() {
        return this.folderLoadingPool;
    }
    
    public RequestSource<UUID, SLInventoryEntry> getFolderRequestSource() {
        return this.folderRequestProcessor;
    }
    
    public Subscribable<InventoryQuery, InventoryEntryList> getInventoryEntries() {
        return this.entryListPool;
    }
    
    @Nullable
    public UUID getRootFolder() {
        return this.rootFolderID.get();
    }
    
    public Subscribable<SubscriptionSingleKey, Boolean> getSearchProcess() {
        return this.searchProcessPool;
    }
    
    public RequestSource<SubscriptionSingleKey, Boolean> getSearchProcessRequestSource() {
        return this.searchProcessPool;
    }
    
    public SubscriptionPool<SubscriptionSingleKey, Boolean> getSearchRunning() {
        return this.searchRunningPool;
    }
    
    public void requestFolderUpdate(@Nonnull final UUID uuid) {
        this.folderEntryPool.requestUpdate(uuid);
    }
    
    public void setCurrentSessionID(final UUID newValue) {
        this.currentSessionID.set(newValue);
    }
    
    public void setRootFolder(final UUID newValue) {
        this.rootFolderID.set(newValue);
    }
    
    private class FolderSubscription implements OnData<SLInventoryEntry>, OnError, UnsubscribableOne
    {
        private final InventoryQuery query;
        private final Subscription<UUID, SLInventoryEntry> subscription;
        
        private FolderSubscription(final InventoryQuery query, @Nonnull final UUID uuid) {
            this.query = query;
            Debug.Printf("Inventory: folder subscription: folderId = '%s'", uuid);
            this.subscription = InventoryManager.this.folderEntryPool.subscribe(uuid, InventoryManager.this.inventoryDbExecutor, this, this);
        }
        
        public void onData(final SLInventoryEntry slInventoryEntry) {
            if (slInventoryEntry != null) {
                Debug.Printf("Inventory: folder subscription got name: %s with folderId = '%s'", slInventoryEntry.name, slInventoryEntry.uuid);
            }
            InventoryManager.this.entryListPool.onResultData(this.query, this.query.query(slInventoryEntry, InventoryManager.this.inventoryDB));
        }
        
        @Override
        public void onError(final Throwable t) {
            Debug.Printf("Inventory: subscription error: %s", t);
            Debug.Warning(t);
            InventoryManager.this.entryListPool.onResultError(this.query, t);
        }
        
        @Override
        public void unsubscribe() {
            this.subscription.unsubscribe();
        }
    }
    
    public static class InventoryClipboardEntry
    {
        @Nonnull
        public final SLInventoryEntry inventoryEntry;
        public final boolean isCut;
        
        public InventoryClipboardEntry(final boolean isCut, @Nonnull final SLInventoryEntry inventoryEntry) {
            this.isCut = isCut;
            this.inventoryEntry = inventoryEntry;
        }
    }
}
