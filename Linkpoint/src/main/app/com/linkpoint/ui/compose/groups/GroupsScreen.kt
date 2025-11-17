package com.linkpoint.ui.compose.groups

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.linkpoint.ui.compose.components.*
import com.linkpoint.ui.compose.theme.LinkpointTheme

/**
 * Groups screen for managing Second Life groups
 * 
 * Features:
 * - List of joined groups
 * - Group search and discovery
 * - Group chat access
 * - Group notices
 * - Group information display
 * - Leave/join group actions
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun GroupsScreen(
    onNavigateBack: () -> Unit = {},
    onGroupClick: (String) -> Unit = {},
    onGroupChatClick: (String) -> Unit = {},
    onSearchClick: () -> Unit = {}
) {
    var searchQuery by remember { mutableStateOf("") }
    var selectedTab by remember { mutableStateOf(0) }
    var showGroupDetails by remember { mutableStateOf(false) }
    var selectedGroup by remember { mutableStateOf<GroupInfo?>(null) }
    
    // Sample groups data (replace with actual data from ViewModel)
    val myGroups = remember {
        listOf(
            GroupInfo(
                id = "group1",
                name = "Builders United",
                memberCount = 1234,
                role = "Member",
                hasUnreadNotices = true,
                isActive = true,
                description = "A community for builders and creators"
            ),
            GroupInfo(
                id = "group2",
                name = "Social Hangout",
                memberCount = 567,
                role = "Officer",
                hasUnreadNotices = false,
                isActive = true,
                description = "Friendly social group for hanging out"
            ),
            GroupInfo(
                id = "group3",
                name = "Merchants Guild",
                memberCount = 890,
                role = "Owner",
                hasUnreadNotices = true,
                isActive = false,
                description = "Group for merchants and shop owners"
            )
        )
    }
    
    LinkpointTheme {
        Scaffold(
            topBar = {
                TopAppBar(
                    title = { Text("Groups") },
                    navigationIcon = {
                        IconButton(onClick = onNavigateBack) {
                            Icon(Icons.Default.ArrowBack, "Back")
                        }
                    },
                    actions = {
                        IconButton(onClick = onSearchClick) {
                            Icon(Icons.Default.Search, "Search Groups")
                        }
                        IconButton(onClick = { /* Show menu */ }) {
                            Icon(Icons.Default.MoreVert, "More options")
                        }
                    }
                )
            }
        ) { paddingValues ->
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues)
            ) {
                // Tabs for different views
                TabRow(selectedTabIndex = selectedTab) {
                    Tab(
                        selected = selectedTab == 0,
                        onClick = { selectedTab = 0 },
                        text = { Text("My Groups") }
                    )
                    Tab(
                        selected = selectedTab == 1,
                        onClick = { selectedTab = 1 },
                        text = { Text("Invites") }
                    )
                    Tab(
                        selected = selectedTab == 2,
                        onClick = { selectedTab = 2 },
                        text = { Text("Discover") }
                    )
                }
                
                // Content based on selected tab
                when (selectedTab) {
                    0 -> MyGroupsList(
                        groups = myGroups,
                        onGroupClick = { group ->
                            selectedGroup = group
                            showGroupDetails = true
                        },
                        onGroupChatClick = { groupId ->
                            onGroupChatClick(groupId)
                        }
                    )
                    1 -> GroupInvitesList()
                    2 -> DiscoverGroupsList(onSearchClick = onSearchClick)
                }
            }
            
            // Group details dialog
            if (showGroupDetails && selectedGroup != null) {
                GroupDetailsDialog(
                    group = selectedGroup!!,
                    onDismiss = { showGroupDetails = false },
                    onOpenChat = { onGroupChatClick(selectedGroup!!.id) },
                    onLeaveGroup = { /* Handle leave */ },
                    onViewNotices = { /* Handle view notices */ }
                )
            }
        }
    }
}

