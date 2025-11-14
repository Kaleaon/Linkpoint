package com.lumiyaviewer.lumiya.react

import android.os.Handler
import android.os.Looper
import java.util.concurrent.Executor

/**
 * Executor that runs commands on the Android UI thread.
 * 
 * This executor ensures that all submitted tasks are executed on the main/UI thread,
 * which is required for UI updates and certain Android framework operations.
 * 
 * Thread-safe and lifecycle-aware implementation using Android's Handler mechanism.
 */
class UIThreadExecutor : Executor {
    
    companion object {
        private const val TAG = "UIThreadExecutor"
        
        /**
         * Shared instance for common use cases.
         * Use this instead of creating multiple instances.
         */
        @Volatile
        private var instance: UIThreadExecutor? = null
        
        fun getInstance(): UIThreadExecutor {
            return instance ?: synchronized(this) {
                instance ?: UIThreadExecutor().also { instance = it }
            }
        }
    }
    
    // Handler tied to the main/UI thread's Looper
    private val mainHandler: Handler = Handler(Looper.getMainLooper())
    
    /**
     * Executes the given command on the UI thread.
     * 
     * If already on the UI thread, executes immediately.
     * Otherwise, posts to the UI thread's message queue.
     * 
     * @param command The Runnable task to execute
     */
    override fun execute(command: Runnable) {
        if (Looper.myLooper() == Looper.getMainLooper()) {
            // Already on UI thread, execute immediately
            try {
                command.run()
            } catch (e: Exception) {
                android.util.Log.e(TAG, "Error executing command on UI thread", e)
            }
        } else {
            // Post to UI thread
            mainHandler.post {
                try {
                    command.run()
                } catch (e: Exception) {
                    android.util.Log.e(TAG, "Error executing posted command on UI thread", e)
                }
            }
        }
    }
    
    /**
     * Executes the given command on the UI thread after a delay.
     * 
     * @param command The Runnable task to execute
     * @param delayMillis Delay in milliseconds before execution
     */
    fun executeDelayed(command: Runnable, delayMillis: Long) {
        mainHandler.postDelayed({
            try {
                command.run()
            } catch (e: Exception) {
                android.util.Log.e(TAG, "Error executing delayed command on UI thread", e)
            }
        }, delayMillis)
    }
    
    /**
     * Removes all pending posts of the given Runnable from the message queue.
     * 
     * @param command The Runnable to remove
     */
    fun removeCallbacks(command: Runnable) {
        mainHandler.removeCallbacks(command)
    }
    
    /**
     * Checks if the current thread is the UI thread.
     * 
     * @return true if on UI thread, false otherwise
     */
    fun isOnUIThread(): Boolean {
        return Looper.myLooper() == Looper.getMainLooper()
    }
    
    /**
     * Ensures that the current code is running on the UI thread.
     * Throws an exception if not.
     */
    fun assertUIThread() {
        if (!isOnUIThread()) {
            throw IllegalStateException("Must be called from UI thread")
        }
    }
}