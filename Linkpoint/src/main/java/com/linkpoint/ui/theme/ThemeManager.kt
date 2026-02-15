package com.linkpoint.ui.theme

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.util.Log
import androidx.core.content.FileProvider
import com.ktheme.library.KthemeAPI
import com.ktheme.library.ThemeChangeListener
import com.ktheme.models.Theme
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.withContext
import java.io.File
import java.util.concurrent.atomic.AtomicBoolean

/**
 * ThemeManager handles loading, saving, and sharing theme packs.
 * 
 * Features:
 * - Load/save user-created themes from app storage
 * - Import themes from JSON files
 * - Export themes for sharing
 * - Share themes via Android share sheet
 * - Manage active theme selection
 */
class ThemeManager private constructor(private val context: Context) {
    
    companion object {
        private const val TAG = "ThemeManager"
        private const val PREFS_NAME = "linkpoint_themes"
        private const val PREF_ACTIVE_THEME = "active_theme_id"
        private const val THEMES_DIR = "themes"
        private const val THEME_FILE_EXTENSION = ".linkpoint-theme.json"
        
        @Volatile
        private var instance: ThemeManager? = null
        
        fun getInstance(context: Context): ThemeManager {
            return instance ?: synchronized(this) {
                instance ?: ThemeManager(context.applicationContext).also { instance = it }
            }
        }
    }
    
    private val isApplyingRemoteUpdate = AtomicBoolean(false)

    private val prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
    private val themesDir = File(context.filesDir, THEMES_DIR).apply { mkdirs() }
    
    // Current active theme
    private val _activeTheme = MutableStateFlow(BuiltInThemes.LINKPOINT_DEFAULT)
    val activeTheme: StateFlow<ThemePack> = _activeTheme.asStateFlow()
    
    // All available themes (built-in + user)
    private val _availableThemes = MutableStateFlow<List<ThemePack>>(emptyList())
    val availableThemes: StateFlow<List<ThemePack>> = _availableThemes.asStateFlow()
    
    init {
        loadThemes()
        loadActiveTheme()
        subscribeToKthemeUpdates()
    }
    
    /**
     * Load all themes (built-in + user-created)
     */
    private fun loadThemes() {
        val themesById = linkedMapOf<String, ThemePack>()

        // Add built-in themes first.
        BuiltInThemes.getAllBuiltInThemes().forEach { themesById[it.id] = it }

        // Load user themes from app storage.
        loadUserThemes().forEach { themesById[it.id] = it }

        // Load shared Ktheme themes (cross-app + auto-updating source).
        loadKthemeThemes().forEach { sharedTheme ->
            if (!themesById.containsKey(sharedTheme.id)) {
                themesById[sharedTheme.id] = sharedTheme
            }
        }

        val themes = themesById.values.toList()
        _availableThemes.value = themes
        Log.d(TAG, "Loaded ${themes.size} themes (${BuiltInThemes.getAllBuiltInThemes().size} built-in)")
    }
    
    /**
     * Load user-created themes from storage
     */
    private fun loadUserThemes(): List<ThemePack> {
        val userThemes = mutableListOf<ThemePack>()
        
        themesDir.listFiles { file -> file.name.endsWith(THEME_FILE_EXTENSION) }?.forEach { file ->
            try {
                val json = file.readText()
                ThemePack.fromJson(json)?.let { theme ->
                    userThemes.add(theme)
                    Log.d(TAG, "Loaded user theme: ${theme.name}")
                }
            } catch (e: Exception) {
                Log.e(TAG, "Failed to load theme from ${file.name}", e)
            }
        }
        
        return userThemes
    }
    
    /**
     * Load themes exposed via KthemeAPI and map them to ThemePack.
     */
    private fun loadKthemeThemes(): List<ThemePack> {
        return try {
            KthemeAPI.getAvailableThemes().map { it.toThemePack() }
        } catch (e: Exception) {
            Log.w(TAG, "Unable to load themes from KthemeAPI", e)
            emptyList()
        }
    }

    /**
     * Subscribe to Ktheme updates so theme list auto-refreshes.
     */
    private fun subscribeToKthemeUpdates() {
        try {
            KthemeAPI.onThemeChanged(object : ThemeChangeListener {
                override fun onThemeAdded(theme: Theme) {
                    refreshThemesFromKtheme()
                }

                override fun onThemeRemoved(themeId: String) {
                    refreshThemesFromKtheme()
                }

                override fun onThemeUpdated(theme: Theme) {
                    refreshThemesFromKtheme()
                }
            })
        } catch (e: Exception) {
            Log.w(TAG, "Failed to subscribe to Ktheme updates", e)
        }
    }

