package com.lumiyaviewer.lumiya.render;

import android.opengl.GLES30;
import android.opengl.GLSurfaceView;
import android.util.Log;

import com.lumiyaviewer.lumiya.modern.graphics.ModernRenderPipeline;

import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.opengles.GL10;

/**
 * Modern world view renderer using OpenGL ES 3.0+ 
 * Replaces legacy WorldViewRenderer with modern graphics pipeline
 * Eliminates all OpenGL ES 1.1 dependencies
 */
public class ModernWorldViewRenderer implements GLSurfaceView.Renderer {
    private static final String TAG = "ModernWorldViewRenderer";
    
    private ModernRenderContext renderContext;
    private boolean initialized = false;
    
    // Rendering state
    private int surfaceWidth = 0;
    private int surfaceHeight = 0;
    
    // Camera parameters
    private final float[] cameraPosition = {0.0f, 0.0f, 5.0f};
    private final float[] lookAtPoint = {0.0f, 0.0f, 0.0f};
    private final float[] upVector = {0.0f, 1.0f, 0.0f};
    
    // Lighting parameters (for PBR)
    private final float[] directionalLight = {
        0.0f, -1.0f, -1.0f, 1.0f,  // direction + intensity
        0.0f, 0.0f, 0.0f, 0.0f,    // unused
        0.0f, 0.0f, 0.0f, 0.0f,    // unused  
        0.0f, 0.0f, 0.0f, 0.0f     // unused
    };
    
    @Override
    public void onSurfaceCreated(GL10 gl, EGLConfig config) {
        Log.i(TAG, "Surface created - initializing modern graphics");
        
        // Create and initialize modern render context
        renderContext = new ModernRenderContext();
        initialized = renderContext.initialize();
        
        if (!initialized) {
            Log.e(TAG, "Failed to initialize modern render context");
            return;
        }
        
        Log.i(TAG, "Modern graphics system initialized successfully");
    }
    
    @Override
    public void onSurfaceChanged(GL10 gl, int width, int height) {
        Log.i(TAG, "Surface changed: " + width + "x" + height);
        
        if (!initialized) {
            Log.w(TAG, "Render context not initialized - skipping surface change");
            return;
        }
        
        surfaceWidth = width;
        surfaceHeight = height;
        
        // Set up modern projection
        renderContext.setupProjection(width, height, 60.0f, 0.1f, 1000.0f);
        
        Log.i(TAG, "Modern projection configured");
    }
    
    @Override
    public void onDrawFrame(GL10 gl) {
        if (!initialized) {
            // Clear screen with error color if not initialized
            GLES30.glClearColor(1.0f, 0.0f, 0.0f, 1.0f);
            GLES30.glClear(GLES30.GL_COLOR_BUFFER_BIT);
            return;
        }
        
        // Begin modern frame
        renderContext.beginFrame();
        
        // Set up camera
        renderContext.setupCamera(cameraPosition, lookAtPoint, upVector);
        
        // Render scene using modern pipeline
        renderModernScene();
        
        // End frame
        renderContext.endFrame();
    }
    
    /**
     * Render scene using modern PBR pipeline
     */
    private void renderModernScene() {
        ModernRenderPipeline renderPipeline = renderContext.getRenderPipeline();
        
        if (!renderPipeline.isModernPipelineAvailable()) {
            Log.w(TAG, "Modern pipeline not available - using fallback");
            renderLegacyFallback();
            return;
        }
        
        // Create render parameters for modern pipeline
        ModernRenderPipeline.RenderParams params = new ModernRenderPipeline.RenderParams();
        
        // Set camera position
        System.arraycopy(cameraPosition, 0, params.cameraPosition, 0, 3);
        
        // Set lighting parameters
        System.arraycopy(directionalLight, 0, params.directionalLight, 0, directionalLight.length);
        
        // Set default PBR textures (would be loaded from assets in real implementation)
        params.albedoTexture = getDefaultAlbedoTexture();
        params.normalTexture = getDefaultNormalTexture();
        params.metallicRoughnessTexture = getDefaultMetallicRoughnessTexture();
        
        // Render with modern pipeline
        renderContext.renderWithModernPipeline(params);
        
        // Render demo geometry to show the system working
        renderDemoGeometry();
    }
    
    /**
     * Fallback rendering for devices without full ES 3.0 support
     */
    private void renderLegacyFallback() {
        Log.d(TAG, "Using legacy fallback rendering");
        
        // Clear with blue color to indicate fallback mode
        GLES30.glClearColor(0.0f, 0.0f, 1.0f, 1.0f);
        GLES30.glClear(GLES30.GL_COLOR_BUFFER_BIT | GLES30.GL_DEPTH_BUFFER_BIT);
    }
    
