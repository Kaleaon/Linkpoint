// Decompiled by Jad v1.5.8e. Copyright 2001 Pavel Kouznetsov.
// Jad home page: http://www.geocities.com/kpdus/jad.html
// Decompiler options: braces fieldsfirst space lnc 

package com.lumiyaviewer.lumiya.slproto.baker;

import android.content.res.AssetManager;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import com.lumiyaviewer.lumiya.Debug;
import com.lumiyaviewer.lumiya.LumiyaApp;
import com.lumiyaviewer.lumiya.openjpeg.OpenJPEG;
import com.lumiyaviewer.lumiya.slproto.avatar.AvatarTextureFaceIndex;
import com.lumiyaviewer.lumiya.slproto.avatar.SLAvatarParamAlpha;
import com.lumiyaviewer.lumiya.slproto.avatar.SLAvatarParamColor;
import com.lumiyaviewer.lumiya.slproto.avatar.SLAvatarParams;
import java.io.InputStream;
import java.util.Iterator;

// Referenced classes of package com.lumiyaviewer.lumiya.slproto.baker:
//            BakeProcess, SLAvatarGlobalColor

public class BakeLayer
{

    private static final int _2D_com_2D_lumiyaviewer_2D_lumiya_2D_slproto_2D_avatar_2D_SLAvatarParamColor$ColorOperationSwitchesValues[];
    public int fixedColor;
    public SLAvatarGlobalColor globalColor;
    public boolean hasFixedColor;
    public boolean isRenderPassBump;
    public String layerName;
    public AvatarTextureFaceIndex localTexture;
    public boolean localTextureAlphaOnly;
    public int paramIDs[];
    public boolean tgaFileIsMask;
    public String tgaTexture;
    public boolean visibilityMask;
    public boolean writeAllChannels;

    private static int[] _2D_getcom_2D_lumiyaviewer_2D_lumiya_2D_slproto_2D_avatar_2D_SLAvatarParamColor$ColorOperationSwitchesValues()
    {
        if (_2D_com_2D_lumiyaviewer_2D_lumiya_2D_slproto_2D_avatar_2D_SLAvatarParamColor$ColorOperationSwitchesValues != null)
        {
            return _2D_com_2D_lumiyaviewer_2D_lumiya_2D_slproto_2D_avatar_2D_SLAvatarParamColor$ColorOperationSwitchesValues;
        }
        int ai[] = new int[com.lumiyaviewer.lumiya.slproto.avatar.SLAvatarParamColor.ColorOperation.values().length];
        try
        {
            ai[com.lumiyaviewer.lumiya.slproto.avatar.SLAvatarParamColor.ColorOperation.Blend.ordinal()] = 1;
        }
        catch (NoSuchFieldError nosuchfielderror2) { }
        try
        {
            ai[com.lumiyaviewer.lumiya.slproto.avatar.SLAvatarParamColor.ColorOperation.Default.ordinal()] = 2;
        }
        catch (NoSuchFieldError nosuchfielderror1) { }
        try
        {
            ai[com.lumiyaviewer.lumiya.slproto.avatar.SLAvatarParamColor.ColorOperation.Multiply.ordinal()] = 3;
        }
        catch (NoSuchFieldError nosuchfielderror) { }
        _2D_com_2D_lumiyaviewer_2D_lumiya_2D_slproto_2D_avatar_2D_SLAvatarParamColor$ColorOperationSwitchesValues = ai;
        return ai;
    }

    public BakeLayer(String s, SLAvatarGlobalColor slavatarglobalcolor, boolean flag, int i, boolean flag1, boolean flag2, boolean flag3, 
            AvatarTextureFaceIndex avatartexturefaceindex, boolean flag4, String s1, boolean flag5, int ai[])
    {
        layerName = s;
        globalColor = slavatarglobalcolor;
        hasFixedColor = flag;
        fixedColor = i;
        isRenderPassBump = flag1;
        visibilityMask = flag2;
        writeAllChannels = flag3;
        localTexture = avatartexturefaceindex;
        localTextureAlphaOnly = flag4;
        tgaTexture = s1;
        tgaFileIsMask = flag5;
        paramIDs = ai;
    }