    private fun refreshThemesFromKtheme() {
        isApplyingRemoteUpdate.set(true)
        loadThemes()
        loadActiveTheme()
        isApplyingRemoteUpdate.set(false)
    }

    /**
     * Load the active theme from preferences
     */
    private fun loadActiveTheme() {
        val activeThemeId = prefs.getString(PREF_ACTIVE_THEME, BuiltInThemes.LINKPOINT_DEFAULT.id)
        val theme = _availableThemes.value.find { it.id == activeThemeId }
            ?: BuiltInThemes.LINKPOINT_DEFAULT
        _activeTheme.value = theme
        Log.d(TAG, "Active theme: ${theme.name}")
    }
    
    /**
     * Set the active theme
     */
    fun setActiveTheme(theme: ThemePack) {
        _activeTheme.value = theme
        prefs.edit().putString(PREF_ACTIVE_THEME, theme.id).apply()

        if (!isApplyingRemoteUpdate.get()) {
            try {
                KthemeAPI.shareTheme(theme.toKthemeTheme())
            } catch (e: Exception) {
                Log.w(TAG, "Unable to publish active theme to KthemeAPI", e)
            }
        }

        Log.d(TAG, "Set active theme: ${theme.name}")
    }
    
    /**
     * Set the active theme by ID
     */
    fun setActiveThemeById(themeId: String): Boolean {
        val theme = _availableThemes.value.find { it.id == themeId }
        return if (theme != null) {
            setActiveTheme(theme)
            true
        } else {
            Log.w(TAG, "Theme not found: $themeId")
            false
        }
    }
    
    /**
     * Save a user-created theme
     */
    suspend fun saveTheme(theme: ThemePack): Result<ThemePack> = withContext(Dispatchers.IO) {
        try {
            // Validate theme
            when (val validation = theme.validate()) {
                is ThemePack.ValidationResult.Invalid -> {
                    return@withContext Result.failure(
                        IllegalArgumentException("Invalid theme: ${validation.errors.joinToString()}")
                    )
                }
                is ThemePack.ValidationResult.Valid -> { /* continue */ }
            }
            
            // Ensure it's not marked as built-in
            val themeToSave = if (theme.isBuiltIn) {
                theme.copy(isBuiltIn = false, id = "custom_${System.currentTimeMillis()}")
            } else {
                theme
            }
            
            // Save to file
            val file = File(themesDir, "${themeToSave.id}$THEME_FILE_EXTENSION")
            file.writeText(themeToSave.toJson())
            
            // Reload themes
            loadThemes()
            
            Log.d(TAG, "Saved theme: ${themeToSave.name}")
            Result.success(themeToSave)
        } catch (e: Exception) {
            Log.e(TAG, "Failed to save theme", e)
            Result.failure(e)
        }
    }
    
    /**
     * Delete a user-created theme
     */
    suspend fun deleteTheme(themeId: String): Boolean = withContext(Dispatchers.IO) {
        // Don't allow deleting built-in themes
        if (BuiltInThemes.getById(themeId) != null) {
            Log.w(TAG, "Cannot delete built-in theme: $themeId")
            return@withContext false
        }
        
        val file = File(themesDir, "$themeId$THEME_FILE_EXTENSION")
        val deleted = file.delete()
        
        if (deleted) {
            // If this was the active theme, switch to default
            if (_activeTheme.value.id == themeId) {
                setActiveTheme(BuiltInThemes.LINKPOINT_DEFAULT)
            }
            loadThemes()
            Log.d(TAG, "Deleted theme: $themeId")
        }
        
        deleted
    }
    
    /**
     * Import a theme from a JSON string
     */
    suspend fun importTheme(json: String): Result<ThemePack> = withContext(Dispatchers.IO) {
        try {
            val theme = ThemePack.fromJson(json)
                ?: return@withContext Result.failure(IllegalArgumentException("Invalid theme JSON"))
            
            // Generate new ID to avoid conflicts
            val importedTheme = theme.copy(
                id = "imported_${System.currentTimeMillis()}",
                isBuiltIn = false
            )
            
            saveTheme(importedTheme)
        } catch (e: Exception) {
            Log.e(TAG, "Failed to import theme", e)
            Result.failure(e)
        }
    }
    
