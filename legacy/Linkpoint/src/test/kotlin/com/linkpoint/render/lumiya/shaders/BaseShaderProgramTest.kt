package com.linkpoint.render.lumiya.shaders

import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner

@RunWith(RobolectricTestRunner::class)
class BaseShaderProgramTest {

    private class ConcreteShaderProgram(programHandle: Int) : BaseShaderProgram() {
        override val vertexSource: String = ""
        override val fragmentSource: String = ""

        init {
            handle = programHandle
        }

        override fun onBind() {}
    }

    @Before
    fun setUp() {
        BaseShaderProgram.resetActiveProgram()
    }

    @Test
    fun testActiveProgramHandleTrackingAndRedundantUseSkip() {
        assertEquals(0, BaseShaderProgram.activeProgramHandle)

        val shader1 = ConcreteShaderProgram(101)
        val shader2 = ConcreteShaderProgram(102)

        // First use sets activeProgramHandle
        shader1.use()
        assertEquals(101, BaseShaderProgram.activeProgramHandle)

        // Repeated use on same shader preserves active handle
        shader1.use()
        assertEquals(101, BaseShaderProgram.activeProgramHandle)

        // Switching shader updates active handle
        shader2.use()
        assertEquals(102, BaseShaderProgram.activeProgramHandle)
    }

    @Test
    fun testResetActiveProgram() {
        val shader = ConcreteShaderProgram(201)
        shader.use()
        assertEquals(201, BaseShaderProgram.activeProgramHandle)

        BaseShaderProgram.resetActiveProgram()
        assertEquals(0, BaseShaderProgram.activeProgramHandle)
    }

    @Test
    fun testDestroyClearsActiveProgramHandleIfActive() {
        val shader1 = ConcreteShaderProgram(301)
        val shader2 = ConcreteShaderProgram(302)

        shader1.use()
        assertEquals(301, BaseShaderProgram.activeProgramHandle)

        // Destroying an inactive shader should not clear active handle
        shader2.destroy()
        assertEquals(301, BaseShaderProgram.activeProgramHandle)

        // Destroying active shader clears active handle
        shader1.destroy()
        assertEquals(0, BaseShaderProgram.activeProgramHandle)
        assertEquals(0, shader1.handle)
    }
}
