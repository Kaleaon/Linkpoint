package viewer.pipeline;

import com.linkpoint.assets.TextureDecoder.TextureFormat;
import com.linkpoint.assets.TextureDecoder.TranscodedTexture;

/**
 * Pipeline adapter for TextureDecoder.
 */
public class TextureDecoder {

    public static TranscodedTexture transcodeToEtc2(byte[] rgbaPixels, int width, int height, boolean hasAlpha, boolean supportsEtc2) {
        return com.linkpoint.assets.TextureDecoder.transcodeToEtc2(rgbaPixels, width, height, hasAlpha, supportsEtc2);
    }

    public static TranscodedTexture decodeAndTranscode(byte[] j2kBytes, boolean supportsEtc2) {
        return com.linkpoint.assets.TextureDecoder.decodeAndTranscode(j2kBytes, supportsEtc2);
    }

    public static int calculateVramSizeBytes(int width, int height, TextureFormat format) {
        return com.linkpoint.assets.TextureDecoder.calculateVramSizeBytes(width, height, format);
    }
}
