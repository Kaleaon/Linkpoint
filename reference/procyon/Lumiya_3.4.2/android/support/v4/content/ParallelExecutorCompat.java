// 
// Decompiled by Procyon v0.6.0
// 

package android.support.v4.content;

import android.os.AsyncTask;
import java.util.concurrent.Executor;
@Deprecated
<<<<<<<< HEAD:lumiya_decompiled_source/android/support/v4/content/ParallelExecutorCompat.java
/* loaded from: classes.dex */
public final class ParallelExecutorCompat {
========
public final class ParallelExecutorCompat
{
>>>>>>>> origin/cursor/research-and-propose-second-life-framework-extensions-56f5:reference/procyon/Lumiya_3.4.2/android/support/v4/content/ParallelExecutorCompat.java
    private ParallelExecutorCompat() {
    }
    
    @Deprecated
    public static Executor getParallelExecutor() {
        return AsyncTask.THREAD_POOL_EXECUTOR;
    }
}
