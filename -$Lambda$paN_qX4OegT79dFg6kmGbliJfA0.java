package com.lumiyaviewer.lumiya.res.executors;

import java.util.concurrent.ThreadFactory;

final /* synthetic */ class -$Lambda$paN_qX4OegT79dFg6kmGbliJfA0 implements ThreadFactory {
    /* renamed from: -$f0 */
    private final /* synthetic */ Object f501-$f0;

    /* renamed from: com.lumiyaviewer.lumiya.res.executors.-$Lambda$paN_qX4OegT79dFg6kmGbliJfA0$1 */
    final /* synthetic */ class AnonymousClass1 implements ThreadFactory {
        /* renamed from: -$f0 */
        private final /* synthetic */ Object f502-$f0;

        private final /* synthetic */ Thread $m$0(Runnable runnable) {
            return WeakExecutor.m78lambda$-com_lumiyaviewer_lumiya_res_executors_WeakExecutor_1106((String) this.f502-$f0, runnable);
        }

        public /* synthetic */ AnonymousClass1(Object obj) {
            this.f502-$f0 = obj;
        }

        public final Thread newThread(Runnable runnable) {
            return $m$0(runnable);
        }
    }

    private final /* synthetic */ Thread $m$0(Runnable runnable) {
        return WeakExecutor.m79lambda$-com_lumiyaviewer_lumiya_res_executors_WeakExecutor_531((String) this.f501-$f0, runnable);
    }

    public /* synthetic */ -$Lambda$paN_qX4OegT79dFg6kmGbliJfA0(Object obj) {
        this.f501-$f0 = obj;
    }

    public final Thread newThread(Runnable runnable) {
        return $m$0(runnable);
    }
}
