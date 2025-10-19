/*
 * OpenJPEGCodec Kotlin Test Suite - Comprehensive testing for Kotlin JPEG2000 implementation
 *
 * Copyright (C) 2024
 */

package lindenlab.llsd.viewer.secondlife.libraries

import lindenlab.llsd.viewer.secondlife.libraries.openjpeg.*
import org.junit.jupiter.api.*
import org.junit.jupiter.api.Assertions.*
import java.awt.image.BufferedImage
import java.io.IOException

/**
 * Comprehensive test suite for Kotlin OpenJPEGCodec implementation.
 */
class OpenJPEGCodecKotlinTest {
    
    @Nested
    @DisplayName("Kotlin OpenJPEGCodec Basic Tests")
    inner class BasicTests {
        
        @Test
        @DisplayName("Should parse J2K header with Kotlin extensions")
        fun testHeaderParsingKotlin() {
            val validData = J2KUtils.createTestJ2KData(256, 256, 3)
            
            assertDoesNotThrow {
                val info = OpenJPEGCodec.parseHeader(validData)
                assertNotNull(info)
                assertTrue(info.width > 0)
                assertTrue(info.height > 0)
            }
        }
        
        @Test
        @DisplayName("Should validate J2K data with utility functions")
        fun testJ2KValidation() {
            val validData = J2KUtils.createTestJ2KData(128, 128)
            val invalidData = byteArrayOf(0x00, 0x01, 0x02, 0x03)
            
            assertTrue(J2KUtils.isValidJ2K(validData))
            assertFalse(J2KUtils.isValidJ2K(invalidData))
        }
        
        @Test
        @DisplayName("Should get J2K summary information")
        fun testJ2KSummary() {
            val validData = J2KUtils.createTestJ2KData(320, 240, 3)
            val summary = J2KUtils.getJ2KSummary(validData)
            
            assertNotNull(summary)
            assertTrue(summary.contains("320x240"))
            assertTrue(summary.contains("3 components"))
        }
        
        @Test
        @DisplayName("Should get image info with extension property")
        fun testImageInfoExtension() {
            val validData = J2KUtils.createTestJ2KData(512, 384)
            
            assertDoesNotThrow {
                val info = validData.j2kImageInfo
                assertNotNull(info)
                assertTrue(info.containsKey("width"))
                assertTrue(info.containsKey("height"))
                assertEquals(512, info["width"])
                assertEquals(384, info["height"])
            }
        }
    }
    
    @Nested
    @DisplayName("Kotlin Extension Functions Tests")
    inner class ExtensionFunctionTests {
        
        @Test
        @DisplayName("Should decode with Kotlin extension function")
        fun testDecodeExtension() {
            val testImage = BufferedImage(64, 64, BufferedImage.TYPE_3BYTE_BGR)
            val encoded = testImage.encodeToJ2K(quality = 90, lossless = false)
            
            assertDoesNotThrow {
                val decoded = encoded.decodeJ2K(
                    qualityLayers = 3,
                    regionWidth = 32,
                    regionHeight = 32
                )
                
                assertNotNull(decoded)
                assertEquals(32, decoded.width)
                assertEquals(32, decoded.height)
            }
        }
        
        @Test
        @DisplayName("Should encode with Kotlin extension function")
        fun testEncodeExtension() {
            val testImage = BufferedImage(128, 96, BufferedImage.TYPE_3BYTE_BGR)
            
            // Fill with test pattern
            for (y in 0 until testImage.height) {
                for (x in 0 until testImage.width) {
                    val rgb = (x * 255 / testImage.width) shl 16 or
                             (y * 255 / testImage.height) shl 8 or
                             ((x + y) * 255 / (testImage.width + testImage.height))
                    testImage.setRGB(x, y, rgb)
                }
            }
            
            assertDoesNotThrow {
                val encoded = testImage.encodeToJ2K(quality = 85)
                assertNotNull(encoded)
                assertTrue(encoded.isNotEmpty())
                
                // Should have J2K markers
                assertEquals(0xFF.toByte(), encoded[0])
                assertEquals(0x4F.toByte(), encoded[1])
            }
        }
        
        @Test
        @DisplayName("Should encode with different quality settings")
        fun testQualityVariations() {
            val testImage = BufferedImage(64, 64, BufferedImage.TYPE_3BYTE_BGR)
            
            val lowQuality = testImage.encodeToJ2K(quality = 30)
            val highQuality = testImage.encodeToJ2K(quality = 95)
            val lossless = testImage.encodeToJ2K(quality = 100, lossless = true)
            
            assertTrue(lowQuality.isNotEmpty())
            assertTrue(highQuality.isNotEmpty())
            assertTrue(lossless.isNotEmpty())
        }
    }
    
