// 
// Decompiled by Procyon v0.6.0
// 

package com.lumiyaviewer.lumiya.utils;

public enum LEDAction
{
    Always("Always", 3, "always"), 
    Fast("Fast", 2, "fast"), 
    None("None", 0, "none"), 
    Slow("Slow", 1, "slow");
    
    private String preferenceValue;
    
    private LEDAction(final String name, final int ordinal, final String preferenceValue) {
        this.preferenceValue = preferenceValue;
    }
    
    public static LEDAction getByPreferenceString(final String anObject) {
        for (int i = 0; i < values().length; ++i) {
            if (values()[i].preferenceValue.equals(anObject)) {
                return values()[i];
            }
        }
        return LEDAction.None;
    }
}
