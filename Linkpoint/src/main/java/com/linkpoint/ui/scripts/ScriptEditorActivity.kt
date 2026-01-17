package com.linkpoint.ui.scripts

import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.linkpoint.LinkpointApp
import com.linkpoint.scripts.lsl.LSLDarkColorScheme
import com.linkpoint.scripts.lsl.LSLSyntaxHighlighter
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.util.UUID
import android.widget.EditText
import android.widget.LinearLayout
import android.view.ViewGroup.LayoutParams
import android.graphics.Typeface
import android.text.Spannable
import android.text.SpannableStringBuilder
import android.text.style.ForegroundColorSpan
import android.widget.ScrollView
import android.widget.HorizontalScrollView

/**
 * Script Editor Activity - View and edit LSL scripts.
 * 
 * Features:
 * - LSL syntax highlighting
 * - Line numbers
 * - Read-only mode for viewing scripts in objects
 * - Copy script text
 * - Dark/light theme support
 */
class ScriptEditorActivity : AppCompatActivity() {
    
    companion object {
        const val EXTRA_ASSET_ID = "asset_id"
        const val EXTRA_ITEM_ID = "item_id"
        const val EXTRA_OBJECT_ID = "object_id"
        const val EXTRA_SCRIPT_NAME = "script_name"
        const val EXTRA_READ_ONLY = "read_only"
    }
    
