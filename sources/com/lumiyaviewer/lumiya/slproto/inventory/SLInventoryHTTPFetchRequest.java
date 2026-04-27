// Decompiled by Jad v1.5.8e. Copyright 2001 Pavel Kouznetsov.
// Jad home page: http://www.geocities.com/kpdus/jad.html
// Decompiler options: braces fieldsfirst space lnc 

package com.lumiyaviewer.lumiya.slproto.inventory;

import android.database.sqlite.SQLiteStatement;
import com.lumiyaviewer.lumiya.Debug;
import com.lumiyaviewer.lumiya.orm.InventoryDB;
import com.lumiyaviewer.lumiya.slproto.https.GenericHTTPExecutor;
import com.lumiyaviewer.lumiya.slproto.https.LLSDStreamingXMLRequest;
import com.lumiyaviewer.lumiya.slproto.llsd.LLSDNode;
import com.lumiyaviewer.lumiya.slproto.llsd.LLSDValueTypeException;
import com.lumiyaviewer.lumiya.slproto.llsd.LLSDXMLException;
import com.lumiyaviewer.lumiya.slproto.llsd.types.LLSDArray;
import com.lumiyaviewer.lumiya.slproto.llsd.types.LLSDBoolean;
import com.lumiyaviewer.lumiya.slproto.llsd.types.LLSDMap;
import com.lumiyaviewer.lumiya.slproto.llsd.types.LLSDUUID;
import java.io.IOException;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Future;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicReference;

// Referenced classes of package com.lumiyaviewer.lumiya.slproto.inventory:
//            SLInventoryFetchRequest, SLInventory, SLInventoryEntry, SLAssetType, 
//            SLInventoryType, SLSaleType

class SLInventoryHTTPFetchRequest extends SLInventoryFetchRequest
{
    private final class DatabaseCommitThread extends Thread
    {

        private volatile boolean aborted;
        private final BlockingQueue commitEntryQueue;
        private final SLInventoryEntry stopEntry;
        final SLInventoryHTTPFetchRequest this$0;

        void addEntry(SLInventoryEntry slinventoryentry)
            throws InterruptedException
        {
            commitEntryQueue.put(slinventoryentry);
        }

        public void run()
        {
            SQLiteStatement sqlitestatement;
            Object obj1;
            HashSet hashset;
            boolean flag;
            int i;
            sqlitestatement = null;
            hashset = new HashSet();
            obj1 = null;
            flag = false;
            i = 0;
_L6:
            SQLiteStatement sqlitestatement1;
            SQLiteStatement sqlitestatement2;
            boolean flag1;
            boolean flag2;
            boolean flag3;
            flag3 = flag;
            sqlitestatement1 = ((SQLiteStatement) (obj1));
            flag1 = flag;
            sqlitestatement2 = ((SQLiteStatement) (obj1));
            flag2 = flag;
            if (Thread.interrupted()) goto _L2; else goto _L1
_L1:
            sqlitestatement1 = ((SQLiteStatement) (obj1));
            flag1 = flag;
            sqlitestatement2 = ((SQLiteStatement) (obj1));
            flag2 = flag;
            SLInventoryEntry slinventoryentry = (SLInventoryEntry)commitEntryQueue.poll();
            if (slinventoryentry != null)
            {
                break MISSING_BLOCK_LABEL_656;
            }
            flag3 = flag;
            if (!flag)
            {
                break MISSING_BLOCK_LABEL_139;
            }
            sqlitestatement1 = ((SQLiteStatement) (obj1));
            flag1 = flag;
            sqlitestatement2 = ((SQLiteStatement) (obj1));
            flag2 = flag;
            db.setTransactionSuccessful();
            sqlitestatement1 = ((SQLiteStatement) (obj1));
            flag1 = flag;
            sqlitestatement2 = ((SQLiteStatement) (obj1));
            flag2 = flag;
            db.endTransaction();
            flag3 = false;
            i = 0;
            sqlitestatement1 = ((SQLiteStatement) (obj1));
            flag1 = flag3;
            sqlitestatement2 = ((SQLiteStatement) (obj1));
            flag2 = flag3;
            slinventoryentry = (SLInventoryEntry)commitEntryQueue.take();
_L12:
            sqlitestatement1 = ((SQLiteStatement) (obj1));
            flag1 = flag3;
            sqlitestatement2 = ((SQLiteStatement) (obj1));
            flag2 = flag3;
            if (slinventoryentry != stopEntry) goto _L3; else goto _L2
_L2:
            sqlitestatement1 = ((SQLiteStatement) (obj1));
            flag1 = flag3;
            sqlitestatement2 = ((SQLiteStatement) (obj1));
            flag2 = flag3;
            boolean flag4 = Thread.interrupted();
            if (!flag4)
            {
                flag = true;
            } else
            {
                flag = false;
            }
            if (flag3)
            {
                Object obj;
                InterruptedException interruptedexception;
                if (flag)
                {
                    obj = "true";
                } else
                {
                    obj = "false";
                }
                Debug.Printf("InvFetch: commit thread ending transaction (success: %s, count %d).", new Object[] {
                    obj, Integer.valueOf(hashset.size())
                });
                db.setTransactionSuccessful();
                db.endTransaction();
            }
            if (obj1 != null)
            {
                ((SQLiteStatement) (obj1)).close();
            }
            if (sqlitestatement != null)
            {
                sqlitestatement.close();
            }
            if (flag && aborted ^ true)
            {
                Debug.Printf("InvFetch: commit thread successful, calling retainChildren.", new Object[0]);
                db.retainChildren(folderId, hashset);
            }
            return;
_L3:
            flag = flag3;
            if (flag3)
            {
                break MISSING_BLOCK_LABEL_371;
            }
            sqlitestatement1 = ((SQLiteStatement) (obj1));
            flag1 = flag3;
            sqlitestatement2 = ((SQLiteStatement) (obj1));
            flag2 = flag3;
            db.beginTransaction();
            flag = true;
            i++;
            if (i < 16)
            {
                break MISSING_BLOCK_LABEL_411;
            }
            sqlitestatement1 = ((SQLiteStatement) (obj1));
            flag1 = flag;
            sqlitestatement2 = ((SQLiteStatement) (obj1));
            flag2 = flag;
            db.yieldIfContendedSafely();
            i = 0;
            sqlitestatement1 = ((SQLiteStatement) (obj1));
            flag1 = flag;
            sqlitestatement2 = ((SQLiteStatement) (obj1));
            flag2 = flag;
            hashset.add(slinventoryentry.uuid);
            sqlitestatement1 = ((SQLiteStatement) (obj1));
            flag1 = flag;
            sqlitestatement2 = ((SQLiteStatement) (obj1));
            flag2 = flag;
            if (android.os.Build.VERSION.SDK_INT < 11) goto _L5; else goto _L4
_L4:
            obj = obj1;
            if (obj1 != null)
            {
                break MISSING_BLOCK_LABEL_494;
            }
            sqlitestatement1 = ((SQLiteStatement) (obj1));
            flag1 = flag;
            sqlitestatement2 = ((SQLiteStatement) (obj1));
            flag2 = flag;
            obj = SLInventoryEntry.getInsertStatement(db.getDatabase());
            if (sqlitestatement != null)
            {
                break MISSING_BLOCK_LABEL_528;
            }
            sqlitestatement1 = ((SQLiteStatement) (obj));
            flag1 = flag;
            sqlitestatement2 = ((SQLiteStatement) (obj));
            flag2 = flag;
            obj1 = SLInventoryEntry.getUpdateStatement(db.getDatabase());
            sqlitestatement = ((SQLiteStatement) (obj1));
            slinventoryentry.updateOrInsert(sqlitestatement, ((SQLiteStatement) (obj)));
_L7:
            obj1 = obj;
              goto _L6
_L5:
            sqlitestatement1 = ((SQLiteStatement) (obj1));
            flag1 = flag;
            sqlitestatement2 = ((SQLiteStatement) (obj1));
            flag2 = flag;
            slinventoryentry.updateOrInsert(db.getDatabase());
            obj = obj1;
              goto _L7
            obj1;
            flag = flag1;
            obj = sqlitestatement1;
_L11:
            Debug.Warning(((Throwable) (obj1)));
            flag1 = false;
            obj1 = obj;
            flag3 = flag;
            flag = flag1;
            break MISSING_BLOCK_LABEL_217;
            interruptedexception;
            flag = flag2;
            obj = sqlitestatement2;
_L9:
            Debug.Warning(interruptedexception);
            flag1 = false;
            interruptedexception = ((InterruptedException) (obj));
            flag3 = flag;
            flag = flag1;
            break MISSING_BLOCK_LABEL_217;
            interruptedexception;
            if (true) goto _L9; else goto _L8
_L8:
            interruptedexception;
            if (true) goto _L11; else goto _L10
_L10:
            flag3 = flag;
              goto _L12
        }

