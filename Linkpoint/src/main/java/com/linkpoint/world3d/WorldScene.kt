package com.linkpoint.world3d

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import dev.romainguy.kotlin.math.Float3
import io.github.sceneview.Scene
import io.github.sceneview.collision.CollisionSystem
import io.github.sceneview.environment.Environment
import io.github.sceneview.loaders.EnvironmentLoader
import io.github.sceneview.loaders.MaterialLoader
import io.github.sceneview.loaders.ModelLoader
import io.github.sceneview.node.CameraNode
import io.github.sceneview.node.LightNode
import io.github.sceneview.node.ModelNode
import io.github.sceneview.node.Node
import io.github.sceneview.rememberCameraManipulator
import io.github.sceneview.rememberCameraNode
import io.github.sceneview.rememberCollisionSystem
import io.github.sceneview.rememberEngine
import io.github.sceneview.rememberEnvironment
import io.github.sceneview.rememberEnvironmentLoader
import io.github.sceneview.rememberMainLightNode
import io.github.sceneview.rememberMaterialLoader
import io.github.sceneview.rememberModelLoader
import io.github.sceneview.rememberNodes
import io.github.sceneview.rememberRenderer
import io.github.sceneview.rememberScene
import io.github.sceneview.rememberView

/**
 * Compose-based 3D World View using SceneView (Filament wrapper).
 * 
 * This composable provides a full 3D rendering surface for the Second Life/OpenSim world,
 * with support for:
 * - glTF/GLB model loading (avatars, objects, terrain)
 * - PBR materials and lighting
 * - Camera manipulation (orbit, pan, zoom)
 * - Collision detection
 * - HDR environment lighting
 * 
 * Integration with libGDX is handled through the WorldBridge class which
 * provides game logic, input processing, and cross-platform compatibility.
 */
@Composable
fun WorldScene(
    modifier: Modifier = Modifier,
    worldState: WorldState = rememberWorldState(),
    onModelTapped: ((ModelNode) -> Unit)? = null,
    onWorldTapped: ((Float3) -> Unit)? = null,
    onFrameUpdate: ((Long) -> Unit)? = null
) {
    // Filament engine and core components
    val engine = rememberEngine()
    val view = rememberView(engine)
    val renderer = rememberRenderer(engine)
    val scene = rememberScene(engine)
    
    // Asset loaders
    val modelLoader = rememberModelLoader(engine)
    val materialLoader = rememberMaterialLoader(engine)
    val environmentLoader = rememberEnvironmentLoader(engine)
    
    // Collision system for hit testing
    val collisionSystem = rememberCollisionSystem(view)
    
    // Main directional light (sun)
    val mainLightNode = rememberMainLightNode(engine) {
        intensity = worldState.sunIntensity
        // Direction will be updated based on world time
    }
    
    // Camera node
    val cameraNode = rememberCameraNode(engine) {
        position = worldState.cameraPosition
    }
    
    // Camera manipulator for user interaction
    val cameraManipulator = rememberCameraManipulator()
    
    // Environment (skybox + IBL)
    val environment = rememberEnvironment(environmentLoader) {
        worldState.environmentHdrPath?.let { hdrPath ->
            try {
                environmentLoader.createHDREnvironment(assetFileLocation = hdrPath)
            } catch (e: Exception) {
                null
            }
        }
    }
    
    // Dynamic nodes from world state
    val childNodes = rememberNodes {
        worldState.modelNodes.forEach { modelData ->
            add(createModelNode(modelLoader, materialLoader, modelData))
        }
    }
    
    // Update nodes when world state changes
    DisposableEffect(worldState.modelNodes) {
        onDispose { }
    }
    
    Box(modifier = modifier.fillMaxSize()) {
        Scene(
            modifier = Modifier.fillMaxSize(),
            engine = engine,
            view = view,
            renderer = renderer,
            scene = scene,
            modelLoader = modelLoader,
            materialLoader = materialLoader,
            environmentLoader = environmentLoader,
            collisionSystem = collisionSystem,
            mainLightNode = mainLightNode,
            environment = environment,
            cameraNode = cameraNode,
            cameraManipulator = cameraManipulator,
            childNodes = childNodes,
            onFrame = { frameTimeNanos ->
                onFrameUpdate?.invoke(frameTimeNanos)
                
                // Update camera from world state
                cameraNode.position = worldState.cameraPosition
                cameraNode.lookAt(worldState.cameraTarget)
            },
            onTouchEvent = { event, hitResult ->
                hitResult?.let { hit ->
                    onWorldTapped?.invoke(hit.worldPosition)
                }
                false
            },
            onGestureListener = rememberOnGestureListener(
                onSingleTapConfirmed = { event, node ->
                    (node as? ModelNode)?.let { modelNode ->
                        onModelTapped?.invoke(modelNode)
                    }
                }
            )
        )
    }
}