@Composable
private fun MyGroupsList(
    groups: List<GroupInfo>,
    onGroupClick: (GroupInfo) -> Unit,
    onGroupChatClick: (String) -> Unit
) {
    if (groups.isEmpty()) {
        EmptyStateComponent(
            icon = Icons.Default.Group,
            title = "No Groups",
            message = "You haven't joined any groups yet. Discover groups to connect with others!",
            actionText = "Discover Groups",
            onActionClick = { /* Navigate to discover */ }
        )
    } else {
        LazyColumn(
            modifier = Modifier.fillMaxSize(),
            contentPadding = PaddingValues(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            items(groups) { group ->
                GroupCard(
                    group = group,
                    onClick = { onGroupClick(group) },
                    onChatClick = { onGroupChatClick(group.id) }
                )
            }
        }
    }
}

@Composable
private fun GroupCard(
    group: GroupInfo,
    onClick: () -> Unit,
    onChatClick: () -> Unit
) {
    StyledCard(
        onClick = onClick,
        modifier = Modifier.fillMaxWidth()
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Text(
                        text = group.name,
                        style = MaterialTheme.typography.titleMedium,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                    
                    if (group.hasUnreadNotices) {
                        BadgeComponent(
                            text = "New",
                            color = MaterialTheme.colorScheme.error
                        )
                    }
                    
                    if (!group.isActive) {
                        ChipComponent(
                            text = "Inactive",
                            variant = ChipVariant.OUTLINED
                        )
                    }
                }
                
                Spacer(modifier = Modifier.height(4.dp))
                
                Text(
                    text = group.description,
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis
                )
                
                Spacer(modifier = Modifier.height(8.dp))
                
                Row(
                    horizontalArrangement = Arrangement.spacedBy(16.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(
                        horizontalArrangement = Arrangement.spacedBy(4.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            imageVector = Icons.Default.People,
                            contentDescription = null,
                            modifier = Modifier.size(16.dp),
                            tint = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                        Text(
                            text = "${group.memberCount} members",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                    
                    ChipComponent(
                        text = group.role,
                        variant = ChipVariant.FILLED
                    )
                }
            }
            
            IconButton(onClick = onChatClick) {
                Icon(
                    imageVector = Icons.Default.Chat,
                    contentDescription = "Open group chat",
                    tint = MaterialTheme.colorScheme.primary
                )
            }
        }
    }
}

@Composable
private fun GroupInvitesList() {
    EmptyStateComponent(
        icon = Icons.Default.Mail,
        title = "No Invites",
        message = "You don't have any pending group invitations.",
        actionText = null,
        onActionClick = null
    )
}

@Composable
private fun DiscoverGroupsList(onSearchClick: () -> Unit) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Icon(
            imageVector = Icons.Default.Search,
            contentDescription = null,
            modifier = Modifier.size(64.dp),
            tint = MaterialTheme.colorScheme.primary
        )
        
        Spacer(modifier = Modifier.height(16.dp))
        
        Text(
            text = "Discover Groups",
            style = MaterialTheme.typography.headlineSmall
        )
        
        Spacer(modifier = Modifier.height(8.dp))
        
        Text(
            text = "Search for groups to join and connect with people who share your interests.",
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
        
        Spacer(modifier = Modifier.height(24.dp))
        
        PrimaryButton(
            text = "Search Groups",
            onClick = onSearchClick,
            icon = Icons.Default.Search
        )
    }
}

@Composable
private fun GroupDetailsDialog(
    group: GroupInfo,
    onDismiss: () -> Unit,
    onOpenChat: () -> Unit,
    onLeaveGroup: () -> Unit,
    onViewNotices: () -> Unit
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text(group.name) },
        text = {
            Column(
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                Text(
                    text = group.description,
                    style = MaterialTheme.typography.bodyMedium
                )
                
                Divider()
                
                InfoRow(label = "Members", value = group.memberCount.toString())
                InfoRow(label = "Your Role", value = group.role)
                InfoRow(label = "Status", value = if (group.isActive) "Active" else "Inactive")
                
                if (group.hasUnreadNotices) {
                    Divider()
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.Notifications,
                            contentDescription = null,
                            tint = MaterialTheme.colorScheme.error
                        )
                        Text(
                            text = "You have unread notices",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.error
                        )
                    }
                }
            }
        },
        confirmButton = {
            TextButton(onClick = onOpenChat) {
                Text("Open Chat")
            }
        },
        dismissButton = {
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                if (group.hasUnreadNotices) {
                    TextButton(onClick = onViewNotices) {
                        Text("View Notices")
                    }
                }
                TextButton(onClick = onDismiss) {
                    Text("Close")
                }
            }
        }
    )
}

@Composable
private fun InfoRow(label: String, value: String) {
    Row(
        modifier = Modifier.fillMaxWidth(),
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

/**
 * Data class representing group information
 */
data class GroupInfo(
    val id: String,
    val name: String,
    val memberCount: Int,
    val role: String,
    val hasUnreadNotices: Boolean,
    val isActive: Boolean,
    val description: String
)