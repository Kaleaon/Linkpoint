package com.lumiyaviewer.lumiya.ui.render

import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import com.lumiyaviewer.lumiya.graphics.filament.FilamentSurfaceView
import com.lumiyaviewer.lumiya.slproto.types.LLVector3

/**
 * FilamentTestActivity - Test activity for Filament rendering in Linkpoint
 * 
 * This activity demonstrates basic Filament rendering with a simple scene.
 */
class FilamentTestActivity : AppCompatActivity() {
    
    companion object {
        private const val TAG = "FilamentTestActivity"
    }
    
    private lateinit var filamentSurfaceView: FilamentSurfaceView
    
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        
        try {
            // Create Filament surface view
            filamentSurfaceView = FilamentSurfaceView(this)
            
            // Set as content view
            setContentView(filamentSurfaceView)
            
            // Initialize world renderer
            filamentSurfaceView.initializeWorldRenderer()
            
            // Set camera position to look at the scene
            filamentSurfaceView.getWorldRenderer()?.setCameraPosition(
                LLVector3(0f, -20f, 10f),  // Position camera back and up
                0f,    // No X rotation
                0f     // No Y rotation
            )
            
            Log.i(TAG, "Linkpoint FilamentTestActivity created successfully")
            
        } catch (e: Exception) {
            Log.e(TAG, "Failed to create FilamentTestActivity", e)
            finish()
        }
    }
    
    override fun onResume() {
        super.onResume()
        filamentSurfaceView.onResume()
    }
    
    override fun onPause() {
        filamentSurfaceView.onPause()
        super.onPause()
    }
    
    override fun onDestroy() {
        filamentSurfaceView.destroy()
        super.onDestroy()
    }
}