    @Nested
    @DisplayName("Kotlin DSL Tests")
    inner class DSLTests {
        
        @Test
        @DisplayName("Should create J2K image info with DSL")
        fun testImageInfoDSL() {
            val imageInfo = j2kImageInfo {
                width = 1024
                height = 768
                numComponents = 3
                tileWidth = 256
                tileHeight = 256
                numQualityLayers = 5
                numDecompositionLevels = 6
                progressionOrder = "RLCP"
            }
            
            assertEquals(1024, imageInfo.width)
            assertEquals(768, imageInfo.height)
            assertEquals(3, imageInfo.numComponents)
            assertEquals(256, imageInfo.tileWidth)
            assertEquals(256, imageInfo.tileHeight)
            assertEquals(5, imageInfo.numQualityLayers)
            assertEquals(6, imageInfo.numDecompositionLevels)
            assertEquals("RLCP", imageInfo.progressionOrder)
        }
        
        @Test
        @DisplayName("Should modify image info properties")
        fun testImageInfoModification() {
            val imageInfo = OpenJPEGCodec.J2KImageInfo()
            
            imageInfo.width = 640
            imageInfo.height = 480
            imageInfo.numComponents = 4
            imageInfo.tileWidth = 128
            imageInfo.tileHeight = 128
            
            assertEquals(640, imageInfo.width)
            assertEquals(480, imageInfo.height)
            assertEquals(4, imageInfo.numComponents)
            assertEquals(128, imageInfo.tileWidth)
            assertEquals(128, imageInfo.tileHeight)
        }
    }
    
    @Nested
    @DisplayName("Decode Parameters Tests")
    inner class DecodeParametersTests {
        
        @Test
        @DisplayName("Should create decode params with defaults")
        fun testDecodeParamsDefaults() {
            val params = OpenJPEGCodec.DecodeParams()
            
            assertEquals(-1, params.qualityLayers)
            assertEquals(-1, params.decompositionLevels)
            assertEquals(0, params.regionX)
            assertEquals(0, params.regionY)
            assertEquals(-1, params.regionWidth)
            assertEquals(-1, params.regionHeight)
            assertTrue(params.useColorTransform)
        }
        
        @Test
        @DisplayName("Should modify decode parameters")
        fun testDecodeParamsModification() {
            val params = OpenJPEGCodec.DecodeParams()
            
            params.qualityLayers = 3
            params.decompositionLevels = 5
            params.regionX = 100
            params.regionY = 100
            params.regionWidth = 200
            params.regionHeight = 200
            params.useColorTransform = false
            
            assertEquals(3, params.qualityLayers)
            assertEquals(5, params.decompositionLevels)
            assertEquals(100, params.regionX)
            assertEquals(100, params.regionY)
            assertEquals(200, params.regionWidth)
            assertEquals(200, params.regionHeight)
            assertFalse(params.useColorTransform)
        }
    }
    
