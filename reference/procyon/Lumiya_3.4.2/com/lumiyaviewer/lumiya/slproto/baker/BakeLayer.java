// 
// Decompiled by Procyon v0.6.0
// 

package com.lumiyaviewer.lumiya.slproto.baker;

import java.util.Iterator;
import java.io.InputStream;
import com.lumiyaviewer.lumiya.LumiyaApp;
import com.lumiyaviewer.lumiya.openjpeg.OpenJPEG;
import com.lumiyaviewer.lumiya.slproto.avatar.SLAvatarParams;
import com.lumiyaviewer.lumiya.Debug;
import com.lumiyaviewer.lumiya.slproto.avatar.SLAvatarParamColor;
import com.lumiyaviewer.lumiya.slproto.avatar.AvatarTextureFaceIndex;

public class BakeLayer
{
    public int fixedColor;
    public SLAvatarGlobalColor globalColor;
    public boolean hasFixedColor;
    public boolean isRenderPassBump;
    public String layerName;
    public AvatarTextureFaceIndex localTexture;
    public boolean localTextureAlphaOnly;
    public int[] paramIDs;
    public boolean tgaFileIsMask;
    public String tgaTexture;
    public boolean visibilityMask;
    public boolean writeAllChannels;
    
    private static /* synthetic */ int[] -getcom-lumiyaviewer-lumiya-slproto-avatar-SLAvatarParamColor$ColorOperationSwitchesValues() {
        if (BakeLayer.-com-lumiyaviewer-lumiya-slproto-avatar-SLAvatarParamColor$ColorOperationSwitchesValues != null) {
            return BakeLayer.-com-lumiyaviewer-lumiya-slproto-avatar-SLAvatarParamColor$ColorOperationSwitchesValues;
        }
        int[] -com-lumiyaviewer-lumiya-slproto-avatar-SLAvatarParamColor$ColorOperationSwitchesValues = new int[SLAvatarParamColor.ColorOperation.values().length];
        while (true) {
            try {
                -com-lumiyaviewer-lumiya-slproto-avatar-SLAvatarParamColor$ColorOperationSwitchesValues[SLAvatarParamColor.ColorOperation.Blend.ordinal()] = 1;
                try {
                    -com-lumiyaviewer-lumiya-slproto-avatar-SLAvatarParamColor$ColorOperationSwitchesValues[SLAvatarParamColor.ColorOperation.Default.ordinal()] = 2;
                    try {
                        -com-lumiyaviewer-lumiya-slproto-avatar-SLAvatarParamColor$ColorOperationSwitchesValues[SLAvatarParamColor.ColorOperation.Multiply.ordinal()] = 3;
                        return -com-lumiyaviewer-lumiya-slproto-avatar-SLAvatarParamColor$ColorOperationSwitchesValues = -com-lumiyaviewer-lumiya-slproto-avatar-SLAvatarParamColor$ColorOperationSwitchesValues;
                    }
                    catch (final NoSuchFieldError noSuchFieldError) {}
                }
                catch (final NoSuchFieldError noSuchFieldError2) {}
            }
            catch (final NoSuchFieldError noSuchFieldError3) {
                continue;
            }
            break;
        }
    }
    
    public BakeLayer(final String layerName, final SLAvatarGlobalColor globalColor, final boolean hasFixedColor, final int fixedColor, final boolean isRenderPassBump, final boolean visibilityMask, final boolean writeAllChannels, final AvatarTextureFaceIndex localTexture, final boolean localTextureAlphaOnly, final String tgaTexture, final boolean tgaFileIsMask, final int[] paramIDs) {
        this.layerName = layerName;
        this.globalColor = globalColor;
        this.hasFixedColor = hasFixedColor;
        this.fixedColor = fixedColor;
        this.isRenderPassBump = isRenderPassBump;
        this.visibilityMask = visibilityMask;
        this.writeAllChannels = writeAllChannels;
        this.localTexture = localTexture;
        this.localTextureAlphaOnly = localTextureAlphaOnly;
        this.tgaTexture = tgaTexture;
        this.tgaFileIsMask = tgaFileIsMask;
        this.paramIDs = paramIDs;
    }
    
