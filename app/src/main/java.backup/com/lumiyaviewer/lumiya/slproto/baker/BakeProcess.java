package com.lumiyaviewer.lumiya.slproto.baker;

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
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.UUID;

public class BakeProcess implements SLTextureUploadRequest.TextureUploadCompleteListener {
    private final SLAvatarAppearance avatarAppearance;
    private final Map<BakedTextureIndex, BakedImage> bakedImages = new EnumMap(BakedTextureIndex.class);
    private final Thread bakingThread;
    private final EventBus eventBus;
    private Map<Integer, Float> paramValues;
    private final Object textureReadyLock = new Object();
    private final SLTextureUploader uploader;
    private final Map<SLWearable, List<WearableTextureData>> wearables = new IdentityHashMap();
    private final Table<SLWearableType, UUID, SLWearable> wornWearables;

    private static class BakedImageUploadRequest extends SLTextureUploadRequest {
        final BakedImage bakedImage;
        final BakedTextureIndex bakedIndex;

        BakedImageUploadRequest(BakedImage bakedImage2, BakedTextureIndex bakedTextureIndex, File file) {
            super(file, bakedTextureIndex.ordinal());
            this.bakedImage = bakedImage2;
            this.bakedIndex = bakedTextureIndex;
        }
    }

    static class DefaultTextureException extends Exception {
        DefaultTextureException() {
        }
    }

    private class WearableTextureData implements ResourceConsumer {
        private final SLWearableData.WearableTexture texture;
        /* access modifiers changed from: private */
        public volatile OpenJPEG textureData;
        private volatile boolean textureReady = false;

        WearableTextureData(SLWearableData.WearableTexture wearableTexture) {
            this.texture = wearableTexture;
        }

        public void OnResourceReady(Object obj, boolean z) {
            if (obj instanceof OpenJPEG) {
                this.textureData = (OpenJPEG) obj;
            }
            this.textureReady = true;
            BakeProcess.this.notifyTextureReady();
        }

        /* access modifiers changed from: protected */
        public SLWearableData.WearableTexture getTexture() {
            return this.texture;
        }

        /* access modifiers changed from: package-private */
        public OpenJPEG getTextureData() {
            return this.textureData;
        }

        /* access modifiers changed from: package-private */
        public boolean getTextureReady() {
            return this.textureReady;
        }

        /* access modifiers changed from: package-private */
        public void requestData() {
            TextureCache.getInstance().RequestResource(DrawableTextureParams.create(this.texture.textureID, TextureClass.Asset), this);
        }
    }

    public BakeProcess(Table<SLWearableType, UUID, SLWearable> table, SLAvatarAppearance sLAvatarAppearance, SLTextureUploader sLTextureUploader, EventBus eventBus2) {
        Debug.Printf("Baking: new BakeProcess created", new Object[0]);
        this.avatarAppearance = sLAvatarAppearance;
        this.wornWearables = table;
        this.uploader = sLTextureUploader;
        this.eventBus = eventBus2;
        for (SLWearable sLWearable : table.values()) {
            SLWearableData wearableData = sLWearable.getWearableData();
            if (wearableData != null) {
                ArrayList arrayList = new ArrayList(wearableData.textures.size());
                for (SLWearableData.WearableTexture wearableTextureData : wearableData.textures) {
                    arrayList.add(new WearableTextureData(wearableTextureData));
                }
                this.wearables.put(sLWearable, arrayList);
            }
        }
        this.bakingThread = new Thread(new $Lambda$qb61PwDoxRPFEOdyYwns3UfUTbM(this), "Baker");
        this.bakingThread.start();
    }

    private SLTextureEntry PrepareAvatarTextureEntry() {
        UUID uploadedID;
        SLTextureEntryFace create = SLTextureEntryFace.create(new MutableSLTextureEntryFace(-1));
        SLTextureEntryFace[] sLTextureEntryFaceArr = new SLTextureEntryFace[32];
        for (BakedTextureIndex bakedTextureIndex : BakedTextureIndex.values()) {
            int ordinal = bakedTextureIndex.getFaceIndex().ordinal();
            BakedImage bakedImage = this.bakedImages.get(bakedTextureIndex);
            if (!(bakedImage == null || (uploadedID = bakedImage.getUploadedID()) == null)) {
                MutableSLTextureEntryFace mutableSLTextureEntryFace = new MutableSLTextureEntryFace(0);
                mutableSLTextureEntryFace.setTextureID(uploadedID);
                sLTextureEntryFaceArr[ordinal] = SLTextureEntryFace.create(mutableSLTextureEntryFace);
            }
        }
        return SLTextureEntry.create(create, sLTextureEntryFaceArr);
    }

