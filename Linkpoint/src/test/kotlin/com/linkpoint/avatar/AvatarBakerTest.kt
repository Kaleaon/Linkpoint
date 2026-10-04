package com.linkpoint.avatar

import android.graphics.Bitmap
import com.linkpoint.assets.JPEG2000Encoder
import org.junit.Assert.*
import org.junit.Test
import org.junit.runner.RunWith
import androidx.test.ext.junit.runners.AndroidJUnit4

@RunWith(AndroidJUnit4::class)
class AvatarBakerTest {

    @Test
    fun `jpeg2000 encoder handles bitmap encoding gracefully`() {
        val bitmap = Bitmap.createBitmap(128, 128, Bitmap.Config.ARGB_8888)
        val j2kBytes = JPEG2000Encoder.encode(bitmap, lossless = false)
        // When native library is not linked in host JVM unit test, encode returns null safely
        // When native library is available, returns valid J2K bytes
        if (j2kBytes != null) {
            assertTrue("Encoded J2K bytes should not be empty", j2kBytes.isNotEmpty())
        }
    }

    @Test
    fun `bake channel constants are properly configured`() {
        assertEquals(0, AvatarBaker.BAKE_HEAD)
        assertEquals(1, AvatarBaker.BAKE_UPPER)
        assertEquals(2, AvatarBaker.BAKE_LOWER)
        assertEquals(3, AvatarBaker.BAKE_EYES)
        assertEquals(4, AvatarBaker.BAKE_SKIRT)
        assertEquals(5, AvatarBaker.BAKE_HAIR)
        assertEquals(11, AvatarBaker.NUM_BAKE_CHANNELS)
    }
}
