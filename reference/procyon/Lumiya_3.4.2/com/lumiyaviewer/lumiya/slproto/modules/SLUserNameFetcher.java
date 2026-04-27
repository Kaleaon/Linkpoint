// 
// Decompiled by Procyon v0.6.0
// 

package com.lumiyaviewer.lumiya.slproto.modules;

import com.lumiyaviewer.lumiya.slproto.handler.SLMessageHandler;
import com.lumiyaviewer.lumiya.slproto.messages.UUIDNameReply;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import com.lumiyaviewer.lumiya.slproto.SLMessage;
import com.lumiyaviewer.lumiya.slproto.messages.UUIDNameRequest;
import java.util.concurrent.locks.ReentrantLock;
import com.lumiyaviewer.lumiya.slproto.SLAgentCircuit;
import com.lumiyaviewer.lumiya.slproto.https.LLSDXMLRequest;
import java.util.UUID;
import com.lumiyaviewer.lumiya.utils.reqset.WeakPriorityRequestSet;
import com.lumiyaviewer.lumiya.slproto.users.manager.UserManager;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.Condition;
import com.lumiyaviewer.lumiya.slproto.caps.SLCaps;
import com.lumiyaviewer.lumiya.utils.reqset.RequestListener;

public class SLUserNameFetcher extends SLModule implements RequestListener
{
    private static final int MAX_BATCH_SIZE = 4;
    private static final long REPLY_TIMEOUT = 10000L;
    private final SLCaps caps;
    private final Condition hasNamesToFetch;
    private boolean isWaitingReply;
    private final Lock lock;
    private volatile boolean threadMustExit;
    private final Runnable threadRunnable;
    private final Object udpLock;
    private final UserManager userManager;
    private final WeakPriorityRequestSet<UUID> userNameRequests;
    private long waitingReplySince;
    private final Thread workingThread;
    private final LLSDXMLRequest xmlReq;
    
    public SLUserNameFetcher(final SLAgentCircuit slAgentCircuit, final SLCaps caps) {
        super(slAgentCircuit);
        this.lock = new ReentrantLock();
        this.hasNamesToFetch = this.lock.newCondition();
        this.udpLock = new Object();
        this.waitingReplySince = 0L;
        this.threadRunnable = new Runnable() {
            final /* synthetic */ SLUserNameFetcher this$0;
            
            @Override
            public void run() {
                while (true) {
                    if (SLUserNameFetcher.this.threadMustExit) {
                        return;
                    }
                    try {
                        while (SLUserNameFetcher.this.FetchSomeNamesOverHTTP()) {}
                        SLUserNameFetcher.this.lock.lock();
                        final Runnable runnable = this;
                        final SLUserNameFetcher slUserNameFetcher = runnable.this$0;
                        final Condition condition = slUserNameFetcher.hasNamesToFetch;
                        condition.await();
                        continue;
                    }
                    catch (final InterruptedException ex) {}
                    try {
                        final Runnable runnable = this;
                        final SLUserNameFetcher slUserNameFetcher = runnable.this$0;
                        final Condition condition = slUserNameFetcher.hasNamesToFetch;
                        condition.await();
                        continue;
                    }
                    finally {
                        SLUserNameFetcher.this.lock.unlock();
                    }
                }
            }
        };
        this.userManager = UserManager.getUserManager(slAgentCircuit.circuitInfo.agentID);
        this.caps = caps;
        this.threadMustExit = false;
        if (caps.getCapability(SLCaps.SLCapability.GetDisplayNames) != null) {
            this.xmlReq = new LLSDXMLRequest();
            (this.workingThread = new Thread(this.threadRunnable, "DisplayNameFetcher")).start();
        }
        else {
            this.workingThread = null;
            this.xmlReq = null;
        }
        if (this.userManager != null) {
            (this.userNameRequests = this.userManager.getUserNameRequests()).addListener(this);
        }
        else {
            this.userNameRequests = null;
        }
    }
    
