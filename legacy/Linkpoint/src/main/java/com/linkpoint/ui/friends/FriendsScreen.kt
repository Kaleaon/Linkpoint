package com.linkpoint.ui.friends

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.Message
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material.icons.filled.PersonAdd
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.linkpoint.ui.common.UiLoadState
import com.linkpoint.ui.common.UiTelemetryEvents
import com.linkpoint.ui.common.logUiTelemetry
import com.linkpoint.ui.components.linkpoint2.primitives.AvatarSize
import com.linkpoint.ui.components.linkpoint2.primitives.L2Avatar
import com.linkpoint.ui.components.linkpoint2.primitives.L2Chip
import com.linkpoint.ui.components.linkpoint2.primitives.L2ChipVariant
import com.linkpoint.ui.components.linkpoint2.primitives.L2Row
import com.linkpoint.ui.components.linkpoint2.primitives.L2SectionHeader
import com.linkpoint.ui.components.linkpoint2.primitives.L2TopBar
import com.linkpoint.ui.components.linkpoint2.tokens.Linkpoint2
import com.linkpoint.ui.components.state.EmptyState
import com.linkpoint.ui.components.state.ErrorState
import com.linkpoint.ui.components.state.LoadingState
import com.linkpoint.ui.components.state.ReconnectingBanner
import java.util.UUID

enum class FriendStatus { ONLINE, OFFLINE, AWAY, BUSY }

data class FriendData(
    val id: UUID,
    val name: String,
    val status: FriendStatus,
    val location: String? = null,
    val canSeeOnline: Boolean = true,
    val canSeeMap: Boolean = false,
    val canModifyObjects: Boolean = false,
)

