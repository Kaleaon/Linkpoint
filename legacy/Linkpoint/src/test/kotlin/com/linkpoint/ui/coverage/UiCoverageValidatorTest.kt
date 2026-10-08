package com.linkpoint.ui.coverage

import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test
import java.nio.charset.StandardCharsets
import java.nio.file.Files
import java.nio.file.Path
import java.nio.file.Paths

// Helpers: project compiles against Java 1.8, so the JDK 11+ extension
// Files.readString isn't available. These shims keep the validator
// readable while staying source-compatible.
private fun readText(path: Path): String =
    String(Files.readAllBytes(path), StandardCharsets.UTF_8)

class UiCoverageValidatorTest {

    @Test
    fun `no duplicate built-in theme ids`() {
        val themeFile = sourcePath("src/main/java/com/linkpoint/ui/theme/BuiltInThemes.kt")
        val text = readText(themeFile)

        val ids = Regex("""id\s*=\s*\"([^\"]+)\"""")
            .findAll(text)
            .map { it.groupValues[1] }
            .toList()

        val duplicates = ids.groupingBy { it }.eachCount().filterValues { it > 1 }.keys
        assertTrue("Duplicate theme IDs found: $duplicates", duplicates.isEmpty())
    }

    @Test
    fun `no orphaned menu routes`() {
        val navDrawer = readText(sourcePath("src/main/res/menu/nav_drawer.xml"))
        val worldView = readText(sourcePath("src/main/java/com/linkpoint/ui/world/WorldViewActivity.kt"))
        val matrixRows = readRouteMatrixRows()

        val menuIds = Regex("""@\+id/(nav_[a-z_]+)""")
            .findAll(navDrawer)
            .map { it.groupValues[1] }
            .toSet()

        val handledIds = Regex("""R\.id\.(nav_[a-z_]+)\s*->""")
            .findAll(worldView)
            .map { it.groupValues[1] }
            .toSet()

        val expectedNonRouteItems = setOf("nav_teleport_home", "nav_logout")
        val matrixMenuIds = matrixRows.mapNotNull { it["menu_id"]?.ifBlank { null } }.toSet()

        val orphanedMenuIds = (menuIds - expectedNonRouteItems) - matrixMenuIds
        val unhandledMenuIds = menuIds - handledIds

        assertTrue("Menu entries missing route mapping in matrix: $orphanedMenuIds", orphanedMenuIds.isEmpty())
        assertTrue("Menu entries missing handler branches in WorldViewActivity: $unhandledMenuIds", unhandledMenuIds.isEmpty())
    }

    @Test
    fun `route metadata is complete for all routes`() {
        val navigationText = readText(sourcePath("src/main/java/com/linkpoint/ui/navigation/Navigation.kt"))
        val routes = Regex("""const\s+val\s+([A-Z_]+)\s*=\s*\"([^\"]+)\"""")
            .findAll(navigationText)
            .associate { it.groupValues[1] to it.groupValues[2] }

        val matrixRows = readRouteMatrixRows()
        val matrixByConst = matrixRows.associateBy { it["route_const"].orEmpty() }

        val missingRows = routes.keys - matrixByConst.keys
        assertTrue("Routes missing matrix rows: $missingRows", missingRows.isEmpty())

        val incomplete = routes.keys.filter { routeConst ->
            val row = matrixByConst[routeConst]
            row == null || row["route_pattern"].isNullOrBlank() || row["destination_type"].isNullOrBlank()
        }
        assertTrue("Routes with incomplete metadata: $incomplete", incomplete.isEmpty())

        val mismatchedPattern = routes.keys.filter { routeConst ->
            val rowPattern = matrixByConst[routeConst]?.get("route_pattern")
            rowPattern != routes[routeConst]
        }
        assertEquals("Route pattern mismatch between Navigation.kt and matrix", emptyList<String>(), mismatchedPattern)
    }

    private fun readRouteMatrixRows(): List<Map<String, String>> {
        val matrixPath = projectPath("scripts/ui/route-migration-matrix.csv")
        val lines = Files.readAllLines(matrixPath).filter { it.isNotBlank() }
        val headers = lines.first().split(',')
        return lines.drop(1).map { line ->
            val values = line.split(',')
            headers.indices.associate { idx -> headers[idx] to values.getOrElse(idx) { "" } }
        }
    }

    private fun sourcePath(relativePath: String): Path {
        val root = findRepoRoot()
        val direct = root.resolve(relativePath)
        if (Files.exists(direct)) return direct
        val nested = root.resolve("Linkpoint").resolve(relativePath)
        if (Files.exists(nested)) return nested
        error("Unable to resolve source path: $relativePath")
    }

    private fun projectPath(relativePath: String): Path {
        val root = findRepoRoot()
        val direct = root.resolve(relativePath)
        if (Files.exists(direct)) return direct
        error("Unable to resolve project path: $relativePath")
    }

    private fun findRepoRoot(): Path {
        var current = Paths.get("").toAbsolutePath().normalize()
        repeat(6) {
            if (Files.exists(current.resolve(".git"))) return current
            current = current.parent ?: return@repeat
        }
        error("Could not locate repository root from ${Paths.get("").toAbsolutePath()}")
    }
}
