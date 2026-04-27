package com.linkpoint.ui.navigation

import android.content.Intent
import android.net.Uri

/**
 * Resolves incoming intents and external links into internal Compose routes.
 */
object RouteResolver {

    fun resolve(intent: Intent?): String? {
        val data = intent?.data ?: return null
        return resolve(data)
    }

    fun resolve(uri: Uri): String? {
        val host = uri.host.orEmpty().lowercase()
        val segments = uri.pathSegments

        if (uri.scheme.equals("secondlife", ignoreCase = true)) {
            // secondlife://Region/128/128/20
            if (segments.isNotEmpty()) {
                return Routes.slurl(uri.toString())
            }
        }

        if (host == "maps.secondlife.com") {
            // https://maps.secondlife.com/secondlife/Region/128/128/20
            if (segments.size >= 5 && segments[0] == "secondlife") {
                val region = segments[1]
                val x = segments[2]
                val y = segments[3]
                val z = segments[4]
                return Routes.placeDetail(region, x, y, z)
            }
        }

        // profile links: /profile/{id} or /app/profile/{id}
        val profileIndex = segments.indexOf("profile")
        if (profileIndex >= 0 && profileIndex + 1 < segments.size) {
            val profileId = segments[profileIndex + 1]
            return if (profileId == "me") Routes.MY_PROFILE else Routes.profile(profileId)
        }

        // place links: /place/{region}/{x}/{y}/{z} and optional /events
        val placeIndex = segments.indexOf("place")
        if (placeIndex >= 0 && placeIndex + 4 < segments.size) {
            val region = segments[placeIndex + 1]
            val x = segments[placeIndex + 2]
            val y = segments[placeIndex + 3]
            val z = segments[placeIndex + 4]
            val hasEventsSuffix = placeIndex + 5 < segments.size && segments[placeIndex + 5] == "events"
            return if (hasEventsSuffix) {
                Routes.placeEvents(region, x, y, z)
            } else {
                Routes.placeDetail(region, x, y, z)
            }
        }

        return null
    }
}
