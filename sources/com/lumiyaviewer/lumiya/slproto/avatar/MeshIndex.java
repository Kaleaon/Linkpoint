// Decompiled by Jad v1.5.8e. Copyright 2001 Pavel Kouznetsov.
// Jad home page: http://www.geocities.com/kpdus/jad.html
// Decompiler options: braces fieldsfirst space lnc 

package com.lumiyaviewer.lumiya.slproto.avatar;


public final class MeshIndex extends Enum
{

    private static final MeshIndex $VALUES[];
    public static final MeshIndex MESH_ID_EYEBALL_LEFT;
    public static final MeshIndex MESH_ID_EYEBALL_RIGHT;
    public static final MeshIndex MESH_ID_EYELASH;
    public static final MeshIndex MESH_ID_HAIR;
    public static final MeshIndex MESH_ID_HEAD;
    public static final MeshIndex MESH_ID_LOWER_BODY;
    public static final MeshIndex MESH_ID_SKIRT;
    public static final MeshIndex MESH_ID_UPPER_BODY;
    public static final MeshIndex VALUES[] = values();

    private MeshIndex(String s, int i)
    {
        super(s, i);
    }

    public static MeshIndex valueOf(String s)
    {
        return (MeshIndex)Enum.valueOf(com/lumiyaviewer/lumiya/slproto/avatar/MeshIndex, s);
    }

    public static MeshIndex[] values()
    {
        return $VALUES;
    }

    static 
    {
        MESH_ID_HAIR = new MeshIndex("MESH_ID_HAIR", 0);
        MESH_ID_HEAD = new MeshIndex("MESH_ID_HEAD", 1);
        MESH_ID_EYELASH = new MeshIndex("MESH_ID_EYELASH", 2);
        MESH_ID_UPPER_BODY = new MeshIndex("MESH_ID_UPPER_BODY", 3);
        MESH_ID_LOWER_BODY = new MeshIndex("MESH_ID_LOWER_BODY", 4);
        MESH_ID_EYEBALL_LEFT = new MeshIndex("MESH_ID_EYEBALL_LEFT", 5);
        MESH_ID_EYEBALL_RIGHT = new MeshIndex("MESH_ID_EYEBALL_RIGHT", 6);
        MESH_ID_SKIRT = new MeshIndex("MESH_ID_SKIRT", 7);
        $VALUES = (new MeshIndex[] {
            MESH_ID_HAIR, MESH_ID_HEAD, MESH_ID_EYELASH, MESH_ID_UPPER_BODY, MESH_ID_LOWER_BODY, MESH_ID_EYEBALL_LEFT, MESH_ID_EYEBALL_RIGHT, MESH_ID_SKIRT
        });
    }
}