    @Nested
    @DisplayName("Round-trip Tests")
    inner class RoundTripTests {
        
        @Test
        @DisplayName("Should maintain image dimensions in round-trip")
        fun testDimensionConsistency() {
            val originalImage = createTestImage(256, 192)
            
            val encoded = originalImage.encodeToJ2K(quality = 85)
            val decoded = encoded.decodeJ2K()
            
            assertEquals(originalImage.width, decoded.width)
            assertEquals(originalImage.height, decoded.height)
        }
        
        @Test
        @DisplayName("Should handle region decoding")
        fun testRegionDecoding() {
            val originalImage = createTestImage(512, 512)
            val encoded = originalImage.encodeToJ2K(quality = 90)
            
            val regionDecoded = encoded.decodeJ2K(
                regionX = 100,
                regionY = 100,
                regionWidth = 200,
                regionHeight = 200
            )
            
            assertEquals(200, regionDecoded.width)
            assertEquals(200, regionDecoded.height)
        }
        
        @Test
        @DisplayName("Should handle quality layer decoding")
        fun testQualityLayerDecoding() {
            val originalImage = createTestImage(128, 128)
            val encoded = originalImage.encodeToJ2K(quality = 95)
            
            assertDoesNotThrow {
                val lowQualityDecoded = encoded.decodeJ2K(qualityLayers = 2)
                val highQualityDecoded = encoded.decodeJ2K(qualityLayers = -1) // All layers
                
                assertNotNull(lowQualityDecoded)
                assertNotNull(highQualityDecoded)
                assertEquals(originalImage.width, lowQualityDecoded.width)
                assertEquals(originalImage.width, highQualityDecoded.width)
            }
        }
    }
    
    @Nested
    @DisplayName("Error Handling Tests")
    inner class ErrorHandlingTests {
        
        @Test
        @DisplayName("Should handle invalid data gracefully")
        fun testInvalidDataHandling() {
            val invalidData = byteArrayOf(0x00, 0x01, 0x02, 0x03, 0x04)
            
            assertThrows<IOException> {
                OpenJPEGCodec.parseHeader(invalidData)
            }
            
            assertThrows<IOException> {
                invalidData.decodeJ2K()
            }
            
            assertThrows<IOException> {
                invalidData.j2kImageInfo
            }
        }
        
        @Test
        @DisplayName("Should handle null data")
        fun testNullDataHandling() {
            assertThrows<IOException> {
                OpenJPEGCodec.parseHeader(null)
            }
            
            assertThrows<IOException> {
                OpenJPEGCodec.encode(null, 85, false)
            }
        }
        
        @Test
        @DisplayName("Should handle too short data")
        fun testTooShortData() {
            val shortData = byteArrayOf(0xFF.toByte(), 0x4F.toByte()) // Just SOC marker
            
            assertThrows<IOException> {
                OpenJPEGCodec.parseHeader(shortData)
            }
        }
    }
    
    @Nested
    @DisplayName("Performance Tests")
    inner class PerformanceTests {
        
        @Test
        @DisplayName("Should handle large images efficiently")
        fun testLargeImagePerformance() {
            val largeImage = createTestImage(1024, 768)
            
            val startTime = System.currentTimeMillis()
            val encoded = largeImage.encodeToJ2K(quality = 80)
            val encodeTime = System.currentTimeMillis() - startTime
            
            val decodeStartTime = System.currentTimeMillis()
            val decoded = encoded.decodeJ2K()
            val decodeTime = System.currentTimeMillis() - decodeStartTime
            
            assertNotNull(encoded)
            assertNotNull(decoded)
            
            // Performance should be reasonable (less than 10 seconds each for mock implementation)
            assertTrue(encodeTime < 10000, "Encoding should complete in under 10 seconds")
            assertTrue(decodeTime < 10000, "Decoding should complete in under 10 seconds")
        }
        
        @Test
        @DisplayName("Should handle multiple operations efficiently")
        fun testMultipleOperations() {
            val testImages = (1..5).map { 
                createTestImage(64 * it, 64 * it) 
            }
            
            val startTime = System.currentTimeMillis()
            
            testImages.forEach { image ->
                val encoded = image.encodeToJ2K(quality = 75)
                val decoded = encoded.decodeJ2K()
                assertNotNull(decoded)
            }
            
            val totalTime = System.currentTimeMillis() - startTime
            assertTrue(totalTime < 30000, "Multiple operations should complete in under 30 seconds")
        }
    }
    
