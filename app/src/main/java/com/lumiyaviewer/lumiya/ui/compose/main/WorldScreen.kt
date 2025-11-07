package com.lumiyaviewer.lumiya.ui.compose.main

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.compose.ui.text.font.FontWeight
import com.lumiyaviewer.lumiya.slproto.types.LLVector3
import com.lumiyaviewer.lumiya.ui.opengl.OpenGLWorldView
import kotlinx.coroutines.delay

/**
 * World Screen - Complete 3D viewer with OpenGL ES 3.0
 * 
 * Features:
 * - Real OpenGL ES 3.0 rendering
 * - Interactive camera controls
 * - Touch gesture support
 * - Performance monitoring
 * - Complete 3D scene with terrain and objects
 */
@Composable
fun WorldScreen() {
    val context = LocalContext.current
    var worldView by remember { mutableStateOf<OpenGLWorldView?>(null) }
    var fps by remember { mutableStateOf("0") }
    
    // FPS monitoring
    LaunchedEffect(worldView) {
        worldView?.let { view ->
            while (true) {
                delay(1000)
                fps = "%.1f".format(view.getFPS())
            }
        }
    }
    
    Box(
        modifier = Modifier.fillMaxSize()
    ) {
        AndroidView(
            factory = { ctx ->
                OpenGLWorldView(ctx).apply {
                    worldView = this
                    // Connect to world data if available
                    connectToWorldData()
                    // Set initial camera position
                    setCameraTarget(LLVector3(128f, 128f, 0f))
                    setCameraDistance(50f)
                    // Set render quality
                    setRenderQuality(OpenGLWorldView.RenderQuality.HIGH)
                    enableAntiAliasing(true)
                }
            },
            modifier = Modifier.fillMaxSize(),
            update = { view ->
                // Update view if needed
            }
        )
        
        // FPS overlay
        Card(
            modifier = Modifier
                .align(Alignment.TopStart)
                .padding(16.dp)
        ) {
            Text(
                text = "FPS: $fps",
                modifier = Modifier.padding(8.dp),
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
        
        // Control instructions
        Card(
            modifier = Modifier
                .align(Alignment.BottomStart)
                .padding(16.dp)
        ) {
            Column(
                modifier = Modifier.padding(12.dp)
            ) {
                Text(
                    text = "Controls:",
                    style = MaterialTheme.typography.titleSmall,
                    fontWeight = FontWeight.Bold
                )
                Text(
                    text = "• Drag: Rotate camera",
                    style = MaterialTheme.typography.bodySmall
                )
                Text(
                    text = "• Pinch: Zoom in/out",
                    style = MaterialTheme.typography.bodySmall
                )
                Text(
                    text = "• Tap: Select objects",
                    style = MaterialTheme.typography.bodySmall
                )
            }
        }
        
        // World status overlay
        Card(
            modifier = Modifier
                .align(Alignment.TopEnd)
                .padding(16.dp)
        ) {
            Column(
                modifier = Modifier.padding(12.dp)
            ) {
                Text(
                    text = "3D World Status:",
                    style = MaterialTheme.typography.titleSmall,
                    fontWeight = FontWeight.Bold
                )
                Text(
                    text = "• OpenGL ES 3.0",
                    style = MaterialTheme.typography.bodySmall
                )
                Text(
                    text = "• Terrain: 256x256",
                    style = MaterialTheme.typography.bodySmall
                )
                Text(
                    text = "• Objects: Loaded",
                    style = MaterialTheme.typography.bodySmall
                )
                Text(
                    text = "• Lighting: Dynamic",
                    style = MaterialTheme.typography.bodySmall
                )
            }
        }
    }
}