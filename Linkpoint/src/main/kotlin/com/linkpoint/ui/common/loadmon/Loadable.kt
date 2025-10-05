package com.linkpoint.ui.common.loadmon
import java.util.*

import javax.annotation.Nonnull

interface Loadable {

    interface LoadableStatusListener {
        Unit onLoadableStatusChange(Loadable loadable, Status status)
    }

    enum class Status {
        Idle,
        Loading,
        Loaded,
        Error
    }

    Unit addLoadableStatusListener(LoadableStatusListener loadableStatusListener)

    Status getLoadableStatus()
}
