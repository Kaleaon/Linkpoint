package com.lumiyaviewer.lumiya.ui.main;

import android.app.Activity;
import android.opengl.GLSurfaceView;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.lumiyaviewer.lumiya.LumiyaApp;
import com.lumiyaviewer.lumiya.modern.graphics.ModernRenderPipeline;
import com.lumiyaviewer.lumiya.modern.samples.ModernLinkpointDemo;

/**
 * Main Activity showcasing modern Linkpoint components
 * Demonstrates HTTP/2 CAPS, WebSocket events, and modern graphics
 */
public class ModernMainActivity extends Activity {
    private static final String TAG = "ModernMainActivity";
    
    private TextView statusText;
    private Button connectButton;
    private Button renderTestButton;
    private GLSurfaceView glSurfaceView;
    
    private ModernLinkpointDemo modernDemo;
    
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        
        Log.i(TAG, "Modern Main Activity starting...");
        
        // Create layout programmatically to avoid resource conflicts
        createLayout();
        
        // Get modern components
        modernDemo = LumiyaApp.getModernDemo();
        
        if (modernDemo != null) {
            statusText.setText("Modern Linkpoint components initialized successfully");
        } else {
            statusText.setText("Modern components initialization failed");
        }
    }
    
    private void createLayout() {
        LinearLayout layout = new LinearLayout(this);
        layout.setOrientation(LinearLayout.VERTICAL);
        layout.setPadding(20, 20, 20, 20);
        
        // Status text
        statusText = new TextView(this);
        statusText.setText("Initializing modern Linkpoint components...");
        statusText.setPadding(10, 10, 10, 20);
        layout.addView(statusText);
        
        // Connect button
        connectButton = new Button(this);
        connectButton.setText("Test Modern SL Connection");
        connectButton.setOnClickListener(v -> testModernConnection());
        layout.addView(connectButton);
        
        // Auth test button
        Button authButton = new Button(this);
        authButton.setText("Test OAuth2 Authentication");
        authButton.setOnClickListener(v -> testModernAuthentication());
        layout.addView(authButton);
        
        // Asset streaming test button
        Button assetButton = new Button(this);
        assetButton.setText("Test Asset Streaming");
        assetButton.setOnClickListener(v -> testAssetStreaming());
        layout.addView(connectButton);
        
        // Render test button
        renderTestButton = new Button(this);
        renderTestButton.setText("Test Modern Graphics Pipeline");
        renderTestButton.setOnClickListener(v -> testModernRender());
        
        setContentView(layout);
    }
    
    private void testModernConnection() {
        if (modernDemo != null) {
            Log.i(TAG, "Testing modern Second Life connection...");
            statusText.setText("Testing modern SL connection with HTTP/2 CAPS and WebSocket events...");
            
            // Test connection with dummy data
            modernDemo.connectToSecondLife(
                "ws://example.com/eventqueue", 
                "https://example.com/caps/seed", 
                "test-auth-token"
            );
            
            statusText.setText("Modern connection test initiated. Check logs for details.");
        } else {
            statusText.setText("Modern components not available");
        }
    }
    
    private void testModernRender() {
        Log.i(TAG, "Testing modern graphics pipeline...");
        statusText.setText("Testing modern OpenGL ES 3.0+ rendering pipeline...");
        
        // This would normally initialize a proper OpenGL context
        // For now, just demonstrate the component is available
        ModernRenderPipeline pipeline = new ModernRenderPipeline();
        
        statusText.setText("Modern graphics pipeline test completed. Modern rendering components are available.");
    }
    
    private void testModernAuthentication() {
        if (modernDemo != null) {
            Log.i(TAG, "Testing OAuth2 authentication...");
            statusText.setText("Testing OAuth2 authentication with Second Life...");
            
            modernDemo.demonstrateModernAuthentication("testuser", "testpass");
            
            statusText.setText("OAuth2 authentication test initiated. Check logs for details.");
        } else {
            statusText.setText("Modern components not available");
        }
    }
    
    private void testAssetStreaming() {
        if (modernDemo != null) {
            Log.i(TAG, "Testing asset streaming...");
            statusText.setText("Testing intelligent asset streaming with adaptive quality...");
            
            modernDemo.demonstrateAssetStreaming();
            
            statusText.setText("Asset streaming test initiated. Check logs for progress.");
        } else {
            statusText.setText("Modern components not available");
        }
    }
    
    @Override
    protected void onResume() {
        super.onResume();
        if (glSurfaceView != null) {
            glSurfaceView.onResume();
        }
    }
    
    @Override
    protected void onPause() {
        super.onPause();
        if (glSurfaceView != null) {
            glSurfaceView.onPause();
        }
    }
}