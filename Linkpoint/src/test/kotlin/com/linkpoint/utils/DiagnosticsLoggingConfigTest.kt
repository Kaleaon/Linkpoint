package com.linkpoint.utils

import android.os.Build
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test
import java.nio.file.Files
import java.util.concurrent.TimeUnit

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

    @Test
    fun `purgeExpiredLogs deletes files older than retention and keeps recent files`() {
        val tempDir = Files.createTempDirectory("diag-logs").toFile()
        val now = 1_000_000L
        val retentionDays = 7
        val cutoff = now - TimeUnit.DAYS.toMillis(retentionDays.toLong())

        val oldFile = Files.createTempFile(tempDir.toPath(), "old", ".log").toFile().apply {
            setLastModified(cutoff - 1)
        }
        val recentFile = Files.createTempFile(tempDir.toPath(), "recent", ".log").toFile().apply {
            setLastModified(cutoff + 1)
        }

        DiagnosticsLoggingConfig.purgeExpiredLogs(tempDir, retentionDays, now)

        assertFalse(oldFile.exists())
        assertTrue(recentFile.exists())
    }

    @Test
    fun `purgeExpiredLogs keeps files exactly at cutoff timestamp`() {
        val tempDir = Files.createTempDirectory("diag-cutoff").toFile()
        val now = 2_000_000L
        val retentionDays = 3
        val cutoff = now - TimeUnit.DAYS.toMillis(retentionDays.toLong())

        val boundaryFile = Files.createTempFile(tempDir.toPath(), "boundary", ".log").toFile().apply {
            setLastModified(cutoff)
        }

        DiagnosticsLoggingConfig.purgeExpiredLogs(tempDir, retentionDays, now)

        assertTrue(boundaryFile.exists())
    }

    @Test
    fun `purgeExpiredLogs ignores non-file entries`() {
        val tempDir = Files.createTempDirectory("diag-non-file").toFile()
        val nestedDir = Files.createDirectory(tempDir.toPath().resolve("nested")).toFile()

        DiagnosticsLoggingConfig.purgeExpiredLogs(tempDir, retentionDays = 1, nowMs = 3_000_000L)

        assertTrue(nestedDir.exists())
        assertTrue(nestedDir.isDirectory)
    }

    @Test
    fun `purgeExpiredLogs safely handles missing directory`() {
        val missingDir = Files.createTempDirectory("diag-missing").toFile().apply { deleteRecursively() }

        DiagnosticsLoggingConfig.purgeExpiredLogs(missingDir, retentionDays = 7, nowMs = 4_000_000L)

        assertFalse(missingDir.exists())
    }
}
