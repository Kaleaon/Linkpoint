package com.linkpoint.slproto.types

import androidx.annotation.NonNull
import androidx.annotation.Nullable
import androidx.annotation.ThreadSafe

@ThreadSafe
class AgentPosition {
    private Boolean isValid = false
    private Long lastAgentDataMillis = 0
    private Any lock = Any()
    private LLVector3 position = LLVector3()
    private LLVector3 velocity = LLVector3()

    @Nullable
    fun getImmutablePosition(): ImmutableVector {
        ImmutableVector immutableVector = null
        synchronized (this.lock) {
            if (this.isValid) {
                immutableVector = ImmutableVector(this.position)
            }
        }
        return immutableVector
    }

    fun getInterpolatedPosition(@NonNull LLVector3 lLVector3): Boolean {
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

    @NonNull
    fun getPosition(): LLVector3 {
        LLVector3 lLVector3 = LLVector3()
        synchronized (this.lock) {
            if (this.isValid) {
                lLVector3.set(this.position)
            }
        }
        return lLVector3
    }

    fun getPosition(@NonNull LLVector3 lLVector3): Boolean {
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

    fun isValid(): Boolean {
        synchronized (this.lock) {
            z = this.isValid
        }
        return z
    }

    fun set(@NonNull LLVector3 lLVector3, @Nullable LLVector3 lLVector32)  {
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
