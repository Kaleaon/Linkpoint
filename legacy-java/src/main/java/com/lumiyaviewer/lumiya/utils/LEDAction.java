package com.lumiyaviewer.lumiya.utils;

/* loaded from: classes.dex */
public enum LEDAction {
    None("none"),
    Slow("slow"),
    Fast("fast"),
    Always("always");

    private String preferenceValue;

    LEDAction(String str) {
        this.preferenceValue = str;
    }

    public static LEDAction getByPreferenceString(String str) {
        for (int i = 0; i < valuesCustom().length; i++) {
            if (valuesCustom()[i].preferenceValue.equals(str)) {
                return valuesCustom()[i];
            }
        }
        return None;
    }

    /* renamed from: values, reason: to resolve conflict with enum method */
    public static LEDAction[] valuesCustom() {
        return values();
    }
}
