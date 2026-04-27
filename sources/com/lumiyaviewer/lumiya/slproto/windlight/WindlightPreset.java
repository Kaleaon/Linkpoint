// Decompiled by Jad v1.5.8e. Copyright 2001 Pavel Kouznetsov.
// Jad home page: http://www.geocities.com/kpdus/jad.html
// Decompiler options: braces fieldsfirst space lnc 

package com.lumiyaviewer.lumiya.slproto.windlight;

import android.content.res.AssetManager;
import com.lumiyaviewer.lumiya.Debug;
import com.lumiyaviewer.lumiya.LumiyaApp;
import com.lumiyaviewer.lumiya.slproto.llsd.LLSDException;
import com.lumiyaviewer.lumiya.slproto.llsd.LLSDNode;
import java.io.IOException;
import java.io.InputStream;

public class WindlightPreset
{

    private static final float WINDLIGHT_GAMMA = 2.2F;
    public float ambient[];
    public float ambientBelowWater[];
    public float blue_density[];
    public float blue_horizon[];
    public float cloud_color[];
    public float cloud_pos_density1[];
    public float cloud_pos_density2[];
    public float cloud_shadow[];
    private String defaultPresets[] = {
        "A%2D12AM", "A%2D3AM", "A%2D6AM", "A%2D9AM", "A%2D12PM", "A%2D3PM", "A%2D6PM", "A%2D9PM"
    };
    public float haze_density[];
    public float haze_horizon[];
    private float hourTable[] = {
        0.0F, 0.125F, 0.25F, 0.375F, 0.5F, 0.625F, 0.75F, 0.875F
    };
    public float lightnorm[];
    public float star_brightness;
    public float sunlightBelowWater[];
    public float sunlight_color[];

    public WindlightPreset()
    {
        ambient = new float[4];
        ambientBelowWater = new float[4];
        lightnorm = new float[4];
        sunlight_color = new float[4];
        sunlightBelowWater = new float[4];
        blue_density = new float[4];
        blue_horizon = new float[4];
        haze_density = new float[4];
        haze_horizon = new float[4];
        cloud_color = new float[4];
        cloud_pos_density1 = new float[4];
        cloud_pos_density2 = new float[4];
        cloud_shadow = new float[4];
        reset();
    }

    public WindlightPreset(String s)
    {
        ambient = new float[4];
        ambientBelowWater = new float[4];
        lightnorm = new float[4];
        sunlight_color = new float[4];
        sunlightBelowWater = new float[4];
        blue_density = new float[4];
        blue_horizon = new float[4];
        haze_density = new float[4];
        haze_horizon = new float[4];
        cloud_color = new float[4];
        cloud_pos_density1 = new float[4];
        cloud_pos_density2 = new float[4];
        cloud_shadow = new float[4];
        loadFromAssetFile(s);
    }

    private void darkenUnderWater(float af[], float af1[])
    {
        int i = 0;
        while (i < af1.length) 
        {
            if (i == 2 || i == 3)
            {
                af[i] = af1[i];
            } else
            {
                af[i] = af1[i] / 2.0F;
            }
            i++;
        }
    }

    private void gammaFloatArray(float af[], float f, float f1)
    {
        for (int i = 0; i < af.length; i++)
        {
            af[i] = (float)Math.pow(af[i], 1.0F / f) * f1;
        }

    }

    private void getFloatArray(LLSDNode llsdnode, float af[], float f)
        throws LLSDException
    {
        for (int i = 0; i < af.length; i++)
        {
            af[i] = (float)llsdnode.byIndex(i).asDouble() / f;
        }

    }

    private static final void lerpFloatArray(float af[], float af1[], float af2[], float f)
    {
        for (int i = 0; i < af.length && i < af1.length && i < af2.length; i++)
        {
            af[i] = af1[i] * (1.0F - f) + af2[i] * f;
        }

    }

