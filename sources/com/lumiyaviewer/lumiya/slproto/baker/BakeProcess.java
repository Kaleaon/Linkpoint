// Decompiled by Jad v1.5.8e. Copyright 2001 Pavel Kouznetsov.
// Jad home page: http://www.geocities.com/kpdus/jad.html
// Decompiler options: braces fieldsfirst space lnc 

package com.lumiyaviewer.lumiya.slproto.baker;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Table;
import com.lumiyaviewer.lumiya.Debug;
import com.lumiyaviewer.lumiya.GlobalOptions;
import com.lumiyaviewer.lumiya.eventbus.EventBus;
import com.lumiyaviewer.lumiya.openjpeg.OpenJPEG;
import com.lumiyaviewer.lumiya.render.avatar.AvatarSkeleton;
import com.lumiyaviewer.lumiya.render.avatar.DrawableAvatarPart;
import com.lumiyaviewer.lumiya.render.tex.DrawableTextureParams;
import com.lumiyaviewer.lumiya.render.tex.TextureClass;
import com.lumiyaviewer.lumiya.res.ResourceConsumer;
import com.lumiyaviewer.lumiya.res.textures.TextureCache;
import com.lumiyaviewer.lumiya.slproto.assets.SLWearable;
import com.lumiyaviewer.lumiya.slproto.assets.SLWearableData;
import com.lumiyaviewer.lumiya.slproto.assets.SLWearableType;
import com.lumiyaviewer.lumiya.slproto.avatar.AvatarTextureFaceIndex;
import com.lumiyaviewer.lumiya.slproto.avatar.BakedTextureIndex;
import com.lumiyaviewer.lumiya.slproto.avatar.SLAvatarParams;
import com.lumiyaviewer.lumiya.slproto.events.SLBakingProgressEvent;
import com.lumiyaviewer.lumiya.slproto.modules.SLAvatarAppearance;
import com.lumiyaviewer.lumiya.slproto.modules.texuploader.SLTextureUploadRequest;
import com.lumiyaviewer.lumiya.slproto.modules.texuploader.SLTextureUploader;
import com.lumiyaviewer.lumiya.slproto.textures.MutableSLTextureEntryFace;
import com.lumiyaviewer.lumiya.slproto.textures.SLTextureEntry;
import com.lumiyaviewer.lumiya.slproto.textures.SLTextureEntryFace;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.EnumMap;
import java.util.HashMap;
import java.util.IdentityHashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.UUID;

// Referenced classes of package com.lumiyaviewer.lumiya.slproto.baker:
//            BakedImage, BakeLayers, BakeLayerSet