    /* access modifiers changed from: private */
    /* renamed from: bakeAppearance */
    public void m140com_lumiyaviewer_lumiya_slproto_baker_BakeProcessmthref0() {
        Debug.Printf("Baking: Requesting texture data.", new Object[0]);
        for (List<WearableTextureData> it : this.wearables.values()) {
            for (WearableTextureData requestData : it) {
                requestData.requestData();
            }
        }
        synchronized (this.textureReadyLock) {
            while (!isTexturesReady()) {
                try {
                    this.textureReadyLock.wait();
                } catch (InterruptedException e) {
                    finishBaking((SLTextureEntry) null);
                    Debug.Printf("Baking: Interrupted before textures were ready.", new Object[0]);
                    return;
                }
            }
        }
        Debug.Log("Baking: calculating param values...");
        this.paramValues = calcAllParamValues(this.wornWearables);
        Debug.Log("Baking: baking...");
        boolean isWearingSkirt = isWearingSkirt();
        File cacheDir = GlobalOptions.getInstance().getCacheDir("baker");
        cacheDir.mkdirs();
        for (BakedTextureIndex bakedTextureIndex : BakedTextureIndex.values()) {
            if (Thread.interrupted()) {
                Debug.Log("Baking: interrupted.");
                this.eventBus.publish(new SLBakingProgressEvent(false, true, 0));
                finishBaking((SLTextureEntry) null);
                return;
            }
            if (bakedTextureIndex != BakedTextureIndex.BAKED_SKIRT || !(!isWearingSkirt)) {
                Debug.Log("Baking: Baking layer " + bakedTextureIndex);
                BakedImage bakedImage = new BakedImage(BakeLayers.layerSets.get(bakedTextureIndex));
                this.bakedImages.put(bakedTextureIndex, bakedImage);
                bakedImage.Bake(this);
                if (Thread.interrupted()) {
                    Debug.Log("Baking: interrupted.");
                    this.eventBus.publish(new SLBakingProgressEvent(false, true, 0));
                    finishBaking((SLTextureEntry) null);
                    return;
                }
                try {
                    File file = new File(cacheDir, bakedTextureIndex.toString() + ".j2k");
                    bakedImage.SaveToJPEG2K(file);
                    BakedImageUploadRequest bakedImageUploadRequest = new BakedImageUploadRequest(bakedImage, bakedTextureIndex, file);
                    bakedImageUploadRequest.setOnUploadComplete(this);
                    this.uploader.BeginUpload(bakedImageUploadRequest);
                } catch (IOException e2) {
                    e2.printStackTrace();
                }
                Debug.Log("Baking: Done layer " + bakedTextureIndex);
            }
        }
        Debug.Log("Baking: Baked all layers.");
    }