    private boolean FetchSomeNamesOverHTTP() {
        // 
        // This method could not be decompiled.
        // 
        // Original Bytecode:
        // 
        //     1: istore_1       
        //     2: aload_0        
        //     3: iconst_4       
        //     4: invokespecial   com/lumiyaviewer/lumiya/slproto/modules/SLUserNameFetcher.getUUIDsToFetch:(I)Ljava/util/List;
        //     7: astore_2       
        //     8: aload_2        
        //     9: invokeinterface java/util/List.isEmpty:()Z
        //    14: ifeq            19
        //    17: iconst_0       
        //    18: ireturn        
        //    19: new             Ljava/lang/StringBuilder;
        //    22: dup            
        //    23: invokespecial   java/lang/StringBuilder.<init>:()V
        //    26: aload_0        
        //    27: getfield        com/lumiyaviewer/lumiya/slproto/modules/SLUserNameFetcher.caps:Lcom/lumiyaviewer/lumiya/slproto/caps/SLCaps;
        //    30: getstatic       com/lumiyaviewer/lumiya/slproto/caps/SLCaps$SLCapability.GetDisplayNames:Lcom/lumiyaviewer/lumiya/slproto/caps/SLCaps$SLCapability;
        //    33: invokevirtual   com/lumiyaviewer/lumiya/slproto/caps/SLCaps.getCapability:(Lcom/lumiyaviewer/lumiya/slproto/caps/SLCaps$SLCapability;)Ljava/lang/String;
        //    36: invokevirtual   java/lang/StringBuilder.append:(Ljava/lang/String;)Ljava/lang/StringBuilder;
        //    39: ldc             "/"
        //    41: invokevirtual   java/lang/StringBuilder.append:(Ljava/lang/String;)Ljava/lang/StringBuilder;
        //    44: invokevirtual   java/lang/StringBuilder.toString:()Ljava/lang/String;
        //    47: astore_3       
        //    48: aload_2        
        //    49: invokeinterface java/lang/Iterable.iterator:()Ljava/util/Iterator;
        //    54: astore_2       
        //    55: iconst_1       
        //    56: istore          4
        //    58: aload_2        
        //    59: invokeinterface java/util/Iterator.hasNext:()Z
        //    64: ifeq            160
        //    67: aload_2        
        //    68: invokeinterface java/util/Iterator.next:()Ljava/lang/Object;
        //    73: checkcast       Ljava/util/UUID;
        //    76: astore          5
        //    78: iload           4
        //    80: ifeq            137
        //    83: new             Ljava/lang/StringBuilder;
        //    86: dup            
        //    87: invokespecial   java/lang/StringBuilder.<init>:()V
        //    90: aload_3        
        //    91: invokevirtual   java/lang/StringBuilder.append:(Ljava/lang/String;)Ljava/lang/StringBuilder;
        //    94: ldc             "?"
        //    96: invokevirtual   java/lang/StringBuilder.append:(Ljava/lang/String;)Ljava/lang/StringBuilder;
        //    99: invokevirtual   java/lang/StringBuilder.toString:()Ljava/lang/String;
        //   102: astore_3       
        //   103: new             Ljava/lang/StringBuilder;
        //   106: dup            
        //   107: invokespecial   java/lang/StringBuilder.<init>:()V
        //   110: aload_3        
        //   111: invokevirtual   java/lang/StringBuilder.append:(Ljava/lang/String;)Ljava/lang/StringBuilder;
        //   114: ldc             "ids="
        //   116: invokevirtual   java/lang/StringBuilder.append:(Ljava/lang/String;)Ljava/lang/StringBuilder;
        //   119: aload           5
        //   121: invokevirtual   java/util/UUID.toString:()Ljava/lang/String;
        //   124: invokevirtual   java/lang/StringBuilder.append:(Ljava/lang/String;)Ljava/lang/StringBuilder;
        //   127: invokevirtual   java/lang/StringBuilder.toString:()Ljava/lang/String;
        //   130: astore_3       
        //   131: iconst_0       
        //   132: istore          4
        //   134: goto            58
        //   137: new             Ljava/lang/StringBuilder;
        //   140: dup            
        //   141: invokespecial   java/lang/StringBuilder.<init>:()V
        //   144: aload_3        
        //   145: invokevirtual   java/lang/StringBuilder.append:(Ljava/lang/String;)Ljava/lang/StringBuilder;
        //   148: ldc             "&"
        //   150: invokevirtual   java/lang/StringBuilder.append:(Ljava/lang/String;)Ljava/lang/StringBuilder;
        //   153: invokevirtual   java/lang/StringBuilder.toString:()Ljava/lang/String;
        //   156: astore_3       
        //   157: goto            103
        //   160: aload_0        
        //   161: getfield        com/lumiyaviewer/lumiya/slproto/modules/SLUserNameFetcher.xmlReq:Lcom/lumiyaviewer/lumiya/slproto/https/LLSDXMLRequest;
        //   164: aload_3        
        //   165: aconst_null    
        //   166: invokevirtual   com/lumiyaviewer/lumiya/slproto/https/LLSDXMLRequest.PerformRequest:(Ljava/lang/String;Lcom/lumiyaviewer/lumiya/slproto/llsd/LLSDNode;)Lcom/lumiyaviewer/lumiya/slproto/llsd/LLSDNode;
        //   169: astore_3       
        //   170: aload_3        
        //   171: ifnull          376
        //   174: aload_3        
        //   175: ldc             "agents"
        //   177: invokevirtual   com/lumiyaviewer/lumiya/slproto/llsd/LLSDNode.keyExists:(Ljava/lang/String;)Z
        //   180: ifeq            301
        //   183: aload_3        
        //   184: ldc             "agents"
        //   186: invokevirtual   com/lumiyaviewer/lumiya/slproto/llsd/LLSDNode.byKey:(Ljava/lang/String;)Lcom/lumiyaviewer/lumiya/slproto/llsd/LLSDNode;
        //   189: astore_2       
        //   190: iconst_0       
        //   191: istore          4
        //   193: iload           4
        //   195: aload_2        
        //   196: invokevirtual   com/lumiyaviewer/lumiya/slproto/llsd/LLSDNode.getCount:()I
        //   199: if_icmpge       301
        //   202: aload_2        
        //   203: iload           4
        //   205: invokevirtual   com/lumiyaviewer/lumiya/slproto/llsd/LLSDNode.byIndex:(I)Lcom/lumiyaviewer/lumiya/slproto/llsd/LLSDNode;
        //   208: astore          6
        //   210: aload           6
        //   212: ldc             "id"
        //   214: invokevirtual   com/lumiyaviewer/lumiya/slproto/llsd/LLSDNode.byKey:(Ljava/lang/String;)Lcom/lumiyaviewer/lumiya/slproto/llsd/LLSDNode;
        //   217: invokevirtual   com/lumiyaviewer/lumiya/slproto/llsd/LLSDNode.asUUID:()Ljava/util/UUID;
        //   220: astore          7
        //   222: aload           6
        //   224: ldc             "display_name"
        //   226: invokevirtual   com/lumiyaviewer/lumiya/slproto/llsd/LLSDNode.byKey:(Ljava/lang/String;)Lcom/lumiyaviewer/lumiya/slproto/llsd/LLSDNode;
        //   229: invokevirtual   com/lumiyaviewer/lumiya/slproto/llsd/LLSDNode.asString:()Ljava/lang/String;
        //   232: astore          5
        //   234: aload           6
        //   236: ldc             "username"
        //   238: invokevirtual   com/lumiyaviewer/lumiya/slproto/llsd/LLSDNode.byKey:(Ljava/lang/String;)Lcom/lumiyaviewer/lumiya/slproto/llsd/LLSDNode;
        //   241: invokevirtual   com/lumiyaviewer/lumiya/slproto/llsd/LLSDNode.asString:()Ljava/lang/String;
        //   244: astore          6
        //   246: aload_0        
        //   247: getfield        com/lumiyaviewer/lumiya/slproto/modules/SLUserNameFetcher.userManager:Lcom/lumiyaviewer/lumiya/slproto/users/manager/UserManager;
        //   250: ifnull          275
        //   253: aload_0        
        //   254: getfield        com/lumiyaviewer/lumiya/slproto/modules/SLUserNameFetcher.userManager:Lcom/lumiyaviewer/lumiya/slproto/users/manager/UserManager;
        //   257: aload           7
        //   259: aload           6
        //   261: aload           5
        //   263: invokevirtual   com/lumiyaviewer/lumiya/slproto/users/manager/UserManager.updateUserNames:(Ljava/util/UUID;Ljava/lang/String;Ljava/lang/String;)V
        //   266: aload_0        
        //   267: getfield        com/lumiyaviewer/lumiya/slproto/modules/SLUserNameFetcher.userNameRequests:Lcom/lumiyaviewer/lumiya/utils/reqset/WeakPriorityRequestSet;
        //   270: aload           7
        //   272: invokevirtual   com/lumiyaviewer/lumiya/utils/reqset/WeakPriorityRequestSet.completeRequest:(Ljava/lang/Object;)V
        //   275: iinc            4, 1
        //   278: goto            193
        //   281: astore_3       
        //   282: aload_3        
        //   283: invokevirtual   java/io/IOException.printStackTrace:()V
        //   286: aconst_null    
        //   287: astore_3       
        //   288: goto            170
        //   291: astore_3       
        //   292: aload_3        
        //   293: invokevirtual   com/lumiyaviewer/lumiya/slproto/llsd/LLSDXMLException.printStackTrace:()V
        //   296: aconst_null    
        //   297: astore_3       
        //   298: goto            170
        //   301: aload_3        
        //   302: ldc             "bad_ids"
        //   304: invokevirtual   com/lumiyaviewer/lumiya/slproto/llsd/LLSDNode.keyExists:(Ljava/lang/String;)Z
        //   307: ifeq            376
        //   310: aload_3        
        //   311: ldc             "bad_ids"
        //   313: invokevirtual   com/lumiyaviewer/lumiya/slproto/llsd/LLSDNode.byKey:(Ljava/lang/String;)Lcom/lumiyaviewer/lumiya/slproto/llsd/LLSDNode;
        //   316: astore_3       
        //   317: iload_1        
        //   318: istore          4
        //   320: iload           4
        //   322: aload_3        
        //   323: invokevirtual   com/lumiyaviewer/lumiya/slproto/llsd/LLSDNode.getCount:()I
        //   326: if_icmpge       376
        //   329: aload_3        
        //   330: iload           4
        //   332: invokevirtual   com/lumiyaviewer/lumiya/slproto/llsd/LLSDNode.byIndex:(I)Lcom/lumiyaviewer/lumiya/slproto/llsd/LLSDNode;
        //   335: invokevirtual   com/lumiyaviewer/lumiya/slproto/llsd/LLSDNode.asString:()Ljava/lang/String;
        //   338: invokestatic    java/util/UUID.fromString:(Ljava/lang/String;)Ljava/util/UUID;
        //   341: astore_2       
        //   342: aload_0        
        //   343: getfield        com/lumiyaviewer/lumiya/slproto/modules/SLUserNameFetcher.userManager:Lcom/lumiyaviewer/lumiya/slproto/users/manager/UserManager;
        //   346: ifnull          365
        //   349: aload_0        
        //   350: getfield        com/lumiyaviewer/lumiya/slproto/modules/SLUserNameFetcher.userManager:Lcom/lumiyaviewer/lumiya/slproto/users/manager/UserManager;
        //   353: aload_2        
        //   354: invokevirtual   com/lumiyaviewer/lumiya/slproto/users/manager/UserManager.setUserBadUUID:(Ljava/util/UUID;)V
        //   357: aload_0        
        //   358: getfield        com/lumiyaviewer/lumiya/slproto/modules/SLUserNameFetcher.userNameRequests:Lcom/lumiyaviewer/lumiya/utils/reqset/WeakPriorityRequestSet;
        //   361: aload_2        
        //   362: invokevirtual   com/lumiyaviewer/lumiya/utils/reqset/WeakPriorityRequestSet.completeRequest:(Ljava/lang/Object;)V
        //   365: iinc            4, 1
        //   368: goto            320
        //   371: astore_3       
        //   372: aload_3        
        //   373: invokevirtual   com/lumiyaviewer/lumiya/slproto/llsd/LLSDException.printStackTrace:()V
        //   376: iconst_1       
        //   377: ireturn        
        //    Exceptions:
        //  Try           Handler
        //  Start  End    Start  End    Type                                                   
        //  -----  -----  -----  -----  -------------------------------------------------------
        //  160    170    291    301    Lcom/lumiyaviewer/lumiya/slproto/llsd/LLSDXMLException;
        //  160    170    281    291    Ljava/io/IOException;
        //  174    190    371    376    Lcom/lumiyaviewer/lumiya/slproto/llsd/LLSDException;
        //  193    275    371    376    Lcom/lumiyaviewer/lumiya/slproto/llsd/LLSDException;
        //  301    317    371    376    Lcom/lumiyaviewer/lumiya/slproto/llsd/LLSDException;
        //  320    365    371    376    Lcom/lumiyaviewer/lumiya/slproto/llsd/LLSDException;
        // 
        // The error that occurred was:
        // 
        // java.lang.IllegalStateException: Expression is linked from several locations: Label_0193:
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
    
    private void FetchSomeNamesOverUDP() {
        final List<UUID> uuiDsToFetch = this.getUUIDsToFetch(4);
        if (uuiDsToFetch.isEmpty()) {
            this.isWaitingReply = false;
            return;
        }
        final UUIDNameRequest uuidNameRequest = new UUIDNameRequest();
        for (final UUID id : uuiDsToFetch) {
            final UUIDNameRequest.UUIDNameBlock e = new UUIDNameRequest.UUIDNameBlock();
            e.ID = id;
            uuidNameRequest.UUIDNameBlock_Fields.add(e);
        }
        this.isWaitingReply = true;
        this.waitingReplySince = System.currentTimeMillis();
        uuidNameRequest.isReliable = true;
        this.SendMessage(uuidNameRequest);
    }
    
    private List<UUID> getUUIDsToFetch(final int initialCapacity) {
        final ArrayList list = new ArrayList(initialCapacity);
        if (this.userNameRequests != null) {
            while (list.size() < initialCapacity) {
                final UUID uuid = this.userNameRequests.getRequest();
                if (uuid == null) {
                    break;
                }
                list.add(uuid);
            }
        }
        return list;
    }
    
    @Override
    public void HandleCloseCircuit() {
        this.threadMustExit = true;
        if (this.xmlReq != null) {
            this.xmlReq.InterruptRequest();
        }
        if (this.workingThread != null) {
            this.workingThread.interrupt();
        }
        if (this.userNameRequests != null) {
            this.userNameRequests.removeListener(this);
        }
    }
    
    @SLMessageHandler
    public void HandleUUIDNameReply(final UUIDNameReply uuidNameReply) {
        synchronized (this) {
            for (final UUIDNameReply.UUIDNameBlock uuidNameBlock : uuidNameReply.UUIDNameBlock_Fields) {
                final UUID id = uuidNameBlock.ID;
                final String string = SLMessage.stringFromVariableOEM(uuidNameBlock.FirstName) + " " + SLMessage.stringFromVariableOEM(uuidNameBlock.LastName);
                if (this.userManager != null) {
                    this.userManager.updateUserNames(id, string, string);
                    this.userNameRequests.completeRequest(id);
                }
            }
        }
        synchronized (this.udpLock) {
            this.isWaitingReply = false;
            this.FetchSomeNamesOverUDP();
            monitorexit(this.udpLock);
        }
    }
    
    @Override
    public void onNewRequest() {
        Label_0047: {
            if (this.workingThread == null) {
                break Label_0047;
            }
            this.lock.lock();
            try {
                this.hasNamesToFetch.signal();
                return;
            }
            finally {
                this.lock.unlock();
            }
        }
        synchronized (this.udpLock) {
            if (!this.isWaitingReply || System.currentTimeMillis() > this.waitingReplySince + 10000L) {
                this.FetchSomeNamesOverUDP();
            }
        }
    }
}
