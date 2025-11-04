// 
// Decompiled by Procyon v0.6.0
// 

package com.lumiyaviewer.lumiya.slproto.users.manager;

public abstract class MyAvatarState
{
    public static MyAvatarState create(final boolean b, final int n, final boolean b2, final boolean b3) {
        return new AutoValue_MyAvatarState(b, n, b2, b3);
    }
    
    public abstract boolean hasHUDs();
    
    public abstract boolean isFlying();
    
    public abstract boolean isSitting();
    
    public abstract int sittingOn();
}
