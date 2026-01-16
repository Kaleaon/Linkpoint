package com.linkpoint.core

import android.content.Context
import android.content.SharedPreferences
import android.util.Log
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import org.json.JSONArray
import org.json.JSONObject

/**
 * Manages start location options for login.
 * 
 * Features:
 * - Cached landmarks from user's inventory (loaded after first successful login)
 * - Saved start location preferences
 * - Custom SLURL support
 * - Integration with themed destinations from DestinationGuide
 * 
 * Based on Second Life mobile app functionality.
 */
class StartLocationManager(private val context: Context) {
    
    companion object {
        private const val TAG = "StartLocationManager"
        private const val PREFS_NAME = "start_location_prefs"
        private const val KEY_LAST_START_OPTION = "last_start_option"
        private const val KEY_CUSTOM_SLURL = "custom_slurl"
        private const val KEY_CACHED_LANDMARKS = "cached_landmarks"
        private const val KEY_FIRST_LOGIN_COMPLETE = "first_login_complete"
        private const val KEY_FAVORITE_LOCATIONS = "favorite_locations"
        private const val MAX_CACHED_LANDMARKS = 50
        private const val MAX_FAVORITE_LOCATIONS = 20
    }
    
    private val prefs: SharedPreferences = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
    
    // Start location options
    private val _startOptions = MutableStateFlow<List<StartLocationOption>>(emptyList())
    val startOptions: StateFlow<List<StartLocationOption>> = _startOptions
    
    // Cached landmarks from inventory
    private val _cachedLandmarks = MutableStateFlow<List<CachedLandmark>>(emptyList())
    val cachedLandmarks: StateFlow<List<CachedLandmark>> = _cachedLandmarks
    
    // Favorite locations (user-saved quick access locations)
    private val _favoriteLocations = MutableStateFlow<List<SavedLocation>>(emptyList())
    val favoriteLocations: StateFlow<List<SavedLocation>> = _favoriteLocations
    
    // Current selection
    private val _selectedOption = MutableStateFlow(StartLocationType.LAST_LOCATION)
    val selectedOption: StateFlow<StartLocationType> = _selectedOption
    
    // Custom SLURL if selected
    private val _customSLURL = MutableStateFlow<String?>(null)
    val customSLURL: StateFlow<String?> = _customSLURL
    
    // Selected landmark if type is LANDMARK
    private val _selectedLandmark = MutableStateFlow<CachedLandmark?>(null)
    val selectedLandmark: StateFlow<CachedLandmark?> = _selectedLandmark
    
    // Selected destination if type is DESTINATION
    private val _selectedDestination = MutableStateFlow<Destination?>(null)
    val selectedDestination: StateFlow<Destination?> = _selectedDestination
    
    init {
        loadSavedPreferences()
        loadCachedLandmarks()
        loadFavoriteLocations()
        refreshStartOptions()
    }
    
    /**
     * Get all available start location options.
     */
    fun getAvailableOptions(): List<StartLocationOption> {
        val options = mutableListOf<StartLocationOption>()
        
        // Basic options - always available
        options.add(StartLocationOption(
            type = StartLocationType.LAST_LOCATION,
            displayName = "Last Location",
            description = "Return to where you logged off",
            iconType = StartLocationIconType.HISTORY
        ))
        
        options.add(StartLocationOption(
            type = StartLocationType.HOME,
            displayName = "Home",
            description = "Teleport to your home location",
            iconType = StartLocationIconType.HOME
        ))
        
        // Favorites section (if any)
        val favorites = _favoriteLocations.value
        if (favorites.isNotEmpty()) {
            options.add(StartLocationOption(
                type = StartLocationType.HEADER,
                displayName = "Favorite Locations",
                description = "",
                iconType = StartLocationIconType.STAR
            ))
            favorites.take(5).forEach { fav ->
                options.add(StartLocationOption(
                    type = StartLocationType.FAVORITE,
                    displayName = fav.name,
                    description = fav.regionName,
                    iconType = StartLocationIconType.STAR,
                    slurl = fav.toSLURL(),
                    favoriteId = fav.id
                ))
            }
        }
        
        // Cached landmarks section (if any)
        val landmarks = _cachedLandmarks.value
        if (landmarks.isNotEmpty()) {
            options.add(StartLocationOption(
                type = StartLocationType.HEADER,
                displayName = "Saved Landmarks",
                description = "",
                iconType = StartLocationIconType.LANDMARK
            ))
            landmarks.take(10).forEach { lm ->
                options.add(StartLocationOption(
                    type = StartLocationType.LANDMARK,
                    displayName = lm.name,
                    description = lm.regionName,
                    iconType = StartLocationIconType.LANDMARK,
                    slurl = lm.toSLURL(),
                    landmarkId = lm.inventoryItemId
                ))
            }
        }
        
        // Custom SLURL option
        options.add(StartLocationOption(
            type = StartLocationType.CUSTOM_SLURL,
            displayName = "Custom Location (SLURL)...",
            description = "Enter a specific location",
            iconType = StartLocationIconType.LOCATION
        ))
        
        return options
    }
    