    private int getColorByParamList(final BakeProcess bakeProcess, final int[] array, int n, int i) {
        int n2 = 0;
        if (this.layerName.equals("lipstick")) {
            Debug.Log(String.format("Baking: lipstick start color %08x default %08x", n, i));
        }
        final int length = array.length;
        int j = 0;
    Label_0242_Outer:
        while (j < length) {
            final int k = array[j];
            final SLAvatarParams.ParamSet set = SLAvatarParams.paramByIDs.get(k);
            if (set != null) {
                final SLAvatarParams.AvatarParam avatarParam = set.params.get(0);
                final SLAvatarParamColor paramColor = avatarParam.paramColor;
                if (paramColor != null) {
                    final float paramWeight = bakeProcess.getParamWeight(k, avatarParam);
                    final int color = paramColor.getColor(paramWeight);
                    if (this.layerName.equals("lipstick")) {
                        Debug.Log(String.format("Baking: lipstick color param weight %ff color %08x", paramWeight, color));
                    }
                    switch (-getcom-lumiyaviewer-lumiya-slproto-avatar-SLAvatarParamColor$ColorOperationSwitchesValues()[paramColor.colorOperation.ordinal()]) {
                        case 1: {
                            n = SLAvatarParamColor.colorLerp(n, color, paramWeight);
                            break;
                        }
                        case 3: {
                            n = SLAvatarParamColor.colorMult(n, color);
                            break;
                        }
                        case 2: {
                            n = SLAvatarParamColor.colorAdd(n, color);
                            break;
                        }
                    }
                    if (this.layerName.equals("lipstick")) {
                        Debug.Log(String.format("Baking: after op, lipstick color result %08x", n));
                        n2 = 1;
                    }
                    else {
                        n2 = 1;
                    }
                }
            }
            while (true) {
                ++j;
                continue Label_0242_Outer;
                continue;
            }
        }
        if (n2 != 0) {
            i = n;
        }
        return i;
    }
    
    private int getNetColor(final BakeProcess bakeProcess) {
        final int[] paramIDs = this.paramIDs;
        final int length = paramIDs.length;
        int i = 0;
        while (true) {
            while (i < length) {
                final SLAvatarParams.ParamSet set = SLAvatarParams.paramByIDs.get(paramIDs[i]);
                if (set != null && ((SLAvatarParams.AvatarParam)set.params.get(0)).paramColor != null) {
                    final int n = 1;
                    if (n != 0) {
                        int n2;
                        if (this.globalColor != null) {
                            n2 = this.getColorByParamList(bakeProcess, this.globalColor.getParamIDs(), 0, 0);
                        }
                        else if (this.hasFixedColor) {
                            n2 = this.fixedColor;
                        }
                        else {
                            n2 = 0;
                        }
                        return this.getColorByParamList(bakeProcess, this.paramIDs, n2, n2);
                    }
                    int n3;
                    if (this.globalColor != null) {
                        n3 = this.getColorByParamList(bakeProcess, this.globalColor.getParamIDs(), 0, 0);
                    }
                    else if (this.hasFixedColor) {
                        n3 = this.fixedColor;
                    }
                    else {
                        n3 = -1;
                    }
                    return n3;
                }
                else {
                    ++i;
                }
            }
            final int n = 0;
            continue;
        }
    }
    
