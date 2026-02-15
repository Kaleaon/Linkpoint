package com.linkpoint.diagnostics

/** Marker for temporary placeholder-only entities. Release builds must not include these. */
@Target(AnnotationTarget.CLASS)
@Retention(AnnotationRetention.SOURCE)
annotation class PlaceholderOnlyEntity
