// 
// Decompiled by Procyon v0.6.0
// 

package com.lumiyaviewer.lumiya.slproto.users.manager;

final class AutoValue_MyAvatarState extends MyAvatarState
{
    private final boolean hasHUDs;
    private final boolean isFlying;
    private final boolean isSitting;
    private final int sittingOn;
    
    AutoValue_MyAvatarState(final boolean isSitting, final int sittingOn, final boolean isFlying, final boolean hasHUDs) {
        this.isSitting = isSitting;
        this.sittingOn = sittingOn;
        this.isFlying = isFlying;
        this.hasHUDs = hasHUDs;
    }
    
    @Override
    public boolean equals(final Object o) {
        boolean b = true;
        if (o == this) {
            return true;
        }
        if (o instanceof MyAvatarState) {
            final MyAvatarState myAvatarState = (MyAvatarState)o;
            if (this.isSitting == myAvatarState.isSitting() && this.sittingOn == myAvatarState.sittingOn() && this.isFlying == myAvatarState.isFlying()) {
                if (this.hasHUDs != myAvatarState.hasHUDs()) {
                    b = false;
                }
            }
            else {
                b = false;
            }
            return b;
        }
        return false;
    }
    
    @Override
    public boolean hasHUDs() {
        return this.hasHUDs;
    }
    
    @Override
    public int hashCode() {
        int n = 1231;
        int n2;
        if (this.isSitting) {
            n2 = 1231;
        }
        else {
            n2 = 1237;
        }
        final int sittingOn = this.sittingOn;
        int n3;
        if (this.isFlying) {
            n3 = 1231;
        }
        else {
            n3 = 1237;
        }
        if (!this.hasHUDs) {
            n = 1237;
        }
        return (n3 ^ ((n2 ^ 0xF4243) * 1000003 ^ sittingOn) * 1000003) * 1000003 ^ n;
    }
    
    @Override
    public boolean isFlying() {
        return this.isFlying;
    }
    
    @Override
    public boolean isSitting() {
        return this.isSitting;
    }
    
    @Override
    public int sittingOn() {
        return this.sittingOn;
    }
    
    @Override
    public String toString() {
        return "MyAvatarState{isSitting=" + this.isSitting + ", " + "sittingOn=" + this.sittingOn + ", " + "isFlying=" + this.isFlying + ", " + "hasHUDs=" + this.hasHUDs + "}";
    }
}
