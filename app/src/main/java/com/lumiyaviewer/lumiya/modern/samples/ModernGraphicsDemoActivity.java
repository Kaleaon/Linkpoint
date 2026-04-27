package com.lumiyaviewer.lumiya.modern.samples;

import android.app.Activity;
import android.opengl.GLSurfaceView;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.opengles.GL10;

/**
 * Demonstration activity for modern OpenGL ES 3.0+ graphics
 * Shows the modernized rendering pipeline in action with a simple renderer
 */
public class ModernGraphicsDemoActivity extends Activity {
    private static final String TAG = "ModernGraphicsDemo";
    
    private GLSurfaceView glSurfaceView;
    private SimpleModernRenderer renderer;
    
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        
        Log.i(TAG, "Creating Modern Graphics Demo Activity");
        
        try {
            // Create OpenGL ES surface view
            glSurfaceView = new GLSurfaceView(this);
            
            // Request OpenGL ES 3.0 context
            glSurfaceView.setEGLContextClientVersion(3);
            
            // Create modern renderer
            renderer = new SimpleModernRenderer();
            glSurfaceView.setRenderer(renderer);
            
            // Set render mode to continuous for animation
            glSurfaceView.setRenderMode(GLSurfaceView.RENDERMODE_CONTINUOUSLY);
            
            setContentView(glSurfaceView);
            
            Toast.makeText(this, "Modern Graphics Demo - OpenGL ES 3.0+", Toast.LENGTH_LONG).show();
            
        } catch (Exception e) {
            Log.e(TAG, "Failed to create modern graphics demo", e);
            Toast.makeText(this, "Error: " + e.getMessage(), Toast.LENGTH_LONG).show();
            finish();
        }
    }
    
    @Override
    protected void onResume() {
        super.onResume();
        if (glSurfaceView != null) {
            glSurfaceView.onResume();
        }
        
        Log.i(TAG, "Demo resumed");
    }
    
    @Override
    protected void onPause() {
        super.onPause();
        if (glSurfaceView != null) {
            glSurfaceView.onPause();
        }
        
        Log.i(TAG, "Demo paused");
    }
    
    @Override
    protected void onDestroy() {
        Log.i(TAG, "Destroying modern graphics demo");
        super.onDestroy();
    }
    
    /**
     * Simple embedded renderer to avoid import issues during development
     */
    private static class SimpleModernRenderer implements GLSurfaceView.Renderer {
        private static final String TAG = "SimpleModernRenderer";
        
        @Override
        public void onSurfaceCreated(GL10 gl, EGLConfig config) {
            Log.i(TAG, "Surface created - OpenGL ES 3.0+ context");
        }
        
        @Override
        public void onSurfaceChanged(GL10 gl, int width, int height) {
            Log.i(TAG, "Surface changed: " + width + "x" + height);
            // Set viewport
            android.opengl.GLES30.glViewport(0, 0, width, height);
        }
        
        @Override
        public void onDrawFrame(GL10 gl) {
            // Clear with a blue color to show ES 3.0 is working
            android.opengl.GLES30.glClearColor(0.0f, 0.2f, 0.8f, 1.0f);
            android.opengl.GLES30.glClear(android.opengl.GLES30.GL_COLOR_BUFFER_BIT);
        }
    }
}