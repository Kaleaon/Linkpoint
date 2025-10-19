/*
 * Second Life Asset Processing Demo - Demonstration of asset stream processing
 *
 * Based on Second Life viewer implementation
 * Copyright (C) 2010, Linden Research, Inc.
 * Java conversion Copyright (C) 2024
 */

package lindenlab.llsd.viewer.secondlife.demo;

import lindenlab.llsd.LLSDException;
import lindenlab.llsd.viewer.secondlife.SecondLifeLLSDUtils;
import lindenlab.llsd.viewer.secondlife.assets.*;
import lindenlab.llsd.viewer.secondlife.engine.*;

import java.util.*;

/**
 * A demonstration application that showcases the various asset processing
 * capabilities of the Second Life LLSD extensions.
 * <p>
 * This class runs a series of demonstrations for:
 * <ul>
 *   <li>Texture processing (detection, metadata extraction).</li>
 *   <li>Sound processing.</li>
 *   <li>Generic data stream processing (compression, chunking).</li>
 *   <li>The visual engine (vector math, scene graph).</li>
 *   <li>Cache statistics.</li>
 * </ul>
 * It is intended to be run as a standalone application to verify and
 * illustrate the functionality of the asset processing utilities.
 */
public class SLAssetDemo {

    /**
     * The main entry point for the demonstration application.
     *
     * @param args Command-line arguments (not used).
     */
    public static void main(String[] args) {
        System.out.println("=== Second Life Asset Processing Demo ===\n");
        
        try {
            // Demo 1: Texture Processing
            demoTextureProcessing();
            
            // Demo 2: Sound Processing  
            demoSoundProcessing();
            
            // Demo 3: Data Stream Processing
            demoDataStreamProcessing();
            
            // Demo 4: Visual Engine
            demoVisualEngine();
            
            // Demo 5: Cache Statistics
            demoCacheStatistics();
            
        } catch (Exception e) {
            System.err.println("Demo error: " + e.getMessage());
            e.printStackTrace();
        }
    }
    
    /**
     * Demonstrates the processing of a sample texture asset.
     * It shows format detection, metadata extraction, and LLSD stream creation.
     * @throws LLSDException if texture processing fails.
     */
    private static void demoTextureProcessing() throws LLSDException {
        System.out.println("1. Texture Processing Demo");
        System.out.println("--------------------------");
        
        // Create sample texture data (simulating J2C format)
        byte[] textureData = createSampleJ2CTexture();
        UUID textureId = UUID.randomUUID();
        
        // Process texture
        SLTextureProcessor.TextureFormat format = SLTextureProcessor.detectTextureFormat(textureData);
        System.out.println("Detected format: " + format);
        
        SLTextureProcessor.TextureInfo info = SLTextureProcessor.processTexture(
            textureId, textureData, format);
        
        System.out.println("Texture ID: " + info.getTextureId());
        System.out.println("Dimensions: " + info.getWidth() + "x" + info.getHeight());
        System.out.println("Levels: " + info.getLevels());
        System.out.println("Has Alpha: " + info.hasAlpha());
        System.out.println("Valid for SL: " + info.isValid());
        
        // Create LLSD texture stream
        Map<String, Object> textureStream = SecondLifeLLSDUtils.createTextureStream(
            textureId, textureData, format);
        
        System.out.println("LLSD Stream Keys: " + textureStream.keySet());
        System.out.println();
    }
    
