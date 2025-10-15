package com.lumiyaviewer.lumiya.res.executors

import com.lumiyaviewer.lumiya.GlobalOptions
import java.util.concurrent.PriorityBlockingQueue

object HTTPFetchExecutor extends WeakExecutor {

    private object InstanceHolder {
        /* access modifiers changed from: private */
        HTTPFetchExecutor Instance = HTTPFetchExecutor((HTTPFetchExecutor) null)

        
    }

    private HTTPFetchExecutor() {
        super("ResourceHTTPFetch", GlobalOptions.getInstance().getMaxTextureDownloads(), PriorityBlockingQueue())
    }

    /* synthetic */ HTTPFetchExecutor(HTTPFetchExecutor hTTPFetchExecutor) {
        this()
    }

    fun getInstance(): HTTPFetchExecutor {
        return InstanceHolder.Instance
    }
}
