package com.linkpoint.graphics

import android.content.Context
import android.opengl.Matrix
import android.view.Choreographer
import android.view.SurfaceHolder
import android.view.SurfaceView
import com.google.android.filament.Engine
import com.google.android.filament.LightManager
import com.google.android.filament.EntityManager
import com.google.android.filament.View as FilamentView
import com.google.android.filament.utils.ModelViewer
import com.linkpoint.graphics.LinkpointRenderer.RendererListener
import java.io.IOException
import java.nio.ByteBuffer
import java.nio.ByteOrder
import java.util.concurrent.atomic.AtomicBoolean
import kotlin.math.max

class LinkpointRenderer(
    private val context: Context,
    private val surfaceView: SurfaceView,
    private val listener: RendererListener? = null
) : SurfaceHolder.Callback, Choreographer.FrameCallback {

    interface RendererListener {
        fun onRendererReady()
        fun onFrame(frameTimeMillis: Float, fps: Float)
        fun onRendererError(throwable: Throwable)
    }

    private val choreographer = Choreographer.getInstance()
    private val running = AtomicBoolean(false)
    private var lastFrameTimeNs = 0L

    private val modelViewer: ModelViewer = ModelViewer(surfaceView)

    private val sunlight = EntityManager.get().create()

    init {
        surfaceView.keepScreenOn = true
        surfaceView.holder.addCallback(this)
        surfaceView.setOnTouchListener { _, motionEvent ->
            modelViewer.onTouchEvent(motionEvent)
            true
        }
        configureView()
    }

    private fun configureView() {
        modelViewer.view.apply {
            ambientOcclusion = FilamentView.AmbientOcclusion.SSAO
            antiAliasing = FilamentView.AntiAliasing.FXAA
        }

        modelViewer.scene.skybox = com.google.android.filament.Skybox.Builder()
            .color(0.05f, 0.05f, 0.08f, 1.0f)
            .build(modelViewer.engine)

        LightManager.Builder(LightManager.Type.SUN)
            .color(1.0f, 0.95f, 0.85f)
            .direction(-0.3f, -1.0f, -0.2f)
            .intensity(110_000.0f)
            .castShadows(true)
            .sunAngularRadius(1.9f)
            .build(modelViewer.engine, sunlight)

        modelViewer.scene.addEntity(sunlight)
        resetCamera()
        loadModel()
    }

    private fun loadModel() {
        try {
            context.assets.open(MODEL_PATH).use { input ->
                val bytes = input.readBytes()
                val buffer = ByteBuffer.allocateDirect(bytes.size)
                    .order(ByteOrder.nativeOrder())
                buffer.put(bytes)
                buffer.flip()
                modelViewer.loadModelGlb(buffer)
            }
            modelViewer.transformToUnitCube()
            modelViewer.asset?.entities?.let { entities ->
                modelViewer.scene.addEntities(entities)
            }
            listener?.onRendererReady()
        } catch (ioe: IOException) {
            listener?.onRendererError(ioe)
        }
    }

    fun resetCamera() {
        modelViewer.camera.apply {
            lookAt(
                0.0, 0.05, 4.5,
                0.0, 0.0, 0.0,
                0.0, 1.0, 0.0
            )
        }
    }

    fun start() {
        if (running.compareAndSet(false, true)) {
            choreographer.postFrameCallback(this)
        }
    }

    fun stop() {
        if (running.compareAndSet(true, false)) {
            choreographer.removeFrameCallback(this)
        }
    }

    fun destroy() {
        stop()
        modelViewer.destroyModel()
        modelViewer.engine.destroyEntity(sunlight)
        EntityManager.get().destroy(sunlight)
    }

    override fun surfaceCreated(holder: SurfaceHolder) = Unit

    override fun surfaceChanged(holder: SurfaceHolder, format: Int, width: Int, height: Int) {
        modelViewer.view.viewport = com.google.android.filament.Viewport(0, 0, width, height)
    }

    override fun surfaceDestroyed(holder: SurfaceHolder) {
        stop()
    }

    override fun doFrame(frameTimeNanos: Long) {
        if (!running.get()) return
        if (lastFrameTimeNs == 0L) {
            lastFrameTimeNs = frameTimeNanos
        }
        val deltaNs = frameTimeNanos - lastFrameTimeNs
        lastFrameTimeNs = frameTimeNanos
        val deltaMs = max(deltaNs / 1_000_000f, 0.0001f)
        val fps = 1000f / deltaMs

        animate(frameTimeNanos)
        modelViewer.render(frameTimeNanos)
        listener?.onFrame(deltaMs, fps)

        choreographer.postFrameCallback(this)
    }

    private fun animate(frameTimeNanos: Long) {
        modelViewer.asset?.let { asset ->
            val transformManager = modelViewer.engine.transformManager
            val root = transformManager.getInstance(asset.root)
            val matrix = FloatArray(16)
            Matrix.setIdentityM(matrix, 0)
            val seconds = frameTimeNanos / 1_000_000_000f
            Matrix.rotateM(matrix, 0, (seconds * 10f) % 360f, 0f, 1f, 0f)
            Matrix.translateM(matrix, 0, 0f, -0.1f, 0f)
            transformManager.setTransform(root, matrix)
        }
    }

    companion object {
        private const val MODEL_PATH = "models/linkpoint_world.glb"
    }
}
