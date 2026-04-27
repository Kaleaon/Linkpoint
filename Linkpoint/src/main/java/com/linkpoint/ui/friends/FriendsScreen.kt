package com.linkpoint.ui.friends

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.Message
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.PersonAdd
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.linkpoint.ui.common.UiLoadState
import com.linkpoint.ui.common.UiTelemetryEvents
import com.linkpoint.ui.common.logUiTelemetry
import com.linkpoint.ui.components.state.EmptyState
import com.linkpoint.ui.components.state.ErrorState
import com.linkpoint.ui.components.state.LoadingState
import com.linkpoint.ui.components.state.ReconnectingBanner
import java.util.UUID

enum class FriendStatus {
    ONLINE,
    OFFLINE,
    AWAY,
    BUSY
}

data class FriendData(
    val id: UUID,
    val name: String,
    val status: FriendStatus,
    val location: String? = null,
    val canSeeOnline: Boolean = true,
    val canSeeMap: Boolean = false,
    val canModifyObjects: Boolean = false
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun FriendsScreen(
    friends: List<FriendData>,
    uiLoadState: UiLoadState = UiLoadState.Content,
    onRetry: () -> Unit,
    onNavigateBack: () -> Unit,
    onOpenIM: (FriendData) -> Unit,
    onTeleportTo: (FriendData) -> Unit,
    onViewProfile: (FriendData) -> Unit,
    onRemoveFriend: (FriendData) -> Unit,
    onAddFriend: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    var showAddDialog by remember { mutableStateOf(false) }
    var addFriendName by remember { mutableStateOf("") }

    val sortedFriends = remember(friends) {
        friends.sortedWith(
            compareBy<FriendData> { it.status != FriendStatus.ONLINE }
                .thenBy { it.name.lowercase() }
        )
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Friends") },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
                    }
                }
            )
        },
        floatingActionButton = {
            FloatingActionButton(onClick = { showAddDialog = true }) {
                Icon(Icons.Default.PersonAdd, contentDescription = "Add Friend")
            }
        }
    ) { paddingValues ->
        Column(
            modifier = modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {
            if (uiLoadState is UiLoadState.Reconnecting) {
                ReconnectingBanner(message = uiLoadState.message)
            }

            when (uiLoadState) {
                is UiLoadState.Loading -> LoadingState(message = uiLoadState.message)
                is UiLoadState.Error -> ErrorState(
                    title = uiLoadState.title,
                    message = uiLoadState.message,
                    retryLabel = uiLoadState.retryLabel,
                    onRetry = {
                        logUiTelemetry(UiTelemetryEvents.RETRY_TAPPED, "friends", uiLoadState)
                        onRetry()
                    }
                )
                is UiLoadState.Empty -> EmptyState(
                    title = uiLoadState.title,
                    message = uiLoadState.message,
                    actionLabel = uiLoadState.actionLabel,
                    onAction = onRetry
                )
                UiLoadState.Content, is UiLoadState.Reconnecting, is UiLoadState.LowBandwidth -> {
                    if (sortedFriends.isEmpty()) {
                        EmptyState(
                            title = "No friends yet",
                            message = "Add a friend to start messaging and teleporting together."
                        )
                    } else {
                        LazyColumn(
                            modifier = Modifier.fillMaxSize(),
                            contentPadding = PaddingValues(16.dp),
                            verticalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            items(sortedFriends, key = { it.id }) { friend ->
                                FriendCard(
                                    friend = friend,
                                    onOpenIM = { onOpenIM(friend) },
                                    onTeleportTo = { onTeleportTo(friend) },
                                    onViewProfile = { onViewProfile(friend) },
                                    onRemove = { onRemoveFriend(friend) }
                                )
                            }
                        }
                    }
                }
            }
        }
    }

    if (showAddDialog) {
        AlertDialog(
            onDismissRequest = {
                showAddDialog = false
                addFriendName = ""
            },
            title = { Text("Add Friend") },
            text = {
                OutlinedTextField(
                    value = addFriendName,
                    onValueChange = { addFriendName = it },
                    label = { Text("Avatar Name") },
                    placeholder = { Text("First Last") },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth()
                )
            },
            confirmButton = {
                TextButton(
                    onClick = {
                        if (addFriendName.isNotBlank()) {
                            onAddFriend(addFriendName)
                            addFriendName = ""
                            showAddDialog = false
                        }
                    },
                    enabled = addFriendName.isNotBlank()
                ) {
                    Text("Send Request")
                }
            },
            dismissButton = {
                TextButton(onClick = {
                    showAddDialog = false
                    addFriendName = ""
                }) {
                    Text("Cancel")
                }
            }
        )
    }
}

@Composable
fun FriendCard(
    friend: FriendData,
    onOpenIM: () -> Unit,
    onTeleportTo: () -> Unit,
    onViewProfile: () -> Unit,
    onRemove: () -> Unit,
    modifier: Modifier = Modifier
) {
    var showMenu by remember { mutableStateOf(false) }

    val statusColor = when (friend.status) {
        FriendStatus.ONLINE -> Color(0xFF4CAF50)
        FriendStatus.AWAY -> Color(0xFFFFC107)
        FriendStatus.BUSY -> Color(0xFFFF5722)
        FriendStatus.OFFLINE -> Color(0xFF9E9E9E)
    }

    Card(
        modifier = modifier
            .fillMaxWidth()
            .clickable(onClick = onViewProfile),
        colors = CardDefaults.cardColors()
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            androidx.compose.foundation.layout.Box(
                modifier = Modifier
                    .size(48.dp)
                    .clip(CircleShape),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.Default.Person,
                    contentDescription = null,
                    modifier = Modifier.size(32.dp),
                    tint = statusColor
                )
            }

            Spacer(modifier = Modifier.width(12.dp))

            Column(modifier = Modifier.weight(1f)) {
                Text(text = friend.name)
                Text(
                    text = when (friend.status) {
                        FriendStatus.ONLINE -> friend.location ?: "Online"
                        FriendStatus.AWAY -> "Away"
                        FriendStatus.BUSY -> "Busy"
                        FriendStatus.OFFLINE -> "Offline"
                    },
                    color = statusColor
                )
            }

            if (friend.status == FriendStatus.ONLINE) {
                IconButton(onClick = onOpenIM) {
                    Icon(Icons.AutoMirrored.Filled.Message, contentDescription = "Message")
                }

                if (friend.canSeeMap) {
                    IconButton(onClick = onTeleportTo) {
                        Icon(Icons.Default.LocationOn, contentDescription = "Teleport")
                    }
                }
            }

            androidx.compose.foundation.layout.Box {
                IconButton(onClick = { showMenu = true }) {
                    Icon(Icons.Default.MoreVert, contentDescription = "More options")
                }

                DropdownMenu(
                    expanded = showMenu,
                    onDismissRequest = { showMenu = false }
                ) {
                    DropdownMenuItem(
                        text = { Text("View Profile") },
                        onClick = {
                            showMenu = false
                            onViewProfile()
                        }
                    )
                    DropdownMenuItem(
                        text = { Text("Send IM") },
                        onClick = {
                            showMenu = false
                            onOpenIM()
                        }
                    )
                    if (friend.canSeeMap && friend.status == FriendStatus.ONLINE) {
                        DropdownMenuItem(
                            text = { Text("Teleport To") },
                            onClick = {
                                showMenu = false
                                onTeleportTo()
                            }
                        )
                    }
                    DropdownMenuItem(
                        text = { Text("Remove Friend") },
                        onClick = {
                            showMenu = false
                            onRemove()
                        }
                    )
                }
            }
        }
    }
}
