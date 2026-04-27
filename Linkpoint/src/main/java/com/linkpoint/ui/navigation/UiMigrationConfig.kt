package com.linkpoint.ui.navigation

/**
 * UI migration policy after bridge-flag collapse.
 *
 * Compose is the default and only forward path for new UI feature work.
 */
object UiMigrationConfig {
    const val COMPOSE_PATH_DEFAULT: Boolean = true
    const val LEGACY_ACTIVITY_BRIDGE_ALLOWED: Boolean = true // temporary compatibility only
    const val LEGACY_REMOVAL_MILESTONE: String = "2026.09"
}
