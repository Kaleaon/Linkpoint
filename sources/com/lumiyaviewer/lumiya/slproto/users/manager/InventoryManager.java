// Decompiled by Jad v1.5.8e. Copyright 2001 Pavel Kouznetsov.
// Jad home page: http://www.geocities.com/kpdus/jad.html
// Decompiler options: braces fieldsfirst space lnc 

package com.lumiyaviewer.lumiya.slproto.users.manager;

import com.google.common.base.Objects;
import com.google.common.base.Strings;
import com.lumiyaviewer.lumiya.Debug;
import com.lumiyaviewer.lumiya.orm.InventoryDB;
import com.lumiyaviewer.lumiya.orm.InventoryDBManager;
import com.lumiyaviewer.lumiya.orm.InventoryEntryList;
import com.lumiyaviewer.lumiya.orm.InventoryQuery;
import com.lumiyaviewer.lumiya.react.AsyncRequestHandler;
import com.lumiyaviewer.lumiya.react.OpportunisticExecutor;
import com.lumiyaviewer.lumiya.react.RequestHandler;
import com.lumiyaviewer.lumiya.react.RequestProcessor;
import com.lumiyaviewer.lumiya.react.RequestSource;
import com.lumiyaviewer.lumiya.react.Subscribable;
import com.lumiyaviewer.lumiya.react.Subscription;
import com.lumiyaviewer.lumiya.react.SubscriptionPool;
import com.lumiyaviewer.lumiya.react.SubscriptionSingleDataPool;
import com.lumiyaviewer.lumiya.react.SubscriptionSingleKey;
import com.lumiyaviewer.lumiya.react.UnsubscribableOne;
import com.lumiyaviewer.lumiya.slproto.inventory.SLInventoryEntry;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.Executor;
import java.util.concurrent.atomic.AtomicReference;

public class InventoryManager
{
    private class FolderSubscription
        implements com.lumiyaviewer.lumiya.react.Subscription.OnData, com.lumiyaviewer.lumiya.react.Subscription.OnError, UnsubscribableOne
    {

        private final InventoryQuery query;
        private final Subscription subscription;
        final InventoryManager this$0;

        public void onData(SLInventoryEntry slinventoryentry)
        {
            if (slinventoryentry != null)
            {
                Debug.Printf("Inventory: folder subscription got name: %s with folderId = '%s'", new Object[] {
                    slinventoryentry.name, slinventoryentry.uuid
                });
            }
            InventoryManager._2D_get1(InventoryManager.this).onResultData(query, query.query(slinventoryentry, InventoryManager._2D_get3(InventoryManager.this)));
        }

        public volatile void onData(Object obj)
        {
            onData((SLInventoryEntry)obj);
        }

        public void onError(Throwable throwable)
        {
            Debug.Printf("Inventory: subscription error: %s", new Object[] {
                throwable
            });
            Debug.Warning(throwable);
            InventoryManager._2D_get1(InventoryManager.this).onResultError(query, throwable);
        }

        public void unsubscribe()
        {
            subscription.unsubscribe();
        }

        private FolderSubscription(InventoryQuery inventoryquery, UUID uuid)
        {
            this$0 = InventoryManager.this;
            super();
            query = inventoryquery;
            Debug.Printf("Inventory: folder subscription: folderId = '%s'", new Object[] {
                uuid
            });
            subscription = InventoryManager._2D_get2(InventoryManager.this).subscribe(uuid, InventoryManager._2D_get4(InventoryManager.this), this, this);
        }

        FolderSubscription(InventoryQuery inventoryquery, UUID uuid, FolderSubscription foldersubscription)
        {
            this(inventoryquery, uuid);
        }
    }

    public static class InventoryClipboardEntry
    {

        public final SLInventoryEntry inventoryEntry;
        public final boolean isCut;

        public InventoryClipboardEntry(boolean flag, SLInventoryEntry slinventoryentry)
        {
            isCut = flag;
            inventoryEntry = slinventoryentry;
        }
    }


