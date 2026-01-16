package com.linkpoint.core

import android.content.Context
import android.content.SharedPreferences
import android.util.Log
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import org.json.JSONArray
import org.json.JSONObject

/**
 * Manages themed and popular destination locations from Second Life.
 * 
 * Features:
 * - Curated destination categories (Shopping, Entertainment, Education, etc.)
 * - Popular/featured destinations
 * - Caching of destination data
 * - Integration with Second Life Destination Guide API
 * 
 * Based on Second Life mobile app functionality.
 */
class DestinationGuide(private val context: Context) {
    
    companion object {
        private const val TAG = "DestinationGuide"
        private const val PREFS_NAME = "destination_guide_prefs"
        private const val KEY_CACHED_DESTINATIONS = "cached_destinations"
        private const val KEY_CACHED_CATEGORIES = "cached_categories"
        private const val KEY_LAST_UPDATE = "last_update"
        private const val CACHE_DURATION_MS = 24 * 60 * 60 * 1000L  // 24 hours
    }
    
    private val prefs: SharedPreferences = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
    private val scope = CoroutineScope(Dispatchers.IO + SupervisorJob())
    
    // Destination categories
    private val _categories = MutableStateFlow<List<DestinationCategory>>(emptyList())
    val categories: StateFlow<List<DestinationCategory>> = _categories
    
    // Featured destinations
    private val _featuredDestinations = MutableStateFlow<List<DestinationEntry>>(emptyList())
    val featuredDestinations: StateFlow<List<DestinationEntry>> = _featuredDestinations
    
    // All destinations by category
    private val _destinations = MutableStateFlow<Map<String, List<DestinationEntry>>>(emptyMap())
    val destinations: StateFlow<Map<String, List<DestinationEntry>>> = _destinations
    
    // Loading state
    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading
    
    init {
        loadCachedData()
        initializeDefaultDestinations()
    }
    
    /**
     * Initialize with default curated destinations.
     * These are well-known Second Life locations that are always available.
     */
    private fun initializeDefaultDestinations() {
        if (_categories.value.isEmpty()) {
            _categories.value = getDefaultCategories()
        }
        if (_featuredDestinations.value.isEmpty()) {
            _featuredDestinations.value = getDefaultFeaturedDestinations()
        }
        if (_destinations.value.isEmpty()) {
            _destinations.value = getDefaultDestinationsByCategory()
        }
    }
    
    /**
     * Get default destination categories.
     */
    private fun getDefaultCategories(): List<DestinationCategory> = listOf(
        DestinationCategory(
            id = "featured",
            name = "Featured",
            description = "Popular and recommended destinations",
            iconName = "star"
        ),
        DestinationCategory(
            id = "newcomer",
            name = "Newcomer Friendly",
            description = "Great places for new residents",
            iconName = "person_add"
        ),
        DestinationCategory(
            id = "shopping",
            name = "Shopping",
            description = "Marketplaces and stores",
            iconName = "shopping_cart"
        ),
        DestinationCategory(
            id = "entertainment",
            name = "Entertainment",
            description = "Clubs, music venues, and events",
            iconName = "music_note"
        ),
        DestinationCategory(
            id = "roleplay",
            name = "Roleplay",
            description = "Fantasy, sci-fi, and themed communities",
            iconName = "theater_comedy"
        ),
        DestinationCategory(
            id = "nature",
            name = "Parks & Nature",
            description = "Scenic landscapes and relaxation spots",
            iconName = "park"
        ),
        DestinationCategory(
            id = "education",
            name = "Education",
            description = "Museums, libraries, and learning centers",
            iconName = "school"
        ),
        DestinationCategory(
            id = "arts",
            name = "Arts & Culture",
            description = "Galleries, theaters, and creative spaces",
            iconName = "palette"
        ),
        DestinationCategory(
            id = "hangouts",
            name = "Social Hangouts",
            description = "Meet new people and make friends",
            iconName = "groups"
        )
    )
    
