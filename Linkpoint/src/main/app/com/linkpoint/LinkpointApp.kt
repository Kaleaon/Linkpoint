package com.linkpoint

import android.app.Application
import android.content.Context
import android.content.SharedPreferences
import android.content.res.AssetManager
import android.util.Log
import androidx.multidex.MultiDex
import androidx.preference.PreferenceManager
import com.linkpoint.modern.graphics.ModernGraphicsSupport
import java.util.concurrent.atomic.AtomicReference

class LinkpointApp : Application() {

    override fun onCreate() {
        super.onCreate()
        bootstrapContext(this)
        ensurePreferences(this)
        val graphicsStatus = ModernGraphicsSupport.evaluate(this)
        modernGraphicsStatus.compareAndSet(null, graphicsStatus)
        Log.i(TAG, "Modern graphics status: ${graphicsStatus.describe()}")
        GlobalOptions.initialize(this)
        Log.i(TAG, "LinkpointApp initialised (rebuild stub)")
    }

    override fun attachBaseContext(base: Context) {
        super.attachBaseContext(base)
        bootstrapContext(base)
        MultiDex.install(this)
    }

    companion object {
        private const val TAG = "LinkpointApp"
        private val contextRef = AtomicReference<Context?>()
        private val prefsRef = AtomicReference<SharedPreferences?>()
        private val modernGraphicsStatus = AtomicReference<ModernGraphicsSupport.Status?>()

        fun getContext(): Context =
            resolveContext() ?: throw IllegalStateException("LinkpointApp context not initialised yet")

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

        fun isModernGraphicsAvailable(): Boolean =
            modernGraphicsStatus.get()?.supported == true

        fun getModernGraphicsStatus(): ModernGraphicsSupport.Status? =
            modernGraphicsStatus.get()

        fun setModernGraphicsStatus(status: ModernGraphicsSupport.Status) {
            modernGraphicsStatus.set(status)
        }

        private fun bootstrapContext(context: Context) {
            val appContext = context.applicationContext
            if (contextRef.compareAndSet(null, appContext)) {
                Log.d(TAG, "LinkpointApp context bootstrapped")
            }
        }

        private fun ensurePreferences(context: Context?): SharedPreferences {
            prefsRef.get()?.let { return it }
            val targetContext = context ?: resolveContext()
            requireNotNull(targetContext) { "LinkpointApp context not initialised yet" }

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
