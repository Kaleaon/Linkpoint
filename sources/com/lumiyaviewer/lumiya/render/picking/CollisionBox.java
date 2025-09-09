package com.lumiyaviewer.lumiya.render.picking;

import com.lumiyaviewer.lumiya.slproto.types.LLVector3;

public class CollisionBox {
    public final LLVector3[] vertices;

    private static class InstanceHolder {
        /* access modifiers changed from: private */
        public static final CollisionBox Instance = new CollisionBox((CollisionBox) null);

        private InstanceHolder() {
        }
    }

    private CollisionBox() {
        this.vertices = new LLVector3[36];
        addCollisionFace(0, -0.5f, -0.5f, 0.5f, 0.5f, -0.5f, 0);
        addCollisionFace(1, -0.5f, -0.5f, 0.5f, 0.5f, 0.5f, 0);
        addCollisionFace(2, -0.5f, -0.5f, 0.5f, 0.5f, -0.5f, 1);
        addCollisionFace(3, -0.5f, -0.5f, 0.5f, 0.5f, 0.5f, 1);
        addCollisionFace(4, -0.5f, -0.5f, 0.5f, 0.5f, -0.5f, 2);
        addCollisionFace(5, -0.5f, -0.5f, 0.5f, 0.5f, 0.5f, 2);
    }

    /* synthetic */ CollisionBox(CollisionBox collisionBox) {
        this();
    }

    private void addCollisionFace(int i, float f, float f2, float f3, float f4, float f5, int i2) {
        int i3 = i * 2 * 3;
        LLVector3[] lLVector3Arr = {getCollisionVertex(f, f2, f5, i2), getCollisionVertex(f3, f2, f5, i2), getCollisionVertex(f3, f4, f5, i2), getCollisionVertex(f, f4, f5, i2)};
        this.vertices[i3 + 0] = lLVector3Arr[0];
        this.vertices[i3 + 1] = lLVector3Arr[1];
        this.vertices[i3 + 2] = lLVector3Arr[3];
        this.vertices[i3 + 3] = lLVector3Arr[1];
        this.vertices[i3 + 4] = lLVector3Arr[2];
        this.vertices[i3 + 5] = lLVector3Arr[3];
    }

    private LLVector3 getCollisionVertex(float f, float f2, float f3, int i) {
        switch (i) {
            case 0:
                return new LLVector3(f3, f, f2);
            case 1:
                return new LLVector3(f, f3, f2);
            case 2:
                return new LLVector3(f, f2, f3);
            default:
                return null;
        }
    }

    public static CollisionBox getInstance() {
        return InstanceHolder.Instance;
    }
}