    public void Bake(final OpenJPEG p0, final BakeProcess p1) {
        // 
        // This method could not be decompiled.
        // 
        // Original Bytecode:
        // 
        //     1: istore_3       
        //     2: aload_0        
        //     3: aload_2        
        //     4: invokespecial   com/lumiyaviewer/lumiya/slproto/baker/BakeLayer.getNetColor:(Lcom/lumiyaviewer/lumiya/slproto/baker/BakeProcess;)I
        //     7: istore          4
        //     9: new             Lcom/lumiyaviewer/lumiya/openjpeg/OpenJPEG;
        //    12: dup            
        //    13: aload_1        
        //    14: getfield        com/lumiyaviewer/lumiya/openjpeg/OpenJPEG.width:I
        //    17: aload_1        
        //    18: getfield        com/lumiyaviewer/lumiya/openjpeg/OpenJPEG.height:I
        //    21: iconst_4       
        //    22: iconst_4       
        //    23: iconst_0       
        //    24: iconst_0       
        //    25: invokespecial   com/lumiyaviewer/lumiya/openjpeg/OpenJPEG.<init>:(IIIIII)V
        //    28: astore          5
        //    30: new             Lcom/lumiyaviewer/lumiya/openjpeg/OpenJPEG;
        //    33: dup            
        //    34: aload_1        
        //    35: getfield        com/lumiyaviewer/lumiya/openjpeg/OpenJPEG.width:I
        //    38: aload_1        
        //    39: getfield        com/lumiyaviewer/lumiya/openjpeg/OpenJPEG.height:I
        //    42: iconst_4       
        //    43: iconst_4       
        //    44: iconst_0       
        //    45: ldc             -16777216
        //    47: invokespecial   com/lumiyaviewer/lumiya/openjpeg/OpenJPEG.<init>:(IIIIII)V
        //    50: astore          6
        //    52: ldc             "Baking: layer %s net_color 0x%08x."
        //    54: iconst_2       
        //    55: anewarray       Ljava/lang/Object;
        //    58: dup            
        //    59: iconst_0       
        //    60: aload_0        
        //    61: getfield        com/lumiyaviewer/lumiya/slproto/baker/BakeLayer.layerName:Ljava/lang/String;
        //    64: aastore        
        //    65: dup            
        //    66: iconst_1       
        //    67: iload           4
        //    69: invokestatic    java/lang/Integer.valueOf:(I)Ljava/lang/Integer;
        //    72: aastore        
        //    73: invokestatic    java/lang/String.format:(Ljava/lang/String;[Ljava/lang/Object;)Ljava/lang/String;
        //    76: invokestatic    com/lumiyaviewer/lumiya/Debug.Log:(Ljava/lang/String;)V
        //    79: iconst_1       
        //    80: istore          7
        //    82: iconst_0       
        //    83: istore          8
        //    85: aload_0        
        //    86: getfield        com/lumiyaviewer/lumiya/slproto/baker/BakeLayer.paramIDs:[I
        //    89: astore          9
        //    91: aload           9
        //    93: arraylength    
        //    94: istore          10
        //    96: iconst_0       
        //    97: istore          11
        //    99: iload           11
        //   101: iload           10
        //   103: if_icmpge       442
        //   106: aload           9
        //   108: iload           11
        //   110: iaload         
        //   111: istore          12
        //   113: getstatic       com/lumiyaviewer/lumiya/slproto/avatar/SLAvatarParams.paramByIDs:Lcom/google/common/collect/ImmutableMap;
        //   116: iload           12
        //   118: invokestatic    java/lang/Integer.valueOf:(I)Ljava/lang/Integer;
        //   121: invokevirtual   com/google/common/collect/ImmutableMap.get:(Ljava/lang/Object;)Ljava/lang/Object;
        //   124: checkcast       Lcom/lumiyaviewer/lumiya/slproto/avatar/SLAvatarParams$ParamSet;
        //   127: astore          13
        //   129: aload           13
        //   131: ifnull          902
        //   134: aload           13
        //   136: getfield        com/lumiyaviewer/lumiya/slproto/avatar/SLAvatarParams$ParamSet.params:Lcom/google/common/collect/ImmutableList;
        //   139: iconst_0       
        //   140: invokevirtual   com/google/common/collect/ImmutableList.get:(I)Ljava/lang/Object;
        //   143: checkcast       Lcom/lumiyaviewer/lumiya/slproto/avatar/SLAvatarParams$AvatarParam;
        //   146: astore          13
        //   148: aload           13
        //   150: getfield        com/lumiyaviewer/lumiya/slproto/avatar/SLAvatarParams$AvatarParam.paramAlpha:Lcom/lumiyaviewer/lumiya/slproto/avatar/SLAvatarParamAlpha;
        //   153: ifnull          902
        //   156: aload           13
        //   158: getfield        com/lumiyaviewer/lumiya/slproto/avatar/SLAvatarParams$AvatarParam.paramAlpha:Lcom/lumiyaviewer/lumiya/slproto/avatar/SLAvatarParamAlpha;
        //   161: getfield        com/lumiyaviewer/lumiya/slproto/avatar/SLAvatarParamAlpha.tgaFile:Ljava/lang/String;
        //   164: ifnull          902
        //   167: aload_2        
        //   168: iload           12
        //   170: aload           13
        //   172: invokevirtual   com/lumiyaviewer/lumiya/slproto/baker/BakeProcess.getParamWeight:(ILcom/lumiyaviewer/lumiya/slproto/avatar/SLAvatarParams$AvatarParam;)F
        //   175: fstore          14
        //   177: iload           7
        //   179: ifeq            899
        //   182: iconst_0       
        //   183: istore          12
        //   185: iload           12
        //   187: istore          7
        //   189: aload           13
        //   191: getfield        com/lumiyaviewer/lumiya/slproto/avatar/SLAvatarParams$AvatarParam.paramAlpha:Lcom/lumiyaviewer/lumiya/slproto/avatar/SLAvatarParamAlpha;
        //   194: getfield        com/lumiyaviewer/lumiya/slproto/avatar/SLAvatarParamAlpha.multiplyBlend:Z
        //   197: ifne            211
        //   200: aload           6
        //   202: iconst_3       
        //   203: iconst_0       
        //   204: invokevirtual   com/lumiyaviewer/lumiya/openjpeg/OpenJPEG.setComponent:(IB)V
        //   207: iload           12
        //   209: istore          7
        //   211: fload           14
        //   213: fconst_0       
        //   214: fcmpl          
        //   215: ifne            235
        //   218: aload           13
        //   220: getfield        com/lumiyaviewer/lumiya/slproto/avatar/SLAvatarParams$AvatarParam.paramAlpha:Lcom/lumiyaviewer/lumiya/slproto/avatar/SLAvatarParamAlpha;
        //   223: getfield        com/lumiyaviewer/lumiya/slproto/avatar/SLAvatarParamAlpha.skipIfZero:Z
        //   226: ifeq            235
        //   229: iinc            11, 1
        //   232: goto            99
        //   235: iload           8
        //   237: aload           13
        //   239: getfield        com/lumiyaviewer/lumiya/slproto/avatar/SLAvatarParams$AvatarParam.paramAlpha:Lcom/lumiyaviewer/lumiya/slproto/avatar/SLAvatarParamAlpha;
        //   242: getfield        com/lumiyaviewer/lumiya/slproto/avatar/SLAvatarParamAlpha.multiplyBlend:Z
        //   245: ior            
        //   246: istore          8
        //   248: invokestatic    com/lumiyaviewer/lumiya/LumiyaApp.getAssetManager:()Landroid/content/res/AssetManager;
        //   251: astore          15
        //   253: new             Ljava/lang/StringBuilder;
        //   256: astore          16
        //   258: aload           16
        //   260: invokespecial   java/lang/StringBuilder.<init>:()V
        //   263: aload           15
        //   265: aload           16
        //   267: ldc             "tga/"
        //   269: invokevirtual   java/lang/StringBuilder.append:(Ljava/lang/String;)Ljava/lang/StringBuilder;
        //   272: aload           13
        //   274: getfield        com/lumiyaviewer/lumiya/slproto/avatar/SLAvatarParams$AvatarParam.paramAlpha:Lcom/lumiyaviewer/lumiya/slproto/avatar/SLAvatarParamAlpha;
        //   277: getfield        com/lumiyaviewer/lumiya/slproto/avatar/SLAvatarParamAlpha.tgaFile:Ljava/lang/String;
        //   280: invokevirtual   java/lang/StringBuilder.append:(Ljava/lang/String;)Ljava/lang/StringBuilder;
        //   283: invokevirtual   java/lang/StringBuilder.toString:()Ljava/lang/String;
        //   286: invokevirtual   android/content/res/AssetManager.open:(Ljava/lang/String;)Ljava/io/InputStream;
        //   289: astore          16
        //   291: new             Lcom/lumiyaviewer/lumiya/openjpeg/OpenJPEG;
        //   294: astore          15
        //   296: aload           15
        //   298: aload           16
        //   300: getstatic       com/lumiyaviewer/lumiya/openjpeg/OpenJPEG$ImageFormat.TGA:Lcom/lumiyaviewer/lumiya/openjpeg/OpenJPEG$ImageFormat;
        //   303: iconst_1       
        //   304: iconst_1       
        //   305: aload           13
        //   307: getfield        com/lumiyaviewer/lumiya/slproto/avatar/SLAvatarParams$AvatarParam.paramAlpha:Lcom/lumiyaviewer/lumiya/slproto/avatar/SLAvatarParamAlpha;
        //   310: getfield        com/lumiyaviewer/lumiya/slproto/avatar/SLAvatarParamAlpha.domain:F
        //   313: fload           14
        //   315: iconst_0       
        //   316: invokespecial   com/lumiyaviewer/lumiya/openjpeg/OpenJPEG.<init>:(Ljava/io/InputStream;Lcom/lumiyaviewer/lumiya/openjpeg/OpenJPEG$ImageFormat;ZZFFZ)V
        //   319: ldc_w           "Baking: layer %s: applying alpha (weight %f domain %f) mask texture %s, width %d, height %d, num_comps %d"
        //   322: bipush          7
        //   324: anewarray       Ljava/lang/Object;
        //   327: dup            
        //   328: iconst_0       
        //   329: aload_0        
        //   330: getfield        com/lumiyaviewer/lumiya/slproto/baker/BakeLayer.layerName:Ljava/lang/String;
        //   333: aastore        
        //   334: dup            
        //   335: iconst_1       
        //   336: fload           14
        //   338: invokestatic    java/lang/Float.valueOf:(F)Ljava/lang/Float;
        //   341: aastore        
        //   342: dup            
        //   343: iconst_2       
        //   344: aload           13
        //   346: getfield        com/lumiyaviewer/lumiya/slproto/avatar/SLAvatarParams$AvatarParam.paramAlpha:Lcom/lumiyaviewer/lumiya/slproto/avatar/SLAvatarParamAlpha;
        //   349: getfield        com/lumiyaviewer/lumiya/slproto/avatar/SLAvatarParamAlpha.domain:F
        //   352: invokestatic    java/lang/Float.valueOf:(F)Ljava/lang/Float;
        //   355: aastore        
        //   356: dup            
        //   357: iconst_3       
        //   358: aload           13
        //   360: getfield        com/lumiyaviewer/lumiya/slproto/avatar/SLAvatarParams$AvatarParam.paramAlpha:Lcom/lumiyaviewer/lumiya/slproto/avatar/SLAvatarParamAlpha;
        //   363: getfield        com/lumiyaviewer/lumiya/slproto/avatar/SLAvatarParamAlpha.tgaFile:Ljava/lang/String;
        //   366: aastore        
        //   367: dup            
        //   368: iconst_4       
        //   369: aload           15
        //   371: invokevirtual   com/lumiyaviewer/lumiya/openjpeg/OpenJPEG.getWidth:()I
        //   374: invokestatic    java/lang/Integer.valueOf:(I)Ljava/lang/Integer;
        //   377: aastore        
        //   378: dup            
        //   379: iconst_5       
        //   380: aload           15
        //   382: invokevirtual   com/lumiyaviewer/lumiya/openjpeg/OpenJPEG.getHeight:()I
        //   385: invokestatic    java/lang/Integer.valueOf:(I)Ljava/lang/Integer;
        //   388: aastore        
        //   389: dup            
        //   390: bipush          6
        //   392: aload           15
        //   394: invokevirtual   com/lumiyaviewer/lumiya/openjpeg/OpenJPEG.getNumComponents:()I
        //   397: invokestatic    java/lang/Integer.valueOf:(I)Ljava/lang/Integer;
        //   400: aastore        
        //   401: invokestatic    java/lang/String.format:(Ljava/lang/String;[Ljava/lang/Object;)Ljava/lang/String;
        //   404: invokestatic    com/lumiyaviewer/lumiya/Debug.Log:(Ljava/lang/String;)V
        //   407: aload           6
        //   409: aload           15
        //   411: aload           13
        //   413: getfield        com/lumiyaviewer/lumiya/slproto/avatar/SLAvatarParams$AvatarParam.paramAlpha:Lcom/lumiyaviewer/lumiya/slproto/avatar/SLAvatarParamAlpha;
        //   416: getfield        com/lumiyaviewer/lumiya/slproto/avatar/SLAvatarParamAlpha.multiplyBlend:Z
        //   419: iconst_1       
        //   420: ixor           
        //   421: invokevirtual   com/lumiyaviewer/lumiya/openjpeg/OpenJPEG.blendAlpha:(Lcom/lumiyaviewer/lumiya/openjpeg/OpenJPEG;Z)V
        //   424: aload           16
        //   426: invokevirtual   java/io/InputStream.close:()V
        //   429: goto            229
        //   432: astore          13
        //   434: aload           13
        //   436: invokevirtual   java/lang/Exception.printStackTrace:()V
        //   439: goto            229
        //   442: iconst_0       
        //   443: istore          11
        //   445: aload_0        
        //   446: getfield        com/lumiyaviewer/lumiya/slproto/baker/BakeLayer.localTexture:Lcom/lumiyaviewer/lumiya/slproto/avatar/AvatarTextureFaceIndex;
        //   449: ifnull          890
        //   452: aload_2        
        //   453: aload_0        
        //   454: getfield        com/lumiyaviewer/lumiya/slproto/baker/BakeLayer.localTexture:Lcom/lumiyaviewer/lumiya/slproto/avatar/AvatarTextureFaceIndex;
        //   457: invokevirtual   com/lumiyaviewer/lumiya/slproto/baker/BakeProcess.getLocalTexture:(Lcom/lumiyaviewer/lumiya/slproto/avatar/AvatarTextureFaceIndex;)Ljava/util/List;
        //   460: astore_2       
        //   461: aload_2        
        //   462: ifnull          540
        //   465: aload_2        
        //   466: invokeinterface java/lang/Iterable.iterator:()Ljava/util/Iterator;
        //   471: astore_2       
        //   472: iconst_0       
        //   473: istore          7
        //   475: aload_2        
        //   476: invokeinterface java/util/Iterator.hasNext:()Z
        //   481: ifeq            878
        //   484: aload_2        
        //   485: invokeinterface java/util/Iterator.next:()Ljava/lang/Object;
        //   490: checkcast       Lcom/lumiyaviewer/lumiya/openjpeg/OpenJPEG;
        //   493: astore          9
        //   495: ldc_w           "Baking: layer %s: applying local texture, writeAllChannels %s"
        //   498: iconst_2       
        //   499: anewarray       Ljava/lang/Object;
        //   502: dup            
        //   503: iconst_0       
        //   504: aload_0        
        //   505: getfield        com/lumiyaviewer/lumiya/slproto/baker/BakeLayer.layerName:Ljava/lang/String;
        //   508: aastore        
        //   509: dup            
        //   510: iconst_1       
        //   511: aload_0        
        //   512: getfield        com/lumiyaviewer/lumiya/slproto/baker/BakeLayer.writeAllChannels:Z
        //   515: invokestatic    java/lang/Boolean.valueOf:(Z)Ljava/lang/Boolean;
        //   518: aastore        
        //   519: invokestatic    java/lang/String.format:(Ljava/lang/String;[Ljava/lang/Object;)Ljava/lang/String;
        //   522: invokestatic    com/lumiyaviewer/lumiya/Debug.Log:(Ljava/lang/String;)V
        //   525: aload           5
        //   527: aload           9
        //   529: iconst_m1      
        //   530: iconst_0       
        //   531: invokevirtual   com/lumiyaviewer/lumiya/openjpeg/OpenJPEG.draw:(Lcom/lumiyaviewer/lumiya/openjpeg/OpenJPEG;IZ)V
        //   534: iconst_1       
        //   535: istore          7
        //   537: goto            475
        //   540: ldc_w           "Baking: layer %s: missing local texture"
        //   543: iconst_1       
        //   544: anewarray       Ljava/lang/Object;
        //   547: dup            
        //   548: iconst_0       
        //   549: aload_0        
        //   550: getfield        com/lumiyaviewer/lumiya/slproto/baker/BakeLayer.layerName:Ljava/lang/String;
        //   553: aastore        
        //   554: invokestatic    java/lang/String.format:(Ljava/lang/String;[Ljava/lang/Object;)Ljava/lang/String;
        //   557: invokestatic    com/lumiyaviewer/lumiya/Debug.Log:(Ljava/lang/String;)V
        //   560: iconst_1       
        //   561: istore          7
        //   563: iload           11
        //   565: istore_3       
        //   566: iload           7
        //   568: istore          11
        //   570: iload_3        
        //   571: istore          7
        //   573: iload           7
        //   575: istore_3       
        //   576: aload_0        
        //   577: getfield        com/lumiyaviewer/lumiya/slproto/baker/BakeLayer.tgaTexture:Ljava/lang/String;
        //   580: ifnull          754
        //   583: iload           7
        //   585: istore_3       
        //   586: invokestatic    com/lumiyaviewer/lumiya/LumiyaApp.getAssetManager:()Landroid/content/res/AssetManager;
        //   589: astore_2       
        //   590: iload           7
        //   592: istore_3       
        //   593: new             Ljava/lang/StringBuilder;
        //   596: astore          9
        //   598: iload           7
        //   600: istore_3       
        //   601: aload           9
        //   603: invokespecial   java/lang/StringBuilder.<init>:()V
        //   606: iload           7
        //   608: istore_3       
        //   609: aload_2        
        //   610: aload           9
        //   612: ldc             "tga/"
        //   614: invokevirtual   java/lang/StringBuilder.append:(Ljava/lang/String;)Ljava/lang/StringBuilder;
        //   617: aload_0        
        //   618: getfield        com/lumiyaviewer/lumiya/slproto/baker/BakeLayer.tgaTexture:Ljava/lang/String;
        //   621: invokevirtual   java/lang/StringBuilder.append:(Ljava/lang/String;)Ljava/lang/StringBuilder;
        //   624: invokevirtual   java/lang/StringBuilder.toString:()Ljava/lang/String;
        //   627: invokevirtual   android/content/res/AssetManager.open:(Ljava/lang/String;)Ljava/io/InputStream;
        //   630: astore          9
        //   632: iload           7
        //   634: istore_3       
        //   635: new             Lcom/lumiyaviewer/lumiya/openjpeg/OpenJPEG;
        //   638: astore_2       
        //   639: iload           7
        //   641: istore_3       
        //   642: aload_2        
        //   643: aload           9
        //   645: getstatic       com/lumiyaviewer/lumiya/openjpeg/OpenJPEG$ImageFormat.TGA:Lcom/lumiyaviewer/lumiya/openjpeg/OpenJPEG$ImageFormat;
        //   648: aload_0        
        //   649: getfield        com/lumiyaviewer/lumiya/slproto/baker/BakeLayer.tgaFileIsMask:Z
        //   652: iconst_0       
        //   653: fconst_0       
        //   654: fconst_0       
        //   655: iconst_0       
        //   656: invokespecial   com/lumiyaviewer/lumiya/openjpeg/OpenJPEG.<init>:(Ljava/io/InputStream;Lcom/lumiyaviewer/lumiya/openjpeg/OpenJPEG$ImageFormat;ZZFFZ)V
        //   659: iload           7
        //   661: istore_3       
        //   662: ldc_w           "Baking: layer %s: applying tga texture %s, writeAllChannels %s, width %d, height %d, num_comps %d"
        //   665: bipush          6
        //   667: anewarray       Ljava/lang/Object;
        //   670: dup            
        //   671: iconst_0       
        //   672: aload_0        
        //   673: getfield        com/lumiyaviewer/lumiya/slproto/baker/BakeLayer.layerName:Ljava/lang/String;
        //   676: aastore        
        //   677: dup            
        //   678: iconst_1       
        //   679: aload_0        
        //   680: getfield        com/lumiyaviewer/lumiya/slproto/baker/BakeLayer.tgaTexture:Ljava/lang/String;
        //   683: aastore        
        //   684: dup            
        //   685: iconst_2       
        //   686: aload_0        
        //   687: getfield        com/lumiyaviewer/lumiya/slproto/baker/BakeLayer.writeAllChannels:Z
        //   690: invokestatic    java/lang/Boolean.valueOf:(Z)Ljava/lang/Boolean;
        //   693: aastore        
        //   694: dup            
        //   695: iconst_3       
        //   696: aload_2        
        //   697: invokevirtual   com/lumiyaviewer/lumiya/openjpeg/OpenJPEG.getWidth:()I
        //   700: invokestatic    java/lang/Integer.valueOf:(I)Ljava/lang/Integer;
        //   703: aastore        
        //   704: dup            
        //   705: iconst_4       
        //   706: aload_2        
        //   707: invokevirtual   com/lumiyaviewer/lumiya/openjpeg/OpenJPEG.getHeight:()I
        //   710: invokestatic    java/lang/Integer.valueOf:(I)Ljava/lang/Integer;
        //   713: aastore        
        //   714: dup            
        //   715: iconst_5       
        //   716: aload_2        
        //   717: invokevirtual   com/lumiyaviewer/lumiya/openjpeg/OpenJPEG.getNumComponents:()I
        //   720: invokestatic    java/lang/Integer.valueOf:(I)Ljava/lang/Integer;
        //   723: aastore        
        //   724: invokestatic    java/lang/String.format:(Ljava/lang/String;[Ljava/lang/Object;)Ljava/lang/String;
        //   727: invokestatic    com/lumiyaviewer/lumiya/Debug.Log:(Ljava/lang/String;)V
        //   730: iload           7
        //   732: istore_3       
        //   733: aload           5
        //   735: aload_2        
        //   736: iconst_m1      
        //   737: iconst_0       
        //   738: invokevirtual   com/lumiyaviewer/lumiya/openjpeg/OpenJPEG.draw:(Lcom/lumiyaviewer/lumiya/openjpeg/OpenJPEG;IZ)V
        //   741: iconst_1       
        //   742: istore_3       
        //   743: iconst_1       
        //   744: istore          7
        //   746: aload           9
        //   748: invokevirtual   java/io/InputStream.close:()V
        //   751: iload           7
        //   753: istore_3       
        //   754: iload_3        
        //   755: ifne            786
        //   758: aload           5
        //   760: iconst_0       
        //   761: iconst_m1      
        //   762: invokevirtual   com/lumiyaviewer/lumiya/openjpeg/OpenJPEG.setComponent:(IB)V
        //   765: aload           5
        //   767: iconst_1       
        //   768: iconst_m1      
        //   769: invokevirtual   com/lumiyaviewer/lumiya/openjpeg/OpenJPEG.setComponent:(IB)V
        //   772: aload           5
        //   774: iconst_2       
        //   775: iconst_m1      
        //   776: invokevirtual   com/lumiyaviewer/lumiya/openjpeg/OpenJPEG.setComponent:(IB)V
        //   779: aload           5
        //   781: iconst_3       
        //   782: iconst_m1      
        //   783: invokevirtual   com/lumiyaviewer/lumiya/openjpeg/OpenJPEG.setComponent:(IB)V
        //   786: aload           5
        //   788: aload           6
        //   790: iconst_0       
        //   791: invokevirtual   com/lumiyaviewer/lumiya/openjpeg/OpenJPEG.blendAlpha:(Lcom/lumiyaviewer/lumiya/openjpeg/OpenJPEG;Z)V
        //   794: iload           11
        //   796: ifne            820
        //   799: aload_0        
        //   800: getfield        com/lumiyaviewer/lumiya/slproto/baker/BakeLayer.isRenderPassBump:Z
        //   803: ifeq            859
        //   806: aload_1        
        //   807: aload           5
        //   809: iload           4
        //   811: aload_0        
        //   812: getfield        com/lumiyaviewer/lumiya/slproto/baker/BakeLayer.writeAllChannels:Z
        //   815: iload           8
        //   817: invokevirtual   com/lumiyaviewer/lumiya/openjpeg/OpenJPEG.drawBump:(Lcom/lumiyaviewer/lumiya/openjpeg/OpenJPEG;IZZ)V
        //   820: return         
        //   821: astore_2       
        //   822: iconst_0       
        //   823: istore          7
        //   825: ldc_w           "Baking: layer %s: default local texture"
        //   828: iconst_1       
        //   829: anewarray       Ljava/lang/Object;
        //   832: dup            
        //   833: iconst_0       
        //   834: aload_0        
        //   835: getfield        com/lumiyaviewer/lumiya/slproto/baker/BakeLayer.layerName:Ljava/lang/String;
        //   838: aastore        
        //   839: invokestatic    java/lang/String.format:(Ljava/lang/String;[Ljava/lang/Object;)Ljava/lang/String;
        //   842: invokestatic    com/lumiyaviewer/lumiya/Debug.Log:(Ljava/lang/String;)V
        //   845: iconst_1       
        //   846: istore          11
        //   848: goto            573
        //   851: astore_2       
        //   852: aload_2        
        //   853: invokevirtual   java/lang/Exception.printStackTrace:()V
        //   856: goto            754
        //   859: aload_1        
        //   860: aload           5
        //   862: iload           4
        //   864: aload_0        
        //   865: getfield        com/lumiyaviewer/lumiya/slproto/baker/BakeLayer.writeAllChannels:Z
        //   868: invokevirtual   com/lumiyaviewer/lumiya/openjpeg/OpenJPEG.draw:(Lcom/lumiyaviewer/lumiya/openjpeg/OpenJPEG;IZ)V
        //   871: goto            820
        //   874: astore_2       
        //   875: goto            825
        //   878: iconst_0       
        //   879: istore_3       
        //   880: iload           7
        //   882: istore          11
        //   884: iload_3        
        //   885: istore          7
        //   887: goto            563
        //   890: iconst_0       
        //   891: istore          7
        //   893: iload_3        
        //   894: istore          11
        //   896: goto            573
        //   899: goto            211
        //   902: goto            229
        //    Exceptions:
        //  Try           Handler
        //  Start  End    Start  End    Type                                                                       
        //  -----  -----  -----  -----  ---------------------------------------------------------------------------
        //  248    429    432    442    Ljava/lang/Exception;
        //  452    461    821    825    Lcom/lumiyaviewer/lumiya/slproto/baker/BakeProcess$DefaultTextureException;
        //  465    472    821    825    Lcom/lumiyaviewer/lumiya/slproto/baker/BakeProcess$DefaultTextureException;
        //  475    534    874    878    Lcom/lumiyaviewer/lumiya/slproto/baker/BakeProcess$DefaultTextureException;
        //  540    560    821    825    Lcom/lumiyaviewer/lumiya/slproto/baker/BakeProcess$DefaultTextureException;
        //  586    590    851    859    Ljava/lang/Exception;
        //  593    598    851    859    Ljava/lang/Exception;
        //  601    606    851    859    Ljava/lang/Exception;
        //  609    632    851    859    Ljava/lang/Exception;
        //  635    639    851    859    Ljava/lang/Exception;
        //  642    659    851    859    Ljava/lang/Exception;
        //  662    730    851    859    Ljava/lang/Exception;
        //  733    741    851    859    Ljava/lang/Exception;
        //  746    751    851    859    Ljava/lang/Exception;
        // 
        // The error that occurred was:
        // 
        // java.lang.IllegalStateException: Expression is linked from several locations: Label_0475:
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
    
    public void BakeAlpha(final OpenJPEG ex, BakeProcess bakeProcess) {
        if (this.isRenderPassBump) {
            return;
        }
        while (true) {
            if (this.tgaTexture == null) {
                break Label_0144;
            }
            try {
                final InputStream open = LumiyaApp.getAssetManager().open("tga/" + this.tgaTexture);
                final OpenJPEG openJPEG = new OpenJPEG(open, OpenJPEG.ImageFormat.TGA, this.tgaFileIsMask, false, 0.0f, 0.0f, false);
                Debug.Log(String.format("Baking: layer %s: applying tga alpha mask %swidth %d, height %d, num_comps %d", this.layerName, this.tgaTexture, openJPEG.getWidth(), openJPEG.getHeight(), openJPEG.getNumComponents()));
                ((OpenJPEG)ex).blendAlpha(openJPEG, false);
                open.close();
                if (this.localTexture != null) {
                    try {
                        bakeProcess = (BakeProcess)bakeProcess.getLocalTexture(this.localTexture);
                        if (bakeProcess != null) {
                            bakeProcess = (BakeProcess)((Iterable<Object>)bakeProcess).iterator();
                            while (((Iterator)bakeProcess).hasNext()) {
                                final OpenJPEG openJPEG2 = ((Iterator<OpenJPEG>)bakeProcess).next();
                                Debug.Log(String.format("Baking: layer %s: applying local texture alpha", this.layerName));
                                ((OpenJPEG)ex).blendAlpha(openJPEG2, false);
                            }
                        }
                    }
                    catch (final BakeProcess.DefaultTextureException ex) {
                        Debug.Log(String.format("Baking: layer %s: default local texture for alpha", this.layerName));
                    }
                }
            }
            catch (final Exception ex2) {
                ex2.printStackTrace();
                continue;
            }
            break;
        }
    }
}
