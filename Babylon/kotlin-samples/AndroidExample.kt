package com.babylonkt.examples.android

import android.content.Context
import android.opengl.GLSurfaceView
import android.view.MotionEvent
import com.babylonkt.core.*
import com.babylonkt.graphics.OpenGLESGraphicsDevice
import com.babylonkt.math.Vector3
import com.babylonkt.math.Color3
import com.babylonkt.math.Color4
import javax.microedition.khronos.egl.EGLConfig
import javax.microedition.khronos.opengles.GL10

/**
 * Android Activity example using Babylon Kotlin.
 */
class BabylonActivity : android.app.Activity() {
    
    private lateinit var glSurfaceView: BabylonGLSurfaceView
    
    override fun onCreate(savedInstanceState: android.os.Bundle?) {
        super.onCreate(savedInstanceState)
        
        glSurfaceView = BabylonGLSurfaceView(this)
        setContentView(glSurfaceView)
    }
    
    override fun onResume() {
        super.onResume()
        glSurfaceView.onResume()
    }
    
    override fun onPause() {
        super.onPause()
        glSurfaceView.onPause()
    }
}

/**
 * Custom GLSurfaceView that integrates Babylon Kotlin engine.
 */
class BabylonGLSurfaceView(context: Context) : GLSurfaceView(context) {
    
    private val renderer: BabylonRenderer
    
    init {
        // Set OpenGL ES 3.0 context
        setEGLContextClientVersion(3)
        
        // Create renderer
        renderer = BabylonRenderer(context)
        setRenderer(renderer)
        
        // Render continuously
        renderMode = RENDERMODE_CONTINUOUSLY
    }
    
    override fun onTouchEvent(event: MotionEvent): Boolean {
        renderer.handleTouch(event)
        return true
    }
}

/**
 * Renderer that manages the Babylon Kotlin engine.
 */
class BabylonRenderer(private val context: Context) : GLSurfaceView.Renderer {
    
    private lateinit var engine: Engine
    private lateinit var scene: Scene
    private lateinit var camera: ArcRotateCamera
    
    // Touch handling
    private var lastTouchX = 0f
    private var lastTouchY = 0f
    
    override fun onSurfaceCreated(gl: GL10?, config: EGLConfig?) {
        // Create graphics device
        val graphicsDevice = OpenGLESGraphicsDevice()
        
        // Create engine
        engine = engine(graphicsDevice) {
            setResolution(1920, 1080) // Will be updated in onSurfaceChanged
            setAntialias(true)
            setTargetFPS(60)
        }
        
        // Create scene
        scene = createDemoScene(engine)
        
        println("Babylon Kotlin Engine initialized on Android")
    }
    
    override fun onSurfaceChanged(gl: GL10?, width: Int, height: Int) {
        engine.resize(width, height)
        camera.onResize(width, height)
    }
    
    override fun onDrawFrame(gl: GL10?) {
        scene.render()
    }
    
    private fun createDemoScene(engine: Engine): Scene {
        return scene(engine) {
            // Set clear color
            clearColor = Color4(0.1f, 0.1f, 0.2f, 1f)
            
            // Create camera
            camera = ArcRotateCamera(
                name = "camera",
                alpha = 0f,
                beta = kotlin.math.PI.toFloat() / 4f,
                radius = 10f,
                target = Vector3.Zero(),
                scene = this
            ).apply {
                addCamera(this)
                activeCamera = this
            }
            
            // Create light
            hemisphericLight("light") {
                direction = Vector3.Up()
                intensity = 0.7f
                diffuse = Color3(1f, 1f, 1f)
            }
            
            // Create a spinning cube
            mesh("cube") {
                // Set cube geometry (simplified - would use MeshBuilder in real code)
                setVerticesData(VertexBufferKind.POSITION, createCubeVertices())
                setIndices(createCubeIndices())
                
                // Set material
                material = StandardMaterial("cubeMat", this@scene).apply {
                    diffuseColor = Color3(0.8f, 0.3f, 0.3f)
                    specularColor = Color3(1f, 1f, 1f)
                    specularPower = 32f
                }
                
                // Animate rotation
                rotation = Vector3(0f, 0f, 0f)
            }
            
            // Create ground
            mesh("ground") {
                // Set ground geometry
                setVerticesData(VertexBufferKind.POSITION, createGroundVertices())
                setIndices(createGroundIndices())
                
                material = StandardMaterial("groundMat", this@scene).apply {
                    diffuseColor = Color3(0.3f, 0.5f, 0.3f)
                }
            }
        }
    }
    
    fun handleTouch(event: MotionEvent) {
        when (event.action) {
            MotionEvent.ACTION_DOWN -> {
                lastTouchX = event.x
                lastTouchY = event.y
            }
            MotionEvent.ACTION_MOVE -> {
                val deltaX = event.x - lastTouchX
                val deltaY = event.y - lastTouchY
                
                // Rotate camera
                camera.onMouseDrag(deltaX, deltaY)
                
                lastTouchX = event.x
                lastTouchY = event.y
            }
        }
    }
    
