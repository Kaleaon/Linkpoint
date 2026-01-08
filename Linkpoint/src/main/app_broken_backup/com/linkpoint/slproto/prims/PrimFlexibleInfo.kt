package com.linkpoint.slproto.prims

import android.opengl.Matrix
import com.linkpoint.openjpeg.OpenJPEG
import com.linkpoint.render.RenderContext
import com.linkpoint.render.glres.buffers.GLLoadableBuffer
import com.linkpoint.slproto.types.LLQuaternion
import com.linkpoint.slproto.types.LLVector3
import com.linkpoint.rawbuffers.DirectByteBuffer

class PrimFlexibleInfo {
    private val FLEXIBLE_OBJECT_MAX_INTERNAL_TENSION_FORCE: Float = 0.99f
    private val MIN_UPDATE_INTERVAL: Long = 200
    private Int NumSections = 0
    private Long lastUpdateMillis
    private volatile Boolean needVertexBufferUpdate = false
    private FloatArray sectionData
    private FloatArray sectionMatrices
    private FlexibleSection[] sections
    private GLLoadableBuffer vertexBuffer = null

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

    fun doFlexibleUpdate(PrimFlexibleParams primFlexibleParams, FloatArray fArr, Int i, Float f, Float f2, Float f3): Boolean {
        Long currentTimeMillis = System.currentTimeMillis()
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
        Float f4 = (((Float) (currentTimeMillis - this.lastUpdateMillis)) / 1000.0f) * 5.0f
        Boolean z = false
        if (this.sectionData == null) {
            this.sectionData = Float[OpenJPEG.getFlexiDataSize(this.NumSections)]
            this.sectionMatrices = Float[(this.NumSections * 16)]
            z = true
        }
        OpenJPEG.calcFlexiSections(this.sectionData, this.NumSections, this.sectionMatrices, fArr, i, f, f2, f3, f4, primFlexibleParams.Tension, primFlexibleParams.AirFriction, primFlexibleParams.Gravity, primFlexibleParams.UserForce.x, primFlexibleParams.UserForce.y, primFlexibleParams.UserForce.z, z)
        this.needVertexBufferUpdate = true
        return true
    }

