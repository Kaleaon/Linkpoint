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
import com.linkpoint.inventory.ItemPermissions
import com.linkpoint.ui.adaptive.LocalWindowSizeClass
import com.linkpoint.ui.components.linkpoint2.tokens.Linkpoint2
import com.linkpoint.ui.theme.LinkpointTheme
import java.text.SimpleDateFormat
import java.util.*

/**
 * Dialog showing detailed information about an inventory item using Jetpack Compose.
 */
class ItemDetailDialog : DialogFragment() {

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
                    ItemDetailSheet(
                        item = item,
                        onClose = { dismiss() }
                    )
                }
            }
        }
    }

    companion object {
        fun newInstance(item: InventoryItem): ItemDetailDialog {
            return ItemDetailDialog().apply {
                arguments = Bundle().apply {
                    putParcelable("item", item)
                }
            }
        }
    }
}

/**
 * Responsive container for Item Details.
 * Uses [ModalBottomSheet] on compact screens (< 600dp) and wide pane card on >= 600dp.
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ItemDetailSheet(
    item: InventoryItem,
    onClose: () -> Unit,
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
            ItemDetailContent(
                item = item,
                onClose = onClose,
                modifier = Modifier.padding(24.dp)
            )
        }
    } else {
        ModalBottomSheet(
            onDismissRequest = onClose,
            containerColor = LinkpointTheme.colors.surface,
            contentColor = LinkpointTheme.colors.onSurface
        ) {
            ItemDetailContent(
                item = item,
                onClose = onClose,
                modifier = Modifier.padding(20.dp)
            )
        }
    }
}

@Composable
fun ItemDetailContent(
    item: InventoryItem,
    onClose: () -> Unit,
    modifier: Modifier = Modifier
) {
    val colors = LinkpointTheme.colors
    val tokens = Linkpoint2.tokens

    val creationDateFormatted = try {
        SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault())
            .format(Date(item.creationDate.toLong() * 1000))
    } catch (_: Exception) {
        "Unknown"
    }

    val permissionsText = buildString {
        append("Base: ${formatPermissions(item.permissions.baseMask)}\n")
        append("Owner: ${formatPermissions(item.permissions.ownerMask)}\n")
        append("Group: ${formatPermissions(item.permissions.groupMask)}\n")
        append("Everyone: ${formatPermissions(item.permissions.everyoneMask)}\n")
        append("Next Owner: ${formatPermissions(item.permissions.nextOwnerMask)}")
    }

    val saleInfoText = if (item.saleInfo.saleType != 0) {
        "Price: ${item.saleInfo.salePrice} L$\nType: ${item.saleInfo.saleType}"
    } else {
        "Not for sale"
    }

    Column(
        modifier = modifier
            .fillMaxWidth()
            .verticalScroll(rememberScrollState()),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        Text(
            text = item.name.ifEmpty { "Item Details" },
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
            PropertyRow(label = "Asset Type", value = item.assetTypeEnum.toString())
            PropertyRow(label = "Inventory Type", value = item.inventoryType.toString())
            PropertyRow(label = "Creation Date", value = creationDateFormatted)
            PropertyRow(label = "Permissions", value = permissionsText)
            PropertyRow(label = "Sale Info", value = saleInfoText)
        }

        Spacer(modifier = Modifier.height(8.dp))

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.End
        ) {
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

private fun formatPermissions(mask: Int): String {
    val permissions = mutableListOf<String>()
    if (mask and ItemPermissions.PERM_MODIFY != 0) permissions.add("Modify")
    if (mask and ItemPermissions.PERM_COPY != 0) permissions.add("Copy")
    if (mask and ItemPermissions.PERM_TRANSFER != 0) permissions.add("Transfer")
    if (mask and ItemPermissions.PERM_MOVE != 0) permissions.add("Move")
    return if (permissions.isEmpty()) "None" else permissions.joinToString(", ")
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
