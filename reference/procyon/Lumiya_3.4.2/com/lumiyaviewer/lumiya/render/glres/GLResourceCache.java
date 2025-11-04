// 
// Decompiled by Procyon v0.6.0
// 

package com.lumiyaviewer.lumiya.render.glres;

import com.lumiyaviewer.lumiya.Debug;
import com.lumiyaviewer.lumiya.render.RenderContext;
import com.lumiyaviewer.lumiya.res.ResourceRequest;
import com.lumiyaviewer.lumiya.res.ResourceManager;
import com.lumiyaviewer.lumiya.res.ResourceConsumer;
import com.lumiyaviewer.lumiya.res.ResourceMemoryCache;

public abstract class GLResourceCache<ResourceParams, RawType, ResourceType extends GLSizedResource> extends ResourceMemoryCache<ResourceParams, ResourceType>
{
    private final GLLoadQueue loadQueue;
    
    protected GLResourceCache(final GLLoadQueue loadQueue) {
        this.loadQueue = loadQueue;
    }
    
    protected abstract void CancelRawResource(final ResourceConsumer p0);
    
    @Override
    protected ResourceRequest<ResourceParams, ResourceType> CreateNewRequest(final ResourceParams resourceParams, final ResourceManager<ResourceParams, ResourceType> resourceManager) {
        return new LoadRequest<Object>(resourceParams, resourceManager);
    }
    
    protected abstract int GetResourceSize(final RawType p0);
    
    protected abstract ResourceType LoadResource(final ResourceParams p0, final RawType p1, final RenderContext p2);
    
    protected abstract void RequestRawResource(final ResourceParams p0, final ResourceConsumer p1);
    
    private class LoadRequest<Raw extends RawType> extends ResourceRequest<ResourceParams, ResourceType> implements GLLoadable, ResourceConsumer
    {
        private volatile boolean finalResult;
        private volatile boolean loadedFinal;
        private volatile ResourceType loadedResource;
        private volatile Raw rawResource;
        
        public LoadRequest(final ResourceParams resourceParams, final ResourceManager<ResourceParams, ResourceType> resourceManager) {
            super(resourceParams, resourceManager);
        }
        
        @Override
        public void GLCompleteLoad() {
            while (true) {
                synchronized (this) {
                    final GLSizedResource loadedResource = this.loadedResource;
                    final boolean loadedFinal = this.loadedFinal;
                    monitorexit(this);
                    if (loadedFinal) {
                        this.completeRequest((ResourceType)loadedResource);
                        return;
                    }
                }
                final ResourceType resourceType;
                this.intermediateResult(resourceType);
            }
        }
        
        @Override
        public int GLGetLoadSize() {
            synchronized (this) {
                final RawType rawResource = this.rawResource;
                monitorexit(this);
                if (rawResource != null) {
                    return GLResourceCache.this.GetResourceSize(rawResource);
                }
            }
            return 0;
        }
        
        @Override
        public int GLLoad(final RenderContext p0, final GLLoadHandler p1) {
            // 
            // This method could not be decompiled.
            // 
            // Original Bytecode:
            // 
            //     1: monitorenter   
            //     2: aload_0        
            //     3: getfield        com/lumiyaviewer/lumiya/render/glres/GLResourceCache$LoadRequest.rawResource:Ljava/lang/Object;
            //     6: astore_3       
            //     7: aload_0        
            //     8: getfield        com/lumiyaviewer/lumiya/render/glres/GLResourceCache$LoadRequest.finalResult:Z
            //    11: istore          4
            //    13: aload_0        
            //    14: monitorexit    
            //    15: aload_0        
            //    16: getfield        com/lumiyaviewer/lumiya/render/glres/GLResourceCache$LoadRequest.this$0:Lcom/lumiyaviewer/lumiya/render/glres/GLResourceCache;
            //    19: aload_0        
            //    20: invokevirtual   com/lumiyaviewer/lumiya/render/glres/GLResourceCache$LoadRequest.getParams:()Ljava/lang/Object;
            //    23: aload_3        
            //    24: aload_1        
            //    25: invokevirtual   com/lumiyaviewer/lumiya/render/glres/GLResourceCache.LoadResource:(Ljava/lang/Object;Ljava/lang/Object;Lcom/lumiyaviewer/lumiya/render/RenderContext;)Lcom/lumiyaviewer/lumiya/render/glres/GLSizedResource;
            //    28: astore_1       
            //    29: aload_1        
            //    30: ifnull          73
            //    33: aload_1        
            //    34: invokevirtual   com/lumiyaviewer/lumiya/render/glres/GLSizedResource.getLoadedSize:()I
            //    37: istore          5
            //    39: aload_0        
            //    40: monitorenter   
            //    41: aload_0        
            //    42: aload_1        
            //    43: putfield        com/lumiyaviewer/lumiya/render/glres/GLResourceCache$LoadRequest.loadedResource:Lcom/lumiyaviewer/lumiya/render/glres/GLSizedResource;
            //    46: aload_0        
            //    47: iload           4
            //    49: putfield        com/lumiyaviewer/lumiya/render/glres/GLResourceCache$LoadRequest.loadedFinal:Z
            //    52: aload_0        
            //    53: monitorexit    
            //    54: aload_1        
            //    55: ifnull          65
            //    58: aload_2        
            //    59: aload_0        
            //    60: invokeinterface com/lumiyaviewer/lumiya/render/glres/GLLoadQueue$GLLoadHandler.GLResourceLoaded:(Lcom/lumiyaviewer/lumiya/render/glres/GLLoadQueue$GLLoadable;)V
            //    65: iload           5
            //    67: ireturn        
            //    68: astore_1       
            //    69: aload_0        
            //    70: monitorexit    
            //    71: aload_1        
            //    72: athrow         
            //    73: iconst_0       
            //    74: istore          5
            //    76: goto            39
            //    79: astore_1       
            //    80: aload_0        
            //    81: monitorexit    
            //    82: aload_1        
            //    83: athrow         
            //    Exceptions:
            //  Try           Handler
            //  Start  End    Start  End    Type
            //  -----  -----  -----  -----  ----
            //  2      13     68     73     Any
            //  41     52     79     84     Any
            // 
            // The error that occurred was:
            // 
            // java.lang.IllegalStateException: Expression is linked from several locations: Label_0065:
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
        
        @Override
        public void OnResourceReady(final Object rawResource, final boolean b) {
            while (true) {
                Label_0057: {
                    if (rawResource == null) {
                        break Label_0057;
                    }
                    try {
                        synchronized (this) {
                            this.rawResource = (Raw)rawResource;
                            this.finalResult = (b ^ true);
                            monitorexit(this);
                            GLResourceCache.this.loadQueue.add((GLLoadQueue.GLLoadable)this);
                            ResourceManager.this.collectReferences();
                            return;
                        }
                    }
                    catch (final ClassCastException ex) {
                        Debug.Warning(ex);
                        this.completeRequest(null);
                        continue;
                    }
                }
                this.completeRequest(null);
                continue;
            }
        }
        
        @Override
        public void cancelRequest() {
            GLResourceCache.this.loadQueue.remove((GLLoadQueue.GLLoadable)this);
            GLResourceCache.this.CancelRawResource(this);
            super.cancelRequest();
        }
        
        @Override
        public void execute() {
            GLResourceCache.this.RequestRawResource(this.getParams(), this);
        }
    }
}
