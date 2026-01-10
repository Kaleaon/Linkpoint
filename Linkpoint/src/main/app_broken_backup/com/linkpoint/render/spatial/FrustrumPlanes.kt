package com.linkpoint.render.spatial

import kotlin.math.*

class FrustrumPlanes {
    val INSIDE: Int = 1
    val INTERSECT: Int = 0
    private val NUM_PLANES: Int = 6
    val OUTSIDE: Int = -1
    private val params: FloatArray = FloatArray(24)
    private val pnIndex: IntArray = IntArray(36)

    constructor(fArr: FloatArray) {
        var i: Int = 0
        while (true) {
            var i2: Int = i
            if (i2 < 6) {
                initPlane(i2, fArr, 2 - (i2 / 2), (i2 & 1) != 0 ? -1.0f : 1.0f)
                i = i2 + 1
            } else {
                return
            }
        }
    }

    private fun initPlane(i: Int, fArr: FloatArray, i2: Int, f: Float)  {
        var i4: Int = 0
        var i5: Int = i * 4
        for (i3 = 0; i3 < 4; i3++) {
            this.params[i5 + i3] = fArr[(i3 * 4) + 3] + (fArr[(i3 * 4) + i2] * f)
        }
        var f2: Float = 0.0f
        for (i3 = 0; i3 < 3; i3++) {
            var f3: Float = this.params[i5 + i3]
            f2 += f3 * f3
        }
        f2 = sqrt(f2.toDouble()).toFloat()
        for (i3 = 0; i3 < 4; i3++) {
            FloatArray fArr2 = this.params
            var i6: Int = i5 + i3
            fArr2[i6] = fArr2[i6] / f2
        }
        while (i4 < 3) {
            this.pnIndex[(i * 6) + i4] = this.params[i5 + i4] >= 0.0f ? i4 + 3 : i4
            this.pnIndex[((i * 6) + i4) + 3] = this.params[i5 + i4] >= 0.0f ? i4 : i4 + 3
            i4++
        }
    }

    private fun planeDistance(i: Int, i2: Int, fArr: FloatArray): Float {
        var f: Float = 0.0f
        for (i3 in 0 until 3) {
            f += this.params[i + i3] * fArr[this.pnIndex[i2 + i3]]
        }
        return this.params[i + 3] + f
    }

    fun testBoundingBox(fArr: FloatArray, fArr2: FloatArray): Int {
        var i: Int = 0
        var i2: Int = 0
        for (i3 in 0 until 6) {
            if (planeDistance(i2, i, fArr) < 0.0f) {
                return -1
            }
            var planeDistance: Float = planeDistance(i2, i + 3, fArr)
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
