package com.linkpoint.ui.scripts

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.ui.Modifier
import com.linkpoint.LinkpointApp
import com.linkpoint.ui.theme.LinkpointTheme
import java.util.UUID

/**
 * Script Editor Activity - View and edit LSL scripts.
 * 
 * Uses Jetpack Compose with custom LSL syntax highlighting.
 * 
 * Features:
 * - LSL syntax highlighting (350+ functions, 60+ events, 400+ constants)
 * - Line numbers
 * - Read-only mode for viewing scripts in objects
 * - Copy script text
 * - Reset script (for object scripts)
 * - Toggle running state (for object scripts)
 * - Dark theme optimized for code editing
 * 
 * Based on Firestorm/Lumiya script editor design.
 */
class ScriptEditorActivity : ComponentActivity() {
    
    companion object {
        const val EXTRA_ASSET_ID = "asset_id"
        const val EXTRA_ITEM_ID = "item_id"
        const val EXTRA_OBJECT_ID = "object_id"
        const val EXTRA_SCRIPT_NAME = "script_name"
        const val EXTRA_READ_ONLY = "read_only"
    }
    
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        
        val assetId = intent.getStringExtra(EXTRA_ASSET_ID)?.let { UUID.fromString(it) }
        val itemId = intent.getStringExtra(EXTRA_ITEM_ID)?.let { UUID.fromString(it) }
        val objectId = intent.getStringExtra(EXTRA_OBJECT_ID)?.let { UUID.fromString(it) }
        val scriptName = intent.getStringExtra(EXTRA_SCRIPT_NAME) ?: "Script"
        val isReadOnly = intent.getBooleanExtra(EXTRA_READ_ONLY, true)
        
        val app = application as LinkpointApp
        
        setContent {
            LinkpointTheme(darkTheme = true) {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    ScriptEditorScreen(
                        scriptName = scriptName,
                        assetId = assetId,
                        itemId = itemId,
                        objectId = objectId,
                        isReadOnly = isReadOnly,
                        onLoadScript = { assetUuid ->
                            app.scriptManager.getScriptSource(assetUuid)
                        },
                        onResetScript = { objId, itmId ->
                            app.scriptManager.resetScript(objId, itmId)
                        },
                        onToggleRunning = { objId, itmId ->
                            val info = app.scriptManager.getScriptRunning(objId, itmId)
                            if (info != null) {
                                val newState = !info.isRunning
                                val success = app.scriptManager.setScriptRunning(objId, itmId, newState)
                                if (success) Pair(true, newState) else null
                            } else {
                                null
                            }
                        },
                        onNavigateBack = { finish() }
                    )
                }
            }
        }
    }
}
