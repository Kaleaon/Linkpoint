package com.lumiyaviewer.lumiya.slproto.mesh;

import com.lumiyaviewer.rawbuffers.DirectByteBuffer;
import javax.annotation.Nonnull;

public class MeshWeightsBuffer {
    @Nonnull
    public final DirectByteBuffer jointIndexBuffer;
    @Nonnull
    public final DirectByteBuffer weightsBuffer;

    public MeshWeightsBuffer(int i) {
        this.jointIndexBuffer = new DirectByteBuffer(i * 4);
        this.weightsBuffer = new DirectByteBuffer(i * 4 * 4);
    }
}