    private int getColorByParamList(BakeProcess bakeprocess, int ai[], int i, int j)
    {
        int k;
        int l;
        int j1;
        boolean flag = false;
        if (layerName.equals("lipstick"))
        {
            Debug.Log(String.format("Baking: lipstick start color %08x default %08x", new Object[] {
                Integer.valueOf(i), Integer.valueOf(j)
            }));
        }
        j1 = ai.length;
        l = 0;
        k = i;
        i = ((flag) ? 1 : 0);
_L10:
        Object obj;
        int i1;
        if (l >= j1)
        {
            break MISSING_BLOCK_LABEL_296;
        }
        i1 = ai[l];
        obj = (com.lumiyaviewer.lumiya.slproto.avatar.SLAvatarParams.ParamSet)SLAvatarParams.paramByIDs.get(Integer.valueOf(i1));
        if (obj == null) goto _L2; else goto _L1
_L1:
        SLAvatarParamColor slavatarparamcolor;
        obj = (com.lumiyaviewer.lumiya.slproto.avatar.SLAvatarParams.AvatarParam)((com.lumiyaviewer.lumiya.slproto.avatar.SLAvatarParams.ParamSet) (obj)).params.get(0);
        slavatarparamcolor = ((com.lumiyaviewer.lumiya.slproto.avatar.SLAvatarParams.AvatarParam) (obj)).paramColor;
        if (slavatarparamcolor == null) goto _L2; else goto _L3
_L3:
        float f;
        f = bakeprocess.getParamWeight(i1, ((com.lumiyaviewer.lumiya.slproto.avatar.SLAvatarParams.AvatarParam) (obj)));
        i = slavatarparamcolor.getColor(f);
        if (layerName.equals("lipstick"))
        {
            Debug.Log(String.format("Baking: lipstick color param weight %ff color %08x", new Object[] {
                Float.valueOf(f), Integer.valueOf(i)
            }));
        }
        _2D_getcom_2D_lumiyaviewer_2D_lumiya_2D_slproto_2D_avatar_2D_SLAvatarParamColor$ColorOperationSwitchesValues()[slavatarparamcolor.colorOperation.ordinal()];
        JVM INSTR tableswitch 1 3: default 212
    //                   1 264
    //                   2 286
    //                   3 276;
           goto _L4 _L5 _L6 _L7
_L6:
        break MISSING_BLOCK_LABEL_286;
_L4:
        i = k;
_L8:
        if (layerName.equals("lipstick"))
        {
            Debug.Log(String.format("Baking: after op, lipstick color result %08x", new Object[] {
                Integer.valueOf(i)
            }));
            boolean flag1 = true;
            k = i;
            i = ((flag1) ? 1 : 0);
        } else
        {
            k = i;
            i = 1;
        }
_L2:
        l++;
        continue; /* Loop/switch isn't completed */
_L5:
        i = SLAvatarParamColor.colorLerp(k, i, f);
          goto _L8
_L7:
        i = SLAvatarParamColor.colorMult(k, i);
          goto _L8
        i = SLAvatarParamColor.colorAdd(k, i);
          goto _L8
        if (i == 0)
        {
            return j;
        }
        return k;
        if (true) goto _L10; else goto _L9
_L9:
    }

    private int getNetColor(BakeProcess bakeprocess)
    {
        int i;
        int ai[] = paramIDs;
        int j = ai.length;
        i = 0;
        do
        {
            if (i >= j)
            {
                break MISSING_BLOCK_LABEL_173;
            }
            int k = ai[i];
            com.lumiyaviewer.lumiya.slproto.avatar.SLAvatarParams.ParamSet paramset = (com.lumiyaviewer.lumiya.slproto.avatar.SLAvatarParams.ParamSet)SLAvatarParams.paramByIDs.get(Integer.valueOf(k));
            if (paramset != null && ((com.lumiyaviewer.lumiya.slproto.avatar.SLAvatarParams.AvatarParam)paramset.params.get(0)).paramColor != null)
            {
                i = 1;
                break MISSING_BLOCK_LABEL_64;
            }
            i++;
        } while (true);
_L2:
        if (i != 0)
        {
            if (globalColor != null)
            {
                i = getColorByParamList(bakeprocess, globalColor.getParamIDs(), 0, 0);
            } else
            if (hasFixedColor)
            {
                i = fixedColor;
            } else
            {
                i = 0;
            }
            return getColorByParamList(bakeprocess, paramIDs, i, i);
        }
        if (globalColor != null)
        {
            return getColorByParamList(bakeprocess, globalColor.getParamIDs(), 0, 0);
        }
        if (hasFixedColor)
        {
            return fixedColor;
        } else
        {
            return -1;
        }
        i = 0;
        if (true) goto _L2; else goto _L1
_L1:
    }

