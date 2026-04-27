package com.lumiyaviewer.lumiya.ui.common.loadmon

interface Loadable {
    interface LoadableStatusListener {
        fun onLoadableStatusChange(loadable: Loadable, status: Status)
    }

    enum class Status {
        Idle,
        Loading,
        Loaded,
        Error,
    }

    fun addLoadableStatusListener(loadableStatusListener: LoadableStatusListener)

    fun getLoadableStatus(): Status
}