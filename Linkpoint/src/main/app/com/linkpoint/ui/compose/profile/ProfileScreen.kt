package com.linkpoint.ui.compose.profile

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.linkpoint.ui.compose.components.*

/**
 * Profile Screen
 * 
 * Displays user profile information including:
 * - Avatar and basic info
 * - Statistics
 * - Recent activity
 * - Profile settings
 */

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProfileScreen(
    userId: Long,
    onNavigateBack: () -> Unit,
    onEditProfile: () -> Unit = {},
    modifier: Modifier = Modifier
) {
    // Mock data - replace with actual data from ViewModel
    var username by remember { mutableStateOf("TestUser") }
    var displayName by remember { mutableStateOf("Test User") }
    var bio by remember { mutableStateOf("Second Life enthusiast") }
    var joinDate by remember { mutableStateOf("January 2024") }
    var onlineStatus by remember { mutableStateOf(true) }
    
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Profile") },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(
                            imageVector = Icons.Default.ArrowBack,
                            contentDescription = "Back"
                        )
                    }
                },
                actions = {
                    IconButton(onClick = onEditProfile) {
                        Icon(
                            imageVector = Icons.Default.Edit,
                            contentDescription = "Edit Profile"
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.surface
                )
            )
        }
    ) { paddingValues ->
        LazyColumn(
            modifier = modifier
                .fillMaxSize()
                .padding(paddingValues),
            contentPadding = PaddingValues(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // Profile Header
            item {
                ProfileHeader(
                    displayName = displayName,
                    username = username,
                    bio = bio,
                    onlineStatus = onlineStatus
                )
            }
            
            // Statistics
            item {
                ProfileStatistics(
                    friendsCount = 42,
                    groupsCount = 8,
                    placesVisited = 156
                )
            }
            
            // Account Info
            item {
                AccountInfoSection(
                    joinDate = joinDate,
                    userId = userId
                )
            }
            
            // Recent Activity
            item {
                RecentActivitySection()
            }
            
            // Actions
            item {
                ProfileActions(
                    onSendMessage = { /* TODO */ },
                    onAddFriend = { /* TODO */ },
                    onBlock = { /* TODO */ }
                )
            }
        }
    }
}

/**
 * Profile Header
 */
@Composable
private fun ProfileHeader(
    displayName: String,
    username: String,
    bio: String,
    onlineStatus: Boolean,
    modifier: Modifier = Modifier
) {
    LinkpointCard(modifier = modifier) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            // Avatar
            Box {
                LinkpointAvatar(
                    name = displayName,
                    size = 80.dp
                )
                
                // Online status indicator
                if (onlineStatus) {
                    Surface(
                        modifier = Modifier
                            .size(20.dp)
                            .align(Alignment.BottomEnd),
                        shape = MaterialTheme.shapes.small,
                        color = MaterialTheme.colorScheme.primary
                    ) {
                        Icon(
                            imageVector = Icons.Default.Check,
                            contentDescription = "Online",
                            modifier = Modifier.padding(4.dp),
                            tint = MaterialTheme.colorScheme.onPrimary
                        )
                    }
                }
            }
            
            // Display name
            Text(
                text = displayName,
                style = MaterialTheme.typography.headlineSmall,
                color = MaterialTheme.colorScheme.onSurface
            )
            
            // Username
            Text(
                text = "@$username",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
            
            // Bio
            if (bio.isNotEmpty()) {
                Text(
                    text = bio,
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurface,
                    textAlign = TextAlign.Center
                )
            }
            
            // Status badge
            StatusBadge(
                text = if (onlineStatus) "Online" else "Offline",
                color = if (onlineStatus) {
                    MaterialTheme.colorScheme.primary
                } else {
                    MaterialTheme.colorScheme.onSurfaceVariant
                }
            )
        }
    }
}

/**
 * Profile Statistics
 */
