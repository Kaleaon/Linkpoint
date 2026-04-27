/*
 * Decompiled with CFR 0.152.
 */
package com.lumiyaviewer.lumiya.res.executors;

import com.lumiyaviewer.lumiya.res.executors.WeakExecutor;
import java.util.concurrent.ThreadFactory;

/*
 * Illegal identifiers - consider using --renameillegalidents true
 */
final class _$Lambda$paN_qX4OegT79dFg6kmGbliJfA0$1
implements ThreadFactory {
    private final Object -$f0;

    private final /* synthetic */ Thread $m$0(Runnable runnable) {
        return WeakExecutor.lambda$-com_lumiyaviewer_lumiya_res_executors_WeakExecutor_1106((String)this.-$f0, runnable);
    }

    public /* synthetic */ _$Lambda$paN_qX4OegT79dFg6kmGbliJfA0$1(Object object) {
        this.-$f0 = object;
    }

    @Override
    public final Thread newThread(Runnable runnable) {
        return this.$m$0(runnable);
    }
}
