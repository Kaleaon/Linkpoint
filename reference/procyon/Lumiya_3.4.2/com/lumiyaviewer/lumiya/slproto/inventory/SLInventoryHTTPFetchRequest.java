// 
// Decompiled by Procyon v0.6.0
// 

package com.lumiyaviewer.lumiya.slproto.inventory;

import java.util.HashMap;
import java.util.Map;
import com.lumiyaviewer.lumiya.slproto.llsd.LLSDValueTypeException;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.BlockingQueue;
import com.lumiyaviewer.lumiya.slproto.https.GenericHTTPExecutor;
import com.lumiyaviewer.lumiya.slproto.llsd.LLSDXMLException;
import java.io.IOException;
import com.lumiyaviewer.lumiya.slproto.llsd.LLSDStreamingParser;
import com.lumiyaviewer.lumiya.slproto.llsd.types.LLSDBoolean;
import com.lumiyaviewer.lumiya.slproto.llsd.LLSDNode;
import com.lumiyaviewer.lumiya.slproto.llsd.types.LLSDMap;
import com.lumiyaviewer.lumiya.slproto.llsd.types.LLSDUUID;
import com.lumiyaviewer.lumiya.slproto.llsd.types.LLSDArray;
import com.lumiyaviewer.lumiya.Debug;
import java.util.UUID;
import com.lumiyaviewer.lumiya.slproto.https.LLSDStreamingXMLRequest;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.Future;
import java.util.concurrent.atomic.AtomicReference;

class SLInventoryHTTPFetchRequest extends SLInventoryFetchRequest
{
    private final String capURL;
    private final AtomicReference<Future<?>> futureRef;
    private final Runnable httpRequest;
    private final AtomicBoolean isCancelled;
    private final AtomicReference<LLSDStreamingXMLRequest> streamingXmlReqRef;
    
    SLInventoryHTTPFetchRequest(final SLInventory slInventory, final UUID uuid, final String capURL) throws SLInventory.NoInventoryItemException {
        super(slInventory, uuid);
        this.futureRef = new AtomicReference<Future<?>>(null);
        this.streamingXmlReqRef = new AtomicReference<LLSDStreamingXMLRequest>(null);
        this.isCancelled = new AtomicBoolean(false);
        this.httpRequest = new Runnable() {
            @Override
            public void run() {
                try {
                    final long currentTimeMillis = System.currentTimeMillis();
                    Debug.Printf("InventoryFetcher: Going to fetch folder: %s", SLInventoryHTTPFetchRequest.this.folderUUID);
                    Object newValue = new LLSDStreamingXMLRequest();
                    Object string = new LLSDArray();
                    Object o = new LLSDMap.LLSDMapEntry("folder_id", new LLSDUUID(SLInventoryHTTPFetchRequest.this.folderUUID));
                    ((LLSDArray)string).add(new LLSDMap(new LLSDMap.LLSDMapEntry[] { (LLSDMap.LLSDMapEntry)o, new LLSDMap.LLSDMapEntry("fetch_folders", new LLSDBoolean(true)), new LLSDMap.LLSDMapEntry("fetch_items", new LLSDBoolean(true)) }));
                    o = new LLSDMap(new LLSDMap.LLSDMapEntry[] { new LLSDMap.LLSDMapEntry("folders", (LLSDNode)string) });
                    SLInventoryHTTPFetchRequest.this.streamingXmlReqRef.set(newValue);
                    int n = 0;
                    int n2 = 0;
                    while (true) {
                        int n3 = n2;
                    Label_0373:
                        while (true) {
                            if (n >= 3) {
                                break Label_0373;
                            }
                            int n4;
                            String -get0;
                            RootContentHandler rootContentHandler;
                            boolean value;
                            StringBuilder sb;
                            StringBuilder sb3;
                            StringBuilder sb2;
                            StringBuilder sb4;
                            String s;
                            StringBuilder sb5;
                            LLSDNode llsdNode;
                            String s2;
                            StringBuilder sb6;
                            String s3;
                            boolean value2;
                            Label_0447_Outer:Label_0467_Outer:
                            while (true) {
                                n4 = n2;
                                while (true) {
                                Label_0637:
                                    while (true) {
                                        Label_0631: {
                                        Label_0577:
                                            while (true) {
                                                try {
                                                    string = new(com.lumiyaviewer.lumiya.slproto.inventory.SLInventoryHTTPFetchRequest.DatabaseCommitThread.class)();
                                                    n4 = n2;
                                                    new DatabaseCommitThread((DatabaseCommitThread)null);
                                                    n4 = n2;
                                                    try {
                                                        ((DatabaseCommitThread)string).start();
                                                        n4 = n2;
                                                        Debug.Printf("InventoryFetcher: Starting HTTP request for folder: %s", SLInventoryHTTPFetchRequest.this.folderUUID);
                                                        n4 = n2;
                                                        -get0 = SLInventoryHTTPFetchRequest.this.capURL;
                                                        n4 = n2;
                                                        n4 = n2;
                                                        rootContentHandler = new RootContentHandler((DatabaseCommitThread)string, (RootContentHandler)null);
                                                        n4 = n2;
                                                        ((LLSDStreamingXMLRequest)newValue).PerformRequest(-get0, o, (LLSDStreamingParser.LLSDContentHandler)rootContentHandler);
                                                        n4 = n2;
                                                        Debug.Printf("InvFetch: done parsing,  waiting for commit thread", new Object[0]);
                                                        n4 = n2;
                                                        ((DatabaseCommitThread)string).stopAndWait(true);
                                                        n4 = n2;
                                                        Debug.Printf("InvFetch: commit thread finished", new Object[0]);
                                                        n2 = (true ? 1 : 0);
                                                        if (n2 == 0) {
                                                            break Label_0577;
                                                        }
                                                        n3 = n2;
                                                        n4 = n3;
                                                        Debug.Printf("InventoryFetcher: Fetched folder: %s (fetch time = %d)", SLInventoryHTTPFetchRequest.this.folderUUID.toString(), System.currentTimeMillis() - currentTimeMillis);
                                                        n4 = n3;
                                                        SLInventoryHTTPFetchRequest.this.streamingXmlReqRef.set(null);
                                                        n4 = n3;
                                                        if (Thread.interrupted()) {
                                                            break Label_0631;
                                                        }
                                                        value = SLInventoryHTTPFetchRequest.this.isCancelled.get();
                                                        string = SLInventoryHTTPFetchRequest.this.folderUUID.toString();
                                                        if (n4 != 0) {
                                                            newValue = "true";
                                                            Debug.Printf("InventoryFetcher: done processing folder %s: success %s cancelled %b", string, newValue, value);
                                                            SLInventoryHTTPFetchRequest.this.completeFetch((boolean)(n4 != 0), value);
                                                            return;
                                                        }
                                                        break Label_0637;
                                                    }
                                                    catch (final IOException ex) {
                                                        n4 = n2;
                                                        ex.printStackTrace();
                                                    }
                                                    catch (final LLSDXMLException ex2) {
                                                        n4 = n2;
                                                        ex2.printStackTrace();
                                                        sb = new(java.lang.StringBuilder.class)();
                                                        sb2 = (sb3 = sb);
                                                        new StringBuilder();
                                                        sb4 = sb2;
                                                        s = "InventoryFetcher: malformed xml after req = ";
                                                        sb5 = sb4.append(s);
                                                        llsdNode = (LLSDNode)o;
                                                        s2 = llsdNode.serializeToXML();
                                                        sb6 = sb5.append(s2);
                                                        s3 = sb6.toString();
                                                        Debug.Log(s3);
                                                    }
                                                }
                                                catch (final Exception ex3) {}
                                                try {
                                                    sb = new(java.lang.StringBuilder.class)();
                                                    sb2 = (sb3 = sb);
                                                    new StringBuilder();
                                                    sb4 = sb2;
                                                    s = "InventoryFetcher: malformed xml after req = ";
                                                    sb5 = sb4.append(s);
                                                    llsdNode = (LLSDNode)o;
                                                    s2 = llsdNode.serializeToXML();
                                                    sb6 = sb5.append(s2);
                                                    s3 = sb6.toString();
                                                    Debug.Log(s3);
                                                    continue Label_0447_Outer;
                                                }
                                                catch (final Exception ex4) {}
                                                break;
                                            }
                                            ((DatabaseCommitThread)string).interrupt();
                                            n3 = n2;
                                            n4 = n2;
                                            if (Thread.interrupted()) {
                                                continue Label_0373;
                                            }
                                            n4 = n2;
                                            value2 = SLInventoryHTTPFetchRequest.this.isCancelled.get();
                                            n3 = n2;
                                            if (!value2) {
                                                break;
                                            }
                                            continue Label_0373;
                                        }
                                        value = true;
                                        continue Label_0467_Outer;
                                    }
                                    newValue = "false";
                                    continue;
                                }
                            }
                            break;
                        }
                        ++n;
                    }
                }
                catch (final Exception newValue) {
                    final int n4 = false ? 1 : 0;
                    goto Label_0520;
                }
            }
        };
        this.capURL = capURL;
    }
    
