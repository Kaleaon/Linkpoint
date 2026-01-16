package com.linkpoint.ui.groups

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
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.ExitToApp
import androidx.compose.material.icons.automirrored.filled.Message
import androidx.compose.material.icons.filled.Group
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRow
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import java.util.UUID

/**
 * Group data for display
 */
data class GroupData(
    val id: UUID,
    val name: String,
    val memberCount: Int,
    val isActive: Boolean = false,  // Active group tag
    val charter: String = "",
    val isOpen: Boolean = true,
    val contribution: Int = 0
)

/**
 * Compose version of GroupsActivity.
 * 
 * Features:
 * - My Groups list
 * - Group search tab
 * - Active group indicator
 * - Group chat, info, leave actions
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun GroupsScreen(
    myGroups: List<GroupData>,
    searchResults: List<GroupData> = emptyList(),
    onNavigateBack: () -> Unit,
    onOpenGroupChat: (GroupData) -> Unit,
    onViewGroupInfo: (GroupData) -> Unit,
    onSetActiveGroup: (GroupData) -> Unit,
    onLeaveGroup: (GroupData) -> Unit,
    onJoinGroup: (GroupData) -> Unit,
    onSearch: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    var selectedTab by remember { mutableIntStateOf(0) }
    val tabs = listOf("My Groups", "Search")
    
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Groups") },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
                    }
                }
            )
        }
    ) { paddingValues ->
        Column(
            modifier = modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {
            TabRow(selectedTabIndex = selectedTab) {
                tabs.forEachIndexed { index, title ->
                    Tab(
                        selected = selectedTab == index,
                        onClick = { selectedTab = index },
                        text = { Text(title) },
                        icon = {
                            Icon(
                                imageVector = if (index == 0) Icons.Default.Group else Icons.Default.Search,
                                contentDescription = null
                            )
                        }
                    )
                }
            }
            
            when (selectedTab) {
                0 -> MyGroupsList(
                    groups = myGroups,
                    onOpenChat = onOpenGroupChat,
                    onViewInfo = onViewGroupInfo,
                    onSetActive = onSetActiveGroup,
                    onLeave = onLeaveGroup
                )
                1 -> GroupSearchTab(
                    searchResults = searchResults,
                    onSearch = onSearch,
                    onJoinGroup = onJoinGroup,
                    onViewInfo = onViewGroupInfo
                )
            }
        }
    }
}

/**
 * My groups list
 */
