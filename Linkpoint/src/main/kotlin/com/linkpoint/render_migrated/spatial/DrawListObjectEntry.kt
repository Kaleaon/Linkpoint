package com.linkpoint.render.spatial
import java.util.*

import com.linkpoint.slproto.objects.SLObjectInfo
import com.linkpoint.slproto.types.Vector3Array
import javax.annotation.Nonnull

abstract class DrawListObjectEntry : DrawListEntry() {
    final SLObjectInfo objectInfo

    public DrawListObjectEntry(SLObjectInfo sLObjectInfo) {
        this.objectInfo = sLObjectInfo
    }

    public SLObjectInfo getObjectInfo() {
        return this.objectInfo
    }

    fun updateBoundingBox() {
        Float[] fArr = this.objectInfo.worldMatrix
        if (fArr != null) {
            Float f
            Vector3Array objectCoords = this.objectInfo.getObjectCoords()
            Float[] data = objectCoords.getData()
            Int elementOffset = objectCoords.getElementOffset(1)
            for (i = 0; i < 3; i++) {
                Float[] fArr2 = this.boundingBox
                f = fArr[i + 12]
                this.boundingBox[i + 3] = f
                fArr2[i] = f
            }
            for (Int i2 = 0; i2 < 3; i2++) {
                for (i = 0; i < 3; i++) {
                    f = fArr[(i2 * 4) + i] * ((-data[elementOffset + i]) / 2.0f)
                    Float f2 = fArr[(i2 * 4) + i] * (data[elementOffset + i] / 2.0f)
                    Float[] fArr3
                    if (f < f2) {
                        fArr3 = this.boundingBox
                        fArr3[i2] = f + fArr3[i2]
                        Float[] fArr4 = this.boundingBox
                        i3 = i2 + 3
                        fArr4[i3] = f2 + fArr4[i3]
                    } else {
                        fArr3 = this.boundingBox
                        fArr3[i2] = f2 + fArr3[i2]
                        Float[] fArr5 = this.boundingBox
                        i3 = i2 + 3
                        fArr5[i3] = f + fArr5[i3]
                    }
                }
            }
            i = 0
            while (i < 3) {
                Float f3 = i == 2 ? 4096.0f : 256.0f
                this.boundingBox[i] = Math.min(f3, Math.max(0.0f, this.boundingBox[i]))
                this.boundingBox[i + 3] = Math.min(f3, Math.max(0.0f, this.boundingBox[i + 3]))
                i++
            }
        }
    }
}
