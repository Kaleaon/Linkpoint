package com.linkpoint.protocol.types

import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test
import kotlin.math.abs

class LLQuaternionVectorRegressionTest {

    @Test
    fun vectorFiniteAndNormalizeRejectNaNInfinity() {
        val nanVector = LLVector3(Float.NaN, 1f, 2f)
        val infVector = LLVector3(Float.POSITIVE_INFINITY, 1f, 2f)

        assertFalse(nanVector.isFinite())
        assertFalse(infVector.isFinite())
        assertEquals(LLVector3.zero(), nanVector.normalize())
        assertEquals(LLVector3.zero(), infVector.normalize())
    }

    @Test
    fun quaternionFiniteAndNormalizeRejectNaNInfinity() {
        val nanQuaternion = LLQuaternion(Float.NaN, 0f, 0f, 1f)
        val infQuaternion = LLQuaternion(0f, Float.NEGATIVE_INFINITY, 0f, 1f)

        assertFalse(nanQuaternion.isFinite())
        assertFalse(infQuaternion.isFinite())
        assertEquals(LLQuaternion.identity(), nanQuaternion.normalize())
        assertEquals(LLQuaternion.identity(), infQuaternion.normalize())
    }

    @Test
    fun normalizeHandlesNearZeroMagnitudes() {
        val tinyVector = LLVector3(1e-9f, -1e-9f, 1e-9f)
        val tinyQuaternion = LLQuaternion(1e-9f, -1e-9f, 1e-9f, -1e-9f)

        assertEquals(LLVector3.zero(), tinyVector.normalize())
        assertEquals(LLQuaternion.identity(), tinyQuaternion.normalize())
    }

    @Test
    fun eulerRoundTripNearGimbalRemainsStable() {
        val pitch = (Math.PI.toFloat() / 2f) - 1e-4f
        val roll = 0.3f
        val yaw = -1.2f
        val original = LLQuaternion.fromEuler(pitch, roll, yaw)

        val euler = original.toEuler()
        val reconstructed = LLQuaternion.fromEuler(euler.x, euler.y, euler.z)
        val dot = abs(
            original.x * reconstructed.x +
                original.y * reconstructed.y +
                original.z * reconstructed.z +
                original.w * reconstructed.w
        )

        assertTrue("Expected orientation-preserving round-trip, got dot=$dot", dot > 0.999f)
    }
}
