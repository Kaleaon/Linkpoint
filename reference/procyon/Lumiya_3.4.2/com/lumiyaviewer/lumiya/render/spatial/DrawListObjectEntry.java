// 
// Decompiled by Procyon v0.6.0
// 

package com.lumiyaviewer.lumiya.render.spatial;

import com.lumiyaviewer.lumiya.slproto.types.Vector3Array;
import javax.annotation.Nonnull;
import com.lumiyaviewer.lumiya.slproto.objects.SLObjectInfo;

public abstract class DrawListObjectEntry extends DrawListEntry
{
    @Nonnull
    final SLObjectInfo objectInfo;
    
    public DrawListObjectEntry(@Nonnull final SLObjectInfo objectInfo) {
        this.objectInfo = objectInfo;
    }
    
    @Nonnull
    public SLObjectInfo getObjectInfo() {
        return this.objectInfo;
    }
    
    public void updateBoundingBox() {
        final float[] worldMatrix = this.objectInfo.worldMatrix;
        if (worldMatrix != null) {
            final Vector3Array objectCoords = this.objectInfo.getObjectCoords();
            final float[] data = objectCoords.getData();
            final int elementOffset = objectCoords.getElementOffset(1);
            for (int i = 0; i < 3; ++i) {
                this.boundingBox[i] = (this.boundingBox[i + 3] = worldMatrix[i + 12]);
            }
            for (int j = 0; j < 3; ++j) {
                for (int k = 0; k < 3; ++k) {
                    final float n = worldMatrix[j * 4 + k] * (-data[elementOffset + k] / 2.0f);
                    final float n2 = worldMatrix[j * 4 + k] * (data[elementOffset + k] / 2.0f);
                    if (n < n2) {
                        final float[] boundingBox = this.boundingBox;
                        boundingBox[j] += n;
                        final float[] boundingBox2 = this.boundingBox;
                        final int n3 = j + 3;
                        boundingBox2[n3] += n2;
                    }
                    else {
                        final float[] boundingBox3 = this.boundingBox;
                        boundingBox3[j] += n2;
                        final float[] boundingBox4 = this.boundingBox;
                        final int n4 = j + 3;
                        boundingBox4[n4] += n;
                    }
                }
            }
            for (int l = 0; l < 3; ++l) {
                float n5;
                if (l == 2) {
                    n5 = 4096.0f;
                }
                else {
                    n5 = 256.0f;
                }
                this.boundingBox[l] = Math.min(n5, Math.max(0.0f, this.boundingBox[l]));
                this.boundingBox[l + 3] = Math.min(n5, Math.max(0.0f, this.boundingBox[l + 3]));
            }
        }
    }
}
