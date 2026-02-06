package com.linkpoint.world

import android.util.Log
import com.linkpoint.protocol.capabilities.CapabilityManager
import com.linkpoint.protocol.llsd.*
import com.linkpoint.util.await
import kotlinx.coroutines.*
import okhttp3.Call
import okhttp3.OkHttpClient
import okhttp3.Request
import org.json.JSONArray
import org.json.JSONObject
import java.util.UUID
import java.util.concurrent.TimeUnit

/**
 * Search manager for people, places, groups, and events
 */
class SearchManager(
    private val capabilityManager: CapabilityManager,
    private val httpCallFactory: Call.Factory = OkHttpClient.Builder()
        .connectTimeout(30, TimeUnit.SECONDS)
        .build()
) {
    companion object {
        private const val TAG = "SearchManager"
        
        // Search categories
        const val CATEGORY_PEOPLE = "people"
        const val CATEGORY_PLACES = "places"
        const val CATEGORY_GROUPS = "groups"
        const val CATEGORY_EVENTS = "events"
        const val CATEGORY_CLASSIFIEDS = "classifieds"
        const val CATEGORY_LAND = "land"
        const val CATEGORY_DESTINATIONS = "destinations"
    }
    
    private val scope = CoroutineScope(Dispatchers.IO + SupervisorJob())
    
    /**
     * Search for avatars by name
     */
    suspend fun searchPeople(query: String, start: Int = 0, count: Int = 100): SearchResults<PersonResult> {
        return withContext(Dispatchers.IO) {
            try {
                // Use avatar picker capability
                val request = LLSDMap().apply {
                    this["query"] = LLSDString(query)
                    this["start"] = LLSDInteger(start)
                    this["count"] = LLSDInteger(count)
                }
                
                val response = capabilityManager.request(
                    CapabilityManager.CAP_AVATAR_PICKER,
                    request
                )
                
                if (response is LLSDMap) {
                    val results = mutableListOf<PersonResult>()
                    val agents = response.getArray("agents")
                    
                    agents?.value?.forEach { agent ->
                        if (agent is LLSDMap) {
                            results.add(PersonResult(
                                agentId = UUID.fromString(agent.getString("id") ?: return@forEach),
                                displayName = agent.getString("display_name") ?: "",
                                userName = agent.getString("username") ?: agent.getString("legacy_name") ?: "",
                                isOnline = agent.getInt("online") == 1
                            ))
                        }
                    }
                    
                    SearchResults(results, results.size, start)
                } else {
                    SearchResults(emptyList(), 0, 0)
                }
            } catch (e: Exception) {
                Log.e(TAG, "People search failed", e)
                SearchResults(emptyList(), 0, 0)
            }
        }
    }
    
    /**
     * Search for places
     */
    suspend fun searchPlaces(
        query: String,
        category: String = "",
        start: Int = 0,
        count: Int = 100
    ): SearchResults<PlaceResult> {
        return withContext(Dispatchers.IO) {
            try {
                // Web search API
                val url = buildSearchUrl(CATEGORY_PLACES, query, category, start, count)
                val request = Request.Builder().url(url).build()
                val response = httpCallFactory.newCall(request).await()
                
                if (!response.isSuccessful) return@withContext SearchResults(emptyList(), 0, 0)
                
                val body = response.body?.string() ?: return@withContext SearchResults(emptyList(), 0, 0)
                parsePlaceResults(body, start)
            } catch (e: Exception) {
                Log.e(TAG, "Places search failed", e)
                SearchResults(emptyList(), 0, 0)
            }
        }
    }
    
    /**
     * Search for groups
     */
    suspend fun searchGroups(
        query: String,
        start: Int = 0,
        count: Int = 100
    ): SearchResults<GroupResult> {
        return withContext(Dispatchers.IO) {
            try {
                val url = buildSearchUrl(CATEGORY_GROUPS, query, "", start, count)
                val request = Request.Builder().url(url).build()
                val response = httpCallFactory.newCall(request).await()
                
                if (!response.isSuccessful) return@withContext SearchResults(emptyList(), 0, 0)
                
                val body = response.body?.string() ?: return@withContext SearchResults(emptyList(), 0, 0)
                parseGroupResults(body, start)
            } catch (e: Exception) {
                Log.e(TAG, "Groups search failed", e)
                SearchResults(emptyList(), 0, 0)
            }
        }
    }
    
    /**
     * Search for events
     */
    suspend fun searchEvents(
        query: String,
        category: String = "",
        start: Int = 0,
        count: Int = 100
    ): SearchResults<EventResult> {
        return withContext(Dispatchers.IO) {
            try {
                val url = buildSearchUrl(CATEGORY_EVENTS, query, category, start, count)
                val request = Request.Builder().url(url).build()
                val response = httpCallFactory.newCall(request).await()
                
                if (!response.isSuccessful) return@withContext SearchResults(emptyList(), 0, 0)
                
                val body = response.body?.string() ?: return@withContext SearchResults(emptyList(), 0, 0)
                parseEventResults(body, start)
            } catch (e: Exception) {
                Log.e(TAG, "Events search failed", e)
                SearchResults(emptyList(), 0, 0)
            }
        }
    }
    
    /**
     * Search for land/parcels
     */
    suspend fun searchLand(
        query: String,
        category: String = "",
        saleType: LandSaleType = LandSaleType.ALL,
        minPrice: Int = 0,
        maxPrice: Int = Int.MAX_VALUE,
        minArea: Int = 0,
        maxArea: Int = Int.MAX_VALUE,
        start: Int = 0,
        count: Int = 100
    ): SearchResults<LandResult> {
        return withContext(Dispatchers.IO) {
            try {
                // Use land search capability
                val request = LLSDMap().apply {
                    this["query"] = LLSDString(query)
                    if (category.isNotEmpty()) {
                        this["category"] = LLSDString(category)
                    }
                    this["sale_type"] = LLSDInteger(when (saleType) {
                        LandSaleType.ALL -> 0
                        LandSaleType.FOR_AUCTION -> 1
                        LandSaleType.FOR_SALE -> 2
                        LandSaleType.MAINLAND -> 3
                        LandSaleType.ESTATE -> 4
                    })
                    this["min_price"] = LLSDInteger(minPrice)
                    this["max_price"] = LLSDInteger(if (maxPrice == Int.MAX_VALUE) -1 else maxPrice)
                    this["min_area"] = LLSDInteger(minArea)
                    this["max_area"] = LLSDInteger(if (maxArea == Int.MAX_VALUE) -1 else maxArea)
                    this["start"] = LLSDInteger(start)
                    this["count"] = LLSDInteger(count)
                }
                
                val response = capabilityManager.request("SearchLand", request)
                if (response != null) {
                    parseLandResults(response)
                } else {
                    Log.w(TAG, "Land search returned no response")
                    SearchResults(emptyList(), 0, 0)
                }
            } catch (e: Exception) {
                Log.e(TAG, "Land search failed", e)
                SearchResults(emptyList(), 0, 0)
            }
        }
    }
    
    private fun parseLandResults(response: Any): SearchResults<LandResult> {
        // Parse LLSD response into land results
        val results = mutableListOf<LandResult>()
        var totalCount = 0
        
        try {
            if (response is LLSDMap) {
                totalCount = (response["total_count"] as? LLSDInteger)?.value ?: 0
                val resultsArray = response["results"] as? LLSDArray
                if (resultsArray != null) {
                    val arraySize = resultsArray.size
                    for (i in 0 until arraySize) {
                        val item = resultsArray[i] as? LLSDMap ?: continue
                        val area = (item["area"] as? LLSDInteger)?.value ?: 0
                        val salePrice = (item["price"] as? LLSDInteger)?.value ?: 0
                        results.add(LandResult(
                            parcelId = try {
                                UUID.fromString((item["parcel_id"] as? LLSDString)?.value ?: "")
                            } catch (e: Exception) { UUID.randomUUID() },
                            name = (item["name"] as? LLSDString)?.value ?: "",
                            description = (item["description"] as? LLSDString)?.value ?: "",
                            region = (item["region"] as? LLSDString)?.value ?: "",
                            area = area,
                            salePrice = salePrice,
                            pricePerMeter = if (area > 0) salePrice.toFloat() / area else 0f,
                            saleType = when ((item["sale_type"] as? LLSDInteger)?.value) {
                                1 -> LandSaleType.FOR_AUCTION
                                2 -> LandSaleType.FOR_SALE
                                3 -> LandSaleType.MAINLAND
                                4 -> LandSaleType.ESTATE
                                else -> LandSaleType.ALL
                            },
                            isAuction = ((item["sale_type"] as? LLSDInteger)?.value ?: 0) == 1
                        ))
                    }
                }
            }
        } catch (e: Exception) {
            Log.e(TAG, "Failed to parse land results", e)
        }
        
        return SearchResults(results, results.size, totalCount)
    }
    
    /**
     * Get popular destinations
     */
    suspend fun getDestinations(category: String = ""): List<DestinationResult> {
        return withContext(Dispatchers.IO) {
            try {
                val url = if (category.isEmpty()) {
                    "https://destinations.secondlife.com/api/destinations"
                } else {
                    "https://destinations.secondlife.com/api/destinations?category=$category"
                }
                
                val request = Request.Builder().url(url).build()
                val response = httpCallFactory.newCall(request).await()
                
                if (!response.isSuccessful) return@withContext emptyList()
                
                val body = response.body?.string() ?: return@withContext emptyList()
                parseDestinations(body)
            } catch (e: Exception) {
                Log.e(TAG, "Destinations fetch failed", e)
                emptyList()
            }
        }
    }
    
    private fun buildSearchUrl(
        category: String,
        query: String,
        subCategory: String,
        start: Int,
        count: Int
    ): String {
        val baseUrl = "https://search.secondlife.com/client_search"
        val params = mutableListOf(
            "q=${query.encodeUrl()}",
            "type=$category",
            "start=$start",
            "perpage=$count"
        )
        
        if (subCategory.isNotEmpty()) {
            params.add("category=${subCategory.encodeUrl()}")
        }
        
        return "$baseUrl?${params.joinToString("&")}"
    }
    
    private fun parsePlaceResults(json: String, start: Int): SearchResults<PlaceResult> {
        val results = mutableListOf<PlaceResult>()
        try {
            val root = JSONObject(json)
            val places = root.optJSONArray("places") ?: JSONArray()
            
            for (i in 0 until places.length()) {
                val place = places.getJSONObject(i)
                results.add(PlaceResult(
                    parcelId = UUID.fromString(place.optString("parcel_id", UUID.randomUUID().toString())),
                    name = place.optString("name", "Unknown"),
                    description = place.optString("description", ""),
                    region = place.optString("region", ""),
                    category = place.optString("category", ""),
                    traffic = place.optDouble("traffic", 0.0).toFloat(),
                    area = place.optInt("area", 0),
                    location = place.optString("location", "")
                ))
            }
            
            val total = root.optInt("total", results.size)
            return SearchResults(results, total, start)
        } catch (e: Exception) {
            Log.e(TAG, "Failed to parse place results", e)
            return SearchResults(emptyList(), 0, 0)
        }
    }
    
    private fun parseGroupResults(json: String, start: Int): SearchResults<GroupResult> {
        val results = mutableListOf<GroupResult>()
        try {
            val root = JSONObject(json)
            val groups = root.optJSONArray("groups") ?: JSONArray()
            
            for (i in 0 until groups.length()) {
                val group = groups.getJSONObject(i)
                results.add(GroupResult(
                    groupId = UUID.fromString(group.optString("group_id", UUID.randomUUID().toString())),
                    name = group.optString("name", "Unknown"),
                    charter = group.optString("charter", ""),
                    memberCount = group.optInt("member_count", 0),
                    isOpen = group.optBoolean("open_enrollment", false),
                    insigniaId = try { UUID.fromString(group.optString("insignia_id")) } catch (e: Exception) { null }
                ))
            }
            
            val total = root.optInt("total", results.size)
            return SearchResults(results, total, start)
        } catch (e: Exception) {
            Log.e(TAG, "Failed to parse group results", e)
            return SearchResults(emptyList(), 0, 0)
        }
    }
    
    private fun parseEventResults(json: String, start: Int): SearchResults<EventResult> {
        val results = mutableListOf<EventResult>()
        try {
            val root = JSONObject(json)
            val events = root.optJSONArray("events") ?: JSONArray()
            
            for (i in 0 until events.length()) {
                val event = events.getJSONObject(i)
                results.add(EventResult(
                    eventId = event.optInt("event_id", 0),
                    name = event.optString("name", "Unknown"),
                    description = event.optString("description", ""),
                    category = event.optString("category", ""),
                    dateUtc = event.optLong("date_utc", 0),
                    duration = event.optInt("duration", 0),
                    region = event.optString("region", ""),
                    coverCharge = event.optInt("cover", 0)
                ))
            }
            
            val total = root.optInt("total", results.size)
            return SearchResults(results, total, start)
        } catch (e: Exception) {
            Log.e(TAG, "Failed to parse event results", e)
            return SearchResults(emptyList(), 0, 0)
        }
    }
    
    private fun parseDestinations(json: String): List<DestinationResult> {
        val results = mutableListOf<DestinationResult>()
        try {
            val destinations = JSONArray(json)
            
            for (i in 0 until destinations.length()) {
                val dest = destinations.getJSONObject(i)
                results.add(DestinationResult(
                    name = dest.optString("name", "Unknown"),
                    description = dest.optString("description", ""),
                    category = dest.optString("category", ""),
                    region = dest.optString("region", ""),
                    location = dest.optString("location", ""),
                    imageUrl = dest.optString("image_url", ""),
                    rating = dest.optDouble("rating", 0.0).toFloat()
                ))
            }
        } catch (e: Exception) {
            Log.e(TAG, "Failed to parse destinations", e)
        }
        return results
    }
    
    private fun String.encodeUrl(): String {
        return java.net.URLEncoder.encode(this, "UTF-8")
    }
    
    fun shutdown() {
        scope.cancel()
    }
}

