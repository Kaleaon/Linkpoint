package com.linkpoint.ui.notecard

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
 * Notecard Editor Activity - View and edit Second Life notecards.
 * 
 * Uses Jetpack Compose for modern UI with the compose-code-editor library.
 * 
 * Features:
 * - View notecard content
 * - Edit notecard text (when permissions allow)
 * - View embedded items list (collapsible)
 * - Copy notecard text
 * - Save changes back to inventory
 * - Unsaved changes confirmation dialog
 * 
 * Based on reference viewer/Firestorm notecard viewer design.
 */
class NotecardEditorActivity : ComponentActivity() {
    
    companion object {
        const val EXTRA_ASSET_ID = "asset_id"
        const val EXTRA_ITEM_ID = "item_id"
        const val EXTRA_NOTECARD_NAME = "notecard_name"
        const val EXTRA_READ_ONLY = "read_only"
    }
    
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        
        val assetId = intent.getStringExtra(EXTRA_ASSET_ID)?.let { UUID.fromString(it) }
        val itemId = intent.getStringExtra(EXTRA_ITEM_ID)?.let { UUID.fromString(it) }
        val notecardName = intent.getStringExtra(EXTRA_NOTECARD_NAME) ?: "Notecard"
        val isReadOnly = intent.getBooleanExtra(EXTRA_READ_ONLY, true)
        
        val app = application as LinkpointApp
        
        setContent {
            LinkpointTheme(darkTheme = true) {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    NotecardEditorScreen(
                        notecardName = notecardName,
                        assetId = assetId,
                        itemId = itemId,
                        isReadOnly = isReadOnly,
                        onLoadNotecard = { assetUuid ->
                            app.notecardManager.fetchNotecard(assetUuid)
                        },
                        onSaveNotecard = { itmId, newText ->
                            app.notecardManager.saveNotecard(itmId, newText)
                        },
                        onNavigateBack = { finish() }
                    )
                }
            }
        }
    }
}
