package com.linkpoint.ui.objects

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
import com.linkpoint.objects.SceneObject
import com.linkpoint.ui.adaptive.LocalWindowSizeClass
import com.linkpoint.ui.components.linkpoint2.tokens.Linkpoint2
import com.linkpoint.ui.theme.LinkpointTheme

/**
 * Dialog showing object properties using Jetpack Compose and Ktheme tokens.
 */
class ObjectPropertiesDialog : DialogFragment() {

    private lateinit var obj: SceneObject

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        obj = requireArguments().getParcelable<SceneObject>("object")
            ?: throw IllegalArgumentException("SceneObject argument is required")
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
                    ObjectPropertiesSheet(
                        obj = obj,
                        onClose = { dismiss() },
                        onEditRequested = {
                            onEditRequested()
                            dismiss()
                        }
                    )
                }
            }
        }
    }

    private fun onEditRequested() {
        (parentFragment as? Listener)?.onObjectEditRequested(obj)
    }

    interface Listener {
        fun onObjectEditRequested(obj: SceneObject)
    }

    companion object {
        fun newInstance(obj: SceneObject): ObjectPropertiesDialog {
            return ObjectPropertiesDialog().apply {
                arguments = Bundle().apply {
                    putParcelable("object", obj)
                }
            }
        }
    }
}

/**
 * Responsive container for Object Properties.
 * Uses [ModalBottomSheet] on compact screens (< 600dp) and wide pane card on >= 600dp.
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ObjectPropertiesSheet(
    obj: SceneObject,
    onClose: () -> Unit,
    onEditRequested: (() -> Unit)? = null,
    onTakeObject: (() -> Unit)? = null,
    onSitOnObject: (() -> Unit)? = null,
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
            ObjectPropertiesContent(
                obj = obj,
                onClose = onClose,
                onEditRequested = onEditRequested,
                onTakeObject = onTakeObject,
                onSitOnObject = onSitOnObject,
                modifier = Modifier.padding(24.dp)
            )
        }
    } else {
        ModalBottomSheet(
            onDismissRequest = onClose,
            containerColor = LinkpointTheme.colors.surface,
            contentColor = LinkpointTheme.colors.onSurface
        ) {
            ObjectPropertiesContent(
                obj = obj,
                onClose = onClose,
                onEditRequested = onEditRequested,
                onTakeObject = onTakeObject,
                onSitOnObject = onSitOnObject,
                modifier = Modifier.padding(20.dp)
            )
        }
    }
}

@Composable
fun ObjectPropertiesContent(
    obj: SceneObject,
    onClose: () -> Unit,
    onEditRequested: (() -> Unit)? = null,
    onTakeObject: (() -> Unit)? = null,
    onSitOnObject: (() -> Unit)? = null,
    modifier: Modifier = Modifier
) {
    val colors = LinkpointTheme.colors
    val tokens = Linkpoint2.tokens

    val flagsText = buildString {
        if (obj.updateFlags and com.linkpoint.objects.ObjectManager.FLAG_PHANTOM != 0) append("Phantom, ")
        if (obj.updateFlags and com.linkpoint.objects.ObjectManager.FLAG_USE_PHYSICS != 0) append("Physics, ")
        if (obj.updateFlags and com.linkpoint.objects.ObjectManager.FLAG_SCRIPTED != 0) append("Scripted, ")
        if (obj.updateFlags and com.linkpoint.objects.ObjectManager.FLAG_INVENTORY_EMPTY == 0) append("Has Inventory, ")
    }.dropLastWhile { it == ',' || it == ' ' }

    Column(
        modifier = modifier
            .fillMaxWidth()
            .verticalScroll(rememberScrollState()),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        Text(
            text = obj.name.ifEmpty { stringResource(R.string.object_properties) },
            style = MaterialTheme.typography.titleLarge,
            fontWeight = FontWeight.Bold,
            color = colors.onSurface
        )

        if (obj.description.isNotBlank()) {
            Text(
                text = obj.description,
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
            PropertyRow(label = "Position", value = "X: %.2f, Y: %.2f, Z: %.2f".format(obj.position.x, obj.position.y, obj.position.z))
            PropertyRow(label = "Rotation", value = "X: %.2f, Y: %.2f, Z: %.2f, W: %.2f".format(obj.rotation.x, obj.rotation.y, obj.rotation.z, obj.rotation.w))
            PropertyRow(label = "Scale", value = "X: %.2f, Y: %.2f, Z: %.2f".format(obj.scale.x, obj.scale.y, obj.scale.z))
            PropertyRow(label = "Full ID", value = obj.fullId.toString())
            PropertyRow(label = "Owner ID", value = obj.ownerId.toString())
            PropertyRow(label = "Flags", value = flagsText.ifEmpty { "None" })
        }

        Spacer(modifier = Modifier.height(8.dp))

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp, Alignment.End)
        ) {
            onSitOnObject?.let { sit ->
                OutlinedButton(onClick = sit) {
                    Text("Sit")
                }
            }
            onTakeObject?.let { take ->
                OutlinedButton(onClick = take) {
                    Text("Take")
                }
            }
            onEditRequested?.let { edit ->
                Button(
                    onClick = edit,
                    colors = ButtonDefaults.buttonColors(
                        containerColor = colors.primary,
                        contentColor = colors.onPrimary
                    )
                ) {
                    Text("Edit")
                }
            }
            Button(
                onClick = onClose,
                colors = ButtonDefaults.buttonColors(
                    containerColor = colors.secondary,
                    contentColor = colors.onSecondary
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
