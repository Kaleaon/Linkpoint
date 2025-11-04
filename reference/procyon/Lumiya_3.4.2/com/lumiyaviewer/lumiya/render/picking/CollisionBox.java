// 
// Decompiled by Procyon v0.6.0
// 

package com.lumiyaviewer.lumiya.render.picking;

import com.lumiyaviewer.lumiya.slproto.types.LLVector3;

public class CollisionBox
{
    public final LLVector3[] vertices;
    
    private CollisionBox() {
        this.vertices = new LLVector3[36];
        this.addCollisionFace(0, -0.5f, -0.5f, 0.5f, 0.5f, -0.5f, 0);
        this.addCollisionFace(1, -0.5f, -0.5f, 0.5f, 0.5f, 0.5f, 0);
        this.addCollisionFace(2, -0.5f, -0.5f, 0.5f, 0.5f, -0.5f, 1);
        this.addCollisionFace(3, -0.5f, -0.5f, 0.5f, 0.5f, 0.5f, 1);
        this.addCollisionFace(4, -0.5f, -0.5f, 0.5f, 0.5f, -0.5f, 2);
        this.addCollisionFace(5, -0.5f, -0.5f, 0.5f, 0.5f, 0.5f, 2);
    }
    
    private void addCollisionFace(int n, final float n2, final float n3, final float n4, final float n5, final float n6, final int n7) {
        n = n * 2 * 3;
        final LLVector3[] array = { this.getCollisionVertex(n2, n3, n6, n7), this.getCollisionVertex(n4, n3, n6, n7), this.getCollisionVertex(n4, n5, n6, n7), this.getCollisionVertex(n2, n5, n6, n7) };
        this.vertices[n + 0] = array[0];
        this.vertices[n + 1] = array[1];
        this.vertices[n + 2] = array[3];
        this.vertices[n + 3] = array[1];
        this.vertices[n + 4] = array[2];
        this.vertices[n + 5] = array[3];
    }
    
    private LLVector3 getCollisionVertex(final float n, final float n2, final float n3, final int n4) {
        switch (n4) {
            default: {
                return null;
            }
            case 0: {
                return new LLVector3(n3, n, n2);
            }
            case 1: {
                return new LLVector3(n, n3, n2);
            }
            case 2: {
                return new LLVector3(n, n2, n3);
            }
        }
    }
    
    public static CollisionBox getInstance() {
        return InstanceHolder.Instance;
    }
    
    private static class InstanceHolder
    {
        private static final CollisionBox Instance;
        
        static {
            Instance = new CollisionBox(null);
        }
    }
}
