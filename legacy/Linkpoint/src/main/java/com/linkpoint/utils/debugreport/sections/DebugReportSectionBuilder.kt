package com.linkpoint.utils.debugreport.sections

import com.linkpoint.utils.debugreport.DebugReportContext

interface DebugReportSectionBuilder {
    fun build(context: DebugReportContext): String
}
