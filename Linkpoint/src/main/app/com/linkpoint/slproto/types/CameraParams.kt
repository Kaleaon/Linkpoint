package com.linkpoint.slproto.types

import com.linkpoint.render.HeadTransformCompat
import com.linkpoint.slproto.types.LLQuaternion
import androidx.annotation.NonNull
import androidx.annotation.Nullable
import androidx.annotation.ThreadSafe

@ThreadSafe
class CameraParams {
    private val FLING_DECEL_PITCH: Float = 100.0f
    private val FLING_DECEL_YAW: Float = 50.0f
    private val MAX_PITCH: Float = 85.0f
    private val MIN_PITCH: Float = -85.0f
    private Long flingStartTime = 0
    private Float heading = 0.0f
    private Float headingFlingSpeed = 0.0f
    private Boolean isFlinging = false
    private Boolean isManualControl = false
    private Boolean isValid = false
    private Any lock = Any()
    private Long manualControlStartTime = 0
    private Float manualFlySpeed = 0.0f
    private Float manualMoveSpeed = 0.0f
    private Float manualStrafeSpeed = 0.0f
    private Float manualTurnSpeed = 0.0f
    private LLVector3 offset = LLVector3(-2.0f, 0.0f, 1.0f)
    private LLVector3 offsetVR = LLVector3(0.0f, 0.0f, 1.0f)
    @NonNull
    private LLVector3 position = LLVector3()
    private Float tilt = 0.0f
    private Float tiltFlingSpeed = 0.0f
    private Boolean useOffset = false

    fun angleMinusAngle(Float f, Float f2): Float {
        return wrapAngle(wrapAngle(f) - wrapAngle(f2))
    }

    private Unit processFling() {
        if (this.isFlinging) {
            Long currentTimeMillis = System.currentTimeMillis()
            Float f = ((Float) (currentTimeMillis - this.flingStartTime)) / 1000.0f
            this.heading = wrapAngle(this.heading + (this.headingFlingSpeed * f))
            this.tilt = Math.max(Math.min(this.tilt + (this.tiltFlingSpeed * f), MAX_PITCH), MIN_PITCH)
            if (this.headingFlingSpeed > 0.0f) {
                this.headingFlingSpeed -= FLING_DECEL_PITCH * f
                if (this.headingFlingSpeed < 0.0f) {
                    this.headingFlingSpeed = 0.0f
                }
            } else if (this.headingFlingSpeed < 0.0f) {
                this.headingFlingSpeed += FLING_DECEL_PITCH * f
                if (this.headingFlingSpeed > 0.0f) {
                    this.headingFlingSpeed = 0.0f
                }
            }
            if (this.tiltFlingSpeed > 0.0f) {
                this.tiltFlingSpeed -= f * FLING_DECEL_YAW
                if (this.tiltFlingSpeed < 0.0f) {
                    this.tiltFlingSpeed = 0.0f
                }
            } else if (this.tiltFlingSpeed < 0.0f) {
                this.tiltFlingSpeed = (f * FLING_DECEL_YAW) + this.tiltFlingSpeed
                if (this.tiltFlingSpeed > 0.0f) {
                    this.tiltFlingSpeed = 0.0f
                }
            }
            this.flingStartTime = currentTimeMillis
            if (this.tiltFlingSpeed == 0.0f && this.headingFlingSpeed == 0.0f) {
                this.isFlinging = false
            }
        }
    }

