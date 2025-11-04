// 
// Decompiled by Procyon v0.6.0
// 

package com.lumiyaviewer.lumiya.slproto.avatar;

public enum MeshIndex
{
    MESH_ID_EYEBALL_LEFT("MESH_ID_EYEBALL_LEFT", 5), 
    MESH_ID_EYEBALL_RIGHT("MESH_ID_EYEBALL_RIGHT", 6), 
    MESH_ID_EYELASH("MESH_ID_EYELASH", 2), 
    MESH_ID_HAIR("MESH_ID_HAIR", 0), 
    MESH_ID_HEAD("MESH_ID_HEAD", 1), 
    MESH_ID_LOWER_BODY("MESH_ID_LOWER_BODY", 4), 
    MESH_ID_SKIRT("MESH_ID_SKIRT", 7), 
    MESH_ID_UPPER_BODY("MESH_ID_UPPER_BODY", 3);
    
    public static final MeshIndex[] VALUES;
    
    static {
        VALUES = values();
    }
    
    private MeshIndex(final String name, final int ordinal) {
    }
}
