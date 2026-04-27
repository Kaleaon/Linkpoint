package com.linkpoint.ui.migration

import android.content.Context
import androidx.preference.PreferenceManager

/**
 * Feature flags for staged Compose migration.
 *
 * Provider indirection makes this remote-config friendly: a remote layer can
 * replace [UiMigrationFlagsProvider.provider] at runtime.
 */
interface UiMigrationFlags {
    fun useComposeLogin(): Boolean
    fun useComposeChat(): Boolean
    fun useComposeInventory(): Boolean
    fun useComposeSettings(): Boolean
}

class SharedPrefsUiMigrationFlags(
    context: Context,
    private val remoteOverrides: () -> Map<String, Boolean> = { emptyMap() }
) : UiMigrationFlags {

    private val prefs = PreferenceManager.getDefaultSharedPreferences(context.applicationContext)

    override fun useComposeLogin(): Boolean = flag(KEY_LOGIN)
    override fun useComposeChat(): Boolean = flag(KEY_CHAT)
    override fun useComposeInventory(): Boolean = flag(KEY_INVENTORY)
    override fun useComposeSettings(): Boolean = flag(KEY_SETTINGS)

    private fun flag(key: String): Boolean {
        return remoteOverrides().getOrElse(key) { prefs.getBoolean(key, false) }
    }

    companion object {
        const val KEY_LOGIN = "ui_migration_compose_login"
        const val KEY_CHAT = "ui_migration_compose_chat"
        const val KEY_INVENTORY = "ui_migration_compose_inventory"
        const val KEY_SETTINGS = "ui_migration_compose_settings"
    }
}

object UiMigrationFlagsProvider {
    @Volatile
    var providerFactory: (Context) -> UiMigrationFlags = { ctx -> SharedPrefsUiMigrationFlags(ctx) }

    fun get(context: Context): UiMigrationFlags = providerFactory(context)
}
