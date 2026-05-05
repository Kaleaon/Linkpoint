package com.linkpoint.render.water

import android.util.Log
import com.google.android.filament.*
import com.google.android.filament.VertexBuffer.AttributeType
import com.google.android.filament.VertexBuffer.VertexAttribute
import com.linkpoint.protocol.types.LLVector3
import java.nio.ByteBuffer
import java.nio.ByteOrder
import kotlin.math.*

/**
 * Renders water surfaces with reflections and waves
 * Based on SL's Windlight water settings
 */
class WaterRenderer(
    private val engine: Engine,
    private val scene: Scene
) {
    companion object {
        private const val TAG = "WaterRenderer"
        const val WATER_SIZE = 256f
    }
    
    private var waterEntity: Int = 0
    private var vertexBuffer: VertexBuffer? = null
    private var indexBuffer: IndexBuffer? = null
    private var materialInstance: MaterialInstance? = null

    // Water settings (Windlight)
    private var waterHeight = 20f
    private var waveDirection1 = LLVector3(1f, 0f, 0f)
    private var waveDirection2 = LLVector3(0f, 1f, 0f)
    private var scaleAbove = 0.025f
    private var scaleBelow = 0.2f
    private var blurMultiplier = 0.04f
    private var fresnelScale = 0.4f
    private var fresnelOffset = 0.5f
    private var normalScale = LLVector3(2f, 2f, 2f)
    private var normalMapTexture: Texture? = null

    /**
     * Planar reflection target. Equivalent of LL viewer
     * `LLPipeline::mWaterRef`. Producers render the world (with the
     * camera mirrored across the water plane) into this texture, then
     * the water shader samples it perturbed by the wave normal.
     * Null when planar reflections are disabled.
     */
    private var mWaterRef: Texture? = null

    /**
     * Refraction / "distortion" target. Equivalent of
     * `LLPipeline::mWaterDis`. Holds the under-water scene rendered
     * with a clip plane at the water height. The water shader
     * samples this with a UV offset to fake refraction. Null when
     * planar reflections are disabled.
     */
    private var mWaterDis: Texture? = null

    private var time = 0f
    
    /**
     * Initialize water renderer
     */
    fun initialize(material: Material) {
        materialInstance = material.createInstance()
        
        createWaterMesh()
        scene.addEntity(waterEntity)
        
        Log.i(TAG, "Water renderer initialized")
    }
    
    /**
     * Set water height
     */
    fun setWaterHeight(height: Float) {
        waterHeight = height
        updateTransform()
    }
    
    /**
     * Apply Windlight water settings
     */
    fun applyWindlightSettings(settings: WaterSettings) {
        waveDirection1 = settings.wave1Dir
        waveDirection2 = settings.wave2Dir
        scaleAbove = settings.scaleAbove
        scaleBelow = settings.scaleBelow
        blurMultiplier = settings.blurMultiplier
        fresnelScale = settings.fresnelScale
        fresnelOffset = settings.fresnelOffset
        
        updateMaterialParams()
    }
    
    /**
     * Set normal map texture
     */
    fun setNormalMap(texture: Texture) {
        normalMapTexture = texture
        updateMaterialParams()
    }

    /**
     * Bind planar reflection ([mWaterRef]) and refraction ([mWaterDis])
     * targets. Pass `null` for either to clear that channel — the
     * material falls back to a plain Fresnel-tinted base colour, the
     * same path the renderer takes when planar reflections are
     * disabled at the device-tier level.
     *
     * Lineage: LLPipeline::generateWaterReflection writes mWaterRef
     * + mWaterDis once per frame and binds them on the next water
     * draw. Producers should follow the same one-shot-per-frame
     * cadence; we don't otherwise hold a reference past the draw.
     */
    fun setReflectionRefractionTargets(reflection: Texture?, refraction: Texture?) {
        mWaterRef = reflection
        mWaterDis = refraction
        updateMaterialParams()
    }
    
    /**
     * Update water animation
     */
    fun update(deltaTime: Float) {
        time += deltaTime

        // "time" is optional on the bound material — wrap so missing
        // uniforms don't tear the render thread down on devices that
        // fell back to the lit material because the wave-shader build
        // failed.
        runCatching { materialInstance?.setParameter("time", time) }
    }
    
    private fun createWaterMesh() {
        val resolution = 64
        val vertexCount = resolution * resolution
        val indexCount = (resolution - 1) * (resolution - 1) * 6
        
        val stride = 5 * 4 // position (3) + uv (2)
        val vertexData = ByteBuffer.allocateDirect(vertexCount * stride)
            .order(ByteOrder.nativeOrder())
        
        val scale = WATER_SIZE / (resolution - 1)
        
        for (y in 0 until resolution) {
            for (x in 0 until resolution) {
                vertexData.putFloat(x * scale)
                vertexData.putFloat(y * scale)
                vertexData.putFloat(0f) // Z is always 0, we transform later
                vertexData.putFloat(x.toFloat() / (resolution - 1))
                vertexData.putFloat(y.toFloat() / (resolution - 1))
            }
        }
        vertexData.flip()
        
        val indexData = ByteBuffer.allocateDirect(indexCount * 2)
            .order(ByteOrder.nativeOrder())
        
        for (y in 0 until resolution - 1) {
            for (x in 0 until resolution - 1) {
                val i00 = y * resolution + x
                val i10 = i00 + 1
                val i01 = i00 + resolution
                val i11 = i01 + 1
                
                indexData.putShort(i00.toShort())
                indexData.putShort(i10.toShort())
                indexData.putShort(i01.toShort())
                
                indexData.putShort(i10.toShort())
                indexData.putShort(i11.toShort())
                indexData.putShort(i01.toShort())
            }
        }
        indexData.flip()
        
        vertexBuffer = VertexBuffer.Builder()
            .vertexCount(vertexCount)
            .bufferCount(1)
            .attribute(VertexAttribute.POSITION, 0, AttributeType.FLOAT3, 0, stride)
            .attribute(VertexAttribute.UV0, 0, AttributeType.FLOAT2, 12, stride)
            .build(engine)
        
        vertexBuffer?.setBufferAt(engine, 0, vertexData)
        
        indexBuffer = IndexBuffer.Builder()
            .indexCount(indexCount)
            .bufferType(IndexBuffer.Builder.IndexType.USHORT)
            .build(engine)
        
        indexBuffer?.setBuffer(engine, indexData)
        
        waterEntity = EntityManager.get().create()
        
        // Validate required components
        val vb = vertexBuffer ?: throw IllegalStateException("Water vertex buffer not initialized")
        val ib = indexBuffer ?: throw IllegalStateException("Water index buffer not initialized")
        val mat = materialInstance ?: throw IllegalStateException("Water material instance not initialized")
        
        RenderableManager.Builder(1)
            .boundingBox(Box(0f, 0f, -1f, 256f, 256f, 1f))
            .geometry(0, RenderableManager.PrimitiveType.TRIANGLES, vb, ib)
            .material(0, mat)
            .build(engine, waterEntity)
        
        // Create transform
        val tm = engine.transformManager
        tm.create(waterEntity)
        
        updateTransform()
    }
    
    private fun updateTransform() {
        val tm = engine.transformManager
        val ti = tm.getInstance(waterEntity)
        
        val matrix = FloatArray(16).apply {
            this[0] = 1f; this[5] = 1f; this[10] = 1f; this[15] = 1f
            this[14] = waterHeight
        }
        
        tm.setTransform(ti, matrix)
    }
    
    private fun updateMaterialParams() {
        materialInstance?.apply {
            setParameter("waveDir1", waveDirection1.x, waveDirection1.y)
            setParameter("waveDir2", waveDirection2.x, waveDirection2.y)
            setParameter("scaleAbove", scaleAbove)
            setParameter("scaleBelow", scaleBelow)
            setParameter("blurMultiplier", blurMultiplier)
            setParameter("fresnelScale", fresnelScale)
            setParameter("fresnelOffset", fresnelOffset)
            
            normalMapTexture?.let {
                setParameter("normalMap", it, TextureSampler(
                    TextureSampler.MinFilter.LINEAR_MIPMAP_LINEAR,
                    TextureSampler.MagFilter.LINEAR,
                    TextureSampler.WrapMode.REPEAT
                ))
            }

            // Planar reflection / refraction samplers. Material is
            // expected to declare `reflectionMap` / `refractionMap`
            // optional sampler2D parameters; when not declared the
            // setParameter call is a silent no-op so older Filament
            // material packages keep working.
            mWaterRef?.let {
                runCatching {
                    setParameter("reflectionMap", it, TextureSampler(
                        TextureSampler.MinFilter.LINEAR,
                        TextureSampler.MagFilter.LINEAR,
                        TextureSampler.WrapMode.CLAMP_TO_EDGE
                    ))
                }
            }
            mWaterDis?.let {
                runCatching {
                    setParameter("refractionMap", it, TextureSampler(
                        TextureSampler.MinFilter.LINEAR,
                        TextureSampler.MagFilter.LINEAR,
                        TextureSampler.WrapMode.CLAMP_TO_EDGE
                    ))
                }
            }
        }
    }
    
    fun shutdown() {
        if (waterEntity != 0) {
            scene.removeEntity(waterEntity)
            engine.destroyEntity(waterEntity)
        }
        vertexBuffer?.let { engine.destroyVertexBuffer(it) }
        indexBuffer?.let { engine.destroyIndexBuffer(it) }
        materialInstance?.let { engine.destroyMaterialInstance(it) }
    }
}

data class WaterSettings(
    val wave1Dir: LLVector3 = LLVector3(1.05f, -0.42f, 0f),
    val wave2Dir: LLVector3 = LLVector3(1.11f, -1.16f, 0f),
    val scaleAbove: Float = 0.025f,
    val scaleBelow: Float = 0.2f,
    val blurMultiplier: Float = 0.04f,
    val fresnelScale: Float = 0.4f,
    val fresnelOffset: Float = 0.5f,
    val normalScale: LLVector3 = LLVector3(2f, 2f, 2f),
    val fogColor: LLVector3 = LLVector3(0.21f, 0.34f, 0.39f),
    val fogDensity: Float = 16f
)
