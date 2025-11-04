// 
// Decompiled by Procyon v0.6.0
// 

package com.lumiyaviewer.lumiya.slproto.baker;

public enum SLAvatarGlobalColor
{
    eye_color("eye_color", 2, new int[] { 99, 98 }), 
    hair_color("hair_color", 1, new int[] { 114, 113, 115, 112 }), 
    skin_color("skin_color", 0, new int[] { 111, 110, 108 });
    
    private int[] paramIDs;
    
    private SLAvatarGlobalColor(final String name, final int ordinal, final int[] paramIDs) {
        this.paramIDs = paramIDs;
    }
    
    public int[] getParamIDs() {
        return this.paramIDs;
    }
}
