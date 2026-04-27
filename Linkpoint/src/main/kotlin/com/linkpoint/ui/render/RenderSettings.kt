package com.linkpoint.ui.render

import android.content.SharedPreferences

class RenderSettings {
    val Int avatarCountLimit
    val Int drawDistance

    public RenderSettings(SharedPreferences sharedPreferences) {
        val i: Int = 20
        try {
            i = Integer.parseInt(sharedPreferences.getString("drawDistance", "20"))
        } catch (Exception e) {
        }
        val i2: Int = 5
        try {
            i2 = Integer.parseInt(sharedPreferences.getString("avatarCountLimit", "5"))
        } catch (Exception e2) {
        }
        this.drawDistance = i
        this.avatarCountLimit = i2
    }
}
