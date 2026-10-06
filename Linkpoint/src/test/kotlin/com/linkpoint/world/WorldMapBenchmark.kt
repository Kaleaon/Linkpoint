package com.linkpoint.world

import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config
import java.util.UUID
import kotlin.math.sqrt
import kotlin.random.Random

@RunWith(RobolectricTestRunner::class)
@Config(sdk = [34])
class WorldMapBenchmark {
    // Stubs
    private data class StubVector3(val x: Float, val y: Float, val z: Float) {
        fun distance(other: StubVector3): Float {
            val dx = x - other.x
            val dy = y - other.y
            val dz = z - other.z
            return sqrt(dx * dx + dy * dy + dz * dz)
        }
    }

    private data class StubAvatar(
        val agentId: UUID,
        val position: StubVector3,
        val displayName: String = "User $agentId",
    )

    private data class StubNearbyUser(
        val agentId: UUID,
        val name: String,
        val distance: Float,
        val isFriend: Boolean,
        val position: FloatArray,
    )


    fun main(args: Array<String>) {
        val avatarCount = 100_000
        val allAvatars = ArrayList<StubAvatar>(avatarCount)
        val rand = Random(12_345)

        // Generate avatars in a 1000x1000x1000 box
        repeat(avatarCount) {
            allAvatars.add(
                StubAvatar(
                    agentId = UUID.randomUUID(),
                    position = StubVector3(
                        rand.nextFloat() * 1000,
                        rand.nextFloat() * 1000,
                        rand.nextFloat() * 1000,
                    ),
                ),
            )
        }

        val myPosition = StubVector3(500f, 500f, 500f)
        val maxDistance = 100f // 100 meters
        val maxResults = 100

        // Warmup
        println("Warming up...")
        repeat(5) {
            runOld(allAvatars, myPosition, maxDistance, maxResults)
            runNew(allAvatars, myPosition, maxDistance, maxResults)
        }

        // Benchmark Old
        println("Running Old Implementation (Map -> Filter)...")
        val startOld = System.nanoTime()
        repeat(20) {
            runOld(allAvatars, myPosition, maxDistance, maxResults)
        }
        val endOld = System.nanoTime()
        val avgOld = (endOld - startOld) / 20.0 / 1_000_000.0
        println("Old Avg Time: $avgOld ms")

        // Benchmark New
        println("Running New Implementation (Filter -> Map)...")
        val startNew = System.nanoTime()
        repeat(20) {
            runNew(allAvatars, myPosition, maxDistance, maxResults)
        }
        val endNew = System.nanoTime()
        val avgNew = (endNew - startNew) / 20.0 / 1_000_000.0
        println("New Avg Time: $avgNew ms")

        val improvement = avgOld - avgNew
        val percent = improvement / avgOld * 100
        println("Improvement: $improvement ms (${String.format("%.2f", percent)}%)")
    }

    // Simulates Kotlin: allAvatars.map { ... }.filter { ... }
    // Creates full intermediate list of NearbyUsers
    private fun runOld(
        avatars: List<StubAvatar>,
        myPos: StubVector3,
        maxDist: Float,
        maxResults: Int,
    ): List<StubNearbyUser> {
        val mapped = ArrayList<StubNearbyUser>(avatars.size)
        for (avatar in avatars) {
            val distance = avatar.position.distance(myPos)
            // Simulate cost of other fields
            val isFriend = false
            val name = avatar.displayName
            val posArray = floatArrayOf(avatar.position.x, avatar.position.y, avatar.position.z)

            mapped.add(
                StubNearbyUser(
                    agentId = avatar.agentId,
                    name = name,
                    distance = distance,
                    isFriend = isFriend,
                    position = posArray,
                ),
            )
        }

        val filtered = ArrayList<StubNearbyUser>()
        for (user in mapped) {
            if (user.distance <= maxDist) {
                filtered.add(user)
            }
        }

        filtered.sortBy { it.distance }
        return if (filtered.size > maxResults) {
            filtered.subList(0, maxResults)
        } else {
            filtered
        }
    }

    // Simulates Kotlin: allAvatars.filter { ... }.map { ... }
    // Creates intermediate list of Avatars (survivors), then maps to NearbyUsers
    private fun runNew(
        avatars: List<StubAvatar>,
        myPos: StubVector3,
        maxDist: Float,
        maxResults: Int,
    ): List<StubNearbyUser> {
        val filteredAvatars = ArrayList<StubAvatar>()
        for (avatar in avatars) {
            if (avatar.position.distance(myPos) <= maxDist) {
                filteredAvatars.add(avatar)
            }
        }

        val mapped = ArrayList<StubNearbyUser>(filteredAvatars.size)
        for (avatar in filteredAvatars) {
            val distance = avatar.position.distance(myPos) // Recalculate distance
            val isFriend = false
            val name = avatar.displayName
            val posArray = floatArrayOf(avatar.position.x, avatar.position.y, avatar.position.z)

            mapped.add(
                StubNearbyUser(
                    agentId = avatar.agentId,
                    name = name,
                    distance = distance,
                    isFriend = isFriend,
                    position = posArray,
                ),
            )
        }

        mapped.sortBy { it.distance }
        return if (mapped.size > maxResults) {
            mapped.subList(0, maxResults)
        } else {
            mapped
        }
    }

    @org.junit.Test
    fun benchmarkWorldMapFilter() {
        val avatarCount = 1000
        val allAvatars = ArrayList<StubAvatar>(avatarCount)
        val rand = Random(12345)
        repeat(avatarCount) {
            allAvatars.add(StubAvatar(UUID.randomUUID(), StubVector3(rand.nextFloat() * 1000, rand.nextFloat() * 1000, rand.nextFloat() * 1000)))
        }
        val myPos = StubVector3(500f, 500f, 500f)
        val oldRes = runOld(allAvatars, myPos, 100f, 50)
        val newRes = runNew(allAvatars, myPos, 100f, 50)
        org.junit.Assert.assertEquals(oldRes.size, newRes.size)
    }

