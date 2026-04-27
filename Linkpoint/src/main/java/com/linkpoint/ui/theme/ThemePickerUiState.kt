package com.linkpoint.ui.theme

import android.net.Uri
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

/** UI state for the theme picker screen; designed for unit testing. */
data class ThemePickerUiState(
    val availableThemes: List<ThemePack> = emptyList(),
    val activeTheme: ThemePack = BuiltInThemes.LINKPOINT_DEFAULT,
    val selectedFamily: ThemeCatalog.Family? = null,
    val searchQuery: String = "",
    val recentlyUsedThemeIds: List<String> = emptyList(),
    val pendingDeleteTheme: ThemePack? = null,
    val deletedThemeUndoCandidate: ThemePack? = null,
    val bannerMessage: String? = null,
    val bannerIsError: Boolean = false
) {
    val currentTheme: ThemePack get() = activeTheme

    val recentThemes: List<ThemePack>
        get() = recentlyUsedThemeIds.mapNotNull { id -> availableThemes.find { it.id == id } }

    val filteredBuiltInThemesByFamily: Map<ThemeCatalog.Family, List<ThemePack>>
        get() {
            val query = searchQuery.trim().lowercase()
            val base = availableThemes.filter { it.isBuiltIn }
                .filter { selectedFamily == null || ThemeCatalog.familyFor(it) == selectedFamily }
                .filter { query.isBlank() || it.name.lowercase().contains(query) || it.description.lowercase().contains(query) }

            return base.groupBy { ThemeCatalog.familyFor(it) }
                .toSortedMap(compareBy { it.displayName })
        }

    val filteredUserThemes: List<ThemePack>
        get() {
            val query = searchQuery.trim().lowercase()
            return availableThemes.filter { !it.isBuiltIn }
                .filter {
                    query.isBlank() ||
                        it.name.lowercase().contains(query) ||
                        it.description.lowercase().contains(query)
                }
        }
}

interface ThemePickerThemeGateway {
    val availableThemes: StateFlow<List<ThemePack>>
    val activeTheme: StateFlow<ThemePack>

    fun setActiveTheme(theme: ThemePack)
    suspend fun saveTheme(theme: ThemePack): Result<ThemePack>
    suspend fun deleteTheme(themeId: String): Boolean
    suspend fun importTheme(json: String): Result<ThemePack>
    suspend fun exportTheme(theme: ThemePack): Result<Uri>
    fun createNewTheme(name: String, description: String = "", author: String = "User", baseTheme: ThemePack = BuiltInThemes.LINKPOINT_DEFAULT): ThemePack
    fun duplicateTheme(theme: ThemePack): ThemePack
}

class ThemeManagerGateway(private val themeManager: ThemeManager) : ThemePickerThemeGateway {
    override val availableThemes: StateFlow<List<ThemePack>> = themeManager.availableThemes
    override val activeTheme: StateFlow<ThemePack> = themeManager.activeTheme

    override fun setActiveTheme(theme: ThemePack) = themeManager.setActiveTheme(theme)
    override suspend fun saveTheme(theme: ThemePack): Result<ThemePack> = themeManager.saveTheme(theme)
    override suspend fun deleteTheme(themeId: String): Boolean = themeManager.deleteTheme(themeId)
    override suspend fun importTheme(json: String): Result<ThemePack> = themeManager.importTheme(json)
    override suspend fun exportTheme(theme: ThemePack): Result<Uri> = themeManager.exportTheme(theme)
    override fun createNewTheme(name: String, description: String, author: String, baseTheme: ThemePack): ThemePack {
        return themeManager.createNewTheme(name = name, description = description, author = author, baseTheme = baseTheme)
    }
    override fun duplicateTheme(theme: ThemePack): ThemePack = themeManager.duplicateTheme(theme)
}