        void stopAndWait(boolean flag)
            throws InterruptedException
        {
            if (!flag)
            {
                aborted = true;
            }
            commitEntryQueue.put(stopEntry);
            join();
        }

        private DatabaseCommitThread()
        {
            this$0 = SLInventoryHTTPFetchRequest.this;
            super();
            commitEntryQueue = new LinkedBlockingQueue(100);
            stopEntry = new SLInventoryEntry();
            aborted = false;
        }

        DatabaseCommitThread(DatabaseCommitThread databasecommitthread)
        {
            this();
        }
    }

    private class FolderDataContentHandler extends com.lumiyaviewer.lumiya.slproto.llsd.LLSDStreamingParser.LLSDDefaultContentHandler
    {

        private final DatabaseCommitThread commitThread;
        private UUID gotUUID;
        private int gotVersion;
        final SLInventoryHTTPFetchRequest this$0;

        static DatabaseCommitThread _2D_get0(FolderDataContentHandler folderdatacontenthandler)
        {
            return folderdatacontenthandler.commitThread;
        }

        public com.lumiyaviewer.lumiya.slproto.llsd.LLSDStreamingParser.LLSDContentHandler onArrayBegin(String s)
            throws LLSDXMLException
        {
            if (s.equals("categories"))
            {
                return new com.lumiyaviewer.lumiya.slproto.llsd.LLSDStreamingParser.LLSDDefaultContentHandler() {

                    final FolderDataContentHandler this$1;

                    public com.lumiyaviewer.lumiya.slproto.llsd.LLSDStreamingParser.LLSDContentHandler onMapBegin(String s)
                        throws LLSDXMLException
                    {
                        return new FolderEntryContentHandler(FolderDataContentHandler._2D_get0(FolderDataContentHandler.this));
                    }

            
            {
                this$1 = FolderDataContentHandler.this;
                super();
            }
                };
            }
            if (s.equals("items"))
            {
                return new com.lumiyaviewer.lumiya.slproto.llsd.LLSDStreamingParser.LLSDDefaultContentHandler() {

                    final FolderDataContentHandler this$1;

                    public com.lumiyaviewer.lumiya.slproto.llsd.LLSDStreamingParser.LLSDContentHandler onMapBegin(String s)
                        throws LLSDXMLException
                    {
                        return new ItemEntryContentHandler(FolderDataContentHandler._2D_get0(FolderDataContentHandler.this));
                    }

            
            {
                this$1 = FolderDataContentHandler.this;
                super();
            }
                };
            } else
            {
                return super.onArrayBegin(s);
            }
        }

        public void onMapEnd(String s)
            throws LLSDXMLException, InterruptedException
        {
            if (gotUUID != null && gotUUID.equals(folderUUID) && gotVersion != folderEntry.version)
            {
                folderEntry.version = gotVersion;
                commitThread.addEntry(folderEntry);
            }
        }

        public void onPrimitiveValue(String s, LLSDNode llsdnode)
            throws LLSDXMLException, LLSDValueTypeException
        {
            Debug.Printf("InvFetch: FolderDataContentHandler: key '%s' value '%s'", new Object[] {
                s, llsdnode
            });
            if (s.equals("version"))
            {
                gotVersion = llsdnode.asInt();
            } else
            if (s.equals("folder_id"))
            {
                gotUUID = llsdnode.asUUID();
                return;
            }
        }