    /**
     * Demonstrates the processing of a sample sound asset.
     * It shows format detection, metadata extraction, and validation.
     * @throws LLSDException if sound processing fails.
     */
    private static void demoSoundProcessing() throws LLSDException {
        System.out.println("2. Sound Processing Demo");
        System.out.println("------------------------");
        
        // Create sample WAV data
        byte[] soundData = createSampleWAVAudio();
        UUID soundId = UUID.randomUUID();
        
        // Process sound
        SLSoundProcessor.AudioFormat format = SLSoundProcessor.detectAudioFormat(soundData);
        System.out.println("Detected format: " + format);
        
        SLSoundProcessor.AudioInfo info = SLSoundProcessor.processSound(
            soundId, soundData, format);
        
        System.out.println("Sound ID: " + info.getSoundId());
        System.out.println("Sample Rate: " + info.getSampleRate() + " Hz");
        System.out.println("Channels: " + info.getChannels());
        System.out.println("Bits per Sample: " + info.getBitsPerSample());
        System.out.println("Duration: " + String.format("%.2f", info.getDuration()) + " seconds");
        System.out.println("Compressed: " + info.isCompressed());
        System.out.println("Valid for SL: " + info.isValid());
        
        // Create LLSD sound stream
        Map<String, Object> soundStream = SecondLifeLLSDUtils.createSoundStream(
            soundId, soundData, format);
        
        System.out.println("LLSD Stream Keys: " + soundStream.keySet());
        System.out.println();
    }
    
    /**
     * Demonstrates the processing of a generic data stream, including
     * compression and checksum validation.
     * @throws LLSDException if data stream processing fails.
     */
    private static void demoDataStreamProcessing() throws LLSDException {
        System.out.println("3. Data Stream Processing Demo");
        System.out.println("------------------------------");
        
        // Create sample data
        String sampleText = "This is sample data for compression testing. ".repeat(500);
        byte[] sampleData = sampleText.getBytes();
        UUID assetId = UUID.randomUUID();
        
        // Process data stream
        SLDataStreamProcessor.DataStreamInfo info = SLDataStreamProcessor.processDataStream(
            assetId, SLAssetType.OBJECT, sampleData, true);
        
        System.out.println("Asset ID: " + info.getAssetId());
        System.out.println("Asset Type: " + SLAssetType.getTypeName(info.getAssetType()));
        System.out.println("Original Size: " + info.getTotalSize() + " bytes");
        System.out.println("Compressed Size: " + info.getCompressedSize() + " bytes");
        System.out.println("Compression: " + info.getCompression().getName());
        System.out.println("Compression Ratio: " + String.format("%.2f", info.getCompressionRatio()));
        System.out.println("Chunk Count: " + info.getChunkCount());
        System.out.println("Valid: " + info.isValid());
        
        // Test data integrity
        String checksum = SLDataStreamProcessor.calculateChecksum(sampleData);
        boolean integrity = SLDataStreamProcessor.validateDataIntegrity(sampleData, checksum);
        System.out.println("Data integrity: " + integrity);
        System.out.println();
    }
    
    /**
     * Demonstrates the basic features of the visual engine, including
     * vector math and scene graph manipulation.
     */
    private static void demoVisualEngine() {
        System.out.println("4. Visual Engine Demo");
        System.out.println("---------------------");
        
        // Vector3 operations
        Vector3 pos1 = new Vector3(1.0, 2.0, 3.0);
        Vector3 pos2 = new Vector3(4.0, 5.0, 6.0);
        
        System.out.println("Vector1: " + pos1.toShortString());
        System.out.println("Vector2: " + pos2.toShortString());
        System.out.println("Sum: " + pos1.add(pos2).toShortString());
        System.out.println("Distance: " + String.format("%.2f", pos1.distance(pos2)));
        
        // Quaternion operations
        Quaternion rotation = Quaternion.fromAxisAngle(Vector3.Z_AXIS, Math.PI / 4);
        Vector3 rotatedX = rotation.rotate(Vector3.X_AXIS);
        System.out.println("45° rotation around Z-axis of X-axis: " + rotatedX.toShortString());
        
        // Scene graph
        SceneNode root = new SceneNode("Scene Root");
        SceneNode child1 = new SceneNode("Child 1");
        SceneNode child2 = new SceneNode("Child 2");
        SceneNode grandChild = new SceneNode("Grand Child");
        
        root.addChild(child1);
        root.addChild(child2);
        child1.addChild(grandChild);
        
        // Set transforms
        child1.setPosition(new Vector3(10.0, 0.0, 0.0));
        grandChild.setPosition(new Vector3(5.0, 0.0, 0.0));
        
        System.out.println("Scene hierarchy:");
        System.out.println("- " + root.getName() + " (children: " + root.getChildren().size() + ")");
        System.out.println("  - " + child1.getName() + " at " + child1.getPosition().toShortString());
        System.out.println("    - " + grandChild.getName() + " at " + grandChild.getPosition().toShortString());
        System.out.println("      World position: " + grandChild.getWorldPosition().toShortString());
        System.out.println("  - " + child2.getName());
        System.out.println();
    }
    
