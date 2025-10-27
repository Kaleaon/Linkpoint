package com.linkpoint.slproto.prims

import android.opengl.Matrix
import com.linkpoint.openjpeg.OpenJPEG
import com.linkpoint.render.RenderContext
import com.linkpoint.render.glres.buffers.GLLoadableBuffer
import com.linkpoint.slproto.types.LLQuaternion
import com.linkpoint.slproto.types.LLVector3
import com.linkpoint.rawbuffers.DirectByteBuffer

class PrimFlexibleInfo {
    private const val FLEXIBLE_OBJECT_MAX_INTERNAL_TENSION_FORCE: Float = 0.99f
    private const val MIN_UPDATE_INTERVAL: Long = 200
    private Int NumSections = 0
    private Long lastUpdateMillis
    private volatile Boolean needVertexBufferUpdate = false
    private FloatArray sectionData
    private FloatArray sectionMatrices
    private Array<FlexibleSection> sections
    private GLLoadableBuffer vertexBuffer = null

    @JvmStatic
private class FlexibleSection {
        LLVector3 Direction
        LLVector3 Position
        LLQuaternion Rotation
        LLVector3 Velocity

        private FlexibleSection() {
        }

        /* synthetic */ FlexibleSection(FlexibleSection flexibleSection) {
            this()
        }
    }

     public fun doFlexibleUpdate(primFlexibleParams: PrimFlexibleParams, fArr: FloatArray, i: Int, f: Float, f2: Float, f3: Float): Boolean {
        val currentTimeMillis: Long = System.currentTimeMillis()
        if (currentTimeMillis < this.lastUpdateMillis + MIN_UPDATE_INTERVAL) {
            return false
        }
        if (primFlexibleParams.NumFlexiSections != this.NumSections) {
            this.sections = null
            this.sectionMatrices = null
            this.sectionData = null
            this.NumSections = primFlexibleParams.NumFlexiSections
        }
        if (this.NumSections == 0) {
            return false
        }
        this.lastUpdateMillis = currentTimeMillis
        val f4: Float = (((Float) (currentTimeMillis - this.lastUpdateMillis)) / 1000.0f) * 5.0f
        val z: Boolean = false
        if (this.sectionData == null) {
            this.sectionData = Float[OpenJPEG.getFlexiDataSize(this.NumSections)]
            this.sectionMatrices = Float[(this.NumSections * 16)]
            z = true
        }
        OpenJPEG.calcFlexiSections(this.sectionData, this.NumSections, this.sectionMatrices, fArr, i, f, f2, f3, f4, primFlexibleParams.Tension, primFlexibleParams.AirFriction, primFlexibleParams.Gravity, primFlexibleParams.UserForce.x, primFlexibleParams.UserForce.y, primFlexibleParams.UserForce.z, z)
        this.needVertexBufferUpdate = true
        return true
    }

