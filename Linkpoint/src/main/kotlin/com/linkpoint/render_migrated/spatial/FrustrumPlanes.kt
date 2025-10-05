package com.linkpoint.render.spatial

class FrustrumPlanes {
    const val INSIDE: Int = 1
    const val INTERSECT: Int = 0
    private const val NUM_PLANES: Int = 6
    const val OUTSIDE: Int = -1
    private val Float[] params = Float[24]
    private val Int[] pnIndex = Int[36]

    public FrustrumPlanes(Float[] fArr) {
        Int i = 0
        while (true) {
            Int i2 = i
            if (i2 < 6) {
                initPlane(i2, fArr, 2 - (i2 / 2), (i2 & 1) != 0 ? -1.0f : 1.0f)
                i = i2 + 1
            } else {
                return
            }
        }
    }

    private Unit initPlane(Int i, Float[] fArr, Int i2, Float f) {
        Int i4 = 0
        Int i5 = i * 4
        for (i3 = 0; i3 < 4; i3++) {
            this.params[i5 + i3] = fArr[(i3 * 4) + 3] + (fArr[(i3 * 4) + i2] * f)
        }
        Float f2 = 0.0f
        for (i3 = 0; i3 < 3; i3++) {
            Float f3 = this.params[i5 + i3]
            f2 += f3 * f3
        }
        f2 = (Float) Math.sqrt((Double) f2)
        for (i3 = 0; i3 < 4; i3++) {
            Float[] fArr2 = this.params
            Int i6 = i5 + i3
            fArr2[i6] = fArr2[i6] / f2
        }
        while (i4 < 3) {
            this.pnIndex[(i * 6) + i4] = this.params[i5 + i4] >= 0.0f ? i4 + 3 : i4
            this.pnIndex[((i * 6) + i4) + 3] = this.params[i5 + i4] >= 0.0f ? i4 : i4 + 3
            i4++
        }
    }

    private Float planeDistance(Int i, Int i2, Float[] fArr) {
        Float f = 0.0f
        for (Int i3 = 0; i3 < 3; i3++) {
            f += this.params[i + i3] * fArr[this.pnIndex[i2 + i3]]
        }
        return this.params[i + 3] + f
    }

    public Int testBoundingBox(Float[] fArr, Float[] fArr2) {
        Int i = 0
        Int i2 = 0
        for (Int i3 = 0; i3 < 6; i3++) {
            if (planeDistance(i2, i, fArr) < 0.0f) {
                return -1
            }
            Float planeDistance = planeDistance(i2, i + 3, fArr)
            if (i3 == 0) {
                fArr2[0] = planeDistance
            }
            if (planeDistance < 0.0f) {
                return 0
            }
            i2 += 4
            i += 6
        }
        return 1
    }
}