    /**
     * Refresh the start options list.
     */
    fun refreshStartOptions() {
        _startOptions.value = getAvailableOptions()
    }
    
    /**
     * Select a start location option.
     */
    fun selectOption(option: StartLocationOption) {
        _selectedOption.value = option.type
        
        when (option.type) {
            StartLocationType.LANDMARK -> {
                val landmark = _cachedLandmarks.value.find { 
                    it.inventoryItemId == option.landmarkId 
                }
                _selectedLandmark.value = landmark
                _selectedDestination.value = null
                _customSLURL.value = option.slurl
            }
            StartLocationType.DESTINATION -> {
                _selectedLandmark.value = null
                _selectedDestination.value = Destination(
                    name = option.displayName,
                    slurl = option.slurl ?: ""
                )
                _customSLURL.value = option.slurl
            }
            StartLocationType.FAVORITE -> {
                _selectedLandmark.value = null
                _selectedDestination.value = null
                _customSLURL.value = option.slurl
            }
            StartLocationType.CUSTOM_SLURL -> {
                _selectedLandmark.value = null
                _selectedDestination.value = null
                // Keep existing custom SLURL or clear it for user input
            }
            else -> {
                _selectedLandmark.value = null
                _selectedDestination.value = null
                _customSLURL.value = null
            }
        }
        
        savePreferences()
    }
    
    /**
     * Set custom SLURL.
     */
    fun setCustomSLURL(slurl: String?) {
        _customSLURL.value = slurl
        if (!slurl.isNullOrBlank()) {
            _selectedOption.value = StartLocationType.CUSTOM_SLURL
        }
        savePreferences()
    }
    
    /**
     * Get the start location string for login request.
     * Returns the format expected by Second Life login API.
     */
    fun getStartLocationForLogin(): String {
        return when (_selectedOption.value) {
            StartLocationType.LAST_LOCATION -> "last"
            StartLocationType.HOME -> "home"
            StartLocationType.LANDMARK,
            StartLocationType.DESTINATION,
            StartLocationType.FAVORITE,
            StartLocationType.CUSTOM_SLURL -> {
                val slurl = _customSLURL.value
                if (!slurl.isNullOrBlank()) {
                    parseSLURLToLoginString(slurl)
                } else {
                    "last"
                }
            }
            else -> "last"
        }
    }
    
    /**
     * Parse SLURL to login start location format.
     * Format: uri:region_name&x&y&z
     */
    private fun parseSLURLToLoginString(slurl: String): String {
        // Handle various SLURL formats
        val patterns = listOf(
            Regex("secondlife://([^/]+)/(\\d+)?/?(\\d+)?/?(\\d+)?", RegexOption.IGNORE_CASE),
            Regex("https?://maps\\.secondlife\\.com/secondlife/([^/]+)/(\\d+)?/?(\\d+)?/?(\\d+)?", RegexOption.IGNORE_CASE)
        )
        
        for (pattern in patterns) {
            val match = pattern.find(slurl)
            if (match != null) {
                val regionName = try {
                    java.net.URLDecoder.decode(match.groupValues[1], Charsets.UTF_8.name())
                } catch (e: Exception) {
                    // If decoding fails, use the raw value
                    match.groupValues[1]
                }
                val x = match.groupValues.getOrNull(2)?.toIntOrNull() ?: 128
                val y = match.groupValues.getOrNull(3)?.toIntOrNull() ?: 128
                val z = match.groupValues.getOrNull(4)?.toIntOrNull() ?: 25
                return "uri:$regionName&$x&$y&$z"
            }
        }
        
        // If parsing fails, try to use as region name directly
        return if (slurl.contains("://") || slurl.contains("&")) {
            "last"  // Invalid format, fall back to last
        } else {
            "uri:$slurl&128&128&25"
        }
    }
    
