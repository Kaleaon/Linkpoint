// 
// Decompiled by Procyon v0.6.0
// 

package com.lumiyaviewer.lumiya.slproto.baker;

import com.lumiyaviewer.lumiya.res.ResourceMemoryCache;
import com.lumiyaviewer.lumiya.render.tex.DrawableTextureParams;
import com.lumiyaviewer.lumiya.render.tex.TextureClass;
import com.lumiyaviewer.lumiya.res.textures.TextureCache;
import com.lumiyaviewer.lumiya.res.ResourceConsumer;
import java.io.File;
import java.util.LinkedList;
import com.lumiyaviewer.lumiya.render.avatar.DrawableAvatarPart;
import com.lumiyaviewer.lumiya.openjpeg.OpenJPEG;
import com.lumiyaviewer.lumiya.slproto.avatar.AvatarTextureFaceIndex;
import com.lumiyaviewer.lumiya.slproto.events.SLBakingProgressEvent;
import com.lumiyaviewer.lumiya.render.avatar.AvatarSkeleton;
import com.lumiyaviewer.lumiya.slproto.avatar.SLAvatarParams;
import java.util.HashMap;
import com.lumiyaviewer.lumiya.slproto.textures.SLTextureEntryFace;
import com.lumiyaviewer.lumiya.slproto.textures.MutableSLTextureEntryFace;
import com.lumiyaviewer.lumiya.slproto.textures.SLTextureEntry;
import java.util.Iterator;
import com.lumiyaviewer.lumiya.slproto.assets.SLWearableData;
import java.util.ArrayList;
import com.lumiyaviewer.lumiya.Debug;
import java.util.EnumMap;
import java.util.IdentityHashMap;
import java.util.UUID;
import com.lumiyaviewer.lumiya.slproto.assets.SLWearableType;
import com.google.common.collect.Table;
import java.util.List;
import com.lumiyaviewer.lumiya.slproto.assets.SLWearable;
import com.lumiyaviewer.lumiya.slproto.modules.texuploader.SLTextureUploader;
import com.lumiyaviewer.lumiya.eventbus.EventBus;
import com.lumiyaviewer.lumiya.slproto.avatar.BakedTextureIndex;
import java.util.Map;
import com.lumiyaviewer.lumiya.slproto.modules.SLAvatarAppearance;
import com.lumiyaviewer.lumiya.slproto.modules.texuploader.SLTextureUploadRequest;

public class BakeProcess implements TextureUploadCompleteListener
{
    private final SLAvatarAppearance avatarAppearance;
    private final Map<BakedTextureIndex, BakedImage> bakedImages;
    private final Thread bakingThread;
    private final EventBus eventBus;
    private Map<Integer, Float> paramValues;
    private final Object textureReadyLock;
    private final SLTextureUploader uploader;
    private final Map<SLWearable, List<WearableTextureData>> wearables;
    private final Table<SLWearableType, UUID, SLWearable> wornWearables;
    
    public BakeProcess(final Table<SLWearableType, UUID, SLWearable> wornWearables, final SLAvatarAppearance avatarAppearance, final SLTextureUploader uploader, final EventBus eventBus) {
        this.wearables = new IdentityHashMap<SLWearable, List<WearableTextureData>>();
        this.textureReadyLock = new Object();
        this.bakedImages = new EnumMap<BakedTextureIndex, BakedImage>(BakedTextureIndex.class);
        Debug.Printf("Baking: new BakeProcess created", new Object[0]);
        this.avatarAppearance = avatarAppearance;
        this.wornWearables = wornWearables;
        this.uploader = uploader;
        this.eventBus = eventBus;
        for (final SLWearable slWearable : wornWearables.values()) {
            final SLWearableData wearableData = slWearable.getWearableData();
            if (wearableData != null) {
                final ArrayList list = new ArrayList(wearableData.textures.size());
                final Iterator<Object> iterator2 = wearableData.textures.iterator();
                while (iterator2.hasNext()) {
                    list.add((Object)new WearableTextureData(iterator2.next()));
                }
                this.wearables.put(slWearable, (ArrayList)list);
            }
        }
        (this.bakingThread = new Thread(new _$Lambda$qb61PwDoxRPFEOdyYwns3UfUTbM(this), "Baker")).start();
    }
    