    /**
     * Displays statistics for the various asset caches.
     */
    private static void demoCacheStatistics() {
        System.out.println("5. Cache Statistics");
        System.out.println("-------------------");
        
        Map<String, Object> textureStats = SLTextureProcessor.getCacheStats();
        Map<String, Object> soundStats = SLSoundProcessor.getCacheStats();
        Map<String, Object> dataStats = SLDataStreamProcessor.getCacheStats();
        
        System.out.println("Texture Cache:");
        System.out.println("  Entries: " + textureStats.get("CacheSize"));
        System.out.println("  Total Size: " + textureStats.get("TotalCacheSize") + " bytes");
        
        System.out.println("Sound Cache:");
        System.out.println("  Entries: " + soundStats.get("CacheSize"));
        System.out.println("  Total Size: " + soundStats.get("TotalCacheSize") + " bytes");
        
        System.out.println("Data Cache:");
        System.out.println("  Entries: " + dataStats.get("CacheSize"));
        System.out.println("  Total Size: " + dataStats.get("TotalCacheSize") + " bytes");
        
        if (dataStats.containsKey("OverallCompressionRatio")) {
            System.out.println("  Compression Ratio: " + 
                String.format("%.2f", (Double) dataStats.get("OverallCompressionRatio")));
        }
        
        System.out.println();
        System.out.println("Demo completed successfully!");
    }
    
    /**
     * Creates a dummy byte array representing a J2C texture for demonstration.
     * @return A byte array with a J2C signature.
     */
    private static byte[] createSampleJ2CTexture() {
        byte[] data = new byte[1024];
        data[0] = (byte)0xFF; // J2C signature
        data[1] = (byte)0x4F;
        
        // Fill with pattern data
        for (int i = 2; i < data.length; i++) {
            data[i] = (byte) ((i * 17) % 256);
        }
        
        return data;
    }
    
    /**
     * Creates a dummy byte array representing a WAV audio file for demonstration.
     * @return A byte array with a valid WAV header.
     */
    private static byte[] createSampleWAVAudio() {
        byte[] data = new byte[1000];
        
        // WAV header  
        System.arraycopy("RIFF".getBytes(), 0, data, 0, 4);
        data[8] = 'W'; data[9] = 'A'; data[10] = 'V'; data[11] = 'E';
        data[12] = 'f'; data[13] = 'm'; data[14] = 't'; data[15] = ' ';
        
        // Format chunk size (16 bytes)
        data[16] = 16;
        
        // Audio format (PCM = 1)
        data[20] = 1;
        
        // Channels (2)
        data[22] = 2;
        
        // Sample rate (44100)
        data[24] = 0x44; data[25] = (byte)0xAC;
        
        // Byte rate (176400)
        data[28] = (byte)0x10; data[29] = (byte)0xB1; data[30] = 2;
        
        // Block align (4)
        data[32] = 4;
        
        // Bits per sample (16)
        data[34] = 16;
        
        // Data chunk
        data[36] = 'd'; data[37] = 'a'; data[38] = 't'; data[39] = 'a';
        
        // Fill with sine wave data
        for (int i = 44; i < data.length; i++) {
            data[i] = (byte) (Math.sin((i - 44) * 0.1) * 127);
        }
        
        return data;
    }
}