    @Override
    public void cancel() {
        this.isCancelled.set(true);
        final LLSDStreamingXMLRequest llsdStreamingXMLRequest = this.streamingXmlReqRef.get();
        if (llsdStreamingXMLRequest != null) {
            llsdStreamingXMLRequest.InterruptRequest();
        }
        final Future<?> future = this.futureRef.getAndSet(null);
        if (future != null) {
            future.cancel(true);
        }
    }
    
    @Override
    public void start() {
        if (!this.isCancelled.get() && this.futureRef.get() == null) {
            this.futureRef.set(GenericHTTPExecutor.getInstance().submit(this.httpRequest));
        }
    }
    
    private final class DatabaseCommitThread extends Thread
    {
        private volatile boolean aborted;
        private final BlockingQueue<SLInventoryEntry> commitEntryQueue;
        private final SLInventoryEntry stopEntry;
        
        private DatabaseCommitThread() {
            this.commitEntryQueue = new LinkedBlockingQueue<SLInventoryEntry>(100);
            this.stopEntry = new SLInventoryEntry();
            this.aborted = false;
        }
        
        void addEntry(final SLInventoryEntry slInventoryEntry) throws InterruptedException {
            this.commitEntryQueue.put(slInventoryEntry);
        }
        
        @Override
        public void run() {
            // 
            // This method could not be decompiled.
            // 
            // Original Bytecode:
            // 
            //     1: astore_1       
            //     2: new             Ljava/util/HashSet;
            //     5: dup            
            //     6: invokespecial   java/util/HashSet.<init>:()V
            //     9: astore_2       
            //    10: aconst_null    
            //    11: astore_3       
            //    12: iconst_0       
            //    13: istore          4
            //    15: iconst_0       
            //    16: istore          5
            //    18: iload           4
            //    20: istore          6
            //    22: aload_3        
            //    23: astore          7
            //    25: iload           4
            //    27: istore          8
            //    29: aload_3        
            //    30: astore          9
            //    32: iload           4
            //    34: istore          10
            //    36: invokestatic    java/lang/Thread.interrupted:()Z
            //    39: ifne            189
            //    42: aload_3        
            //    43: astore          7
            //    45: iload           4
            //    47: istore          8
            //    49: aload_3        
            //    50: astore          9
            //    52: iload           4
            //    54: istore          10
            //    56: aload_0        
            //    57: getfield        com/lumiyaviewer/lumiya/slproto/inventory/SLInventoryHTTPFetchRequest$DatabaseCommitThread.commitEntryQueue:Ljava/util/concurrent/BlockingQueue;
            //    60: invokeinterface java/util/concurrent/BlockingQueue.poll:()Ljava/lang/Object;
            //    65: checkcast       Lcom/lumiyaviewer/lumiya/slproto/inventory/SLInventoryEntry;
            //    68: astore          11
            //    70: aload           11
            //    72: ifnonnull       659
            //    75: iload           4
            //    77: istore          6
            //    79: iload           4
            //    81: ifeq            138
            //    84: aload_3        
            //    85: astore          7
            //    87: iload           4
            //    89: istore          8
            //    91: aload_3        
            //    92: astore          9
            //    94: iload           4
            //    96: istore          10
            //    98: aload_0        
            //    99: getfield        com/lumiyaviewer/lumiya/slproto/inventory/SLInventoryHTTPFetchRequest$DatabaseCommitThread.this$0:Lcom/lumiyaviewer/lumiya/slproto/inventory/SLInventoryHTTPFetchRequest;
            //   102: getfield        com/lumiyaviewer/lumiya/slproto/inventory/SLInventoryHTTPFetchRequest.db:Lcom/lumiyaviewer/lumiya/orm/InventoryDB;
            //   105: invokevirtual   com/lumiyaviewer/lumiya/orm/InventoryDB.setTransactionSuccessful:()V
            //   108: aload_3        
            //   109: astore          7
            //   111: iload           4
            //   113: istore          8
            //   115: aload_3        
            //   116: astore          9
            //   118: iload           4
            //   120: istore          10
            //   122: aload_0        
            //   123: getfield        com/lumiyaviewer/lumiya/slproto/inventory/SLInventoryHTTPFetchRequest$DatabaseCommitThread.this$0:Lcom/lumiyaviewer/lumiya/slproto/inventory/SLInventoryHTTPFetchRequest;
            //   126: getfield        com/lumiyaviewer/lumiya/slproto/inventory/SLInventoryHTTPFetchRequest.db:Lcom/lumiyaviewer/lumiya/orm/InventoryDB;
            //   129: invokevirtual   com/lumiyaviewer/lumiya/orm/InventoryDB.endTransaction:()V
            //   132: iconst_0       
            //   133: istore          6
            //   135: iconst_0       
            //   136: istore          5
            //   138: aload_3        
            //   139: astore          7
            //   141: iload           6
            //   143: istore          8
            //   145: aload_3        
            //   146: astore          9
            //   148: iload           6
            //   150: istore          10
            //   152: aload_0        
            //   153: getfield        com/lumiyaviewer/lumiya/slproto/inventory/SLInventoryHTTPFetchRequest$DatabaseCommitThread.commitEntryQueue:Ljava/util/concurrent/BlockingQueue;
            //   156: invokeinterface java/util/concurrent/BlockingQueue.take:()Ljava/lang/Object;
            //   161: checkcast       Lcom/lumiyaviewer/lumiya/slproto/inventory/SLInventoryEntry;
            //   164: astore          11
            //   166: aload_3        
            //   167: astore          7
            //   169: iload           6
            //   171: istore          8
            //   173: aload_3        
            //   174: astore          9
            //   176: iload           6
            //   178: istore          10
            //   180: aload           11
            //   182: aload_0        
            //   183: getfield        com/lumiyaviewer/lumiya/slproto/inventory/SLInventoryHTTPFetchRequest$DatabaseCommitThread.stopEntry:Lcom/lumiyaviewer/lumiya/slproto/inventory/SLInventoryEntry;
            //   186: if_acmpne       337
            //   189: aload_3        
            //   190: astore          7
            //   192: iload           6
            //   194: istore          8
            //   196: aload_3        
            //   197: astore          9
            //   199: iload           6
            //   201: istore          10
            //   203: invokestatic    java/lang/Thread.interrupted:()Z
            //   206: istore          12
            //   208: iload           12
            //   210: ifne            647
            //   213: iconst_1       
            //   214: istore          4
            //   216: aload_3        
            //   217: astore          13
            //   219: iload           6
            //   221: ifeq            277
            //   224: iload           4
            //   226: ifeq            633
            //   229: ldc             "true"
            //   231: astore_3       
            //   232: ldc             "InvFetch: commit thread ending transaction (success: %s, count %d)."
            //   234: iconst_2       
            //   235: anewarray       Ljava/lang/Object;
            //   238: dup            
            //   239: iconst_0       
            //   240: aload_3        
            //   241: aastore        
            //   242: dup            
            //   243: iconst_1       
            //   244: aload_2        
            //   245: invokeinterface java/util/Set.size:()I
            //   250: invokestatic    java/lang/Integer.valueOf:(I)Ljava/lang/Integer;
            //   253: aastore        
            //   254: invokestatic    com/lumiyaviewer/lumiya/Debug.Printf:(Ljava/lang/String;[Ljava/lang/Object;)V
            //   257: aload_0        
            //   258: getfield        com/lumiyaviewer/lumiya/slproto/inventory/SLInventoryHTTPFetchRequest$DatabaseCommitThread.this$0:Lcom/lumiyaviewer/lumiya/slproto/inventory/SLInventoryHTTPFetchRequest;
            //   261: getfield        com/lumiyaviewer/lumiya/slproto/inventory/SLInventoryHTTPFetchRequest.db:Lcom/lumiyaviewer/lumiya/orm/InventoryDB;
            //   264: invokevirtual   com/lumiyaviewer/lumiya/orm/InventoryDB.setTransactionSuccessful:()V
            //   267: aload_0        
            //   268: getfield        com/lumiyaviewer/lumiya/slproto/inventory/SLInventoryHTTPFetchRequest$DatabaseCommitThread.this$0:Lcom/lumiyaviewer/lumiya/slproto/inventory/SLInventoryHTTPFetchRequest;
            //   271: getfield        com/lumiyaviewer/lumiya/slproto/inventory/SLInventoryHTTPFetchRequest.db:Lcom/lumiyaviewer/lumiya/orm/InventoryDB;
            //   274: invokevirtual   com/lumiyaviewer/lumiya/orm/InventoryDB.endTransaction:()V
            //   277: aload           13
            //   279: ifnull          287
            //   282: aload           13
            //   284: invokevirtual   android/database/sqlite/SQLiteStatement.close:()V
            //   287: aload_1        
            //   288: ifnull          295
            //   291: aload_1        
            //   292: invokevirtual   android/database/sqlite/SQLiteStatement.close:()V
            //   295: iload           4
            //   297: ifeq            336
            //   300: aload_0        
            //   301: getfield        com/lumiyaviewer/lumiya/slproto/inventory/SLInventoryHTTPFetchRequest$DatabaseCommitThread.aborted:Z
            //   304: iconst_1       
            //   305: ixor           
            //   306: ifeq            336
            //   309: ldc             "InvFetch: commit thread successful, calling retainChildren."
            //   311: iconst_0       
            //   312: anewarray       Ljava/lang/Object;
            //   315: invokestatic    com/lumiyaviewer/lumiya/Debug.Printf:(Ljava/lang/String;[Ljava/lang/Object;)V
            //   318: aload_0        
            //   319: getfield        com/lumiyaviewer/lumiya/slproto/inventory/SLInventoryHTTPFetchRequest$DatabaseCommitThread.this$0:Lcom/lumiyaviewer/lumiya/slproto/inventory/SLInventoryHTTPFetchRequest;
            //   322: getfield        com/lumiyaviewer/lumiya/slproto/inventory/SLInventoryHTTPFetchRequest.db:Lcom/lumiyaviewer/lumiya/orm/InventoryDB;
            //   325: aload_0        
            //   326: getfield        com/lumiyaviewer/lumiya/slproto/inventory/SLInventoryHTTPFetchRequest$DatabaseCommitThread.this$0:Lcom/lumiyaviewer/lumiya/slproto/inventory/SLInventoryHTTPFetchRequest;
            //   329: getfield        com/lumiyaviewer/lumiya/slproto/inventory/SLInventoryHTTPFetchRequest.folderId:J
            //   332: aload_2        
            //   333: invokevirtual   com/lumiyaviewer/lumiya/orm/InventoryDB.retainChildren:(JLjava/util/Set;)V
            //   336: return         
            //   337: iload           6
            //   339: istore          4
            //   341: iload           6
            //   343: ifne            373
            //   346: aload_3        
            //   347: astore          7
            //   349: iload           6
            //   351: istore          8
            //   353: aload_3        
            //   354: astore          9
            //   356: iload           6
            //   358: istore          10
            //   360: aload_0        
            //   361: getfield        com/lumiyaviewer/lumiya/slproto/inventory/SLInventoryHTTPFetchRequest$DatabaseCommitThread.this$0:Lcom/lumiyaviewer/lumiya/slproto/inventory/SLInventoryHTTPFetchRequest;
            //   364: getfield        com/lumiyaviewer/lumiya/slproto/inventory/SLInventoryHTTPFetchRequest.db:Lcom/lumiyaviewer/lumiya/orm/InventoryDB;
            //   367: invokevirtual   com/lumiyaviewer/lumiya/orm/InventoryDB.beginTransaction:()V
            //   370: iconst_1       
            //   371: istore          4
            //   373: iinc            5, 1
            //   376: iload           5
            //   378: bipush          16
            //   380: if_icmplt       656
            //   383: aload_3        
            //   384: astore          7
            //   386: iload           4
            //   388: istore          8
            //   390: aload_3        
            //   391: astore          9
            //   393: iload           4
            //   395: istore          10
            //   397: aload_0        
            //   398: getfield        com/lumiyaviewer/lumiya/slproto/inventory/SLInventoryHTTPFetchRequest$DatabaseCommitThread.this$0:Lcom/lumiyaviewer/lumiya/slproto/inventory/SLInventoryHTTPFetchRequest;
            //   401: getfield        com/lumiyaviewer/lumiya/slproto/inventory/SLInventoryHTTPFetchRequest.db:Lcom/lumiyaviewer/lumiya/orm/InventoryDB;
            //   404: invokevirtual   com/lumiyaviewer/lumiya/orm/InventoryDB.yieldIfContendedSafely:()V
            //   407: iconst_0       
            //   408: istore          5
            //   410: aload_3        
            //   411: astore          7
            //   413: iload           4
            //   415: istore          8
            //   417: aload_3        
            //   418: astore          9
            //   420: iload           4
            //   422: istore          10
            //   424: aload_2        
            //   425: aload           11
            //   427: getfield        com/lumiyaviewer/lumiya/slproto/inventory/SLInventoryEntry.uuid:Ljava/util/UUID;
            //   430: invokeinterface java/util/Set.add:(Ljava/lang/Object;)Z
            //   435: pop            
            //   436: aload_3        
            //   437: astore          7
            //   439: iload           4
            //   441: istore          8
            //   443: aload_3        
            //   444: astore          9
            //   446: iload           4
            //   448: istore          10
            //   450: getstatic       android/os/Build$VERSION.SDK_INT:I
            //   453: bipush          11
            //   455: if_icmplt       544
            //   458: aload_3        
            //   459: astore          13
            //   461: aload_3        
            //   462: ifnonnull       494
            //   465: aload_3        
            //   466: astore          7
            //   468: iload           4
            //   470: istore          8
            //   472: aload_3        
            //   473: astore          9
            //   475: iload           4
            //   477: istore          10
            //   479: aload_0        
            //   480: getfield        com/lumiyaviewer/lumiya/slproto/inventory/SLInventoryHTTPFetchRequest$DatabaseCommitThread.this$0:Lcom/lumiyaviewer/lumiya/slproto/inventory/SLInventoryHTTPFetchRequest;
            //   483: getfield        com/lumiyaviewer/lumiya/slproto/inventory/SLInventoryHTTPFetchRequest.db:Lcom/lumiyaviewer/lumiya/orm/InventoryDB;
            //   486: invokevirtual   com/lumiyaviewer/lumiya/orm/InventoryDB.getDatabase:()Landroid/database/sqlite/SQLiteDatabase;
            //   489: invokestatic    com/lumiyaviewer/lumiya/slproto/inventory/SLInventoryEntry.getInsertStatement:(Landroid/database/sqlite/SQLiteDatabase;)Landroid/database/sqlite/SQLiteStatement;
            //   492: astore          13
            //   494: aload_1        
            //   495: ifnonnull       653
            //   498: aload           13
            //   500: astore          7
            //   502: iload           4
            //   504: istore          8
            //   506: aload           13
            //   508: astore          9
            //   510: iload           4
            //   512: istore          10
            //   514: aload_0        
            //   515: getfield        com/lumiyaviewer/lumiya/slproto/inventory/SLInventoryHTTPFetchRequest$DatabaseCommitThread.this$0:Lcom/lumiyaviewer/lumiya/slproto/inventory/SLInventoryHTTPFetchRequest;
            //   518: getfield        com/lumiyaviewer/lumiya/slproto/inventory/SLInventoryHTTPFetchRequest.db:Lcom/lumiyaviewer/lumiya/orm/InventoryDB;
            //   521: invokevirtual   com/lumiyaviewer/lumiya/orm/InventoryDB.getDatabase:()Landroid/database/sqlite/SQLiteDatabase;
            //   524: invokestatic    com/lumiyaviewer/lumiya/slproto/inventory/SLInventoryEntry.getUpdateStatement:(Landroid/database/sqlite/SQLiteDatabase;)Landroid/database/sqlite/SQLiteStatement;
            //   527: astore_3       
            //   528: aload_3        
            //   529: astore_1       
            //   530: aload           11
            //   532: aload_1        
            //   533: aload           13
            //   535: invokevirtual   com/lumiyaviewer/lumiya/slproto/inventory/SLInventoryEntry.updateOrInsert:(Landroid/database/sqlite/SQLiteStatement;Landroid/database/sqlite/SQLiteStatement;)V
            //   538: aload           13
            //   540: astore_3       
            //   541: goto            18
            //   544: aload_3        
            //   545: astore          7
            //   547: iload           4
            //   549: istore          8
            //   551: aload_3        
            //   552: astore          9
            //   554: iload           4
            //   556: istore          10
            //   558: aload           11
            //   560: aload_0        
            //   561: getfield        com/lumiyaviewer/lumiya/slproto/inventory/SLInventoryHTTPFetchRequest$DatabaseCommitThread.this$0:Lcom/lumiyaviewer/lumiya/slproto/inventory/SLInventoryHTTPFetchRequest;
            //   564: getfield        com/lumiyaviewer/lumiya/slproto/inventory/SLInventoryHTTPFetchRequest.db:Lcom/lumiyaviewer/lumiya/orm/InventoryDB;
            //   567: invokevirtual   com/lumiyaviewer/lumiya/orm/InventoryDB.getDatabase:()Landroid/database/sqlite/SQLiteDatabase;
            //   570: invokevirtual   com/lumiyaviewer/lumiya/slproto/inventory/SLInventoryEntry.updateOrInsert:(Landroid/database/sqlite/SQLiteDatabase;)V
            //   573: aload_3        
            //   574: astore          13
            //   576: goto            538
            //   579: astore_3       
            //   580: iload           8
            //   582: istore          4
            //   584: aload           7
            //   586: astore          13
            //   588: aload_3        
            //   589: invokestatic    com/lumiyaviewer/lumiya/Debug.Warning:(Ljava/lang/Throwable;)V
            //   592: iconst_0       
            //   593: istore          8
            //   595: iload           4
            //   597: istore          6
            //   599: iload           8
            //   601: istore          4
            //   603: goto            219
            //   606: astore_3       
            //   607: iload           10
            //   609: istore          4
            //   611: aload           9
            //   613: astore          13
            //   615: aload_3        
            //   616: invokestatic    com/lumiyaviewer/lumiya/Debug.Warning:(Ljava/lang/Throwable;)V
            //   619: iconst_0       
            //   620: istore          8
            //   622: iload           4
            //   624: istore          6
            //   626: iload           8
            //   628: istore          4
            //   630: goto            219
            //   633: ldc             "false"
            //   635: astore_3       
            //   636: goto            232
            //   639: astore_3       
            //   640: goto            615
            //   643: astore_3       
            //   644: goto            588
            //   647: iconst_0       
            //   648: istore          4
            //   650: goto            216
            //   653: goto            530
            //   656: goto            410
            //   659: iload           4
            //   661: istore          6
            //   663: goto            166
            //    Exceptions:
            //  Try           Handler
            //  Start  End    Start  End    Type                                                           
            //  -----  -----  -----  -----  ---------------------------------------------------------------
            //  36     42     606    615    Ljava/lang/InterruptedException;
            //  36     42     579    588    Lcom/lumiyaviewer/lumiya/orm/DBObject$DatabaseBindingException;
            //  56     70     606    615    Ljava/lang/InterruptedException;
            //  56     70     579    588    Lcom/lumiyaviewer/lumiya/orm/DBObject$DatabaseBindingException;
            //  98     108    606    615    Ljava/lang/InterruptedException;
            //  98     108    579    588    Lcom/lumiyaviewer/lumiya/orm/DBObject$DatabaseBindingException;
            //  122    132    606    615    Ljava/lang/InterruptedException;
            //  122    132    579    588    Lcom/lumiyaviewer/lumiya/orm/DBObject$DatabaseBindingException;
            //  152    166    606    615    Ljava/lang/InterruptedException;
            //  152    166    579    588    Lcom/lumiyaviewer/lumiya/orm/DBObject$DatabaseBindingException;
            //  180    189    606    615    Ljava/lang/InterruptedException;
            //  180    189    579    588    Lcom/lumiyaviewer/lumiya/orm/DBObject$DatabaseBindingException;
            //  203    208    606    615    Ljava/lang/InterruptedException;
            //  203    208    579    588    Lcom/lumiyaviewer/lumiya/orm/DBObject$DatabaseBindingException;
            //  360    370    606    615    Ljava/lang/InterruptedException;
            //  360    370    579    588    Lcom/lumiyaviewer/lumiya/orm/DBObject$DatabaseBindingException;
            //  397    407    606    615    Ljava/lang/InterruptedException;
            //  397    407    579    588    Lcom/lumiyaviewer/lumiya/orm/DBObject$DatabaseBindingException;
            //  424    436    606    615    Ljava/lang/InterruptedException;
            //  424    436    579    588    Lcom/lumiyaviewer/lumiya/orm/DBObject$DatabaseBindingException;
            //  450    458    606    615    Ljava/lang/InterruptedException;
            //  450    458    579    588    Lcom/lumiyaviewer/lumiya/orm/DBObject$DatabaseBindingException;
            //  479    494    606    615    Ljava/lang/InterruptedException;
            //  479    494    579    588    Lcom/lumiyaviewer/lumiya/orm/DBObject$DatabaseBindingException;
            //  514    528    606    615    Ljava/lang/InterruptedException;
            //  514    528    579    588    Lcom/lumiyaviewer/lumiya/orm/DBObject$DatabaseBindingException;
            //  530    538    639    643    Ljava/lang/InterruptedException;
            //  530    538    643    647    Lcom/lumiyaviewer/lumiya/orm/DBObject$DatabaseBindingException;
            //  558    573    606    615    Ljava/lang/InterruptedException;
            //  558    573    579    588    Lcom/lumiyaviewer/lumiya/orm/DBObject$DatabaseBindingException;
            // 
            // The error that occurred was:
            // 
            // java.lang.IllegalStateException: Expression is linked from several locations: Label_0530:
            //     at com.strobel.decompiler.ast.Error.expressionLinkedFromMultipleLocations(Error.java:27)
            //     at com.strobel.decompiler.ast.AstOptimizer.mergeDisparateObjectInitializations(AstOptimizer.java:2604)
            //     at com.strobel.decompiler.ast.AstOptimizer.optimize(AstOptimizer.java:235)
            //     at com.strobel.decompiler.ast.AstOptimizer.optimize(AstOptimizer.java:42)
            //     at com.strobel.decompiler.languages.java.ast.AstMethodBodyBuilder.createMethodBody(AstMethodBodyBuilder.java:206)
            //     at com.strobel.decompiler.languages.java.ast.AstMethodBodyBuilder.createMethodBody(AstMethodBodyBuilder.java:93)
            //     at com.strobel.decompiler.languages.java.ast.AstBuilder.createMethodBody(AstBuilder.java:868)
            //     at com.strobel.decompiler.languages.java.ast.AstBuilder.createMethod(AstBuilder.java:761)
            //     at com.strobel.decompiler.languages.java.ast.AstBuilder.addTypeMembers(AstBuilder.java:638)
            //     at com.strobel.decompiler.languages.java.ast.AstBuilder.createTypeCore(AstBuilder.java:605)
            //     at com.strobel.decompiler.languages.java.ast.AstBuilder.createTypeNoCache(AstBuilder.java:195)
            //     at com.strobel.decompiler.languages.java.ast.AstBuilder.addTypeMembers(AstBuilder.java:662)
            //     at com.strobel.decompiler.languages.java.ast.AstBuilder.createTypeCore(AstBuilder.java:605)
            //     at com.strobel.decompiler.languages.java.ast.AstBuilder.createTypeNoCache(AstBuilder.java:195)
            //     at com.strobel.decompiler.languages.java.ast.AstBuilder.createType(AstBuilder.java:162)
            //     at com.strobel.decompiler.languages.java.ast.AstBuilder.addType(AstBuilder.java:137)
            //     at com.strobel.decompiler.languages.java.JavaLanguage.buildAst(JavaLanguage.java:71)
            //     at com.strobel.decompiler.languages.java.JavaLanguage.decompileType(JavaLanguage.java:59)
            //     at com.strobel.decompiler.DecompilerDriver.decompileType(DecompilerDriver.java:333)
            //     at com.strobel.decompiler.DecompilerDriver.decompileJar(DecompilerDriver.java:254)
            //     at com.strobel.decompiler.DecompilerDriver.main(DecompilerDriver.java:144)
            // 
            throw new IllegalStateException("An error occurred while decompiling this method.");
        }
        
