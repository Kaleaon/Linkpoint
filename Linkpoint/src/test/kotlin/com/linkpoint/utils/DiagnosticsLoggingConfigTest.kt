package com.linkpoint.utils

import android.os.Build
import org.junit.Assert.assertEquals
import org.junit.Test

class DiagnosticsLoggingConfigTest {

    @Test
    fun `storage target remains app private on legacy and scoped storage apis`() {
        val apis = listOf(24, 28, Build.VERSION_CODES.Q, 34)
        apis.forEach { api ->
            assertEquals(
                DiagnosticsLoggingConfig.StorageTarget.APP_PRIVATE_INTERNAL,
                DiagnosticsLoggingConfig.storageTargetForApi(api)
            )
        }
    }
}
