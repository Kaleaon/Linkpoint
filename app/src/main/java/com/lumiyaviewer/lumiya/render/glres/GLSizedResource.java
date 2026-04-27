package com.lumiyaviewer.lumiya.render.glres;

public abstract class GLSizedResource extends GLResource {
    private final int loadedSize;

    protected GLSizedResource(GLResourceManager gLResourceManager, int i) {
        super(gLResourceManager);
        this.loadedSize = i;
    }

    public final int getLoadedSize() {
        return this.loadedSize;
    }
}
