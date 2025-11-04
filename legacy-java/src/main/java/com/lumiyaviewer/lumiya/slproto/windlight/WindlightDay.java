package com.lumiyaviewer.lumiya.slproto.windlight;

/* loaded from: classes.dex */
public class WindlightDay {
    private WindlightPreset[] presets = new WindlightPreset[defaultPresets.length];
    private static final float[] hourTable = {0.0f, 0.125f, 0.25f, 0.375f, 0.5f, 0.625f, 0.75f, 0.875f};
    private static final String[] defaultPresets = {"A%2D12AM", "A%2D3AM", "A%2D6AM", "A%2D9AM", "A%2D12PM", "A%2D3PM", "A%2D6PM", "A%2D9PM"};

    public WindlightDay() {
        for (int i = 0; i < this.presets.length; i++) {
            this.presets[i] = new WindlightPreset("windlight/" + defaultPresets[i] + ".xml");
        }
    }

    public void InterpolatePreset(WindlightPreset windlightPreset, float f) {
        int i;
        int length = hourTable.length - 1;
        while (true) {
            if (length < 0) {
                i = -1;
                break;
            } else {
                if (f >= hourTable[length]) {
                    i = length;
                    break;
                }
                length--;
            }
        }
        if (i == -1) {
            return;
        }
        int i2 = i + 1;
        int i3 = i2 < hourTable.length ? i2 : 0;
        float f2 = hourTable[i];
        float f3 = hourTable[i3];
        if (f3 < f2) {
            f3 += 1.0f;
        }
        windlightPreset.setByInterpolation(this.presets[i], this.presets[i3], (f - f2) / (f3 - f2));
    }
}
