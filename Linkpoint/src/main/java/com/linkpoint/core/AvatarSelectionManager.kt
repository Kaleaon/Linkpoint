package com.linkpoint.core

import android.content.Context
import android.content.SharedPreferences
import android.util.Log
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import org.json.JSONArray
import org.json.JSONObject

/**
 * Manages avatar selection options for login and appearance.
 * 
 * Features:
 * - Avatar type selection (Human, Robot, Animal, etc.)
 * - Saved avatar configurations
 * - Default avatar presets
 * - Integration with Second Life avatar system
 * 
 * Based on Second Life mobile app functionality.
 */
class AvatarSelectionManager(private val context: Context) {
    
    companion object {
        private const val TAG = "AvatarSelectionManager"
        private const val PREFS_NAME = "avatar_selection_prefs"
        private const val KEY_SELECTED_TYPE = "selected_avatar_type"
        private const val KEY_SAVED_AVATARS = "saved_avatars"
        private const val KEY_LAST_AVATAR_ID = "last_avatar_id"
        private const val MAX_SAVED_AVATARS = 10
    }
    
    private val prefs: SharedPreferences = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
    
    // Available avatar types
    private val _avatarTypes = MutableStateFlow<List<AvatarType>>(emptyList())
    val avatarTypes: StateFlow<List<AvatarType>> = _avatarTypes
    
    // Saved avatar configurations
    private val _savedAvatars = MutableStateFlow<List<SavedAvatar>>(emptyList())
    val savedAvatars: StateFlow<List<SavedAvatar>> = _savedAvatars
    
    // Current selection
    private val _selectedType = MutableStateFlow<AvatarType?>(null)
    val selectedType: StateFlow<AvatarType?> = _selectedType
    
    private val _selectedAvatar = MutableStateFlow<SavedAvatar?>(null)
    val selectedAvatar: StateFlow<SavedAvatar?> = _selectedAvatar
    
    init {
        initializeAvatarTypes()
        loadSavedAvatars()
        loadLastSelection()
    }
    
    /**
     * Initialize available avatar types.
     * These correspond to Second Life's starter avatar categories.
     */
    private fun initializeAvatarTypes() {
        _avatarTypes.value = listOf(
            AvatarType(
                id = "human_male",
                name = "Human Male",
                description = "Classic human male avatars",
                category = AvatarCategory.HUMAN,
                gender = AvatarGender.MALE,
                iconName = "person"
            ),
            AvatarType(
                id = "human_female",
                name = "Human Female",
                description = "Classic human female avatars",
                category = AvatarCategory.HUMAN,
                gender = AvatarGender.FEMALE,
                iconName = "person"
            ),
            AvatarType(
                id = "vampire",
                name = "Vampire",
                description = "Gothic vampire avatars",
                category = AvatarCategory.FANTASY,
                gender = AvatarGender.NEUTRAL,
                iconName = "nightlight"
            ),
            AvatarType(
                id = "robot",
                name = "Robot",
                description = "Mechanical and android avatars",
                category = AvatarCategory.SCIFI,
                gender = AvatarGender.NEUTRAL,
                iconName = "android"
            ),
            AvatarType(
                id = "furry",
                name = "Furry/Animal",
                description = "Animal and anthropomorphic avatars",
                category = AvatarCategory.ANIMAL,
                gender = AvatarGender.NEUTRAL,
                iconName = "pets"
            ),
            AvatarType(
                id = "fantasy",
                name = "Fantasy",
                description = "Elves, fairies, and mythical creatures",
                category = AvatarCategory.FANTASY,
                gender = AvatarGender.NEUTRAL,
                iconName = "auto_awesome"
            ),
            AvatarType(
                id = "vehicle",
                name = "Vehicle/Object",
                description = "Non-humanoid vehicle avatars",
                category = AvatarCategory.OTHER,
                gender = AvatarGender.NEUTRAL,
                iconName = "directions_car"
            ),
            AvatarType(
                id = "custom",
                name = "Custom/Current",
                description = "Keep your current avatar appearance",
                category = AvatarCategory.CUSTOM,
                gender = AvatarGender.NEUTRAL,
                iconName = "tune"
            )
        )
    }
    
    /**
     * Get avatar types by category.
     */
    fun getAvatarsByCategory(category: AvatarCategory): List<AvatarType> {
        return _avatarTypes.value.filter { it.category == category }
    }
    
    /**
     * Get avatar types by gender.
     */
    fun getAvatarsByGender(gender: AvatarGender): List<AvatarType> {
        return _avatarTypes.value.filter { 
            it.gender == gender || it.gender == AvatarGender.NEUTRAL 
        }
    }
    
    /**
     * Select an avatar type.
     */
    fun selectAvatarType(type: AvatarType) {
        _selectedType.value = type
        _selectedAvatar.value = null
        saveSelection()
        Log.d(TAG, "Selected avatar type: ${type.name}")
    }
    
