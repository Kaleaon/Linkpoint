// Decompiled by Jad v1.5.8e. Copyright 2001 Pavel Kouznetsov.
// Jad home page: http://www.geocities.com/kpdus/jad.html
// Decompiler options: braces fieldsfirst space lnc 

package com.lumiyaviewer.lumiya.slproto.windlight;


// Referenced classes of package com.lumiyaviewer.lumiya.slproto.windlight:
//            WindlightPreset

public class WindlightDay
{

    private static final String defaultPresets[] = {
        "A%2D12AM", "A%2D3AM", "A%2D6AM", "A%2D9AM", "A%2D12PM", "A%2D3PM", "A%2D6PM", "A%2D9PM"
    };
    private static final float hourTable[] = {
        0.0F, 0.125F, 0.25F, 0.375F, 0.5F, 0.625F, 0.75F, 0.875F
    };
    private WindlightPreset presets[];

    public WindlightDay()
    {
        presets = new WindlightPreset[defaultPresets.length];
        for (int i = 0; i < presets.length; i++)
        {
            presets[i] = new WindlightPreset((new StringBuilder()).append("windlight/").append(defaultPresets[i]).append(".xml").toString());
        }

    }

    public void InterpolatePreset(WindlightPreset windlightpreset, float f)
    {
        int i;
        int j;
        j = 0;
        i = hourTable.length - 1;
_L4:
label0:
        {
            if (i < 0)
            {
                break MISSING_BLOCK_LABEL_127;
            }
            if (f >= hourTable[i])
            {
                break label0;
            } else
            {
                i--;
                continue; /* Loop/switch isn't completed */
            }
        }
_L2:
        if (i == -1)
        {
            return;
        }
        int k = i + 1;
        float f1;
        float f2;
        float f3;
        if (k < hourTable.length)
        {
            j = k;
        }
        f3 = hourTable[i];
        f2 = hourTable[j];
        f1 = f2;
        if (f2 < f3)
        {
            f1 = f2 + 1.0F;
        }
        f = (f - f3) / (f1 - f3);
        windlightpreset.setByInterpolation(presets[i], presets[j], f);
        return;
        i = -1;
        if (true) goto _L2; else goto _L1
_L1:
        if (true) goto _L4; else goto _L3
_L3:
    }

}
