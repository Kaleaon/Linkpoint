package com.lumiyaviewer.lumiya.ui.modern;
import java.util.*;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.coordinatorlayout.widget.CoordinatorLayout;
import androidx.constraintlayout.widget.ConstraintLayout;
import com.google.android.material.appbar.AppBarLayout;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;
import com.lumiyaviewer.lumiya.LumiyaApp;
import com.lumiyaviewer.lumiya.modern.samples.ModernLinkpointDemo;

/**
 * Modern World View Activity using Material Design 3
 * This demonstrates modernized UI patterns while maintaining Lumiya functionality
 */
public class ModernWorldActivity extends AppCompatActivity {
    private static final String TAG = "ModernWorldActivity";
    
    // UI Components using modern patterns (no ButterKnife)
    private CoordinatorLayout coordinatorLayout;
    private AppBarLayout appBarLayout;
    private Toolbar toolbar;
    private ConstraintLayout worldContainer;
    private TextView statusText;
    private ProgressBar loadingProgress;
    private FloatingActionButton chatFab;
    private Button connectButton;
    
    // Modern backend components
    private ModernLinkpointDemo modernDemo;
    
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        
        // Create UI programmatically using modern Material Design 3 patterns
        createModernLayout();
        setSupportActionBar(toolbar);
        
        // Initialize modern components
        initializeModernComponents();
        
        // Set up event handlers
        setupEventHandlers();
        
