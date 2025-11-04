// 
// Decompiled by Procyon v0.6.0
// 

package com.lumiyaviewer.lumiya.utils.wlist;

import java.util.ArrayList;
import java.util.List;
import com.lumiyaviewer.lumiya.Debug;
import java.util.HashMap;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.Map;
import java.util.concurrent.atomic.AtomicBoolean;
import javax.annotation.Nullable;
import javax.annotation.Nonnull;
import java.util.concurrent.Executor;
import java.util.Comparator;
import java.util.Queue;
import java.util.RandomAccess;
import java.util.AbstractList;
import com.lumiyaviewer.lumiya.utils.Identifiable;

public class ChunkedListLoader<E extends Identifiable<Long>> extends AbstractList<E> implements ChunkFactory<E>, RandomAccess
{
    private final Queue<E> addedElements;
    private final Comparator<E> chatMessageComparator;
    @Nonnull
    private final Executor executor;
    private boolean hasAbove;
    private boolean hasBelow;
    @Nonnull
    private ChunkedList<E> items;
    @Nonnull
    private final EventListener listener;
    @Nonnull
    private final Executor listenerExecutor;
    @Nullable
    private LoadResult<E> loadAboveResult;
    private long loadAboveTopmostId;
    private boolean loadAboveWanted;
    private long loadBelowLastId;
    @Nullable
    private LoadResult<E> loadBelowResult;
    private boolean loadBelowWanted;
    private final Runnable loadMoreData;
    private final AtomicBoolean loadRequested;
    private final Object lock;
    private final Runnable processUpdate;
    private final AtomicBoolean reloadAccepted;
    private final AtomicBoolean reloadRequested;
    private final boolean startFromStart;
    private final AtomicBoolean updatePosted;
    private final Map<Long, E> updatedElements;
    private final int windowSize;
    
