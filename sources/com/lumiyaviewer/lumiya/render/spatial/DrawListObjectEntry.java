package com.lumiyaviewer.lumiya.render.spatial;

import com.lumiyaviewer.lumiya.slproto.objects.SLObjectInfo;
import com.lumiyaviewer.lumiya.slproto.types.Vector3Array;
import javax.annotation.Nonnull;

public abstract class DrawListObjectEntry extends DrawListEntry {
    @Nonnull
    final SLObjectInfo objectInfo;

    public DrawListObjectEntry(@Nonnull SLObjectInfo sLObjectInfo) {
        this.objectInfo = sLObjectInfo;
    }

    @Nonnull
    public SLObjectInfo getObjectInfo() {
        return this.objectInfo;
    }

    public void updateBoundingBox() {
        float[] fArr = this.objectInfo.worldMatrix;
        if (fArr != null) {
            Vector3Array objectCoords = this.objectInfo.getObjectCoords();
            float[] data = objectCoords.getData();
            int elementOffset = objectCoords.getElementOffset(1);
            for (int i = 0; i < 3; i++) {
                float[] fArr2 = this.boundingBox;
                float f = fArr[i + 12];
                this.boundingBox[i + 3] = f;
                fArr2[i] = f;
            }
            for (int i2 = 0; i2 < 3; i2++) {
                for (int i3 = 0; i3 < 3; i3++) {
                    float f2 = fArr[(i2 * 4) + i3] * ((-data[elementOffset + i3]) / 2.0f);
                    float f3 = fArr[(i2 * 4) + i3] * (data[elementOffset + i3] / 2.0f);
                    if (f2 < f3) {
                        float[] fArr3 = this.boundingBox;
                        fArr3[i2] = f2 + fArr3[i2];
                        float[] fArr4 = this.boundingBox;
                        int i4 = i2 + 3;
                        fArr4[i4] = f3 + fArr4[i4];
                    } else {
                        float[] fArr5 = this.boundingBox;
                        fArr5[i2] = f3 + fArr5[i2];
                        float[] fArr6 = this.boundingBox;
                        int i5 = i2 + 3;
                        fArr6[i5] = f2 + fArr6[i5];
                    }
                }
            }
            int i6 = 0;
            while (i6 < 3) {
                float f4 = i6 == 2 ? 4096.0f : 256.0f;
                this.boundingBox[i6] = Math.min(f4, Math.max(0.0f, this.boundingBox[i6]));
                this.boundingBox[i6 + 3] = Math.min(f4, Math.max(0.0f, this.boundingBox[i6 + 3]));
                i6++;
            }
        }
    }
}
