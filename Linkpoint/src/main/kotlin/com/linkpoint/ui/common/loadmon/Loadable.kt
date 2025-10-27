package com.linkpoint.ui.common.loadmon
import java.util.*

import javax.annotation.Nonnull

interface Loadable {

    interface LoadableStatusListener {
         fun onLoadableStatusChange(loadable: Loadable, status: Status)
    }

    enum class Status {
        Idle,
        Loading,
        Loaded,
        Error
    }

     fun addLoadableStatusListener(loadableStatusListener: LoadableStatusListener)

     fun getLoadableStatus(): Status)
}
