package com.linkpoint.ui.settings

import android.content.Context
import android.content.SharedPreferences

/**
 * Reads and writes [SettingsState] to the app's default SharedPreferences file.
 *
 * The prefs file name `com.linkpoint_preferences` matches the file produced by
 * Android's [androidx.preference.PreferenceManager.getDefaultSharedPreferences],
 * which is the store used by [SettingsActivity]. Both surfaces therefore share
 * a single backing file so a change in one is immediately visible in the other.
 */
class SettingsRepository private constructor(context: Context) {

    companion object {
        private const val PREFS_NAME = "com.linkpoint_preferences"

        // Keys shared with the XML preference definitions in SettingsActivity.
        private const val KEY_NOTIFICATIONS_ENABLED = "notifications_enabled"
        private const val KEY_SOUND_ENABLED = "sound_enabled"
        private const val KEY_SOUND_VOLUME = "sound_volume"
        private const val KEY_SHOW_ONLINE_STATUS = "show_online_status"
        private const val KEY_AUTO_ACCEPT_FRIENDS = "auto_accept_friends"
        private const val KEY_RENDER_DISTANCE = "render_distance"
        private const val KEY_BRIGHTNESS = "brightness"
        private const val KEY_RLV_ENABLED = "rlv_enabled"

        @Volatile
        private var instance: SettingsRepository? = null

        fun getInstance(context: Context): SettingsRepository {
            return instance ?: synchronized(this) {
                instance ?: SettingsRepository(context.applicationContext).also { instance = it }
            }
        }
    }

    private val prefs: SharedPreferences =
        context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)

    fun load(): SettingsState = SettingsState(
        notificationsEnabled = prefs.getBoolean(KEY_NOTIFICATIONS_ENABLED, true),
        soundEnabled = prefs.getBoolean(KEY_SOUND_ENABLED, true),
        soundVolume = prefs.getFloat(KEY_SOUND_VOLUME, 0.8f),
        showOnlineStatus = prefs.getBoolean(KEY_SHOW_ONLINE_STATUS, true),
        autoAcceptFriends = prefs.getBoolean(KEY_AUTO_ACCEPT_FRIENDS, false),
        renderDistance = prefs.getFloat(KEY_RENDER_DISTANCE, 64f),
        brightness = prefs.getFloat(KEY_BRIGHTNESS, 1.0f),
        rlvEnabled = prefs.getBoolean(KEY_RLV_ENABLED, false),
        showHud = prefs.getBoolean(SettingsKeys.SHOW_HUD, true),
        showJoysticks = prefs.getBoolean(SettingsKeys.SHOW_JOYSTICKS, true),
        showActionButtons = prefs.getBoolean(SettingsKeys.SHOW_ACTION_BUTTONS, true),
        showMovementButtons = prefs.getBoolean(SettingsKeys.SHOW_MOVEMENT_BUTTONS, true),
    )

    fun save(settings: SettingsState) {
        prefs.edit().apply {
            putBoolean(KEY_NOTIFICATIONS_ENABLED, settings.notificationsEnabled)
            putBoolean(KEY_SOUND_ENABLED, settings.soundEnabled)
            putFloat(KEY_SOUND_VOLUME, settings.soundVolume)
            putBoolean(KEY_SHOW_ONLINE_STATUS, settings.showOnlineStatus)
            putBoolean(KEY_AUTO_ACCEPT_FRIENDS, settings.autoAcceptFriends)
            putFloat(KEY_RENDER_DISTANCE, settings.renderDistance)
            putFloat(KEY_BRIGHTNESS, settings.brightness)
            putBoolean(KEY_RLV_ENABLED, settings.rlvEnabled)
            putBoolean(SettingsKeys.SHOW_HUD, settings.showHud)
            putBoolean(SettingsKeys.SHOW_JOYSTICKS, settings.showJoysticks)
            putBoolean(SettingsKeys.SHOW_ACTION_BUTTONS, settings.showActionButtons)
            putBoolean(SettingsKeys.SHOW_MOVEMENT_BUTTONS, settings.showMovementButtons)
        }.apply()
    }
}
