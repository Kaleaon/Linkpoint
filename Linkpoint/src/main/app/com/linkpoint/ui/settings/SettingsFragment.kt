package com.linkpoint.ui.settings

import android.annotation.SuppressLint
import android.app.ProgressDialog
import android.content.DialogInterface
import android.content.Intent
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.os.StatFs
import androidx.appcompat.app.AlertDialog
import androidx.lifecycle.lifecycleScope
import androidx.preference.Preference
import androidx.preference.PreferenceFragmentCompat
import com.google.common.base.Objects
import com.linkpoint.GlobalOptions
import com.linkpoint.LumiyaApp
import com.linkpoint.R
import com.linkpoint.ui.common.DetailsActivity
import com.linkpoint.ui.common.FragmentHasTitle
import com.linkpoint.ui.media.NotificationSounds
import com.linkpoint.ui.notify.NotificationChannels
import com.linkpoint.ui.notify.NotificationType
import com.linkpoint.utils.FileUtils
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.io.File

class SettingsFragment : PreferenceFragmentCompat(), FragmentHasTitle {
    private val PREF_RESOURCE_KEY: String = "prefResourceId"
    private var requestedRingtonePreference: RingtonePreference? = null

    companion object {
        fun makeSelection(resourceId: Int): Bundle {
            val bundle = Bundle()
            bundle.putInt("prefResourceId", resourceId)
            return bundle
        }
    }

    override fun onCreatePreferences(savedInstanceState: Bundle?, rootKey: String?) {
        val args = arguments
        if (args != null) {
            addPreferencesFromResource(args.getInt(PREF_RESOURCE_KEY))
        }
        
        val activity = activity
        if (activity is DetailsActivity) {
            activity.onFragmentTitleUpdated()
        }
        
        val soundPref = findPreference<Preference>("soundOnNotify")
        if (soundPref != null) {
            soundPref.isVisible = !NotificationChannels.getInstance().areNotificationsSystemControlled()
        }
    }

    override fun getSubTitle(): String? {
        return null
    }

    override fun getTitle(): String? {
        val screen = preferenceScreen
        return screen?.title?.toString()
    }

    private fun askForRestart() {
        val builder = AlertDialog.Builder(requireContext())
        builder.setMessage(R.string.restart_after_changing_cache_location)
        builder.setCancelable(true)
        builder.setNegativeButton("No") { dialog, _ -> dialog.dismiss() }
        builder.setPositiveButton("Yes") { dialog, _ ->
            dialog.dismiss()
            LumiyaApp.restartApp()
        }
        builder.create().show()
    }

    private fun handleCacheLocationPreference(pref: CacheLocationPreference) {
        val availableCacheDirs = GlobalOptions.getInstance().availableCacheDirs
        val baseCacheDir = GlobalOptions.getInstance().baseCacheDir
        val displayStrings = arrayOfNulls<String>(availableCacheDirs.size)
        var selectedIndex = -1
        
        for (i in availableCacheDirs.indices) {
            val dir = availableCacheDirs[i]
            if (Objects.equal(dir, baseCacheDir)) {
                selectedIndex = i
            }
            val statFs = StatFs(dir.absolutePath)
            val availableBytes = if (Build.VERSION.SDK_INT >= 18) {
                statFs.blockSizeLong * statFs.availableBlocksLong
            } else {
                statFs.blockSize.toLong() * statFs.availableBlocks.toLong()
            }
            val freeGb = availableBytes.toFloat() / 1.07374182E9f
            displayStrings[i] = String.format("%s (%.1f Gb free)", CacheLocationPreference.makeDisplayableCacheLocation(dir.absolutePath), freeGb)
        }
        
        val builder = AlertDialog.Builder(requireContext())
        builder.setTitle(R.string.select_cache_location)
        builder.setSingleChoiceItems(displayStrings, selectedIndex) { dialog, which ->
            val selectedFile = availableCacheDirs[which]
            val editor = pref.sharedPreferences?.edit()
            editor?.putString(pref.key, selectedFile.toString())
            editor?.apply() // Use apply instead of commit
            
            dialog.dismiss()
            updatePreferencesDisplay()
            
            if (!Objects.equal(baseCacheDir, selectedFile) && GlobalOptions.getInstance().isCacheDirUsed) {
                askForRestart()
            }
        }
        builder.create().show()
    }

