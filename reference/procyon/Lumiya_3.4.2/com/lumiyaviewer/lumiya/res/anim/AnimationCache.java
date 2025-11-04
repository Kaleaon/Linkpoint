// 
// Decompiled by Procyon v0.6.0
// 

package com.lumiyaviewer.lumiya.res.anim;

import java.util.concurrent.Executor;
import com.lumiyaviewer.lumiya.slproto.users.manager.assets.AssetKey;
import com.lumiyaviewer.lumiya.slproto.users.manager.assets.AssetData;
import com.lumiyaviewer.lumiya.react.Subscription;
import com.lumiyaviewer.lumiya.res.executors.LoaderExecutor;
import com.lumiyaviewer.lumiya.res.ResourceRequest;
import com.lumiyaviewer.lumiya.res.ResourceManager;
import android.content.res.AssetManager;
import java.io.IOException;
import com.lumiyaviewer.lumiya.Debug;
import java.util.Arrays;
import com.lumiyaviewer.lumiya.LumiyaApp;
import com.lumiyaviewer.lumiya.slproto.users.manager.assets.AssetResponseCacher;
import java.util.concurrent.atomic.AtomicReference;
import com.google.common.collect.ImmutableSet;
import com.lumiyaviewer.lumiya.render.avatar.AnimationData;
import java.util.UUID;
import com.lumiyaviewer.lumiya.res.ResourceMemoryCache;

public class AnimationCache extends ResourceMemoryCache<UUID, AnimationData>
{
    private final ImmutableSet<String> assetAnimations;
    private final AtomicReference<AssetResponseCacher> assetResponseCacher;
    
    private AnimationCache() {
        this.assetResponseCacher = new AtomicReference<AssetResponseCacher>(null);
        final ImmutableSet.Builder<String> builder = ImmutableSet.builder();
        final AssetManager assetManager = LumiyaApp.getAssetManager();
        while (true) {
            if (assetManager == null) {
                break Label_0048;
            }
            try {
                final String[] list = assetManager.list("anims");
                if (list != null) {
                    builder.addAll(Arrays.asList(list));
                }
                this.assetAnimations = builder.build();
            }
            catch (final IOException ex) {
                Debug.Warning(ex);
                continue;
            }
            break;
        }
    }
    
    public static AnimationCache getInstance() {
        return InstanceHolder.Instance;
    }
    
    @Override
    protected ResourceRequest<UUID, AnimationData> CreateNewRequest(final UUID uuid, final ResourceManager<UUID, AnimationData> resourceManager) {
        final String string = uuid.toString();
        if (this.assetAnimations.contains(string)) {
            return new AssetLoadRequest(uuid, resourceManager, string);
        }
        return new DownloadRequest(uuid, resourceManager);
    }
    
    public void setAssetResponseCacher(final AssetResponseCacher newValue) {
        this.assetResponseCacher.set(newValue);
    }
    
    private static class AssetLoadRequest extends ResourceRequest<UUID, AnimationData> implements Runnable
    {
        private final String assetName;
        
        AssetLoadRequest(final UUID uuid, final ResourceManager<UUID, AnimationData> resourceManager, final String assetName) {
            super(uuid, resourceManager);
            this.assetName = assetName;
        }
        
        @Override
        public void cancelRequest() {
            LoaderExecutor.getInstance().remove(this);
            super.cancelRequest();
        }
        
        @Override
        public void execute() {
            LoaderExecutor.getInstance().execute(this);
        }
        
