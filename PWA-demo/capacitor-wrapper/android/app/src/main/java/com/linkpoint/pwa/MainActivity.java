package com.linkpoint.pwa;

import android.os.Bundle;
import com.getcapacitor.BridgeActivity;

/**
 * Main Activity for Linkpoint PWA
 * Registers all custom Capacitor plugins
 */
public class MainActivity extends BridgeActivity {
    
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        
        // Register custom plugins
        registerPlugin(EnhancedNotificationsPlugin.class);
        registerPlugin(FileSystemPlugin.class);
        registerPlugin(NetworkStatusPlugin.class);
        registerPlugin(DeviceInfoPlugin.class);
        registerPlugin(SecureStoragePlugin.class);
        registerPlugin(BackgroundSyncPlugin.class);
        registerPlugin(HapticsPlugin.class);
        registerPlugin(BadgePlugin.class);
    }
}
