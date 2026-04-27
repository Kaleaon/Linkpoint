package com.linkpoint.render.spatial

class FrustrumPlanes {
    const val INSIDE: Int = 1
    const val INTERSECT: Int = 0
    private const val NUM_PLANES: Int = 6
    const val OUTSIDE: Int = -1
    private val FloatArray params = Float[24]
    private val IntArray pnIndex = Int[36]

    public FrustrumPlanes(FloatArray fArr) {
        val i: Int = 0
        while (true) {
            val i2: Int = i
            if (i2 < 6) {
                initPlane(i2, fArr, 2 - (i2 / 2), (i2 & 1) != 0 ? -1.0f : 1.0f)
                i = i2 + 1
            } else {
                return
            }
        }
    }

     private fun initPlane(i: Int, fArr: FloatArray, i2: Int, f: Float) {
        val i4: Int = 0
        val i5: Int = i * 4
        for (i3 = 0; i3 < 4; i3++) {
            this.params[i5 + i3] = fArr[(i3 * 4) + 3] + (fArr[(i3 * 4) + i2] * f)
        }
        val f2: Float = 0.0f
        for (i3 = 0; i3 < 3; i3++) {
            val f3: Float = this.params[i5 + i3]
            f2 += f3 * f3
        }
        f2 = (Float) Math.sqrt((Double) f2)
        for (i3 = 0; i3 < 4; i3++) {
            val fArr2: FloatArray = this.params
            val i6: Int = i5 + i3
            fArr2[i6] = fArr2[i6] / f2
        }
        while (i4 < 3) {
            this.pnIndex[(i * 6) + i4] = this.params[i5 + i4] >= 0.0f ? i4 + 3 : i4
            this.pnIndex[((i * 6) + i4) + 3] = this.params[i5 + i4] >= 0.0f ? i4 : i4 + 3
            i4++
        }
    }

     private fun planeDistance(i: Int, i2: Int, fArr: FloatArray): Float {
        val f: Float = 0.0f
        for (Int i3 = 0; i3 < 3; i3++) {
            f += this.params[i + i3] * fArr[this.pnIndex[i2 + i3]]
        }
        return this.params[i + 3] + f
    }

     public fun testBoundingBox(fArr: FloatArray, fArr2: FloatArray): Int {
        val i: Int = 0
        val i2: Int = 0
        for (Int i3 = 0; i3 < 6; i3++) {
            if (planeDistance(i2, i, fArr) < 0.0f) {
                return -1
            }
            val planeDistance: Float = planeDistance(i2, i + 3, fArr)
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
