// 
// Decompiled by Procyon v0.6.0
// 

package com.lumiyaviewer.lumiya.slproto.handler;

import java.util.Collection;
import java.util.LinkedList;
import java.lang.reflect.InvocationTargetException;
import com.lumiyaviewer.lumiya.Debug;
import java.lang.ref.WeakReference;
import java.lang.reflect.Method;
import java.util.Iterator;
import com.lumiyaviewer.lumiya.slproto.llsd.LLSDNode;
import java.util.HashMap;
import com.lumiyaviewer.lumiya.slproto.caps.SLCapEventQueue;
import java.util.Map;

public class SLMessageRouter
{
    private Map<SLCapEventQueue.CapsEventType, HandlerList> eventQueueMessageHandlers;
    private Map<Class<?>, HandlerList> messageHandlers;
    
    public SLMessageRouter() {
        this.messageHandlers = new HashMap<Class<?>, HandlerList>();
        this.eventQueueMessageHandlers = new HashMap<SLCapEventQueue.CapsEventType, HandlerList>();
    }
    
    public boolean handleEventQueueMessage(final SLCapEventQueue.CapsEventType capsEventType, final LLSDNode llsdNode) {
        synchronized (this) {
            final HandlerList list = this.eventQueueMessageHandlers.get(capsEventType);
            if (list != null) {
                list.invokeAll(llsdNode);
                return true;
            }
            return false;
        }
    }
    
    public boolean handleMessage(final Object o) {
        synchronized (this) {
            final HandlerList list = this.messageHandlers.get(o.getClass());
            if (list != null) {
                list.invokeAll(o);
                return true;
            }
            return false;
        }
    }
    