        void stopAndWait(final boolean b) throws InterruptedException {
            if (!b) {
                this.aborted = true;
            }
            this.commitEntryQueue.put(this.stopEntry);
            this.join();
        }
    }
    
    private class FolderDataContentHandler extends LLSDDefaultContentHandler
    {
        private final DatabaseCommitThread commitThread;
        private UUID gotUUID;
        private int gotVersion;
        
        private FolderDataContentHandler(final DatabaseCommitThread commitThread) {
            this.commitThread = commitThread;
        }
        
        @Override
        public LLSDContentHandler onArrayBegin(final String s) throws LLSDXMLException {
            if (s.equals("categories")) {
                return new LLSDDefaultContentHandler() {
                    @Override
                    public LLSDContentHandler onMapBegin(final String s) throws LLSDXMLException {
                        return new FolderEntryContentHandler(FolderDataContentHandler.this.commitThread);
                    }
                };
            }
            if (s.equals("items")) {
                return new LLSDDefaultContentHandler() {
                    @Override
                    public LLSDContentHandler onMapBegin(final String s) throws LLSDXMLException {
                        return new ItemEntryContentHandler(FolderDataContentHandler.this.commitThread);
                    }
                };
            }
            return super.onArrayBegin(s);
        }
        
        @Override
        public void onMapEnd(final String s) throws LLSDXMLException, InterruptedException {
            if (this.gotUUID != null && this.gotUUID.equals(SLInventoryHTTPFetchRequest.this.folderUUID) && this.gotVersion != SLInventoryHTTPFetchRequest.this.folderEntry.version) {
                SLInventoryHTTPFetchRequest.this.folderEntry.version = this.gotVersion;
                this.commitThread.addEntry(SLInventoryHTTPFetchRequest.this.folderEntry);
            }
        }
        
