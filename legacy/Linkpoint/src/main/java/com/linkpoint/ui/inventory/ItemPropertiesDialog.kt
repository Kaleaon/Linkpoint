package com.linkpoint.ui.inventory

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.ComposeView
import androidx.compose.ui.platform.ViewCompositionStrategy
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.fragment.app.DialogFragment
import com.linkpoint.R
import com.linkpoint.inventory.InventoryItem
import com.linkpoint.ui.adaptive.LocalWindowSizeClass
import com.linkpoint.ui.components.linkpoint2.tokens.Linkpoint2
import com.linkpoint.ui.theme.LinkpointTheme

/**
 * Dialog showing inventory item properties using Jetpack Compose and Ktheme tokens.
 */
class ItemPropertiesDialog : DialogFragment() {

    private lateinit var item: InventoryItem

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        item = requireArguments().getParcelable<InventoryItem>("item")
            ?: throw IllegalArgumentException("InventoryItem argument is required")
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        return ComposeView(requireContext()).apply {
            setViewCompositionStrategy(ViewCompositionStrategy.DisposeOnViewTreeLifecycleDestroyed)
            setContent {
                LinkpointTheme {
                    ItemPropertiesSheet(
                        item = item,
                        onClose = { dismiss() }
                    )
                }
            }
        }
    }

    companion object {
        fun newInstance(item: InventoryItem): ItemPropertiesDialog {
            return ItemPropertiesDialog().apply {
                arguments = Bundle().apply {
                    putParcelable("item", item)
                }
            }
        }
    }
}

/**
 * Responsive container for Item Properties.
 * Uses [ModalBottomSheet] on compact screens (< 600dp) and wide pane card on >= 600dp.
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ItemPropertiesSheet(
    item: InventoryItem,
    onClose: () -> Unit,
    onWear: (() -> Unit)? = null,
    onRez: (() -> Unit)? = null,
    onDelete: (() -> Unit)? = null,
    modifier: Modifier = Modifier
) {
    val windowSizeClass = LocalWindowSizeClass.current
    if (windowSizeClass.isAtLeastMedium) {
        Surface(
            modifier = modifier.fillMaxWidth(0.85f).padding(16.dp),
            shape = RoundedCornerShape(Linkpoint2.tokens.radii.lg),
            color = LinkpointTheme.colors.surface,
            contentColor = LinkpointTheme.colors.onSurface,
            tonalElevation = 8.dp
        ) {
            ItemPropertiesContent(
                item = item,
                onClose = onClose,
                onWear = onWear,
                onRez = onRez,
                onDelete = onDelete,
                modifier = Modifier.padding(24.dp)
            )
        }
    } else {
        ModalBottomSheet(
            onDismissRequest = onClose,
            containerColor = LinkpointTheme.colors.surface,
            contentColor = LinkpointTheme.colors.onSurface
        ) {
            ItemPropertiesContent(
                item = item,
                onClose = onClose,
                onWear = onWear,
                onRez = onRez,
                onDelete = onDelete,
                modifier = Modifier.padding(20.dp)
            )
        }
    }
}

@Composable
fun ItemPropertiesContent(
    item: InventoryItem,
    onClose: () -> Unit,
    onWear: (() -> Unit)? = null,
    onRez: (() -> Unit)? = null,
    onDelete: (() -> Unit)? = null,
    modifier: Modifier = Modifier
) {
    val colors = LinkpointTheme.colors
    val tokens = Linkpoint2.tokens

    val flagsText = buildString {
        if (item.flags and 0x01 != 0) append("Shared, ")
        if (item.flags and 0x02 != 0) append("Marketplace, ")
        if (item.flags and 0x04 != 0) append("Folder, ")
        if (item.flags and 0x08 != 0) append("Hidden, ")
        if (item.flags and 0x10 != 0) append("Broken, ")
    }.dropLastWhile { it == ',' || it == ' ' }

    Column(
        modifier = modifier
            .fillMaxWidth()
            .verticalScroll(rememberScrollState()),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        Text(
            text = item.name.ifEmpty { stringResource(R.string.item_properties) },
            style = MaterialTheme.typography.titleLarge,
            fontWeight = FontWeight.Bold,
            color = colors.onSurface
        )

        if (item.description.isNotBlank()) {
            Text(
                text = item.description,
                style = MaterialTheme.typography.bodyMedium,
                color = colors.onSurfaceVariant
            )
        }

        HorizontalDivider(color = colors.surfaceVariant)

        Column(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(tokens.radii.md))
                .background(colors.surfaceVariant.copy(alpha = 0.5f))
                .padding(12.dp),
            verticalArrangement = Arrangement.spacedBy(6.dp)
        ) {
            PropertyRow(label = "Type", value = item.assetTypeEnum.toString())
            PropertyRow(label = "Asset ID", value = item.assetId.toString())
            PropertyRow(label = "Item ID", value = item.itemId.toString())
            PropertyRow(label = "Parent ID", value = item.parentId.toString())
            PropertyRow(label = "Flags", value = flagsText.ifEmpty { "None" })
        }

        Spacer(modifier = Modifier.height(8.dp))

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp, Alignment.End)
        ) {
            onWear?.let { wear ->
                OutlinedButton(onClick = wear) {
                    Text("Wear")
                }
            }
            onRez?.let { rez ->
                OutlinedButton(onClick = rez) {
                    Text("Rez")
                }
            }
            onDelete?.let { delete ->
                OutlinedButton(
                    onClick = delete,
                    colors = ButtonDefaults.outlinedButtonColors(contentColor = colors.error)
                ) {
                    Text("Delete")
                }
            }
            Button(
                onClick = onClose,
                colors = ButtonDefaults.buttonColors(
                    containerColor = colors.primary,
                    contentColor = colors.onPrimary
                )
            ) {
                Text(stringResource(R.string.close))
            }
        }
    }
}

@Composable
private fun PropertyRow(label: String, value: String) {
    Column {
        Text(
            text = label,
            style = MaterialTheme.typography.labelSmall,
            color = LinkpointTheme.colors.onSurfaceVariant
        )
        Text(
            text = value,
            style = MaterialTheme.typography.bodySmall,
            color = LinkpointTheme.colors.onSurface,
            fontWeight = FontWeight.Medium
        )
    }
}