/**
 * Linkpoint 2.0 friends list — sectioned by online/offline, generative
 * avatars with online indicators, sort/search header. Matches
 * design/screens-2.jsx → FriendsScreen.
 */
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
    modifier: Modifier = Modifier,
) {
    var showAddDialog by remember { mutableStateOf(false) }
    var addFriendName by remember { mutableStateOf("") }
    var query by remember { mutableStateOf("") }

    val tokens = Linkpoint2.tokens
    val sorted = remember(friends, query) {
        friends
            .filter { query.isBlank() || it.name.contains(query, ignoreCase = true) }
            .sortedWith(
                compareBy<FriendData> { it.status != FriendStatus.ONLINE }
                    .thenBy { it.name.lowercase() },
            )
    }
    val online = sorted.filter { it.status == FriendStatus.ONLINE }
    val offline = sorted.filter { it.status != FriendStatus.ONLINE }

    Scaffold(
        topBar = {
            L2TopBar(
                title = "Friends",
                subtitle = "${online.size} online · ${friends.size} total",
                leading = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
                    }
                },
                trailing = {
                    IconButton(onClick = { /* sort */ }) {
                        Icon(Icons.Default.Search, contentDescription = "Search")
                    }
                },
            )
        },
        floatingActionButton = {
            FloatingActionButton(onClick = { showAddDialog = true }) {
                Icon(Icons.Default.PersonAdd, contentDescription = "Add friend")
            }
        },
    ) { padding ->
        Column(
            modifier = modifier
                .fillMaxSize()
                .padding(padding),
        ) {
            if (uiLoadState is UiLoadState.Reconnecting) {
                ReconnectingBanner(message = uiLoadState.message)
            }

            // Search bar
            OutlinedTextField(
                value = query,
                onValueChange = { query = it },
                placeholder = { Text("Search friends…") },
                leadingIcon = { Icon(Icons.Default.Search, contentDescription = null) },
                singleLine = true,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 8.dp),
                shape = androidx.compose.foundation.shape.RoundedCornerShape(tokens.radii.pill),
            )

            when (uiLoadState) {
                is UiLoadState.Loading -> LoadingState(message = uiLoadState.message)
                is UiLoadState.Error -> ErrorState(
                    title = uiLoadState.title,
                    message = uiLoadState.message,
                    retryLabel = uiLoadState.retryLabel,
                    onRetry = {
                        logUiTelemetry(UiTelemetryEvents.RETRY_TAPPED, "friends", uiLoadState)
                        onRetry()
                    },
                )
                is UiLoadState.Empty -> EmptyState(
                    title = uiLoadState.title,
                    message = uiLoadState.message,
                    actionLabel = uiLoadState.actionLabel,
                    onAction = onRetry,
                )
                UiLoadState.Content, is UiLoadState.Reconnecting, is UiLoadState.LowBandwidth -> {
                    if (sorted.isEmpty()) {
                        EmptyState(
                            title = if (query.isBlank()) "No friends yet" else "No matches",
                            message = if (query.isBlank()) "Add a friend to start messaging and teleporting together."
                            else "Try a different name.",
                        )
                    } else {
                        LazyColumn(
                            modifier = Modifier.fillMaxSize(),
                            contentPadding = PaddingValues(bottom = 80.dp),
                        ) {
                            if (online.isNotEmpty()) {
                                item("hdr_online") {
                                    L2SectionHeader("Online · ${online.size}")
                                }
                                items(online, key = { "on_${it.id}" }) { friend ->
                                    FriendL2Row(
                                        friend = friend,
                                        onOpenIM = { onOpenIM(friend) },
                                        onTeleportTo = { onTeleportTo(friend) },
                                        onViewProfile = { onViewProfile(friend) },
                                        onRemove = { onRemoveFriend(friend) },
                                    )
                                }
                            }
                            if (offline.isNotEmpty()) {
                                item("hdr_offline") {
                                    L2SectionHeader("Offline · ${offline.size}")
                                }
                                items(offline, key = { "off_${it.id}" }) { friend ->
                                    FriendL2Row(
                                        friend = friend,
                                        onOpenIM = { onOpenIM(friend) },
                                        onTeleportTo = { onTeleportTo(friend) },
                                        onViewProfile = { onViewProfile(friend) },
                                        onRemove = { onRemoveFriend(friend) },
                                    )
                                }
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
            title = { Text("Add friend") },
            text = {
                OutlinedTextField(
                    value = addFriendName,
                    onValueChange = { addFriendName = it },
                    label = { Text("Avatar name") },
                    placeholder = { Text("First Last") },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth(),
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
                    enabled = addFriendName.isNotBlank(),
                ) { Text("Send request") }
            },
            dismissButton = {
                TextButton(onClick = {
                    showAddDialog = false
                    addFriendName = ""
                }) { Text("Cancel") }
            },
        )
    }
}

@Composable
fun FriendL2Row(
    friend: FriendData,
    onOpenIM: () -> Unit,
    onTeleportTo: () -> Unit,
    onViewProfile: () -> Unit,
    onRemove: () -> Unit,
    modifier: Modifier = Modifier,
) {
    var showMenu by remember { mutableStateOf(false) }
    val statusVariant = when (friend.status) {
        FriendStatus.ONLINE -> L2ChipVariant.Success
        FriendStatus.AWAY -> L2ChipVariant.Warn
        FriendStatus.BUSY -> L2ChipVariant.Error
        FriendStatus.OFFLINE -> L2ChipVariant.Neutral
    }

    L2Row(
        modifier = modifier,
        headline = friend.name,
        supporting = when (friend.status) {
            FriendStatus.ONLINE -> friend.location ?: "Online"
            FriendStatus.AWAY -> "Away"
            FriendStatus.BUSY -> "Busy · do not disturb"
            FriendStatus.OFFLINE -> "Offline"
        },
        leading = {
            L2Avatar(
                name = friend.name,
                size = AvatarSize.MD,
                online = friend.status == FriendStatus.ONLINE,
            )
        },
        trailing = {
            Row(verticalAlignment = Alignment.CenterVertically) {
                if (friend.status != FriendStatus.OFFLINE) {
                    L2Chip(
                        label = friend.status.name.lowercase().replaceFirstChar { it.titlecase() },
                        variant = statusVariant,
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
                Box {
                    IconButton(onClick = { showMenu = true }) {
                        Icon(Icons.Default.MoreVert, contentDescription = "More options")
                    }
                    DropdownMenu(
                        expanded = showMenu,
                        onDismissRequest = { showMenu = false },
                    ) {
                        DropdownMenuItem(
                            text = { Text("View profile") },
                            onClick = {
                                showMenu = false
                                onViewProfile()
                            },
                        )
                        DropdownMenuItem(
                            text = { Text("Send IM") },
                            onClick = {
                                showMenu = false
                                onOpenIM()
                            },
                        )
                        if (friend.canSeeMap && friend.status == FriendStatus.ONLINE) {
                            DropdownMenuItem(
                                text = { Text("Teleport to") },
                                onClick = {
                                    showMenu = false
                                    onTeleportTo()
                                },
                            )
                        }
                        DropdownMenuItem(
                            text = { Text("Remove friend") },
                            onClick = {
                                showMenu = false
                                onRemove()
                            },
                        )
                    }
                }
            }
        },
        onClick = onViewProfile,
    )
}

/**
 * Backwards-compatible alias for older call sites.
 */
@Composable
fun FriendCard(
    friend: FriendData,
    onOpenIM: () -> Unit,
    onTeleportTo: () -> Unit,
    onViewProfile: () -> Unit,
    onRemove: () -> Unit,
    modifier: Modifier = Modifier,
) {
    FriendL2Row(
        friend = friend,
        onOpenIM = onOpenIM,
        onTeleportTo = onTeleportTo,
        onViewProfile = onViewProfile,
        onRemove = onRemove,
        modifier = modifier,
    )
}