    private Unit processManualControl(HeadTransformCompat headTransformCompat) {
        Float f
        Float f2
        if (this.isManualControl) {
            Long currentTimeMillis = System.currentTimeMillis()
            Float f3 = ((Float) (currentTimeMillis - this.manualControlStartTime)) / 1000.0f
            if (headTransformCompat != null) {
                f = wrapAngle(headTransformCompat.yawDegrees + headTransformCompat.viewExtraYaw)
                f2 = headTransformCompat.pitchDegrees
            } else {
                this.heading = wrapAngle(this.heading + (this.manualTurnSpeed * f3))
                f = this.heading
                f2 = this.tilt
            }
            if (!(this.manualMoveSpeed == 0.0f && this.manualFlySpeed == 0.0f && this.manualStrafeSpeed == 0.0f)) {
                LLQuaternion mayaQ = LLQuaternion.mayaQ(0.0f, f2, f, LLQuaternion.Order.YZX)
                LLVector3 lLVector3 = LLVector3(1.0f, 0.0f, 0.0f)
                LLVector3 lLVector32 = LLVector3(0.0f, 0.0f, 1.0f)
                LLVector3 lLVector33 = LLVector3(0.0f, 1.0f, 0.0f)
                lLVector3.mul(mayaQ)
                lLVector32.mul(mayaQ)
                lLVector33.mul(mayaQ)
                this.position.addMul(lLVector3, this.manualMoveSpeed * f3)
                this.position.addMul(lLVector32, this.manualFlySpeed * f3)
                this.position.addMul(lLVector33, this.manualStrafeSpeed * f3)
            }
            this.manualControlStartTime = currentTimeMillis
        }
    }

    fun wrapAngle(Float f): Float {
        Float f2 = (f + 180.0f) % 360.0f
        if (f2 < 0.0f) {
            f2 += 360.0f
        }
        return f2 - 180.0f
    }

    fun copyFrom(@Nullable CameraParams cameraParams): Unit {
        Float f
        Float f2
        Float f3
        Float f4
        Float f5
        if (cameraParams != null) {
            synchronized (cameraParams.lock) {
                cameraParams.processFling()
                cameraParams.processManualControl((HeadTransformCompat) null)
                f = cameraParams.position.x
                f2 = cameraParams.position.y
                f3 = cameraParams.position.z
                f4 = cameraParams.heading
                f5 = cameraParams.tilt
                z = cameraParams.isValid
                if (cameraParams.useOffset) {
                    LLVector3 lLVector3 = LLVector3(this.offset)
                    lLVector3.mul(LLQuaternion.mayaQ(0.0f, f5, f4, LLQuaternion.Order.YZX))
                    f += lLVector3.x
                    f2 += lLVector3.y
                    f3 += lLVector3.z
                }
            }
            synchronized (this.lock) {
                this.position.set(f, f2, f3)
                this.heading = f4
                this.tilt = f5
                this.isValid = z
            }
        }
    }

    fun fling(Float f, Float f2): Unit {
        synchronized (this.lock) {
            this.headingFlingSpeed = f
            this.tiltFlingSpeed = f2
            this.flingStartTime = System.currentTimeMillis()
            this.isFlinging = true
        }
    }

    fun getHeading(): Float {
        Float f
        synchronized (this.lock) {
            f = this.heading
        }
        return f
    }

    @NonNull
    fun getPosition(): LLVector3 {
        LLVector3 lLVector3
        synchronized (this.lock) {
            lLVector3 = this.position
        }
        return lLVector3
    }

    fun getTilt(): Float {
        Float f
        synchronized (this.lock) {
            f = this.tilt
        }
        return f
    }

    fun getVRCamera(@Nullable CameraParams cameraParams, HeadTransformCompat headTransformCompat): Unit {
        Float f
        Float f2
        Float f3
        Float f4
        Float f5
        if (cameraParams != null) {
            synchronized (cameraParams.lock) {
                cameraParams.processManualControl(headTransformCompat)
                f = cameraParams.position.x
                f2 = cameraParams.position.y
                f3 = cameraParams.position.z
                f4 = cameraParams.heading
                f5 = cameraParams.tilt
                z = cameraParams.isValid
                if (cameraParams.useOffset) {
                    LLVector3 lLVector3 = LLVector3(this.offsetVR)
                    lLVector3.mul(LLQuaternion.mayaQ(0.0f, f5, f4, LLQuaternion.Order.YZX))
                    f += lLVector3.x
                    f2 += lLVector3.y
                    f3 += lLVector3.z
                }
            }
            synchronized (this.lock) {
                this.position.set(f, f2, f3)
                this.heading = f4
                this.tilt = f5
                this.isValid = z
            }
        }
    }

