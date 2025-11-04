// 
// Decompiled by Procyon v0.6.0
// 

package com.lumiyaviewer.lumiya.render.spatial;

import com.lumiyaviewer.lumiya.res.collections.WeakQueue;
import com.lumiyaviewer.lumiya.render.avatar.DrawableAvatar;
import com.lumiyaviewer.lumiya.slproto.objects.SLObjectInfo;
import com.lumiyaviewer.lumiya.res.executors.PrimComputeExecutor;
import com.lumiyaviewer.lumiya.slproto.terrain.TerrainPatchInfo;
import java.util.Iterator;
import java.util.HashMap;
import java.util.Collections;
import java.util.IdentityHashMap;
import com.lumiyaviewer.lumiya.slproto.terrain.TerrainData;
import java.util.Map;
import java.util.Set;
import com.lumiyaviewer.lumiya.render.DrawableStore;
import java.util.concurrent.atomic.AtomicBoolean;

public class SpatialObjectIndex
{
    private static final int NUM_DEPTH_BINS = 16;
    private static final float REGION_SIZE_XY = 256.0f;
    private static final float REGION_SIZE_Z = 4096.0f;
    private volatile int avatarCountLimit;
    private final AtomicBoolean drawListUpdateRequested;
    private final DrawListUpdateTask drawListUpdateTask;
    private final DrawableStore drawableStore;
    private final AtomicBoolean frustrumChanged;
    private volatile FrustrumInfo frustrumInfo;
    private volatile FrustrumPlanes frustrumPlanes;
    private volatile boolean indexDisabled;
    private volatile boolean initialUpdateCompleted;
    private final Object lock;
    private final Object objectUpdateRemoveLock;
    private volatile DrawList objectsInFrustrum;
    private final Set<DrawListObjectEntry> objectsToRemove;
    private final Set<DrawListObjectEntry> objectsToUpdate;
    private final ObjectsUpdateTask objectsUpdateTask;
    private final SpatialTree spatialTree;
    private final DrawListTerrainEntry[][] terrain;
    private final Map<Integer, TerrainData> terrainDirty;
    private final Object terrainLock;
    private final Runnable terrainUpdate;
    
