package com.linkpoint.assets

import android.content.Context
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import kotlinx.coroutines.runBlocking
import org.junit.Assert.assertArrayEquals
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import java.io.File
import java.util.UUID

@RunWith(AndroidJUnit4::class)
class AssetCacheTest {

    private lateinit var context: Context
    private lateinit var cacheManager: CacheManager
    private lateinit var assetCache: AssetCache

    @Before
    fun setUp() {
        context = ApplicationProvider.getApplicationContext()
        cacheManager = CacheManager(context)
        assetCache = AssetCache(context, cacheManager)
    }

    @Test
    fun `getGeneralAssetDirectory returns grid-segregated directory`() {
        cacheManager.setCurrentGrid("GridOne")
        val dirOne = cacheManager.getGeneralAssetDirectory()
        assertTrue(dirOne.absolutePath.contains("Public${File.separator}GridOne${File.separator}asset_cache"))
        assertTrue(dirOne.exists())

        cacheManager.setCurrentGrid("GridTwo")
        val dirTwo = cacheManager.getGeneralAssetDirectory()
        assertTrue(dirTwo.absolutePath.contains("Public${File.separator}GridTwo${File.separator}asset_cache"))
        assertTrue(dirTwo.exists())
    }

    @Test
    fun `fallback asset disk cache routes through CacheManager general directory`() = runBlocking {
        cacheManager.setCurrentGrid("AgniGrid")
        val assetId = UUID.randomUUID()
        val clothingData = "clothing_bytes_123".toByteArray()

        assetCache.put(assetId, AssetType.CLOTHING, clothingData)

        val generalDir = cacheManager.getGeneralAssetDirectory()
        val savedFile = File(generalDir, assetId.toString())
        assertTrue("Fallback asset must be saved in general asset directory", savedFile.exists())
        assertTrue("Path must contain current grid name", savedFile.absolutePath.contains("AgniGrid"))
        assertArrayEquals(clothingData, savedFile.readBytes())
    }

    @Test
    fun `grid switching isolates fallback assets between grids`() = runBlocking {
        val sharedUuid = UUID.randomUUID()
        val dataGridA = "grid_A_gesture".toByteArray()
        val dataGridB = "grid_B_gesture".toByteArray()

        // Store asset on GridA
        cacheManager.setCurrentGrid("GridA")
        assetCache.put(sharedUuid, AssetType.GESTURE, dataGridA)

        val retrievedA = assetCache.get(sharedUuid, AssetType.GESTURE)
        assertNotNull(retrievedA)
        assertArrayEquals(dataGridA, retrievedA)

        // Switch to GridB
        cacheManager.setCurrentGrid("GridB")
        val retrievedOnGridBBeforePut = assetCache.get(sharedUuid, AssetType.GESTURE)
        assertNull("Asset from GridA should not be visible on GridB", retrievedOnGridBBeforePut)

        // Store different asset data with same UUID on GridB
        assetCache.put(sharedUuid, AssetType.GESTURE, dataGridB)
        val retrievedB = assetCache.get(sharedUuid, AssetType.GESTURE)
        assertNotNull(retrievedB)
        assertArrayEquals(dataGridB, retrievedB)

        // Switch back to GridA
        cacheManager.setCurrentGrid("GridA")
        val retrievedAAfter = assetCache.get(sharedUuid, AssetType.GESTURE)
        assertNotNull(retrievedAAfter)
        assertArrayEquals(dataGridA, retrievedAAfter)
    }

    @Test
    fun `clear removes fallback assets and legacy unsegregated cache files`() = runBlocking {
        cacheManager.setCurrentGrid("AgniGrid")
        val gestureId = UUID.randomUUID()
        assetCache.put(gestureId, AssetType.GESTURE, "gesture_data".toByteArray())

        // Create legacy unsegregated cache file
        val legacyDir = File(context.cacheDir, "asset_cache")
        legacyDir.mkdirs()
        val legacyFile = File(legacyDir, "legacy_asset.bin")
        legacyFile.writeText("legacy_content")
        assertTrue("Legacy file should exist prior to clear", legacyFile.exists())

        // Perform clear
        assetCache.clear()

        val generalDir = cacheManager.getGeneralAssetDirectory()
        val savedGestureFile = File(generalDir, gestureId.toString())
        assertFalse("General asset file should be removed after clear", savedGestureFile.exists())
        assertFalse("Legacy unsegregated file should be removed after clear", legacyFile.exists())
    }
}
