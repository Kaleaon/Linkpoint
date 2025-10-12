package com.lumiyaviewer.lumiya.media

import android.content.Context
import android.media.AudioManager
import android.os.Handler
import com.lumiyaviewer.lumiya.Debug
import java.lang.reflect.InvocationHandler
import java.lang.reflect.Method
import java.lang.reflect.Proxy

/**
 * Modern Kotlin AudioManagerWrapper
 * Wraps AudioManager with audio focus handling using reflection for compatibility
 */
class AudioManagerWrapper(context: Context) : InvocationHandler {
    
    private val audioManager: AudioManager = 
        context.getSystemService(Context.AUDIO_SERVICE) as AudioManager
    
    private var audioFocusHandler: Any? = null
    private var hasAudioFocusAPI: Boolean = false
    private var mHandler: Handler? = null
    private var msgCode: Int = 0
    
    companion object {
        const val AUDIOFOCUS_GAIN = 1
        const val AUDIOFOCUS_GAIN_TRANSIENT = 2
        const val AUDIOFOCUS_GAIN_TRANSIENT_MAY_DUCK = 3
        const val AUDIOFOCUS_LOSS = -1
        const val AUDIOFOCUS_LOSS_TRANSIENT = -2
        const val AUDIOFOCUS_LOSS_TRANSIENT_CAN_DUCK = -3
        const val AUDIOFOCUS_REQUEST_FAILED = 0
        const val AUDIOFOCUS_REQUEST_GRANTED = 1
        
        private var mRequestAudioFocus: Method? = null
        private var mAbandonAudioFocus: Method? = null
    }
    
    init {
        try {
            // Find OnAudioFocusChangeListener interface
            val listenerClass = audioManager.javaClass.declaredClasses
                .firstOrNull { it.simpleName == "OnAudioFocusChangeListener" }
                ?: throw Exception("Failed to get OnAudioFocusChangeListener interface")
            
            // Get methods via reflection
            mRequestAudioFocus = AudioManager::class.java.getMethod(
                "requestAudioFocus",
                listenerClass,
                Int::class.javaPrimitiveType,
                Int::class.javaPrimitiveType
            )
            
            mAbandonAudioFocus = AudioManager::class.java.getMethod(
                "abandonAudioFocus",
                listenerClass
            )
            
            // Create proxy for the listener
            audioFocusHandler = Proxy.newProxyInstance(
                listenerClass.classLoader,
                arrayOf(listenerClass),
                this
            )
            
            hasAudioFocusAPI = true
            Debug.Log("AudioManagerWrapper: has audio focus api = $hasAudioFocusAPI")
        } catch (e: Exception) {
            hasAudioFocusAPI = false
            Debug.Log("AudioManagerWrapper: audio focus api not found")
            e.printStackTrace()
        }
    }
    
    /**
     * Handles audio focus changes
     */
    private fun onAudioFocusChange(focusChange: Int) {
        mHandler?.sendMessage(
            mHandler?.obtainMessage(msgCode, focusChange, 0)
        )
    }
    
    /**
     * Requests audio focus
     * @return true if focus was granted or API not available, false otherwise
     */
    fun requestAudioFocus(): Boolean {
        Debug.Log("AudioManagerWrapper: requesting audio focus")
        
        if (!hasAudioFocusAPI) return true
        
        return try {
            val result = mRequestAudioFocus?.invoke(
                audioManager,
                audioFocusHandler,
                AudioManager.STREAM_MUSIC,
                AUDIOFOCUS_GAIN
            ) as? Int
            
            result == AUDIOFOCUS_REQUEST_GRANTED
        } catch (e: Exception) {
            true // Assume success if reflection fails
        }
    }
    
    /**
     * Abandons audio focus
     */
    fun abandonAudioFocus() {
        Debug.Log("AudioManagerWrapper: abandoning audio focus")
        
        if (hasAudioFocusAPI) {
            try {
                mAbandonAudioFocus?.invoke(audioManager, audioFocusHandler)
            } catch (e: Exception) {
                // Ignore errors
            }
        }
    }
    
    /**
     * Sets the handler for audio focus change callbacks
     */
    fun setHandler(handler: Handler?, messageCode: Int) {
        mHandler = handler
        msgCode = messageCode
    }
    
    /**
     * InvocationHandler implementation for proxy
     */
    override fun invoke(proxy: Any?, method: Method?, args: Array<out Any>?): Any? {
        return try {
            if (method?.name.equals("onAudioFocusChange", ignoreCase = true) &&
                args != null && args.isNotEmpty() && args[0] is Int) {
                onAudioFocusChange(args[0] as Int)
            }
            null
        } catch (e: Exception) {
            null
        }
    }
}