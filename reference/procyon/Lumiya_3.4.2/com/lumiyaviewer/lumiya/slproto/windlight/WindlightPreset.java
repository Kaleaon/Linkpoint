// 
// Decompiled by Procyon v0.6.0
// 

package com.lumiyaviewer.lumiya.slproto.windlight;

import java.io.InputStream;
import java.io.IOException;
import com.lumiyaviewer.lumiya.LumiyaApp;
import com.lumiyaviewer.lumiya.Debug;
import com.lumiyaviewer.lumiya.slproto.llsd.LLSDException;
import com.lumiyaviewer.lumiya.slproto.llsd.LLSDNode;

public class WindlightPreset
{
    private static final float WINDLIGHT_GAMMA = 2.2f;
    public float[] ambient;
    public float[] ambientBelowWater;
    public float[] blue_density;
    public float[] blue_horizon;
    public float[] cloud_color;
    public float[] cloud_pos_density1;
    public float[] cloud_pos_density2;
    public float[] cloud_shadow;
    private String[] defaultPresets;
    public float[] haze_density;
    public float[] haze_horizon;
    private float[] hourTable;
    public float[] lightnorm;
    public float star_brightness;
    public float[] sunlightBelowWater;
    public float[] sunlight_color;
    
    public WindlightPreset() {
        this.hourTable = new float[] { 0.0f, 0.125f, 0.25f, 0.375f, 0.5f, 0.625f, 0.75f, 0.875f };
        this.defaultPresets = new String[] { "A%2D12AM", "A%2D3AM", "A%2D6AM", "A%2D9AM", "A%2D12PM", "A%2D3PM", "A%2D6PM", "A%2D9PM" };
        this.ambient = new float[4];
        this.ambientBelowWater = new float[4];
        this.lightnorm = new float[4];
        this.sunlight_color = new float[4];
        this.sunlightBelowWater = new float[4];
        this.blue_density = new float[4];
        this.blue_horizon = new float[4];
        this.haze_density = new float[4];
        this.haze_horizon = new float[4];
        this.cloud_color = new float[4];
        this.cloud_pos_density1 = new float[4];
        this.cloud_pos_density2 = new float[4];
        this.cloud_shadow = new float[4];
        this.reset();
    }
    
    public WindlightPreset(final String s) {
        this.hourTable = new float[] { 0.0f, 0.125f, 0.25f, 0.375f, 0.5f, 0.625f, 0.75f, 0.875f };
        this.defaultPresets = new String[] { "A%2D12AM", "A%2D3AM", "A%2D6AM", "A%2D9AM", "A%2D12PM", "A%2D3PM", "A%2D6PM", "A%2D9PM" };
        this.ambient = new float[4];
        this.ambientBelowWater = new float[4];
        this.lightnorm = new float[4];
        this.sunlight_color = new float[4];
        this.sunlightBelowWater = new float[4];
        this.blue_density = new float[4];
        this.blue_horizon = new float[4];
        this.haze_density = new float[4];
        this.haze_horizon = new float[4];
        this.cloud_color = new float[4];
        this.cloud_pos_density1 = new float[4];
        this.cloud_pos_density2 = new float[4];
        this.cloud_shadow = new float[4];
        this.loadFromAssetFile(s);
    }
    
    private void darkenUnderWater(final float[] array, final float[] array2) {
        for (int i = 0; i < array2.length; ++i) {
            if (i == 2 || i == 3) {
                array[i] = array2[i];
            }
            else {
                array[i] = array2[i] / 2.0f;
            }
        }
    }
    
    private void gammaFloatArray(final float[] array, final float n, final float n2) {
        for (int i = 0; i < array.length; ++i) {
            array[i] = (float)Math.pow(array[i], 1.0f / n) * n2;
        }
    }
    
    private void getFloatArray(final LLSDNode llsdNode, final float[] array, final float n) throws LLSDException {
        for (int i = 0; i < array.length; ++i) {
            array[i] = (float)llsdNode.byIndex(i).asDouble() / n;
        }
    }
    
    private static final void lerpFloatArray(final float[] array, final float[] array2, final float[] array3, final float n) {
        for (int n2 = 0; n2 < array.length && n2 < array2.length && n2 < array3.length; ++n2) {
            array[n2] = array2[n2] * (1.0f - n) + array3[n2] * n;
        }
    }
    