    private SLTextureEntry PrepareAvatarTextureEntry() {
        final SLTextureEntryFace create = SLTextureEntryFace.create(new MutableSLTextureEntryFace(-1));
        final SLTextureEntryFace[] array = new SLTextureEntryFace[32];
        for (final BakedTextureIndex bakedTextureIndex : BakedTextureIndex.values()) {
            final int ordinal = bakedTextureIndex.getFaceIndex().ordinal();
            final BakedImage bakedImage = this.bakedImages.get(bakedTextureIndex);
            if (bakedImage != null) {
                final UUID uploadedID = bakedImage.getUploadedID();
                if (uploadedID != null) {
                    final MutableSLTextureEntryFace mutableSLTextureEntryFace = new MutableSLTextureEntryFace(0);
                    mutableSLTextureEntryFace.setTextureID(uploadedID);
                    array[ordinal] = SLTextureEntryFace.create(mutableSLTextureEntryFace);
                }
            }
        }
        return SLTextureEntry.create(create, array);
    }
    
    private void bakeAppearance() {
        // 
        // This method could not be decompiled.
        // 
        // Original Bytecode:
        // 
        //     2: iconst_0       
        //     3: anewarray       Ljava/lang/Object;
        //     6: invokestatic    com/lumiyaviewer/lumiya/Debug.Printf:(Ljava/lang/String;[Ljava/lang/Object;)V
        //     9: aload_0        
        //    10: getfield        com/lumiyaviewer/lumiya/slproto/baker/BakeProcess.wearables:Ljava/util/Map;
        //    13: invokeinterface java/util/Map.values:()Ljava/util/Collection;
        //    18: invokeinterface java/lang/Iterable.iterator:()Ljava/util/Iterator;
        //    23: astore_1       
        //    24: aload_1        
        //    25: invokeinterface java/util/Iterator.hasNext:()Z
        //    30: ifeq            72
        //    33: aload_1        
        //    34: invokeinterface java/util/Iterator.next:()Ljava/lang/Object;
        //    39: checkcast       Ljava/util/List;
        //    42: invokeinterface java/lang/Iterable.iterator:()Ljava/util/Iterator;
        //    47: astore_2       
        //    48: aload_2        
        //    49: invokeinterface java/util/Iterator.hasNext:()Z
        //    54: ifeq            24
        //    57: aload_2        
        //    58: invokeinterface java/util/Iterator.next:()Ljava/lang/Object;
        //    63: checkcast       Lcom/lumiyaviewer/lumiya/slproto/baker/BakeProcess$WearableTextureData;
        //    66: invokevirtual   com/lumiyaviewer/lumiya/slproto/baker/BakeProcess$WearableTextureData.requestData:()V
        //    69: goto            48
        //    72: aload_0        
        //    73: getfield        com/lumiyaviewer/lumiya/slproto/baker/BakeProcess.textureReadyLock:Ljava/lang/Object;
        //    76: astore_2       
        //    77: aload_2        
        //    78: monitorenter   
        //    79: aload_0        
        //    80: invokespecial   com/lumiyaviewer/lumiya/slproto/baker/BakeProcess.isTexturesReady:()Z
        //    83: istore_3       
        //    84: iload_3        
        //    85: ifne            116
        //    88: aload_0        
        //    89: getfield        com/lumiyaviewer/lumiya/slproto/baker/BakeProcess.textureReadyLock:Ljava/lang/Object;
        //    92: invokevirtual   java/lang/Object.wait:()V
        //    95: goto            79
        //    98: astore_1       
        //    99: aload_0        
        //   100: aconst_null    
        //   101: invokespecial   com/lumiyaviewer/lumiya/slproto/baker/BakeProcess.finishBaking:(Lcom/lumiyaviewer/lumiya/slproto/textures/SLTextureEntry;)V
        //   104: ldc             "Baking: Interrupted before textures were ready."
        //   106: iconst_0       
        //   107: anewarray       Ljava/lang/Object;
        //   110: invokestatic    com/lumiyaviewer/lumiya/Debug.Printf:(Ljava/lang/String;[Ljava/lang/Object;)V
        //   113: aload_2        
        //   114: monitorexit    
        //   115: return         
        //   116: aload_2        
        //   117: monitorexit    
        //   118: ldc             "Baking: calculating param values..."
        //   120: invokestatic    com/lumiyaviewer/lumiya/Debug.Log:(Ljava/lang/String;)V
        //   123: aload_0        
        //   124: aload_0        
        //   125: aload_0        
        //   126: getfield        com/lumiyaviewer/lumiya/slproto/baker/BakeProcess.wornWearables:Lcom/google/common/collect/Table;
        //   129: invokespecial   com/lumiyaviewer/lumiya/slproto/baker/BakeProcess.calcAllParamValues:(Lcom/google/common/collect/Table;)Ljava/util/Map;
        //   132: putfield        com/lumiyaviewer/lumiya/slproto/baker/BakeProcess.paramValues:Ljava/util/Map;
        //   135: ldc             "Baking: baking..."
        //   137: invokestatic    com/lumiyaviewer/lumiya/Debug.Log:(Ljava/lang/String;)V
        //   140: aload_0        
        //   141: invokespecial   com/lumiyaviewer/lumiya/slproto/baker/BakeProcess.isWearingSkirt:()Z
        //   144: istore_3       
        //   145: invokestatic    com/lumiyaviewer/lumiya/GlobalOptions.getInstance:()Lcom/lumiyaviewer/lumiya/GlobalOptions;
        //   148: ldc             "baker"
        //   150: invokevirtual   com/lumiyaviewer/lumiya/GlobalOptions.getCacheDir:(Ljava/lang/String;)Ljava/io/File;
        //   153: astore          4
        //   155: aload           4
        //   157: invokevirtual   java/io/File.mkdirs:()Z
        //   160: pop            
        //   161: invokestatic    com/lumiyaviewer/lumiya/slproto/avatar/BakedTextureIndex.values:()[Lcom/lumiyaviewer/lumiya/slproto/avatar/BakedTextureIndex;
        //   164: astore_1       
        //   165: aload_1        
        //   166: arraylength    
        //   167: istore          5
        //   169: iconst_0       
        //   170: istore          6
        //   172: iload           6
        //   174: iload           5
        //   176: if_icmpge       454
        //   179: aload_1        
        //   180: iload           6
        //   182: aaload         
        //   183: astore_2       
        //   184: invokestatic    java/lang/Thread.interrupted:()Z
        //   187: ifeq            224
        //   190: ldc_w           "Baking: interrupted."
        //   193: invokestatic    com/lumiyaviewer/lumiya/Debug.Log:(Ljava/lang/String;)V
        //   196: aload_0        
        //   197: getfield        com/lumiyaviewer/lumiya/slproto/baker/BakeProcess.eventBus:Lcom/lumiyaviewer/lumiya/eventbus/EventBus;
        //   200: new             Lcom/lumiyaviewer/lumiya/slproto/events/SLBakingProgressEvent;
        //   203: dup            
        //   204: iconst_0       
        //   205: iconst_1       
        //   206: iconst_0       
        //   207: invokespecial   com/lumiyaviewer/lumiya/slproto/events/SLBakingProgressEvent.<init>:(ZZI)V
        //   210: invokevirtual   com/lumiyaviewer/lumiya/eventbus/EventBus.publish:(Ljava/lang/Object;)V
        //   213: aload_0        
        //   214: aconst_null    
        //   215: invokespecial   com/lumiyaviewer/lumiya/slproto/baker/BakeProcess.finishBaking:(Lcom/lumiyaviewer/lumiya/slproto/textures/SLTextureEntry;)V
        //   218: return         
        //   219: astore_1       
        //   220: aload_2        
        //   221: monitorexit    
        //   222: aload_1        
        //   223: athrow         
        //   224: aload_2        
        //   225: getstatic       com/lumiyaviewer/lumiya/slproto/avatar/BakedTextureIndex.BAKED_SKIRT:Lcom/lumiyaviewer/lumiya/slproto/avatar/BakedTextureIndex;
        //   228: if_acmpne       243
        //   231: iload_3        
        //   232: iconst_1       
        //   233: ixor           
        //   234: ifeq            243
        //   237: iinc            6, 1
        //   240: goto            172
        //   243: new             Ljava/lang/StringBuilder;
        //   246: dup            
        //   247: invokespecial   java/lang/StringBuilder.<init>:()V
        //   250: ldc_w           "Baking: Baking layer "
        //   253: invokevirtual   java/lang/StringBuilder.append:(Ljava/lang/String;)Ljava/lang/StringBuilder;
        //   256: aload_2        
        //   257: invokevirtual   java/lang/StringBuilder.append:(Ljava/lang/Object;)Ljava/lang/StringBuilder;
        //   260: invokevirtual   java/lang/StringBuilder.toString:()Ljava/lang/String;
        //   263: invokestatic    com/lumiyaviewer/lumiya/Debug.Log:(Ljava/lang/String;)V
        //   266: new             Lcom/lumiyaviewer/lumiya/slproto/baker/BakedImage;
        //   269: dup            
        //   270: getstatic       com/lumiyaviewer/lumiya/slproto/baker/BakeLayers.layerSets:Ljava/util/Map;
        //   273: aload_2        
        //   274: invokeinterface java/util/Map.get:(Ljava/lang/Object;)Ljava/lang/Object;
        //   279: checkcast       Lcom/lumiyaviewer/lumiya/slproto/baker/BakeLayerSet;
        //   282: invokespecial   com/lumiyaviewer/lumiya/slproto/baker/BakedImage.<init>:(Lcom/lumiyaviewer/lumiya/slproto/baker/BakeLayerSet;)V
        //   285: astore          7
        //   287: aload_0        
        //   288: getfield        com/lumiyaviewer/lumiya/slproto/baker/BakeProcess.bakedImages:Ljava/util/Map;
        //   291: aload_2        
        //   292: aload           7
        //   294: invokeinterface java/util/Map.put:(Ljava/lang/Object;Ljava/lang/Object;)Ljava/lang/Object;
        //   299: pop            
        //   300: aload           7
        //   302: aload_0        
        //   303: invokevirtual   com/lumiyaviewer/lumiya/slproto/baker/BakedImage.Bake:(Lcom/lumiyaviewer/lumiya/slproto/baker/BakeProcess;)V
        //   306: invokestatic    java/lang/Thread.interrupted:()Z
        //   309: ifeq            341
        //   312: ldc_w           "Baking: interrupted."
        //   315: invokestatic    com/lumiyaviewer/lumiya/Debug.Log:(Ljava/lang/String;)V
        //   318: aload_0        
        //   319: getfield        com/lumiyaviewer/lumiya/slproto/baker/BakeProcess.eventBus:Lcom/lumiyaviewer/lumiya/eventbus/EventBus;
        //   322: new             Lcom/lumiyaviewer/lumiya/slproto/events/SLBakingProgressEvent;
        //   325: dup            
        //   326: iconst_0       
        //   327: iconst_1       
        //   328: iconst_0       
        //   329: invokespecial   com/lumiyaviewer/lumiya/slproto/events/SLBakingProgressEvent.<init>:(ZZI)V
        //   332: invokevirtual   com/lumiyaviewer/lumiya/eventbus/EventBus.publish:(Ljava/lang/Object;)V
        //   335: aload_0        
        //   336: aconst_null    
        //   337: invokespecial   com/lumiyaviewer/lumiya/slproto/baker/BakeProcess.finishBaking:(Lcom/lumiyaviewer/lumiya/slproto/textures/SLTextureEntry;)V
        //   340: return         
        //   341: new             Ljava/io/File;
        //   344: astore          8
        //   346: new             Ljava/lang/StringBuilder;
        //   349: astore          9
        //   351: aload           9
        //   353: invokespecial   java/lang/StringBuilder.<init>:()V
        //   356: aload           8
        //   358: aload           4
        //   360: aload           9
        //   362: aload_2        
        //   363: invokevirtual   com/lumiyaviewer/lumiya/slproto/avatar/BakedTextureIndex.toString:()Ljava/lang/String;
        //   366: invokevirtual   java/lang/StringBuilder.append:(Ljava/lang/String;)Ljava/lang/StringBuilder;
        //   369: ldc_w           ".j2k"
        //   372: invokevirtual   java/lang/StringBuilder.append:(Ljava/lang/String;)Ljava/lang/StringBuilder;
        //   375: invokevirtual   java/lang/StringBuilder.toString:()Ljava/lang/String;
        //   378: invokespecial   java/io/File.<init>:(Ljava/io/File;Ljava/lang/String;)V
        //   381: aload           7
        //   383: aload           8
        //   385: invokevirtual   com/lumiyaviewer/lumiya/slproto/baker/BakedImage.SaveToJPEG2K:(Ljava/io/File;)V
        //   388: new             Lcom/lumiyaviewer/lumiya/slproto/baker/BakeProcess$BakedImageUploadRequest;
        //   391: astore          9
        //   393: aload           9
        //   395: aload           7
        //   397: aload_2        
        //   398: aload           8
        //   400: invokespecial   com/lumiyaviewer/lumiya/slproto/baker/BakeProcess$BakedImageUploadRequest.<init>:(Lcom/lumiyaviewer/lumiya/slproto/baker/BakedImage;Lcom/lumiyaviewer/lumiya/slproto/avatar/BakedTextureIndex;Ljava/io/File;)V
        //   403: aload           9
        //   405: aload_0        
        //   406: invokevirtual   com/lumiyaviewer/lumiya/slproto/modules/texuploader/SLTextureUploadRequest.setOnUploadComplete:(Lcom/lumiyaviewer/lumiya/slproto/modules/texuploader/SLTextureUploadRequest$TextureUploadCompleteListener;)V
        //   409: aload_0        
        //   410: getfield        com/lumiyaviewer/lumiya/slproto/baker/BakeProcess.uploader:Lcom/lumiyaviewer/lumiya/slproto/modules/texuploader/SLTextureUploader;
        //   413: aload           9
        //   415: invokevirtual   com/lumiyaviewer/lumiya/slproto/modules/texuploader/SLTextureUploader.BeginUpload:(Lcom/lumiyaviewer/lumiya/slproto/modules/texuploader/SLTextureUploadRequest;)V
        //   418: new             Ljava/lang/StringBuilder;
        //   421: dup            
        //   422: invokespecial   java/lang/StringBuilder.<init>:()V
        //   425: ldc_w           "Baking: Done layer "
        //   428: invokevirtual   java/lang/StringBuilder.append:(Ljava/lang/String;)Ljava/lang/StringBuilder;
        //   431: aload_2        
        //   432: invokevirtual   java/lang/StringBuilder.append:(Ljava/lang/Object;)Ljava/lang/StringBuilder;
        //   435: invokevirtual   java/lang/StringBuilder.toString:()Ljava/lang/String;
        //   438: invokestatic    com/lumiyaviewer/lumiya/Debug.Log:(Ljava/lang/String;)V
        //   441: goto            237
        //   444: astore          8
        //   446: aload           8
        //   448: invokevirtual   java/io/IOException.printStackTrace:()V
        //   451: goto            418
        //   454: ldc_w           "Baking: Baked all layers."
        //   457: invokestatic    com/lumiyaviewer/lumiya/Debug.Log:(Ljava/lang/String;)V
        //   460: return         
        //    Exceptions:
        //  Try           Handler
        //  Start  End    Start  End    Type                            
        //  -----  -----  -----  -----  --------------------------------
        //  79     84     219    224    Any
        //  88     95     98     116    Ljava/lang/InterruptedException;
        //  88     95     219    224    Any
        //  99     113    219    224    Any
        //  341    418    444    454    Ljava/io/IOException;
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
    
    private Map<Integer, Float> calcAllParamValues(final Table<SLWearableType, UUID, SLWearable> table) {
        final HashMap hashMap = new HashMap();
        for (final Map.Entry entry : SLAvatarParams.paramByIDs.entrySet()) {
            hashMap.put(entry.getKey(), ((SLAvatarParams.AvatarParam)((SLAvatarParams.ParamSet)entry.getValue()).params.get(0)).defValue);
        }
        final Iterator<Object> iterator2 = table.values().iterator();
        while (iterator2.hasNext()) {
            final SLWearableData wearableData = iterator2.next().getWearableData();
            if (wearableData != null) {
                for (final SLWearableData.WearableParam wearableParam : wearableData.params) {
                    hashMap.put(wearableParam.paramIndex, wearableParam.paramValue);
                    final SLAvatarParams.ParamSet set = SLAvatarParams.paramByIDs.get(wearableParam.paramIndex);
                    if (set != null) {
                        final SLAvatarParams.AvatarParam avatarParam = set.params.get(0);
                        if (avatarParam.drivenParams == null) {
                            continue;
                        }
                        for (final SLAvatarParams.DrivenParam drivenParam : avatarParam.drivenParams) {
                            final SLAvatarParams.ParamSet set2 = SLAvatarParams.paramByIDs.get(drivenParam.drivenID);
                            if (set2 != null) {
                                final Iterator<Object> iterator5 = set2.params.iterator();
                                while (iterator5.hasNext()) {
                                    hashMap.put(drivenParam.drivenID, AvatarSkeleton.getDrivenWeight(wearableParam.paramValue, avatarParam, drivenParam, iterator5.next()));
                                }
                            }
                        }
                    }
                }
            }
        }
        return hashMap;
    }
    
    private void finishBaking(final SLTextureEntry slTextureEntry) {
        this.avatarAppearance.finishBaking(this, slTextureEntry);
    }
    
    private boolean isTexturesReady() {
        final Iterator<Object> iterator = this.wearables.values().iterator();
        boolean b = true;
        while (iterator.hasNext()) {
            final Iterator iterator2 = iterator.next().iterator();
            boolean b2 = b;
            while (true) {
                b = b2;
                if (!iterator2.hasNext()) {
                    break;
                }
                if (((WearableTextureData)iterator2.next()).getTextureReady()) {
                    continue;
                }
                b2 = false;
            }
        }
        return b;
    }
    
    private boolean isWearingSkirt() {
        boolean b = false;
        if (!this.wornWearables.row(SLWearableType.WT_SKIRT).isEmpty()) {
            b = true;
        }
        return b;
    }
    
    private void notifyTextureReady() {
        synchronized (this.textureReadyLock) {
            this.textureReadyLock.notifyAll();
        }
    }
    
    @Override
    public void OnTextureUploadComplete(final SLTextureUploadRequest slTextureUploadRequest) {
        if (slTextureUploadRequest instanceof BakedImageUploadRequest) {
            final BakedImageUploadRequest bakedImageUploadRequest = (BakedImageUploadRequest)slTextureUploadRequest;
            Debug.Log("Baking: texture " + bakedImageUploadRequest.bakedIndex + " uploaded, UUID = " + bakedImageUploadRequest.getTextureID());
            bakedImageUploadRequest.bakedImage.setUploadedID(bakedImageUploadRequest.getTextureID());
            this.bakedImages.put(bakedImageUploadRequest.bakedIndex, bakedImageUploadRequest.bakedImage);
            final boolean wearingSkirt = this.isWearingSkirt();
            final BakedTextureIndex[] values = BakedTextureIndex.values();
            final int length = values.length;
            int i = 0;
            boolean b = true;
            int n = 0;
            int n2 = 0;
            while (i < length) {
                final BakedTextureIndex bakedTextureIndex = values[i];
                if (bakedTextureIndex != BakedTextureIndex.BAKED_SKIRT || !(wearingSkirt ^ true)) {
                    ++n2;
                    if (!this.bakedImages.containsKey(bakedTextureIndex)) {
                        ++n;
                        b = false;
                    }
                    else if (this.bakedImages.get(bakedTextureIndex).getUploadedID() == null) {
                        ++n;
                        b = false;
                    }
                }
                ++i;
            }
            if (b) {
                this.eventBus.publish(new SLBakingProgressEvent(false, true, 100));
                Debug.Log("Baking: all textures uploaded.");
                this.finishBaking(this.PrepareAvatarTextureEntry());
            }
            else {
                this.eventBus.publish(new SLBakingProgressEvent(false, false, (n2 - n) * 100 / n2));
            }
        }
    }
    
    public void cancel() {
        this.bakingThread.interrupt();
    }
    
    List<OpenJPEG> getLocalTexture(final AvatarTextureFaceIndex avatarTextureFaceIndex) throws DefaultTextureException {
        final Iterator<Object> iterator = this.wearables.values().iterator();
        int n = 0;
        List<OpenJPEG> list = null;
        while (iterator.hasNext()) {
            final Iterator iterator2 = iterator.next().iterator();
            List<OpenJPEG> list2 = list;
            int n2 = n;
            while (true) {
                n = n2;
                list = list2;
                if (!iterator2.hasNext()) {
                    break;
                }
                final WearableTextureData wearableTextureData = (WearableTextureData)iterator2.next();
                if (wearableTextureData.getTexture().layer != avatarTextureFaceIndex.ordinal()) {
                    continue;
                }
                int n3;
                if (wearableTextureData.getTexture().textureID != null && wearableTextureData.getTexture().textureID.equals(DrawableAvatarPart.DEFAULT_AVATAR_TEXTURE)) {
                    n3 = 1;
                }
                else {
                    n3 = 0;
                }
                if (n3 != 0) {
                    n2 = 1;
                }
                else {
                    if (wearableTextureData.textureData == null) {
                        continue;
                    }
                    final OpenJPEG textureData = wearableTextureData.getTextureData();
                    if (textureData == null) {
                        continue;
                    }
                    List<OpenJPEG> list3;
                    if ((list3 = list2) == null) {
                        list3 = new LinkedList<OpenJPEG>();
                    }
                    list3.add(textureData);
                    list2 = list3;
                }
            }
        }
        if (list == null && n != 0) {
            throw new DefaultTextureException();
        }
        return list;
    }
    
    float getParamWeight(final int i, final SLAvatarParams.AvatarParam avatarParam) {
        final Float n = this.paramValues.get(i);
        if (n != null) {
            return n;
        }
        return avatarParam.defValue;
    }
    
    private static class BakedImageUploadRequest extends SLTextureUploadRequest
    {
        final BakedImage bakedImage;
        final BakedTextureIndex bakedIndex;
        
        BakedImageUploadRequest(final BakedImage bakedImage, final BakedTextureIndex bakedIndex, final File file) {
            super(file, bakedIndex.ordinal());
            this.bakedImage = bakedImage;
            this.bakedIndex = bakedIndex;
        }
    }
    
    static class DefaultTextureException extends Exception
    {
    }
    
    private class WearableTextureData implements ResourceConsumer
    {
        private final SLWearableData.WearableTexture texture;
        private volatile OpenJPEG textureData;
        private volatile boolean textureReady;
        
        WearableTextureData(final SLWearableData.WearableTexture texture) {
            this.texture = texture;
            this.textureReady = false;
        }
        
        @Override
        public void OnResourceReady(final Object o, final boolean b) {
            if (o instanceof OpenJPEG) {
                this.textureData = (OpenJPEG)o;
            }
            this.textureReady = true;
            BakeProcess.this.notifyTextureReady();
        }
        
        protected SLWearableData.WearableTexture getTexture() {
            return this.texture;
        }
        
        OpenJPEG getTextureData() {
            return this.textureData;
        }
        
        boolean getTextureReady() {
            return this.textureReady;
        }
        
        void requestData() {
            ((ResourceMemoryCache<DrawableTextureParams, ResourceType>)TextureCache.getInstance()).RequestResource(DrawableTextureParams.create(this.texture.textureID, TextureClass.Asset), this);
        }
    }
}
