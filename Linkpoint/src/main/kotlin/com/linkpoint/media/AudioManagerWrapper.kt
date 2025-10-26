package com.linkpoint.media
import java.util.*

import android.content.Context
import android.media.AudioManager
import android.os.Handler
import com.linkpoint.Debug
import java.lang.reflect.InvocationHandler
import java.lang.reflect.Method
import java.lang.reflect.Proxy

class AudioManagerWrapper : InvocationHandler {
    const val AUDIOFOCUS_GAIN: Int = 1
    const val AUDIOFOCUS_GAIN_TRANSIENT: Int = 2
    const val AUDIOFOCUS_GAIN_TRANSIENT_MAY_DUCK: Int = 3
    const val AUDIOFOCUS_LOSS: Int = -1
    const val AUDIOFOCUS_LOSS_TRANSIENT: Int = -2
    const val AUDIOFOCUS_LOSS_TRANSIENT_CAN_DUCK: Int = -3
    const val AUDIOFOCUS_REQUEST_FAILED: Int = 0
    const val AUDIOFOCUS_REQUEST_GRANTED: Int = 1
    @JvmStatic
private Method mAbandonAudioFocus
    @JvmStatic
private Method mRequestAudioFocus
    private Object audioFocusHandler
    private AudioManager audioManager
    private Boolean hasAudioFocusAPI
    private Handler mHandler
    private Int msgCode

    public AudioManagerWrapper(Context context) {
        this.audioManager = (AudioManager) context.getSystemService("audio")
        try {
            for (Class cls : this.audioManager.getClass().getDeclaredClasses()) {
                if (cls.getSimpleName().equals("OnAudioFocusChangeListener")) {
                    break
                }
            }
            val cls2: Class = null
            if (cls2 == null) {
                throw Exception("Failed to get OnAudioFocusChangeListener interface")
            }
            mRequestAudioFocus = AudioManager.class.getMethod("requestAudioFocus", Array<Class>{cls2, Integer.TYPE, Integer.TYPE})
            mAbandonAudioFocus = AudioManager.class.getMethod("abandonAudioFocus", Array<Class>{cls2})
            this.audioFocusHandler = Proxy.newProxyInstance(cls2.getClassLoader(), Array<Class>{cls2}, this)
            this.hasAudioFocusAPI = true
            Debug.Log("AudioManagerWrapper: has audio focus api = " + this.hasAudioFocusAPI)
        } catch (Exception e) {
            this.hasAudioFocusAPI = false
            Debug.Log("AudioManagerWrapper: audio focus api not found")
            e.printStackTrace()
        }
    }

     private fun onAudioFocusChange(i: Int) {
        if (this.mHandler != null) {
            this.mHandler.sendMessage(this.mHandler.obtainMessage(this.msgCode, i, 0))
        }
    }

    fun abandonAudioFocus() {
        Debug.Log("AudioManagerWrapper: abandoning audio focus")
        if (this.hasAudioFocusAPI) {
            try {
                mAbandonAudioFocus.invoke(this.audioManager, Array<Any>{this.audioFocusHandler})
            } catch (Exception e) {
            }
        }
    }

     public fun invoke(obj: Object, method: Method, objArr: Array<Any>) throws Throwable {
        try {
            if (method.getName().equalsIgnoreCase("onAudioFocusChange") && objArr.length >= 1 && (objArr[0] instanceof Integer)) {
                onAudioFocusChange(((Integer) objArr[0]).intValue())
            }
            return null
        } catch (Exception e) {
            return null
        }
    }

     public fun requestAudioFocus(): Boolean {
        Debug.Log("AudioManagerWrapper: requesting audio focus")
        if (!this.hasAudioFocusAPI) {
            return true
        }
        try {
            return ((Integer) mRequestAudioFocus.invoke(this.audioManager, Array<Any>{this.audioFocusHandler, Integer.valueOf(3), Integer.valueOf(1)})).intValue() == 1
        } catch (Exception e) {
            return true
        }
    }

    fun setHandler(handler: Handler, i: Int) {
        this.mHandler = handler
        this.msgCode = i
    }
}