    private void loadFromAssetFile(final String s) {
        Debug.Printf("Windlight preset loading from '%s'", s);
        try {
            final InputStream open = LumiyaApp.getAssetManager().open(s);
            final LLSDNode xml = LLSDNode.parseXML(open, "UTF-8");
            open.close();
            this.getFloatArray(xml.byKey("ambient"), this.ambient, 3.0f);
            this.getFloatArray(xml.byKey("sunlight_color"), this.sunlight_color, 3.0f);
            this.getFloatArray(xml.byKey("lightnorm"), this.lightnorm, 1.0f);
            this.getFloatArray(xml.byKey("blue_density"), this.blue_density, 2.0f);
            this.getFloatArray(xml.byKey("blue_horizon"), this.blue_horizon, 2.0f);
            this.getFloatArray(xml.byKey("haze_density"), this.haze_density, 5.0f);
            this.getFloatArray(xml.byKey("haze_horizon"), this.haze_horizon, 5.0f);
            this.getFloatArray(xml.byKey("cloud_color"), this.cloud_color, 1.0f);
            this.getFloatArray(xml.byKey("cloud_pos_density1"), this.cloud_pos_density1, 3.0f);
            this.getFloatArray(xml.byKey("cloud_pos_density2"), this.cloud_pos_density2, 3.0f);
            this.getFloatArray(xml.byKey("cloud_shadow"), this.cloud_shadow, 1.0f);
            this.star_brightness = (float)xml.byKey("star_brightness").asDouble();
            this.gammaFloatArray(this.ambient, 2.2f, 1.25f);
            this.gammaFloatArray(this.sunlight_color, 2.2f, 1.25f);
            this.darkenUnderWater(this.ambientBelowWater, this.ambient);
            this.darkenUnderWater(this.sunlightBelowWater, this.sunlight_color);
        }
        catch (final LLSDException ex) {
            Debug.Warning(ex);
        }
        catch (final IOException ex2) {
            Debug.Warning(ex2);
        }
    }
    
    public void reset() {
        this.loadFromAssetFile("windlight/A%2D12PM.xml");
    }
    
    public void setByInterpolation(final WindlightPreset windlightPreset, final WindlightPreset windlightPreset2, final float n) {
        this.star_brightness = windlightPreset.star_brightness * (1.0f - n) + windlightPreset2.star_brightness * n;
        lerpFloatArray(this.ambient, windlightPreset.ambient, windlightPreset2.ambient, n);
        lerpFloatArray(this.ambientBelowWater, windlightPreset.ambientBelowWater, windlightPreset2.ambientBelowWater, n);
        lerpFloatArray(this.sunlight_color, windlightPreset.sunlight_color, windlightPreset2.sunlight_color, n);
        lerpFloatArray(this.sunlightBelowWater, windlightPreset.sunlightBelowWater, windlightPreset2.sunlightBelowWater, n);
        lerpFloatArray(this.lightnorm, windlightPreset.lightnorm, windlightPreset2.lightnorm, n);
        lerpFloatArray(this.blue_density, windlightPreset.blue_density, windlightPreset2.blue_density, n);
        lerpFloatArray(this.blue_horizon, windlightPreset.blue_horizon, windlightPreset2.blue_horizon, n);
        lerpFloatArray(this.haze_density, windlightPreset.haze_density, windlightPreset2.haze_density, n);
        lerpFloatArray(this.haze_horizon, windlightPreset.haze_horizon, windlightPreset2.haze_horizon, n);
        lerpFloatArray(this.cloud_color, windlightPreset.cloud_color, windlightPreset2.cloud_color, n);
        lerpFloatArray(this.cloud_pos_density1, windlightPreset.cloud_pos_density1, windlightPreset2.cloud_pos_density1, n);
        lerpFloatArray(this.cloud_pos_density2, windlightPreset.cloud_pos_density2, windlightPreset2.cloud_pos_density2, n);
        lerpFloatArray(this.cloud_shadow, windlightPreset.cloud_shadow, windlightPreset2.cloud_shadow, n);
    }
}
