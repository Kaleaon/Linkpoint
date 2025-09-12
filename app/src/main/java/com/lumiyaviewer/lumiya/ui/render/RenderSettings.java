package com.lumiyaviewer.lumiya.ui.render;

import android.content.SharedPreferences;

public class RenderSettings {
    public final int avatarCountLimit;
    public final int drawDistance;

    public RenderSettings(SharedPreferences sharedPreferences) {
        int i = 20;
        try {
            i = Integer.parseInt(sharedPreferences.getString("drawDistance", "20"));
        } catch (Exception e) {
        }
        int i2 = 5;
        try {
            i2 = Integer.parseInt(sharedPreferences.getString("avatarCountLimit", "5"));
        } catch (Exception e2) {
        }
        this.drawDistance = i;
        this.avatarCountLimit = i2;
    }
}
