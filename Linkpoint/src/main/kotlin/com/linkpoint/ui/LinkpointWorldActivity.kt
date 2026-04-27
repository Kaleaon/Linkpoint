package com.linkpoint.ui

import android.os.Bundle
import android.view.WindowManager
import androidx.appcompat.app.AppCompatActivity
import com.linkpoint.integration.IntegratedGLSurfaceView
import com.linkpoint.integration.LinkpointMainApplication

/**
 * World View Activity
 * Full-screen 3D rendering with integrated modern features
 */
class LinkpointWorldActivity : AppCompatActivity() {
    
    private lateinit var glSurfaceView: IntegratedGLSurfaceView
    
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        
        // Keep screen on during world view
        window.addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON)
        
        // Create integrated GL surface view
        glSurfaceView = IntegratedGLSurfaceView(this)
        setContentView(glSurfaceView)
        
        // Get access to managers through renderer
        val app = application as LinkpointMainApplication
        val renderer = glSurfaceView.getIntegratedRenderer()
        
        // Managers are now accessible:
        // - renderer.getAnimeshManager()
        // - renderer.getBomManager()
        // - renderer.getEEPManager()
        // - renderer.getPBRManager()
    }
    
    override fun onPause() {
        super.onPause()
        glSurfaceView.onPause()
    }
    
    override fun onResume() {
        super.onResume()
        glSurfaceView.onResume()
    }
    
    override fun onDestroy() {
        glSurfaceView.getIntegratedRenderer().cleanup()
        super.onDestroy()
    }
}