    private Map<Integer, Float> calcAllParamValues(Table<SLWearableType, UUID, SLWearable> table) {
        HashMap hashMap = new HashMap();
        for (Map.Entry entry : SLAvatarParams.paramByIDs.entrySet()) {
            hashMap.put((Integer) entry.getKey(), Float.valueOf(((SLAvatarParams.AvatarParam) ((SLAvatarParams.ParamSet) entry.getValue()).params.get(0)).defValue));
        }
        for (SLWearable wearableData : table.values()) {
            SLWearableData wearableData2 = wearableData.getWearableData();
            if (wearableData2 != null) {
                for (SLWearableData.WearableParam wearableParam : wearableData2.params) {
                    hashMap.put(Integer.valueOf(wearableParam.paramIndex), Float.valueOf(wearableParam.paramValue));
                    SLAvatarParams.ParamSet paramSet = SLAvatarParams.paramByIDs.get(Integer.valueOf(wearableParam.paramIndex));
                    if (paramSet != null) {
                        SLAvatarParams.AvatarParam avatarParam = (SLAvatarParams.AvatarParam) paramSet.params.get(0);
                        if (avatarParam.drivenParams != null) {
                            for (SLAvatarParams.DrivenParam drivenParam : avatarParam.drivenParams) {
                                SLAvatarParams.ParamSet paramSet2 = SLAvatarParams.paramByIDs.get(Integer.valueOf(drivenParam.drivenID));
                                if (paramSet2 != null) {
                                    for (SLAvatarParams.AvatarParam drivenWeight : paramSet2.params) {
                                        hashMap.put(Integer.valueOf(drivenParam.drivenID), Float.valueOf(AvatarSkeleton.getDrivenWeight(wearableParam.paramValue, avatarParam, drivenParam, drivenWeight)));
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
        return hashMap;
    }

    private void finishBaking(SLTextureEntry sLTextureEntry) {
        this.avatarAppearance.finishBaking(this, sLTextureEntry);
    }

    private boolean isTexturesReady() {
        boolean z = true;
        for (List<WearableTextureData> it : this.wearables.values()) {
            for (WearableTextureData textureReady : it) {
                if (!textureReady.getTextureReady()) {
                    z = false;
                }
            }
        }
        return z;
    }

    private boolean isWearingSkirt() {
        return !this.wornWearables.row(SLWearableType.WT_SKIRT).isEmpty();
    }

    /* access modifiers changed from: private */
    public void notifyTextureReady() {
        synchronized (this.textureReadyLock) {
            this.textureReadyLock.notifyAll();
        }
    }

    public void OnTextureUploadComplete(SLTextureUploadRequest sLTextureUploadRequest) {
        boolean z;
        int i;
        int i2;
        if (sLTextureUploadRequest instanceof BakedImageUploadRequest) {
            BakedImageUploadRequest bakedImageUploadRequest = (BakedImageUploadRequest) sLTextureUploadRequest;
            Debug.Log("Baking: texture " + bakedImageUploadRequest.bakedIndex + " uploaded, UUID = " + bakedImageUploadRequest.getTextureID());
            bakedImageUploadRequest.bakedImage.setUploadedID(bakedImageUploadRequest.getTextureID());
            this.bakedImages.put(bakedImageUploadRequest.bakedIndex, bakedImageUploadRequest.bakedImage);
            boolean isWearingSkirt = isWearingSkirt();
            BakedTextureIndex[] values = BakedTextureIndex.values();
            int length = values.length;
            int i3 = 0;
            boolean z2 = true;
            int i4 = 0;
            int i5 = 0;
            while (i3 < length) {
                BakedTextureIndex bakedTextureIndex = values[i3];
                if (bakedTextureIndex != BakedTextureIndex.BAKED_SKIRT || !(!isWearingSkirt)) {
                    int i6 = i5 + 1;
                    if (!this.bakedImages.containsKey(bakedTextureIndex)) {
                        i = i4 + 1;
                        i2 = i6;
                        z = false;
                    } else if (this.bakedImages.get(bakedTextureIndex).getUploadedID() == null) {
                        i = i4 + 1;
                        i2 = i6;
                        z = false;
                    } else {
                        z = z2;
                        i = i4;
                        i2 = i6;
                    }
                } else {
                    boolean z3 = z2;
                    i = i4;
                    i2 = i5;
                    z = z3;
                }
                i3++;
                boolean z4 = z;
                i5 = i2;
                i4 = i;
                z2 = z4;
            }
            if (z2) {
                this.eventBus.publish(new SLBakingProgressEvent(false, true, 100));
                Debug.Log("Baking: all textures uploaded.");
                finishBaking(PrepareAvatarTextureEntry());
                return;
            }
            this.eventBus.publish(new SLBakingProgressEvent(false, false, ((i5 - i4) * 100) / i5));
        }
    }

    public void cancel() {
        this.bakingThread.interrupt();
    }

    /* access modifiers changed from: package-private */
    public List<OpenJPEG> getLocalTexture(AvatarTextureFaceIndex avatarTextureFaceIndex) throws DefaultTextureException {
        OpenJPEG textureData;
        boolean z = false;
        LinkedList linkedList = null;
        for (List<WearableTextureData> it : this.wearables.values()) {
            for (WearableTextureData wearableTextureData : it) {
                if (wearableTextureData.getTexture().layer == avatarTextureFaceIndex.ordinal()) {
                    if (wearableTextureData.getTexture().textureID != null && wearableTextureData.getTexture().textureID.equals(DrawableAvatarPart.DEFAULT_AVATAR_TEXTURE)) {
                        z = true;
                    } else if (!(wearableTextureData.textureData == null || (textureData = wearableTextureData.getTextureData()) == null)) {
                        if (linkedList == null) {
                            linkedList = new LinkedList();
                        }
                        linkedList.add(textureData);
                    }
                }
            }
        }
        if (linkedList != null || !z) {
            return linkedList;
        }
        throw new DefaultTextureException();
    }

    /* access modifiers changed from: package-private */
    public float getParamWeight(int i, SLAvatarParams.AvatarParam avatarParam) {
        Float f = this.paramValues.get(Integer.valueOf(i));
        return f != null ? f.floatValue() : avatarParam.defValue;
    }
}
