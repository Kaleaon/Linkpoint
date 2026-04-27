package com.linkpoint.ui.theme

import androidx.compose.foundation.layout.sizeIn
import androidx.compose.material3.IconButton
import androidx.compose.material3.IconButtonDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.unit.dp

private val MinimumTouchTargetModifier = Modifier.sizeIn(minWidth = 48.dp, minHeight = 48.dp)

@Composable
fun AccessibleIconActionButton(
    contentDescription: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
    content: @Composable () -> Unit
) {
    IconButton(
        onClick = onClick,
        modifier = modifier
            .then(MinimumTouchTargetModifier)
            .semantics {
                this.contentDescription = contentDescription
            },
        enabled = enabled,
        colors = IconButtonDefaults.iconButtonColors(),
        content = content
    )
}