    public ChunkedListLoader(final int windowSize, @Nonnull final Executor executor, final boolean startFromStart, @Nonnull final EventListener listener) {
        this.addedElements = new ConcurrentLinkedQueue<E>();
        this.updatePosted = new AtomicBoolean();
        this.loadRequested = new AtomicBoolean();
        this.reloadRequested = new AtomicBoolean();
        this.reloadAccepted = new AtomicBoolean();
        this.lock = new Object();
        this.loadAboveWanted = false;
        this.loadAboveResult = null;
        this.loadBelowWanted = false;
        this.loadBelowResult = null;
        this.updatedElements = new HashMap<Long, E>();
        this.loadMoreData = new Runnable() {
            @Override
            public void run() {
                // 
                // This method could not be decompiled.
                // 
                // Original Bytecode:
                // 
                //     1: istore_1       
                //     2: ldc             "ChatView: processing loadMoreData(), reloadRequested %b"
                //     4: iconst_1       
                //     5: anewarray       Ljava/lang/Object;
                //     8: dup            
                //     9: iconst_0       
                //    10: aload_0        
                //    11: getfield        com/lumiyaviewer/lumiya/utils/wlist/ChunkedListLoader$1.this$0:Lcom/lumiyaviewer/lumiya/utils/wlist/ChunkedListLoader;
                //    14: invokestatic    com/lumiyaviewer/lumiya/utils/wlist/ChunkedListLoader.-get14:(Lcom/lumiyaviewer/lumiya/utils/wlist/ChunkedListLoader;)Ljava/util/concurrent/atomic/AtomicBoolean;
                //    17: invokevirtual   java/util/concurrent/atomic/AtomicBoolean.get:()Z
                //    20: invokestatic    java/lang/Boolean.valueOf:(Z)Ljava/lang/Boolean;
                //    23: aastore        
                //    24: invokestatic    com/lumiyaviewer/lumiya/Debug.Printf:(Ljava/lang/String;[Ljava/lang/Object;)V
                //    27: aload_0        
                //    28: getfield        com/lumiyaviewer/lumiya/utils/wlist/ChunkedListLoader$1.this$0:Lcom/lumiyaviewer/lumiya/utils/wlist/ChunkedListLoader;
                //    31: invokestatic    com/lumiyaviewer/lumiya/utils/wlist/ChunkedListLoader.-get11:(Lcom/lumiyaviewer/lumiya/utils/wlist/ChunkedListLoader;)Ljava/util/concurrent/atomic/AtomicBoolean;
                //    34: iconst_0       
                //    35: invokevirtual   java/util/concurrent/atomic/AtomicBoolean.set:(Z)V
                //    38: aload_0        
                //    39: getfield        com/lumiyaviewer/lumiya/utils/wlist/ChunkedListLoader$1.this$0:Lcom/lumiyaviewer/lumiya/utils/wlist/ChunkedListLoader;
                //    42: invokestatic    com/lumiyaviewer/lumiya/utils/wlist/ChunkedListLoader.-get12:(Lcom/lumiyaviewer/lumiya/utils/wlist/ChunkedListLoader;)Ljava/lang/Object;
                //    45: astore_2       
                //    46: aload_2        
                //    47: monitorenter   
                //    48: aload_0        
                //    49: getfield        com/lumiyaviewer/lumiya/utils/wlist/ChunkedListLoader$1.this$0:Lcom/lumiyaviewer/lumiya/utils/wlist/ChunkedListLoader;
                //    52: invokestatic    com/lumiyaviewer/lumiya/utils/wlist/ChunkedListLoader.-get7:(Lcom/lumiyaviewer/lumiya/utils/wlist/ChunkedListLoader;)Z
                //    55: ifeq            284
                //    58: aload_0        
                //    59: getfield        com/lumiyaviewer/lumiya/utils/wlist/ChunkedListLoader$1.this$0:Lcom/lumiyaviewer/lumiya/utils/wlist/ChunkedListLoader;
                //    62: invokestatic    com/lumiyaviewer/lumiya/utils/wlist/ChunkedListLoader.-get5:(Lcom/lumiyaviewer/lumiya/utils/wlist/ChunkedListLoader;)Lcom/lumiyaviewer/lumiya/utils/wlist/ChunkedListLoader$LoadResult;
                //    65: ifnonnull       284
                //    68: aload_0        
                //    69: getfield        com/lumiyaviewer/lumiya/utils/wlist/ChunkedListLoader$1.this$0:Lcom/lumiyaviewer/lumiya/utils/wlist/ChunkedListLoader;
                //    72: invokestatic    com/lumiyaviewer/lumiya/utils/wlist/ChunkedListLoader.-get14:(Lcom/lumiyaviewer/lumiya/utils/wlist/ChunkedListLoader;)Ljava/util/concurrent/atomic/AtomicBoolean;
                //    75: invokevirtual   java/util/concurrent/atomic/AtomicBoolean.get:()Z
                //    78: iconst_1       
                //    79: ixor           
                //    80: istore_3       
                //    81: aload_0        
                //    82: getfield        com/lumiyaviewer/lumiya/utils/wlist/ChunkedListLoader$1.this$0:Lcom/lumiyaviewer/lumiya/utils/wlist/ChunkedListLoader;
                //    85: invokestatic    com/lumiyaviewer/lumiya/utils/wlist/ChunkedListLoader.-get6:(Lcom/lumiyaviewer/lumiya/utils/wlist/ChunkedListLoader;)J
                //    88: lstore          4
                //    90: aload_2        
                //    91: monitorexit    
                //    92: iload_3        
                //    93: ifeq            325
                //    96: aload_0        
                //    97: getfield        com/lumiyaviewer/lumiya/utils/wlist/ChunkedListLoader$1.this$0:Lcom/lumiyaviewer/lumiya/utils/wlist/ChunkedListLoader;
                //   100: aload_0        
                //   101: getfield        com/lumiyaviewer/lumiya/utils/wlist/ChunkedListLoader$1.this$0:Lcom/lumiyaviewer/lumiya/utils/wlist/ChunkedListLoader;
                //   104: invokestatic    com/lumiyaviewer/lumiya/utils/wlist/ChunkedListLoader.-get17:(Lcom/lumiyaviewer/lumiya/utils/wlist/ChunkedListLoader;)I
                //   107: lload           4
                //   109: iconst_0       
                //   110: invokevirtual   com/lumiyaviewer/lumiya/utils/wlist/ChunkedListLoader.loadInBackground:(IJZ)Lcom/lumiyaviewer/lumiya/utils/wlist/ChunkedListLoader$LoadResult;
                //   113: astore          6
                //   115: aload_0        
                //   116: getfield        com/lumiyaviewer/lumiya/utils/wlist/ChunkedListLoader$1.this$0:Lcom/lumiyaviewer/lumiya/utils/wlist/ChunkedListLoader;
                //   119: invokestatic    com/lumiyaviewer/lumiya/utils/wlist/ChunkedListLoader.-get12:(Lcom/lumiyaviewer/lumiya/utils/wlist/ChunkedListLoader;)Ljava/lang/Object;
                //   122: astore_2       
                //   123: aload_2        
                //   124: monitorenter   
                //   125: aload_0        
                //   126: getfield        com/lumiyaviewer/lumiya/utils/wlist/ChunkedListLoader$1.this$0:Lcom/lumiyaviewer/lumiya/utils/wlist/ChunkedListLoader;
                //   129: aload           6
                //   131: invokestatic    com/lumiyaviewer/lumiya/utils/wlist/ChunkedListLoader.-set2:(Lcom/lumiyaviewer/lumiya/utils/wlist/ChunkedListLoader;Lcom/lumiyaviewer/lumiya/utils/wlist/ChunkedListLoader$LoadResult;)Lcom/lumiyaviewer/lumiya/utils/wlist/ChunkedListLoader$LoadResult;
                //   134: pop            
                //   135: aload_2        
                //   136: monitorexit    
                //   137: iconst_1       
                //   138: istore_3       
                //   139: aload_0        
                //   140: getfield        com/lumiyaviewer/lumiya/utils/wlist/ChunkedListLoader$1.this$0:Lcom/lumiyaviewer/lumiya/utils/wlist/ChunkedListLoader;
                //   143: invokestatic    com/lumiyaviewer/lumiya/utils/wlist/ChunkedListLoader.-get12:(Lcom/lumiyaviewer/lumiya/utils/wlist/ChunkedListLoader;)Ljava/lang/Object;
                //   146: astore          6
                //   148: aload           6
                //   150: monitorenter   
                //   151: aload_0        
                //   152: getfield        com/lumiyaviewer/lumiya/utils/wlist/ChunkedListLoader$1.this$0:Lcom/lumiyaviewer/lumiya/utils/wlist/ChunkedListLoader;
                //   155: invokestatic    com/lumiyaviewer/lumiya/utils/wlist/ChunkedListLoader.-get10:(Lcom/lumiyaviewer/lumiya/utils/wlist/ChunkedListLoader;)Z
                //   158: ifeq            303
                //   161: aload_0        
                //   162: getfield        com/lumiyaviewer/lumiya/utils/wlist/ChunkedListLoader$1.this$0:Lcom/lumiyaviewer/lumiya/utils/wlist/ChunkedListLoader;
                //   165: invokestatic    com/lumiyaviewer/lumiya/utils/wlist/ChunkedListLoader.-get9:(Lcom/lumiyaviewer/lumiya/utils/wlist/ChunkedListLoader;)Lcom/lumiyaviewer/lumiya/utils/wlist/ChunkedListLoader$LoadResult;
                //   168: ifnonnull       303
                //   171: aload_0        
                //   172: getfield        com/lumiyaviewer/lumiya/utils/wlist/ChunkedListLoader$1.this$0:Lcom/lumiyaviewer/lumiya/utils/wlist/ChunkedListLoader;
                //   175: invokestatic    com/lumiyaviewer/lumiya/utils/wlist/ChunkedListLoader.-get14:(Lcom/lumiyaviewer/lumiya/utils/wlist/ChunkedListLoader;)Ljava/util/concurrent/atomic/AtomicBoolean;
                //   178: invokevirtual   java/util/concurrent/atomic/AtomicBoolean.get:()Z
                //   181: iconst_1       
                //   182: ixor           
                //   183: istore          7
                //   185: aload_0        
                //   186: getfield        com/lumiyaviewer/lumiya/utils/wlist/ChunkedListLoader$1.this$0:Lcom/lumiyaviewer/lumiya/utils/wlist/ChunkedListLoader;
                //   189: invokestatic    com/lumiyaviewer/lumiya/utils/wlist/ChunkedListLoader.-get8:(Lcom/lumiyaviewer/lumiya/utils/wlist/ChunkedListLoader;)J
                //   192: lstore          4
                //   194: aload           6
                //   196: monitorexit    
                //   197: iload           7
                //   199: ifeq            245
                //   202: aload_0        
                //   203: getfield        com/lumiyaviewer/lumiya/utils/wlist/ChunkedListLoader$1.this$0:Lcom/lumiyaviewer/lumiya/utils/wlist/ChunkedListLoader;
                //   206: aload_0        
                //   207: getfield        com/lumiyaviewer/lumiya/utils/wlist/ChunkedListLoader$1.this$0:Lcom/lumiyaviewer/lumiya/utils/wlist/ChunkedListLoader;
                //   210: invokestatic    com/lumiyaviewer/lumiya/utils/wlist/ChunkedListLoader.-get17:(Lcom/lumiyaviewer/lumiya/utils/wlist/ChunkedListLoader;)I
                //   213: lload           4
                //   215: iconst_1       
                //   216: invokevirtual   com/lumiyaviewer/lumiya/utils/wlist/ChunkedListLoader.loadInBackground:(IJZ)Lcom/lumiyaviewer/lumiya/utils/wlist/ChunkedListLoader$LoadResult;
                //   219: astore          6
                //   221: aload_0        
                //   222: getfield        com/lumiyaviewer/lumiya/utils/wlist/ChunkedListLoader$1.this$0:Lcom/lumiyaviewer/lumiya/utils/wlist/ChunkedListLoader;
                //   225: invokestatic    com/lumiyaviewer/lumiya/utils/wlist/ChunkedListLoader.-get12:(Lcom/lumiyaviewer/lumiya/utils/wlist/ChunkedListLoader;)Ljava/lang/Object;
                //   228: astore_2       
                //   229: aload_2        
                //   230: monitorenter   
                //   231: aload_0        
                //   232: getfield        com/lumiyaviewer/lumiya/utils/wlist/ChunkedListLoader$1.this$0:Lcom/lumiyaviewer/lumiya/utils/wlist/ChunkedListLoader;
                //   235: aload           6
                //   237: invokestatic    com/lumiyaviewer/lumiya/utils/wlist/ChunkedListLoader.-set4:(Lcom/lumiyaviewer/lumiya/utils/wlist/ChunkedListLoader;Lcom/lumiyaviewer/lumiya/utils/wlist/ChunkedListLoader$LoadResult;)Lcom/lumiyaviewer/lumiya/utils/wlist/ChunkedListLoader$LoadResult;
                //   240: pop            
                //   241: aload_2        
                //   242: monitorexit    
                //   243: iconst_1       
                //   244: istore_3       
                //   245: aload_0        
                //   246: getfield        com/lumiyaviewer/lumiya/utils/wlist/ChunkedListLoader$1.this$0:Lcom/lumiyaviewer/lumiya/utils/wlist/ChunkedListLoader;
                //   249: invokestatic    com/lumiyaviewer/lumiya/utils/wlist/ChunkedListLoader.-get14:(Lcom/lumiyaviewer/lumiya/utils/wlist/ChunkedListLoader;)Ljava/util/concurrent/atomic/AtomicBoolean;
                //   252: iconst_0       
                //   253: invokevirtual   java/util/concurrent/atomic/AtomicBoolean.getAndSet:(Z)Z
                //   256: ifeq            322
                //   259: aload_0        
                //   260: getfield        com/lumiyaviewer/lumiya/utils/wlist/ChunkedListLoader$1.this$0:Lcom/lumiyaviewer/lumiya/utils/wlist/ChunkedListLoader;
                //   263: invokestatic    com/lumiyaviewer/lumiya/utils/wlist/ChunkedListLoader.-get13:(Lcom/lumiyaviewer/lumiya/utils/wlist/ChunkedListLoader;)Ljava/util/concurrent/atomic/AtomicBoolean;
                //   266: iconst_1       
                //   267: invokevirtual   java/util/concurrent/atomic/AtomicBoolean.set:(Z)V
                //   270: iload_1        
                //   271: istore_3       
                //   272: iload_3        
                //   273: ifeq            283
                //   276: aload_0        
                //   277: getfield        com/lumiyaviewer/lumiya/utils/wlist/ChunkedListLoader$1.this$0:Lcom/lumiyaviewer/lumiya/utils/wlist/ChunkedListLoader;
                //   280: invokestatic    com/lumiyaviewer/lumiya/utils/wlist/ChunkedListLoader.-wrap0:(Lcom/lumiyaviewer/lumiya/utils/wlist/ChunkedListLoader;)V
                //   283: return         
                //   284: iconst_0       
                //   285: istore_3       
                //   286: goto            81
                //   289: astore          6
                //   291: aload_2        
                //   292: monitorexit    
                //   293: aload           6
                //   295: athrow         
                //   296: astore          6
                //   298: aload_2        
                //   299: monitorexit    
                //   300: aload           6
                //   302: athrow         
                //   303: iconst_0       
                //   304: istore          7
                //   306: goto            185
                //   309: astore_2       
                //   310: aload           6
                //   312: monitorexit    
                //   313: aload_2        
                //   314: athrow         
                //   315: astore          6
                //   317: aload_2        
                //   318: monitorexit    
                //   319: aload           6
                //   321: athrow         
                //   322: goto            272
                //   325: iconst_0       
                //   326: istore_3       
                //   327: goto            139
                //    Exceptions:
                //  Try           Handler
                //  Start  End    Start  End    Type
                //  -----  -----  -----  -----  ----
                //  48     81     289    296    Any
                //  81     90     289    296    Any
                //  125    135    296    303    Any
                //  151    185    309    315    Any
                //  185    194    309    315    Any
                //  231    241    315    322    Any
                // 
                // The error that occurred was:
                // 
                // java.lang.IndexOutOfBoundsException: Index 174 out of bounds for length 174
                //     at java.base/jdk.internal.util.Preconditions.outOfBounds(Preconditions.java:100)
                //     at java.base/jdk.internal.util.Preconditions.outOfBoundsCheckIndex(Preconditions.java:106)
                //     at java.base/jdk.internal.util.Preconditions.checkIndex(Preconditions.java:302)
                //     at java.base/java.util.Objects.checkIndex(Objects.java:385)
                //     at java.base/java.util.ArrayList.get(ArrayList.java:427)
                //     at com.strobel.decompiler.ast.AstBuilder.convertToAst(AstBuilder.java:3362)
                //     at com.strobel.decompiler.ast.AstBuilder.build(AstBuilder.java:112)
                //     at com.strobel.decompiler.languages.java.ast.AstMethodBodyBuilder.createMethodBody(AstMethodBodyBuilder.java:203)
                //     at com.strobel.decompiler.languages.java.ast.AstMethodBodyBuilder.createMethodBody(AstMethodBodyBuilder.java:93)
                //     at com.strobel.decompiler.languages.java.ast.AstBuilder.createMethodBody(AstBuilder.java:868)
                //     at com.strobel.decompiler.languages.java.ast.AstBuilder.createMethod(AstBuilder.java:761)
                //     at com.strobel.decompiler.languages.java.ast.AstBuilder.addTypeMembers(AstBuilder.java:638)
                //     at com.strobel.decompiler.languages.java.ast.AstBuilder.createTypeCore(AstBuilder.java:605)
                //     at com.strobel.decompiler.languages.java.ast.AstBuilder.createTypeNoCache(AstBuilder.java:195)
                //     at com.strobel.decompiler.languages.java.ast.AstBuilder.createType(AstBuilder.java:162)
                //     at com.strobel.decompiler.languages.java.ast.AstMethodBodyBuilder.transformCall(AstMethodBodyBuilder.java:1151)
                //     at com.strobel.decompiler.languages.java.ast.AstMethodBodyBuilder.transformByteCode(AstMethodBodyBuilder.java:993)
                //     at com.strobel.decompiler.languages.java.ast.AstMethodBodyBuilder.transformExpression(AstMethodBodyBuilder.java:534)
                //     at com.strobel.decompiler.languages.java.ast.AstMethodBodyBuilder.transformByteCode(AstMethodBodyBuilder.java:548)
                //     at com.strobel.decompiler.languages.java.ast.AstMethodBodyBuilder.transformExpression(AstMethodBodyBuilder.java:534)
                //     at com.strobel.decompiler.languages.java.ast.AstMethodBodyBuilder.transformNode(AstMethodBodyBuilder.java:377)
                //     at com.strobel.decompiler.languages.java.ast.AstMethodBodyBuilder.transformBlock(AstMethodBodyBuilder.java:318)
                //     at com.strobel.decompiler.languages.java.ast.AstMethodBodyBuilder.createMethodBody(AstMethodBodyBuilder.java:213)
                //     at com.strobel.decompiler.languages.java.ast.AstMethodBodyBuilder.createMethodBody(AstMethodBodyBuilder.java:93)
                //     at com.strobel.decompiler.languages.java.ast.AstBuilder.createMethodBody(AstBuilder.java:868)
                //     at com.strobel.decompiler.languages.java.ast.AstBuilder.createConstructor(AstBuilder.java:799)
                //     at com.strobel.decompiler.languages.java.ast.AstBuilder.addTypeMembers(AstBuilder.java:635)
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
        };
        this.processUpdate = new Runnable() {
            @Override
            public void run() {
                // 
                // This method could not be decompiled.
                // 
                // Original Bytecode:
                // 
                //     1: getfield        com/lumiyaviewer/lumiya/utils/wlist/ChunkedListLoader$2.this$0:Lcom/lumiyaviewer/lumiya/utils/wlist/ChunkedListLoader;
                //     4: invokestatic    com/lumiyaviewer/lumiya/utils/wlist/ChunkedListLoader.-get15:(Lcom/lumiyaviewer/lumiya/utils/wlist/ChunkedListLoader;)Ljava/util/concurrent/atomic/AtomicBoolean;
                //     7: iconst_0       
                //     8: invokevirtual   java/util/concurrent/atomic/AtomicBoolean.set:(Z)V
                //    11: ldc             "ChatView: processUpdate, reloadAccepted: %b"
                //    13: iconst_1       
                //    14: anewarray       Ljava/lang/Object;
                //    17: dup            
                //    18: iconst_0       
                //    19: aload_0        
                //    20: getfield        com/lumiyaviewer/lumiya/utils/wlist/ChunkedListLoader$2.this$0:Lcom/lumiyaviewer/lumiya/utils/wlist/ChunkedListLoader;
                //    23: invokestatic    com/lumiyaviewer/lumiya/utils/wlist/ChunkedListLoader.-get13:(Lcom/lumiyaviewer/lumiya/utils/wlist/ChunkedListLoader;)Ljava/util/concurrent/atomic/AtomicBoolean;
                //    26: invokevirtual   java/util/concurrent/atomic/AtomicBoolean.get:()Z
                //    29: invokestatic    java/lang/Boolean.valueOf:(Z)Ljava/lang/Boolean;
                //    32: aastore        
                //    33: invokestatic    com/lumiyaviewer/lumiya/Debug.Printf:(Ljava/lang/String;[Ljava/lang/Object;)V
                //    36: aload_0        
                //    37: getfield        com/lumiyaviewer/lumiya/utils/wlist/ChunkedListLoader$2.this$0:Lcom/lumiyaviewer/lumiya/utils/wlist/ChunkedListLoader;
                //    40: invokestatic    com/lumiyaviewer/lumiya/utils/wlist/ChunkedListLoader.-get13:(Lcom/lumiyaviewer/lumiya/utils/wlist/ChunkedListLoader;)Ljava/util/concurrent/atomic/AtomicBoolean;
                //    43: iconst_0       
                //    44: invokevirtual   java/util/concurrent/atomic/AtomicBoolean.getAndSet:(Z)Z
                //    47: ifeq            156
                //    50: aload_0        
                //    51: getfield        com/lumiyaviewer/lumiya/utils/wlist/ChunkedListLoader$2.this$0:Lcom/lumiyaviewer/lumiya/utils/wlist/ChunkedListLoader;
                //    54: invokestatic    com/lumiyaviewer/lumiya/utils/wlist/ChunkedListLoader.-get12:(Lcom/lumiyaviewer/lumiya/utils/wlist/ChunkedListLoader;)Ljava/lang/Object;
                //    57: astore_1       
                //    58: aload_1        
                //    59: monitorenter   
                //    60: aload_0        
                //    61: getfield        com/lumiyaviewer/lumiya/utils/wlist/ChunkedListLoader$2.this$0:Lcom/lumiyaviewer/lumiya/utils/wlist/ChunkedListLoader;
                //    64: iconst_0       
                //    65: invokestatic    com/lumiyaviewer/lumiya/utils/wlist/ChunkedListLoader.-set3:(Lcom/lumiyaviewer/lumiya/utils/wlist/ChunkedListLoader;Z)Z
                //    68: pop            
                //    69: aload_0        
                //    70: getfield        com/lumiyaviewer/lumiya/utils/wlist/ChunkedListLoader$2.this$0:Lcom/lumiyaviewer/lumiya/utils/wlist/ChunkedListLoader;
                //    73: aconst_null    
                //    74: invokestatic    com/lumiyaviewer/lumiya/utils/wlist/ChunkedListLoader.-set2:(Lcom/lumiyaviewer/lumiya/utils/wlist/ChunkedListLoader;Lcom/lumiyaviewer/lumiya/utils/wlist/ChunkedListLoader$LoadResult;)Lcom/lumiyaviewer/lumiya/utils/wlist/ChunkedListLoader$LoadResult;
                //    77: pop            
                //    78: aload_0        
                //    79: getfield        com/lumiyaviewer/lumiya/utils/wlist/ChunkedListLoader$2.this$0:Lcom/lumiyaviewer/lumiya/utils/wlist/ChunkedListLoader;
                //    82: aconst_null    
                //    83: invokestatic    com/lumiyaviewer/lumiya/utils/wlist/ChunkedListLoader.-set4:(Lcom/lumiyaviewer/lumiya/utils/wlist/ChunkedListLoader;Lcom/lumiyaviewer/lumiya/utils/wlist/ChunkedListLoader$LoadResult;)Lcom/lumiyaviewer/lumiya/utils/wlist/ChunkedListLoader$LoadResult;
                //    86: pop            
                //    87: aload_0        
                //    88: getfield        com/lumiyaviewer/lumiya/utils/wlist/ChunkedListLoader$2.this$0:Lcom/lumiyaviewer/lumiya/utils/wlist/ChunkedListLoader;
                //    91: iconst_0       
                //    92: invokestatic    com/lumiyaviewer/lumiya/utils/wlist/ChunkedListLoader.-set5:(Lcom/lumiyaviewer/lumiya/utils/wlist/ChunkedListLoader;Z)Z
                //    95: pop            
                //    96: aload_1        
                //    97: monitorexit    
                //    98: aload_0        
                //    99: getfield        com/lumiyaviewer/lumiya/utils/wlist/ChunkedListLoader$2.this$0:Lcom/lumiyaviewer/lumiya/utils/wlist/ChunkedListLoader;
                //   102: iconst_1       
                //   103: invokestatic    com/lumiyaviewer/lumiya/utils/wlist/ChunkedListLoader.-set0:(Lcom/lumiyaviewer/lumiya/utils/wlist/ChunkedListLoader;Z)Z
                //   106: pop            
                //   107: aload_0        
                //   108: getfield        com/lumiyaviewer/lumiya/utils/wlist/ChunkedListLoader$2.this$0:Lcom/lumiyaviewer/lumiya/utils/wlist/ChunkedListLoader;
                //   111: iconst_1       
                //   112: invokestatic    com/lumiyaviewer/lumiya/utils/wlist/ChunkedListLoader.-set1:(Lcom/lumiyaviewer/lumiya/utils/wlist/ChunkedListLoader;Z)Z
                //   115: pop            
                //   116: aload_0        
                //   117: getfield        com/lumiyaviewer/lumiya/utils/wlist/ChunkedListLoader$2.this$0:Lcom/lumiyaviewer/lumiya/utils/wlist/ChunkedListLoader;
                //   120: invokestatic    com/lumiyaviewer/lumiya/utils/wlist/ChunkedListLoader.-get0:(Lcom/lumiyaviewer/lumiya/utils/wlist/ChunkedListLoader;)Ljava/util/Queue;
                //   123: invokeinterface java/util/Queue.clear:()V
                //   128: aload_0        
                //   129: getfield        com/lumiyaviewer/lumiya/utils/wlist/ChunkedListLoader$2.this$0:Lcom/lumiyaviewer/lumiya/utils/wlist/ChunkedListLoader;
                //   132: invokestatic    com/lumiyaviewer/lumiya/utils/wlist/ChunkedListLoader.-get3:(Lcom/lumiyaviewer/lumiya/utils/wlist/ChunkedListLoader;)Lcom/lumiyaviewer/lumiya/utils/wlist/ChunkedList;
                //   135: invokevirtual   com/lumiyaviewer/lumiya/utils/wlist/ChunkedList.clear:()V
                //   138: aload_0        
                //   139: getfield        com/lumiyaviewer/lumiya/utils/wlist/ChunkedListLoader$2.this$0:Lcom/lumiyaviewer/lumiya/utils/wlist/ChunkedListLoader;
                //   142: invokestatic    com/lumiyaviewer/lumiya/utils/wlist/ChunkedListLoader.-get4:(Lcom/lumiyaviewer/lumiya/utils/wlist/ChunkedListLoader;)Lcom/lumiyaviewer/lumiya/utils/wlist/ChunkedListLoader$EventListener;
                //   145: invokeinterface com/lumiyaviewer/lumiya/utils/wlist/ChunkedListLoader$EventListener.onListReloaded:()V
                //   150: return         
                //   151: astore_2       
                //   152: aload_1        
                //   153: monitorexit    
                //   154: aload_2        
                //   155: athrow         
                //   156: aload_0        
                //   157: getfield        com/lumiyaviewer/lumiya/utils/wlist/ChunkedListLoader$2.this$0:Lcom/lumiyaviewer/lumiya/utils/wlist/ChunkedListLoader;
                //   160: invokestatic    com/lumiyaviewer/lumiya/utils/wlist/ChunkedListLoader.-get12:(Lcom/lumiyaviewer/lumiya/utils/wlist/ChunkedListLoader;)Ljava/lang/Object;
                //   163: astore_1       
                //   164: aload_1        
                //   165: monitorenter   
                //   166: aload_0        
                //   167: getfield        com/lumiyaviewer/lumiya/utils/wlist/ChunkedListLoader$2.this$0:Lcom/lumiyaviewer/lumiya/utils/wlist/ChunkedListLoader;
                //   170: invokestatic    com/lumiyaviewer/lumiya/utils/wlist/ChunkedListLoader.-get5:(Lcom/lumiyaviewer/lumiya/utils/wlist/ChunkedListLoader;)Lcom/lumiyaviewer/lumiya/utils/wlist/ChunkedListLoader$LoadResult;
                //   173: astore_2       
                //   174: aload_2        
                //   175: ifnull          196
                //   178: aload_0        
                //   179: getfield        com/lumiyaviewer/lumiya/utils/wlist/ChunkedListLoader$2.this$0:Lcom/lumiyaviewer/lumiya/utils/wlist/ChunkedListLoader;
                //   182: aconst_null    
                //   183: invokestatic    com/lumiyaviewer/lumiya/utils/wlist/ChunkedListLoader.-set2:(Lcom/lumiyaviewer/lumiya/utils/wlist/ChunkedListLoader;Lcom/lumiyaviewer/lumiya/utils/wlist/ChunkedListLoader$LoadResult;)Lcom/lumiyaviewer/lumiya/utils/wlist/ChunkedListLoader$LoadResult;
                //   186: pop            
                //   187: aload_0        
                //   188: getfield        com/lumiyaviewer/lumiya/utils/wlist/ChunkedListLoader$2.this$0:Lcom/lumiyaviewer/lumiya/utils/wlist/ChunkedListLoader;
                //   191: iconst_0       
                //   192: invokestatic    com/lumiyaviewer/lumiya/utils/wlist/ChunkedListLoader.-set3:(Lcom/lumiyaviewer/lumiya/utils/wlist/ChunkedListLoader;Z)Z
                //   195: pop            
                //   196: aload_1        
                //   197: monitorexit    
                //   198: aload_2        
                //   199: ifnull          781
                //   202: aload_2        
                //   203: getfield        com/lumiyaviewer/lumiya/utils/wlist/ChunkedListLoader$LoadResult.entries:Ljava/util/List;
                //   206: invokeinterface java/util/List.size:()I
                //   211: iconst_0       
                //   212: iadd           
                //   213: istore_3       
                //   214: aload_0        
                //   215: getfield        com/lumiyaviewer/lumiya/utils/wlist/ChunkedListLoader$2.this$0:Lcom/lumiyaviewer/lumiya/utils/wlist/ChunkedListLoader;
                //   218: invokestatic    com/lumiyaviewer/lumiya/utils/wlist/ChunkedListLoader.-get3:(Lcom/lumiyaviewer/lumiya/utils/wlist/ChunkedListLoader;)Lcom/lumiyaviewer/lumiya/utils/wlist/ChunkedList;
                //   221: aload_2        
                //   222: getfield        com/lumiyaviewer/lumiya/utils/wlist/ChunkedListLoader$LoadResult.entries:Ljava/util/List;
                //   225: invokestatic    com/google/common/collect/Lists.reverse:(Ljava/util/List;)Ljava/util/List;
                //   228: invokevirtual   com/lumiyaviewer/lumiya/utils/wlist/ChunkedList.addChunkAtStart:(Ljava/util/List;)V
                //   231: aload_0        
                //   232: getfield        com/lumiyaviewer/lumiya/utils/wlist/ChunkedListLoader$2.this$0:Lcom/lumiyaviewer/lumiya/utils/wlist/ChunkedListLoader;
                //   235: aload_2        
                //   236: getfield        com/lumiyaviewer/lumiya/utils/wlist/ChunkedListLoader$LoadResult.hasMore:Z
                //   239: invokestatic    com/lumiyaviewer/lumiya/utils/wlist/ChunkedListLoader.-set0:(Lcom/lumiyaviewer/lumiya/utils/wlist/ChunkedListLoader;Z)Z
                //   242: pop            
                //   243: iload_3        
                //   244: istore          4
                //   246: aload_2        
                //   247: getfield        com/lumiyaviewer/lumiya/utils/wlist/ChunkedListLoader$LoadResult.fromId:J
                //   250: ldc2_w          9223372036854775807
                //   253: lcmp           
                //   254: ifne            269
                //   257: aload_0        
                //   258: getfield        com/lumiyaviewer/lumiya/utils/wlist/ChunkedListLoader$2.this$0:Lcom/lumiyaviewer/lumiya/utils/wlist/ChunkedListLoader;
                //   261: iconst_0       
                //   262: invokestatic    com/lumiyaviewer/lumiya/utils/wlist/ChunkedListLoader.-set1:(Lcom/lumiyaviewer/lumiya/utils/wlist/ChunkedListLoader;Z)Z
                //   265: pop            
                //   266: iload_3        
                //   267: istore          4
                //   269: iload           4
                //   271: ifeq            289
                //   274: aload_0        
                //   275: getfield        com/lumiyaviewer/lumiya/utils/wlist/ChunkedListLoader$2.this$0:Lcom/lumiyaviewer/lumiya/utils/wlist/ChunkedListLoader;
                //   278: invokestatic    com/lumiyaviewer/lumiya/utils/wlist/ChunkedListLoader.-get4:(Lcom/lumiyaviewer/lumiya/utils/wlist/ChunkedListLoader;)Lcom/lumiyaviewer/lumiya/utils/wlist/ChunkedListLoader$EventListener;
                //   281: iconst_0       
                //   282: iload           4
                //   284: invokeinterface com/lumiyaviewer/lumiya/utils/wlist/ChunkedListLoader$EventListener.onListItemsAdded:(II)V
                //   289: aload_0        
                //   290: getfield        com/lumiyaviewer/lumiya/utils/wlist/ChunkedListLoader$2.this$0:Lcom/lumiyaviewer/lumiya/utils/wlist/ChunkedListLoader;
                //   293: invokestatic    com/lumiyaviewer/lumiya/utils/wlist/ChunkedListLoader.-get3:(Lcom/lumiyaviewer/lumiya/utils/wlist/ChunkedListLoader;)Lcom/lumiyaviewer/lumiya/utils/wlist/ChunkedList;
                //   296: invokevirtual   com/lumiyaviewer/lumiya/utils/wlist/ChunkedList.size:()I
                //   299: istore          5
                //   301: aload_0        
                //   302: getfield        com/lumiyaviewer/lumiya/utils/wlist/ChunkedListLoader$2.this$0:Lcom/lumiyaviewer/lumiya/utils/wlist/ChunkedListLoader;
                //   305: invokestatic    com/lumiyaviewer/lumiya/utils/wlist/ChunkedListLoader.-get12:(Lcom/lumiyaviewer/lumiya/utils/wlist/ChunkedListLoader;)Ljava/lang/Object;
                //   308: astore_1       
                //   309: aload_1        
                //   310: monitorenter   
                //   311: aload_0        
                //   312: getfield        com/lumiyaviewer/lumiya/utils/wlist/ChunkedListLoader$2.this$0:Lcom/lumiyaviewer/lumiya/utils/wlist/ChunkedListLoader;
                //   315: invokestatic    com/lumiyaviewer/lumiya/utils/wlist/ChunkedListLoader.-get9:(Lcom/lumiyaviewer/lumiya/utils/wlist/ChunkedListLoader;)Lcom/lumiyaviewer/lumiya/utils/wlist/ChunkedListLoader$LoadResult;
                //   318: astore_2       
                //   319: aload_2        
                //   320: ifnull          341
                //   323: aload_0        
                //   324: getfield        com/lumiyaviewer/lumiya/utils/wlist/ChunkedListLoader$2.this$0:Lcom/lumiyaviewer/lumiya/utils/wlist/ChunkedListLoader;
                //   327: aconst_null    
                //   328: invokestatic    com/lumiyaviewer/lumiya/utils/wlist/ChunkedListLoader.-set4:(Lcom/lumiyaviewer/lumiya/utils/wlist/ChunkedListLoader;Lcom/lumiyaviewer/lumiya/utils/wlist/ChunkedListLoader$LoadResult;)Lcom/lumiyaviewer/lumiya/utils/wlist/ChunkedListLoader$LoadResult;
                //   331: pop            
                //   332: aload_0        
                //   333: getfield        com/lumiyaviewer/lumiya/utils/wlist/ChunkedListLoader$2.this$0:Lcom/lumiyaviewer/lumiya/utils/wlist/ChunkedListLoader;
                //   336: iconst_0       
                //   337: invokestatic    com/lumiyaviewer/lumiya/utils/wlist/ChunkedListLoader.-set5:(Lcom/lumiyaviewer/lumiya/utils/wlist/ChunkedListLoader;Z)Z
                //   340: pop            
                //   341: aload_1        
                //   342: monitorexit    
                //   343: aload_2        
                //   344: ifnull          775
                //   347: aload_2        
                //   348: getfield        com/lumiyaviewer/lumiya/utils/wlist/ChunkedListLoader$LoadResult.entries:Ljava/util/List;
                //   351: invokeinterface java/util/List.size:()I
                //   356: istore_3       
                //   357: aload_0        
                //   358: getfield        com/lumiyaviewer/lumiya/utils/wlist/ChunkedListLoader$2.this$0:Lcom/lumiyaviewer/lumiya/utils/wlist/ChunkedListLoader;
                //   361: invokestatic    com/lumiyaviewer/lumiya/utils/wlist/ChunkedListLoader.-get3:(Lcom/lumiyaviewer/lumiya/utils/wlist/ChunkedListLoader;)Lcom/lumiyaviewer/lumiya/utils/wlist/ChunkedList;
                //   364: aload_2        
                //   365: getfield        com/lumiyaviewer/lumiya/utils/wlist/ChunkedListLoader$LoadResult.entries:Ljava/util/List;
                //   368: invokevirtual   com/lumiyaviewer/lumiya/utils/wlist/ChunkedList.addChunkAtEnd:(Ljava/util/List;)V
                //   371: aload_0        
                //   372: getfield        com/lumiyaviewer/lumiya/utils/wlist/ChunkedListLoader$2.this$0:Lcom/lumiyaviewer/lumiya/utils/wlist/ChunkedListLoader;
                //   375: aload_2        
                //   376: getfield        com/lumiyaviewer/lumiya/utils/wlist/ChunkedListLoader$LoadResult.hasMore:Z
                //   379: invokestatic    com/lumiyaviewer/lumiya/utils/wlist/ChunkedListLoader.-set1:(Lcom/lumiyaviewer/lumiya/utils/wlist/ChunkedListLoader;Z)Z
                //   382: pop            
                //   383: iload_3        
                //   384: istore          4
                //   386: aload_2        
                //   387: getfield        com/lumiyaviewer/lumiya/utils/wlist/ChunkedListLoader$LoadResult.fromId:J
                //   390: lconst_0       
                //   391: lcmp           
                //   392: ifne            407
                //   395: aload_0        
                //   396: getfield        com/lumiyaviewer/lumiya/utils/wlist/ChunkedListLoader$2.this$0:Lcom/lumiyaviewer/lumiya/utils/wlist/ChunkedListLoader;
                //   399: iconst_0       
                //   400: invokestatic    com/lumiyaviewer/lumiya/utils/wlist/ChunkedListLoader.-set0:(Lcom/lumiyaviewer/lumiya/utils/wlist/ChunkedListLoader;Z)Z
                //   403: pop            
                //   404: iload_3        
                //   405: istore          4
                //   407: iconst_0       
                //   408: istore_3       
                //   409: aload_0        
                //   410: getfield        com/lumiyaviewer/lumiya/utils/wlist/ChunkedListLoader$2.this$0:Lcom/lumiyaviewer/lumiya/utils/wlist/ChunkedListLoader;
                //   413: invokestatic    com/lumiyaviewer/lumiya/utils/wlist/ChunkedListLoader.-get0:(Lcom/lumiyaviewer/lumiya/utils/wlist/ChunkedListLoader;)Ljava/util/Queue;
                //   416: invokeinterface java/util/Queue.poll:()Ljava/lang/Object;
                //   421: checkcast       Lcom/lumiyaviewer/lumiya/utils/Identifiable;
                //   424: astore_1       
                //   425: aload_1        
                //   426: ifnull          598
                //   429: aload_0        
                //   430: getfield        com/lumiyaviewer/lumiya/utils/wlist/ChunkedListLoader$2.this$0:Lcom/lumiyaviewer/lumiya/utils/wlist/ChunkedListLoader;
                //   433: invokestatic    com/lumiyaviewer/lumiya/utils/wlist/ChunkedListLoader.-get3:(Lcom/lumiyaviewer/lumiya/utils/wlist/ChunkedListLoader;)Lcom/lumiyaviewer/lumiya/utils/wlist/ChunkedList;
                //   436: invokevirtual   com/lumiyaviewer/lumiya/utils/wlist/ChunkedList.size:()I
                //   439: ifle            587
                //   442: aload_0        
                //   443: getfield        com/lumiyaviewer/lumiya/utils/wlist/ChunkedListLoader$2.this$0:Lcom/lumiyaviewer/lumiya/utils/wlist/ChunkedListLoader;
                //   446: invokestatic    com/lumiyaviewer/lumiya/utils/wlist/ChunkedListLoader.-get3:(Lcom/lumiyaviewer/lumiya/utils/wlist/ChunkedListLoader;)Lcom/lumiyaviewer/lumiya/utils/wlist/ChunkedList;
                //   449: aload_0        
                //   450: getfield        com/lumiyaviewer/lumiya/utils/wlist/ChunkedListLoader$2.this$0:Lcom/lumiyaviewer/lumiya/utils/wlist/ChunkedListLoader;
                //   453: invokestatic    com/lumiyaviewer/lumiya/utils/wlist/ChunkedListLoader.-get3:(Lcom/lumiyaviewer/lumiya/utils/wlist/ChunkedListLoader;)Lcom/lumiyaviewer/lumiya/utils/wlist/ChunkedList;
                //   456: invokevirtual   com/lumiyaviewer/lumiya/utils/wlist/ChunkedList.size:()I
                //   459: iconst_1       
                //   460: isub           
                //   461: invokevirtual   com/lumiyaviewer/lumiya/utils/wlist/ChunkedList.get:(I)Ljava/lang/Object;
                //   464: checkcast       Lcom/lumiyaviewer/lumiya/utils/Identifiable;
                //   467: invokeinterface com/lumiyaviewer/lumiya/utils/Identifiable.getId:()Ljava/lang/Object;
                //   472: checkcast       Ljava/lang/Long;
                //   475: invokevirtual   java/lang/Long.longValue:()J
                //   478: lstore          6
                //   480: ldc             "ChatView: added element: id %d, lastId %d, hasBelow %b"
                //   482: iconst_3       
                //   483: anewarray       Ljava/lang/Object;
                //   486: dup            
                //   487: iconst_0       
                //   488: aload_1        
                //   489: invokeinterface com/lumiyaviewer/lumiya/utils/Identifiable.getId:()Ljava/lang/Object;
                //   494: aastore        
                //   495: dup            
                //   496: iconst_1       
                //   497: lload           6
                //   499: invokestatic    java/lang/Long.valueOf:(J)Ljava/lang/Long;
                //   502: aastore        
                //   503: dup            
                //   504: iconst_2       
                //   505: aload_0        
                //   506: getfield        com/lumiyaviewer/lumiya/utils/wlist/ChunkedListLoader$2.this$0:Lcom/lumiyaviewer/lumiya/utils/wlist/ChunkedListLoader;
                //   509: invokestatic    com/lumiyaviewer/lumiya/utils/wlist/ChunkedListLoader.-get2:(Lcom/lumiyaviewer/lumiya/utils/wlist/ChunkedListLoader;)Z
                //   512: invokestatic    java/lang/Boolean.valueOf:(Z)Ljava/lang/Boolean;
                //   515: aastore        
                //   516: invokestatic    com/lumiyaviewer/lumiya/Debug.Printf:(Ljava/lang/String;[Ljava/lang/Object;)V
                //   519: aload_0        
                //   520: getfield        com/lumiyaviewer/lumiya/utils/wlist/ChunkedListLoader$2.this$0:Lcom/lumiyaviewer/lumiya/utils/wlist/ChunkedListLoader;
                //   523: invokestatic    com/lumiyaviewer/lumiya/utils/wlist/ChunkedListLoader.-get2:(Lcom/lumiyaviewer/lumiya/utils/wlist/ChunkedListLoader;)Z
                //   526: ifne            595
                //   529: aload_1        
                //   530: invokeinterface com/lumiyaviewer/lumiya/utils/Identifiable.getId:()Ljava/lang/Object;
                //   535: checkcast       Ljava/lang/Long;
                //   538: invokevirtual   java/lang/Long.longValue:()J
                //   541: lload           6
                //   543: lcmp           
                //   544: ifle            595
                //   547: aload_0        
                //   548: getfield        com/lumiyaviewer/lumiya/utils/wlist/ChunkedListLoader$2.this$0:Lcom/lumiyaviewer/lumiya/utils/wlist/ChunkedListLoader;
                //   551: invokestatic    com/lumiyaviewer/lumiya/utils/wlist/ChunkedListLoader.-get3:(Lcom/lumiyaviewer/lumiya/utils/wlist/ChunkedListLoader;)Lcom/lumiyaviewer/lumiya/utils/wlist/ChunkedList;
                //   554: aload_1        
                //   555: aload_0        
                //   556: getfield        com/lumiyaviewer/lumiya/utils/wlist/ChunkedListLoader$2.this$0:Lcom/lumiyaviewer/lumiya/utils/wlist/ChunkedListLoader;
                //   559: invokestatic    com/lumiyaviewer/lumiya/utils/wlist/ChunkedListLoader.-get17:(Lcom/lumiyaviewer/lumiya/utils/wlist/ChunkedListLoader;)I
                //   562: aload_0        
                //   563: getfield        com/lumiyaviewer/lumiya/utils/wlist/ChunkedListLoader$2.this$0:Lcom/lumiyaviewer/lumiya/utils/wlist/ChunkedListLoader;
                //   566: invokevirtual   com/lumiyaviewer/lumiya/utils/wlist/ChunkedList.addElement:(Ljava/lang/Object;ILcom/lumiyaviewer/lumiya/utils/wlist/ChunkedList$ChunkFactory;)V
                //   569: iconst_1       
                //   570: istore_3       
                //   571: iinc            4, 1
                //   574: goto            409
                //   577: astore_2       
                //   578: aload_1        
                //   579: monitorexit    
                //   580: aload_2        
                //   581: athrow         
                //   582: astore_2       
                //   583: aload_1        
                //   584: monitorexit    
                //   585: aload_2        
                //   586: athrow         
                //   587: ldc2_w          -1
                //   590: lstore          6
                //   592: goto            480
                //   595: goto            574
                //   598: iload           4
                //   600: ifeq            619
                //   603: aload_0        
                //   604: getfield        com/lumiyaviewer/lumiya/utils/wlist/ChunkedListLoader$2.this$0:Lcom/lumiyaviewer/lumiya/utils/wlist/ChunkedListLoader;
                //   607: invokestatic    com/lumiyaviewer/lumiya/utils/wlist/ChunkedListLoader.-get4:(Lcom/lumiyaviewer/lumiya/utils/wlist/ChunkedListLoader;)Lcom/lumiyaviewer/lumiya/utils/wlist/ChunkedListLoader$EventListener;
                //   610: iload           5
                //   612: iload           4
                //   614: invokeinterface com/lumiyaviewer/lumiya/utils/wlist/ChunkedListLoader$EventListener.onListItemsAdded:(II)V
                //   619: iload_3        
                //   620: ifeq            635
                //   623: aload_0        
                //   624: getfield        com/lumiyaviewer/lumiya/utils/wlist/ChunkedListLoader$2.this$0:Lcom/lumiyaviewer/lumiya/utils/wlist/ChunkedListLoader;
                //   627: invokestatic    com/lumiyaviewer/lumiya/utils/wlist/ChunkedListLoader.-get4:(Lcom/lumiyaviewer/lumiya/utils/wlist/ChunkedListLoader;)Lcom/lumiyaviewer/lumiya/utils/wlist/ChunkedListLoader$EventListener;
                //   630: invokeinterface com/lumiyaviewer/lumiya/utils/wlist/ChunkedListLoader$EventListener.onListItemAddedAtEnd:()V
                //   635: aload_0        
                //   636: getfield        com/lumiyaviewer/lumiya/utils/wlist/ChunkedListLoader$2.this$0:Lcom/lumiyaviewer/lumiya/utils/wlist/ChunkedListLoader;
                //   639: invokestatic    com/lumiyaviewer/lumiya/utils/wlist/ChunkedListLoader.-get12:(Lcom/lumiyaviewer/lumiya/utils/wlist/ChunkedListLoader;)Ljava/lang/Object;
                //   642: astore_2       
                //   643: aload_2        
                //   644: monitorenter   
                //   645: aload_0        
                //   646: getfield        com/lumiyaviewer/lumiya/utils/wlist/ChunkedListLoader$2.this$0:Lcom/lumiyaviewer/lumiya/utils/wlist/ChunkedListLoader;
                //   649: invokestatic    com/lumiyaviewer/lumiya/utils/wlist/ChunkedListLoader.-get16:(Lcom/lumiyaviewer/lumiya/utils/wlist/ChunkedListLoader;)Ljava/util/Map;
                //   652: invokeinterface java/util/Map.entrySet:()Ljava/util/Set;
                //   657: invokeinterface java/util/Set.iterator:()Ljava/util/Iterator;
                //   662: astore          8
                //   664: aload           8
                //   666: invokeinterface java/util/Iterator.hasNext:()Z
                //   671: ifeq            770
                //   674: aload           8
                //   676: invokeinterface java/util/Iterator.next:()Ljava/lang/Object;
                //   681: checkcast       Ljava/util/Map$Entry;
                //   684: invokeinterface java/util/Map$Entry.getValue:()Ljava/lang/Object;
                //   689: checkcast       Lcom/lumiyaviewer/lumiya/utils/Identifiable;
                //   692: astore_1       
                //   693: aload           8
                //   695: invokeinterface java/util/Iterator.remove:()V
                //   700: aload_2        
                //   701: monitorexit    
                //   702: aload_1        
                //   703: ifnull          150
                //   706: aload_0        
                //   707: getfield        com/lumiyaviewer/lumiya/utils/wlist/ChunkedListLoader$2.this$0:Lcom/lumiyaviewer/lumiya/utils/wlist/ChunkedListLoader;
                //   710: invokestatic    com/lumiyaviewer/lumiya/utils/wlist/ChunkedListLoader.-get3:(Lcom/lumiyaviewer/lumiya/utils/wlist/ChunkedListLoader;)Lcom/lumiyaviewer/lumiya/utils/wlist/ChunkedList;
                //   713: aload_1        
                //   714: aload_0        
                //   715: getfield        com/lumiyaviewer/lumiya/utils/wlist/ChunkedListLoader$2.this$0:Lcom/lumiyaviewer/lumiya/utils/wlist/ChunkedListLoader;
                //   718: invokestatic    com/lumiyaviewer/lumiya/utils/wlist/ChunkedListLoader.-get1:(Lcom/lumiyaviewer/lumiya/utils/wlist/ChunkedListLoader;)Ljava/util/Comparator;
                //   721: invokevirtual   com/lumiyaviewer/lumiya/utils/wlist/ChunkedList.replaceElement:(Ljava/lang/Object;Ljava/util/Comparator;)I
                //   724: istore          4
                //   726: ldc             "ChunkedListLoader: replace: replacedIndex is %d"
                //   728: iconst_1       
                //   729: anewarray       Ljava/lang/Object;
                //   732: dup            
                //   733: iconst_0       
                //   734: iload           4
                //   736: invokestatic    java/lang/Integer.valueOf:(I)Ljava/lang/Integer;
                //   739: aastore        
                //   740: invokestatic    com/lumiyaviewer/lumiya/Debug.Printf:(Ljava/lang/String;[Ljava/lang/Object;)V
                //   743: iload           4
                //   745: iflt            635
                //   748: aload_0        
                //   749: getfield        com/lumiyaviewer/lumiya/utils/wlist/ChunkedListLoader$2.this$0:Lcom/lumiyaviewer/lumiya/utils/wlist/ChunkedListLoader;
                //   752: invokestatic    com/lumiyaviewer/lumiya/utils/wlist/ChunkedListLoader.-get4:(Lcom/lumiyaviewer/lumiya/utils/wlist/ChunkedListLoader;)Lcom/lumiyaviewer/lumiya/utils/wlist/ChunkedListLoader$EventListener;
                //   755: iload           4
                //   757: invokeinterface com/lumiyaviewer/lumiya/utils/wlist/ChunkedListLoader$EventListener.onListItemChanged:(I)V
                //   762: goto            635
                //   765: astore_1       
                //   766: aload_2        
                //   767: monitorexit    
                //   768: aload_1        
                //   769: athrow         
                //   770: aconst_null    
                //   771: astore_1       
                //   772: goto            700
                //   775: iconst_0       
                //   776: istore          4
                //   778: goto            407
                //   781: iconst_0       
                //   782: istore          4
                //   784: goto            269
                //    Exceptions:
                //  Try           Handler
                //  Start  End    Start  End    Type
                //  -----  -----  -----  -----  ----
                //  60     96     151    156    Any
                //  166    174    577    582    Any
                //  178    196    577    582    Any
                //  311    319    582    587    Any
                //  323    341    582    587    Any
                //  645    700    765    770    Any
                // 
                // The error that occurred was:
                // 
                // java.lang.IllegalStateException: Expression is linked from several locations: Label_0341:
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
                //     at com.strobel.decompiler.languages.java.ast.AstMethodBodyBuilder.transformCall(AstMethodBodyBuilder.java:1151)
                //     at com.strobel.decompiler.languages.java.ast.AstMethodBodyBuilder.transformByteCode(AstMethodBodyBuilder.java:993)
                //     at com.strobel.decompiler.languages.java.ast.AstMethodBodyBuilder.transformExpression(AstMethodBodyBuilder.java:534)
                //     at com.strobel.decompiler.languages.java.ast.AstMethodBodyBuilder.transformByteCode(AstMethodBodyBuilder.java:548)
                //     at com.strobel.decompiler.languages.java.ast.AstMethodBodyBuilder.transformExpression(AstMethodBodyBuilder.java:534)
                //     at com.strobel.decompiler.languages.java.ast.AstMethodBodyBuilder.transformNode(AstMethodBodyBuilder.java:377)
                //     at com.strobel.decompiler.languages.java.ast.AstMethodBodyBuilder.transformBlock(AstMethodBodyBuilder.java:318)
                //     at com.strobel.decompiler.languages.java.ast.AstMethodBodyBuilder.createMethodBody(AstMethodBodyBuilder.java:213)
                //     at com.strobel.decompiler.languages.java.ast.AstMethodBodyBuilder.createMethodBody(AstMethodBodyBuilder.java:93)
                //     at com.strobel.decompiler.languages.java.ast.AstBuilder.createMethodBody(AstBuilder.java:868)
                //     at com.strobel.decompiler.languages.java.ast.AstBuilder.createConstructor(AstBuilder.java:799)
                //     at com.strobel.decompiler.languages.java.ast.AstBuilder.addTypeMembers(AstBuilder.java:635)
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
        };
        this.chatMessageComparator = new _$Lambda$QDlX9uefQr1Wq8gtt1O6M2wUNME();
        this.windowSize = windowSize;
        this.executor = executor;
        this.startFromStart = startFromStart;
        this.listener = listener;
        this.listenerExecutor = listener.getListEventsExecutor();
        this.items = new ChunkedList<E>();
        this.hasAbove = true;
        this.hasBelow = true;
    }
    
    private void postUpdate() {
        if (this.updatePosted.compareAndSet(false, true)) {
            Debug.Printf("ChatView: requesting processUpdate ()", new Object[0]);
            this.listenerExecutor.execute(this.processUpdate);
        }
        else {
            Debug.Printf("ChatView: processUpdate () already requested", new Object[0]);
        }
    }
    
    public void addElement(final E e) {
        Debug.Printf("ChatView: addElement: adding element with id %d", ((Identifiable<Object>)e).getId());
        this.addedElements.add(e);
        this.postUpdate();
    }
    
    @Override
    public List<E> createEmptyChunk() {
        return new ArrayList<E>(this.windowSize);
    }
    
    @Override
    public E get(final int n) {
        return this.items.get(n);
    }
    
    public boolean hasMoreItemsAtBottom() {
        return this.hasBelow;
    }
    
    protected LoadResult<E> loadInBackground(final int n, final long n2, final boolean b) {
        return new LoadResult<E>(new ArrayList<E>(0), false, n2);
    }
    
    public void reload() {
        this.reloadRequested.set(true);
        if (this.loadRequested.compareAndSet(false, true)) {
            this.executor.execute(this.loadMoreData);
        }
    }
    
    public void setVisibleRange(final int p0, final int p1) {
        // 
        // This method could not be decompiled.
        // 
        // Original Bytecode:
        // 
        //     1: getfield        com/lumiyaviewer/lumiya/utils/wlist/ChunkedListLoader.lock:Ljava/lang/Object;
        //     4: astore_3       
        //     5: aload_3        
        //     6: monitorenter   
        //     7: aload_0        
        //     8: getfield        com/lumiyaviewer/lumiya/utils/wlist/ChunkedListLoader.items:Lcom/lumiyaviewer/lumiya/utils/wlist/ChunkedList;
        //    11: invokevirtual   com/lumiyaviewer/lumiya/utils/wlist/ChunkedList.size:()I
        //    14: istore          4
        //    16: aload_0        
        //    17: getfield        com/lumiyaviewer/lumiya/utils/wlist/ChunkedListLoader.loadAboveWanted:Z
        //    20: ifne            364
        //    23: aload_0        
        //    24: getfield        com/lumiyaviewer/lumiya/utils/wlist/ChunkedListLoader.loadAboveResult:Lcom/lumiyaviewer/lumiya/utils/wlist/ChunkedListLoader$LoadResult;
        //    27: ifnonnull       364
        //    30: ldc_w           "yes"
        //    33: astore          5
        //    35: aload_0        
        //    36: getfield        com/lumiyaviewer/lumiya/utils/wlist/ChunkedListLoader.loadBelowWanted:Z
        //    39: ifne            372
        //    42: aload_0        
        //    43: getfield        com/lumiyaviewer/lumiya/utils/wlist/ChunkedListLoader.loadBelowResult:Lcom/lumiyaviewer/lumiya/utils/wlist/ChunkedListLoader$LoadResult;
        //    46: ifnonnull       372
        //    49: ldc_w           "yes"
        //    52: astore          6
        //    54: ldc_w           "ChatView: new visible range %d, %d size %d above possible %s below possible %s"
        //    57: iconst_5       
        //    58: anewarray       Ljava/lang/Object;
        //    61: dup            
        //    62: iconst_0       
        //    63: iload_1        
        //    64: invokestatic    java/lang/Integer.valueOf:(I)Ljava/lang/Integer;
        //    67: aastore        
        //    68: dup            
        //    69: iconst_1       
        //    70: iload_2        
        //    71: invokestatic    java/lang/Integer.valueOf:(I)Ljava/lang/Integer;
        //    74: aastore        
        //    75: dup            
        //    76: iconst_2       
        //    77: iload           4
        //    79: invokestatic    java/lang/Integer.valueOf:(I)Ljava/lang/Integer;
        //    82: aastore        
        //    83: dup            
        //    84: iconst_3       
        //    85: aload           5
        //    87: aastore        
        //    88: dup            
        //    89: iconst_4       
        //    90: aload           6
        //    92: aastore        
        //    93: invokestatic    com/lumiyaviewer/lumiya/Debug.Printf:(Ljava/lang/String;[Ljava/lang/Object;)V
        //    96: aload_3        
        //    97: monitorexit    
        //    98: aload_0        
        //    99: getfield        com/lumiyaviewer/lumiya/utils/wlist/ChunkedListLoader.items:Lcom/lumiyaviewer/lumiya/utils/wlist/ChunkedList;
        //   102: invokevirtual   com/lumiyaviewer/lumiya/utils/wlist/ChunkedList.size:()I
        //   105: ifle            628
        //   108: iload_1        
        //   109: ifgt            401
        //   112: aload_0        
        //   113: getfield        com/lumiyaviewer/lumiya/utils/wlist/ChunkedListLoader.hasAbove:Z
        //   116: ifeq            401
        //   119: aload_0        
        //   120: getfield        com/lumiyaviewer/lumiya/utils/wlist/ChunkedListLoader.lock:Ljava/lang/Object;
        //   123: astore          5
        //   125: aload           5
        //   127: monitorenter   
        //   128: aload_0        
        //   129: getfield        com/lumiyaviewer/lumiya/utils/wlist/ChunkedListLoader.loadAboveWanted:Z
        //   132: ifne            387
        //   135: aload_0        
        //   136: getfield        com/lumiyaviewer/lumiya/utils/wlist/ChunkedListLoader.loadAboveResult:Lcom/lumiyaviewer/lumiya/utils/wlist/ChunkedListLoader$LoadResult;
        //   139: ifnonnull       387
        //   142: aload_0        
        //   143: aload_0        
        //   144: getfield        com/lumiyaviewer/lumiya/utils/wlist/ChunkedListLoader.items:Lcom/lumiyaviewer/lumiya/utils/wlist/ChunkedList;
        //   147: iconst_0       
        //   148: invokevirtual   com/lumiyaviewer/lumiya/utils/wlist/ChunkedList.get:(I)Ljava/lang/Object;
        //   151: checkcast       Lcom/lumiyaviewer/lumiya/utils/Identifiable;
        //   154: invokeinterface com/lumiyaviewer/lumiya/utils/Identifiable.getId:()Ljava/lang/Object;
        //   159: checkcast       Ljava/lang/Long;
        //   162: invokevirtual   java/lang/Long.longValue:()J
        //   165: putfield        com/lumiyaviewer/lumiya/utils/wlist/ChunkedListLoader.loadAboveTopmostId:J
        //   168: aload_0        
        //   169: iconst_1       
        //   170: putfield        com/lumiyaviewer/lumiya/utils/wlist/ChunkedListLoader.loadAboveWanted:Z
        //   173: ldc_w           "ChatView: requesting load above id %d"
        //   176: iconst_1       
        //   177: anewarray       Ljava/lang/Object;
        //   180: dup            
        //   181: iconst_0       
        //   182: aload_0        
        //   183: getfield        com/lumiyaviewer/lumiya/utils/wlist/ChunkedListLoader.loadAboveTopmostId:J
        //   186: invokestatic    java/lang/Long.valueOf:(J)Ljava/lang/Long;
        //   189: aastore        
        //   190: invokestatic    com/lumiyaviewer/lumiya/Debug.Printf:(Ljava/lang/String;[Ljava/lang/Object;)V
        //   193: iconst_1       
        //   194: istore          4
        //   196: aload           5
        //   198: monitorexit    
        //   199: iload_2        
        //   200: aload_0        
        //   201: getfield        com/lumiyaviewer/lumiya/utils/wlist/ChunkedListLoader.items:Lcom/lumiyaviewer/lumiya/utils/wlist/ChunkedList;
        //   204: invokevirtual   com/lumiyaviewer/lumiya/utils/wlist/ChunkedList.size:()I
        //   207: iconst_1       
        //   208: isub           
        //   209: if_icmplt       500
        //   212: aload_0        
        //   213: getfield        com/lumiyaviewer/lumiya/utils/wlist/ChunkedListLoader.hasBelow:Z
        //   216: ifeq            500
        //   219: aload_0        
        //   220: getfield        com/lumiyaviewer/lumiya/utils/wlist/ChunkedListLoader.lock:Ljava/lang/Object;
        //   223: astore          6
        //   225: aload           6
        //   227: monitorenter   
        //   228: iload           4
        //   230: istore_1       
        //   231: aload           6
        //   233: astore          5
        //   235: aload_0        
        //   236: getfield        com/lumiyaviewer/lumiya/utils/wlist/ChunkedListLoader.loadBelowWanted:Z
        //   239: ifne            321
        //   242: iload           4
        //   244: istore_1       
        //   245: aload           6
        //   247: astore          5
        //   249: aload_0        
        //   250: getfield        com/lumiyaviewer/lumiya/utils/wlist/ChunkedListLoader.loadBelowResult:Lcom/lumiyaviewer/lumiya/utils/wlist/ChunkedListLoader$LoadResult;
        //   253: ifnonnull       321
        //   256: aload_0        
        //   257: aload_0        
        //   258: getfield        com/lumiyaviewer/lumiya/utils/wlist/ChunkedListLoader.items:Lcom/lumiyaviewer/lumiya/utils/wlist/ChunkedList;
        //   261: aload_0        
        //   262: getfield        com/lumiyaviewer/lumiya/utils/wlist/ChunkedListLoader.items:Lcom/lumiyaviewer/lumiya/utils/wlist/ChunkedList;
        //   265: invokevirtual   com/lumiyaviewer/lumiya/utils/wlist/ChunkedList.size:()I
        //   268: iconst_1       
        //   269: isub           
        //   270: invokevirtual   com/lumiyaviewer/lumiya/utils/wlist/ChunkedList.get:(I)Ljava/lang/Object;
        //   273: checkcast       Lcom/lumiyaviewer/lumiya/utils/Identifiable;
        //   276: invokeinterface com/lumiyaviewer/lumiya/utils/Identifiable.getId:()Ljava/lang/Object;
        //   281: checkcast       Ljava/lang/Long;
        //   284: invokevirtual   java/lang/Long.longValue:()J
        //   287: putfield        com/lumiyaviewer/lumiya/utils/wlist/ChunkedListLoader.loadBelowLastId:J
        //   290: aload_0        
        //   291: iconst_1       
        //   292: putfield        com/lumiyaviewer/lumiya/utils/wlist/ChunkedListLoader.loadBelowWanted:Z
        //   295: ldc_w           "ChatView: requesting load below id %d"
        //   298: iconst_1       
        //   299: anewarray       Ljava/lang/Object;
        //   302: dup            
        //   303: iconst_0       
        //   304: aload_0        
        //   305: getfield        com/lumiyaviewer/lumiya/utils/wlist/ChunkedListLoader.loadBelowLastId:J
        //   308: invokestatic    java/lang/Long.valueOf:(J)Ljava/lang/Long;
        //   311: aastore        
        //   312: invokestatic    com/lumiyaviewer/lumiya/Debug.Printf:(Ljava/lang/String;[Ljava/lang/Object;)V
        //   315: iconst_1       
        //   316: istore_1       
        //   317: aload           6
        //   319: astore          5
        //   321: aload           5
        //   323: monitorexit    
        //   324: iload_1        
        //   325: ifeq            363
        //   328: aload_0        
        //   329: getfield        com/lumiyaviewer/lumiya/utils/wlist/ChunkedListLoader.loadRequested:Ljava/util/concurrent/atomic/AtomicBoolean;
        //   332: iconst_0       
        //   333: iconst_1       
        //   334: invokevirtual   java/util/concurrent/atomic/AtomicBoolean.compareAndSet:(ZZ)Z
        //   337: ifeq            793
        //   340: ldc_w           "ChatView: requesting loadMoreData ()"
        //   343: iconst_0       
        //   344: anewarray       Ljava/lang/Object;
        //   347: invokestatic    com/lumiyaviewer/lumiya/Debug.Printf:(Ljava/lang/String;[Ljava/lang/Object;)V
        //   350: aload_0        
        //   351: getfield        com/lumiyaviewer/lumiya/utils/wlist/ChunkedListLoader.executor:Ljava/util/concurrent/Executor;
        //   354: aload_0        
        //   355: getfield        com/lumiyaviewer/lumiya/utils/wlist/ChunkedListLoader.loadMoreData:Ljava/lang/Runnable;
        //   358: invokeinterface java/util/concurrent/Executor.execute:(Ljava/lang/Runnable;)V
        //   363: return         
        //   364: ldc_w           "no"
        //   367: astore          5
        //   369: goto            35
        //   372: ldc_w           "no"
        //   375: astore          6
        //   377: goto            54
        //   380: astore          5
        //   382: aload_3        
        //   383: monitorexit    
        //   384: aload           5
        //   386: athrow         
        //   387: iconst_0       
        //   388: istore          4
        //   390: goto            196
        //   393: astore          6
        //   395: aload           5
        //   397: monitorexit    
        //   398: aload           6
        //   400: athrow         
        //   401: iload_1        
        //   402: ifle            811
        //   405: aload_0        
        //   406: getfield        com/lumiyaviewer/lumiya/utils/wlist/ChunkedListLoader.lock:Ljava/lang/Object;
        //   409: astore          5
        //   411: aload           5
        //   413: monitorenter   
        //   414: aload_0        
        //   415: getfield        com/lumiyaviewer/lumiya/utils/wlist/ChunkedListLoader.loadAboveWanted:Z
        //   418: ifne            478
        //   421: aload_0        
        //   422: getfield        com/lumiyaviewer/lumiya/utils/wlist/ChunkedListLoader.loadAboveResult:Lcom/lumiyaviewer/lumiya/utils/wlist/ChunkedListLoader$LoadResult;
        //   425: astore          6
        //   427: aload           6
        //   429: ifnonnull       478
        //   432: iconst_1       
        //   433: istore          4
        //   435: aload           5
        //   437: monitorexit    
        //   438: iload           4
        //   440: ifeq            811
        //   443: aload_0        
        //   444: getfield        com/lumiyaviewer/lumiya/utils/wlist/ChunkedListLoader.items:Lcom/lumiyaviewer/lumiya/utils/wlist/ChunkedList;
        //   447: iload_1        
        //   448: invokevirtual   com/lumiyaviewer/lumiya/utils/wlist/ChunkedList.removeElementsBefore:(I)I
        //   451: istore_1       
        //   452: iload_1        
        //   453: ifeq            811
        //   456: aload_0        
        //   457: iconst_1       
        //   458: putfield        com/lumiyaviewer/lumiya/utils/wlist/ChunkedListLoader.hasAbove:Z
        //   461: aload_0        
        //   462: getfield        com/lumiyaviewer/lumiya/utils/wlist/ChunkedListLoader.listener:Lcom/lumiyaviewer/lumiya/utils/wlist/ChunkedListLoader$EventListener;
        //   465: iconst_0       
        //   466: iload_1        
        //   467: invokeinterface com/lumiyaviewer/lumiya/utils/wlist/ChunkedListLoader$EventListener.onListItemsRemoved:(II)V
        //   472: iconst_0       
        //   473: istore          4
        //   475: goto            199
        //   478: iconst_0       
        //   479: istore          4
        //   481: goto            435
        //   484: astore          6
        //   486: aload           5
        //   488: monitorexit    
        //   489: aload           6
        //   491: athrow         
        //   492: astore          5
        //   494: aload           6
        //   496: monitorexit    
        //   497: aload           5
        //   499: athrow         
        //   500: iload           4
        //   502: istore_1       
        //   503: iload_2        
        //   504: iflt            324
        //   507: iload           4
        //   509: istore_1       
        //   510: iload_2        
        //   511: aload_0        
        //   512: getfield        com/lumiyaviewer/lumiya/utils/wlist/ChunkedListLoader.items:Lcom/lumiyaviewer/lumiya/utils/wlist/ChunkedList;
        //   515: invokevirtual   com/lumiyaviewer/lumiya/utils/wlist/ChunkedList.size:()I
        //   518: iconst_1       
        //   519: isub           
        //   520: if_icmpge       324
        //   523: aload_0        
        //   524: getfield        com/lumiyaviewer/lumiya/utils/wlist/ChunkedListLoader.lock:Ljava/lang/Object;
        //   527: astore          5
        //   529: aload           5
        //   531: monitorenter   
        //   532: aload_0        
        //   533: getfield        com/lumiyaviewer/lumiya/utils/wlist/ChunkedListLoader.loadBelowWanted:Z
        //   536: ifne            614
        //   539: aload_0        
        //   540: getfield        com/lumiyaviewer/lumiya/utils/wlist/ChunkedListLoader.loadBelowResult:Lcom/lumiyaviewer/lumiya/utils/wlist/ChunkedListLoader$LoadResult;
        //   543: astore          6
        //   545: aload           6
        //   547: ifnonnull       614
        //   550: iconst_1       
        //   551: istore          7
        //   553: aload           5
        //   555: monitorexit    
        //   556: iload           4
        //   558: istore_1       
        //   559: iload           7
        //   561: ifeq            324
        //   564: aload_0        
        //   565: getfield        com/lumiyaviewer/lumiya/utils/wlist/ChunkedListLoader.items:Lcom/lumiyaviewer/lumiya/utils/wlist/ChunkedList;
        //   568: invokevirtual   com/lumiyaviewer/lumiya/utils/wlist/ChunkedList.size:()I
        //   571: istore          7
        //   573: aload_0        
        //   574: getfield        com/lumiyaviewer/lumiya/utils/wlist/ChunkedListLoader.items:Lcom/lumiyaviewer/lumiya/utils/wlist/ChunkedList;
        //   577: iload_2        
        //   578: invokevirtual   com/lumiyaviewer/lumiya/utils/wlist/ChunkedList.removeElementsAfter:(I)I
        //   581: istore_2       
        //   582: iload           4
        //   584: istore_1       
        //   585: iload_2        
        //   586: ifeq            324
        //   589: aload_0        
        //   590: iconst_1       
        //   591: putfield        com/lumiyaviewer/lumiya/utils/wlist/ChunkedListLoader.hasBelow:Z
        //   594: aload_0        
        //   595: getfield        com/lumiyaviewer/lumiya/utils/wlist/ChunkedListLoader.listener:Lcom/lumiyaviewer/lumiya/utils/wlist/ChunkedListLoader$EventListener;
        //   598: iload           7
        //   600: iload_2        
        //   601: isub           
        //   602: iload_2        
        //   603: invokeinterface com/lumiyaviewer/lumiya/utils/wlist/ChunkedListLoader$EventListener.onListItemsRemoved:(II)V
        //   608: iload           4
        //   610: istore_1       
        //   611: goto            324
        //   614: iconst_0       
        //   615: istore          7
        //   617: goto            553
        //   620: astore          6
        //   622: aload           5
        //   624: monitorexit    
        //   625: aload           6
        //   627: athrow         
        //   628: aload_0        
        //   629: getfield        com/lumiyaviewer/lumiya/utils/wlist/ChunkedListLoader.startFromStart:Z
        //   632: ifeq            713
        //   635: aload_0        
        //   636: getfield        com/lumiyaviewer/lumiya/utils/wlist/ChunkedListLoader.hasBelow:Z
        //   639: ifeq            806
        //   642: aload_0        
        //   643: getfield        com/lumiyaviewer/lumiya/utils/wlist/ChunkedListLoader.lock:Ljava/lang/Object;
        //   646: astore          5
        //   648: aload           5
        //   650: monitorenter   
        //   651: aload_0        
        //   652: getfield        com/lumiyaviewer/lumiya/utils/wlist/ChunkedListLoader.loadBelowWanted:Z
        //   655: ifne            700
        //   658: aload_0        
        //   659: getfield        com/lumiyaviewer/lumiya/utils/wlist/ChunkedListLoader.loadBelowResult:Lcom/lumiyaviewer/lumiya/utils/wlist/ChunkedListLoader$LoadResult;
        //   662: ifnonnull       700
        //   665: aload_0        
        //   666: lconst_0       
        //   667: putfield        com/lumiyaviewer/lumiya/utils/wlist/ChunkedListLoader.loadBelowLastId:J
        //   670: aload_0        
        //   671: iconst_1       
        //   672: putfield        com/lumiyaviewer/lumiya/utils/wlist/ChunkedListLoader.loadBelowWanted:Z
        //   675: ldc_w           "ChatView: requesting load below id %d"
        //   678: iconst_1       
        //   679: anewarray       Ljava/lang/Object;
        //   682: dup            
        //   683: iconst_0       
        //   684: aload_0        
        //   685: getfield        com/lumiyaviewer/lumiya/utils/wlist/ChunkedListLoader.loadBelowLastId:J
        //   688: invokestatic    java/lang/Long.valueOf:(J)Ljava/lang/Long;
        //   691: aastore        
        //   692: invokestatic    com/lumiyaviewer/lumiya/Debug.Printf:(Ljava/lang/String;[Ljava/lang/Object;)V
        //   695: iconst_1       
        //   696: istore_1       
        //   697: goto            321
        //   700: iconst_0       
        //   701: istore_1       
        //   702: goto            321
        //   705: astore          6
        //   707: aload           5
        //   709: monitorexit    
        //   710: aload           6
        //   712: athrow         
        //   713: aload_0        
        //   714: getfield        com/lumiyaviewer/lumiya/utils/wlist/ChunkedListLoader.hasAbove:Z
        //   717: ifeq            806
        //   720: aload_0        
        //   721: getfield        com/lumiyaviewer/lumiya/utils/wlist/ChunkedListLoader.lock:Ljava/lang/Object;
        //   724: astore          5
        //   726: aload           5
        //   728: monitorenter   
        //   729: aload_0        
        //   730: getfield        com/lumiyaviewer/lumiya/utils/wlist/ChunkedListLoader.loadAboveWanted:Z
        //   733: ifne            780
        //   736: aload_0        
        //   737: getfield        com/lumiyaviewer/lumiya/utils/wlist/ChunkedListLoader.loadAboveResult:Lcom/lumiyaviewer/lumiya/utils/wlist/ChunkedListLoader$LoadResult;
        //   740: ifnonnull       780
        //   743: aload_0        
        //   744: ldc2_w          9223372036854775807
        //   747: putfield        com/lumiyaviewer/lumiya/utils/wlist/ChunkedListLoader.loadAboveTopmostId:J
        //   750: aload_0        
        //   751: iconst_1       
        //   752: putfield        com/lumiyaviewer/lumiya/utils/wlist/ChunkedListLoader.loadAboveWanted:Z
        //   755: ldc_w           "ChatView: requesting load above id %d"
        //   758: iconst_1       
        //   759: anewarray       Ljava/lang/Object;
        //   762: dup            
        //   763: iconst_0       
        //   764: aload_0        
        //   765: getfield        com/lumiyaviewer/lumiya/utils/wlist/ChunkedListLoader.loadAboveTopmostId:J
        //   768: invokestatic    java/lang/Long.valueOf:(J)Ljava/lang/Long;
        //   771: aastore        
        //   772: invokestatic    com/lumiyaviewer/lumiya/Debug.Printf:(Ljava/lang/String;[Ljava/lang/Object;)V
        //   775: iconst_1       
        //   776: istore_1       
        //   777: goto            321
        //   780: iconst_0       
        //   781: istore_1       
        //   782: goto            321
        //   785: astore          6
        //   787: aload           5
        //   789: monitorexit    
        //   790: aload           6
        //   792: athrow         
        //   793: ldc_w           "ChatView: loadMoreData() already requested"
        //   796: iconst_0       
        //   797: anewarray       Ljava/lang/Object;
        //   800: invokestatic    com/lumiyaviewer/lumiya/Debug.Printf:(Ljava/lang/String;[Ljava/lang/Object;)V
        //   803: goto            363
        //   806: iconst_0       
        //   807: istore_1       
        //   808: goto            324
        //   811: iconst_0       
        //   812: istore          4
        //   814: goto            199
        //    Exceptions:
        //  Try           Handler
        //  Start  End    Start  End    Type
        //  -----  -----  -----  -----  ----
        //  7      30     380    387    Any
        //  35     49     380    387    Any
        //  54     96     380    387    Any
        //  128    193    393    401    Any
        //  235    242    492    500    Any
        //  249    315    492    500    Any
        //  414    427    484    492    Any
        //  532    545    620    628    Any
        //  651    695    705    713    Any
        //  729    775    785    793    Any
        // 
        // The error that occurred was:
        // 
        // java.lang.IndexOutOfBoundsException: Index 416 out of bounds for length 416
        //     at java.base/jdk.internal.util.Preconditions.outOfBounds(Preconditions.java:100)
        //     at java.base/jdk.internal.util.Preconditions.outOfBoundsCheckIndex(Preconditions.java:106)
        //     at java.base/jdk.internal.util.Preconditions.checkIndex(Preconditions.java:302)
        //     at java.base/java.util.Objects.checkIndex(Objects.java:385)
        //     at java.base/java.util.ArrayList.get(ArrayList.java:427)
        //     at com.strobel.decompiler.ast.AstBuilder.convertToAst(AstBuilder.java:3362)
        //     at com.strobel.decompiler.ast.AstBuilder.build(AstBuilder.java:112)
        //     at com.strobel.decompiler.languages.java.ast.AstMethodBodyBuilder.createMethodBody(AstMethodBodyBuilder.java:203)
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
    
    @Override
    public int size() {
        return this.items.size();
    }
    
    public void updateElement(final E e) {
        Debug.Printf("ChatView: addElement: updated element with id %d", ((Identifiable<Object>)e).getId());
        synchronized (this.lock) {
            this.updatedElements.put(e.getId(), e);
            monitorexit(this.lock);
            this.postUpdate();
        }
    }
    
    public interface EventListener
    {
        @Nonnull
        Executor getListEventsExecutor();
        
        void onListItemAddedAtEnd();
        
        void onListItemChanged(final int p0);
        
        void onListItemsAdded(final int p0, final int p1);
        
        void onListItemsRemoved(final int p0, final int p1);
        
        void onListReloaded();
    }
    
    protected static class LoadResult<E>
    {
        public final List<E> entries;
        final long fromId;
        final boolean hasMore;
        
        public LoadResult(final List<E> entries, final boolean hasMore, final long fromId) {
            this.entries = entries;
            this.hasMore = hasMore;
            this.fromId = fromId;
        }
    }
}
