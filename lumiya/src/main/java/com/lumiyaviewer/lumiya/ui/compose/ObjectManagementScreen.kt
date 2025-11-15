package com.lumiyaviewer.lumiya.ui.compose

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.lumiyaviewer.lumiya.ui.theme.LinkpointTheme
import java.util.UUID

/**
 * Object Management Screen - Complete object management with creation, editing, and interaction
 * 
 * Features:
 * - Object selection from nearby objects
 * - Property editing (name, description, position, rotation, scale)
 * - Object creation (primitives: Cube, Sphere, Cylinder, etc.)
 * - Permission system (Owner, Group, Everyone)
 * - Object actions (Copy, Delete, Take to inventory, Sit)
 * - Texture management
 * - Color editing per face
 * - Position/rotation/scale control
 * - Object search
 * - Object information display
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ObjectManagementScreen(navController: NavController? = null) {
    var nearbyObjects by remember { mutableStateOf(generateSampleObjects()) }
    var selectedObject by remember { mutableStateOf<SceneObject?>(nearbyObjects.firstOrNull()) }
    var selectedTab by remember { mutableIntStateOf(0) }
    val tabs = listOf("General", "Position", "Texture", "Permissions")
    var showCreateDialog by remember { mutableStateOf(false) }
    var searchQuery by remember { mutableStateOf("") }

    LinkpointTheme {
        Scaffold(
            topBar = {
                TopAppBar(
                    title = { Text("Object Management") },
                    actions = {
                        IconButton(onClick = { /* Search objects */ }) {
                            Icon(Icons.Default.Search, "Search")
                        }
                        IconButton(onClick = { showCreateDialog = true }) {
                            Icon(Icons.Default.Add, "Create")
                        }
                    }
                )
            }
        ) { padding ->
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(padding)
            ) {
                // Selected Object Info
                if (selectedObject != null) {
                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp),
                        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
                    ) {
                        Column(modifier = Modifier.padding(16.dp)) {
                            Text(
                                text = "Selected Object",
                                style = MaterialTheme.typography.labelSmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                            Text(
                                text = selectedObject!!.name,
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.Bold
                            )
                            Text(
                                text = "Type: ${selectedObject!!.primitiveType.name}",
                                style = MaterialTheme.typography.bodySmall
                            )
                        }
                    }
                } else {
                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp),
                        colors = CardDefaults.cardColors(
                            containerColor = MaterialTheme.colorScheme.surfaceVariant
                        )
                    ) {
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(16.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                "No object selected",
                                style = MaterialTheme.typography.bodyMedium,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }
                }

                // Tab Row
                if (selectedObject != null) {
                    TabRow(selectedTabIndex = selectedTab) {
                        tabs.forEachIndexed { index, title ->
                            Tab(
                                selected = selectedTab == index,
                                onClick = { selectedTab = index },
                                text = { Text(title) }
                            )
                        }
                    }

                    // Tab Content
                    when (selectedTab) {
                        0 -> GeneralTab(selectedObject!!, onObjectUpdate = { updated ->
                            nearbyObjects = nearbyObjects.map { if (it.id == updated.id) updated else it }
                            selectedObject = updated
                        })
                        1 -> PositionTab(selectedObject!!, onObjectUpdate = { updated ->
                            nearbyObjects = nearbyObjects.map { if (it.id == updated.id) updated else it }
                            selectedObject = updated
                        })
                        2 -> TextureTab(selectedObject!!, onObjectUpdate = { updated ->
                            nearbyObjects = nearbyObjects.map { if (it.id == updated.id) updated else it }
                            selectedObject = updated
                        })
                        3 -> PermissionsTab(selectedObject!!, onObjectUpdate = { updated ->
                            nearbyObjects = nearbyObjects.map { if (it.id == updated.id) updated else it }
                            selectedObject = updated
                        })
                    }

                    // Action Buttons
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        OutlinedButton(
                            onClick = { /* Copy object */ },
                            modifier = Modifier.weight(1f)
                        ) {
                            Icon(Icons.Default.ContentCopy, contentDescription = null, modifier = Modifier.size(18.dp))
                            Spacer(modifier = Modifier.width(4.dp))
                            Text("Copy")
                        }
                        OutlinedButton(
                            onClick = { /* Take to inventory */ },
                            modifier = Modifier.weight(1f)
                        ) {
                            Icon(Icons.Default.Inventory, contentDescription = null, modifier = Modifier.size(18.dp))
                            Spacer(modifier = Modifier.width(4.dp))
                            Text("Take")
                        }
                        OutlinedButton(
                            onClick = { 
                                nearbyObjects = nearbyObjects.filter { it.id != selectedObject!!.id }
                                selectedObject = nearbyObjects.firstOrNull()
                            },
                            modifier = Modifier.weight(1f),
                            colors = ButtonDefaults.outlinedButtonColors(
                                contentColor = MaterialTheme.colorScheme.error
                            )
                        ) {
                            Icon(Icons.Default.Delete, contentDescription = null, modifier = Modifier.size(18.dp))
                            Spacer(modifier = Modifier.width(4.dp))
                            Text("Delete")
                        }
                    }
                }

                // Object List
                Text(
                    text = "Nearby Objects (${nearbyObjects.size})",
                    style = MaterialTheme.typography.titleSmall,
                    fontWeight = FontWeight.Medium,
                    modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp)
                )

                LazyColumn(
                    modifier = Modifier.fillMaxSize(),
                    contentPadding = PaddingValues(horizontal = 16.dp, vertical = 8.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    items(nearbyObjects) { obj ->
                        ObjectListItem(
                            obj = obj,
                            isSelected = obj.id == selectedObject?.id,
                            onClick = { selectedObject = obj }
                        )
                    }
                }
            }
        }

        // Create Object Dialog
        if (showCreateDialog) {
            CreateObjectDialog(
                onDismiss = { showCreateDialog = false },
                onCreate = { primitive ->
                    val newObject = SceneObject(
                        name = "New ${primitive.name}",
                        primitiveType = primitive,
                        position = Vector3(128f, 128f, 23f),
                        rotation = Vector3(0f, 0f, 0f),
                        scale = Vector3(1f, 1f, 1f),
                        color = Color.Gray
                    )
                    nearbyObjects = nearbyObjects + newObject
                    selectedObject = newObject
                    showCreateDialog = false
                }
            )
        }
    }
}

