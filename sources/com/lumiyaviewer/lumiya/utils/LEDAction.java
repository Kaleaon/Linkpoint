package com.lumiyaviewer.lumiya.utils;

public enum LEDAction {
    None("none"),
    Slow("slow"),
    Fast("fast"),
    Always("always");
    
    private String preferenceValue;

    private LEDAction(String str) {
        this.preferenceValue = str;
    }

    public static LEDAction getByPreferenceString(String str) {
        for (int i = 0; i < values().length; i++) {
            if (values()[i].preferenceValue.equals(str)) {
                return values()[i];
            }
        }
        return None;
    }
}
