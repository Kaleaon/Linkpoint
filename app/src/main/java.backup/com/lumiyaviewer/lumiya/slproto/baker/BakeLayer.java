package com.lumiyaviewer.lumiya.slproto.baker;

import com.lumiyaviewer.lumiya.Debug;
import com.lumiyaviewer.lumiya.LumiyaApp;
import com.lumiyaviewer.lumiya.openjpeg.OpenJPEG;
import com.lumiyaviewer.lumiya.slproto.avatar.AvatarTextureFaceIndex;
import com.lumiyaviewer.lumiya.slproto.avatar.SLAvatarParamColor;
import com.lumiyaviewer.lumiya.slproto.avatar.SLAvatarParams;
import com.lumiyaviewer.lumiya.slproto.baker.BakeProcess;
import java.io.InputStream;
import java.util.List;

public class BakeLayer {

    /* renamed from: -com-lumiyaviewer-lumiya-slproto-avatar-SLAvatarParamColor$ColorOperationSwitchesValues  reason: not valid java name */
    private static final /* synthetic */ int[] f64comlumiyaviewerlumiyaslprotoavatarSLAvatarParamColor$ColorOperationSwitchesValues = null;
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

    /* renamed from: -getcom-lumiyaviewer-lumiya-slproto-avatar-SLAvatarParamColor$ColorOperationSwitchesValues  reason: not valid java name */
    private static /* synthetic */ int[] m138getcomlumiyaviewerlumiyaslprotoavatarSLAvatarParamColor$ColorOperationSwitchesValues() {
        if (f64comlumiyaviewerlumiyaslprotoavatarSLAvatarParamColor$ColorOperationSwitchesValues != null) {
            return f64comlumiyaviewerlumiyaslprotoavatarSLAvatarParamColor$ColorOperationSwitchesValues;
        }
        int[] iArr = new int[SLAvatarParamColor.ColorOperation.values().length];
        try {
            iArr[SLAvatarParamColor.ColorOperation.Blend.ordinal()] = 1;
        } catch (NoSuchFieldError e) {
        }
        try {
            iArr[SLAvatarParamColor.ColorOperation.Default.ordinal()] = 2;
        } catch (NoSuchFieldError e2) {
        }
        try {
            iArr[SLAvatarParamColor.ColorOperation.Multiply.ordinal()] = 3;
        } catch (NoSuchFieldError e3) {
        }
        f64comlumiyaviewerlumiyaslprotoavatarSLAvatarParamColor$ColorOperationSwitchesValues = iArr;
        return iArr;
    }

    public BakeLayer(String str, SLAvatarGlobalColor sLAvatarGlobalColor, boolean z, int i, boolean z2, boolean z3, boolean z4, AvatarTextureFaceIndex avatarTextureFaceIndex, boolean z5, String str2, boolean z6, int[] iArr) {
        this.layerName = str;
        this.globalColor = sLAvatarGlobalColor;
        this.hasFixedColor = z;
        this.fixedColor = i;
        this.isRenderPassBump = z2;
        this.visibilityMask = z3;
        this.writeAllChannels = z4;
        this.localTexture = avatarTextureFaceIndex;
        this.localTextureAlphaOnly = z5;
        this.tgaTexture = str2;
        this.tgaFileIsMask = z6;
        this.paramIDs = iArr;
    }

