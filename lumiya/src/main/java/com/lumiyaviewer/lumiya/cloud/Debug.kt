package com.lumiyaviewer.lumiya.cloud

import android.util.Log

object Debug {
    private const val TAG = "LumiyaCloud"

    @JvmStatic
    fun Printf(format: String, vararg args: Any?) {
        try {
            Log.d(TAG, String.format(format, *args))
        } catch (e: Exception) {
            Log.e(TAG, "Error formatting log message: $format", e)
        }
    }

    @JvmStatic
    fun Warning(t: Throwable) {
        Log.w(TAG, "Warning", t)
    }
}
