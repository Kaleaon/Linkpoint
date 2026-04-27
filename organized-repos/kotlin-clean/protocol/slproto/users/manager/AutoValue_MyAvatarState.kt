package com.linkpoint.slproto.users.manager

final class AutoValue_MyAvatarState : MyAvatarState() {
    private val Boolean hasHUDs
    private val Boolean isFlying
    private val Boolean isSitting
    private val Int sittingOn

    AutoValue_MyAvatarState(Boolean z, Int i, Boolean z2, Boolean z3) {
        this.isSitting = z
        this.sittingOn = i
        this.isFlying = z2
        this.hasHUDs = z3
    }

    public Boolean equals(Object obj) {
        if (obj == this) {
            return true
        }
        if (!(obj instanceof MyAvatarState)) {
            return false
        }
        MyAvatarState myAvatarState = (MyAvatarState) obj
        if (this.isSitting == myAvatarState.isSitting() && this.sittingOn == myAvatarState.sittingOn() && this.isFlying == myAvatarState.isFlying()) {
            return this.hasHUDs == myAvatarState.hasHUDs()
        }
        return false
    }

    public Boolean hasHUDs() {
        return this.hasHUDs
    }

    public Int hashCode() {
        Int i = 1231
        Int i2 = ((this.isFlying ? 1231 : 1237) ^ (((((this.isSitting ? 1231 : 1237) ^ 1000003) * 1000003) ^ this.sittingOn) * 1000003)) * 1000003
        if (!this.hasHUDs) {
            i = 1237
        }
        return i2 ^ i
    }

    public Boolean isFlying() {
        return this.isFlying
    }

    public Boolean isSitting() {
        return this.isSitting
    }

    public Int sittingOn() {
        return this.sittingOn
    }

    public String toString() {
        return "MyAvatarState{isSitting=" + this.isSitting + ", " + "sittingOn=" + this.sittingOn + ", " + "isFlying=" + this.isFlying + ", " + "hasHUDs=" + this.hasHUDs + "}"
    }
}
