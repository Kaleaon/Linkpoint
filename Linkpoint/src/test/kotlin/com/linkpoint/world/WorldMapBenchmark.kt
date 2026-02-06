package com.linkpoint.world

import java.util.UUID
import kotlin.math.sqrt
import kotlin.random.Random

object WorldMapBenchmark {
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

    @JvmStatic
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
}
