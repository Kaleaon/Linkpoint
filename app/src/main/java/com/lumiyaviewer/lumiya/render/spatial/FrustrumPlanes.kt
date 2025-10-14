package com.lumiyaviewer.lumiya.render.spatial

class FrustrumPlanes {
    Int INSIDE = 1
    Int INTERSECT = 0
    private Int NUM_PLANES = 6
    Int OUTSIDE = -1
    private Float[] params = Float[24]
    private Int[] pnIndex = Int[36]

    constructor(fArr: FloatArray) {
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

    private fun initPlane(i: Int, fArr: FloatArray, i2: Int, f: Float): Unit {
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

    private fun planeDistance(i: Int, i2: Int, fArr: FloatArray): Float {
        Float f = 0.0f
        for (Int i3 = 0; i3 < 3; i3++) {
            f += this.params[i + i3] * fArr[this.pnIndex[i2 + i3]]
        }
        return this.params[i + 3] + f
    }

    fun testBoundingBox(fArr: FloatArray, fArr2: FloatArray): Int {
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
