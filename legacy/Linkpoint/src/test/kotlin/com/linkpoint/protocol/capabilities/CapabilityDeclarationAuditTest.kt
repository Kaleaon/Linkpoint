package com.linkpoint.protocol.capabilities

import org.junit.Assert.assertTrue
import org.junit.Test
import java.io.File

class CapabilityDeclarationAuditTest {

    @Test
    fun `capability constants must have callsite or explicit cap status annotation`() {
        val repoRoot = locateRepoRoot()
        val capabilityFile = locateCapabilityFile(repoRoot)
        val sourceText = capabilityFile.readText()
        val lines = capabilityFile.readLines()

        val constantRegex = Regex("""const\s+val\s+(CAP_[A-Z0-9_]+)\s*=\s*"[^"]+"""")
        val constants = constantRegex.findAll(sourceText).toList()

        val sourceFiles = locateSourceDir(repoRoot)
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

    private fun locateCapabilityFile(repoRoot: File): File {
        val path0 = File(repoRoot, "legacy/Linkpoint/src/main/java/com/linkpoint/protocol/capabilities/CapabilityManager.kt")
        if (path0.exists()) return path0
        val path1 = File(repoRoot, "Linkpoint/src/main/java/com/linkpoint/protocol/capabilities/CapabilityManager.kt")
        if (path1.exists()) return path1
        val path2 = File(repoRoot, "src/main/java/com/linkpoint/protocol/capabilities/CapabilityManager.kt")
        if (path2.exists()) return path2
        return path1
    }

    private fun locateSourceDir(repoRoot: File): File {
        val dir0 = File(repoRoot, "legacy/Linkpoint/src/main/java/com/linkpoint")
        if (dir0.exists()) return dir0
        val dir1 = File(repoRoot, "Linkpoint/src/main/java/com/linkpoint")
        if (dir1.exists()) return dir1
        val dir2 = File(repoRoot, "src/main/java/com/linkpoint")
        if (dir2.exists()) return dir2
        return dir1
    }

    private fun locateRepoRoot(): File {
        val userDir = System.getProperty("user.dir") ?: "."
        var dir: File? = File(userDir)
        repeat(6) {
            val current = dir ?: return@repeat
            if (File(current, ".git").exists() || File(current, "legacy/Linkpoint/src").exists() || File(current, "Linkpoint/src").exists()) {
                return current
            }
            dir = current.parentFile
        }
        return File(userDir)
    }
}
