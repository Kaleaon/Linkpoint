package com.linkpoint.linden.llinventory

import com.linkpoint.linden.llcommon.LLSD
import com.linkpoint.linden.llcommon.LLUUID
import com.linkpoint.linden.llmath.Vector3

class LLLandmark(
    var regionHandle: ULong = 0uL,
    var localPosition: Vector3 = Vector3.ZERO
) {
    fun getRegionX(): Int = ((regionHandle shr 32) * 256uL).toInt()
    fun getRegionY(): Int = ((regionHandle and 0xFFFFFFFFuL) * 256uL).toInt()

    fun toLLSD(): LLSD = LLSD.ofMap(mapOf(
        "region_handle" to LLSD.of(regionHandle.toString()),
        "local_x"       to LLSD.of(localPosition.x.toDouble()),
        "local_y"       to LLSD.of(localPosition.y.toDouble()),
        "local_z"       to LLSD.of(localPosition.z.toDouble())
    ))

    companion object {
        fun fromLLSD(sd: LLSD): LLLandmark = LLLandmark(
            regionHandle  = sd["region_handle"].asString().toULongOrNull() ?: 0uL,
            localPosition = Vector3(
                sd["local_x"].asReal().toFloat(),
                sd["local_y"].asReal().toFloat(),
                sd["local_z"].asReal().toFloat()
            )
        )

        fun fromString(data: String): LLLandmark? {
            val lines = data.lines().filter { it.isNotBlank() }
            if (lines.isEmpty()) return null
            var regionHandle = 0uL
            var x = 128f; var y = 128f; var z = 0f
            for (line in lines) {
                when {
                    line.startsWith("region_handle") -> regionHandle = line.substringAfterLast(' ').trim().toULongOrNull() ?: 0uL
                    line.startsWith("local_x")       -> x = line.substringAfterLast(' ').trim().toFloatOrNull() ?: x
                    line.startsWith("local_y")       -> y = line.substringAfterLast(' ').trim().toFloatOrNull() ?: y
                    line.startsWith("local_z")       -> z = line.substringAfterLast(' ').trim().toFloatOrNull() ?: z
                }
            }
            return LLLandmark(regionHandle, Vector3(x, y, z))
        }
    }
}
