// 
// Decompiled by Procyon v0.6.0
// 

package com.lumiyaviewer.lumiya.slproto.modules.rlv;

import java.util.UUID;

public interface RLVCommand
{
    void Handle(final RLVController p0, final UUID p1, final RLVCommands p2, final String p3, final String p4);
}
