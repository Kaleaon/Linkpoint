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

/* loaded from: classes.dex */
public class BakeLayer {

    /* renamed from: -com-lumiyaviewer-lumiya-slproto-avatar-SLAvatarParamColor$ColorOperationSwitchesValues, reason: not valid java name */
    private static final /* synthetic */ int[] f64x335e3db9 = null;
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

    /* renamed from: -getcom-lumiyaviewer-lumiya-slproto-avatar-SLAvatarParamColor$ColorOperationSwitchesValues, reason: not valid java name */
    private static /* synthetic */ int[] m148x403ad495() {
        if (f64x335e3db9 != null) {
            return f64x335e3db9;
        }
        int[] iArr = new int[SLAvatarParamColor.ColorOperation.valuesCustom().length];
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
        f64x335e3db9 = iArr;
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
            Debug.Log(String.format("Baking: lipstick start color %08x default %08x", Integer.valueOf(i), Integer.valueOf(i2)));
        }
        int length = iArr.length;
        int i3 = 0;
        int i4 = i;
        while (i3 < length) {
            int i5 = iArr[i3];
            SLAvatarParams.ParamSet paramSet = SLAvatarParams.paramByIDs.get(Integer.valueOf(i5));
            if (paramSet != null && (sLAvatarParamColor = (avatarParam = paramSet.params.get(0)).paramColor) != null) {
                z = true;
                float paramWeight = bakeProcess.getParamWeight(i5, avatarParam);
                int color = sLAvatarParamColor.getColor(paramWeight);
                if (this.layerName.equals("lipstick")) {
                    Debug.Log(String.format("Baking: lipstick color param weight %ff color %08x", Float.valueOf(paramWeight), Integer.valueOf(color)));
                }
                switch (m148x403ad495()[sLAvatarParamColor.colorOperation.ordinal()]) {
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
                    Debug.Log(String.format("Baking: after op, lipstick color result %08x", Integer.valueOf(colorAdd)));
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
            if (paramSet != null && paramSet.params.get(0).paramColor != null) {
                z = true;
                break;
            }
            i++;
        }
        if (z) {
            int colorByParamList = this.globalColor != null ? getColorByParamList(bakeProcess, this.globalColor.getParamIDs(), 0, 0) : this.hasFixedColor ? this.fixedColor : 0;
            return getColorByParamList(bakeProcess, this.paramIDs, colorByParamList, colorByParamList);
        }
        if (this.globalColor != null) {
            return getColorByParamList(bakeProcess, this.globalColor.getParamIDs(), 0, 0);
        }
        if (this.hasFixedColor) {
            return this.fixedColor;
        }
        return -1;
    }

    /* JADX WARN: Removed duplicated region for block: B:41:0x0229  */
    /* JADX WARN: Removed duplicated region for block: B:44:0x0243  */
    /* JADX WARN: Removed duplicated region for block: B:51:? A[RETURN, SYNTHETIC] */
    /* JADX WARN: Removed duplicated region for block: B:52:0x01a8 A[EXC_TOP_SPLITTER, SYNTHETIC] */
    /*
        Code decompiled incorrectly, please refer to instructions dump.
        To view partially-correct add '--show-bad-code' argument
    */
    public void Bake(com.lumiyaviewer.lumiya.openjpeg.OpenJPEG r21, com.lumiyaviewer.lumiya.slproto.baker.BakeProcess r22) {
        /*
            Method dump skipped, instructions count: 655
            To view this dump add '--comments-level debug' option
        */
        throw new UnsupportedOperationException("Method not decompiled: com.lumiyaviewer.lumiya.slproto.baker.BakeLayer.Bake(com.lumiyaviewer.lumiya.openjpeg.OpenJPEG, com.lumiyaviewer.lumiya.slproto.baker.BakeProcess):void");
    }

    public void BakeAlpha(OpenJPEG openJPEG, BakeProcess bakeProcess) {
        if (this.isRenderPassBump) {
            return;
        }
        if (this.tgaTexture != null) {
            try {
                InputStream open = LumiyaApp.getAssetManager().open("tga/" + this.tgaTexture);
                OpenJPEG openJPEG2 = new OpenJPEG(open, OpenJPEG.ImageFormat.TGA, this.tgaFileIsMask, false, 0.0f, 0.0f, false);
                Debug.Log(String.format("Baking: layer %s: applying tga alpha mask %swidth %d, height %d, num_comps %d", this.layerName, this.tgaTexture, Integer.valueOf(openJPEG2.getWidth()), Integer.valueOf(openJPEG2.getHeight()), Integer.valueOf(openJPEG2.getNumComponents())));
                openJPEG.blendAlpha(openJPEG2, false);
                open.close();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        if (this.localTexture != null) {
            try {
                List<OpenJPEG> localTexture = bakeProcess.getLocalTexture(this.localTexture);
                if (localTexture != null) {
                    for (OpenJPEG openJPEG3 : localTexture) {
                        Debug.Log(String.format("Baking: layer %s: applying local texture alpha", this.layerName));
                        openJPEG.blendAlpha(openJPEG3, false);
                    }
                }
            } catch (BakeProcess.DefaultTextureException e2) {
                Debug.Log(String.format("Baking: layer %s: default local texture for alpha", this.layerName));
            }
        }
    }
}