    private int getColorByParamList(BakeProcess bakeProcess, int[] iArr, int i, int i2) {
        SLAvatarParams.AvatarParam avatarParam;
        SLAvatarParamColor sLAvatarParamColor;
        int colorAdd;
        boolean z = false;
        if (this.layerName.equals("lipstick")) {
            Debug.Log(String.format("Baking: lipstick start color %08x default %08x", new Object[]{Integer.valueOf(i), Integer.valueOf(i2)}));
        }
        int length = iArr.length;
        int i3 = 0;
        int i4 = i;
        while (i3 < length) {
            int i5 = iArr[i3];
            SLAvatarParams.ParamSet paramSet = SLAvatarParams.paramByIDs.get(Integer.valueOf(i5));
            if (!(paramSet == null || (sLAvatarParamColor = avatarParam.paramColor) == null)) {
                z = true;
                float paramWeight = bakeProcess.getParamWeight(i5, (avatarParam = (SLAvatarParams.AvatarParam) paramSet.params.get(0)));
                int color = sLAvatarParamColor.getColor(paramWeight);
                if (this.layerName.equals("lipstick")) {
                    Debug.Log(String.format("Baking: lipstick color param weight %ff color %08x", new Object[]{Float.valueOf(paramWeight), Integer.valueOf(color)}));
                }
                switch (m138getcomlumiyaviewerlumiyaslprotoavatarSLAvatarParamColor$ColorOperationSwitchesValues()[sLAvatarParamColor.colorOperation.ordinal()]) {
                    case 1:
                        colorAdd = SLAvatarParamColor.colorLerp(i4, color, paramWeight);
                        break;
                    case 2:
                        colorAdd = SLAvatarParamColor.colorAdd(i4, color);
                        break;
                    case 3:
                        colorAdd = SLAvatarParamColor.colorMult(i4, color);
                        break;
                    default:
                        colorAdd = i4;
                        break;
                }
                if (this.layerName.equals("lipstick")) {
                    Debug.Log(String.format("Baking: after op, lipstick color result %08x", new Object[]{Integer.valueOf(colorAdd)}));
                    i4 = colorAdd;
                } else {
                    i4 = colorAdd;
                }
            }
            i3++;
            z = z;
        }
        return !z ? i2 : i4;
    }

    private int getNetColor(BakeProcess bakeProcess) {
        boolean z;
        int[] iArr = this.paramIDs;
        int length = iArr.length;
        int i = 0;
        while (true) {
            if (i >= length) {
                z = false;
                break;
            }
            SLAvatarParams.ParamSet paramSet = SLAvatarParams.paramByIDs.get(Integer.valueOf(iArr[i]));
            if (paramSet != null && ((SLAvatarParams.AvatarParam) paramSet.params.get(0)).paramColor != null) {
                z = true;
                break;
            }
            i++;
        }
        if (z) {
            int colorByParamList = this.globalColor != null ? getColorByParamList(bakeProcess, this.globalColor.getParamIDs(), 0, 0) : this.hasFixedColor ? this.fixedColor : 0;
            return getColorByParamList(bakeProcess, this.paramIDs, colorByParamList, colorByParamList);
        } else if (this.globalColor != null) {
            return getColorByParamList(bakeProcess, this.globalColor.getParamIDs(), 0, 0);
        } else {
            if (this.hasFixedColor) {
                return this.fixedColor;
            }
            return -1;
        }
    }