        @Override
        public void onPrimitiveValue(final String s, final LLSDNode llsdNode) throws LLSDXMLException, LLSDValueTypeException {
            Debug.Printf("InvFetch: FolderDataContentHandler: key '%s' value '%s'", s, llsdNode);
            if (s.equals("version")) {
                this.gotVersion = llsdNode.asInt();
            }
            else if (s.equals("folder_id")) {
                this.gotUUID = llsdNode.asUUID();
            }
        }
    }
    
    private class FolderEntryContentHandler extends LLSDDefaultContentHandler
    {
        private final DatabaseCommitThread commitThread;
        private final SLInventoryEntry entry;
        
        private static /* synthetic */ int[] -getcom-lumiyaviewer-lumiya-slproto-inventory-SLInventoryHTTPFetchRequest$FolderValueKeySwitchesValues() {
            if (FolderEntryContentHandler.-com-lumiyaviewer-lumiya-slproto-inventory-SLInventoryHTTPFetchRequest$FolderValueKeySwitchesValues != null) {
                return FolderEntryContentHandler.-com-lumiyaviewer-lumiya-slproto-inventory-SLInventoryHTTPFetchRequest$FolderValueKeySwitchesValues;
            }
            int[] -com-lumiyaviewer-lumiya-slproto-inventory-SLInventoryHTTPFetchRequest$FolderValueKeySwitchesValues = new int[FolderValueKey.values().length];
            while (true) {
                try {
                    -com-lumiyaviewer-lumiya-slproto-inventory-SLInventoryHTTPFetchRequest$FolderValueKeySwitchesValues[FolderValueKey.agent_id.ordinal()] = 1;
                    try {
                        -com-lumiyaviewer-lumiya-slproto-inventory-SLInventoryHTTPFetchRequest$FolderValueKeySwitchesValues[FolderValueKey.category_id.ordinal()] = 2;
                        try {
                            -com-lumiyaviewer-lumiya-slproto-inventory-SLInventoryHTTPFetchRequest$FolderValueKeySwitchesValues[FolderValueKey.folder_id.ordinal()] = 3;
                            try {
                                -com-lumiyaviewer-lumiya-slproto-inventory-SLInventoryHTTPFetchRequest$FolderValueKeySwitchesValues[FolderValueKey.name.ordinal()] = 4;
                                try {
                                    -com-lumiyaviewer-lumiya-slproto-inventory-SLInventoryHTTPFetchRequest$FolderValueKeySwitchesValues[FolderValueKey.parent_id.ordinal()] = 5;
                                    try {
                                        -com-lumiyaviewer-lumiya-slproto-inventory-SLInventoryHTTPFetchRequest$FolderValueKeySwitchesValues[FolderValueKey.preferred_type.ordinal()] = 6;
                                        try {
                                            -com-lumiyaviewer-lumiya-slproto-inventory-SLInventoryHTTPFetchRequest$FolderValueKeySwitchesValues[FolderValueKey.type.ordinal()] = 7;
                                            try {
                                                -com-lumiyaviewer-lumiya-slproto-inventory-SLInventoryHTTPFetchRequest$FolderValueKeySwitchesValues[FolderValueKey.type_default.ordinal()] = 8;
                                                try {
                                                    -com-lumiyaviewer-lumiya-slproto-inventory-SLInventoryHTTPFetchRequest$FolderValueKeySwitchesValues[FolderValueKey.version.ordinal()] = 9;
                                                    return -com-lumiyaviewer-lumiya-slproto-inventory-SLInventoryHTTPFetchRequest$FolderValueKeySwitchesValues = -com-lumiyaviewer-lumiya-slproto-inventory-SLInventoryHTTPFetchRequest$FolderValueKeySwitchesValues;
                                                }
                                                catch (final NoSuchFieldError noSuchFieldError) {}
                                            }
                                            catch (final NoSuchFieldError noSuchFieldError2) {}
                                        }
                                        catch (final NoSuchFieldError noSuchFieldError3) {}
                                    }
                                    catch (final NoSuchFieldError noSuchFieldError4) {}
                                }
                                catch (final NoSuchFieldError noSuchFieldError5) {}
                            }
                            catch (final NoSuchFieldError noSuchFieldError6) {}
                        }
                        catch (final NoSuchFieldError noSuchFieldError7) {}
                    }
                    catch (final NoSuchFieldError noSuchFieldError8) {}
                }
                catch (final NoSuchFieldError noSuchFieldError9) {
                    continue;
                }
                break;
            }
        }
        
