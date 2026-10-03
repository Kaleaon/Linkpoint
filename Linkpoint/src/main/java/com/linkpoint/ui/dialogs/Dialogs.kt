package com.linkpoint.ui.dialogs

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import java.util.UUID

/**
 * Friendship offer dialog
 */
@Composable
fun FriendshipOfferDialog(
    fromName: String,
    message: String,
    onAccept: () -> Unit,
    onDecline: () -> Unit,
    onDismiss: () -> Unit
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        icon = { Icon(Icons.Default.Person, contentDescription = null) },
        title = { Text("Friendship Offer") },
        text = {
            Column {
                Text(
                    text = "$fromName wants to be your friend",
                    style = MaterialTheme.typography.bodyLarge
                )
                if (message.isNotBlank()) {
                    Spacer(modifier = Modifier.height(8.dp))
                    Card(
                        colors = CardDefaults.cardColors(
                            containerColor = MaterialTheme.colorScheme.surfaceVariant
                        )
                    ) {
                        Text(
                            text = message,
                            style = MaterialTheme.typography.bodyMedium,
                            modifier = Modifier.padding(12.dp)
                        )
                    }
                }
            }
        },
        confirmButton = {
            Button(onClick = onAccept) {
                Text("Accept")
            }
        },
        dismissButton = {
            TextButton(onClick = onDecline) {
                Text("Decline")
            }
        }
    )
}

/**
 * Teleport offer dialog
 */
@Composable
fun TeleportOfferDialog(
    fromName: String,
    location: String,
    message: String,
    onAccept: () -> Unit,
    onDecline: () -> Unit,
    onDismiss: () -> Unit
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        icon = { Icon(Icons.Default.LocationOn, contentDescription = null) },
        title = { Text("Teleport Offer") },
        text = {
            Column {
                Text(
                    text = "$fromName has offered to teleport you",
                    style = MaterialTheme.typography.bodyLarge
                )
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = "Location: $location",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                if (message.isNotBlank()) {
                    Spacer(modifier = Modifier.height(8.dp))
                    Card(
                        colors = CardDefaults.cardColors(
                            containerColor = MaterialTheme.colorScheme.surfaceVariant
                        )
                    ) {
                        Text(
                            text = message,
                            style = MaterialTheme.typography.bodyMedium,
                            modifier = Modifier.padding(12.dp)
                        )
                    }
                }
            }
        },
        confirmButton = {
            Button(onClick = onAccept) {
                Text("Teleport")
            }
        },
        dismissButton = {
            TextButton(onClick = onDecline) {
                Text("Decline")
            }
        }
    )
}

/**
 * Group invitation dialog
 */
@Composable
fun GroupInviteDialog(
    groupName: String,
    inviterName: String,
    onAccept: () -> Unit,
    onDecline: () -> Unit,
    onDismiss: () -> Unit
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        icon = { Icon(Icons.Default.Star, contentDescription = null) },
        title = { Text("Group Invitation") },
        text = {
            Column {
                Text(
                    text = "$inviterName has invited you to join",
                    style = MaterialTheme.typography.bodyLarge
                )
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = groupName,
                    style = MaterialTheme.typography.titleLarge,
                    color = MaterialTheme.colorScheme.primary
                )
            }
        },
        confirmButton = {
            Button(onClick = onAccept) {
                Text("Join")
            }
        },
        dismissButton = {
            TextButton(onClick = onDecline) {
                Text("Decline")
            }
        }
    )
}

/**
 * Object properties dialog
 */
@Composable
fun ObjectPropertiesDialog(
    objectName: String,
    objectDescription: String,
    ownerName: String,
    creatorName: String,
    onClose: () -> Unit,
    onTakeObject: (() -> Unit)? = null,
    onSitOnObject: (() -> Unit)? = null
) {
    AlertDialog(
        onDismissRequest = onClose,
        title = { Text(objectName) },
        text = {
            Column(
                modifier = Modifier.verticalScroll(rememberScrollState()),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                if (objectDescription.isNotBlank()) {
                    Text(
                        text = objectDescription,
                        style = MaterialTheme.typography.bodyMedium
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                }
                
                InfoRow(label = "Owner", value = ownerName)
                InfoRow(label = "Creator", value = creatorName)
            }
        },
        confirmButton = {
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                onSitOnObject?.let { sit ->
                    OutlinedButton(onClick = sit) {
                        Text("Sit")
                    }
                }
                onTakeObject?.let { take ->
                    OutlinedButton(onClick = take) {
                        Text("Take")
                    }
                }
                Button(onClick = onClose) {
                    Text("Close")
                }
            }
        }
    )
}