    private final SubscriptionSingleDataPool clipboardPool = new SubscriptionSingleDataPool();
    private final AtomicReference currentSessionID = new AtomicReference(null);
    private final SubscriptionPool entryListPool = new SubscriptionPool();
    private final SubscriptionPool folderEntryPool = new SubscriptionPool();
    private final SubscriptionPool folderLoadingPool = new SubscriptionPool();
    private final RequestProcessor folderRequestProcessor;
    private final InventoryDB inventoryDB;
    private final OpportunisticExecutor inventoryDbExecutor = new OpportunisticExecutor("InventoryDB");
    private final RequestHandler queryRequestHandler = new RequestHandler() {

        private final Map folderQueries = new ConcurrentHashMap();
        final InventoryManager this$0;

        public void onRequest(InventoryQuery inventoryquery)
        {
            if (inventoryquery.containsString() != null)
            {
                InventoryManager._2D_get1(InventoryManager.this).onResultData(inventoryquery, inventoryquery.query(null, InventoryManager._2D_get3(InventoryManager.this)));
            } else
            {
                UUID uuid1 = inventoryquery.folderId();
                Object obj = uuid1;
                if (uuid1 == null)
                {
                    obj = (UUID)InventoryManager._2D_get5(InventoryManager.this).get();
                }
                Debug.Printf("Inventory: queryRequestHandler: folderId = '%s'", new Object[] {
                    obj
                });
                if (obj != null)
                {
                    obj = new FolderSubscription(inventoryquery, ((UUID) (obj)), null);
                    inventoryquery = (FolderSubscription)folderQueries.put(inventoryquery, obj);
                    if (inventoryquery != null)
                    {
                        inventoryquery.unsubscribe();
                        return;
                    }
                }
            }
        }

        public volatile void onRequest(Object obj)
        {
            onRequest((InventoryQuery)obj);
        }

        public void onRequestCancelled(InventoryQuery inventoryquery)
        {
            inventoryquery = (FolderSubscription)folderQueries.get(inventoryquery);
            if (inventoryquery != null)
            {
                inventoryquery.unsubscribe();
            }
        }

        public volatile void onRequestCancelled(Object obj)
        {
            onRequestCancelled((InventoryQuery)obj);
        }

            
            {
                this$0 = InventoryManager.this;
                super();
            }
    };
    private final AtomicReference rootFolderID = new AtomicReference(null);
    private final SubscriptionPool searchProcessPool = new SubscriptionPool();
    private final SubscriptionPool searchRunningPool = new SubscriptionPool();

    static void _2D_com_lumiyaviewer_lumiya_slproto_users_manager_InventoryManager_2D_mthref_2D_0(InventoryEntryList inventoryentrylist)
    {
        inventoryentrylist.close();
    }

    static AtomicReference _2D_get0(InventoryManager inventorymanager)
    {
        return inventorymanager.currentSessionID;
    }

    static SubscriptionPool _2D_get1(InventoryManager inventorymanager)
    {
        return inventorymanager.entryListPool;
    }

    static SubscriptionPool _2D_get2(InventoryManager inventorymanager)
    {
        return inventorymanager.folderEntryPool;
    }

    static InventoryDB _2D_get3(InventoryManager inventorymanager)
    {
        return inventorymanager.inventoryDB;
    }

    static OpportunisticExecutor _2D_get4(InventoryManager inventorymanager)
    {
        return inventorymanager.inventoryDbExecutor;
    }

    static AtomicReference _2D_get5(InventoryManager inventorymanager)
    {
        return inventorymanager.rootFolderID;
    }

    static void _2D_wrap0(InventoryManager inventorymanager)
    {
        inventorymanager.updateSearchResults();
    }

