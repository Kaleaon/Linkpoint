package com.linkpoint.slproto.users.manager

abstract class MyAvatarState {
    abstract fun hasHUDs(): Boolean
    abstract fun isFlying(): Boolean
    abstract fun isSitting(): Boolean
    abstract fun sittingOn(): Int

    companion object {
        @JvmStatic
        fun create(isFlying: Boolean, sittingOn: Int, isSitting: Boolean, hasHUDs: Boolean): MyAvatarState {
            return AutoValue_MyAvatarState(isFlying, sittingOn, isSitting, hasHUDs)
        }
    }
}