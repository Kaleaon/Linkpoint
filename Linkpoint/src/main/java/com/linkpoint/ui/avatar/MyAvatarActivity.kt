package com.linkpoint.ui.avatar

import android.os.Bundle
import android.view.MenuItem
import android.view.SurfaceView
import android.widget.Button
import android.widget.FrameLayout
import android.widget.SeekBar
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.tabs.TabLayout
import com.linkpoint.LinkpointApp
import com.linkpoint.R

/**
 * My Avatar Activity - Avatar customization
 * Based on the reference viewer's MyAvatarActivity
 */
class MyAvatarActivity : AppCompatActivity() {
    
    companion object {
        private const val TAG = "MyAvatarActivity"
    }
    
    private lateinit var previewContainer: FrameLayout
    private lateinit var previewSurface: SurfaceView
    private lateinit var tabLayout: TabLayout
    private lateinit var optionsContainer: FrameLayout
    
    private val app by lazy { LinkpointApp.getInstance() }
    
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_my_avatar)
        
        supportActionBar?.apply {
            setDisplayHomeAsUpEnabled(true)
            title = "My Avatar"
        }
        
        initViews()
        setupTabs()
        loadAppearance()
    }
    
    private fun initViews() {
        previewContainer = findViewById(R.id.previewContainer)
        tabLayout = findViewById(R.id.tabLayout)
        optionsContainer = findViewById(R.id.optionsContainer)
        
        // Create preview surface for 3D avatar view
        previewSurface = SurfaceView(this)
        previewContainer.addView(previewSurface)
    }
    
    private fun setupTabs() {
        tabLayout.addTab(tabLayout.newTab().setText("Shape"))
        tabLayout.addTab(tabLayout.newTab().setText("Skin"))
        tabLayout.addTab(tabLayout.newTab().setText("Hair"))
        tabLayout.addTab(tabLayout.newTab().setText("Eyes"))
        tabLayout.addTab(tabLayout.newTab().setText("Outfit"))
        
        tabLayout.addOnTabSelectedListener(object : TabLayout.OnTabSelectedListener {
            override fun onTabSelected(tab: TabLayout.Tab) {
                when (tab.position) {
                    0 -> showShapeOptions()
                    1 -> showSkinOptions()
                    2 -> showHairOptions()
                    3 -> showEyeOptions()
                    4 -> showOutfitOptions()
                }
            }
            override fun onTabUnselected(tab: TabLayout.Tab) {}
            override fun onTabReselected(tab: TabLayout.Tab) {}
        })
    }
    
    private fun loadAppearance() {
        // Load current avatar appearance
        showShapeOptions()
    }
    
    private fun showShapeOptions() {
        optionsContainer.removeAllViews()
        
        val view = layoutInflater.inflate(R.layout.panel_avatar_shape, optionsContainer, false)
        optionsContainer.addView(view)
        
        // Setup shape sliders
        setupSlider(view, R.id.sliderHeight, "Height", 0, 100, 50)
        setupSlider(view, R.id.sliderBodyFat, "Body Fat", 0, 100, 25)
        setupSlider(view, R.id.sliderMuscle, "Muscle", 0, 100, 30)
    }
    
    private fun showSkinOptions() {
        optionsContainer.removeAllViews()
        
        val view = layoutInflater.inflate(R.layout.panel_avatar_skin, optionsContainer, false)
        optionsContainer.addView(view)
    }
    
    private fun showHairOptions() {
        optionsContainer.removeAllViews()
        
        val view = layoutInflater.inflate(R.layout.panel_avatar_hair, optionsContainer, false)
        optionsContainer.addView(view)
    }
    
    private fun showEyeOptions() {
        optionsContainer.removeAllViews()
        
        val view = layoutInflater.inflate(R.layout.panel_avatar_eyes, optionsContainer, false)
        optionsContainer.addView(view)
    }
    
    private fun showOutfitOptions() {
        optionsContainer.removeAllViews()
        
        val view = layoutInflater.inflate(R.layout.panel_avatar_outfit, optionsContainer, false)
        optionsContainer.addView(view)
        
        // Outfit buttons
        view.findViewById<Button>(R.id.btnWearOutfit)?.setOnClickListener {
            // Open outfit picker
        }
        
        view.findViewById<Button>(R.id.btnSaveOutfit)?.setOnClickListener {
            // Save current outfit
        }
    }
    
    private fun setupSlider(parent: android.view.View, id: Int, label: String, min: Int, max: Int, current: Int) {
        val slider = parent.findViewById<SeekBar>(id) ?: return
        
        slider.max = max - min
        slider.progress = current - min
        
        slider.setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener {
            override fun onProgressChanged(seekBar: SeekBar, progress: Int, fromUser: Boolean) {
                val value = min + progress
                // Update avatar parameter
                updateAvatarParameter(label, value.toFloat() / max)
            }
            override fun onStartTrackingTouch(seekBar: SeekBar) {}
            override fun onStopTrackingTouch(seekBar: SeekBar) {}
        })
    }
    
    private fun updateAvatarParameter(parameter: String, value: Float) {
        // Update the avatar shape parameter
        // This would send the update to the server
    }
    
    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if (item.itemId == android.R.id.home) {
            finish()
            return true
        }
        return super.onOptionsItemSelected(item)
    }
}
