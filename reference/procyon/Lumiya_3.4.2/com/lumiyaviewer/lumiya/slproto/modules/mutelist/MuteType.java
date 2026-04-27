// 
// Decompiled by Procyon v0.6.0
// 

package com.lumiyaviewer.lumiya.slproto.modules.mutelist;

public enum MuteType
{
    AGENT("AGENT", 1, 0), 
    BY_NAME("BY_NAME", 0, 2), 
    EXTERNAL("EXTERNAL", 4, 4), 
    GROUP("GROUP", 3, 3), 
    OBJECT("OBJECT", 2, 1);
    
    private int viewOrder;
    
    private MuteType(final String name, final int ordinal, final int viewOrder) {
        this.viewOrder = viewOrder;
    }
    
    public int getViewOrder() {
        return this.viewOrder;
    }
}