        FolderEntryContentHandler(final DatabaseCommitThread commitThread) {
            this.commitThread = commitThread;
            this.entry = new SLInventoryEntry();
            this.entry.isFolder = true;
        }
        
        @Override
        public void onMapEnd(final String s) throws LLSDXMLException, InterruptedException {
            if (this.entry.parentUUID == null) {
                this.entry.parentUUID = SLInventoryHTTPFetchRequest.this.folderEntry.parentUUID;
                this.entry.parent_id = SLInventoryHTTPFetchRequest.this.folderEntry.getId();
            }
            if (this.entry.agentUUID == null) {
                this.entry.agentUUID = SLInventoryHTTPFetchRequest.this.folderEntry.agentUUID;
            }
            this.commitThread.addEntry(this.entry);
        }
        
        @Override
        public void onPrimitiveValue(final String s, final LLSDNode llsdNode) throws LLSDXMLException, LLSDValueTypeException {
            final FolderValueKey byTag = FolderValueKey.byTag(s);
            if (byTag != null) {
                switch (-getcom-lumiyaviewer-lumiya-slproto-inventory-SLInventoryHTTPFetchRequest$FolderValueKeySwitchesValues()[byTag.ordinal()]) {
                    case 1: {
                        this.entry.agentUUID = llsdNode.asUUID();
                        break;
                    }
                    case 2: {
                        this.entry.uuid = llsdNode.asUUID();
                        break;
                    }
                    case 3: {
                        this.entry.uuid = llsdNode.asUUID();
                        break;
                    }
                    case 4: {
                        this.entry.name = llsdNode.asString();
                        break;
                    }
                    case 5: {
                        this.entry.parentUUID = llsdNode.asUUID();
                        if (this.entry.parentUUID.equals(SLInventoryHTTPFetchRequest.this.folderUUID)) {
                            this.entry.parent_id = SLInventoryHTTPFetchRequest.this.folderEntry.getId();
                            break;
                        }
                        final SLInventoryEntry entry = SLInventoryHTTPFetchRequest.this.db.findEntry(this.entry.parentUUID);
                        if (entry != null) {
                            this.entry.parent_id = entry.getId();
                            break;
                        }
                        this.entry.parent_id = 0L;
                        break;
                    }
                    case 7: {
                        if (llsdNode.isInt()) {
                            this.entry.typeDefault = llsdNode.asInt();
                            break;
                        }
                        final SLAssetType byString = SLAssetType.getByString(llsdNode.asString());
                        if (byString != SLAssetType.AT_UNKNOWN) {
                            this.entry.typeDefault = byString.getInventoryType().getTypeCode();
                            break;
                        }
                        this.entry.typeDefault = SLInventoryType.getByString(llsdNode.asString()).getTypeCode();
                        break;
                    }
                    case 8: {
                        this.entry.typeDefault = llsdNode.asInt();
                        break;
                    }
                    case 9: {
                        this.entry.version = llsdNode.asInt();
                        break;
                    }
                }
            }
            else {
                Debug.Printf("InvFetch: Folder unknown key '%s'", s);
            }
        }
    }
    
