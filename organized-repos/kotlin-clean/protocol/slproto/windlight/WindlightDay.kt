package com.linkpoint.slproto.windlight

class WindlightDay {
    private const val String[] defaultPresets = {"A%2D12AM", "A%2D3AM", "A%2D6AM", "A%2D9AM", "A%2D12PM", "A%2D3PM", "A%2D6PM", "A%2D9PM"}
    private const val Float[] hourTable = {0.0f, 0.125f, 0.25f, 0.375f, 0.5f, 0.625f, 0.75f, 0.875f}
    private WindlightPreset[] presets = WindlightPreset[defaultPresets.length]

    public WindlightDay() {
        for (Int i = 0; i < this.presets.length; i++) {
            this.presets[i] = WindlightPreset("windlight/" + defaultPresets[i] + ".xml")
        }
    }

    fun InterpolatePreset(WindlightPreset windlightPreset, Float f) {
        Int i2 = 0
        Int length = hourTable.length - 1
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
            Int i3 = i + 1
            if (i3 < hourTable.length) {
                i2 = i3
            }
            Float f2 = hourTable[i]
            Float f3 = hourTable[i2]
            if (f3 < f2) {
                f3 += 1.0f
            }
            windlightPreset.setByInterpolation(this.presets[i], this.presets[i2], (f - f2) / (f3 - f2))
        }
    }
}
