package com.linkpoint.assets;

import org.junit.Assert;
import org.junit.Test;

public class TextureDecoderTest {

    @Test
    public void testEtc2RgbaTranscodingSize() {
        // 1024x1024 RGBA
        int width = 1024;
        int height = 1024;
        byte[] rgbaPixels = createTestRgbaBuffer(width, height, true);

        TextureDecoder.TranscodedTexture texture = TextureDecoder.transcodeToEtc2(
                rgbaPixels, width, height, true, true
        );

        Assert.assertNotNull("Transcoded texture should not be null", texture);
        Assert.assertEquals(width, texture.getWidth());
        Assert.assertEquals(height, texture.getHeight());
        Assert.assertTrue(texture.hasAlpha());

        // ETC2 EAC RGBA8 is 16 bytes per 4x4 block
        // (1024/4) * (1024/4) * 16 = 256 * 256 * 16 = 1,048,576 bytes (1MB)
        int expectedCompressedBytes = 1024 * 1024;
        int rawRgbaBytes = 1024 * 1024 * 4; // 4MB

        Assert.assertEquals(expectedCompressedBytes, texture.getVramSizeBytes());
        Assert.assertEquals(expectedCompressedBytes, texture.getPayload().length);
        Assert.assertEquals(TextureDecoder.TextureFormat.ETC2_EAC_RGBA8, texture.getFormat());
        Assert.assertTrue("Compressed size should be 4x smaller than raw RGBA8",
                texture.getVramSizeBytes() == rawRgbaBytes / 4);
    }

    @Test
    public void test2048x2048Etc2TranscodingSize() {
        // 2048x2048 RGBA
        int width = 2048;
        int height = 2048;
        byte[] rgbaPixels = createTestRgbaBuffer(width, height, true);

        TextureDecoder.TranscodedTexture texture = TextureDecoder.transcodeToEtc2(
                rgbaPixels, width, height, true, true
        );

        Assert.assertNotNull(texture);
        // Raw RGBA8 is 2048 * 2048 * 4 = 16MB (16,777,216 bytes)
        // ETC2 RGBA8 payload is (2048/4) * (2048/4) * 16 = 512 * 512 * 16 = 4MB (4,194,304 bytes)
        int expectedBytes = 4 * 1024 * 1024;
        Assert.assertEquals(expectedBytes, texture.getVramSizeBytes());
        Assert.assertEquals(expectedBytes, texture.getPayload().length);
        Assert.assertEquals(TextureDecoder.TextureFormat.ETC2_EAC_RGBA8, texture.getFormat());
    }

    @Test
    public void testEtc2OpaqueRgbTranscodingSize() {
        int width = 1024;
        int height = 1024;
        byte[] rgbaPixels = createTestRgbaBuffer(width, height, false);

        TextureDecoder.TranscodedTexture texture = TextureDecoder.transcodeToEtc2(
                rgbaPixels, width, height, false, true
        );

        Assert.assertNotNull(texture);
        // ETC2 RGB8 is 8 bytes per 4x4 block -> 512KB
        int expectedBytes = 512 * 1024;
        Assert.assertEquals(expectedBytes, texture.getVramSizeBytes());
        Assert.assertEquals(TextureDecoder.TextureFormat.ETC2_RGB8, texture.getFormat());
    }

    @Test
    public void testFallbackWhenEtc2Unsupported() {
        int width = 512;
        int height = 512;
        byte[] rgbaPixels = createTestRgbaBuffer(width, height, true);

        // When supportsEtc2 is false, transparent textures fall back to RGBA8
        TextureDecoder.TranscodedTexture texture = TextureDecoder.transcodeToEtc2(
                rgbaPixels, width, height, true, false
        );

        Assert.assertNotNull("Texture should fall back gracefully without returning null", texture);
        Assert.assertEquals(TextureDecoder.TextureFormat.RGBA8, texture.getFormat());
        Assert.assertEquals(512 * 512 * 4, texture.getVramSizeBytes());
    }

    @Test
    public void testNonMultipleOf4DimensionsFallback() {
        int width = 125; // Not divisible by 4
        int height = 125;
        byte[] rgbaPixels = createTestRgbaBuffer(width, height, true);

        TextureDecoder.TranscodedTexture texture = TextureDecoder.transcodeToEtc2(
                rgbaPixels, width, height, true, true
        );

        Assert.assertNotNull(texture);
        Assert.assertEquals(TextureDecoder.TextureFormat.RGBA8, texture.getFormat());
        Assert.assertEquals(width * height * 4, texture.getVramSizeBytes());
    }

    @Test
    public void testTextureCacheManagerAccounting() {
        // Cache limit 10MB
        TextureCacheManager cacheManager = new TextureCacheManager(10 * 1024 * 1024);

        // 4 x 2048x2048 ETC2 RGBA textures = 4 * 4MB = 16MB total
        // Uncompressed RGBA8 would be 4 * 16MB = 64MB and cause early LRU eviction!
        for (int i = 1; i <= 2; i++) {
            byte[] rgba = createTestRgbaBuffer(2048, 2048, true);
            TextureDecoder.TranscodedTexture tex = TextureDecoder.transcodeToEtc2(
                    rgba, 2048, 2048, true, true
            );
            cacheManager.put("tex_" + i, tex);
        }

        // With ETC2 compression (4MB each), 2 textures fit inside 10MB limit without eviction!
        Assert.assertEquals(2, cacheManager.getItemCount());
        Assert.assertEquals(8 * 1024 * 1024, cacheManager.getCurrentSizeBytes());
        Assert.assertEquals(0, cacheManager.getEvictionCount());

        // Adding a 3rd texture should trigger 1 eviction
        byte[] rgba = createTestRgbaBuffer(2048, 2048, true);
        TextureDecoder.TranscodedTexture tex3 = TextureDecoder.transcodeToEtc2(
                rgba, 2048, 2048, true, true
        );
        cacheManager.put("tex_3", tex3);

        Assert.assertEquals(2, cacheManager.getItemCount()); // 2 active textures (8MB)
        Assert.assertEquals(1, cacheManager.getEvictionCount());
    }

    private byte[] createTestRgbaBuffer(int width, int height, boolean includeAlpha) {
        byte[] buffer = new byte[width * height * 4];
        for (int i = 0; i < buffer.length; i += 4) {
            buffer[i] = (byte) 128;     // R
            buffer[i + 1] = (byte) 128; // G
            buffer[i + 2] = (byte) 128; // B
            buffer[i + 3] = includeAlpha ? (byte) 200 : (byte) 255; // A
        }
        return buffer;
    }
}
