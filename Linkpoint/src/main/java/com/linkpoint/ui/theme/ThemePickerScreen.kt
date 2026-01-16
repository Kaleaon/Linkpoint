package com.linkpoint.ui.theme

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Share
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.launch

/**
 * Theme Picker Screen - Allows users to browse, select, and manage themes.
 * 
 * Features:
 * - Browse built-in and user themes
 * - Preview theme colors
 * - Select active theme
 * - Create new themes
 * - Share themes
 * - Delete user themes
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ThemePickerScreen(
    themeManager: ThemeManager,
    onThemeSelected: (ThemePack) -> Unit = {},
    onShareTheme: (ThemePack) -> Unit = {},
    onCreateTheme: () -> Unit = {},
    onEditTheme: (ThemePack) -> Unit = {},
    onNavigateBack: () -> Unit = {}
) {
    val availableThemes by themeManager.availableThemes.collectAsState()
    val activeTheme by themeManager.activeTheme.collectAsState()
    val scope = rememberCoroutineScope()
    
    var showDeleteDialog by remember { mutableStateOf<ThemePack?>(null) }
    
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Theme Packs") }
            )
        },
        floatingActionButton = {
            FloatingActionButton(
                onClick = onCreateTheme,
                containerColor = MaterialTheme.colorScheme.primary
            ) {
                Icon(Icons.Default.Add, contentDescription = "Create theme")
            }
        }
    ) { paddingValues ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues),
            contentPadding = PaddingValues(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            // Built-in themes section
            item {
                Text(
                    text = "Built-in Themes",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold
                )
            }
            
            items(
                items = availableThemes.filter { it.isBuiltIn },
                key = { it.id }
            ) { theme ->
                ThemeCard(
                    theme = theme,
                    isSelected = theme.id == activeTheme.id,
                    onSelect = {
                        themeManager.setActiveTheme(theme)
                        onThemeSelected(theme)
                    },
                    onShare = { onShareTheme(theme) },
                    onEdit = null,  // Can't edit built-in themes
                    onDelete = null  // Can't delete built-in themes
                )
            }
            
            // User themes section
            val userThemes = availableThemes.filter { !it.isBuiltIn }
            if (userThemes.isNotEmpty()) {
                item {
                    Spacer(modifier = Modifier.height(16.dp))
                    Text(
                        text = "My Themes",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold
                    )
                }
                
                items(
                    items = userThemes,
                    key = { it.id }
                ) { theme ->
                    ThemeCard(
                        theme = theme,
                        isSelected = theme.id == activeTheme.id,
                        onSelect = {
                            themeManager.setActiveTheme(theme)
                            onThemeSelected(theme)
                        },
                        onShare = { onShareTheme(theme) },
                        onEdit = { onEditTheme(theme) },
                        onDelete = { showDeleteDialog = theme }
                    )
                }
            }
        }
    }
    
    // Delete confirmation dialog
    showDeleteDialog?.let { theme ->
        AlertDialog(
            onDismissRequest = { showDeleteDialog = null },
            title = { Text("Delete Theme") },
            text = { Text("Are you sure you want to delete \"${theme.name}\"?") },
            confirmButton = {
                Button(
                    onClick = {
                        scope.launch {
                            themeManager.deleteTheme(theme.id)
                            showDeleteDialog = null
                        }
                    }
                ) {
                    Text("Delete")
                }
            },
            dismissButton = {
                TextButton(onClick = { showDeleteDialog = null }) {
                    Text("Cancel")
                }
            }
        )
    }
}

/**
 * Card displaying a single theme with preview and actions.
 */