        private FolderDataContentHandler(DatabaseCommitThread databasecommitthread)
        {
            this$0 = SLInventoryHTTPFetchRequest.this;
            super();
            commitThread = databasecommitthread;
        }

        FolderDataContentHandler(DatabaseCommitThread databasecommitthread, FolderDataContentHandler folderdatacontenthandler)
        {
            this(databasecommitthread);
        }
    }

    private class FolderEntryContentHandler extends com.lumiyaviewer.lumiya.slproto.llsd.LLSDStreamingParser.LLSDDefaultContentHandler
    {

        private static final int _2D_com_2D_lumiyaviewer_2D_lumiya_2D_slproto_2D_inventory_2D_SLInventoryHTTPFetchRequest$FolderValueKeySwitchesValues[];
        final int $SWITCH_TABLE$com$lumiyaviewer$lumiya$slproto$inventory$SLInventoryHTTPFetchRequest$FolderValueKey[];
        private final DatabaseCommitThread commitThread;
        private final SLInventoryEntry entry = new SLInventoryEntry();
        final SLInventoryHTTPFetchRequest this$0;

        private static int[] _2D_getcom_2D_lumiyaviewer_2D_lumiya_2D_slproto_2D_inventory_2D_SLInventoryHTTPFetchRequest$FolderValueKeySwitchesValues()
        {
            if (_2D_com_2D_lumiyaviewer_2D_lumiya_2D_slproto_2D_inventory_2D_SLInventoryHTTPFetchRequest$FolderValueKeySwitchesValues != null)
            {
                return _2D_com_2D_lumiyaviewer_2D_lumiya_2D_slproto_2D_inventory_2D_SLInventoryHTTPFetchRequest$FolderValueKeySwitchesValues;
            }
            int ai[] = new int[FolderValueKey.values().length];
            try
            {
                ai[FolderValueKey.agent_id.ordinal()] = 1;
            }
            catch (NoSuchFieldError nosuchfielderror8) { }
            try
            {
                ai[FolderValueKey.category_id.ordinal()] = 2;
            }
            catch (NoSuchFieldError nosuchfielderror7) { }
            try
            {
                ai[FolderValueKey.folder_id.ordinal()] = 3;
            }
            catch (NoSuchFieldError nosuchfielderror6) { }
            try
            {
                ai[FolderValueKey.name.ordinal()] = 4;
            }
            catch (NoSuchFieldError nosuchfielderror5) { }
            try
            {
                ai[FolderValueKey.parent_id.ordinal()] = 5;
            }
            catch (NoSuchFieldError nosuchfielderror4) { }
            try
            {
                ai[FolderValueKey.preferred_type.ordinal()] = 6;
            }
            catch (NoSuchFieldError nosuchfielderror3) { }
            try
            {
                ai[FolderValueKey.type.ordinal()] = 7;
            }
            catch (NoSuchFieldError nosuchfielderror2) { }
            try
            {
                ai[FolderValueKey.type_default.ordinal()] = 8;
            }
            catch (NoSuchFieldError nosuchfielderror1) { }
            try
            {
                ai[FolderValueKey.version.ordinal()] = 9;
            }
            catch (NoSuchFieldError nosuchfielderror) { }
            _2D_com_2D_lumiyaviewer_2D_lumiya_2D_slproto_2D_inventory_2D_SLInventoryHTTPFetchRequest$FolderValueKeySwitchesValues = ai;
            return ai;
        }

        public void onMapEnd(String s)
            throws LLSDXMLException, InterruptedException
        {
            if (entry.parentUUID == null)
            {
                entry.parentUUID = folderEntry.parentUUID;
                entry.parent_id = folderEntry.getId();
            }
            if (entry.agentUUID == null)
            {
                entry.agentUUID = folderEntry.agentUUID;
            }
            commitThread.addEntry(entry);
        }

        public void onPrimitiveValue(String s, LLSDNode llsdnode)
            throws LLSDXMLException, LLSDValueTypeException
        {
            FolderValueKey foldervaluekey = FolderValueKey.byTag(s);
            if (foldervaluekey != null)
            {
                switch (_2D_getcom_2D_lumiyaviewer_2D_lumiya_2D_slproto_2D_inventory_2D_SLInventoryHTTPFetchRequest$FolderValueKeySwitchesValues()[foldervaluekey.ordinal()])
                {
                case 6: // '\006'
                default:
                    return;

                case 1: // '\001'
                    entry.agentUUID = llsdnode.asUUID();
                    return;

                case 2: // '\002'
                    entry.uuid = llsdnode.asUUID();
                    return;

                case 3: // '\003'
                    entry.uuid = llsdnode.asUUID();
                    return;

                case 4: // '\004'
                    entry.name = llsdnode.asString();
                    return;

                case 5: // '\005'
                    entry.parentUUID = llsdnode.asUUID();
                    if (entry.parentUUID.equals(folderUUID))
                    {
                        entry.parent_id = folderEntry.getId();
                        return;
                    }
                    s = db.findEntry(entry.parentUUID);
                    if (s != null)
                    {
                        entry.parent_id = s.getId();
                        return;
                    } else
                    {
                        entry.parent_id = 0L;
                        return;
                    }

                case 7: // '\007'
                    if (llsdnode.isInt())
                    {
                        entry.typeDefault = llsdnode.asInt();
                        return;
                    }
                    s = SLAssetType.getByString(llsdnode.asString());
                    if (s != SLAssetType.AT_UNKNOWN)
                    {
                        entry.typeDefault = s.getInventoryType().getTypeCode();
                        return;
                    } else
                    {
                        entry.typeDefault = SLInventoryType.getByString(llsdnode.asString()).getTypeCode();
                        return;
                    }

                case 8: // '\b'
                    entry.typeDefault = llsdnode.asInt();
                    return;

                case 9: // '\t'
                    entry.version = llsdnode.asInt();
                    return;
                }
            } else
            {
                Debug.Printf("InvFetch: Folder unknown key '%s'", new Object[] {
                    s
                });
                return;
            }
        }

