package com.lumiyaviewer.lumiya.slproto.windlight

import com.lumiyaviewer.lumiya.Debug
import com.lumiyaviewer.lumiya.LumiyaApp
import com.lumiyaviewer.lumiya.slproto.llsd.LLSDException
import com.lumiyaviewer.lumiya.slproto.llsd.LLSDNode
import java.io.IOException
import kotlin.math.pow

class WindlightPreset {
    val ambient = FloatArray(4)
    val ambientBelowWater = FloatArray(4)
    val blue_density = FloatArray(4)
    val blue_horizon = FloatArray(4)
    val cloud_color = FloatArray(4)
    val cloud_pos_density1 = FloatArray(4)
    val cloud_pos_density2 = FloatArray(4)
    val cloud_shadow = FloatArray(4)
    val haze_density = FloatArray(4)
    val haze_horizon = FloatArray(4)
    val lightnorm = FloatArray(4)
    var star_brightness: Float = 0f
    val sunlightBelowWater = FloatArray(4)
    val sunlight_color = FloatArray(4)

    private val WINDLIGHT_GAMMA = 2.2f
    private val defaultPresets = arrayOf("A%2D12AM", "A%2D3AM", "A%2D6AM", "A%2D9AM", "A%2D12PM", "A%2D3PM", "A%2D6PM", "A%2D9PM")
    private val hourTable = floatArrayOf(0.0f, 0.125f, 0.25f, 0.375f, 0.5f, 0.625f, 0.75f, 0.875f)

    constructor() {
        reset()
    }

    constructor(str: String) {
        loadFromAssetFile(str)
    }

    private fun darkenUnderWater(fArr: FloatArray, fArr2: FloatArray) {
        for (i in fArr2.indices) {
            if (i == 2 || i == 3) {
                fArr[i] = fArr2[i]
            } else {
                fArr[i] = fArr2[i] / 2.0f
            }
        }
    }

    private fun gammaFloatArray(fArr: FloatArray, f: Float, f2: Float) {
        for (i in fArr.indices) {
            fArr[i] = fArr[i].toDouble().pow((1.0f / f).toDouble()).toFloat() * f2
        }
    }

    @Throws(LLSDException::class)
    private fun getFloatArray(lLSDNode: LLSDNode, fArr: FloatArray, f: Float) {
        for (i in fArr.indices) {
            fArr[i] = lLSDNode.byIndex(i).asDouble().toFloat() / f
        }
    }

    private fun lerpFloatArray(fArr: FloatArray, fArr2: FloatArray, fArr3: FloatArray, f: Float) {
        var i = 0
        while (i < fArr.size && i < fArr2.size && i < fArr3.size) {
            fArr[i] = (fArr2[i] * (1.0f - f)) + (fArr3[i] * f)
            i++
        }
    }

    private fun loadFromAssetFile(str: String) {
        Debug.Log("Windlight preset loading from '$str'")
        try {
            LumiyaApp.getAssetManager()?.open(str)?.use { open ->
                val parseXML = LLSDNode.parseXML(open, "UTF-8")
                getFloatArray(parseXML.byKey("ambient"), this.ambient, 3.0f)
                getFloatArray(parseXML.byKey("sunlight_color"), this.sunlight_color, 3.0f)
                getFloatArray(parseXML.byKey("lightnorm"), this.lightnorm, 1.0f)
                getFloatArray(parseXML.byKey("blue_density"), this.blue_density, 2.0f)
                getFloatArray(parseXML.byKey("blue_horizon"), this.blue_horizon, 2.0f)
                getFloatArray(parseXML.byKey("haze_density"), this.haze_density, 5.0f)
                getFloatArray(parseXML.byKey("haze_horizon"), this.haze_horizon, 5.0f)
                getFloatArray(parseXML.byKey("cloud_color"), this.cloud_color, 1.0f)
                getFloatArray(parseXML.byKey("cloud_pos_density1"), this.cloud_pos_density1, 3.0f)
                getFloatArray(parseXML.byKey("cloud_pos_density2"), this.cloud_pos_density2, 3.0f)
                getFloatArray(parseXML.byKey("cloud_shadow"), this.cloud_shadow, 1.0f)
                this.star_brightness = parseXML.byKey("star_brightness").asDouble().toFloat()
                gammaFloatArray(this.ambient, WINDLIGHT_GAMMA, 1.25f)
                gammaFloatArray(this.sunlight_color, WINDLIGHT_GAMMA, 1.25f)
                darkenUnderWater(this.ambientBelowWater, this.ambient)
                darkenUnderWater(this.sunlightBelowWater, this.sunlight_color)
            }
        } catch (e: IOException) {
            Debug.Warning(e)
        } catch (e2: LLSDException) {
            Debug.Warning(e2)
        }
    }

    fun reset() {
        loadFromAssetFile("windlight/A%2D12PM.xml")
    }

    fun setByInterpolation(windlightPreset: WindlightPreset, windlightPreset2: WindlightPreset, f: Float) {
        this.star_brightness = (windlightPreset.star_brightness * (1.0f - f)) + (windlightPreset2.star_brightness * f)
        lerpFloatArray(this.ambient, windlightPreset.ambient, windlightPreset2.ambient, f)
        lerpFloatArray(this.ambientBelowWater, windlightPreset.ambientBelowWater, windlightPreset2.ambientBelowWater, f)
        lerpFloatArray(this.sunlight_color, windlightPreset.sunlight_color, windlightPreset2.sunlight_color, f)
        lerpFloatArray(this.sunlightBelowWater, windlightPreset.sunlightBelowWater, windlightPreset2.sunlightBelowWater, f)
        lerpFloatArray(this.lightnorm, windlightPreset.lightnorm, windlightPreset2.lightnorm, f)
        lerpFloatArray(this.blue_density, windlightPreset.blue_density, windlightPreset2.blue_density, f)
        lerpFloatArray(this.blue_horizon, windlightPreset.blue_horizon, windlightPreset2.blue_horizon, f)
        lerpFloatArray(this.haze_density, windlightPreset.haze_density, windlightPreset2.haze_density, f)
        lerpFloatArray(this.haze_horizon, windlightPreset.haze_horizon, windlightPreset2.haze_horizon, f)
        lerpFloatArray(this.cloud_color, windlightPreset.cloud_color, windlightPreset2.cloud_color, f)
        lerpFloatArray(this.cloud_pos_density1, windlightPreset.cloud_pos_density1, windlightPreset2.cloud_pos_density1, f)
        lerpFloatArray(this.cloud_pos_density2, windlightPreset.cloud_pos_density2, windlightPreset2.cloud_pos_density2, f)
        lerpFloatArray(this.cloud_shadow, windlightPreset.cloud_shadow, windlightPreset2.cloud_shadow, f)
    }
}
