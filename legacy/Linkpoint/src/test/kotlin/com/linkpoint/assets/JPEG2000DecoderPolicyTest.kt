package com.linkpoint.assets

import org.junit.runner.RunWith
import androidx.test.ext.junit.runners.AndroidJUnit4

import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

@RunWith(AndroidJUnit4::class)
class JPEG2000DecoderPolicyTest {

    @Test
    fun `discard plan is deterministic and bounded`() {
        assertEquals(listOf(0, 1, 2), JPEG2000Decoder.buildDiscardPlan(0))
        assertEquals(listOf(3, 4, 5), JPEG2000Decoder.buildDiscardPlan(3))
        assertEquals(listOf(5), JPEG2000Decoder.buildDiscardPlan(9))
        assertEquals(listOf(0, 1, 2), JPEG2000Decoder.buildDiscardPlan(-9))
    }

    @Test
    fun `startup status always surfaces backend diagnostics`() {
        val status = JPEG2000Decoder.getStartupStatus()
        assertTrue(status.activeBackend.isNotBlank())
        if (!status.available) {
            assertTrue(status.warningMessage?.isNotBlank() == true)
        }
    }
}
