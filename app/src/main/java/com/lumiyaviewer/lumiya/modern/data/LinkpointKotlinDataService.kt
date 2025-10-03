package com.lumiyaviewer.lumiya.modern.data

import com.lumiyaviewer.lumiya.slproto.llsd.kotlin.KotlinLLSDValue
import com.lumiyaviewer.lumiya.slproto.llsd.kotlin.kotlinLlsdArray
import com.lumiyaviewer.lumiya.slproto.llsd.kotlin.kotlinLlsdMap
import java.util.Date
import java.util.UUID

/**
 * Provides additional structured data built with the Kotlin LLSD DSL.
 * This simulates richer agent profile, groups, and region metrics.
 */
class LinkpointKotlinDataService {

    fun fetchExtendedAgentProfile(): KotlinLLSDValue.Map {
        return kotlinLlsdMap {
            "agent" to kotlinLlsdMap {
                "id" to UUID.randomUUID()
                "name" to "Linkpoint Tester"
                "display_name" to "Tester Resident"
                "account_age_days" to 365
                "born_on" to Date()
                "status" to kotlinLlsdMap {
                    "online" to true
                    "away" to false
                    "busy" to false
                }
                "position" to kotlinLlsdArray { +128.0; +128.0; +23.0 }
                "rotation" to kotlinLlsdArray { +0.0; +0.0; +0.0; +1.0 }
            }

            "groups" to kotlinLlsdArray {
                repeat(3) { idx ->
                    +kotlinLlsdMap {
                        "group_id" to UUID.randomUUID()
                        "name" to "Group $idx"
                        "role" to if (idx == 0) "Owner" else "Member"
                        "is_active" to (idx == 0)
                    }
                }
            }

            "profile" to kotlinLlsdMap {
                "about" to "Modernized Android viewer powered by Kotlin LLSD"
                "homepage" to "https://example.com"
                "picks" to kotlinLlsdArray {
                    repeat(2) { pickIdx ->
                        +kotlinLlsdMap {
                            "pick_id" to UUID.randomUUID()
                            "title" to "Favorite Place #$pickIdx"
                            "desc" to "A lovely spot in-world."
                        }
                    }
                }
            }

            "region" to kotlinLlsdMap {
                "name" to "Sandbox Modern"
                "handle" to 1099511627776L
                "server" to "192.168.1.100:9000"
                "agents_in_region" to 42
                "stats" to kotlinLlsdMap {
                    "fps" to 44.5
                    "physics_fps" to 44.9
                    "time_dilation" to 0.98
                }
            }
        }
    }
}