    /* JADX WARNING: Removed duplicated region for block: B:46:0x01a8 A[SYNTHETIC, Splitter:B:46:0x01a8] */
    /* JADX WARNING: Removed duplicated region for block: B:49:0x0229  */
    /* JADX WARNING: Removed duplicated region for block: B:52:0x0243  */
    /* JADX WARNING: Removed duplicated region for block: B:73:? A[RETURN, SYNTHETIC] */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public void Bake(com.lumiyaviewer.lumiya.openjpeg.OpenJPEG r21, com.lumiyaviewer.lumiya.slproto.baker.BakeProcess r22) {
        /*
            r20 = this;
            r15 = 0
            r0 = r20
            r1 = r22
            int r17 = r0.getNetColor(r1)
            com.lumiyaviewer.lumiya.openjpeg.OpenJPEG r2 = new com.lumiyaviewer.lumiya.openjpeg.OpenJPEG
            r0 = r21
            int r3 = r0.width
            r0 = r21
            int r4 = r0.height
            r5 = 4
            r6 = 4
            r7 = 0
            r8 = 0
            r2.<init>(r3, r4, r5, r6, r7, r8)
            com.lumiyaviewer.lumiya.openjpeg.OpenJPEG r3 = new com.lumiyaviewer.lumiya.openjpeg.OpenJPEG
            r0 = r21
            int r4 = r0.width
            r0 = r21
            int r5 = r0.height
            r6 = 4
            r7 = 4
            r8 = 0
            r9 = -16777216(0xffffffffff000000, float:-1.7014118E38)
            r3.<init>(r4, r5, r6, r7, r8, r9)
            java.lang.String r4 = "Baking: layer %s net_color 0x%08x."
            r5 = 2
            java.lang.Object[] r5 = new java.lang.Object[r5]
            r0 = r20
            java.lang.String r6 = r0.layerName
            r7 = 0
            r5[r7] = r6
            java.lang.Integer r6 = java.lang.Integer.valueOf(r17)
            r7 = 1
            r5[r7] = r6
            java.lang.String r4 = java.lang.String.format(r4, r5)
            com.lumiyaviewer.lumiya.Debug.Log(r4)
            r5 = 1
            r13 = 0
            r0 = r20
            int[] r0 = r0.paramIDs
            r18 = r0
            r4 = 0
            r0 = r18
            int r0 = r0.length
            r19 = r0
            r16 = r4
        L_0x0057:
            r0 = r16
            r1 = r19
            if (r0 >= r1) goto L_0x0141
            r6 = r18[r16]
            com.google.common.collect.ImmutableMap<java.lang.Integer, com.lumiyaviewer.lumiya.slproto.avatar.SLAvatarParams$ParamSet> r4 = com.lumiyaviewer.lumiya.slproto.avatar.SLAvatarParams.paramByIDs
            java.lang.Integer r7 = java.lang.Integer.valueOf(r6)
            java.lang.Object r4 = r4.get(r7)
            com.lumiyaviewer.lumiya.slproto.avatar.SLAvatarParams$ParamSet r4 = (com.lumiyaviewer.lumiya.slproto.avatar.SLAvatarParams.ParamSet) r4
            if (r4 == 0) goto L_0x028c
            com.google.common.collect.ImmutableList<com.lumiyaviewer.lumiya.slproto.avatar.SLAvatarParams$AvatarParam> r4 = r4.params
            r7 = 0
            java.lang.Object r4 = r4.get(r7)
            r12 = r4
            com.lumiyaviewer.lumiya.slproto.avatar.SLAvatarParams$AvatarParam r12 = (com.lumiyaviewer.lumiya.slproto.avatar.SLAvatarParams.AvatarParam) r12
            com.lumiyaviewer.lumiya.slproto.avatar.SLAvatarParamAlpha r4 = r12.paramAlpha
            if (r4 == 0) goto L_0x028c
            com.lumiyaviewer.lumiya.slproto.avatar.SLAvatarParamAlpha r4 = r12.paramAlpha
            java.lang.String r4 = r4.tgaFile
            if (r4 == 0) goto L_0x028c
            r0 = r22
            float r10 = r0.getParamWeight(r6, r12)
            if (r5 == 0) goto L_0x0289
            r14 = 0
            com.lumiyaviewer.lumiya.slproto.avatar.SLAvatarParamAlpha r4 = r12.paramAlpha
            boolean r4 = r4.multiplyBlend
            if (r4 != 0) goto L_0x0095
            r4 = 3
            r5 = 0
            r3.setComponent(r4, r5)
        L_0x0095:
            r4 = 0
            int r4 = (r10 > r4 ? 1 : (r10 == r4 ? 0 : -1))
            if (r4 != 0) goto L_0x00a8
            com.lumiyaviewer.lumiya.slproto.avatar.SLAvatarParamAlpha r4 = r12.paramAlpha
            boolean r4 = r4.skipIfZero
            if (r4 == 0) goto L_0x00a8
            r4 = r13
            r5 = r14
        L_0x00a2:
            int r6 = r16 + 1
            r16 = r6
            r13 = r4
            goto L_0x0057
        L_0x00a8:
            com.lumiyaviewer.lumiya.slproto.avatar.SLAvatarParamAlpha r4 = r12.paramAlpha
            boolean r4 = r4.multiplyBlend
            r13 = r13 | r4
            android.content.res.AssetManager r4 = com.lumiyaviewer.lumiya.LumiyaApp.getAssetManager()     // Catch:{ Exception -> 0x0139 }
            java.lang.StringBuilder r5 = new java.lang.StringBuilder     // Catch:{ Exception -> 0x0139 }
            r5.<init>()     // Catch:{ Exception -> 0x0139 }
            java.lang.String r6 = "tga/"
            java.lang.StringBuilder r5 = r5.append(r6)     // Catch:{ Exception -> 0x0139 }
            com.lumiyaviewer.lumiya.slproto.avatar.SLAvatarParamAlpha r6 = r12.paramAlpha     // Catch:{ Exception -> 0x0139 }
            java.lang.String r6 = r6.tgaFile     // Catch:{ Exception -> 0x0139 }
            java.lang.StringBuilder r5 = r5.append(r6)     // Catch:{ Exception -> 0x0139 }
            java.lang.String r5 = r5.toString()     // Catch:{ Exception -> 0x0139 }
            java.io.InputStream r5 = r4.open(r5)     // Catch:{ Exception -> 0x0139 }
            com.lumiyaviewer.lumiya.openjpeg.OpenJPEG r4 = new com.lumiyaviewer.lumiya.openjpeg.OpenJPEG     // Catch:{ Exception -> 0x0139 }
            com.lumiyaviewer.lumiya.openjpeg.OpenJPEG$ImageFormat r6 = com.lumiyaviewer.lumiya.openjpeg.OpenJPEG.ImageFormat.TGA     // Catch:{ Exception -> 0x0139 }
            com.lumiyaviewer.lumiya.slproto.avatar.SLAvatarParamAlpha r7 = r12.paramAlpha     // Catch:{ Exception -> 0x0139 }
            float r9 = r7.domain     // Catch:{ Exception -> 0x0139 }
            r7 = 1
            r8 = 1
            r11 = 0
            r4.<init>(r5, r6, r7, r8, r9, r10, r11)     // Catch:{ Exception -> 0x0139 }
            java.lang.String r6 = "Baking: layer %s: applying alpha (weight %f domain %f) mask texture %s, width %d, height %d, num_comps %d"
            r7 = 7
            java.lang.Object[] r7 = new java.lang.Object[r7]     // Catch:{ Exception -> 0x0139 }
            r0 = r20
            java.lang.String r8 = r0.layerName     // Catch:{ Exception -> 0x0139 }
            r9 = 0
            r7[r9] = r8     // Catch:{ Exception -> 0x0139 }
            java.lang.Float r8 = java.lang.Float.valueOf(r10)     // Catch:{ Exception -> 0x0139 }
            r9 = 1
            r7[r9] = r8     // Catch:{ Exception -> 0x0139 }
            com.lumiyaviewer.lumiya.slproto.avatar.SLAvatarParamAlpha r8 = r12.paramAlpha     // Catch:{ Exception -> 0x0139 }
            float r8 = r8.domain     // Catch:{ Exception -> 0x0139 }
            java.lang.Float r8 = java.lang.Float.valueOf(r8)     // Catch:{ Exception -> 0x0139 }
            r9 = 2
            r7[r9] = r8     // Catch:{ Exception -> 0x0139 }
            com.lumiyaviewer.lumiya.slproto.avatar.SLAvatarParamAlpha r8 = r12.paramAlpha     // Catch:{ Exception -> 0x0139 }
            java.lang.String r8 = r8.tgaFile     // Catch:{ Exception -> 0x0139 }
            r9 = 3
            r7[r9] = r8     // Catch:{ Exception -> 0x0139 }
            int r8 = r4.getWidth()     // Catch:{ Exception -> 0x0139 }
            java.lang.Integer r8 = java.lang.Integer.valueOf(r8)     // Catch:{ Exception -> 0x0139 }
            r9 = 4
            r7[r9] = r8     // Catch:{ Exception -> 0x0139 }
            int r8 = r4.getHeight()     // Catch:{ Exception -> 0x0139 }
            java.lang.Integer r8 = java.lang.Integer.valueOf(r8)     // Catch:{ Exception -> 0x0139 }
            r9 = 5
            r7[r9] = r8     // Catch:{ Exception -> 0x0139 }
            int r8 = r4.getNumComponents()     // Catch:{ Exception -> 0x0139 }
            java.lang.Integer r8 = java.lang.Integer.valueOf(r8)     // Catch:{ Exception -> 0x0139 }
            r9 = 6
            r7[r9] = r8     // Catch:{ Exception -> 0x0139 }
            java.lang.String r6 = java.lang.String.format(r6, r7)     // Catch:{ Exception -> 0x0139 }
            com.lumiyaviewer.lumiya.Debug.Log(r6)     // Catch:{ Exception -> 0x0139 }
            com.lumiyaviewer.lumiya.slproto.avatar.SLAvatarParamAlpha r6 = r12.paramAlpha     // Catch:{ Exception -> 0x0139 }
            boolean r6 = r6.multiplyBlend     // Catch:{ Exception -> 0x0139 }
            r6 = r6 ^ 1
            r3.blendAlpha(r4, r6)     // Catch:{ Exception -> 0x0139 }
            r5.close()     // Catch:{ Exception -> 0x0139 }
            r4 = r13
            r5 = r14
            goto L_0x00a2
        L_0x0139:
            r4 = move-exception
            r4.printStackTrace()
            r4 = r13
            r5 = r14
            goto L_0x00a2
        L_0x0141:
            r4 = 0
            r0 = r20
            com.lumiyaviewer.lumiya.slproto.avatar.AvatarTextureFaceIndex r5 = r0.localTexture
            if (r5 == 0) goto L_0x0286
            r0 = r20
            com.lumiyaviewer.lumiya.slproto.avatar.AvatarTextureFaceIndex r5 = r0.localTexture     // Catch:{ DefaultTextureException -> 0x0255 }
            r0 = r22
            java.util.List r5 = r0.getLocalTexture(r5)     // Catch:{ DefaultTextureException -> 0x0255 }
            if (r5 == 0) goto L_0x018b
            java.util.Iterator r6 = r5.iterator()     // Catch:{ DefaultTextureException -> 0x0255 }
            r5 = r4
        L_0x0159:
            boolean r4 = r6.hasNext()     // Catch:{ DefaultTextureException -> 0x0280 }
            if (r4 == 0) goto L_0x0282
            java.lang.Object r4 = r6.next()     // Catch:{ DefaultTextureException -> 0x0280 }
            com.lumiyaviewer.lumiya.openjpeg.OpenJPEG r4 = (com.lumiyaviewer.lumiya.openjpeg.OpenJPEG) r4     // Catch:{ DefaultTextureException -> 0x0280 }
            java.lang.String r7 = "Baking: layer %s: applying local texture, writeAllChannels %s"
            r8 = 2
            java.lang.Object[] r8 = new java.lang.Object[r8]     // Catch:{ DefaultTextureException -> 0x0280 }
            r0 = r20
            java.lang.String r9 = r0.layerName     // Catch:{ DefaultTextureException -> 0x0280 }
            r10 = 0
            r8[r10] = r9     // Catch:{ DefaultTextureException -> 0x0280 }
            r0 = r20
            boolean r9 = r0.writeAllChannels     // Catch:{ DefaultTextureException -> 0x0280 }
            java.lang.Boolean r9 = java.lang.Boolean.valueOf(r9)     // Catch:{ DefaultTextureException -> 0x0280 }
            r10 = 1
            r8[r10] = r9     // Catch:{ DefaultTextureException -> 0x0280 }
            java.lang.String r7 = java.lang.String.format(r7, r8)     // Catch:{ DefaultTextureException -> 0x0280 }
            com.lumiyaviewer.lumiya.Debug.Log(r7)     // Catch:{ DefaultTextureException -> 0x0280 }
            r7 = -1
            r8 = 0
            r2.draw(r4, r7, r8)     // Catch:{ DefaultTextureException -> 0x0280 }
            r5 = 1
            goto L_0x0159
        L_0x018b:
            java.lang.String r5 = "Baking: layer %s: missing local texture"
            r6 = 1
            java.lang.Object[] r6 = new java.lang.Object[r6]     // Catch:{ DefaultTextureException -> 0x0255 }
            r0 = r20
            java.lang.String r7 = r0.layerName     // Catch:{ DefaultTextureException -> 0x0255 }
            r8 = 0
            r6[r8] = r7     // Catch:{ DefaultTextureException -> 0x0255 }
            java.lang.String r5 = java.lang.String.format(r5, r6)     // Catch:{ DefaultTextureException -> 0x0255 }
            com.lumiyaviewer.lumiya.Debug.Log(r5)     // Catch:{ DefaultTextureException -> 0x0255 }
            r5 = 1
        L_0x01a0:
            r12 = r4
            r15 = r5
        L_0x01a2:
            r0 = r20
            java.lang.String r4 = r0.tgaTexture
            if (r4 == 0) goto L_0x0227
            android.content.res.AssetManager r4 = com.lumiyaviewer.lumiya.LumiyaApp.getAssetManager()     // Catch:{ Exception -> 0x026f }
            java.lang.StringBuilder r5 = new java.lang.StringBuilder     // Catch:{ Exception -> 0x026f }
            r5.<init>()     // Catch:{ Exception -> 0x026f }
            java.lang.String r6 = "tga/"
            java.lang.StringBuilder r5 = r5.append(r6)     // Catch:{ Exception -> 0x026f }
            r0 = r20
            java.lang.String r6 = r0.tgaTexture     // Catch:{ Exception -> 0x026f }
            java.lang.StringBuilder r5 = r5.append(r6)     // Catch:{ Exception -> 0x026f }
            java.lang.String r5 = r5.toString()     // Catch:{ Exception -> 0x026f }
            java.io.InputStream r5 = r4.open(r5)     // Catch:{ Exception -> 0x026f }
            com.lumiyaviewer.lumiya.openjpeg.OpenJPEG r4 = new com.lumiyaviewer.lumiya.openjpeg.OpenJPEG     // Catch:{ Exception -> 0x026f }
            com.lumiyaviewer.lumiya.openjpeg.OpenJPEG$ImageFormat r6 = com.lumiyaviewer.lumiya.openjpeg.OpenJPEG.ImageFormat.TGA     // Catch:{ Exception -> 0x026f }
            r0 = r20
            boolean r7 = r0.tgaFileIsMask     // Catch:{ Exception -> 0x026f }
            r8 = 0
            r9 = 0
            r10 = 0
            r11 = 0
            r4.<init>(r5, r6, r7, r8, r9, r10, r11)     // Catch:{ Exception -> 0x026f }
            java.lang.String r6 = "Baking: layer %s: applying tga texture %s, writeAllChannels %s, width %d, height %d, num_comps %d"
            r7 = 6
            java.lang.Object[] r7 = new java.lang.Object[r7]     // Catch:{ Exception -> 0x026f }
            r0 = r20
            java.lang.String r8 = r0.layerName     // Catch:{ Exception -> 0x026f }
            r9 = 0
            r7[r9] = r8     // Catch:{ Exception -> 0x026f }
            r0 = r20
            java.lang.String r8 = r0.tgaTexture     // Catch:{ Exception -> 0x026f }
            r9 = 1
            r7[r9] = r8     // Catch:{ Exception -> 0x026f }
            r0 = r20
            boolean r8 = r0.writeAllChannels     // Catch:{ Exception -> 0x026f }
            java.lang.Boolean r8 = java.lang.Boolean.valueOf(r8)     // Catch:{ Exception -> 0x026f }
            r9 = 2
            r7[r9] = r8     // Catch:{ Exception -> 0x026f }
            int r8 = r4.getWidth()     // Catch:{ Exception -> 0x026f }
            java.lang.Integer r8 = java.lang.Integer.valueOf(r8)     // Catch:{ Exception -> 0x026f }
            r9 = 3
            r7[r9] = r8     // Catch:{ Exception -> 0x026f }
            int r8 = r4.getHeight()     // Catch:{ Exception -> 0x026f }
            java.lang.Integer r8 = java.lang.Integer.valueOf(r8)     // Catch:{ Exception -> 0x026f }
            r9 = 4
            r7[r9] = r8     // Catch:{ Exception -> 0x026f }
            int r8 = r4.getNumComponents()     // Catch:{ Exception -> 0x026f }
            java.lang.Integer r8 = java.lang.Integer.valueOf(r8)     // Catch:{ Exception -> 0x026f }
            r9 = 5
            r7[r9] = r8     // Catch:{ Exception -> 0x026f }
            java.lang.String r6 = java.lang.String.format(r6, r7)     // Catch:{ Exception -> 0x026f }
            com.lumiyaviewer.lumiya.Debug.Log(r6)     // Catch:{ Exception -> 0x026f }
            r6 = -1
            r7 = 0
            r2.draw(r4, r6, r7)     // Catch:{ Exception -> 0x026f }
            r12 = 1
            r5.close()     // Catch:{ Exception -> 0x026f }
        L_0x0227:
            if (r12 != 0) goto L_0x023d
            r4 = 0
            r5 = -1
            r2.setComponent(r4, r5)
            r4 = 1
            r5 = -1
            r2.setComponent(r4, r5)
            r4 = 2
            r5 = -1
            r2.setComponent(r4, r5)
            r4 = 3
            r5 = -1
            r2.setComponent(r4, r5)
        L_0x023d:
            r4 = 0
            r2.blendAlpha(r3, r4)
            if (r15 != 0) goto L_0x0254
            r0 = r20
            boolean r3 = r0.isRenderPassBump
            if (r3 == 0) goto L_0x0274
            r0 = r20
            boolean r3 = r0.writeAllChannels
            r0 = r21
            r1 = r17
            r0.drawBump(r2, r1, r3, r13)
        L_0x0254:
            return
        L_0x0255:
            r5 = move-exception
            r5 = r4
        L_0x0257:
            java.lang.String r4 = "Baking: layer %s: default local texture"
            r6 = 1
            java.lang.Object[] r6 = new java.lang.Object[r6]
            r0 = r20
            java.lang.String r7 = r0.layerName
            r8 = 0
            r6[r8] = r7
            java.lang.String r4 = java.lang.String.format(r4, r6)
            com.lumiyaviewer.lumiya.Debug.Log(r4)
            r15 = 1
            r12 = r5
            goto L_0x01a2
        L_0x026f:
            r4 = move-exception
            r4.printStackTrace()
            goto L_0x0227
        L_0x0274:
            r0 = r20
            boolean r3 = r0.writeAllChannels
            r0 = r21
            r1 = r17
            r0.draw(r2, r1, r3)
            goto L_0x0254
        L_0x0280:
            r4 = move-exception
            goto L_0x0257
        L_0x0282:
            r4 = r5
            r5 = r15
            goto L_0x01a0
        L_0x0286:
            r12 = r4
            goto L_0x01a2
        L_0x0289:
            r14 = r5
            goto L_0x0095
        L_0x028c:
            r4 = r13
            goto L_0x00a2
        */
        throw new UnsupportedOperationException("Method not decompiled: com.lumiyaviewer.lumiya.slproto.baker.BakeLayer.Bake(com.lumiyaviewer.lumiya.openjpeg.OpenJPEG, com.lumiyaviewer.lumiya.slproto.baker.BakeProcess):void");
    }

