package com.linkpoint.slproto.users.manager

class AutoValue_MyAvatarState : MyAvatarState {
    private Boolean hasHUDs
    private Boolean isFlying
    private Boolean isSitting
    private Int sittingOn

    AutoValue_MyAvatarState(Boolean z, Int i, Boolean z2, Boolean z3) {
        this.isSitting = z
        this.sittingOn = i
        this.isFlying = z2
        this.hasHUDs = z3
    }

    fun equals(Any obj): Boolean {
        if (obj == this) {
            return true
        }
        if (!(obj is MyAvatarState)) {
            return false
        }
        MyAvatarState myAvatarState = (MyAvatarState) obj
        if (this.isSitting == myAvatarState.isSitting() && this.sittingOn == myAvatarState.sittingOn() && this.isFlying == myAvatarState.isFlying()) {
            return this.hasHUDs == myAvatarState.hasHUDs()
        }
        return false
    }

    fun hasHUDs(): Boolean {
        return this.hasHUDs
    }

    fun hashCode(): Int {
        var i: Int = 1231
        var i2: Int = ((this.isFlying ? 1231 : 1237) ^ (((((this.isSitting ? 1231 : 1237) ^ 1000003) * 1000003) ^ this.sittingOn) * 1000003)) * 1000003
        if (!this.hasHUDs) {
            i = 1237
        }
        return i2 ^ i
    }

    fun isFlying(): Boolean {
        return this.isFlying
    }

    fun isSitting(): Boolean {
        return this.isSitting
    }

    fun sittingOn(): Int {
        return this.sittingOn
    }

    fun toString(): String {
        return "MyAvatarState{isSitting=" + this.isSitting + ", " + "sittingOn=" + this.sittingOn + ", " + "isFlying=" + this.isFlying + ", " + "hasHUDs=" + this.hasHUDs + "}"
    }
}
