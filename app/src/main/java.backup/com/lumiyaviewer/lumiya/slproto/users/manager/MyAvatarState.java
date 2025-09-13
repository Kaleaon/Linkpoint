package com.lumiyaviewer.lumiya.slproto.users.manager;

public abstract class MyAvatarState {
    public static MyAvatarState create(boolean z, int i, boolean z2, boolean z3) {
        return new AutoValue_MyAvatarState(z, i, z2, z3);
    }

    public abstract boolean hasHUDs();

    public abstract boolean isFlying();

    public abstract boolean isSitting();

    public abstract int sittingOn();
}