    public SpatialObjectIndex(final DrawableStore drawableStore, final int avatarCountLimit) {
        this.frustrumChanged = new AtomicBoolean(false);
        this.drawListUpdateRequested = new AtomicBoolean(false);
        this.objectUpdateRemoveLock = new Object();
        this.objectsToUpdate = Collections.newSetFromMap(new IdentityHashMap<DrawListObjectEntry, Boolean>());
        this.objectsToRemove = Collections.newSetFromMap(new IdentityHashMap<DrawListObjectEntry, Boolean>());
        this.initialUpdateCompleted = false;
        this.indexDisabled = false;
        this.terrainLock = new Object();
        this.terrain = new DrawListTerrainEntry[16][16];
        this.terrainDirty = new HashMap<Integer, TerrainData>();
        this.lock = new Object();
        this.frustrumInfo = null;
        this.frustrumPlanes = null;
        this.avatarCountLimit = 5;
        this.drawListUpdateTask = new DrawListUpdateTask((DrawListUpdateTask)null);
        this.objectsUpdateTask = new ObjectsUpdateTask((ObjectsUpdateTask)null);
        this.terrainUpdate = new Runnable() {
            @Override
            public void run() {
                int n = 0;
                while (true) {
                    Label_0115: {
                        if (!SpatialObjectIndex.this.initialUpdateCompleted || !(SpatialObjectIndex.this.indexDisabled ^ true)) {
                            break Label_0115;
                        }
                        while (true) {
                            while (true) {
                                Label_0339: {
                                    int intValue;
                                    synchronized (SpatialObjectIndex.this.terrainLock) {
                                        final Iterator iterator = SpatialObjectIndex.this.terrainDirty.entrySet().iterator();
                                        if (!iterator.hasNext()) {
                                            break Label_0339;
                                        }
                                        final Map.Entry<Integer, V> entry = (Map.Entry<Integer, V>)iterator.next();
                                        intValue = entry.getKey();
                                        final TerrainData terrainData = (TerrainData)entry.getValue();
                                        iterator.remove();
                                        final int n2 = 1;
                                        monitorexit(SpatialObjectIndex.this.terrainLock);
                                        if (n2 == 0) {
                                            if (n != 0) {
                                                SpatialObjectIndex.this.drawListUpdateRequested.set(true);
                                            }
                                            return;
                                        }
                                    }
                                    while (true) {
                                        Label_0254: {
                                            final TerrainData terrainData2;
                                            if (intValue < 0 || terrainData2 == null) {
                                                break Label_0254;
                                            }
                                            n = intValue % 16;
                                            intValue /= 16;
                                            final TerrainPatchInfo patchInfo = terrainData2.getPatchInfo(n, intValue);
                                            if (patchInfo == null) {
                                                break Label_0254;
                                            }
                                            synchronized (SpatialObjectIndex.this.terrainLock) {
                                                DrawListTerrainEntry drawListTerrainEntry = SpatialObjectIndex.this.terrain[n][intValue];
                                                if (drawListTerrainEntry == null) {
                                                    final DrawListTerrainEntry[] array = SpatialObjectIndex.this.terrain[n];
                                                    drawListTerrainEntry = new DrawListTerrainEntry(patchInfo, n, intValue);
                                                    array[intValue] = drawListTerrainEntry;
                                                }
                                                else {
                                                    drawListTerrainEntry.updatePatchInfo(patchInfo);
                                                }
                                                monitorexit(SpatialObjectIndex.this.terrainLock);
                                                SpatialObjectIndex.this.spatialTree.updateObject(drawListTerrainEntry);
                                                n = 1;
                                                while (true) {
                                                    break;
                                                    continue;
                                                }
                                            }
                                        }
                                        synchronized (SpatialObjectIndex.this.terrainLock) {
                                            final DrawListTerrainEntry drawListTerrainEntry2 = SpatialObjectIndex.this.terrain[n][intValue];
                                            SpatialObjectIndex.this.terrain[n][intValue] = null;
                                            monitorexit(SpatialObjectIndex.this.terrainLock);
                                            if (drawListTerrainEntry2 != null) {
                                                SpatialObjectIndex.this.spatialTree.removeObject(drawListTerrainEntry2);
                                                continue;
                                            }
                                            continue;
                                        }
                                        break;
                                    }
                                }
                                int intValue = -1;
                                final int n2 = 0;
                                continue;
                            }
                        }
                    }
                }
            }
        };
        this.drawableStore = drawableStore;
        this.avatarCountLimit = avatarCountLimit;
        this.spatialTree = new SpatialTree(16, 256.0f, 256.0f, 4096.0f, this);
        this.objectsInFrustrum = DrawList.create(drawableStore, null, avatarCountLimit);
    }
    
    private DrawList getObjectsInCells(final int n) {
        final DrawList create = DrawList.create(this.drawableStore, this.objectsInFrustrum, n);
        this.spatialTree.addDrawables(create);
        create.initRenderPasses();
        return create;
    }
    
    private boolean handleRemoveObject(final DrawListObjectEntry drawListObjectEntry) {
        this.spatialTree.removeObject(drawListObjectEntry);
        drawListObjectEntry.getObjectInfo().clearDrawListEntry();
        return false;
    }
    
    private boolean handleUpdateObject(final DrawListObjectEntry drawListObjectEntry) {
        drawListObjectEntry.updateBoundingBox();
        this.spatialTree.updateObject(drawListObjectEntry);
        return false;
    }
    
    private void removeObject(final DrawListObjectEntry drawListObjectEntry) {
        synchronized (this.objectUpdateRemoveLock) {
            final boolean remove = this.objectsToUpdate.remove(drawListObjectEntry);
            final boolean add = this.objectsToRemove.add(drawListObjectEntry);
            monitorexit(this.objectUpdateRemoveLock);
            if ((remove | add) && this.initialUpdateCompleted && (this.indexDisabled ^ true)) {
                PrimComputeExecutor.getInstance().execute(this.objectsUpdateTask);
            }
        }
    }
    