    private enum FolderValueKey
    {
        agent_id("agent_id", 2), 
        category_id("category_id", 0), 
        folder_id("folder_id", 1), 
        name("name", 3), 
        parent_id("parent_id", 7), 
        preferred_type("preferred_type", 8);
        
        private static final Map<String, FolderValueKey> tagMap;
        
        type("type", 5), 
        type_default("type_default", 4), 
        version("version", 6);
        
        static {
            int i = 0;
            tagMap = new HashMap<String, FolderValueKey>(values().length * 2);
            for (FolderValueKey[] values = values(); i < values.length; ++i) {
                final FolderValueKey folderValueKey = values[i];
                FolderValueKey.tagMap.put(folderValueKey.toString(), folderValueKey);
            }
        }
        
        private FolderValueKey(final String name, final int ordinal) {
        }
        
        public static FolderValueKey byTag(final String s) {
            return FolderValueKey.tagMap.get(s);
        }
    }
    
    private class ItemEntryContentHandler extends LLSDDefaultContentHandler
    {
        private final DatabaseCommitThread commitThread;
        private final SLInventoryEntry entry;
        private final LLSDContentHandler permissionsHandler;
        private final LLSDContentHandler saleInfoHandler;
        
        private static /* synthetic */ int[] -getcom-lumiyaviewer-lumiya-slproto-inventory-SLInventoryHTTPFetchRequest$ItemValueKeySwitchesValues() {
            if (ItemEntryContentHandler.-com-lumiyaviewer-lumiya-slproto-inventory-SLInventoryHTTPFetchRequest$ItemValueKeySwitchesValues != null) {
                return ItemEntryContentHandler.-com-lumiyaviewer-lumiya-slproto-inventory-SLInventoryHTTPFetchRequest$ItemValueKeySwitchesValues;
            }
            int[] -com-lumiyaviewer-lumiya-slproto-inventory-SLInventoryHTTPFetchRequest$ItemValueKeySwitchesValues = new int[ItemValueKey.values().length];
            while (true) {
                try {
                    -com-lumiyaviewer-lumiya-slproto-inventory-SLInventoryHTTPFetchRequest$ItemValueKeySwitchesValues[ItemValueKey.agent_id.ordinal()] = 1;
                    try {
                        -com-lumiyaviewer-lumiya-slproto-inventory-SLInventoryHTTPFetchRequest$ItemValueKeySwitchesValues[ItemValueKey.asset_id.ordinal()] = 2;
                        try {
                            -com-lumiyaviewer-lumiya-slproto-inventory-SLInventoryHTTPFetchRequest$ItemValueKeySwitchesValues[ItemValueKey.created_at.ordinal()] = 3;
                            try {
                                -com-lumiyaviewer-lumiya-slproto-inventory-SLInventoryHTTPFetchRequest$ItemValueKeySwitchesValues[ItemValueKey.desc.ordinal()] = 4;
                                try {
                                    -com-lumiyaviewer-lumiya-slproto-inventory-SLInventoryHTTPFetchRequest$ItemValueKeySwitchesValues[ItemValueKey.flags.ordinal()] = 5;
                                    try {
                                        -com-lumiyaviewer-lumiya-slproto-inventory-SLInventoryHTTPFetchRequest$ItemValueKeySwitchesValues[ItemValueKey.inv_type.ordinal()] = 6;
                                        try {
                                            -com-lumiyaviewer-lumiya-slproto-inventory-SLInventoryHTTPFetchRequest$ItemValueKeySwitchesValues[ItemValueKey.item_id.ordinal()] = 7;
                                            try {
                                                -com-lumiyaviewer-lumiya-slproto-inventory-SLInventoryHTTPFetchRequest$ItemValueKeySwitchesValues[ItemValueKey.name.ordinal()] = 8;
                                                try {
                                                    -com-lumiyaviewer-lumiya-slproto-inventory-SLInventoryHTTPFetchRequest$ItemValueKeySwitchesValues[ItemValueKey.parent_id.ordinal()] = 9;
                                                    try {
                                                        -com-lumiyaviewer-lumiya-slproto-inventory-SLInventoryHTTPFetchRequest$ItemValueKeySwitchesValues[ItemValueKey.type.ordinal()] = 10;
                                                        return -com-lumiyaviewer-lumiya-slproto-inventory-SLInventoryHTTPFetchRequest$ItemValueKeySwitchesValues = -com-lumiyaviewer-lumiya-slproto-inventory-SLInventoryHTTPFetchRequest$ItemValueKeySwitchesValues;
                                                    }
                                                    catch (final NoSuchFieldError noSuchFieldError) {}
                                                }
                                                catch (final NoSuchFieldError noSuchFieldError2) {}
                                            }
                                            catch (final NoSuchFieldError noSuchFieldError3) {}
                                        }
                                        catch (final NoSuchFieldError noSuchFieldError4) {}
                                    }
                                    catch (final NoSuchFieldError noSuchFieldError5) {}
                                }
                                catch (final NoSuchFieldError noSuchFieldError6) {}
                            }
                            catch (final NoSuchFieldError noSuchFieldError7) {}
                        }
                        catch (final NoSuchFieldError noSuchFieldError8) {}
                    }
                    catch (final NoSuchFieldError noSuchFieldError9) {}
                }
                catch (final NoSuchFieldError noSuchFieldError10) {
                    continue;
                }
                break;
            }
        }
        
