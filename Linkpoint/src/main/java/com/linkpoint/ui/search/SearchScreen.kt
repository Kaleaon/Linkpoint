package com.linkpoint.ui.search

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
import androidx.compose.material.icons.filled.Event
import androidx.compose.material.icons.filled.Group
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.ScrollableTabRow
import androidx.compose.material3.Tab
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
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.linkpoint.ui.common.UiLoadState
import com.linkpoint.ui.common.UiTelemetryEvents
import com.linkpoint.ui.common.logUiTelemetry
import com.linkpoint.ui.components.state.EmptyState
import com.linkpoint.ui.components.state.ErrorState
import com.linkpoint.ui.components.state.LoadingState
import com.linkpoint.ui.components.state.ReconnectingBanner
import java.util.UUID

enum class SearchCategory(val displayName: String, val icon: ImageVector) {
    PEOPLE("People", Icons.Default.Person),
    PLACES("Places", Icons.Default.LocationOn),
    GROUPS("Groups", Icons.Default.Group),
    EVENTS("Events", Icons.Default.Event)
}

sealed class ComposeSearchResult {
    abstract val id: UUID
    abstract val name: String
    abstract val description: String

    data class PersonResult(
        override val id: UUID,
        override val name: String,
        override val description: String = "",
        val isOnline: Boolean = false
    ) : ComposeSearchResult()

    data class PlaceResult(
        override val id: UUID,
        override val name: String,
        override val description: String = "",
        val traffic: Int = 0,
        val slurl: String = ""
    ) : ComposeSearchResult()

    data class GroupResult(
        override val id: UUID,
        override val name: String,
        override val description: String = "",
        val memberCount: Int = 0,
        val isOpen: Boolean = true
    ) : ComposeSearchResult()

    data class EventResult(
        override val id: UUID,
        override val name: String,
        override val description: String = "",
        val location: String = "",
        val dateTime: String = ""
    ) : ComposeSearchResult()
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SearchScreen(
    results: List<ComposeSearchResult>,
    uiLoadState: UiLoadState = UiLoadState.Content,
    onRetry: () -> Unit,
    onSearch: (String, SearchCategory) -> Unit,
    onResultClick: (ComposeSearchResult) -> Unit,
    onNavigateBack: () -> Unit,
    modifier: Modifier = Modifier
) {
    var selectedCategory by remember { mutableIntStateOf(0) }
    var searchQuery by remember { mutableStateOf("") }

    val categories = SearchCategory.entries
    val currentCategory = categories[selectedCategory]

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Search") },
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
            if (uiLoadState is UiLoadState.Reconnecting) {
                ReconnectingBanner(message = uiLoadState.message)
            }

            OutlinedTextField(
                value = searchQuery,
                onValueChange = { searchQuery = it },
                label = { Text("Search") },
                leadingIcon = { Icon(Icons.Default.Search, contentDescription = null) },
                trailingIcon = {
                    if (searchQuery.isNotBlank()) {
                        IconButton(onClick = { onSearch(searchQuery, currentCategory) }) {
                            Icon(Icons.Default.Search, contentDescription = "Search")
                        }
                    }
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                singleLine = true
            )

            ScrollableTabRow(
                selectedTabIndex = selectedCategory,
                modifier = Modifier.fillMaxWidth()
            ) {
                categories.forEachIndexed { index, category ->
                    Tab(
                        selected = selectedCategory == index,
                        onClick = { selectedCategory = index },
                        text = { Text(category.displayName) },
                        icon = { Icon(category.icon, contentDescription = null) }
                    )
                }
            }

            when (uiLoadState) {
                is UiLoadState.Loading -> LoadingState(message = uiLoadState.message)
                is UiLoadState.Error -> ErrorState(
                    title = uiLoadState.title,
                    message = uiLoadState.message,
                    retryLabel = uiLoadState.retryLabel,
                    onRetry = {
                        logUiTelemetry(UiTelemetryEvents.RETRY_TAPPED, "search", uiLoadState)
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
                    if (results.isEmpty()) {
                        EmptyState(
                            title = if (searchQuery.isBlank()) "Start searching" else "No results found",
                            message = if (searchQuery.isBlank()) "Enter a term to search avatars, places, groups, or events."
                            else "Try another keyword or switch category."
                        )
                    } else {
                        LazyColumn(
                            modifier = Modifier.fillMaxSize(),
                            contentPadding = PaddingValues(16.dp),
                            verticalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            items(results, key = { it.id }) { result ->
                                SearchResultCard(result = result, onClick = { onResultClick(result) })
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun SearchResultCard(
    result: ComposeSearchResult,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier
            .fillMaxWidth()
            .clickable(onClick = onClick),
        colors = CardDefaults.cardColors()
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
                    imageVector = when (result) {
                        is ComposeSearchResult.PersonResult -> Icons.Default.Person
                        is ComposeSearchResult.PlaceResult -> Icons.Default.LocationOn
                        is ComposeSearchResult.GroupResult -> Icons.Default.Group
                        is ComposeSearchResult.EventResult -> Icons.Default.Event
                    },
                    contentDescription = null,
                    modifier = Modifier.size(28.dp)
                )
            }

            Spacer(modifier = Modifier.width(12.dp))

            Column(modifier = Modifier.weight(1f)) {
                Text(text = result.name, maxLines = 1, overflow = TextOverflow.Ellipsis)

                val subtitle = when (result) {
                    is ComposeSearchResult.PersonResult -> if (result.isOnline) "Online" else result.description.ifBlank { "Offline" }
                    is ComposeSearchResult.PlaceResult -> if (result.traffic > 0) "Traffic: ${result.traffic}" else result.description
                    is ComposeSearchResult.GroupResult -> "${result.memberCount} members" + if (result.isOpen) " • Open" else ""
                    is ComposeSearchResult.EventResult -> result.dateTime.ifBlank { result.location }
                }

                if (subtitle.isNotBlank()) {
                    Text(text = subtitle, maxLines = 1, overflow = TextOverflow.Ellipsis)
                }
            }
        }
    }
}