    public void completeInitialUpdate() {
        this.initialUpdateCompleted = true;
        if (!this.indexDisabled) {
            PrimComputeExecutor.getInstance().execute(this.objectsUpdateTask);
            PrimComputeExecutor.getInstance().execute(this.terrainUpdate);
            this.drawListUpdateRequested.set(true);
        }
    }
    
    void disableIndex() {
        this.indexDisabled = true;
    }
    
    DrawableAvatar getDrawableAvatar(final SLObjectInfo slObjectInfo) {
        return this.drawableStore.drawableAvatarCache.getIfPresent(slObjectInfo);
    }
    
    public DrawList getObjectsInFrustrum() {
        return this.objectsInFrustrum;
    }
    
    void requestEntryRemoval(final DrawListEntry drawListEntry) {
        if (drawListEntry instanceof DrawListObjectEntry) {
            this.removeObject((DrawListObjectEntry)drawListEntry);
        }
    }
    
    public void setAvatarCountLimit(final int avatarCountLimit) {
        this.avatarCountLimit = avatarCountLimit;
    }
    
    public void setViewport(final FrustrumInfo frustrumInfo, final FrustrumPlanes frustrumPlanes) {
        while (true) {
            int n = 1;
            while (true) {
                Label_0093: {
                    synchronized (this.lock) {
                        if (this.frustrumInfo == null) {
                            this.frustrumInfo = frustrumInfo;
                        }
                        else {
                            if (this.frustrumInfo.equals(frustrumInfo)) {
                                break Label_0093;
                            }
                            this.frustrumInfo = frustrumInfo;
                        }
                        if (n != 0) {
                            this.frustrumPlanes = frustrumPlanes;
                            this.frustrumChanged.set(true);
                            if (this.initialUpdateCompleted && (this.indexDisabled ^ true)) {
                                this.drawListUpdateRequested.set(true);
                            }
                        }
                        return;
                    }
                }
                n = 0;
                continue;
            }
        }
    }
    
    public boolean updateDrawListIfNeeded() {
        if (this.drawListUpdateRequested.getAndSet(false)) {
            PrimComputeExecutor.getInstance().execute(this.drawListUpdateTask);
            return true;
        }
        return false;
    }
    
    public void updateObject(final DrawListObjectEntry drawListObjectEntry) {
        synchronized (this.objectUpdateRemoveLock) {
            boolean b;
            if (!drawListObjectEntry.getObjectInfo().isDead) {
                b = this.objectsToUpdate.add(drawListObjectEntry);
            }
            else {
                b = this.objectsToRemove.add(drawListObjectEntry);
            }
            monitorexit(this.objectUpdateRemoveLock);
            if (b && this.initialUpdateCompleted && (this.indexDisabled ^ true)) {
                PrimComputeExecutor.getInstance().execute(this.objectsUpdateTask);
            }
        }
    }
    
    void updateTerrainPatch(final int n, final int n2, final TerrainData terrainData) {
        synchronized (this.terrainLock) {
            this.terrainDirty.put(n2 * 16 + n, terrainData);
            monitorexit(this.terrainLock);
            if (this.initialUpdateCompleted && (this.indexDisabled ^ true)) {
                PrimComputeExecutor.getInstance().execute(this.terrainUpdate);
            }
        }
    }
    
    private class DrawListUpdateTask implements Runnable, LowPriority
    {
        @Override
        public void run() {
            if (SpatialObjectIndex.this.initialUpdateCompleted && (SpatialObjectIndex.this.indexDisabled ^ true)) {
                if (SpatialObjectIndex.this.frustrumChanged.getAndSet(false) || SpatialObjectIndex.this.spatialTree.isTreeWalkNeeded()) {
                    final FrustrumPlanes -get4 = SpatialObjectIndex.this.frustrumPlanes;
                    final FrustrumInfo -get5 = SpatialObjectIndex.this.frustrumInfo;
                    if (-get4 != null && -get5 != null) {
                        SpatialObjectIndex.this.spatialTree.walkTree(-get4, -get5.viewDistance);
                    }
                }
                if (SpatialObjectIndex.this.spatialTree.isDrawListChanged()) {
                    SpatialObjectIndex.this.objectsInFrustrum = SpatialObjectIndex.this.getObjectsInCells(SpatialObjectIndex.this.avatarCountLimit);
                }
            }
        }
    }
    
