package com.linkpoint.render.lumiya.core

import android.content.Context
import android.graphics.SurfaceTexture
import android.test.mock.MockContext
import android.view.Surface
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertTrue
import org.junit.Test

class RenderEngineSwitcherTest {

    @Test
    fun `switch from filament to lumiya initializes new engine and renders frames`() {
        val context: Context = MockContext()
        val switcher = RenderEngineSwitcher(context)
        val filamentEngine = FakeRenderEngine("Filament")
        val lumiyaEngine = FakeRenderEngine("Lumiya")

        switcher.registerEngine(RenderEngineSwitcher.EngineType.FILAMENT, filamentEngine)
        switcher.registerEngine(RenderEngineSwitcher.EngineType.LUMIYA, lumiyaEngine)

        val surfaceTexture = SurfaceTexture(0)
        val surface = Surface(surfaceTexture)
        try {
            assertTrue(switcher.initialize(surface, 640, 480))
            assertEquals(1, filamentEngine.initializeCalls)

            switcher.setActiveEngine(RenderEngineSwitcher.EngineType.LUMIYA)
            switcher.renderFrame()

            val activeEngine = switcher.getActiveEngine()
            assertNotNull(activeEngine)
            assertEquals(RenderEngineSwitcher.EngineType.LUMIYA, switcher.getActiveType())
            assertEquals(1, lumiyaEngine.initializeCalls)
            assertEquals(1L, lumiyaEngine.frameCount)
        } finally {
            surface.release()
            surfaceTexture.release()
        }
    }

    private class FakeRenderEngine(
        override val engineName: String,
        private val shouldInitialize: Boolean = true
    ) : RenderEngineProvider {

        override var isInitialized: Boolean = false
            private set
        override var viewportWidth: Int = 0
            private set
        override var viewportHeight: Int = 0
            private set
        override var frameCount: Long = 0L
            private set

        var initializeCalls: Int = 0
            private set

        override fun initialize(context: Context, surface: Surface, width: Int, height: Int): Boolean {
            initializeCalls += 1
            if (!shouldInitialize) {
                isInitialized = false
                return false
            }
            viewportWidth = width
            viewportHeight = height
            isInitialized = true
            return true
        }

        override fun onSurfaceChanged(width: Int, height: Int) {
            viewportWidth = width
            viewportHeight = height
        }

        override fun onSurfaceDestroyed() {
            isInitialized = false
        }

        override fun renderFrame() {
            if (isInitialized) {
                frameCount += 1
            }
        }

        override fun shutdown() {
            isInitialized = false
        }

        override fun setCameraPosition(x: Float, y: Float, z: Float) = Unit

        override fun setCameraTarget(x: Float, y: Float, z: Float) = Unit

        override fun setFieldOfView(fovDegrees: Float) = Unit

        override fun setDrawDistance(distance: Float) = Unit

        override fun addObject(id: Long, posX: Float, posY: Float, posZ: Float) = Unit

        override fun removeObject(id: Long) = Unit

        override fun updateTerrain(heightmap: FloatArray, width: Int, depth: Int) = Unit

        override fun clearScene() = Unit
    }
}
