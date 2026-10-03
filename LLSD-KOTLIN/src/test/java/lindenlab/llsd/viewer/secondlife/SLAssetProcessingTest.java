/*
 * Test for Second Life asset processing functionality
 */

package lindenlab.llsd.viewer.secondlife;

import lindenlab.llsd.LLSDException;
import lindenlab.llsd.viewer.secondlife.assets.*;
import lindenlab.llsd.viewer.secondlife.engine.*;

import org.junit.jupiter.api.*;
import static org.junit.jupiter.api.Assertions.*;

import java.util.*;
import java.io.*;

/**
 * Comprehensive test for Second Life asset processing and visual engine components.
 */
public class SLAssetProcessingTest {
    
    private static final UUID TEST_TEXTURE_ID = UUID.randomUUID();
    private static final UUID TEST_SOUND_ID = UUID.randomUUID();
    private static final UUID TEST_DATA_ID = UUID.randomUUID();
    
    @BeforeEach
    void setUp() {
        // Clear caches before each test
        SLTextureProcessor.clearCache();
        SLSoundProcessor.clearCache();
        SLDataStreamProcessor.clearCache();
    }
    
    @Nested
    @DisplayName("Asset Type Tests")
    class AssetTypeTests {
        
        @Test
        @DisplayName("Test asset type identification")
        void testAssetTypeIdentification() {
            assertTrue(SLAssetType.isTextureType(SLAssetType.TEXTURE));
            assertTrue(SLAssetType.isTextureType(SLAssetType.TEXTURE_TGA));
            assertTrue(SLAssetType.isTextureType(SLAssetType.IMAGE_JPEG));
            assertTrue(SLAssetType.isTextureType(SLAssetType.TEXTURE_STREAM));
            
            assertTrue(SLAssetType.isSoundType(SLAssetType.SOUND));
            assertTrue(SLAssetType.isSoundType(SLAssetType.SOUND_WAV));
            assertTrue(SLAssetType.isSoundType(SLAssetType.AUDIO_STREAM));
            
            assertTrue(SLAssetType.isStreamType(SLAssetType.AUDIO_STREAM));
            assertTrue(SLAssetType.isStreamType(SLAssetType.TEXTURE_STREAM));
            
            assertFalse(SLAssetType.isTextureType(SLAssetType.SOUND));
            assertFalse(SLAssetType.isSoundType(SLAssetType.TEXTURE));
        }
        
        @Test
        @DisplayName("Test asset type names and MIME types")
        void testAssetTypeNamesAndMimeTypes() {
            assertEquals("Texture", SLAssetType.getTypeName(SLAssetType.TEXTURE));
            assertEquals("Sound", SLAssetType.getTypeName(SLAssetType.SOUND));
            assertEquals("Audio Stream", SLAssetType.getTypeName(SLAssetType.AUDIO_STREAM));
            
            assertEquals("image/x-j2c", SLAssetType.getMimeType(SLAssetType.TEXTURE));
            assertEquals("audio/wav", SLAssetType.getMimeType(SLAssetType.SOUND));
            assertEquals("image/jpeg", SLAssetType.getMimeType(SLAssetType.IMAGE_JPEG));
        }
    }
    
    @Nested
    @DisplayName("Texture Processing Tests")
    class TextureProcessingTests {
        