/**
 * Creates a ModelNode from world model data.
 */
private fun createModelNode(
    modelLoader: ModelLoader,
    materialLoader: MaterialLoader,
    modelData: WorldModelData
): Node {
    return try {
        ModelNode(
            modelInstance = modelLoader.createModelInstance(
                assetFileLocation = modelData.assetPath
            ),
            scaleToUnits = modelData.scale
        ).apply {
            position = modelData.position
            rotation = modelData.rotation
        }
    } catch (e: Exception) {
        // Return empty node if model fails to load
        Node()
    }
}

/**
 * Remember a gesture listener for SceneView.
 */
@Composable
private fun rememberOnGestureListener(
    onSingleTapConfirmed: ((android.view.MotionEvent, Node?) -> Unit)? = null,
    onDoubleTap: ((android.view.MotionEvent, Node?) -> Unit)? = null,
    onLongPress: ((android.view.MotionEvent, Node?) -> Unit)? = null
): io.github.sceneview.gesture.GestureDetector.OnGestureListener {
    return remember {
        object : io.github.sceneview.gesture.GestureDetector.OnGestureListener {
            override fun onSingleTapConfirmed(e: android.view.MotionEvent, node: Node?) {
                onSingleTapConfirmed?.invoke(e, node)
            }
            
            override fun onDoubleTap(e: android.view.MotionEvent, node: Node?) {
                onDoubleTap?.invoke(e, node)
            }
            
            override fun onLongPress(e: android.view.MotionEvent, node: Node?) {
                onLongPress?.invoke(e, node)
            }
            
            override fun onDown(e: android.view.MotionEvent, node: Node?) {}
            override fun onShowPress(e: android.view.MotionEvent, node: Node?) {}
            override fun onSingleTapUp(e: android.view.MotionEvent, node: Node?) {}
            override fun onScroll(e1: android.view.MotionEvent?, e2: android.view.MotionEvent, node: Node?, distance: Float2) {}
            override fun onFling(e1: android.view.MotionEvent?, e2: android.view.MotionEvent, node: Node?, velocity: Float2) {}
            override fun onDoubleTapEvent(e: android.view.MotionEvent, node: Node?) {}
            override fun onContextClick(e: android.view.MotionEvent, node: Node?) {}
            override fun onMoveBegin(detector: io.github.sceneview.gesture.MoveGestureDetector, e: android.view.MotionEvent, node: Node?) {}
            override fun onMove(detector: io.github.sceneview.gesture.MoveGestureDetector, e: android.view.MotionEvent, node: Node?) {}
            override fun onMoveEnd(detector: io.github.sceneview.gesture.MoveGestureDetector, e: android.view.MotionEvent, node: Node?) {}
            override fun onRotateBegin(detector: io.github.sceneview.gesture.RotateGestureDetector, e: android.view.MotionEvent, node: Node?) {}
            override fun onRotate(detector: io.github.sceneview.gesture.RotateGestureDetector, e: android.view.MotionEvent, node: Node?) {}
            override fun onRotateEnd(detector: io.github.sceneview.gesture.RotateGestureDetector, e: android.view.MotionEvent, node: Node?) {}
            override fun onScaleBegin(detector: io.github.sceneview.gesture.ScaleGestureDetector, e: android.view.MotionEvent, node: Node?) {}
            override fun onScale(detector: io.github.sceneview.gesture.ScaleGestureDetector, e: android.view.MotionEvent, node: Node?) {}
            override fun onScaleEnd(detector: io.github.sceneview.gesture.ScaleGestureDetector, e: android.view.MotionEvent, node: Node?) {}
        }
    }
}

// Type alias for Float2 used in gestures
private typealias Float2 = dev.romainguy.kotlin.math.Float2
