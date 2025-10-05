package com.linkpoint.res.executors

class LoaderExecutor : WeakExecutor() {

    @JvmStatic
private class InstanceHolder {
        /* access modifiers changed from: private */
        const val LoaderExecutor Instance = LoaderExecutor((LoaderExecutor) null)

        private InstanceHolder() {
        }
    }

    private LoaderExecutor() {
        super("ResourceLoader", 1)
    }

    /* synthetic */ LoaderExecutor(LoaderExecutor loaderExecutor) {
        this()
    }

    @JvmStatic
    LoaderExecutor getInstance() {
        return InstanceHolder.Instance
    }
}
