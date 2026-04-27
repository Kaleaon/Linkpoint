package com.lumiyaviewer.lumiya.slproto.baker;

import android.graphics.Bitmap;
import com.lumiyaviewer.lumiya.openjpeg.OpenJPEG;
import java.io.File;
import java.io.IOException;
import java.util.UUID;

public class BakedImage {
    private BakeLayerSet layerSet;
    private OpenJPEG resultImage;
    private UUID uploadedID;

    public BakedImage(BakeLayerSet bakeLayerSet) {
        this.layerSet = bakeLayerSet;
        this.resultImage = new OpenJPEG(bakeLayerSet.width, bakeLayerSet.height, 4, 4, 1, -1);
        this.resultImage.setComponent(4, (byte) -1);
    }

    public void Bake(BakeProcess bakeProcess) {
        for (BakeLayer Bake : this.layerSet.layers) {
            Bake.Bake(this.resultImage, bakeProcess);
        }
        if (this.layerSet.clear_alpha || this.layerSet.maskLayers.length > 0) {
            this.resultImage.setComponent(3, (byte) -1);
        }
        for (BakeLayer BakeAlpha : this.layerSet.maskLayers) {
            BakeAlpha.BakeAlpha(this.resultImage, bakeProcess);
        }
    }

    public void SaveToJPEG2K(File file) throws IOException {
        this.resultImage.SaveJPEG2K(file);
    }

    public Bitmap getAsBitmap() {
        return this.resultImage.getAsBitmap();
    }

    public OpenJPEG getBakedImage() {
        return this.resultImage;
    }

    public UUID getUploadedID() {
        return this.uploadedID;
    }

    public void setUploadedID(UUID uuid) {
        this.uploadedID = uuid;
    }
}