        FolderEntryContentHandler(DatabaseCommitThread databasecommitthread)
        {
            this$0 = SLInventoryHTTPFetchRequest.this;
            super();
            commitThread = databasecommitthread;
            entry.isFolder = true;
        }
    }

    private static final class FolderValueKey extends Enum
    {

        private static final FolderValueKey $VALUES[];
        public static final FolderValueKey agent_id;
        public static final FolderValueKey category_id;
        public static final FolderValueKey folder_id;
        public static final FolderValueKey name;
        public static final FolderValueKey parent_id;
        public static final FolderValueKey preferred_type;
        private static final Map tagMap;
        public static final FolderValueKey type;
        public static final FolderValueKey type_default;
        public static final FolderValueKey version;

        public static FolderValueKey byTag(String s)
        {
            return (FolderValueKey)tagMap.get(s);
        }

        public static FolderValueKey valueOf(String s)
        {
            return (FolderValueKey)Enum.valueOf(com/lumiyaviewer/lumiya/slproto/inventory/SLInventoryHTTPFetchRequest$FolderValueKey, s);
        }

        public static FolderValueKey[] values()
        {
            return $VALUES;
        }

        static 
        {
            int i = 0;
            category_id = new FolderValueKey("category_id", 0);
            folder_id = new FolderValueKey("folder_id", 1);
            agent_id = new FolderValueKey("agent_id", 2);
            name = new FolderValueKey("name", 3);
            type_default = new FolderValueKey("type_default", 4);
            type = new FolderValueKey("type", 5);
            version = new FolderValueKey("version", 6);
            parent_id = new FolderValueKey("parent_id", 7);
            preferred_type = new FolderValueKey("preferred_type", 8);
            $VALUES = (new FolderValueKey[] {
                category_id, folder_id, agent_id, name, type_default, type, version, parent_id, preferred_type
            });
            tagMap = new HashMap(values().length * 2);
            FolderValueKey afoldervaluekey[] = values();
            for (int j = afoldervaluekey.length; i < j; i++)
            {
                FolderValueKey foldervaluekey = afoldervaluekey[i];
                tagMap.put(foldervaluekey.toString(), foldervaluekey);
            }

        }

        private FolderValueKey(String s, int i)
        {
            super(s, i);
        }
    }

    private class ItemEntryContentHandler extends com.lumiyaviewer.lumiya.slproto.llsd.LLSDStreamingParser.LLSDDefaultContentHandler
    {

        private static final int _2D_com_2D_lumiyaviewer_2D_lumiya_2D_slproto_2D_inventory_2D_SLInventoryHTTPFetchRequest$ItemValueKeySwitchesValues[];
        final int $SWITCH_TABLE$com$lumiyaviewer$lumiya$slproto$inventory$SLInventoryHTTPFetchRequest$ItemValueKey[];
        private final DatabaseCommitThread commitThread;
        private final SLInventoryEntry entry = new SLInventoryEntry();
        private final com.lumiyaviewer.lumiya.slproto.llsd.LLSDStreamingParser.LLSDContentHandler permissionsHandler = new _cls1();
        private final com.lumiyaviewer.lumiya.slproto.llsd.LLSDStreamingParser.LLSDContentHandler saleInfoHandler = new _cls2();
        final SLInventoryHTTPFetchRequest this$0;

        static SLInventoryEntry _2D_get0(ItemEntryContentHandler itementrycontenthandler)
        {
            return itementrycontenthandler.entry;
        }

        private static int[] _2D_getcom_2D_lumiyaviewer_2D_lumiya_2D_slproto_2D_inventory_2D_SLInventoryHTTPFetchRequest$ItemValueKeySwitchesValues()
        {
            if (_2D_com_2D_lumiyaviewer_2D_lumiya_2D_slproto_2D_inventory_2D_SLInventoryHTTPFetchRequest$ItemValueKeySwitchesValues != null)
            {
                return _2D_com_2D_lumiyaviewer_2D_lumiya_2D_slproto_2D_inventory_2D_SLInventoryHTTPFetchRequest$ItemValueKeySwitchesValues;
            }
            int ai[] = new int[ItemValueKey.values().length];
            try
            {
                ai[ItemValueKey.agent_id.ordinal()] = 1;
            }
            catch (NoSuchFieldError nosuchfielderror9) { }
            try
            {
                ai[ItemValueKey.asset_id.ordinal()] = 2;
            }
            catch (NoSuchFieldError nosuchfielderror8) { }
            try
            {
                ai[ItemValueKey.created_at.ordinal()] = 3;
            }
            catch (NoSuchFieldError nosuchfielderror7) { }
            try
            {
                ai[ItemValueKey.desc.ordinal()] = 4;
            }
            catch (NoSuchFieldError nosuchfielderror6) { }
            try
            {
                ai[ItemValueKey.flags.ordinal()] = 5;
            }
            catch (NoSuchFieldError nosuchfielderror5) { }
            try
            {
                ai[ItemValueKey.inv_type.ordinal()] = 6;
            }
            catch (NoSuchFieldError nosuchfielderror4) { }
            try
            {
                ai[ItemValueKey.item_id.ordinal()] = 7;
            }
            catch (NoSuchFieldError nosuchfielderror3) { }
            try
            {
                ai[ItemValueKey.name.ordinal()] = 8;
            }
            catch (NoSuchFieldError nosuchfielderror2) { }
            try
            {
                ai[ItemValueKey.parent_id.ordinal()] = 9;
            }
            catch (NoSuchFieldError nosuchfielderror1) { }
            try
            {
                ai[ItemValueKey.type.ordinal()] = 10;
            }
            catch (NoSuchFieldError nosuchfielderror) { }
            _2D_com_2D_lumiyaviewer_2D_lumiya_2D_slproto_2D_inventory_2D_SLInventoryHTTPFetchRequest$ItemValueKeySwitchesValues = ai;
            return ai;
        }