class ThemePickerStateModel(
    private val gateway: ThemePickerThemeGateway,
    private val scope: CoroutineScope,
    private val maxRecentThemes: Int = 5
) {
    private val _uiState = MutableStateFlow(ThemePickerUiState())
    val uiState: StateFlow<ThemePickerUiState> = _uiState.asStateFlow()

    init {
        scope.launch {
            gateway.availableThemes.collect { themes ->
                _uiState.update { current -> current.copy(availableThemes = themes) }
            }
        }
        scope.launch {
            gateway.activeTheme.collect { active ->
                _uiState.update { current ->
                    current.copy(
                        activeTheme = active,
                        recentlyUsedThemeIds = (listOf(active.id) + current.recentlyUsedThemeIds)
                            .distinct()
                            .take(maxRecentThemes)
                    )
                }
            }
        }
    }

    fun onSearchQueryChanged(query: String) {
        _uiState.update { it.copy(searchQuery = query) }
    }

    fun onFamilyFilterSelected(family: ThemeCatalog.Family?) {
        _uiState.update { state ->
            val next = if (state.selectedFamily == family) null else family
            state.copy(selectedFamily = next)
        }
    }

    fun onThemeSelected(theme: ThemePack, onThemeSelected: (ThemePack) -> Unit = {}) {
        gateway.setActiveTheme(theme)
        onThemeSelected(theme)
    }

    fun onCreateTheme(name: String, description: String, author: String = "User", baseTheme: ThemePack? = null) {
        scope.launch {
            val theme = gateway.createNewTheme(
                name = name,
                description = description,
                author = author,
                baseTheme = baseTheme ?: _uiState.value.activeTheme
            )
            gateway.saveTheme(theme)
                .onSuccess { created ->
                    banner("Created theme \"${created.name}\"", isError = false)
                    gateway.setActiveTheme(created)
                }
                .onFailure { banner("Create failed: ${it.message ?: "unknown error"}", isError = true) }
        }
    }

    fun onEditTheme(theme: ThemePack, newName: String, newDescription: String) {
        scope.launch {
            val updated = theme.copy(name = newName, description = newDescription, isBuiltIn = false)
            gateway.saveTheme(updated)
                .onSuccess { banner("Saved \"${it.name}\"", isError = false) }
                .onFailure { banner("Edit failed: ${it.message ?: "unknown error"}", isError = true) }
        }
    }

    fun onImportTheme(json: String) {
        scope.launch {
            gateway.importTheme(json)
                .onSuccess { imported ->
                    banner("Imported \"${imported.name}\"", isError = false)
                    gateway.setActiveTheme(imported)
                }
                .onFailure { banner("Import failed: ${it.message ?: "unknown error"}", isError = true) }
        }
    }

    fun onExportTheme(theme: ThemePack, onExportReady: (Uri) -> Unit = {}) {
        scope.launch {
            gateway.exportTheme(theme)
                .onSuccess {
                    banner("Export ready for \"${theme.name}\"", isError = false)
                    onExportReady(it)
                }
                .onFailure { banner("Export failed: ${it.message ?: "unknown error"}", isError = true) }
        }
    }

    fun requestDelete(theme: ThemePack) {
        _uiState.update { it.copy(pendingDeleteTheme = theme) }
    }

    fun dismissDeleteDialog() {
        _uiState.update { it.copy(pendingDeleteTheme = null) }
    }

    fun confirmDeleteTheme() {
        val theme = _uiState.value.pendingDeleteTheme ?: return
        scope.launch {
            val deleted = gateway.deleteTheme(theme.id)
            _uiState.update { it.copy(pendingDeleteTheme = null) }
            if (deleted) {
                _uiState.update { it.copy(deletedThemeUndoCandidate = theme) }
                banner("Deleted \"${theme.name}\". Tap undo to restore.", isError = false)
            } else {
                banner("Delete failed for \"${theme.name}\"", isError = true)
            }
        }
    }

    fun undoDeleteTheme() {
        val deleted = _uiState.value.deletedThemeUndoCandidate ?: return
        scope.launch {
            gateway.saveTheme(deleted)
                .onSuccess {
                    _uiState.update { it.copy(deletedThemeUndoCandidate = null) }
                    banner("Restored \"${deleted.name}\"", isError = false)
                }
                .onFailure { banner("Undo failed: ${it.message ?: "unknown error"}", isError = true) }
        }
    }

    fun clearBanner() {
        _uiState.update { it.copy(bannerMessage = null, bannerIsError = false) }
    }

    private fun banner(message: String, isError: Boolean) {
        _uiState.update { it.copy(bannerMessage = message, bannerIsError = isError) }
    }
}