    private fun handleClearCachePreference() {
        val builder = AlertDialog.Builder(requireContext())
        builder.setMessage(R.string.clear_cache_dialog_message)
        builder.setCancelable(true)
        builder.setNegativeButton("No") { dialog, _ -> dialog.dismiss() }
        builder.setPositiveButton("Yes") { dialog, _ ->
            dialog.dismiss()
            clearCache()
        }
        builder.create().show()
    }

    private fun clearCache() {
        val cacheDirs = GlobalOptions.getInstance().availableCacheDirs
        val progressDialog = ProgressDialog.show(context, null, getString(R.string.clearing_cache), true, true) {
             // Cancel listener
        }

        lifecycleScope.launch(Dispatchers.IO) {
            try {
                for (dir in cacheDirs) {
                    FileUtils.clearFolder(dir)
                }
            } finally {
                withContext(Dispatchers.Main) {
                    progressDialog.dismiss()
                    if (GlobalOptions.getInstance().isCacheDirUsed) {
                        askForRestart()
                    }
                }
            }
        }
    }

    private fun handleRingtonePreference(pref: RingtonePreference) {
        val intent = Intent("android.intent.action.RINGTONE_PICKER")
        intent.putExtra("android.intent.extra.ringtone.TYPE", 2)
        intent.putExtra("android.intent.extra.ringtone.SHOW_DEFAULT", true)
        intent.putExtra("android.intent.extra.ringtone.SHOW_SILENT", true)
        intent.putExtra("android.intent.extra.ringtone.DEFAULT_URI", NotificationSounds.getResourceUri(pref.getDefaultRawResource()))
        
        val currentUriString = pref.sharedPreferences?.getString(pref.key, null)
        val currentUri = if (currentUriString == null) {
             NotificationSounds.getResourceUri(pref.getDefaultRawResource())
        } else if (currentUriString.isEmpty()) {
            null
        } else {
            Uri.parse(currentUriString)
        }
        
        intent.putExtra("android.intent.extra.ringtone.EXISTING_URI", currentUri)
        this.requestedRingtonePreference = pref
        startActivityForResult(intent, 2)
    }

    private fun updatePreferencesDisplay() {
        listView?.adapter?.notifyDataSetChanged()
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        if (requestCode == 2 && resultCode == -1 && data != null && requestedRingtonePreference != null) {
            val uri = data.getParcelableExtra<Uri>("android.intent.extra.ringtone.PICKED_URI")
            val uriString = uri?.toString() ?: ""
            val editor = requestedRingtonePreference?.sharedPreferences?.edit()
            editor?.putString(requestedRingtonePreference?.key, uriString)
            editor?.apply()
            updatePreferencesDisplay()
        } else if (requestCode == 11) {
            updatePreferencesDisplay()
        } else {
            super.onActivityResult(requestCode, resultCode, data)
        }
    }

    override fun onDetach() {
        super.onDetach()
        val activity = activity
        if (activity is DetailsActivity) {
            activity.onFragmentTitleUpdated()
        }
    }

    override fun onPreferenceTreeClick(preference: Preference): Boolean {
        if (preference is PreferenceSubPage) {
            val type = preference.notificationType
            val channel = if (type != null) NotificationChannels.getInstance().getChannelByType(type) else null
            
            if (channel != null && NotificationChannels.getInstance().showSystemNotificationSettings(context, this, channel)) {
                return true
            }
            
            val selection = makeSelection(preference.pageResource)
            // Assuming SettingsSubPageFragment exists and works
            DetailsActivity.showEmbeddedDetails(activity, SettingsSubPageFragment::class.java, selection)
            return true
        } else if (preference is RingtonePreference) {
            handleRingtonePreference(preference)
            return true
        } else if (preference is CacheLocationPreference) {
            handleCacheLocationPreference(preference)
            return true
        } else if (preference is ClearCachePreference) {
            handleClearCachePreference()
            return true
        }
        return super.onPreferenceTreeClick(preference)
    }

    override fun onStart() {
        super.onStart()
        val activity = activity
        if (activity is DetailsActivity) {
            activity.onFragmentTitleUpdated()
        }
    }
}
