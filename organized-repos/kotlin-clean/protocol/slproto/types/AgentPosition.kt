package com.linkpoint.slproto.types

import javax.annotation.Nonnull
import javax.annotation.Nullable
import javax.annotation.concurrent.ThreadSafe

@ThreadSafe
class AgentPosition {
    private Boolean isValid = false
    private Long lastAgentDataMillis = 0
    private val Object lock = Object()
    private val LLVector3 position = LLVector3()
    private val LLVector3 velocity = LLVector3()

    public ImmutableVector getImmutablePosition() {
        ImmutableVector immutableVector = null
        synchronized (this.lock) {
            if (this.isValid) {
                immutableVector = ImmutableVector(this.position)
            }
        }
        return immutableVector
    }

    public Boolean getInterpolatedPosition(LLVector3 lLVector3) {
        synchronized (this.lock) {
            if (this.isValid) {
                if (this.velocity.x == 0.0f && this.velocity.y == 0.0f && this.velocity.z == 0.0f) {
                    lLVector3.set(this.position)
                } else {
                    lLVector3.setMul(this.velocity, ((Float) (System.currentTimeMillis() - this.lastAgentDataMillis)) / 1000.0f)
                    lLVector3.add(this.position)
                }
                z = true
            } else {
                z = false
            }
        }
        return z
    }

    public LLVector3 getPosition() {
        LLVector3 lLVector3 = LLVector3()
        synchronized (this.lock) {
            if (this.isValid) {
                lLVector3.set(this.position)
            }
        }
        return lLVector3
    }

    public Boolean getPosition(LLVector3 lLVector3) {
        synchronized (this.lock) {
            if (this.isValid) {
                lLVector3.set(this.position)
                z = true
            } else {
                z = false
            }
        }
        return z
    }

    public Boolean isValid() {
        synchronized (this.lock) {
            z = this.isValid
        }
        return z
    }

    fun set(LLVector3 lLVector3, LLVector3 lLVector32) {
        synchronized (this.lock) {
            this.position.set(lLVector3)
            LLVector3 lLVector33 = this.velocity
            if (lLVector32 == null) {
                lLVector32 = LLVector3.Zero
            }
            lLVector33.set(lLVector32)
            this.lastAgentDataMillis = System.currentTimeMillis()
            this.isValid = true
        }
    }
}
