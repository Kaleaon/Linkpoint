package com.linkpoint.slproto.llsd

@Target(AnnotationTarget.FIELD)
@Retention(AnnotationRetention.RUNTIME)
annotation class LLSDSerialized(
    val name: String = "",
)
