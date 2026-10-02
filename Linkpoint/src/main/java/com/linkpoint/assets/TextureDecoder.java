package com.linkpoint.assets;

import android.graphics.Bitmap;
import android.util.Log;

import java.nio.ByteBuffer;

/**
 * TextureDecoder handles JPEG2000 image decoding and hardware ETC2/ETC1
 * texture transcoding for optimal VRAM utilization.
 */
public class TextureDecoder {

    private static final String TAG = "TextureDecoder";

    public enum TextureFormat {
        ETC2_EAC_RGBA8(16), // 16 bytes per 4x4 block
        ETC2_RGB8(8),       // 8 bytes per 4x4 block
        ETC1_RGB(8),        // 8 bytes per 4x4 block
        RGBA8(16);          // 16 bytes per 4x4 pixels (4 bytes/pixel)

        private final int bytesPer4x4Block;

        TextureFormat(int bytesPer4x4Block) {
            this.bytesPer4x4Block = bytesPer4x4Block;
        }

        public int getBytesPer4x4Block() {
            return bytesPer4x4Block;
        }
    }

    public static class TranscodedTexture {
        private final byte[] payload;
        private final int width;
        private final int height;
        private final TextureFormat format;
        private final int vramSizeBytes;
        private final boolean hasAlpha;

        public TranscodedTexture(byte[] payload, int width, int height, TextureFormat format, boolean hasAlpha) {
            this.payload = payload;
            this.width = width;
            this.height = height;
            this.format = format;
            this.hasAlpha = hasAlpha;
            this.vramSizeBytes = calculateVramSizeBytes(width, height, format);
        }

        public byte[] getPayload() {
            return payload;
        }

        public int getWidth() {
            return width;
        }

        public int getHeight() {
            return height;
        }

        public TextureFormat getFormat() {
            return format;
        }

        public int getVramSizeBytes() {
            return vramSizeBytes;
        }

        public boolean hasAlpha() {
            return hasAlpha;
        }
    }

    /**
     * Calculate VRAM size in bytes for a given texture resolution and format.
     */
    public static int calculateVramSizeBytes(int width, int height, TextureFormat format) {
        if (width <= 0 || height <= 0) {
            return 0;
        }
        if (format == TextureFormat.RGBA8) {
            return width * height * 4;
        }
        int blocksX = (width + 3) / 4;
        int blocksY = (height + 3) / 4;
        return blocksX * blocksY * format.getBytesPer4x4Block();
    }

    /**
     * Transcode RGBA pixel buffer into an ETC2/ETC1 texture payload.
     *
     * @param rgbaPixels Raw RGBA byte array (width * height * 4)
     * @param width Texture width
     * @param height Texture height
     * @param hasAlpha Whether alpha channel contains transparency
     * @param supportsEtc2 Whether the GPU supports ETC2
     * @return TranscodedTexture object containing compressed payload and format metadata
     */
    public static TranscodedTexture transcodeToEtc2(byte[] rgbaPixels, int width, int height, boolean hasAlpha, boolean supportsEtc2) {
        if (rgbaPixels == null || rgbaPixels.length == 0 || width <= 0 || height <= 0) {
            try { Log.w(TAG, "Invalid RGBA pixels or dimensions"); } catch (Throwable ignored) {}
            return null;
        }

        // ETC2 / ETC1 requires multiple-of-4 dimensions for hardware block alignment
        boolean isValidBlockDimensions = (width % 4 == 0) && (height % 4 == 0);

        if (supportsEtc2 && isValidBlockDimensions) {
            Etc2Compressor compressor = Etc2CompressorFactory.INSTANCE.get();
            Etc2Compressor.Result result = compressor.compress(rgbaPixels, width, height, hasAlpha);
            if (result != null) {
                TextureFormat format = (result.getFormat() == Etc2Compressor.GpuFormat.ETC2_EAC_RGBA)
                        ? TextureFormat.ETC2_EAC_RGBA8
                        : TextureFormat.ETC2_RGB8;
                return new TranscodedTexture(result.getData(), width, height, format, hasAlpha);
            }
        }

        // Fallback for legacy GPUs or non-multiple-of-4 dimensions
        if (isValidBlockDimensions && !hasAlpha) {
            // Opaque textures on legacy GPUs can safely compress to ETC1
            Etc2Compressor.Result result = Etc1AsEtc2Rgb.INSTANCE.compress(rgbaPixels, width, height, false);
            if (result != null) {
                return new TranscodedTexture(result.getData(), width, height, TextureFormat.ETC1_RGB, false);
            }
        }

        // Uncompressed RGBA8 fallback to prevent rendering black textures
        try { Log.i(TAG, "Falling back to uncompressed RGBA8 for " + width + "x" + height + " (hasAlpha=" + hasAlpha + ", supportsEtc2=" + supportsEtc2 + ")"); } catch (Throwable ignored) {}
        return new TranscodedTexture(rgbaPixels, width, height, TextureFormat.RGBA8, hasAlpha);
    }

    /**
     * Decode JPEG2000 payload and transcode to ETC2/ETC1 texture payload.
     */
    public static TranscodedTexture decodeAndTranscode(byte[] j2kBytes, boolean supportsEtc2) {
        if (j2kBytes == null || j2kBytes.length == 0) {
            return null;
        }
        Bitmap bitmap = JPEG2000Decoder.INSTANCE.decode(j2kBytes);
        if (bitmap == null) {
            Log.e(TAG, "Failed to decode JPEG2000 image");
            return null;
        }
        int width = bitmap.getWidth();
        int height = bitmap.getHeight();
        byte[] rgbaPixels = new byte[width * height * 4];
        ByteBuffer buffer = ByteBuffer.wrap(rgbaPixels);
        bitmap.copyPixelsToBuffer(buffer);
        if (!bitmap.isRecycled()) {
            bitmap.recycle();
        }

        boolean hasAlpha = checkAlphaChannel(rgbaPixels);
        return transcodeToEtc2(rgbaPixels, width, height, hasAlpha, supportsEtc2);
    }

    /**
     * Helper to detect if alpha channel has non-opaque pixels.
     */
    public static boolean checkAlphaChannel(byte[] rgba) {
        if (rgba == null) return false;
        for (int i = 3; i < rgba.length; i += 4) {
            int alpha = rgba[i] & 0xFF;
            if (alpha < 254) { // non-opaque pixel
                return true;
            }
        }
        return false;
    }
}
