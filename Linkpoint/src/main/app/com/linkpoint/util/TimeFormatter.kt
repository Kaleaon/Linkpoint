package com.linkpoint.util

import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class TimeFormatter(
    pattern: String = "HH:mm"
) {
    private val dateFormat = SimpleDateFormat(pattern, Locale.getDefault())

    fun format(timestamp: Long): String = dateFormat.format(Date(timestamp))
}
