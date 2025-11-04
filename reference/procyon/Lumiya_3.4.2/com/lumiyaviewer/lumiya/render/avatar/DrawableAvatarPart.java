// 
// Decompiled by Procyon v0.6.0
// 

package com.lumiyaviewer.lumiya.render.avatar;

import com.lumiyaviewer.lumiya.res.ResourceMemoryCache;
import com.lumiyaviewer.lumiya.res.textures.TextureCache;
import com.lumiyaviewer.lumiya.render.tex.DrawableTextureParams;
import com.lumiyaviewer.lumiya.utils.UUIDPool;
import com.lumiyaviewer.lumiya.render.glres.textures.GLTextureCache;
import java.util.Arrays;
import com.lumiyaviewer.lumiya.Debug;
import com.lumiyaviewer.lumiya.render.RenderContext;
import com.lumiyaviewer.lumiya.res.executors.PrimComputeExecutor;
import com.lumiyaviewer.lumiya.render.drawable.DrawableFaceTexture;
import com.lumiyaviewer.lumiya.slproto.avatar.SLPolyMesh;
import com.lumiyaviewer.lumiya.openjpeg.OpenJPEG;
import com.lumiyaviewer.lumiya.slproto.avatar.SLAnimatedMeshData;
import com.lumiyaviewer.lumiya.slproto.avatar.AvatarTextureFaceIndex;
import java.util.UUID;
import com.lumiyaviewer.lumiya.res.ResourceConsumer;

public class DrawableAvatarPart implements ResourceConsumer
{
    public static final UUID DEFAULT_AVATAR_TEXTURE;
    private final UUID avatarUUID;
    private final AvatarTextureFaceIndex faceIndex;
    private final boolean hasGL20;
    private volatile SLAnimatedMeshData meshData;
    private volatile boolean meshDataUpdated;
    private final Runnable meshUpdate;
    private volatile float[] partMorphParams;
    private volatile OpenJPEG rawTexture;
    private final SLPolyMesh referenceMeshData;
    private volatile DrawableFaceTexture texture;
    private volatile UUID textureUUID;
    private final Object updateLock;
    
    static {
        DEFAULT_AVATAR_TEXTURE = UUID.fromString("c228d1cf-4b5d-4ba8-84f4-899a0796aa97");
    }
    
