package com.linkpoint.render.particles

import android.util.Log
import com.google.android.filament.*
import com.linkpoint.assets.TextureManager
import com.linkpoint.protocol.types.LLColor4
import com.linkpoint.protocol.types.LLVector3
import java.nio.ByteBuffer
import java.nio.ByteOrder
import java.util.UUID
import java.util.concurrent.ConcurrentHashMap
import kotlin.math.*
import kotlin.random.Random

/**
 * Second Life particle system renderer
 * Implements SL particle parameters and behavior
 */
class ParticleSystem(
    private val engine: Engine,
    private val scene: Scene,
    @Suppress("unused") private val textureManager: TextureManager? = null
) {
    companion object {
        private const val TAG = "ParticleSystem"
        
        // Maximum particles per source
        const val MAX_PARTICLES_PER_SOURCE = 256
        
        // Pattern flags
        const val PATTERN_DROP = 0x01
        const val PATTERN_EXPLODE = 0x02
        const val PATTERN_ANGLE = 0x04
        const val PATTERN_ANGLE_CONE = 0x08
        const val PATTERN_ANGLE_CONE_EMPTY = 0x10
        
        // Source/target flags
        const val TARGET_LINEAR = 0x01
        const val EMISSIVE = 0x02
        const val BOUNCE = 0x04
        const val WIND = 0x08
        const val FOLLOW_SRC = 0x10
        const val FOLLOW_VELOCITY = 0x20
        const val INTERP_COLOR = 0x40
        const val INTERP_SCALE = 0x80
        const val TARGET_POS = 0x100
        const val RIBBON = 0x400
    }
    
    private val particleSources = ConcurrentHashMap<Int, ParticleSource>()
    private var vertexBuffer: VertexBuffer? = null
    private var indexBuffer: IndexBuffer? = null
    private var materialInstance: MaterialInstance? = null
    // Renderable entity that draws the entire particle pool. Without this
    // the per-frame updateBuffers() refreshes the VertexBuffer contents
    // but Filament has nothing bound to those buffers, so particles never
    // appear on screen even when sources spawn them.
    private var particleEntity: Int = 0
    private var maxParticles: Int = 0

    private val particlePool = mutableListOf<Particle>()
    private var time = 0f
    
    /**
     * Initialize particle system
     */
    fun initialize(material: Material) {
        materialInstance = material.createInstance()
        
        createBuffers()
        
        Log.i(TAG, "Particle system initialized")
    }
    
    /**
     * Add particle source
     */
    fun addSource(localId: Int, params: ParticleParams, position: LLVector3) {
        val source = ParticleSource(
            localId = localId,
            params = params,
            position = position,
            particles = mutableListOf()
        )
        particleSources[localId] = source
    }
    
    /**
     * Update particle source position
     */
    fun updateSourcePosition(localId: Int, position: LLVector3) {
        particleSources[localId]?.position = position
    }
    
    /**
     * Remove particle source
     */
    fun removeSource(localId: Int) {
        particleSources.remove(localId)
    }
    
    /**
     * Update all particles
     */
    fun update(deltaTime: Float, cameraPosition: LLVector3, wind: LLVector3) {
        time += deltaTime

        // The shared particle material may or may not declare a "time"
        // uniform — older fallback materials don't. Wrap the set so a
        // missing parameter doesn't tear the render thread down.
        runCatching { materialInstance?.setParameter("time", time) }

        for (source in particleSources.values) {
            updateSource(source, deltaTime, cameraPosition, wind)
        }

        updateBuffers(cameraPosition)
    }
    
    private fun updateSource(
        source: ParticleSource,
        deltaTime: Float,
        cameraPosition: LLVector3,
        wind: LLVector3
    ) {
        val params = source.params
        
        // Emit new particles
        source.timeSinceEmit += deltaTime
        val emitInterval = 1f / params.burstRate
        
        while (source.timeSinceEmit >= emitInterval) {
            source.timeSinceEmit -= emitInterval
            
            for (i in 0 until params.burstPartCount) {
                if (source.particles.size >= MAX_PARTICLES_PER_SOURCE) break
                
                val particle = createParticle(source.position, params)
                source.particles.add(particle)
            }
        }
        
        // Update existing particles
        var i = 0
        while (i < source.particles.size) {
            val particle = source.particles[i]
            particle.age += deltaTime
            
            if (particle.age >= particle.lifetime) {
                // Swap-remove optimization for O(1) removal
                val lastIndex = source.particles.size - 1
                if (i < lastIndex) {
                    source.particles[i] = source.particles[lastIndex]
                }
                source.particles.removeAt(lastIndex)
                continue
            }
            
            // Apply forces
            var acceleration = LLVector3(0f, 0f, -params.burstSpeedMin * 0.1f) // Gravity
            
            if ((params.flags and WIND) != 0) {
                acceleration = acceleration + wind * 0.5f
            }
            
            particle.velocity = particle.velocity + acceleration * deltaTime
            particle.position = particle.position + particle.velocity * deltaTime
            
            // Bounce
            if ((params.flags and BOUNCE) != 0 && particle.position.z < 0) {
                particle.position = LLVector3(particle.position.x, particle.position.y, 0f)
                particle.velocity = LLVector3(particle.velocity.x, particle.velocity.y, -particle.velocity.z * 0.6f)
            }
            
            // Interpolate color
            if ((params.flags and INTERP_COLOR) != 0) {
                val t = particle.age / particle.lifetime
                particle.color = params.startColor.lerp(params.endColor, t)
            }
            
            // Interpolate scale
            if ((params.flags and INTERP_SCALE) != 0) {
                val t = particle.age / particle.lifetime
                particle.scale = params.startScale.lerp(params.endScale, t)
            }
            i++
        }
    }
    
    private fun createParticle(sourcePosition: LLVector3, params: ParticleParams): Particle {
        val velocity = when {
            (params.pattern and PATTERN_EXPLODE) != 0 -> {
                // Random direction
                val theta = Random.nextFloat() * 2f * PI.toFloat()
                val phi = acos(2f * Random.nextFloat() - 1f)
                val speed = params.burstSpeedMin + Random.nextFloat() * (params.burstSpeedMax - params.burstSpeedMin)
                LLVector3(
                    sin(phi) * cos(theta) * speed,
                    sin(phi) * sin(theta) * speed,
                    cos(phi) * speed
                )
            }
            (params.pattern and PATTERN_ANGLE) != 0 -> {
                // Cone pattern
                val theta = Random.nextFloat() * 2f * PI.toFloat()
                val phi = params.innerAngle + Random.nextFloat() * (params.outerAngle - params.innerAngle)
                val speed = params.burstSpeedMin + Random.nextFloat() * (params.burstSpeedMax - params.burstSpeedMin)
                LLVector3(
                    sin(phi) * cos(theta) * speed,
                    sin(phi) * sin(theta) * speed,
                    cos(phi) * speed
                )
            }
            else -> {
                // Drop pattern
                LLVector3(0f, 0f, params.burstSpeedMin)
            }
        }
        
        val offset = LLVector3(
            (Random.nextFloat() - 0.5f) * params.burstRadius,
            (Random.nextFloat() - 0.5f) * params.burstRadius,
            (Random.nextFloat() - 0.5f) * params.burstRadius
        )
        
        return Particle(
            position = sourcePosition + offset,
            velocity = velocity,
            color = params.startColor,
            scale = params.startScale,
            age = 0f,
            lifetime = params.partMaxAge
        )
    }
    
    private fun createBuffers() {
        // Pre-allocate buffers for all particles
        maxParticles = MAX_PARTICLES_PER_SOURCE * 100
        val vertexCount = maxParticles * 4 // Quad per particle
        val indexCount = maxParticles * 6
        
        val stride = (3 + 4 + 2) * 4 // Position + Color + UV
        
        vertexBuffer = VertexBuffer.Builder()
            .vertexCount(vertexCount)
            .bufferCount(1)
            .attribute(VertexBuffer.VertexAttribute.POSITION, 0, 
                VertexBuffer.AttributeType.FLOAT3, 0, stride)
            .attribute(VertexBuffer.VertexAttribute.COLOR, 0,
                VertexBuffer.AttributeType.FLOAT4, 12, stride)
            .attribute(VertexBuffer.VertexAttribute.UV0, 0,
                VertexBuffer.AttributeType.FLOAT2, 28, stride)
            .build(engine)
        
        indexBuffer = IndexBuffer.Builder()
            .indexCount(indexCount)
            .bufferType(IndexBuffer.Builder.IndexType.USHORT)
            .build(engine)
        
        // Pre-fill index buffer (doesn't change)
        val indexData = ByteBuffer.allocateDirect(indexCount * 2)
            .order(ByteOrder.nativeOrder())
        
        for (i in 0 until maxParticles) {
            val base = i * 4
            indexData.putShort((base + 0).toShort())
            indexData.putShort((base + 1).toShort())
            indexData.putShort((base + 2).toShort())
            indexData.putShort((base + 0).toShort())
            indexData.putShort((base + 2).toShort())
            indexData.putShort((base + 3).toShort())
        }
        indexData.flip()

        indexBuffer?.setBuffer(engine, indexData)

        // Build a single renderable that points at the shared
        // VertexBuffer/IndexBuffer. The geometry's index count is bumped
        // each frame in updateBuffers() to match the live particle count.
        val mat = materialInstance ?: return
        val vb = vertexBuffer ?: return
        val ib = indexBuffer ?: return
        particleEntity = EntityManager.get().create()
        RenderableManager.Builder(1)
            // Particles can move anywhere in the region; use a generous
            // bounding box and disable culling so the renderer doesn't
            // skip emitters that wander outside the box.
            .boundingBox(Box(0f, 0f, 0f, 256f, 256f, 256f))
            // Start with zero indices drawn; updateBuffers() bumps the
            // count to (totalParticles * 6) once the first source spawns.
            .geometry(0, RenderableManager.PrimitiveType.TRIANGLES, vb, ib)
            .material(0, mat)
            .culling(false)
            .castShadows(false)
            .receiveShadows(false)
            .priority(7) // Draw after opaques; particles are alpha-blended.
            .build(engine, particleEntity)
        // Force the initial draw range to zero so the uninitialized
        // vertex buffer doesn't render garbage on the first frame
        // before any source has spawned.
        try {
            val rm = engine.renderableManager
            val instance = rm.getInstance(particleEntity)
            if (instance != 0) {
                rm.setGeometryAt(
                    instance, 0,
                    RenderableManager.PrimitiveType.TRIANGLES,
                    vb, ib, 0, 0
                )
            }
        } catch (_: Throwable) {
            // see updateBuffers() for the binding-mismatch fallback note
        }
        scene.addEntity(particleEntity)
    }
    
    private fun updateBuffers(cameraPosition: LLVector3) {
        // Count total particles
        var totalParticles = 0
        for (source in particleSources.values) {
            totalParticles += source.particles.size
        }

        // Resize the renderable's draw range to match live particles
        // (6 indices per quad). When totalParticles==0 we set the range
        // to 0 instead of skipping, so a previously-populated frame
        // doesn't keep drawing stale geometry.
        val rm = engine.renderableManager
        val vb = vertexBuffer
        val ib = indexBuffer
        if (particleEntity != 0 && vb != null && ib != null) {
            val instance = rm.getInstance(particleEntity)
            if (instance != 0) {
                val drawIndices = (totalParticles.coerceAtMost(maxParticles)) * 6
                try {
                    rm.setGeometryAt(
                        instance, 0,
                        RenderableManager.PrimitiveType.TRIANGLES,
                        vb, ib, 0, drawIndices
                    )
                } catch (_: Throwable) {
                    // No-op: Filament Java bindings vary between releases.
                    // If this overload is missing the buffers stay bound,
                    // we just can't shrink the draw range — a benign visual
                    // artifact (last frame's quads stay visible) but no
                    // crash.
                }
            }
        }

        if (totalParticles == 0) return
        
        val stride = (3 + 4 + 2) * 4
        val vertexData = ByteBuffer.allocateDirect(totalParticles * 4 * stride)
            .order(ByteOrder.nativeOrder())
        
        // Compute billboard vectors from camera position
        // Using a simplified world-aligned billboard for now
        // For camera-aligned billboards, we'd need the full camera transform
        val cameraRight = LLVector3(1f, 0f, 0f)
        val cameraUp = LLVector3(0f, 0f, 1f)
        
        for (source in particleSources.values) {
            for (particle in source.particles) {
                val halfWidth = particle.scale.x * 0.5f
                val halfHeight = particle.scale.y * 0.5f
                
                // For true billboarding, compute direction to camera
                val toCamera = (cameraPosition - particle.position).normalize()
                
                // Use cross products to get proper billboard orientation
                // This creates camera-facing quads
                val right = if (kotlin.math.abs(toCamera.z) < 0.99f) {
                    LLVector3(0f, 0f, 1f).cross(toCamera).normalize()
                } else {
                    cameraRight
                }
                val up = toCamera.cross(right).normalize()
                
                // Quad corners (billboard facing camera)
                val corners = arrayOf(
                    particle.position + right * (-halfWidth) + up * (-halfHeight),
                    particle.position + right * halfWidth + up * (-halfHeight),
                    particle.position + right * halfWidth + up * halfHeight,
                    particle.position + right * (-halfWidth) + up * halfHeight
                )
                
                val uvs = arrayOf(
                    floatArrayOf(0f, 0f),
                    floatArrayOf(1f, 0f),
                    floatArrayOf(1f, 1f),
                    floatArrayOf(0f, 1f)
                )
                
                for (i in 0..3) {
                    vertexData.putFloat(corners[i].x)
                    vertexData.putFloat(corners[i].y)
                    vertexData.putFloat(corners[i].z)
                    vertexData.putFloat(particle.color.r)
                    vertexData.putFloat(particle.color.g)
                    vertexData.putFloat(particle.color.b)
                    vertexData.putFloat(particle.color.a)
                    vertexData.putFloat(uvs[i][0])
                    vertexData.putFloat(uvs[i][1])
                }
            }
        }
        vertexData.flip()
        
        vertexBuffer?.setBufferAt(engine, 0, vertexData)
    }
    
    fun shutdown() {
        particleSources.clear()
        if (particleEntity != 0) {
            scene.removeEntity(particleEntity)
            engine.renderableManager.destroy(particleEntity)
            EntityManager.get().destroy(particleEntity)
            particleEntity = 0
        }
        vertexBuffer?.let { engine.destroyVertexBuffer(it) }
        indexBuffer?.let { engine.destroyIndexBuffer(it) }
        materialInstance?.let { engine.destroyMaterialInstance(it) }
        vertexBuffer = null
        indexBuffer = null
        materialInstance = null
    }
}

