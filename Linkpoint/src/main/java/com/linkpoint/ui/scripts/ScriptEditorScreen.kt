package com.linkpoint.ui.scripts

import androidx.compose.foundation.background
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.selection.SelectionContainer
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.ContentCopy
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material.icons.filled.Pause
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Save
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
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
import com.linkpoint.scripts.lsl.LSLLanguage
import kotlinx.coroutines.launch
import java.util.UUID

/**
 * Compose-based LSL Script Editor Screen.
 * 
 * Features:
 * - LSL syntax highlighting with complete language support
 * - Line numbers
 * - Read-only mode for viewing scripts
 * - Copy to clipboard
 * - Reset script (for object scripts)
 * - Toggle running state (for object scripts)
 * - Dark theme optimized for code editing
 * 
 * Based on Firestorm/reference viewer script editor design.
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ScriptEditorScreen(
    scriptName: String,
    assetId: UUID?,
    itemId: UUID?,
    objectId: UUID?,
    isReadOnly: Boolean = true,
    onLoadScript: suspend (UUID) -> String?,
    onSaveScript: suspend (itemId: UUID, objectId: UUID?, scriptText: String) -> Pair<Boolean, String?>,
    onResetScript: suspend (UUID, UUID) -> Boolean,
    onToggleRunning: suspend (UUID, UUID) -> Pair<Boolean, Boolean>?, // Returns (success, newState)
    onNavigateBack: () -> Unit,
    modifier: Modifier = Modifier
) {
    val scope = rememberCoroutineScope()
    val snackbarHostState = remember { SnackbarHostState() }
    val clipboardManager = LocalClipboardManager.current
    
    var scriptContent by remember { mutableStateOf("") }
    var highlightedContent by remember { mutableStateOf(AnnotatedString("")) }
    var isLoading by remember { mutableStateOf(true) }
    var isRunning by remember { mutableStateOf(false) }
    var showMenu by remember { mutableStateOf(false) }
    var originalScriptContent by remember { mutableStateOf("") }
    var hasUnsavedChanges by remember { mutableStateOf(false) }
    var showUnsavedDialog by remember { mutableStateOf(false) }

    LaunchedEffect(scriptContent, isReadOnly) {
        hasUnsavedChanges = !isReadOnly && scriptContent != originalScriptContent
    }
    
    // Load script on mount
    LaunchedEffect(assetId) {
        if (assetId != null) {
            isLoading = true
            val content = onLoadScript(assetId)
            if (content != null) {
                scriptContent = content
                highlightedContent = LSLLanguage.highlight(content)
                originalScriptContent = content
            } else {
                scriptContent = "// Failed to load script\n// Asset ID: $assetId"
                highlightedContent = AnnotatedString(scriptContent)
                originalScriptContent = scriptContent
            }
            isLoading = false
        } else {
            scriptContent = LSLLanguage.DEFAULT_SCRIPT
            highlightedContent = LSLLanguage.highlight(scriptContent)
            originalScriptContent = scriptContent
            isLoading = false
        }
    }

    fun handleBack() {
        if (hasUnsavedChanges) {
            showUnsavedDialog = true
        } else {
            onNavigateBack()
        }
    }

    if (showUnsavedDialog) {
        AlertDialog(
            onDismissRequest = { showUnsavedDialog = false },
            title = { Text("Unsaved Changes") },
            text = { Text("You have unsaved script changes. Save before closing?") },
            confirmButton = {
                TextButton(onClick = {
                    showUnsavedDialog = false
                    scope.launch {
                        if (itemId != null) {
                            val (success, message) = onSaveScript(itemId, objectId, scriptContent)
                            if (success) {
                                originalScriptContent = scriptContent
                                hasUnsavedChanges = false
                            } else {
                                snackbarHostState.showSnackbar(message ?: "Failed to save script")
                                return@launch
                            }
                        }
                        onNavigateBack()
                    }
                }) { Text("Save") }
            },
            dismissButton = {
                Row {
                    TextButton(onClick = { showUnsavedDialog = false }) { Text("Cancel") }
                    TextButton(onClick = {
                        showUnsavedDialog = false
                        onNavigateBack()
                    }) { Text("Discard") }
                }
            }
        )
    }
    
    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Column {
                        Text(scriptName, maxLines = 1)
                        Text(
                            text = when {
                                hasUnsavedChanges -> "Unsaved changes"
                                isReadOnly -> "Read Only"
                                else -> "Editing"
                            },
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
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
                        clipboardManager.setText(AnnotatedString(scriptContent))
                        scope.launch {
                            snackbarHostState.showSnackbar("Script copied to clipboard")
                        }
                    }) {
                        Icon(Icons.Default.ContentCopy, contentDescription = "Copy")
                    }
                    
                    if (!isReadOnly && itemId != null) {
                        IconButton(
                            onClick = {
                                scope.launch {
                                    val (success, message) = onSaveScript(itemId, objectId, scriptContent)
                                    if (success) {
                                        originalScriptContent = scriptContent
                                        hasUnsavedChanges = false
                                        snackbarHostState.showSnackbar("Script saved")
                                    } else {
                                        snackbarHostState.showSnackbar(message ?: "Failed to save script")
                                    }
                                }
                            },
                            enabled = hasUnsavedChanges
                        ) {
                            Icon(Icons.Default.Save, contentDescription = "Save")
                        }
                    }

                    // More options menu
                    if (objectId != null && itemId != null) {
                        IconButton(onClick = { showMenu = true }) {
                            Icon(Icons.Default.MoreVert, contentDescription = "More options")
                        }
                        
                        DropdownMenu(
                            expanded = showMenu,
                            onDismissRequest = { showMenu = false }
                        ) {
                            DropdownMenuItem(
                                text = { Text("Reset Script") },
                                leadingIcon = { Icon(Icons.Default.Refresh, contentDescription = null) },
                                onClick = {
                                    showMenu = false
                                    scope.launch {
                                        val success = onResetScript(objectId, itemId)
                                        snackbarHostState.showSnackbar(
                                            if (success) "Script reset" else "Failed to reset script"
                                        )
                                    }
                                }
                            )
                            
                            DropdownMenuItem(
                                text = { Text(if (isRunning) "Stop Script" else "Start Script") },
                                leadingIcon = {
                                    Icon(
                                        if (isRunning) Icons.Default.Pause else Icons.Default.PlayArrow,
                                        contentDescription = null
                                    )
                                },
                                onClick = {
                                    showMenu = false
                                    scope.launch {
                                        val result = onToggleRunning(objectId, itemId)
                                        if (result != null) {
                                            val (success, newState) = result
                                            if (success) {
                                                isRunning = newState
                                                snackbarHostState.showSnackbar(
                                                    if (newState) "Script running" else "Script stopped"
                                                )
                                            }
                                        } else {
                                            snackbarHostState.showSnackbar("Failed to toggle script state")
                                        }
                                    }
                                }
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
        containerColor = LSLLanguage.Colors.BACKGROUND,
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
                    Text("Loading script...", color = Color.White)
                }
            }
        } else {
            LSLCodeEditor(
                code = scriptContent,
                highlightedCode = highlightedContent,
                isReadOnly = isReadOnly,
                onCodeChange = { newCode ->
                    scriptContent = newCode
                    highlightedContent = LSLLanguage.highlight(newCode)
                },
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues)
            )
        }
    }
}

/**
 * Custom LSL Code Editor with line numbers and syntax highlighting.
 */
