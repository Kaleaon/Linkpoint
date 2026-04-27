package com.lumiyaviewer.lumiya.slproto.types;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.annotation.concurrent.ThreadSafe;

@ThreadSafe
/* loaded from: classes.dex */
public class AgentPosition {
    private final Object lock = new Object();
    private boolean isValid = false;
    private final LLVector3 position = new LLVector3();
    private final LLVector3 velocity = new LLVector3();
    private long lastAgentDataMillis = 0;

    @Nullable
    public ImmutableVector getImmutablePosition() {
        ImmutableVector immutableVector;
        synchronized (this.lock) {
            immutableVector = this.isValid ? new ImmutableVector(this.position) : null;
        }
        return immutableVector;
    }

    public boolean getInterpolatedPosition(@Nonnull LLVector3 lLVector3) {
        boolean z;
        synchronized (this.lock) {
            if (this.isValid) {
                if (this.velocity.x == 0.0f && this.velocity.y == 0.0f && this.velocity.z == 0.0f) {
                    lLVector3.set(this.position);
                } else {
                    lLVector3.setMul(this.velocity, ((float) (System.currentTimeMillis() - this.lastAgentDataMillis)) / 1000.0f);
                    lLVector3.add(this.position);
                }
                z = true;
            } else {
                z = false;
            }
        }
        return z;
    }

    @Nonnull
    public LLVector3 getPosition() {
        LLVector3 lLVector3 = new LLVector3();
        synchronized (this.lock) {
            if (this.isValid) {
                lLVector3.set(this.position);
            }
        }
        return lLVector3;
    }

    public boolean getPosition(@Nonnull LLVector3 lLVector3) {
        boolean z;
        synchronized (this.lock) {
            if (this.isValid) {
                lLVector3.set(this.position);
                z = true;
            } else {
                z = false;
            }
        }
        return z;
    }

    public boolean isValid() {
        boolean z;
        synchronized (this.lock) {
            z = this.isValid;
        }
        return z;
    }

    public void set(@Nonnull LLVector3 lLVector3, @Nullable LLVector3 lLVector32) {
        synchronized (this.lock) {
            this.position.set(lLVector3);
            LLVector3 lLVector33 = this.velocity;
            if (lLVector32 == null) {
                lLVector32 = LLVector3.Zero;
            }
            lLVector33.set(lLVector32);
            this.lastAgentDataMillis = System.currentTimeMillis();
            this.isValid = true;
        }
    }
}