        @Test
        @DisplayName("Test texture format detection")
        void testTextureFormatDetection() {
            // Create sample texture data signatures
            byte[] jpegData = {(byte)0xFF, (byte)0xD8, 0x00, 0x00};
            byte[] pngData = {(byte)0x89, 'P', 'N', 'G'};
            byte[] j2cData = {(byte)0xFF, (byte)0x4F, 0x00, 0x00};
            byte[] bmpData = {'B', 'M', 0x00, 0x00};
            
            assertEquals(SLTextureProcessor.TextureFormat.JPEG, 
                        SLTextureProcessor.detectTextureFormat(jpegData));
            assertEquals(SLTextureProcessor.TextureFormat.PNG, 
                        SLTextureProcessor.detectTextureFormat(pngData));
            assertEquals(SLTextureProcessor.TextureFormat.J2C, 
                        SLTextureProcessor.detectTextureFormat(j2cData));
            assertEquals(SLTextureProcessor.TextureFormat.BMP, 
                        SLTextureProcessor.detectTextureFormat(bmpData));
            assertEquals(SLTextureProcessor.TextureFormat.UNKNOWN, 
                        SLTextureProcessor.detectTextureFormat(new byte[]{0x00, 0x00, 0x00, 0x00}));
        }
        
        @Test
        @DisplayName("Test texture validation")
        void testTextureValidation() {
            assertTrue(SLTextureProcessor.isValidSLTextureDimensions(256, 256));
            assertTrue(SLTextureProcessor.isValidSLTextureDimensions(512, 512));
            assertTrue(SLTextureProcessor.isValidSLTextureDimensions(1024, 1024));
            
            assertFalse(SLTextureProcessor.isValidSLTextureDimensions(100, 100)); // Not power of 2
            assertFalse(SLTextureProcessor.isValidSLTextureDimensions(2048, 2048)); // Too large
            assertFalse(SLTextureProcessor.isValidSLTextureDimensions(32, 32)); // Too small
        }
        
        @Test
        @DisplayName("Test texture stream data creation")
        void testTextureStreamDataCreation() throws LLSDException {
            byte[] testData = createTestJ2CData();
            
            Map<String, Object> streamData = SecondLifeLLSDUtils.createTextureStream(
                TEST_TEXTURE_ID, testData, SLTextureProcessor.TextureFormat.J2C);
            
            assertNotNull(streamData);
            assertEquals(TEST_TEXTURE_ID, streamData.get("AssetID"));
            assertEquals(SLAssetType.TEXTURE_STREAM, streamData.get("AssetType"));
            assertEquals("J2C", streamData.get("Format"));
            assertTrue(streamData.containsKey("Width"));
            assertTrue(streamData.containsKey("Height"));
            assertTrue(streamData.containsKey("Data"));
        }
    }
    
    @Nested
    @DisplayName("Sound Processing Tests")
    class SoundProcessingTests {
        
        @Test
        @DisplayName("Test audio format detection")
        void testAudioFormatDetection() {
            // Create sample audio data signatures
            byte[] wavData = {'R', 'I', 'F', 'F'};
            byte[] oggData = {'O', 'g', 'g', 'S'};
            byte[] mp3Data = {(byte)0xFF, (byte)0xE0, 0x00, 0x00};
            
            assertEquals(SLSoundProcessor.AudioFormat.WAV, 
                        SLSoundProcessor.detectAudioFormat(wavData));
            assertEquals(SLSoundProcessor.AudioFormat.OGG, 
                        SLSoundProcessor.detectAudioFormat(oggData));
            assertEquals(SLSoundProcessor.AudioFormat.MP3, 
                        SLSoundProcessor.detectAudioFormat(mp3Data));
            assertEquals(SLSoundProcessor.AudioFormat.UNKNOWN, 
                        SLSoundProcessor.detectAudioFormat(new byte[]{0x00, 0x00, 0x00, 0x00}));
        }
        
        @Test
        @DisplayName("Test sound stream data creation")
        void testSoundStreamDataCreation() throws LLSDException {
            byte[] testData = createTestWAVData();
            
            Map<String, Object> streamData = SecondLifeLLSDUtils.createSoundStream(
                TEST_SOUND_ID, testData, SLSoundProcessor.AudioFormat.WAV);
            
            assertNotNull(streamData);
            assertEquals(TEST_SOUND_ID, streamData.get("AssetID"));
            assertEquals(SLAssetType.AUDIO_STREAM, streamData.get("AssetType"));
            assertEquals("WAV", streamData.get("Format"));
            assertTrue(streamData.containsKey("SampleRate"));
            assertTrue(streamData.containsKey("Channels"));
            assertTrue(streamData.containsKey("Duration"));
            assertTrue(streamData.containsKey("Data"));
        }
    }
    