    fun doFlexibleUpdateSlow(PrimFlexibleParams primFlexibleParams, FloatArray fArr, Int i, Float f, Float f2, Float f3): Boolean {
        Long currentTimeMillis = System.currentTimeMillis()
        if (currentTimeMillis < this.lastUpdateMillis + MIN_UPDATE_INTERVAL) {
            return false
        }
        LLVector3 lLVector3 = LLVector3(fArr[i + 12], fArr[i + 13], fArr[i + 14])
        LLVector3 lLVector32 = LLVector3(f, f2, f3)
        FloatArray fArr2 = FloatArray(32)
        Matrix.invertM(fArr2, 0, fArr, i)
        LLQuaternion lLQuaternion = LLQuaternion(fArr2)
        if (primFlexibleParams.NumFlexiSections != this.NumSections) {
            this.sections = null
            this.sectionMatrices = null
            this.NumSections = primFlexibleParams.NumFlexiSections
        }
        if (this.NumSections == 0) {
            return false
        }
        this.lastUpdateMillis = currentTimeMillis
        Float f4 = (((Float) (currentTimeMillis - this.lastUpdateMillis)) / 1000.0f) * 5.0f
        LLQuaternion lLQuaternion2 = LLQuaternion(lLQuaternion)
        LLVector3 lLVector33 = LLVector3(LLVector3.z_axis)
        lLVector33.mul(lLQuaternion2)
        Float f5 = lLVector32.z / (this.toFloat().NumSections)
        LLVector3 lLVector34 = LLVector3(lLVector33)
        lLVector34.mul(lLVector32.z / 2.0f)
        LLVector3 sub = LLVector3.sub(lLVector3, lLVector34)
        if (this.sections == null) {
            this.sections = FlexibleSection[this.NumSections]
            for (i2 in 0 until this.NumSections) {
                this.sections[i2] = FlexibleSection((FlexibleSection) null)
                this.sections[i2].Position = LLVector3(sub)
                this.sections[i2].Position.addMul(lLVector33, (i2.toFloat()) * f5)
                this.sections[i2].Direction = LLVector3(lLVector33)
                this.sections[i2].Rotation = LLQuaternion(lLQuaternion)
                this.sections[i2].Velocity = LLVector3()
            }
        }
        this.sections[0].Position.set(sub)
        this.sections[0].Direction.set(lLVector33)
        this.sections[0].Rotation.set(lLQuaternion)
        Float pow = primFlexibleParams.Tension * 0.1f * (1.0f - (Math.toFloat().pow(0.85d, (f4.toDouble()) * 30.0d)))
        if (pow > FLEXIBLE_OBJECT_MAX_INTERNAL_TENSION_FORCE) {
            pow = FLEXIBLE_OBJECT_MAX_INTERNAL_TENSION_FORCE
        }
        Float pow2 = Math.toFloat().pow(10.0d, (Double) (((primFlexibleParams.AirFriction * 2.0f) + 1.0f) * f4))
        if (pow2 <= 1.0f) {
            pow2 = 1.0f
        }
        Float f6 = 1.0f / pow2
        Float atan = Math.toFloat().atan((Double) (2.0f * f5))
        Float f7 = f5 * f4
        LLVector3 lLVector35 = LLVector3()
        LLVector3 lLVector36 = LLVector3()
        LLQuaternion lLQuaternion3 = LLQuaternion()
        LLQuaternion lLQuaternion4 = LLQuaternion()
        LLQuaternion lLQuaternion5 = LLQuaternion()
        Int i3 = 1
        while (i3 < this.NumSections) {
            lLVector35.set(this.sections[i3].Position)
            this.sections[i3].Position.z -= primFlexibleParams.Gravity * f7
            this.sections[i3].Position.addMul(primFlexibleParams.UserForce, f7)
            LLVector3 lLVector37 = this.sections[i3 - 1].Position
            LLVector3 lLVector38 = this.sections[i3 - 1].Direction
            LLVector3 lLVector39 = i3 == 1 ? this.sections[0].Direction : this.sections[i3 - 2].Direction
            LLVector3 sub2 = LLVector3.sub(this.sections[i3].Position, lLVector37)
            LLVector3 lLVector310 = LLVector3(lLVector39)
            lLVector310.mul(f5)
            lLVector310.sub(sub2)
            this.sections[i3].Position.addMul(lLVector310, pow)
            this.sections[i3].Position.addMul(this.sections[i3].Velocity, f6)
            this.sections[i3].Direction.setSub(this.sections[i3].Position, lLVector37)
            this.sections[i3].Direction.normVec()
            LLQuaternion shortestArc = LLQuaternion.shortestArc(lLVector38, this.sections[i3].Direction)
            Float angleAxis = shortestArc.getAngleAxis(lLVector36)
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
        FloatArray fArr3 = FloatArray(32)
        Matrix.setIdentityM(fArr3, 16)
        Matrix.scaleM(fArr3, 16, 1.0f / lLVector32.x, 1.0f / lLVector32.y, 1.0f / lLVector32.z)
        Matrix.multiplyMM(fArr3, 0, fArr3, 16, lLQuaternion.getMatrix(), 0)
        Matrix.translateM(fArr3, 0, -lLVector3.x, -lLVector3.y, -lLVector3.z)
        if (this.sectionMatrices == null) {
            this.sectionMatrices = Float[(this.NumSections * 16)]
        }
        FloatArray fArr4 = FloatArray(8)
        Int i4 = 0
        while (true) {
            Int i5 = i4
            if (i5 < this.NumSections) {
                fArr4[0] = this.sections[i5].Position.x
                fArr4[1] = this.sections[i5].Position.y
                fArr4[2] = this.sections[i5].Position.z
                fArr4[3] = 1.0f
                Matrix.multiplyMV(fArr4, 4, fArr3, 0, fArr4, 0)
                Float f8 = ((i5.toFloat()) / (this.toFloat().NumSections)) - 0.5f
                FloatArray fArr5 = FloatArray(32)
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

    fun getFlexedVertexBuffer(RenderContext renderContext, GLLoadableBuffer gLLoadableBuffer, Int i): GLLoadableBuffer {
        if (this.sectionMatrices != null) {
            if (this.needVertexBufferUpdate) {
                DirectByteBuffer rawBuffer = gLLoadableBuffer.getRawBuffer()
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

    fun getMatrices(): FloatArray {
        return this.sectionMatrices
    }
}
