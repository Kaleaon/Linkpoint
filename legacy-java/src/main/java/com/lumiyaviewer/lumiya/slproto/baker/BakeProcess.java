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
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.UUID;

/* loaded from: classes.dex */
public class BakeProcess implements SLTextureUploadRequest.TextureUploadCompleteListener {
    private final SLAvatarAppearance avatarAppearance;
    private final Thread bakingThread;
    private final EventBus eventBus;
    private Map<Integer, Float> paramValues;
    private final SLTextureUploader uploader;
    private final Table<SLWearableType, UUID, SLWearable> wornWearables;
    private final Map<SLWearable, List<WearableTextureData>> wearables = new IdentityHashMap();
    private final Object textureReadyLock = new Object();
    private final Map<BakedTextureIndex, BakedImage> bakedImages = new EnumMap(BakedTextureIndex.class);

    /* JADX INFO: Access modifiers changed from: private */
    /* loaded from: classes.dex */
    public static class BakedImageUploadRequest extends SLTextureUploadRequest {
        final BakedImage bakedImage;
        final BakedTextureIndex bakedIndex;

        BakedImageUploadRequest(BakedImage bakedImage, BakedTextureIndex bakedTextureIndex, File file) {
            super(file, bakedTextureIndex.ordinal());
            this.bakedImage = bakedImage;
            this.bakedIndex = bakedTextureIndex;
        }
    }

