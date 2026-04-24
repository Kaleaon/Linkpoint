package com.linkpoint.protocol.capabilities

import org.junit.Assert.assertTrue
import org.junit.Test
import java.io.File

class CapabilityDeclarationAuditTest {

    @Test
    fun `capability constants must have callsite or explicit cap status annotation`() {
        val repoRoot = locateRepoRoot()
        val capabilityFile = File(repoRoot, "Linkpoint/src/main/java/com/linkpoint/protocol/capabilities/CapabilityManager.kt")
        val sourceText = capabilityFile.readText()
        val lines = capabilityFile.readLines()

        val constantRegex = Regex("""const\\s+val\\s+(CAP_[A-Z0-9_]+)\\s*=\\s*\"[^\"]+\"""")
        val constants = constantRegex.findAll(sourceText).toList()

        val sourceFiles = File(repoRoot, "Linkpoint/src/main/java/com/linkpoint")
            .walkTopDown()
            .filter { it.isFile && it.extension == "kt" }
            .filter { it.absolutePath != capabilityFile.absolutePath }
            .toList()

        val orphans = mutableListOf<String>()

        constants.forEach { match ->
            val constantName = match.groupValues[1]
            val hasCallsite = sourceFiles.any { it.readText().contains(constantName) }
            if (hasCallsite) {
                return@forEach
            }

            val declarationLine = sourceText.substring(0, match.range.first).count { it == '\n' }
            val annotationWindowStart = (declarationLine - 3).coerceAtLeast(0)
            val annotationWindow = lines.subList(annotationWindowStart, declarationLine)
            val hasDeferredAnnotation = annotationWindow.any { it.contains("@cap-status") }

            if (!hasDeferredAnnotation) {
                orphans += constantName
            }
        }

        assertTrue(
            "Found CAP_* constants without callsites or @cap-status annotation: ${orphans.joinToString()}",
            orphans.isEmpty()
        )
    }

    private fun locateRepoRoot(): File {
        var dir = File(System.getProperty("user.dir"))
        repeat(6) {
            if (File(dir, ".git").exists()) return dir
            dir = dir.parentFile ?: return@repeat
        }
        return File(System.getProperty("user.dir"))
    }
}