        ItemEntryContentHandler(final DatabaseCommitThread commitThread) {
            this.permissionsHandler = new LLSDDefaultContentHandler() {
                private static /* synthetic */ int[] -getcom-lumiyaviewer-lumiya-slproto-inventory-SLInventoryHTTPFetchRequest$PermissionsValueKeySwitchesValues() {
                    if (SLInventoryHTTPFetchRequest$ItemEntryContentHandler$1.-com-lumiyaviewer-lumiya-slproto-inventory-SLInventoryHTTPFetchRequest$PermissionsValueKeySwitchesValues != null) {
                        return SLInventoryHTTPFetchRequest$ItemEntryContentHandler$1.-com-lumiyaviewer-lumiya-slproto-inventory-SLInventoryHTTPFetchRequest$PermissionsValueKeySwitchesValues;
                    }
                    int[] -com-lumiyaviewer-lumiya-slproto-inventory-SLInventoryHTTPFetchRequest$PermissionsValueKeySwitchesValues = new int[PermissionsValueKey.values().length];
                    while (true) {
                        try {
                            -com-lumiyaviewer-lumiya-slproto-inventory-SLInventoryHTTPFetchRequest$PermissionsValueKeySwitchesValues[PermissionsValueKey.base_mask.ordinal()] = 1;
                            try {
                                -com-lumiyaviewer-lumiya-slproto-inventory-SLInventoryHTTPFetchRequest$PermissionsValueKeySwitchesValues[PermissionsValueKey.creator_id.ordinal()] = 2;
                                try {
                                    -com-lumiyaviewer-lumiya-slproto-inventory-SLInventoryHTTPFetchRequest$PermissionsValueKeySwitchesValues[PermissionsValueKey.everyone_mask.ordinal()] = 3;
                                    try {
                                        -com-lumiyaviewer-lumiya-slproto-inventory-SLInventoryHTTPFetchRequest$PermissionsValueKeySwitchesValues[PermissionsValueKey.group_id.ordinal()] = 4;
                                        try {
                                            -com-lumiyaviewer-lumiya-slproto-inventory-SLInventoryHTTPFetchRequest$PermissionsValueKeySwitchesValues[PermissionsValueKey.group_mask.ordinal()] = 5;
                                            try {
                                                -com-lumiyaviewer-lumiya-slproto-inventory-SLInventoryHTTPFetchRequest$PermissionsValueKeySwitchesValues[PermissionsValueKey.is_owner_group.ordinal()] = 6;
                                                try {
                                                    -com-lumiyaviewer-lumiya-slproto-inventory-SLInventoryHTTPFetchRequest$PermissionsValueKeySwitchesValues[PermissionsValueKey.last_owner_id.ordinal()] = 7;
                                                    try {
                                                        -com-lumiyaviewer-lumiya-slproto-inventory-SLInventoryHTTPFetchRequest$PermissionsValueKeySwitchesValues[PermissionsValueKey.next_owner_mask.ordinal()] = 8;
                                                        try {
                                                            -com-lumiyaviewer-lumiya-slproto-inventory-SLInventoryHTTPFetchRequest$PermissionsValueKeySwitchesValues[PermissionsValueKey.owner_id.ordinal()] = 9;
                                                            try {
                                                                -com-lumiyaviewer-lumiya-slproto-inventory-SLInventoryHTTPFetchRequest$PermissionsValueKeySwitchesValues[PermissionsValueKey.owner_mask.ordinal()] = 10;
                                                                return -com-lumiyaviewer-lumiya-slproto-inventory-SLInventoryHTTPFetchRequest$PermissionsValueKeySwitchesValues = -com-lumiyaviewer-lumiya-slproto-inventory-SLInventoryHTTPFetchRequest$PermissionsValueKeySwitchesValues;
                                                            }
                                                            catch (final NoSuchFieldError noSuchFieldError) {}
                                                        }
                                                        catch (final NoSuchFieldError noSuchFieldError2) {}
                                                    }
                                                    catch (final NoSuchFieldError noSuchFieldError3) {}
                                                }
                                                catch (final NoSuchFieldError noSuchFieldError4) {}
                                            }
                                            catch (final NoSuchFieldError noSuchFieldError5) {}
                                        }
                                        catch (final NoSuchFieldError noSuchFieldError6) {}
                                    }
                                    catch (final NoSuchFieldError noSuchFieldError7) {}
                                }
                                catch (final NoSuchFieldError noSuchFieldError8) {}
                            }
                            catch (final NoSuchFieldError noSuchFieldError9) {}
                        }
                        catch (final NoSuchFieldError noSuchFieldError10) {
                            continue;
                        }
                        break;
                    }
                }
                
                @Override
                public void onPrimitiveValue(final String s, final LLSDNode llsdNode) throws LLSDXMLException, LLSDValueTypeException {
                    final PermissionsValueKey byTag = PermissionsValueKey.byTag(s);
                    if (byTag != null) {
                        switch (-getcom-lumiyaviewer-lumiya-slproto-inventory-SLInventoryHTTPFetchRequest$PermissionsValueKeySwitchesValues()[byTag.ordinal()]) {
                            case 1: {
                                ItemEntryContentHandler.this.entry.baseMask = llsdNode.asInt();
                                break;
                            }
                            case 3: {
                                ItemEntryContentHandler.this.entry.everyoneMask = llsdNode.asInt();
                                break;
                            }
                            case 5: {
                                ItemEntryContentHandler.this.entry.groupMask = llsdNode.asInt();
                                break;
                            }
                            case 8: {
                                ItemEntryContentHandler.this.entry.nextOwnerMask = llsdNode.asInt();
                                break;
                            }
                            case 10: {
                                ItemEntryContentHandler.this.entry.ownerMask = llsdNode.asInt();
                                break;
                            }
                            case 2: {
                                ItemEntryContentHandler.this.entry.creatorUUID = llsdNode.asUUID();
                                break;
                            }
                            case 4: {
                                ItemEntryContentHandler.this.entry.groupUUID = llsdNode.asUUID();
                                break;
                            }
                            case 7: {
                                ItemEntryContentHandler.this.entry.lastOwnerUUID = llsdNode.asUUID();
                                break;
                            }
                            case 9: {
                                ItemEntryContentHandler.this.entry.ownerUUID = llsdNode.asUUID();
                                break;
                            }
                            case 6: {
                                ItemEntryContentHandler.this.entry.isGroupOwned = llsdNode.asBoolean();
                                break;
                            }
                        }
                    }
                    else {
                        Debug.Printf("InvFetch: Permissions unknown key '%s'", s);
                    }
                }
            };
            this.saleInfoHandler = new LLSDDefaultContentHandler() {
                @Override
                public void onPrimitiveValue(final String s, final LLSDNode llsdNode) throws LLSDXMLException, LLSDValueTypeException {
                    if (s.equals("sale_type")) {
                        if (llsdNode.isString()) {
                            ItemEntryContentHandler.this.entry.saleType = SLSaleType.getByString(llsdNode.asString()).getTypeCode();
                        }
                        else {
                            ItemEntryContentHandler.this.entry.saleType = llsdNode.asInt();
                        }
                    }
                    else if (s.equals("sale_price")) {
                        ItemEntryContentHandler.this.entry.salePrice = llsdNode.asInt();
                    }
                    else {
                        Debug.Printf("InvFetch: Sale info unknown key '%s'", s);
                    }
                }
            };
            this.commitThread = commitThread;
            this.entry = new SLInventoryEntry();
            this.entry.isFolder = false;
        }
        