    public void Bake(OpenJPEG openjpeg, BakeProcess bakeprocess)
    {
        OpenJPEG openjpeg1;
        OpenJPEG openjpeg2;
        boolean flag2;
        boolean flag3;
        int k;
        boolean flag5;
        flag3 = false;
        k = getNetColor(bakeprocess);
        openjpeg1 = new OpenJPEG(openjpeg.width, openjpeg.height, 4, 4, 0, 0);
        openjpeg2 = new OpenJPEG(openjpeg.width, openjpeg.height, 4, 4, 0, 0xff000000);
        Debug.Log(String.format("Baking: layer %s net_color 0x%08x.", new Object[] {
            layerName, Integer.valueOf(k)
        }));
        boolean flag = true;
        flag5 = false;
        int ai[] = paramIDs;
        int l = ai.length;
        int i = 0;
        while (i < l) 
        {
            int j = ai[i];
            Object obj = (com.lumiyaviewer.lumiya.slproto.avatar.SLAvatarParams.ParamSet)SLAvatarParams.paramByIDs.get(Integer.valueOf(j));
            if (obj == null)
            {
                continue;
            }
            obj = (com.lumiyaviewer.lumiya.slproto.avatar.SLAvatarParams.AvatarParam)((com.lumiyaviewer.lumiya.slproto.avatar.SLAvatarParams.ParamSet) (obj)).params.get(0);
            if (((com.lumiyaviewer.lumiya.slproto.avatar.SLAvatarParams.AvatarParam) (obj)).paramAlpha != null && ((com.lumiyaviewer.lumiya.slproto.avatar.SLAvatarParams.AvatarParam) (obj)).paramAlpha.tgaFile != null)
            {
                float f = bakeprocess.getParamWeight(j, ((com.lumiyaviewer.lumiya.slproto.avatar.SLAvatarParams.AvatarParam) (obj)));
                if (flag)
                {
                    boolean flag4 = false;
                    flag = flag4;
                    if (!((com.lumiyaviewer.lumiya.slproto.avatar.SLAvatarParams.AvatarParam) (obj)).paramAlpha.multiplyBlend)
                    {
                        openjpeg2.setComponent(3, (byte)0);
                        flag = flag4;
                    }
                }
                if (f != 0.0F || !((com.lumiyaviewer.lumiya.slproto.avatar.SLAvatarParams.AvatarParam) (obj)).paramAlpha.skipIfZero)
                {
                    flag5 |= ((com.lumiyaviewer.lumiya.slproto.avatar.SLAvatarParams.AvatarParam) (obj)).paramAlpha.multiplyBlend;
                    try
                    {
                        InputStream inputstream = LumiyaApp.getAssetManager().open((new StringBuilder()).append("tga/").append(((com.lumiyaviewer.lumiya.slproto.avatar.SLAvatarParams.AvatarParam) (obj)).paramAlpha.tgaFile).toString());
                        OpenJPEG openjpeg5 = new OpenJPEG(inputstream, com.lumiyaviewer.lumiya.openjpeg.OpenJPEG.ImageFormat.TGA, true, true, ((com.lumiyaviewer.lumiya.slproto.avatar.SLAvatarParams.AvatarParam) (obj)).paramAlpha.domain, f, false);
                        Debug.Log(String.format("Baking: layer %s: applying alpha (weight %f domain %f) mask texture %s, width %d, height %d, num_comps %d", new Object[] {
                            layerName, Float.valueOf(f), Float.valueOf(((com.lumiyaviewer.lumiya.slproto.avatar.SLAvatarParams.AvatarParam) (obj)).paramAlpha.domain), ((com.lumiyaviewer.lumiya.slproto.avatar.SLAvatarParams.AvatarParam) (obj)).paramAlpha.tgaFile, Integer.valueOf(openjpeg5.getWidth()), Integer.valueOf(openjpeg5.getHeight()), Integer.valueOf(openjpeg5.getNumComponents())
                        }));
                        openjpeg2.blendAlpha(openjpeg5, ((com.lumiyaviewer.lumiya.slproto.avatar.SLAvatarParams.AvatarParam) (obj)).paramAlpha.multiplyBlend ^ true);
                        inputstream.close();
                    }
                    catch (Exception exception)
                    {
                        exception.printStackTrace();
                    }
                }
            }
            i++;
        }
        flag2 = false;
        if (localTexture == null)
        {
            break MISSING_BLOCK_LABEL_873;
        }
        bakeprocess = bakeprocess.getLocalTexture(localTexture);
        if (bakeprocess == null) goto _L2; else goto _L1
_L1:
        bakeprocess = bakeprocess.iterator();
        boolean flag1 = false;
_L4:
        if (!bakeprocess.hasNext())
        {
            break; /* Loop/switch isn't completed */
        }
        OpenJPEG openjpeg3 = (OpenJPEG)bakeprocess.next();
        Debug.Log(String.format("Baking: layer %s: applying local texture, writeAllChannels %s", new Object[] {
            layerName, Boolean.valueOf(writeAllChannels)
        }));
        openjpeg1.draw(openjpeg3, -1, false);
        flag1 = true;
        if (true) goto _L4; else goto _L3
_L2:
        Debug.Log(String.format("Baking: layer %s: missing local texture", new Object[] {
            layerName
        }));
        flag1 = true;
_L12:
        flag3 = flag2;
        flag2 = flag1;
        flag1 = flag3;
_L9:
        flag3 = flag1;
        if (tgaTexture == null)
        {
            break MISSING_BLOCK_LABEL_736;
        }
        flag3 = flag1;
        bakeprocess = LumiyaApp.getAssetManager().open((new StringBuilder()).append("tga/").append(tgaTexture).toString());
        flag3 = flag1;
        OpenJPEG openjpeg4 = new OpenJPEG(bakeprocess, com.lumiyaviewer.lumiya.openjpeg.OpenJPEG.ImageFormat.TGA, tgaFileIsMask, false, 0.0F, 0.0F, false);
        flag3 = flag1;
        Debug.Log(String.format("Baking: layer %s: applying tga texture %s, writeAllChannels %s, width %d, height %d, num_comps %d", new Object[] {
            layerName, tgaTexture, Boolean.valueOf(writeAllChannels), Integer.valueOf(openjpeg4.getWidth()), Integer.valueOf(openjpeg4.getHeight()), Integer.valueOf(openjpeg4.getNumComponents())
        }));
        flag3 = flag1;
        openjpeg1.draw(openjpeg4, -1, false);
        flag3 = true;
        flag1 = true;
        bakeprocess.close();
        flag3 = flag1;
_L10:
        if (!flag3)
        {
            openjpeg1.setComponent(0, (byte)-1);
            openjpeg1.setComponent(1, (byte)-1);
            openjpeg1.setComponent(2, (byte)-1);
            openjpeg1.setComponent(3, (byte)-1);
        }
        openjpeg1.blendAlpha(openjpeg2, false);
        if (flag2) goto _L6; else goto _L5
_L5:
        if (!isRenderPassBump) goto _L8; else goto _L7
_L7:
        openjpeg.drawBump(openjpeg1, k, writeAllChannels, flag5);
_L6:
        return;
        bakeprocess;
        flag1 = false;
_L11:
        Debug.Log(String.format("Baking: layer %s: default local texture", new Object[] {
            layerName
        }));
        flag2 = true;
          goto _L9
        bakeprocess;
        bakeprocess.printStackTrace();
          goto _L10
_L8:
        openjpeg.draw(openjpeg1, k, writeAllChannels);
        return;
        bakeprocess;
          goto _L11
_L3:
        flag3 = false;
        flag2 = flag1;
        flag1 = flag3;
          goto _L12
        flag1 = false;
        flag2 = flag3;
          goto _L9
    }

