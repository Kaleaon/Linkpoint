package viewer.pipeline;

import com.linkpoint.assets.TextureDecoder;
import com.linkpoint.assets.TextureCacheManager;
import org.junit.Assert;
import org.junit.Test;

public class TextureDecoderTest {

    @Test
    public void testTranscodeToEtc2() {
        int width = 1024;
        int height = 1024;
        byte[] rgbaPixels = new byte[width * height * 4];
        for (int i = 0; i < rgbaPixels.length; i += 4) {
            rgbaPixels[i] = (byte) 255;
            rgbaPixels[i + 1] = (byte) 128;
            rgbaPixels[i + 2] = (byte) 64;
            rgbaPixels[i + 3] = (byte) 200;
        }

        TextureDecoder.TranscodedTexture texture = TextureDecoder.transcodeToEtc2(rgbaPixels, width, height, true, true);
        Assert.assertNotNull(texture);
        Assert.assertEquals(1024 * 1024, texture.getVramSizeBytes());
        Assert.assertEquals(TextureDecoder.TextureFormat.ETC2_EAC_RGBA8, texture.getFormat());
    }

    @Test
    public void testTextureCacheManagerEviction() {
        TextureCacheManager cacheManager = new TextureCacheManager(5 * 1024 * 1024); // 5MB limit

        byte[] rgbaPixels = new byte[1024 * 1024 * 4];
        TextureDecoder.TranscodedTexture texture1 = TextureDecoder.transcodeToEtc2(rgbaPixels, 1024, 1024, true, true); // 1MB
        TextureDecoder.TranscodedTexture texture2 = TextureDecoder.transcodeToEtc2(rgbaPixels, 1024, 1024, true, true); // 1MB

        cacheManager.put("t1", texture1);
        cacheManager.put("t2", texture2);

        Assert.assertEquals(2 * 1024 * 1024, cacheManager.getCurrentSizeBytes());
        Assert.assertEquals(2, cacheManager.getItemCount());
    }
}