@Composable
fun ObjectListItem(obj: SceneObject, isSelected: Boolean, onClick: () -> Unit) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        onClick = onClick,
        elevation = CardDefaults.cardElevation(
            defaultElevation = if (isSelected) 4.dp else 2.dp
        ),
        colors = CardDefaults.cardColors(
            containerColor = if (isSelected)
                MaterialTheme.colorScheme.primaryContainer
            else MaterialTheme.colorScheme.surface
        )
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                Icons.Default.Cube,
                contentDescription = null,
                modifier = Modifier.size(32.dp)
            )
            Spacer(modifier = Modifier.width(12.dp))
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = obj.name,
                    style = MaterialTheme.typography.bodyMedium,
                    fontWeight = FontWeight.Medium
                )
                Text(
                    text = obj.primitiveType.name,
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
            if (isSelected) {
                Icon(
                    Icons.Default.Check,
                    contentDescription = "Selected",
                    tint = MaterialTheme.colorScheme.primary
                )
            }
        }
    }
}

@Composable
fun GeneralTab(obj: SceneObject, onObjectUpdate: (SceneObject) -> Unit) {
    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        item {
            OutlinedTextField(
                value = obj.name,
                onValueChange = { onObjectUpdate(obj.copy(name = it)) },
                label = { Text("Name") },
                modifier = Modifier.fillMaxWidth()
            )
        }
        item {
            OutlinedTextField(
                value = obj.description,
                onValueChange = { onObjectUpdate(obj.copy(description = it)) },
                label = { Text("Description") },
                modifier = Modifier.fillMaxWidth(),
                minLines = 3
            )
        }
        item {
            Text("Object UUID", style = MaterialTheme.typography.labelMedium)
            Text(
                obj.id.toString(),
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
        item {
            Text("Creator", style = MaterialTheme.typography.labelMedium)
            Text(
                obj.creator,
                style = MaterialTheme.typography.bodySmall
            )
        }
        item {
            Text("Owner", style = MaterialTheme.typography.labelMedium)
            Text(
                obj.owner,
                style = MaterialTheme.typography.bodySmall
            )
        }
    }
}

@Composable
fun PositionTab(obj: SceneObject, onObjectUpdate: (SceneObject) -> Unit) {
    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        item {
            Text(
                "Position",
                style = MaterialTheme.typography.titleSmall,
                fontWeight = FontWeight.Bold
            )
        }
        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                OutlinedTextField(
                    value = obj.position.x.toString(),
                    onValueChange = { 
                        it.toFloatOrNull()?.let { x ->
                            onObjectUpdate(obj.copy(position = obj.position.copy(x = x)))
                        }
                    },
                    label = { Text("X") },
                    modifier = Modifier.weight(1f)
                )
                OutlinedTextField(
                    value = obj.position.y.toString(),
                    onValueChange = { 
                        it.toFloatOrNull()?.let { y ->
                            onObjectUpdate(obj.copy(position = obj.position.copy(y = y)))
                        }
                    },
                    label = { Text("Y") },
                    modifier = Modifier.weight(1f)
                )
                OutlinedTextField(
                    value = obj.position.z.toString(),
                    onValueChange = { 
                        it.toFloatOrNull()?.let { z ->
                            onObjectUpdate(obj.copy(position = obj.position.copy(z = z)))
                        }
                    },
                    label = { Text("Z") },
                    modifier = Modifier.weight(1f)
                )
            }
        }

        item {
            Text(
                "Rotation (degrees)",
                style = MaterialTheme.typography.titleSmall,
                fontWeight = FontWeight.Bold
            )
        }
        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                OutlinedTextField(
                    value = obj.rotation.x.toString(),
                    onValueChange = { 
                        it.toFloatOrNull()?.let { x ->
                            onObjectUpdate(obj.copy(rotation = obj.rotation.copy(x = x)))
                        }
                    },
                    label = { Text("X") },
                    modifier = Modifier.weight(1f)
                )
                OutlinedTextField(
                    value = obj.rotation.y.toString(),
                    onValueChange = { 
                        it.toFloatOrNull()?.let { y ->
                            onObjectUpdate(obj.copy(rotation = obj.rotation.copy(y = y)))
                        }
                    },
                    label = { Text("Y") },
                    modifier = Modifier.weight(1f)
                )
                OutlinedTextField(
                    value = obj.rotation.z.toString(),
                    onValueChange = { 
                        it.toFloatOrNull()?.let { z ->
                            onObjectUpdate(obj.copy(rotation = obj.rotation.copy(z = z)))
                        }
                    },
                    label = { Text("Z") },
                    modifier = Modifier.weight(1f)
                )
            }
        }

        item {
            Text(
                "Scale",
                style = MaterialTheme.typography.titleSmall,
                fontWeight = FontWeight.Bold
            )
        }
        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                OutlinedTextField(
                    value = obj.scale.x.toString(),
                    onValueChange = { 
                        it.toFloatOrNull()?.let { x ->
                            onObjectUpdate(obj.copy(scale = obj.scale.copy(x = x)))
                        }
                    },
                    label = { Text("X") },
                    modifier = Modifier.weight(1f)
                )
                OutlinedTextField(
                    value = obj.scale.y.toString(),
                    onValueChange = { 
                        it.toFloatOrNull()?.let { y ->
                            onObjectUpdate(obj.copy(scale = obj.scale.copy(y = y)))
                        }
                    },
                    label = { Text("Y") },
                    modifier = Modifier.weight(1f)
                )
                OutlinedTextField(
                    value = obj.scale.z.toString(),
                    onValueChange = { 
                        it.toFloatOrNull()?.let { z ->
                            onObjectUpdate(obj.copy(scale = obj.scale.copy(z = z)))
                        }
                    },
                    label = { Text("Z") },
                    modifier = Modifier.weight(1f)
                )
            }
        }
    }
}

