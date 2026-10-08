package com.linkpoint.ui.settings

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.widget.Toast
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.lifecycle.lifecycleScope
import androidx.preference.PreferenceFragmentCompat
import com.linkpoint.R
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.io.File

class LogExportCoordinator(
    private val fragment: PreferenceFragmentCompat,
    private val context: Context,
) {
    private var pendingText: String? = null
    private val createDocumentLauncher: ActivityResultLauncher<Intent> =
        fragment.registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
            if (result.resultCode == Activity.RESULT_OK) {
                result.data?.data?.let { uri -> writeTextToUri(uri, pendingText) }
            }
            pendingText = null
        }

    fun exportText(defaultFilename: String, textProvider: suspend () -> String) {
        fragment.viewLifecycleOwner.lifecycleScope.launch {
            pendingText = withContext(Dispatchers.IO) { textProvider() }
            val intent = Intent(Intent.ACTION_CREATE_DOCUMENT).apply {
                addCategory(Intent.CATEGORY_OPENABLE)
                type = "text/plain"
                putExtra(Intent.EXTRA_TITLE, defaultFilename)
            }
            runCatching { createDocumentLauncher.launch(intent) }
                .onFailure {
                    pendingText = null
                    Toast.makeText(context, context.getString(R.string.settings_file_picker_failed, it.message), Toast.LENGTH_LONG).show()
                }
        }
    }

    fun shareFile(file: File, chooserTitle: String, subject: String) {
        SettingsShareHelper.shareFile(fragment, context, file, chooserTitle, subject)
    }

    private fun writeTextToUri(uri: android.net.Uri, content: String?) {
        if (content == null) {
            Toast.makeText(context, R.string.settings_no_log_content, Toast.LENGTH_SHORT).show(); return
        }
        fragment.viewLifecycleOwner.lifecycleScope.launch {
            val success = withContext(Dispatchers.IO) {
                runCatching {
                    context.contentResolver.openOutputStream(uri)?.use { os -> os.bufferedWriter().use { it.write(content) } }
                }.isSuccess
            }
            Toast.makeText(context, if (success) R.string.settings_log_export_success else R.string.settings_log_export_failed, Toast.LENGTH_SHORT).show()
        }
    }
}

private object SettingsShareHelper {
    fun shareFile(fragment: PreferenceFragmentCompat, context: Context, file: File, chooserTitle: String, subject: String) {
        val authority = context.packageName + ".fileprovider"
        val uri = runCatching { androidx.core.content.FileProvider.getUriForFile(context, authority, file) }.getOrElse {
            Toast.makeText(context, context.getString(R.string.settings_cannot_share_log, it.message), Toast.LENGTH_LONG).show(); return
        }
        val send = Intent(Intent.ACTION_SEND).apply {
            type = "text/plain"
            putExtra(Intent.EXTRA_STREAM, uri)
            putExtra(Intent.EXTRA_SUBJECT, subject)
            addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
        }
        runCatching { fragment.startActivity(Intent.createChooser(send, chooserTitle)) }
            .onFailure { Toast.makeText(context, R.string.settings_no_share_target, Toast.LENGTH_SHORT).show() }
    }
}