    /**
     * Select a saved avatar configuration.
     */
    fun selectSavedAvatar(avatar: SavedAvatar) {
        _selectedAvatar.value = avatar
        _selectedType.value = _avatarTypes.value.find { it.id == avatar.typeId }
        saveSelection()
        Log.d(TAG, "Selected saved avatar: ${avatar.name}")
    }
    
    /**
     * Save current avatar configuration.
     */
    fun saveCurrentAvatar(name: String, typeId: String, outfitFolderId: String? = null) {
        val avatar = SavedAvatar(
            id = java.util.UUID.randomUUID().toString(),
            name = name,
            typeId = typeId,
            outfitFolderId = outfitFolderId,
            savedAt = System.currentTimeMillis()
        )
        
        val current = _savedAvatars.value.toMutableList()
        
        // Remove existing with same name
        current.removeAll { it.name == name }
        
        // Add at beginning
        current.add(0, avatar)
        
        // Limit size
        if (current.size > MAX_SAVED_AVATARS) {
            current.removeAt(current.size - 1)
        }
        
        _savedAvatars.value = current
        saveSavedAvatars()
        
        Log.i(TAG, "Saved avatar configuration: $name")
    }
    
    /**
     * Delete a saved avatar configuration.
     */
    fun deleteSavedAvatar(avatarId: String) {
        val current = _savedAvatars.value.toMutableList()
        current.removeAll { it.id == avatarId }
        _savedAvatars.value = current
        saveSavedAvatars()
        
        if (_selectedAvatar.value?.id == avatarId) {
            _selectedAvatar.value = null
        }
    }
    
    /**
     * Get the selected avatar type ID for login.
     * Returns null to keep current avatar appearance.
     */
    fun getSelectedAvatarTypeForLogin(): String? {
        val selected = _selectedType.value ?: return null
        
        // Custom type means keep current appearance
        if (selected.category == AvatarCategory.CUSTOM) {
            return null
        }
        
        return selected.id
    }
    
    /**
     * Get outfit folder ID if a saved avatar is selected.
     */
    fun getSelectedOutfitFolderId(): String? {
        return _selectedAvatar.value?.outfitFolderId
    }
    
    // ==================== PERSISTENCE ====================
    
    private fun loadSavedAvatars() {
        try {
            val json = prefs.getString(KEY_SAVED_AVATARS, null) ?: return
            val array = JSONArray(json)
            val avatars = mutableListOf<SavedAvatar>()
            
            for (i in 0 until array.length()) {
                val obj = array.getJSONObject(i)
                avatars.add(SavedAvatar(
                    id = obj.getString("id"),
                    name = obj.getString("name"),
                    typeId = obj.getString("typeId"),
                    outfitFolderId = obj.optString("outfitFolderId", null),
                    savedAt = obj.getLong("savedAt")
                ))
            }
            
            _savedAvatars.value = avatars
            Log.i(TAG, "Loaded ${avatars.size} saved avatars")
        } catch (e: Exception) {
            Log.w(TAG, "Failed to load saved avatars", e)
        }
    }
    
    private fun saveSavedAvatars() {
        try {
            val array = JSONArray()
            for (avatar in _savedAvatars.value) {
                val obj = JSONObject().apply {
                    put("id", avatar.id)
                    put("name", avatar.name)
                    put("typeId", avatar.typeId)
                    if (avatar.outfitFolderId != null) {
                        put("outfitFolderId", avatar.outfitFolderId)
                    }
                    put("savedAt", avatar.savedAt)
                }
                array.put(obj)
            }
            prefs.edit().putString(KEY_SAVED_AVATARS, array.toString()).apply()
        } catch (e: Exception) {
            Log.e(TAG, "Failed to save avatars", e)
        }
    }
    
    private fun loadLastSelection() {
        try {
            val typeId = prefs.getString(KEY_SELECTED_TYPE, null)
            if (typeId != null) {
                _selectedType.value = _avatarTypes.value.find { it.id == typeId }
            }
            
            val avatarId = prefs.getString(KEY_LAST_AVATAR_ID, null)
            if (avatarId != null) {
                _selectedAvatar.value = _savedAvatars.value.find { it.id == avatarId }
            }
        } catch (e: Exception) {
            Log.w(TAG, "Failed to load last selection", e)
        }
    }
    
    private fun saveSelection() {
        prefs.edit()
            .putString(KEY_SELECTED_TYPE, _selectedType.value?.id)
            .putString(KEY_LAST_AVATAR_ID, _selectedAvatar.value?.id)
            .apply()
    }
}

// ==================== DATA CLASSES ====================

enum class AvatarCategory {
    HUMAN,
    FANTASY,
    SCIFI,
    ANIMAL,
    CUSTOM,
    OTHER
}

enum class AvatarGender {
    MALE,
    FEMALE,
    NEUTRAL
}

data class AvatarType(
    val id: String,
    val name: String,
    val description: String,
    val category: AvatarCategory,
    val gender: AvatarGender,
    val iconName: String,
    val previewImageUrl: String? = null
)

data class SavedAvatar(
    val id: String,
    val name: String,
    val typeId: String,
    val outfitFolderId: String? = null,
    val savedAt: Long = System.currentTimeMillis()
)