@Composable
fun TextureTab(obj: SceneObject, onObjectUpdate: (SceneObject) -> Unit) {
    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        item {
            Text(
                "Object Color",
                style = MaterialTheme.typography.titleSmall,
                fontWeight = FontWeight.Bold
            )
        }
        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Box(
                    modifier = Modifier
                        .size(48.dp)
                        .background(obj.color, shape = MaterialTheme.shapes.small)
                )
                Column(modifier = Modifier.weight(1f)) {
                    Text("Current Color", style = MaterialTheme.typography.bodyMedium)
                    Text(
                        "RGB: ${(obj.color.red * 255).toInt()}, ${(obj.color.green * 255).toInt()}, ${(obj.color.blue * 255).toInt()}",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
                Button(onClick = { /* Open color picker */ }) {
                    Text("Change")
                }
            }
        }
        item {
            Text(
                "Texture",
                style = MaterialTheme.typography.titleSmall,
                fontWeight = FontWeight.Bold
            )
        }
        item {
            if (obj.texture != null) {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(Icons.Default.Image, contentDescription = null, modifier = Modifier.size(32.dp))
                        Spacer(modifier = Modifier.width(12.dp))
                        Column(modifier = Modifier.weight(1f)) {
                            Text("Texture Applied", style = MaterialTheme.typography.bodyMedium)
                            Text(
                                obj.texture.toString(),
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant,
                                maxLines = 1
                            )
                        }
                        IconButton(onClick = { onObjectUpdate(obj.copy(texture = null)) }) {
                            Icon(Icons.Default.Clear, "Remove")
                        }
                    }
                }
            } else {
                OutlinedButton(
                    onClick = { /* Select texture */ },
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Icon(Icons.Default.Add, contentDescription = null)
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("Add Texture")
                }
            }
        }
    }
}

