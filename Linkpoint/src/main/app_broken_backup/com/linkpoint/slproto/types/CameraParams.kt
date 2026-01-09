package com.linkpoint.slproto.types

import androidx.annotation.NonNull
import androidx.annotation.Nullable
import androidx.annotation.GuardedBy
import kotlin.math.max
import kotlin.math.min

/**
 * Camera parameters for the SL viewer
 * Manages camera position, orientation, and movement controls
 */
class CameraParams {
    companion object {
        private const val FLING_DECEL_PITCH: Float = 100.0f
        private const val FLING_DECEL_YAW: Float = 50.0f
        private const val MAX_PITCH: Float = 85.0f
        private const val MIN_PITCH: Float = -85.0f
    }
    
    private val lock = Any()
    
    @GuardedBy("lock")
    private var flingStartTime: Long = 0
    
    @GuardedBy("lock")
    private var heading: Float = 0.0f
    
    @GuardedBy("lock")
    private var headingFlingSpeed: Float = 0.0f
    
    @GuardedBy("lock")
    private var isFlinging: Boolean = false
    
    @GuardedBy("lock")
    private var isManualControl: Boolean = false
    
    @GuardedBy("lock")
    private var isValid: Boolean = false
    
    @GuardedBy("lock")
    private var manualControlStartTime: Long = 0
    
    @GuardedBy("lock")
    private var manualFlySpeed: Float = 0.0f
    
    @GuardedBy("lock")
    private var manualMoveSpeed: Float = 0.0f
    
    @GuardedBy("lock")
    private var manualStrafeSpeed: Float = 0.0f
    
    @GuardedBy("lock")
    private var manualTurnSpeed: Float = 0.0f
    
    private val offset = LLVector3(-2.0f, 0.0f, 1.0f)
    private val offsetVR = LLVector3(0.0f, 0.0f, 1.0f)
    
    @GuardedBy("lock")
    @NonNull
    private val position = LLVector3()
    
    @GuardedBy("lock")
    private var tilt: Float = 0.0f
    
    @GuardedBy("lock")
    private var tiltFlingSpeed: Float = 0.0f
    
    @GuardedBy("lock")
    private var useOffset: Boolean = false
    
    fun angleMinusAngle(angle1: Float, angle2: Float): Float {
        return wrapAngle(wrapAngle(angle1) - wrapAngle(angle2))
    }
    
    private fun processFling() {
        if (isFlinging) {
            val currentTimeMillis = System.currentTimeMillis()
            val deltaTime = (currentTimeMillis - flingStartTime).toFloat() / 1000.0f
            heading = wrapAngle(heading + (headingFlingSpeed * deltaTime))
            tilt = max(min(tilt + (tiltFlingSpeed * deltaTime), MAX_PITCH), MIN_PITCH)
            
            if (headingFlingSpeed > 0.0f) {
                headingFlingSpeed -= FLING_DECEL_PITCH * deltaTime
                if (headingFlingSpeed < 0.0f) headingFlingSpeed = 0.0f
            } else if (headingFlingSpeed < 0.0f) {
                headingFlingSpeed += FLING_DECEL_PITCH * deltaTime
                if (headingFlingSpeed > 0.0f) headingFlingSpeed = 0.0f
            }
            
            if (tiltFlingSpeed > 0.0f) {
                tiltFlingSpeed -= deltaTime * FLING_DECEL_YAW
                if (tiltFlingSpeed < 0.0f) tiltFlingSpeed = 0.0f
            } else if (tiltFlingSpeed < 0.0f) {
                tiltFlingSpeed = (deltaTime * FLING_DECEL_YAW) + tiltFlingSpeed
                if (tiltFlingSpeed > 0.0f) tiltFlingSpeed = 0.0f
            }
            
            flingStartTime = currentTimeMillis
            if (tiltFlingSpeed == 0.0f && headingFlingSpeed == 0.0f) {
                isFlinging = false
            }
        }
    }
    
    // HeadTransformCompat interface for VR head tracking
    interface HeadTransformCompat {
        val yawDegrees: Float
        val pitchDegrees: Float
        val viewExtraYaw: Float
    }
    
