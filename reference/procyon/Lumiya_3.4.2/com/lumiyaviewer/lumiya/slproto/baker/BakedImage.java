// 
// Decompiled by Procyon v0.6.0
// 

package com.lumiyaviewer.lumiya.slproto.baker;

import android.graphics.Bitmap;
import java.io.IOException;
import java.io.File;
import java.util.UUID;
import com.lumiyaviewer.lumiya.openjpeg.OpenJPEG;

public class BakedImage
{
    private BakeLayerSet layerSet;
    private OpenJPEG resultImage;
    private UUID uploadedID;
    
    public BakedImage(final BakeLayerSet layerSet) {
        this.layerSet = layerSet;
        (this.resultImage = new OpenJPEG(layerSet.width, layerSet.height, 4, 4, 1, -1)).setComponent(4, (byte)(-1));
    }
    
    public void Bake(final BakeProcess bakeProcess) {
        final int n = 0;
        final BakeLayer[] layers = this.layerSet.layers;
        for (int length = layers.length, i = 0; i < length; ++i) {
            layers[i].Bake(this.resultImage, bakeProcess);
        }
        if (this.layerSet.clear_alpha || this.layerSet.maskLayers.length > 0) {
            this.resultImage.setComponent(3, (byte)(-1));
        }
        final BakeLayer[] maskLayers = this.layerSet.maskLayers;
        for (int length2 = maskLayers.length, j = n; j < length2; ++j) {
            maskLayers[j].BakeAlpha(this.resultImage, bakeProcess);
        }
    }
    
    public void SaveToJPEG2K(final File file) throws IOException {
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
    
    public void setUploadedID(final UUID uploadedID) {
        this.uploadedID = uploadedID;
    }
}
