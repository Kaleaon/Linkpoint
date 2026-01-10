package com.linkpoint

import android.app.Application
import android.util.Log
import com.linkpoint.network.SecondLifeConnection

/**
 * Main Application class for Linkpoint - Second Life viewer for Android
 */
class LinkpointApp : Application() {
    
    companion object {
        private const val TAG = "LinkpointApp"
        
        @Volatile
        private var instance: LinkpointApp? = null
        
        fun getInstance(): LinkpointApp {
            return instance ?: throw IllegalStateException("Application not initialized")
        }
    }
    
    /**
     * Connection to Second Life grid
     */
    val connection: SecondLifeConnection by lazy { SecondLifeConnection() }
    
    override fun onCreate() {
        super.onCreate()
        instance = this
        Log.i(TAG, "Linkpoint application started")
        
        // Initialize core services
        initializeServices()
    }
    
    private fun initializeServices() {
        // Initialize rendering engine
        Log.d(TAG, "Initializing services...")
        
        // Pre-initialize the connection (lazy, but touch it here to trigger early init if needed)
        Log.d(TAG, "SL Connection initialized: ${connection.isConnected()}")
    }
    
    override fun onTerminate() {
        super.onTerminate()
        Log.i(TAG, "Linkpoint application terminated")
    }
}
