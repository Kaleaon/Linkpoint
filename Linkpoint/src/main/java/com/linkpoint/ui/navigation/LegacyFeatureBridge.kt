package com.linkpoint.ui.navigation

import android.content.Context
import android.content.Intent
import com.linkpoint.ui.chat.ChatActivity
import com.linkpoint.ui.inventory.InventoryActivity
import com.linkpoint.ui.login.LoginActivity
import com.linkpoint.ui.migration.UiMigrationFlagsProvider
import com.linkpoint.ui.settings.SettingsActivity

/**
 * Bridge launcher: route to Compose-enabled destination (same activity + args)
 * or fallback legacy view based on migration flags.
 */
object LegacyFeatureBridge {
    fun loginIntent(context: Context, args: RouteArgs.Login = RouteArgs.Login()): Intent {
        val useCompose = UiMigrationFlagsProvider.get(context).useComposeLogin()
        return RouteArgs.putLogin(Intent(context, LoginActivity::class.java), args)
            .putExtra(RouteArgs.EXTRA_USE_COMPOSE, useCompose)
    }

    fun chatIntent(context: Context, args: RouteArgs.Chat = RouteArgs.Chat()): Intent {
        val useCompose = UiMigrationFlagsProvider.get(context).useComposeChat()
        return RouteArgs.putChat(Intent(context, ChatActivity::class.java), args)
            .putExtra(RouteArgs.EXTRA_USE_COMPOSE, useCompose)
    }

    fun inventoryIntent(context: Context): Intent {
        val useCompose = UiMigrationFlagsProvider.get(context).useComposeInventory()
        return Intent(context, InventoryActivity::class.java)
            .putExtra(RouteArgs.EXTRA_USE_COMPOSE, useCompose)
    }

    fun settingsIntent(context: Context): Intent {
        val useCompose = UiMigrationFlagsProvider.get(context).useComposeSettings()
        return Intent(context, SettingsActivity::class.java)
            .putExtra(RouteArgs.EXTRA_USE_COMPOSE, useCompose)
    }
}
