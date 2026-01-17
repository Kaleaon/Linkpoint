package com.linkpoint.ui.notecard

import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.view.ViewGroup.LayoutParams
import android.widget.EditText
import android.widget.HorizontalScrollView
import android.widget.LinearLayout
import android.widget.ScrollView
import android.widget.Toast
import androidx.activity.OnBackPressedCallback
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.linkpoint.LinkpointApp
import com.linkpoint.inventory.notecard.NotecardData
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.util.UUID

/**
 * Notecard Editor Activity - View and edit Second Life notecards.
 * 
 * Features:
 * - View notecard content
 * - Edit notecard text (when permissions allow)
 * - View embedded items list
 * - Copy notecard text
 * - Save changes back to inventory
 * 
 * Based on Lumiya/Firestorm notecard viewer.
 */
class NotecardEditorActivity : AppCompatActivity() {
    
    companion object {
        const val EXTRA_ASSET_ID = "asset_id"
        const val EXTRA_ITEM_ID = "item_id"
        const val EXTRA_NOTECARD_NAME = "notecard_name"
        const val EXTRA_READ_ONLY = "read_only"
    }
    
    private lateinit var contentEditor: EditText
    private lateinit var embeddedItemsLayout: LinearLayout
    
    private var assetId: UUID? = null
    private var itemId: UUID? = null
    private var notecardName: String = "Notecard"
    private var isReadOnly: Boolean = true
    private var notecardData: NotecardData? = null
    private var hasUnsavedChanges: Boolean = false
    
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        
        // Create layout programmatically
        val rootLayout = LinearLayout(this).apply {
            orientation = LinearLayout.VERTICAL
            layoutParams = LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT)
            setBackgroundColor(0xFF2B2B2B.toInt())
        }
        
        // Embedded items section (collapsible)
        embeddedItemsLayout = LinearLayout(this).apply {
            orientation = LinearLayout.VERTICAL
            layoutParams = LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT)
            setPadding(16, 8, 16, 8)
            setBackgroundColor(0xFF3C3C3C.toInt())
            visibility = android.view.View.GONE
        }
        rootLayout.addView(embeddedItemsLayout)
        
        // Content editor in scrollable area
        val scrollView = ScrollView(this).apply {
            layoutParams = LinearLayout.LayoutParams(LayoutParams.MATCH_PARENT, 0, 1f)
        }
        
        val horizontalScroll = HorizontalScrollView(this).apply {
            layoutParams = LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT)
        }
        
        contentEditor = EditText(this).apply {
            layoutParams = LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT)
            setBackgroundColor(0xFF2B2B2B.toInt())
            setTextColor(0xFFD4D4D4.toInt())
            setHintTextColor(0xFF808080.toInt())
            textSize = 14f
            setPadding(32, 32, 32, 32)
            hint = "Loading notecard..."
            minLines = 20
            gravity = android.view.Gravity.TOP or android.view.Gravity.START
        }
        
        horizontalScroll.addView(contentEditor)
        scrollView.addView(horizontalScroll)
        rootLayout.addView(scrollView)
        
        setContentView(rootLayout)
        
        // Get intent extras
        intent.getStringExtra(EXTRA_ASSET_ID)?.let { assetId = UUID.fromString(it) }
        intent.getStringExtra(EXTRA_ITEM_ID)?.let { itemId = UUID.fromString(it) }
        notecardName = intent.getStringExtra(EXTRA_NOTECARD_NAME) ?: "Notecard"
        isReadOnly = intent.getBooleanExtra(EXTRA_READ_ONLY, true)
        
        setupToolbar()
        setupEditor()
        loadNotecard()
        
        // Track changes
        contentEditor.addTextChangedListener(object : android.text.TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}
            override fun afterTextChanged(s: android.text.Editable?) {
                if (!isReadOnly) {
                    hasUnsavedChanges = true
                    invalidateOptionsMenu()
                }
            }
        })
        
        // Handle back button properly
        onBackPressedDispatcher.addCallback(this, object : OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {
                handleBack()
            }
        })
    }
    
    private fun setupToolbar() {
        supportActionBar?.apply {
            title = notecardName
            setDisplayHomeAsUpEnabled(true)
            subtitle = if (isReadOnly) "Read Only" else "Editing"
        }
    }
    
    private fun setupEditor() {
        contentEditor.apply {
            isFocusable = !isReadOnly
            isFocusableInTouchMode = !isReadOnly
            isClickable = !isReadOnly
            isCursorVisible = !isReadOnly
        }
    }
    
    private fun loadNotecard() {
        val app = application as LinkpointApp
        val assetUuid = assetId ?: return
        
        lifecycleScope.launch {
            try {
                contentEditor.setText("Loading notecard...")
                
                // Fetch notecard
                val notecard = withContext(Dispatchers.IO) {
                    app.notecardManager.fetchNotecard(assetUuid)
                }
                
                if (notecard != null) {
                    notecardData = notecard
                    contentEditor.setText(notecard.text)
                    
                    // Show embedded items if any
                    if (notecard.embeddedItems.isNotEmpty()) {
                        showEmbeddedItems(notecard.embeddedItems)
                    }
                    
                    // Move cursor to start
                    contentEditor.setSelection(0)
                } else {
                    contentEditor.setText("Failed to load notecard.\n\nAsset ID: $assetUuid")
                    Toast.makeText(this@NotecardEditorActivity, "Failed to load notecard", Toast.LENGTH_SHORT).show()
                }
            } catch (e: Exception) {
                contentEditor.setText("Error loading notecard: ${e.message}")
                Toast.makeText(this@NotecardEditorActivity, "Error: ${e.message}", Toast.LENGTH_SHORT).show()
            }
        }
    }
    
    private fun showEmbeddedItems(items: List<com.linkpoint.inventory.notecard.EmbeddedItem>) {
        embeddedItemsLayout.removeAllViews()
        
        // Header
        val header = android.widget.TextView(this).apply {
            text = "Embedded Items (${items.size})"
            setTextColor(0xFFFFFFFF.toInt())
            textSize = 12f
            setPadding(0, 4, 0, 8)
        }
        embeddedItemsLayout.addView(header)
        
        // Item list
        for (item in items) {
            val itemView = android.widget.TextView(this).apply {
                text = "• ${item.name} (${getItemTypeName(item.type)})"
                setTextColor(0xFFB0B0B0.toInt())
                textSize = 11f
                setPadding(16, 2, 0, 2)
            }
            embeddedItemsLayout.addView(itemView)
        }
        
        embeddedItemsLayout.visibility = android.view.View.VISIBLE
    }
    
    private fun getItemTypeName(type: Int): String {
        return when (type) {
            0 -> "Texture"
            1 -> "Sound"
            2 -> "Calling Card"
            3 -> "Landmark"
            5 -> "Clothing"
            6 -> "Object"
            7 -> "Notecard"
            10 -> "Script"
            13 -> "Body Part"
            20 -> "Animation"
            21 -> "Gesture"
            49 -> "Mesh"
            56 -> "Settings"
            57 -> "Material"
            else -> "Item"
        }
    }
    
    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menu.add(0, 1, 0, "Copy").setShowAsAction(MenuItem.SHOW_AS_ACTION_IF_ROOM)
        
        if (!isReadOnly) {
            val saveItem = menu.add(0, 2, 0, "Save")
            saveItem.setShowAsAction(MenuItem.SHOW_AS_ACTION_IF_ROOM)
            saveItem.isEnabled = hasUnsavedChanges
        }
        
        menu.add(0, 3, 0, "Select All").setShowAsAction(MenuItem.SHOW_AS_ACTION_NEVER)
        
        return true
    }
    
    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            android.R.id.home -> {
                handleBack()
                true
            }
            1 -> { // Copy
                copyToClipboard()
                true
            }
            2 -> { // Save
                saveNotecard()
                true
            }
            3 -> { // Select All
                contentEditor.selectAll()
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }
    
    private fun handleBack() {
        if (hasUnsavedChanges && !isReadOnly) {
            AlertDialog.Builder(this)
                .setTitle("Unsaved Changes")
                .setMessage("You have unsaved changes. Do you want to save before closing?")
                .setPositiveButton("Save") { _, _ ->
                    saveNotecard()
                    finish()
                }
                .setNegativeButton("Discard") { _, _ ->
                    finish()
                }
                .setNeutralButton("Cancel", null)
                .show()
        } else {
            finish()
        }
    }
    
    private fun copyToClipboard() {
        val clipboard = getSystemService(CLIPBOARD_SERVICE) as android.content.ClipboardManager
        val clip = android.content.ClipData.newPlainText("Notecard", contentEditor.text)
        clipboard.setPrimaryClip(clip)
        Toast.makeText(this, "Notecard copied to clipboard", Toast.LENGTH_SHORT).show()
    }
    
    private fun saveNotecard() {
        val app = application as LinkpointApp
        val itmId = itemId ?: run {
            Toast.makeText(this, "Cannot save: No item ID", Toast.LENGTH_SHORT).show()
            return
        }
        
        lifecycleScope.launch {
            try {
                val newText = contentEditor.text.toString()
                
                val success = withContext(Dispatchers.IO) {
                    app.notecardManager.saveNotecard(itmId, newText)
                }
                
                if (success) {
                    hasUnsavedChanges = false
                    invalidateOptionsMenu()
                    Toast.makeText(this@NotecardEditorActivity, "Notecard saved", Toast.LENGTH_SHORT).show()
                } else {
                    Toast.makeText(this@NotecardEditorActivity, "Failed to save notecard", Toast.LENGTH_SHORT).show()
                }
            } catch (e: Exception) {
                Toast.makeText(this@NotecardEditorActivity, "Error saving: ${e.message}", Toast.LENGTH_SHORT).show()
            }
        }
    }
}
