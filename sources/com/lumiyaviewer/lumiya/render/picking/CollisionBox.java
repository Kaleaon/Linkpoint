// Decompiled by Jad v1.5.8e. Copyright 2001 Pavel Kouznetsov.
// Jad home page: http://www.geocities.com/kpdus/jad.html
// Decompiler options: braces fieldsfirst space lnc 

package com.lumiyaviewer.lumiya.render.picking;

import com.lumiyaviewer.lumiya.slproto.types.LLVector3;

public class CollisionBox
{
    private static class InstanceHolder
    {

        private static final CollisionBox Instance = new CollisionBox(null);

        static CollisionBox _2D_get0()
        {
            return Instance;
        }


        private InstanceHolder()
        {
        }
    }


    public final LLVector3 vertices[];

    private CollisionBox()
    {
        vertices = new LLVector3[36];
        addCollisionFace(0, -0.5F, -0.5F, 0.5F, 0.5F, -0.5F, 0);
        addCollisionFace(1, -0.5F, -0.5F, 0.5F, 0.5F, 0.5F, 0);
        addCollisionFace(2, -0.5F, -0.5F, 0.5F, 0.5F, -0.5F, 1);
        addCollisionFace(3, -0.5F, -0.5F, 0.5F, 0.5F, 0.5F, 1);
        addCollisionFace(4, -0.5F, -0.5F, 0.5F, 0.5F, -0.5F, 2);
        addCollisionFace(5, -0.5F, -0.5F, 0.5F, 0.5F, 0.5F, 2);
    }

    CollisionBox(CollisionBox collisionbox)
    {
        this();
    }

    private void addCollisionFace(int i, float f, float f1, float f2, float f3, float f4, int j)
    {
        i = i * 2 * 3;
        LLVector3 allvector3[] = new LLVector3[4];
        allvector3[0] = getCollisionVertex(f, f1, f4, j);
        allvector3[1] = getCollisionVertex(f2, f1, f4, j);
        allvector3[2] = getCollisionVertex(f2, f3, f4, j);
        allvector3[3] = getCollisionVertex(f, f3, f4, j);
        vertices[i + 0] = allvector3[0];
        vertices[i + 1] = allvector3[1];
        vertices[i + 2] = allvector3[3];
        vertices[i + 3] = allvector3[1];
        vertices[i + 4] = allvector3[2];
        vertices[i + 5] = allvector3[3];
    }

    private LLVector3 getCollisionVertex(float f, float f1, float f2, int i)
    {
        switch (i)
        {
        default:
            return null;

        case 0: // '\0'
            return new LLVector3(f2, f, f1);

        case 1: // '\001'
            return new LLVector3(f, f2, f1);

        case 2: // '\002'
            return new LLVector3(f, f1, f2);
        }
    }

    public static CollisionBox getInstance()
    {
        return InstanceHolder._2D_get0();
    }
}
