package com.lumiyaviewer.lumiya.res.executors;

import java.util.concurrent.ThreadFactory;

/* renamed from: com.lumiyaviewer.lumiya.res.executors.-$Lambda$paN_qX4OegT79dFg6kmGbliJfA0  reason: invalid class name */
final /* synthetic */ class $Lambda$paN_qX4OegT79dFg6kmGbliJfA0 implements ThreadFactory {

    /* renamed from: -$f0  reason: not valid java name */
    private final /* synthetic */ Object f50$f0;

    private final /* synthetic */ Thread $m$0(Runnable runnable) {
        return WeakExecutor.m108lambda$com_lumiyaviewer_lumiya_res_executors_WeakExecutor_531((String) this.f50$f0, runnable);
    }

    public /* synthetic */ $Lambda$paN_qX4OegT79dFg6kmGbliJfA0(Object obj) {
        this.f50$f0 = obj;
    }

    public final Thread newThread(Runnable runnable) {
        return $m$0(runnable);
    }
}
