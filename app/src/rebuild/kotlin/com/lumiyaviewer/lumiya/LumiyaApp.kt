package com.lumiyaviewer.lumiya

import android.app.Application
import android.content.Context
import android.content.SharedPreferences
import android.content.res.AssetManager
import android.util.Log
import androidx.multidex.MultiDex
import androidx.preference.PreferenceManager
import java.util.concurrent.atomic.AtomicReference

class LumiyaApp : Application() {

    override fun onCreate() {
        super.onCreate()
        bootstrapContext(this)
        ensurePreferences(this)
        GlobalOptions.initialize(this)
        Log.i(TAG, "LumiyaApp initialised (rebuild stub)")
    }

    override fun attachBaseContext(base: Context) {
        super.attachBaseContext(base)
        bootstrapContext(base)
        MultiDex.install(this)
    }

    companion object {
        private const val TAG = "LumiyaApp"
        private val contextRef = AtomicReference<Context?>()
        private val prefsRef = AtomicReference<SharedPreferences?>()

        fun getContext(): Context =
            resolveContext() ?: throw IllegalStateException("LumiyaApp context not initialised yet")

        fun getDefaultSharedPreferences(): SharedPreferences =
            prefsRef.get() ?: ensurePreferences(null)

        fun getAssetManager(): AssetManager = getContext().assets

        fun getAppVersion(): String = runCatching {
            val ctx = getContext()
            val pkg = ctx.packageName
            ctx.packageManager.getPackageInfo(pkg, 0)?.versionName ?: ""
        }.getOrDefault("")

        fun restartApp() {
            Log.w(TAG, "restartApp() is a no-op in rebuild mode")
        }

        private fun bootstrapContext(context: Context) {
            val appContext = context.applicationContext
            if (contextRef.compareAndSet(null, appContext)) {
                Log.d(TAG, "LumiyaApp context bootstrapped")
            }
        }

        private fun ensurePreferences(context: Context?): SharedPreferences {
            prefsRef.get()?.let { return it }
            val targetContext = context ?: resolveContext()
            requireNotNull(targetContext) { "LumiyaApp context not initialised yet" }

            return PreferenceManager.getDefaultSharedPreferences(targetContext).also {
                prefsRef.compareAndSet(null, it)
            }
        }

        private fun resolveContext(): Context? {
            contextRef.get()?.let { return it }

            val bootstrap = runCatching {
                val appClass = Class.forName("android.app.AppGlobals")
                val method = appClass.getMethod("getInitialApplication")
                val application = method.invoke(null) as? Application
                application?.applicationContext
            }.getOrNull()

            return bootstrap?.also { contextRef.compareAndSet(null, it) }
        }
    }
}