data class ParticleParams(
    val textureId: UUID? = null,
    val pattern: Int = PATTERN_EXPLODE,
    val flags: Int = 0,
    val burstRate: Float = 10f,
    val burstPartCount: Int = 1,
    val burstRadius: Float = 0.1f,
    val burstSpeedMin: Float = 1f,
    val burstSpeedMax: Float = 2f,
    val innerAngle: Float = 0f,
    val outerAngle: Float = PI.toFloat() / 4f,
    val partMaxAge: Float = 5f,
    val startColor: LLColor4 = LLColor4.white(),
    val endColor: LLColor4 = LLColor4(1f, 1f, 1f, 0f),
    val startScale: LLVector3 = LLVector3(0.5f, 0.5f, 0.5f),
    val endScale: LLVector3 = LLVector3(0.1f, 0.1f, 0.1f),
    val targetId: UUID? = null,
    val omega: LLVector3 = LLVector3.zero()
) {
    companion object {
        const val PATTERN_EXPLODE = ParticleSystem.PATTERN_EXPLODE
        const val PATTERN_DROP = ParticleSystem.PATTERN_DROP
        const val PATTERN_ANGLE = ParticleSystem.PATTERN_ANGLE
    }
}

private data class ParticleSource(
    val localId: Int,
    val params: ParticleParams,
    var position: LLVector3,
    val particles: MutableList<Particle>,
    var timeSinceEmit: Float = 0f
)

private data class Particle(
    var position: LLVector3,
    var velocity: LLVector3,
    var color: LLColor4,
    var scale: LLVector3,
    var age: Float,
    val lifetime: Float
)
