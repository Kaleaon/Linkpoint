// 
// Decompiled by Procyon v0.6.0
// 

package com.lumiyaviewer.lumiya.slproto;

import java.io.IOException;
import java.util.Timer;
import java.nio.channels.Selector;

public class SLConnection implements Runnable
{
    private static final int DEFAULT_IDLE_INTERVAL = 1000;
    private Selector selector;
    private volatile Timer timer;
    private Thread workingThread;
    
    public SLConnection() {
        System.setProperty("java.net.preferIPv4Stack", "true");
        System.setProperty("java.net.preferIPv6Addresses", "false");
        while (true) {
            try {
                this.selector = Selector.open();
                this.workingThread = null;
                this.timer = null;
            }
            catch (final IOException ex) {
                ex.printStackTrace();
                continue;
            }
            break;
        }
    }
    
    public void AddCircuit(final SLCircuit slCircuit) {
        synchronized (this) {
            if (this.workingThread == null) {
                (this.workingThread = new Thread(this, "SLConnection")).start();
            }
        }
    }
    
    public Selector getSelector() {
        return this.selector;
    }
    
    public Timer getTimer() {
        synchronized (this) {
            if (this.timer == null) {
                this.timer = new Timer("SLConnectionTimer", true);
            }
            return this.timer;
        }
    }
    
