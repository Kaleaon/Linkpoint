package com.linkpoint.slproto.users.manager

data class MyAvatarState(
    val isFlying: Boolean,
    val sittingOn: Int,
    val isSitting: Boolean,
    val hasHUDs: Boolean,
) {
    companion object {
        @JvmStatic
        fun create(
            isFlying: Boolean,
            sittingOn: Int,
            isSitting: Boolean,
            hasHUDs: Boolean,
        ): MyAvatarState {
            return MyAvatarState(isFlying, sittingOn, isSitting, hasHUDs)
        }
    }
}
