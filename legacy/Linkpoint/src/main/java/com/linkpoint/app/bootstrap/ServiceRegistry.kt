package com.linkpoint.app.bootstrap

import com.linkpoint.LinkpointApp

class ServiceRegistry(private val app: LinkpointApp) {
    fun initializeCoreServices() {
        app.initializeManagers()
    }
}