    private class ObjectsUpdateTask implements Runnable
    {
        @Override
        public void run() {
            // 
            // This method could not be decompiled.
            // 
            // Original Bytecode:
            // 
            //     1: getfield        com/lumiyaviewer/lumiya/render/spatial/SpatialObjectIndex$ObjectsUpdateTask.this$0:Lcom/lumiyaviewer/lumiya/render/spatial/SpatialObjectIndex;
            //     4: invokestatic    com/lumiyaviewer/lumiya/render/spatial/SpatialObjectIndex.-get6:(Lcom/lumiyaviewer/lumiya/render/spatial/SpatialObjectIndex;)Z
            //     7: ifeq            280
            //    10: aload_0        
            //    11: getfield        com/lumiyaviewer/lumiya/render/spatial/SpatialObjectIndex$ObjectsUpdateTask.this$0:Lcom/lumiyaviewer/lumiya/render/spatial/SpatialObjectIndex;
            //    14: invokestatic    com/lumiyaviewer/lumiya/render/spatial/SpatialObjectIndex.-get5:(Lcom/lumiyaviewer/lumiya/render/spatial/SpatialObjectIndex;)Z
            //    17: iconst_1       
            //    18: ixor           
            //    19: ifeq            280
            //    22: aload_0        
            //    23: getfield        com/lumiyaviewer/lumiya/render/spatial/SpatialObjectIndex$ObjectsUpdateTask.this$0:Lcom/lumiyaviewer/lumiya/render/spatial/SpatialObjectIndex;
            //    26: invokestatic    com/lumiyaviewer/lumiya/render/spatial/SpatialObjectIndex.-get7:(Lcom/lumiyaviewer/lumiya/render/spatial/SpatialObjectIndex;)Ljava/lang/Object;
            //    29: astore_1       
            //    30: aload_1        
            //    31: monitorenter   
            //    32: aload_0        
            //    33: getfield        com/lumiyaviewer/lumiya/render/spatial/SpatialObjectIndex$ObjectsUpdateTask.this$0:Lcom/lumiyaviewer/lumiya/render/spatial/SpatialObjectIndex;
            //    36: invokestatic    com/lumiyaviewer/lumiya/render/spatial/SpatialObjectIndex.-get9:(Lcom/lumiyaviewer/lumiya/render/spatial/SpatialObjectIndex;)Ljava/util/Set;
            //    39: aload_0        
            //    40: getfield        com/lumiyaviewer/lumiya/render/spatial/SpatialObjectIndex$ObjectsUpdateTask.this$0:Lcom/lumiyaviewer/lumiya/render/spatial/SpatialObjectIndex;
            //    43: invokestatic    com/lumiyaviewer/lumiya/render/spatial/SpatialObjectIndex.-get9:(Lcom/lumiyaviewer/lumiya/render/spatial/SpatialObjectIndex;)Ljava/util/Set;
            //    46: invokeinterface java/util/Set.size:()I
            //    51: anewarray       Lcom/lumiyaviewer/lumiya/render/spatial/DrawListObjectEntry;
            //    54: invokeinterface java/util/Set.toArray:([Ljava/lang/Object;)[Ljava/lang/Object;
            //    59: checkcast       [Lcom/lumiyaviewer/lumiya/render/spatial/DrawListObjectEntry;
            //    62: astore_2       
            //    63: aload_0        
            //    64: getfield        com/lumiyaviewer/lumiya/render/spatial/SpatialObjectIndex$ObjectsUpdateTask.this$0:Lcom/lumiyaviewer/lumiya/render/spatial/SpatialObjectIndex;
            //    67: invokestatic    com/lumiyaviewer/lumiya/render/spatial/SpatialObjectIndex.-get8:(Lcom/lumiyaviewer/lumiya/render/spatial/SpatialObjectIndex;)Ljava/util/Set;
            //    70: aload_0        
            //    71: getfield        com/lumiyaviewer/lumiya/render/spatial/SpatialObjectIndex$ObjectsUpdateTask.this$0:Lcom/lumiyaviewer/lumiya/render/spatial/SpatialObjectIndex;
            //    74: invokestatic    com/lumiyaviewer/lumiya/render/spatial/SpatialObjectIndex.-get8:(Lcom/lumiyaviewer/lumiya/render/spatial/SpatialObjectIndex;)Ljava/util/Set;
            //    77: invokeinterface java/util/Set.size:()I
            //    82: anewarray       Lcom/lumiyaviewer/lumiya/render/spatial/DrawListObjectEntry;
            //    85: invokeinterface java/util/Set.toArray:([Ljava/lang/Object;)[Ljava/lang/Object;
            //    90: checkcast       [Lcom/lumiyaviewer/lumiya/render/spatial/DrawListObjectEntry;
            //    93: astore_3       
            //    94: aload_0        
            //    95: getfield        com/lumiyaviewer/lumiya/render/spatial/SpatialObjectIndex$ObjectsUpdateTask.this$0:Lcom/lumiyaviewer/lumiya/render/spatial/SpatialObjectIndex;
            //    98: invokestatic    com/lumiyaviewer/lumiya/render/spatial/SpatialObjectIndex.-get9:(Lcom/lumiyaviewer/lumiya/render/spatial/SpatialObjectIndex;)Ljava/util/Set;
            //   101: invokeinterface java/util/Set.clear:()V
            //   106: aload_0        
            //   107: getfield        com/lumiyaviewer/lumiya/render/spatial/SpatialObjectIndex$ObjectsUpdateTask.this$0:Lcom/lumiyaviewer/lumiya/render/spatial/SpatialObjectIndex;
            //   110: invokestatic    com/lumiyaviewer/lumiya/render/spatial/SpatialObjectIndex.-get8:(Lcom/lumiyaviewer/lumiya/render/spatial/SpatialObjectIndex;)Ljava/util/Set;
            //   113: invokeinterface java/util/Set.clear:()V
            //   118: aload_1        
            //   119: monitorexit    
            //   120: aload_2        
            //   121: arraylength    
            //   122: istore          4
            //   124: iconst_0       
            //   125: istore          5
            //   127: iconst_0       
            //   128: istore          6
            //   130: iload           5
            //   132: iload           4
            //   134: if_icmpge       192
            //   137: aload_2        
            //   138: iload           5
            //   140: aaload         
            //   141: astore_1       
            //   142: aload_1        
            //   143: invokevirtual   com/lumiyaviewer/lumiya/render/spatial/DrawListObjectEntry.getObjectInfo:()Lcom/lumiyaviewer/lumiya/slproto/objects/SLObjectInfo;
            //   146: getfield        com/lumiyaviewer/lumiya/slproto/objects/SLObjectInfo.isDead:Z
            //   149: ifne            176
            //   152: iload           6
            //   154: aload_0        
            //   155: getfield        com/lumiyaviewer/lumiya/render/spatial/SpatialObjectIndex$ObjectsUpdateTask.this$0:Lcom/lumiyaviewer/lumiya/render/spatial/SpatialObjectIndex;
            //   158: aload_1        
            //   159: invokestatic    com/lumiyaviewer/lumiya/render/spatial/SpatialObjectIndex.-wrap1:(Lcom/lumiyaviewer/lumiya/render/spatial/SpatialObjectIndex;Lcom/lumiyaviewer/lumiya/render/spatial/DrawListObjectEntry;)Z
            //   162: ior            
            //   163: istore          6
            //   165: iinc            5, 1
            //   168: goto            130
            //   171: astore_3       
            //   172: aload_1        
            //   173: monitorexit    
            //   174: aload_3        
            //   175: athrow         
            //   176: iload           6
            //   178: aload_0        
            //   179: getfield        com/lumiyaviewer/lumiya/render/spatial/SpatialObjectIndex$ObjectsUpdateTask.this$0:Lcom/lumiyaviewer/lumiya/render/spatial/SpatialObjectIndex;
            //   182: aload_1        
            //   183: invokestatic    com/lumiyaviewer/lumiya/render/spatial/SpatialObjectIndex.-wrap0:(Lcom/lumiyaviewer/lumiya/render/spatial/SpatialObjectIndex;Lcom/lumiyaviewer/lumiya/render/spatial/DrawListObjectEntry;)Z
            //   186: ior            
            //   187: istore          6
            //   189: goto            165
            //   192: aload_3        
            //   193: arraylength    
            //   194: istore          7
            //   196: iconst_0       
            //   197: istore          4
            //   199: iload           6
            //   201: istore          5
            //   203: iload           4
            //   205: istore          6
            //   207: iload           6
            //   209: iload           7
            //   211: if_icmpge       238
            //   214: aload_3        
            //   215: iload           6
            //   217: aaload         
            //   218: astore_2       
            //   219: iload           5
            //   221: aload_0        
            //   222: getfield        com/lumiyaviewer/lumiya/render/spatial/SpatialObjectIndex$ObjectsUpdateTask.this$0:Lcom/lumiyaviewer/lumiya/render/spatial/SpatialObjectIndex;
            //   225: aload_2        
            //   226: invokestatic    com/lumiyaviewer/lumiya/render/spatial/SpatialObjectIndex.-wrap0:(Lcom/lumiyaviewer/lumiya/render/spatial/SpatialObjectIndex;Lcom/lumiyaviewer/lumiya/render/spatial/DrawListObjectEntry;)Z
            //   229: ior            
            //   230: istore          5
            //   232: iinc            6, 1
            //   235: goto            207
            //   238: iload           5
            //   240: ifne            269
            //   243: aload_0        
            //   244: getfield        com/lumiyaviewer/lumiya/render/spatial/SpatialObjectIndex$ObjectsUpdateTask.this$0:Lcom/lumiyaviewer/lumiya/render/spatial/SpatialObjectIndex;
            //   247: invokestatic    com/lumiyaviewer/lumiya/render/spatial/SpatialObjectIndex.-get10:(Lcom/lumiyaviewer/lumiya/render/spatial/SpatialObjectIndex;)Lcom/lumiyaviewer/lumiya/render/spatial/SpatialTree;
            //   250: invokevirtual   com/lumiyaviewer/lumiya/render/spatial/SpatialTree.isDrawListChanged:()Z
            //   253: ifne            269
            //   256: aload_0        
            //   257: getfield        com/lumiyaviewer/lumiya/render/spatial/SpatialObjectIndex$ObjectsUpdateTask.this$0:Lcom/lumiyaviewer/lumiya/render/spatial/SpatialObjectIndex;
            //   260: invokestatic    com/lumiyaviewer/lumiya/render/spatial/SpatialObjectIndex.-get10:(Lcom/lumiyaviewer/lumiya/render/spatial/SpatialObjectIndex;)Lcom/lumiyaviewer/lumiya/render/spatial/SpatialTree;
            //   263: invokevirtual   com/lumiyaviewer/lumiya/render/spatial/SpatialTree.isTreeWalkNeeded:()Z
            //   266: ifeq            280
            //   269: aload_0        
            //   270: getfield        com/lumiyaviewer/lumiya/render/spatial/SpatialObjectIndex$ObjectsUpdateTask.this$0:Lcom/lumiyaviewer/lumiya/render/spatial/SpatialObjectIndex;
            //   273: invokestatic    com/lumiyaviewer/lumiya/render/spatial/SpatialObjectIndex.-get1:(Lcom/lumiyaviewer/lumiya/render/spatial/SpatialObjectIndex;)Ljava/util/concurrent/atomic/AtomicBoolean;
            //   276: iconst_1       
            //   277: invokevirtual   java/util/concurrent/atomic/AtomicBoolean.set:(Z)V
            //   280: return         
            //    Exceptions:
            //  Try           Handler
            //  Start  End    Start  End    Type
            //  -----  -----  -----  -----  ----
            //  32     118    171    176    Any
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
    }
}