    // Helper functions to create geometry
    private fun createCubeVertices(): FloatArray {
        return floatArrayOf(
            // Front face
            -1f, -1f, 1f,
            1f, -1f, 1f,
            1f, 1f, 1f,
            -1f, 1f, 1f,
            // Back face
            -1f, -1f, -1f,
            -1f, 1f, -1f,
            1f, 1f, -1f,
            1f, -1f, -1f,
            // Top face
            -1f, 1f, -1f,
            -1f, 1f, 1f,
            1f, 1f, 1f,
            1f, 1f, -1f,
            // Bottom face
            -1f, -1f, -1f,
            1f, -1f, -1f,
            1f, -1f, 1f,
            -1f, -1f, 1f,
            // Right face
            1f, -1f, -1f,
            1f, 1f, -1f,
            1f, 1f, 1f,
            1f, -1f, 1f,
            // Left face
            -1f, -1f, -1f,
            -1f, -1f, 1f,
            -1f, 1f, 1f,
            -1f, 1f, -1f
        )
    }
    
    private fun createCubeIndices(): IntArray {
        return intArrayOf(
            0, 1, 2, 0, 2, 3,    // Front
            4, 5, 6, 4, 6, 7,    // Back
            8, 9, 10, 8, 10, 11, // Top
            12, 13, 14, 12, 14, 15, // Bottom
            16, 17, 18, 16, 18, 19, // Right
            20, 21, 22, 20, 22, 23  // Left
        )
    }
    
    private fun createGroundVertices(): FloatArray {
        return floatArrayOf(
            -10f, 0f, -10f,
            10f, 0f, -10f,
            10f, 0f, 10f,
            -10f, 0f, 10f
        )
    }
    
    private fun createGroundIndices(): IntArray {
        return intArrayOf(0, 1, 2, 0, 2, 3)
    }
}

/**
 * Example of a more complex Android game using Babylon Kotlin.
 */
class GameScene(engine: Engine) {
    
    private val scene: Scene
    private val player: Mesh
    private val enemies = mutableListOf<Mesh>()
    
    init {
        scene = Scene(engine)
        
        // Setup scene
        scene.clearColor = Color4(0.5f, 0.7f, 1f, 1f)
        
        // Create camera
        val camera = FollowCamera("followCamera", Vector3(0f, 5f, -10f), scene)
        camera.radius = 15f
        camera.heightOffset = 5f
        scene.addCamera(camera)
        scene.activeCamera = camera
        
        // Create sun light
        val sun = DirectionalLight("sun", Vector3(-1f, -2f, -1f), scene)
        sun.intensity = 1f
        scene.addLight(sun)
        
        // Create player
        player = createPlayer()
        camera.followedMesh = player
        
        // Create enemies
        repeat(5) {
            val enemy = createEnemy(it)
            enemies.add(enemy)
        }
        
        // Create environment
        createEnvironment()
    }
    
    private fun createPlayer(): Mesh {
        val player = Mesh("player", scene)
        
        // Create player geometry (simplified capsule)
        player.position = Vector3(0f, 1f, 0f)
        player.scaling = Vector3(1f, 2f, 1f)
        
        // Player material
        player.material = PBRMaterial("playerMat", scene).apply {
            albedoColor = Color3(0.2f, 0.5f, 1f)
            metallic = 0.3f
            roughness = 0.7f
        }
        
        scene.addMesh(player)
        return player
    }
    
    private fun createEnemy(index: Int): Mesh {
        val enemy = Mesh("enemy$index", scene)
        
        // Position enemies in a circle
        val angle = (index * 2f * kotlin.math.PI.toFloat()) / 5f
        val radius = 20f
        enemy.position = Vector3(
            kotlin.math.cos(angle) * radius,
            1f,
            kotlin.math.sin(angle) * radius
        )
        
        // Enemy material
        enemy.material = StandardMaterial("enemyMat$index", scene).apply {
            diffuseColor = Color3(1f, 0.2f, 0.2f)
            emissiveColor = Color3(0.2f, 0f, 0f)
        }
        
        scene.addMesh(enemy)
        return enemy
    }
    
    private fun createEnvironment() {
        // Create ground
        val ground = Mesh("ground", scene)
        ground.position = Vector3(0f, 0f, 0f)
        ground.scaling = Vector3(50f, 1f, 50f)
        
        ground.material = PBRMaterial("groundMat", scene).apply {
            albedoColor = Color3(0.3f, 0.6f, 0.3f)
            roughness = 0.9f
        }
        
        scene.addMesh(ground)
        
        // Create some obstacles
        repeat(10) {
            val obstacle = Mesh("obstacle$it", scene)
            obstacle.position = Vector3(
                (Math.random().toFloat() - 0.5f) * 40f,
                2f,
                (Math.random().toFloat() - 0.5f) * 40f
            )
            obstacle.scaling = Vector3(2f, 4f, 2f)
            
            obstacle.material = StandardMaterial("obstacleMat$it", scene).apply {
                diffuseColor = Color3(0.5f, 0.5f, 0.5f)
            }
            
            scene.addMesh(obstacle)
        }
    }
    
    fun update(deltaTime: Float) {
        // Update player movement
        // (Would be controlled by touch input in real game)
        
        // Update enemies (simple AI)
        enemies.forEach { enemy ->
            // Move towards player
            val direction = (player.position - enemy.position).normalize()
            enemy.position += direction * 2f * deltaTime
            
            // Rotate to face player
            val angle = kotlin.math.atan2(direction.x, direction.z)
            enemy.rotation = Vector3(0f, angle, 0f)
        }
    }
    
    fun render() {
        val deltaTime = scene.engine.getDeltaTime()
        update(deltaTime)
        scene.render()
    }
    
    fun dispose() {
        scene.dispose()
    }
}