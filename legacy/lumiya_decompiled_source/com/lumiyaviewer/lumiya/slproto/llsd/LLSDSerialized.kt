package com.lumiyaviewer.lumiya.slproto.llsd

@Target(AnnotationTarget.FIELD)
@Retention(AnnotationRetention.RUNTIME)
annotation class LLSDSerialized(
    val name: String = ""
)
