// Decompiled by Jad v1.5.8e. Copyright 2001 Pavel Kouznetsov.
// Jad home page: http://www.geocities.com/kpdus/jad.html
// Decompiler options: braces fieldsfirst space lnc 

package com.lumiyaviewer.lumiya.react;

import java.util.concurrent.Executor;

// Referenced classes of package com.lumiyaviewer.lumiya.react:
//            ResultHandler

public abstract class ResultOperator
    implements ResultHandler
{

    private final Executor executor;
    private final ResultHandler toHandler;

    public ResultOperator(ResultHandler resulthandler)
    {
        toHandler = resulthandler;
        executor = null;
    }

    public ResultOperator(ResultHandler resulthandler, Executor executor1)
    {
        toHandler = resulthandler;
        executor = executor1;
    }

    void lambda$_2D_com_lumiyaviewer_lumiya_react_ResultOperator_1065(Object obj, Throwable throwable)
    {
        toHandler.onResultError(obj, throwable);
    }

    void lambda$_2D_com_lumiyaviewer_lumiya_react_ResultOperator_796(Object obj, Object obj1)
    {
        toHandler.onResultData(obj, onData(obj1));
    }

    protected abstract Object onData(Object obj);

    public void onResultData(Object obj, Object obj1)
    {
        if (executor != null)
        {
            executor.execute(new _2D_.Lambda.rbwdofHzZNihI1HZoTkj8gWFECo(this, obj, obj1));
            return;
        } else
        {
            toHandler.onResultData(obj, onData(obj1));
            return;
        }
    }

    public void onResultError(Object obj, Throwable throwable)
    {
        if (executor != null)
        {
            executor.execute(new _2D_.Lambda.rbwdofHzZNihI1HZoTkj8gWFECo._cls1(this, obj, throwable));
            return;
        } else
        {
            toHandler.onResultError(obj, throwable);
            return;
        }
    }
}
