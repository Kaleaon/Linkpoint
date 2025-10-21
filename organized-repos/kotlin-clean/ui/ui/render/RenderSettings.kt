package com.linkpoint.ui.render

import android.content.SharedPreferences

class RenderSettings {
    val Int avatarCountLimit
    val Int drawDistance

    public RenderSettings(SharedPreferences sharedPreferences) {
        Int i = 20
        try {
            i = Integer.parseInt(sharedPreferences.getString("drawDistance", "20"))
        } catch (Exception e) {
        }
        Int i2 = 5
        try {
            i2 = Integer.parseInt(sharedPreferences.getString("avatarCountLimit", "5"))
        } catch (Exception e2) {
        }
        this.drawDistance = i
        this.avatarCountLimit = i2
    }
}