    @org.junit.Test
    fun testWorldMapMemoryLruEviction() {
        val capManager = com.linkpoint.protocol.capabilities.CapabilityManager()
        val worldMap = WorldMap(capManager)

        // Verify configuration
        org.junit.Assert.assertEquals(WorldMap.TILE_MEMORY_CACHE_MAX_BYTES, worldMap.getMemoryTileCacheMaxSize())
        org.junit.Assert.assertEquals(0, worldMap.getMemoryTileCacheSize())

        // Insert 120 tile bitmaps into memory LRU cache
        // Each 256x256 ARGB_8888 bitmap = 256 * 256 * 4 = 262,144 bytes (256 KB)
        // 120 * 256 KB = 31,457,280 bytes (> 24 MB max capacity of 25,165,824 bytes)
        val bitmapCount = 120
        for (i in 0 until bitmapCount) {
            val bmp = android.graphics.Bitmap.createBitmap(256, 256, android.graphics.Bitmap.Config.ARGB_8888)
            worldMap.putMemoryCachedTile(1000 + i, 1000, bmp)
        }

        // The first inserted tile (1000, 1000) should have been evicted when limit was reached
        org.junit.Assert.assertNull(
            "Oldest tile (1000, 1000) should be evicted from LRU memory cache",
            worldMap.getMemoryCachedTile(1000, 1000)
        )

        // The latest inserted tile (1000 + bitmapCount - 1, 1000) should still be in cache
        org.junit.Assert.assertNotNull(
            "Newest tile should remain in memory cache",
            worldMap.getMemoryCachedTile(1000 + bitmapCount - 1, 1000)
        )

        // Verify size limit is respected
        org.junit.Assert.assertTrue(
            "Memory LRU size (${worldMap.getMemoryTileCacheSize()}) must not exceed max size (${worldMap.getMemoryTileCacheMaxSize()})",
            worldMap.getMemoryTileCacheSize() <= worldMap.getMemoryTileCacheMaxSize()
        )
    }

    @org.junit.Test
    fun testWorldMapDiskCacheHits() = kotlinx.coroutines.runBlocking {
        val tempDir = java.nio.file.Files.createTempDirectory("tile_cache_test").toFile()
        try {
            val capManager = com.linkpoint.protocol.capabilities.CapabilityManager()
            val worldMap = WorldMap(capManager, tileCacheDir = tempDir)

            // Create a valid JPEG byte array and save it to the disk cache directory
            val bitmap = android.graphics.Bitmap.createBitmap(256, 256, android.graphics.Bitmap.Config.ARGB_8888)
            val bos = java.io.ByteArrayOutputStream()
            bitmap.compress(android.graphics.Bitmap.CompressFormat.JPEG, 90, bos)
            val jpegBytes = bos.toByteArray()

            val key = "3-${worldMap.activeManifoldFrameId}-1000-1000"
            val diskFile = java.io.File(tempDir, "tile_$key.jpg")
            diskFile.writeBytes(jpegBytes)

            // Request tile - should hit disk cache without network requests
            val loadedTile = worldMap.getMapTile(1000, 1000, WorldMap.ZOOM_REGION)

            org.junit.Assert.assertNotNull("Tile should be loaded from disk cache", loadedTile)
            org.junit.Assert.assertNotNull(
                "Tile should be promoted to memory LRU cache",
                worldMap.getMemoryCachedTile(1000, 1000, WorldMap.ZOOM_REGION)
            )
        } finally {
            tempDir.deleteRecursively()
        }
    }

    @org.junit.Test
    fun testViewportGridTileFilteringAndEviction() {
        // Simulate local tile map in MapActivity
        val zoomLevel = 3
        val tileSize = 128f
        val width = 512
        val height = 512

        // Tiles per viewport: (512/128)+2 = 6 in X, 6 in Y => 36 tiles
        var centerX = 1000
        var centerY = 1000

        fun calculateVisibleKeys(cx: Int, cy: Int): Set<String> {
            val tilesX = (width / tileSize).toInt() + 2
            val tilesY = (height / tileSize).toInt() + 2
            val startX = cx - tilesX / 2
            val startY = cy - tilesY / 2
            val keys = HashSet<String>()
            for (y in 0 until tilesY) {
                for (x in 0 until tilesX) {
                    keys.add("$zoomLevel-${startX + x}-${startY + y}")
                }
            }
            return keys
        }

        val localMap = mutableMapOf<String, android.graphics.Bitmap>()
        val dummyBitmap = android.graphics.Bitmap.createBitmap(16, 16, android.graphics.Bitmap.Config.ARGB_8888)

        // Initial viewport load
        val initialKeys = calculateVisibleKeys(centerX, centerY)
        for (k in initialKeys) {
            localMap[k] = dummyBitmap
        }

        org.junit.Assert.assertEquals(36, localMap.size)

        // Simulate panning far away
        centerX = 2000
        centerY = 2000
        val pannedKeys = calculateVisibleKeys(centerX, centerY)

        // Purge out of bounds keys
        localMap.keys.retainAll(pannedKeys)

        // Verify that initial keys were evicted
        for (k in initialKeys) {
            org.junit.Assert.assertFalse("Initial key $k should be evicted after panning far away", localMap.containsKey(k))
        }

        // Populate new panned viewport tiles
        for (k in pannedKeys) {
            localMap[k] = dummyBitmap
        }

        org.junit.Assert.assertEquals(36, localMap.size)
        org.junit.Assert.assertTrue(localMap.keys.containsAll(pannedKeys))
    }

}