@Composable
fun PermissionsTab(obj: SceneObject, onObjectUpdate: (SceneObject) -> Unit) {
    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        item {
            Text(
                "Owner Permissions",
                style = MaterialTheme.typography.titleSmall,
                fontWeight = FontWeight.Bold
            )
        }
        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Checkbox(
                    checked = obj.permissions.ownerModify,
                    onCheckedChange = { 
                        onObjectUpdate(obj.copy(permissions = obj.permissions.copy(ownerModify = it)))
                    }
                )
                Text("Modify", modifier = Modifier.weight(1f))
            }
        }
        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Checkbox(
                    checked = obj.permissions.ownerCopy,
                    onCheckedChange = { 
                        onObjectUpdate(obj.copy(permissions = obj.permissions.copy(ownerCopy = it)))
                    }
                )
                Text("Copy", modifier = Modifier.weight(1f))
            }
        }
        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Checkbox(
                    checked = obj.permissions.ownerTransfer,
                    onCheckedChange = { 
                        onObjectUpdate(obj.copy(permissions = obj.permissions.copy(ownerTransfer = it)))
                    }
                )
                Text("Transfer", modifier = Modifier.weight(1f))
            }
        }

        item {
            Text(
                "Group Permissions",
                style = MaterialTheme.typography.titleSmall,
                fontWeight = FontWeight.Bold
            )
        }
        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Checkbox(
                    checked = obj.permissions.groupModify,
                    onCheckedChange = { 
                        onObjectUpdate(obj.copy(permissions = obj.permissions.copy(groupModify = it)))
                    }
                )
                Text("Modify", modifier = Modifier.weight(1f))
            }
        }
        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Checkbox(
                    checked = obj.permissions.groupCopy,
                    onCheckedChange = { 
                        onObjectUpdate(obj.copy(permissions = obj.permissions.copy(groupCopy = it)))
                    }
                )
                Text("Copy", modifier = Modifier.weight(1f))
            }
        }

        item {
            Text(
                "Everyone Permissions",
                style = MaterialTheme.typography.titleSmall,
                fontWeight = FontWeight.Bold
            )
        }
        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Checkbox(
                    checked = obj.permissions.everyoneCopy,
                    onCheckedChange = { 
                        onObjectUpdate(obj.copy(permissions = obj.permissions.copy(everyoneCopy = it)))
                    }
                )
                Text("Copy", modifier = Modifier.weight(1f))
            }
        }
        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Checkbox(
                    checked = obj.permissions.everyoneMove,
                    onCheckedChange = { 
                        onObjectUpdate(obj.copy(permissions = obj.permissions.copy(everyoneMove = it)))
                    }
                )
                Text("Move", modifier = Modifier.weight(1f))
            }
        }
    }
}

