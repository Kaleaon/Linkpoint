package com.lumiyaviewer.lumiya.res.executors;
/* loaded from: classes.dex */
public class LoaderExecutor extends WeakExecutor {

    /* loaded from: classes.dex */
    private static class InstanceHolder {
        private static final LoaderExecutor Instance = new LoaderExecutor(null);

        private InstanceHolder() {
        }
    }

    private LoaderExecutor() {
        super("ResourceLoader", 1);
    }

    /* synthetic */ LoaderExecutor(LoaderExecutor loaderExecutor) {
        this();
    }

    public static LoaderExecutor getInstance() {
        return InstanceHolder.Instance;
    }
}