    /**
     * Cache landmarks from inventory after login.
     */
    fun cacheLandmarksFromInventory(landmarks: List<InventoryLandmarkInfo>) {
        val cached = landmarks.take(MAX_CACHED_LANDMARKS).map { info ->
            CachedLandmark(
                inventoryItemId = info.itemId,
                assetId = info.assetId,
                name = info.name,
                description = info.description,
                regionName = info.regionName,
                x = info.x,
                y = info.y,
                z = info.z,
                cachedAt = System.currentTimeMillis()
            )
        }
        
        _cachedLandmarks.value = cached
        saveCachedLandmarks()
        refreshStartOptions()
        
        Log.i(TAG, "Cached ${cached.size} landmarks from inventory")
    }
    
    /**
     * Add a favorite location.
     */
    fun addFavoriteLocation(location: SavedLocation) {
        val current = _favoriteLocations.value.toMutableList()
        
        // Remove if already exists
        current.removeAll { it.id == location.id || it.toSLURL() == location.toSLURL() }
        
        // Add at beginning
        current.add(0, location)
        
        // Limit size
        if (current.size > MAX_FAVORITE_LOCATIONS) {
            current.removeAt(current.size - 1)
        }
        
        _favoriteLocations.value = current
        saveFavoriteLocations()
        refreshStartOptions()
    }
    
    /**
     * Remove a favorite location.
     */
    fun removeFavoriteLocation(locationId: String) {
        val current = _favoriteLocations.value.toMutableList()
        current.removeAll { it.id == locationId }
        _favoriteLocations.value = current
        saveFavoriteLocations()
        refreshStartOptions()
    }
    
    /**
     * Mark first login as complete (enables landmark caching).
     */
    fun markFirstLoginComplete() {
        prefs.edit().putBoolean(KEY_FIRST_LOGIN_COMPLETE, true).apply()
        Log.i(TAG, "First login marked complete - landmark caching enabled")
    }
    
    /**
     * Check if first login has been completed.
     */
    fun isFirstLoginComplete(): Boolean = prefs.getBoolean(KEY_FIRST_LOGIN_COMPLETE, false)
    
    // ==================== PERSISTENCE ====================
    
    private fun loadSavedPreferences() {
        try {
            val lastOption = prefs.getString(KEY_LAST_START_OPTION, null)
            if (lastOption != null) {
                _selectedOption.value = StartLocationType.valueOf(lastOption)
            }
            _customSLURL.value = prefs.getString(KEY_CUSTOM_SLURL, null)
        } catch (e: Exception) {
            Log.w(TAG, "Failed to load preferences", e)
        }
    }
    
    private fun savePreferences() {
        prefs.edit()
            .putString(KEY_LAST_START_OPTION, _selectedOption.value.name)
            .putString(KEY_CUSTOM_SLURL, _customSLURL.value)
            .apply()
    }
    
    private fun loadCachedLandmarks() {
        try {
            val json = prefs.getString(KEY_CACHED_LANDMARKS, null) ?: return
            val array = JSONArray(json)
            val landmarks = mutableListOf<CachedLandmark>()
            
            for (i in 0 until array.length()) {
                val obj = array.getJSONObject(i)
                landmarks.add(CachedLandmark(
                    inventoryItemId = obj.getString("inventoryItemId"),
                    assetId = obj.optString("assetId", ""),
                    name = obj.getString("name"),
                    description = obj.optString("description", ""),
                    regionName = obj.getString("regionName"),
                    x = obj.getInt("x"),
                    y = obj.getInt("y"),
                    z = obj.getInt("z"),
                    cachedAt = obj.getLong("cachedAt")
                ))
            }
            
            _cachedLandmarks.value = landmarks
            Log.i(TAG, "Loaded ${landmarks.size} cached landmarks")
        } catch (e: Exception) {
            Log.w(TAG, "Failed to load cached landmarks", e)
        }
    }
    