        public com.lumiyaviewer.lumiya.slproto.llsd.LLSDStreamingParser.LLSDContentHandler onMapBegin(String s)
            throws LLSDXMLException
        {
            if (s.equals("permissions"))
            {
                return permissionsHandler;
            }
            if (s.equals("sale_info"))
            {
                return saleInfoHandler;
            } else
            {
                return super.onMapBegin(s);
            }
        }

        public void onMapEnd(String s)
            throws LLSDXMLException, InterruptedException
        {
            if (entry.parentUUID == null)
            {
                entry.parentUUID = folderEntry.parentUUID;
                entry.parent_id = folderEntry.getId();
            }
            if (entry.agentUUID == null)
            {
                entry.agentUUID = folderEntry.agentUUID;
            }
            commitThread.addEntry(entry);
        }

        public void onPrimitiveValue(String s, LLSDNode llsdnode)
            throws LLSDXMLException, LLSDValueTypeException
        {
            ItemValueKey itemvaluekey = ItemValueKey.byTag(s);
            if (itemvaluekey != null)
            {
                switch (_2D_getcom_2D_lumiyaviewer_2D_lumiya_2D_slproto_2D_inventory_2D_SLInventoryHTTPFetchRequest$ItemValueKeySwitchesValues()[itemvaluekey.ordinal()])
                {
                default:
                    return;

                case 1: // '\001'
                    entry.agentUUID = llsdnode.asUUID();
                    return;

                case 7: // '\007'
                    entry.uuid = llsdnode.asUUID();
                    return;

                case 8: // '\b'
                    entry.name = llsdnode.asString();
                    return;

                case 2: // '\002'
                    entry.assetUUID = llsdnode.asUUID();
                    return;

                case 9: // '\t'
                    entry.parentUUID = llsdnode.asUUID();
                    if (entry.parentUUID.equals(folderUUID))
                    {
                        entry.parent_id = folderEntry.getId();
                        return;
                    }
                    s = db.findEntry(entry.parentUUID);
                    if (s != null)
                    {
                        entry.parent_id = s.getId();
                        return;
                    } else
                    {
                        entry.parent_id = 0L;
                        return;
                    }

                case 10: // '\n'
                    if (llsdnode.isInt())
                    {
                        entry.assetType = llsdnode.asInt();
                        return;
                    } else
                    {
                        entry.assetType = SLAssetType.getByString(llsdnode.asString()).getTypeCode();
                        return;
                    }

                case 6: // '\006'
                    if (llsdnode.isInt())
                    {
                        entry.invType = llsdnode.asInt();
                        return;
                    } else
                    {
                        entry.invType = SLInventoryType.getByString(llsdnode.asString()).getTypeCode();
                        return;
                    }

                case 4: // '\004'
                    entry.description = llsdnode.asString();
                    return;

                case 3: // '\003'
                    entry.creationDate = llsdnode.asInt();
                    return;

                case 5: // '\005'
                    entry.flags = llsdnode.asInt();
                    return;
                }
            } else
            {
                Debug.Printf("InvFetch: Item unknown key '%s'", new Object[] {
                    s
                });
                return;
            }
        }

        ItemEntryContentHandler(DatabaseCommitThread databasecommitthread)
        {
            this$0 = SLInventoryHTTPFetchRequest.this;
            super();
            commitThread = databasecommitthread;
            entry.isFolder = false;
        }
    }

    private static final class ItemValueKey extends Enum
    {

        private static final ItemValueKey $VALUES[];
        public static final ItemValueKey agent_id;
        public static final ItemValueKey asset_id;
        public static final ItemValueKey created_at;
        public static final ItemValueKey desc;
        public static final ItemValueKey flags;
        public static final ItemValueKey inv_type;
        public static final ItemValueKey item_id;
        public static final ItemValueKey name;
        public static final ItemValueKey parent_id;
        private static final Map tagMap;
        public static final ItemValueKey type;

        public static ItemValueKey byTag(String s)
        {
            return (ItemValueKey)tagMap.get(s);
        }

        public static ItemValueKey valueOf(String s)
        {
            return (ItemValueKey)Enum.valueOf(com/lumiyaviewer/lumiya/slproto/inventory/SLInventoryHTTPFetchRequest$ItemValueKey, s);
        }

        public static ItemValueKey[] values()
        {
            return $VALUES;
        }

        static 
        {
            int i = 0;
            item_id = new ItemValueKey("item_id", 0);
            name = new ItemValueKey("name", 1);
            parent_id = new ItemValueKey("parent_id", 2);
            agent_id = new ItemValueKey("agent_id", 3);
            type = new ItemValueKey("type", 4);
            inv_type = new ItemValueKey("inv_type", 5);
            desc = new ItemValueKey("desc", 6);
            flags = new ItemValueKey("flags", 7);
            created_at = new ItemValueKey("created_at", 8);
            asset_id = new ItemValueKey("asset_id", 9);
            $VALUES = (new ItemValueKey[] {
                item_id, name, parent_id, agent_id, type, inv_type, desc, flags, created_at, asset_id
            });
            tagMap = new HashMap(values().length * 2);
            ItemValueKey aitemvaluekey[] = values();
            for (int j = aitemvaluekey.length; i < j; i++)
            {
                ItemValueKey itemvaluekey = aitemvaluekey[i];
                tagMap.put(itemvaluekey.toString(), itemvaluekey);
            }

        }

        private ItemValueKey(String s, int i)
        {
            super(s, i);
        }
    }

    private static final class PermissionsValueKey extends Enum
    {

        private static final PermissionsValueKey $VALUES[];
        public static final PermissionsValueKey base_mask;
        public static final PermissionsValueKey creator_id;
        public static final PermissionsValueKey everyone_mask;
        public static final PermissionsValueKey group_id;
        public static final PermissionsValueKey group_mask;
        public static final PermissionsValueKey is_owner_group;
        public static final PermissionsValueKey last_owner_id;
        public static final PermissionsValueKey next_owner_mask;
        public static final PermissionsValueKey owner_id;
        public static final PermissionsValueKey owner_mask;
        private static final Map tagMap;

