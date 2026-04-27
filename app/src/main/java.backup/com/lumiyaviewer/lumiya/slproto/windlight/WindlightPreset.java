package com.lumiyaviewer.lumiya.slproto.windlight;

import com.lumiyaviewer.lumiya.Debug;
import com.lumiyaviewer.lumiya.LumiyaApp;
import com.lumiyaviewer.lumiya.slproto.llsd.LLSDException;
import com.lumiyaviewer.lumiya.slproto.llsd.LLSDNode;
import java.io.IOException;
import java.io.InputStream;

public class WindlightPreset {
    private static final float WINDLIGHT_GAMMA = 2.2f;
    public float[] ambient = new float[4];
    public float[] ambientBelowWater = new float[4];
    public float[] blue_density = new float[4];
    public float[] blue_horizon = new float[4];
    public float[] cloud_color = new float[4];
    public float[] cloud_pos_density1 = new float[4];
    public float[] cloud_pos_density2 = new float[4];
    public float[] cloud_shadow = new float[4];
    private String[] defaultPresets = {"A%2D12AM", "A%2D3AM", "A%2D6AM", "A%2D9AM", "A%2D12PM", "A%2D3PM", "A%2D6PM", "A%2D9PM"};
    public float[] haze_density = new float[4];
    public float[] haze_horizon = new float[4];
    private float[] hourTable = {0.0f, 0.125f, 0.25f, 0.375f, 0.5f, 0.625f, 0.75f, 0.875f};
    public float[] lightnorm = new float[4];
    public float star_brightness;
    public float[] sunlightBelowWater = new float[4];
    public float[] sunlight_color = new float[4];

    public WindlightPreset() {
        reset();
    }

    public WindlightPreset(String str) {
        loadFromAssetFile(str);
    }

    private void darkenUnderWater(float[] fArr, float[] fArr2) {
        for (int i = 0; i < fArr2.length; i++) {
            if (i == 2 || i == 3) {
                fArr[i] = fArr2[i];
            } else {
                fArr[i] = fArr2[i] / 2.0f;
            }
        }
    }

    private void gammaFloatArray(float[] fArr, float f, float f2) {
        for (int i = 0; i < fArr.length; i++) {
            fArr[i] = ((float) Math.pow((double) fArr[i], (double) (1.0f / f))) * f2;
        }
    }

    private void getFloatArray(LLSDNode lLSDNode, float[] fArr, float f) throws LLSDException {
        for (int i = 0; i < fArr.length; i++) {
            fArr[i] = ((float) lLSDNode.byIndex(i).asDouble()) / f;
        }
    }

    private static final void lerpFloatArray(float[] fArr, float[] fArr2, float[] fArr3, float f) {
        int i = 0;
        while (i < fArr.length && i < fArr2.length && i < fArr3.length) {
            fArr[i] = (fArr2[i] * (1.0f - f)) + (fArr3[i] * f);
            i++;
        }
    }

    private void loadFromAssetFile(String str) {
        Debug.Printf("Windlight preset loading from '%s'", str);
        try {
            InputStream open = LumiyaApp.getAssetManager().open(str);
            LLSDNode parseXML = LLSDNode.parseXML(open, "UTF-8");
            open.close();
            getFloatArray(parseXML.byKey("ambient"), this.ambient, 3.0f);
            getFloatArray(parseXML.byKey("sunlight_color"), this.sunlight_color, 3.0f);
            getFloatArray(parseXML.byKey("lightnorm"), this.lightnorm, 1.0f);
            getFloatArray(parseXML.byKey("blue_density"), this.blue_density, 2.0f);
            getFloatArray(parseXML.byKey("blue_horizon"), this.blue_horizon, 2.0f);
            getFloatArray(parseXML.byKey("haze_density"), this.haze_density, 5.0f);
            getFloatArray(parseXML.byKey("haze_horizon"), this.haze_horizon, 5.0f);
            getFloatArray(parseXML.byKey("cloud_color"), this.cloud_color, 1.0f);
            getFloatArray(parseXML.byKey("cloud_pos_density1"), this.cloud_pos_density1, 3.0f);
            getFloatArray(parseXML.byKey("cloud_pos_density2"), this.cloud_pos_density2, 3.0f);
            getFloatArray(parseXML.byKey("cloud_shadow"), this.cloud_shadow, 1.0f);
            this.star_brightness = (float) parseXML.byKey("star_brightness").asDouble();
            gammaFloatArray(this.ambient, WINDLIGHT_GAMMA, 1.25f);
            gammaFloatArray(this.sunlight_color, WINDLIGHT_GAMMA, 1.25f);
            darkenUnderWater(this.ambientBelowWater, this.ambient);
            darkenUnderWater(this.sunlightBelowWater, this.sunlight_color);
        } catch (IOException e) {
            Debug.Warning(e);
        } catch (LLSDException e2) {
            Debug.Warning(e2);
        }
    }

    public void reset() {
        loadFromAssetFile("windlight/A%2D12PM.xml");
    }

    public void setByInterpolation(WindlightPreset windlightPreset, WindlightPreset windlightPreset2, float f) {
        this.star_brightness = (windlightPreset.star_brightness * (1.0f - f)) + (windlightPreset2.star_brightness * f);
        lerpFloatArray(this.ambient, windlightPreset.ambient, windlightPreset2.ambient, f);
        lerpFloatArray(this.ambientBelowWater, windlightPreset.ambientBelowWater, windlightPreset2.ambientBelowWater, f);
        lerpFloatArray(this.sunlight_color, windlightPreset.sunlight_color, windlightPreset2.sunlight_color, f);
        lerpFloatArray(this.sunlightBelowWater, windlightPreset.sunlightBelowWater, windlightPreset2.sunlightBelowWater, f);
        lerpFloatArray(this.lightnorm, windlightPreset.lightnorm, windlightPreset2.lightnorm, f);
        lerpFloatArray(this.blue_density, windlightPreset.blue_density, windlightPreset2.blue_density, f);
        lerpFloatArray(this.blue_horizon, windlightPreset.blue_horizon, windlightPreset2.blue_horizon, f);
        lerpFloatArray(this.haze_density, windlightPreset.haze_density, windlightPreset2.haze_density, f);
        lerpFloatArray(this.haze_horizon, windlightPreset.haze_horizon, windlightPreset2.haze_horizon, f);
        lerpFloatArray(this.cloud_color, windlightPreset.cloud_color, windlightPreset2.cloud_color, f);
        lerpFloatArray(this.cloud_pos_density1, windlightPreset.cloud_pos_density1, windlightPreset2.cloud_pos_density1, f);
        lerpFloatArray(this.cloud_pos_density2, windlightPreset.cloud_pos_density2, windlightPreset2.cloud_pos_density2, f);
        lerpFloatArray(this.cloud_shadow, windlightPreset.cloud_shadow, windlightPreset2.cloud_shadow, f);
    }
}