/**
 * Item properties dialog (inventory item)
 */
@Composable
fun ItemPropertiesDialog(
    itemName: String,
    itemType: String,
    creatorName: String,
    permissions: String,
    onClose: () -> Unit,
    onWear: (() -> Unit)? = null,
    onRez: (() -> Unit)? = null,
    onDelete: (() -> Unit)? = null
) {
    AlertDialog(
        onDismissRequest = onClose,
        title = { Text(itemName) },
        text = {
            Column(
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                InfoRow(label = "Type", value = itemType)
                InfoRow(label = "Creator", value = creatorName)
                InfoRow(label = "Permissions", value = permissions)
            }
        },
        confirmButton = {
            Column {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    onWear?.let { wear ->
                        OutlinedButton(onClick = wear, modifier = Modifier.weight(1f)) {
                            Text("Wear")
                        }
                    }
                    onRez?.let { rez ->
                        OutlinedButton(onClick = rez, modifier = Modifier.weight(1f)) {
                            Text("Rez")
                        }
                    }
                }
                Spacer(modifier = Modifier.height(8.dp))
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    onDelete?.let { delete ->
                        OutlinedButton(
                            onClick = delete,
                            modifier = Modifier.weight(1f)
                        ) {
                            Text("Delete")
                        }
                    }
                    Button(
                        onClick = onClose,
                        modifier = Modifier.weight(1f)
                    ) {
                        Text("Close")
                    }
                }
            }
        }
    )
}

/**
 * Start location selection dialog
 */
@Composable
fun StartLocationDialog(
    options: List<StartLocationOption>,
    selectedOption: StartLocationOption?,
    onOptionSelected: (StartLocationOption) -> Unit,
    onCustomSLURL: (String) -> Unit,
    onDismiss: () -> Unit
) {
    var customSLURL by remember { mutableStateOf("") }
    var showCustomInput by remember { mutableStateOf(false) }
    
    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Start Location") },
        text = {
            Column(
                modifier = Modifier.verticalScroll(rememberScrollState()),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                options.forEach { option ->
                    Card(
                        onClick = { onOptionSelected(option) },
                        colors = CardDefaults.cardColors(
                            containerColor = if (option == selectedOption) {
                                MaterialTheme.colorScheme.primaryContainer
                            } else {
                                MaterialTheme.colorScheme.surface
                            }
                        ),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Row(
                            modifier = Modifier.padding(16.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(
                                imageVector = Icons.Default.LocationOn,
                                contentDescription = null,
                                tint = MaterialTheme.colorScheme.primary
                            )
                            Spacer(modifier = Modifier.width(12.dp))
                            Column {
                                Text(
                                    text = option.displayName,
                                    style = MaterialTheme.typography.bodyLarge
                                )
                                if (option.description.isNotBlank()) {
                                    Text(
                                        text = option.description,
                                        style = MaterialTheme.typography.bodySmall,
                                        color = MaterialTheme.colorScheme.onSurfaceVariant
                                    )
                                }
                            }
                        }
                    }
                }
                
                // Custom SLURL option
                if (showCustomInput) {
                    OutlinedTextField(
                        value = customSLURL,
                        onValueChange = { customSLURL = it },
                        label = { Text("SLURL") },
                        placeholder = { Text("secondlife://RegionName/128/128/25") },
                        modifier = Modifier.fillMaxWidth(),
                        singleLine = true
                    )
                } else {
                    TextButton(
                        onClick = { showCustomInput = true },
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text("Enter Custom SLURL...")
                    }
                }
            }
        },
        confirmButton = {
            if (showCustomInput && customSLURL.isNotBlank()) {
                Button(onClick = { onCustomSLURL(customSLURL) }) {
                    Text("Use Custom")
                }
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
 * Start location option data
 */
data class StartLocationOption(
    val id: String = UUID.randomUUID().toString(),
    val displayName: String,
    val description: String = "",
    val slurl: String? = null
)

/**
 * Helper composable for info rows
 */
@Composable
private fun InfoRow(
    label: String,
    value: String,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(
            text = label,
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
        Text(
            text = value,
            style = MaterialTheme.typography.bodyMedium
        )
    }
}