@Composable
fun LSLCodeEditor(
    code: String,
    highlightedCode: AnnotatedString,
    isReadOnly: Boolean,
    onCodeChange: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    val verticalScrollState = rememberScrollState()
    val horizontalScrollState = rememberScrollState()
    
    val lines = code.split("\n")
    val lineCount = lines.size
    
    Row(
        modifier = modifier
            .background(LSLLanguage.Colors.BACKGROUND)
    ) {
        // Line numbers column
        Column(
            modifier = Modifier
                .verticalScroll(verticalScrollState)
                .background(Color(0xFF2D2D2D))
                .padding(horizontal = 8.dp, vertical = 12.dp)
        ) {
            for (i in 1..lineCount) {
                Text(
                    text = i.toString().padStart(4),
                    style = TextStyle(
                        fontFamily = FontFamily.Monospace,
                        fontSize = 14.sp,
                        color = LSLLanguage.Colors.LINE_NUMBER
                    )
                )
            }
        }
        
        // Code content
        SelectionContainer {
            Box(
                modifier = Modifier
                    .weight(1f)
                    .verticalScroll(verticalScrollState)
                    .horizontalScroll(horizontalScrollState)
                    .padding(12.dp)
            ) {
                if (isReadOnly) {
                    // Read-only: just display highlighted text
                    Text(
                        text = highlightedCode,
                        style = TextStyle(
                            fontFamily = FontFamily.Monospace,
                            fontSize = 14.sp,
                            color = LSLLanguage.Colors.DEFAULT
                        )
                    )
                } else {
                    // Editable: use BasicTextField
                    BasicTextField(
                        value = code,
                        onValueChange = onCodeChange,
                        textStyle = TextStyle(
                            fontFamily = FontFamily.Monospace,
                            fontSize = 14.sp,
                            color = LSLLanguage.Colors.DEFAULT
                        ),
                        cursorBrush = SolidColor(Color.White),
                        decorationBox = { innerTextField ->
                            Box {
                                // Show highlighted text behind
                                Text(
                                    text = highlightedCode,
                                    style = TextStyle(
                                        fontFamily = FontFamily.Monospace,
                                        fontSize = 14.sp,
                                        color = Color.Transparent
                                    )
                                )
                                // Actual editable text field on top
                                innerTextField()
                            }
                        }
                    )
                }
            }
        }
    }
}

/**
 * Preview for the script editor screen.
 */
@Composable
fun ScriptEditorScreenPreview() {
    ScriptEditorScreen(
        scriptName = "Test Script",
        assetId = null,
        itemId = null,
        objectId = null,
        isReadOnly = true,
        onLoadScript = { null },
        onSaveScript = { _, _, _ -> Pair(true, null) },
        onResetScript = { _, _ -> true },
        onToggleRunning = { _, _ -> Pair(true, true) },
        onNavigateBack = {}
    )
}