@Composable
private fun MyGroupsList(
    groups: List<GroupData>,
    onOpenChat: (GroupData) -> Unit,
    onViewInfo: (GroupData) -> Unit,
    onSetActive: (GroupData) -> Unit,
    onLeave: (GroupData) -> Unit,
    modifier: Modifier = Modifier
) {
    if (groups.isEmpty()) {
        Box(
            modifier = modifier.fillMaxSize(),
            contentAlignment = Alignment.Center
        ) {
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Icon(
                    imageVector = Icons.Default.Group,
                    contentDescription = null,
                    modifier = Modifier.size(64.dp),
                    tint = MaterialTheme.colorScheme.onSurfaceVariant
                )
                Text(
                    text = "You haven't joined any groups yet",
                    style = MaterialTheme.typography.bodyLarge,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }
    } else {
        LazyColumn(
            modifier = modifier.fillMaxSize(),
            contentPadding = PaddingValues(16.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            items(groups, key = { it.id }) { group ->
                GroupCard(
                    group = group,
                    showJoinButton = false,
                    onOpenChat = { onOpenChat(group) },
                    onViewInfo = { onViewInfo(group) },
                    onSetActive = { onSetActive(group) },
                    onLeave = { onLeave(group) }
                )
            }
        }
    }
}

/**
 * Group search tab
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun GroupSearchTab(
    searchResults: List<GroupData>,
    onSearch: (String) -> Unit,
    onJoinGroup: (GroupData) -> Unit,
    onViewInfo: (GroupData) -> Unit,
    modifier: Modifier = Modifier
) {
    var searchQuery by remember { mutableStateOf("") }
    
    Column(modifier = modifier.fillMaxSize()) {
        // Search input
        androidx.compose.material3.OutlinedTextField(
            value = searchQuery,
            onValueChange = { 
                searchQuery = it
                if (it.length >= 3) {
                    onSearch(it)
                }
            },
            label = { Text("Search Groups") },
            leadingIcon = { Icon(Icons.Default.Search, contentDescription = null) },
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            singleLine = true
        )
        
        if (searchResults.isEmpty()) {
            Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = if (searchQuery.length < 3) "Enter at least 3 characters to search"
                           else "No groups found",
                    style = MaterialTheme.typography.bodyLarge,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        } else {
            LazyColumn(
                modifier = Modifier.fillMaxSize(),
                contentPadding = PaddingValues(16.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                items(searchResults, key = { it.id }) { group ->
                    GroupCard(
                        group = group,
                        showJoinButton = true,
                        onJoin = { onJoinGroup(group) },
                        onViewInfo = { onViewInfo(group) }
                    )
                }
            }
        }
    }
}

/**
 * Individual group card
 */
@Composable
fun GroupCard(
    group: GroupData,
    showJoinButton: Boolean = false,
    onOpenChat: (() -> Unit)? = null,
    onViewInfo: () -> Unit,
    onSetActive: (() -> Unit)? = null,
    onLeave: (() -> Unit)? = null,
    onJoin: (() -> Unit)? = null,
    modifier: Modifier = Modifier
) {
    var showMenu by remember { mutableStateOf(false) }
    
    Card(
        modifier = modifier
            .fillMaxWidth()
            .clickable(onClick = onViewInfo),
        colors = CardDefaults.cardColors(
            containerColor = if (group.isActive) {
                MaterialTheme.colorScheme.primaryContainer
            } else {
                MaterialTheme.colorScheme.surface
            }
        )
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Group icon placeholder
            Box(
                modifier = Modifier
                    .size(48.dp)
                    .clip(RoundedCornerShape(8.dp)),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.Default.Group,
                    contentDescription = null,
                    modifier = Modifier.size(32.dp),
                    tint = if (group.isActive) {
                        MaterialTheme.colorScheme.onPrimaryContainer
                    } else {
                        MaterialTheme.colorScheme.onSurfaceVariant
                    }
                )
            }
            
            Spacer(modifier = Modifier.width(12.dp))
            
            Column(modifier = Modifier.weight(1f)) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(
                        text = group.name,
                        style = MaterialTheme.typography.bodyLarge,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                    if (group.isActive) {
                        Spacer(modifier = Modifier.width(4.dp))
                        Icon(
                            imageVector = Icons.Default.Star,
                            contentDescription = "Active",
                            modifier = Modifier.size(16.dp),
                            tint = MaterialTheme.colorScheme.primary
                        )
                    }
                }
                Text(
                    text = "${group.memberCount} members",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
            
            // Actions
            if (showJoinButton && onJoin != null && group.isOpen) {
                androidx.compose.material3.TextButton(onClick = onJoin) {
                    Text("Join")
                }
            } else if (!showJoinButton) {
                if (onOpenChat != null) {
                    IconButton(onClick = onOpenChat) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.Message,
                            contentDescription = "Group Chat"
                        )
                    }
                }
                
                Box {
                    IconButton(onClick = { showMenu = true }) {
                        Icon(Icons.Default.MoreVert, contentDescription = "More options")
                    }
                    
                    DropdownMenu(
                        expanded = showMenu,
                        onDismissRequest = { showMenu = false }
                    ) {
                        DropdownMenuItem(
                            text = { Text("Group Info") },
                            leadingIcon = { Icon(Icons.Default.Info, contentDescription = null) },
                            onClick = {
                                showMenu = false
                                onViewInfo()
                            }
                        )
                        if (onSetActive != null && !group.isActive) {
                            DropdownMenuItem(
                                text = { Text("Set as Active") },
                                leadingIcon = { Icon(Icons.Default.Star, contentDescription = null) },
                                onClick = {
                                    showMenu = false
                                    onSetActive()
                                }
                            )
                        }
                        if (onLeave != null) {
                            DropdownMenuItem(
                                text = { Text("Leave Group") },
                                leadingIcon = { Icon(Icons.AutoMirrored.Filled.ExitToApp, contentDescription = null) },
                                onClick = {
                                    showMenu = false
                                    onLeave()
                                }
                            )
                        }
                    }
                }
            }
        }
    }
}