     public fun doFlexibleUpdateSlow(primFlexibleParams: PrimFlexibleParams, fArr: FloatArray, i: Int, f: Float, f2: Float, f3: Float): Boolean {
        val currentTimeMillis: Long = System.currentTimeMillis()
        if (currentTimeMillis < this.lastUpdateMillis + MIN_UPDATE_INTERVAL) {
            return false
        }
        val lLVector3: LLVector3 = LLVector3(fArr[i + 12], fArr[i + 13], fArr[i + 14])
        val lLVector32: LLVector3 = LLVector3(f, f2, f3)
        val fArr2: FloatArray = Float[32]
        Matrix.invertM(fArr2, 0, fArr, i)
        val lLQuaternion: LLQuaternion = LLQuaternion(fArr2)
        if (primFlexibleParams.NumFlexiSections != this.NumSections) {
            this.sections = null
            this.sectionMatrices = null
            this.NumSections = primFlexibleParams.NumFlexiSections
        }
        if (this.NumSections == 0) {
            return false
        }
        this.lastUpdateMillis = currentTimeMillis
        val f4: Float = (((Float) (currentTimeMillis - this.lastUpdateMillis)) / 1000.0f) * 5.0f
        val lLQuaternion2: LLQuaternion = LLQuaternion(lLQuaternion)
        val lLVector33: LLVector3 = LLVector3(LLVector3.z_axis)
        lLVector33.mul(lLQuaternion2)
        val f5: Float = lLVector32.z / ((Float) this.NumSections)
        val lLVector34: LLVector3 = LLVector3(lLVector33)
        lLVector34.mul(lLVector32.z / 2.0f)
        val sub: LLVector3 = LLVector3.sub(lLVector3, lLVector34)
        if (this.sections == null) {
            this.sections = FlexibleSection[this.NumSections]
            for (Int i2 = 0; i2 < this.NumSections; i2++) {
                this.sections[i2] = FlexibleSection((FlexibleSection) null)
                this.sections[i2].Position = LLVector3(sub)
                this.sections[i2].Position.addMul(lLVector33, ((Float) i2) * f5)
                this.sections[i2].Direction = LLVector3(lLVector33)
                this.sections[i2].Rotation = LLQuaternion(lLQuaternion)
                this.sections[i2].Velocity = LLVector3()
            }
        }
        this.sections[0].Position.set(sub)
        this.sections[0].Direction.set(lLVector33)
        this.sections[0].Rotation.set(lLQuaternion)
        val pow: Float = primFlexibleParams.Tension * 0.1f * (1.0f - ((Float) Math.pow(0.85d, ((Double) f4) * 30.0d)))
        if (pow > FLEXIBLE_OBJECT_MAX_INTERNAL_TENSION_FORCE) {
            pow = FLEXIBLE_OBJECT_MAX_INTERNAL_TENSION_FORCE
        }
        val pow2: Float = (Float) Math.pow(10.0d, (Double) (((primFlexibleParams.AirFriction * 2.0f) + 1.0f) * f4))
        if (pow2 <= 1.0f) {
            pow2 = 1.0f
        }
        val f6: Float = 1.0f / pow2
        val atan: Float = (Float) Math.atan((Double) (2.0f * f5))
        val f7: Float = f5 * f4
        val lLVector35: LLVector3 = LLVector3()
        val lLVector36: LLVector3 = LLVector3()
        val lLQuaternion3: LLQuaternion = LLQuaternion()
        val lLQuaternion4: LLQuaternion = LLQuaternion()
        val lLQuaternion5: LLQuaternion = LLQuaternion()
        val i3: Int = 1
        while (i3 < this.NumSections) {
            lLVector35.set(this.sections[i3].Position)
            this.sections[i3].Position.z -= primFlexibleParams.Gravity * f7
            this.sections[i3].Position.addMul(primFlexibleParams.UserForce, f7)
            val lLVector37: LLVector3 = this.sections[i3 - 1].Position
            val lLVector38: LLVector3 = this.sections[i3 - 1].Direction
            val lLVector39: LLVector3 = i3 == 1 ? this.sections[0].Direction : this.sections[i3 - 2].Direction
            val sub2: LLVector3 = LLVector3.sub(this.sections[i3].Position, lLVector37)
            val lLVector310: LLVector3 = LLVector3(lLVector39)
            lLVector310.mul(f5)
            lLVector310.sub(sub2)
            this.sections[i3].Position.addMul(lLVector310, pow)
            this.sections[i3].Position.addMul(this.sections[i3].Velocity, f6)
            this.sections[i3].Direction.setSub(this.sections[i3].Position, lLVector37)
            this.sections[i3].Direction.normVec()
            val shortestArc: LLQuaternion = LLQuaternion.shortestArc(lLVector38, this.sections[i3].Direction)
            val angleAxis: Float = shortestArc.getAngleAxis(lLVector36)
            if (angleAxis > 3.1415927f) {
                angleAxis -= 6.2831855f
            }
            if (angleAxis < -3.1415927f) {
                angleAxis += 6.2831855f
            }
            if (angleAxis > atan) {
                shortestArc.setQuat(atan, lLVector36)
            } else if (angleAxis < (-atan)) {
                shortestArc.setQuat(-atan, lLVector36)
            }
            lLQuaternion3.setMul(lLQuaternion2, shortestArc)
            lLQuaternion2.set(lLQuaternion3)
            this.sections[i3].Direction.set(lLVector38)
            this.sections[i3].Direction.mul(shortestArc)
            this.sections[i3].Position.set(lLVector37)
            this.sections[i3].Position.addMul(this.sections[i3].Direction, f5)
            this.sections[i3].Rotation.set(lLQuaternion3)
            if (i3 > 1) {
                lLQuaternion4.setQuat(angleAxis / 2.0f, lLVector36)
                lLQuaternion5.setMul(this.sections[i3 - 1].Rotation, lLQuaternion4)
                this.sections[i3 - 1].Rotation.set(lLQuaternion5)
            }
            this.sections[i3].Velocity.setSub(this.sections[i3].Position, lLVector35)
            if (this.sections[i3].Velocity.magVecSquared() > 1.0f) {
                this.sections[i3].Velocity.normVec()
            }
            i3++
        }
        val fArr3: FloatArray = Float[32]
        Matrix.setIdentityM(fArr3, 16)
        Matrix.scaleM(fArr3, 16, 1.0f / lLVector32.x, 1.0f / lLVector32.y, 1.0f / lLVector32.z)
        Matrix.multiplyMM(fArr3, 0, fArr3, 16, lLQuaternion.getMatrix(), 0)
        Matrix.translateM(fArr3, 0, -lLVector3.x, -lLVector3.y, -lLVector3.z)
        if (this.sectionMatrices == null) {
            this.sectionMatrices = Float[(this.NumSections * 16)]
        }
        val fArr4: FloatArray = Float[8]
        val i4: Int = 0
        while (true) {
            val i5: Int = i4
            if (i5 < this.NumSections) {
                fArr4[0] = this.sections[i5].Position.x
                fArr4[1] = this.sections[i5].Position.y
                fArr4[2] = this.sections[i5].Position.z
                fArr4[3] = 1.0f
                Matrix.multiplyMV(fArr4, 4, fArr3, 0, fArr4, 0)
                val f8: Float = (((Float) i5) / ((Float) this.NumSections)) - 0.5f
                val fArr5: FloatArray = Float[32]
                Matrix.setIdentityM(fArr5, 16)
                Matrix.translateM(fArr5, 16, fArr4[4], fArr4[5], fArr4[6] - f8)
                Matrix.translateM(fArr5, 16, 0.0f, 0.0f, f8)
                Matrix.scaleM(fArr5, 16, 1.0f / lLVector32.x, 1.0f / lLVector32.y, 1.0f / lLVector32.z)
                Matrix.multiplyMM(fArr5, 0, fArr5, 16, lLQuaternion.getMatrix(), 0)
                Matrix.multiplyMM(fArr5, 16, fArr5, 0, this.sections[i5].Rotation.getInverseMatrix(), 0)
                Matrix.scaleM(fArr5, 16, lLVector32.x, lLVector32.y, lLVector32.z)
                Matrix.translateM(fArr5, 16, 0.0f, 0.0f, -f8)
                System.arraycopy(fArr5, 16, this.sectionMatrices, i5 * 16, 16)
                i4 = i5 + 1
            } else {
                this.needVertexBufferUpdate = true
                return true
            }
        }
    }

     public fun getFlexedVertexBuffer(renderContext: RenderContext, gLLoadableBuffer: GLLoadableBuffer, i: Int): GLLoadableBuffer {
        if (this.sectionMatrices != null) {
            if (this.needVertexBufferUpdate) {
                val rawBuffer: DirectByteBuffer = gLLoadableBuffer.getRawBuffer()
                if (this.vertexBuffer == null) {
                    this.vertexBuffer = GLLoadableBuffer(DirectByteBuffer(rawBuffer))
                }
                OpenJPEG.applyFlexibleMorph(this.vertexBuffer.getRawBuffer().asByteBuffer(), rawBuffer.asByteBuffer(), i, this.sectionMatrices)
                this.vertexBuffer.Reload(renderContext)
                this.needVertexBufferUpdate = false
            }
            if (this.vertexBuffer != null) {
                return this.vertexBuffer
            }
        }
        return gLLoadableBuffer
    }

     public fun getMatrices(): FloatArray {
        return this.sectionMatrices
    }
}