    public void registerHandler(final Object p0) {
        // 
        // This method could not be decompiled.
        // 
        // Original Bytecode:
        // 
        //     1: monitorenter   
        //     2: aload_1        
        //     3: invokevirtual   java/lang/Object.getClass:()Ljava/lang/Class;
        //     6: invokevirtual   java/lang/Class.getMethods:()[Ljava/lang/reflect/Method;
        //     9: astore_2       
        //    10: aload_2        
        //    11: arraylength    
        //    12: istore_3       
        //    13: iconst_0       
        //    14: istore          4
        //    16: iload           4
        //    18: iload_3        
        //    19: if_icmpge       274
        //    22: aload_2        
        //    23: iload           4
        //    25: aaload         
        //    26: astore          5
        //    28: aload           5
        //    30: ldc             Lcom/lumiyaviewer/lumiya/slproto/handler/SLMessageHandler;.class
        //    32: invokevirtual   java/lang/reflect/Method.getAnnotation:(Ljava/lang/Class;)Ljava/lang/annotation/Annotation;
        //    35: checkcast       Lcom/lumiyaviewer/lumiya/slproto/handler/SLMessageHandler;
        //    38: ifnull          149
        //    41: aload           5
        //    43: invokevirtual   java/lang/reflect/Method.getParameterTypes:()[Ljava/lang/Class;
        //    46: astore          6
        //    48: aload           6
        //    50: arraylength    
        //    51: iconst_1       
        //    52: if_icmpeq       72
        //    55: new             Ljava/lang/IllegalArgumentException;
        //    58: astore_1       
        //    59: aload_1        
        //    60: ldc             "SLMessageHandler methods must specify a single SLMessage paramter."
        //    62: invokespecial   java/lang/IllegalArgumentException.<init>:(Ljava/lang/String;)V
        //    65: aload_1        
        //    66: athrow         
        //    67: astore_1       
        //    68: aload_0        
        //    69: monitorexit    
        //    70: aload_1        
        //    71: athrow         
        //    72: aload           6
        //    74: iconst_0       
        //    75: aaload         
        //    76: astore          7
        //    78: new             Lcom/lumiyaviewer/lumiya/slproto/handler/SLMessageRouter$HandlerInfo;
        //    81: astore          8
        //    83: aload           8
        //    85: aload           5
        //    87: aload_1        
        //    88: invokespecial   com/lumiyaviewer/lumiya/slproto/handler/SLMessageRouter$HandlerInfo.<init>:(Ljava/lang/reflect/Method;Ljava/lang/Object;)V
        //    91: aload_0        
        //    92: getfield        com/lumiyaviewer/lumiya/slproto/handler/SLMessageRouter.messageHandlers:Ljava/util/Map;
        //    95: aload           7
        //    97: invokeinterface java/util/Map.get:(Ljava/lang/Object;)Ljava/lang/Object;
        //   102: checkcast       Lcom/lumiyaviewer/lumiya/slproto/handler/SLMessageRouter$HandlerList;
        //   105: astore          9
        //   107: aload           9
        //   109: astore          6
        //   111: aload           9
        //   113: ifnonnull       141
        //   116: new             Lcom/lumiyaviewer/lumiya/slproto/handler/SLMessageRouter$HandlerList;
        //   119: astore          6
        //   121: aload           6
        //   123: aconst_null    
        //   124: invokespecial   com/lumiyaviewer/lumiya/slproto/handler/SLMessageRouter$HandlerList.<init>:(Lcom/lumiyaviewer/lumiya/slproto/handler/SLMessageRouter$HandlerList;)V
        //   127: aload_0        
        //   128: getfield        com/lumiyaviewer/lumiya/slproto/handler/SLMessageRouter.messageHandlers:Ljava/util/Map;
        //   131: aload           7
        //   133: aload           6
        //   135: invokeinterface java/util/Map.put:(Ljava/lang/Object;Ljava/lang/Object;)Ljava/lang/Object;
        //   140: pop            
        //   141: aload           6
        //   143: aload           8
        //   145: invokevirtual   com/lumiyaviewer/lumiya/slproto/handler/SLMessageRouter$HandlerList.add:(Ljava/lang/Object;)Z
        //   148: pop            
        //   149: aload           5
        //   151: ldc             Lcom/lumiyaviewer/lumiya/slproto/handler/SLEventQueueMessageHandler;.class
        //   153: invokevirtual   java/lang/reflect/Method.getAnnotation:(Ljava/lang/Class;)Ljava/lang/annotation/Annotation;
        //   156: checkcast       Lcom/lumiyaviewer/lumiya/slproto/handler/SLEventQueueMessageHandler;
        //   159: astore          6
        //   161: aload           6
        //   163: ifnull          268
        //   166: aload           5
        //   168: invokevirtual   java/lang/reflect/Method.getParameterTypes:()[Ljava/lang/Class;
        //   171: arraylength    
        //   172: iconst_1       
        //   173: if_icmpeq       188
        //   176: new             Ljava/lang/IllegalArgumentException;
        //   179: astore_1       
        //   180: aload_1        
        //   181: ldc             "SLMessageHandler methods must specify a single LLSDNode paramter."
        //   183: invokespecial   java/lang/IllegalArgumentException.<init>:(Ljava/lang/String;)V
        //   186: aload_1        
        //   187: athrow         
        //   188: aload           6
        //   190: invokeinterface com/lumiyaviewer/lumiya/slproto/handler/SLEventQueueMessageHandler.eventName:()Lcom/lumiyaviewer/lumiya/slproto/caps/SLCapEventQueue$CapsEventType;
        //   195: astore          8
        //   197: new             Lcom/lumiyaviewer/lumiya/slproto/handler/SLMessageRouter$HandlerInfo;
        //   200: astore          7
        //   202: aload           7
        //   204: aload           5
        //   206: aload_1        
        //   207: invokespecial   com/lumiyaviewer/lumiya/slproto/handler/SLMessageRouter$HandlerInfo.<init>:(Ljava/lang/reflect/Method;Ljava/lang/Object;)V
        //   210: aload_0        
        //   211: getfield        com/lumiyaviewer/lumiya/slproto/handler/SLMessageRouter.eventQueueMessageHandlers:Ljava/util/Map;
        //   214: aload           8
        //   216: invokeinterface java/util/Map.get:(Ljava/lang/Object;)Ljava/lang/Object;
        //   221: checkcast       Lcom/lumiyaviewer/lumiya/slproto/handler/SLMessageRouter$HandlerList;
        //   224: astore          9
        //   226: aload           9
        //   228: astore          6
        //   230: aload           9
        //   232: ifnonnull       260
        //   235: new             Lcom/lumiyaviewer/lumiya/slproto/handler/SLMessageRouter$HandlerList;
        //   238: astore          6
        //   240: aload           6
        //   242: aconst_null    
        //   243: invokespecial   com/lumiyaviewer/lumiya/slproto/handler/SLMessageRouter$HandlerList.<init>:(Lcom/lumiyaviewer/lumiya/slproto/handler/SLMessageRouter$HandlerList;)V
        //   246: aload_0        
        //   247: getfield        com/lumiyaviewer/lumiya/slproto/handler/SLMessageRouter.eventQueueMessageHandlers:Ljava/util/Map;
        //   250: aload           8
        //   252: aload           6
        //   254: invokeinterface java/util/Map.put:(Ljava/lang/Object;Ljava/lang/Object;)Ljava/lang/Object;
        //   259: pop            
        //   260: aload           6
        //   262: aload           7
        //   264: invokevirtual   com/lumiyaviewer/lumiya/slproto/handler/SLMessageRouter$HandlerList.add:(Ljava/lang/Object;)Z
        //   267: pop            
        //   268: iinc            4, 1
        //   271: goto            16
        //   274: aload_0        
        //   275: monitorexit    
        //   276: return         
        //    Exceptions:
        //  Try           Handler
        //  Start  End    Start  End    Type
        //  -----  -----  -----  -----  ----
        //  2      13     67     72     Any
        //  28     67     67     72     Any
        //  78     107    67     72     Any
        //  116    141    67     72     Any
        //  141    149    67     72     Any
        //  149    161    67     72     Any
        //  166    188    67     72     Any
        //  188    226    67     72     Any
        //  235    260    67     72     Any
        //  260    268    67     72     Any
        // 
        // The error that occurred was:
        // 
        // java.lang.NullPointerException: Cannot invoke "com.strobel.assembler.metadata.TypeReference.getSimpleType()" because "type" is null
        //     at com.strobel.assembler.ir.StackMappingVisitor.push(StackMappingVisitor.java:290)
        //     at com.strobel.assembler.ir.StackMappingVisitor$InstructionAnalyzer.execute(StackMappingVisitor.java:837)
        //     at com.strobel.assembler.ir.StackMappingVisitor$InstructionAnalyzer.visit(StackMappingVisitor.java:398)
        //     at com.strobel.decompiler.ast.AstBuilder.performStackAnalysis(AstBuilder.java:2086)
        //     at com.strobel.decompiler.ast.AstBuilder.build(AstBuilder.java:108)
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
    
    public void unregisterHandler(final Object o) {
        synchronized (this) {
            final Iterator<Object> iterator = this.messageHandlers.values().iterator();
            while (iterator.hasNext()) {
                iterator.next().deleteAll(o);
            }
        }
        final Iterator<Object> iterator2 = this.eventQueueMessageHandlers.values().iterator();
        while (iterator2.hasNext()) {
            final Throwable t;
            iterator2.next().deleteAll(t);
        }
        monitorexit(this);
    }
    
    private static class HandlerInfo
    {
        private final Method method;
        private final WeakReference<?> subscriber;
        
        public HandlerInfo(final Method method, final Object referent) {
            this.method = method;
            this.subscriber = new WeakReference<Object>(referent);
        }
        
        public void invoke(Object cause) {
            try {
                final Object value = this.subscriber.get();
                if (value != null) {
                    this.method.invoke(value, cause);
                }
            }
            catch (final InvocationTargetException ex) {
                Debug.Log("InvocationTargetException in handler for " + cause.getClass().getSimpleName());
                cause = (IllegalAccessException)ex.getCause();
                if (cause != null) {
                    cause.printStackTrace();
                    return;
                }
                ex.printStackTrace();
            }
            catch (final IllegalAccessException cause) {
                cause.printStackTrace();
            }
            catch (final IllegalArgumentException cause) {
                cause.printStackTrace();
            }
        }
    }
    
    private static class HandlerList extends LinkedList<HandlerInfo>
    {
        public void deleteAll(final Object o) {
            final LinkedList c = new LinkedList();
            for (final HandlerInfo e : this) {
                final Object value = e.subscriber.get();
                if (value == null || value == o) {
                    c.add(e);
                }
            }
            this.removeAll(c);
        }
        
        public void invokeAll(final Object o) {
            final Iterator<HandlerInfo> iterator = this.iterator();
            while (iterator.hasNext()) {
                iterator.next().invoke(o);
            }
        }
    }
}
