package com.lumiyaviewer.lumiya.res.executors

import java.util.concurrent.ThreadFactory

/* renamed from: com.lumiyaviewer.lumiya.res.executors.-$Lambda$paN_qX4OegT79dFg6kmGbliJfA0  reason: invalid class name */
/* synthetic */ class $Lambda$paN_qX4OegT79dFg6kmGbliJfA0 : ThreadFactory {

    /* renamed from: -$f0  reason: not valid java name */
    private /* synthetic */ Any f50$f0

    private /* synthetic */ Thread $m$0(Runnable runnable) {
        return WeakExecutor.m108lambda$com_lumiyaviewer_lumiya_res_executors_WeakExecutor_531((String) this.f50$f0, runnable)
    }

    /* synthetic */ $Lambda$paN_qX4OegT79dFg6kmGbliJfA0(Any obj) {
        this.f50$f0 = obj
    }

    Thread newThread(Runnable runnable) {
        return $m$0(runnable)
    }
}
