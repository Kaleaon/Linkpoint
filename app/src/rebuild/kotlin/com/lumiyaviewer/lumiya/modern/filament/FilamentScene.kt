package com.lumiyaviewer.lumiya.modern.filament

import android.content.Context
import android.opengl.Matrix
import android.view.Surface
import android.view.SurfaceView
import com.google.android.filament.Camera
import com.google.android.filament.Engine
import com.google.android.filament.Entity
import com.google.android.filament.EntityManager
import com.google.android.filament.IndirectLight
import com.google.android.filament.LightManager
import com.google.android.filament.Renderer
import com.google.android.filament.Scene
import com.google.android.filament.Skybox
import com.google.android.filament.SwapChain
import com.google.android.filament.View
import com.google.android.filament.Viewport
import com.google.android.filament.android.DisplayHelper
import com.google.android.filament.android.UiHelper
import com.google.android.filament.utils.Float2
import kotlin.math.max

/**
 * Lightweight wrapper around Filament engine components that manages the lifecycle of
 * [Engine], [Renderer], [Scene], [View], [Camera] and swap chains for a [SurfaceView].
 */
internal class FilamentScene(context: Context) {

    private val entityManager = EntityManager.get()

    val engine: Engine = Engine.create()
    val renderer: Renderer = engine.createRenderer()
    val scene: Scene = engine.createScene()
    val view: View = engine.createView()
    private val cameraEntity: Entity = entityManager.create()
    val camera: Camera = engine.createCamera(cameraEntity)

    private val displayHelper = DisplayHelper(context)
    private val uiHelper = UiHelper(UiHelper.ContextErrorPolicy.DONT_CHECK)

    private var surfaceView: SurfaceView? = null
    private var swapChain: SwapChain? = null
    private var lastViewport = Viewport(0, 0, 1, 1)

    init {
        view.scene = scene
        view.camera = camera
        view.antiAliasing = View.AntiAliasing.FXAA

        scene.skybox = Skybox.Builder()
            .color(0.08f, 0.09f, 0.13f, 1.0f)
            .build(engine)

        val sun = entityManager.create()
        LightManager.Builder(LightManager.Type.SUN)
            .color(1.0f, 1.0f, 0.98f)
            .intensity(100_000.0f)
            .direction(0.6f, -1.0f, -0.6f)
            .castShadows(true)
            .build(engine, sun)
        scene.addEntity(sun)

        uiHelper.renderCallback = object : UiHelper.RendererCallback {
            override fun onNativeWindowChanged(surface: Surface) {
                destroySwapChain()
                swapChain = engine.createSwapChain(surface)
                displayHelper.attach(renderer, surfaceView?.display)
            }

            override fun onDetachedFromSurface() {
                displayHelper.detach()
                destroySwapChain()
            }

            override fun onResized(width: Int, height: Int) {
                if (width <= 0 || height <= 0) return
                lastViewport = Viewport(0, 0, width, height)
                view.viewport = lastViewport
                val aspect = max(width, 1) / max(height.toDouble(), 1.0)
                camera.setProjection(
                    Camera.Projection.PERSPECTIVE,
                    45.0,
                    aspect,
                    0.1,
                    1000.0
                )
            }
        }

        setDefaultCameraPose()
    }

    fun attachTo(surfaceView: SurfaceView) {
        this.surfaceView = surfaceView
        uiHelper.attachTo(surfaceView)
    }

    fun detach() {
        surfaceView = null
        uiHelper.detach()
    }

    fun isReadyToRender(): Boolean = uiHelper.isReadyToRender && swapChain != null

    fun render(frameTimeNanos: Long) {
        val targetSwapChain = swapChain ?: return
        if (!uiHelper.isReadyToRender) return
        if (renderer.beginFrame(targetSwapChain, frameTimeNanos)) {
            renderer.render(view)
            renderer.endFrame()
        }
    }

    fun destroy() {
        detach()
        destroySwapChain()
        scene.skybox?.let {
            engine.destroySkybox(it)
        }
        scene.indirectLight?.let {
            engine.destroyIndirectLight(it)
        }

        // Destroy all entities that might still be in the scene (lights, renderables)
        scene.entities.forEach { entity ->
            scene.removeEntity(entity)
            engine.renderableManager.destroy(entity)
            engine.lightManager.destroy(entity)
            engine.destroyEntity(entity)
            entityManager.destroy(entity)
        }

        scene.destroyLights()

        engine.destroyView(view)
        engine.destroyScene(scene)
        engine.destroyRenderer(renderer)
        engine.destroyCameraComponent(cameraEntity)
        engine.destroyEntity(cameraEntity)
        entityManager.destroy(cameraEntity)
        engine.destroy()
    }

    fun setDefaultCameraPose() {
        camera.lookAt(
            0.0, 3.0, 8.0,
            0.0, 1.0, 0.0,
            0.0, 1.0, 0.0
        )
        view.viewport = lastViewport
    }

    private fun destroySwapChain() {
        swapChain?.let {
            engine.destroySwapChain(it)
        }
        swapChain = null
    }
}