data class SearchResults<T>(
    val results: List<T>,
    val totalCount: Int,
    val startIndex: Int
) {
    val hasMore: Boolean get() = startIndex + results.size < totalCount
}

data class PersonResult(
    val agentId: UUID,
    val displayName: String,
    val userName: String,
    val isOnline: Boolean
)

data class PlaceResult(
    val parcelId: UUID,
    val name: String,
    val description: String,
    val region: String,
    val category: String,
    val traffic: Float,
    val area: Int,
    val location: String
)

data class GroupResult(
    val groupId: UUID,
    val name: String,
    val charter: String,
    val memberCount: Int,
    val isOpen: Boolean,
    val insigniaId: UUID?
)

data class EventResult(
    val eventId: Int,
    val name: String,
    val description: String,
    val category: String,
    val dateUtc: Long,
    val duration: Int,
    val region: String,
    val coverCharge: Int
)

data class LandResult(
    val parcelId: UUID,
    val name: String,
    val description: String,
    val region: String,
    val area: Int,
    val salePrice: Int,
    val pricePerMeter: Float,
    val saleType: LandSaleType,
    val isAuction: Boolean
)

data class DestinationResult(
    val name: String,
    val description: String,
    val category: String,
    val region: String,
    val location: String,
    val imageUrl: String,
    val rating: Float
)

enum class LandSaleType {
    ALL, FOR_SALE, FOR_AUCTION, MAINLAND, ESTATE
}