    @Nested
    @DisplayName("Utility Functions Tests")
    inner class UtilityFunctionTests {
        
        @Test
        @DisplayName("Should create test data with specified dimensions")
        fun testCreateTestData() {
            val testData1 = J2KUtils.createTestJ2KData(100, 200)
            val testData2 = J2KUtils.createTestJ2KData(300, 400, 4)
            
            assertTrue(testData1.isNotEmpty())
            assertTrue(testData2.isNotEmpty())
            
            assertTrue(J2KUtils.isValidJ2K(testData1))
            assertTrue(J2KUtils.isValidJ2K(testData2))
        }
        
        @Test
        @DisplayName("Should provide meaningful summaries")
        fun testSummaryGeneration() {
            val testData = J2KUtils.createTestJ2KData(640, 480, 3)
            val summary = J2KUtils.getJ2KSummary(testData)
            
            assertTrue(summary.contains("640"))
            assertTrue(summary.contains("480"))
            assertTrue(summary.contains("3"))
            assertTrue(summary.contains("J2K Image") || summary.contains("components"))
        }
        
        @Test
        @DisplayName("Should handle invalid data in summaries")
        fun testInvalidDataSummary() {
            val invalidData = byteArrayOf(0x00, 0x01, 0x02)
            val summary = J2KUtils.getJ2KSummary(invalidData)
            
            assertTrue(summary.contains("Invalid") || summary.contains("error"))
        }
    }
    
    @Nested
    @DisplayName("Integration with Java Components Tests")
    inner class JavaIntegrationTests {
        
        @Test
        @DisplayName("Should work with Java BufferedImage")
        fun testJavaBufferedImageIntegration() {
            val javaImage = BufferedImage(200, 150, BufferedImage.TYPE_3BYTE_BGR)
            
            // Fill with Java Graphics2D operations
            val g2d = javaImage.createGraphics()
            g2d.color = java.awt.Color.RED
            g2d.fillRect(0, 0, 100, 75)
            g2d.color = java.awt.Color.BLUE
            g2d.fillRect(100, 75, 100, 75)
            g2d.dispose()
            
            assertDoesNotThrow {
                val encoded = javaImage.encodeToJ2K()
                val decoded = encoded.decodeJ2K()
                
                assertNotNull(decoded)
                assertEquals(javaImage.width, decoded.width)
                assertEquals(javaImage.height, decoded.height)
            }
        }
        
        @Test
        @DisplayName("Should handle different BufferedImage types")
        fun testDifferentImageTypes() {
            val grayImage = BufferedImage(64, 64, BufferedImage.TYPE_BYTE_GRAY)
            val rgbImage = BufferedImage(64, 64, BufferedImage.TYPE_3BYTE_BGR)
            val argbImage = BufferedImage(64, 64, BufferedImage.TYPE_4BYTE_ABGR)
            
            assertDoesNotThrow {
                val grayEncoded = grayImage.encodeToJ2K()
                val rgbEncoded = rgbImage.encodeToJ2K()
                val argbEncoded = argbImage.encodeToJ2K()
                
                assertNotNull(grayEncoded)
                assertNotNull(rgbEncoded)
                assertNotNull(argbEncoded)
                
                assertTrue(grayEncoded.isNotEmpty())
                assertTrue(rgbEncoded.isNotEmpty())
                assertTrue(argbEncoded.isNotEmpty())
            }
        }
    }
    
    // Helper methods
    
    private fun createTestImage(width: Int, height: Int): BufferedImage {
        val image = BufferedImage(width, height, BufferedImage.TYPE_3BYTE_BGR)
        
        // Fill with a colorful test pattern
        for (y in 0 until height) {
            for (x in 0 until width) {
                val r = (x * 255) / width
                val g = (y * 255) / height
                val b = ((x + y) * 255) / (width + height)
                val rgb = (0xFF shl 24) or (r shl 16) or (g shl 8) or b
                image.setRGB(x, y, rgb)
            }
        }
        
        return image
    }
}