    /* loaded from: classes.dex */
    static class DefaultTextureException extends Exception {
        DefaultTextureException() {
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    /* loaded from: classes.dex */
    public class WearableTextureData implements ResourceConsumer {
        private final SLWearableData.WearableTexture texture;
        private volatile OpenJPEG textureData;
        private volatile boolean textureReady = false;

        WearableTextureData(SLWearableData.WearableTexture wearableTexture) {
            this.texture = wearableTexture;
        }

        @Override // com.lumiyaviewer.lumiya.res.ResourceConsumer
        public void OnResourceReady(Object obj, boolean z) {
            if (obj instanceof OpenJPEG) {
                this.textureData = (OpenJPEG) obj;
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
            TextureCache.getInstance().RequestResource(DrawableTextureParams.create(this.texture.textureID, TextureClass.Asset), this);
        }
    }

    public BakeProcess(Table<SLWearableType, UUID, SLWearable> table, SLAvatarAppearance sLAvatarAppearance, SLTextureUploader sLTextureUploader, EventBus eventBus) {
        Debug.Printf("Baking: new BakeProcess created", new Object[0]);
        this.avatarAppearance = sLAvatarAppearance;
        this.wornWearables = table;
        this.uploader = sLTextureUploader;
        this.eventBus = eventBus;
        for (SLWearable sLWearable : table.values()) {
            SLWearableData wearableData = sLWearable.getWearableData();
            if (wearableData != null) {
                ArrayList arrayList = new ArrayList(wearableData.textures.size());
                Iterator<SLWearableData.WearableTexture> it = wearableData.textures.iterator();
                while (it.hasNext()) {
                    arrayList.add(new WearableTextureData(it.next()));
                }
                this.wearables.put(sLWearable, arrayList);
            }
        }
        this.bakingThread = new Thread(new Runnable() { // from class: com.lumiyaviewer.lumiya.slproto.baker.-$Lambda$qb61PwDoxRPFEOdyYwns3UfUTbM
            private final /* synthetic */ void $m$0() {
                ((BakeProcess) this).m150com_lumiyaviewer_lumiya_slproto_baker_BakeProcessmthref0();
            }

            @Override // java.lang.Runnable
            public final void run() {
                $m$0();
            }
        }, "Baker");
        this.bakingThread.start();
    }

    private SLTextureEntry PrepareAvatarTextureEntry() {
        UUID uploadedID;
        SLTextureEntryFace create = SLTextureEntryFace.create(new MutableSLTextureEntryFace(-1));
        SLTextureEntryFace[] sLTextureEntryFaceArr = new SLTextureEntryFace[32];
        for (BakedTextureIndex bakedTextureIndex : BakedTextureIndex.valuesCustom()) {
            int ordinal = bakedTextureIndex.getFaceIndex().ordinal();
            BakedImage bakedImage = this.bakedImages.get(bakedTextureIndex);
            if (bakedImage != null && (uploadedID = bakedImage.getUploadedID()) != null) {
                MutableSLTextureEntryFace mutableSLTextureEntryFace = new MutableSLTextureEntryFace(0);
                mutableSLTextureEntryFace.setTextureID(uploadedID);
                sLTextureEntryFaceArr[ordinal] = SLTextureEntryFace.create(mutableSLTextureEntryFace);
            }
        }
        return SLTextureEntry.create(create, sLTextureEntryFaceArr);
    }

    /* JADX INFO: Access modifiers changed from: private */
    /* renamed from: bakeAppearance, reason: merged with bridge method [inline-methods] */
    public void m150com_lumiyaviewer_lumiya_slproto_baker_BakeProcessmthref0() {
        Debug.Printf("Baking: Requesting texture data.", new Object[0]);
        Iterator<T> it = this.wearables.values().iterator();
        while (it.hasNext()) {
            Iterator it2 = ((List) it.next()).iterator();
            while (it2.hasNext()) {
                ((WearableTextureData) it2.next()).requestData();
            }
        }
        synchronized (this.textureReadyLock) {
            while (!isTexturesReady()) {
                try {
                    this.textureReadyLock.wait();
                } catch (InterruptedException e) {
                    finishBaking(null);
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
        for (BakedTextureIndex bakedTextureIndex : BakedTextureIndex.valuesCustom()) {
            if (Thread.interrupted()) {
                Debug.Log("Baking: interrupted.");
                this.eventBus.publish(new SLBakingProgressEvent(false, true, 0));
                finishBaking(null);
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
                    finishBaking(null);
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
        for (Map.Entry<Integer, SLAvatarParams.ParamSet> entry : SLAvatarParams.paramByIDs.entrySet()) {
            hashMap.put(entry.getKey(), Float.valueOf(entry.getValue().params.get(0).defValue));
        }
        Iterator<T> it = table.values().iterator();
        while (it.hasNext()) {
            SLWearableData wearableData = ((SLWearable) it.next()).getWearableData();
            if (wearableData != null) {
                for (SLWearableData.WearableParam wearableParam : wearableData.params) {
                    hashMap.put(Integer.valueOf(wearableParam.paramIndex), Float.valueOf(wearableParam.paramValue));
                    SLAvatarParams.ParamSet paramSet = SLAvatarParams.paramByIDs.get(Integer.valueOf(wearableParam.paramIndex));
                    if (paramSet != null) {
                        SLAvatarParams.AvatarParam avatarParam = paramSet.params.get(0);
                        if (avatarParam.drivenParams != null) {
                            for (SLAvatarParams.DrivenParam drivenParam : avatarParam.drivenParams) {
                                SLAvatarParams.ParamSet paramSet2 = SLAvatarParams.paramByIDs.get(Integer.valueOf(drivenParam.drivenID));
                                if (paramSet2 != null) {
                                    Iterator<SLAvatarParams.AvatarParam> it2 = paramSet2.params.iterator();
                                    while (it2.hasNext()) {
                                        hashMap.put(Integer.valueOf(drivenParam.drivenID), Float.valueOf(AvatarSkeleton.getDrivenWeight(wearableParam.paramValue, avatarParam, drivenParam, it2.next())));
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
        Iterator<T> it = this.wearables.values().iterator();
        boolean z = true;
        while (it.hasNext()) {
            Iterator it2 = ((List) it.next()).iterator();
            while (it2.hasNext()) {
                if (!((WearableTextureData) it2.next()).getTextureReady()) {
                    z = false;
                }
            }
        }
        return z;
    }

    private boolean isWearingSkirt() {
        return !this.wornWearables.row(SLWearableType.WT_SKIRT).isEmpty();
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void notifyTextureReady() {
        synchronized (this.textureReadyLock) {
            this.textureReadyLock.notifyAll();
        }
    }

    @Override // com.lumiyaviewer.lumiya.slproto.modules.texuploader.SLTextureUploadRequest.TextureUploadCompleteListener
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
            BakedTextureIndex[] valuesCustom = BakedTextureIndex.valuesCustom();
            int length = valuesCustom.length;
            int i3 = 0;
            boolean z2 = true;
            int i4 = 0;
            int i5 = 0;
            while (i3 < length) {
                BakedTextureIndex bakedTextureIndex = valuesCustom[i3];
                if (bakedTextureIndex == BakedTextureIndex.BAKED_SKIRT && (!isWearingSkirt)) {
                    boolean z3 = z2;
                    i = i4;
                    i2 = i5;
                    z = z3;
                } else {
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
                }
                i3++;
                boolean z4 = z;
                i5 = i2;
                i4 = i;
                z2 = z4;
            }
            if (!z2) {
                this.eventBus.publish(new SLBakingProgressEvent(false, false, ((i5 - i4) * 100) / i5));
            } else {
                this.eventBus.publish(new SLBakingProgressEvent(false, true, 100));
                Debug.Log("Baking: all textures uploaded.");
                finishBaking(PrepareAvatarTextureEntry());
            }
        }
    }

    public void cancel() {
        this.bakingThread.interrupt();
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public List<OpenJPEG> getLocalTexture(AvatarTextureFaceIndex avatarTextureFaceIndex) throws DefaultTextureException {
        OpenJPEG textureData;
        Iterator<T> it = this.wearables.values().iterator();
        boolean z = false;
        LinkedList linkedList = null;
        while (it.hasNext()) {
            for (WearableTextureData wearableTextureData : (List) it.next()) {
                if (wearableTextureData.getTexture().layer == avatarTextureFaceIndex.ordinal()) {
                    if (wearableTextureData.getTexture().textureID != null && wearableTextureData.getTexture().textureID.equals(DrawableAvatarPart.DEFAULT_AVATAR_TEXTURE)) {
                        z = true;
                    } else if (wearableTextureData.textureData != null && (textureData = wearableTextureData.getTextureData()) != null) {
                        if (linkedList == null) {
                            linkedList = new LinkedList();
                        }
                        linkedList.add(textureData);
                    }
                }
            }
        }
        if (linkedList == null && z) {
            throw new DefaultTextureException();
        }
        return linkedList;
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public float getParamWeight(int i, SLAvatarParams.AvatarParam avatarParam) {
        Float f = this.paramValues.get(Integer.valueOf(i));
        return f != null ? f.floatValue() : avatarParam.defValue;
    }
}
