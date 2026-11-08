package com.linkpoint.ui.adaptive

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Info
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.VerticalDivider
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp

/**
 * Custom breakpoint-driven adaptive list-detail pane scaffold.
 *
 * Renders a side-by-side split layout on wide screens (>= 600dp, e.g. Medium/Expanded)
 * and a single pane on compact screens (< 600dp).
 *
 * @param selectedItem Currently selected detail item, or null if no item selected.
 * @param onClearSelection Callback to deselect the active item (used when back pressed in compact mode).
 * @param listPane Content composable for the list view.
 * @param detailPane Content composable for displaying details of a selected item.
 * @param placeholderPane Content composable displayed in detail column when no item is selected in split mode.
 * @param isSplitPane Flag overriding auto-detection from [LocalWindowSizeClass].
 */
@Composable
fun <T> AdaptiveListDetailPane(
    selectedItem: T?,
    onClearSelection: () -> Unit,
    listPane: @Composable () -> Unit,
    detailPane: @Composable (T) -> Unit,
    modifier: Modifier = Modifier,
    placeholderPane: @Composable () -> Unit = { ListDetailPlaceholder() },
    isSplitPane: Boolean = LocalWindowSizeClass.current.isAtLeastMedium,
    listPaneWeight: Float = 0.38f,
    detailPaneWeight: Float = 0.62f,
) {
    // Intercept back-button in compact mode when detail is active
    BackHandler(enabled = !isSplitPane && selectedItem != null) {
        onClearSelection()
    }

    if (isSplitPane) {
        Row(modifier = modifier.fillMaxSize()) {
            Box(
                modifier = Modifier
                    .weight(listPaneWeight)
                    .fillMaxHeight()
            ) {
                listPane()
            }

            VerticalDivider(
                modifier = Modifier.fillMaxHeight(),
                color = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.5f)
            )

            Box(
                modifier = Modifier
                    .weight(detailPaneWeight)
                    .fillMaxHeight()
            ) {
                if (selectedItem != null) {
                    detailPane(selectedItem)
                } else {
                    placeholderPane()
                }
            }
        }
    } else {
        Box(modifier = modifier.fillMaxSize()) {
            if (selectedItem != null) {
                detailPane(selectedItem)
            } else {
                listPane()
            }
        }
    }
}

/**
 * Default placeholder displayed in detail pane when no item is selected in split mode.
 */
@Composable
fun ListDetailPlaceholder(
    title: String = "No Selection",
    subtitle: String = "Select an item from the list to view details.",
    icon: ImageVector = Icons.Outlined.Info,
    modifier: Modifier = Modifier,
) {
    Surface(
        modifier = modifier.fillMaxSize(),
        color = MaterialTheme.colorScheme.surface
    ) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(32.dp),
            contentAlignment = Alignment.Center
        ) {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {
                Box(
                    modifier = Modifier
                        .size(64.dp)
                        .background(
                            color = MaterialTheme.colorScheme.surfaceVariant,
                            shape = CircleShape
                        ),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = icon,
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.onSurfaceVariant,
                        modifier = Modifier.size(32.dp)
                    )
                }
                Spacer(modifier = Modifier.height(16.dp))
                Text(
                    text = title,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.SemiBold,
                    color = MaterialTheme.colorScheme.onSurface
                )
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = subtitle,
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    textAlign = TextAlign.Center
                )
            }
        }
    }
}