    private lateinit var codeEditor: EditText
    private var assetId: UUID? = null
    private var itemId: UUID? = null
    private var objectId: UUID? = null
    private var scriptName: String = "Script"
    private var isReadOnly: Boolean = true
    private var originalSource: String = ""
    
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        
        // Create layout programmatically
        val scrollView = ScrollView(this).apply {
            layoutParams = LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT)
            setBackgroundColor(0xFF1E1E1E.toInt())
        }
        
        val horizontalScroll = HorizontalScrollView(this).apply {
            layoutParams = LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT)
        }
        
        codeEditor = EditText(this).apply {
            layoutParams = LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT)
            setBackgroundColor(0xFF1E1E1E.toInt())
            setTextColor(0xFFA9B7C6.toInt())
            textSize = 14f
            typeface = Typeface.MONOSPACE
            setPadding(32, 32, 32, 32)
            setHorizontallyScrolling(true)
            isSingleLine = false
            minLines = 50
        }
        
        horizontalScroll.addView(codeEditor)
        scrollView.addView(horizontalScroll)
        setContentView(scrollView)
        
        // Get intent extras
        intent.getStringExtra(EXTRA_ASSET_ID)?.let { assetId = UUID.fromString(it) }
        intent.getStringExtra(EXTRA_ITEM_ID)?.let { itemId = UUID.fromString(it) }
        intent.getStringExtra(EXTRA_OBJECT_ID)?.let { objectId = UUID.fromString(it) }
        scriptName = intent.getStringExtra(EXTRA_SCRIPT_NAME) ?: "Script"
        isReadOnly = intent.getBooleanExtra(EXTRA_READ_ONLY, true)
        
        setupToolbar()
        setupCodeEditor()
        loadScript()
    }
    
    private fun setupToolbar() {
        supportActionBar?.apply {
            title = scriptName
            setDisplayHomeAsUpEnabled(true)
            subtitle = if (isReadOnly) "Read Only" else "Editing"
        }
    }
    
    private fun setupCodeEditor() {
        codeEditor.apply {
            // Set read-only mode
            isFocusable = !isReadOnly
            isFocusableInTouchMode = !isReadOnly
            isClickable = !isReadOnly
            isCursorVisible = !isReadOnly
        }
    }
    
    private fun loadScript() {
        val app = application as LinkpointApp
        val assetUuid = assetId ?: return
        
        lifecycleScope.launch {
            try {
                // Show loading state
                codeEditor.setText("// Loading script...")
                
                // Fetch script source
                val source = withContext(Dispatchers.IO) {
                    app.scriptManager.getScriptSource(assetUuid)
                }
                
                if (source != null) {
                    originalSource = source
                    highlightAndSetText(source)
                } else {
                    codeEditor.setText("// Failed to load script\n// Asset ID: $assetUuid")
                    Toast.makeText(this@ScriptEditorActivity, "Failed to load script", Toast.LENGTH_SHORT).show()
                }
            } catch (e: Exception) {
                codeEditor.setText("// Error loading script: ${e.message}")
                Toast.makeText(this@ScriptEditorActivity, "Error: ${e.message}", Toast.LENGTH_SHORT).show()
            }
        }
    }
    
    private fun highlightAndSetText(source: String) {
        val spannable = SpannableStringBuilder(source)
        
        // Apply syntax highlighting using LSLSyntax
        val highlighter = LSLSyntaxHighlighter()
        val colorScheme = LSLDarkColorScheme()
        val highlights = highlighter.highlight(source, colorScheme)
        
        for (region in highlights) {
            spannable.setSpan(
                ForegroundColorSpan(region.color),
                region.startIndex,
                region.endIndex,
                Spannable.SPAN_EXCLUSIVE_EXCLUSIVE
            )
        }
        
        codeEditor.setText(spannable)
    }
    
    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menu.add(0, 1, 0, "Copy").setShowAsAction(MenuItem.SHOW_AS_ACTION_IF_ROOM)
        menu.add(0, 2, 0, "Reset Script").setShowAsAction(MenuItem.SHOW_AS_ACTION_NEVER)
        menu.add(0, 3, 0, "Toggle Running").setShowAsAction(MenuItem.SHOW_AS_ACTION_NEVER)
        return true
    }
    
    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            android.R.id.home -> {
                finish()
                true
            }
            1 -> { // Copy
                copyToClipboard()
                true
            }
            2 -> { // Reset Script
                resetScript()
                true
            }
            3 -> { // Toggle Running
                toggleScriptRunning()
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }
    
    private fun copyToClipboard() {
        val clipboard = getSystemService(CLIPBOARD_SERVICE) as android.content.ClipboardManager
        val clip = android.content.ClipData.newPlainText("LSL Script", codeEditor.text)
        clipboard.setPrimaryClip(clip)
        Toast.makeText(this, "Script copied to clipboard", Toast.LENGTH_SHORT).show()
    }
    
    private fun resetScript() {
        val app = application as LinkpointApp
        val objId = objectId ?: return
        val itmId = itemId ?: return
        
        lifecycleScope.launch {
            try {
                val success = withContext(Dispatchers.IO) {
                    app.scriptManager.resetScript(objId, itmId)
                }
                if (success) {
                    Toast.makeText(this@ScriptEditorActivity, "Script reset", Toast.LENGTH_SHORT).show()
                } else {
                    Toast.makeText(this@ScriptEditorActivity, "Failed to reset script", Toast.LENGTH_SHORT).show()
                }
            } catch (e: Exception) {
                Toast.makeText(this@ScriptEditorActivity, "Error: ${e.message}", Toast.LENGTH_SHORT).show()
            }
        }
    }
    
    private fun toggleScriptRunning() {
        val app = application as LinkpointApp
        val objId = objectId ?: return
        val itmId = itemId ?: return
        
        lifecycleScope.launch {
            try {
                // Get current state
                val info = withContext(Dispatchers.IO) {
                    app.scriptManager.getScriptRunning(objId, itmId)
                }
                
                if (info != null) {
                    // Toggle state
                    val newState = !info.isRunning
                    val success = withContext(Dispatchers.IO) {
                        app.scriptManager.setScriptRunning(objId, itmId, newState)
                    }
                    
                    if (success) {
                        val state = if (newState) "running" else "stopped"
                        Toast.makeText(this@ScriptEditorActivity, "Script $state", Toast.LENGTH_SHORT).show()
                    }
                }
            } catch (e: Exception) {
                Toast.makeText(this@ScriptEditorActivity, "Error: ${e.message}", Toast.LENGTH_SHORT).show()
            }
        }
    }
}
