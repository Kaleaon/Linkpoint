/*
 * Quality Settings - Overall quality control for rendering system
 */

package lindenlab.llsd.viewer.secondlife.rendering

import kotlin.math.max
import kotlin.math.min

class QualitySettings {
    var overallQuality: Float = 0.6f // 0.0 to 1.0
        set(value) {
            field = max(0.0f, min(1.0f, value))
        }
    
    var autoAdjustQuality: Boolean = true
    var renderScale: Float = 1.0f
    var maxDrawDistance: Int = 256
    
    fun applySetting(setting: String, value: Any) {
        when (setting.lowercase()) {
            "overallquality" -> {
                if (value is Number) {
                    overallQuality = value.toFloat()
                }
            }
            "autoadjust" -> {
                if (value is Boolean) {
                    autoAdjustQuality = value
                }
            }
            "renderscale" -> {
                if (value is Number) {
                    renderScale = value.toFloat()
                }
            }
            "maxdrawdistance" -> {
                if (value is Number) {
                    maxDrawDistance = value.toInt()
                }
            }
        }
    }
    
    fun toMap(): Map<String, Any> {
        return mapOf(
            "overallQuality" to overallQuality,
            "autoAdjustQuality" to autoAdjustQuality,
            "renderScale" to renderScale,
            "maxDrawDistance" to maxDrawDistance
        )
    }
    
    fun fromMap(map: Map<String, Any>) {
        map["overallQuality"]?.let { overallQuality = (it as Number).toFloat() }
        map["autoAdjustQuality"]?.let { autoAdjustQuality = it as Boolean }
        map["renderScale"]?.let { renderScale = (it as Number).toFloat() }
        map["maxDrawDistance"]?.let { maxDrawDistance = (it as Number).toInt() }
    }
}