    /**
     * Render demo geometry to demonstrate modern pipeline
     */
    private void renderDemoGeometry() {
        // Animate rotation for demo
        float time = System.currentTimeMillis() * 0.001f;
        
        // Transform for demo object
        renderContext.pushMatrix();
        renderContext.rotate(time * 30.0f, 0.0f, 1.0f, 0.0f);  // Rotate around Y axis
        renderContext.scale(2.0f, 2.0f, 2.0f);
        
        // In a real implementation, this would render actual geometry
        // For now, we just log that we're rendering
        Log.v(TAG, "Rendering demo geometry at frame " + renderContext.frameCount);
        
        renderContext.popMatrix();
    }
    
    /**
     * Get default textures (placeholder implementation)
     * In real implementation, these would be loaded from assets
     */
    private int getDefaultAlbedoTexture() {
        // Return white texture handle (would create actual texture)
        return createSolidColorTexture(255, 255, 255, 255);
    }
    
    private int getDefaultNormalTexture() {
        // Return default normal map (128, 128, 255) for flat surface
        return createSolidColorTexture(128, 128, 255, 255);
    }
    
    private int getDefaultMetallicRoughnessTexture() {
        // Return default metallic/roughness (0, 128) = not metallic, medium roughness
        return createSolidColorTexture(0, 128, 0, 255);
    }
    
    /**
     * Create a simple solid color texture for testing
     */
    private int createSolidColorTexture(int r, int g, int b, int a) {
        int[] textureHandle = new int[1];
        GLES30.glGenTextures(1, textureHandle, 0);
        
        if (textureHandle[0] == 0) {
            Log.e(TAG, "Failed to generate texture");
            return 0;
        }
        
        GLES30.glBindTexture(GLES30.GL_TEXTURE_2D, textureHandle[0]);
        
        // Create 1x1 pixel texture
        byte[] pixelData = new byte[] { (byte)r, (byte)g, (byte)b, (byte)a };
        
        GLES30.glTexImage2D(GLES30.GL_TEXTURE_2D, 0, GLES30.GL_RGBA, 1, 1, 0, 
                           GLES30.GL_RGBA, GLES30.GL_UNSIGNED_BYTE, 
                           java.nio.ByteBuffer.wrap(pixelData));
        
        GLES30.glTexParameteri(GLES30.GL_TEXTURE_2D, GLES30.GL_TEXTURE_MIN_FILTER, GLES30.GL_NEAREST);
        GLES30.glTexParameteri(GLES30.GL_TEXTURE_2D, GLES30.GL_TEXTURE_MAG_FILTER, GLES30.GL_NEAREST);
        GLES30.glTexParameteri(GLES30.GL_TEXTURE_2D, GLES30.GL_TEXTURE_WRAP_S, GLES30.GL_CLAMP_TO_EDGE);
        GLES30.glTexParameteri(GLES30.GL_TEXTURE_2D, GLES30.GL_TEXTURE_WRAP_T, GLES30.GL_CLAMP_TO_EDGE);
        
        return textureHandle[0];
    }
    
    /**
     * Update camera position
     */
    public void setCameraPosition(float x, float y, float z) {
        cameraPosition[0] = x;
        cameraPosition[1] = y;
        cameraPosition[2] = z;
    }
    
    /**
     * Update look at point  
     */
    public void setLookAt(float x, float y, float z) {
        lookAtPoint[0] = x;
        lookAtPoint[1] = y;
        lookAtPoint[2] = z;
    }
    
    /**
     * Update lighting direction
     */
    public void setDirectionalLight(float x, float y, float z, float intensity) {
        directionalLight[0] = x;
        directionalLight[1] = y;
        directionalLight[2] = z;
        directionalLight[3] = intensity;
    }
    
    /**
     * Get current surface dimensions
     */
    public int getSurfaceWidth() { return surfaceWidth; }
    public int getSurfaceHeight() { return surfaceHeight; }
    
    /**
     * Check if modern graphics are available
     */
    public boolean isModernGraphicsAvailable() {
        return initialized && renderContext.getRenderPipeline().isModernPipelineAvailable();
    }
    
    /**
     * Get graphics capabilities string for debugging
     */
    public String getGraphicsInfo() {
        if (!initialized) {
            return "Graphics not initialized";
        }
        
        StringBuilder info = new StringBuilder();
        info.append("Modern Graphics: ").append(isModernGraphicsAvailable()).append("\n");
        info.append("Compute Shaders: ").append(renderContext.hasComputeShaders()).append("\n");
        info.append("Tessellation: ").append(renderContext.hasTessellation()).append("\n");
        info.append("Geometry Shaders: ").append(renderContext.hasGeometryShaders()).append("\n");
        info.append("Frame Count: ").append(renderContext.frameCount).append("\n");
        
        return info.toString();
    }
    
    /**
     * Cleanup resources
     */
    public void cleanup() {
        Log.i(TAG, "Cleaning up modern world view renderer");
        
        if (renderContext != null) {
            renderContext.cleanup();
            renderContext = null;
        }
        
        initialized = false;
    }
}