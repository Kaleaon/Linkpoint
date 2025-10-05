package com.linkpoint.cloud.common

import android.os.Bundle
import android.support.v4.app.NotificationCompat

class LogSyncStatus : Bundleable {
    val errorMessage: String?
    val pluginVersionCode: Int
    val status: Status

    enum class Status {
        Ready,
        AppVersionRejected,
        GoogleDriveError
    }

    constructor(pluginVersionCode: Int, status: Status, errorMessage: String?) {
        this.pluginVersionCode = pluginVersionCode
        this.status = status
        this.errorMessage = errorMessage
    }

    constructor(bundle: Bundle) {
        this.pluginVersionCode = bundle.getInt("pluginVersionCode")
        this.status = Status.valueOf(bundle.getString(NotificationCompat.CATEGORY_STATUS)!!)
        this.errorMessage = bundle.getString("errorMessage")
    }

    override fun toBundle(): Bundle {
        return Bundle().apply {
            putInt("pluginVersionCode", pluginVersionCode)
            putString(NotificationCompat.CATEGORY_STATUS, status.toString())
            putString("errorMessage", errorMessage)
        }
    }
}