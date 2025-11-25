package com.lumiyaviewer.lumiya.render

import com.lumiyaviewer.lumiya.render.glres.GLResourceManager
import com.lumiyaviewer.lumiya.render.glres.GLQuery
import com.lumiyaviewer.lumiya.render.glres.programs.QuadProgram
import com.lumiyaviewer.lumiya.render.glres.programs.RiggedMeshProgram
import com.lumiyaviewer.lumiya.render.glres.shapes.Quad
import com.lumiyaviewer.lumiya.render.shaders.PrimProgram
import com.lumiyaviewer.lumiya.render.shaders.Shader
import com.lumiyaviewer.lumiya.render.shaders.WaterProgram
import com.lumiyaviewer.rawbuffers.DirectByteBuffer
import java.nio.ByteBuffer

class RenderContext {
    var hasGL20: Boolean = false
    var hasGL30: Boolean = false
    var useVBO: Boolean = false
    var frameCount: Int = 0
    var projectionMatrix: FloatArray = FloatArray(16)
    var currentRiggedMeshProgram: RiggedMeshProgram = RiggedMeshProgram()
    
    var modelViewMatrix: MatrixStack = MatrixStack()
    var viewportRect: IntArray = IntArray(4)
    var quadProgram: QuadProgram = QuadProgram()
    var quad: Quad = Quad()
    var underWater: Boolean = false
    var windlightPreset: com.lumiyaviewer.lumiya.slproto.windlight.WindlightPreset? = null
    var glResourceManager: GLResourceManager = GLResourceManager()
    var drawableStore: DrawableStore? = null
    
    lateinit var curPrimProgram: PrimProgram
    lateinit var primProgram: PrimProgram
    lateinit var waterProgram: WaterProgram
    
    var waterTime: Float = 0f

    fun glModelPushMatrix() {}
    fun glModelPopMatrix() {}
    fun glModelScalef(x: Float, y: Float, z: Float) {}
    fun glModelTranslatef(x: Float, y: Float, z: Float) {}
    fun glObjWorldPushAndMultMatrixf(matrix: FloatArray, offset: Int) {}
    fun glObjWorldPopMatrix() {}
    fun setupRiggedMeshProgram(useUniforms: Boolean) {}
    fun clearRiggedMeshProgram() {}
    
    fun glModelApplyMatrix(handle: Int) {}
    
    fun KeepBuffer(buffer: DirectByteBuffer) {}
    fun KeepTexture(texture: Any) {}
    fun glBindArrayBuffer(handle: Int) {}
    fun glBufferArrayData(size: Int, buffer: ByteBuffer, dynamic: Boolean) {}
    fun glBindElementArrayBuffer(handle: Int) {}
    fun glBufferElementArrayData(size: Int, buffer: ByteBuffer, dynamic: Boolean) {}
    
    fun enqueueOcclusionQuery(query: GLQuery) {}
}
