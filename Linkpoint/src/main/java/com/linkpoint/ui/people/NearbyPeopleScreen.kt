package com.linkpoint.ui.people

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
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
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.PersonAdd
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
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
import com.linkpoint.ui.components.linkpoint2.primitives.L2TopBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.linkpoint.R
import java.util.UUID

/**
 * Nearby person data
 */
data class NearbyPerson(
    val id: UUID,
    val name: String,
    val distance: Float,
    val isFriend: Boolean = false
)

enum class NearbyPeopleFilter {
    ALL,
    FRIENDS,
    STRANGERS
}

/**
 * Compose version of NearbyPeopleActivity.
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun NearbyPeopleScreen(
    people: List<NearbyPerson>,
    selectedFilter: NearbyPeopleFilter,
    onFilterChange: (NearbyPeopleFilter) -> Unit,
    isLoading: Boolean = false,
    emptyMessage: String? = null,
    onRefresh: () -> Unit,
    onSendIM: (NearbyPerson) -> Unit,
    onAddFriend: (NearbyPerson) -> Unit,
    onViewProfile: (NearbyPerson) -> Unit,
    onNavigateBack: () -> Unit,
    modifier: Modifier = Modifier
) {
    val filteredPeople = remember(people, selectedFilter) {
        val filtered = when (selectedFilter) {
            NearbyPeopleFilter.ALL -> people
            NearbyPeopleFilter.FRIENDS -> people.filter { it.isFriend }
            NearbyPeopleFilter.STRANGERS -> people.filterNot { it.isFriend }
        }
        filtered.sortedBy { it.distance }
    }

    Scaffold(
        topBar = {
            L2TopBar(
                title = stringResource(R.string.nearby_people),
                subtitle = "People around you",
                leading = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = stringResource(R.string.back))
                    }
                },
                trailing = {
                    IconButton(onClick = onRefresh, enabled = !isLoading) {
                        if (isLoading) {
                            CircularProgressIndicator(
                                modifier = Modifier.size(24.dp),
                                strokeWidth = 2.dp
                            )
                        } else {
                            Icon(Icons.Default.Refresh, contentDescription = stringResource(R.string.refresh))
                        }
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
            NearbyPeopleTabs(selectedFilter = selectedFilter, onFilterChange = onFilterChange)

            if (filteredPeople.isEmpty() && !isLoading) {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Icon(
                            imageVector = Icons.Default.Person,
                            contentDescription = null,
                            modifier = Modifier.size(64.dp),
                            tint = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                        Text(
                            text = emptyMessage ?: defaultEmptyMessage(selectedFilter),
                            style = MaterialTheme.typography.bodyLarge,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
            } else {
                LazyColumn(
                    modifier = Modifier.fillMaxSize(),
                    contentPadding = PaddingValues(16.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    items(filteredPeople, key = { it.id }) { person ->
                        NearbyPersonCard(
                            person = person,
                            onClick = { onViewProfile(person) },
                            onSendIM = { onSendIM(person) },
                            onAddFriend = { onAddFriend(person) },
                            onViewProfile = { onViewProfile(person) }
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun NearbyPeopleTabs(
    selectedFilter: NearbyPeopleFilter,
    onFilterChange: (NearbyPeopleFilter) -> Unit
) {
    val tabs = listOf(
        NearbyPeopleFilter.ALL to stringResource(R.string.all),
        NearbyPeopleFilter.FRIENDS to stringResource(R.string.friends),
        NearbyPeopleFilter.STRANGERS to stringResource(R.string.strangers)
    )

    TabRow(selectedTabIndex = tabs.indexOfFirst { it.first == selectedFilter }) {
        tabs.forEach { (filter, title) ->
            Tab(
                selected = selectedFilter == filter,
                onClick = { onFilterChange(filter) },
                text = { Text(title) }
            )
        }
    }
}

@Composable
private fun defaultEmptyMessage(filter: NearbyPeopleFilter): String {
    return when (filter) {
        NearbyPeopleFilter.ALL -> stringResource(R.string.no_nearby_people)
        NearbyPeopleFilter.FRIENDS -> stringResource(R.string.no_nearby_friends)
        NearbyPeopleFilter.STRANGERS -> stringResource(R.string.no_nearby_strangers)
    }
}

@Composable
fun NearbyPersonCard(
    person: NearbyPerson,
    onClick: () -> Unit,
    onSendIM: () -> Unit,
    onAddFriend: () -> Unit,
    onViewProfile: () -> Unit,
    modifier: Modifier = Modifier
) {
    var showMenu by remember { mutableStateOf(false) }

    Card(
        modifier = modifier
            .fillMaxWidth()
            .clickable(onClick = onClick),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface
        )
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(48.dp)
                    .clip(CircleShape),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.Default.Person,
                    contentDescription = null,
                    modifier = Modifier.size(32.dp),
                    tint = if (person.isFriend) {
                        MaterialTheme.colorScheme.primary
                    } else {
                        MaterialTheme.colorScheme.onSurfaceVariant
                    }
                )
            }

            Spacer(modifier = Modifier.width(12.dp))

            Column(modifier = Modifier.weight(1f)) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(
                        text = person.name,
                        style = MaterialTheme.typography.bodyLarge,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                    if (person.isFriend) {
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(
                            text = "★",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.primary
                        )
                    }
                }
                Text(
                    text = formatDistance(person.distance),
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }

            IconButton(onClick = onSendIM) {
                Icon(
                    imageVector = Icons.AutoMirrored.Filled.Message,
                    contentDescription = stringResource(R.string.send_im),
                    tint = MaterialTheme.colorScheme.primary
                )
            }

            Box {
                IconButton(onClick = { showMenu = true }) {
                    Icon(Icons.Default.MoreVert, contentDescription = stringResource(R.string.more_options))
                }

                DropdownMenu(
                    expanded = showMenu,
                    onDismissRequest = { showMenu = false }
                ) {
                    DropdownMenuItem(
                        text = { Text(stringResource(R.string.view_profile)) },
                        onClick = {
                            showMenu = false
                            onViewProfile()
                        }
                    )
                    DropdownMenuItem(
                        text = { Text(stringResource(R.string.send_im)) },
                        onClick = {
                            showMenu = false
                            onSendIM()
                        }
                    )
                    if (!person.isFriend) {
                        DropdownMenuItem(
                            text = { Text(stringResource(R.string.add_friend)) },
                            leadingIcon = { Icon(Icons.Default.PersonAdd, contentDescription = null) },
                            onClick = {
                                showMenu = false
                                onAddFriend()
                            }
                        )
                    }
                }
            }
        }
    }
}

private fun formatDistance(meters: Float): String {
    return when {
        meters < 1 -> "< 1m"
        meters < 10 -> "${meters.toInt()}m"
        meters < 100 -> "${(meters / 10).toInt() * 10}m"
        else -> "${(meters / 100).toInt() * 100}m"
    }
}