    private fun processManualControl(headTransform: HeadTransformCompat?) {
        if (isManualControl) {
            val currentTimeMillis = System.currentTimeMillis()
            val deltaTime = (currentTimeMillis - manualControlStartTime).toFloat() / 1000.0f
            
            val currentHeading: Float
            val currentTilt: Float
            
            if (headTransform != null) {
                currentHeading = wrapAngle(headTransform.yawDegrees + headTransform.viewExtraYaw)
                currentTilt = headTransform.pitchDegrees
            } else {
                heading = wrapAngle(heading + (manualTurnSpeed * deltaTime))
                currentHeading = heading
                currentTilt = tilt
            }
            
            if (!(manualMoveSpeed == 0.0f && manualFlySpeed == 0.0f && manualStrafeSpeed == 0.0f)) {
                val quat = LLQuaternion()
                quat.setFromEuler(0.0f, currentTilt, currentHeading, LLQuaternion.Order.YZX)
                
                val forward = LLVector3(1.0f, 0.0f, 0.0f)
                val up = LLVector3(0.0f, 0.0f, 1.0f)
                val right = LLVector3(0.0f, 1.0f, 0.0f)
                
                forward.mul(quat)
                up.mul(quat)
                right.mul(quat)
                
                position.addMul(forward, manualMoveSpeed * deltaTime)
                position.addMul(up, manualFlySpeed * deltaTime)
                position.addMul(right, manualStrafeSpeed * deltaTime)
            }
            manualControlStartTime = currentTimeMillis
        }
    }
    
    fun wrapAngle(angle: Float): Float {
        var wrapped = (angle + 180.0f) % 360.0f
        if (wrapped < 0.0f) {
            wrapped += 360.0f
        }
        return wrapped - 180.0f
    }
    
    fun copyFrom(@Nullable other: CameraParams?) {
        if (other == null) return
        
        var posX: Float
        var posY: Float
        var posZ: Float
        var otherHeading: Float
        var otherTilt: Float
        var otherIsValid: Boolean
        
        synchronized(other.lock) {
            other.processFling()
            other.processManualControl(null)
            posX = other.position.x
            posY = other.position.y
            posZ = other.position.z
            otherHeading = other.heading
            otherTilt = other.tilt
            otherIsValid = other.isValid
            
            if (other.useOffset) {
                val offsetVec = LLVector3(offset)
                val quat = LLQuaternion()
                quat.setFromEuler(0.0f, otherTilt, otherHeading, LLQuaternion.Order.YZX)
                offsetVec.mul(quat)
                posX += offsetVec.x
                posY += offsetVec.y
                posZ += offsetVec.z
            }
        }
        
        synchronized(lock) {
            position.set(posX, posY, posZ)
            heading = otherHeading
            tilt = otherTilt
            isValid = otherIsValid
        }
    }
    
    fun fling(headingSpeed: Float, tiltSpeed: Float) {
        synchronized(lock) {
            headingFlingSpeed = headingSpeed
            tiltFlingSpeed = tiltSpeed
            flingStartTime = System.currentTimeMillis()
            isFlinging = true
        }
    }
    
    fun getHeading(): Float {
        synchronized(lock) {
            return heading
        }
    }
    
    @NonNull
    fun getPosition(): LLVector3 {
        synchronized(lock) {
            return LLVector3(position)
        }
    }
    
    fun getTilt(): Float {
        synchronized(lock) {
            return tilt
        }
    }
    
