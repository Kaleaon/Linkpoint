// 
// Decompiled by Procyon v0.6.0
// 

package com.lumiyaviewer.lumiya.ui.common.loadmon;

import javax.annotation.Nonnull;

public interface Loadable
{
    void addLoadableStatusListener(final LoadableStatusListener p0);
    
    @Nonnull
    Status getLoadableStatus();
    
    public interface LoadableStatusListener
    {
        void onLoadableStatusChange(final Loadable p0, final Status p1);
    }
    
    public enum Status
    {
        Error("Error", 3), 
        Idle("Idle", 0), 
        Loaded("Loaded", 2), 
        Loading("Loading", 1);
        
        private Status(final String name, final int ordinal) {
        }
    }
}