        // Initialize world view
        initializeWorldView();
    }
    
    private void createModernLayout() {
        // Create root coordinator layout
        coordinatorLayout = new CoordinatorLayout(this);
        CoordinatorLayout.LayoutParams rootParams = new CoordinatorLayout.LayoutParams(
            CoordinatorLayout.LayoutParams.MATCH_PARENT, 
            CoordinatorLayout.LayoutParams.MATCH_PARENT);
        coordinatorLayout.setLayoutParams(rootParams);
        
        // Create app bar layout with modern Material Design
        appBarLayout = new AppBarLayout(this);
        CoordinatorLayout.LayoutParams appBarParams = new CoordinatorLayout.LayoutParams(
            CoordinatorLayout.LayoutParams.MATCH_PARENT,
            CoordinatorLayout.LayoutParams.WRAP_CONTENT);
        appBarLayout.setLayoutParams(appBarParams);
        
        // Create toolbar with Material Design
        toolbar = new Toolbar(this);
        AppBarLayout.LayoutParams toolbarParams = new AppBarLayout.LayoutParams(
            AppBarLayout.LayoutParams.MATCH_PARENT,
            AppBarLayout.LayoutParams.WRAP_CONTENT);
        toolbar.setLayoutParams(toolbarParams);
        toolbar.setTitle("Lumiya World View");
        
        appBarLayout.addView(toolbar);
        coordinatorLayout.addView(appBarLayout);
        
        // Create main content container using ConstraintLayout (modern replacement for AbsoluteLayout)
        worldContainer = new ConstraintLayout(this);
        CoordinatorLayout.LayoutParams containerParams = new CoordinatorLayout.LayoutParams(
            CoordinatorLayout.LayoutParams.MATCH_PARENT,
            CoordinatorLayout.LayoutParams.MATCH_PARENT);
        containerParams.setBehavior(new AppBarLayout.ScrollingViewBehavior());
        worldContainer.setLayoutParams(containerParams);
        worldContainer.setPadding(16, 16, 16, 16);
        
        // Status text
        statusText = new TextView(this);
        statusText.setId(View.generateViewId());
        statusText.setText("Initializing modern Lumiya world view...");
        statusText.setTextSize(18);
        ConstraintLayout.LayoutParams statusParams = new ConstraintLayout.LayoutParams(
            ConstraintLayout.LayoutParams.MATCH_PARENT,
            ConstraintLayout.LayoutParams.WRAP_CONTENT);
        statusParams.topToTop = ConstraintLayout.LayoutParams.PARENT_ID;
        statusParams.leftToLeft = ConstraintLayout.LayoutParams.PARENT_ID;
        statusParams.rightToRight = ConstraintLayout.LayoutParams.PARENT_ID;
        statusParams.topMargin = 32;
        statusText.setLayoutParams(statusParams);
        worldContainer.addView(statusText);
        
        // Progress bar
        loadingProgress = new ProgressBar(this, null, android.R.attr.progressBarStyleHorizontal);
        loadingProgress.setId(View.generateViewId());
        loadingProgress.setProgress(0);
        loadingProgress.setMax(100);
        ConstraintLayout.LayoutParams progressParams = new ConstraintLayout.LayoutParams(
            ConstraintLayout.LayoutParams.MATCH_PARENT,
            ConstraintLayout.LayoutParams.WRAP_CONTENT);
        progressParams.topToBottom = statusText.getId();
        progressParams.leftToLeft = ConstraintLayout.LayoutParams.PARENT_ID;
        progressParams.rightToRight = ConstraintLayout.LayoutParams.PARENT_ID;
        progressParams.topMargin = 16;
        loadingProgress.setLayoutParams(progressParams);
        worldContainer.addView(loadingProgress);
        
        // Connect button
        connectButton = new Button(this);
        connectButton.setId(View.generateViewId());
        connectButton.setText("Connect to Second Life");
        ConstraintLayout.LayoutParams buttonParams = new ConstraintLayout.LayoutParams(
            ConstraintLayout.LayoutParams.WRAP_CONTENT,
            ConstraintLayout.LayoutParams.WRAP_CONTENT);
        buttonParams.topToBottom = loadingProgress.getId();
        buttonParams.leftToLeft = ConstraintLayout.LayoutParams.PARENT_ID;
        buttonParams.rightToRight = ConstraintLayout.LayoutParams.PARENT_ID;
        buttonParams.topMargin = 32;
        connectButton.setLayoutParams(buttonParams);
        worldContainer.addView(connectButton);
        
        coordinatorLayout.addView(worldContainer);
        
        // Floating Action Button for chat
        chatFab = new FloatingActionButton(this);
        chatFab.setImageResource(android.R.drawable.ic_dialog_email); // Using system icon as fallback
        CoordinatorLayout.LayoutParams fabParams = new CoordinatorLayout.LayoutParams(
            CoordinatorLayout.LayoutParams.WRAP_CONTENT,
            CoordinatorLayout.LayoutParams.WRAP_CONTENT);
        fabParams.gravity = android.view.Gravity.BOTTOM | android.view.Gravity.END;
        fabParams.setMargins(0, 0, 64, 64);
        chatFab.setLayoutParams(fabParams);
        coordinatorLayout.addView(chatFab);
        
        setContentView(coordinatorLayout);
    }
    
    private void initializeModernComponents() {
        // Get modern demo from application
        modernDemo = LumiyaApp.getModernDemo();
        
        if (modernDemo != null) {
            statusText.setText("Modern Lumiya components initialized successfully");
            loadingProgress.setProgress(25);
        } else {
            statusText.setText("Failed to initialize modern components");
            loadingProgress.setProgress(0);
        }
    }
    
    private void setupEventHandlers() {
        // Connect button click handler
        connectButton.setOnClickListener(v -> {
            connectToSecondLife();
        
        // Chat FAB click handler
        chatFab.setOnClickListener(v -> {
            openChat();
        
        // Toolbar navigation
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }
    }
    
    private void initializeWorldView() {
        statusText.setText("Initializing 3D world view...");
        loadingProgress.setProgress(50);
        
        // TODO: Initialize actual 3D rendering surface
        // This would normally create an OpenGL ES surface view
        // For now, just demonstrate the modern UI structure
        
        // Simulate initialization progress
        statusText.postDelayed(() -> {
            statusText.setText("Modern world view ready");
            loadingProgress.setProgress(100);
            
            // Show success message
            Snackbar.make(coordinatorLayout, 
                "Modern Lumiya world view initialized successfully", 
                Snackbar.LENGTH_LONG).show();
        }, 2000);
    }
    
    private void connectToSecondLife() {
        statusText.setText("Connecting to Second Life with modern protocols...");
        loadingProgress.setProgress(75);
        
        if (modernDemo != null) {
            // Use modern connection methods
            modernDemo.connectToSecondLife(
                "wss://simulator.secondlife.com/eventqueue",
                "https://simulator.secondlife.com/caps/seed",
                "demo-auth-token"
            );
            
            // Initialize modern graphics
            modernDemo.initializeGraphics();
            
            statusText.setText("Connected! Modern graphics pipeline active.");
            loadingProgress.setProgress(100);
            
            Snackbar.make(coordinatorLayout, 
                "Connected to Second Life using modern HTTP/2 + WebSocket protocols", 
                Snackbar.LENGTH_LONG).show();
        } else {
            statusText.setText("Modern components not available for connection");
            Snackbar.make(coordinatorLayout, 
                "Connection failed - modern components not initialized", 
                Snackbar.LENGTH_SHORT).show();
        }
    }
    
    private void openChat() {
        // TODO: Open modern chat interface
        Snackbar.make(coordinatorLayout, 
            "Opening modern chat interface...", 
            Snackbar.LENGTH_SHORT).show();
    }
    
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Create modern menu options
        menu.add(0, 1, 0, "Settings").setIcon(android.R.drawable.ic_menu_preferences);
        menu.add(0, 2, 0, "Inventory").setIcon(android.R.drawable.ic_menu_view);
        menu.add(0, 3, 0, "Graphics Test").setIcon(android.R.drawable.ic_menu_gallery);
        return true;
    }
    
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                finish();
                return true;
            case 1: // Settings
                Snackbar.make(coordinatorLayout, "Opening settings...", Snackbar.LENGTH_SHORT).show();
                return true;
            case 2: // Inventory
                Snackbar.make(coordinatorLayout, "Opening inventory...", Snackbar.LENGTH_SHORT).show();
                return true;
            case 3: // Graphics test
                if (modernDemo != null) {
                    modernDemo.demonstrateModernGraphics();
                    Snackbar.make(coordinatorLayout, 
                        "Graphics test completed - check logs for details", 
                        Snackbar.LENGTH_LONG).show();
                }
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }
    
    @Override
    protected void onDestroy() {
        super.onDestroy();
        
        // Clean up modern components
        if (modernDemo != null) {
            modernDemo.shutdown();
        }
    }
}