    @Nested
    @DisplayName("Data Stream Processing Tests")
    class DataStreamProcessingTests {
        
        @Test
        @DisplayName("Test data compression and decompression")
        void testDataCompression() throws IOException {
            // Use larger, more repetitive data that compresses better
            String repeatedText = "This is test data for compression testing. ".repeat(100);
            byte[] originalData = repeatedText.getBytes();
            
            // Test GZIP compression
            byte[] compressed = SLDataStreamProcessor.compressData(originalData, 
                SLDataStreamProcessor.CompressionType.GZIP);
            byte[] decompressed = SLDataStreamProcessor.decompressData(compressed, 
                SLDataStreamProcessor.CompressionType.GZIP);
            
            assertArrayEquals(originalData, decompressed);
            assertTrue(compressed.length < originalData.length, 
                "Compressed size (" + compressed.length + ") should be less than original (" + originalData.length + ")");
            
            // Test DEFLATE compression
            compressed = SLDataStreamProcessor.compressData(originalData, 
                SLDataStreamProcessor.CompressionType.DEFLATE);
            decompressed = SLDataStreamProcessor.decompressData(compressed, 
                SLDataStreamProcessor.CompressionType.DEFLATE);
            
            assertArrayEquals(originalData, decompressed);
        }
        
        @Test
        @DisplayName("Test checksum validation")
        void testChecksumValidation() {
            byte[] testData = "Test data for checksum".getBytes();
            String checksum = SLDataStreamProcessor.calculateChecksum(testData);
            
            assertTrue(SLDataStreamProcessor.validateDataIntegrity(testData, checksum));
            assertFalse(SLDataStreamProcessor.validateDataIntegrity(
                "Different data".getBytes(), checksum));
        }
        
        @Test
        @DisplayName("Test stream chunking")
        void testStreamChunking() throws IOException, LLSDException {
            byte[] testData = new byte[200000]; // 200KB test data
            for (int i = 0; i < testData.length; i++) {
                testData[i] = (byte) (i % 256);
            }
            
            SLDataStreamProcessor.DataStreamInfo info = SLDataStreamProcessor.processDataStream(
                TEST_DATA_ID, SLAssetType.OBJECT, testData, true);
            
            List<SLDataStreamProcessor.StreamChunk> chunks = 
                SLDataStreamProcessor.createStreamChunks(testData, info.getCompression());
            
            assertTrue(chunks.size() > 1); // Should be split into multiple chunks
            
            // Verify all chunks are valid
            for (SLDataStreamProcessor.StreamChunk chunk : chunks) {
                assertTrue(chunk.isValid());
            }
            
            // Test reconstruction
            byte[] reconstructed = SLDataStreamProcessor.reconstructFromChunks(
                chunks, info.getCompression());
            
            assertArrayEquals(testData, reconstructed);
        }
    }
    
    @Nested
    @DisplayName("Visual Engine Tests")
    class VisualEngineTests {
        
        @Test
        @DisplayName("Test Vector3 operations")
        void testVector3Operations() {
            Vector3 v1 = new Vector3(1.0, 2.0, 3.0);
            Vector3 v2 = new Vector3(4.0, 5.0, 6.0);
            
            // Test addition
            Vector3 sum = v1.add(v2);
            assertEquals(new Vector3(5.0, 7.0, 9.0), sum);
            
            // Test subtraction
            Vector3 diff = v2.subtract(v1);
            assertEquals(new Vector3(3.0, 3.0, 3.0), diff);
            
            // Test dot product
            double dot = v1.dot(v2);
            assertEquals(32.0, dot, 1e-6);
            
            // Test cross product
            Vector3 cross = v1.cross(v2);
            assertEquals(new Vector3(-3.0, 6.0, -3.0), cross);
            
            // Test magnitude
            Vector3 unit = new Vector3(1.0, 0.0, 0.0);
            assertEquals(1.0, unit.magnitude(), 1e-6);
            
            // Test normalization
            Vector3 normalized = v1.normalize();
            assertTrue(normalized.isNormalized());
        }
        
