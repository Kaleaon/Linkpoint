package com.lumiyaviewer.lumiya.ui.compose.common

import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Error
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp

/**
 * Common Dialog Components for Linkpoint
 * 
 * Provides reusable dialog components following Material Design 3 guidelines:
 * - Confirmation dialogs
 * - Error dialogs
 * - Info dialogs
 * - Progress dialogs
 * - Custom dialogs
 */

/**
 * Confirmation Dialog
 * 
 * Standard confirmation dialog with title, message, and confirm/cancel buttons.
 * 
 * @param title Dialog title
 * @param message Dialog message
 * @param confirmText Text for confirm button (default: "Confirm")
 * @param cancelText Text for cancel button (default: "Cancel")
 * @param onConfirm Callback when user confirms
 * @param onDismiss Callback when user dismisses
 * @param icon Optional icon to display
 */
@Composable
fun ConfirmationDialog(
    title: String,
    message: String,
    confirmText: String = "Confirm",
    cancelText: String = "Cancel",
    onConfirm: () -> Unit,
    onDismiss: () -> Unit,
    icon: ImageVector? = null
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        icon = icon?.let {
            {
                Icon(
                    imageVector = it,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.primary
                )
            }
        },
        title = {
            Text(
                text = title,
                style = MaterialTheme.typography.headlineSmall
            )
        },
        text = {
            Text(
                text = message,
                style = MaterialTheme.typography.bodyMedium
            )
        },
        confirmButton = {
            Button(onClick = {
                onConfirm()
                onDismiss()
            }) {
                Text(confirmText)
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text(cancelText)
            }
        }
    )
}

/**
 * Error Dialog
 * 
 * Displays an error message with an error icon.
 * 
 * @param title Error title
 * @param message Error message
 * @param onDismiss Callback when user dismisses
 * @param actionText Optional action button text
 * @param onAction Optional action callback
 */
@Composable
fun ErrorDialog(
    title: String,
    message: String,
    onDismiss: () -> Unit,
    actionText: String? = null,
    onAction: (() -> Unit)? = null
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        icon = {
            Icon(
                imageVector = Icons.Default.Error,
                contentDescription = null,
                tint = MaterialTheme.colorScheme.error
            )
        },
        title = {
            Text(
                text = title,
                style = MaterialTheme.typography.headlineSmall,
                color = MaterialTheme.colorScheme.error
            )
        },
        text = {
            Text(
                text = message,
                style = MaterialTheme.typography.bodyMedium
            )
        },
        confirmButton = {
            if (actionText != null && onAction != null) {
                Button(
                    onClick = {
                        onAction()
                        onDismiss()
                    },
                    colors = ButtonDefaults.buttonColors(
                        containerColor = MaterialTheme.colorScheme.error
                    )
                ) {
                    Text(actionText)
                }
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Close")
            }
        }
    )
}

/**
 * Warning Dialog
 * 
 * Displays a warning message with a warning icon.
 * 
 * @param title Warning title
 * @param message Warning message
 * @param onConfirm Callback when user confirms
 * @param onDismiss Callback when user dismisses
 */
@Composable
fun WarningDialog(
    title: String,
    message: String,
    onConfirm: () -> Unit,
    onDismiss: () -> Unit
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        icon = {
            Icon(
                imageVector = Icons.Default.Warning,
                contentDescription = null,
                tint = MaterialTheme.colorScheme.tertiary
            )
        },
        title = {
            Text(
                text = title,
                style = MaterialTheme.typography.headlineSmall
            )
        },
        text = {
            Text(
                text = message,
                style = MaterialTheme.typography.bodyMedium
            )
        },
        confirmButton = {
            Button(onClick = {
                onConfirm()
                onDismiss()
            }) {
                Text("Continue")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Cancel")
            }
        }
    )
}

/**
 * Info Dialog
 * 
 * Displays an informational message with an info icon.
 * 
 * @param title Info title
 * @param message Info message
 * @param onDismiss Callback when user dismisses
 */
@Composable
fun InfoDialog(
    title: String,
    message: String,
    onDismiss: () -> Unit
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        icon = {
            Icon(
                imageVector = Icons.Default.Info,
                contentDescription = null,
                tint = MaterialTheme.colorScheme.primary
            )
        },
        title = {
            Text(
                text = title,
                style = MaterialTheme.typography.headlineSmall
            )
        },
        text = {
            Text(
                text = message,
                style = MaterialTheme.typography.bodyMedium
            )
        },
        confirmButton = {
            TextButton(onClick = onDismiss) {
                Text("OK")
            }
        }
    )
}

/**
 * Progress Dialog
 * 
 * Displays a progress indicator with a message.
 * Cannot be dismissed by the user.
 * 
 * @param title Progress title
 * @param message Progress message
 * @param progress Optional progress value (0.0 to 1.0). If null, shows indeterminate progress.
 */
