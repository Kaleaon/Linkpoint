// 
// Decompiled by Procyon v0.6.0
// 

package com.lumiyaviewer.lumiya.slproto.types;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.annotation.concurrent.ThreadSafe;

@ThreadSafe
public class AgentPosition
{
    private boolean isValid;
    private long lastAgentDataMillis;
    private final Object lock;
    private final LLVector3 position;
    private final LLVector3 velocity;
    
    public AgentPosition() {
        this.lock = new Object();
        this.isValid = false;
        this.position = new LLVector3();
        this.velocity = new LLVector3();
        this.lastAgentDataMillis = 0L;
    }
    
    @Nullable
    public ImmutableVector getImmutablePosition() {
        ImmutableVector immutableVector = null;
        synchronized (this.lock) {
            if (this.isValid) {
                immutableVector = new ImmutableVector(this.position);
            }
            return immutableVector;
        }
    }
    
    public boolean getInterpolatedPosition(@Nonnull final LLVector3 llVector3) {
        while (true) {
            synchronized (this.lock) {
                if (this.isValid) {
                    if (this.velocity.x == 0.0f && this.velocity.y == 0.0f && this.velocity.z == 0.0f) {
                        llVector3.set(this.position);
                    }
                    else {
                        llVector3.setMul(this.velocity, (System.currentTimeMillis() - this.lastAgentDataMillis) / 1000.0f);
                        llVector3.add(this.position);
                    }
                    return true;
                }
            }
            return false;
        }
    }
    
    @Nonnull
    public LLVector3 getPosition() {
        final LLVector3 llVector3 = new LLVector3();
        synchronized (this.lock) {
            if (this.isValid) {
                llVector3.set(this.position);
            }
            return llVector3;
        }
    }
    
    public boolean getPosition(@Nonnull final LLVector3 llVector3) {
        synchronized (this.lock) {
            boolean b;
            if (this.isValid) {
                llVector3.set(this.position);
                b = true;
            }
            else {
                b = false;
            }
            return b;
        }
    }
    
    public boolean isValid() {
        synchronized (this.lock) {
            return this.isValid;
        }
    }
    
    public void set(@Nonnull LLVector3 velocity, @Nullable LLVector3 zero) {
        synchronized (this.lock) {
            this.position.set(velocity);
            velocity = this.velocity;
            if (zero == null) {
                zero = LLVector3.Zero;
            }
            velocity.set(zero);
            this.lastAgentDataMillis = System.currentTimeMillis();
            this.isValid = true;
        }
    }
}