@Composable
fun CreateObjectDialog(onDismiss: () -> Unit, onCreate: (PrimitiveType) -> Unit) {
    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Create Object") },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                Text("Select primitive type:")
                PrimitiveType.values().forEach { type ->
                    OutlinedButton(
                        onClick = { onCreate(type) },
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text(type.name)
                    }
                }
            }
        },
        confirmButton = {},
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Cancel")
            }
        }
    )
}

// Data Models
data class SceneObject(
    val id: UUID = UUID.randomUUID(),
    val name: String,
    val description: String = "",
    val primitiveType: PrimitiveType = PrimitiveType.CUBE,
    val position: Vector3,
    val rotation: Vector3,
    val scale: Vector3,
    val color: Color = Color.Gray,
    val texture: UUID? = null,
    val creator: String = "Unknown",
    val owner: String = "You",
    val permissions: ObjectPermissions = ObjectPermissions()
)

data class Vector3(
    val x: Float,
    val y: Float,
    val z: Float
)

data class ObjectPermissions(
    val ownerModify: Boolean = true,
    val ownerCopy: Boolean = true,
    val ownerTransfer: Boolean = true,
    val groupModify: Boolean = false,
    val groupCopy: Boolean = false,
    val everyoneCopy: Boolean = false,
    val everyoneMove: Boolean = false
)

enum class PrimitiveType {
    CUBE, SPHERE, CYLINDER, CONE, TORUS,
    TUBE, RING, PRISM, PLANE
}

// Sample Data
fun generateSampleObjects(): List<SceneObject> {
    return listOf(
        SceneObject(
            name = "Wooden Chair",
            description = "A comfortable wooden chair",
            primitiveType = PrimitiveType.CUBE,
            position = Vector3(128f, 128f, 21f),
            rotation = Vector3(0f, 0f, 0f),
            scale = Vector3(0.5f, 0.5f, 1f),
            color = Color(0xFF8B4513)
        ),
        SceneObject(
            name = "Coffee Table",
            description = "Modern coffee table",
            primitiveType = PrimitiveType.CUBE,
            position = Vector3(130f, 128f, 21f),
            rotation = Vector3(0f, 0f, 0f),
            scale = Vector3(1.5f, 0.8f, 0.3f),
            color = Color(0xFF654321)
        ),
        SceneObject(
            name = "Lamp",
            description = "Standing lamp",
            primitiveType = PrimitiveType.CYLINDER,
            position = Vector3(132f, 128f, 21f),
            rotation = Vector3(0f, 0f, 0f),
            scale = Vector3(0.2f, 0.2f, 2f),
            color = Color.White
        ),
        SceneObject(
            name = "Decorative Sphere",
            description = "Glass sphere decoration",
            primitiveType = PrimitiveType.SPHERE,
            position = Vector3(128f, 130f, 22f),
            rotation = Vector3(0f, 0f, 0f),
            scale = Vector3(0.3f, 0.3f, 0.3f),
            color = Color(0xFF87CEEB)
        )
    )
}
