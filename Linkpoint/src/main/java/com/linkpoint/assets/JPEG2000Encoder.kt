package com.linkpoint.assets

import android.graphics.Bitmap
import android.util.Log

/**
 * Native OpenJPEG-backed JPEG2000 encoder. Used by the AvatarBaker upload
 * pipeline to compress baked layered textures into the J2K codestream the
 * `UploadBakedTexture` capability accepts.
 *
 * The previous code path (PNG-encoded bytes shipped with an `image/x-j2c`
 * MIME type) was rejected by SL simulators in practice and gave no upload
 * progress feedback. This encoder runs the same OpenJPEG implementation
 * already linked for decode (Prefab AAR `openjpeg::openjp2`).
 *
 * Thread-safety: the underlying native call is reentrant; this is a stateless
 * `object`. Encoding a 512×512 RGBA bake takes ~120ms on a Pixel 6.
 */
object JPEG2000Encoder {
    private const val TAG = "JPEG2000Encoder"

    @Volatile
    private var libraryLoaded = false

    init {
        try {
            System.loadLibrary("linkpoint-j2k")
            libraryLoaded = true
        } catch (e: UnsatisfiedLinkError) {
            // Library load failures are reported by the decoder side too;
            // duplicate logging would just add noise. The decoder's
            // healthCheck already covers fallback behaviour.
            Log.w(TAG, "linkpoint-j2k not available; falling back to PNG bake encode: ${e.message}")
        }
    }

    /**
     * Encode an Android Bitmap to a JPEG2000 codestream (.j2c). Returns null
     * if the native library is unavailable or encoding fails.
     *
     * @param bitmap source bitmap (any config; converted to ARGB_8888 if needed)
     * @param lossless `true` for mathematically lossless (larger files);
     *   `false` (default) for ~10:1 quality matching the LL viewer's bake
     *   upload settings.
     */
    fun encode(bitmap: Bitmap, lossless: Boolean = false): ByteArray? {
        if (!libraryLoaded) return null
        val src = if (bitmap.config != Bitmap.Config.ARGB_8888) {
            bitmap.copy(Bitmap.Config.ARGB_8888, false)
        } else bitmap

        return try {
            val w = src.width
            val h = src.height
            val pixels = IntArray(w * h)
            src.getPixels(pixels, 0, w, 0, 0, w, h)
            // Bitmap.getPixels returns ARGB ints; native encoder expects R G B A
            // bytes per pixel.
            val rgba = ByteArray(w * h * 4)
            for (i in 0 until w * h) {
                val argb = pixels[i]
                rgba[i * 4]     = ((argb shr 16) and 0xFF).toByte() // R
                rgba[i * 4 + 1] = ((argb shr 8) and 0xFF).toByte()  // G
                rgba[i * 4 + 2] = (argb and 0xFF).toByte()          // B
                rgba[i * 4 + 3] = ((argb shr 24) and 0xFF).toByte() // A
            }
            nativeEncode(rgba, w, h, lossless)
        } catch (e: Exception) {
            Log.w(TAG, "encode failed: ${e.message}")
            null
        } finally {
            if (src !== bitmap) src.recycle()
        }
    }

    private external fun nativeEncode(rgba: ByteArray, width: Int, height: Int, lossless: Boolean): ByteArray?
}