    /**
     * Get default featured destinations.
     * These are popular, well-established Second Life locations.
     */
    private fun getDefaultFeaturedDestinations(): List<DestinationEntry> = listOf(
        DestinationEntry(
            id = "london_city",
            name = "London City",
            description = "Explore a detailed recreation of London",
            regionName = "London City",
            x = 128,
            y = 128,
            z = 25,
            category = "featured",
            trafficRating = 5,
            isFeatured = true
        ),
        DestinationEntry(
            id = "the_trace",
            name = "The Trace",
            description = "Beautiful nature and hiking trails",
            regionName = "The Trace",
            x = 128,
            y = 128,
            z = 25,
            category = "nature",
            trafficRating = 4,
            isFeatured = true
        ),
        DestinationEntry(
            id = "linden_realms",
            name = "Linden Realms",
            description = "Official Linden Lab game experience",
            regionName = "Linden Realms Portal Park",
            x = 128,
            y = 128,
            z = 25,
            category = "entertainment",
            trafficRating = 5,
            isFeatured = true
        ),
        DestinationEntry(
            id = "social_island",
            name = "Social Island",
            description = "Perfect for new residents to meet people",
            regionName = "Social Island",
            x = 128,
            y = 128,
            z = 25,
            category = "newcomer",
            trafficRating = 4,
            isFeatured = true
        ),
        DestinationEntry(
            id = "destination_island",
            name = "Destination Island",
            description = "Discover places to visit in Second Life",
            regionName = "Destination Island",
            x = 128,
            y = 128,
            z = 25,
            category = "newcomer",
            trafficRating = 4,
            isFeatured = true
        )
    )
    
    /**
     * Get default destinations organized by category.
     */
    private fun getDefaultDestinationsByCategory(): Map<String, List<DestinationEntry>> {
        val result = mutableMapOf<String, MutableList<DestinationEntry>>()
        
        // Add featured destinations
        result["featured"] = _featuredDestinations.value.toMutableList()
        
        // Newcomer friendly destinations
        result["newcomer"] = mutableListOf(
            DestinationEntry(
                id = "firestorm_gateway",
                name = "Firestorm Gateway",
                description = "Help area for Firestorm viewer users",
                regionName = "Firestorm Gateway",
                x = 128, y = 128, z = 25,
                category = "newcomer",
                trafficRating = 4
            ),
            DestinationEntry(
                id = "helping_haven",
                name = "Helping Haven",
                description = "Get help and learn about Second Life",
                regionName = "Helping Haven",
                x = 128, y = 128, z = 25,
                category = "newcomer",
                trafficRating = 3
            ),
            DestinationEntry(
                id = "caledon_oxbridge",
                name = "Caledon Oxbridge University",
                description = "Classes and tutorials for new residents",
                regionName = "Caledon Oxbridge",
                x = 128, y = 128, z = 25,
                category = "newcomer",
                trafficRating = 4
            )
        )
        
        // Shopping destinations
        result["shopping"] = mutableListOf(
            DestinationEntry(
                id = "fameshed",
                name = "FaMESHed",
                description = "Popular monthly shopping event",
                regionName = "FaMESHed",
                x = 128, y = 128, z = 25,
                category = "shopping",
                trafficRating = 5
            ),
            DestinationEntry(
                id = "the_arcade",
                name = "The Arcade",
                description = "Quarterly gacha and shopping event",
                regionName = "The Arcade",
                x = 128, y = 128, z = 25,
                category = "shopping",
                trafficRating = 5
            ),
            DestinationEntry(
                id = "uber",
                name = "Uber",
                description = "Monthly sale event with top designers",
                regionName = "Uber",
                x = 128, y = 128, z = 25,
                category = "shopping",
                trafficRating = 4
            )
        )
        
        // Entertainment destinations
        result["entertainment"] = mutableListOf(
            DestinationEntry(
                id = "dance_island",
                name = "Dance Island",
                description = "Dance clubs and live music events",
                regionName = "Dance Island",
                x = 128, y = 128, z = 25,
                category = "entertainment",
                trafficRating = 4
            ),
            DestinationEntry(
                id = "franks_place",
                name = "Frank's Place",
                description = "Popular jazz club and hangout",
                regionName = "Franks Place",
                x = 128, y = 128, z = 25,
                category = "entertainment",
                trafficRating = 4
            )
        )
        
        // Parks & Nature
        result["nature"] = mutableListOf(
            DestinationEntry(
                id = "bellisseria",
                name = "Bellisseria",
                description = "Linden Homes community with beautiful landscapes",
                regionName = "Bellisseria",
                x = 128, y = 128, z = 25,
                category = "nature",
                trafficRating = 5
            ),
            DestinationEntry(
                id = "tempura_island",
                name = "Tempura Island",
                description = "Stunning Japanese-themed nature retreat",
                regionName = "Tempura Island",
                x = 128, y = 128, z = 25,
                category = "nature",
                trafficRating = 4
            )
        )
        
        // Arts & Culture
        result["arts"] = mutableListOf(
            DestinationEntry(
                id = "sl_art_museum",
                name = "SL Art Museum",
                description = "Curated virtual art exhibitions",
                regionName = "SL Art Museum",
                x = 128, y = 128, z = 25,
                category = "arts",
                trafficRating = 3
            ),
            DestinationEntry(
                id = "ivory_tower",
                name = "Ivory Tower of Primitives",
                description = "Learn to build in Second Life",
                regionName = "Ivory Tower",
                x = 128, y = 128, z = 25,
                category = "arts",
                trafficRating = 4
            )
        )
        
        // Roleplay
        result["roleplay"] = mutableListOf(
            DestinationEntry(
                id = "new_babbage",
                name = "New Babbage",
                description = "Steampunk and Victorian roleplay community",
                regionName = "New Babbage",
                x = 128, y = 128, z = 25,
                category = "roleplay",
                trafficRating = 4
            ),
            DestinationEntry(
                id = "gor_hub",
                name = "Gor Hub",
                description = "Fantasy roleplay based on Gor novels",
                regionName = "Gor Hub",
                x = 128, y = 128, z = 25,
                category = "roleplay",
                trafficRating = 3
            )
        )
        
        // Education
        result["education"] = mutableListOf(
            DestinationEntry(
                id = "virtual_ability",
                name = "Virtual Ability Island",
                description = "Accessibility-focused community and education",
                regionName = "Virtual Ability",
                x = 128, y = 128, z = 25,
                category = "education",
                trafficRating = 4
            ),
            DestinationEntry(
                id = "info_island",
                name = "Info Island",
                description = "Virtual libraries and information resources",
                regionName = "Info Island",
                x = 128, y = 128, z = 25,
                category = "education",
                trafficRating = 3
            )
        )
        
        // Social Hangouts
        result["hangouts"] = mutableListOf(
            DestinationEntry(
                id = "london_pub",
                name = "The London Pub",
                description = "Casual pub atmosphere to meet people",
                regionName = "London",
                x = 128, y = 128, z = 25,
                category = "hangouts",
                trafficRating = 4
            ),
            DestinationEntry(
                id = "nude_beach",
                name = "Nude Beach",
                description = "Adult social beach hangout",
                regionName = "Nude Beach",
                x = 128, y = 128, z = 25,
                category = "hangouts",
                trafficRating = 4,
                maturityRating = MaturityRating.ADULT
            )
        )
        
        return result
    }
    
