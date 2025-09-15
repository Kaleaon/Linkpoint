package com.lumiyaviewer.lumiya.ui.settings;

import android.app.AlertDialog;
import android.content.Context;
import android.os.Bundle;
import android.text.InputType;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import java.util.List;

/**
 * Emulator Settings Activity
 * 
 * Provides a comprehensive UI for managing Android emulators through the CLI tool integration.
 * Users can create, start, stop, and manage AVDs directly from the app settings.
 */
public class EmulatorSettingsActivity extends AppCompatActivity {
    private static final String TAG = "EmulatorSettings";
    
    private EmulatorManager emulatorManager;
    private ScrollView scrollView;
    private LinearLayout mainLayout;
    private TextView statusText;
    
    // Settings sections
    private LinearLayout avdSection;
    private LinearLayout actionSection;
    private LinearLayout infoSection;
    
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        
        emulatorManager = new EmulatorManager(this);
        
        createLayout();
        setupToolbar();
        populateSettings();
        refreshAVDList();
    }
    
    private void createLayout() {
        // Create root scroll view
        scrollView = new ScrollView(this);
        scrollView.setFillViewport(true);
        
        // Create main layout
        mainLayout = new LinearLayout(this);
        mainLayout.setOrientation(LinearLayout.VERTICAL);
        mainLayout.setPadding(24, 16, 24, 24);
        
        // Status text
        statusText = new TextView(this);
        statusText.setTextSize(12);
        statusText.setTextColor(0xFF666666);
        statusText.setPadding(16, 8, 16, 16);
        mainLayout.addView(statusText);
        
        scrollView.addView(mainLayout);
        setContentView(scrollView);
    }
    
    private void setupToolbar() {
        Toolbar toolbar = new Toolbar(this);
        toolbar.setTitle("Emulator Management");
        toolbar.setTitleTextColor(0xFFFFFFFF);
        toolbar.setBackgroundColor(0xFF2196F3);
        
        // Add toolbar to layout
        LinearLayout.LayoutParams toolbarParams = new LinearLayout.LayoutParams(
            LinearLayout.LayoutParams.MATCH_PARENT,
            LinearLayout.LayoutParams.WRAP_CONTENT
        );
        mainLayout.addView(toolbar, 0, toolbarParams);
        
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }
    }
    
    private void populateSettings() {
        // Check if emulator management is available
        if (!emulatorManager.isAvailable()) {
            addErrorSection();
            return;
        }
        
        createAVDSection();
        createActionSection();
        createInfoSection();
    }
    
    private void addErrorSection() {
        LinearLayout errorSection = createSection("❌ Emulator Management Unavailable");
        
        addInfoText(errorSection, 
                   "Emulator management is not available", 
                   "This may be due to:\n" +
                   "• Android SDK not found\n" +
                   "• Emulator script not executable\n" +
                   "• Missing command-line tools\n\n" +
                   "Please ensure Android SDK is properly installed and ANDROID_HOME is set.");
    }
    
    private void createAVDSection() {
        avdSection = createSection("📱 Android Virtual Devices");
        
        // Refresh button for AVD list
        addActionButton(avdSection,
                       "Refresh AVD List",
                       "Update the list of available emulators",
                       this::refreshAVDList);
        
        // AVD list will be populated dynamically
    }
    
    private void createActionSection() {
        actionSection = createSection("⚡ Emulator Actions");
        
        // Create new AVD
        addActionButton(actionSection,
                       "Create New AVD",
                       "Create a new Android Virtual Device",
                       this::showCreateAVDDialog);
        
        // Start AVD
        addActionButton(actionSection,
                       "Start Emulator",
                       "Start an existing AVD",
                       this::showStartAVDDialog);
        
        // Stop AVD
        addActionButton(actionSection,
                       "Stop Emulator",
                       "Stop a running AVD",
                       this::showStopAVDDialog);
        
        // Delete AVD
        addActionButton(actionSection,
                       "Delete AVD",
                       "Remove an existing AVD",
                       this::showDeleteAVDDialog);
        
        // Show status
        addActionButton(actionSection,
                       "Show Emulator Status",
                       "Display running emulators and ADB devices",
                       this::showEmulatorStatus);
    }
    
    private void createInfoSection() {
        infoSection = createSection("ℹ️ System Information");
        
        // Show device profiles
        addActionButton(infoSection,
                       "List Device Profiles",
                       "Show available device profiles for AVDs",
                       this::showDeviceProfiles);
        
        // Show system images
        addActionButton(infoSection,
                       "List System Images",
                       "Show available Android system images",
                       this::showSystemImages);
        
        // Install system image
        addActionButton(infoSection,
                       "Install System Image",
                       "Download and install Android system images",
                       this::showInstallImageDialog);
    }
    
    private LinearLayout createSection(String title) {
        // Section title
        TextView titleText = new TextView(this);
        titleText.setText(title);
        titleText.setTextSize(18);
        titleText.setTypeface(null, android.graphics.Typeface.BOLD);
        titleText.setPadding(16, 24, 16, 8);
        titleText.setTextColor(0xFF1976D2);
        mainLayout.addView(titleText);
        
        // Section container
        LinearLayout section = new LinearLayout(this);
        section.setOrientation(LinearLayout.VERTICAL);
        section.setBackgroundColor(0xFFF5F5F5);
        section.setPadding(8, 8, 8, 16);
        mainLayout.addView(section);
        
        return section;
    }
    
    private void addActionButton(LinearLayout parent, String title, String description, Runnable action) {
        // Container for this setting
        LinearLayout container = new LinearLayout(this);
        container.setOrientation(LinearLayout.VERTICAL);
        container.setPadding(16, 12, 16, 12);
        container.setClickable(true);
        container.setOnClickListener(v -> action.run());
        
        // Add slight background color for buttons
        container.setBackgroundColor(0xFFE3F2FD);
        
        // Add margin between buttons
        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
            LinearLayout.LayoutParams.MATCH_PARENT,
            LinearLayout.LayoutParams.WRAP_CONTENT
        );
        params.setMargins(8, 4, 8, 4);
        container.setLayoutParams(params);
        
        // Title
        TextView titleText = new TextView(this);
        titleText.setText(title);
        titleText.setTextSize(16);
        titleText.setTypeface(null, android.graphics.Typeface.BOLD);
        titleText.setTextColor(0xFF1976D2);
        container.addView(titleText);
        
        // Description
        TextView descText = new TextView(this);
        descText.setText(description);
        descText.setTextSize(12);
        descText.setTextColor(0xFF666666);
        container.addView(descText);
        
        parent.addView(container);
    }
    
    private void addInfoText(LinearLayout parent, String title, String content) {
        LinearLayout container = new LinearLayout(this);
        container.setOrientation(LinearLayout.VERTICAL);
        container.setPadding(16, 12, 16, 12);
        container.setBackgroundColor(0xFFFFF3E0);
        
        // Title
        TextView titleText = new TextView(this);
        titleText.setText(title);
        titleText.setTextSize(16);
        titleText.setTypeface(null, android.graphics.Typeface.BOLD);
        titleText.setTextColor(0xFFE65100);
        container.addView(titleText);
        
        // Content
        TextView contentText = new TextView(this);
        contentText.setText(content);
        contentText.setTextSize(14);
        contentText.setTextColor(0xFF5D4037);
        container.addView(contentText);
        
        parent.addView(container);
    }
    
    private void updateStatus(String status) {
        runOnUiThread(() -> {
            statusText.setText("Status: " + status);
    }
    
    private EmulatorManager.EmulatorCallback createCallback(String operation) {
        return new EmulatorManager.EmulatorCallback() {
            @Override
            public void onSuccess(String message) {
                runOnUiThread(() -> {
                    updateStatus(operation + " completed");
                    showResultDialog("Success", message);
            }
            
            @Override
            public void onError(String error) {
                runOnUiThread(() -> {
                    updateStatus(operation + " failed");
                    showResultDialog("Error", error);
            }
            
            @Override
            public void onStatusUpdate(String status) {
                updateStatus(status);
            }
        };
    }
    
    private void refreshAVDList() {
        updateStatus("Loading AVD list...");
        
        emulatorManager.listAVDs(new EmulatorManager.EmulatorCallback() {
            @Override
            public void onSuccess(String output) {
                runOnUiThread(() -> {
                    updateStatus("AVD list updated");
                    
                    // Clear existing AVD items
                    // Find and remove old AVD items
                    for (int i = avdSection.getChildCount() - 1; i >= 0; i--) {
                        View child = avdSection.getChildAt(i);
                        if (child.getTag() != null && "avd_item".equals(child.getTag())) {
                            avdSection.removeView(child);
                        }
                    }
                    
                    // Parse and display AVDs
                    List<EmulatorManager.AVDInfo> avds = EmulatorManager.parseAVDList(output);
                    if (avds.isEmpty()) {
                        addInfoText(avdSection, "No AVDs Found", "Create a new AVD to get started with emulator testing.");
                        avdSection.getChildAt(avdSection.getChildCount() - 1).setTag("avd_item");
                    } else {
                        for (EmulatorManager.AVDInfo avd : avds) {
                            addAVDItem(avd);
                        }
                    }
            }
            
            @Override
            public void onError(String error) {
                runOnUiThread(() -> {
                    updateStatus("Failed to load AVD list");
                    showResultDialog("Error", "Failed to load AVD list:\n" + error);
            }
            
            @Override
            public void onStatusUpdate(String status) {
                updateStatus(status);
            }
    }
    
    private void addAVDItem(EmulatorManager.AVDInfo avd) {
        LinearLayout container = new LinearLayout(this);
        container.setOrientation(LinearLayout.VERTICAL);
        container.setPadding(16, 8, 16, 8);
        container.setBackgroundColor(0xFFE8F5E8);
        container.setTag("avd_item");
        
        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
            LinearLayout.LayoutParams.MATCH_PARENT,
            LinearLayout.LayoutParams.WRAP_CONTENT
        );
        params.setMargins(8, 4, 8, 4);
        container.setLayoutParams(params);
        
        // AVD name
        TextView nameText = new TextView(this);
        nameText.setText("📱 " + avd.name);
        nameText.setTextSize(16);
        nameText.setTypeface(null, android.graphics.Typeface.BOLD);
        nameText.setTextColor(0xFF2E7D32);
        container.addView(nameText);
        
        // AVD details
        StringBuilder details = new StringBuilder();
        if (!avd.device.isEmpty()) details.append("Device: ").append(avd.device).append("  ");
        if (!avd.apiLevel.isEmpty()) details.append("API: ").append(avd.apiLevel).append("  ");
        if (!avd.abi.isEmpty()) details.append("ABI: ").append(avd.abi);
        
        if (details.length() > 0) {
            TextView detailsText = new TextView(this);
            detailsText.setText(details.toString());
            detailsText.setTextSize(12);
            detailsText.setTextColor(0xFF4CAF50);
            container.addView(detailsText);
        }
        
        avdSection.addView(container);
    }
    
    private void showCreateAVDDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Create New AVD");
        
        LinearLayout layout = new LinearLayout(this);
        layout.setOrientation(LinearLayout.VERTICAL);
        layout.setPadding(20, 20, 20, 20);
        
        // AVD Name
        TextView nameLabel = new TextView(this);
        nameLabel.setText("AVD Name:");
        nameLabel.setTextSize(14);
        nameLabel.setTypeface(null, android.graphics.Typeface.BOLD);
        layout.addView(nameLabel);
        
        EditText nameInput = new EditText(this);
        nameInput.setHint("e.g., linkpoint-test");
        nameInput.setInputType(InputType.TYPE_CLASS_TEXT);
        layout.addView(nameInput);
        
        // Device Profile
        TextView deviceLabel = new TextView(this);
        deviceLabel.setText("Device Profile:");
        deviceLabel.setTextSize(14);
        deviceLabel.setTypeface(null, android.graphics.Typeface.BOLD);
        layout.addView(deviceLabel);
        
        Spinner deviceSpinner = new Spinner(this);
        ArrayAdapter<String> deviceAdapter = new ArrayAdapter<>(this, 
            android.R.layout.simple_spinner_item, EmulatorManager.EmulatorDefaults.POPULAR_DEVICES);
        deviceAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        deviceSpinner.setAdapter(deviceAdapter);
        layout.addView(deviceSpinner);
        
        // API Level
        TextView apiLabel = new TextView(this);
        apiLabel.setText("API Level:");
        apiLabel.setTextSize(14);
        apiLabel.setTypeface(null, android.graphics.Typeface.BOLD);
        layout.addView(apiLabel);
        
        Spinner apiSpinner = new Spinner(this);
        ArrayAdapter<String> apiAdapter = new ArrayAdapter<>(this, 
            android.R.layout.simple_spinner_item, EmulatorManager.EmulatorDefaults.SUPPORTED_APIS);
        apiAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        apiSpinner.setAdapter(apiAdapter);
        apiSpinner.setSelection(apiAdapter.getPosition(EmulatorManager.EmulatorDefaults.DEFAULT_API));
        layout.addView(apiSpinner);
        
        // ABI
        TextView abiLabel = new TextView(this);
        abiLabel.setText("ABI:");
        abiLabel.setTextSize(14);
        abiLabel.setTypeface(null, android.graphics.Typeface.BOLD);
        layout.addView(abiLabel);
        
        Spinner abiSpinner = new Spinner(this);
        ArrayAdapter<String> abiAdapter = new ArrayAdapter<>(this, 
            android.R.layout.simple_spinner_item, EmulatorManager.EmulatorDefaults.SUPPORTED_ABIS);
        abiAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        abiSpinner.setAdapter(abiAdapter);
        abiSpinner.setSelection(abiAdapter.getPosition(EmulatorManager.EmulatorDefaults.DEFAULT_ABI));
        layout.addView(abiSpinner);
        
        builder.setView(layout);
        
        builder.setPositiveButton("Create", (dialog, which) -> {
            String name = nameInput.getText().toString().trim();
            String device = (String) deviceSpinner.getSelectedItem();
            String api = (String) apiSpinner.getSelectedItem();
            String abi = (String) abiSpinner.getSelectedItem();
            
            if (name.isEmpty()) {
                Toast.makeText(this, "Please enter an AVD name", Toast.LENGTH_SHORT).show();
                return;
            }
            
            updateStatus("Creating AVD: " + name);
            emulatorManager.createAVD(name, device, api, abi, createCallback("Create AVD"));
        
        builder.setNegativeButton("Cancel", null);
        builder.show();
    }
    
    private void showStartAVDDialog() {
        // For simplicity, we'll ask for AVD name
        // In a full implementation, we'd show a list of available AVDs
        showSimpleInputDialog("Start Emulator", "Enter AVD name to start:", 
                            (name) -> {
                                updateStatus("Starting AVD: " + name);
                                emulatorManager.startAVD(name, createCallback("Start AVD"));
    }
    
    private void showStopAVDDialog() {
        showSimpleInputDialog("Stop Emulator", "Enter AVD name to stop:", 
                            (name) -> {
                                updateStatus("Stopping AVD: " + name);
                                emulatorManager.stopAVD(name, createCallback("Stop AVD"));
    }
    
    private void showDeleteAVDDialog() {
        showSimpleInputDialog("Delete AVD", "Enter AVD name to delete:", 
                            (name) -> {
                                updateStatus("Deleting AVD: " + name);
                                emulatorManager.deleteAVD(name, createCallback("Delete AVD"));
    }
    
    private void showSimpleInputDialog(String title, String message, SimpleInputCallback callback) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle(title);
        builder.setMessage(message);
        
        EditText input = new EditText(this);
        input.setInputType(InputType.TYPE_CLASS_TEXT);
        builder.setView(input);
        
        builder.setPositiveButton("OK", (dialog, which) -> {
            String value = input.getText().toString().trim();
            if (!value.isEmpty()) {
                callback.onInput(value);
            } else {
                Toast.makeText(this, "Please enter a value", Toast.LENGTH_SHORT).show();
            }
        
        builder.setNegativeButton("Cancel", null);
        builder.show();
    }
    
    private void showEmulatorStatus() {
        updateStatus("Checking emulator status...");
        emulatorManager.getStatus(createCallback("Emulator Status"));
    }
    
    private void showDeviceProfiles() {
        updateStatus("Loading device profiles...");
        emulatorManager.listDevices(createCallback("Device Profiles"));
    }
    
    private void showSystemImages() {
        updateStatus("Loading system images...");
        emulatorManager.listSystemImages(createCallback("System Images"));
    }
    
    private void showInstallImageDialog() {
        // Simple implementation - in practice, you'd want a more sophisticated UI
        showSimpleInputDialog("Install System Image", 
                            "Enter API level (default: 34):", 
                            (api) -> {
                                updateStatus("Installing system image for API " + api);
                                emulatorManager.installSystemImage(api, 
                                    EmulatorManager.EmulatorDefaults.DEFAULT_ABI,
                                    EmulatorManager.EmulatorDefaults.DEFAULT_TAG,
                                    createCallback("Install System Image"));
    }
    
    private void showResultDialog(String title, String message) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle(title);
        builder.setMessage(message);
        builder.setPositiveButton("OK", null);
        builder.show();
    }
    
    private interface SimpleInputCallback {
        void onInput(String input);
    }
}