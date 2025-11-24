package com.lumiyaviewer.lumiya.modern.samples

import android.content.Context
import android.util.Log
import com.lumiyaviewer.lumiya.modern.graphics.ModernRenderPipeline
import com.lumiyaviewer.lumiya.modern.graphics.ModernTextureManager
import com.lumiyaviewer.lumiya.modern.protocol.HybridSLTransport
import com.lumiyaviewer.lumiya.modern.protocol.WebSocketEventClient
import com.lumiyaviewer.lumiya.modern.auth.OAuth2AuthManager
import com.lumiyaviewer.lumiya.modern.assets.ModernAssetManager

/**
 * Demonstration of modern Linkpoint components
 */
class ModernLinkpointDemo(private val context: Context) {
    private val TAG = "ModernLinkpointDemo"
    
    private var transport: HybridSLTransport? = null
    private var textureManager: ModernTextureManager? = null
    private var renderPipeline: ModernRenderPipeline? = null
    private var authManager: OAuth2AuthManager? = null
    private var assetManager: ModernAssetManager? = null
    
    init {
        Log.i(TAG, "Initializing ModernLinkpointDemo components...")
        
        transport = initializeTransport()
        textureManager = initializeTextureManager(context)
        renderPipeline = initializeRenderPipeline()
        authManager = initializeAuthManager(context)
        assetManager = initializeAssetManager(context)
        
        Log.i(TAG, "Modern Linkpoint components initialized with graceful error handling")
    }
    
    private fun initializeTransport(): HybridSLTransport? {
        return try {
            HybridSLTransport()
        } catch (e: Exception) {
            Log.e(TAG, "Failed to initialize transport - using null fallback", e)
            null
        }
    }
    
    private fun initializeTextureManager(context: Context): ModernTextureManager? {
        return try {
            ModernTextureManager(context)
        } catch (e: Exception) {
            Log.e(TAG, "Failed to initialize texture manager - using null fallback", e)
            null
        }
    }
    
    private fun initializeRenderPipeline(): ModernRenderPipeline? {
        return try {
            ModernRenderPipeline()
        } catch (e: Exception) {
            Log.e(TAG, "Failed to initialize render pipeline - using null fallback", e)
            null
        }
    }
    
    private fun initializeAuthManager(context: Context): OAuth2AuthManager? {
        return try {
            OAuth2AuthManager(context)
        } catch (e: Exception) {
            Log.e(TAG, "Failed to initialize auth manager - using null fallback", e)
            null
        }
    }
    
    private fun initializeAssetManager(context: Context): ModernAssetManager? {
        return try {
            ModernAssetManager(context)
        } catch (e: Exception) {
            Log.e(TAG, "Failed to initialize asset manager - using null fallback", e)
            null
        }
    }
    
    fun connectToSecondLife(eventQueueUrl: String, seedCapability: String, authToken: String) {
        Log.i(TAG, "Connecting to Second Life with modern protocols...")
        
        val t = transport
        if (t == null) {
            Log.w(TAG, "Transport not available - cannot connect to Second Life")
            return
        }
        
        try {
            t.setAuthToken(authToken)
            t.initialize(eventQueueUrl, seedCapability)
            
            t.subscribeToEvents("chat", object : WebSocketEventClient.EventListener {
                override fun onEvent(event: WebSocketEventClient.EventMessage) {
                    Log.d(TAG, "Received chat event: " + event.getData())
                }
            })
        
            t.subscribeToEvents("objectUpdate", object : WebSocketEventClient.EventListener {
                override fun onEvent(event: WebSocketEventClient.EventMessage) {
                    Log.d(TAG, "Received object update: " + event.getData())
                }
            })
            
            Log.i(TAG, "Modern transport layer connected successfully")
        } catch (e: Exception) {
            Log.e(TAG, "Failed to connect to Second Life", e)
        }
    }
    
    fun initializeGraphics() {
        Log.i(TAG, "Initializing modernized graphics system (ES 3.0+ only)...")
        
        val pipeline = renderPipeline
        if (pipeline == null) {
            Log.w(TAG, "Render pipeline not available - graphics initialization skipped")
            return
        }
        
        try {
            val success = pipeline.initialize()
            if (success) {
                if (pipeline.isModernPipelineAvailable()) {
                    Log.i(TAG, "Modern PBR rendering pipeline enabled (ES 3.0+)")
                    Log.i(TAG, "Legacy ES 1.1/2.0 code paths removed")
                } else {
                    Log.e(TAG, "CRITICAL: OpenGL ES 3.0+ required but not available")
                    Log.e(TAG, "Device does not meet minimum graphics requirements")
                    return
                }
            }
            
            val tm = textureManager
            if (tm != null) {
                try {
                    val optimalFormat = tm.getOptimalTextureFormat()
                    val formatName = getFormatName(optimalFormat)
                    Log.i(TAG, "Optimal texture format: " + formatName + " (ES 3.0 ETC2+ only)")
                    Log.i(TAG, "Legacy JPEG2000 format support removed")
                } catch (e: Exception) {
                    Log.w(TAG, "Modern texture manager not fully available - using fallback", e)
                }
            } else {
                Log.w(TAG, "Texture manager not available - texture operations will be limited")
            }
            
            Log.i(TAG, "Graphics system initialized successfully")
        } catch (e: Exception) {
            Log.e(TAG, "Failed to initialize graphics system", e)
        }
    }
    
    fun isConnected(): Boolean {
        val t = transport ?: return false
        return try {
            t.isConnected()
        } catch (e: Exception) {
            Log.e(TAG, "Error checking connection status", e)
            false
        }
    }
    
    fun getGraphicsInfo(): String {
        return try {
            val modernPipeline = renderPipeline != null && renderPipeline!!.isModernPipelineAvailable()
            
            var textureFormat = 0
            if (textureManager != null) {
                textureFormat = textureManager!!.getOptimalTextureFormat()
            }
            val formatName = getFormatName(textureFormat)
            
            String.format("Modern Pipeline: %s, Optimal Texture: %s", 
                               if (modernPipeline) "Available" else "Legacy", formatName)
        } catch (e: Exception) {
            Log.e(TAG, "Error getting graphics info", e)
            "Graphics info unavailable"
        }
    }
    
    private fun getFormatName(format: Int): String {
        return when (format) {
            0x93B0 -> "ASTC 4x4 RGBA"
            0x9278 -> "ETC2 RGBA"
            0x1908 -> "RGBA32"
            else -> "Unknown Format"
        }
    }
}
