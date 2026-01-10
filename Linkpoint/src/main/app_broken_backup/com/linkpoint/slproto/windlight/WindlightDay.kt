package com.linkpoint.slproto.windlight

class WindlightDay {
    private val defaultPresets: Array<String> = {"A%2D12AM", "A%2D3AM", "A%2D6AM", "A%2D9AM", "A%2D12PM", "A%2D3PM", "A%2D6PM", "A%2D9PM"}
    private val hourTable: FloatArray = {0.0f, 0.125f, 0.25f, 0.375f, 0.5f, 0.625f, 0.75f, 0.875f}
    private WindlightPreset[] presets = WindlightPreset[defaultPresets.size]

    WindlightDay() {
        for (i in 0 until this.presets.size) {
            this.presets[i] = WindlightPreset("windlight/" + defaultPresets[i] + ".xml")
        }
    }

    fun InterpolatePreset(WindlightPreset windlightPreset, Float f)  {
        var i2: Int = 0
        var length: Int = hourTable.size - 1
        while (true) {
            if (length < 0) {
                i = -1
                break
            } else if (f >= hourTable[length]) {
                i = length
                break
            } else {
                length--
            }
        }
        if (i != -1) {
            var i3: Int = i + 1
            if (i3 < hourTable.size) {
                i2 = i3
            }
            var f2: Float = hourTable[i]
            var f3: Float = hourTable[i2]
            if (f3 < f2) {
                f3 += 1.0f
            }
            windlightPreset.setByInterpolation(this.presets[i], this.presets[i2], (f - f2) / (f3 - f2))
        }
    }
}
