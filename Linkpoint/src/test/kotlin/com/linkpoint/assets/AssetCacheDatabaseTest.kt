package com.linkpoint.assets

import androidx.test.core.app.ApplicationProvider
import org.junit.After
import org.junit.Assert.*
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config
import java.io.File

@RunWith(RobolectricTestRunner::class)
@Config(sdk = [34])
class AssetCacheDatabaseTest {

    private lateinit var db: AssetCacheDatabase

    @Before
    fun setUp() {
        val context = ApplicationProvider.getApplicationContext<android.content.Context>()
        db = AssetCacheDatabase(context, "test_asset_cache_${System.currentTimeMillis()}.db")
    }

    @After
    fun tearDown() {
        db.deleteAll()
        db.close()
    }

    @Test
    fun testStoreAndRetrieveAsset() {
        val key = "tex_12345"
        val data = "sample_texture_binary_data".toByteArray()
        val success = db.storeAsset(key, CacheableAssetType.TEXTURES.name, data)
        assertTrue("Asset should store successfully", success)

        assertTrue("hasAsset should return true", db.hasAsset(key))

        val retrieved = db.getAssetData(key)
        assertNotNull("Retrieved data should not be null", retrieved)
        assertArrayEquals("Retrieved data should match stored data", data, retrieved)
    }

    @Test
    fun testSqlAggregationQueryForSizeAndCount() {
        val tex1 = "tex_001".toByteArray()
        val tex2 = "tex_002_larger_data".toByteArray()
        val mesh1 = "mesh_001".toByteArray()

        db.storeAsset("tex1", CacheableAssetType.TEXTURES.name, tex1)
        db.storeAsset("tex2", CacheableAssetType.TEXTURES.name, tex2)
        db.storeAsset("mesh1", CacheableAssetType.MESHES.name, mesh1)

        val expectedTexSize = (tex1.size + tex2.size).toLong()
        val actualTexSize = db.getCategorySizeBytes(CacheableAssetType.TEXTURES.name)
        assertEquals("SQL aggregation SUM(length(data)) for TEXTURES should match", expectedTexSize, actualTexSize)

        val texCount = db.getCategoryCount(CacheableAssetType.TEXTURES.name)
        assertEquals("SQL aggregation COUNT(*) for TEXTURES should match", 2, texCount)

        val meshCount = db.getCategoryCount(CacheableAssetType.MESHES.name)
        assertEquals("SQL aggregation COUNT(*) for MESHES should match", 1, meshCount)

        val totalSize = db.getTotalSizeBytes()
        assertEquals("SQL aggregation total size should equal sum of all assets", expectedTexSize + mesh1.size, totalSize)
    }

    @Test
    fun testPruneToTargetSize() {
        val data1 = ByteArray(1000) { 1 }
        val data2 = ByteArray(1000) { 2 }
        val data3 = ByteArray(1000) { 3 }

        db.storeAsset("asset1", "TEST", data1)
        Thread.sleep(10)
        db.storeAsset("asset2", "TEST", data2)
        Thread.sleep(10)
        db.storeAsset("asset3", "TEST", data3)

        assertEquals(3000L, db.getTotalSizeBytes())

        val (prunedBytes, prunedFiles) = db.pruneToTargetSize(1500L)
        assertTrue("Pruned bytes should be > 0", prunedBytes > 0)
        assertTrue("Pruned files should be > 0", prunedFiles > 0)
        assertTrue("Total size after pruning should be <= 1500", db.getTotalSizeBytes() <= 1500L)
    }

    @Test
    fun testLegacyDirectoryMigrationToSqlite() {
        val tempDir = File(ApplicationProvider.getApplicationContext<android.content.Context>().cacheDir, "migration_test_dir")
        tempDir.mkdirs()

        val file1 = File(tempDir, "legacy_asset_1.dat")
        file1.writeBytes("legacy_content_1".toByteArray())
        val file2 = File(tempDir, "legacy_asset_2.dat")
        file2.writeBytes("legacy_content_2".toByteArray())

        val migratedCount = db.migrateFromDirectory(tempDir, "LEGACY")
        assertEquals("2 files should be migrated into SQLite WAL database", 2, migratedCount)

        assertFalse("Legacy file1 should be deleted after migration", file1.exists())
        assertFalse("Legacy file2 should be deleted after migration", file2.exists())

        val migratedData = db.getAssetData("legacy_asset_1.dat")
        assertNotNull("Migrated data should exist in SQLite database", migratedData)
        assertEquals("legacy_content_1", String(migratedData!!))
    }
}
