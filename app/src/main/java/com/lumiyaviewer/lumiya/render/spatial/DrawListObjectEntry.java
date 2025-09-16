package com.lumiyaviewer.lumiya.render.spatial;
import java.util.*;

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
            float f;
            Vector3Array objectCoords = this.objectInfo.getObjectCoords();
            float[] data = objectCoords.getData();
            int elementOffset = objectCoords.getElementOffset(1);
            for (i = 0; i < 3; i++) {
                float[] fArr2 = this.boundingBox;
                f = fArr[i + 12];
                this.boundingBox[i + 3] = f;
                fArr2[i] = f;
            }
            for (int i2 = 0; i2 < 3; i2++) {
                for (i = 0; i < 3; i++) {
                    f = fArr[(i2 * 4) + i] * ((-data[elementOffset + i]) / 2.0f);
                    float f2 = fArr[(i2 * 4) + i] * (data[elementOffset + i] / 2.0f);
                    float[] fArr3;
                    if (f < f2) {
                        fArr3 = this.boundingBox;
                        fArr3[i2] = f + fArr3[i2];
                        float[] fArr4 = this.boundingBox;
                        i3 = i2 + 3;
                        fArr4[i3] = f2 + fArr4[i3];
                    } else {
                        fArr3 = this.boundingBox;
                        fArr3[i2] = f2 + fArr3[i2];
                        float[] fArr5 = this.boundingBox;
                        i3 = i2 + 3;
                        fArr5[i3] = f + fArr5[i3];
                    }
                }
            }
            i = 0;
            while (i < 3) {
                float f3 = i == 2 ? 4096.0f : 256.0f;
                this.boundingBox[i] = Math.min(f3, Math.max(0.0f, this.boundingBox[i]));
                this.boundingBox[i + 3] = Math.min(f3, Math.max(0.0f, this.boundingBox[i + 3]));
                i++;
            }
        }
    }
}
