package com.linkpoint.utils.debugreport

import android.content.Context
import com.linkpoint.LinkpointApp

data class DebugReportContext(
    val androidContext: Context,
    val app: LinkpointApp?,
    val timestampMs: Long
)
