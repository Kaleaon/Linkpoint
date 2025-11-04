// 
// Decompiled by Procyon v0.6.0
// 

package com.lumiyaviewer.lumiya.res.executors;

public class LoaderExecutor extends WeakExecutor
{
    private LoaderExecutor() {
        super("ResourceLoader", 1);
    }
    
    public static LoaderExecutor getInstance() {
        return InstanceHolder.Instance;
    }
    
    private static class InstanceHolder
    {
        private static final LoaderExecutor Instance;
        
        static {
            Instance = new LoaderExecutor(null);
        }
    }
}