        public static PermissionsValueKey byTag(String s)
        {
            return (PermissionsValueKey)tagMap.get(s);
        }

        public static PermissionsValueKey valueOf(String s)
        {
            return (PermissionsValueKey)Enum.valueOf(com/lumiyaviewer/lumiya/slproto/inventory/SLInventoryHTTPFetchRequest$PermissionsValueKey, s);
        }

        public static PermissionsValueKey[] values()
        {
            return $VALUES;
        }

        static 
        {
            int i = 0;
            creator_id = new PermissionsValueKey("creator_id", 0);
            group_id = new PermissionsValueKey("group_id", 1);
            owner_id = new PermissionsValueKey("owner_id", 2);
            last_owner_id = new PermissionsValueKey("last_owner_id", 3);
            is_owner_group = new PermissionsValueKey("is_owner_group", 4);
            base_mask = new PermissionsValueKey("base_mask", 5);
            owner_mask = new PermissionsValueKey("owner_mask", 6);
            next_owner_mask = new PermissionsValueKey("next_owner_mask", 7);
            group_mask = new PermissionsValueKey("group_mask", 8);
            everyone_mask = new PermissionsValueKey("everyone_mask", 9);
            $VALUES = (new PermissionsValueKey[] {
                creator_id, group_id, owner_id, last_owner_id, is_owner_group, base_mask, owner_mask, next_owner_mask, group_mask, everyone_mask
            });
            tagMap = new HashMap(values().length * 2);
            PermissionsValueKey apermissionsvaluekey[] = values();
            for (int j = apermissionsvaluekey.length; i < j; i++)
            {
                PermissionsValueKey permissionsvaluekey = apermissionsvaluekey[i];
                tagMap.put(permissionsvaluekey.toString(), permissionsvaluekey);
            }

        }

        private PermissionsValueKey(String s, int i)
        {
            super(s, i);
        }
    }

    private class RootContentHandler extends com.lumiyaviewer.lumiya.slproto.llsd.LLSDStreamingParser.LLSDDefaultContentHandler
    {

        private final DatabaseCommitThread commitThread;
        final SLInventoryHTTPFetchRequest this$0;

        static DatabaseCommitThread _2D_get0(RootContentHandler rootcontenthandler)
        {
            return rootcontenthandler.commitThread;
        }

        public com.lumiyaviewer.lumiya.slproto.llsd.LLSDStreamingParser.LLSDContentHandler onMapBegin(String s)
            throws LLSDXMLException
        {
            return new com.lumiyaviewer.lumiya.slproto.llsd.LLSDStreamingParser.LLSDDefaultContentHandler() {

                final RootContentHandler this$1;

                public com.lumiyaviewer.lumiya.slproto.llsd.LLSDStreamingParser.LLSDContentHandler onArrayBegin(String s)
                    throws LLSDXMLException
                {
                    if (s.equals("folders"))
                    {
                        return new com.lumiyaviewer.lumiya.slproto.llsd.LLSDStreamingParser.LLSDDefaultContentHandler() {

                            final RootContentHandler._cls1 this$2;

                            public com.lumiyaviewer.lumiya.slproto.llsd.LLSDStreamingParser.LLSDContentHandler onMapBegin(String s)
                                throws LLSDXMLException
                            {
                                return new FolderDataContentHandler(RootContentHandler._2D_get0(_fld1), null);
                            }

            
            {
                this$2 = RootContentHandler._cls1.this;
                super();
            }
                        };
                    } else
                    {
                        return super.onArrayBegin(s);
                    }
                }

            
            {
                this$1 = RootContentHandler.this;
                super();
            }
            };
        }

        private RootContentHandler(DatabaseCommitThread databasecommitthread)
        {
            this$0 = SLInventoryHTTPFetchRequest.this;
            super();
            commitThread = databasecommitthread;
        }

        RootContentHandler(DatabaseCommitThread databasecommitthread, RootContentHandler rootcontenthandler)
        {
            this(databasecommitthread);
        }
    }


