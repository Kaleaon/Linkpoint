package com.lumiyaviewer.lumiya

import android.app.Application
import android.util.Log

class LumiyaApp : Application() {
    override fun onCreate() {
        super.onCreate()
        Log.i("LumiyaApp", "Initialized rebuild stub")
    }
}
