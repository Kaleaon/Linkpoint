// 
// Decompiled by Procyon v0.6.0
// 

package com.lumiyaviewer.lumiya.render.tex;

public enum TexturePriority
{
    Asset("Asset", 0), 
    Lowest("Lowest", 7), 
    PrimInvisible("PrimInvisible", 6), 
    PrimVisibleClose("PrimVisibleClose", 2), 
    PrimVisibleFar("PrimVisibleFar", 5), 
    PrimVisibleMedium("PrimVisibleMedium", 3), 
    Sculpt("Sculpt", 1), 
    Terrain("Terrain", 4);
    
    private TexturePriority(final String name, final int ordinal) {
    }
}
