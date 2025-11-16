package com.linkpoint

import android.app.Application
import android.content.Context
import com.linkpoint.di.LinkpointServiceLocator

/**
 * Entry point for the modern Linkpoint application.
 *
 * The legacy source tree that shipped with Lumiya is still present in the repo,
 * but the Gradle source set now points at this lightweight, fully Kotlin based
 * implementation so we can iterate quickly on rendering + chat features.
 */
class LinkpointApp : Application() {

    lateinit var services: LinkpointServiceLocator
        private set

    override fun onCreate() {
        super.onCreate()
        instance = this
        services = LinkpointServiceLocator(this)
    }

    companion object {
        private lateinit var instance: LinkpointApp

        fun get(): LinkpointApp = instance

        fun from(context: Context): LinkpointApp = context.applicationContext as LinkpointApp
    }
}
