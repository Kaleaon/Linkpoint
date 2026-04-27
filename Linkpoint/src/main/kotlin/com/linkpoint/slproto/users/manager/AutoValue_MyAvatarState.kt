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

     public override fun equals(obj: Object): Boolean {
        if (obj == this) {
            return true
        }
        if (!(obj instanceof MyAvatarState)) {
            return false
        }
        val myAvatarState: MyAvatarState = (MyAvatarState) obj
        if (this.isSitting == myAvatarState.isSitting() && this.sittingOn == myAvatarState.sittingOn() && this.isFlying == myAvatarState.isFlying()) {
            return this.hasHUDs == myAvatarState.hasHUDs()
        }
        return false
    }

     public fun hasHUDs(): Boolean {
        return this.hasHUDs
    }

     public override fun hashCode(): Int {
        val i: Int = 1231
        val i2: Int = ((this.isFlying ? 1231 : 1237) ^ (((((this.isSitting ? 1231 : 1237) ^ 1000003) * 1000003) ^ this.sittingOn) * 1000003)) * 1000003
        if (!this.hasHUDs) {
            i = 1237
        }
        return i2 ^ i
    }

     public fun isFlying(): Boolean {
        return this.isFlying
    }

     public fun isSitting(): Boolean {
        return this.isSitting
    }

     public fun sittingOn(): Int {
        return this.sittingOn
    }

     public override fun toString(): String {
        return "MyAvatarState{isSitting=" + this.isSitting + ", " + "sittingOn=" + this.sittingOn + ", " + "isFlying=" + this.isFlying + ", " + "hasHUDs=" + this.hasHUDs + "}"
    }
}
