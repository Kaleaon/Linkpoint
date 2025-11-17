package com.linkpoint.ui.render

import android.content.SharedPreferences

class RenderSettings(sharedPreferences: SharedPreferences) {
    val drawDistance: Int
    val avatarCountLimit: Int

    init {
        var i = 20
        try {
            i = sharedPreferences.getString("drawDistance", "20")?.toInt() ?: 20
        } catch (e: Exception) {
            // Use default value
        }

        var i2 = 5
        try {
            i2 = sharedPreferences.getString("avatarCountLimit", "5")?.toInt() ?: 5
        } catch (e: Exception) {
            // Use default value
        }

        drawDistance = i
        avatarCountLimit = i2
    }
}