@Composable
fun ThemeCard(
    theme: ThemePack,
    isSelected: Boolean,
    onSelect: () -> Unit,
    onShare: () -> Unit,
    onEdit: (() -> Unit)?,
    onDelete: (() -> Unit)?,
    modifier: Modifier = Modifier
) {
    val colors = theme.toComposeColors()
    
    Card(
        modifier = modifier
            .fillMaxWidth()
            .clickable(onClick = onSelect)
            .then(
                if (isSelected) {
                    Modifier.border(
                        width = 2.dp,
                        color = MaterialTheme.colorScheme.primary,
                        shape = RoundedCornerShape(12.dp)
                    )
                } else {
                    Modifier
                }
            ),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(
            containerColor = colors.surface
        )
    ) {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text(
                            text = theme.name,
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold,
                            color = colors.onSurface
                        )
                        if (isSelected) {
                            Spacer(modifier = Modifier.width(8.dp))
                            Icon(
                                imageVector = Icons.Default.Check,
                                contentDescription = "Selected",
                                tint = colors.primary,
                                modifier = Modifier.size(20.dp)
                            )
                        }
                    }
                    
                    Text(
                        text = "by ${theme.author}",
                        style = MaterialTheme.typography.bodySmall,
                        color = colors.onSurfaceVariant
                    )
                    
                    if (theme.description.isNotBlank()) {
                        Text(
                            text = theme.description,
                            style = MaterialTheme.typography.bodySmall,
                            color = colors.onSurfaceVariant,
                            maxLines = 2,
                            overflow = TextOverflow.Ellipsis,
                            modifier = Modifier.padding(top = 4.dp)
                        )
                    }
                }
                
                // Action buttons
                Row {
                    IconButton(onClick = onShare) {
                        Icon(
                            imageVector = Icons.Default.Share,
                            contentDescription = "Share",
                            tint = colors.onSurfaceVariant
                        )
                    }
                    
                    onEdit?.let { edit ->
                        IconButton(onClick = edit) {
                            Icon(
                                imageVector = Icons.Default.Edit,
                                contentDescription = "Edit",
                                tint = colors.onSurfaceVariant
                            )
                        }
                    }
                    
                    onDelete?.let { delete ->
                        IconButton(onClick = delete) {
                            Icon(
                                imageVector = Icons.Default.Delete,
                                contentDescription = "Delete",
                                tint = colors.error
                            )
                        }
                    }
                }
            }
            
            Spacer(modifier = Modifier.height(12.dp))
            
            // Color preview
            ThemeColorPreview(colors = colors)
        }
    }
}

/**
 * Row of color swatches showing theme colors.
 */
@Composable
fun ThemeColorPreview(
    colors: LinkpointColors,
    modifier: Modifier = Modifier
) {
    LazyRow(
        modifier = modifier,
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        item { ColorSwatch(color = colors.primary, label = "Primary") }
        item { ColorSwatch(color = colors.secondary, label = "Secondary") }
        item { ColorSwatch(color = colors.background, label = "Background") }
        item { ColorSwatch(color = colors.surface, label = "Surface") }
        item { ColorSwatch(color = colors.error, label = "Error") }
        item { ColorSwatch(color = colors.success, label = "Success") }
    }
}

/**
 * Single color swatch with label.
 */
@Composable
fun ColorSwatch(
    color: Color,
    label: String,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Box(
            modifier = Modifier
                .size(36.dp)
                .clip(CircleShape)
                .background(color)
                .border(1.dp, Color.White.copy(alpha = 0.3f), CircleShape)
        )
        Text(
            text = label,
            style = MaterialTheme.typography.labelSmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            modifier = Modifier.padding(top = 4.dp)
        )
    }
}

/**
 * Simple theme selector for use in settings or dialogs.
 */
@Composable
fun ThemeSelector(
    themes: List<ThemePack>,
    selectedThemeId: String,
    onThemeSelected: (ThemePack) -> Unit,
    modifier: Modifier = Modifier
) {
    LazyRow(
        modifier = modifier,
        horizontalArrangement = Arrangement.spacedBy(12.dp),
        contentPadding = PaddingValues(horizontal = 16.dp)
    ) {
        items(themes, key = { it.id }) { theme ->
            ThemeSelectorItem(
                theme = theme,
                isSelected = theme.id == selectedThemeId,
                onClick = { onThemeSelected(theme) }
            )
        }
    }
}

/**
 * Compact theme selector item.
 */
@Composable
private fun ThemeSelectorItem(
    theme: ThemePack,
    isSelected: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val colors = theme.toComposeColors()
    
    Column(
        modifier = modifier
            .clickable(onClick = onClick)
            .padding(8.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Box(
            modifier = Modifier
                .size(48.dp)
                .clip(CircleShape)
                .background(colors.primary)
                .then(
                    if (isSelected) {
                        Modifier.border(3.dp, MaterialTheme.colorScheme.primary, CircleShape)
                    } else {
                        Modifier.border(1.dp, Color.White.copy(alpha = 0.2f), CircleShape)
                    }
                ),
            contentAlignment = Alignment.Center
        ) {
            if (isSelected) {
                Icon(
                    imageVector = Icons.Default.Check,
                    contentDescription = "Selected",
                    tint = colors.onPrimary,
                    modifier = Modifier.size(24.dp)
                )
            }
        }
        
        Text(
            text = theme.name,
            style = MaterialTheme.typography.labelSmall,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis,
            modifier = Modifier.padding(top = 4.dp)
        )
    }
}