    public void BakeAlpha(OpenJPEG openJPEG, BakeProcess bakeProcess) {
        if (!this.isRenderPassBump) {
            if (this.tgaTexture != null) {
                try {
                    InputStream open = LumiyaApp.getAssetManager().open("tga/" + this.tgaTexture);
                    OpenJPEG openJPEG2 = new OpenJPEG(open, OpenJPEG.ImageFormat.TGA, this.tgaFileIsMask, false, 0.0f, 0.0f, false);
                    Debug.Log(String.format("Baking: layer %s: applying tga alpha mask %swidth %d, height %d, num_comps %d", new Object[]{this.layerName, this.tgaTexture, Integer.valueOf(openJPEG2.getWidth()), Integer.valueOf(openJPEG2.getHeight()), Integer.valueOf(openJPEG2.getNumComponents())}));
                    openJPEG.blendAlpha(openJPEG2, false);
                    open.close();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
            if (this.localTexture != null) {
                try {
                    List<OpenJPEG> localTexture2 = bakeProcess.getLocalTexture(this.localTexture);
                    if (localTexture2 != null) {
                        for (OpenJPEG blendAlpha : localTexture2) {
                            Debug.Log(String.format("Baking: layer %s: applying local texture alpha", new Object[]{this.layerName}));
                            openJPEG.blendAlpha(blendAlpha, false);
                        }
                    }
                } catch (BakeProcess.DefaultTextureException e2) {
                    Debug.Log(String.format("Baking: layer %s: default local texture for alpha", new Object[]{this.layerName}));
                }
            }
        }
    }
}