@Composable
fun ProgressDialog(
    title: String,
    message: String,
    progress: Float? = null
) {
    AlertDialog(
        onDismissRequest = { /* Cannot be dismissed */ },
        title = {
            Text(
                text = title,
                style = MaterialTheme.typography.headlineSmall
            )
        },
        text = {
            Column(
                modifier = Modifier.fillMaxWidth(),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                if (progress != null) {
                    LinearProgressIndicator(
                        progress = progress,
                        modifier = Modifier.fillMaxWidth()
                    )
                    Text(
                        text = "${(progress * 100).toInt()}%",
                        style = MaterialTheme.typography.bodySmall
                    )
                } else {
                    CircularProgressIndicator()
                }
                
                Text(
                    text = message,
                    style = MaterialTheme.typography.bodyMedium,
                    textAlign = TextAlign.Center
                )
            }
        },
        confirmButton = { /* No buttons */ }
    )
}

/**
 * Input Dialog
 * 
 * Dialog with a text input field.
 * 
 * @param title Dialog title
 * @param message Dialog message
 * @param initialValue Initial input value
 * @param placeholder Input placeholder text
 * @param onConfirm Callback with input value when user confirms
 * @param onDismiss Callback when user dismisses
 * @param validator Optional input validator function
 */
@Composable
fun InputDialog(
    title: String,
    message: String,
    initialValue: String = "",
    placeholder: String = "",
    onConfirm: (String) -> Unit,
    onDismiss: () -> Unit,
    validator: ((String) -> Boolean)? = null
) {
    var inputValue by remember { mutableStateOf(initialValue) }
    var isError by remember { mutableStateOf(false) }
    
    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Text(
                text = title,
                style = MaterialTheme.typography.headlineSmall
            )
        },
        text = {
            Column(
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Text(
                    text = message,
                    style = MaterialTheme.typography.bodyMedium
                )
                
                OutlinedTextField(
                    value = inputValue,
                    onValueChange = {
                        inputValue = it
                        isError = validator?.invoke(it) == false
                    },
                    placeholder = { Text(placeholder) },
                    isError = isError,
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth()
                )
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    if (validator == null || validator(inputValue)) {
                        onConfirm(inputValue)
                        onDismiss()
                    } else {
                        isError = true
                    }
                },
                enabled = !isError && inputValue.isNotBlank()
            ) {
                Text("OK")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Cancel")
            }
        }
    )
}

/**
 * List Selection Dialog
 * 
 * Dialog for selecting an item from a list.
 * 
 * @param title Dialog title
 * @param items List of items to display
 * @param selectedItem Currently selected item
 * @param onItemSelected Callback when item is selected
 * @param onDismiss Callback when dialog is dismissed
 * @param itemContent Composable for rendering each item
 */
@Composable
fun <T> ListSelectionDialog(
    title: String,
    items: List<T>,
    selectedItem: T?,
    onItemSelected: (T) -> Unit,
    onDismiss: () -> Unit,
    itemContent: @Composable (T, Boolean) -> Unit
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Text(
                text = title,
                style = MaterialTheme.typography.headlineSmall
            )
        },
        text = {
            Column(
                modifier = Modifier.fillMaxWidth(),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                items.forEach { item ->
                    val isSelected = item == selectedItem
                    Surface(
                        modifier = Modifier.fillMaxWidth(),
                        onClick = {
                            onItemSelected(item)
                            onDismiss()
                        },
                        color = if (isSelected) {
                            MaterialTheme.colorScheme.primaryContainer
                        } else {
                            MaterialTheme.colorScheme.surface
                        }
                    ) {
                        Box(modifier = Modifier.padding(16.dp)) {
                            itemContent(item, isSelected)
                        }
                    }
                }
            }
        },
        confirmButton = {
            TextButton(onClick = onDismiss) {
                Text("Cancel")
            }
        }
    )
}

/**
 * Custom Dialog
 * 
 * Flexible dialog with custom content.
 * 
 * @param title Dialog title
 * @param onDismiss Callback when dialog is dismissed
 * @param confirmText Text for confirm button
 * @param onConfirm Callback when user confirms
 * @param dismissText Text for dismiss button
 * @param content Custom content composable
 */
@Composable
fun CustomDialog(
    title: String,
    onDismiss: () -> Unit,
    confirmText: String = "OK",
    onConfirm: () -> Unit,
    dismissText: String = "Cancel",
    content: @Composable () -> Unit
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Text(
                text = title,
                style = MaterialTheme.typography.headlineSmall
            )
        },
        text = content,
        confirmButton = {
            Button(onClick = {
                onConfirm()
                onDismiss()
            }) {
                Text(confirmText)
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text(dismissText)
            }
        }
    )
}

/**
 * Dialog State Management
 * 
 * Helper class for managing dialog state.
 */
class DialogState {
    var isShowing by mutableStateOf(false)
        private set
    
    fun show() {
        isShowing = true
    }
    
    fun hide() {
        isShowing = false
    }
}

/**
 * Remember dialog state
 */
@Composable
fun rememberDialogState(): DialogState {
    return remember { DialogState() }
}