        @Test
        @DisplayName("Test Quaternion operations")
        void testQuaternionOperations() {
            // Test identity
            Quaternion identity = Quaternion.IDENTITY;
            assertTrue(identity.isIdentity());
            assertTrue(identity.isNormalized());
            
            // Test axis-angle construction
            Vector3 axis = Vector3.Z_AXIS;
            double angle = Math.PI / 2; // 90 degrees
            Quaternion rotation = Quaternion.fromAxisAngle(axis, angle);
            assertTrue(rotation.isNormalized());
            
            // Test vector rotation
            Vector3 xAxis = Vector3.X_AXIS;
            Vector3 rotated = rotation.rotate(xAxis);
            Vector3 expected = Vector3.Y_AXIS;
            assertTrue(Math.abs(rotated.distance(expected)) < 1e-6);
            
            // Test quaternion multiplication
            Quaternion combined = rotation.multiply(rotation);
            Vector3 doubleRotated = combined.rotate(xAxis);
            Vector3 expectedDouble = Vector3.X_AXIS.negate(); // 180 degrees
            assertTrue(Math.abs(doubleRotated.distance(expectedDouble)) < 1e-6);
        }
        
        @Test
        @DisplayName("Test SceneNode hierarchy")
        void testSceneNodeHierarchy() {
            SceneNode root = new SceneNode("Root");
            SceneNode child1 = new SceneNode("Child1");
            SceneNode child2 = new SceneNode("Child2");
            SceneNode grandChild = new SceneNode("GrandChild");
            
            // Build hierarchy
            root.addChild(child1);
            root.addChild(child2);
            child1.addChild(grandChild);
            
            // Test hierarchy
            assertEquals(root, child1.getParent());
            assertEquals(root, child2.getParent());
            assertEquals(child1, grandChild.getParent());
            assertEquals(2, root.getChildren().size());
            assertEquals(1, child1.getChildren().size());
            assertEquals(0, child2.getChildren().size());
            
            // Test finding nodes
            assertEquals(child1, root.findChild("Child1"));
            assertEquals(grandChild, root.findDescendant("GrandChild"));
            
            // Test depth
            assertEquals(0, root.getDepth());
            assertEquals(1, child1.getDepth());
            assertEquals(2, grandChild.getDepth());
            
            // Test ancestry
            assertTrue(root.isAncestorOf(grandChild));
            assertTrue(child1.isAncestorOf(grandChild));
            assertFalse(child2.isAncestorOf(grandChild));
        }
        
        @Test
        @DisplayName("Test SceneNode transformations")
        void testSceneNodeTransformations() {
            SceneNode parent = new SceneNode("Parent");
            SceneNode child = new SceneNode("Child");
            parent.addChild(child);
            
            // Set transforms
            parent.setPosition(new Vector3(10.0, 0.0, 0.0));
            child.setPosition(new Vector3(5.0, 0.0, 0.0));
            
            // Test world position
            Vector3 worldPos = child.getWorldPosition();
            assertEquals(new Vector3(15.0, 0.0, 0.0), worldPos);
            
            // Test local to world transformation
            Vector3 localPoint = Vector3.ZERO;
            Vector3 worldPoint = child.transformToWorld(localPoint);
            assertEquals(new Vector3(15.0, 0.0, 0.0), worldPoint);
        }
    }
    
    @Nested
    @DisplayName("Integration Tests")
    class IntegrationTests {
        