    private final String capURL;
    private final AtomicReference futureRef = new AtomicReference(null);
    private final Runnable httpRequest = new Runnable() {

        final SLInventoryHTTPFetchRequest this$0;

        public void run()
        {
            Object obj;
            Object obj1;
            long l;
            l = System.currentTimeMillis();
            Debug.Printf("InventoryFetcher: Going to fetch folder: %s", new Object[] {
                folderUUID
            });
            obj = new LLSDStreamingXMLRequest();
            obj1 = new LLSDArray();
            ((LLSDArray) (obj1)).add(new LLSDMap(new com.lumiyaviewer.lumiya.slproto.llsd.types.LLSDMap.LLSDMapEntry[] {
                new com.lumiyaviewer.lumiya.slproto.llsd.types.LLSDMap.LLSDMapEntry("folder_id", new LLSDUUID(folderUUID)), new com.lumiyaviewer.lumiya.slproto.llsd.types.LLSDMap.LLSDMapEntry("fetch_folders", new LLSDBoolean(true)), new com.lumiyaviewer.lumiya.slproto.llsd.types.LLSDMap.LLSDMapEntry("fetch_items", new LLSDBoolean(true))
            }));
            obj1 = new LLSDMap(new com.lumiyaviewer.lumiya.slproto.llsd.types.LLSDMap.LLSDMapEntry[] {
                new com.lumiyaviewer.lumiya.slproto.llsd.types.LLSDMap.LLSDMapEntry("folders", ((LLSDNode) (obj1)))
            });
            SLInventoryHTTPFetchRequest._2D_get2(SLInventoryHTTPFetchRequest.this).set(obj);
            int i;
            boolean flag2;
            i = 0;
            flag2 = false;
_L9:
            boolean flag = flag2;
            if (i >= 3) goto _L2; else goto _L1
_L1:
            boolean flag1 = flag2;
            DatabaseCommitThread databasecommitthread = new DatabaseCommitThread(null);
            flag1 = flag2;
            databasecommitthread.start();
            flag1 = flag2;
            Debug.Printf("InventoryFetcher: Starting HTTP request for folder: %s", new Object[] {
                folderUUID
            });
            flag1 = flag2;
            ((LLSDStreamingXMLRequest) (obj)).PerformRequest(SLInventoryHTTPFetchRequest._2D_get0(SLInventoryHTTPFetchRequest.this), ((LLSDNode) (obj1)), new RootContentHandler(databasecommitthread, null));
            flag1 = flag2;
            Debug.Printf("InvFetch: done parsing,  waiting for commit thread", new Object[0]);
            flag1 = flag2;
            databasecommitthread.stopAndWait(true);
            flag1 = flag2;
            Debug.Printf("InvFetch: commit thread finished", new Object[0]);
            flag2 = true;
_L5:
            if (!flag2) goto _L4; else goto _L3
_L3:
            flag = flag2;
_L2:
            flag1 = flag;
            Debug.Printf("InventoryFetcher: Fetched folder: %s (fetch time = %d)", new Object[] {
                folderUUID.toString(), Long.valueOf(System.currentTimeMillis() - l)
            });
            flag1 = flag;
            SLInventoryHTTPFetchRequest._2D_get2(SLInventoryHTTPFetchRequest.this).set(null);
_L6:
            Object obj2;
            boolean flag3;
            if (!Thread.interrupted())
            {
                flag1 = SLInventoryHTTPFetchRequest._2D_get1(SLInventoryHTTPFetchRequest.this).get();
            } else
            {
                flag1 = true;
            }
            obj1 = folderUUID.toString();
            if (flag)
            {
                obj = "true";
            } else
            {
                obj = "false";
            }
            Debug.Printf("InventoryFetcher: done processing folder %s: success %s cancelled %b", new Object[] {
                obj1, obj, Boolean.valueOf(flag1)
            });
            completeFetch(flag, flag1);
            return;
            obj2;
            flag1 = flag2;
            ((IOException) (obj2)).printStackTrace();
              goto _L5
            obj;
_L10:
            Debug.Warning(((Throwable) (obj)));
            flag = flag1;
              goto _L6
            obj2;
            flag1 = flag2;
            ((LLSDXMLException) (obj2)).printStackTrace();
            try
            {
                Debug.Log((new StringBuilder()).append("InventoryFetcher: malformed xml after req = ").append(((LLSDNode) (obj1)).serializeToXML()).toString());
            }
            // Misplaced declaration of an exception variable
            catch (Object obj2) { }
              goto _L5
_L4:
            flag1 = flag2;
            databasecommitthread.interrupt();
            flag = flag2;
            flag1 = flag2;
            if (Thread.interrupted()) goto _L2; else goto _L7
_L7:
            flag1 = flag2;
            flag3 = SLInventoryHTTPFetchRequest._2D_get1(SLInventoryHTTPFetchRequest.this).get();
            flag = flag2;
            if (flag3) goto _L2; else goto _L8
_L8:
            i++;
              goto _L9
            obj;
            flag1 = false;
              goto _L10
        }

            
            {
                this$0 = SLInventoryHTTPFetchRequest.this;
                super();
            }
    };
    private final AtomicBoolean isCancelled = new AtomicBoolean(false);
    private final AtomicReference streamingXmlReqRef = new AtomicReference(null);

    static String _2D_get0(SLInventoryHTTPFetchRequest slinventoryhttpfetchrequest)
    {
        return slinventoryhttpfetchrequest.capURL;
    }

    static AtomicBoolean _2D_get1(SLInventoryHTTPFetchRequest slinventoryhttpfetchrequest)
    {
        return slinventoryhttpfetchrequest.isCancelled;
    }

    static AtomicReference _2D_get2(SLInventoryHTTPFetchRequest slinventoryhttpfetchrequest)
    {
        return slinventoryhttpfetchrequest.streamingXmlReqRef;
    }

    SLInventoryHTTPFetchRequest(SLInventory slinventory, UUID uuid, String s)
        throws SLInventory.NoInventoryItemException
    {
        super(slinventory, uuid);
        capURL = s;
    }

    public void cancel()
    {
        isCancelled.set(true);
        Object obj = (LLSDStreamingXMLRequest)streamingXmlReqRef.get();
        if (obj != null)
        {
            ((LLSDStreamingXMLRequest) (obj)).InterruptRequest();
        }
        obj = (Future)futureRef.getAndSet(null);
        if (obj != null)
        {
            ((Future) (obj)).cancel(true);
        }
    }

    public void start()
    {
        if (!isCancelled.get() && futureRef.get() == null)
        {
            futureRef.set(GenericHTTPExecutor.getInstance().submit(httpRequest));
        }
    }

    // Unreferenced inner class com/lumiyaviewer/lumiya/slproto/inventory/SLInventoryHTTPFetchRequest$ItemEntryContentHandler$1

/* anonymous class */
    class ItemEntryContentHandler._cls1 extends com.lumiyaviewer.lumiya.slproto.llsd.LLSDStreamingParser.LLSDDefaultContentHandler
    {

        private static final int _2D_com_2D_lumiyaviewer_2D_lumiya_2D_slproto_2D_inventory_2D_SLInventoryHTTPFetchRequest$PermissionsValueKeySwitchesValues[];
        final int $SWITCH_TABLE$com$lumiyaviewer$lumiya$slproto$inventory$SLInventoryHTTPFetchRequest$PermissionsValueKey[];
        final ItemEntryContentHandler this$1;

