package com.lumiyaviewer.lumiya.slproto.windlight

import com.lumiyaviewer.lumiya.Debug
import com.lumiyaviewer.lumiya.LumiyaApp
import com.lumiyaviewer.lumiya.slproto.llsd.LLSDException
import com.lumiyaviewer.lumiya.slproto.llsd.LLSDNode
import java.io.IOException
import kotlin.math.pow

class WindlightPreset {

    companion object {
        private const val WINDLIGHT_GAMMA = 2.2f

        private fun lerpFloatArray(
            result: FloatArray,
            a: FloatArray,
            b: FloatArray,
            t: Float
        ) {
            val minSize = minOf(result.size, a.size, b.size)
            for (i in 0 until minSize) {
                result[i] = a[i] * (1.0f - t) + b[i] * t
            }
        }
    }

    var ambient = FloatArray(4)
    var ambientBelowWater = FloatArray(4)
    var blue_density = FloatArray(4)
    var blue_horizon = FloatArray(4)
    var cloud_color = FloatArray(4)
    var cloud_pos_density1 = FloatArray(4)
    var cloud_pos_density2 = FloatArray(4)
    var cloud_shadow = FloatArray(4)
    var haze_density = FloatArray(4)
    var haze_horizon = FloatArray(4)
    var lightnorm = FloatArray(4)
    var sunlightBelowWater = FloatArray(4)
    var sunlight_color = FloatArray(4)
    var star_brightness: Float = 0f

    constructor() {
        reset()
    }

    constructor(filename: String) {
        loadFromAssetFile(filename)
    }

    fun reset() {
        loadFromAssetFile("windlight/A%2D12PM.xml")
    }

    fun setByInterpolation(preset1: WindlightPreset, preset2: WindlightPreset, t: Float) {
        star_brightness = preset1.star_brightness * (1.0f - t) + preset2.star_brightness * t
        
        lerpFloatArray(ambient, preset1.ambient, preset2.ambient, t)
        lerpFloatArray(ambientBelowWater, preset1.ambientBelowWater, preset2.ambientBelowWater, t)
        lerpFloatArray(sunlight_color, preset1.sunlight_color, preset2.sunlight_color, t)
        lerpFloatArray(sunlightBelowWater, preset1.sunlightBelowWater, preset2.sunlightBelowWater, t)
        lerpFloatArray(lightnorm, preset1.lightnorm, preset2.lightnorm, t)
        lerpFloatArray(blue_density, preset1.blue_density, preset2.blue_density, t)
        lerpFloatArray(blue_horizon, preset1.blue_horizon, preset2.blue_horizon, t)
        lerpFloatArray(haze_density, preset1.haze_density, preset2.haze_density, t)
        lerpFloatArray(haze_horizon, preset1.haze_horizon, preset2.haze_horizon, t)
        lerpFloatArray(cloud_color, preset1.cloud_color, preset2.cloud_color, t)
        lerpFloatArray(cloud_pos_density1, preset1.cloud_pos_density1, preset2.cloud_pos_density1, t)
        lerpFloatArray(cloud_pos_density2, preset1.cloud_pos_density2, preset2.cloud_pos_density2, t)
        lerpFloatArray(cloud_shadow, preset1.cloud_shadow, preset2.cloud_shadow, t)
    }

    private fun loadFromAssetFile(filename: String) {
        Debug.Printf("Windlight preset loading from '%s'", filename)
        try {
            val inputStream = LumiyaApp.getAssetManager().open(filename)
            val llsd = LLSDNode.parseXML(inputStream, "UTF-8")
            inputStream.close()

            getFloatArray(llsd.byKey("ambient"), ambient, 3.0f)
            getFloatArray(llsd.byKey("sunlight_color"), sunlight_color, 3.0f)
            getFloatArray(llsd.byKey("lightnorm"), lightnorm, 1.0f)
            getFloatArray(llsd.byKey("blue_density"), blue_density, 2.0f)
            getFloatArray(llsd.byKey("blue_horizon"), blue_horizon, 2.0f)
            getFloatArray(llsd.byKey("haze_density"), haze_density, 5.0f)
            getFloatArray(llsd.byKey("haze_horizon"), haze_horizon, 5.0f)
            getFloatArray(llsd.byKey("cloud_color"), cloud_color, 1.0f)
            getFloatArray(llsd.byKey("cloud_pos_density1"), cloud_pos_density1, 3.0f)
            getFloatArray(llsd.byKey("cloud_pos_density2"), cloud_pos_density2, 3.0f)
            getFloatArray(llsd.byKey("cloud_shadow"), cloud_shadow, 1.0f)
            
            star_brightness = llsd.byKey("star_brightness").asDouble().toFloat()

            gammaFloatArray(ambient, WINDLIGHT_GAMMA, 1.25f)
            gammaFloatArray(sunlight_color, WINDLIGHT_GAMMA, 1.25f)
            
            darkenUnderWater(ambientBelowWater, ambient)
            darkenUnderWater(sunlightBelowWater, sunlight_color)
            
        } catch (e: IOException) {
            Debug.Warning(e)
        } catch (e: LLSDException) {
            Debug.Warning(e)
        }
    }

    private fun getFloatArray(node: LLSDNode, array: FloatArray, divisor: Float) {
        for (i in array.indices) {
            array[i] = (node.byIndex(i).asDouble() / divisor).toFloat()
        }
    }

    private fun gammaFloatArray(array: FloatArray, gamma: Float, scale: Float) {
        for (i in array.indices) {
            array[i] = array[i].toDouble().pow((1.0 / gamma).toDouble()).toFloat() * scale
        }
    }

    private fun darkenUnderWater(underwater: FloatArray, surface: FloatArray) {
        for (i in underwater.indices) {
            underwater[i] = if (i == 2 || i == 3) {
                surface[i]
            } else {
                surface[i] / 2.0f
            }
        }
    }
}
