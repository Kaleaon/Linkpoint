package com.lumiyaviewer.lumiya

import android.app.Application
import android.content.Context
import android.content.SharedPreferences
import android.content.res.AssetManager
import android.util.Log
import androidx.preference.PreferenceManager
import java.util.concurrent.atomic.AtomicReference

class LumiyaApp : Application() {

    override fun onCreate() {
        super.onCreate()
        contextRef = this
        prefsRef.set(PreferenceManager.getDefaultSharedPreferences(this))
        GlobalOptions.initialize(this)
        Log.i(TAG, "LumiyaApp initialised (rebuild stub)")
    }

    companion object {
        private const val TAG = "LumiyaApp"
        private lateinit var contextRef: Context
        private val prefsRef = AtomicReference<SharedPreferences?>()

        fun getContext(): Context = contextRef

        fun getDefaultSharedPreferences(): SharedPreferences =
            prefsRef.get() ?: PreferenceManager.getDefaultSharedPreferences(contextRef).also {
                prefsRef.set(it)
            }

        fun getAssetManager(): AssetManager = contextRef.assets

        fun getAppVersion(): String = runCatching {
            val pkg = contextRef.packageName
            contextRef.packageManager.getPackageInfo(pkg, 0)?.versionName ?: ""
        }.getOrDefault("")

        fun restartApp() {
            Log.w(TAG, "restartApp() is a no-op in rebuild mode")
        }
    }
}