    /**
     * Get destinations for a specific category.
     */
    fun getDestinationsForCategory(categoryId: String): List<DestinationEntry> {
        return _destinations.value[categoryId] ?: emptyList()
    }
    
    /**
     * Search destinations by name or description.
     */
    fun searchDestinations(query: String): List<DestinationEntry> {
        if (query.isBlank()) return emptyList()
        
        val lowerQuery = query.lowercase()
        val results = mutableListOf<DestinationEntry>()
        
        _destinations.value.values.flatten().forEach { dest ->
            if (dest.name.lowercase().contains(lowerQuery) ||
                dest.description.lowercase().contains(lowerQuery) ||
                dest.regionName.lowercase().contains(lowerQuery)) {
                results.add(dest)
            }
        }
        
        return results.sortedByDescending { it.trafficRating }
    }
    
    /**
     * Get top destinations across all categories.
     */
    fun getTopDestinations(limit: Int = 10): List<DestinationEntry> {
        return _destinations.value.values.flatten()
            .sortedByDescending { it.trafficRating }
            .take(limit)
    }
    
    /**
     * Get destinations suitable for a specific maturity rating.
     */
    fun getDestinationsForMaturity(maxMaturity: MaturityRating): List<DestinationEntry> {
        return _destinations.value.values.flatten()
            .filter { it.maturityRating.ordinal <= maxMaturity.ordinal }
            .sortedByDescending { it.trafficRating }
    }
    