    DrawableAvatarPart(final UUID avatarUUID, final AvatarTextureFaceIndex faceIndex, final SLPolyMesh referenceMeshData, final boolean hasGL20) {
        this.updateLock = new Object();
        this.meshUpdate = new Runnable() {
            @Override
            public void run() {
                // 
                // This method could not be decompiled.
                // 
                // Original Bytecode:
                // 
                //     2: iconst_1       
                //     3: anewarray       Ljava/lang/Object;
                //     6: dup            
                //     7: iconst_0       
                //     8: aload_0        
                //     9: getfield        com/lumiyaviewer/lumiya/render/avatar/DrawableAvatarPart$1.this$0:Lcom/lumiyaviewer/lumiya/render/avatar/DrawableAvatarPart;
                //    12: invokestatic    com/lumiyaviewer/lumiya/render/avatar/DrawableAvatarPart.-get0:(Lcom/lumiyaviewer/lumiya/render/avatar/DrawableAvatarPart;)Lcom/lumiyaviewer/lumiya/slproto/avatar/AvatarTextureFaceIndex;
                //    15: invokevirtual   com/lumiyaviewer/lumiya/slproto/avatar/AvatarTextureFaceIndex.toString:()Ljava/lang/String;
                //    18: aastore        
                //    19: invokestatic    com/lumiyaviewer/lumiya/Debug.Printf:(Ljava/lang/String;[Ljava/lang/Object;)V
                //    22: aload_0        
                //    23: getfield        com/lumiyaviewer/lumiya/render/avatar/DrawableAvatarPart$1.this$0:Lcom/lumiyaviewer/lumiya/render/avatar/DrawableAvatarPart;
                //    26: invokestatic    com/lumiyaviewer/lumiya/render/avatar/DrawableAvatarPart.-get5:(Lcom/lumiyaviewer/lumiya/render/avatar/DrawableAvatarPart;)Ljava/lang/Object;
                //    29: astore_1       
                //    30: aload_1        
                //    31: monitorenter   
                //    32: aload_0        
                //    33: getfield        com/lumiyaviewer/lumiya/render/avatar/DrawableAvatarPart$1.this$0:Lcom/lumiyaviewer/lumiya/render/avatar/DrawableAvatarPart;
                //    36: invokestatic    com/lumiyaviewer/lumiya/render/avatar/DrawableAvatarPart.-get3:(Lcom/lumiyaviewer/lumiya/render/avatar/DrawableAvatarPart;)Lcom/lumiyaviewer/lumiya/openjpeg/OpenJPEG;
                //    39: astore_2       
                //    40: aload_0        
                //    41: getfield        com/lumiyaviewer/lumiya/render/avatar/DrawableAvatarPart$1.this$0:Lcom/lumiyaviewer/lumiya/render/avatar/DrawableAvatarPart;
                //    44: invokestatic    com/lumiyaviewer/lumiya/render/avatar/DrawableAvatarPart.-get2:(Lcom/lumiyaviewer/lumiya/render/avatar/DrawableAvatarPart;)[F
                //    47: astore_3       
                //    48: aload_1        
                //    49: monitorexit    
                //    50: aload_3        
                //    51: ifnull          152
                //    54: aload_2        
                //    55: ifnull          152
                //    58: ldc             "Avatar: meshUpdate: part %s params %s"
                //    60: iconst_2       
                //    61: anewarray       Ljava/lang/Object;
                //    64: dup            
                //    65: iconst_0       
                //    66: aload_0        
                //    67: getfield        com/lumiyaviewer/lumiya/render/avatar/DrawableAvatarPart$1.this$0:Lcom/lumiyaviewer/lumiya/render/avatar/DrawableAvatarPart;
                //    70: invokestatic    com/lumiyaviewer/lumiya/render/avatar/DrawableAvatarPart.-get0:(Lcom/lumiyaviewer/lumiya/render/avatar/DrawableAvatarPart;)Lcom/lumiyaviewer/lumiya/slproto/avatar/AvatarTextureFaceIndex;
                //    73: invokevirtual   com/lumiyaviewer/lumiya/slproto/avatar/AvatarTextureFaceIndex.toString:()Ljava/lang/String;
                //    76: aastore        
                //    77: dup            
                //    78: iconst_1       
                //    79: aload_3        
                //    80: invokestatic    java/util/Arrays.toString:([F)Ljava/lang/String;
                //    83: aastore        
                //    84: invokestatic    com/lumiyaviewer/lumiya/Debug.Printf:(Ljava/lang/String;[Ljava/lang/Object;)V
                //    87: new             Lcom/lumiyaviewer/lumiya/slproto/avatar/SLAnimatedMeshData;
                //    90: dup            
                //    91: aload_0        
                //    92: getfield        com/lumiyaviewer/lumiya/render/avatar/DrawableAvatarPart$1.this$0:Lcom/lumiyaviewer/lumiya/render/avatar/DrawableAvatarPart;
                //    95: invokestatic    com/lumiyaviewer/lumiya/render/avatar/DrawableAvatarPart.-get4:(Lcom/lumiyaviewer/lumiya/render/avatar/DrawableAvatarPart;)Lcom/lumiyaviewer/lumiya/slproto/avatar/SLPolyMesh;
                //    98: aload_0        
                //    99: getfield        com/lumiyaviewer/lumiya/render/avatar/DrawableAvatarPart$1.this$0:Lcom/lumiyaviewer/lumiya/render/avatar/DrawableAvatarPart;
                //   102: invokestatic    com/lumiyaviewer/lumiya/render/avatar/DrawableAvatarPart.-get1:(Lcom/lumiyaviewer/lumiya/render/avatar/DrawableAvatarPart;)Z
                //   105: invokespecial   com/lumiyaviewer/lumiya/slproto/avatar/SLAnimatedMeshData.<init>:(Lcom/lumiyaviewer/lumiya/slproto/avatar/SLPolyMesh;Z)V
                //   108: astore_1       
                //   109: aload_0        
                //   110: getfield        com/lumiyaviewer/lumiya/render/avatar/DrawableAvatarPart$1.this$0:Lcom/lumiyaviewer/lumiya/render/avatar/DrawableAvatarPart;
                //   113: invokestatic    com/lumiyaviewer/lumiya/render/avatar/DrawableAvatarPart.-get4:(Lcom/lumiyaviewer/lumiya/render/avatar/DrawableAvatarPart;)Lcom/lumiyaviewer/lumiya/slproto/avatar/SLPolyMesh;
                //   116: aload_1        
                //   117: aload_3        
                //   118: aload_2        
                //   119: invokevirtual   com/lumiyaviewer/lumiya/slproto/avatar/SLPolyMesh.applyMorphData:(Lcom/lumiyaviewer/lumiya/slproto/avatar/SLMeshData;[FLcom/lumiyaviewer/lumiya/render/GLTexture;)V
                //   122: aload_0        
                //   123: getfield        com/lumiyaviewer/lumiya/render/avatar/DrawableAvatarPart$1.this$0:Lcom/lumiyaviewer/lumiya/render/avatar/DrawableAvatarPart;
                //   126: invokestatic    com/lumiyaviewer/lumiya/render/avatar/DrawableAvatarPart.-get5:(Lcom/lumiyaviewer/lumiya/render/avatar/DrawableAvatarPart;)Ljava/lang/Object;
                //   129: astore_3       
                //   130: aload_3        
                //   131: monitorenter   
                //   132: aload_0        
                //   133: getfield        com/lumiyaviewer/lumiya/render/avatar/DrawableAvatarPart$1.this$0:Lcom/lumiyaviewer/lumiya/render/avatar/DrawableAvatarPart;
                //   136: aload_1        
                //   137: invokestatic    com/lumiyaviewer/lumiya/render/avatar/DrawableAvatarPart.-set0:(Lcom/lumiyaviewer/lumiya/render/avatar/DrawableAvatarPart;Lcom/lumiyaviewer/lumiya/slproto/avatar/SLAnimatedMeshData;)Lcom/lumiyaviewer/lumiya/slproto/avatar/SLAnimatedMeshData;
                //   140: pop            
                //   141: aload_0        
                //   142: getfield        com/lumiyaviewer/lumiya/render/avatar/DrawableAvatarPart$1.this$0:Lcom/lumiyaviewer/lumiya/render/avatar/DrawableAvatarPart;
                //   145: iconst_1       
                //   146: invokestatic    com/lumiyaviewer/lumiya/render/avatar/DrawableAvatarPart.-set1:(Lcom/lumiyaviewer/lumiya/render/avatar/DrawableAvatarPart;Z)Z
                //   149: pop            
                //   150: aload_3        
                //   151: monitorexit    
                //   152: return         
                //   153: astore_3       
                //   154: aload_1        
                //   155: monitorexit    
                //   156: aload_3        
                //   157: athrow         
                //   158: astore_1       
                //   159: aload_3        
                //   160: monitorexit    
                //   161: aload_1        
                //   162: athrow         
                //    Exceptions:
                //  Try           Handler
                //  Start  End    Start  End    Type
                //  -----  -----  -----  -----  ----
                //  32     48     153    158    Any
                //  132    150    158    163    Any
                // 
                // The error that occurred was:
                // 
                // java.lang.IllegalStateException: Expression is linked from several locations: Label_0152:
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
        this.avatarUUID = avatarUUID;
        this.faceIndex = faceIndex;
        this.referenceMeshData = referenceMeshData;
        this.hasGL20 = hasGL20;
    }
    
    private void RequestMeshUpdate() {
        PrimComputeExecutor.getInstance().execute(this.meshUpdate);
    }
    
    public final void GLDraw(final RenderContext renderContext, final float[] array, boolean b) {
        Label_0098: {
            if (renderContext.hasGL20) {
                break Label_0098;
            }
            while (true) {
                synchronized (this.updateLock) {
                    final SLAnimatedMeshData meshData = this.meshData;
                    final DrawableFaceTexture texture = this.texture;
                    int n = b ? 1 : 0;
                    if (meshData != null) {
                        n = (b ? 1 : 0);
                        if (this.meshDataUpdated) {
                            n = (b ? 1 : 0);
                            if (array != null) {
                                this.meshDataUpdated = false;
                                n = 1;
                            }
                        }
                    }
                    monitorexit(this.updateLock);
                    if (meshData != null) {
                        if (n != 0) {
                            this.referenceMeshData.applySkeleton(meshData, array);
                            meshData.setVerticesDirty();
                        }
                        meshData.GLDraw(renderContext, texture);
                    }
                    return;
                    b = false;
                }
            }
        }
    }
    
    @Override
    public void OnResourceReady(final Object o, final boolean b) {
        final String string = this.faceIndex.toString();
        Label_0068: {
            if (o == null) {
                break Label_0068;
            }
            String string2 = o.toString();
            while (true) {
                Debug.Printf("Avatar: (requesting meshUpdate) face %s texture %s", string, string2);
                if (!(o instanceof OpenJPEG)) {
                    return;
                }
                synchronized (this.updateLock) {
                    this.rawTexture = (OpenJPEG)o;
                    monitorexit(this.updateLock);
                    this.RequestMeshUpdate();
                    return;
                    string2 = "null";
                    continue;
                }
                break;
            }
        }
    }
    
    public AvatarTextureFaceIndex getFaceIndex() {
        return this.faceIndex;
    }
    
    void setPartMorphParams(final float[] array) {
        synchronized (this.updateLock) {
            final boolean b = Arrays.equals(this.partMorphParams, array) ^ true;
            if (b) {
                this.partMorphParams = array;
            }
            monitorexit(this.updateLock);
            if (b) {
                Debug.Printf("Avatar: (requesting meshUpdate) new morphParams for part %s", this.faceIndex.toString());
                this.RequestMeshUpdate();
            }
        }
    }
    
    void setTexture(final GLTextureCache glTextureCache, UUID textureUUID) {
        synchronized (this.updateLock) {
            final String string = this.faceIndex.toString();
            String string2;
            if (textureUUID != null) {
                string2 = textureUUID.toString();
            }
            else {
                string2 = "null";
            }
            Debug.Printf("Avatar: face %s texture %s", string, string2);
            UUID uuid = textureUUID;
            if (textureUUID != null) {
                if (textureUUID.equals(UUIDPool.ZeroUUID)) {
                    uuid = null;
                }
                else {
                    final boolean equals = textureUUID.equals(DrawableAvatarPart.DEFAULT_AVATAR_TEXTURE);
                    uuid = textureUUID;
                    if (equals) {
                        uuid = null;
                    }
                }
            }
            textureUUID = this.textureUUID;
            if (textureUUID != null) {
                if (uuid != null && textureUUID.equals(uuid)) {
                    return;
                }
            }
            else if (uuid == null) {
                return;
            }
            if ((this.textureUUID = uuid) != null) {
                final DrawableTextureParams create = DrawableTextureParams.create(uuid, this.faceIndex, this.avatarUUID);
                ((ResourceMemoryCache<DrawableTextureParams, ResourceType>)TextureCache.getInstance()).RequestResource(create, this);
                this.texture = new DrawableFaceTexture(create);
            }
            else {
                this.texture = null;
                this.meshData = null;
                this.rawTexture = null;
            }
        }
    }
}
