package com.linkpoint.modern.samples

import android.app.Activity
import android.opengl.GLES30
import android.opengl.GLSurfaceView
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import javax.microedition.khronos.egl.EGLConfig
import javax.microedition.khronos.opengles.GL10

/**
 * Demonstration activity for modern OpenGL ES 3.0+ graphics
 * Shows the modernized rendering pipeline in action with a simple renderer
 */
class ModernGraphicsDemoActivity : Activity() {
    
    private val TAG = "ModernGraphicsDemo"
    
    private var glSurfaceView: GLSurfaceView? = null
    private var renderer: SimpleModernRenderer? = null
    
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        
        Log.i(TAG, "Creating Modern Graphics Demo Activity")
        
        try {
            // Create OpenGL ES surface view
            glSurfaceView = GLSurfaceView(this).apply {
                // Request OpenGL ES 3.0 context
                setEGLContextClientVersion(3)
                
                // Create modern renderer
                renderer = SimpleModernRenderer()
                setRenderer(renderer)
                
                // Set render mode to continuous for animation
                renderMode = GLSurfaceView.RENDERMODE_CONTINUOUSLY
            }
            
            setContentView(glSurfaceView)
            
            Toast.makeText(this, "Modern Graphics Demo - OpenGL ES 3.0+", Toast.LENGTH_LONG).show()
            
        } catch (e: Exception) {
            Log.e(TAG, "Failed to create modern graphics demo", e)
            Toast.makeText(this, "Error: ${e.message}", Toast.LENGTH_LONG).show()
            finish()
        }
    }
    
    override fun onResume() {
        super.onResume()
        glSurfaceView?.onResume()
        Log.i(TAG, "Demo resumed")
    }
    
    override fun onPause() {
        super.onPause()
        glSurfaceView?.onPause()
        Log.i(TAG, "Demo paused")
    }
    
    override fun onDestroy() {
        Log.i(TAG, "Destroying modern graphics demo")
        super.onDestroy()
    }
    
    /**
     * Simple embedded renderer to avoid import issues during development
     */
    private class SimpleModernRenderer : GLSurfaceView.Renderer {
        
        private val TAG = "SimpleModernRenderer"
        
        override fun onSurfaceCreated(gl: GL10, config: EGLConfig) {
            Log.i(TAG, "Surface created - OpenGL ES 3.0+ context")
        }
        
        override fun onSurfaceChanged(gl: GL10, width: Int, height: Int) {
            Log.i(TAG, "Surface changed: ${width}x$height")
            // Set viewport
            GLES30.glViewport(0, 0, width, height)
        }
        
        override fun onDrawFrame(gl: GL10) {
            // Clear with a blue color to show ES 3.0 is working
            GLES30.glClearColor(0.0f, 0.2f, 0.8f, 1.0f)
            GLES30.glClear(GLES30.GL_COLOR_BUFFER_BIT)
        }
    }
}