        @Override
        public void run() {
            // 
            // This method could not be decompiled.
            // 
            // Original Bytecode:
            // 
            //     1: astore_1       
            //     2: invokestatic    com/lumiyaviewer/lumiya/LumiyaApp.getAssetManager:()Landroid/content/res/AssetManager;
            //     5: astore_2       
            //     6: aload_2        
            //     7: ifnull          223
            //    10: new             Ljava/lang/StringBuilder;
            //    13: astore_3       
            //    14: aload_3        
            //    15: invokespecial   java/lang/StringBuilder.<init>:()V
            //    18: aload_2        
            //    19: aload_3        
            //    20: ldc             "anims/"
            //    22: invokevirtual   java/lang/StringBuilder.append:(Ljava/lang/String;)Ljava/lang/StringBuilder;
            //    25: aload_0        
            //    26: getfield        com/lumiyaviewer/lumiya/res/anim/AnimationCache$AssetLoadRequest.assetName:Ljava/lang/String;
            //    29: invokevirtual   java/lang/StringBuilder.append:(Ljava/lang/String;)Ljava/lang/StringBuilder;
            //    32: invokevirtual   java/lang/StringBuilder.toString:()Ljava/lang/String;
            //    35: invokevirtual   android/content/res/AssetManager.open:(Ljava/lang/String;)Ljava/io/InputStream;
            //    38: astore_2       
            //    39: aload_2        
            //    40: ifnull          218
            //    43: new             Lcom/lumiyaviewer/lumiya/render/avatar/AnimationData;
            //    46: astore          4
            //    48: aload           4
            //    50: aload_0        
            //    51: invokevirtual   com/lumiyaviewer/lumiya/res/anim/AnimationCache$AssetLoadRequest.getParams:()Ljava/lang/Object;
            //    54: checkcast       Ljava/util/UUID;
            //    57: aload_2        
            //    58: invokespecial   com/lumiyaviewer/lumiya/render/avatar/AnimationData.<init>:(Ljava/util/UUID;Ljava/io/InputStream;)V
            //    61: aload           4
            //    63: astore_1       
            //    64: aload           4
            //    66: invokevirtual   com/lumiyaviewer/lumiya/render/avatar/AnimationData.getPriority:()I
            //    69: bipush          6
            //    71: if_icmplt       104
            //    74: ldc             "Animation: priority %d loaded from asset %s"
            //    76: iconst_2       
            //    77: anewarray       Ljava/lang/Object;
            //    80: dup            
            //    81: iconst_0       
            //    82: aload           4
            //    84: invokevirtual   com/lumiyaviewer/lumiya/render/avatar/AnimationData.getPriority:()I
            //    87: invokestatic    java/lang/Integer.valueOf:(I)Ljava/lang/Integer;
            //    90: aastore        
            //    91: dup            
            //    92: iconst_1       
            //    93: aload_0        
            //    94: getfield        com/lumiyaviewer/lumiya/res/anim/AnimationCache$AssetLoadRequest.assetName:Ljava/lang/String;
            //    97: aastore        
            //    98: invokestatic    com/lumiyaviewer/lumiya/Debug.Printf:(Ljava/lang/String;[Ljava/lang/Object;)V
            //   101: aload           4
            //   103: astore_1       
            //   104: aload_1        
            //   105: astore_3       
            //   106: aload_2        
            //   107: ifnull          116
            //   110: aload_2        
            //   111: invokevirtual   java/io/InputStream.close:()V
            //   114: aload_1        
            //   115: astore_3       
            //   116: aload_0        
            //   117: aload_3        
            //   118: invokevirtual   com/lumiyaviewer/lumiya/res/anim/AnimationCache$AssetLoadRequest.completeRequest:(Ljava/lang/Object;)V
            //   121: return         
            //   122: astore_2       
            //   123: aload_2        
            //   124: invokestatic    com/lumiyaviewer/lumiya/Debug.Warning:(Ljava/lang/Throwable;)V
            //   127: aload_1        
            //   128: astore_3       
            //   129: goto            116
            //   132: astore_3       
            //   133: aconst_null    
            //   134: astore_2       
            //   135: aload_3        
            //   136: invokestatic    com/lumiyaviewer/lumiya/Debug.Warning:(Ljava/lang/Throwable;)V
            //   139: aload_2        
            //   140: astore_3       
            //   141: aload_1        
            //   142: ifnull          116
            //   145: aload_1        
            //   146: invokevirtual   java/io/InputStream.close:()V
            //   149: aload_2        
            //   150: astore_3       
            //   151: goto            116
            //   154: astore_1       
            //   155: aload_1        
            //   156: invokestatic    com/lumiyaviewer/lumiya/Debug.Warning:(Ljava/lang/Throwable;)V
            //   159: aload_2        
            //   160: astore_3       
            //   161: goto            116
            //   164: astore_1       
            //   165: aconst_null    
            //   166: astore_2       
            //   167: aload_2        
            //   168: ifnull          175
            //   171: aload_2        
            //   172: invokevirtual   java/io/InputStream.close:()V
            //   175: aload_1        
            //   176: athrow         
            //   177: astore_2       
            //   178: aload_2        
            //   179: invokestatic    com/lumiyaviewer/lumiya/Debug.Warning:(Ljava/lang/Throwable;)V
            //   182: goto            175
            //   185: astore_1       
            //   186: goto            167
            //   189: astore_3       
            //   190: aload_1        
            //   191: astore_2       
            //   192: aload_3        
            //   193: astore_1       
            //   194: goto            167
            //   197: astore_3       
            //   198: aconst_null    
            //   199: astore          4
            //   201: aload_2        
            //   202: astore_1       
            //   203: aload           4
            //   205: astore_2       
            //   206: goto            135
            //   209: astore_3       
            //   210: aload_2        
            //   211: astore_1       
            //   212: aload           4
            //   214: astore_2       
            //   215: goto            135
            //   218: aconst_null    
            //   219: astore_1       
            //   220: goto            104
            //   223: aconst_null    
            //   224: astore_3       
            //   225: goto            116
            //    Exceptions:
            //  Try           Handler
            //  Start  End    Start  End    Type                 
            //  -----  -----  -----  -----  ---------------------
            //  10     39     132    135    Ljava/io/IOException;
            //  10     39     164    167    Any
            //  43     61     197    209    Ljava/io/IOException;
            //  43     61     185    189    Any
            //  64     101    209    218    Ljava/io/IOException;
            //  64     101    185    189    Any
            //  110    114    122    132    Ljava/io/IOException;
            //  135    139    189    197    Any
            //  145    149    154    164    Ljava/io/IOException;
            //  171    175    177    185    Ljava/io/IOException;
            // 
            // The error that occurred was:
            // 
            // java.lang.UnsupportedOperationException
            //     at java.base/java.util.Collections$1.remove(Collections.java:5041)
            //     at java.base/java.util.AbstractCollection.removeAll(AbstractCollection.java:371)
            //     at com.strobel.decompiler.ast.AstBuilder.convertLocalVariables(AstBuilder.java:3018)
            //     at com.strobel.decompiler.ast.AstBuilder.performStackAnalysis(AstBuilder.java:2501)
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
    
    private class DownloadRequest extends ResourceRequest<UUID, AnimationData> implements OnData<AssetData>, OnError
    {
        private Subscription<AssetKey, AssetData> assetSubscription;
        
        DownloadRequest(final UUID uuid, final ResourceManager<UUID, AnimationData> resourceManager) {
            super(uuid, resourceManager);
        }
        
        @Override
        public void cancelRequest() {
            final Subscription<AssetKey, AssetData> assetSubscription = this.assetSubscription;
            if (assetSubscription != null) {
                assetSubscription.unsubscribe();
            }
            super.cancelRequest();
        }
        
        @Override
        public void completeRequest(final AnimationData animationData) {
            final Subscription<AssetKey, AssetData> assetSubscription = this.assetSubscription;
            if (assetSubscription != null) {
                assetSubscription.unsubscribe();
            }
            super.completeRequest(animationData);
        }
        
        @Override
        public void execute() {
            final AssetResponseCacher assetResponseCacher = AnimationCache.this.assetResponseCacher.get();
            if (assetResponseCacher != null) {
                this.assetSubscription = assetResponseCacher.getPool().subscribe(AssetKey.createAssetKey(null, null, ((ResourceRequest<UUID, ResourceType>)this).getParams(), 20), LoaderExecutor.getInstance(), this, this);
            }
            else {
                this.completeRequest(null);
            }
        }
        
        public void onData(final AssetData p0) {
            // 
            // This method could not be decompiled.
            // 
            // Original Bytecode:
            // 
            //     1: ifnull          67
            //     4: aload_1        
            //     5: invokevirtual   com/lumiyaviewer/lumiya/slproto/users/manager/assets/AssetData.getData:()[B
            //     8: ifnull          67
            //    11: aload_1        
            //    12: invokevirtual   com/lumiyaviewer/lumiya/slproto/users/manager/assets/AssetData.getStatus:()I
            //    15: iconst_1       
            //    16: if_icmpne       67
            //    19: new             Ljava/io/ByteArrayInputStream;
            //    22: dup            
            //    23: aload_1        
            //    24: invokevirtual   com/lumiyaviewer/lumiya/slproto/users/manager/assets/AssetData.getData:()[B
            //    27: invokespecial   java/io/ByteArrayInputStream.<init>:([B)V
            //    30: astore_2       
            //    31: new             Lcom/lumiyaviewer/lumiya/render/avatar/AnimationData;
            //    34: astore_1       
            //    35: aload_1        
            //    36: aload_0        
            //    37: invokevirtual   com/lumiyaviewer/lumiya/res/anim/AnimationCache$DownloadRequest.getParams:()Ljava/lang/Object;
            //    40: checkcast       Ljava/util/UUID;
            //    43: aload_2        
            //    44: invokespecial   com/lumiyaviewer/lumiya/render/avatar/AnimationData.<init>:(Ljava/util/UUID;Ljava/io/InputStream;)V
            //    47: aload_2        
            //    48: invokevirtual   java/io/ByteArrayInputStream.close:()V
            //    51: aload_0        
            //    52: aload_1        
            //    53: invokevirtual   com/lumiyaviewer/lumiya/res/anim/AnimationCache$DownloadRequest.completeRequest:(Lcom/lumiyaviewer/lumiya/render/avatar/AnimationData;)V
            //    56: return         
            //    57: astore_2       
            //    58: aconst_null    
            //    59: astore_1       
            //    60: aload_2        
            //    61: invokestatic    com/lumiyaviewer/lumiya/Debug.Warning:(Ljava/lang/Throwable;)V
            //    64: goto            51
            //    67: aload_0        
            //    68: aconst_null    
            //    69: invokevirtual   com/lumiyaviewer/lumiya/res/anim/AnimationCache$DownloadRequest.completeRequest:(Lcom/lumiyaviewer/lumiya/render/avatar/AnimationData;)V
            //    72: goto            56
            //    75: astore_2       
            //    76: goto            60
            //    Exceptions:
            //  Try           Handler
            //  Start  End    Start  End    Type                 
            //  -----  -----  -----  -----  ---------------------
            //  31     47     57     60     Ljava/io/IOException;
            //  47     51     75     79     Ljava/io/IOException;
            // 
            // The error that occurred was:
            // 
            // java.lang.IllegalStateException: Expression is linked from several locations: Label_0051:
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
        public void onError(final Throwable t) {
            this.completeRequest(null);
        }
    }
    
    private static class InstanceHolder
    {
        private static final AnimationCache Instance;
        
        static {
            Instance = new AnimationCache(null);
        }
    }
}
