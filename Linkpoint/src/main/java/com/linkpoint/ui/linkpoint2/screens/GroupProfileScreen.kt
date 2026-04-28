package com.linkpoint.ui.linkpoint2.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Group
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material.icons.filled.PrivacyTip
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SecondaryTabRow
import androidx.compose.material3.Tab
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.linkpoint.ui.components.linkpoint2.primitives.AvatarSize
import com.linkpoint.ui.components.linkpoint2.primitives.L2Avatar
import com.linkpoint.ui.components.linkpoint2.primitives.L2Chip
import com.linkpoint.ui.components.linkpoint2.primitives.L2ChipVariant
import com.linkpoint.ui.components.linkpoint2.primitives.L2Row
import com.linkpoint.ui.components.linkpoint2.primitives.L2TopBar
import com.linkpoint.ui.components.linkpoint2.tokens.Linkpoint2

data class GroupMember(
    val id: String,
    val name: String,
    val role: String,
    val online: Boolean,
)

/**
 * Cluster E — Group profile. See design/screens-extra.jsx → GroupProfileScreen.
 */
@Composable
@OptIn(ExperimentalMaterial3Api::class)
fun GroupProfileScreen(
    groupTag: String,
    groupName: String,
    description: String,
    membersOnline: Int,
    membersTotal: Int,
    members: List<GroupMember>,
    role: String,
    onBack: () -> Unit,
    modifier: Modifier = Modifier,
) {
    var tab by remember { mutableIntStateOf(0) }
    val tokens = Linkpoint2.tokens
    val tabs = listOf("Members", "Roles", "Notices", "Land")

    Scaffold(
        topBar = {
            L2TopBar(
                title = groupName,
                subtitle = "$membersOnline online · $membersTotal members",
                leading = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
                    }
                },
            )
        },
    ) { padding ->
        Column(
            modifier = modifier
                .fillMaxSize()
                .padding(padding),
        ) {
            // Cover gradient
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(140.dp)
                    .background(
                        Brush.linearGradient(
                            colors = listOf(
                                MaterialTheme.colorScheme.primary,
                                MaterialTheme.colorScheme.secondary,
                            ),
                        ),
                    ),
            )
            // Tag avatar + name
            Row(
                modifier = Modifier
                    .padding(start = 16.dp, end = 16.dp)
                    .offset(y = (-32).dp),
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Box(
                    modifier = Modifier
                        .size(72.dp)
                        .clip(RoundedCornerShape(tokens.radii.lg))
                        .background(MaterialTheme.colorScheme.surface)
                        .padding(8.dp),
                    contentAlignment = Alignment.Center,
                ) {
                    Text(
                        text = groupTag,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.primary,
                        style = MaterialTheme.typography.titleLarge,
                    )
                }
                Spacer(Modifier.size(12.dp))
                Column {
                    Text(groupName, fontWeight = FontWeight.Bold, style = MaterialTheme.typography.titleLarge)
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        L2Chip(label = role, variant = L2ChipVariant.Primary)
                    }
                }
            }
            Text(
                description,
                style = MaterialTheme.typography.bodyMedium,
                color = tokens.onSurfaceDim,
                modifier = Modifier.padding(horizontal = 16.dp),
            )
            Spacer(Modifier.height(8.dp))

            SecondaryTabRow(selectedTabIndex = tab) {
                tabs.forEachIndexed { i, label ->
                    Tab(selected = tab == i, onClick = { tab = i }, text = { Text(label) })
                }
            }

            when (tab) {
                0 -> LazyColumn(modifier = Modifier.fillMaxSize()) {
                    items(members, key = { it.id }) { m ->
                        L2Row(
                            headline = m.name,
                            supporting = m.role,
                            leading = { L2Avatar(name = m.name, size = AvatarSize.MD, online = m.online) },
                        )
                    }
                }
                1 -> EmptyStatePattern(
                    icon = Icons.Default.PrivacyTip,
                    title = "Roles",
                    body = "Officer, Member, Initiate — and 4 more.",
                )
                2 -> EmptyStatePattern(
                    icon = Icons.Default.Notifications,
                    title = "Notices",
                    body = "No notices in the past 14 days.",
                )
                else -> EmptyStatePattern(
                    icon = Icons.Default.LocationOn,
                    title = "Land",
                    body = "Group owns 2 parcels totalling 8,192 sq m.",
                )
            }
        }
    }
}