    public InventoryManager(UUID uuid)
    {
        uuid = InventoryDBManager.getUserInventoryDB(uuid);
        if (uuid == null)
        {
            throw new IllegalArgumentException("Null inventory database");
        } else
        {
            inventoryDB = uuid;
            folderRequestProcessor = new RequestProcessor(inventoryDbExecutor, uuid) {

                final InventoryManager this$0;
                final InventoryDB val$inventoryDB;

                protected volatile boolean isRequestComplete(Object obj, Object obj1)
                {
                    return isRequestComplete((UUID)obj, (SLInventoryEntry)obj1);
                }

                protected boolean isRequestComplete(UUID uuid1, SLInventoryEntry slinventoryentry)
                {
                    return slinventoryentry != null && Objects.equal(slinventoryentry.sessionID, InventoryManager._2D_get0(InventoryManager.this).get());
                }

                protected SLInventoryEntry processRequest(UUID uuid1)
                {
                    return inventoryDB.findEntry(uuid1);
                }

                protected volatile Object processRequest(Object obj)
                {
                    return processRequest((UUID)obj);
                }

                protected SLInventoryEntry processResult(UUID uuid1, SLInventoryEntry slinventoryentry)
                {
                    if (slinventoryentry != null)
                    {
                        Debug.Printf("Inventory: entry subscription got name: %s with folderId = '%s'", new Object[] {
                            slinventoryentry.name, slinventoryentry.uuid
                        });
                    }
                    InventoryManager._2D_wrap0(InventoryManager.this);
                    return slinventoryentry;
                }

                protected volatile Object processResult(Object obj, Object obj1)
                {
                    return processResult((UUID)obj, (SLInventoryEntry)obj1);
                }

            
            {
                this$0 = InventoryManager.this;
                inventoryDB = inventorydb;
                super(final_requestsource, executor);
            }
            };
            folderEntryPool.setCacheInvalidateHandler(new _2D_.Lambda.JIBenvPHaOomPgMJhTFPuiVXBzY._cls2(uuid), inventoryDbExecutor);
            entryListPool.attachRequestHandler(new AsyncRequestHandler(inventoryDbExecutor, queryRequestHandler));
            entryListPool.setDisposeHandler(new _2D_.Lambda.JIBenvPHaOomPgMJhTFPuiVXBzY._cls1(), inventoryDbExecutor);
            return;
        }
    }

    static void lambda$_2D_com_lumiyaviewer_lumiya_slproto_users_manager_InventoryManager_3450(InventoryDB inventorydb, UUID uuid)
    {
        uuid = inventorydb.findEntry(uuid);
        if (uuid == null)
        {
            break MISSING_BLOCK_LABEL_20;
        }
        uuid.sessionID = null;
        inventorydb.saveEntry(uuid);
        return;
        inventorydb;
        Debug.Warning(inventorydb);
        return;
    }

    static boolean lambda$_2D_com_lumiyaviewer_lumiya_slproto_users_manager_InventoryManager_6838(InventoryQuery inventoryquery)
    {
        if (inventoryquery != null)
        {
            return Strings.isNullOrEmpty(inventoryquery.containsString()) ^ true;
        } else
        {
            return false;
        }
    }

    private void updateSearchResults()
    {
        entryListPool.requestUpdateSome(new _2D_.Lambda.JIBenvPHaOomPgMJhTFPuiVXBzY());
    }

    public void copyToClipboard(InventoryClipboardEntry inventoryclipboardentry)
    {
        clipboardPool.setData(SubscriptionSingleKey.Value, inventoryclipboardentry);
    }

    public Subscribable getClipboard()
    {
        return clipboardPool;
    }

    public InventoryDB getDatabase()
    {
        return inventoryDB;
    }

    public Executor getExecutor()
    {
        return inventoryDbExecutor;
    }

    public Subscribable getFolderEntryPool()
    {
        return folderEntryPool;
    }

    public Subscribable getFolderLoading()
    {
        return folderLoadingPool;
    }

    public RequestSource getFolderLoadingRequestSource()
    {
        return folderLoadingPool;
    }

    public RequestSource getFolderRequestSource()
    {
        return folderRequestProcessor;
    }

    public Subscribable getInventoryEntries()
    {
        return entryListPool;
    }

    public UUID getRootFolder()
    {
        return (UUID)rootFolderID.get();
    }

    public Subscribable getSearchProcess()
    {
        return searchProcessPool;
    }

    public RequestSource getSearchProcessRequestSource()
    {
        return searchProcessPool;
    }

    public SubscriptionPool getSearchRunning()
    {
        return searchRunningPool;
    }

    public void requestFolderUpdate(UUID uuid)
    {
        folderEntryPool.requestUpdate(uuid);
    }

    public void setCurrentSessionID(UUID uuid)
    {
        currentSessionID.set(uuid);
    }

    public void setRootFolder(UUID uuid)
    {
        rootFolderID.set(uuid);
    }
}
