// 
// Decompiled by Procyon v0.6.0
// 

package com.lumiyaviewer.lumiya.slproto.mesh;

import javax.annotation.Nonnull;
import com.lumiyaviewer.rawbuffers.DirectByteBuffer;

public class MeshWeightsBuffer
{
    @Nonnull
    public final DirectByteBuffer jointIndexBuffer;
    @Nonnull
    public final DirectByteBuffer weightsBuffer;
    
    public MeshWeightsBuffer(final int n) {
        this.jointIndexBuffer = new DirectByteBuffer(n * 4);
        this.weightsBuffer = new DirectByteBuffer(n * 4 * 4);
    }
}
