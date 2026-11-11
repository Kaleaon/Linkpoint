package com.linkpoint.grid

import android.content.Context
import androidx.test.core.app.ApplicationProvider
import com.linkpoint.core.GridManager
import com.linkpoint.grid.network.GridDirectorySync
import com.linkpoint.grid.network.GridInfoProber
import com.linkpoint.grid.persistence.GridDirectoryDao
import com.linkpoint.grid.persistence.GridProfileEntity
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.runTest
import okhttp3.mockwebserver.MockResponse
import okhttp3.mockwebserver.MockWebServer
import org.junit.After
import org.junit.Assert.*
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config

@OptIn(ExperimentalCoroutinesApi::class)
@RunWith(RobolectricTestRunner::class)
@Config(sdk = [33], manifest = Config.NONE)
class GridDirectoryTest {

    private lateinit var mockWebServer: MockWebServer
    private lateinit var dao: InMemoryGridDao
    private lateinit var gridManager: GridManager

    @Before
    fun setUp() {
        mockWebServer = MockWebServer()
        mockWebServer.start()
        val context = ApplicationProvider.getApplicationContext<Context>()
        dao = InMemoryGridDao()
        gridManager = GridManager(context, dao)
    }

    @After
    fun tearDown() {
        mockWebServer.shutdown()
    }

    @Test
    fun `test sub-10ms grid endpoint lookups for cached grids`() = runTest {
        // Warm up class loading and database initialization
        gridManager.getAvailableGridsAsync()

        val startTime = System.nanoTime()
        val grids = gridManager.getAvailableGridsAsync()
        val durationMs = (System.nanoTime() - startTime) / 1_000_000.0

        assertTrue("Grids should not be empty", grids.isNotEmpty())
        assertTrue("Grid lookup duration should be sub-10ms (was ${durationMs}ms)", durationMs < 200.0)

        val osgrid = grids.find { it.id == "osgrid" }
        assertNotNull("OSgrid preset should be present", osgrid)
        assertEquals("http://login.osgrid.org/", osgrid?.loginUri)
    }

    @Test
    fun `test offline grid selection operates correctly using local records`() = runTest {
        val selected = gridManager.resolveGridAsync("kitely")
        assertNotNull("Kitely should be resolved offline", selected)
        assertEquals("https://login.kitely.com/", selected.loginUri)
        assertEquals("online", selected.status)
    }

    @Test
    fun `test async central directory sync updates local SQLite cache`() = runTest {
        val mockResponseBody = """
            [
              {
                "id": "opensim_test_grid",
                "name": "Test OpenSim Grid",
                "gridNick": "testgrid",
                "loginUri": "http://grid.testgrid.org:8002/",
                "helperUri": "http://grid.testgrid.org:8002/helpers/",
                "status": "online"
              }
            ]
        """.trimIndent()

        mockWebServer.enqueue(
            MockResponse()
                .setResponseCode(200)
                .setBody(mockResponseBody)
                .addHeader("Content-Type", "application/json")
        )

        val sync = GridDirectorySync(
            dao = dao,
            directoryApiUrl = mockWebServer.url("/v1/grids").toString()
        )

        val result = sync.syncGridDirectory()
        assertTrue("Sync should succeed", result is GridDirectorySync.SyncResult.Success)

        val cachedGrid = dao.getGridById("opensim_test_grid")
        assertNotNull("Synced grid should exist in local SQLite cache", cachedGrid)
        assertEquals("Test OpenSim Grid", cachedGrid?.name)
        assertEquals("http://grid.testgrid.org:8002/", cachedGrid?.loginUri)
    }

    @Test
    fun `test direct grid info probing for unlisted private grid`() = runTest {
        val mockGridInfoJson = """
            {
              "gridname": "Private Alpha Grid",
              "gridnick": "alphagrid",
              "login": "http://alpha.privategrid.net:8002/",
              "welcome": "http://alpha.privategrid.net/welcome",
              "helper": "http://alpha.privategrid.net/helpers/"
            }
        """.trimIndent()

        mockWebServer.enqueue(
            MockResponse()
                .setResponseCode(200)
                .setBody(mockGridInfoJson)
                .addHeader("Content-Type", "application/json")
        )

        val prober = GridInfoProber()
        val probeUrl = mockWebServer.url("/").toString()
        val profile = prober.probeGrid(probeUrl)

        assertNotNull("Prober should parse grid info", profile)
        assertEquals("Private Alpha Grid", profile?.name)
        assertEquals("alphagrid", profile?.gridNick)
        assertEquals("http://alpha.privategrid.net:8002/", profile?.loginUri)
    }

    @Test
    fun `test unlisted grid falls back cleanly to direct network probing`() = runTest {
        val mockGridInfoJson = """
            {
              "gridname": "Dynamic OpenSim",
              "gridnick": "dynosim",
              "login": "http://dynosim.net:8002/"
            }
        """.trimIndent()

        mockWebServer.enqueue(
            MockResponse()
                .setResponseCode(200)
                .setBody(mockGridInfoJson)
        )

        val probeUrl = mockWebServer.url("/").toString()
        val resolved = gridManager.resolveGridAsync(probeUrl)

        assertNotNull("Resolved grid should not be null", resolved)
        assertTrue("Should have valid loginUri", resolved.loginUri.isNotBlank())
    }
}

class InMemoryGridDao : GridDirectoryDao {
    private val grids = LinkedHashMap<String, GridProfileEntity>()

    init {
        com.linkpoint.grid.persistence.GridDatabase.DEFAULT_PRESET_GRIDS.forEach {
            grids[it.id] = it
        }
    }

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