    private void loadFromAssetFile(String s)
    {
        Debug.Printf("Windlight preset loading from '%s'", new Object[] {
            s
        });
        try
        {
            s = LumiyaApp.getAssetManager().open(s);
            LLSDNode llsdnode = LLSDNode.parseXML(s, "UTF-8");
            s.close();
            getFloatArray(llsdnode.byKey("ambient"), ambient, 3F);
            getFloatArray(llsdnode.byKey("sunlight_color"), sunlight_color, 3F);
            getFloatArray(llsdnode.byKey("lightnorm"), lightnorm, 1.0F);
            getFloatArray(llsdnode.byKey("blue_density"), blue_density, 2.0F);
            getFloatArray(llsdnode.byKey("blue_horizon"), blue_horizon, 2.0F);
            getFloatArray(llsdnode.byKey("haze_density"), haze_density, 5F);
            getFloatArray(llsdnode.byKey("haze_horizon"), haze_horizon, 5F);
            getFloatArray(llsdnode.byKey("cloud_color"), cloud_color, 1.0F);
            getFloatArray(llsdnode.byKey("cloud_pos_density1"), cloud_pos_density1, 3F);
            getFloatArray(llsdnode.byKey("cloud_pos_density2"), cloud_pos_density2, 3F);
            getFloatArray(llsdnode.byKey("cloud_shadow"), cloud_shadow, 1.0F);
            star_brightness = (float)llsdnode.byKey("star_brightness").asDouble();
            gammaFloatArray(ambient, 2.2F, 1.25F);
            gammaFloatArray(sunlight_color, 2.2F, 1.25F);
            darkenUnderWater(ambientBelowWater, ambient);
            darkenUnderWater(sunlightBelowWater, sunlight_color);
            return;
        }
        // Misplaced declaration of an exception variable
        catch (String s)
        {
            Debug.Warning(s);
        }
        // Misplaced declaration of an exception variable
        catch (String s)
        {
            Debug.Warning(s);
            return;
        }
    }

    public void reset()
    {
        loadFromAssetFile("windlight/A%2D12PM.xml");
    }

    public void setByInterpolation(WindlightPreset windlightpreset, WindlightPreset windlightpreset1, float f)
    {
        star_brightness = windlightpreset.star_brightness * (1.0F - f) + windlightpreset1.star_brightness * f;
        lerpFloatArray(ambient, windlightpreset.ambient, windlightpreset1.ambient, f);
        lerpFloatArray(ambientBelowWater, windlightpreset.ambientBelowWater, windlightpreset1.ambientBelowWater, f);
        lerpFloatArray(sunlight_color, windlightpreset.sunlight_color, windlightpreset1.sunlight_color, f);
        lerpFloatArray(sunlightBelowWater, windlightpreset.sunlightBelowWater, windlightpreset1.sunlightBelowWater, f);
        lerpFloatArray(lightnorm, windlightpreset.lightnorm, windlightpreset1.lightnorm, f);
        lerpFloatArray(blue_density, windlightpreset.blue_density, windlightpreset1.blue_density, f);
        lerpFloatArray(blue_horizon, windlightpreset.blue_horizon, windlightpreset1.blue_horizon, f);
        lerpFloatArray(haze_density, windlightpreset.haze_density, windlightpreset1.haze_density, f);
        lerpFloatArray(haze_horizon, windlightpreset.haze_horizon, windlightpreset1.haze_horizon, f);
        lerpFloatArray(cloud_color, windlightpreset.cloud_color, windlightpreset1.cloud_color, f);
        lerpFloatArray(cloud_pos_density1, windlightpreset.cloud_pos_density1, windlightpreset1.cloud_pos_density1, f);
        lerpFloatArray(cloud_pos_density2, windlightpreset.cloud_pos_density2, windlightpreset1.cloud_pos_density2, f);
        lerpFloatArray(cloud_shadow, windlightpreset.cloud_shadow, windlightpreset1.cloud_shadow, f);
    }
}
