package com.linkpoint.grid

import android.content.Context
import androidx.test.core.app.ApplicationProvider
import com.linkpoint.core.GridManager
import com.linkpoint.feature.auth.resolveAuthGridList
import com.linkpoint.grid.persistence.GridDirectoryDao
import com.linkpoint.grid.persistence.GridProfileEntity
import com.linkpoint.ui.linkpoint2.screens.DefaultPermissions
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.runTest
import org.junit.Assert.*
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config

@RunWith(RobolectricTestRunner::class)
@Config(sdk = [33], manifest = Config.NONE)
class PointOfUsePermissionAndGridConfigTest {

    private lateinit var context: Context
    private lateinit var emptyDao: EmptyInMemoryGridDao
    private lateinit var gridManager: GridManager

    @Before
    fun setUp() {
        context = ApplicationProvider.getApplicationContext<Context>()
        emptyDao = EmptyInMemoryGridDao()
        gridManager = GridManager(context, emptyDao)
    }

    @Test
    fun `test all onboarding default permissions are optional`() {
        for (permission in DefaultPermissions) {
            assertFalse(
                "Permission ${permission.id} (${permission.title}) must be optional (required = false)",
                permission.required
            )
        }
    }

    @Test
    fun `test empty grid storage auto populates default preset grids synchronously sub-10ms`() = runTest {
        val startTime = System.nanoTime()
        val grids = gridManager.getAvailableGridsAsync()
        val durationMs = (System.nanoTime() - startTime) / 1_000_000.0

        assertTrue("Grids should auto-populate and return built-in defaults", grids.isNotEmpty())
        assertEquals("Should return built-in preset grids", GridManager.BUILTIN_GRIDS.size, grids.size)
        assertTrue("Grid lookup duration should be sub-10ms (was ${durationMs}ms)", durationMs < 10.0)
    }

    @Test
    fun `test resolveAuthGridList falls back to builtin grids when local grid storage is empty`() = runTest {
        val resolved = resolveAuthGridList(gridManager)
        assertTrue("Auth gateway must resolve non-empty grid list", resolved.isNotEmpty())
        assertEquals(GridManager.BUILTIN_GRIDS.size, resolved.size)
        assertEquals("secondlife", resolved[0].id)
    }
}

class EmptyInMemoryGridDao : GridDirectoryDao {
    private val grids = LinkedHashMap<String, GridProfileEntity>()

    override fun getAllGridsFlow(): Flow<List<GridProfileEntity>> = flowOf(grids.values.toList())
    override suspend fun getAllGrids(): List<GridProfileEntity> = grids.values.toList()
    override suspend fun getGridById(id: String): GridProfileEntity? = grids[id]
    override suspend fun getGridByLoginUri(loginUri: String, loginUriWithSlash: String): GridProfileEntity? {
        return grids.values.find { it.loginUri == loginUri || it.loginUri == loginUriWithSlash }
    }

    override suspend fun insertGrids(grids: List<GridProfileEntity>) {
        grids.forEach { this.grids[it.id] = it }
    }

    override suspend fun insertGrid(grid: GridProfileEntity) {
        grids[grid.id] = grid
    }

    override suspend fun deleteGrid(id: String) {
        grids.remove(id)
    }

    override suspend fun clearDirectoryGrids() {
        grids.entries.removeIf { !it.value.isCustom }
    }
}