    fun isFlinging(): Boolean {
        synchronized (this.lock) {
            z = this.isFlinging
        }
        return z
    }

    fun isValid(): Boolean {
        synchronized (this.lock) {
            z = this.isValid
        }
        return z
    }

    fun rotate(Float f, Float f2): Unit {
        synchronized (this.lock) {
            this.heading = wrapAngle(this.heading + f)
            this.tilt = Math.max(Math.min(this.tilt + f2, MAX_PITCH), MIN_PITCH)
            this.isFlinging = false
        }
    }

    fun set(@Nullable LLVector3 lLVector3, Float f, Float f2): Unit {
        synchronized (this.lock) {
            if (lLVector3 != null) {
                this.position.set(lLVector3)
            }
            this.heading = f
            this.tilt = f2
            this.isValid = true
        }
    }

    fun setHeading(Float f): Unit {
        synchronized (this.lock) {
            this.heading = f
        }
    }

    fun setPosition(@Nullable LLVector3 lLVector3): Unit {
        synchronized (this.lock) {
            if (lLVector3 != null) {
                this.position.set(lLVector3)
            }
            this.useOffset = true
            this.isValid = true
        }
    }

    fun setPosition(@Nullable LLVector3 lLVector3, Float f): Unit {
        synchronized (this.lock) {
            if (lLVector3 != null) {
                this.position.set(lLVector3)
            }
            this.heading = f
            this.tilt = 0.0f
            this.isFlinging = false
            this.isValid = true
            this.useOffset = true
        }
    }

    fun startManualControl(Float f, Float f2, Float f3, Float f4): Unit {
        synchronized (this.lock) {
            if (!this.isManualControl) {
                LLVector3 lLVector3 = LLVector3(this.position)
                LLQuaternion mayaQ = LLQuaternion.mayaQ(0.0f, this.tilt, this.heading, LLQuaternion.Order.YZX)
                if (!this.useOffset) {
                    LLVector3 lLVector32 = LLVector3(this.offset)
                    lLVector32.mul(mayaQ)
                    lLVector3.add(lLVector32)
                    this.useOffset = true
                }
                this.position.set(lLVector3)
                this.isManualControl = true
                this.manualControlStartTime = System.currentTimeMillis()
            }
            this.manualMoveSpeed = f2
            this.manualTurnSpeed = f
            this.manualFlySpeed = f3
            this.manualStrafeSpeed = f4
        }
    }

    fun stopManualControl(): Unit {
        synchronized (this.lock) {
            this.isManualControl = false
        }
    }

    fun zoom(Float f, Float f2, Float f3, Float f4, Float f5): Unit {
        synchronized (this.lock) {
            Float f6 = f - 1.0f
            LLVector3 lLVector3 = LLVector3(this.position)
            LLQuaternion mayaQ = LLQuaternion.mayaQ(0.0f, this.tilt, this.heading, LLQuaternion.Order.YZX)
            if (!this.useOffset) {
                LLVector3 lLVector32 = LLVector3(this.offset)
                lLVector32.mul(mayaQ)
                lLVector3.add(lLVector32)
                this.useOffset = true
            }
            LLVector3 lLVector33 = LLVector3(1.0f, 0.0f, 0.0f)
            LLVector3 lLVector34 = LLVector3(0.0f, 0.0f, 1.0f)
            LLVector3 lLVector35 = LLVector3(0.0f, 1.0f, 0.0f)
            lLVector33.mul(mayaQ)
            lLVector33.mul(f6)
            lLVector34.mul(mayaQ)
            lLVector34.mul((f6 * f3) + f5)
            lLVector35.mul(mayaQ)
            lLVector35.mul((f6 * f2) + f4)
            lLVector3.add(lLVector33)
            lLVector3.add(lLVector34)
            lLVector3.add(lLVector35)
            this.position.set(lLVector3)
        }
    }
}
