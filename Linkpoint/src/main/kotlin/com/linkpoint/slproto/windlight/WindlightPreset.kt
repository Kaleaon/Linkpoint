package com.linkpoint.slproto.windlight

import com.linkpoint.Debug
import com.linkpoint.LinkpointApp
import com.linkpoint.slproto.llsd.LLSDException
import com.linkpoint.slproto.llsd.LLSDNode
import java.io.IOException
import java.io.InputStream

class WindlightPreset {
    private const val WINDLIGHT_GAMMA: Float = 2.2f
    public FloatArray ambient = Float[4]
    public FloatArray ambientBelowWater = Float[4]
    public FloatArray blue_density = Float[4]
    public FloatArray blue_horizon = Float[4]
    public FloatArray cloud_color = Float[4]
    public FloatArray cloud_pos_density1 = Float[4]
    public FloatArray cloud_pos_density2 = Float[4]
    public FloatArray cloud_shadow = Float[4]
    private Array<String> defaultPresets = {"A%2D12AM", "A%2D3AM", "A%2D6AM", "A%2D9AM", "A%2D12PM", "A%2D3PM", "A%2D6PM", "A%2D9PM"}
    public FloatArray haze_density = Float[4]
    public FloatArray haze_horizon = Float[4]
    private FloatArray hourTable = {0.0f, 0.125f, 0.25f, 0.375f, 0.5f, 0.625f, 0.75f, 0.875f}
    public FloatArray lightnorm = Float[4]
    public Float star_brightness
    public FloatArray sunlightBelowWater = Float[4]
    public FloatArray sunlight_color = Float[4]

    public WindlightPreset() {
        reset()
    }

    public WindlightPreset(String str) {
        loadFromAssetFile(str)
    }

     private fun darkenUnderWater(fArr: FloatArray, fArr2: FloatArray) {
        for (Int i = 0; i < fArr2.length; i++) {
            if (i == 2 || i == 3) {
                fArr[i] = fArr2[i]
            } else {
                fArr[i] = fArr2[i] / 2.0f
            }
        }
    }

     private fun gammaFloatArray(fArr: FloatArray, f: Float, f2: Float) {
        for (Int i = 0; i < fArr.length; i++) {
            fArr[i] = ((Float) Math.pow((Double) fArr[i], (Double) (1.0f / f))) * f2
        }
    }

     private fun getFloatArray(lLSDNode: LLSDNode, fArr: FloatArray, f: Float) throws LLSDException {
        for (Int i = 0; i < fArr.length; i++) {
            fArr[i] = ((Float) lLSDNode.byIndex(i).asDouble()) / f
        }
    }

    private const val Unit lerpFloatArray(FloatArray fArr, FloatArray fArr2, FloatArray fArr3, Float f) {
        val i: Int = 0
        while (i < fArr.length && i < fArr2.length && i < fArr3.length) {
            fArr[i] = (fArr2[i] * (1.0f - f)) + (fArr3[i] * f)
            i++
        }
    }

     private fun loadFromAssetFile(str: String) {
        Debug.Printf("Windlight preset loading from '%s'", str)
        try {
            val open: InputStream = LinkpointApp.getAssetManager().open(str)
            val parseXML: LLSDNode = LLSDNode.parseXML(open, "UTF-8")
            open.close()
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
            this.star_brightness = (Float) parseXML.byKey("star_brightness").asDouble()
            gammaFloatArray(this.ambient, WINDLIGHT_GAMMA, 1.25f)
            gammaFloatArray(this.sunlight_color, WINDLIGHT_GAMMA, 1.25f)
            darkenUnderWater(this.ambientBelowWater, this.ambient)
            darkenUnderWater(this.sunlightBelowWater, this.sunlight_color)
        } catch (IOException e) {
            Debug.Warning(e)
        } catch (LLSDException e2) {
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
