package com.lumiyaviewer.lumiya.res.executors;

public class LoaderExecutor extends WeakExecutor {

    private static class InstanceHolder {
        /* access modifiers changed from: private */
        public static final LoaderExecutor Instance = new LoaderExecutor((LoaderExecutor) null);

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
