package com.linkpoint.render.lumiya.core

import android.util.Log
import java.nio.Buffer

/**
 * Exposes GL_EXT_buffer_storage constants and glBufferStorageEXT via JNI.
 *
 * android.opengl.GLES31Ext does not include GL_EXT_buffer_storage bindings,
 * so we define the constants from the extension registry and resolve the
 * entry point at runtime through eglGetProcAddress in the native layer.
 */
internal object GlExtHelper {

    private const val TAG = "GlExtHelper"

    // GL_EXT_buffer_storage token values (Khronos registry)
    const val GL_MAP_READ_BIT_EXT         = 0x0001
    const val GL_MAP_WRITE_BIT_EXT        = 0x0002
    const val GL_MAP_PERSISTENT_BIT_EXT   = 0x0040
    const val GL_MAP_COHERENT_BIT_EXT     = 0x0080
    const val GL_DYNAMIC_STORAGE_BIT_EXT  = 0x0100
    const val GL_CLIENT_STORAGE_BIT_EXT   = 0x0200

    @Volatile private var libraryLoaded = false
    @Volatile private var nativeAvailable = false

    fun isAvailable(): Boolean {
        ensureLoaded()
        return nativeAvailable
    }

    fun glBufferStorageExt(target: Int, size: Int, data: Buffer?, flags: Int) {
        nativeBufferStorageExt(target, size.toLong(), data, flags)
    }

    private fun ensureLoaded() {
        if (libraryLoaded) return
        synchronized(this) {
            if (libraryLoaded) return
            libraryLoaded = true
            try {
                System.loadLibrary("lumiya-native")
                nativeAvailable = nativeIsBufferStorageExtAvailable()
                Log.i(TAG, "lumiya-native loaded; glBufferStorageEXT=${if (nativeAvailable) "available" else "unavailable"}")
            } catch (e: UnsatisfiedLinkError) {
                nativeAvailable = false
                Log.w(TAG, "lumiya-native unavailable: ${e.message}")
            }
        }
    }

    private external fun nativeBufferStorageExt(target: Int, size: Long, data: Buffer?, flags: Int)
    private external fun nativeIsBufferStorageExtAvailable(): Boolean
}