        @Override
        public LLSDContentHandler onMapBegin(final String s) throws LLSDXMLException {
            if (s.equals("permissions")) {
                return this.permissionsHandler;
            }
            if (s.equals("sale_info")) {
                return this.saleInfoHandler;
            }
            return super.onMapBegin(s);
        }
        
        @Override
        public void onMapEnd(final String s) throws LLSDXMLException, InterruptedException {
            if (this.entry.parentUUID == null) {
                this.entry.parentUUID = SLInventoryHTTPFetchRequest.this.folderEntry.parentUUID;
                this.entry.parent_id = SLInventoryHTTPFetchRequest.this.folderEntry.getId();
            }
            if (this.entry.agentUUID == null) {
                this.entry.agentUUID = SLInventoryHTTPFetchRequest.this.folderEntry.agentUUID;
            }
            this.commitThread.addEntry(this.entry);
        }
        
        @Override
        public void onPrimitiveValue(final String s, final LLSDNode llsdNode) throws LLSDXMLException, LLSDValueTypeException {
            final ItemValueKey byTag = ItemValueKey.byTag(s);
            if (byTag != null) {
                switch (-getcom-lumiyaviewer-lumiya-slproto-inventory-SLInventoryHTTPFetchRequest$ItemValueKeySwitchesValues()[byTag.ordinal()]) {
                    case 1: {
                        this.entry.agentUUID = llsdNode.asUUID();
                        break;
                    }
                    case 7: {
                        this.entry.uuid = llsdNode.asUUID();
                        break;
                    }
                    case 8: {
                        this.entry.name = llsdNode.asString();
                        break;
                    }
                    case 2: {
                        this.entry.assetUUID = llsdNode.asUUID();
                        break;
                    }
                    case 9: {
                        this.entry.parentUUID = llsdNode.asUUID();
                        if (this.entry.parentUUID.equals(SLInventoryHTTPFetchRequest.this.folderUUID)) {
                            this.entry.parent_id = SLInventoryHTTPFetchRequest.this.folderEntry.getId();
                            break;
                        }
                        final SLInventoryEntry entry = SLInventoryHTTPFetchRequest.this.db.findEntry(this.entry.parentUUID);
                        if (entry != null) {
                            this.entry.parent_id = entry.getId();
                            break;
                        }
                        this.entry.parent_id = 0L;
                        break;
                    }
                    case 10: {
                        if (llsdNode.isInt()) {
                            this.entry.assetType = llsdNode.asInt();
                            break;
                        }
                        this.entry.assetType = SLAssetType.getByString(llsdNode.asString()).getTypeCode();
                        break;
                    }
                    case 6: {
                        if (llsdNode.isInt()) {
                            this.entry.invType = llsdNode.asInt();
                            break;
                        }
                        this.entry.invType = SLInventoryType.getByString(llsdNode.asString()).getTypeCode();
                        break;
                    }
                    case 4: {
                        this.entry.description = llsdNode.asString();
                        break;
                    }
                    case 3: {
                        this.entry.creationDate = llsdNode.asInt();
                        break;
                    }
                    case 5: {
                        this.entry.flags = llsdNode.asInt();
                        break;
                    }
                }
            }
            else {
                Debug.Printf("InvFetch: Item unknown key '%s'", s);
            }
        }
    }
    
    private enum ItemValueKey
    {
        agent_id("agent_id", 3), 
        asset_id("asset_id", 9), 
        created_at("created_at", 8), 
        desc("desc", 6), 
        flags("flags", 7), 
        inv_type("inv_type", 5), 
        item_id("item_id", 0), 
        name("name", 1), 
        parent_id("parent_id", 2);
        
        private static final Map<String, ItemValueKey> tagMap;
        
        type("type", 4);
        
        static {
            int i = 0;
            tagMap = new HashMap<String, ItemValueKey>(values().length * 2);
            for (ItemValueKey[] values = values(); i < values.length; ++i) {
                final ItemValueKey itemValueKey = values[i];
                ItemValueKey.tagMap.put(itemValueKey.toString(), itemValueKey);
            }
        }
        
        private ItemValueKey(final String name, final int ordinal) {
        }
        
        public static ItemValueKey byTag(final String s) {
            return ItemValueKey.tagMap.get(s);
        }
    }
    
    private enum PermissionsValueKey
    {
        base_mask("base_mask", 5), 
        creator_id("creator_id", 0), 
        everyone_mask("everyone_mask", 9), 
        group_id("group_id", 1), 
        group_mask("group_mask", 8), 
        is_owner_group("is_owner_group", 4), 
        last_owner_id("last_owner_id", 3), 
        next_owner_mask("next_owner_mask", 7), 
        owner_id("owner_id", 2), 
        owner_mask("owner_mask", 6);
        
        private static final Map<String, PermissionsValueKey> tagMap;
        
        static {
            int i = 0;
            tagMap = new HashMap<String, PermissionsValueKey>(values().length * 2);
            for (PermissionsValueKey[] values = values(); i < values.length; ++i) {
                final PermissionsValueKey permissionsValueKey = values[i];
                PermissionsValueKey.tagMap.put(permissionsValueKey.toString(), permissionsValueKey);
            }
        }
        
        private PermissionsValueKey(final String name, final int ordinal) {
        }
        
        public static PermissionsValueKey byTag(final String s) {
            return PermissionsValueKey.tagMap.get(s);
        }
    }
    
    private class RootContentHandler extends LLSDDefaultContentHandler
    {
        private final DatabaseCommitThread commitThread;
        
        private RootContentHandler(final DatabaseCommitThread commitThread) {
            this.commitThread = commitThread;
        }
        
        @Override
        public LLSDContentHandler onMapBegin(final String s) throws LLSDXMLException {
            return new LLSDDefaultContentHandler() {
                @Override
                public LLSDContentHandler onArrayBegin(final String s) throws LLSDXMLException {
                    if (s.equals("folders")) {
                        return new LLSDDefaultContentHandler() {
                            @Override
                            public LLSDContentHandler onMapBegin(final String s) throws LLSDXMLException {
                                return new FolderDataContentHandler(RootContentHandler.this.commitThread, (FolderDataContentHandler)null);
                            }
                        };
                    }
                    return super.onArrayBegin(s);
                }
            };
        }
    }
}