    /**
     * Import a theme from a URI (e.g., from file picker)
     */
    suspend fun importThemeFromUri(uri: Uri): Result<ThemePack> = withContext(Dispatchers.IO) {
        try {
            val inputStream = context.contentResolver.openInputStream(uri)
                ?: return@withContext Result.failure(IllegalArgumentException("Cannot open file"))
            
            val json = inputStream.bufferedReader().use { it.readText() }
            importTheme(json)
        } catch (e: Exception) {
            Log.e(TAG, "Failed to import theme from URI", e)
            Result.failure(e)
        }
    }
    
    /**
     * Export a theme to a shareable file.
     * 
     * Note: This requires FileProvider configuration in AndroidManifest.xml:
     * ```xml
     * <provider
     *     android:name="androidx.core.content.FileProvider"
     *     android:authorities="${applicationId}.fileprovider"
     *     android:exported="false"
     *     android:grantUriPermissions="true">
     *     <meta-data
     *         android:name="android.support.FILE_PROVIDER_PATHS"
     *         android:resource="@xml/file_paths" />
     * </provider>
     * ```
     * And res/xml/file_paths.xml with cache-path for "theme_exports".
     */
    suspend fun exportTheme(theme: ThemePack): Result<Uri> = withContext(Dispatchers.IO) {
        try {
            // Create export file in cache directory
            val exportDir = File(context.cacheDir, "theme_exports").apply { mkdirs() }
            val exportFile = File(exportDir, "${theme.id}$THEME_FILE_EXTENSION")
            exportFile.writeText(theme.toJson())
            
            // Get content URI for sharing (requires FileProvider in manifest)
            val uri = FileProvider.getUriForFile(
                context,
                "${context.packageName}.fileprovider",
                exportFile
            )
            
            Log.d(TAG, "Exported theme: ${theme.name} to $uri")
            Result.success(uri)
        } catch (e: Exception) {
            Log.e(TAG, "Failed to export theme", e)
            Result.failure(e)
        }
    }
    
    /**
     * Share a theme via Android share sheet
     */
    fun shareTheme(theme: ThemePack): Intent {
        val shareText = buildString {
            appendLine("🎨 Linkpoint Theme Pack: ${theme.name}")
            appendLine()
            appendLine("Author: ${theme.author}")
            if (theme.description.isNotBlank()) {
                appendLine("Description: ${theme.description}")
            }
            appendLine()
            appendLine("--- Theme JSON (copy and import in Linkpoint) ---")
            appendLine(theme.toJson())
        }
        
        return Intent(Intent.ACTION_SEND).apply {
            type = "text/plain"
            putExtra(Intent.EXTRA_SUBJECT, "Linkpoint Theme: ${theme.name}")
            putExtra(Intent.EXTRA_TEXT, shareText)
        }
    }
    
    /**
     * Create a share intent with the theme file attached
     */
    suspend fun createShareIntent(theme: ThemePack): Result<Intent> = withContext(Dispatchers.IO) {
        exportTheme(theme).map { uri ->
            Intent(Intent.ACTION_SEND).apply {
                type = "application/json"
                putExtra(Intent.EXTRA_SUBJECT, "Linkpoint Theme: ${theme.name}")
                putExtra(Intent.EXTRA_TEXT, "Check out my Linkpoint theme: ${theme.name}!")
                putExtra(Intent.EXTRA_STREAM, uri)
                addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
            }
        }
    }
    
    /**
     * Create a new theme from a template
     */
    fun createNewTheme(
        name: String,
        description: String = "",
        author: String = "User",
        baseTheme: ThemePack = BuiltInThemes.LINKPOINT_DEFAULT
    ): ThemePack {
        return baseTheme.copy(
            id = "custom_${System.currentTimeMillis()}",
            name = name,
            description = description,
            author = author,
            version = "1.0.0",
            isBuiltIn = false
        )
    }
    
    /**
     * Duplicate an existing theme for editing
     */
    fun duplicateTheme(theme: ThemePack): ThemePack {
        return theme.copy(
            id = "copy_${System.currentTimeMillis()}",
            name = "${theme.name} (Copy)",
            isBuiltIn = false
        )
    }
    
    /**
     * Refresh themes from storage
     */
    fun refreshThemes() {
        loadThemes()
    }
}
