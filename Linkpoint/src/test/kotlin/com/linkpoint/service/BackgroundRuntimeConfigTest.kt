package com.linkpoint.service

import org.junit.Assert.assertEquals
import org.junit.Test

class BackgroundRuntimeConfigTest {

    @Test
    fun `maps conservative profile string`() {
        assertEquals(
            BackgroundRuntimeConfig.IntensityProfile.CONSERVATIVE,
            BackgroundRuntimeConfig.mapIntensityProfile("conservative")
        )
    }

    @Test
    fun `maps aggressive profile string`() {
        assertEquals(
            BackgroundRuntimeConfig.IntensityProfile.AGGRESSIVE,
            BackgroundRuntimeConfig.mapIntensityProfile("aggressive")
        )
    }

    @Test
    fun `defaults to balanced for null or unknown values`() {
        assertEquals(
            BackgroundRuntimeConfig.IntensityProfile.BALANCED,
            BackgroundRuntimeConfig.mapIntensityProfile(null)
        )
        assertEquals(
            BackgroundRuntimeConfig.IntensityProfile.BALANCED,
            BackgroundRuntimeConfig.mapIntensityProfile("unknown")
        )
    }
}