public class BakeProcess
    implements com.lumiyaviewer.lumiya.slproto.modules.texuploader.SLTextureUploadRequest.TextureUploadCompleteListener
{
    private static class BakedImageUploadRequest extends SLTextureUploadRequest
    {

        final BakedImage bakedImage;
        final BakedTextureIndex bakedIndex;

        BakedImageUploadRequest(BakedImage bakedimage, BakedTextureIndex bakedtextureindex, File file)
        {
            super(file, bakedtextureindex.ordinal());
            bakedImage = bakedimage;
            bakedIndex = bakedtextureindex;
        }
    }

    static class DefaultTextureException extends Exception
    {

        DefaultTextureException()
        {
        }
    }

    private class WearableTextureData
        implements ResourceConsumer
    {

        private final com.lumiyaviewer.lumiya.slproto.assets.SLWearableData.WearableTexture texture;
        private volatile OpenJPEG textureData;
        private volatile boolean textureReady;
        final BakeProcess this$0;

        static OpenJPEG _2D_get0(WearableTextureData wearabletexturedata)
        {
            return wearabletexturedata.textureData;
        }

        public void OnResourceReady(Object obj, boolean flag)
        {
            if (obj instanceof OpenJPEG)
            {
                textureData = (OpenJPEG)obj;
            }
            textureReady = true;
            BakeProcess._2D_wrap0(BakeProcess.this);
        }

        protected com.lumiyaviewer.lumiya.slproto.assets.SLWearableData.WearableTexture getTexture()
        {
            return texture;
        }

        OpenJPEG getTextureData()
        {
            return textureData;
        }

        boolean getTextureReady()
        {
            return textureReady;
        }

        void requestData()
        {
            TextureCache.getInstance().RequestResource(DrawableTextureParams.create(texture.textureID, TextureClass.Asset), this);
        }

        WearableTextureData(com.lumiyaviewer.lumiya.slproto.assets.SLWearableData.WearableTexture wearabletexture)
        {
            this$0 = BakeProcess.this;
            super();
            texture = wearabletexture;
            textureReady = false;
        }
    }


    private final SLAvatarAppearance avatarAppearance;
    private final Map bakedImages = new EnumMap(com/lumiyaviewer/lumiya/slproto/avatar/BakedTextureIndex);
    private final Thread bakingThread = new Thread(new _2D_.Lambda.qb61PwDoxRPFEOdyYwns3UfUTbM(this), "Baker");
    private final EventBus eventBus;
    private Map paramValues;
    private final Object textureReadyLock = new Object();
    private final SLTextureUploader uploader;
    private final Map wearables = new IdentityHashMap();
    private final Table wornWearables;

    static void _2D_wrap0(BakeProcess bakeprocess)
    {
        bakeprocess.notifyTextureReady();
    }

    public BakeProcess(Table table, SLAvatarAppearance slavatarappearance, SLTextureUploader sltextureuploader, EventBus eventbus)
    {
        Debug.Printf("Baking: new BakeProcess created", new Object[0]);
        avatarAppearance = slavatarappearance;
        wornWearables = table;
        uploader = sltextureuploader;
        eventBus = eventbus;
        table = table.values().iterator();
        do
        {
            if (!table.hasNext())
            {
                break;
            }
            slavatarappearance = (SLWearable)table.next();
            eventbus = slavatarappearance.getWearableData();
            if (eventbus != null)
            {
                sltextureuploader = new ArrayList(((SLWearableData) (eventbus)).textures.size());
                for (eventbus = ((SLWearableData) (eventbus)).textures.iterator(); eventbus.hasNext(); sltextureuploader.add(new WearableTextureData((com.lumiyaviewer.lumiya.slproto.assets.SLWearableData.WearableTexture)eventbus.next()))) { }
                wearables.put(slavatarappearance, sltextureuploader);
            }
        } while (true);
        bakingThread.start();
    }

    private SLTextureEntry PrepareAvatarTextureEntry()
    {
        SLTextureEntryFace sltextureentryface = SLTextureEntryFace.create(new MutableSLTextureEntryFace(-1));
        SLTextureEntryFace asltextureentryface[] = new SLTextureEntryFace[32];
        BakedTextureIndex abakedtextureindex[] = BakedTextureIndex.values();
        int j = abakedtextureindex.length;
        for (int i = 0; i < j; i++)
        {
            Object obj = abakedtextureindex[i];
            int k = ((BakedTextureIndex) (obj)).getFaceIndex().ordinal();
            obj = (BakedImage)bakedImages.get(obj);
            if (obj == null)
            {
                continue;
            }
            obj = ((BakedImage) (obj)).getUploadedID();
            if (obj != null)
            {
                MutableSLTextureEntryFace mutablesltextureentryface = new MutableSLTextureEntryFace(0);
                mutablesltextureentryface.setTextureID(((UUID) (obj)));
                asltextureentryface[k] = SLTextureEntryFace.create(mutablesltextureentryface);
            }
        }

        return SLTextureEntry.create(sltextureentryface, asltextureentryface);
    }

    private void bakeAppearance()
    {
        Debug.Printf("Baking: Requesting texture data.", new Object[0]);
        for (Iterator iterator = wearables.values().iterator(); iterator.hasNext();)
        {
            Iterator iterator1 = ((List)iterator.next()).iterator();
            while (iterator1.hasNext()) 
            {
                ((WearableTextureData)iterator1.next()).requestData();
            }
        }

        Object obj = textureReadyLock;
        obj;
        JVM INSTR monitorenter ;
_L1:
        boolean flag = isTexturesReady();
        if (flag)
        {
            break MISSING_BLOCK_LABEL_118;
        }
        textureReadyLock.wait();
          goto _L1
        Object obj1;
        obj1;
        finishBaking(null);
        Debug.Printf("Baking: Interrupted before textures were ready.", new Object[0]);
        obj;
        JVM INSTR monitorexit ;
        return;
        obj;
        JVM INSTR monitorexit ;
        BakedTextureIndex abakedtextureindex[];
        int i;
        int j;
        Debug.Log("Baking: calculating param values...");
        paramValues = calcAllParamValues(wornWearables);
        Debug.Log("Baking: baking...");
        flag = isWearingSkirt();
        obj = GlobalOptions.getInstance().getCacheDir("baker");
        ((File) (obj)).mkdirs();
        abakedtextureindex = BakedTextureIndex.values();
        j = abakedtextureindex.length;
        i = 0;
_L3:
        BakedTextureIndex bakedtextureindex;
        if (i >= j)
        {
            break; /* Loop/switch isn't completed */
        }
        bakedtextureindex = abakedtextureindex[i];
        if (Thread.interrupted())
        {
            Debug.Log("Baking: interrupted.");
            eventBus.publish(new SLBakingProgressEvent(false, true, 0));
            finishBaking(null);
            return;
        }
        break MISSING_BLOCK_LABEL_225;
        abakedtextureindex;
        throw abakedtextureindex;
        if (bakedtextureindex != BakedTextureIndex.BAKED_SKIRT || !(flag ^ true))
        {
            Debug.Log((new StringBuilder()).append("Baking: Baking layer ").append(bakedtextureindex).toString());
            Object obj2 = new BakedImage((BakeLayerSet)BakeLayers.layerSets.get(bakedtextureindex));
            bakedImages.put(bakedtextureindex, obj2);
            ((BakedImage) (obj2)).Bake(this);
            if (Thread.interrupted())
            {
                Debug.Log("Baking: interrupted.");
                eventBus.publish(new SLBakingProgressEvent(false, true, 0));
                finishBaking(null);
                return;
            }
            try
            {
                File file = new File(((File) (obj)), (new StringBuilder()).append(bakedtextureindex.toString()).append(".j2k").toString());
                ((BakedImage) (obj2)).SaveToJPEG2K(file);
                obj2 = new BakedImageUploadRequest(((BakedImage) (obj2)), bakedtextureindex, file);
                ((SLTextureUploadRequest) (obj2)).setOnUploadComplete(this);
                uploader.BeginUpload(((SLTextureUploadRequest) (obj2)));
            }
            catch (IOException ioexception)
            {
                ioexception.printStackTrace();
            }
            Debug.Log((new StringBuilder()).append("Baking: Done layer ").append(bakedtextureindex).toString());
        }
        i++;
        if (true) goto _L3; else goto _L2
_L2:
        Debug.Log("Baking: Baked all layers.");
        return;
    }

    private Map calcAllParamValues(Table table)
    {
        HashMap hashmap = new HashMap();
        java.util.Map.Entry entry;
        com.lumiyaviewer.lumiya.slproto.avatar.SLAvatarParams.AvatarParam avatarparam;
        for (Iterator iterator = SLAvatarParams.paramByIDs.entrySet().iterator(); iterator.hasNext(); hashmap.put((Integer)entry.getKey(), Float.valueOf(avatarparam.defValue)))
        {
            entry = (java.util.Map.Entry)iterator.next();
            avatarparam = (com.lumiyaviewer.lumiya.slproto.avatar.SLAvatarParams.AvatarParam)((com.lumiyaviewer.lumiya.slproto.avatar.SLAvatarParams.ParamSet)entry.getValue()).params.get(0);
        }

        for (table = table.values().iterator(); table.hasNext();)
        {
            Object obj = ((SLWearable)table.next()).getWearableData();
            if (obj != null)
            {
                obj = ((SLWearableData) (obj)).params.iterator();
                while (((Iterator) (obj)).hasNext()) 
                {
                    com.lumiyaviewer.lumiya.slproto.assets.SLWearableData.WearableParam wearableparam = (com.lumiyaviewer.lumiya.slproto.assets.SLWearableData.WearableParam)((Iterator) (obj)).next();
                    hashmap.put(Integer.valueOf(wearableparam.paramIndex), Float.valueOf(wearableparam.paramValue));
                    Object obj1 = (com.lumiyaviewer.lumiya.slproto.avatar.SLAvatarParams.ParamSet)SLAvatarParams.paramByIDs.get(Integer.valueOf(wearableparam.paramIndex));
                    if (obj1 != null)
                    {
                        obj1 = (com.lumiyaviewer.lumiya.slproto.avatar.SLAvatarParams.AvatarParam)((com.lumiyaviewer.lumiya.slproto.avatar.SLAvatarParams.ParamSet) (obj1)).params.get(0);
                        if (((com.lumiyaviewer.lumiya.slproto.avatar.SLAvatarParams.AvatarParam) (obj1)).drivenParams != null)
                        {
                            Iterator iterator1 = ((com.lumiyaviewer.lumiya.slproto.avatar.SLAvatarParams.AvatarParam) (obj1)).drivenParams.iterator();
                            while (iterator1.hasNext()) 
                            {
                                com.lumiyaviewer.lumiya.slproto.avatar.SLAvatarParams.DrivenParam drivenparam = (com.lumiyaviewer.lumiya.slproto.avatar.SLAvatarParams.DrivenParam)iterator1.next();
                                Object obj2 = (com.lumiyaviewer.lumiya.slproto.avatar.SLAvatarParams.ParamSet)SLAvatarParams.paramByIDs.get(Integer.valueOf(drivenparam.drivenID));
                                if (obj2 != null)
                                {
                                    obj2 = ((com.lumiyaviewer.lumiya.slproto.avatar.SLAvatarParams.ParamSet) (obj2)).params.iterator();
                                    while (((Iterator) (obj2)).hasNext()) 
                                    {
                                        com.lumiyaviewer.lumiya.slproto.avatar.SLAvatarParams.AvatarParam avatarparam1 = (com.lumiyaviewer.lumiya.slproto.avatar.SLAvatarParams.AvatarParam)((Iterator) (obj2)).next();
                                        float f = AvatarSkeleton.getDrivenWeight(wearableparam.paramValue, ((com.lumiyaviewer.lumiya.slproto.avatar.SLAvatarParams.AvatarParam) (obj1)), drivenparam, avatarparam1);
                                        hashmap.put(Integer.valueOf(drivenparam.drivenID), Float.valueOf(f));
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }

        return hashmap;
    }

    private void finishBaking(SLTextureEntry sltextureentry)
    {
        avatarAppearance.finishBaking(this, sltextureentry);
    }

    private boolean isTexturesReady()
    {
        boolean flag;
        Iterator iterator = wearables.values().iterator();
        flag = true;
        do
        {
label0:
            {
                if (!iterator.hasNext())
                {
                    break label0;
                }
                Iterator iterator1 = ((List)iterator.next()).iterator();
                boolean flag1 = flag;
                do
                {
                    flag = flag1;
                    if (!iterator1.hasNext())
                    {
                        break;
                    }
                    if (!((WearableTextureData)iterator1.next()).getTextureReady())
                    {
                        flag1 = false;
                    }
                } while (true);
            }
        } while (true);
        return flag;
    }

    private boolean isWearingSkirt()
    {
        boolean flag = false;
        if (!wornWearables.row(SLWearableType.WT_SKIRT).isEmpty())
        {
            flag = true;
        }
        return flag;
    }

    private void notifyTextureReady()
    {
        Object obj = textureReadyLock;
        obj;
        JVM INSTR monitorenter ;
        textureReadyLock.notifyAll();
        obj;
        JVM INSTR monitorexit ;
        return;
        Exception exception;
        exception;
        throw exception;
    }

    void _2D_com_lumiyaviewer_lumiya_slproto_baker_BakeProcess_2D_mthref_2D_0()
    {
        bakeAppearance();
    }

    public void OnTextureUploadComplete(SLTextureUploadRequest sltextureuploadrequest)
    {
        int j;
        int k;
label0:
        {
            if (sltextureuploadrequest instanceof BakedImageUploadRequest)
            {
                sltextureuploadrequest = (BakedImageUploadRequest)sltextureuploadrequest;
                Debug.Log((new StringBuilder()).append("Baking: texture ").append(((BakedImageUploadRequest) (sltextureuploadrequest)).bakedIndex).append(" uploaded, UUID = ").append(sltextureuploadrequest.getTextureID()).toString());
                ((BakedImageUploadRequest) (sltextureuploadrequest)).bakedImage.setUploadedID(sltextureuploadrequest.getTextureID());
                bakedImages.put(((BakedImageUploadRequest) (sltextureuploadrequest)).bakedIndex, ((BakedImageUploadRequest) (sltextureuploadrequest)).bakedImage);
                boolean flag1 = isWearingSkirt();
                sltextureuploadrequest = BakedTextureIndex.values();
                int i1 = sltextureuploadrequest.length;
                int l = 0;
                boolean flag = true;
                j = 0;
                k = 0;
                while (l < i1) 
                {
                    BakedTextureIndex bakedtextureindex = sltextureuploadrequest[l];
                    if (bakedtextureindex != BakedTextureIndex.BAKED_SKIRT || !(flag1 ^ true))
                    {
                        k++;
                        if (!bakedImages.containsKey(bakedtextureindex))
                        {
                            j++;
                            flag = false;
                        } else
                        if (((BakedImage)bakedImages.get(bakedtextureindex)).getUploadedID() == null)
                        {
                            j++;
                            flag = false;
                        }
                    }
                    l++;
                }
                if (!flag)
                {
                    break label0;
                }
                eventBus.publish(new SLBakingProgressEvent(false, true, 100));
                Debug.Log("Baking: all textures uploaded.");
                finishBaking(PrepareAvatarTextureEntry());
            }
            return;
        }
        int i = ((k - j) * 100) / k;
        eventBus.publish(new SLBakingProgressEvent(false, false, i));
    }

    public void cancel()
    {
        bakingThread.interrupt();
    }

    List getLocalTexture(AvatarTextureFaceIndex avatartexturefaceindex)
        throws DefaultTextureException
    {
        Object obj1;
        Iterator iterator;
        boolean flag1;
        iterator = wearables.values().iterator();
        flag1 = false;
        obj1 = null;
_L5:
        Object obj;
        Iterator iterator1;
        boolean flag;
        if (!iterator.hasNext())
        {
            break MISSING_BLOCK_LABEL_181;
        }
        iterator1 = ((List)iterator.next()).iterator();
        obj = obj1;
        flag = flag1;
_L3:
        flag1 = flag;
        obj1 = obj;
        if (!iterator1.hasNext()) goto _L2; else goto _L1
_L1:
        obj1 = (WearableTextureData)iterator1.next();
        if (((WearableTextureData) (obj1)).getTexture().layer == avatartexturefaceindex.ordinal())
        {
            if (((WearableTextureData) (obj1)).getTexture().textureID != null && ((WearableTextureData) (obj1)).getTexture().textureID.equals(DrawableAvatarPart.DEFAULT_AVATAR_TEXTURE))
            {
                flag1 = true;
            } else
            {
                flag1 = false;
            }
            if (flag1)
            {
                flag = true;
            } else
            if (WearableTextureData._2D_get0(((WearableTextureData) (obj1))) != null)
            {
                OpenJPEG openjpeg = ((WearableTextureData) (obj1)).getTextureData();
                if (openjpeg != null)
                {
                    obj1 = obj;
                    if (obj == null)
                    {
                        obj1 = new LinkedList();
                    }
                    ((List) (obj1)).add(openjpeg);
                    obj = obj1;
                }
            }
        }
          goto _L3
_L2:
        if (true) goto _L5; else goto _L4
_L4:
        if (obj1 == null && flag1)
        {
            throw new DefaultTextureException();
        } else
        {
            return ((List) (obj1));
        }
    }

    float getParamWeight(int i, com.lumiyaviewer.lumiya.slproto.avatar.SLAvatarParams.AvatarParam avatarparam)
    {
        Float float1 = (Float)paramValues.get(Integer.valueOf(i));
        if (float1 != null)
        {
            return float1.floatValue();
        } else
        {
            return avatarparam.defValue;
        }
    }
}