    @Override
    public void run() {
        // 
        // This method could not be decompiled.
        // 
        // Original Bytecode:
        // 
        //     2: invokestatic    com/lumiyaviewer/lumiya/Debug.Log:(Ljava/lang/String;)V
        //     5: aload_0        
        //     6: getfield        com/lumiyaviewer/lumiya/slproto/SLConnection.selector:Ljava/nio/channels/Selector;
        //     9: invokevirtual   java/nio/channels/Selector.keys:()Ljava/util/Set;
        //    12: invokeinterface java/util/Set.isEmpty:()Z
        //    17: ifne            298
        //    20: sipush          1000
        //    23: istore_1       
        //    24: aload_0        
        //    25: getfield        com/lumiyaviewer/lumiya/slproto/SLConnection.selector:Ljava/nio/channels/Selector;
        //    28: invokevirtual   java/nio/channels/Selector.keys:()Ljava/util/Set;
        //    31: invokeinterface java/util/Set.iterator:()Ljava/util/Iterator;
        //    36: astore_2       
        //    37: aload_2        
        //    38: invokeinterface java/util/Iterator.hasNext:()Z
        //    43: ifeq            105
        //    46: aload_2        
        //    47: invokeinterface java/util/Iterator.next:()Ljava/lang/Object;
        //    52: checkcast       Ljava/nio/channels/SelectionKey;
        //    55: astore_3       
        //    56: aload_3        
        //    57: invokevirtual   java/nio/channels/SelectionKey.attachment:()Ljava/lang/Object;
        //    60: checkcast       Lcom/lumiyaviewer/lumiya/slproto/SLCircuit;
        //    63: astore          4
        //    65: aload           4
        //    67: ifnull          101
        //    70: aload_3        
        //    71: invokevirtual   java/nio/channels/SelectionKey.isValid:()Z
        //    74: ifeq            365
        //    77: aload           4
        //    79: invokevirtual   com/lumiyaviewer/lumiya/slproto/SLCircuit.ProcessWakeup:()V
        //    82: aload           4
        //    84: invokevirtual   com/lumiyaviewer/lumiya/slproto/SLCircuit.getIdleInterval:()I
        //    87: istore          5
        //    89: iload           5
        //    91: iload_1        
        //    92: if_icmpge       365
        //    95: iload           5
        //    97: istore_1       
        //    98: goto            37
        //   101: goto            98
        //   104: astore_2       
        //   105: aload_0        
        //   106: getfield        com/lumiyaviewer/lumiya/slproto/SLConnection.selector:Ljava/nio/channels/Selector;
        //   109: invokevirtual   java/nio/channels/Selector.selectedKeys:()Ljava/util/Set;
        //   112: invokeinterface java/util/Set.iterator:()Ljava/util/Iterator;
        //   117: astore_2       
        //   118: aload_2        
        //   119: invokeinterface java/util/Iterator.hasNext:()Z
        //   124: ifeq            218
        //   127: aload_2        
        //   128: invokeinterface java/util/Iterator.next:()Ljava/lang/Object;
        //   133: checkcast       Ljava/nio/channels/SelectionKey;
        //   136: astore_3       
        //   137: aload_2        
        //   138: invokeinterface java/util/Iterator.remove:()V
        //   143: aload_3        
        //   144: invokevirtual   java/nio/channels/SelectionKey.attachment:()Ljava/lang/Object;
        //   147: checkcast       Lcom/lumiyaviewer/lumiya/slproto/SLCircuit;
        //   150: astore          4
        //   152: aload           4
        //   154: ifnull          118
        //   157: aload_3        
        //   158: invokevirtual   java/nio/channels/SelectionKey.isValid:()Z
        //   161: ifeq            177
        //   164: aload_3        
        //   165: invokevirtual   java/nio/channels/SelectionKey.isReadable:()Z
        //   168: ifeq            177
        //   171: aload           4
        //   173: invokevirtual   com/lumiyaviewer/lumiya/slproto/SLCircuit.ProcessReceive:()Z
        //   176: pop            
        //   177: aload_3        
        //   178: invokevirtual   java/nio/channels/SelectionKey.isValid:()Z
        //   181: ifeq            197
        //   184: aload_3        
        //   185: invokevirtual   java/nio/channels/SelectionKey.isWritable:()Z
        //   188: ifeq            197
        //   191: aload           4
        //   193: invokevirtual   com/lumiyaviewer/lumiya/slproto/SLCircuit.ProcessTransmit:()Z
        //   196: pop            
        //   197: aload_3        
        //   198: invokevirtual   java/nio/channels/SelectionKey.isValid:()Z
        //   201: ifeq            118
        //   204: aload           4
        //   206: invokevirtual   com/lumiyaviewer/lumiya/slproto/SLCircuit.UpdateSelectorOps:()V
        //   209: aload           4
        //   211: invokevirtual   com/lumiyaviewer/lumiya/slproto/SLCircuit.TryProcessIdle:()V
        //   214: goto            118
        //   217: astore_2       
        //   218: aload_0        
        //   219: getfield        com/lumiyaviewer/lumiya/slproto/SLConnection.selector:Ljava/nio/channels/Selector;
        //   222: iload_1        
        //   223: i2l            
        //   224: invokevirtual   java/nio/channels/Selector.select:(J)I
        //   227: pop            
        //   228: goto            5
        //   231: astore_2       
        //   232: aload_2        
        //   233: invokevirtual   java/io/IOException.printStackTrace:()V
        //   236: aload_0        
        //   237: getfield        com/lumiyaviewer/lumiya/slproto/SLConnection.selector:Ljava/nio/channels/Selector;
        //   240: invokevirtual   java/nio/channels/Selector.keys:()Ljava/util/Set;
        //   243: invokeinterface java/util/Set.iterator:()Ljava/util/Iterator;
        //   248: astore_2       
        //   249: aload_2        
        //   250: invokeinterface java/util/Iterator.hasNext:()Z
        //   255: ifeq            298
        //   258: aload_2        
        //   259: invokeinterface java/util/Iterator.next:()Ljava/lang/Object;
        //   264: checkcast       Ljava/nio/channels/SelectionKey;
        //   267: astore_3       
        //   268: aload_3        
        //   269: invokevirtual   java/nio/channels/SelectionKey.attachment:()Ljava/lang/Object;
        //   272: checkcast       Lcom/lumiyaviewer/lumiya/slproto/SLCircuit;
        //   275: astore          4
        //   277: aload           4
        //   279: ifnull          249
        //   282: aload_3        
        //   283: invokevirtual   java/nio/channels/SelectionKey.isValid:()Z
        //   286: ifeq            249
        //   289: aload           4
        //   291: invokevirtual   com/lumiyaviewer/lumiya/slproto/SLCircuit.ProcessNetworkError:()V
        //   294: goto            249
        //   297: astore_2       
        //   298: ldc             "working thread exiting"
        //   300: invokestatic    com/lumiyaviewer/lumiya/Debug.Log:(Ljava/lang/String;)V
        //   303: aload_0        
        //   304: monitorenter   
        //   305: aload_0        
        //   306: aconst_null    
        //   307: putfield        com/lumiyaviewer/lumiya/slproto/SLConnection.workingThread:Ljava/lang/Thread;
        //   310: aload_0        
        //   311: getfield        com/lumiyaviewer/lumiya/slproto/SLConnection.timer:Ljava/util/Timer;
        //   314: ifnull          329
        //   317: aload_0        
        //   318: getfield        com/lumiyaviewer/lumiya/slproto/SLConnection.timer:Ljava/util/Timer;
        //   321: invokevirtual   java/util/Timer.cancel:()V
        //   324: aload_0        
        //   325: aconst_null    
        //   326: putfield        com/lumiyaviewer/lumiya/slproto/SLConnection.timer:Ljava/util/Timer;
        //   329: aload_0        
        //   330: monitorexit    
        //   331: return         
        //   332: astore_2       
        //   333: aload_0        
        //   334: monitorexit    
        //   335: aload_2        
        //   336: athrow         
        //   337: astore_2       
        //   338: goto            298
        //   341: astore_2       
        //   342: goto            298
        //   345: astore_2       
        //   346: goto            298
        //   349: astore_2       
        //   350: goto            218
        //   353: astore_2       
        //   354: goto            218
        //   357: astore_2       
        //   358: goto            218
        //   361: astore_2       
        //   362: goto            105
        //   365: goto            98
        //    Exceptions:
        //  Try           Handler
        //  Start  End    Start  End    Type                                       
        //  -----  -----  -----  -----  -------------------------------------------
        //  24     37     231    349    Ljava/io/IOException;
        //  37     65     104    105    Ljava/util/NoSuchElementException;
        //  37     65     361    365    Ljava/util/ConcurrentModificationException;
        //  37     65     231    349    Ljava/io/IOException;
        //  70     89     104    105    Ljava/util/NoSuchElementException;
        //  70     89     361    365    Ljava/util/ConcurrentModificationException;
        //  70     89     231    349    Ljava/io/IOException;
        //  105    118    231    349    Ljava/io/IOException;
        //  118    152    217    218    Ljava/nio/channels/CancelledKeyException;
        //  118    152    349    353    Ljava/util/NoSuchElementException;
        //  118    152    353    357    Ljava/nio/channels/ClosedSelectorException;
        //  118    152    357    361    Ljava/util/ConcurrentModificationException;
        //  118    152    231    349    Ljava/io/IOException;
        //  157    177    217    218    Ljava/nio/channels/CancelledKeyException;
        //  157    177    349    353    Ljava/util/NoSuchElementException;
        //  157    177    353    357    Ljava/nio/channels/ClosedSelectorException;
        //  157    177    357    361    Ljava/util/ConcurrentModificationException;
        //  157    177    231    349    Ljava/io/IOException;
        //  177    197    217    218    Ljava/nio/channels/CancelledKeyException;
        //  177    197    349    353    Ljava/util/NoSuchElementException;
        //  177    197    353    357    Ljava/nio/channels/ClosedSelectorException;
        //  177    197    357    361    Ljava/util/ConcurrentModificationException;
        //  177    197    231    349    Ljava/io/IOException;
        //  197    214    217    218    Ljava/nio/channels/CancelledKeyException;
        //  197    214    349    353    Ljava/util/NoSuchElementException;
        //  197    214    353    357    Ljava/nio/channels/ClosedSelectorException;
        //  197    214    357    361    Ljava/util/ConcurrentModificationException;
        //  197    214    231    349    Ljava/io/IOException;
        //  218    228    231    349    Ljava/io/IOException;
        //  249    277    297    298    Ljava/util/NoSuchElementException;
        //  249    277    337    341    Ljava/nio/channels/CancelledKeyException;
        //  249    277    341    345    Ljava/nio/channels/ClosedSelectorException;
        //  249    277    345    349    Ljava/util/ConcurrentModificationException;
        //  282    294    297    298    Ljava/util/NoSuchElementException;
        //  282    294    337    341    Ljava/nio/channels/CancelledKeyException;
        //  282    294    341    345    Ljava/nio/channels/ClosedSelectorException;
        //  282    294    345    349    Ljava/util/ConcurrentModificationException;
        //  305    329    332    337    Any
        // 
        // The error that occurred was:
        // 
        // java.lang.IndexOutOfBoundsException: Index 164 out of bounds for length 164
        //     at java.base/jdk.internal.util.Preconditions.outOfBounds(Preconditions.java:100)
        //     at java.base/jdk.internal.util.Preconditions.outOfBoundsCheckIndex(Preconditions.java:106)
        //     at java.base/jdk.internal.util.Preconditions.checkIndex(Preconditions.java:302)
        //     at java.base/java.util.Objects.checkIndex(Objects.java:385)
        //     at java.base/java.util.ArrayList.get(ArrayList.java:427)
        //     at com.strobel.decompiler.ast.AstBuilder.convertToAst(AstBuilder.java:3362)
        //     at com.strobel.decompiler.ast.AstBuilder.convertToAst(AstBuilder.java:3611)
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
}
