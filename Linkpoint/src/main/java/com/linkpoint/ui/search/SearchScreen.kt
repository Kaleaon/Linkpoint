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
import androidx.compose.foundation.layout.height
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
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
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
import java.util.UUID

/**
 * Search result types
 */
enum class SearchCategory(val displayName: String, val icon: ImageVector) {
    PEOPLE("People", Icons.Default.Person),
    PLACES("Places", Icons.Default.LocationOn),
    GROUPS("Groups", Icons.Default.Group),
    EVENTS("Events", Icons.Default.Event)
}

/**
 * Generic search result
 */
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

/**
 * Compose version of SearchActivity.
 * 
 * Features:
 * - Tabbed search categories (People, Places, Groups, Events)
 * - Search input
 * - Results list with category-specific displays
 * - Click to view details
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SearchScreen(
    results: List<ComposeSearchResult>,
    isLoading: Boolean = false,
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
            // Search input
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
            
            // Category tabs
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
            
            // Results
            when {
                isLoading -> {
                    Box(
                        modifier = Modifier.fillMaxSize(),
                        contentAlignment = Alignment.Center
                    ) {
                        CircularProgressIndicator()
                    }
                }
                results.isEmpty() -> {
                    Box(
                        modifier = Modifier.fillMaxSize(),
                        contentAlignment = Alignment.Center
                    ) {
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Icon(
                                imageVector = Icons.Default.Search,
                                contentDescription = null,
                                modifier = Modifier.size(64.dp),
                                tint = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                            Spacer(modifier = Modifier.height(8.dp))
                            Text(
                                text = if (searchQuery.isBlank()) "Enter a search term"
                                       else "No results found",
                                style = MaterialTheme.typography.bodyLarge,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }
                }
                else -> {
                    LazyColumn(
                        modifier = Modifier.fillMaxSize(),
                        contentPadding = PaddingValues(16.dp),
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        items(results, key = { it.id }) { result ->
                            SearchResultCard(
                                result = result,
                                onClick = { onResultClick(result) }
                            )
                        }
                    }
                }
            }
        }
    }
}

/**
 * Search result card
 */
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
            // Icon
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
                    modifier = Modifier.size(28.dp),
                    tint = MaterialTheme.colorScheme.primary
                )
            }
            
            Spacer(modifier = Modifier.width(12.dp))
            
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = result.name,
                    style = MaterialTheme.typography.bodyLarge,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
                
                // Type-specific subtitle
                val subtitle = when (result) {
                    is ComposeSearchResult.PersonResult -> 
                        if (result.isOnline) "Online" else result.description.ifBlank { "Offline" }
                    is ComposeSearchResult.PlaceResult -> 
                        if (result.traffic > 0) "Traffic: ${result.traffic}" else result.description
                    is ComposeSearchResult.GroupResult -> 
                        "${result.memberCount} members" + if (result.isOpen) " • Open" else ""
                    is ComposeSearchResult.EventResult -> 
                        result.dateTime.ifBlank { result.location }
                }
                
                if (subtitle.isNotBlank()) {
                    Text(
                        text = subtitle,
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                }
            }
        }
    }
}