    fun getVRCamera(@Nullable other: CameraParams?, headTransform: HeadTransformCompat?) {
        if (other == null) return
        
        var posX: Float
        var posY: Float
        var posZ: Float
        var otherHeading: Float
        var otherTilt: Float
        var otherIsValid: Boolean
        
        synchronized(other.lock) {
            other.processManualControl(headTransform)
            posX = other.position.x
            posY = other.position.y
            posZ = other.position.z
            otherHeading = other.heading
            otherTilt = other.tilt
            otherIsValid = other.isValid
            
            if (other.useOffset) {
                val offsetVec = LLVector3(offsetVR)
                val quat = LLQuaternion()
                quat.setFromEuler(0.0f, otherTilt, otherHeading, LLQuaternion.Order.YZX)
                offsetVec.mul(quat)
                posX += offsetVec.x
                posY += offsetVec.y
                posZ += offsetVec.z
            }
        }
        
        synchronized(lock) {
            position.set(posX, posY, posZ)
            heading = otherHeading
            tilt = otherTilt
            isValid = otherIsValid
        }
    }
    
    fun isFlinging(): Boolean {
        synchronized(lock) {
            return isFlinging
        }
    }
    
    fun isValid(): Boolean {
        synchronized(lock) {
            return isValid
        }
    }
    
    fun rotate(headingDelta: Float, tiltDelta: Float) {
        synchronized(lock) {
            heading = wrapAngle(heading + headingDelta)
            tilt = max(min(tilt + tiltDelta, MAX_PITCH), MIN_PITCH)
            isFlinging = false
        }
    }
    
    fun set(@Nullable pos: LLVector3?, newHeading: Float, newTilt: Float) {
        synchronized(lock) {
            if (pos != null) {
                position.set(pos)
            }
            heading = newHeading
            tilt = newTilt
            isValid = true
        }
    }
    
    fun setHeading(newHeading: Float) {
        synchronized(lock) {
            heading = newHeading
        }
    }
    
    fun setPosition(@Nullable pos: LLVector3?) {
        synchronized(lock) {
            if (pos != null) {
                position.set(pos)
            }
            useOffset = true
            isValid = true
        }
    }
    
    fun setPosition(@Nullable pos: LLVector3?, newHeading: Float) {
        synchronized(lock) {
            if (pos != null) {
                position.set(pos)
            }
            heading = newHeading
            tilt = 0.0f
            isFlinging = false
            isValid = true
            useOffset = true
        }
    }
    
    fun startManualControl(turnSpeed: Float, moveSpeed: Float, flySpeed: Float, strafeSpeed: Float) {
        synchronized(lock) {
            if (!isManualControl) {
                val newPos = LLVector3(position)
                val quat = LLQuaternion()
                quat.setFromEuler(0.0f, tilt, heading, LLQuaternion.Order.YZX)
                
                if (!useOffset) {
                    val offsetVec = LLVector3(offset)
                    offsetVec.mul(quat)
                    newPos.add(offsetVec)
                    useOffset = true
                }
                position.set(newPos)
                isManualControl = true
                manualControlStartTime = System.currentTimeMillis()
            }
            manualMoveSpeed = moveSpeed
            manualTurnSpeed = turnSpeed
            manualFlySpeed = flySpeed
            manualStrafeSpeed = strafeSpeed
        }
    }
    
    fun stopManualControl() {
        synchronized(lock) {
            isManualControl = false
        }
    }
    
    fun zoom(scale: Float, horizOffset: Float, vertOffset: Float, horizDelta: Float, vertDelta: Float) {
        synchronized(lock) {
            val scaleDelta = scale - 1.0f
            val newPos = LLVector3(position)
            val quat = LLQuaternion()
            quat.setFromEuler(0.0f, tilt, heading, LLQuaternion.Order.YZX)
            
            if (!useOffset) {
                val offsetVec = LLVector3(offset)
                offsetVec.mul(quat)
                newPos.add(offsetVec)
                useOffset = true
            }
            
            val forward = LLVector3(1.0f, 0.0f, 0.0f)
            val up = LLVector3(0.0f, 0.0f, 1.0f)
            val right = LLVector3(0.0f, 1.0f, 0.0f)
            
            forward.mul(quat)
            forward.mul(scaleDelta)
            
            up.mul(quat)
            up.mul((scaleDelta * vertOffset) + vertDelta)
            
            right.mul(quat)
            right.mul((scaleDelta * horizOffset) + horizDelta)
            
            newPos.add(forward)
            newPos.add(up)
            newPos.add(right)
            
            position.set(newPos)
        }
    }
}