        @Test
        @DisplayName("Test complete asset processing workflow")
        void testCompleteAssetProcessingWorkflow() throws LLSDException {
            // Create test texture data
            byte[] textureData = createTestJ2CData();
            
            // Process through SecondLifeLLSDUtils
            Map<String, Object> textureStream = SecondLifeLLSDUtils.createTextureStream(
                TEST_TEXTURE_ID, textureData, SLTextureProcessor.TextureFormat.J2C);
            
            // Verify texture stream structure
            assertNotNull(textureStream);
            assertTrue(textureStream.containsKey("AssetID"));
            assertTrue(textureStream.containsKey("AssetType"));
            assertTrue(textureStream.containsKey("Format"));
            assertTrue(textureStream.containsKey("Data"));
            
            // Test incoming stream processing
            Map<String, Object> processed = SecondLifeLLSDUtils.processIncomingStream(textureStream);
            
            assertNotNull(processed);
            assertTrue(processed.containsKey("AssetID"));
            assertTrue(processed.containsKey("TextureInfo"));
            assertTrue((Boolean) processed.get("Valid"));
        }
        
        @Test
        @DisplayName("Test cache statistics")
        void testCacheStatistics() throws LLSDException {
            byte[] testData = createTestJ2CData();
            
            // Process some textures to populate cache
            for (int i = 0; i < 5; i++) {
                UUID textureId = UUID.randomUUID();
                SLTextureProcessor.processTexture(textureId, testData, 
                    SLTextureProcessor.TextureFormat.J2C);
            }
            
            // Get cache stats
            Map<String, Object> stats = SLTextureProcessor.getCacheStats();
            
            assertNotNull(stats);
            assertTrue((Integer) stats.get("CacheSize") > 0);
            assertTrue((Long) stats.get("TotalCacheSize") > 0);
        }
    }
    
    // Test data creation helpers
    private byte[] createTestJ2CData() {
        // Create minimal J2C data with proper signature
        byte[] data = new byte[100];
        data[0] = (byte)0xFF; // J2C signature
        data[1] = (byte)0x4F;
        // Fill rest with dummy data
        for (int i = 2; i < data.length; i++) {
            data[i] = (byte) (i % 256);
        }
        return data;
    }
    
    private byte[] createTestWAVData() {
        // Create minimal WAV data with proper header
        byte[] data = new byte[200];
        
        // RIFF header
        data[0] = 'R'; data[1] = 'I'; data[2] = 'F'; data[3] = 'F';
        
        // File size (placeholder)
        data[4] = data[5] = data[6] = data[7] = 0;
        
        // WAVE format
        data[8] = 'W'; data[9] = 'A'; data[10] = 'V'; data[11] = 'E';
        
        // fmt chunk
        data[12] = 'f'; data[13] = 'm'; data[14] = 't'; data[15] = ' ';
        
        // fmt chunk size (16 bytes)
        data[16] = 16; data[17] = data[18] = data[19] = 0;
        
        // Audio format (PCM = 1)
        data[20] = 1; data[21] = 0;
        
        // Channels (2 = stereo)
        data[22] = 2; data[23] = 0;
        
        // Sample rate (44100 Hz)
        data[24] = 0x44; data[25] = (byte)0xAC; data[26] = 0; data[27] = 0;
        
        // Byte rate
        data[28] = (byte)0x10; data[29] = (byte)0xB1; data[30] = 2; data[31] = 0;
        
        // Block align
        data[32] = 4; data[33] = 0;
        
        // Bits per sample
        data[34] = 16; data[35] = 0;
        
        // data chunk
        data[36] = 'd'; data[37] = 'a'; data[38] = 't'; data[39] = 'a';
        
        // Fill rest with dummy audio data
        for (int i = 44; i < data.length; i++) {
            data[i] = (byte) (Math.sin(i * 0.1) * 127);
        }
        
        return data;
    }
}