@Composable
private fun ProfileStatistics(
    friendsCount: Int,
    groupsCount: Int,
    placesVisited: Int,
    modifier: Modifier = Modifier
) {
    LinkpointCard(modifier = modifier) {
        Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
            Text(
                text = "Statistics",
                style = MaterialTheme.typography.titleMedium,
                color = MaterialTheme.colorScheme.primary
            )
            
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceEvenly
            ) {
                StatisticItem(
                    label = "Friends",
                    value = friendsCount.toString(),
                    icon = Icons.Default.Person
                )
                
                StatisticItem(
                    label = "Groups",
                    value = groupsCount.toString(),
                    icon = Icons.Default.Star
                )
                
                StatisticItem(
                    label = "Places",
                    value = placesVisited.toString(),
                    icon = Icons.Default.Place
                )
            }
        }
    }
}

/**
 * Statistic Item
 */
@Composable
private fun StatisticItem(
    label: String,
    value: String,
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier,
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(4.dp)
    ) {
        Icon(
            imageVector = icon,
            contentDescription = null,
            tint = MaterialTheme.colorScheme.primary
        )
        
        Text(
            text = value,
            style = MaterialTheme.typography.titleLarge,
            color = MaterialTheme.colorScheme.onSurface
        )
        
        Text(
            text = label,
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
    }
}

/**
 * Account Info Section
 */
@Composable
private fun AccountInfoSection(
    joinDate: String,
    userId: Long,
    modifier: Modifier = Modifier
) {
    LinkpointCard(modifier = modifier) {
        Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
            Text(
                text = "Account Information",
                style = MaterialTheme.typography.titleMedium,
                color = MaterialTheme.colorScheme.primary
            )
            
            InfoRow(
                label = "User ID",
                value = userId.toString(),
                icon = Icons.Default.Info
            )
            
            InfoRow(
                label = "Member Since",
                value = joinDate,
                icon = Icons.Default.DateRange
            )
            
            InfoRow(
                label = "Account Type",
                value = "Premium",
                icon = Icons.Default.Star
            )
        }
    }
}

/**
 * Info Row
 */
@Composable
private fun InfoRow(
    label: String,
    value: String,
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(12.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(
            imageVector = icon,
            contentDescription = null,
            tint = MaterialTheme.colorScheme.onSurfaceVariant
        )
        
        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = label,
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
            
            Text(
                text = value,
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurface
            )
        }
    }
}

/**
 * Recent Activity Section
 */
@Composable
private fun RecentActivitySection(
    modifier: Modifier = Modifier
) {
    LinkpointCard(modifier = modifier) {
        Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
            Text(
                text = "Recent Activity",
                style = MaterialTheme.typography.titleMedium,
                color = MaterialTheme.colorScheme.primary
            )
            
            ActivityItem(
                title = "Visited Sandbox",
                time = "2 hours ago",
                icon = Icons.Default.Place
            )
            
            HorizontalDivider(modifier = Modifier.padding(vertical = 4.dp))
            
            ActivityItem(
                title = "Joined Group: Builders",
                time = "1 day ago",
                icon = Icons.Default.Star
            )
            
            HorizontalDivider(modifier = Modifier.padding(vertical = 4.dp))
            
            ActivityItem(
                title = "Added Friend: JohnDoe",
                time = "3 days ago",
                icon = Icons.Default.Person
            )
        }
    }
}

/**
 * Activity Item
 */
@Composable
private fun ActivityItem(
    title: String,
    time: String,
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(12.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(
            imageVector = icon,
            contentDescription = null,
            tint = MaterialTheme.colorScheme.primary
        )
        
        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = title,
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurface
            )
            
            Text(
                text = time,
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}

/**
 * Profile Actions
 */
@Composable
private fun ProfileActions(
    onSendMessage: () -> Unit,
    onAddFriend: () -> Unit,
    onBlock: () -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        PrimaryButton(
            text = "Send Message",
            onClick = onSendMessage,
            icon = Icons.Default.Email,
            modifier = Modifier.fillMaxWidth()
        )
        
        SecondaryButton(
            text = "Add Friend",
            onClick = onAddFriend,
            icon = Icons.Default.Person,
            modifier = Modifier.fillMaxWidth()
        )
        
        LinkpointTextButton(
            text = "Block User",
            onClick = onBlock,
            icon = Icons.Default.Close,
            modifier = Modifier.fillMaxWidth()
        )
    }
}