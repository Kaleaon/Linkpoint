package com.linkpoint.ui.notecard

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.AttachFile
import androidx.compose.material.icons.filled.ContentCopy
import androidx.compose.material.icons.filled.ExpandLess
import androidx.compose.material.icons.filled.ExpandMore
import androidx.compose.material.icons.filled.Save
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Divider
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.platform.LocalClipboardManager
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.linkpoint.inventory.notecard.EmbeddedItem
import com.linkpoint.inventory.notecard.NotecardData
import kotlinx.coroutines.launch
import java.util.UUID

/**
 * Compose-based Notecard Editor Screen.
 * 
 * Features:
 * - View and edit notecard content
 * - Display embedded items list (collapsible)
 * - Copy to clipboard
 * - Save changes (when editable)
 * - Unsaved changes confirmation dialog
 * - Dark theme optimized for reading/editing
 * 
 * Based on Lumiya/Firestorm notecard viewer design.
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun NotecardEditorScreen(
    notecardName: String,
    assetId: UUID?,
    itemId: UUID?,
    isReadOnly: Boolean = true,
    onLoadNotecard: suspend (UUID) -> NotecardData?,
    onSaveNotecard: suspend (UUID, String) -> Boolean,
    onNavigateBack: () -> Unit,
    modifier: Modifier = Modifier
) {
    val scope = rememberCoroutineScope()
    val snackbarHostState = remember { SnackbarHostState() }
    val clipboardManager = LocalClipboardManager.current
    
    var notecardContent by remember { mutableStateOf("") }
    var originalContent by remember { mutableStateOf("") }
    var embeddedItems by remember { mutableStateOf<List<EmbeddedItem>>(emptyList()) }
    var isLoading by remember { mutableStateOf(true) }
    var showEmbeddedItems by remember { mutableStateOf(false) }
    var hasUnsavedChanges by remember { mutableStateOf(false) }
    var showUnsavedDialog by remember { mutableStateOf(false) }
    
    // Track changes
    LaunchedEffect(notecardContent) {
        hasUnsavedChanges = notecardContent != originalContent && !isReadOnly
    }
    
    // Load notecard on mount
    LaunchedEffect(assetId) {
        if (assetId != null) {
            isLoading = true
            val notecard = onLoadNotecard(assetId)
            if (notecard != null) {
                notecardContent = notecard.text
                originalContent = notecard.text
                embeddedItems = notecard.embeddedItems
            } else {
                notecardContent = "Failed to load notecard.\n\nAsset ID: $assetId"
                originalContent = notecardContent
            }
            isLoading = false
        } else {
            notecardContent = ""
            originalContent = ""
            isLoading = false
        }
    }
    
    // Handle back press with unsaved changes
    fun handleBack() {
        if (hasUnsavedChanges) {
            showUnsavedDialog = true
        } else {
            onNavigateBack()
        }
    }
    
    // Unsaved changes dialog
    if (showUnsavedDialog) {
        AlertDialog(
            onDismissRequest = { showUnsavedDialog = false },
            title = { Text("Unsaved Changes") },
            text = { Text("You have unsaved changes. Do you want to save before closing?") },
            confirmButton = {
                TextButton(
                    onClick = {
                        showUnsavedDialog = false
                        scope.launch {
                            if (itemId != null) {
                                val success = onSaveNotecard(itemId, notecardContent)
                                if (success) {
                                    originalContent = notecardContent
                                    hasUnsavedChanges = false
                                }
                            }
                            onNavigateBack()
                        }
                    }
                ) {
                    Text("Save")
                }
            },
            dismissButton = {
                Row {
                    TextButton(onClick = { showUnsavedDialog = false }) {
                        Text("Cancel")
                    }
                    TextButton(onClick = {
                        showUnsavedDialog = false
                        onNavigateBack()
                    }) {
                        Text("Discard")
                    }
                }
            }
        )
    }
    
    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Column {
                        Text(notecardName, maxLines = 1)
                        Text(
                            text = when {
                                hasUnsavedChanges -> "Unsaved changes"
                                isReadOnly -> "Read Only"
                                else -> "Editing"
                            },
                            style = MaterialTheme.typography.bodySmall,
                            color = if (hasUnsavedChanges) 
                                Color(0xFFFF9800) 
                            else 
                                MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                },
                navigationIcon = {
                    IconButton(onClick = { handleBack() }) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
                    }
                },
                actions = {
                    // Copy button
                    IconButton(onClick = {
                        clipboardManager.setText(AnnotatedString(notecardContent))
                        scope.launch {
                            snackbarHostState.showSnackbar("Notecard copied to clipboard")
                        }
                    }) {
                        Icon(Icons.Default.ContentCopy, contentDescription = "Copy")
                    }
                    
                    // Save button (only for editable notecards)
                    if (!isReadOnly && itemId != null) {
                        IconButton(
                            onClick = {
                                scope.launch {
                                    val success = onSaveNotecard(itemId, notecardContent)
                                    if (success) {
                                        originalContent = notecardContent
                                        hasUnsavedChanges = false
                                        snackbarHostState.showSnackbar("Notecard saved")
                                    } else {
                                        snackbarHostState.showSnackbar("Failed to save notecard")
                                    }
                                }
                            },
                            enabled = hasUnsavedChanges
                        ) {
                            Icon(
                                Icons.Default.Save,
                                contentDescription = "Save",
                                tint = if (hasUnsavedChanges) 
                                    MaterialTheme.colorScheme.primary 
                                else 
                                    MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = Color(0xFF252526),
                    titleContentColor = Color.White
                )
            )
        },
        snackbarHost = { SnackbarHost(snackbarHostState) },
        containerColor = Color(0xFF1E1E1E),
        modifier = modifier
    ) { paddingValues ->
        if (isLoading) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues),
                contentAlignment = Alignment.Center
            ) {
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    CircularProgressIndicator()
                    Text("Loading notecard...", color = Color.White)
                }
            }
        } else {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues)
            ) {
                // Embedded items section (if any)
                if (embeddedItems.isNotEmpty()) {
                    EmbeddedItemsSection(
                        items = embeddedItems,
                        expanded = showEmbeddedItems,
                        onToggle = { showEmbeddedItems = !showEmbeddedItems }
                    )
                    Divider(color = Color(0xFF3C3C3C))
                }
                
                // Content editor
                NotecardContent(
                    content = notecardContent,
                    isReadOnly = isReadOnly,
                    onContentChange = { notecardContent = it },
                    modifier = Modifier
                        .weight(1f)
                        .fillMaxWidth()
                )
            }
        }
    }
}

/**
 * Collapsible section showing embedded items in the notecard.
 */
