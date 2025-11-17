package com.linkpoint.ui.compose.about

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.linkpoint.ui.compose.components.*

/**
 * About Screen
 * 
 * Displays app information including:
 * - App version
 * - Credits
 * - License information
 * - Links to resources
 */

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AboutScreen(
    onNavigateBack: () -> Unit,
    modifier: Modifier = Modifier
) {
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("About") },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(
                            imageVector = Icons.Default.ArrowBack,
                            contentDescription = "Back"
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
            // App Info
            item {
                AppInfoSection()
            }
            
            // Version Info
            item {
                VersionInfoSection()
            }
            
            // Credits
            item {
                CreditsSection()
            }
            
            // Links
            item {
                LinksSection()
            }
            
            // Legal
            item {
                LegalSection()
            }
        }
    }
}

/**
 * App Info Section
 */
@Composable
private fun AppInfoSection(
    modifier: Modifier = Modifier
) {
    LinkpointCard(modifier = modifier) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // App icon placeholder
            Surface(
                modifier = Modifier.size(80.dp),
                shape = MaterialTheme.shapes.large,
                color = MaterialTheme.colorScheme.primaryContainer
            ) {
                Icon(
                    imageVector = Icons.Default.Star,
                    contentDescription = "App Icon",
                    modifier = Modifier.padding(20.dp),
                    tint = MaterialTheme.colorScheme.onPrimaryContainer
                )
            }
            
            Text(
                text = "Linkpoint",
                style = MaterialTheme.typography.headlineMedium,
                color = MaterialTheme.colorScheme.onSurface
            )
            
            Text(
                text = "A modern Second Life viewer for Android",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                textAlign = TextAlign.Center
            )
        }
    }
}

/**
 * Version Info Section
 */
@Composable
private fun VersionInfoSection(
    modifier: Modifier = Modifier
) {
    LinkpointCard(modifier = modifier) {
        Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
            Text(
                text = "Version Information",
                style = MaterialTheme.typography.titleMedium,
                color = MaterialTheme.colorScheme.primary
            )
            
            InfoRow(
                label = "Version",
                value = "1.0.0 (Beta)"
            )
            
            InfoRow(
                label = "Build",
                value = "2025.01.001"
            )
            
            InfoRow(
                label = "Protocol Version",
                value = "SL 2024.1"
            )
            
            InfoRow(
                label = "Last Updated",
                value = "January 2025"
            )
        }
    }
}

/**
 * Credits Section
 */
@Composable
private fun CreditsSection(
    modifier: Modifier = Modifier
) {
    LinkpointCard(modifier = modifier) {
        Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
            Text(
                text = "Credits",
                style = MaterialTheme.typography.titleMedium,
                color = MaterialTheme.colorScheme.primary
            )
            
            Text(
                text = "Developed by the Linkpoint Team",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurface
            )
            
            Spacer(modifier = Modifier.height(8.dp))
            
            Text(
                text = "Special Thanks",
                style = MaterialTheme.typography.titleSmall,
                color = MaterialTheme.colorScheme.onSurface
            )
            
            Text(
                text = "• Linden Lab for Second Life\n" +
                        "• The open source community\n" +
                        "• All contributors and testers",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}

/**
 * Links Section
 */
@Composable
private fun LinksSection(
    modifier: Modifier = Modifier
) {
    LinkpointCard(modifier = modifier) {
        Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
            Text(
                text = "Links",
                style = MaterialTheme.typography.titleMedium,
                color = MaterialTheme.colorScheme.primary
            )
            
            LinkItem(
                title = "Website",
                url = "https://linkpoint.app",
                icon = Icons.Default.Home
            )
            
            HorizontalDivider(modifier = Modifier.padding(vertical = 4.dp))
            
            LinkItem(
                title = "GitHub Repository",
                url = "https://github.com/Kaleaon/Linkpoint",
                icon = Icons.Default.Info
            )
            
            HorizontalDivider(modifier = Modifier.padding(vertical = 4.dp))
            
            LinkItem(
                title = "Documentation",
                url = "https://docs.linkpoint.app",
                icon = Icons.Default.Info
            )
            
            HorizontalDivider(modifier = Modifier.padding(vertical = 4.dp))
            
            LinkItem(
                title = "Support",
                url = "https://support.linkpoint.app",
                icon = Icons.Default.Email
            )
        }
    }
}

/**
 * Legal Section
 */
@Composable
private fun LegalSection(
    modifier: Modifier = Modifier
) {
    LinkpointCard(modifier = modifier) {
        Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
            Text(
                text = "Legal",
                style = MaterialTheme.typography.titleMedium,
                color = MaterialTheme.colorScheme.primary
            )
            
            Text(
                text = "Copyright © 2025 Linkpoint Team",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
            
            Spacer(modifier = Modifier.height(4.dp))
            
            Text(
                text = "This application is not affiliated with or endorsed by Linden Lab.",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
            
            Spacer(modifier = Modifier.height(8.dp))
            
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                LinkpointTextButton(
                    text = "Privacy Policy",
                    onClick = { /* TODO */ },
                    modifier = Modifier.weight(1f)
                )
                
                LinkpointTextButton(
                    text = "Terms of Service",
                    onClick = { /* TODO */ },
                    modifier = Modifier.weight(1f)
                )
            }
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
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurface
        )
    }
}

/**
 * Link Item
 */
@Composable
private fun LinkItem(
    title: String,
    url: String,
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp),
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
                text = url,
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.primary
            )
        }
        
        Icon(
            imageVector = Icons.Default.KeyboardArrowRight,
            contentDescription = null,
            tint = MaterialTheme.colorScheme.onSurfaceVariant
        )
    }
}