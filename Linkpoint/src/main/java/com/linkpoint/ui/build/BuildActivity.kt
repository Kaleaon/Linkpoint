package com.linkpoint.ui.build

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.MenuItem
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.tabs.TabLayout
import com.linkpoint.R

/**
 * Build/Edit mode activity for in-world building and object editing.
 *
 * Receives the selected prim's current transform via Intent extras so the
 * panels can be pre-populated. Changes are logged; wiring through the
 * ObjectUpdate / MultipleObjectUpdate protocol path is the next step.
 */
class BuildActivity : AppCompatActivity() {

    companion object {
        private const val TAG = "BuildActivity"

        const val EXTRA_LOCAL_ID  = "local_id"
        const val EXTRA_POS_X     = "pos_x"
        const val EXTRA_POS_Y     = "pos_y"
        const val EXTRA_POS_Z     = "pos_z"
        const val EXTRA_SCALE_X   = "scale_x"
        const val EXTRA_SCALE_Y   = "scale_y"
        const val EXTRA_SCALE_Z   = "scale_z"
        const val EXTRA_ROT_X     = "rot_x"
        const val EXTRA_ROT_Y     = "rot_y"
        const val EXTRA_ROT_Z     = "rot_z"

        private val TAB_LABELS = listOf("Object", "Features", "Texture", "General", "Content")
        private val TAB_LAYOUTS = listOf(
            R.layout.panel_build_object,
            R.layout.panel_build_features,
            R.layout.panel_build_texture,
            R.layout.panel_build_general,
            R.layout.panel_build_content
        )
    }

    private var localId: Int = -1
    private lateinit var tabLayout: TabLayout
    private lateinit var contentFrame: android.widget.FrameLayout
    private val inflater by lazy { LayoutInflater.from(this) }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_build)

        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.title = "Build"

        localId = intent.getIntExtra(EXTRA_LOCAL_ID, -1)
        Log.d(TAG, "BuildActivity opened for localId=$localId")

        tabLayout    = findViewById(R.id.tabLayout)
        contentFrame = findViewById(R.id.contentFrame)

        TAB_LABELS.forEach { label ->
            tabLayout.addTab(tabLayout.newTab().setText(label))
        }

        tabLayout.addOnTabSelectedListener(object : TabLayout.OnTabSelectedListener {
            override fun onTabSelected(tab: TabLayout.Tab) = loadPanel(tab.position)
            override fun onTabUnselected(tab: TabLayout.Tab) {}
            override fun onTabReselected(tab: TabLayout.Tab) {}
        })

        // Load the first panel immediately.
        loadPanel(0)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            android.R.id.home -> { finish(); true }
            else -> super.onOptionsItemSelected(item)
        }
    }

    // ── Panel loading ─────────────────────────────────────────────────────

    private fun loadPanel(index: Int) {
        contentFrame.removeAllViews()
        val layoutRes = TAB_LAYOUTS.getOrNull(index) ?: return
        val panel = inflater.inflate(layoutRes, contentFrame, false)
        contentFrame.addView(panel)
        when (index) {
            0 -> setupObjectPanel(panel)
            2 -> setupTexturePanel(panel)
        }
    }

    // ── Object tab ────────────────────────────────────────────────────────

    private fun setupObjectPanel(view: View) {
        val posX  = view.findViewById<EditText>(R.id.posX)
        val posY  = view.findViewById<EditText>(R.id.posY)
        val posZ  = view.findViewById<EditText>(R.id.posZ)
        val scaleX = view.findViewById<EditText>(R.id.scaleX)
        val scaleY = view.findViewById<EditText>(R.id.scaleY)
        val scaleZ = view.findViewById<EditText>(R.id.scaleZ)
        val rotX  = view.findViewById<EditText>(R.id.rotX)
        val rotY  = view.findViewById<EditText>(R.id.rotY)
        val rotZ  = view.findViewById<EditText>(R.id.rotZ)

        // Pre-populate from intent extras.
        posX?.setText(intent.getFloatExtra(EXTRA_POS_X, 0f).fmt())
        posY?.setText(intent.getFloatExtra(EXTRA_POS_Y, 0f).fmt())
        posZ?.setText(intent.getFloatExtra(EXTRA_POS_Z, 0f).fmt())
        scaleX?.setText(intent.getFloatExtra(EXTRA_SCALE_X, 1f).fmt())
        scaleY?.setText(intent.getFloatExtra(EXTRA_SCALE_Y, 1f).fmt())
        scaleZ?.setText(intent.getFloatExtra(EXTRA_SCALE_Z, 1f).fmt())
        rotX?.setText(intent.getFloatExtra(EXTRA_ROT_X, 0f).fmt())
        rotY?.setText(intent.getFloatExtra(EXTRA_ROT_Y, 0f).fmt())
        rotZ?.setText(intent.getFloatExtra(EXTRA_ROT_Z, 0f).fmt())

        view.findViewById<Button>(R.id.applyTransform)?.setOnClickListener {
            val px = posX?.text?.toString()?.toFloatOrNull() ?: return@setOnClickListener
            val py = posY?.text?.toString()?.toFloatOrNull() ?: return@setOnClickListener
            val pz = posZ?.text?.toString()?.toFloatOrNull() ?: return@setOnClickListener
            val sx = scaleX?.text?.toString()?.toFloatOrNull()?.coerceAtLeast(0.01f) ?: return@setOnClickListener
            val sy = scaleY?.text?.toString()?.toFloatOrNull()?.coerceAtLeast(0.01f) ?: return@setOnClickListener
            val sz = scaleZ?.text?.toString()?.toFloatOrNull()?.coerceAtLeast(0.01f) ?: return@setOnClickListener
            val rx = rotX?.text?.toString()?.toFloatOrNull() ?: 0f
            val ry = rotY?.text?.toString()?.toFloatOrNull() ?: 0f
            val rz = rotZ?.text?.toString()?.toFloatOrNull() ?: 0f
            Log.i(TAG, "applyTransform localId=$localId " +
                "pos=($px,$py,$pz) scale=($sx,$sy,$sz) rot=($rx,$ry,$rz)")
            Toast.makeText(this, "Transform queued (protocol wiring pending)", Toast.LENGTH_SHORT).show()
        }

        view.findViewById<Button>(R.id.linkButton)?.setOnClickListener {
            Log.i(TAG, "link requested for localId=$localId")
            Toast.makeText(this, "Link (protocol wiring pending)", Toast.LENGTH_SHORT).show()
        }
        view.findViewById<Button>(R.id.unlinkButton)?.setOnClickListener {
            Log.i(TAG, "unlink requested for localId=$localId")
            Toast.makeText(this, "Unlink (protocol wiring pending)", Toast.LENGTH_SHORT).show()
        }
    }

    // ── Texture tab ───────────────────────────────────────────────────────

    private fun setupTexturePanel(view: View) {
        view.findViewById<Button>(R.id.colorButton)?.setOnClickListener {
            Toast.makeText(this, "Colour picker (coming soon)", Toast.LENGTH_SHORT).show()
        }
    }

    // ── Helpers ───────────────────────────────────────────────────────────

    private fun Float.fmt(): String = String.format("%.4f", this)
}
