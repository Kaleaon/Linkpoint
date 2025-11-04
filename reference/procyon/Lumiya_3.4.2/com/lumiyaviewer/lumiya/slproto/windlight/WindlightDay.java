// 
// Decompiled by Procyon v0.6.0
// 

package com.lumiyaviewer.lumiya.slproto.windlight;

public class WindlightDay
{
    private static final String[] defaultPresets;
    private static final float[] hourTable;
    private WindlightPreset[] presets;
    
    static {
        hourTable = new float[] { 0.0f, 0.125f, 0.25f, 0.375f, 0.5f, 0.625f, 0.75f, 0.875f };
        defaultPresets = new String[] { "A%2D12AM", "A%2D3AM", "A%2D6AM", "A%2D9AM", "A%2D12PM", "A%2D3PM", "A%2D6PM", "A%2D9PM" };
    }
    
    public WindlightDay() {
        this.presets = new WindlightPreset[WindlightDay.defaultPresets.length];
        for (int i = 0; i < this.presets.length; ++i) {
            this.presets[i] = new WindlightPreset("windlight/" + WindlightDay.defaultPresets[i] + ".xml");
        }
    }
    
    public void InterpolatePreset(final WindlightPreset windlightPreset, float n) {
        int n2 = 0;
        int i = WindlightDay.hourTable.length - 1;
        while (true) {
            while (i >= 0) {
                if (n >= WindlightDay.hourTable[i]) {
                    if (i == -1) {
                        return;
                    }
                    final int n3 = i + 1;
                    if (n3 < WindlightDay.hourTable.length) {
                        n2 = n3;
                    }
                    final float n4 = WindlightDay.hourTable[i];
                    float n6;
                    final float n5 = n6 = WindlightDay.hourTable[n2];
                    if (n5 < n4) {
                        n6 = n5 + 1.0f;
                    }
                    n = (n - n4) / (n6 - n4);
                    windlightPreset.setByInterpolation(this.presets[i], this.presets[n2], n);
                    return;
                }
                else {
                    --i;
                }
            }
            i = -1;
            continue;
        }
    }
}
