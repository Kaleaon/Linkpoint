# Linkpoint Second Life Open Source Integration Troubleshooting Guide

## Overview

This guide provides comprehensive troubleshooting information for common issues when using Linkpoint with Second Life's open source ecosystem, including the main Second Life grid, OpenSimulator grids, and other virtual world platforms.

## Table of Contents

1. [Connection Issues](#connection-issues)
2. [Authentication Problems](#authentication-problems)
3. [Asset Loading Issues](#asset-loading-issues)
4. [Performance Problems](#performance-problems)
5. [OpenSimulator-Specific Issues](#opensimulator-specific-issues)
6. [Network and Firewall Issues](#network-and-firewall-issues)
7. [Mobile-Specific Problems](#mobile-specific-problems)
8. [Developer Tools and Debugging](#developer-tools-and-debugging)
9. [Common Error Messages](#common-error-messages)
10. [Getting Help](#getting-help)

## Connection Issues

### Cannot Connect to Second Life Grid

#### Symptoms
- "Login failed" error
- "Cannot reach login server" message
- Connection timeout errors
- SSL/TLS handshake failures

#### Diagnostics
```java
public class ConnectionDiagnostics {
    
    public void diagnoseConnectionProblem(String loginUri) {
        Log.i("Diagnostics", "Starting connection diagnosis for: " + loginUri);
        
        // Test 1: Basic network connectivity
        if (!isNetworkAvailable()) {
            Log.e("Diagnostics", "No network connection available");
            return;
        }
        
        // Test 2: DNS resolution
        try {
            URL url = new URL(loginUri);
            InetAddress address = InetAddress.getByName(url.getHost());
            Log.i("Diagnostics", "DNS resolved to: " + address.getHostAddress());
        } catch (Exception e) {
            Log.e("Diagnostics", "DNS resolution failed", e);
            return;
        }
        
        // Test 3: HTTP connectivity
        try {
            HttpURLConnection conn = (HttpURLConnection) new URL(loginUri).openConnection();
            conn.setRequestMethod("GET");
            conn.setConnectTimeout(10000);
            conn.setReadTimeout(10000);
            
            int responseCode = conn.getResponseCode();
            Log.i("Diagnostics", "HTTP response code: " + responseCode);
            
            if (responseCode != 200) {
                Log.w("Diagnostics", "Unexpected HTTP response: " + responseCode);
            }
            
        } catch (Exception e) {
            Log.e("Diagnostics", "HTTP connection failed", e);
        }
        
        // Test 4: SSL/TLS test
        if (loginUri.startsWith("https://")) {
            testSSLConnection(loginUri);
        }
        
        // Test 5: Login server specific test
        testLoginServerCompatibility(loginUri);
    }
    
    private void testSSLConnection(String httpsUrl) {
        try {
            SSLContext sslContext = SSLContext.getInstance("TLS");
            sslContext.init(null, null, null);
            
            URL url = new URL(httpsUrl);
            HttpsURLConnection conn = (HttpsURLConnection) url.openConnection();
            conn.setSSLSocketFactory(sslContext.getSocketFactory());
            conn.setRequestMethod("HEAD");
            conn.setConnectTimeout(10000);
            
            conn.connect();
            Log.i("Diagnostics", "SSL/TLS connection successful");
            
            Certificate[] certificates = conn.getServerCertificates();
            Log.i("Diagnostics", "Server certificate count: " + certificates.length);
            
        } catch (Exception e) {
            Log.e("Diagnostics", "SSL/TLS connection failed", e);
        }
    }
    
    private void testLoginServerCompatibility(String loginUri) {
        // Test if the server supports the expected login protocol
        try {
            // Try a minimal login request to check server response format
            String testRequest = createMinimalLoginRequest();
            
            HttpURLConnection conn = (HttpURLConnection) new URL(loginUri).openConnection();
            conn.setRequestMethod("POST");
            conn.setDoOutput(true);
            conn.setRequestProperty("Content-Type", "text/xml");
            
            try (OutputStream os = conn.getOutputStream()) {
                os.write(testRequest.getBytes());
            }
            
            int responseCode = conn.getResponseCode();
            Log.i("Diagnostics", "Login server response code: " + responseCode);
            
            // Read response to check format
            try (BufferedReader reader = new BufferedReader(
                    new InputStreamReader(conn.getInputStream()))) {
                String response = reader.lines().collect(Collectors.joining("\n"));
                
                if (response.contains("<?xml")) {
                    Log.i("Diagnostics", "Server returned XML response (expected)");
                } else {
                    Log.w("Diagnostics", "Server response format unexpected");
                }
            }
            
        } catch (Exception e) {
            Log.e("Diagnostics", "Login server compatibility test failed", e);
        }
    }
}
```

#### Solutions

1. **Check Network Connection**
   ```java
   // Verify network connectivity
   ConnectivityManager cm = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
   NetworkInfo activeNetwork = cm.getActiveNetworkInfo();
   boolean isConnected = activeNetwork != null && activeNetwork.isConnectedOrConnecting();
   ```

2. **Update Login URI**
   - Second Life Main Grid: `https://login.agni.lindenlab.com/cgi-bin/login.cgi`
   - Second Life Beta Grid: `https://login.aditi.lindenlab.com/cgi-bin/login.cgi`

3. **Certificate Issues**
   ```java
   // If certificate errors, update certificate store
   private void updateTrustStore() {
       try {
           CertificateFactory cf = CertificateFactory.getInstance("X.509");
           // Add custom certificates if needed
       } catch (Exception e) {
           Log.e("SSL", "Certificate update failed", e);
       }
   }
   ```

### Frequent Disconnections

#### Symptoms
- Connection drops every few minutes
- "Simulator disconnected" messages
- Timeouts during region crossings

#### Diagnostics
```java
public class DisconnectionDiagnostics {
    
    private long lastPacketTime = 0;
    private int missedHeartbeats = 0;
    private final AtomicLong packetsSent = new AtomicLong(0);
    private final AtomicLong packetsReceived = new AtomicLong(0);
    
    public void monitorConnectionStability() {
        Timer connectionMonitor = new Timer("ConnectionMonitor");
        connectionMonitor.scheduleAtFixedRate(new TimerTask() {
            @Override
            public void run() {
                checkConnectionHealth();
            }
        }, 0, 5000); // Check every 5 seconds
    }
    
    private void checkConnectionHealth() {
        long currentTime = System.currentTimeMillis();
        
        // Check if we've received packets recently
        if (currentTime - lastPacketTime > 30000) { // 30 seconds
            missedHeartbeats++;
            Log.w("Connection", "Missed heartbeat #" + missedHeartbeats);
            
            if (missedHeartbeats >= 3) {
                Log.e("Connection", "Connection appears to be dead, attempting reconnect");
                attemptReconnection();
            }
        } else {
            missedHeartbeats = 0; // Reset counter
        }
        
        // Log packet statistics
        Log.d("Connection", String.format("Packets - Sent: %d, Received: %d", 
            packetsSent.get(), packetsReceived.get()));
    }
    
    public void recordPacketReceived() {
        lastPacketTime = System.currentTimeMillis();
        packetsReceived.incrementAndGet();
    }
    
    public void recordPacketSent() {
        packetsSent.incrementAndGet();
    }
}
```

#### Solutions

1. **Increase Ping Frequency**
   ```java
   // Send ping packets more frequently
   public void sendPingPacket() {
       PingMessage ping = new PingMessage();
       ping.pingID = generatePingId();
       ping.oldestUnacked = getOldestUnacked();
       networkManager.sendMessage(ping);
   }
   ```

2. **Implement Connection Recovery**
   ```java
   public void attemptReconnection() {
       Log.i("Recovery", "Attempting connection recovery");
       
       // Close existing connections
       networkManager.closeConnections();
       
       // Wait briefly
       try { Thread.sleep(2000); } catch (InterruptedException e) {}
       
       // Re-establish connection
       networkManager.reconnectToCurrentRegion();
   }
   ```

3. **Mobile Network Optimization**
   ```java
   // Detect mobile network and adjust settings
   private void optimizeForMobileNetwork() {
       ConnectivityManager cm = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
       NetworkInfo networkInfo = cm.getActiveNetworkInfo();
       
       if (networkInfo != null && networkInfo.getType() == ConnectivityManager.TYPE_MOBILE) {
           // Reduce packet frequency for mobile networks
           setPacketThrottling(true);
           setHeartbeatInterval(10000); // 10 seconds instead of 5
           Log.i("Network", "Optimized for mobile network");
       }
   }
   ```

## Authentication Problems

### Login Failure Messages

#### "Authentication failed"
```java
public void handleAuthenticationFailure(LoginResponse response) {
    String reason = response.getReason();
    
    switch (reason) {
        case "key":
            showError("Invalid username or password. Please check your credentials.");
            break;
        case "presence":
            showError("You appear to be already logged in. Please wait a few minutes and try again.");
            break;
        case "disabled":
            showError("Your account has been disabled. Please contact support.");
            break;
        case "maintenance":
            showError("The grid is currently under maintenance. Please try again later.");
            break;
        default:
            showError("Login failed: " + reason);
            break;
    }
}
```

#### Account-Related Issues
```java
public class AccountDiagnostics {
    
    public void checkAccountStatus(String firstName, String lastName) {
        // Check if account exists (careful with privacy)
        String fullName = firstName + " " + lastName;
        
        // Basic validation
        if (firstName.length() < 2 || lastName.length() < 2) {
            Log.w("Account", "Username appears to be too short");
        }
        
        if (firstName.contains(" ") || lastName.contains(" ")) {
            Log.w("Account", "Username should not contain spaces");
        }
        
        // Check for special characters that might cause issues
        if (!firstName.matches("[a-zA-Z]+") || !lastName.matches("[a-zA-Z]+")) {
            Log.w("Account", "Username contains non-alphabetic characters");
        }
    }
    
    public void checkPasswordRequirements(String password) {
        if (password.length() < 6) {
            Log.w("Password", "Password may be too short");
        }
        
        if (password.length() > 16) {
            Log.w("Password", "Password may be too long for some grids");
        }
        
        // Check for special characters that might need encoding
        if (password.matches(".*[<>&\"'].*")) {
            Log.w("Password", "Password contains characters that may need special handling");
        }
    }
}
```

### Two-Factor Authentication Issues

#### Handling TOTP Authentication
```java
public class TOTPHandler {
    
    public String generateTOTP(String secret) {
        // Implementation for TOTP generation if Second Life supports it
        try {
            byte[] key = Base32.decode(secret);
            long timeStep = System.currentTimeMillis() / 30000; // 30-second intervals
            
            Mac hmac = Mac.getInstance("HmacSHA1");
            SecretKeySpec secretKey = new SecretKeySpec(key, "HmacSHA1");
            hmac.init(secretKey);
            
            byte[] hash = hmac.doFinal(ByteBuffer.allocate(8).putLong(timeStep).array());
            
            int offset = hash[hash.length - 1] & 0x0f;
            int code = (hash[offset] & 0x7f) << 24 |
                      (hash[offset + 1] & 0xff) << 16 |
                      (hash[offset + 2] & 0xff) << 8 |
                      (hash[offset + 3] & 0xff);
            
            return String.format("%06d", code % 1000000);
            
        } catch (Exception e) {
            Log.e("TOTP", "Failed to generate TOTP", e);
            return null;
        }
    }
}
```

## Asset Loading Issues

### Texture Loading Problems

#### Symptoms
- White or grey textures
- Slow texture loading
- Texture loading failures

#### Diagnostics
```java
public class TextureDiagnostics {
    
    public void diagnoseTextureLoading(UUID textureId) {
        Log.i("TextureDiag", "Diagnosing texture: " + textureId);
        
        // Check if texture exists in cache
        if (textureCache.contains(textureId)) {
            Log.i("TextureDiag", "Texture found in cache");
            
            TextureCacheEntry entry = textureCache.get(textureId);
            if (entry.isCorrupted()) {
                Log.w("TextureDiag", "Cached texture is corrupted");
                textureCache.remove(textureId);
            } else if (entry.isExpired()) {
                Log.w("TextureDiag", "Cached texture is expired");
            } else {
                Log.i("TextureDiag", "Cached texture is valid");
                return;
            }
        }
        
        // Check network availability for texture download
        if (!isNetworkAvailable()) {
            Log.e("TextureDiag", "No network available for texture download");
            return;
        }
        
        // Test asset server connectivity
        String assetServerUrl = getCurrentGrid().getAssetServerUrl();
        if (assetServerUrl != null) {
            testAssetServerConnectivity(assetServerUrl, textureId);
        } else {
            Log.w("TextureDiag", "No asset server URL configured");
        }
        
        // Check texture format support
        AssetType assetType = determineAssetType(textureId);
        if (!isAssetTypeSupported(assetType)) {
            Log.e("TextureDiag", "Unsupported texture format: " + assetType);
        }
    }
    
    private void testAssetServerConnectivity(String assetServerUrl, UUID textureId) {
        try {
            String testUrl = assetServerUrl + "/assets/" + textureId;
            
            HttpURLConnection conn = (HttpURLConnection) new URL(testUrl).openConnection();
            conn.setRequestMethod("HEAD");
            conn.setConnectTimeout(5000);
            
            int responseCode = conn.getResponseCode();
            
            switch (responseCode) {
                case 200:
                    Log.i("TextureDiag", "Asset server reports texture available");
                    break;
                case 404:
                    Log.w("TextureDiag", "Texture not found on asset server");
                    break;
                case 403:
                    Log.w("TextureDiag", "Access denied to texture");
                    break;
                default:
                    Log.w("TextureDiag", "Asset server returned: " + responseCode);
                    break;
            }
            
        } catch (Exception e) {
            Log.e("TextureDiag", "Asset server connectivity test failed", e);
        }
    }
}
```

#### Solutions

1. **Clear Texture Cache**
   ```java
   public void clearTextureCache() {
       textureCache.clear();
       Log.i("Cache", "Texture cache cleared");
   }
   ```

2. **Increase Download Timeout**
   ```java
   // Increase timeout for slow connections
   assetDownloader.setTimeout(30000); // 30 seconds
   ```

3. **Alternative Asset Servers**
   ```java
   // Try alternative asset servers for robustness
   private final List<String> assetServerUrls = Arrays.asList(
       "https://asset.secondlife.com/",
       "https://asset-cdn.secondlife.com/",
       "http://asset.lindenlab.com/"
   );
   
   public Asset downloadAssetWithFallback(UUID assetId) {
       for (String serverUrl : assetServerUrls) {
           try {
               return downloadAsset(serverUrl, assetId);
           } catch (Exception e) {
               Log.w("Asset", "Failed to download from " + serverUrl, e);
           }
       }
       throw new AssetNotFoundException("Asset not available from any server");
   }
   ```

### Mesh Loading Problems

#### Diagnostics for Mesh Assets
```java
public class MeshDiagnostics {
    
    public void diagnoseMeshLoading(UUID meshId) {
        Log.i("MeshDiag", "Diagnosing mesh: " + meshId);
        
        try {
            // Download mesh data
            Asset meshAsset = assetManager.downloadAsset(meshId, AssetType.MESH);
            
            if (meshAsset == null) {
                Log.e("MeshDiag", "Mesh asset could not be downloaded");
                return;
            }
            
            // Check mesh data format
            byte[] meshData = meshAsset.getData();
            if (meshData.length == 0) {
                Log.e("MeshDiag", "Mesh asset data is empty");
                return;
            }
            
            // Try to parse mesh header
            if (!isValidMeshFormat(meshData)) {
                Log.e("MeshDiag", "Mesh data format is invalid");
                return;
            }
            
            // Check mesh complexity
            MeshInfo meshInfo = analyzeMeshComplexity(meshData);
            Log.i("MeshDiag", String.format("Mesh info - Vertices: %d, Faces: %d, LODs: %d",
                meshInfo.getVertexCount(), meshInfo.getFaceCount(), meshInfo.getLODCount()));
            
            if (meshInfo.getVertexCount() > MAX_VERTICES) {
                Log.w("MeshDiag", "Mesh has too many vertices for mobile rendering");
            }
            
        } catch (Exception e) {
            Log.e("MeshDiag", "Mesh diagnosis failed", e);
        }
    }
    
    private boolean isValidMeshFormat(byte[] data) {
        // Check for LLSD mesh format markers
        String header = new String(data, 0, Math.min(100, data.length));
        return header.contains("llsd") || data[0] == 0x3C; // XML start
    }
}
```

## Performance Problems

### Low Frame Rate Issues

#### Frame Rate Monitoring
```java
public class FrameRateMonitor {
    private long lastFrameTime = 0;
    private int frameCount = 0;
    private float averageFPS = 0;
    private final Queue<Long> frameTimes = new LinkedList<>();
    
    public void recordFrame() {
        long currentTime = System.currentTimeMillis();
        
        if (lastFrameTime != 0) {
            long frameTime = currentTime - lastFrameTime;
            frameTimes.offer(frameTime);
            
            // Keep only last 60 frame times
            while (frameTimes.size() > 60) {
                frameTimes.poll();
            }
            
            // Calculate average FPS
            if (!frameTimes.isEmpty()) {
                long totalTime = frameTimes.stream().mapToLong(Long::longValue).sum();
                averageFPS = 1000.0f / (totalTime / (float) frameTimes.size());
            }
        }
        
        lastFrameTime = currentTime;
        frameCount++;
        
        // Log performance warnings
        if (frameCount % 300 == 0) { // Every 5 seconds at 60 FPS
            if (averageFPS < 20) {
                Log.w("Performance", String.format("Low FPS detected: %.1f", averageFPS));
                suggestPerformanceImprovements();
            }
        }
    }
    
    private void suggestPerformanceImprovements() {
        Log.i("Performance", "Suggesting performance improvements:");
        
        // Check rendering settings
        if (renderingSettings.getShadowQuality() > QualityLevel.LOW) {
            Log.i("Performance", "- Reduce shadow quality");
        }
        
        if (renderingSettings.getTextureQuality() > QualityLevel.MEDIUM) {
            Log.i("Performance", "- Reduce texture quality");
        }
        
        if (renderingSettings.getDrawDistance() > 128) {
            Log.i("Performance", "- Reduce draw distance");
        }
        
        // Check memory usage
        Runtime runtime = Runtime.getRuntime();
        long usedMemory = runtime.totalMemory() - runtime.freeMemory();
        long maxMemory = runtime.maxMemory();
        
        if (usedMemory > maxMemory * 0.8) {
            Log.i("Performance", "- High memory usage detected, consider clearing caches");
        }
    }
}
```

### Memory Issues

#### Memory Leak Detection
```java
public class MemoryDiagnostics {
    private final Map<String, Integer> objectCounts = new HashMap<>();
    
    public void recordObjectCreation(String objectType) {
        objectCounts.merge(objectType, 1, Integer::sum);
    }
    
    public void recordObjectDestruction(String objectType) {
        objectCounts.merge(objectType, -1, Integer::sum);
    }
    
    public void checkForLeaks() {
        Log.i("Memory", "Object count summary:");
        
        for (Map.Entry<String, Integer> entry : objectCounts.entrySet()) {
            int count = entry.getValue();
            if (count > 1000) {
                Log.w("Memory", String.format("Potential leak - %s: %d objects", 
                    entry.getKey(), count));
            } else {
                Log.d("Memory", String.format("%s: %d objects", entry.getKey(), count));
            }
        }
        
        // Check memory usage
        Runtime runtime = Runtime.getRuntime();
        long totalMemory = runtime.totalMemory();
        long freeMemory = runtime.freeMemory();
        long maxMemory = runtime.maxMemory();
        long usedMemory = totalMemory - freeMemory;
        
        Log.i("Memory", String.format("Memory usage: %d MB / %d MB (%.1f%%)", 
            usedMemory / 1024 / 1024, 
            maxMemory / 1024 / 1024,
            (usedMemory * 100.0) / maxMemory));
        
        if (usedMemory > maxMemory * 0.9) {
            Log.e("Memory", "Critical memory usage detected!");
            suggestMemoryCleanup();
        }
    }
    
    private void suggestMemoryCleanup() {
        Log.i("Memory", "Suggesting memory cleanup actions:");
        Log.i("Memory", "- Clear texture cache");
        Log.i("Memory", "- Clear mesh cache");
        Log.i("Memory", "- Reduce draw distance");
        Log.i("Memory", "- Lower texture quality");
        Log.i("Memory", "- Force garbage collection");
        
        // Perform automatic cleanup
        performEmergencyCleanup();
    }
    
    private void performEmergencyCleanup() {
        // Clear caches
        textureCache.clear();
        meshCache.clear();
        animationCache.clear();
        
        // Force garbage collection
        System.gc();
        
        Log.i("Memory", "Emergency cleanup performed");
    }
}
```

## OpenSimulator-Specific Issues

### Variable Region Size Problems

#### Region Size Detection
```java
public class RegionSizeDiagnostics {
    
    public void diagnoseRegionSize() {
        RegionInfo currentRegion = getCurrentRegion();
        
        if (currentRegion == null) {
            Log.e("Region", "No current region information available");
            return;
        }
        
        int sizeX = currentRegion.getSizeX();
        int sizeY = currentRegion.getSizeY();
        
        Log.i("Region", String.format("Region size: %dx%d", sizeX, sizeY));
        
        // Check if size is non-standard
        if (sizeX != 256 || sizeY != 256) {
            Log.i("Region", "Non-standard region size detected (OpenSim)");
            
            // Check if we handle this size properly
            if (!isRegionSizeSupported(sizeX, sizeY)) {
                Log.e("Region", "Unsupported region size");
                return;
            }
            
            // Verify terrain patch count
            int expectedPatchesX = sizeX / 16;
            int expectedPatchesY = sizeY / 16;
            int actualPatches = terrainManager.getPatchCount();
            
            if (actualPatches != expectedPatchesX * expectedPatchesY) {
                Log.w("Region", String.format("Terrain patch count mismatch. Expected: %d, Actual: %d", 
                    expectedPatchesX * expectedPatchesY, actualPatches));
            }
            
            // Check camera bounds
            checkCameraBounds(sizeX, sizeY);
        }
    }
    
    private void checkCameraBounds(int sizeX, int sizeY) {
        float maxDistance = Math.max(sizeX, sizeY);
        float currentFarPlane = camera.getFarPlane();
        
        if (currentFarPlane < maxDistance) {
            Log.w("Camera", String.format("Camera far plane (%f) may be too small for region size (%dx%d)", 
                currentFarPlane, sizeX, sizeY));
        }
    }
}
```

### Physics Engine Compatibility

#### Physics Engine Detection
```java
public class PhysicsEngineDiagnostics {
    
    public void diagnosePhysicsEngine() {
        String physicsEngine = getCurrentRegion().getPhysicsEngine();
        
        Log.i("Physics", "Physics engine: " + physicsEngine);
        
        switch (physicsEngine.toLowerCase()) {
            case "bulletphysics":
            case "bulletsim":
                Log.i("Physics", "BulletSim physics detected - advanced features available");
                checkBulletSimFeatures();
                break;
                
            case "ode":
                Log.i("Physics", "ODE physics detected - standard features");
                checkODEFeatures();
                break;
                
            case "ubode":
                Log.i("Physics", "ubODE physics detected - enhanced ODE");
                checkUbODEFeatures();
                break;
                
            default:
                Log.w("Physics", "Unknown physics engine: " + physicsEngine);
                break;
        }
    }
    
    private void checkBulletSimFeatures() {
        // Test BulletSim-specific features
        if (supportsPhysicsMaterials()) {
            Log.i("Physics", "Physics materials supported");
        }
        
        if (supportsVehiclePhysics()) {
            Log.i("Physics", "Vehicle physics supported");
        }
        
        if (supportsConstraints()) {
            Log.i("Physics", "Physics constraints supported");
        }
    }
    
    private void checkODEFeatures() {
        // Test ODE features
        Log.i("Physics", "Basic physics features available");
        
        if (!supportsPhysicsMaterials()) {
            Log.i("Physics", "Physics materials not available with ODE");
        }
    }
}
```

### Hypergrid Connectivity Issues

#### Hypergrid Diagnostics
```java
public class HypergridDiagnostics {
    
    public void diagnoseHypergridConnectivity(String hgAddress) {
        Log.i("Hypergrid", "Diagnosing hypergrid address: " + hgAddress);
        
        try {
            // Parse hypergrid address
            HypergridAddress parsed = HypergridAddress.parse(hgAddress);
            Log.i("Hypergrid", String.format("Parsed address - Grid: %s, Region: %s", 
                parsed.getGridUri(), parsed.getRegionName()));
            
            // Test destination grid connectivity
            String gridInfoUrl = parsed.getGridUri() + "/get_grid_info";
            
            HttpURLConnection conn = (HttpURLConnection) new URL(gridInfoUrl).openConnection();
            conn.setRequestMethod("GET");
            conn.setConnectTimeout(10000);
            
            int responseCode = conn.getResponseCode();
            
            if (responseCode == 200) {
                Log.i("Hypergrid", "Destination grid is reachable");
                
                // Parse grid info
                try (BufferedReader reader = new BufferedReader(
                        new InputStreamReader(conn.getInputStream()))) {
                    String response = reader.lines().collect(Collectors.joining("\n"));
                    
                    // Check if hypergrid is enabled
                    if (response.contains("hypergrid") || response.contains("HypergridEnabled")) {
                        Log.i("Hypergrid", "Destination supports hypergrid");
                    } else {
                        Log.w("Hypergrid", "Destination may not support hypergrid");
                    }
                }
                
            } else {
                Log.e("Hypergrid", "Destination grid not reachable: " + responseCode);
            }
            
        } catch (Exception e) {
            Log.e("Hypergrid", "Hypergrid connectivity test failed", e);
        }
    }
    
    public void checkCurrentGridHypergridSupport() {
        GridInfo currentGrid = getCurrentGrid();
        
        if (currentGrid.getBoolean("HypergridEnabled", false)) {
            Log.i("Hypergrid", "Current grid supports hypergrid");
            
            String hgDirectory = currentGrid.getString("HypergridDirectory");
            if (hgDirectory != null) {
                Log.i("Hypergrid", "Hypergrid directory available: " + hgDirectory);
            }
            
        } else {
            Log.i("Hypergrid", "Current grid does not support hypergrid");
        }
    }
}
```

## Network and Firewall Issues

### Port and Protocol Issues

#### Network Configuration Check
```java
public class NetworkConfigurationCheck {
    
    public void checkNetworkConfiguration() {
        Log.i("Network", "Checking network configuration");
        
        // Check required ports
        int[] requiredPorts = {80, 443, 8002, 8003, 9000, 9001}; // Common SL/OpenSim ports
        
        for (int port : requiredPorts) {
            if (isPortBlocked(port)) {
                Log.w("Network", "Port " + port + " appears to be blocked");
            } else {
                Log.d("Network", "Port " + port + " is accessible");
            }
        }
        
        // Check UDP connectivity
        checkUDPConnectivity();
        
        // Check HTTP/HTTPS connectivity
        checkHTTPConnectivity();
        
        // Check for proxy settings
        checkProxyConfiguration();
    }
    
    private boolean isPortBlocked(int port) {
        try {
            Socket socket = new Socket();
            socket.connect(new InetSocketAddress("google.com", port), 3000);
            socket.close();
            return false;
        } catch (IOException e) {
            return true;
        }
    }
    
    private void checkUDPConnectivity() {
        try {
            DatagramSocket socket = new DatagramSocket();
            
            // Try to send a test UDP packet
            byte[] testData = "test".getBytes();
            InetAddress address = InetAddress.getByName("8.8.8.8");
            DatagramPacket packet = new DatagramPacket(testData, testData.length, address, 53);
            
            socket.send(packet);
            socket.close();
            
            Log.i("Network", "UDP connectivity test passed");
            
        } catch (Exception e) {
            Log.w("Network", "UDP connectivity test failed", e);
        }
    }
    
    private void checkProxyConfiguration() {
        String proxyHost = System.getProperty("http.proxyHost");
        String proxyPort = System.getProperty("http.proxyPort");
        
        if (proxyHost != null) {
            Log.i("Network", String.format("Proxy detected: %s:%s", proxyHost, proxyPort));
            
            // Test proxy connectivity
            try {
                Proxy proxy = new Proxy(Proxy.Type.HTTP, 
                    new InetSocketAddress(proxyHost, Integer.parseInt(proxyPort)));
                
                URLConnection conn = new URL("http://www.google.com").openConnection(proxy);
                conn.setConnectTimeout(5000);
                conn.connect();
                
                Log.i("Network", "Proxy connection successful");
                
            } catch (Exception e) {
                Log.w("Network", "Proxy connection failed", e);
            }
        } else {
            Log.d("Network", "No proxy detected");
        }
    }
}
```

### Firewall Configuration

#### Corporate/School Firewall Issues
```java
public class FirewallDiagnostics {
    
    public void checkForFirewallIssues() {
        Log.i("Firewall", "Checking for firewall issues");
        
        // Test different connection methods
        testDirectConnection();
        testHTTPSOnlyConnection();
        testAlternativePorts();
    }
    
    private void testDirectConnection() {
        try {
            String testUrl = "http://login.agni.lindenlab.com/cgi-bin/login.cgi";
            URL url = new URL(testUrl);
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setConnectTimeout(5000);
            conn.setRequestMethod("HEAD");
            
            int responseCode = conn.getResponseCode();
            Log.i("Firewall", "Direct connection test result: " + responseCode);
            
        } catch (Exception e) {
            Log.w("Firewall", "Direct connection blocked", e);
        }
    }
    
    private void testHTTPSOnlyConnection() {
        try {
            String testUrl = "https://secondlife.com/";
            URL url = new URL(testUrl);
            HttpsURLConnection conn = (HttpsURLConnection) url.openConnection();
            conn.setConnectTimeout(5000);
            conn.setRequestMethod("HEAD");
            
            int responseCode = conn.getResponseCode();
            Log.i("Firewall", "HTTPS-only connection test result: " + responseCode);
            
        } catch (Exception e) {
            Log.w("Firewall", "HTTPS connection issues", e);
        }
    }
    
    private void testAlternativePorts() {
        // Test common alternative ports
        int[] alternativePorts = {8080, 8443, 3128};
        
        for (int port : alternativePorts) {
            try {
                Socket socket = new Socket();
                socket.connect(new InetSocketAddress("www.google.com", port), 3000);
                socket.close();
                Log.i("Firewall", "Alternative port " + port + " is accessible");
            } catch (IOException e) {
                Log.d("Firewall", "Alternative port " + port + " is blocked");
            }
        }
    }
}
```

## Mobile-Specific Problems

### Android Permissions

#### Permission Diagnostics
```java
public class PermissionDiagnostics {
    
    public void checkRequiredPermissions(Context context) {
        Log.i("Permissions", "Checking required permissions");
        
        // Network permissions
        if (checkPermission(context, Manifest.permission.INTERNET)) {
            Log.i("Permissions", "INTERNET permission granted");
        } else {
            Log.e("Permissions", "INTERNET permission missing");
        }
        
        if (checkPermission(context, Manifest.permission.ACCESS_NETWORK_STATE)) {
            Log.i("Permissions", "ACCESS_NETWORK_STATE permission granted");
        } else {
            Log.w("Permissions", "ACCESS_NETWORK_STATE permission missing");
        }
        
        // Storage permissions (for caching)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (checkPermission(context, Manifest.permission.READ_EXTERNAL_STORAGE)) {
                Log.i("Permissions", "READ_EXTERNAL_STORAGE permission granted");
            } else {
                Log.w("Permissions", "READ_EXTERNAL_STORAGE permission missing - cache limited");
            }
        }
        
        // Optional permissions
        checkOptionalPermissions(context);
    }
    
    private void checkOptionalPermissions(Context context) {
        // Audio permissions (for voice)
        if (checkPermission(context, Manifest.permission.RECORD_AUDIO)) {
            Log.i("Permissions", "RECORD_AUDIO permission granted - voice enabled");
        } else {
            Log.i("Permissions", "RECORD_AUDIO permission not granted - voice disabled");
        }
        
        // Location permissions (for region detection)
        if (checkPermission(context, Manifest.permission.ACCESS_FINE_LOCATION)) {
            Log.i("Permissions", "LOCATION permission granted");
        } else {
            Log.i("Permissions", "LOCATION permission not granted");
        }
    }
    
    private boolean checkPermission(Context context, String permission) {
        return ContextCompat.checkSelfPermission(context, permission) == 
               PackageManager.PERMISSION_GRANTED;
    }
}
```

### Battery Optimization Issues

#### Battery Optimization Detection
```java
public class BatteryOptimizationDiagnostics {
    
    public void checkBatteryOptimization(Context context) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            PowerManager powerManager = (PowerManager) context.getSystemService(Context.POWER_SERVICE);
            String packageName = context.getPackageName();
            
            if (powerManager.isIgnoringBatteryOptimizations(packageName)) {
                Log.i("Battery", "App is excluded from battery optimization");
            } else {
                Log.w("Battery", "App is subject to battery optimization - may affect performance");
                suggestBatteryOptimizationExclusion();
            }
            
            // Check doze mode
            if (powerManager.isDeviceIdleMode()) {
                Log.w("Battery", "Device is in doze mode - network activity may be limited");
            }
        }
    }
    
    private void suggestBatteryOptimizationExclusion() {
        Log.i("Battery", "To improve performance, consider excluding Linkpoint from battery optimization:");
        Log.i("Battery", "Settings > Battery > Battery Optimization > Linkpoint > Don't optimize");
    }
}
```

## Developer Tools and Debugging

### Debug Logging

#### Comprehensive Logging System
```java
public class DebugLoggingSystem {
    private static final String TAG = "LinkpointDebug";
    private boolean debugEnabled = BuildConfig.DEBUG;
    private final Set<String> enabledCategories = new HashSet<>();
    
    public void enableDebugCategory(String category) {
        enabledCategories.add(category);
        Log.i(TAG, "Debug logging enabled for: " + category);
    }
    
    public void logNetworkPacket(String direction, SLMessage message) {
        if (isDebugEnabled("Network")) {
            Log.d(TAG, String.format("[Network] %s: %s (Size: %d bytes)", 
                direction, message.getType(), message.getSerializedSize()));
        }
    }
    
    public void logAssetDownload(UUID assetId, AssetType type, boolean success) {
        if (isDebugEnabled("Assets")) {
            Log.d(TAG, String.format("[Assets] Download %s: %s (%s)", 
                success ? "SUCCESS" : "FAILED", assetId, type));
        }
    }
    
    public void logPerformanceMetric(String metric, long value) {
        if (isDebugEnabled("Performance")) {
            Log.d(TAG, String.format("[Performance] %s: %d", metric, value));
        }
    }
    
    public void logObjectUpdate(long localId, String updateType) {
        if (isDebugEnabled("Objects")) {
            Log.d(TAG, String.format("[Objects] Update: %d (%s)", localId, updateType));
        }
    }
    
    private boolean isDebugEnabled(String category) {
        return debugEnabled && enabledCategories.contains(category);
    }
}
```

### Network Packet Analyzer

#### Packet Analysis Tool
```java
public class PacketAnalyzer {
    private final Map<MessageType, AtomicLong> messageStats = new ConcurrentHashMap<>();
    private final Queue<PacketInfo> recentPackets = new ConcurrentLinkedQueue<>();
    private static final int MAX_RECENT_PACKETS = 100;
    
    public void analyzePacket(byte[] packetData, String direction) {
        try {
            Packet packet = packetDecoder.decode(packetData);
            
            PacketInfo info = new PacketInfo(
                System.currentTimeMillis(),
                direction,
                packet.getType(),
                packetData.length,
                packet.isReliable()
            );
            
            recentPackets.offer(info);
            while (recentPackets.size() > MAX_RECENT_PACKETS) {
                recentPackets.poll();
            }
            
            // Update statistics
            for (SLMessage message : packet.getMessages()) {
                messageStats.computeIfAbsent(message.getType(), k -> new AtomicLong(0))
                           .incrementAndGet();
            }
            
        } catch (Exception e) {
            Log.e("PacketAnalyzer", "Failed to analyze packet", e);
        }
    }
    
    public void printStatistics() {
        Log.i("PacketAnalyzer", "=== Packet Statistics ===");
        
        messageStats.entrySet().stream()
            .sorted(Map.Entry.<MessageType, AtomicLong>comparingByValue(
                Comparator.comparing(AtomicLong::get)).reversed())
            .limit(20)
            .forEach(entry -> 
                Log.i("PacketAnalyzer", String.format("%s: %d", 
                    entry.getKey(), entry.getValue().get())));
        
        Log.i("PacketAnalyzer", "=== Recent Packets ===");
        recentPackets.stream()
            .limit(10)
            .forEach(info -> 
                Log.i("PacketAnalyzer", String.format("%s %s: %d bytes %s", 
                    info.getDirection(), info.getType(), info.getSize(),
                    info.isReliable() ? "(reliable)" : "")));
    }
    
    private static class PacketInfo {
        private final long timestamp;
        private final String direction;
        private final MessageType type;
        private final int size;
        private final boolean reliable;
        
        // Constructor and getters...
    }
}
```

## Common Error Messages

### Error Message Reference

| Error Message | Likely Cause | Solution |
|---------------|--------------|----------|
| "Login failed: key" | Wrong username/password | Check credentials |
| "Login failed: presence" | Already logged in elsewhere | Wait 5 minutes, try again |
| "Cannot connect to simulator" | Network/firewall issue | Check network connectivity |
| "Asset download timeout" | Slow connection or server issue | Increase timeout, try again |
| "Region crossing failed" | Server lag or connectivity | Try teleporting instead |
| "Inventory fetch failed" | Server overload | Try logging out and back in |
| "Voice initialization failed" | Missing permissions or hardware | Check microphone permissions |
| "Physics update error" | OpenSim physics engine issue | Contact grid administrator |

### Automated Error Reporting

```java
public class AutomatedErrorReporting {
    
    public void handleException(Exception e, String context) {
        // Log the error
        Log.e("ErrorReporting", String.format("Error in %s: %s", context, e.getMessage()), e);
        
        // Collect diagnostic information
        Map<String, String> diagnosticInfo = collectDiagnosticInfo();
        
        // Create error report
        ErrorReport report = new ErrorReport(
            e.getClass().getSimpleName(),
            e.getMessage(),
            getStackTraceString(e),
            context,
            diagnosticInfo
        );
        
        // Store for later transmission (respect privacy)
        errorReportQueue.offer(report);
        
        // Attempt automatic recovery if possible
        attemptRecovery(e, context);
    }
    
    private Map<String, String> collectDiagnosticInfo() {
        Map<String, String> info = new HashMap<>();
        
        info.put("AppVersion", getAppVersion());
        info.put("AndroidVersion", Build.VERSION.RELEASE);
        info.put("DeviceModel", Build.MODEL);
        info.put("NetworkType", getNetworkType());
        info.put("MemoryUsage", getMemoryUsage());
        info.put("CurrentGrid", getCurrentGridName());
        info.put("ConnectionState", getConnectionState());
        
        return info;
    }
    
    private void attemptRecovery(Exception e, String context) {
        if (e instanceof NetworkException) {
            Log.i("Recovery", "Attempting network recovery");
            networkManager.attemptReconnection();
        } else if (e instanceof OutOfMemoryError) {
            Log.i("Recovery", "Attempting memory recovery");
            performEmergencyMemoryCleanup();
        } else if (e instanceof AssetException) {
            Log.i("Recovery", "Clearing asset cache for recovery");
            assetManager.clearCache();
        }
    }
}
```

## Getting Help

### Community Resources

1. **Linkpoint GitHub Repository**
   - Submit issues: https://github.com/Kaleaon/Linkpoint/issues
   - Wiki: https://github.com/Kaleaon/Linkpoint/wiki
   - Discussions: https://github.com/Kaleaon/Linkpoint/discussions

2. **Second Life Developer Community**
   - SL Developer Forums: https://community.secondlife.com/forums/forum/15-lsl-scripting/
   - LibreMetaverse: https://github.com/cinderblocks/libremetaverse

3. **OpenSimulator Community**
   - OpenSimulator Website: http://opensimulator.org/
   - OpenSimulator Forums: http://opensimulator.org/viewforum.php?f=1
   - OpenSim Discord: https://discord.com/invite/opensim

### Creating Effective Bug Reports

```java
public class BugReportGenerator {
    
    public String generateBugReport(Exception e, String userDescription) {
        StringBuilder report = new StringBuilder();
        
        report.append("# Linkpoint Bug Report\n\n");
        report.append("## User Description\n");
        report.append(userDescription).append("\n\n");
        
        report.append("## Error Details\n");
        report.append("**Error Type:** ").append(e.getClass().getSimpleName()).append("\n");
        report.append("**Error Message:** ").append(e.getMessage()).append("\n\n");
        
        report.append("## Stack Trace\n");
        report.append("```\n");
        report.append(getStackTraceString(e));
        report.append("\n```\n\n");
        
        report.append("## System Information\n");
        report.append("**App Version:** ").append(getAppVersion()).append("\n");
        report.append("**Android Version:** ").append(Build.VERSION.RELEASE).append("\n");
        report.append("**Device:** ").append(Build.MANUFACTURER).append(" ").append(Build.MODEL).append("\n");
        report.append("**Network:** ").append(getNetworkType()).append("\n");
        report.append("**Grid:** ").append(getCurrentGridName()).append("\n\n");
        
        report.append("## Steps to Reproduce\n");
        report.append("1. \n");
        report.append("2. \n");
        report.append("3. \n\n");
        
        report.append("## Expected Behavior\n\n");
        report.append("## Actual Behavior\n\n");
        
        return report.toString();
    }
}
```

---

This comprehensive troubleshooting guide covers the most common issues encountered when using Linkpoint with the Second Life open source ecosystem. For issues not covered here, please consult the community resources or create a detailed bug report using the provided template.