    public void BakeAlpha(OpenJPEG openjpeg, BakeProcess bakeprocess)
    {
        if (isRenderPassBump)
        {
            return;
        }
        OpenJPEG openjpeg1;
        if (tgaTexture != null)
        {
            try
            {
                InputStream inputstream = LumiyaApp.getAssetManager().open((new StringBuilder()).append("tga/").append(tgaTexture).toString());
                OpenJPEG openjpeg2 = new OpenJPEG(inputstream, com.lumiyaviewer.lumiya.openjpeg.OpenJPEG.ImageFormat.TGA, tgaFileIsMask, false, 0.0F, 0.0F, false);
                Debug.Log(String.format("Baking: layer %s: applying tga alpha mask %swidth %d, height %d, num_comps %d", new Object[] {
                    layerName, tgaTexture, Integer.valueOf(openjpeg2.getWidth()), Integer.valueOf(openjpeg2.getHeight()), Integer.valueOf(openjpeg2.getNumComponents())
                }));
                openjpeg.blendAlpha(openjpeg2, false);
                inputstream.close();
            }
            catch (Exception exception)
            {
                exception.printStackTrace();
            }
        }
        if (localTexture == null)
        {
            break MISSING_BLOCK_LABEL_232;
        }
        bakeprocess = bakeprocess.getLocalTexture(localTexture);
        if (bakeprocess != null)
        {
            try
            {
                for (bakeprocess = bakeprocess.iterator(); bakeprocess.hasNext(); openjpeg.blendAlpha(openjpeg1, false))
                {
                    openjpeg1 = (OpenJPEG)bakeprocess.next();
                    Debug.Log(String.format("Baking: layer %s: applying local texture alpha", new Object[] {
                        layerName
                    }));
                }

            }
            // Misplaced declaration of an exception variable
            catch (OpenJPEG openjpeg)
            {
                Debug.Log(String.format("Baking: layer %s: default local texture for alpha", new Object[] {
                    layerName
                }));
            }
        }
    }
}