        private static int[] _2D_getcom_2D_lumiyaviewer_2D_lumiya_2D_slproto_2D_inventory_2D_SLInventoryHTTPFetchRequest$PermissionsValueKeySwitchesValues()
        {
            if (_2D_com_2D_lumiyaviewer_2D_lumiya_2D_slproto_2D_inventory_2D_SLInventoryHTTPFetchRequest$PermissionsValueKeySwitchesValues != null)
            {
                return _2D_com_2D_lumiyaviewer_2D_lumiya_2D_slproto_2D_inventory_2D_SLInventoryHTTPFetchRequest$PermissionsValueKeySwitchesValues;
            }
            int ai[] = new int[PermissionsValueKey.values().length];
            try
            {
                ai[PermissionsValueKey.base_mask.ordinal()] = 1;
            }
            catch (NoSuchFieldError nosuchfielderror9) { }
            try
            {
                ai[PermissionsValueKey.creator_id.ordinal()] = 2;
            }
            catch (NoSuchFieldError nosuchfielderror8) { }
            try
            {
                ai[PermissionsValueKey.everyone_mask.ordinal()] = 3;
            }
            catch (NoSuchFieldError nosuchfielderror7) { }
            try
            {
                ai[PermissionsValueKey.group_id.ordinal()] = 4;
            }
            catch (NoSuchFieldError nosuchfielderror6) { }
            try
            {
                ai[PermissionsValueKey.group_mask.ordinal()] = 5;
            }
            catch (NoSuchFieldError nosuchfielderror5) { }
            try
            {
                ai[PermissionsValueKey.is_owner_group.ordinal()] = 6;
            }
            catch (NoSuchFieldError nosuchfielderror4) { }
            try
            {
                ai[PermissionsValueKey.last_owner_id.ordinal()] = 7;
            }
            catch (NoSuchFieldError nosuchfielderror3) { }
            try
            {
                ai[PermissionsValueKey.next_owner_mask.ordinal()] = 8;
            }
            catch (NoSuchFieldError nosuchfielderror2) { }
            try
            {
                ai[PermissionsValueKey.owner_id.ordinal()] = 9;
            }
            catch (NoSuchFieldError nosuchfielderror1) { }
            try
            {
                ai[PermissionsValueKey.owner_mask.ordinal()] = 10;
            }
            catch (NoSuchFieldError nosuchfielderror) { }
            _2D_com_2D_lumiyaviewer_2D_lumiya_2D_slproto_2D_inventory_2D_SLInventoryHTTPFetchRequest$PermissionsValueKeySwitchesValues = ai;
            return ai;
        }

        public void onPrimitiveValue(String s, LLSDNode llsdnode)
            throws LLSDXMLException, LLSDValueTypeException
        {
            PermissionsValueKey permissionsvaluekey = PermissionsValueKey.byTag(s);
            if (permissionsvaluekey != null)
            {
                switch (_2D_getcom_2D_lumiyaviewer_2D_lumiya_2D_slproto_2D_inventory_2D_SLInventoryHTTPFetchRequest$PermissionsValueKeySwitchesValues()[permissionsvaluekey.ordinal()])
                {
                default:
                    return;

                case 1: // '\001'
                    ItemEntryContentHandler._2D_get0(ItemEntryContentHandler.this).baseMask = llsdnode.asInt();
                    return;

                case 3: // '\003'
                    ItemEntryContentHandler._2D_get0(ItemEntryContentHandler.this).everyoneMask = llsdnode.asInt();
                    return;

                case 5: // '\005'
                    ItemEntryContentHandler._2D_get0(ItemEntryContentHandler.this).groupMask = llsdnode.asInt();
                    return;

                case 8: // '\b'
                    ItemEntryContentHandler._2D_get0(ItemEntryContentHandler.this).nextOwnerMask = llsdnode.asInt();
                    return;

                case 10: // '\n'
                    ItemEntryContentHandler._2D_get0(ItemEntryContentHandler.this).ownerMask = llsdnode.asInt();
                    return;

                case 2: // '\002'
                    ItemEntryContentHandler._2D_get0(ItemEntryContentHandler.this).creatorUUID = llsdnode.asUUID();
                    return;

                case 4: // '\004'
                    ItemEntryContentHandler._2D_get0(ItemEntryContentHandler.this).groupUUID = llsdnode.asUUID();
                    return;

                case 7: // '\007'
                    ItemEntryContentHandler._2D_get0(ItemEntryContentHandler.this).lastOwnerUUID = llsdnode.asUUID();
                    return;

                case 9: // '\t'
                    ItemEntryContentHandler._2D_get0(ItemEntryContentHandler.this).ownerUUID = llsdnode.asUUID();
                    return;

                case 6: // '\006'
                    ItemEntryContentHandler._2D_get0(ItemEntryContentHandler.this).isGroupOwned = llsdnode.asBoolean();
                    return;
                }
            } else
            {
                Debug.Printf("InvFetch: Permissions unknown key '%s'", new Object[] {
                    s
                });
                return;
            }
        }

            
            {
                this$1 = ItemEntryContentHandler.this;
                super();
            }
    }


    // Unreferenced inner class com/lumiyaviewer/lumiya/slproto/inventory/SLInventoryHTTPFetchRequest$ItemEntryContentHandler$2

/* anonymous class */
    class ItemEntryContentHandler._cls2 extends com.lumiyaviewer.lumiya.slproto.llsd.LLSDStreamingParser.LLSDDefaultContentHandler
    {

        final ItemEntryContentHandler this$1;

        public void onPrimitiveValue(String s, LLSDNode llsdnode)
            throws LLSDXMLException, LLSDValueTypeException
        {
            if (s.equals("sale_type"))
            {
                if (llsdnode.isString())
                {
                    ItemEntryContentHandler._2D_get0(ItemEntryContentHandler.this).saleType = SLSaleType.getByString(llsdnode.asString()).getTypeCode();
                    return;
                } else
                {
                    ItemEntryContentHandler._2D_get0(ItemEntryContentHandler.this).saleType = llsdnode.asInt();
                    return;
                }
            }
            if (s.equals("sale_price"))
            {
                ItemEntryContentHandler._2D_get0(ItemEntryContentHandler.this).salePrice = llsdnode.asInt();
                return;
            } else
            {
                Debug.Printf("InvFetch: Sale info unknown key '%s'", new Object[] {
                    s
                });
                return;
            }
        }

            
            {
                this$1 = ItemEntryContentHandler.this;
                super();
            }
    }

}