    private fun saveCachedLandmarks() {
        try {
            val array = JSONArray()
            for (lm in _cachedLandmarks.value) {
                val obj = JSONObject().apply {
                    put("inventoryItemId", lm.inventoryItemId)
                    put("assetId", lm.assetId)
                    put("name", lm.name)
                    put("description", lm.description)
                    put("regionName", lm.regionName)
                    put("x", lm.x)
                    put("y", lm.y)
                    put("z", lm.z)
                    put("cachedAt", lm.cachedAt)
                }
                array.put(obj)
            }
            prefs.edit().putString(KEY_CACHED_LANDMARKS, array.toString()).apply()
        } catch (e: Exception) {
            Log.e(TAG, "Failed to save cached landmarks", e)
        }
    }
    
    private fun loadFavoriteLocations() {
        try {
            val json = prefs.getString(KEY_FAVORITE_LOCATIONS, null) ?: return
            val array = JSONArray(json)
            val favorites = mutableListOf<SavedLocation>()
            
            for (i in 0 until array.length()) {
                val obj = array.getJSONObject(i)
                favorites.add(SavedLocation(
                    id = obj.getString("id"),
                    name = obj.getString("name"),
                    regionName = obj.getString("regionName"),
                    x = obj.getInt("x"),
                    y = obj.getInt("y"),
                    z = obj.getInt("z"),
                    addedAt = obj.getLong("addedAt")
                ))
            }
            
            _favoriteLocations.value = favorites
            Log.i(TAG, "Loaded ${favorites.size} favorite locations")
        } catch (e: Exception) {
            Log.w(TAG, "Failed to load favorite locations", e)
        }
    }
    
    private fun saveFavoriteLocations() {
        try {
            val array = JSONArray()
            for (loc in _favoriteLocations.value) {
                val obj = JSONObject().apply {
                    put("id", loc.id)
                    put("name", loc.name)
                    put("regionName", loc.regionName)
                    put("x", loc.x)
                    put("y", loc.y)
                    put("z", loc.z)
                    put("addedAt", loc.addedAt)
                }
                array.put(obj)
            }
            prefs.edit().putString(KEY_FAVORITE_LOCATIONS, array.toString()).apply()
        } catch (e: Exception) {
            Log.e(TAG, "Failed to save favorite locations", e)
        }
    }
}

// ==================== DATA CLASSES ====================

enum class StartLocationType {
    LAST_LOCATION,
    HOME,
    LANDMARK,
    DESTINATION,
    FAVORITE,
    CUSTOM_SLURL,
    HEADER  // For section headers in list
}

enum class StartLocationIconType {
    HISTORY,
    HOME,
    LANDMARK,
    LOCATION,
    STAR,
    CATEGORY
}

data class StartLocationOption(
    val type: StartLocationType,
    val displayName: String,
    val description: String,
    val iconType: StartLocationIconType,
    val slurl: String? = null,
    val landmarkId: String? = null,
    val favoriteId: String? = null,
    val categoryId: String? = null
)

data class CachedLandmark(
    val inventoryItemId: String,
    val assetId: String,
    val name: String,
    val description: String,
    val regionName: String,
    val x: Int,
    val y: Int,
    val z: Int,
    val cachedAt: Long
) {
    fun toSLURL(): String = "secondlife://$regionName/$x/$y/$z"
}

data class SavedLocation(
    val id: String = java.util.UUID.randomUUID().toString(),
    val name: String,
    val regionName: String,
    val x: Int,
    val y: Int,
    val z: Int,
    val addedAt: Long = System.currentTimeMillis()
) {
    fun toSLURL(): String = "secondlife://$regionName/$x/$y/$z"
}

data class Destination(
    val name: String,
    val slurl: String,
    val description: String = "",
    val category: String = "",
    val imageUrl: String? = null
)

/**
 * Info about a landmark from inventory (used when caching after login).
 */
data class InventoryLandmarkInfo(
    val itemId: String,
    val assetId: String,
    val name: String,
    val description: String,
    val regionName: String,
    val x: Int,
    val y: Int,
    val z: Int
)
