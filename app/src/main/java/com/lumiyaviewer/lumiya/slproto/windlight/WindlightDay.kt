package com.lumiyaviewer.lumiya.slproto.windlight

class WindlightDay {

    companion object {
        private val defaultPresets = arrayOf(
            "A%2D12AM", "A%2D3AM", "A%2D6AM", "A%2D9AM",
            "A%2D12PM", "A%2D3PM", "A%2D6PM", "A%2D9PM"
        )
        
        private val hourTable = floatArrayOf(
            0.0f, 0.125f, 0.25f, 0.375f,
            0.5f, 0.625f, 0.75f, 0.875f
        )
    }

    private val presets = Array(defaultPresets.size) { i ->
        WindlightPreset("windlight/${defaultPresets[i]}.xml")
    }

    fun InterpolatePreset(result: WindlightPreset, dayFraction: Float) {
        // Find the hour index for interpolation
        var lowerIndex = -1
        for (i in hourTable.indices.reversed()) {
            if (dayFraction >= hourTable[i]) {
                lowerIndex = i
                break
            }
        }

        if (lowerIndex != -1) {
            val upperIndex = if (lowerIndex + 1 < hourTable.size) {
                lowerIndex + 1
            } else {
                0 // Wrap around to midnight
            }

            var lowerHour = hourTable[lowerIndex]
            var upperHour = hourTable[upperIndex]
            
            // Handle wrap-around at midnight
            if (upperHour < lowerHour) {
                upperHour += 1.0f
            }

            val t = (dayFraction - lowerHour) / (upperHour - lowerHour)
            result.setByInterpolation(presets[lowerIndex], presets[upperIndex], t)
        }
    }
}
