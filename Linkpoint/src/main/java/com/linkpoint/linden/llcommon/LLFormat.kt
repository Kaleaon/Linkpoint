package com.linkpoint.linden.llcommon

import java.util.Locale

fun llformat(fmt: String, vararg args: Any?): String =
    String.format(Locale.ROOT, fmt, *args)
