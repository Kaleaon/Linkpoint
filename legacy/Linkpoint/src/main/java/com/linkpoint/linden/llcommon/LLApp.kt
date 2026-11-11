package com.linkpoint.linden.llcommon

import java.util.concurrent.atomic.AtomicReference

abstract class LLApp {

    enum class AppStatus { RUNNING, QUITTING, STOPPED, ERROR }

    private val status: AtomicReference<AppStatus> = AtomicReference(AppStatus.STOPPED)

    abstract fun init(): Boolean
    abstract fun mainLoop(): Boolean
    abstract fun cleanup(): Boolean

    fun getStatus(): AppStatus = status.get()

    fun setStatus(s: AppStatus) {
        status.set(s)
        Companion.statusRef.set(s)
    }

    fun isRunning(): Boolean = status.get() == AppStatus.RUNNING
    fun isQuitting(): Boolean = status.get() == AppStatus.QUITTING
    fun isError(): Boolean = status.get() == AppStatus.ERROR
    fun isStopped(): Boolean = status.get() == AppStatus.STOPPED
    fun isExiting(): Boolean = isQuitting() || isError()

    // Runs the full application lifecycle. Blocks until the app exits.
    fun run() {
        setStatus(AppStatus.RUNNING)
        startShutdownWatcher()
        try {
            if (!init()) {
                setStatus(AppStatus.ERROR)
                return
            }
            while (isRunning()) {
                if (!mainLoop()) {
                    break
                }
            }
        } catch (t: Throwable) {
            setStatus(AppStatus.ERROR)
            LLError.Log.error("LLApp", "Unhandled exception in run(): ${t.message}")
            throw t
        } finally {
            try {
                cleanup()
            } finally {
                setStatus(AppStatus.STOPPED)
            }
        }
    }

    // Starts a daemon thread that watches for the QUITTING signal and logs it.
    private fun startShutdownWatcher() {
        val app = this
        Thread(null, {
            while (app.isRunning()) {
                Thread.sleep(100)
            }
            if (app.isQuitting()) {
                LLError.Log.info("LLApp", "Application is quitting — shutdown watcher notified")
            }
        }, "LLApp-shutdown-watcher").also { it.isDaemon = true }.start()
    }

    companion object {
        internal val statusRef: AtomicReference<AppStatus> = AtomicReference(AppStatus.STOPPED)

        @Volatile private var instance: LLApp? = null

        @JvmStatic
        fun getInstance(): LLApp? = instance

        @JvmStatic
        fun setInstance(app: LLApp) {
            instance = app
        }

        fun isRunning(): Boolean = statusRef.get() == AppStatus.RUNNING
        fun isQuitting(): Boolean = statusRef.get() == AppStatus.QUITTING
        fun isError(): Boolean = statusRef.get() == AppStatus.ERROR
        fun isStopped(): Boolean = statusRef.get() == AppStatus.STOPPED
        fun isExiting(): Boolean = isQuitting() || isError()

        @JvmStatic
        fun setQuitting() { statusRef.set(AppStatus.QUITTING) }

        @JvmStatic
        fun setStopped() { statusRef.set(AppStatus.STOPPED) }

        @JvmStatic
        fun setError() { statusRef.set(AppStatus.ERROR) }

        @JvmStatic
        fun getPid(): Int = android.os.Process.myPid()
    }
}