@Composable
private fun EmbeddedItemsSection(
    items: List<EmbeddedItem>,
    expanded: Boolean,
    onToggle: () -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .fillMaxWidth()
            .background(Color(0xFF2D2D2D))
            .clickable { onToggle() }
            .padding(12.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(
                    Icons.Default.AttachFile,
                    contentDescription = null,
                    tint = Color(0xFFB0B0B0),
                    modifier = Modifier.size(16.dp)
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = "Embedded Items (${items.size})",
                    style = MaterialTheme.typography.bodyMedium,
                    color = Color.White
                )
            }
            Icon(
                if (expanded) Icons.Default.ExpandLess else Icons.Default.ExpandMore,
                contentDescription = if (expanded) "Collapse" else "Expand",
                tint = Color(0xFFB0B0B0)
            )
        }
        
        if (expanded) {
            Spacer(modifier = Modifier.height(8.dp))
            items.forEach { item ->
                EmbeddedItemRow(item = item)
            }
        }
    }
}

/**
 * Single embedded item row.
 */
@Composable
private fun EmbeddedItemRow(
    item: EmbeddedItem,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp, horizontal = 8.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = "•",
            color = Color(0xFF808080),
            modifier = Modifier.padding(end = 8.dp)
        )
        Text(
            text = item.name,
            style = MaterialTheme.typography.bodySmall,
            color = Color(0xFFB0B0B0)
        )
        Spacer(modifier = Modifier.width(8.dp))
        Text(
            text = "(${getItemTypeName(item.type)})",
            style = MaterialTheme.typography.bodySmall,
            color = Color(0xFF808080)
        )
    }
}

/**
 * Notecard text content editor.
 */
@Composable
private fun NotecardContent(
    content: String,
    isReadOnly: Boolean,
    onContentChange: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    val verticalScrollState = rememberScrollState()
    val horizontalScrollState = rememberScrollState()
    
    Box(
        modifier = modifier
            .background(Color(0xFF1E1E1E))
            .verticalScroll(verticalScrollState)
            .horizontalScroll(horizontalScrollState)
            .padding(16.dp)
    ) {
        if (isReadOnly) {
            Text(
                text = content,
                style = TextStyle(
                    fontFamily = FontFamily.Default,
                    fontSize = 14.sp,
                    color = Color(0xFFD4D4D4)
                )
            )
        } else {
            BasicTextField(
                value = content,
                onValueChange = onContentChange,
                textStyle = TextStyle(
                    fontFamily = FontFamily.Default,
                    fontSize = 14.sp,
                    color = Color(0xFFD4D4D4)
                ),
                cursorBrush = SolidColor(Color.White),
                modifier = Modifier.fillMaxSize()
            )
        }
    }
}

/**
 * Convert inventory type number to human-readable name.
 */
private fun getItemTypeName(type: Int): String {
    return when (type) {
        0 -> "Texture"
        1 -> "Sound"
        2 -> "Calling Card"
        3 -> "Landmark"
        5 -> "Clothing"
        6 -> "Object"
        7 -> "Notecard"
        10 -> "Script"
        13 -> "Body Part"
        20 -> "Animation"
        21 -> "Gesture"
        49 -> "Mesh"
        56 -> "Settings"
        57 -> "Material"
        else -> "Item"
    }
}

/**
 * Preview for the notecard editor screen.
 */
@Composable
fun NotecardEditorScreenPreview() {
    NotecardEditorScreen(
        notecardName = "Test Notecard",
        assetId = null,
        itemId = null,
        isReadOnly = false,
        onLoadNotecard = { null },
        onSaveNotecard = { _, _ -> true },
        onNavigateBack = {}
    )
}
