package com.linkpoint.res.executors

import com.linkpoint.GlobalOptions
import java.util.concurrent.PriorityBlockingQueue

class HTTPFetchExecutor : WeakExecutor() {

    @JvmStatic
private class InstanceHolder {
        /* access modifiers changed from: private */
        const val HTTPFetchExecutor Instance = HTTPFetchExecutor((HTTPFetchExecutor) null)

        private InstanceHolder() {
        }
    }

    private HTTPFetchExecutor() {
        super("ResourceHTTPFetch", GlobalOptions.getInstance().getMaxTextureDownloads(), PriorityBlockingQueue())
    }

    /* synthetic */ HTTPFetchExecutor(HTTPFetchExecutor hTTPFetchExecutor) {
        this()
    }

    @JvmStatic
     fun getInstance(): HTTPFetchExecutor {
        return InstanceHolder.Instance
    }
}