    /**
     * Convert destination to start location option.
     */
    fun toStartLocationOption(destination: DestinationEntry): StartLocationOption {
        return StartLocationOption(
            type = StartLocationType.DESTINATION,
            displayName = destination.name,
            description = destination.description,
            iconType = StartLocationIconType.LOCATION,
            slurl = destination.toSLURL(),
            categoryId = destination.category
        )
    }
    
    // ==================== PERSISTENCE ====================
    
    private fun loadCachedData() {
        try {
            val lastUpdate = prefs.getLong(KEY_LAST_UPDATE, 0)
            val cacheAge = System.currentTimeMillis() - lastUpdate
            
            if (cacheAge > CACHE_DURATION_MS) {
                Log.d(TAG, "Cache expired, using defaults")
                return
            }
            
            // Load cached categories
            val categoriesJson = prefs.getString(KEY_CACHED_CATEGORIES, null)
            if (categoriesJson != null) {
                val categories = parseCategoriesJson(categoriesJson)
                if (categories.isNotEmpty()) {
                    _categories.value = categories
                }
            }
            
            // Load cached destinations
            val destinationsJson = prefs.getString(KEY_CACHED_DESTINATIONS, null)
            if (destinationsJson != null) {
                val destinations = parseDestinationsJson(destinationsJson)
                if (destinations.isNotEmpty()) {
                    _destinations.value = destinations
                    _featuredDestinations.value = destinations.values.flatten()
                        .filter { it.isFeatured }
                }
            }
            
            Log.i(TAG, "Loaded cached destination data")
        } catch (e: Exception) {
            Log.w(TAG, "Failed to load cached data", e)
        }
    }
    
    private fun parseCategoriesJson(json: String): List<DestinationCategory> {
        val result = mutableListOf<DestinationCategory>()
        try {
            val array = JSONArray(json)
            for (i in 0 until array.length()) {
                val obj = array.getJSONObject(i)
                result.add(DestinationCategory(
                    id = obj.getString("id"),
                    name = obj.getString("name"),
                    description = obj.optString("description", ""),
                    iconName = obj.optString("iconName", "place")
                ))
            }
        } catch (e: Exception) {
            Log.w(TAG, "Failed to parse categories JSON", e)
        }
        return result
    }
    
    private fun parseDestinationsJson(json: String): Map<String, List<DestinationEntry>> {
        val result = mutableMapOf<String, MutableList<DestinationEntry>>()
        try {
            val obj = JSONObject(json)
            val keys = obj.keys()
            while (keys.hasNext()) {
                val category = keys.next()
                val array = obj.getJSONArray(category)
                val destinations = mutableListOf<DestinationEntry>()
                
                for (i in 0 until array.length()) {
                    val destObj = array.getJSONObject(i)
                    destinations.add(DestinationEntry(
                        id = destObj.getString("id"),
                        name = destObj.getString("name"),
                        description = destObj.optString("description", ""),
                        regionName = destObj.getString("regionName"),
                        x = destObj.optInt("x", 128),
                        y = destObj.optInt("y", 128),
                        z = destObj.optInt("z", 25),
                        category = destObj.optString("category", category),
                        trafficRating = destObj.optInt("trafficRating", 0),
                        isFeatured = destObj.optBoolean("isFeatured", false),
                        imageUrl = destObj.optString("imageUrl", null),
                        maturityRating = MaturityRating.valueOf(
                            destObj.optString("maturityRating", "GENERAL")
                        )
                    ))
                }
                
                result[category] = destinations
            }
        } catch (e: Exception) {
            Log.w(TAG, "Failed to parse destinations JSON", e)
        }
        return result
    }
    
    fun shutdown() {
        scope.cancel()
    }
}

// ==================== DATA CLASSES ====================

data class DestinationCategory(
    val id: String,
    val name: String,
    val description: String,
    val iconName: String
)

data class DestinationEntry(
    val id: String,
    val name: String,
    val description: String,
    val regionName: String,
    val x: Int,
    val y: Int,
    val z: Int,
    val category: String,
    val trafficRating: Int = 0,
    val isFeatured: Boolean = false,
    val imageUrl: String? = null,
    val maturityRating: MaturityRating = MaturityRating.GENERAL
) {
    fun toSLURL(): String = "secondlife://$regionName/$x/$y/$z"
}

enum class MaturityRating {
    GENERAL,    // PG content
    MODERATE,   // Mature content
    ADULT       // Adult content
}
